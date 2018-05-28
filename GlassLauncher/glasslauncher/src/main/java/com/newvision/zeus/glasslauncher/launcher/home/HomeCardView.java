package com.newvision.zeus.glasslauncher.launcher.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.newvision.zeus.glasslauncher.R;

import cn.ceyes.glasswidget.cardview.GlassCardView;

/**
 * Created by zhangsong on 17-6-29.
 */

public class HomeCardView extends GlassCardView {

    private TextView mTimeView; // current time icon

    public HomeCardView(Context context) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.card_home, this);
        mTimeView = (TextView) findViewById(R.id.nowtime);
    }

    @Override
    public void onCardVisible() {
        super.onCardVisible();
        sendGlassEvent(10000, null);
    }

    @Override
    public void onCardInvisible() {
        super.onCardInvisible();
        sendGlassEvent(10001, null);
    }

    public void setTime(String time) {
        mTimeView.setText(time);
    }

    public TextView getTimeView() {
        return mTimeView;
    }
}
