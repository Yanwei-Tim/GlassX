package com.newvision.zeus.glasslauncher.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zhangsong on 1/5/15.
 */
public class SharedPreferenceUtil {

    public static final String SharedName = "C210Launcher";

    public static void setSharedPreferencesString(Context context, String key, String value) {
        SharedPreferences pref = context.getSharedPreferences(SharedName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void setSharedPreferencesInt(Context context, String key, int value) {
        SharedPreferences pref = context.getSharedPreferences(SharedName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void setSharedPreferencesFloat(Context context, String key, float value) {
        SharedPreferences pref = context.getSharedPreferences(SharedName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public static String getSharedPreferencesString(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(SharedName,
                Context.MODE_PRIVATE);
        String value = pref.getString(key, "");
        return value;
    }

    public static int getSharedPreferencesInt(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(SharedName,
                Context.MODE_PRIVATE);
        int value = pref.getInt(key, 0);
        return value;
    }

    public static Float getSharedPreferencesFloat(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(SharedName,
                Context.MODE_PRIVATE);
        float value = pref.getFloat(key, 0);
        return value;
    }

    public static void setSharedPreferencesBoolean(Context context, String key, boolean value) {
        SharedPreferences pref = context.getSharedPreferences(SharedName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getSharedPreferencesBoolean(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(SharedName,
                Context.MODE_PRIVATE);
        return pref.getBoolean(key, false);
    }

    private SharedPreferenceUtil() {
    }
}
