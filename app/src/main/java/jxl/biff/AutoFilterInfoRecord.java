package jxl.biff;

import jxl.common.Logger;
import jxl.read.biff.Record;

/* loaded from: classes.dex */
public class AutoFilterInfoRecord extends WritableRecordData {
    private static Logger logger = Logger.getLogger(AutoFilterInfoRecord.class);
    private byte[] data;

    public AutoFilterInfoRecord(Record t) {
        super(t);
        this.data = getRecord().getData();
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        return this.data;
    }
}
