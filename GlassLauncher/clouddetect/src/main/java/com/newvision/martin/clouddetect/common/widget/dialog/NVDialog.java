package com.newvision.martin.clouddetect.common.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.newvision.martin.clouddetect.R;
import com.newvision.martin.clouddetect.common.util.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangsong on 17-1-4.
 */

public class NVDialog {
    private static final String TAG = "CeyesDialog";

    protected Context context;
    private Dialog dialog;

    private View contentView;
    private FrameLayout contentContainer;
    private LinearLayout btnContainer;
    private List<String> btnList = new ArrayList<>();

    public NVDialog(Context context) {
        this.context = context;
        dialog = new Dialog(context, R.style.DialogTheme);
        contentView = LayoutInflater.from(context).inflate(R.layout.layout_ceyes_dialog, null);
        contentContainer = (FrameLayout) findViewById(R.id.container_content);
        btnContainer = (LinearLayout) findViewById(R.id.container_btn);
        dialog.setContentView(contentView);
        dialog.setCanceledOnTouchOutside(false);
    }

    public NVDialog addContentView(int layoutId) {
        contentContainer.addView(LayoutInflater.from(context).inflate(layoutId, null));
        return this;
    }

    public NVDialog addContentView(View v) {
        contentContainer.addView(v);
        return this;
    }

    public NVDialog addButton(String name, View.OnClickListener listener) {
        btnList.add(name);
        btnContainer.setWeightSum(btnList.size());
        if (btnList.size() == 1) {
            btnContainer.addView(getButton(name, listener));
            return this;
        }

        btnContainer.addView(getSplit());
        btnContainer.addView(getButton(name, listener));
        return this;
    }


    public NVDialog show() {
        dialog.show();
        return this;
    }

    public NVDialog dismiss() {
        dialog.dismiss();
        return this;
    }

    public boolean isShowing() {

        return dialog.isShowing();
    }

    public View findViewById(int resId) {
        return contentView.findViewById(resId);
    }

    private View getSplit() {
        View v = new View(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DisplayUtil.dip2px(context, 1),
                ViewGroup.LayoutParams.MATCH_PARENT);
        v.setLayoutParams(params);
        v.setBackgroundColor(context.getResources().getColor(R.color.province_line_border));
        return v;
    }

    private TextView getButton(String name, View.OnClickListener listener) {
        TextView tv = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        tv.setLayoutParams(params);
        tv.setPadding(0, DisplayUtil.dip2px(context, 10), 0, DisplayUtil.dip2px(context, 10));
        tv.setBackgroundColor(Color.parseColor("#00000000"));
        tv.setText(name);
        tv.setTextColor(Color.parseColor("#3A7AE5"));
        tv.setTextSize(14);
        tv.setGravity(Gravity.CENTER);
        tv.setOnClickListener(listener);
        return tv;
    }
}
