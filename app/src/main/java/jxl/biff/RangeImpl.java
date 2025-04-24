package jxl.biff;

import jxl.Cell;
import jxl.Range;
import jxl.Sheet;
import jxl.common.Logger;

/* loaded from: classes.dex */
public class RangeImpl implements Range {
    private static Logger logger = Logger.getLogger(RangeImpl.class);
    private int column1;
    private int column2;
    private int row1;
    private int row2;
    private int sheet1;
    private int sheet2;
    private WorkbookMethods workbook;

    public RangeImpl(WorkbookMethods w, int s1, int c1, int r1, int s2, int c2, int r2) {
        this.workbook = w;
        this.sheet1 = s1;
        this.sheet2 = s2;
        this.row1 = r1;
        this.row2 = r2;
        this.column1 = c1;
        this.column2 = c2;
    }

    @Override // jxl.Range
    public Cell getTopLeft() {
        Sheet s = this.workbook.getReadSheet(this.sheet1);
        if (this.column1 < s.getColumns() && this.row1 < s.getRows()) {
            return s.getCell(this.column1, this.row1);
        }
        return new EmptyCell(this.column1, this.row1);
    }

    @Override // jxl.Range
    public Cell getBottomRight() {
        Sheet s = this.workbook.getReadSheet(this.sheet2);
        if (this.column2 < s.getColumns() && this.row2 < s.getRows()) {
            return s.getCell(this.column2, this.row2);
        }
        return new EmptyCell(this.column2, this.row2);
    }

    @Override // jxl.Range
    public int getFirstSheetIndex() {
        return this.sheet1;
    }

    @Override // jxl.Range
    public int getLastSheetIndex() {
        return this.sheet2;
    }
}
