package org.seiko.panc.bean;

import org.seiko.panc.base.ItemType;

/**
 * Created by Seiko on 2017/6/6/006. Y
 */

public class TagBean extends BaseBean implements ItemType{

    private String name;
    private String logo;
    private String url;
    private String status;
    private String newSection;
    private String updateTime;

    public void setName(String name) {this.name = name;}
    public String getName() {return name;}

    public void setLogo(String logo) {this.logo = logo;}
    public String getLogo() {return logo;}

    public void setUrl(String url) {this.url = url;}
    public String getUrl() {return url;}

    public void setStatus(String status) {this.status = status;}
    public String getStatus() {return status;}

    public void setNewSection(String newSection) {this.newSection = newSection;}
    public String getNewSection() {return newSection;}

    public void setUpdateTime(String updateTime) {this.updateTime = updateTime;}
    public String getUpdateTime() {return updateTime;}

    @Override
    public int itemType() {
        return 0;
    }
}
