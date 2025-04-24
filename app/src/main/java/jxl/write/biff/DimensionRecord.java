package jxl.write.biff;

import jxl.biff.IntegerHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class DimensionRecord extends WritableRecordData {
    private byte[] data;
    private int numCols;
    private int numRows;

    public DimensionRecord(int r, int c) {
        super(Type.DIMENSION);
        this.numRows = r;
        this.numCols = c;
        byte[] bArr = new byte[14];
        this.data = bArr;
        IntegerHelper.getFourBytes(r, bArr, 4);
        IntegerHelper.getTwoBytes(this.numCols, this.data, 10);
    }

    @Override // jxl.biff.WritableRecordData
    protected byte[] getData() {
        return this.data;
    }
}
