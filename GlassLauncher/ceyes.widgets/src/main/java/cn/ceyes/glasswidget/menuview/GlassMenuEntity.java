package cn.ceyes.glasswidget.menuview;

public class GlassMenuEntity {

    private int itemId;
    private int iconResId;
    private Object title;
    private Object tip;

    public GlassMenuEntity(int itemId){
        this.itemId = itemId;
    }

    public GlassMenuEntity(int itemId, int iconResId, int titleResId) {
        this.itemId = itemId;
        this.iconResId = iconResId;
        this.title = titleResId;
    }

    public GlassMenuEntity(int itemId, int iconResId, String titleString) {
        this.itemId = itemId;
        this.iconResId = iconResId;
        this.title = titleString;
    }

    public int getItemId() {
        return itemId;
    }

    public int getIconResId() {
        return iconResId;
    }

    public GlassMenuEntity setIconResId(int iconResId) {
        this.iconResId = iconResId;
        return this;
    }

    public GlassMenuEntity setTitle(int titleResId) {
        this.title = titleResId;
        return this;
    }

    public GlassMenuEntity setTitle(String titleString) {
        this.title = titleString;
        return this;
    }

    public Object getTitle() {
        return title;
    }

    public GlassMenuEntity setTip(int tipResId) {
        this.tip = tipResId;
        return this;
    }

    public GlassMenuEntity setTip(String tipString) {
        this.tip = tipString;
        return this;
    }

    public Object getTip() {
        return tip;
    }
}
