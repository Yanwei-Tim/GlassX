package com.newvision.zeus.glasslauncher.launcher.home;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.newvision.zeus.glasslauncher.common.helper.GlassTimeTickObserver;
import com.newvision.zeus.glasslauncher.common.helper.TimeFormatter;

import java.util.Date;

import cn.ceyes.glasswidget.cardview.GlassCardProvider;
import cn.ceyes.glasswidget.cardview.GlassCardView;

/**
 * Created by zhangsong on 17-6-29.
 */

public class HomeCardProvider extends GlassCardProvider {
    private static final String TAG = "TimeCardProvider";

    private HomeCardView homeCardView;
    private TimeFormatter timeFormatter;

    public HomeCardProvider() {
        timeFormatter = TimeFormatter.getInstance("HH:mm");
    }

    @Override
    public String getCardProviderId() {
        return "time-card";
    }

    @Override
    public GlassCardView onCreateView(Context context, ViewGroup parent) {
        homeCardView = new HomeCardView(context);
        return homeCardView;
    }

    @Override
    public boolean onGlassEvent(int eventCode, Object event) {
        Log.i(TAG, "onGlassEvent: code: " + eventCode + ", event: " + event);
        if (eventCode == 10000) {
            registerObservers();
            return true;
        }
        if (eventCode == 10001) {
            unregisterObservers();
            return true;
        }
        return false;
    }

    private void registerObservers() {
        GlassTimeTickObserver.getInstance().registerObserver(timeTickListener);
    }

    private void unregisterObservers() {
        GlassTimeTickObserver.getInstance().unregisterObserver(timeTickListener);
    }

    GlassTimeTickObserver.ITimeTickListener timeTickListener = new GlassTimeTickObserver.ITimeTickListener() {
        @Override
        public void onTimeTick() {
            String time = timeFormatter.format(new Date());
            homeCardView.setTime(time);
        }
    };
}
