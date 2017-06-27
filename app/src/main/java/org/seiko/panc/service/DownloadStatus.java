package org.seiko.panc.service;

/**
 * Created by Seiko on 2017/6/23/023. Y
 */

public class DownloadStatus {

    private int url;
    private int flag;
    private int progress;
    private int max;

    public void setUrl(int url) {this.url = url;}
    public int getUrl() {return url;}

    public void setFlag(int flag) {this.flag = flag;}
    public int getFlag() {return flag;}

    public void setProgress(int progress) {this.progress = progress;}
    public int getProgress() {return progress;}

    public void setMax(int max) {this.max = max;}
    public int getMax() {return max;}

}
