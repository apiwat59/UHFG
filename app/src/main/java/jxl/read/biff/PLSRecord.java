package jxl.read.biff;

import jxl.biff.RecordData;

/* loaded from: classes.dex */
public class PLSRecord extends RecordData {
    public PLSRecord(Record r) {
        super(r);
    }

    public byte[] getData() {
        return getRecord().getData();
    }
}
