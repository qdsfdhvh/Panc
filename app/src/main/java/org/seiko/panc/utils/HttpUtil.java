package org.seiko.panc.utils;

import android.accounts.NetworkErrorException;
import android.util.Log;

import org.seiko.panc.App;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Seiko on 2017/6/5/005. Y
 */

public class HttpUtil {

    public static Observable<String> get(final String url) {
        return Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> s) throws Exception {
                        Request.Builder builder = new Request.Builder().url(url).get();

                        try {
                            String html = getResponseBody(App.getHttpClient(), builder.build());
                            s.onNext(html);
                            s.onComplete();
                        } catch (Exception e) {
                            s.onError(e);
                        }
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    public static String getResponseBody(OkHttpClient client, Request request) throws NetworkErrorException {
        return getResponseBody(client, request, "UTF-8");
    }

    public static String getResponseBody(OkHttpClient client, Request request, String encode) throws NetworkErrorException {
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                Log.d("HttpUtil-RequestHeader", request.headers().toString());
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
}
