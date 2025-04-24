package jxl.write;

import jxl.biff.DisplayFormat;
import jxl.format.CellFormat;
import jxl.format.Orientation;
import jxl.write.biff.CellXFRecord;

/* loaded from: classes.dex */
public class WritableCellFormat extends CellXFRecord {
    public WritableCellFormat() {
        this(WritableWorkbook.ARIAL_10_PT, NumberFormats.DEFAULT);
    }

    public WritableCellFormat(WritableFont font) {
        this(font, NumberFormats.DEFAULT);
    }

    public WritableCellFormat(DisplayFormat format) {
        this(WritableWorkbook.ARIAL_10_PT, format);
    }

    public WritableCellFormat(WritableFont font, DisplayFormat format) {
        super(font, format);
    }

    public WritableCellFormat(CellFormat format) {
        super(format);
    }

    @Override // jxl.write.biff.CellXFRecord
    public void setAlignment(jxl.format.Alignment a) throws WriteException {
        super.setAlignment(a);
    }

    @Override // jxl.write.biff.CellXFRecord
    public void setVerticalAlignment(jxl.format.VerticalAlignment va) throws WriteException {
        super.setVerticalAlignment(va);
    }

    @Override // jxl.write.biff.CellXFRecord
    public void setOrientation(Orientation o) throws WriteException {
        super.setOrientation(o);
    }

    @Override // jxl.write.biff.CellXFRecord
    public void setWrap(boolean w) throws WriteException {
        super.setWrap(w);
    }

    public void setBorder(jxl.format.Border b, jxl.format.BorderLineStyle ls) throws WriteException {
        super.setBorder(b, ls, jxl.format.Colour.BLACK);
    }

    @Override // jxl.write.biff.CellXFRecord
    public void setBorder(jxl.format.Border b, jxl.format.BorderLineStyle ls, jxl.format.Colour c) throws WriteException {
        super.setBorder(b, ls, c);
    }

    public void setBackground(jxl.format.Colour c) throws WriteException {
        setBackground(c, jxl.format.Pattern.SOLID);
    }

    @Override // jxl.write.biff.CellXFRecord
    public void setBackground(jxl.format.Colour c, jxl.format.Pattern p) throws WriteException {
        super.setBackground(c, p);
    }

    @Override // jxl.write.biff.CellXFRecord
    public void setShrinkToFit(boolean s) throws WriteException {
        super.setShrinkToFit(s);
    }

    @Override // jxl.write.biff.CellXFRecord
    public void setIndentation(int i) throws WriteException {
        super.setIndentation(i);
    }

    @Override // jxl.write.biff.CellXFRecord
    public void setLocked(boolean l) throws WriteException {
        super.setLocked(l);
    }
}
