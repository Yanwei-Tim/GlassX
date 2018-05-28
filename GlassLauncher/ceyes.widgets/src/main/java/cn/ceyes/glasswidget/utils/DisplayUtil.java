package cn.ceyes.glasswidget.utils;

import android.content.Context;

/**
 * 显示参数处理
 */
public class DisplayUtil {

    /**
     * 获取屏幕宽度
     */
    public static int getDisplayWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static int getDisplayHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * dp转px
     */
    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * px转dp
     */
    public static int px2dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int dp = (int) (px / scale + 0.5f);
        return dp;
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private DisplayUtil() {
    }
}
