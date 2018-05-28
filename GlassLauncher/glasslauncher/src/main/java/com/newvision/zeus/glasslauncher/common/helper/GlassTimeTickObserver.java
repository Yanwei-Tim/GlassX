package com.newvision.zeus.glasslauncher.common.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangsong on 17-6-29.
 */

public class GlassTimeTickObserver {
    public interface ITimeTickListener {
        void onTimeTick();
    }

    private static final String TAG = "GlassTimeTickObserver";

    private static GlassTimeTickObserver instance;

    private Context mContext;
    private BroadcastReceiver receiver;
    private List<ITimeTickListener> timeTickListeners;

    public static GlassTimeTickObserver getInstance() {
        if (instance == null) {
            synchronized (GlassTimeTickObserver.class) {
                if (instance == null) {
                    instance = new GlassTimeTickObserver();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        mContext = context;

        mContext.registerReceiver(receiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    public void registerObserver(ITimeTickListener listener) {
        if (timeTickListeners.contains(listener)) return;
        timeTickListeners.add(listener);

        listener.onTimeTick();
    }

    public void unregisterObserver(ITimeTickListener listener) {
        if (timeTickListeners.contains(listener))
            timeTickListeners.remove(listener);
    }

    public void destroy() {
        mContext.unregisterReceiver(receiver);
    }

    private GlassTimeTickObserver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                for (ITimeTickListener listener : timeTickListeners) {
                    listener.onTimeTick();
                }
            }
        };

        timeTickListeners = new ArrayList<>();
    }
}
