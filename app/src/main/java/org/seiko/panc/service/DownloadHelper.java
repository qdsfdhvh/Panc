package org.seiko.panc.service;

import android.util.Log;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.reactivestreams.Publisher;
import org.seiko.panc.App;
import org.seiko.panc.bean.DownloadBean;
import org.seiko.panc.bean.SectionBean;
import org.seiko.panc.manager.PathManager;
import org.seiko.panc.manager.SourceManager;
import org.seiko.panc.sited.YhNode;
import org.seiko.panc.sited.YhPair;
import org.seiko.panc.sited.YhSource;
import org.seiko.panc.utils.FileUtil;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.CacheControl;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Seiko on 2017/6/25/025. Y
 */

class DownloadHelper {

    Observable<DownloadStatus> downloadDispatcher(final DownloadBean bean) {
        return Observable.just(bean)
                .flatMap(new Function<DownloadBean, ObservableSource<DownloadStatus>>() {
                    @Override
                    public ObservableSource<DownloadStatus> apply(@NonNull DownloadBean downloadBean) throws Exception {
                        if (downloadBean.getUrls() == null) {
                            downloadBean = loadUrls(downloadBean);
                        }
                        return startDownload(downloadBean);
                    }
                }).subscribeOn(Schedulers.io());
    }

    private DownloadBean loadUrls(final DownloadBean bean) {
        return SourceManager.getInstance().rxgetSource(bean.getSource())
                .flatMap(new Function<YhSource, Publisher<YhPair<YhNode, String>>>() {
                    @Override
                    public Publisher<YhPair<YhNode, String>> apply(@NonNull YhSource sd) throws Exception {
                        return sd.doGetNodeViewModel(sd.getNode(sd._section, bean.getUrl()), bean.getUrl());
                    }
                })
                .toList().toObservable()
                .flatMap(new Function<List<YhPair<YhNode, String>>, ObservableSource<DownloadBean>>() {
                    @Override
                    public ObservableSource<DownloadBean> apply(@NonNull List<YhPair<YhNode, String>> pairs) throws Exception {
                        List<SectionBean> list = new ArrayList<>();

                        int i = 0;
                        for (YhPair<YhNode, String> pair : pairs) {
                            JsonElement element = new JsonParser().parse(pair.getSecond());
                            JsonArray array = element.getAsJsonArray();
                            for (JsonElement el : array) {
                                list.add(new SectionBean(el.getAsString(), i));
                                i++;
                            }
                        }
                        bean.setUrls(list);
                        //保存DownBean
                        FileUtil.save(PathManager.getSectionBean(bean.getSource(), bean.getTitle(), bean.getName()), bean);
                        return Observable.just(bean);
                    }
                }).subscribeOn(Schedulers.io()).blockingFirst();
    }

    private Observable<DownloadStatus> startDownload(final DownloadBean bean) {
        return Observable.create(new ObservableOnSubscribe<DownloadStatus>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<DownloadStatus> e) throws Exception {
                Log.d("startDownload", "开始下载：" + bean.getName());
                DownloadStatus status = new DownloadStatus();
                status.setFlag(DownloadFlag.STARTED);
                status.setMax(bean.getUrls().size());
                status.setProgress(bean.getProgress());

                try {
                    String path = PathManager.getSectionPath(bean.getSource(), bean.getTitle(), bean.getName());
                    for (int i = status.getProgress(); i < bean.getUrls().size(); i++) {
                        String url = bean.getUrls().get(i).getUrl();

                        Log.d("startDownload", url);
                        int count = 0;            // 单页下载错误次数
                        boolean success = false;  // 是否下载成功
                        while (count++ <= 3 && !success) {
                            Request request = buildRequest(url);
                            success = RequestAndWrite(path, request, i);

                            status.setProgress(i + 1);
                            e.onNext(status);
                        }

                        if (!success) {
                            status.setFlag(DownloadFlag.FAILED);
                            e.onNext(status);
                            break;
                        }
                    }
                    status.setFlag(DownloadFlag.COMPLETED);
                } catch (InterruptedIOException ex) {
                    status.setFlag(DownloadFlag.PAUSED);
                }
                e.onNext(status);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    //尝试下载图片并保存
    private boolean RequestAndWrite(String path, Request request, int i) throws InterruptedIOException {
        Response response = null;
        try {
            response = App.getHttpClient().newCall(request).execute();
            if (response.isSuccessful()) {
                FileUtil.saveText2Sdcard(PathManager.getSectionImgPath(path, i), response.body().byteStream());
                return true;
            }
        } catch (InterruptedIOException e) {
            // 由暂停下载引发，需要抛出以便退出外层循环，结束任务
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return false;
    }

    //创建请求
    private Request buildRequest(String url) {
        return new Request.Builder()
                .cacheControl(new CacheControl.Builder().noStore().build())
                .url(url)
                .get()
                .build();
    }
}
