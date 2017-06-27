package org.seiko.panc.sited;

import java.util.HashMap;
import okhttp3.FormBody;

/**
 * Created by Seiko on 2017/4/15. Y
 */

class HttpMessage {

    HashMap<String, String> headers = new HashMap<>();
    FormBody body;

    private String url;
    private YhNode config;

    //可由cfg实始化
    private String encode;
    private String method;
    private String ua;

    HttpMessage(YhNode cfg, String url) {
        this.config = cfg;
        this.url = url;
        rebuild(null);
    }

    private void rebuild(final YhNode cfg) {
        if (cfg != null) {
            this.config = cfg;
        }
        encode = config.encode();
        method = config.method();
        ua = config.ua();

        headers = config.headers();
        headers.put("User-Agent", getUa());
        headers.put("Referer", url);

        isPost();
    }

    private void isPost() {
        if (url.startsWith("POST::")) {
            url = url.replace("POST::", "");
            method = "post";
        }
        url = url.replace("GET::", "");

        if ("post".equals(method)) {
            body = config.body();
        }
    }

    String getEncode() {
        return encode==null ? "UTF-8":encode;
    }

    String getUa() {
        return ua==null ? Util.defUA:ua;
    }

    String getMethod() {
        return method==null?"get":method;
    }

    String getUrl() {
        return url;
    }

    String getSource() {
        return config.source();
    }

}
