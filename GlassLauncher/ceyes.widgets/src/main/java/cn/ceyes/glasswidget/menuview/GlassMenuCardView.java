package cn.ceyes.glasswidget.menuview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.ceyes.glasswidget.cardview.GlassCardView;
import cn.ceyes.widgets.R;

public class GlassMenuCardView extends GlassCardView {

    private ImageView mMenuIcon = null;
    private TextView mMenuTitle = null;
    private TextView mMenuTip = null;
    private GlassMenuEntity mMenuEntity = null;

    public GlassMenuCardView(Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.card_menu, this);
        mMenuIcon = (ImageView) view.findViewById(R.id.menu_icon);
        mMenuTitle = (TextView) view.findViewById(R.id.menu_title);
        mMenuTip = (TextView) view.findViewById(R.id.menu_tips);
    }

    public void initMenu(GlassMenuEntity menuEntity) {
        mMenuEntity = menuEntity;
    }

    @Override
    public void onBindData() {
        mMenuIcon.setImageResource(mMenuEntity.getIconResId());

        Object titleObj = mMenuEntity.getTitle();
        if (titleObj instanceof Integer) {
            mMenuTitle.setText((Integer) titleObj);
        } else if (titleObj instanceof String) {
            mMenuTitle.setText((String) titleObj);
        }

        Object tipObj = mMenuEntity.getTip();
        if (tipObj instanceof Integer) {
            mMenuTip.setText((Integer) tipObj);
        } else if (tipObj instanceof String) {
            mMenuTip.setText((String) tipObj);
        }
    }
}
