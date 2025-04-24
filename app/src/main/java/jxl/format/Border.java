package jxl.format;

/* loaded from: classes.dex */
public class Border {
    private String string;
    public static final Border NONE = new Border("none");
    public static final Border ALL = new Border("all");
    public static final Border TOP = new Border("top");
    public static final Border BOTTOM = new Border("bottom");
    public static final Border LEFT = new Border("left");
    public static final Border RIGHT = new Border("right");

    protected Border(String s) {
        this.string = s;
    }

    public String getDescription() {
        return this.string;
    }
}
