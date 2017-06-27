package org.seiko.panc.service;

import java.util.Map;
import java.util.concurrent.Semaphore;

import io.reactivex.processors.FlowableProcessor;

/**
 * Created by Seiko on 2017/6/23/023. Y
 */

abstract class DownloadMission {

    protected RxDownload rxdownload;
    protected FlowableProcessor<DownloadEvent> processor;
    private boolean canceled = false;
    private boolean completed = false;

    DownloadMission(RxDownload rxdownload) {
        this.rxdownload = rxdownload;
    }

    public boolean isCanceled() {return canceled;}
    public void setCanceled(boolean canceled) {this.canceled = canceled;}

    public boolean isCompleted() {return completed;}
    public void setCompleted(boolean completed) {this.completed = completed;}

    //===============================================
    public abstract String getUrl();

    public abstract void init(Map<String, DownloadMission> missionMap,
                              Map<String, FlowableProcessor<DownloadEvent>> processorMap);

    public abstract void start(final Semaphore semaphore) throws InterruptedException;

    public abstract void pause();

}
