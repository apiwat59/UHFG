package jxl.read.biff;

import jxl.biff.RecordData;

/* loaded from: classes.dex */
class NineteenFourRecord extends RecordData {
    private boolean nineteenFour;

    public NineteenFourRecord(Record t) {
        super(t);
        byte[] data = getRecord().getData();
        this.nineteenFour = data[0] == 1;
    }

    public boolean is1904() {
        return this.nineteenFour;
    }
}
