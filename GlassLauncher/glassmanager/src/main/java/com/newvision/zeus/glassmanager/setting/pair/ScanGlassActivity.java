package com.newvision.zeus.glassmanager.setting.pair;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.newvision.zeus.glasscore.base.IBindServiceStatusListener;
import com.newvision.zeus.glasscore.protocol.entity.GlassDevicesInfo;
import com.newvision.zeus.glasscore.protocol.entity.GlassMessageType;
import com.newvision.zeus.glasscore.protocol.entity.IConnectServerListener;
import com.newvision.zeus.glasscore.protocol.entity.IScanIPFinishListener;
import com.newvision.zeus.glasscore.utils.ScanDeviceHelper;
import com.newvision.zeus.glassmanager.setting.wifi.WifiHelper;
import com.newvision.zeus.glassmanager.ui.BaseActivity;
import com.newvision.zeus.glassmanager.R;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.progressbar.BGAProgressBar;

/**
 * Created by yanjiatian on 2017/7/10.
 */

public class ScanGlassActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = ScanGlassActivity.class.getSimpleName();
    private BGAProgressBar mLoading;
    private static final int START_SCAN = 1;
    private static final int START_CONNECT = 2;
    private static final int LOADING_ANIM = 3;
    private static final int REFRESH_LIST = 4;
    private boolean bindStatus = false;
    private List<String> mIpList = new ArrayList<String>();
    private int index = 0;
    private Button btn_start_scan;

    private ListView mListView;
    private GlassListAdapter mGlassListAdapter;
    private List<GlassDevicesInfo> mGlassListIP;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_glass);
        initViews();
    }

    @Override
    public void initViews() {
        setTitle(R.string.pair_glass);
        showNavigateLeftIcon(true);
        setNavigateLeftOnClickListener(this);
        mLoading = (BGAProgressBar) findViewById(R.id.loading_count);
        mLoading.setProgress(index);
        btn_start_scan = (Button) findViewById(R.id.start_scan);
        btn_start_scan.setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.glass_list);
        mListView.setDividerHeight(2);
        mListView.setCacheColorHint(Color.TRANSPARENT);
        mListView.setDivider(ContextCompat.getDrawable(this, R.drawable.icon_line));
        mGlassListIP = new ArrayList<GlassDevicesInfo>();
        mGlassListIP.add(new GlassDevicesInfo("192.168.0.241", "241"));
        mGlassListIP.add(new GlassDevicesInfo("192.168.0.201", "zuk"));
        mGlassListIP.add(new GlassDevicesInfo("192.168.0.175", "motor"));
        mGlassListIP.add(new GlassDevicesInfo("192.168.0.146", "c210"));
        mGlassListAdapter = new GlassListAdapter(this, mGlassListIP);
        mListView.setAdapter(mGlassListAdapter);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.d(TAG, "长按" + mGlassListIP.get(position).ip);
                Toast.makeText(ScanGlassActivity.this, "长按" + mGlassListIP.get(position).ip, Toast.LENGTH_SHORT).show();
                //需要判断一下是否已经扫描完成，设置不可点击
                if (bindStatus) {
                    mClientServiceBinder.connectServer(mGlassListIP.get(position).ip, new IConnectServerListener() {
                        @Override
                        public void getConnectServerStatus(boolean status, GlassDevicesInfo info) {

                        }

                        @Override
                        public int getType() {
                            return GlassMessageType.CONNECT_SIGNAL_SERVER;
                        }

                        @Override
                        public String getIP() {
                            return mGlassListIP.get(position).ip;
                        }
                    });

                }
                return false;
            }
        });
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START_SCAN:
                    ScanDeviceHelper.getInstance().scan(ScanGlassActivity.this, new IScanIPFinishListener() {
                        @Override
                        public void finish(List<String> ipList) {
                            for (int i = 0; i < ipList.size(); i++) {
                                Log.d(TAG, "ip list :" + ipList.get(i));
                            }
                            mIpList = ipList;
                            mLoading.setMax(mIpList.size());
                            mHandler.sendEmptyMessage(START_CONNECT);
                        }
                    }, WifiHelper.getInstance().isWLANSharingOn());
                    break;
                case START_CONNECT:
                    if (bindStatus && index < mIpList.size()) {
                        mClientServiceBinder.connectServer(mIpList.get(index), new IConnectServerListener() {

                            @Override
                            public void getConnectServerStatus(boolean status, GlassDevicesInfo info) {
                                if (status) { //连接成功，保存IP地址
                                    Log.d(TAG, "connect ip is " + mIpList.get(index) + " index : " + index + "  message : " + info.ip);
                                    mGlassListIP.add(info);
                                    mHandler.sendEmptyMessage(REFRESH_LIST);
                                } else { //连接失败，接着连接下一个

                                }
                                mHandler.sendEmptyMessageDelayed(START_CONNECT, 500);
                                mHandler.sendEmptyMessage(LOADING_ANIM);
                            }

                            @Override
                            public int getType() {
                                return GlassMessageType.GLASS_PAIR;
                            }

                            @Override
                            public String getIP() {
                                return mIpList.get(index);
                            }
                        });

                    }
                    break;

                case LOADING_ANIM:
                    mLoading.setProgress(index + 1);
                    Log.d(TAG, "max : " + mLoading.getMax() + "  size :" + mIpList.size() + " now progress : " + index + " ip : " + mIpList.get(index));
                    index++;
                    if (index == mLoading.getMax()) {
                        mLoading.setVisibility(View.GONE);
                    }
                    break;

                case REFRESH_LIST:
                    mGlassListAdapter.notifyDataSetChanged();
                    break;
            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        bindTcpService(new IBindServiceStatusListener() {
            @Override
            public void bindSuccess() {
                bindStatus = true;
            }

            @Override
            public void unbindSuccess() {
                bindStatus = false;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindTcpService();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_navigate_left:
                finish();
                overridePendingTransition(R.anim.action_left_enter, R.anim.action_rigth_exit);
                break;


            case R.id.start_scan:
                Log.d(TAG, "start scan ...");
                mLoading.setVisibility(View.VISIBLE);
                mHandler.sendEmptyMessage(START_SCAN);
                break;
        }
    }
}
