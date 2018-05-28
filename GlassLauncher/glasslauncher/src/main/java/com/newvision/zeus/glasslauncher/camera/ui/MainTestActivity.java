package com.newvision.zeus.glasslauncher.camera.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.newvision.zeus.glasscore.base.GlassCoreServerActivity;
import com.newvision.zeus.glasscore.base.IBindServiceStatusListener;
import com.newvision.zeus.glasscore.helper.PhoneRequestHelper;
import com.newvision.zeus.glasscore.protocol.entity.GlassConstants;
import com.newvision.zeus.glasscore.protocol.entity.IMessageCallback;
import com.newvision.zeus.glasslauncher.R;


public class MainTestActivity extends GlassCoreServerActivity implements View.OnClickListener {

    private static final String TAG = "MainTestActivity";
    LinearLayout mPlayerLayout;
    Button btn_sn;
    Button btn_login_status;
    Button btn_volume;
    Button btn_parameter;
    Button btn_yuv_pull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_main);
        initView();
    }


    public void initView() {
        btn_sn = (Button) findViewById(R.id.sn);
        btn_sn.setOnClickListener(this);

        btn_login_status = (Button) findViewById(R.id.login_status);
        btn_login_status.setOnClickListener(this);
        btn_volume = (Button) findViewById(R.id.volume);
        btn_volume.setOnClickListener(this);
        btn_parameter = (Button) findViewById(R.id.parameter);
        btn_parameter.setOnClickListener(this);
        btn_yuv_pull = (Button) findViewById(R.id.yuv_pull);
        btn_yuv_pull.setOnClickListener(this);
        findViewById(R.id.set_sn).setOnClickListener(this);
        findViewById(R.id.set_volume).setOnClickListener(this);
        findViewById(R.id.sys_date).setOnClickListener(this);
        findViewById(R.id.version).setOnClickListener(this);

        findViewById(R.id.set_parameter).setOnClickListener(this);
        findViewById(R.id.bt_to_set).setOnClickListener(this);

        mPlayerLayout = (LinearLayout) findViewById(R.id.ll_test);

    }


    @Override
    protected void onResume() {
        super.onResume();
        bindTcpService(new IBindServiceStatusListener() {
            @Override
            public void bindSuccess() {
                Log.i(TAG, "bindSuccess: ");
                mServerServiceBinder.registerListener(callback);
            }

            @Override
            public void unbindSuccess() {
                Log.i(TAG, "unbindSuccess: ");
                mServerServiceBinder.unregisterListener(callback);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mServerServiceBinder.unregisterListener(callback);
        unbindTcpService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private IMessageCallback callback = new IMessageCallback() {
        @Override
        public void getResult(int tag, Object result) {
            switch (tag) {

            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_status:
                showToast("login status :" + mServerServiceBinder.getLoginGlassStatus());
                break;
            case R.id.sn:
//                GlassAskUtils.sendOpenAppAsk(mServerServiceBinder, GlassConstants.INNER_APP_CAMERA);
                PhoneRequestHelper.getInstance().openInternalApp(mServerServiceBinder,GlassConstants.INNER_APP_CAMERA);
                break;
            case R.id.set_sn:
//                GlassAskUtils.sendStartPreviewAsk(mServerServiceBinder);
                PhoneRequestHelper.getInstance().startCameraPreview(mServerServiceBinder);
                break;
            case R.id.yuv_pull:
//                GlassAskUtils.startPushH264(mServerServiceBinder, new byte[5]);
                PhoneRequestHelper.getInstance().sendVideoStream(mServerServiceBinder,new byte[5]);
                break;
            case R.id.sys_date:
//                GlassAskUtils.startPushPhoto(mServerServiceBinder, new byte[5]);
                PhoneRequestHelper.getInstance().sendFileData(mServerServiceBinder,new byte[5]);
                break;
        }
    }


    private void showToast(String msg) {
        Toast.makeText(MainTestActivity.this, msg, Toast.LENGTH_SHORT).show();
    }


}
