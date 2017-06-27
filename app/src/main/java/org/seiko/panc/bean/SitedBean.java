package org.seiko.panc.bean;

import org.seiko.panc.App;
import org.seiko.panc.base.ItemType;

import java.io.File;

/**
 * Created by Seiko on 2017/6/5/005. Y
 */

public class SitedBean implements ItemType {

    private String name;
    private boolean search;

    public SitedBean(File file) {
        this.name = file.getName().replace(".sited", "");
        this.search = App.getInstance().getPreferenceManager().getBoolean(name, true);
    }

    public void setName(String name) {this.name = name;}
    public String getName() {return name;}

    public void setSearch(boolean search) {this.search = search;}
    public boolean isSearch() {return search;}

    @Override
    public int itemType() {
        return 0;
    }
}
