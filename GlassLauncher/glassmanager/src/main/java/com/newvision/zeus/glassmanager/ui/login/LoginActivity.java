package com.newvision.zeus.glassmanager.ui.login;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.droidbyme.dialoglib.DroidDialog;
import com.newvision.zeus.glassmanager.R;
import com.newvision.zeus.glassmanager.ui.BaseActivity;
import com.newvision.zeus.glassmanager.ui.MainActivity;
import com.newvision.zeus.glassmanager.utils.PermissionHelper;
import com.yanzhenjie.permission.PermissionListener;

import java.util.List;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private DroidDialog mDialog;
    private static final int SHOW_PERMISSION_DIALOG = 1;
    private static final int EXIT_APP = 2;

    private TextView btn_login = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        PermissionHelper.checkPermission(this, permissionListener, PermissionHelper.ALL_PERMISSION);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_PERMISSION_DIALOG:
                    mDialog = new DroidDialog.Builder(LoginActivity.this)
                            .title("授权请求")
                            .content("请您授予应用权限，以便应用正常提供服务")
                            .cancelable(false, false)
                            .positiveButton("确定", new DroidDialog.onPositiveListener() {
                                @Override
                                public void onPositive(Dialog dialog) {
                                    PermissionHelper.gotoPermission(LoginActivity.this);
                                    finish();
                                }
                            }).negativeButton("取消", new DroidDialog.onNegativeListener() {
                                @Override
                                public void onNegative(Dialog dialog) {
                                    mHandler.sendEmptyMessage(EXIT_APP);
                                }
                            }).show();
                    break;
                case EXIT_APP:
                    if (mDialog != null) {
                        mDialog.dismiss();
                    }
                    finish();
                    break;
            }
        }
    };

    @Override
    public void initViews() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //设置状态栏和导航栏的颜色
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        btn_login = (TextView) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                break;
        }
    }


    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            Log.d(TAG, "onSucceed");
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            Log.d(TAG, "onFailed");
            mHandler.sendEmptyMessage(SHOW_PERMISSION_DIALOG);
        }
    };


}
