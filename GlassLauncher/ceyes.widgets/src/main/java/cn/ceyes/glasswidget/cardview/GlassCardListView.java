/**
 * ****************************************************************************
 * Copyright (C) 2014 Ceyes Inc. All rights reserved.
 * *****************************************************************************
 */

package cn.ceyes.glasswidget.cardview;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import cn.ceyes.glasswidget.gestures.GlassGestureDetector;
import cn.ceyes.glasswidget.gestures.GlassGestureListener;
import cn.ceyes.glasswidget.keyevents.GlassKeyEventDetector;
import cn.ceyes.glasswidget.keyevents.GlassKeyEventListener;
import cn.ceyes.glasswidget.utils.SoundPoolHelper;
import cn.ceyes.widgets.R;

public class GlassCardListView extends LinearLayout {
    private static final String TAG = GlassCardListView.class.getSimpleName();

    public static final int POSITION_TYPE_NORMAL = 0;
    public static final int POSITION_TYPE_FIRST = 1;
    public static final int POSITION_TYPE_LAST = 2;
    public static final int POSITION_TYPE_SINGLE_CARD = 3;

    private GlassCardManagerObserver mGlassCardObserver = new GlassCardManagerObserver() {
        public void onGlassCardInserted(int position) {
            Log.d(TAG, "onGlassCardInserted: " + position);
            mGlassCardViewAdapter.notifyItemInserted(position);
        }

        public void onGlassCardUpdated(int position) {
            Log.d(TAG, "onGlassCardUpdated: " + position);
            mGlassCardViewAdapter.notifyItemChanged(position);
        }

        public void onGlassCardRemoved(int position) {
            Log.d(TAG, "onGlassCardRemoved: " + position);
            mGlassCardViewAdapter.notifyItemRemoved(position);
        }
    };

    private Context mContext;
    private IGlassCardManager mGlassCardManager;
    private GlassCardListViewObserver mGlassCardListViewObserver;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private GlassCardViewAdapter mGlassCardViewAdapter;

    private GlassGestureDetector mGestureDetector;
    private GlassKeyEventDetector mKeyEventDetector;

    private boolean isCycleSlip = false;
    private boolean isScrollbarEnabled = true;
    private boolean mFirstLoadingPivotView = true;

    private boolean isScrollDisabled = false;
    private boolean isInited = false;

    public GlassCardListView(Context context) {
        this(context, null);
    }

    public GlassCardListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.card_launcher, this);
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.recyclerview_horizontal);
    }

    /**
     * @param cardManager GlassCard管理器
     */
    public void init(IGlassCardManager cardManager) {
        this.init(cardManager, true);
    }

    /**
     * @param cardManager        GlassCard管理器
     * @param isScrollbarEnabled scrollbar是否可见
     */
    public void init(IGlassCardManager cardManager, boolean isScrollbarEnabled) {
        setGlassCardManager(cardManager).setScrollBarEnabled(isScrollbarEnabled).init();
    }

    public void disableScroll(boolean disable) {
        isScrollDisabled = disable;
    }

    public void setGlassCardListViewObserver(GlassCardListViewObserver observer) {
        mGlassCardListViewObserver = observer;
    }

    public GlassCardView getCurrentView() {
        int curPosition = mLayoutManager.findLastCompletelyVisibleItemPosition();
        GlassCardView cardView = (GlassCardView) mLayoutManager.findViewByPosition(curPosition);
        return cardView;
    }

    public int getCurrentViewPosition() {
        return mLayoutManager.findLastCompletelyVisibleItemPosition();
    }

    public int getCardCount() {
        return mGlassCardManager.getCardCount();
    }

    public int getCurrentViewPositionType() {
        int count = mGlassCardManager.getCardCount();
        int position = mLayoutManager.findLastCompletelyVisibleItemPosition();
        int positionType;
        if (position == 0 && count == 1) {
            positionType = GlassCardListView.POSITION_TYPE_SINGLE_CARD;
        } else if (position == 0 && count > 1) {
            positionType = GlassCardListView.POSITION_TYPE_FIRST;
        } else if (position == count - 1 && count > 1) {
            positionType = GlassCardListView.POSITION_TYPE_LAST;
        } else {
            positionType = GlassCardListView.POSITION_TYPE_NORMAL;
        }
        return positionType;
    }

    public void setCycleSlip(boolean cycleSlip) {
        this.isCycleSlip = cycleSlip;
    }

    public void navigateToNextCard() {
        navigateToNextCard(true);
    }

    public void navigateToNextCard(boolean smooth) {
        int curPosition = mLayoutManager.findLastCompletelyVisibleItemPosition();
        if (curPosition + 1 == mGlassCardManager.getCardCount()) {
            if (isCycleSlip) {
                mRecyclerView.scrollToPosition(0);
            }
            return;
        }

        if (smooth) {
            mRecyclerView.smoothScrollToPosition(curPosition + 1);
        } else {
            mRecyclerView.scrollToPosition(curPosition + 1);
        }
    }

    public void navigateToPreviousCard() {
        navigateToPreviousCard(true);
    }

    public void navigateToPreviousCard(boolean smooth) {
        int curPosition = mLayoutManager.findLastCompletelyVisibleItemPosition();
        if (curPosition == 0) {
            if (isCycleSlip) {
                mRecyclerView.scrollToPosition(mGlassCardManager.getCardCount() - 1);
            }
            return;
        }

        if (smooth) {
            mRecyclerView.smoothScrollToPosition(curPosition - 1);
        } else {
            mRecyclerView.scrollToPosition(curPosition - 1);
        }
    }

    public void navigateToCard(int cardPosition) {
        if (cardPosition >= 0 && cardPosition < mGlassCardManager.getCardCount()) {
            mRecyclerView.smoothScrollToPosition(cardPosition);
        }
    }

    /*
     * Internal implementation...
     */

    private void init() {
        if (isInited) {
            mRecyclerView.scrollToPosition(mGlassCardManager.getPivotCardViewPosition());
            return;
        }

//        ((Application) (mContext.getApplicationContext())).registerActivityLifecycleCallbacks(
//                new Application.ActivityLifecycleCallbacks() {
//
//                    @Override
//                    public void onActivityResumed(Activity activity) {
//                        if (((Activity) mContext).getLocalClassName().hashCode() == activity.getLocalClassName().hashCode()
//                                && getCurrentView() != null)
//                            getCurrentView().onCardVisibilityChanged(true);
//                    }
//
//                    @Override
//                    public void onActivityPaused(Activity activity) {
//                        if (((Activity) mContext).getLocalClassName().hashCode() == activity.getLocalClassName().hashCode()
//                                && getCurrentView() != null)
//                            getCurrentView().onCardVisibilityChanged(false);
//                    }
//
//                    @Override
//                    public void onActivityDestroyed(Activity activity) {
//                        if (((Activity) mContext).getLocalClassName().hashCode() == activity.getLocalClassName().hashCode())
//                            mRecyclerView.removeAllViews();
//                    }
//
//                    @Override
//                    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
//                    }
//
//                    @Override
//                    public void onActivityStarted(Activity activity) {
//
//                    }
//
//                    @Override
//                    public void onActivityStopped(Activity activity) {
//
//                    }
//
//                    @Override
//                    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
//
//                    }
//
//                }
//        );

        mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mGlassCardViewAdapter = new GlassCardViewAdapter();
        mRecyclerView.setAdapter(mGlassCardViewAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
                    int lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();

                    if (firstVisibleItem != lastVisibleItem) {
                        float posXLastView = mLayoutManager.findViewByPosition(lastVisibleItem).getX();

                        int targetPosition = firstVisibleItem;
                        if (posXLastView * 2 < mRecyclerView.getWidth()) {
                            targetPosition = lastVisibleItem;
                        }

                        mRecyclerView.smoothScrollToPosition(targetPosition);
                        Log.d(TAG, "scroll to: " + targetPosition);
                    } else {
                        if (GlassCardListView.this.mGlassCardListViewObserver != null) {
                            GlassCardListView.this.mGlassCardListViewObserver.onCardScrolled(
                                    (GlassCardView) mLayoutManager.findViewByPosition(firstVisibleItem), firstVisibleItem);
                        }
                        SoundPoolHelper.playMusic(SoundPoolHelper.SOUND_KEYPRESS);
                        Log.d(TAG, "scrolled to: " + firstVisibleItem);
                    }
                }
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                if (isScrollDisabled) {// e does not contain the ACTION_UP action.
                    mGestureDetector.onTouchEvent(getCurrentView(), e);
                }
                return isScrollDisabled;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                if (isScrollDisabled) {// e does not contain the ACTION_DOWN action.
                    mGestureDetector.onTouchEvent(getCurrentView(), e);
                }
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });

        mKeyEventDetector = new GlassKeyEventDetector(new GlassKeyEventListener() {
            @Override
            public void onPreviousKey() {
                super.onPreviousKey();
                Log.d(TAG, "onPreviousKey");
                GlassCardListView.this.navigateToPreviousCard();
            }

            @Override
            public void onNextKey() {
                Log.d(TAG, "onNextKey");
                super.onNextKey();
                GlassCardListView.this.navigateToNextCard();
            }

            @Override
            public void onBackKey() {
                Log.d(TAG, "onBackKey");
                super.onBackKey();
                int position = mLayoutManager.findLastCompletelyVisibleItemPosition();
                GlassCardView cardView = (GlassCardView) mLayoutManager.findViewByPosition(position);
                if (cardView != null) {
                    cardView.onCardFinished();

                    if (GlassCardListView.this.mGlassCardListViewObserver != null) {
                        GlassCardListView.this.mGlassCardListViewObserver.onFinished();
                    }
                } else {
                    Log.d(TAG, "onBackKey: during transition, can't get cardView");
                }
            }

            @Override
            public void onEnterKey() {
                Log.d(TAG, "onEnterKey");
                super.onEnterKey();
                int position = mLayoutManager.findLastCompletelyVisibleItemPosition();
                GlassCardView cardView = (GlassCardView) mLayoutManager.findViewByPosition(position);
                if (cardView != null) {
                    cardView.onCardSelected();

                    if (GlassCardListView.this.mGlassCardListViewObserver != null) {
                        GlassCardListView.this.mGlassCardListViewObserver.onCardSelected(cardView, position);
                    }
                } else {
                    Log.d(TAG, "onEnterKey: during transition, can't get cardView");
                }
            }
        });

        mRecyclerView.setFocusable(true);
        mRecyclerView.requestFocus();
        mRecyclerView.setFocusableInTouchMode(true);
        mRecyclerView.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d(TAG, "onKey: keycode=" + keyCode);
                return mKeyEventDetector.onKeyEvent(v, keyCode, event);
            }
        });

        mGestureDetector = new GlassGestureDetector(mContext, new GlassGestureListener() {
            @Override
            public void onSingleTap(View v) {
                SoundPoolHelper.playMusic(SoundPoolHelper.SOUND_NOTIFICATION);

                GlassCardView cardView = (GlassCardView) v;
                cardView.onCardSelected();

                if (mGlassCardListViewObserver != null) {
                    mGlassCardListViewObserver.onCardSelected(cardView, mRecyclerView.getChildPosition(v));
                }
            }
        });

        // performance optimizations
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(6);

        mRecyclerView.scrollToPosition(mGlassCardManager.getPivotCardViewPosition());
        isInited = true;
    }

    private GlassCardListView setGlassCardManager(IGlassCardManager cardManager) {
        mGlassCardManager = cardManager;
        if (mGlassCardManager != null) {
            mGlassCardManager.registerGlassCardObserver(mGlassCardObserver);
        }
        return this;
    }

    private GlassCardListView setScrollBarEnabled(boolean isScrollbarEnabled) {
        this.isScrollbarEnabled = isScrollbarEnabled;
        if (mRecyclerView != null) {
            mRecyclerView.setHorizontalScrollBarEnabled(isScrollbarEnabled);
        }
        return this;
    }

    private class GlassCardViewAdapter extends RecyclerView.Adapter<GlassCardViewAdapter.ViewHolder> {

        private final static String TAG = "GlassCardViewAdapter";

        public GlassCardViewAdapter() {
            super();
        }

        @Override
        public int getItemCount() {
            Log.d(TAG, "getItemCount: " + mGlassCardManager.getCardCount());

            return mGlassCardManager.getCardCount();
        }

        @Override
        public int getItemViewType(int position) {
            Log.d(TAG, "getItemViewType: " + position);

            return mGlassCardManager.getCardViewType(position);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateViewHolder: " + viewType);

            View view = mGlassCardManager.onCreateCardView(parent, viewType);
            view.setLayoutParams(new LayoutParams(mRecyclerView.getWidth(), mRecyclerView.getHeight()));

            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            Log.d(TAG, "onBindViewHolder: " + position + ", viewHolder: " + viewHolder.itemView);

            mGlassCardManager.onBindCardView(viewHolder.glassCardView, position);

            if (mFirstLoadingPivotView && position == GlassCardListView.this.mGlassCardManager.getPivotCardViewPosition()) {
                if (GlassCardListView.this.mGlassCardListViewObserver != null) {
                    GlassCardListView.this.mGlassCardListViewObserver.onCardScrolled(viewHolder.glassCardView, position);
                }

                Log.d(TAG, "scrolled to: " + position);

                mFirstLoadingPivotView = false;
            }
        }


        @Override
        public void onViewRecycled(ViewHolder holder) {
            holder.glassCardView.onCardRecycled();
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            holder.glassCardView.onCardVisibilityChanged(true);
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            holder.glassCardView.onCardVisibilityChanged(false);
        }

        protected class ViewHolder extends RecyclerView.ViewHolder {

            private static final String TAG = "ViewHolder";
            public GlassCardView glassCardView;

            public ViewHolder(View view) {
                super(view);

                glassCardView = (GlassCardView) view;
                glassCardView.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return mGestureDetector.onTouchEvent(v, event);
                    }
                });
            }

            @Override
            protected void finalize() throws Throwable {
                Log.d(TAG, "finalized: " + glassCardView);
                super.finalize();
            }
        }
    }
}
