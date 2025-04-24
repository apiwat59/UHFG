package jxl.biff.formula;

import jxl.Cell;
import jxl.biff.CellReferenceHelper;
import jxl.biff.IntegerHelper;
import jxl.common.Logger;

/* loaded from: classes.dex */
class SharedFormulaCellReference extends Operand implements ParsedThing {
    private static Logger logger = Logger.getLogger(SharedFormulaCellReference.class);
    private int column;
    private boolean columnRelative;
    private Cell relativeTo;
    private int row;
    private boolean rowRelative;

    public SharedFormulaCellReference(Cell rt) {
        this.relativeTo = rt;
    }

    @Override // jxl.biff.formula.ParsedThing
    public int read(byte[] data, int pos) {
        Cell cell;
        Cell cell2;
        this.row = IntegerHelper.getShort(data[pos], data[pos + 1]);
        int columnMask = IntegerHelper.getInt(data[pos + 2], data[pos + 3]);
        this.column = (byte) (columnMask & 255);
        boolean z = (columnMask & 16384) != 0;
        this.columnRelative = z;
        this.rowRelative = (32768 & columnMask) != 0;
        if (z && (cell2 = this.relativeTo) != null) {
            this.column = cell2.getColumn() + this.column;
        }
        if (this.rowRelative && (cell = this.relativeTo) != null) {
            this.row = cell.getRow() + this.row;
            return 4;
        }
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
        CellReferenceHelper.getCellReference(this.column, this.row, buf);
    }

    @Override // jxl.biff.formula.ParseItem
    byte[] getBytes() {
        byte[] data = new byte[5];
        data[0] = Token.REF.getCode();
        IntegerHelper.getTwoBytes(this.row, data, 1);
        int columnMask = this.column;
        if (this.columnRelative) {
            columnMask |= 16384;
        }
        if (this.rowRelative) {
            columnMask |= 32768;
        }
        IntegerHelper.getTwoBytes(columnMask, data, 3);
        return data;
    }

    @Override // jxl.biff.formula.ParseItem
    void handleImportedCellReferences() {
    }
}
