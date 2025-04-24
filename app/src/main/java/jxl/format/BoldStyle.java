package jxl.format;

import androidx.constraintlayout.core.motion.utils.TypedValues;

/* loaded from: classes.dex */
public class BoldStyle {
    private String string;
    private int value;
    public static final BoldStyle NORMAL = new BoldStyle(400, "Normal");
    public static final BoldStyle BOLD = new BoldStyle(TypedValues.TransitionType.TYPE_DURATION, "Bold");

    protected BoldStyle(int val, String s) {
        this.value = val;
        this.string = s;
    }

    public int getValue() {
        return this.value;
    }

    public String getDescription() {
        return this.string;
    }
}
