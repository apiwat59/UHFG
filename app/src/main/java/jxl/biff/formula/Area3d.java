package jxl.biff.formula;

import jxl.biff.CellReferenceHelper;
import jxl.biff.IntegerHelper;
import jxl.common.Assert;
import jxl.common.Logger;

/* loaded from: classes.dex */
class Area3d extends Operand implements ParsedThing {
    private static Logger logger = Logger.getLogger(Area3d.class);
    private int columnFirst;
    private boolean columnFirstRelative;
    private int columnLast;
    private boolean columnLastRelative;
    private int rowFirst;
    private boolean rowFirstRelative;
    private int rowLast;
    private boolean rowLastRelative;
    private int sheet;
    private ExternalSheet workbook;

    Area3d(ExternalSheet es) {
        this.workbook = es;
    }

    Area3d(String s, ExternalSheet es) throws FormulaException {
        this.workbook = es;
        int seppos = s.lastIndexOf(":");
        Assert.verify(seppos != -1);
        String endcell = s.substring(seppos + 1);
        int sep = s.indexOf(33);
        String cellString = s.substring(sep + 1, seppos);
        this.columnFirst = CellReferenceHelper.getColumn(cellString);
        this.rowFirst = CellReferenceHelper.getRow(cellString);
        String sheetName = s.substring(0, sep);
        if (sheetName.charAt(0) == '\'' && sheetName.charAt(sheetName.length() - 1) == '\'') {
            sheetName = sheetName.substring(1, sheetName.length() - 1);
        }
        int externalSheetIndex = es.getExternalSheetIndex(sheetName);
        this.sheet = externalSheetIndex;
        if (externalSheetIndex < 0) {
            throw new FormulaException(FormulaException.SHEET_REF_NOT_FOUND, sheetName);
        }
        this.columnLast = CellReferenceHelper.getColumn(endcell);
        this.rowLast = CellReferenceHelper.getRow(endcell);
        this.columnFirstRelative = true;
        this.rowFirstRelative = true;
        this.columnLastRelative = true;
        this.rowLastRelative = true;
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
        this.sheet = IntegerHelper.getInt(data[pos], data[pos + 1]);
        this.rowFirst = IntegerHelper.getInt(data[pos + 2], data[pos + 3]);
        this.rowLast = IntegerHelper.getInt(data[pos + 4], data[pos + 5]);
        int columnMask = IntegerHelper.getInt(data[pos + 6], data[pos + 7]);
        this.columnFirst = columnMask & 255;
        this.columnFirstRelative = (columnMask & 16384) != 0;
        this.rowFirstRelative = (columnMask & 32768) != 0;
        int columnMask2 = IntegerHelper.getInt(data[pos + 8], data[pos + 9]);
        this.columnLast = columnMask2 & 255;
        this.columnLastRelative = (columnMask2 & 16384) != 0;
        this.rowLastRelative = (32768 & columnMask2) != 0;
        return 10;
    }

    @Override // jxl.biff.formula.ParseItem
    public void getString(StringBuffer buf) {
        CellReferenceHelper.getCellReference(this.sheet, this.columnFirst, this.rowFirst, this.workbook, buf);
        buf.append(':');
        CellReferenceHelper.getCellReference(this.columnLast, this.rowLast, buf);
    }

    @Override // jxl.biff.formula.ParseItem
    byte[] getBytes() {
        byte[] data = new byte[11];
        data[0] = Token.AREA3D.getCode();
        IntegerHelper.getTwoBytes(this.sheet, data, 1);
        IntegerHelper.getTwoBytes(this.rowFirst, data, 3);
        IntegerHelper.getTwoBytes(this.rowLast, data, 5);
        int grcol = this.columnFirst;
        if (this.rowFirstRelative) {
            grcol |= 32768;
        }
        if (this.columnFirstRelative) {
            grcol |= 16384;
        }
        IntegerHelper.getTwoBytes(grcol, data, 7);
        int grcol2 = this.columnLast;
        if (this.rowLastRelative) {
            grcol2 |= 32768;
        }
        if (this.columnLastRelative) {
            grcol2 |= 16384;
        }
        IntegerHelper.getTwoBytes(grcol2, data, 9);
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
    public void columnInserted(int sheetIndex, int col, boolean currentSheet) {
        if (sheetIndex != this.sheet) {
            return;
        }
        int i = this.columnFirst;
        if (i >= col) {
            this.columnFirst = i + 1;
        }
        int i2 = this.columnLast;
        if (i2 >= col) {
            this.columnLast = i2 + 1;
        }
    }

    @Override // jxl.biff.formula.Operand, jxl.biff.formula.ParseItem
    void columnRemoved(int sheetIndex, int col, boolean currentSheet) {
        if (sheetIndex != this.sheet) {
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
        if (sheetIndex != this.sheet || (i = this.rowLast) == 65535) {
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
        if (sheetIndex != this.sheet || (i = this.rowLast) == 65535) {
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

    protected void setRangeData(int sht, int colFirst, int colLast, int rwFirst, int rwLast, boolean colFirstRel, boolean colLastRel, boolean rowFirstRel, boolean rowLastRel) {
        this.sheet = sht;
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
        setInvalid();
    }
}
