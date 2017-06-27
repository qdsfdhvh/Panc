package org.seiko.panc.service;

import org.seiko.panc.bean.DownloadBean;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.Semaphore;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.schedulers.Schedulers;
import static org.seiko.panc.service.DownloadEventFactory.completed;
import static org.seiko.panc.service.DownloadEventFactory.failed;
import static org.seiko.panc.service.DownloadEventFactory.started;
import static org.seiko.panc.service.DownloadEventFactory.paused;
import static org.seiko.panc.service.Utils.createProcessor;
import static org.seiko.panc.service.Utils.dispose;
import static org.seiko.panc.service.Utils.formatStr;
import static org.seiko.panc.service.Utils.log;

/**
 * Author: Season(ssseasonnn@gmail.com)
 * Date: 2017/2/24
 * <p>
 * SingleMission, only one url.
 */
public class SingleMission extends DownloadMission {

    private DownloadBean bean;
    protected Disposable disposable;
    protected DownloadStatus status;

    SingleMission(RxDownload rxdownload, DownloadBean bean) {
        super(rxdownload);
        this.bean = bean;
    }

    @Override
    public String getUrl() {
        return bean.getUrl();
    }

    @Override
    public void init(Map<String, DownloadMission> missionMap, Map<String, FlowableProcessor<DownloadEvent>> processorMap) {
        DownloadManager.getInstance().InsertOrUpdate(bean); //添加到数据库
        DownloadMission mission = missionMap.get(getUrl());
        if (mission == null) {
            missionMap.put(getUrl(), this);
        } else {
            if (mission.isCanceled()) {
                missionMap.put(getUrl(), this);
            } else {
                throw new IllegalArgumentException(formatStr("The url [%s] already exists.", getUrl()));
            }
        }
        this.processor = createProcessor(getUrl(), processorMap);
    }

    @Override
    public void start(final Semaphore semaphore) throws InterruptedException {
        if (isCanceled()) {
            return;
        }

        semaphore.acquire();
        if (isCanceled()) {
            semaphore.release();
            return;
        }

        disposable = rxdownload.download(bean)
                .subscribeOn(Schedulers.io())
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        log("finally and release...");
                        setCanceled(true);
                    }
                })
                .subscribe(new Consumer<DownloadStatus>() {
                    @Override
                    public void accept(@NonNull DownloadStatus value) throws Exception {
                        status = value;
                        processor.onNext(started(value));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        save();
                        processor.onNext(failed(status, throwable));
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {  //这里接收onComplete。
                        save();
                        processor.onNext(completed(status));
                        semaphore.release();
                        setCompleted(true);
                    }
                });
    }

    private void save() {
        if (status != null) {
            bean.setMax(status.getMax());
            bean.setProgress(status.getProgress());
            bean.setFlag(status.getFlag());
            bean.setDate(new Date().getTime());
            DownloadManager.getInstance().InsertOrUpdate(bean);
        }
    }


    @Override
    public void pause() {
        dispose(disposable);
        setCanceled(true);
        if (processor != null && !isCompleted()) {
            processor.onNext(paused(status));
        }
    }


}
