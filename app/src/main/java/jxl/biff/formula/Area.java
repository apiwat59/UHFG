package jxl.biff.formula;

import jxl.biff.CellReferenceHelper;
import jxl.biff.IntegerHelper;
import jxl.common.Assert;
import jxl.common.Logger;

/* loaded from: classes.dex */
class Area extends Operand implements ParsedThing {
    private static Logger logger = Logger.getLogger(Area.class);
    private int columnFirst;
    private boolean columnFirstRelative;
    private int columnLast;
    private boolean columnLastRelative;
    private int rowFirst;
    private boolean rowFirstRelative;
    private int rowLast;
    private boolean rowLastRelative;

    Area() {
    }

    Area(String s) {
        int seppos = s.indexOf(":");
        Assert.verify(seppos != -1);
        String startcell = s.substring(0, seppos);
        String endcell = s.substring(seppos + 1);
        this.columnFirst = CellReferenceHelper.getColumn(startcell);
        this.rowFirst = CellReferenceHelper.getRow(startcell);
        this.columnLast = CellReferenceHelper.getColumn(endcell);
        this.rowLast = CellReferenceHelper.getRow(endcell);
        this.columnFirstRelative = CellReferenceHelper.isColumnRelative(startcell);
        this.rowFirstRelative = CellReferenceHelper.isRowRelative(startcell);
        this.columnLastRelative = CellReferenceHelper.isColumnRelative(endcell);
        this.rowLastRelative = CellReferenceHelper.isRowRelative(endcell);
    }

    int getFirstColumn() {
        return this.columnFirst;
    }

    int getFirstRow() {
        return this.rowFirst;
    }

    int getLastColumn() {
        return this.columnLast;
    }

    int getLastRow() {
        return this.rowLast;
    }

    @Override // jxl.biff.formula.ParsedThing
    public int read(byte[] data, int pos) {
        this.rowFirst = IntegerHelper.getInt(data[pos], data[pos + 1]);
        this.rowLast = IntegerHelper.getInt(data[pos + 2], data[pos + 3]);
        int columnMask = IntegerHelper.getInt(data[pos + 4], data[pos + 5]);
        this.columnFirst = columnMask & 255;
        this.columnFirstRelative = (columnMask & 16384) != 0;
        this.rowFirstRelative = (columnMask & 32768) != 0;
        int columnMask2 = IntegerHelper.getInt(data[pos + 6], data[pos + 7]);
        this.columnLast = columnMask2 & 255;
        this.columnLastRelative = (columnMask2 & 16384) != 0;
        this.rowLastRelative = (32768 & columnMask2) != 0;
        return 8;
    }

    @Override // jxl.biff.formula.ParseItem
    public void getString(StringBuffer buf) {
        CellReferenceHelper.getCellReference(this.columnFirst, this.rowFirst, buf);
        buf.append(':');
        CellReferenceHelper.getCellReference(this.columnLast, this.rowLast, buf);
    }

    @Override // jxl.biff.formula.ParseItem
    byte[] getBytes() {
        byte[] data = new byte[9];
        data[0] = !useAlternateCode() ? Token.AREA.getCode() : Token.AREA.getCode2();
        IntegerHelper.getTwoBytes(this.rowFirst, data, 1);
        IntegerHelper.getTwoBytes(this.rowLast, data, 3);
        int grcol = this.columnFirst;
        if (this.rowFirstRelative) {
            grcol |= 32768;
        }
        if (this.columnFirstRelative) {
            grcol |= 16384;
        }
        IntegerHelper.getTwoBytes(grcol, data, 5);
        int grcol2 = this.columnLast;
        if (this.rowLastRelative) {
            grcol2 |= 32768;
        }
        if (this.columnLastRelative) {
            grcol2 |= 16384;
        }
        IntegerHelper.getTwoBytes(grcol2, data, 7);
        return data;
    }

    @Override // jxl.biff.formula.Operand, jxl.biff.formula.ParseItem
    public void adjustRelativeCellReferences(int colAdjust, int rowAdjust) {
        if (this.columnFirstRelative) {
            this.columnFirst += colAdjust;
        }
        if (this.columnLastRelative) {
            this.columnLast += colAdjust;
        }
        if (this.rowFirstRelative) {
            this.rowFirst += rowAdjust;
        }
        if (this.rowLastRelative) {
            this.rowLast += rowAdjust;
        }
    }

    @Override // jxl.biff.formula.Operand, jxl.biff.formula.ParseItem
    void columnInserted(int sheetIndex, int col, boolean currentSheet) {
        if (!currentSheet) {
            return;
        }
        int i = this.columnFirst;
        if (col <= i) {
            this.columnFirst = i + 1;
        }
        int i2 = this.columnLast;
        if (col <= i2) {
            this.columnLast = i2 + 1;
        }
    }

    @Override // jxl.biff.formula.Operand, jxl.biff.formula.ParseItem
    void columnRemoved(int sheetIndex, int col, boolean currentSheet) {
        if (!currentSheet) {
            return;
        }
        int i = this.columnFirst;
        if (col < i) {
            this.columnFirst = i - 1;
        }
        int i2 = this.columnLast;
        if (col <= i2) {
            this.columnLast = i2 - 1;
        }
    }

    @Override // jxl.biff.formula.Operand, jxl.biff.formula.ParseItem
    void rowInserted(int sheetIndex, int row, boolean currentSheet) {
        int i;
        if (!currentSheet || (i = this.rowLast) == 65535) {
            return;
        }
        int i2 = this.rowFirst;
        if (row <= i2) {
            this.rowFirst = i2 + 1;
        }
        if (row <= i) {
            this.rowLast = i + 1;
        }
    }

    @Override // jxl.biff.formula.Operand, jxl.biff.formula.ParseItem
    void rowRemoved(int sheetIndex, int row, boolean currentSheet) {
        int i;
        if (!currentSheet || (i = this.rowLast) == 65535) {
            return;
        }
        int i2 = this.rowFirst;
        if (row < i2) {
            this.rowFirst = i2 - 1;
        }
        if (row <= i) {
            this.rowLast = i - 1;
        }
    }

    protected void setRangeData(int colFirst, int colLast, int rwFirst, int rwLast, boolean colFirstRel, boolean colLastRel, boolean rowFirstRel, boolean rowLastRel) {
        this.columnFirst = colFirst;
        this.columnLast = colLast;
        this.rowFirst = rwFirst;
        this.rowLast = rwLast;
        this.columnFirstRelative = colFirstRel;
        this.columnLastRelative = colLastRel;
        this.rowFirstRelative = rowFirstRel;
        this.rowLastRelative = rowLastRel;
    }

    @Override // jxl.biff.formula.ParseItem
    void handleImportedCellReferences() {
    }
}
