package cn.ceyes.glasswidget.alertview;

import android.content.Context;
import android.view.ViewGroup;

import cn.ceyes.glasswidget.cardview.GlassCardProvider;
import cn.ceyes.glasswidget.cardview.GlassCardView;

/**
 * Created by zhangsong on 12/31/14.
 */
public class GlassAlertCardProvider extends GlassCardProvider {
    @Override
    public String getCardProviderId() {
        return "GlassAlertCard";
    }

    @Override
    public GlassCardView onCreateView(Context context, ViewGroup parent) {
        return new GlassAlertCardView(context);
    }
}
