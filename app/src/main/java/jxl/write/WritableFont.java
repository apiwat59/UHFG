package jxl.write;

import androidx.constraintlayout.core.motion.utils.TypedValues;
import jxl.format.ScriptStyle;
import jxl.format.UnderlineStyle;
import jxl.write.biff.WritableFontRecord;

/* loaded from: classes.dex */
public class WritableFont extends WritableFontRecord {
    public static final int DEFAULT_POINT_SIZE = 10;
    public static final FontName ARIAL = new FontName("Arial");
    public static final FontName TIMES = new FontName("Times New Roman");
    public static final FontName COURIER = new FontName("Courier New");
    public static final FontName TAHOMA = new FontName("Tahoma");
    public static final BoldStyle NO_BOLD = new BoldStyle(400);
    public static final BoldStyle BOLD = new BoldStyle(TypedValues.TransitionType.TYPE_DURATION);

    public static class FontName {
        String name;

        FontName(String s) {
            this.name = s;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static class BoldStyle {
        public int value;

        BoldStyle(int val) {
            this.value = val;
        }
    }

    public WritableFont(FontName fn) {
        this(fn, 10, NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK, ScriptStyle.NORMAL_SCRIPT);
    }

    public WritableFont(jxl.format.Font f) {
        super(f);
    }

    public WritableFont(FontName fn, int ps) {
        this(fn, ps, NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK, ScriptStyle.NORMAL_SCRIPT);
    }

    public WritableFont(FontName fn, int ps, BoldStyle bs) {
        this(fn, ps, bs, false, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK, ScriptStyle.NORMAL_SCRIPT);
    }

    public WritableFont(FontName fn, int ps, BoldStyle bs, boolean italic) {
        this(fn, ps, bs, italic, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK, ScriptStyle.NORMAL_SCRIPT);
    }

    public WritableFont(FontName fn, int ps, BoldStyle bs, boolean it, UnderlineStyle us) {
        this(fn, ps, bs, it, us, jxl.format.Colour.BLACK, ScriptStyle.NORMAL_SCRIPT);
    }

    public WritableFont(FontName fn, int ps, BoldStyle bs, boolean it, UnderlineStyle us, jxl.format.Colour c) {
        this(fn, ps, bs, it, us, c, ScriptStyle.NORMAL_SCRIPT);
    }

    public WritableFont(FontName fn, int ps, BoldStyle bs, boolean it, UnderlineStyle us, jxl.format.Colour c, ScriptStyle ss) {
        super(fn.name, ps, bs.value, it, us.getValue(), c.getValue(), ss.getValue());
    }

    @Override // jxl.write.biff.WritableFontRecord
    public void setPointSize(int pointSize) throws WriteException {
        super.setPointSize(pointSize);
    }

    public void setBoldStyle(BoldStyle boldStyle) throws WriteException {
        super.setBoldStyle(boldStyle.value);
    }

    @Override // jxl.write.biff.WritableFontRecord
    public void setItalic(boolean italic) throws WriteException {
        super.setItalic(italic);
    }

    public void setUnderlineStyle(UnderlineStyle us) throws WriteException {
        super.setUnderlineStyle(us.getValue());
    }

    public void setColour(jxl.format.Colour colour) throws WriteException {
        super.setColour(colour.getValue());
    }

    public void setScriptStyle(ScriptStyle scriptStyle) throws WriteException {
        super.setScriptStyle(scriptStyle.getValue());
    }

    @Override // jxl.biff.FontRecord, jxl.format.Font
    public boolean isStruckout() {
        return super.isStruckout();
    }

    @Override // jxl.write.biff.WritableFontRecord
    public void setStruckout(boolean struckout) throws WriteException {
        super.setStruckout(struckout);
    }

    public static FontName createFont(String fontName) {
        return new FontName(fontName);
    }
}
