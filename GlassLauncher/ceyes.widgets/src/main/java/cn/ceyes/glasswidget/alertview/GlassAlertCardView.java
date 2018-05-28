package cn.ceyes.glasswidget.alertview;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import cn.ceyes.glasswidget.cardview.GlassCardView;
import cn.ceyes.widgets.R;

/**
 * Created by zhangsong on 12/31/14.
 */
public class GlassAlertCardView extends GlassCardView {

    public interface IAlertViewDismissCallback {
        void onDismissed();
    }

    private static final int MSG_PROGRESSING = 0;
    private int step_length = 10;
    // horizon views
    private RelativeLayout horizon_hint = null;
    private ImageView hor_icon = null;
    private TextView hor_tv = null;
    private TextView hor_tip = null;
    private ImageView hor_refresh = null;
    // vertical views
    private RelativeLayout vertical_hint = null;
    private ImageView ver_icon = null;
    private TextView ver_tv = null;
    private TextView ver_tip = null;
    private ImageView ver_refresh = null;
    // global progressbar
    private ProgressBar progressbar = null;

    private Timer mTimer = null;
    private TimerTask mTimerTask = null;

    private IAlertViewDismissCallback mDismissCallback;

    public GlassAlertCardView(Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.card_alert, this);
        // horizon views
        horizon_hint = (RelativeLayout) view.findViewById(R.id.horizon_hint);
        hor_icon = (ImageView) view.findViewById(R.id.hor_icon);
        hor_tv = (TextView) view.findViewById(R.id.hor_tv);
        hor_tip = (TextView) view.findViewById(R.id.hor_tip);
        hor_refresh = (ImageView) view.findViewById(R.id.hor_refresh);
        // vertical views
        vertical_hint = (RelativeLayout) view.findViewById(R.id.vertical_hint);
        ver_icon = (ImageView) view.findViewById(R.id.ver_icon);
        ver_tv = (TextView) view.findViewById(R.id.ver_tv);
        ver_tip = (TextView) view.findViewById(R.id.ver_tip);
        ver_refresh = (ImageView) view.findViewById(R.id.ver_refresh);

        progressbar = (ProgressBar) view.findViewById(R.id.progressbar);
    }

    public void setOnDismissCallback(IAlertViewDismissCallback callback) {
        mDismissCallback = callback;
    }

    private void initViewsVisibility() {
        horizon_hint.setVisibility(View.GONE);
        hor_tip.setVisibility(View.GONE);
        hor_refresh.setVisibility(View.GONE);

        vertical_hint.setVisibility(View.GONE);
        ver_refresh.setVisibility(View.GONE);

        progressbar.setVisibility(View.GONE);

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    public void setAlertEntity(GlassAlertEntity alertEntity) {
        initViewsVisibility();
        switch (alertEntity.getOrientation()) {
            case GlassAlertEntity.ORIENTATION_HORIZONTAL:
                horizon_hint.setVisibility(View.VISIBLE);
                hor_icon.setImageResource(alertEntity.getIconResource());

                Object titleObj = alertEntity.getAlertTitle();
                if (titleObj instanceof Integer) {
                    hor_tv.setText((Integer) titleObj);
                } else if (titleObj instanceof String) {
                    hor_tv.setText((String) titleObj);
                }

                if (alertEntity.getRefreshResource() > 0) {
                    hor_refresh.setVisibility(View.VISIBLE);
                    hor_refresh.setBackgroundResource(alertEntity.getRefreshResource());
                    AnimationDrawable rocketAnimation = (AnimationDrawable) hor_refresh
                            .getBackground();
                    rocketAnimation.start();
                }
                break;
            case GlassAlertEntity.ORIENTATION_VERTICAL:
                vertical_hint.setVisibility(View.VISIBLE);
                ver_icon.setImageResource(alertEntity.getIconResource());

                Object titleObj2 = alertEntity.getAlertTitle();
                if (titleObj2 instanceof Integer) {
                    ver_tv.setText((Integer) titleObj2);
                } else if (titleObj2 instanceof String) {
                    ver_tv.setText((String) titleObj2);
                }

                Object contentObj = alertEntity.getAlertContent();
                if (contentObj instanceof Integer) {
                    ver_tip.setText((Integer) contentObj);
                } else if (contentObj instanceof String) {
                    ver_tip.setText((String) contentObj);
                }

                if (alertEntity.getRefreshResource() > 0) {
                    ver_refresh.setVisibility(View.VISIBLE);
                    ver_refresh.setBackgroundResource(alertEntity.getRefreshResource());
                    AnimationDrawable rocketAnimation = (AnimationDrawable) ver_refresh
                            .getBackground();
                    rocketAnimation.start();
                }
                break;
        }
        if (alertEntity.getProgressbarTime() > 0) {
            progressbar.setVisibility(View.VISIBLE);
            progressbar.setMax(alertEntity.getProgressbarTime());
            progressbar.setProgress(0);
            startProgress();
        }
    }

    private void startProgress() {
        mTimer = new Timer(true);
        mTimerTask = new TimerTask() {
            public void run() {
                timerHandler.obtainMessage(MSG_PROGRESSING).sendToTarget();
            }
        };
        mTimer.schedule(mTimerTask, 0, step_length); // 延时0ms后执行，10ms执行一次
    }

    private Handler timerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_PROGRESSING:
                    int progress = progressbar.getProgress();
                    progress += step_length;
                    progressbar.setProgress(progress);

                    if (progressbar.getProgress() == progressbar.getMax()) {
                        if (mDismissCallback != null)
                            mDismissCallback.onDismissed();

                        if (mTimerTask != null) {
                            mTimerTask.cancel();
                        }
                        if (mTimer != null) {
                            mTimer.cancel();
                        }
                    }

                    break;
            }
        }
    };

}
