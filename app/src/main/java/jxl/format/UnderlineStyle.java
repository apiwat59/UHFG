package jxl.format;

/* loaded from: classes.dex */
public final class UnderlineStyle {
    private String string;
    private int value;
    private static UnderlineStyle[] styles = new UnderlineStyle[0];
    public static final UnderlineStyle NO_UNDERLINE = new UnderlineStyle(0, "none");
    public static final UnderlineStyle SINGLE = new UnderlineStyle(1, "single");
    public static final UnderlineStyle DOUBLE = new UnderlineStyle(2, "double");
    public static final UnderlineStyle SINGLE_ACCOUNTING = new UnderlineStyle(33, "single accounting");
    public static final UnderlineStyle DOUBLE_ACCOUNTING = new UnderlineStyle(34, "double accounting");

    protected UnderlineStyle(int val, String s) {
        this.value = val;
        this.string = s;
        UnderlineStyle[] oldstyles = styles;
        UnderlineStyle[] underlineStyleArr = new UnderlineStyle[oldstyles.length + 1];
        styles = underlineStyleArr;
        System.arraycopy(oldstyles, 0, underlineStyleArr, 0, oldstyles.length);
        styles[oldstyles.length] = this;
    }

    public int getValue() {
        return this.value;
    }

    public String getDescription() {
        return this.string;
    }

    public static UnderlineStyle getStyle(int val) {
        int i = 0;
        while (true) {
            UnderlineStyle[] underlineStyleArr = styles;
            if (i < underlineStyleArr.length) {
                if (underlineStyleArr[i].getValue() != val) {
                    i++;
                } else {
                    return styles[i];
                }
            } else {
                return NO_UNDERLINE;
            }
        }
    }
}
