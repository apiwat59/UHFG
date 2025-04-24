package jxl.write;

import jxl.format.ScriptStyle;
import jxl.format.UnderlineStyle;
import jxl.write.WritableFont;

/* loaded from: classes.dex */
public class Font extends WritableFont {
    public static final WritableFont.FontName ARIAL = WritableFont.ARIAL;
    public static final WritableFont.FontName TIMES = WritableFont.TIMES;
    public static final WritableFont.BoldStyle NO_BOLD = WritableFont.NO_BOLD;
    public static final WritableFont.BoldStyle BOLD = WritableFont.BOLD;
    public static final UnderlineStyle NO_UNDERLINE = UnderlineStyle.NO_UNDERLINE;
    public static final UnderlineStyle SINGLE = UnderlineStyle.SINGLE;
    public static final UnderlineStyle DOUBLE = UnderlineStyle.DOUBLE;
    public static final UnderlineStyle SINGLE_ACCOUNTING = UnderlineStyle.SINGLE_ACCOUNTING;
    public static final UnderlineStyle DOUBLE_ACCOUNTING = UnderlineStyle.DOUBLE_ACCOUNTING;
    public static final ScriptStyle NORMAL_SCRIPT = ScriptStyle.NORMAL_SCRIPT;
    public static final ScriptStyle SUPERSCRIPT = ScriptStyle.SUPERSCRIPT;
    public static final ScriptStyle SUBSCRIPT = ScriptStyle.SUBSCRIPT;

    public Font(WritableFont.FontName fn) {
        super(fn);
    }

    public Font(WritableFont.FontName fn, int ps) {
        super(fn, ps);
    }

    public Font(WritableFont.FontName fn, int ps, WritableFont.BoldStyle bs) {
        super(fn, ps, bs);
    }

    public Font(WritableFont.FontName fn, int ps, WritableFont.BoldStyle bs, boolean italic) {
        super(fn, ps, bs, italic);
    }

    public Font(WritableFont.FontName fn, int ps, WritableFont.BoldStyle bs, boolean it, UnderlineStyle us) {
        super(fn, ps, bs, it, us);
    }

    public Font(WritableFont.FontName fn, int ps, WritableFont.BoldStyle bs, boolean it, UnderlineStyle us, jxl.format.Colour c) {
        super(fn, ps, bs, it, us, c);
    }

    public Font(WritableFont.FontName fn, int ps, WritableFont.BoldStyle bs, boolean it, UnderlineStyle us, jxl.format.Colour c, ScriptStyle ss) {
        super(fn, ps, bs, it, us, c, ss);
    }
}
