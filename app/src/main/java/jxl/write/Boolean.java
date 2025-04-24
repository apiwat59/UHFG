package jxl.write;

import jxl.BooleanCell;
import jxl.format.CellFormat;
import jxl.write.biff.BooleanRecord;

/* loaded from: classes.dex */
public class Boolean extends BooleanRecord implements WritableCell, BooleanCell {
    public Boolean(int c, int r, boolean val) {
        super(c, r, val);
    }

    public Boolean(int c, int r, boolean val, CellFormat st) {
        super(c, r, val, st);
    }

    public Boolean(BooleanCell nc) {
        super(nc);
    }

    protected Boolean(int col, int row, Boolean b) {
        super(col, row, b);
    }

    @Override // jxl.write.biff.BooleanRecord
    public void setValue(boolean val) {
        super.setValue(val);
    }

    @Override // jxl.write.WritableCell
    public WritableCell copyTo(int col, int row) {
        return new Boolean(col, row, this);
    }
}
