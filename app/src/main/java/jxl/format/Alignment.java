package jxl.format;

/* loaded from: classes.dex */
public class Alignment {
    private String string;
    private int value;
    private static Alignment[] alignments = new Alignment[0];
    public static Alignment GENERAL = new Alignment(0, "general");
    public static Alignment LEFT = new Alignment(1, "left");
    public static Alignment CENTRE = new Alignment(2, "centre");
    public static Alignment RIGHT = new Alignment(3, "right");
    public static Alignment FILL = new Alignment(4, "fill");
    public static Alignment JUSTIFY = new Alignment(5, "justify");

    protected Alignment(int val, String s) {
        this.value = val;
        this.string = s;
        Alignment[] oldaligns = alignments;
        Alignment[] alignmentArr = new Alignment[oldaligns.length + 1];
        alignments = alignmentArr;
        System.arraycopy(oldaligns, 0, alignmentArr, 0, oldaligns.length);
        alignments[oldaligns.length] = this;
    }

    public int getValue() {
        return this.value;
    }

    public String getDescription() {
        return this.string;
    }

    public static Alignment getAlignment(int val) {
        int i = 0;
        while (true) {
            Alignment[] alignmentArr = alignments;
            if (i < alignmentArr.length) {
                if (alignmentArr[i].getValue() != val) {
                    i++;
                } else {
                    return alignments[i];
                }
            } else {
                return GENERAL;
            }
        }
    }
}
