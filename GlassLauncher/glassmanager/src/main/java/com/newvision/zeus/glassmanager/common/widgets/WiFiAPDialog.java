package com.newvision.zeus.glassmanager.common.widgets;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.newvision.zeus.glassmanager.R;
import com.newvision.zeus.glassmanager.entity.IWifiDialogListener;

/**
 * Created by yanjiatian on 2017/9/8.
 */

public class WiFiAPDialog extends Dialog implements View.OnClickListener {

    private IWifiDialogListener mListener;

    public WiFiAPDialog(Context context, IWifiDialogListener listener) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mListener = listener;
        setContentView(R.layout.layout_ap_dialog);
        TextView tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        TextView tv_open_wifi = (TextView) findViewById(R.id.tv_open_wifi);
        TextView tv_ap = (TextView) findViewById(R.id.tv_ap);
        tv_cancel.setOnClickListener(this);
        tv_open_wifi.setOnClickListener(this);
        tv_ap.setOnClickListener(this);
        this.setCanceledOnTouchOutside(false);
        this.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    // 操作
                    if (mListener != null) {
                        mListener.onCancel();
                        dismiss();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_cancel:
                if (mListener != null) {
                    mListener.onCancel();
                }
                this.dismiss();
                break;
            case R.id.tv_open_wifi:
                if (mListener != null) {
                    mListener.onOpenWiFi();
                }
                this.dismiss();
                break;
            case R.id.tv_ap:
                if (mListener != null) {
                    mListener.onOpenAP();
                }
                this.dismiss();
                return;
        }
    }
}
