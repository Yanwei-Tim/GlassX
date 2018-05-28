package com.newvision.zeus.glassmanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.newvision.zeus.glasscore.base.IBindServiceStatusListener;
import com.newvision.zeus.glasscore.helper.GlassRequestHelper;
import com.newvision.zeus.glasscore.protocol.entity.GlassConstants;
import com.newvision.zeus.glasscore.protocol.entity.GlassErrorCode;
import com.newvision.zeus.glasscore.protocol.entity.GlassMessageType;
import com.newvision.zeus.glasscore.protocol.entity.IMessageCallback;
import com.newvision.zeus.glassmanager.R;
import com.newvision.zeus.glassmanager.test.CameraTestActivity;

/**
 * Created by Qing Jiwei on 8/10/17.
 */


public class HomeMainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    @Override
    public void initViews() {
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        bindTcpService(new IBindServiceStatusListener() {
            @Override
            public void bindSuccess() {
                Log.i(TAG, "bindSuccess: success");
                GlassRequestHelper.getInstance().registerListener(mClientServiceBinder, callback);
            }

            @Override
            public void unbindSuccess() {
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        GlassRequestHelper.getInstance().unregisterListener(mClientServiceBinder, callback);
        unbindTcpService();
    }

    private IMessageCallback callback = new IMessageCallback() {
        @Override
        public void getResult(int tag, Object result) {
            Log.i(TAG, "getResult: message type=" + Integer.toHexString(tag));
            switch (tag) {
                case GlassMessageType.GLASS_OPEN_APP_ASK:
                    if (((String) result).equals(GlassConstants.INNER_APP_CAMERA)) {
                        startActivity(new Intent(HomeMainActivity.this, CameraTestActivity.class));
//                        GlassAskUtils.sendOpenAppAns(mClientServiceBinder, GlassConstants.INNER_APP_CAMERA);
                        GlassRequestHelper.getInstance().openInternalAppAns(mClientServiceBinder, GlassConstants.INNER_APP_CAMERA, GlassErrorCode.OK);
                        finish();
                    }

                    break;
                case GlassMessageType.PHONE_OPEN_APP_ANS:
                    Log.i(TAG, "getResult: PHONE_START_CAMERA_ANS");
                    if (((String) result).equals(GlassConstants.INNER_APP_CAMERA)) {
                        startActivity(new Intent(HomeMainActivity.this, CameraTestActivity.class));
                        finish();
                    }
                    break;

            }
        }
    };
}
