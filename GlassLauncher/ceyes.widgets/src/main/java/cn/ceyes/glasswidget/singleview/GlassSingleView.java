package cn.ceyes.glasswidget.singleview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import cn.ceyes.glasswidget.gestures.GlassGestureDetector;
import cn.ceyes.glasswidget.gestures.GlassGestureListener;
import cn.ceyes.glasswidget.keyevents.GlassKeyEventDetector;
import cn.ceyes.glasswidget.keyevents.GlassKeyEventListener;

/**
 * Created by zhangsong on 3/3/15.
 */
public class GlassSingleView extends FrameLayout {

    protected Context context;

    public GlassSingleView(Context context) {
        this(context, null);
    }

    public GlassSingleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesturedDetector.onTouchEvent(v, event);
            }
        });

        this.setFocusable(true);
        this.requestFocus();
        this.setFocusableInTouchMode(true);
        this.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return keyEventDector.onKeyEvent(v, keyCode, event);
            }
        });
    }

    public void onViewVisible() {
        /* card becomes visible */
    }

    /* card is selected by touch or key press */
    public void onViewTaped() {

    }

    public void onViewInvisible() {
        /* card becomes invisible */
    }

    /* card is going to be destroyed */
    public void onViewFlingDown() {
        ((Activity) context).finish();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        switch (visibility) {
            case View.VISIBLE:
                onViewVisible();
                break;

            case View.INVISIBLE: // invisible but still takes up space for layout purposes.
            case View.GONE:      // invisible and it doesn't take any space for layout
                onViewInvisible();
                break;

            default:
                break;
        }

        super.onWindowVisibilityChanged(visibility);
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        super.setOnTouchListener(l);
    }

    private GlassGestureDetector gesturedDetector = new GlassGestureDetector(context, new GlassGestureListener() {
        @Override
        public void onSingleTap(View v) {
            super.onSingleTap(v);
            onViewTaped();
        }
    });

    private GlassKeyEventDetector keyEventDector = new GlassKeyEventDetector(new GlassKeyEventListener() {
        @Override
        public void onBackKey() {
            super.onBackKey();
            onViewFlingDown();
        }

        @Override
        public void onEnterKey() {
            super.onEnterKey();
            onViewTaped();
        }
    });
}
