package cn.ceyes.glasswidget.keyevents;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

/**
 * Created by liusong on 2/2/15.
 */
public class GlassKeyEventDetector {
    private static final String TAG = "GlassKeyEventDetector";

    private GlassKeyEventListener mGlassKeyEventListener;

    public GlassKeyEventDetector(GlassKeyEventListener keyEventListener) {
        mGlassKeyEventListener = keyEventListener;
    }

    public boolean onKeyEvent(View v, int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyEvent, keyCode: " + keyCode);
        if (event.getAction() != KeyEvent.ACTION_DOWN) {
            return false;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (mGlassKeyEventListener != null)
                    mGlassKeyEventListener.onPreviousKey();
                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (mGlassKeyEventListener != null)
                    mGlassKeyEventListener.onNextKey();
                break;

            case KeyEvent.KEYCODE_ENTER:
                if (mGlassKeyEventListener != null)
                    mGlassKeyEventListener.onEnterKey();
                break;

            case KeyEvent.KEYCODE_BACK:
                if (mGlassKeyEventListener != null)
                    mGlassKeyEventListener.onBackKey();
                break;

            default:
                return false;
        }

        return true;
    }
}
