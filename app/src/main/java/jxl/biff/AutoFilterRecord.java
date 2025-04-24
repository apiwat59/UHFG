package jxl.biff;

import jxl.common.Logger;
import jxl.read.biff.Record;

/* loaded from: classes.dex */
public class AutoFilterRecord extends WritableRecordData {
    private static Logger logger = Logger.getLogger(AutoFilterRecord.class);
    private byte[] data;

    public AutoFilterRecord(Record t) {
        super(t);
        this.data = getRecord().getData();
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        return this.data;
    }
}
