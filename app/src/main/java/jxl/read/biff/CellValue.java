package jxl.read.biff;

import jxl.Cell;
import jxl.CellFeatures;
import jxl.biff.FormattingRecords;
import jxl.biff.IntegerHelper;
import jxl.biff.RecordData;
import jxl.biff.XFRecord;
import jxl.common.Logger;
import jxl.format.CellFormat;

/* loaded from: classes.dex */
public abstract class CellValue extends RecordData implements Cell, CellFeaturesAccessor {
    private static Logger logger = Logger.getLogger(CellValue.class);
    private int column;
    private CellFeatures features;
    private XFRecord format;
    private FormattingRecords formattingRecords;
    private boolean initialized;
    private int row;
    private SheetImpl sheet;
    private int xfIndex;

    protected CellValue(Record t, FormattingRecords fr, SheetImpl si) {
        super(t);
        byte[] data = getRecord().getData();
        this.row = IntegerHelper.getInt(data[0], data[1]);
        this.column = IntegerHelper.getInt(data[2], data[3]);
        this.xfIndex = IntegerHelper.getInt(data[4], data[5]);
        this.sheet = si;
        this.formattingRecords = fr;
        this.initialized = false;
    }

    @Override // jxl.Cell
    public final int getRow() {
        return this.row;
    }

    @Override // jxl.Cell
    public final int getColumn() {
        return this.column;
    }

    public final int getXFIndex() {
        return this.xfIndex;
    }

    @Override // jxl.Cell
    public CellFormat getCellFormat() {
        if (!this.initialized) {
            this.format = this.formattingRecords.getXFRecord(this.xfIndex);
            this.initialized = true;
        }
        return this.format;
    }

    @Override // jxl.Cell
    public boolean isHidden() {
        ColumnInfoRecord cir = this.sheet.getColumnInfo(this.column);
        if (cir != null && (cir.getWidth() == 0 || cir.getHidden())) {
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

    protected SheetImpl getSheet() {
        return this.sheet;
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
