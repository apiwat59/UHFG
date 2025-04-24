package jxl.write.biff;

import jxl.biff.IntegerHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class RefreshAllRecord extends WritableRecordData {
    private byte[] data;
    private boolean refreshall;

    public RefreshAllRecord(boolean refresh) {
        super(Type.REFRESHALL);
        this.refreshall = refresh;
        byte[] bArr = new byte[2];
        this.data = bArr;
        if (refresh) {
            IntegerHelper.getTwoBytes(1, bArr, 0);
        }
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        return this.data;
    }
}
