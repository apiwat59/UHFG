package jxl.write.biff;

import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class InterfaceHeaderRecord extends WritableRecordData {
    public InterfaceHeaderRecord() {
        super(Type.INTERFACEHDR);
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        byte[] data = {-80, 4};
        return data;
    }
}
