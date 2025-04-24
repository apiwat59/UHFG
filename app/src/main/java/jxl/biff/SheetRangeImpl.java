package jxl.biff;

import androidx.core.internal.view.SupportMenu;
import jxl.Cell;
import jxl.Range;
import jxl.Sheet;

/* loaded from: classes.dex */
public class SheetRangeImpl implements Range {
    private int column1;
    private int column2;
    private int row1;
    private int row2;
    private Sheet sheet;

    public SheetRangeImpl(Sheet s, int c1, int r1, int c2, int r2) {
        this.sheet = s;
        this.row1 = r1;
        this.row2 = r2;
        this.column1 = c1;
        this.column2 = c2;
    }

    public SheetRangeImpl(SheetRangeImpl c, Sheet s) {
        this.sheet = s;
        this.row1 = c.row1;
        this.row2 = c.row2;
        this.column1 = c.column1;
        this.column2 = c.column2;
    }

    @Override // jxl.Range
    public Cell getTopLeft() {
        if (this.column1 >= this.sheet.getColumns() || this.row1 >= this.sheet.getRows()) {
            return new EmptyCell(this.column1, this.row1);
        }
        return this.sheet.getCell(this.column1, this.row1);
    }

    @Override // jxl.Range
    public Cell getBottomRight() {
        if (this.column2 >= this.sheet.getColumns() || this.row2 >= this.sheet.getRows()) {
            return new EmptyCell(this.column2, this.row2);
        }
        return this.sheet.getCell(this.column2, this.row2);
    }

    @Override // jxl.Range
    public int getFirstSheetIndex() {
        return -1;
    }

    @Override // jxl.Range
    public int getLastSheetIndex() {
        return -1;
    }

    public boolean intersects(SheetRangeImpl range) {
        if (range == this) {
            return true;
        }
        if (this.row2 >= range.row1 && this.row1 <= range.row2 && this.column2 >= range.column1 && this.column1 <= range.column2) {
            return true;
        }
        return false;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        CellReferenceHelper.getCellReference(this.column1, this.row1, sb);
        sb.append('-');
        CellReferenceHelper.getCellReference(this.column2, this.row2, sb);
        return sb.toString();
    }

    public void insertRow(int r) {
        int i = this.row2;
        if (r > i) {
            return;
        }
        int i2 = this.row1;
        if (r <= i2) {
            this.row1 = i2 + 1;
        }
        if (r <= i) {
            this.row2 = i + 1;
        }
    }

    public void insertColumn(int c) {
        int i = this.column2;
        if (c > i) {
            return;
        }
        int i2 = this.column1;
        if (c <= i2) {
            this.column1 = i2 + 1;
        }
        if (c <= i) {
            this.column2 = i + 1;
        }
    }

    public void removeRow(int r) {
        int i = this.row2;
        if (r > i) {
            return;
        }
        int i2 = this.row1;
        if (r < i2) {
            this.row1 = i2 - 1;
        }
        if (r < i) {
            this.row2 = i - 1;
        }
    }

    public void removeColumn(int c) {
        int i = this.column2;
        if (c > i) {
            return;
        }
        int i2 = this.column1;
        if (c < i2) {
            this.column1 = i2 - 1;
        }
        if (c < i) {
            this.column2 = i - 1;
        }
    }

    public int hashCode() {
        return (((this.row1 ^ SupportMenu.USER_MASK) ^ this.row2) ^ this.column1) ^ this.column2;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SheetRangeImpl)) {
            return false;
        }
        SheetRangeImpl compare = (SheetRangeImpl) o;
        return this.column1 == compare.column1 && this.column2 == compare.column2 && this.row1 == compare.row1 && this.row2 == compare.row2;
    }
}
