package jxl.read.biff;

import jxl.CellType;
import jxl.ErrorCell;
import jxl.biff.FormattingRecords;

/* loaded from: classes.dex */
class ErrorRecord extends CellValue implements ErrorCell {
    private int errorCode;

    public ErrorRecord(Record t, FormattingRecords fr, SheetImpl si) {
        super(t, fr, si);
        byte[] data = getRecord().getData();
        this.errorCode = data[6];
    }

    @Override // jxl.ErrorCell
    public int getErrorCode() {
        return this.errorCode;
    }

    @Override // jxl.Cell
    public String getContents() {
        return "ERROR " + this.errorCode;
    }

    @Override // jxl.Cell
    public CellType getType() {
        return CellType.ERROR;
    }
}
