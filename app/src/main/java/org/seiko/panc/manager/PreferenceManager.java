package org.seiko.panc.manager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Seiko on 2017/6/20/020. Y
 */

public class PreferenceManager {

    private static final String PREFERENCES_NAME = "panc_preferences";

    private SharedPreferences mSharedPreferences;

    public PreferenceManager(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public String getString(String key) {return getString(key, null);}
    public String getString(String key, String defValue) {return mSharedPreferences.getString(key, defValue);}
    public void putString(String key, String value) {mSharedPreferences.edit().putString(key, value).apply();}

    public boolean getBoolean(String key, boolean defValue) {return mSharedPreferences.getBoolean(key, defValue);}
    public void putBoolean(String key, boolean value) {mSharedPreferences.edit().putBoolean(key, value).apply();}

    public int getInt(String key, int defValue) {return mSharedPreferences.getInt(key, defValue);}
    public void putInt(String key, int value) {mSharedPreferences.edit().putInt(key, value).apply();}

    public long getLong(String key, long defValue) {return mSharedPreferences.getLong(key, defValue);}
    public void putLong(String key, long value) {mSharedPreferences.edit().putLong(key, value).apply();}

}
