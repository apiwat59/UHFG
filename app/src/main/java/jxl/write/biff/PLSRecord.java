package jxl.write.biff;

import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class PLSRecord extends WritableRecordData {
    private byte[] data;

    public PLSRecord(jxl.read.biff.PLSRecord hr) {
        super(Type.PLS);
        this.data = hr.getData();
    }

    public PLSRecord(PLSRecord hr) {
        super(Type.PLS);
        byte[] bArr = new byte[hr.data.length];
        this.data = bArr;
        System.arraycopy(hr.data, 0, bArr, 0, bArr.length);
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        return this.data;
    }
}
