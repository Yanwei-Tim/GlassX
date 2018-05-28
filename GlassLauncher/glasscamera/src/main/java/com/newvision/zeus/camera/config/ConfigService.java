package com.newvision.zeus.camera.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.newvision.zeus.camera.GlassApplication;
import com.newvision.zeus.camera.utils.LogUtil;


/**
 * Created by Qing Jiwei on 2017/5/17.
 */
public class ConfigService {
    private final static String TAG = "ConfigService";

    private SharedPreferences mSettings;
    private SharedPreferences.Editor mSettingsEditor;

    public ConfigService(String sharedPrefsName) {
        final Context appContext = GlassApplication.getContext();
        if (appContext != null) {
            mSettings = appContext.getSharedPreferences(sharedPrefsName, 0);
            mSettingsEditor = mSettings.edit();
            Log.i(TAG, "ConfigService: init mSettingsEditor");
        } else {
            Log.i(TAG, "ConfigService:appContext is null ");
        }
    }

    public boolean putString(final String entry, String value, boolean commit) {
        if (mSettingsEditor == null) {
            LogUtil.e(TAG, "Settings are null");
            return false;

        }
        mSettingsEditor.putString(entry.toString(), value);
        if (commit) {
            return mSettingsEditor.commit();
        }
        return true;
    }

    public boolean putString(final String entry, String value) {
        return putString(entry, value, false);
    }

    public boolean putInt(final String entry, int value, boolean commit) {
        if (mSettingsEditor == null) {
            LogUtil.e(TAG, "Settings are null");
            return false;
        }
        mSettingsEditor.putInt(entry.toString(), value);
        if (commit) {
            return mSettingsEditor.commit();
        }
        return true;
    }

    public boolean putInt(final String entry, int value) {
        return putInt(entry, value, false);
    }

    public boolean putFloat(final String entry, float value, boolean commit) {
        if (mSettingsEditor == null) {
            LogUtil.e(TAG, "Settings are null");
            return false;
        }
        mSettingsEditor.putFloat(entry.toString(), value);
        if (commit) {
            return mSettingsEditor.commit();
        }
        return true;
    }

    public boolean putFloat(final String entry, float value) {
        return putFloat(entry, value, false);
    }

    public boolean putBoolean(final String entry, boolean value, boolean commit) {
        if (mSettingsEditor == null) {
            LogUtil.e(TAG, "Settings are null");
            return false;
        }
        mSettingsEditor.putBoolean(entry.toString(), value);
        if (commit) {
            return mSettingsEditor.commit();
        }
        return true;
    }

    public boolean putBoolean(final String entry, boolean value) {
        return putBoolean(entry, value, false);
    }

    public String getString(final String entry, String defaultValue) {
        if (mSettingsEditor == null) {
            LogUtil.e(TAG, "Settings are null");
            return defaultValue;
        }
        try {
            return mSettings.getString(entry.toString(), defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public int getInt(final String entry, int defaultValue) {
        if (mSettingsEditor == null) {
            LogUtil.e(TAG, "Settings are null");
            return defaultValue;
        }
        try {
            return mSettings.getInt(entry.toString(), defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public float getFloat(final String entry, float defaultValue) {
        if (mSettingsEditor == null) {
            LogUtil.e(TAG, "Settings are null");
            return defaultValue;
        }
        try {
            return mSettings.getFloat(entry.toString(), defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public boolean getBoolean(final String entry, boolean defaultValue) {
        if (mSettingsEditor == null) {
            LogUtil.e(TAG, "Settings are null");
            return defaultValue;
        }
        try {
            return mSettings.getBoolean(entry.toString(), defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public boolean commit() {
        if (mSettingsEditor == null) {
            LogUtil.e(TAG, "Settings are null");
            return false;
        }
        return mSettingsEditor.commit();
    }

    public boolean clearData() {
        if (mSettingsEditor == null) {
            LogUtil.e(TAG, "Settings are null");
            return false;
        }
        return mSettingsEditor.clear().commit();
    }

}
