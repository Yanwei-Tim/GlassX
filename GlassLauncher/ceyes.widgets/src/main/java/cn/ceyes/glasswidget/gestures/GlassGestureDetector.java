package cn.ceyes.glasswidget.gestures;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by zhangsong on 1/21/15.
 */
public class GlassGestureDetector {

    private static final String TAG = GlassGestureDetector.class.getSimpleName();

    private boolean DEBUG = false;

    private GestureDetector mGestureDetector;

    private View mTargetView;
    private GlassGestureListener mGestureListener;

    public GlassGestureDetector(final Context context, GlassGestureListener listener) {
        mTargetView = null;
        mGestureListener = listener;

        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            private static final float FLING_MIN_HORIZONTAL_FAST_VELOCITY = 8000;
            private static final float SCROLL_HORIZONTALLY_MAX_ANGLE = 30;

            @Override
            public boolean onDown(MotionEvent e) {
                Log.d(TAG, "onDown");
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                if (DEBUG) {
                    Log.i(TAG, String.format("onFling from (%f, %f) to (%f, %f) with velocity(%f, %f)",
                            e1.getX(), e1.getY(), e2.getX(), e2.getY(), velocityX, velocityY));
                }

                float dx = (int) Math.abs(e2.getX() - e1.getX());
                float dy = (int) Math.abs(e2.getY() - e1.getY());
                double angle = (dx != 0) ? Math.atan(Math.abs(dy / dx)) / 3.1415 * 180.0 : 90.0;
                boolean willScrollHorizontally = (angle <= SCROLL_HORIZONTALLY_MAX_ANGLE);

                if (willScrollHorizontally) {
                    boolean isFlingRight = velocityX > 0;
                    boolean isFlingFast = Math.abs(velocityX) >= FLING_MIN_HORIZONTAL_FAST_VELOCITY;

                    if (isFlingRight) {
                        if (isFlingFast) {
                            Log.d(TAG, "onFlingRightFast");
                            mGestureListener.onFlingRightFast(mTargetView);
                        } else {
                            Log.d(TAG, "onFlingRight");
                            mGestureListener.onFlingRight(mTargetView);
                        }
                    } else {
                        if (isFlingFast) {
                            Log.d(TAG, "onFlingLeftFast");
                            mGestureListener.onFlingLeftFast(mTargetView);
                        } else {
                            Log.d(TAG, "onFlingLeft");
                            mGestureListener.onFlingLeft(mTargetView);
                        }
                    }
                }

                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.i(TAG, "onSingleTapUp");
                mGestureListener.onSingleTap(mTargetView);
                return super.onSingleTapUp(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {// 单击屏幕
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {// 长按屏幕
                super.onLongPress(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {// 双击屏幕
                return super.onDoubleTap(e);
            }
        });
    }

    public boolean onTouchEvent(View view, MotionEvent e) {

        if (DEBUG) {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d(TAG, "Touch Down: " + e.getX() + "," + e.getY());
            } else if (e.getAction() == MotionEvent.ACTION_UP) {
                Log.d(TAG, "Touch Up: " + e.getX() + "," + e.getY());
            } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
                Log.d(TAG, "Touch Move: " + e.getX() + "," + e.getY());
            }
        }

        mTargetView = view;
        return mGestureDetector.onTouchEvent(e);
    }
}
