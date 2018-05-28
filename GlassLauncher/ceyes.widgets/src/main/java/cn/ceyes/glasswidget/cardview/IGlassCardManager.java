/*******************************************************************************
 * Copyright (C) 2014 Ceyes Inc. All rights reserved.
 *******************************************************************************/

package cn.ceyes.glasswidget.cardview;

import android.view.ViewGroup;

interface IGlassCardManager {

    void registerGlassCardObserver(GlassCardManagerObserver observer);

    int getPivotCardViewPosition();

    int getCardCount();

    int getCardViewType(int position);

    GlassCardView onCreateCardView(ViewGroup parent, int viewType);

    void onBindCardView(GlassCardView cardView, int position);

}
