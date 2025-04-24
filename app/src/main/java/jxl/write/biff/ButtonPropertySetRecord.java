package jxl.write.biff;

import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class ButtonPropertySetRecord extends WritableRecordData {
    private byte[] data;

    public ButtonPropertySetRecord(jxl.read.biff.ButtonPropertySetRecord bps) {
        super(Type.BUTTONPROPERTYSET);
        this.data = bps.getData();
    }

    public ButtonPropertySetRecord(ButtonPropertySetRecord bps) {
        super(Type.BUTTONPROPERTYSET);
        this.data = bps.getData();
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        return this.data;
    }
}
