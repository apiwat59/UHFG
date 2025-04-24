package jxl.write.biff;

import jxl.biff.IntegerHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class NineteenFourRecord extends WritableRecordData {
    private byte[] data;
    private boolean nineteenFourDate;

    public NineteenFourRecord(boolean oldDate) {
        super(Type.NINETEENFOUR);
        this.nineteenFourDate = oldDate;
        byte[] bArr = new byte[2];
        this.data = bArr;
        if (oldDate) {
            IntegerHelper.getTwoBytes(1, bArr, 0);
        }
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        return this.data;
    }
}
