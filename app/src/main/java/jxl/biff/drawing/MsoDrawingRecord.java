package jxl.biff.drawing;

import jxl.biff.Type;
import jxl.biff.WritableRecordData;
import jxl.common.Logger;
import jxl.read.biff.Record;

/* loaded from: classes.dex */
public class MsoDrawingRecord extends WritableRecordData {
    private static Logger logger = Logger.getLogger(MsoDrawingRecord.class);
    private byte[] data;
    private boolean first;

    public MsoDrawingRecord(Record t) {
        super(t);
        this.data = getRecord().getData();
        this.first = false;
    }

    public MsoDrawingRecord(byte[] d) {
        super(Type.MSODRAWING);
        this.data = d;
        this.first = false;
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        return this.data;
    }

    @Override // jxl.biff.RecordData
    public Record getRecord() {
        return super.getRecord();
    }

    public void setFirst() {
        this.first = true;
    }

    public boolean isFirst() {
        return this.first;
    }
}
