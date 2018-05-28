package com.newvision.zeus.glassmanager.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.newvision.zeus.glassmanager.ui.BaseActivity;
import com.newvision.zeus.glassmanager.R;
import com.newvision.zeus.glassmanager.setting.wifi.WifiListActivity;

/**
 * Created by yanjiatian on 2017/7/4.
 * 配置眼镜端参数的设置列表
 */

public class GlassSettingActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glass_setting);
        initViews();
    }

    @Override
    public void initViews() {
        setTitle(R.string.setting_title);
        showNavigateLeftIcon(true);
        setNavigateLeftOnClickListener(this);

        findViewById(R.id.ll_notification).setOnClickListener(this);
        findViewById(R.id.ll_connect_wifi).setOnClickListener(this);
        findViewById(R.id.ll_about_glass).setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_navigate_left:
                finish();
                overridePendingTransition(R.anim.action_left_enter, R.anim.action_rigth_exit);
                break;
            case R.id.ll_connect_wifi:
                startActivity(new Intent(this, WifiListActivity.class));
                overridePendingTransition(R.anim.action_rigth_enter, R.anim.action_left_exit);
                break;
        }
    }
}
