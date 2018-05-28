package com.newvision.zeus.glassmanager.setting.wifi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.newvision.zeus.glasscore.utils.GlassQRCodeUtils;
import com.newvision.zeus.glassmanager.ui.BaseActivity;
import com.newvision.zeus.glassmanager.R;

/**
 * Created by yanjiatian on 2017/7/14.
 */

public class GlassWifiQRCodeActivity extends BaseActivity implements View.OnClickListener {
    private ImageView mQRCode;
    private TextView mWifiSSID;
    private String mSSID;
    private String mQRMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSSID = getIntent().getStringExtra("ssid");
        mQRMessage = getIntent().getStringExtra("msg");
        setContentView(R.layout.activity_wifi_qr);
        initViews();
    }

    @Override
    public void initViews() {
        setTitle(R.string.connect_wifi);
        showNavigateLeftIcon(true);
        setNavigateLeftOnClickListener(this);
        mQRCode = (ImageView) findViewById(R.id.wifi_qr);
        mQRCode.setImageBitmap(GlassQRCodeUtils.getQRBitmap(GlassWifiQRCodeActivity.this, mQRMessage));
        mWifiSSID = (TextView) findViewById(R.id.wifi_ssid);
        mWifiSSID.setText(mSSID);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_navigate_left:
                finish();
                overridePendingTransition(R.anim.action_left_enter, R.anim.action_rigth_exit);
                break;
        }
    }
}
