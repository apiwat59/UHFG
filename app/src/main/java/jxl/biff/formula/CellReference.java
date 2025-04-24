package jxl.biff.formula;

import jxl.Cell;
import jxl.biff.CellReferenceHelper;
import jxl.biff.IntegerHelper;
import jxl.common.Logger;

/* loaded from: classes.dex */
class CellReference extends Operand implements ParsedThing {
    private static Logger logger = Logger.getLogger(CellReference.class);
    private int column;
    private boolean columnRelative;
    private Cell relativeTo;
    private int row;
    private boolean rowRelative;

    public CellReference(Cell rt) {
        this.relativeTo = rt;
    }

    public CellReference() {
    }

    public CellReference(String s) {
        this.column = CellReferenceHelper.getColumn(s);
        this.row = CellReferenceHelper.getRow(s);
        this.columnRelative = CellReferenceHelper.isColumnRelative(s);
        this.rowRelative = CellReferenceHelper.isRowRelative(s);
    }

    @Override // jxl.biff.formula.ParsedThing
    public int read(byte[] data, int pos) {
        this.row = IntegerHelper.getInt(data[pos], data[pos + 1]);
        int columnMask = IntegerHelper.getInt(data[pos + 2], data[pos + 3]);
        this.column = columnMask & 255;
        this.columnRelative = (columnMask & 16384) != 0;
        this.rowRelative = (32768 & columnMask) != 0;
        return 4;
    }

    public int getColumn() {
        return this.column;
    }

    public int getRow() {
        return this.row;
    }

    @Override // jxl.biff.formula.ParseItem
    public void getString(StringBuffer buf) {
        CellReferenceHelper.getCellReference(this.column, !this.columnRelative, this.row, !this.rowRelative, buf);
    }

    @Override // jxl.biff.formula.ParseItem
    byte[] getBytes() {
        byte[] data = new byte[5];
        data[0] = !useAlternateCode() ? Token.REF.getCode() : Token.REF.getCode2();
        IntegerHelper.getTwoBytes(this.row, data, 1);
        int grcol = this.column;
        if (this.rowRelative) {
            grcol |= 32768;
        }
        if (this.columnRelative) {
            grcol |= 16384;
        }
        IntegerHelper.getTwoBytes(grcol, data, 3);
        return data;
    }

    @Override // jxl.biff.formula.Operand, jxl.biff.formula.ParseItem
    public void adjustRelativeCellReferences(int colAdjust, int rowAdjust) {
        if (this.columnRelative) {
            this.column += colAdjust;
        }
        if (this.rowRelative) {
            this.row += rowAdjust;
        }
    }

    @Override // jxl.biff.formula.Operand, jxl.biff.formula.ParseItem
    public void columnInserted(int sheetIndex, int col, boolean currentSheet) {
        int i;
        if (currentSheet && (i = this.column) >= col) {
            this.column = i + 1;
        }
    }

    @Override // jxl.biff.formula.Operand, jxl.biff.formula.ParseItem
    void columnRemoved(int sheetIndex, int col, boolean currentSheet) {
        int i;
        if (currentSheet && (i = this.column) >= col) {
            this.column = i - 1;
        }
    }

    @Override // jxl.biff.formula.Operand, jxl.biff.formula.ParseItem
    void rowInserted(int sheetIndex, int r, boolean currentSheet) {
        int i;
        if (currentSheet && (i = this.row) >= r) {
            this.row = i + 1;
        }
    }

    @Override // jxl.biff.formula.Operand, jxl.biff.formula.ParseItem
    void rowRemoved(int sheetIndex, int r, boolean currentSheet) {
        int i;
        if (currentSheet && (i = this.row) >= r) {
            this.row = i - 1;
        }
    }

    @Override // jxl.biff.formula.ParseItem
    void handleImportedCellReferences() {
    }
}
