package jxl.read.biff;

import jxl.BooleanCell;
import jxl.CellType;
import jxl.biff.FormattingRecords;
import jxl.common.Assert;

/* loaded from: classes.dex */
class BooleanRecord extends CellValue implements BooleanCell {
    private boolean error;
    private boolean value;

    public BooleanRecord(Record t, FormattingRecords fr, SheetImpl si) {
        super(t, fr, si);
        this.error = false;
        this.value = false;
        byte[] data = getRecord().getData();
        boolean z = data[7] == 1;
        this.error = z;
        if (!z) {
            this.value = data[6] == 1;
        }
    }

    public boolean isError() {
        return this.error;
    }

    @Override // jxl.BooleanCell
    public boolean getValue() {
        return this.value;
    }

    @Override // jxl.Cell
    public String getContents() {
        Assert.verify(!isError());
        return new Boolean(this.value).toString();
    }

    @Override // jxl.Cell
    public CellType getType() {
        return CellType.BOOLEAN;
    }

    @Override // jxl.biff.RecordData
    public Record getRecord() {
        return super.getRecord();
    }
}
