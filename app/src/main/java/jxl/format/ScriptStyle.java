package jxl.format;

/* loaded from: classes.dex */
public final class ScriptStyle {
    private String string;
    private int value;
    private static ScriptStyle[] styles = new ScriptStyle[0];
    public static final ScriptStyle NORMAL_SCRIPT = new ScriptStyle(0, "normal");
    public static final ScriptStyle SUPERSCRIPT = new ScriptStyle(1, "super");
    public static final ScriptStyle SUBSCRIPT = new ScriptStyle(2, "sub");

    protected ScriptStyle(int val, String s) {
        this.value = val;
        this.string = s;
        ScriptStyle[] oldstyles = styles;
        ScriptStyle[] scriptStyleArr = new ScriptStyle[oldstyles.length + 1];
        styles = scriptStyleArr;
        System.arraycopy(oldstyles, 0, scriptStyleArr, 0, oldstyles.length);
        styles[oldstyles.length] = this;
    }

    public int getValue() {
        return this.value;
    }

    public String getDescription() {
        return this.string;
    }

    public static ScriptStyle getStyle(int val) {
        int i = 0;
        while (true) {
            ScriptStyle[] scriptStyleArr = styles;
            if (i < scriptStyleArr.length) {
                if (scriptStyleArr[i].getValue() != val) {
                    i++;
                } else {
                    return styles[i];
                }
            } else {
                return NORMAL_SCRIPT;
            }
        }
    }
}
