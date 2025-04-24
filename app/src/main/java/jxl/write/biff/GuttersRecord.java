package jxl.write.biff;

import jxl.biff.IntegerHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class GuttersRecord extends WritableRecordData {
    private int colGutter;
    private byte[] data;
    private int maxColumnOutline;
    private int maxRowOutline;
    private int rowGutter;

    public GuttersRecord() {
        super(Type.GUTS);
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        byte[] bArr = new byte[8];
        this.data = bArr;
        IntegerHelper.getTwoBytes(this.rowGutter, bArr, 0);
        IntegerHelper.getTwoBytes(this.colGutter, this.data, 2);
        IntegerHelper.getTwoBytes(this.maxRowOutline, this.data, 4);
        IntegerHelper.getTwoBytes(this.maxColumnOutline, this.data, 6);
        return this.data;
    }

    public int getMaxRowOutline() {
        return this.maxRowOutline;
    }

    public void setMaxRowOutline(int value) {
        this.maxRowOutline = value;
        this.rowGutter = (value * 14) + 1;
    }

    public int getMaxColumnOutline() {
        return this.maxColumnOutline;
    }

    public void setMaxColumnOutline(int value) {
        this.maxColumnOutline = value;
        this.colGutter = (value * 14) + 1;
    }
}
