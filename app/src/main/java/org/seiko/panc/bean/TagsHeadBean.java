package org.seiko.panc.bean;

import org.seiko.panc.base.ItemType;

/**
 * Created by Seiko on 2017/6/5/005. Y
 */

public class TagsHeadBean implements ItemType {

    private String name;
    private String url;

    public TagsHeadBean(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public void setName(String name) {this.name = name;}
    public String getName() {return name;}

    public void setUrl(String url) {this.url = url;}
    public String getUrl() {return url;}

    @Override
    public int itemType() {
        return 1;
    }
}
