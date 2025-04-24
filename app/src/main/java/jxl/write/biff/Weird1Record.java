package jxl.write.biff;

import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class Weird1Record extends WritableRecordData {
    public Weird1Record() {
        super(Type.WEIRD1);
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        byte[] data = new byte[6];
        data[2] = 55;
        return data;
    }
}
