package com.newvision.zeus.glasslauncher.settings.wifi.wifilist;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.view.ViewGroup;

import cn.ceyes.glasswidget.cardview.GlassCardProvider;
import cn.ceyes.glasswidget.cardview.GlassCardView;

public class GlassWifiItemProvider extends GlassCardProvider {

    private static final String TAG = GlassWifiItemProvider.class.getSimpleName();
    private String mCardProviderId = null;
    private ScanResult mScanResult = null;

    public void setCardProviderId(String cardProviderId) {
        mCardProviderId = cardProviderId;
    }

    @Override
    public String getCardProviderId() {
        return mCardProviderId;
    }

    @Override
    public GlassCardView onCreateView(Context context, ViewGroup parent) {
        GlassWifiItemView mItemView = new GlassWifiItemView(context);
        mItemView.initData(mScanResult);
        Log.i(TAG, "onCreateView, mScanResult :" + mScanResult.toString());
        return mItemView;
    }

    public void initData(ScanResult scanResult) {
        mScanResult = scanResult;
    }

    public ScanResult getData() {
        return mScanResult;
    }
}
