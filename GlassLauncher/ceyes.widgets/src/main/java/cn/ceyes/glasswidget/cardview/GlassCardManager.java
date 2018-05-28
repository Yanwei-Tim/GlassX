package cn.ceyes.glasswidget.cardview;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaoliang on 1/8/15.
 */
public class GlassCardManager implements IGlassCardManager, IGlassEventSender, IGlassEventDispatcher,
        IGlassEventListener {
    private static final String TAG = "GlassCardManager";

    protected Context mContext;
    protected IGlassEventListener parentListener;

    protected List<GlassCardProvider> mActiveGlassCardProviders;
    private List<GlassCardManagerObserver> mGlassCardObservers;

    public GlassCardManager(Context context, IGlassEventListener listener) {
        mContext = context;
        parentListener = listener;

        mActiveGlassCardProviders = new ArrayList<GlassCardProvider>();
        mGlassCardObservers = new ArrayList<GlassCardManagerObserver>();
    }

    @Override
    public void registerGlassCardObserver(GlassCardManagerObserver observer) {
        if (!mGlassCardObservers.contains(observer)) {
            mGlassCardObservers.add(observer);
        }
    }

    @Override
    public int getPivotCardViewPosition() {
        return 0;
    }

    @Override
    public int getCardCount() {
        return mActiveGlassCardProviders.size();
    }

    @Override
    public int getCardViewType(int position) {
        String providerId = mActiveGlassCardProviders.get(position).getCardProviderId();
        return providerId.hashCode();
    }

    @Override
    public GlassCardView onCreateCardView(ViewGroup parent, int viewType) {

        int providerId = viewType;

        for (GlassCardProvider activeProvider : mActiveGlassCardProviders) {
            if (activeProvider.getCardProviderId().hashCode() == providerId) {
                return activeProvider.onCreateView(mContext, parent);
            }
        }

        Log.e(TAG, "onCreateCardView: viewType is wrong - " + viewType);
        return null;
    }

    @Override
    public void onBindCardView(GlassCardView cardView, int position) {
        mActiveGlassCardProviders.get(position).onBindView(cardView);
    }

    @Override
    public final void sendGlassEvent(int eventCode, Object event) {
        if (this.parentListener == null) {
            Log.e(TAG, "No parent GlassEventListener.");
            return;
        }
        this.parentListener.onGlassEvent(eventCode, event);
    }

    @Override
    public final void dispatchGlassEvent(int eventCode, Object event) {
        if (onGlassEvent(eventCode, event)) return;
        if (this.parentListener == null) {
            Log.e(TAG, "No parent GlassEventListener.");
            return;
        }
        this.parentListener.onGlassEvent(eventCode, event);
    }

    @Override
    public boolean onGlassEvent(int eventCode, Object event) {
        return false;
    }

    public boolean activateGlassCard(GlassCardProvider cardProvider) {
        int position = calculateGlassCardPosition(cardProvider);
        if (position == -1) {
            return false;
        }

        Log.d(TAG, "activateGlassCard: position: " + position);

        cardProvider.setParentDispatcher(this);
        mActiveGlassCardProviders.add(position, cardProvider);

        for (GlassCardManagerObserver observer : mGlassCardObservers) {
            observer.onGlassCardInserted(position);
        }
        return true;
    }

    public boolean deactivateGlassCard(GlassCardProvider cardProvider) {
        int position = mActiveGlassCardProviders.indexOf(cardProvider);
        Log.i(TAG, "position: " + position);
        if (position == -1) {
            return false;
        }

        cardProvider.setParentDispatcher(null);
        if (mActiveGlassCardProviders.remove(cardProvider)) {
            Log.i(TAG, "remove cardProvider");
            for (GlassCardManagerObserver observer : mGlassCardObservers) {
                observer.onGlassCardRemoved(position);
            }
        }
        return true;
    }

    public void updateGlassCard(GlassCardProvider cardProvider) {
        for (GlassCardManagerObserver observer : mGlassCardObservers) {
            observer.onGlassCardUpdated(mActiveGlassCardProviders.indexOf(cardProvider));
        }
    }

    public List<GlassCardProvider> getActiveCardProviders() {
        return mActiveGlassCardProviders;
    }

    protected int calculateGlassCardPosition(GlassCardProvider cardProvider) {
        return mActiveGlassCardProviders.size();
    }
}
