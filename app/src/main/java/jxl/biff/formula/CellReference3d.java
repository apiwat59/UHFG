package jxl.biff.formula;

import jxl.Cell;
import jxl.biff.CellReferenceHelper;
import jxl.biff.IntegerHelper;
import jxl.common.Logger;

/* loaded from: classes.dex */
class CellReference3d extends Operand implements ParsedThing {
    private static Logger logger = Logger.getLogger(CellReference3d.class);
    private int column;
    private boolean columnRelative;
    private Cell relativeTo;
    private int row;
    private boolean rowRelative;
    private int sheet;
    private ExternalSheet workbook;

    public CellReference3d(Cell rt, ExternalSheet w) {
        this.relativeTo = rt;
        this.workbook = w;
    }

    public CellReference3d(String s, ExternalSheet w) throws FormulaException {
        this.workbook = w;
        this.columnRelative = true;
        this.rowRelative = true;
        int sep = s.indexOf(33);
        String cellString = s.substring(sep + 1);
        this.column = CellReferenceHelper.getColumn(cellString);
        this.row = CellReferenceHelper.getRow(cellString);
        String sheetName = s.substring(0, sep);
        if (sheetName.charAt(0) == '\'' && sheetName.charAt(sheetName.length() - 1) == '\'') {
            sheetName = sheetName.substring(1, sheetName.length() - 1);
        }
        int externalSheetIndex = w.getExternalSheetIndex(sheetName);
        this.sheet = externalSheetIndex;
        if (externalSheetIndex < 0) {
            throw new FormulaException(FormulaException.SHEET_REF_NOT_FOUND, sheetName);
        }
    }

    @Override // jxl.biff.formula.ParsedThing
    public int read(byte[] data, int pos) {
        this.sheet = IntegerHelper.getInt(data[pos], data[pos + 1]);
        this.row = IntegerHelper.getInt(data[pos + 2], data[pos + 3]);
        int columnMask = IntegerHelper.getInt(data[pos + 4], data[pos + 5]);
        this.column = columnMask & 255;
        this.columnRelative = (columnMask & 16384) != 0;
        this.rowRelative = (32768 & columnMask) != 0;
        return 6;
    }

    public int getColumn() {
        return this.column;
    }

    public int getRow() {
        return this.row;
    }

    @Override // jxl.biff.formula.ParseItem
    public void getString(StringBuffer buf) {
        CellReferenceHelper.getCellReference(this.sheet, this.column, !this.columnRelative, this.row, !this.rowRelative, this.workbook, buf);
    }

    @Override // jxl.biff.formula.ParseItem
    byte[] getBytes() {
        byte[] data = new byte[7];
        data[0] = Token.REF3D.getCode();
        IntegerHelper.getTwoBytes(this.sheet, data, 1);
        IntegerHelper.getTwoBytes(this.row, data, 3);
        int grcol = this.column;
        if (this.rowRelative) {
            grcol |= 32768;
        }
        if (this.columnRelative) {
            grcol |= 16384;
        }
        IntegerHelper.getTwoBytes(grcol, data, 5);
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
        if (sheetIndex == this.sheet && (i = this.column) >= col) {
            this.column = i + 1;
        }
    }

    @Override // jxl.biff.formula.Operand, jxl.biff.formula.ParseItem
    void columnRemoved(int sheetIndex, int col, boolean currentSheet) {
        int i;
        if (sheetIndex == this.sheet && (i = this.column) >= col) {
            this.column = i - 1;
        }
    }

    @Override // jxl.biff.formula.Operand, jxl.biff.formula.ParseItem
    void rowInserted(int sheetIndex, int r, boolean currentSheet) {
        int i;
        if (sheetIndex == this.sheet && (i = this.row) >= r) {
            this.row = i + 1;
        }
    }

    @Override // jxl.biff.formula.Operand, jxl.biff.formula.ParseItem
    void rowRemoved(int sheetIndex, int r, boolean currentSheet) {
        int i;
        if (sheetIndex == this.sheet && (i = this.row) >= r) {
            this.row = i - 1;
        }
    }

    @Override // jxl.biff.formula.ParseItem
    void handleImportedCellReferences() {
        setInvalid();
    }
}
