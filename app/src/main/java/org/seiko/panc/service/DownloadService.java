package org.seiko.panc.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import org.seiko.panc.bean.DownloadBean;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.schedulers.Schedulers;
import static org.seiko.panc.service.DownloadEventFactory.normal;
import static org.seiko.panc.service.Utils.createProcessor;
import static org.seiko.panc.service.Utils.dispose;
import static org.seiko.panc.service.Utils.log;

/**
 * Created by Seiko on 2017/6/23/023. Y
 */

public class DownloadService extends Service {
    public static final String INTENT_KEY = "max_download_number";

    private DownloadBinder mBinder;
    private Semaphore semaphore;
    private BlockingQueue<DownloadMission> downloadQueue;
    private Map<String, DownloadMission> missionMap;
    private Map<String, FlowableProcessor<DownloadEvent>> processorMap;

    private Disposable disposable;

    @Override
    public void onCreate() {
        super.onCreate();
        log("create Download Service");
        mBinder = new DownloadBinder();
        downloadQueue = new LinkedBlockingQueue<>();
        missionMap = new ConcurrentHashMap<>();
        processorMap = new ConcurrentHashMap<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log("start Download Service");
        if (intent != null) {
            int maxDownloadNumber = intent.getIntExtra(INTENT_KEY, 1);
            semaphore = new Semaphore(maxDownloadNumber);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        log("bind Download Service");
        startDispatch();
        return mBinder;
    }

    @Override
    public void onDestroy() {
        log("destroy Download Service");
        super.onDestroy();
        destroy();
    }

    //=======================================================

    public FlowableProcessor<DownloadEvent> receiveDownloadEvent(String url) {
        FlowableProcessor<DownloadEvent> processor = createProcessor(url, processorMap);

        DownloadMission mission = missionMap.get(url);
        if (mission == null) {  //Not yet add this url mission.
            DownloadStatus status = DownloadManager.getInstance().queryStatus(url);
            processor.onNext(normal(status));
        }
        return processor;
    }

    /**
     * Add this mission into download queue.
     *
     * @param mission mission
     * @throws InterruptedException Blocking queue
     */
    public void addDownloadMission(DownloadMission mission) throws InterruptedException {
        mission.init(missionMap, processorMap);
        downloadQueue.put(mission);
    }

    private void startDispatch() {
        disposable = Observable
                .create(new ObservableOnSubscribe<DownloadMission>() {
                    @Override
                    public void subscribe(ObservableEmitter<DownloadMission> emitter) throws Exception {
                        DownloadMission mission;
                        while (!emitter.isDisposed()) {
                            try {
                                mission = downloadQueue.take();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                continue;
                            }
                            emitter.onNext(mission);
                        }
                        emitter.onComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<DownloadMission>() {
                    @Override
                    public void accept(DownloadMission mission) throws Exception {
                        mission.start(semaphore);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        log(throwable);
                    }
                });
    }

    /**
     * Call when service is onDestroy.
     */
    private void destroy() {
        dispose(disposable);
        for (DownloadMission each : missionMap.values()) {
            each.pause();
        }
        downloadQueue.clear();
    }

    //=======================================================
    class DownloadBinder extends Binder {
        public DownloadService getService() {
            return DownloadService.this;
        }
    }
}
