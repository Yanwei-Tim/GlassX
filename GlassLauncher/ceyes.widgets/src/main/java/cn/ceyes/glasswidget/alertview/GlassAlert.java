package cn.ceyes.glasswidget.alertview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import cn.ceyes.glasswidget.gestures.GlassGestureListener;
import cn.ceyes.glasswidget.keyevents.GlassKeyEventListener;
import cn.ceyes.glasswidget.singleview.GlassBaseDialog;

/**
 * Created by zhangsong on 2/2/15.
 */
public class GlassAlert {

    public interface IGlassAlertClickCallback {
        void onAlertClicked(int alertEntityId);
    }

    /**
     * dismissed automatically after 2s or an assigned time.
     */
    public interface IGlassAlertDismissCallback {
        void onAlertDismissed(int alertEntityId, boolean forced);
    }

    /**
     * cancelled by user manually.
     */
    public interface IGlassAlertCancelCallback {
        void onAlertCancelled(int alertEntityId);
    }

    private static final String TAG = "GlassAlert";

    private static final int DISMISS_DELAY = 2000;
    private static final int MSG_ALERT_DISMISS = 0;

    private IGlassAlertDismissCallback mDismissCallback = null;
    private IGlassAlertClickCallback mClickCallback = null;
    private IGlassAlertCancelCallback mCancelCallback = null;

    private GlassBaseDialog mBaseDialog;

    private GlassAlertEntity mAlertEntity = null;
    private Context mContext = null;

    private GlassAlertCardView mAlertCardView = null;

    public GlassAlert(Context context) {
        this.mContext = context;
        mBaseDialog = new GlassBaseDialog(context);
        mAlertCardView = new GlassAlertCardView(mContext);
        mBaseDialog.setContentView(mAlertCardView);
        mBaseDialog.setOnGestureListener(new GlassGestureListener() {
            @Override
            public void onSingleTap(View v) {
                if (mAlertEntity.isClickable() && mClickCallback != null) {
                    mClickCallback.onAlertClicked(mAlertEntity.getAlertId());
                }
            }
        });

        mBaseDialog.setOnKeyEventListener(new GlassKeyEventListener() {
            @Override
            public void onEnterKey() {
                if (mAlertEntity.isClickable() && mClickCallback != null) {
                    mClickCallback.onAlertClicked(mAlertEntity.getAlertId());
                }
            }

            @Override
            public void onBackKey() {
                if (mAlertEntity.isCancelable()) {
                    if (mCancelCallback != null) {
                        mCancelCallback.onAlertCancelled(mAlertEntity.getAlertId());
                    }
                    GlassAlert.this.dismiss();
                }
            }
        });
    }

    public GlassAlert setAlertEntity(GlassAlertEntity alertEntity) {

        mAlertEntity = alertEntity;
        mAlertCardView.setAlertEntity(alertEntity);

        return this;
    }

    public boolean isCancelable() {
        if (mAlertEntity == null) {
            return false;
        }
        return mAlertEntity.isCancelable();
    }

    public void show() {

        mBaseDialog.show();

        if (mAlertEntity.isAutoDismiss()) {
            if (mAlertEntity.getProgressbarTime() > 0) {
                mAlertCardView.setOnDismissCallback(new GlassAlertCardView.IAlertViewDismissCallback() {
                    @Override
                    public void onDismissed() {
                        mHandler.sendEmptyMessage(MSG_ALERT_DISMISS);
                    }
                });
            } else if (mAlertEntity.getTimeoutMillis() > 0) {
                mHandler.sendEmptyMessageDelayed(MSG_ALERT_DISMISS, mAlertEntity.getTimeoutMillis());
            } else {
                mHandler.sendEmptyMessageDelayed(MSG_ALERT_DISMISS, DISMISS_DELAY);
            }
        }
    }

    public void dismiss() {
        dismiss(true);
    }

    public boolean isShowing() {
        return mBaseDialog.isShowing();
    }

    public GlassAlert setOnAlertDismissCallback(IGlassAlertDismissCallback callback) {
        mDismissCallback = callback;
        return this;
    }

    public GlassAlert setOnAlertCancelCallback(IGlassAlertCancelCallback callback) {
        mCancelCallback = callback;
        return this;
    }

    public GlassAlert setOnClickCallback(IGlassAlertClickCallback callback) {
        mClickCallback = callback;
        return this;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_ALERT_DISMISS:
                    Log.i(TAG, "onAlertDismissed callback 01!");
                    dismiss(false);
                    break;
            }
        }
    };

    private void dismiss(boolean fromUser) {
        if (!mBaseDialog.isShowing()) {
            return;
        }

        mAlertCardView.setOnDismissCallback(null);
        mHandler.removeCallbacksAndMessages(null);
        mBaseDialog.dismiss();

        if (mDismissCallback != null && mAlertEntity != null) {
            Log.i(TAG, "onAlertDismissed callback!");
            mDismissCallback.onAlertDismissed(mAlertEntity.getAlertId(), fromUser);
        }
    }
}
