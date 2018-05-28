package cn.ceyes.glasswidget.cardview;

/**
 * Created by liusong on 2/2/15.
 */
public abstract  class GlassCardListViewObserver {
    public void onCardSelected(GlassCardView cardView, int position) {
        // do nothing
    }

    public void onCardScrolled(GlassCardView cardView, int position) {
        // do nothing
    }

    public void onFinished() {
        // do nothing
    }
}
