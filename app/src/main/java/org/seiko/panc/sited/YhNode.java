package org.seiko.panc.sited;

import android.text.TextUtils;
import android.util.Log;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;

/**
 * Created by Seiko on 2017/4/15. Y
 */

public class YhNode implements YhView {

    private YhAttrList attrs;
    private YhSource source;
    private boolean _isEmpty;

    public String name; //节点名称
    public String url;
    public String title; //标题
    public String lib;
    public String group;
    public int dtype;
    public String expr;
    //parse
    public String parse;
    public String parseUrl; //解析出真正在请求的Url
    //http
    private String method;   //http method
    private String _encode;  //http 编码
    private String _ua;      //http ua
    //cache
    private int cache = 1;//单位为秒(0不缓存；1不限时间)
    //build
    public String buildArgs;
    public String buildUrl;
    public String buildRef;
    public String buildHeader;
    public String header;
    public String args;

    private List<YhNode> _items; //下属项目
    private List<YhNode> _adds;  //下属数据节点

    public YhNode(YhSource source) {
        this.source = source;
        attrs = new YhAttrList();
    }

    YhNode buildForNode(Element cfg) {
        _isEmpty = (cfg == null);


        if (cfg != null) {
            SetNodeMap(cfg);

            this.name   = cfg.getTagName();
            this.url    = attrs.getString("url");
            this.title  = attrs.getString("title");
            this.group  = attrs.getString("group");
            this.lib    = attrs.getString("lib");
            this.method = attrs.getString("method","get");
            this.parse  = attrs.getString("parse");
            this.parseUrl = attrs.getString("parseUrl");
            this.expr    = attrs.getString("expr");

            this._encode = attrs.getString("encode");
            this._ua     = attrs.getString("ua");

            this.buildArgs   = attrs.getString("buildArgs");
            this.buildRef    = attrs.getString("buildRef");
            this.buildUrl    = attrs.getString("buildUrl");
            this.buildHeader = attrs.getString("buildHeader");
            this.header = attrs.getString("header");
            this.args   = attrs.getString("args");

            {
                String temp = attrs.getString("cache");
                if (!TextUtils.isEmpty(temp)) {
                    int len = temp.length();
                    if (len == 1) {
                        cache = Integer.parseInt(temp);
                    } else if (len > 1) {
                        cache = Integer.parseInt(temp.substring(0, len - 1));

                        String p = temp.substring(len - 1);
                        switch (p) {
                            case "d":
                                cache = cache * 24 * 60 * 60;
                                break;
                            case "h":
                                cache = cache * 60 * 60;
                                break;
                            case "m":
                                cache = cache * 60;
                                break;
                        }
                    }
                }
            }

            if (cfg.hasChildNodes()) {
                _items = new ArrayList<>();
                _adds  = new ArrayList<>();

                NodeList list = cfg.getChildNodes();
                for (int i=0, len=list.getLength(); i<len; i++){
                    Node n1 = list.item(i);
                    if(n1.getNodeType()==Node.ELEMENT_NODE) {
                        Element e1 = (Element) n1;

                        if (e1.getTagName().equals("item")) {
                            YhNode temp = new YhNode(this.source).buildForItem(e1, this);
                            _items.add(temp);
                        }
                        else if (e1.hasAttributes()) {
                            YhNode temp = new YhNode(this.source).buildForAdd(e1, this);
                            _adds.add(temp);
                        }
                        else {
                            attrs.set(e1.getTagName(), e1.getTextContent());
                        }
                    }
                }
            }
        }
        return this;
    }

    private YhNode buildForItem(Element cfg, YhNode p) {
        SetNodeMap(cfg);

        this.name = p.name;
        this.url   = attrs.getString("url");
        this.title = attrs.getString("title");
        this.group   = attrs.getString("group");
        this.lib   = attrs.getString("lib");
        this._encode = attrs.getString("encode");
        this.expr    = attrs.getString("expr");
        return this;
    }

    private YhNode buildForAdd(Element cfg, YhNode p) {
        SetNodeMap(cfg);

        this.name  = cfg.getTagName();//默认为标签
        this.dtype = attrs.getInt("dtype");
        this.url   = attrs.getString("url");
        this.title = attrs.getString("title");
        this.group   = attrs.getString("group");
        this.method  = attrs.getString("method");
        this.parseUrl = attrs.getString("parseUrl");
        this.parse    = attrs.getString("parse");

        this._encode = attrs.getString("encode");
        this._ua     = attrs.getString("ua");

        this.buildArgs   = attrs.getString("buildArgs");
        this.buildRef    = attrs.getString("buildRef");
        this.buildUrl    = attrs.getString("buildUrl");
        this.buildHeader = attrs.getString("buildHeader");
        this.header = attrs.getString("header");
        this.args   = attrs.getString("args");
        return this;
    }

    private void SetNodeMap(Element cfg) {
        NamedNodeMap nnMap = cfg.getAttributes();
        for(int i=0, len=nnMap.getLength(); i<len; i++) {
            Node att = nnMap.item(i);
            attrs.set(att.getNodeName(), att.getNodeValue());
        }
    }

    boolean isMatch(String url) {
        if(!TextUtils.isEmpty(expr)){
            Pattern pattern = Pattern.compile(expr);
            Matcher m = pattern.matcher(url);
            return m.find();
        }
        return false;
    }

    String encode() {
        if(!TextUtils.isEmpty(_encode)) {
            return _encode;
        }
        return source.encode();
    }

    String ua() {
        if(!TextUtils.isEmpty(_ua)) {
            return _ua;
        }
        return source.ua();
    }

    HashMap<String, String> headers() {
        final HashMap<String, String> headers = new HashMap<>();
        loadParam param = new loadParam() {
            @Override
            public void run(String key, String value) {
                headers.put(key, value);
            }
        };
        loadParams(header, param);
        loadParams(source.getHeader(this), param);
        return headers;
    }

    FormBody body() {
        final FormBody.Builder build_body = new FormBody.Builder();
        loadParam param = new loadParam() {
            @Override
            public void run(String key, String value) {
                build_body.add(key, value);
            }
        };
        loadParams(args, param);
        loadParams(source.getArgs(this), param);
        return build_body.build();
    }

    private void loadParams(final String data, final loadParam param) {
        if (!TextUtils.isEmpty(data)) {
            if (source.getEngine() < 34) {
                addParams(data, ";", "=", param);
            } else {
                addParams(data, "\\$\\$", ":", param);//new
            }
        }
    }

    private void addParams(final String str, final String sp1, final String sp2, loadParam param) {
        for (String kv : str.split(sp1)) {
            int idx = kv.indexOf(sp2);
            if (idx > 0) {
                String k = kv.substring(0, idx).trim();
                String v = kv.substring(idx + 1).trim();
                param.run(k, v);
            }
        }
    }

    private interface loadParam {
        void run(String key, String value);
    }

    public String source() {return source.Title();}

    public String method() {
        return method;
    }

    public boolean hasItems() {
        return _items != null && _items.size() != 0;
    }

    public int getCache() {
        return cache;
    }

    //==========================================
    public List<YhNode> items() {
        return _items;
    }

    @Override
    public String nodeName() {
        return name;
    }

    @Override
    public boolean isEmpty() {
        return _isEmpty;
    }

}
;