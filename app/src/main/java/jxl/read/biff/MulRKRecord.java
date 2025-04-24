package jxl.read.biff;

import jxl.biff.IntegerHelper;
import jxl.biff.RecordData;
import jxl.common.Logger;

/* loaded from: classes.dex */
class MulRKRecord extends RecordData {
    private static Logger logger = Logger.getLogger(MulRKRecord.class);
    private int colFirst;
    private int colLast;
    private int numrks;
    private int[] rknumbers;
    private int row;
    private int[] xfIndices;

    public MulRKRecord(Record t) {
        super(t);
        byte[] data = getRecord().getData();
        int length = getRecord().getLength();
        this.row = IntegerHelper.getInt(data[0], data[1]);
        this.colFirst = IntegerHelper.getInt(data[2], data[3]);
        int i = IntegerHelper.getInt(data[length - 2], data[length - 1]);
        this.colLast = i;
        int i2 = (i - this.colFirst) + 1;
        this.numrks = i2;
        this.rknumbers = new int[i2];
        this.xfIndices = new int[i2];
        readRks(data);
    }

    private void readRks(byte[] data) {
        int pos = 4;
        for (int i = 0; i < this.numrks; i++) {
            this.xfIndices[i] = IntegerHelper.getInt(data[pos], data[pos + 1]);
            int rk = IntegerHelper.getInt(data[pos + 2], data[pos + 3], data[pos + 4], data[pos + 5]);
            this.rknumbers[i] = rk;
            pos += 6;
        }
    }

    public int getRow() {
        return this.row;
    }

    public int getFirstColumn() {
        return this.colFirst;
    }

    public int getNumberOfColumns() {
        return this.numrks;
    }

    public int getRKNumber(int index) {
        return this.rknumbers[index];
    }

    public int getXFIndex(int index) {
        return this.xfIndices[index];
    }
}
