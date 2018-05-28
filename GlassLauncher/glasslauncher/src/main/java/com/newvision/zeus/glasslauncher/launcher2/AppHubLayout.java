package com.newvision.zeus.glasslauncher.launcher2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.newvision.zeus.glasslauncher.R;
import com.newvision.zeus.glasslauncher.settings.SettingsActivity;
import com.newvision.zeus.glasslauncher.test.CameraTestActivity;
import com.newvision.zeus.glasslauncher.test.CameraUsbTestActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangsong on 17-7-6.
 */

public class AppHubLayout extends RelativeLayout {
    private static final String TAG = AppHubLayout.class.getSimpleName();

    private TextView[] textViews = new TextView[3];

    private int selectedIndex = 1;

    /**
     * THE SWITCH TO OPEN USB MODE
     */
    private boolean isUsbMode = true;

    public AppHubLayout(Context context) {
        super(context);
        init();
    }

    public AppHubLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AppHubLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void selectNext() {
        unselectCurrent();
        if (++selectedIndex == textViews.length) selectedIndex = 0;
        selectCurrent();
    }

    public void selectPrevious() {
        unselectCurrent();
        if (--selectedIndex == -1) selectedIndex = textViews.length - 1;
        selectCurrent();
    }

    public Intent getLaunchIntent() {
        return (Intent) textViews[selectedIndex].getTag();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_apphub, this);

        textViews[0] = (TextView) findViewById(R.id.txt_settings);
        textViews[1] = (TextView) findViewById(R.id.txt_martin);
        textViews[2] = (TextView) findViewById(R.id.txt_camera);

        final List<HubInfo> hubInfos = new ArrayList<>();
        hubInfos.add(new HubInfo("设置", new Intent(getContext(), SettingsActivity.class)));
        ComponentName cn = new ComponentName("com.newvision.martin.clouddetect", "com.newvision.martin.clouddetect.ui.ARCloudDetectActivity");
        hubInfos.add(new HubInfo("Martin", new Intent().setComponent(cn)));
        if (isUsbMode) {
            hubInfos.add(new HubInfo("相机", new Intent(getContext(), CameraUsbTestActivity.class)));
        } else {
            hubInfos.add(new HubInfo("相机", new Intent(getContext(), CameraTestActivity.class)));
        }
        for (int i = 0; i < textViews.length; i++) {
            HubInfo entry = hubInfos.get(i);
            Log.i(TAG, entry.appName + " --> " + entry.launchIntent.toString());
            textViews[i].setText(entry.appName);
            textViews[i].setTag(entry.launchIntent);
        }

        selectCurrent();
    }

    /**
     * 改变{@link #selectedIndex}对应的TextView的颜色为 #00FFFF
     */
    private void selectCurrent() {
        textViews[selectedIndex].setTextColor(Color.parseColor("#00FFFF"));
    }

    /**
     * 改变{@link #selectedIndex}对应的TextView的颜色为 #B5B5B5
     */
    private void unselectCurrent() {
        textViews[selectedIndex].setTextColor(Color.parseColor("#B5B5B5"));
    }

    private static final class HubInfo {
        public String appName;
        public Intent launchIntent;

        public HubInfo(String appName, Intent launcher) {
            this.appName = appName;
            this.launchIntent = launcher;
        }
    }
}
