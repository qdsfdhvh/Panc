package org.seiko.panc.service;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import org.reactivestreams.Publisher;
import org.seiko.panc.bean.DownloadBean;

import java.util.List;
import java.util.concurrent.Semaphore;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import static org.seiko.panc.service.Utils.log;

/**
 * Created by Seiko on 2017/6/23/023. Y
 */

public class RxDownload {
    private static final Object object = new Object();
    @SuppressLint("StaticFieldLeak")
    private volatile static RxDownload instance;
    private volatile static boolean bound = false;

    private int maxDownloadNumber = 1;

    private Context context;
    private Semaphore semaphore; //限制个数，阻塞
    private DownloadService downloadService;
    private DownloadHelper downloadHelper;

    private RxDownload(Context context) {
        this.context = context.getApplicationContext();
        semaphore = new Semaphore(1);
        downloadHelper = new DownloadHelper();
    }

    /**
     * set max download number when service download
     *
     * @param max max download number
     * @return instance
     */
    public RxDownload maxDownloadNumber(int max) {
        this.maxDownloadNumber = max;
        return this;
    }

    /**
     * Return RxDownload Instance
     *
     * @param context context
     * @return RxDownload
     */
    public static RxDownload getInstance(Context context) {
        if (instance == null) {
            synchronized (RxDownload.class) {
                if (instance == null) {
                    instance = new RxDownload(context);
                }
            }
        }
        return instance;
    }

    /**
     * Normal download.
     * <p>
     * You can construct a DownloadBean to save extra data to the database.
     *
     * @param bean DownloadBean.
     * @return Observable<DownloadStatus>
     */
    public Observable<DownloadStatus> download(final DownloadBean bean) {
        return downloadHelper.downloadDispatcher(bean);
    }

    /**
     * Using Service to download.
     *
     * @param bean download bean
     * @return Observable<DownloadStatus>
     */
    public Flowable<?> serviceDownload(final DownloadBean bean) {
        return createGeneralFlowable(new GeneralObservableCallback() {
            @Override
            public void call() throws Exception {
                downloadService.addDownloadMission(new SingleMission(RxDownload.this, bean));
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<?> serviceDownload(final List<DownloadBean> beans) {
        return createGeneralFlowable(new GeneralObservableCallback() {
            @Override
            public void call() throws Exception {
                for (DownloadBean bean : beans) {
                    downloadService.addDownloadMission(new SingleMission(RxDownload.this, bean));
                }
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Receive the url download event.
     * <p>
     * Will receive the following event:
     * {@link DownloadFlag#NORMAL}、{@link DownloadFlag#WAITING}、
     * {@link DownloadFlag#STARTED}、{@link DownloadFlag#PAUSED}、
     * {@link DownloadFlag#COMPLETED}、{@link DownloadFlag#FAILED};
     * <p>
     * Every event has {@link DownloadStatus}, you can get it and display it on the interface.
     *
     * @param url url
     * @return DownloadEvent
     */
    public Flowable<DownloadEvent> receiveStatus(final String url) {
        return createGeneralFlowable(null)
                .flatMap(new Function<Object, Publisher<DownloadEvent>>() {
                    @Override
                    public Publisher<DownloadEvent> apply(@NonNull Object o) throws Exception {
                        return downloadService.receiveDownloadEvent(url);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * return general observable
     *
     * @param callback Called when observable created.
     * @return Observable
     */
    private Flowable<?> createGeneralFlowable(final GeneralObservableCallback callback) {
        return Flowable.create(new FlowableOnSubscribe<Object>() {

            @Override
            public void subscribe(final FlowableEmitter<Object> emitter) throws Exception {
                if (!bound) { //判断是否已经链接了DownloadService
                    semaphore.acquire();
                    if (!bound) {
                        startBindService(new ServiceConnectedCallback() {
                            @Override
                            public void call() {
                                doCall(callback, emitter);
                                semaphore.release();
                            }
                        });
                    } else {
                        doCall(callback, emitter);
                        semaphore.release();
                    }
                } else {
                    doCall(callback, emitter);
                    semaphore.release();
                }
            }
        }, BackpressureStrategy.ERROR).subscribeOn(Schedulers.io());
    }

    private void doCall(GeneralObservableCallback callback, FlowableEmitter<Object> emitter) {
        if (callback != null) {
            try {
                callback.call();  //真正要做的事件
            } catch (Exception ex) {
                emitter.onError(ex);
            }
        }
        emitter.onNext(object); //对请求的地方发送一个空的object
        emitter.onComplete();
    }

    /**
     * start and bind service.
     *
     * @param callback Called when service connected.
     */
    private void startBindService(final ServiceConnectedCallback callback) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(DownloadService.INTENT_KEY, maxDownloadNumber);
        context.startService(intent);
        context.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                DownloadService.DownloadBinder downloadBinder
                        = (DownloadService.DownloadBinder) binder;
                downloadService = downloadBinder.getService();
                context.unbindService(this);
                bound = true;
                callback.call();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                //注意!!这个方法只会在系统杀掉Service时才会调用!!
                bound = false;
                log("service链接失败");
            }
        }, Context.BIND_AUTO_CREATE);
    }

    private interface GeneralObservableCallback {
        void call() throws Exception;
    }

    private interface ServiceConnectedCallback {
        void call();
    }
}
