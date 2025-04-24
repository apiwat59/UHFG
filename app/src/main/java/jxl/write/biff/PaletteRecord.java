package jxl.write.biff;

import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class PaletteRecord extends WritableRecordData {
    private byte[] data;

    public PaletteRecord(jxl.read.biff.PaletteRecord p) {
        super(Type.PALETTE);
        this.data = p.getData();
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        return this.data;
    }
}
