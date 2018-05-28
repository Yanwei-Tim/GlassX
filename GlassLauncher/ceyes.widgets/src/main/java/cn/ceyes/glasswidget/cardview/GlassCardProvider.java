package cn.ceyes.glasswidget.cardview;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

/**
 * Created by zhangsong on 17-6-29.
 */

public abstract class GlassCardProvider implements IGlassEventSender, IGlassEventDispatcher, IGlassEventListener {
    private static final String TAG = "AbsGlassCardProvider";

    private IGlassEventDispatcher parentDispatcher;

    @Override
    public final void sendGlassEvent(int eventCode, Object event) {
        if (this.parentDispatcher == null) {
            Log.e(TAG, "Glass event send must be after bindData() method called.");
            return;
        }
        this.parentDispatcher.dispatchGlassEvent(eventCode, event);
    }

    @Override
    public final void dispatchGlassEvent(int eventCode, Object event) {
        if (onGlassEvent(eventCode, event)) return;
        if (this.parentDispatcher == null) {
            Log.e(TAG, "No parent GlassEventDispatcher.");
            return;
        }
        this.parentDispatcher.dispatchGlassEvent(eventCode, event);
    }

    @Override
    public boolean onGlassEvent(int eventCode, Object event) {
        return false;
    }

    public abstract String getCardProviderId();

    public abstract GlassCardView onCreateView(Context context, ViewGroup parent);

    /**
     * 在{@link GlassCardManager}中调用以构造责任链
     * <p>
     * Can not implements in sub classes.
     *
     * @param dispatcher
     */
    final void setParentDispatcher(IGlassEventDispatcher dispatcher) {
        this.parentDispatcher = dispatcher;
    }

    /**
     * Can not implements in sub classes.
     *
     * @param cardView
     */
    final void onBindView(GlassCardView cardView) {
        cardView.bindData(this);
    }
}
