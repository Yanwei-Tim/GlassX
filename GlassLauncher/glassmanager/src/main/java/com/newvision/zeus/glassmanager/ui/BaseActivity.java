package com.newvision.zeus.glassmanager.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;

import com.newvision.zeus.glasscore.base.GlassCoreClientActivity;
import com.newvision.zeus.glassmanager.R;
import com.newvision.zeus.glassmanager.common.widgets.SystemBarTintManager;
import com.newvision.zeus.glassmanager.common.widgets.TitleBarView;

/**
 * Created by yanjiatian on 2017/7/4.
 */

public abstract class BaseActivity extends GlassCoreClientActivity {
    public TitleBarView titleBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //设置状态栏和导航栏的颜色
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.title_bar));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.title_bar));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < 21) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏 如果只是想要更改状态栏 这句可以注销
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            // 创建状态栏导航栏的管理实例
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            // 激活状态栏设置
            tintManager.setStatusBarTintEnabled(true);
            // 激活导航栏设置,如果Navigation不想改变颜色，此处设置为false即可
            tintManager.setNavigationBarTintEnabled(true);
            // 设置一个颜色给系统栏,这个方法会把状态栏和导航栏设置为一样的颜色，这就不好了
            //当然,如果你想设置一样的颜色，那么你随便啊
            //tintManager.setTintColor(Color.parseColor("#FFFF6666"));
            //给状态栏设置颜色
            //Apply the specified drawable or color resource to the system status bar.
            tintManager.setStatusBarTintResource(R.color.title_bar);
            //Apply the specified drawable or color resource to the system navigation bar.
            //给导航栏设置资源
            tintManager.setNavigationBarTintResource(R.color.title_bar);
        }
    }

    public abstract void initViews();

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        try {
            titleBar = (TitleBarView) findViewById(R.id.title_bar);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTitle(int id) {
        if (titleBar != null) {
            titleBar.setTitle(id);
        }
    }

    public void showNavigateLeftIcon(boolean show) {
        if (titleBar != null) {
            titleBar.showNavigateLeftIcon(show);
        }
    }

    public void setNavigateLeftImage(int id) {
        if (titleBar != null) {
            titleBar.setNavigateLeftImage(id);
        }
    }

    public void showNavigateRightIcon(boolean show) {
        if (titleBar != null) {
            titleBar.showNavigateRightIcon(show);
        }
    }

    public void setNavigateRightImage(int id) {
        if (titleBar != null) {
            titleBar.setNavigateRightImage(id);
        }
    }

    public void setNavigateLeftOnClickListener(View.OnClickListener listener) {
        if (titleBar != null) {
            titleBar.setNavigateLeftOnClickListener(listener);
        }
    }

    public void setNavigateRightOnClickListener(View.OnClickListener listener) {
        if (titleBar != null) {
            titleBar.setNavigateRightOnClickListener(listener);
        }

    }
}