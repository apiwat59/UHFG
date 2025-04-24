package jxl.write;

import jxl.Cell;
import jxl.format.CellFormat;
import jxl.write.biff.BlankRecord;

/* loaded from: classes.dex */
public class Blank extends BlankRecord implements WritableCell {
    public Blank(int c, int r) {
        super(c, r);
    }

    public Blank(int c, int r, CellFormat st) {
        super(c, r, st);
    }

    public Blank(Cell lc) {
        super(lc);
    }

    protected Blank(int col, int row, Blank b) {
        super(col, row, b);
    }

    @Override // jxl.write.WritableCell
    public WritableCell copyTo(int col, int row) {
        return new Blank(col, row, this);
    }
}
