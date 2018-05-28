package cn.ceyes.glasswidget.alertview;

import cn.ceyes.widgets.R;

/**
 * Created by zhangsong on 12/31/14.
 */
public class GlassAlertEntity {

    public static final int ORIENTATION_HORIZONTAL = 0;
    public static final int ORIENTATION_VERTICAL = 1;

    public static final int STYLE_DEFAULT = 1;

    public static final int NONE = 0;

    private int alertId;
    /**
     * view orientation in alert view.
     * <p/>
     * horizon vertical
     */
    private int orientation;
    /**
     * the icon of alert
     */
    private int iconResource;
    /**
     * the title of the alter
     */
    private Object alertTitle;
    /**
     * the content resource id of the alter
     */
    private Object alertContent;
    /**
     * progressbarTime = 0,the progressbar's visibility is gone
     */
    private int progressbarTime;
    /**
     * if refreshResource = 0, this image's visibility is gone.
     * if refreshResource = 1, show the default refreshing animation.
     */
    private int refreshResource;

    private boolean autoDismiss = false;

    private boolean clickable = false;

    private boolean cancelable = false;

    private long timeoutMillis;

    private GlassAlertEntity(int alertId, int orientation, int iconResId, Object alertTitle, Object alertContent, int progressbarTime, int refreshStyle,
                             boolean autoDismiss, boolean clickable) {
        this.alertId = alertId;
        this.orientation = orientation;
        this.iconResource = iconResId;
        this.alertTitle = alertTitle;
        this.alertContent = alertContent;
        this.progressbarTime = progressbarTime;
        if (refreshStyle == STYLE_DEFAULT) {
            this.refreshResource = R.drawable.horizontal_refreshing;
        }
        this.autoDismiss = autoDismiss;
        this.clickable = clickable;
    }

    public static GlassAlertEntity createHorizontalAlert(int alertId, int iconId, int titleId) {
        return new GlassAlertEntity(alertId, ORIENTATION_HORIZONTAL, iconId, titleId, 0, 0, 0, true, false);
    }

    public static GlassAlertEntity createHorizontalAlert(int alertId, int iconId, String titleString) {
        return new GlassAlertEntity(alertId, ORIENTATION_HORIZONTAL, iconId, titleString, 0, 0, 0, true, false);
    }

    public static GlassAlertEntity createVerticalAlert(int alertId, int iconId, int titleId, int contentId) {
        return new GlassAlertEntity(alertId, ORIENTATION_VERTICAL, iconId, titleId, contentId, 0, 0, true, false);
    }

    public static GlassAlertEntity createVerticalAlert(int alertId, int iconId, String titleString, int contentId) {
        return new GlassAlertEntity(alertId, ORIENTATION_VERTICAL, iconId, titleString, contentId, 0, 0, true, false);
    }

    public static GlassAlertEntity createVerticalAlert(int alertId, int iconId, int titleId, String contentString) {
        return new GlassAlertEntity(alertId, ORIENTATION_VERTICAL, iconId, titleId, contentString, 0, 0, true, false);
    }

    public static GlassAlertEntity createVerticalAlert(int alertId, int iconId, String titleString, String contentString) {
        return new GlassAlertEntity(alertId, ORIENTATION_VERTICAL, iconId, titleString, contentString, 0, 0, true, false);
    }

    public int getAlertId() {
        return alertId;
    }

    public int getOrientation() {
        return orientation;
    }

    public int getIconResource() {
        return iconResource;
    }

    public GlassAlertEntity setIconResource(int iconResource) {
        this.iconResource = iconResource;
        return this;
    }

    public Object getAlertContent() {
        return alertContent;
    }

    public GlassAlertEntity setAlertContent(int resId) {
        this.alertContent = resId;
        return this;
    }

    public GlassAlertEntity setAlertContent(String resString) {
        this.alertContent = resString;
        return this;
    }

    public Object getAlertTitle() {
        return alertTitle;
    }

    public GlassAlertEntity setAlertTitle(int resId) {
        this.alertTitle = resId;
        return this;
    }

    public GlassAlertEntity setAlertTitle(String resString) {
        this.alertTitle = resString;
        return this;
    }

    public int getProgressbarTime() {
        return progressbarTime;
    }

    public GlassAlertEntity setProgressbarTime(int progressbarTime) {
        this.progressbarTime = progressbarTime;
        return this;
    }

    public int getRefreshResource() {
        return refreshResource;
    }

    public GlassAlertEntity setRefreshStyle(int refreshStyle) {
        if (refreshStyle == STYLE_DEFAULT) {
            this.refreshResource = R.drawable.horizontal_refreshing;
        }
        return this;
    }

    public boolean isAutoDismiss() {
        return autoDismiss;
    }

    public GlassAlertEntity setAutoDismiss(boolean autoDismiss) {
        this.autoDismiss = autoDismiss;
        return this;
    }

    public boolean isClickable() {
        return clickable;
    }

    public GlassAlertEntity setClickable(boolean clickable) {
        this.clickable = clickable;
        return this;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public GlassAlertEntity setTimeoutMillis(long millis) {
        this.timeoutMillis = millis;
        return this;
    }

    public boolean isCancelable() {
        return cancelable;
    }

    public GlassAlertEntity setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }
}
