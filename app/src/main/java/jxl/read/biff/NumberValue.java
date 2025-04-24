package jxl.read.biff;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import jxl.CellFeatures;
import jxl.CellType;
import jxl.NumberCell;
import jxl.biff.FormattingRecords;
import jxl.format.CellFormat;

/* loaded from: classes.dex */
class NumberValue implements NumberCell, CellFeaturesAccessor {
    private static DecimalFormat defaultFormat = new DecimalFormat("#.###");
    private CellFormat cellFormat;
    private int column;
    private CellFeatures features;
    private FormattingRecords formattingRecords;
    private int row;
    private SheetImpl sheet;
    private double value;
    private int xfIndex;
    private NumberFormat format = defaultFormat;
    private boolean initialized = false;

    public NumberValue(int r, int c, double val, int xfi, FormattingRecords fr, SheetImpl si) {
        this.row = r;
        this.column = c;
        this.value = val;
        this.xfIndex = xfi;
        this.formattingRecords = fr;
        this.sheet = si;
    }

    final void setNumberFormat(NumberFormat f) {
        if (f != null) {
            this.format = f;
        }
    }

    @Override // jxl.Cell
    public final int getRow() {
        return this.row;
    }

    @Override // jxl.Cell
    public final int getColumn() {
        return this.column;
    }

    @Override // jxl.NumberCell
    public double getValue() {
        return this.value;
    }

    @Override // jxl.Cell
    public String getContents() {
        return this.format.format(this.value);
    }

    @Override // jxl.Cell
    public CellType getType() {
        return CellType.NUMBER;
    }

    @Override // jxl.Cell
    public CellFormat getCellFormat() {
        if (!this.initialized) {
            this.cellFormat = this.formattingRecords.getXFRecord(this.xfIndex);
            this.initialized = true;
        }
        return this.cellFormat;
    }

    @Override // jxl.Cell
    public boolean isHidden() {
        ColumnInfoRecord cir = this.sheet.getColumnInfo(this.column);
        if (cir != null && cir.getWidth() == 0) {
            return true;
        }
        RowRecord rr = this.sheet.getRowInfo(this.row);
        if (rr == null) {
            return false;
        }
        if (rr.getRowHeight() == 0 || rr.isCollapsed()) {
            return true;
        }
        return false;
    }

    @Override // jxl.NumberCell
    public NumberFormat getNumberFormat() {
        return this.format;
    }

    @Override // jxl.Cell
    public CellFeatures getCellFeatures() {
        return this.features;
    }

    @Override // jxl.read.biff.CellFeaturesAccessor
    public void setCellFeatures(CellFeatures cf) {
        this.features = cf;
    }
}
