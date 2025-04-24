package jxl.read.biff;

import jxl.biff.IntegerHelper;
import jxl.biff.RecordData;

/* loaded from: classes.dex */
class ProtectRecord extends RecordData {
    private boolean prot;

    ProtectRecord(Record t) {
        super(t);
        byte[] data = getRecord().getData();
        int protflag = IntegerHelper.getInt(data[0], data[1]);
        this.prot = protflag == 1;
    }

    boolean isProtected() {
        return this.prot;
    }
}
