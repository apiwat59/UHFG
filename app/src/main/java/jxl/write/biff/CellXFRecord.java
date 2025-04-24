package jxl.write.biff;

import jxl.biff.DisplayFormat;
import jxl.biff.FontRecord;
import jxl.biff.XFRecord;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.format.Orientation;
import jxl.format.Pattern;
import jxl.format.VerticalAlignment;
import jxl.write.WriteException;

/* loaded from: classes.dex */
public class CellXFRecord extends XFRecord {
    protected CellXFRecord(FontRecord fnt, DisplayFormat form) {
        super(fnt, form);
        setXFDetails(XFRecord.cell, 0);
    }

    CellXFRecord(XFRecord fmt) {
        super(fmt);
        setXFDetails(XFRecord.cell, 0);
    }

    protected CellXFRecord(CellFormat format) {
        super(format);
    }

    public void setAlignment(Alignment a) throws WriteException {
        if (isInitialized()) {
            throw new JxlWriteException(JxlWriteException.formatInitialized);
        }
        super.setXFAlignment(a);
    }

    public void setBackground(Colour c, Pattern p) throws WriteException {
        if (isInitialized()) {
            throw new JxlWriteException(JxlWriteException.formatInitialized);
        }
        super.setXFBackground(c, p);
        super.setXFCellOptions(16384);
    }

    public void setLocked(boolean l) throws WriteException {
        if (isInitialized()) {
            throw new JxlWriteException(JxlWriteException.formatInitialized);
        }
        super.setXFLocked(l);
        super.setXFCellOptions(32768);
    }

    public void setIndentation(int i) throws WriteException {
        if (isInitialized()) {
            throw new JxlWriteException(JxlWriteException.formatInitialized);
        }
        super.setXFIndentation(i);
    }

    public void setShrinkToFit(boolean s) throws WriteException {
        if (isInitialized()) {
            throw new JxlWriteException(JxlWriteException.formatInitialized);
        }
        super.setXFShrinkToFit(s);
    }

    public void setVerticalAlignment(VerticalAlignment va) throws WriteException {
        if (isInitialized()) {
            throw new JxlWriteException(JxlWriteException.formatInitialized);
        }
        super.setXFVerticalAlignment(va);
    }

    public void setOrientation(Orientation o) throws WriteException {
        if (isInitialized()) {
            throw new JxlWriteException(JxlWriteException.formatInitialized);
        }
        super.setXFOrientation(o);
    }

    public void setWrap(boolean w) throws WriteException {
        if (isInitialized()) {
            throw new JxlWriteException(JxlWriteException.formatInitialized);
        }
        super.setXFWrap(w);
    }

    public void setBorder(Border b, BorderLineStyle ls, Colour c) throws WriteException {
        if (isInitialized()) {
            throw new JxlWriteException(JxlWriteException.formatInitialized);
        }
        if (b == Border.ALL) {
            super.setXFBorder(Border.LEFT, ls, c);
            super.setXFBorder(Border.RIGHT, ls, c);
            super.setXFBorder(Border.TOP, ls, c);
            super.setXFBorder(Border.BOTTOM, ls, c);
            return;
        }
        if (b == Border.NONE) {
            super.setXFBorder(Border.LEFT, BorderLineStyle.NONE, Colour.BLACK);
            super.setXFBorder(Border.RIGHT, BorderLineStyle.NONE, Colour.BLACK);
            super.setXFBorder(Border.TOP, BorderLineStyle.NONE, Colour.BLACK);
            super.setXFBorder(Border.BOTTOM, BorderLineStyle.NONE, Colour.BLACK);
            return;
        }
        super.setXFBorder(b, ls, c);
    }
}
