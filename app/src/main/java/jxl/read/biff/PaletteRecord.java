package jxl.read.biff;

import jxl.biff.RecordData;

/* loaded from: classes.dex */
public class PaletteRecord extends RecordData {
    PaletteRecord(Record t) {
        super(t);
    }

    public byte[] getData() {
        return getRecord().getData();
    }
}
