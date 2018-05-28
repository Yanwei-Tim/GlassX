package cn.ceyes.glasswidget.singleview;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import cn.ceyes.glasswidget.gestures.GlassGestureDetector;
import cn.ceyes.glasswidget.gestures.GlassGestureListener;
import cn.ceyes.glasswidget.keyevents.GlassKeyEventDetector;
import cn.ceyes.glasswidget.keyevents.GlassKeyEventListener;
import cn.ceyes.glasswidget.utils.DisplayUtil;
import cn.ceyes.widgets.R;

/**
 * Created by zhangsong on 1/26/15.
 */
public class GlassBaseDialog extends Dialog {

    private static final String TAG = "GlassBaseDialog";

    private Context mContext;
    private View mContentView;
    private GlassGestureDetector mGestureDetector;
    private GlassKeyEventDetector mKeyEventDetector;

    public GlassBaseDialog(Context context) {
        this(context, R.style.DialogMenuTheme);
    }

    public GlassBaseDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    @Override
    public void setContentView(int layoutResID) {
        View contentView = LayoutInflater.from(mContext).inflate(layoutResID, null);
        setContentView(contentView);
    }

    @Override
    public void setContentView(View view) {
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(DisplayUtil.getDisplayWidth(mContext), DisplayUtil.getDisplayHeight(mContext));
        super.setContentView(view, layoutParams);
        mContentView = view;
    }

    public void setOnGestureListener(GlassGestureListener listener) {
        mGestureDetector = new GlassGestureDetector(mContext, listener);
        setOnTouchListener(mContentView);
    }

    public void setOnKeyEventListener(GlassKeyEventListener listener) {
        Log.i(TAG, "setOnKeyEventListener");
        mKeyEventDetector = new GlassKeyEventDetector(listener);
        Log.i(TAG, "after setOnKeyEventListener mKeyEventDetector turn to " + mKeyEventDetector);
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return mKeyEventDetector.onKeyEvent(null, keyCode, event);
            }
        });
    }

    private void setOnTouchListener(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(v, event);
            }
        });
    }

}
