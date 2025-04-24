package jxl.write.biff;

import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class InterfaceEndRecord extends WritableRecordData {
    public InterfaceEndRecord() {
        super(Type.INTERFACEEND);
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        return new byte[0];
    }
}
