package org.seiko.panc.bean;

import org.seiko.panc.base.ItemType;

/**
 * Created by Seiko on 2017/6/14/014. Y
 */

public class SectionBean extends BaseBean implements ItemType {

    private String url;
    private int index;

    public SectionBean(String url, int index) {
        this.url = url;
        this.index = index;
    }

    public void setUrl(String url) {this.url = url;}
    public String getUrl() {return url;}

    public void setIndex(int index) {this.index = index;}
    public int getIndex() {return index;}

    @Override
    public int itemType() {
        return 0;
    }
}
