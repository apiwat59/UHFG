package jxl.biff;

import jxl.common.Logger;
import jxl.read.biff.Record;

/* loaded from: classes.dex */
public class ConditionalFormatRangeRecord extends WritableRecordData {
    private static Logger logger = Logger.getLogger(ConditionalFormatRangeRecord.class);
    private byte[] data;
    private Range enclosingRange;
    private boolean initialized;
    private boolean modified;
    private int numRanges;
    private Range[] ranges;

    private static class Range {
        public int firstColumn;
        public int firstRow;
        public int lastColumn;
        public int lastRow;
        public boolean modified = false;

        public void insertColumn(int col) {
            int i = this.lastColumn;
            if (col > i) {
                return;
            }
            int i2 = this.firstColumn;
            if (col <= i2) {
                this.firstColumn = i2 + 1;
                this.modified = true;
            }
            if (col <= i) {
                this.lastColumn = i + 1;
                this.modified = true;
            }
        }

        public void removeColumn(int col) {
            int i = this.lastColumn;
            if (col > i) {
                return;
            }
            int i2 = this.firstColumn;
            if (col < i2) {
                this.firstColumn = i2 - 1;
                this.modified = true;
            }
            if (col <= i) {
                this.lastColumn = i - 1;
                this.modified = true;
            }
        }

        public void removeRow(int row) {
            int i = this.lastRow;
            if (row > i) {
                return;
            }
            int i2 = this.firstRow;
            if (row < i2) {
                this.firstRow = i2 - 1;
                this.modified = true;
            }
            if (row <= i) {
                this.lastRow = i - 1;
                this.modified = true;
            }
        }

        public void insertRow(int row) {
            int i = this.lastRow;
            if (row > i) {
                return;
            }
            int i2 = this.firstRow;
            if (row <= i2) {
                this.firstRow = i2 + 1;
                this.modified = true;
            }
            if (row <= i) {
                this.lastRow = i + 1;
                this.modified = true;
            }
        }
    }

    public ConditionalFormatRangeRecord(Record t) {
        super(t);
        this.initialized = false;
        this.modified = false;
        this.data = getRecord().getData();
    }

    private void initialize() {
        Range range = new Range();
        this.enclosingRange = range;
        byte[] bArr = this.data;
        range.firstRow = IntegerHelper.getInt(bArr[4], bArr[5]);
        Range range2 = this.enclosingRange;
        byte[] bArr2 = this.data;
        range2.lastRow = IntegerHelper.getInt(bArr2[6], bArr2[7]);
        Range range3 = this.enclosingRange;
        byte[] bArr3 = this.data;
        range3.firstColumn = IntegerHelper.getInt(bArr3[8], bArr3[9]);
        Range range4 = this.enclosingRange;
        byte[] bArr4 = this.data;
        range4.lastColumn = IntegerHelper.getInt(bArr4[10], bArr4[11]);
        byte[] bArr5 = this.data;
        int i = IntegerHelper.getInt(bArr5[12], bArr5[13]);
        this.numRanges = i;
        this.ranges = new Range[i];
        int pos = 14;
        for (int i2 = 0; i2 < this.numRanges; i2++) {
            this.ranges[i2] = new Range();
            Range range5 = this.ranges[i2];
            byte[] bArr6 = this.data;
            range5.firstRow = IntegerHelper.getInt(bArr6[pos], bArr6[pos + 1]);
            Range range6 = this.ranges[i2];
            byte[] bArr7 = this.data;
            range6.lastRow = IntegerHelper.getInt(bArr7[pos + 2], bArr7[pos + 3]);
            Range range7 = this.ranges[i2];
            byte[] bArr8 = this.data;
            range7.firstColumn = IntegerHelper.getInt(bArr8[pos + 4], bArr8[pos + 5]);
            Range range8 = this.ranges[i2];
            byte[] bArr9 = this.data;
            range8.lastColumn = IntegerHelper.getInt(bArr9[pos + 6], bArr9[pos + 7]);
            pos += 8;
        }
        this.initialized = true;
    }

    public void insertColumn(int col) {
        if (!this.initialized) {
            initialize();
        }
        this.enclosingRange.insertColumn(col);
        if (this.enclosingRange.modified) {
            this.modified = true;
        }
        int i = 0;
        while (true) {
            Range[] rangeArr = this.ranges;
            if (i < rangeArr.length) {
                rangeArr[i].insertColumn(col);
                if (this.ranges[i].modified) {
                    this.modified = true;
                }
                i++;
            } else {
                return;
            }
        }
    }

    public void removeColumn(int col) {
        if (!this.initialized) {
            initialize();
        }
        this.enclosingRange.removeColumn(col);
        if (this.enclosingRange.modified) {
            this.modified = true;
        }
        int i = 0;
        while (true) {
            Range[] rangeArr = this.ranges;
            if (i < rangeArr.length) {
                rangeArr[i].removeColumn(col);
                if (this.ranges[i].modified) {
                    this.modified = true;
                }
                i++;
            } else {
                return;
            }
        }
    }

    public void removeRow(int row) {
        if (!this.initialized) {
            initialize();
        }
        this.enclosingRange.removeRow(row);
        if (this.enclosingRange.modified) {
            this.modified = true;
        }
        int i = 0;
        while (true) {
            Range[] rangeArr = this.ranges;
            if (i < rangeArr.length) {
                rangeArr[i].removeRow(row);
                if (this.ranges[i].modified) {
                    this.modified = true;
                }
                i++;
            } else {
                return;
            }
        }
    }

    public void insertRow(int row) {
        if (!this.initialized) {
            initialize();
        }
        this.enclosingRange.insertRow(row);
        if (this.enclosingRange.modified) {
            this.modified = true;
        }
        int i = 0;
        while (true) {
            Range[] rangeArr = this.ranges;
            if (i < rangeArr.length) {
                rangeArr[i].insertRow(row);
                if (this.ranges[i].modified) {
                    this.modified = true;
                }
                i++;
            } else {
                return;
            }
        }
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        if (!this.modified) {
            return this.data;
        }
        byte[] d = new byte[(this.ranges.length * 8) + 14];
        System.arraycopy(this.data, 0, d, 0, 4);
        IntegerHelper.getTwoBytes(this.enclosingRange.firstRow, d, 4);
        IntegerHelper.getTwoBytes(this.enclosingRange.lastRow, d, 6);
        IntegerHelper.getTwoBytes(this.enclosingRange.firstColumn, d, 8);
        IntegerHelper.getTwoBytes(this.enclosingRange.lastColumn, d, 10);
        IntegerHelper.getTwoBytes(this.numRanges, d, 12);
        int pos = 14;
        int i = 0;
        while (true) {
            Range[] rangeArr = this.ranges;
            if (i < rangeArr.length) {
                IntegerHelper.getTwoBytes(rangeArr[i].firstRow, d, pos);
                IntegerHelper.getTwoBytes(this.ranges[i].lastRow, d, pos + 2);
                IntegerHelper.getTwoBytes(this.ranges[i].firstColumn, d, pos + 4);
                IntegerHelper.getTwoBytes(this.ranges[i].lastColumn, d, pos + 6);
                pos += 8;
                i++;
            } else {
                return d;
            }
        }
    }
}
