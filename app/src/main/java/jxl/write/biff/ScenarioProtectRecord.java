package jxl.write.biff;

import jxl.biff.IntegerHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class ScenarioProtectRecord extends WritableRecordData {
    private byte[] data;
    private boolean protection;

    public ScenarioProtectRecord(boolean prot) {
        super(Type.SCENPROTECT);
        this.protection = prot;
        byte[] bArr = new byte[2];
        this.data = bArr;
        if (prot) {
            IntegerHelper.getTwoBytes(1, bArr, 0);
        }
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        return this.data;
    }
}
