package cn.ceyes.glasswidget.menuview;

import android.content.Context;
import android.view.ViewGroup;

import cn.ceyes.glasswidget.cardview.GlassCardProvider;
import cn.ceyes.glasswidget.cardview.GlassCardView;

public class GlassMenuCardProvider extends GlassCardProvider {

    private String mCardProviderId = null;

    private GlassMenuEntity mMenuEntity = null;

    public void setCardProviderId(String cardProviderId) {
        mCardProviderId = cardProviderId;
    }

    @Override
    public String getCardProviderId() {
        return mCardProviderId;
    }

    @Override
    public GlassCardView onCreateView(Context context, ViewGroup parent) {
        GlassMenuCardView cardView = new GlassMenuCardView(context);
        cardView.initMenu(mMenuEntity);
        return cardView;
    }

    public void initMenu(GlassMenuEntity menuEntities) {
        mMenuEntity = menuEntities;
    }
}
