package com.newvision.zeus.glassmanager.common.widgets;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.newvision.zeus.glassmanager.R;
import com.newvision.zeus.glassmanager.entity.IDialogClickListener;
import com.newvision.zeus.glassmanager.entity.IShowPasswordListener;

/**
 * Created by yanjiatian on 2017/7/7.
 * 自定义的dialog
 */

public class CommonDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = CommonDialog.class.getSimpleName();
    private Context mContext;
    private RelativeLayout mParentView;
    private LinearLayout ll_dialog_view;
    private RelativeLayout rl_show_password;
    private IDialogClickListener mIDialogClickListener;
    private IShowPasswordListener mShowPasswordListener;
    private TextView tv_cancel;
    private TextView tv_confirm;
    private TextView dialog_title;
    private ImageView img_show_wifi;

    //normal dialog
    public CommonDialog(Context context, IDialogClickListener listener, int layoutId) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.mContext = context;
        mIDialogClickListener = listener;

        setContentView(layoutId);
        mParentView = (RelativeLayout) findViewById(R.id.parent_view);
        tv_confirm = (TextView) findViewById(R.id.txt_confirm);
        tv_confirm.setOnClickListener(this);
        tv_cancel = (TextView) findViewById(R.id.txt_cancel);
        tv_cancel.setOnClickListener(this);
        dialog_title = (TextView) findViewById(R.id.dialog_tv_title);
        this.setCanceledOnTouchOutside(false);
        this.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (mIDialogClickListener != null) {
                        mIDialogClickListener.onCancel();
                        dismiss();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    //wifi dialog
    public CommonDialog(Context context, IDialogClickListener clickListener, IShowPasswordListener showListener) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.mContext = context;
        this.mIDialogClickListener = clickListener;
        this.mShowPasswordListener = showListener;
        setContentView(R.layout.dialog_wifi);
        mParentView = (RelativeLayout) findViewById(R.id.parent_view);
        ll_dialog_view = (LinearLayout) findViewById(R.id.ll_dialog_view);

        rl_show_password = (RelativeLayout) findViewById(R.id.rl_show_password);
        rl_show_password.setOnClickListener(this);

        tv_confirm = (TextView) findViewById(R.id.txt_confirm);
        tv_confirm.setOnClickListener(this);

        tv_cancel = (TextView) findViewById(R.id.txt_cancel);
        tv_cancel.setOnClickListener(this);

        img_show_wifi = (ImageView) findViewById(R.id.img_show_password);
    }

    public void setDialogView(View view) {
        ll_dialog_view.addView(view);
    }

    public boolean updatePasswordType(EditText edit_wifi_password, boolean wifiPasswordVisible) {
        if (wifiPasswordVisible) {
            // 显示为密码
            edit_wifi_password.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else if (!wifiPasswordVisible) {
            // 显示为普通文本
            edit_wifi_password.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
        // 使光标始终在最后位置
        edit_wifi_password.setSelection(edit_wifi_password.getText().length());
        if (edit_wifi_password.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)) {
            wifiPasswordVisible = true;
        } else {
            wifiPasswordVisible = false;
        }
        return wifiPasswordVisible;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_cancel:
                if (mIDialogClickListener != null) {
                    mIDialogClickListener.onCancel();
                }
                this.dismiss();
                break;
            case R.id.txt_confirm:
                if (mIDialogClickListener != null) {
                    mIDialogClickListener.onConfirm();
                }
                this.dismiss();
                break;
            case R.id.rl_show_password:
                if (mShowPasswordListener != null) {
                    updateCheckBox(mShowPasswordListener.updateCheckBox());
                }
                break;
        }
    }

    private void updateCheckBox(boolean wifiPasswordVisible) {
        if (wifiPasswordVisible) {
            img_show_wifi.setBackgroundResource(R.drawable.icon_check_pre);
        } else {
            img_show_wifi.setBackgroundResource(R.drawable.icon_check_def);
        }
    }

}
