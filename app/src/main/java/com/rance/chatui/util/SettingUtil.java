package com.rance.chatui.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Tomdag on 2017/3/1.
 */

public class SettingUtil {
    private final String PREFERENCE_NAME = "SmackIM";
    private Context context;
    private SharedPreferences sharedPreferences;

    public SettingUtil(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    // 保存配置信息
    public void saveString(String key, String value) {
        sharedPreferences.edit().putString(key, value).commit();
    }

    // 获得配置信息
    public String getString(String key, String... defValue) {
        if (defValue.length > 0)
            return sharedPreferences.getString(key, defValue[0]);
        else
            return sharedPreferences.getString(key, "");

    }
}
