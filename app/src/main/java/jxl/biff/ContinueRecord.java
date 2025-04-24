package jxl.biff;

import jxl.read.biff.Record;

/* loaded from: classes.dex */
public class ContinueRecord extends WritableRecordData {
    private byte[] data;

    public ContinueRecord(Record t) {
        super(t);
        this.data = t.getData();
    }

    public ContinueRecord(byte[] d) {
        super(Type.CONTINUE);
        this.data = d;
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        return this.data;
    }

    @Override // jxl.biff.RecordData
    public Record getRecord() {
        return super.getRecord();
    }
}
