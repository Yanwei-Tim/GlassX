package com.newvision.martin.clouddetect.common.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by zhangsong on 4/25/16.
 */
public class ConfigService {
    private final static String TAG = "ConfigService";

    private SharedPreferences mSettings;
    private SharedPreferences.Editor mSettingsEditor;

    public ConfigService(Context context) {
        assert (context != null);

        mSettings = context.getSharedPreferences(context.getPackageName(), 0);
        mSettingsEditor = mSettings.edit();
    }

    public ConfigService(Context context, String sharedPrefsName) {
        assert (context != null);

        mSettings = context.getSharedPreferences(sharedPrefsName, 0);
        mSettingsEditor = mSettings.edit();
    }

    public boolean putString(final String entry, String value, boolean commit) {
        if (mSettingsEditor == null) {
            Log.e(TAG, "Settings are null");
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
            Log.e(TAG, "Settings are null");
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
            Log.e(TAG, "Settings are null");
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
            Log.e(TAG, "Settings are null");
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
            Log.e(TAG, "Settings are null");
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
            Log.e(TAG, "Settings are null");
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
            Log.e(TAG, "Settings are null");
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
            Log.e(TAG, "Settings are null");
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
            Log.e(TAG, "Settings are null");
            return false;
        }
        return mSettingsEditor.commit();
    }

}
