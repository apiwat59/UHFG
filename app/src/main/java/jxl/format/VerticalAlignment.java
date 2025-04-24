package jxl.format;

/* loaded from: classes.dex */
public class VerticalAlignment {
    private String string;
    private int value;
    private static VerticalAlignment[] alignments = new VerticalAlignment[0];
    public static VerticalAlignment TOP = new VerticalAlignment(0, "top");
    public static VerticalAlignment CENTRE = new VerticalAlignment(1, "centre");
    public static VerticalAlignment BOTTOM = new VerticalAlignment(2, "bottom");
    public static VerticalAlignment JUSTIFY = new VerticalAlignment(3, "Justify");

    protected VerticalAlignment(int val, String s) {
        this.value = val;
        this.string = s;
        VerticalAlignment[] oldaligns = alignments;
        VerticalAlignment[] verticalAlignmentArr = new VerticalAlignment[oldaligns.length + 1];
        alignments = verticalAlignmentArr;
        System.arraycopy(oldaligns, 0, verticalAlignmentArr, 0, oldaligns.length);
        alignments[oldaligns.length] = this;
    }

    public int getValue() {
        return this.value;
    }

    public String getDescription() {
        return this.string;
    }

    public static VerticalAlignment getAlignment(int val) {
        int i = 0;
        while (true) {
            VerticalAlignment[] verticalAlignmentArr = alignments;
            if (i < verticalAlignmentArr.length) {
                if (verticalAlignmentArr[i].getValue() != val) {
                    i++;
                } else {
                    return alignments[i];
                }
            } else {
                return BOTTOM;
            }
        }
    }
}
