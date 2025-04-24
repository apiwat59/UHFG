package jxl.read.biff;

import jxl.biff.IntegerHelper;
import jxl.biff.RecordData;
import jxl.common.Logger;

/* loaded from: classes.dex */
class DimensionRecord extends RecordData {
    private int numCols;
    private int numRows;
    private static Logger logger = Logger.getLogger(DimensionRecord.class);
    public static Biff7 biff7 = new Biff7();

    /* JADX INFO: Access modifiers changed from: private */
    static class Biff7 {
        private Biff7() {
        }
    }

    public DimensionRecord(Record t) {
        super(t);
        byte[] data = t.getData();
        if (data.length == 10) {
            read10ByteData(data);
        } else {
            read14ByteData(data);
        }
    }

    public DimensionRecord(Record t, Biff7 biff72) {
        super(t);
        byte[] data = t.getData();
        read10ByteData(data);
    }

    private void read10ByteData(byte[] data) {
        this.numRows = IntegerHelper.getInt(data[2], data[3]);
        this.numCols = IntegerHelper.getInt(data[6], data[7]);
    }

    private void read14ByteData(byte[] data) {
        this.numRows = IntegerHelper.getInt(data[4], data[5], data[6], data[7]);
        this.numCols = IntegerHelper.getInt(data[10], data[11]);
    }

    public int getNumberOfRows() {
        return this.numRows;
    }

    public int getNumberOfColumns() {
        return this.numCols;
    }
}
