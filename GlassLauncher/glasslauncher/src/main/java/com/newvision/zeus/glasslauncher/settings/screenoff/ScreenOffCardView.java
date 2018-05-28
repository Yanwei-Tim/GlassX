package com.newvision.zeus.glasslauncher.settings.screenoff;

import android.content.Context;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.newvision.zeus.glasslauncher.R;

import java.util.ArrayList;
import java.util.List;

import cn.ceyes.glasswidget.cardview.GlassCardView;
import cn.ceyes.glasswidget.menuview.GlassMenu;
import cn.ceyes.glasswidget.menuview.GlassMenuEntity;

public class ScreenOffCardView extends GlassCardView {
    private static final String TAG = "ScreenOffCardView";

    private TextView mScreenOff = null;

    private GlassMenu mGlassMenu = null;

    public ScreenOffCardView(Context context) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.card_screen_off, this);
        mScreenOff = (TextView) findViewById(R.id.txt_screen_off);
    }

    @Override
    public void onBindData() {
        setScreenOffText();
    }

    @Override
    public void onCardSelected() {
        final int MENU_ID_FIFTY_SECOND = 0;
        final int MENU_ID_THIRTY_SECOND = 1;
        final int MENU_ID_ONE_MINUTE = 2;
        final int MENU_ID_TWO_MINUTES = 3;
        final int MENU_ID_FIVE_MINUTES = 4;
        final int MENU_ID_TEN_MINUTES = 5;
        final int MENU_ID_THIRTY_MINUTES = 6;
        final int MENU_ID_NEVER = 7;


        List<GlassMenuEntity> menuEntities = new ArrayList<GlassMenuEntity>();
        menuEntities.add(new GlassMenuEntity(MENU_ID_FIFTY_SECOND, R.drawable.icon_screen_off_menu, R.string.screen_off_fifty_second));
        menuEntities.add(new GlassMenuEntity(MENU_ID_THIRTY_SECOND, R.drawable.icon_screen_off_menu, R.string.screen_off_thirty_second));
        menuEntities.add(new GlassMenuEntity(MENU_ID_ONE_MINUTE, R.drawable.icon_screen_off_menu, R.string.screen_off_one_minute));
        menuEntities.add(new GlassMenuEntity(MENU_ID_TWO_MINUTES, R.drawable.icon_screen_off_menu, R.string.screen_off_two_minutes));
        menuEntities.add(new GlassMenuEntity(MENU_ID_FIVE_MINUTES, R.drawable.icon_screen_off_menu, R.string.screen_off_five_minutes));
        menuEntities.add(new GlassMenuEntity(MENU_ID_TEN_MINUTES, R.drawable.icon_screen_off_menu, R.string.screen_off_ten_minutes));
        menuEntities.add(new GlassMenuEntity(MENU_ID_THIRTY_MINUTES, R.drawable.icon_screen_off_menu, R.string.screen_off_thirty_minutes));
        menuEntities.add(new GlassMenuEntity(MENU_ID_NEVER, R.drawable.icon_screen_off_menu, R.string.screen_off_never));


        mGlassMenu = new GlassMenu(mContext);
        mGlassMenu.setMenuEntities(menuEntities).setOnMenuSelectCallback(new GlassMenu.IMenuSelectCallback() {
            @Override
            public void onMenuSelected(int menuEntityId) {
                switch (menuEntityId) {
                    case MENU_ID_FIFTY_SECOND:
                        setScreenOffTime(15000); // 15 * 1000
                        break;
                    case MENU_ID_THIRTY_SECOND:
                        setScreenOffTime(30000); // 30 * 1000
                        break;
                    case MENU_ID_ONE_MINUTE:
                        setScreenOffTime(60000); // 60 * 1000
                        break;
                    case MENU_ID_TWO_MINUTES:
                        setScreenOffTime(120000); // 2 * 60 * 1000
                        break;
                    case MENU_ID_FIVE_MINUTES:
                        setScreenOffTime(300000); // 5 * 60 * 1000
                        break;
                    case MENU_ID_TEN_MINUTES:
                        setScreenOffTime(600000); // 10 * 60 * 1000
                        break;
                    case MENU_ID_THIRTY_MINUTES:
                        setScreenOffTime(1800000); // 30 * 60 * 1000
                        break;
                    case MENU_ID_NEVER:
                        setScreenOffTime(604800000); // 7 * 24 * 60 * 60 * 1000
                        break;
                }
            }
        }).show();
    }

    @Override
    public void onCardInvisible() {
        if (mGlassMenu != null) {
            mGlassMenu.dismiss();
            mGlassMenu = null;
        }
    }

    @Override
    public void onCardFinished() {
        super.onCardFinished();

        sendGlassEvent(50000, null);
    }

    private void setScreenOffTime(int paramInt) {
        Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT,
                paramInt);
        setScreenOffText();
    }

    private void setScreenOffText() {
        int timeout = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 0);

        String screenOffTime = null;
        switch (timeout) {
            case 15000:
                screenOffTime = mContext.getResources().getString(R.string.screen_off_fifty_second);
                break;
            case 30000:
                screenOffTime = mContext.getResources().getString(R.string.screen_off_thirty_second);
                break;
            case 60000:
                screenOffTime = mContext.getResources().getString(R.string.screen_off_one_minute);
                break;
            case 120000:
                screenOffTime = mContext.getResources().getString(R.string.screen_off_two_minutes);
                break;
            case 300000:
                screenOffTime = mContext.getResources().getString(R.string.screen_off_five_minutes);
                break;
            case 600000:
                screenOffTime = mContext.getResources().getString(R.string.screen_off_ten_minutes);
                break;
            case 1800000:
                screenOffTime = mContext.getResources().getString(R.string.screen_off_thirty_minutes);
                break;
            case 604800000:
                screenOffTime = mContext.getResources().getString(R.string.screen_off_never);
                break;
        }
        mScreenOff.setText(screenOffTime);
    }
}
