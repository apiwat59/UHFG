package jxl.read.biff;

import jxl.Cell;
import jxl.CellFeatures;
import jxl.CellType;
import jxl.biff.FormattingRecords;
import jxl.common.Logger;
import jxl.format.CellFormat;

/* loaded from: classes.dex */
class MulBlankCell implements Cell, CellFeaturesAccessor {
    private static Logger logger = Logger.getLogger(MulBlankCell.class);
    private CellFormat cellFormat;
    private int column;
    private CellFeatures features;
    private FormattingRecords formattingRecords;
    private boolean initialized = false;
    private int row;
    private SheetImpl sheet;
    private int xfIndex;

    public MulBlankCell(int r, int c, int xfi, FormattingRecords fr, SheetImpl si) {
        this.row = r;
        this.column = c;
        this.xfIndex = xfi;
        this.formattingRecords = fr;
        this.sheet = si;
    }

    @Override // jxl.Cell
    public final int getRow() {
        return this.row;
    }

    @Override // jxl.Cell
    public final int getColumn() {
        return this.column;
    }

    @Override // jxl.Cell
    public String getContents() {
        return "";
    }

    @Override // jxl.Cell
    public CellType getType() {
        return CellType.EMPTY;
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

    @Override // jxl.Cell
    public CellFeatures getCellFeatures() {
        return this.features;
    }

    @Override // jxl.read.biff.CellFeaturesAccessor
    public void setCellFeatures(CellFeatures cf) {
        if (this.features != null) {
            logger.warn("current cell features not null - overwriting");
        }
        this.features = cf;
    }
}
