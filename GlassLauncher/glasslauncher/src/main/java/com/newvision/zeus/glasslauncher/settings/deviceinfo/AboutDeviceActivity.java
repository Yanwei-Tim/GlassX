package com.newvision.zeus.glasslauncher.settings.deviceinfo;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by zhangsong on 4/10/15.
 */
public class AboutDeviceActivity extends Activity {

    private static final String TAG = AboutDeviceActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AboutDeviceCardView cardView = new AboutDeviceCardView(this);
        setContentView(cardView);
    }
}
