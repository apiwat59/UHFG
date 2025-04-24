package jxl.biff.formula;

import jxl.Cell;
import jxl.biff.CellReferenceHelper;
import jxl.biff.IntegerHelper;

/* loaded from: classes.dex */
class SharedFormulaArea extends Operand implements ParsedThing {
    private int columnFirst;
    private boolean columnFirstRelative;
    private int columnLast;
    private boolean columnLastRelative;
    private Cell relativeTo;
    private int rowFirst;
    private boolean rowFirstRelative;
    private int rowLast;
    private boolean rowLastRelative;

    public SharedFormulaArea(Cell rt) {
        this.relativeTo = rt;
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
        this.rowFirst = IntegerHelper.getShort(data[pos], data[pos + 1]);
        this.rowLast = IntegerHelper.getShort(data[pos + 2], data[pos + 3]);
        int columnMask = IntegerHelper.getInt(data[pos + 4], data[pos + 5]);
        this.columnFirst = columnMask & 255;
        boolean z = (columnMask & 16384) != 0;
        this.columnFirstRelative = z;
        this.rowFirstRelative = (columnMask & 32768) != 0;
        if (z) {
            this.columnFirst = this.relativeTo.getColumn() + this.columnFirst;
        }
        if (this.rowFirstRelative) {
            this.rowFirst = this.relativeTo.getRow() + this.rowFirst;
        }
        int columnMask2 = IntegerHelper.getInt(data[pos + 6], data[pos + 7]);
        this.columnLast = columnMask2 & 255;
        boolean z2 = (columnMask2 & 16384) != 0;
        this.columnLastRelative = z2;
        this.rowLastRelative = (32768 & columnMask2) != 0;
        if (z2) {
            this.columnLast = this.relativeTo.getColumn() + this.columnLast;
        }
        if (this.rowLastRelative) {
            this.rowLast = this.relativeTo.getRow() + this.rowLast;
            return 8;
        }
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
        data[0] = Token.AREA.getCode();
        IntegerHelper.getTwoBytes(this.rowFirst, data, 1);
        IntegerHelper.getTwoBytes(this.rowLast, data, 3);
        IntegerHelper.getTwoBytes(this.columnFirst, data, 5);
        IntegerHelper.getTwoBytes(this.columnLast, data, 7);
        return data;
    }

    @Override // jxl.biff.formula.ParseItem
    void handleImportedCellReferences() {
    }
}
