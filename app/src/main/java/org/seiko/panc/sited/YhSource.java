package org.seiko.panc.sited;

import android.app.Application;
import android.text.TextUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import static org.seiko.panc.sited.Util.log;

/**
 * Created by Seiko on 2017/4/15. Y
 */

public class YhSource {

    private Element root;

    private int engine;
    private int schema;


    private String _ua;
    private String _title;  //标题
    private String _encode; //编码
    private String _intro;

    public int dtype;
    public YhNodeSet home;
    private YhNode hots;
    private YhNode updates;
    private YhNode tags;

    public YhNode _search;
    public YhNode _tag;
    public YhView _book;
    public YhView _section;

    private String _cookies;
    private JsEngine js;
    private YhAttrList attrs;


    public YhSource(Application app, String xml) throws Exception {
        doInit(xml);
        doLoad(app);
    }

    private void doInit(String xml) throws Exception {
        if (xml.startsWith("sited::")) {
            int start = xml.indexOf("::") + 2;
            int end = xml.lastIndexOf("::");
            String txt = xml.substring(start, end);
            String key = xml.substring(end + 2);
            xml = Util.unsuan(txt, key);
        }

        attrs = new YhAttrList();
        root = Util.getXmlroot(xml);

        {
            NamedNodeMap temp = root.getAttributes();
            for (int i = 0, len = temp.getLength(); i < len; i++) {
                Node p = temp.item(i);
                attrs.set(p.getNodeName(), p.getNodeValue());
            }
        }

        {
            NodeList temp = root.getChildNodes();
            for (int i = 0, len = temp.getLength(); i < len; i++) {
                Node p = temp.item(i);

                if (p.getNodeType() == Node.ELEMENT_NODE && !p.hasAttributes() && p.hasChildNodes()) {
                    if(p.getChildNodes().getLength()==1) {
                        Node p2 = p.getFirstChild();
                        if (p2.getNodeType() == Node.TEXT_NODE) {
                            attrs.set(p.getNodeName(), p2.getNodeValue());
                        }
                    }
                }
            }
        }

        engine = attrs.getInt("engine");
        schema = attrs.getInt("schema");
    }

    private void doLoad(final Application app) {
        String metaName;
        String mainName;
        String scriptName;
        if (schema > 0) {
            metaName = "meta";
            mainName = "main";
            scriptName = "script";
        } else {
            metaName = "meta";
            mainName = "main";
            scriptName = "jscript";
        }

        //1.head
        YhNodeSet meta = new YhNodeSet(this).buildForNode(Util.getElement(root, metaName));
        YhNodeSet main = new YhNodeSet(this).buildForNode(Util.getElement(root, mainName));
        meta.Attrs().addAll(attrs);
        _title = meta.Attrs().getString("title");
        _encode = meta.Attrs().getString("encode");
        _ua = meta.Attrs().getString("ua");
        _intro = meta.Attrs().getString("intro");
        dtype = main.Attrs().getInt("dtype");
        //2.body
        home = (YhNodeSet) main.get("home");
        hots     = (YhNode) home.get("hots");
        updates  = (YhNode) home.get("updates");
        tags     = (YhNode) home.get("tags");
        _search  = (YhNode) main.get("search");
        _tag     = (YhNode) main.get("tag");
        _book    = main.get("book");
        _section = main.get("section");
        //3.script :: 放后面
        js = new JsEngine(app);  //（需要放到主线程）
        YhJscript script = new YhJscript(this, Util.getElement(root, scriptName));
        script.loadJs(app, js);
        root = null;
    }

    public YhNode getNode(YhView node, String url) {
        if (node instanceof YhNode) {
            return (YhNode) node;
        } else if (node instanceof YhNodeSet) {
            YhNodeSet nodeSet = (YhNodeSet) node;
            return nodeSet.getNode(url);
        }
        return null;
    }

    private Observable<String> rxParse(YhNode cfg,String url, String html) {
        log("rxParse-url", url);
        log("rxParse-html", html == null ? "null" : html);

        if (TextUtils.isEmpty(cfg.parse) || "@null".equals(cfg.parse)) {
            if (TextUtils.isEmpty(html)) {
                html = "";
            }
            return Observable.just(html);
        }
        return js.rxCallJs(cfg.parse, url, html);
    }

    private Observable<String> rxParseUrl(YhNode cfg, String url, String html) {
        log("parseUrl-url", url);
        log("parseUrl-html", html == null ? "null" : html);

        return js.rxCallJs(cfg.parseUrl, url, html);
    }

    private String getUrl(YhNode cfg, String defUrl) {
        return getUrl(cfg, defUrl, "", "");
    }

    private String getUrl(YhNode cfg, String defUrl, String page, String key) {
        String u1 = TextUtils.isEmpty(cfg.url) ? defUrl:cfg.url;
        if (!TextUtils.isEmpty(cfg.buildUrl)) {
            return js.rxCallJs(cfg.buildUrl, u1, page, key).blockingFirst();
        }
        return u1;
    }

    String getHeader(YhNode cfg) {
        if (!TextUtils.isEmpty(cfg.buildHeader)) {

            return js.rxCallJs(cfg.buildHeader).blockingFirst();
        }
        return "";
    }

    String getArgs(YhNode cfg) {
        if (!TextUtils.isEmpty(cfg.buildArgs)) {

            return js.rxCallJs(cfg.buildArgs).blockingFirst();
        }
        return "";
    }

    //===============================================
    /** rxjava */
    public Flowable<YhPair<YhNode, String>> doGetNodeViewModel(final YhNode cfg, String url, final int page) {
        return doGetNodeViewModel(cfg, url, page, cfg.url);
    }

    public Flowable<YhPair<YhNode, String>> doGetNodeViewModel(final YhNode cfg, final String key, final int page, final String defUrl) {
        // Tags专用
        if (cfg.hasItems() && TextUtils.isEmpty(cfg.parse)) {
            return Flowable.just(YhPair.create(cfg, ""));
        }

        String url = getUrl(cfg, defUrl,  page + "", key);
        url = url.replace("@key", Util.urlEncode(key, cfg.encode()));
        url = url.replace("@page", page + "");
        return doGetNodeViewModel(cfg, url);
    }

    public Flowable<YhPair<YhNode, String>> doGetNodeViewModel(final YhNode cfg, final String url) {
        return Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                        String html = Util.getHtml(cfg, url);
                        if (!TextUtils.isEmpty(cfg.parseUrl)) {
                            String parseUrl = rxParseUrl(cfg, url, html).blockingFirst();

                            while (parseUrl.startsWith(Util.NEXT_CALL)) {
                                parseUrl = parseUrl.replace(Util.NEXT_CALL, "");
                                log("doGetNodeViewModel-isNextUrl", parseUrl);
                                String html2 = Util.getHtml(cfg, parseUrl);
                                parseUrl = rxParseUrl(cfg, url, html2).blockingFirst();
                                parseUrl = getUrl(cfg, parseUrl);
                            }

                            String[] urls = parseUrl.split(";");
                            for (String u1 : urls) {
                                String html3 = Util.getHtml(cfg, u1);
                                log("doGetNodeViewModel-isParseUrl", html3);
                                e.onNext(html3);
                            }
                        } else {
                            e.onNext(html);
                        }
                        e.onComplete();
                    }
                })
                .concatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(@NonNull String html) throws Exception {
                        return rxParse(cfg, url, html);
                    }
                })
                .concatMap(new Function<String, ObservableSource<YhPair<YhNode, String>>>() {
                    @Override
                    public ObservableSource<YhPair<YhNode, String>> apply(@NonNull String json) throws Exception {
                        log("rxPrase-json", json);
                        return Observable.just(YhPair.create(cfg, json));
                    }
                }).subscribeOn(Schedulers.io()).toFlowable(BackpressureStrategy.BUFFER);
    }


    //========================================
    public int getEngine() {
        return engine;
    }

    public YhNode Hots() {
        return hots;
    }

    public YhNode Updates() {
        return updates;
    }

    public YhNode Tags() {
        return tags;
    }

    public String cookies() {
        return _cookies;
    }

    public void setCookies(String cookies) {
        this._cookies = cookies;
    }

    public String ua() {
        return _ua;
    }

    public String encode() {
        return _encode;
    }

    public String Title() {return _title;}

    public String Intro() {return _intro;}

}
