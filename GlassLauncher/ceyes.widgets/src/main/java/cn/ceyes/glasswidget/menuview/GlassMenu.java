package cn.ceyes.glasswidget.menuview;

import android.content.Context;

import java.util.List;

import cn.ceyes.glasswidget.cardview.GlassCardListView;
import cn.ceyes.glasswidget.cardview.GlassCardListViewObserver;
import cn.ceyes.glasswidget.cardview.GlassCardManager;
import cn.ceyes.glasswidget.cardview.GlassCardView;
import cn.ceyes.glasswidget.singleview.GlassBaseDialog;

/**
 * Created by zhangsong on 2/3/15.
 */
public class GlassMenu {

    public interface IMenuSelectCallback {
        void onMenuSelected(int menuEntityId);
    }

    private Context mContext = null;
    private List<GlassMenuEntity> mMenuEntities = null;
    private GlassBaseDialog mBaseDialog;

    private IMenuSelectCallback mMenuSelectCallback = null;

    public GlassMenu(Context context) {
        mContext = context;
        mBaseDialog = new GlassBaseDialog(context);
    }

    public GlassMenu setMenuEntities(List<GlassMenuEntity> menuEntities) {
        mMenuEntities = menuEntities;

        GlassCardManager cardManager = new GlassCardManager(mContext, null);

        for (int i = 0; i < menuEntities.size(); i++) {
            GlassMenuCardProvider cardProvider = new GlassMenuCardProvider();

            cardProvider.setCardProviderId("GlassMenu_" + i);
            cardProvider.initMenu(menuEntities.get(i));
            cardManager.activateGlassCard(cardProvider);
        }

        GlassCardListView menuListView = new GlassCardListView(mContext);
        menuListView.init(cardManager);
        menuListView.setGlassCardListViewObserver(new GlassCardListViewObserver() {
            @Override
            public void onCardSelected(GlassCardView cardView, int position) {
                if (mMenuSelectCallback != null)
                    mMenuSelectCallback.onMenuSelected(mMenuEntities.get(position).getItemId());
                GlassMenu.this.dismiss();
            }

            @Override
            public void onFinished() {
                GlassMenu.this.dismiss();
            }
        });
        mBaseDialog.setContentView(menuListView);

        return this;
    }

    public GlassMenu show() {
        mBaseDialog.show();
        return this;
    }

    public GlassMenu dismiss() {
        mBaseDialog.dismiss();
        return this;
    }

    public boolean isShowing() {
        return mBaseDialog.isShowing();
    }

    public GlassMenu setOnMenuSelectCallback(IMenuSelectCallback callback) {
        mMenuSelectCallback = callback;
        return this;
    }
}
