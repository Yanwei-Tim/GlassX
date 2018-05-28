package com.newvision.martin.clouddetect.common.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangsong on 17-3-16.
 */

public class ScreenStateWatcher {
    public interface OnScreenStateChangeListener {
        void onScreenOn();

        void onScreenOff();

        public static class Stub implements OnScreenStateChangeListener {
            @Override
            public void onScreenOn() {
            }

            @Override
            public void onScreenOff() {
            }
        }
    }

    private static final String TAG = "ScreenStateWatcher";

    private static ScreenStateWatcher instance = new ScreenStateWatcher();

    private Context mContext;
    private List<OnScreenStateChangeListener> listeners = null;

    public static ScreenStateWatcher getInstance() {
        return instance;
    }

    public void init(Context context) {
        if (mContext != null) return;

        mContext = context;

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mContext.registerReceiver(receiver, filter);
    }

    public void addListener(OnScreenStateChangeListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    public void removeListener(OnScreenStateChangeListener listener) {
        if (listeners.contains(listener))
            listeners.remove(listener);
    }

    public void deInit() {
        mContext.unregisterReceiver(receiver);
    }

    private ScreenStateWatcher() {
        listeners = new ArrayList<>();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            for (OnScreenStateChangeListener listener : listeners) {
                switch (intent.getAction()) {
                    case Intent.ACTION_SCREEN_ON:
                        listener.onScreenOn();
                        break;
                    case Intent.ACTION_SCREEN_OFF:
                        listener.onScreenOff();
                        break;
                }
            }
        }
    };
}
