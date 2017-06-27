package org.seiko.panc.sited;

import android.accounts.NetworkErrorException;
import android.support.annotation.Nullable;
import android.util.Log;
import org.seiko.panc.App;
import org.seiko.panc.utils.EncryptUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Seiko on 2017/4/15. Y
 */

class Util {
    static final String NEXT_CALL = "CALL::";
    static final String defUA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/12.10240";

    static String getHtml(final YhNode cfg, final String url)  {
        try {
            final HttpMessage msg = new HttpMessage(cfg, url);
            //加载本地html
            YhPair<String, Boolean> cache = _FileCache.get(url, cfg);
            if (msg.getMethod().equals("get") && !cache.getSecond()) {
                return cache.getFirst();
            }
            String html = Util.loadHtml(msg).blockingFirst();
            //保存html
            if (cache.getSecond()) {
                _FileCache.save(url, html);
            }
            return html;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static Observable<String> loadHtml(final HttpMessage msg) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> s) throws Exception {
                Request.Builder builder = new Request.Builder().url(msg.getUrl());

                for(Map.Entry<String, String> entry: msg.headers.entrySet()){
                    builder.addHeader(entry.getKey(), entry.getValue());
                }

                if ("post".equals(msg.getMethod())) {
                    builder.post(msg.body);
                } else {
                    builder.get();
                }

                try {
                    String html = getResponseBody(App.getHttpClient(), builder.build(), msg.getEncode());
                    s.onNext(html);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                s.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    private static String getResponseBody(OkHttpClient client, Request request, String encode) throws NetworkErrorException {
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                Log.d("HttpUtil-RequestHeader", request.headers().toString());
                Log.d("HttpUtil-ResponseHeader", response.headers().toString());
                byte[] bytes = response.body().bytes();
                return new String(bytes, encode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        throw new NetworkErrorException();
    }

    @Nullable
    static Element getElement(Element n, String tag) {
        NodeList temp = n.getElementsByTagName(tag);
        if (temp.getLength() > 0)
            return (Element) (temp.item(0));
        else
            return null;
    }

    static Element getXmlroot(String xml) throws Exception {
        StringReader sr = new StringReader(xml);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dombuild = factory.newDocumentBuilder();
        return dombuild.parse(new InputSource(sr)).getDocumentElement();
    }

    static String urlEncode(String str, String encode) {
        try {
            return URLEncoder.encode(str, encode);
        } catch (Exception ex) {
            return "";
        }
    }

    static void log(String tag, String msg) {
        Log.d(tag, msg);
    }

    static void log(String tag, String msg, Throwable tr) {
        Log.v(tag, msg, tr);
    }
    

    /*生成MD5值*/
    static String md5(String code) {

        String s = null;

        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        try {
            byte[] code_byts = code.getBytes("UTF-8");

            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(code_byts);
            byte tmp[] = md.digest();          // MD5 的计算结果是一个 128 位的长整数，
            // 用字节表示就是 16 个字节
            char str[] = new char[16 * 2];   // 每个字节用 16 进制表示的话，使用两个字符，
            // 所以表示成 16 进制需要 32 个字符
            int k = 0;                                // 表示转换结果中对应的字符位置
            for (int i = 0; i < 16; i++) {          // 从第一个字节开始，对 MD5 的每一个字节
                // 转换成 16 进制字符的转换
                byte byte0 = tmp[i];                 // 取第 i 个字节
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];  // 取字节中高 4 位的数字转换,
                // >>> 为逻辑右移，将符号位一起右移
                str[k++] = hexDigits[byte0 & 0xf];            // 取字节中低 4 位的数字转换
            }
            s = new String(str);                                 // 换后的结果转换为字符串

        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }
}
