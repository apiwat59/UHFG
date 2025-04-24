package jxl.write.biff;

import jxl.CellType;
import jxl.LabelCell;
import jxl.biff.FormattingRecords;
import jxl.biff.IntegerHelper;
import jxl.biff.Type;
import jxl.common.Assert;
import jxl.common.Logger;
import jxl.format.CellFormat;

/* loaded from: classes.dex */
public abstract class LabelRecord extends CellValue {
    private static Logger logger = Logger.getLogger(LabelRecord.class);
    private String contents;
    private int index;
    private SharedStrings sharedStrings;

    protected LabelRecord(int c, int r, String cont) {
        super(Type.LABELSST, c, r);
        this.contents = cont;
        if (cont == null) {
            this.contents = "";
        }
    }

    protected LabelRecord(int c, int r, String cont, CellFormat st) {
        super(Type.LABELSST, c, r, st);
        this.contents = cont;
        if (cont == null) {
            this.contents = "";
        }
    }

    protected LabelRecord(int c, int r, LabelRecord lr) {
        super(Type.LABELSST, c, r, lr);
        this.contents = lr.contents;
    }

    protected LabelRecord(LabelCell lc) {
        super(Type.LABELSST, lc);
        String string = lc.getString();
        this.contents = string;
        if (string == null) {
            this.contents = "";
        }
    }

    @Override // jxl.Cell
    public CellType getType() {
        return CellType.LABEL;
    }

    @Override // jxl.write.biff.CellValue, jxl.biff.WritableRecordData
    public byte[] getData() {
        byte[] celldata = super.getData();
        byte[] data = new byte[celldata.length + 4];
        System.arraycopy(celldata, 0, data, 0, celldata.length);
        IntegerHelper.getFourBytes(this.index, data, celldata.length);
        return data;
    }

    @Override // jxl.Cell
    public String getContents() {
        return this.contents;
    }

    public String getString() {
        return this.contents;
    }

    protected void setString(String s) {
        if (s == null) {
            s = "";
        }
        this.contents = s;
        if (!isReferenced()) {
            return;
        }
        Assert.verify(this.sharedStrings != null);
        int index = this.sharedStrings.getIndex(this.contents);
        this.index = index;
        this.contents = this.sharedStrings.get(index);
    }

    @Override // jxl.write.biff.CellValue
    void setCellDetails(FormattingRecords fr, SharedStrings ss, WritableSheetImpl s) {
        super.setCellDetails(fr, ss, s);
        this.sharedStrings = ss;
        int index = ss.getIndex(this.contents);
        this.index = index;
        this.contents = this.sharedStrings.get(index);
    }
}
