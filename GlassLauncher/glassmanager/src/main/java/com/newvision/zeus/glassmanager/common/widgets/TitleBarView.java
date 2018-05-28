package com.newvision.zeus.glassmanager.common.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.newvision.zeus.glassmanager.R;

/**
 * Created by yanjiatian on 2017/7/4.
 */

public class TitleBarView extends LinearLayout {
    private ImageView img_navigate_left;
    private ImageView img_navigate_right;
    private TextView tv_title;

    public TitleBarView(Context context) {
        super(context, null);
    }

    public TitleBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View title_bar = LayoutInflater.from(context).inflate(R.layout.detail_title_bar, this);
        img_navigate_left = (ImageView) title_bar.findViewById(R.id.img_navigate_left);
        img_navigate_right = (ImageView) title_bar.findViewById(R.id.img_navigate_right);
        tv_title = (TextView) title_bar.findViewById(R.id.tv_title);
    }

    public void setTitle(int id) {
        setTitle(getResources().getString(id));
    }

    public void setTitle(String title) {
        tv_title.setText(title);
    }

    public void showNavigateLeftIcon(boolean show) {
        img_navigate_left.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setNavigateLeftImage(int id) {
        img_navigate_left.setImageResource(id);
    }

    public void showNavigateRightIcon(boolean show) {
        img_navigate_right.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setNavigateRightImage(int id) {
        img_navigate_right.setImageResource(id);
    }

    public ImageView getNavigateLeftView() {
        return img_navigate_left;
    }

    public ImageView getNavigateRightView() {
        return img_navigate_right;
    }

    public void setNavigateLeftOnClickListener(OnClickListener listener) {
        img_navigate_left.setOnClickListener(listener);
    }

    public void setNavigateRightOnClickListener(OnClickListener listener) {
        img_navigate_right.setOnClickListener(listener);
    }
}
