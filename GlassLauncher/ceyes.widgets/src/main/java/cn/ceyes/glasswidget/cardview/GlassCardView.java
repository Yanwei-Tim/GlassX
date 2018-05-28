/**
 * ****************************************************************************
 * Copyright (C) 2014 Ceyes Inc. All rights reserved.
 * *****************************************************************************
 */

package cn.ceyes.glasswidget.cardview;

import android.content.Context;
import android.util.Log;
import android.widget.FrameLayout;

public class GlassCardView extends FrameLayout implements IGlassEventSender {
    private static final String TAG = GlassCardView.class.getSimpleName();

    protected Context mContext;
    private boolean isVisible = false;

    private IGlassEventDispatcher parentDispatcher;

    public GlassCardView(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public final void sendGlassEvent(int eventCode, Object event) {
        if (this.parentDispatcher == null) {
            Log.e(TAG, "Glass event send must be after bindData() method called.");
            return;
        }

        this.parentDispatcher.dispatchGlassEvent(eventCode, event);
    }

    final void bindData(IGlassEventDispatcher dispatcher) {
        this.parentDispatcher = dispatcher;
        onBindData();
    }

    public void onBindData() {
        /* Client should implement it if needs to bind data after view created... */
    }

    public void onCardVisible() {
        /* card becomes visible */
    }

    public void onCardSelected() {
        /* card is selected by touch or key press */
    }

    public void onCardInvisible() {
        /* card becomes invisible */
    }

    public void onCardRecycled() {
        /* card is to be recycled */
    }

    public void onCardFinished() {
        /* card is going to be destroyed */
    }

    final void onCardVisibilityChanged(boolean visible) {
        if (visible) {
            if (!isVisible) {
                isVisible = true;
                onCardVisible();
            }
        } else {
            if (isVisible) {
                onCardInvisible();
                isVisible = false;
            }
        }
    }
}
