package org.seiko.panc.bean;

import org.seiko.panc.base.ItemType;

/**
 * Created by Seiko on 2017/6/6/006. Y
 */

public class HotsBean extends BaseBean implements ItemType{

    private String name;
    private String logo;
    private String url;

    public void setName(String name) {this.name = name;}
    public String getName() {return name;}

    public void setLogo(String logo) {this.logo = logo;}
    public String getLogo() {return logo;}

    public void setUrl(String url) {this.url = url;}
    public String getUrl() {return url;}

    @Override
    public int itemType() {
        return 0;
    }
}
