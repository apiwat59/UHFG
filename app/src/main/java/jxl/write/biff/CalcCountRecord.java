package jxl.write.biff;

import jxl.biff.IntegerHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class CalcCountRecord extends WritableRecordData {
    private int calcCount;
    private byte[] data;

    public CalcCountRecord(int cnt) {
        super(Type.CALCCOUNT);
        this.calcCount = cnt;
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        byte[] data = new byte[2];
        IntegerHelper.getTwoBytes(this.calcCount, data, 0);
        return data;
    }
}
