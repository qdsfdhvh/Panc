package org.seiko.panc.sited;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Seiko on 2017/4/15. Y
 */

class YhAttrList {

    private Map<String,String> _items;

    YhAttrList(){
        _items  =new HashMap<>();
    }

    public void clear(){
        _items.clear();
    }

    public boolean contains(String key){
        return _items.containsKey(key);
    }

    public void set(String key, String val){
        _items.put(key,val);
    }

    public String getString(String key) {
        return getString(key,null);
    }

    public String getString(String key, String def){
        if(contains(key))
            return _items.get(key);
        else
            return def;
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int def) {
        if (contains(key))
            return Integer.parseInt(_items.get(key));
        else
            return def;
    }

    public void addAll(YhAttrList attrs){
        _items.putAll(attrs._items);
    }

}
