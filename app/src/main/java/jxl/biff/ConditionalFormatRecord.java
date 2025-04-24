package jxl.biff;

import jxl.common.Logger;
import jxl.read.biff.Record;

/* loaded from: classes.dex */
public class ConditionalFormatRecord extends WritableRecordData {
    private static Logger logger = Logger.getLogger(ConditionalFormatRecord.class);
    private byte[] data;

    public ConditionalFormatRecord(Record t) {
        super(t);
        this.data = getRecord().getData();
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        return this.data;
    }
}
