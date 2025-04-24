package jxl.write;

import jxl.LabelCell;
import jxl.format.CellFormat;
import jxl.write.biff.LabelRecord;

/* loaded from: classes.dex */
public class Label extends LabelRecord implements WritableCell, LabelCell {
    public Label(int c, int r, String cont) {
        super(c, r, cont);
    }

    public Label(int c, int r, String cont, CellFormat st) {
        super(c, r, cont, st);
    }

    protected Label(int col, int row, Label l) {
        super(col, row, l);
    }

    public Label(LabelCell lc) {
        super(lc);
    }

    @Override // jxl.write.biff.LabelRecord
    public void setString(String s) {
        super.setString(s);
    }

    @Override // jxl.write.WritableCell
    public WritableCell copyTo(int col, int row) {
        return new Label(col, row, this);
    }
}
