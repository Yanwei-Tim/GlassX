package com.newvision.zeus.glassmanager.setting.wifi;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.droidbyme.dialoglib.DroidDialog;
import com.newvision.zeus.glassmanager.common.widgets.WiFiAPDialog;
import com.newvision.zeus.glassmanager.entity.IWifiDialogListener;
import com.newvision.zeus.glassmanager.entity.IWifiListObserver;
import com.newvision.zeus.glassmanager.ui.BaseActivity;
import com.newvision.zeus.glassmanager.R;
import com.newvision.zeus.glassmanager.entity.IDialogClickListener;
import com.newvision.zeus.glassmanager.entity.IShowPasswordListener;
import com.newvision.zeus.glassmanager.common.widgets.CommonDialog;
import com.newvision.zeus.glassmanager.utils.SystemUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * Created by yanjiatian on 2017/7/4.
 */

public class WifiListActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = WifiListActivity.class.getSimpleName();
    private static final int DISMISS_LOCATION_DIALOG = 1;
    private static final int OPEN_WIFI = 2;
    private ListView mWifiList;
    private List<ScanResult> mScanResults;
    private WifiListAdapter mWifiListAdapter;
    private ScanResult mCurResult = null;
    private ProgressBar mWaitProgress;
    private LinearLayout mNoWifiHint;

    private CommonDialog mOpenWifiDialog;
    private CommonDialog mDialog;
    private View mDialogView;
    private TextView mDialogSSID;
    private EditText mDialogPassword;
    private boolean mShowPassword = false;
    private DroidDialog mDialogLocation;

    private boolean isConnecting;// if is connecting, the wifi list does not changed.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_list);
        checkPermission();
        initViews();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DISMISS_LOCATION_DIALOG:
                    mDialogLocation.dismiss();
                    break;
                case OPEN_WIFI:
                    mOpenWifiDialog = new CommonDialog(WifiListActivity.this, new IDialogClickListener() {
                        @Override
                        public void onConfirm() {
                            WifiHelper.getInstance().openWifi();
                        }

                        @Override
                        public void onCancel() {

                        }
                    }, R.layout.dialog_open_wifi);
                    mOpenWifiDialog.show();
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        checkWifi();
    }

    @Override
    protected void onPause() {
        super.onPause();
        WifiHelper.getInstance().unregisterObserver(mWifiListObserver);
    }

    private void checkWifi() {
        if (!WifiHelper.getInstance().isWifiConnected()) {
            if (WifiHelper.getInstance().isWLANSharingOn()) {
                mWaitProgress.setVisibility(View.GONE);
                //开启热点
                openWiFiAP();
            } else {
                //wifi及个人热点均未开启，自动开启wifi
                mHandler.sendEmptyMessageDelayed(OPEN_WIFI, 200);
            }
        }
        // register wifi scan results observer.
        WifiHelper.getInstance().registerObserver(mWifiListObserver);
    }

    private void openWiFiAP() {
        Log.i(TAG, "wifi ap open...");
        WiFiAPDialog apDialog = new WiFiAPDialog(this, new IWifiDialogListener() {
            @Override
            public void onOpenAP() {
                String ssid = WifiHelper.getInstance().getWifiApSSID();
                String pwd = WifiHelper.getInstance().getWifiApSharedKey();
                Log.d(TAG, "ssid = " + ssid + " pwd " + pwd);
                startWifiQRActivity(createWifiString(ssid, pwd, WifiHelper.WIFI_CIPHER_WPA), ssid);
            }

            @Override
            public void onOpenWiFi() {
                mHandler.sendEmptyMessage(OPEN_WIFI);
            }

            @Override
            public void onCancel() {
                WifiListActivity.this.finish();
            }
        });
        apDialog.show();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23 && !SystemUtils.checkGPSIsOpen(WifiListActivity.this)) {
            mDialogLocation = new DroidDialog.Builder(WifiListActivity.this)
                    .title("权限授予")
                    .content("需要打开系统定位开关")
                    .cancelable(false, false)
                    .positiveButton("确定", new DroidDialog.onPositiveListener() {
                        @Override
                        public void onPositive(Dialog dialog) {
                            mHandler.sendEmptyMessage(DISMISS_LOCATION_DIALOG);
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                        }
                    }).negativeButton("取消", new DroidDialog.onNegativeListener() {
                        @Override
                        public void onNegative(Dialog dialog) {
                            mHandler.sendEmptyMessage(DISMISS_LOCATION_DIALOG);
                            finish();
                        }
                    }).show();
        }
    }

    @Override
    public void initViews() {
        setTitle(R.string.connect_wifi);
        showNavigateLeftIcon(true);
        setNavigateLeftOnClickListener(this);
        showNavigateRightIcon(true);
        setNavigateRightImage(R.drawable.icon_add);
        setNavigateRightOnClickListener(this);
        mWaitProgress = (ProgressBar) findViewById(R.id.progress_wait);
        mNoWifiHint = (LinearLayout) findViewById(R.id.no_wifi);
        mWifiList = (ListView) findViewById(R.id.wifi_list);
        mWifiList.setDividerHeight(2);
        mWifiList.setCacheColorHint(Color.TRANSPARENT);
        mWifiList.setDivider(ContextCompat.getDrawable(this, R.drawable.icon_line));
        mScanResults = new ArrayList<>();
        mWifiListAdapter = new WifiListAdapter(this, mScanResults);
        mWifiList.setAdapter(mWifiListAdapter);
        mWifiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurResult = mScanResults.get(position);
                final int wifiType = WifiHelper.getInstance().getWifiType(mCurResult);
                if (wifiType == WifiHelper.WIFI_CIPHER_NO_PASS) {

                } else {
                    inflateDialogView();
                    mDialogSSID.setText(mCurResult.SSID);
                    isConnecting = true;
                    mDialog = new CommonDialog(WifiListActivity.this, new IDialogClickListener() {
                        @Override
                        public void onConfirm() {
                            String pwd = mDialogPassword.getText().toString().trim();
                            if (pwd.equals("") || pwd == null) {
                                Toasty.warning(WifiListActivity.this, getString(R.string.hint_wifi_pwd), Toast.LENGTH_SHORT, true).show();
                            } else if (pwd.length() < 8) {
                                Toasty.error(WifiListActivity.this, getString(R.string.wifi_pwd_error), Toast.LENGTH_SHORT, true).show();
                                mDialogPassword.setText("");
                            } else {
                                mDialogPassword.setText("");
                                startWifiQRActivity(createWifiString(mCurResult.SSID, pwd, WifiHelper.getInstance().getWifiType(mCurResult)), mCurResult.SSID);
                            }
                        }

                        @Override
                        public void onCancel() {
                            isConnecting = false;
                        }
                    }, new IShowPasswordListener() {
                        @Override
                        public boolean updateCheckBox() {
                            mShowPassword = mDialog.updatePasswordType(mDialogPassword, mShowPassword);
                            return mShowPassword;
                        }
                    });
                    mDialog.setDialogView(mDialogView);
                    mDialog.show();

                }
            }
        });
    }

    private void inflateDialogView() {
        mDialogView = getLayoutInflater().inflate(R.layout.dialog_wifi_config, null);
        mDialogSSID = (TextView) mDialogView.findViewById(R.id.tv_ssid);
        mDialogPassword = (EditText) mDialogView.findViewById(R.id.edit_password);
    }

    private void startWifiQRActivity(String msg, String ssid) {
        Intent intent = new Intent(WifiListActivity.this, GlassWifiQRCodeActivity.class);
        intent.putExtra("msg", msg);
        intent.putExtra("ssid", ssid);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_navigate_left:
                finish();
                overridePendingTransition(R.anim.action_left_enter, R.anim.action_rigth_exit);
                break;
            case R.id.img_navigate_right:
                break;
        }
    }

    private String createWifiString(String ssid, String pwd, int crypt_type) {
        //wifi protocol: {"type":0,"ssid":"TP-LINK_OFFICE","pwd":"a1b2c3d4","crypt_type":3}
        JSONObject jsonObj = new JSONObject();
        byte[] bytes = null;
        try {
            jsonObj.put("type", 0);
            jsonObj.put("ssid", ssid);
            jsonObj.put("pwd", pwd);
            jsonObj.put("crypt_type", crypt_type); // wifi类型
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj.toString();
    }

    private IWifiListObserver mWifiListObserver = new IWifiListObserver() {

        @Override
        public void onWifiListChanged(List<ScanResult> results) {
            Log.i(TAG, "onWifiListChanged");
            mWaitProgress.setVisibility(View.GONE);
            if (isConnecting)
                return;
            mScanResults.clear();
            for (int i = 0; i < results.size(); i++) {
                if (results.get(i).frequency >= 5000) {
                    Log.d(TAG, results.get(i).SSID + "~~~" + results.get(i).frequency);
                    results.remove(i);
                }
            }
            mScanResults.addAll(results);
            if (results.size() == 0) {
                mNoWifiHint.setVisibility(View.VISIBLE);
            } else {
                mNoWifiHint.setVisibility(View.GONE);
            }
            mWifiListAdapter.notifyDataSetChanged();
        }

        @Override
        public void onWifiStateChanged(int wifiState) {
            if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
                mWaitProgress.setVisibility(View.GONE);
                mScanResults.clear();
                mWifiListAdapter.notifyDataSetChanged();
            } else if (wifiState == WifiManager.WIFI_STATE_ENABLING) {
                mWaitProgress.setVisibility(View.VISIBLE);
            }
        }
    };
}
