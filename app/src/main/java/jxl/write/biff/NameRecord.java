package jxl.write.biff;

import jxl.biff.BuiltInName;
import jxl.biff.IntegerHelper;
import jxl.biff.StringHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;
import jxl.common.Logger;
import jxl.read.biff.NameRecord;

/* loaded from: classes.dex */
class NameRecord extends WritableRecordData {
    private static final int areaReference = 59;
    private static final int cellReference = 58;
    private static final int subExpression = 41;
    private static final int union = 16;
    private BuiltInName builtInName;
    private byte[] data;
    private int index;
    private boolean modified;
    private String name;
    private NameRange[] ranges;
    private int sheetRef;
    private static Logger logger = Logger.getLogger(NameRecord.class);
    private static final NameRange EMPTY_RANGE = new NameRange(0, 0, 0, 0, 0);

    static class NameRange {
        private int columnFirst;
        private int columnLast;
        private int externalSheet;
        private int rowFirst;
        private int rowLast;

        NameRange(NameRecord.NameRange nr) {
            this.columnFirst = nr.getFirstColumn();
            this.rowFirst = nr.getFirstRow();
            this.columnLast = nr.getLastColumn();
            this.rowLast = nr.getLastRow();
            this.externalSheet = nr.getExternalSheet();
        }

        NameRange(int extSheet, int theStartRow, int theEndRow, int theStartCol, int theEndCol) {
            this.columnFirst = theStartCol;
            this.rowFirst = theStartRow;
            this.columnLast = theEndCol;
            this.rowLast = theEndRow;
            this.externalSheet = extSheet;
        }

        int getFirstColumn() {
            return this.columnFirst;
        }

        int getFirstRow() {
            return this.rowFirst;
        }

        int getLastColumn() {
            return this.columnLast;
        }

        int getLastRow() {
            return this.rowLast;
        }

        int getExternalSheet() {
            return this.externalSheet;
        }

        void incrementFirstRow() {
            this.rowFirst++;
        }

        void incrementLastRow() {
            this.rowLast++;
        }

        void decrementFirstRow() {
            this.rowFirst--;
        }

        void decrementLastRow() {
            this.rowLast--;
        }

        void incrementFirstColumn() {
            this.columnFirst++;
        }

        void incrementLastColumn() {
            this.columnLast++;
        }

        void decrementFirstColumn() {
            this.columnFirst--;
        }

        void decrementLastColumn() {
            this.columnLast--;
        }

        byte[] getData() {
            byte[] d = new byte[10];
            IntegerHelper.getTwoBytes(this.externalSheet, d, 0);
            IntegerHelper.getTwoBytes(this.rowFirst, d, 2);
            IntegerHelper.getTwoBytes(this.rowLast, d, 4);
            IntegerHelper.getTwoBytes(this.columnFirst & 255, d, 6);
            IntegerHelper.getTwoBytes(this.columnLast & 255, d, 8);
            return d;
        }
    }

    public NameRecord(jxl.read.biff.NameRecord sr, int ind) {
        super(Type.NAME);
        this.sheetRef = 0;
        this.data = sr.getData();
        this.name = sr.getName();
        this.sheetRef = sr.getSheetRef();
        this.index = ind;
        this.modified = false;
        NameRecord.NameRange[] r = sr.getRanges();
        this.ranges = new NameRange[r.length];
        int i = 0;
        while (true) {
            NameRange[] nameRangeArr = this.ranges;
            if (i < nameRangeArr.length) {
                nameRangeArr[i] = new NameRange(r[i]);
                i++;
            } else {
                return;
            }
        }
    }

    NameRecord(String theName, int theIndex, int extSheet, int theStartRow, int theEndRow, int theStartCol, int theEndCol, boolean global) {
        super(Type.NAME);
        this.sheetRef = 0;
        this.name = theName;
        this.index = theIndex;
        this.sheetRef = global ? 0 : theIndex + 1;
        this.ranges = new NameRange[]{new NameRange(extSheet, theStartRow, theEndRow, theStartCol, theEndCol)};
        this.modified = true;
    }

    NameRecord(BuiltInName theName, int theIndex, int extSheet, int theStartRow, int theEndRow, int theStartCol, int theEndCol, boolean global) {
        super(Type.NAME);
        this.sheetRef = 0;
        this.builtInName = theName;
        this.index = theIndex;
        this.sheetRef = global ? 0 : theIndex + 1;
        this.ranges = new NameRange[]{new NameRange(extSheet, theStartRow, theEndRow, theStartCol, theEndCol)};
    }

    NameRecord(BuiltInName theName, int theIndex, int extSheet, int theStartRow, int theEndRow, int theStartCol, int theEndCol, int theStartRow2, int theEndRow2, int theStartCol2, int theEndCol2, boolean global) {
        super(Type.NAME);
        this.sheetRef = 0;
        this.builtInName = theName;
        this.index = theIndex;
        this.sheetRef = global ? 0 : theIndex + 1;
        NameRange[] nameRangeArr = new NameRange[2];
        this.ranges = nameRangeArr;
        nameRangeArr[0] = new NameRange(extSheet, theStartRow, theEndRow, theStartCol, theEndCol);
        this.ranges[1] = new NameRange(extSheet, theStartRow2, theEndRow2, theStartCol2, theEndCol2);
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        int detailLength;
        byte[] bArr = this.data;
        if (bArr != null && !this.modified) {
            return bArr;
        }
        NameRange[] nameRangeArr = this.ranges;
        if (nameRangeArr.length > 1) {
            detailLength = (nameRangeArr.length * 11) + 4;
        } else {
            detailLength = 11;
        }
        int length = detailLength + 15;
        byte[] bArr2 = new byte[length + (this.builtInName != null ? 1 : this.name.length())];
        this.data = bArr2;
        int options = 0;
        if (this.builtInName != null) {
            options = 0 | 32;
        }
        IntegerHelper.getTwoBytes(options, bArr2, 0);
        byte[] bArr3 = this.data;
        bArr3[2] = 0;
        if (this.builtInName != null) {
            bArr3[3] = 1;
        } else {
            bArr3[3] = (byte) this.name.length();
        }
        IntegerHelper.getTwoBytes(detailLength, this.data, 4);
        IntegerHelper.getTwoBytes(this.sheetRef, this.data, 6);
        IntegerHelper.getTwoBytes(this.sheetRef, this.data, 8);
        BuiltInName builtInName = this.builtInName;
        if (builtInName != null) {
            this.data[15] = (byte) builtInName.getValue();
        } else {
            StringHelper.getBytes(this.name, this.data, 15);
        }
        int pos = this.builtInName != null ? 16 : this.name.length() + 15;
        NameRange[] nameRangeArr2 = this.ranges;
        if (nameRangeArr2.length > 1) {
            byte[] bArr4 = this.data;
            int pos2 = pos + 1;
            bArr4[pos] = 41;
            IntegerHelper.getTwoBytes(detailLength - 3, bArr4, pos2);
            int pos3 = pos2 + 2;
            int i = 0;
            while (true) {
                NameRange[] nameRangeArr3 = this.ranges;
                if (i >= nameRangeArr3.length) {
                    break;
                }
                int pos4 = pos3 + 1;
                this.data[pos3] = 59;
                byte[] rd = nameRangeArr3[i].getData();
                System.arraycopy(rd, 0, this.data, pos4, rd.length);
                pos3 = rd.length + pos4;
                i++;
            }
            this.data[pos3] = 16;
        } else {
            this.data[pos] = 59;
            byte[] rd2 = nameRangeArr2[0].getData();
            System.arraycopy(rd2, 0, this.data, pos + 1, rd2.length);
        }
        return this.data;
    }

    public String getName() {
        return this.name;
    }

    public int getIndex() {
        return this.index;
    }

    public int getSheetRef() {
        return this.sheetRef;
    }

    public void setSheetRef(int i) {
        this.sheetRef = i;
        IntegerHelper.getTwoBytes(i, this.data, 8);
    }

    public NameRange[] getRanges() {
        return this.ranges;
    }

    void rowInserted(int sheetIndex, int row) {
        int i = 0;
        while (true) {
            NameRange[] nameRangeArr = this.ranges;
            if (i < nameRangeArr.length) {
                if (sheetIndex == nameRangeArr[i].getExternalSheet()) {
                    if (row <= this.ranges[i].getFirstRow()) {
                        this.ranges[i].incrementFirstRow();
                        this.modified = true;
                    }
                    if (row <= this.ranges[i].getLastRow()) {
                        this.ranges[i].incrementLastRow();
                        this.modified = true;
                    }
                }
                i++;
            } else {
                return;
            }
        }
    }

    boolean rowRemoved(int sheetIndex, int row) {
        NameRange[] nameRangeArr;
        int i = 0;
        while (true) {
            NameRange[] nameRangeArr2 = this.ranges;
            if (i >= nameRangeArr2.length) {
                break;
            }
            if (sheetIndex == nameRangeArr2[i].getExternalSheet()) {
                if (row == this.ranges[i].getFirstRow() && row == this.ranges[i].getLastRow()) {
                    this.ranges[i] = EMPTY_RANGE;
                }
                if (row < this.ranges[i].getFirstRow() && row > 0) {
                    this.ranges[i].decrementFirstRow();
                    this.modified = true;
                }
                if (row <= this.ranges[i].getLastRow()) {
                    this.ranges[i].decrementLastRow();
                    this.modified = true;
                }
            }
            i++;
        }
        int emptyRanges = 0;
        int i2 = 0;
        while (true) {
            nameRangeArr = this.ranges;
            if (i2 >= nameRangeArr.length) {
                break;
            }
            if (nameRangeArr[i2] == EMPTY_RANGE) {
                emptyRanges++;
            }
            i2++;
        }
        int i3 = nameRangeArr.length;
        if (emptyRanges == i3) {
            return true;
        }
        NameRange[] newRanges = new NameRange[nameRangeArr.length - emptyRanges];
        int i4 = 0;
        while (true) {
            NameRange[] nameRangeArr3 = this.ranges;
            if (i4 < nameRangeArr3.length) {
                if (nameRangeArr3[i4] != EMPTY_RANGE) {
                    newRanges[i4] = nameRangeArr3[i4];
                }
                i4++;
            } else {
                this.ranges = newRanges;
                return false;
            }
        }
    }

    boolean columnRemoved(int sheetIndex, int col) {
        NameRange[] nameRangeArr;
        int i = 0;
        while (true) {
            NameRange[] nameRangeArr2 = this.ranges;
            if (i >= nameRangeArr2.length) {
                break;
            }
            if (sheetIndex == nameRangeArr2[i].getExternalSheet()) {
                if (col == this.ranges[i].getFirstColumn() && col == this.ranges[i].getLastColumn()) {
                    this.ranges[i] = EMPTY_RANGE;
                }
                if (col < this.ranges[i].getFirstColumn() && col > 0) {
                    this.ranges[i].decrementFirstColumn();
                    this.modified = true;
                }
                if (col <= this.ranges[i].getLastColumn()) {
                    this.ranges[i].decrementLastColumn();
                    this.modified = true;
                }
            }
            i++;
        }
        int emptyRanges = 0;
        int i2 = 0;
        while (true) {
            nameRangeArr = this.ranges;
            if (i2 >= nameRangeArr.length) {
                break;
            }
            if (nameRangeArr[i2] == EMPTY_RANGE) {
                emptyRanges++;
            }
            i2++;
        }
        int i3 = nameRangeArr.length;
        if (emptyRanges == i3) {
            return true;
        }
        NameRange[] newRanges = new NameRange[nameRangeArr.length - emptyRanges];
        int i4 = 0;
        while (true) {
            NameRange[] nameRangeArr3 = this.ranges;
            if (i4 < nameRangeArr3.length) {
                if (nameRangeArr3[i4] != EMPTY_RANGE) {
                    newRanges[i4] = nameRangeArr3[i4];
                }
                i4++;
            } else {
                this.ranges = newRanges;
                return false;
            }
        }
    }

    void columnInserted(int sheetIndex, int col) {
        int i = 0;
        while (true) {
            NameRange[] nameRangeArr = this.ranges;
            if (i < nameRangeArr.length) {
                if (sheetIndex == nameRangeArr[i].getExternalSheet()) {
                    if (col <= this.ranges[i].getFirstColumn()) {
                        this.ranges[i].incrementFirstColumn();
                        this.modified = true;
                    }
                    if (col <= this.ranges[i].getLastColumn()) {
                        this.ranges[i].incrementLastColumn();
                        this.modified = true;
                    }
                }
                i++;
            } else {
                return;
            }
        }
    }
}
