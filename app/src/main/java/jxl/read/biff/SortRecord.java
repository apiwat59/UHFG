package jxl.read.biff;

import jxl.biff.RecordData;
import jxl.biff.StringHelper;
import jxl.biff.Type;

/* loaded from: classes.dex */
public class SortRecord extends RecordData {
    private String col1Name;
    private int col1Size;
    private String col2Name;
    private int col2Size;
    private String col3Name;
    private int col3Size;
    private byte optionFlags;
    private boolean sortCaseSensitive;
    private boolean sortColumns;
    private boolean sortKey1Desc;
    private boolean sortKey2Desc;
    private boolean sortKey3Desc;

    public SortRecord(Record r) {
        super(Type.SORT);
        int curPos;
        this.sortColumns = false;
        this.sortKey1Desc = false;
        this.sortKey2Desc = false;
        this.sortKey3Desc = false;
        this.sortCaseSensitive = false;
        byte[] data = r.getData();
        byte b = data[0];
        this.optionFlags = b;
        this.sortColumns = (b & 1) != 0;
        this.sortKey1Desc = (b & 2) != 0;
        this.sortKey2Desc = (b & 4) != 0;
        this.sortKey3Desc = (b & 8) != 0;
        this.sortCaseSensitive = (b & 16) != 0;
        byte b2 = data[2];
        this.col1Size = b2;
        this.col2Size = data[3];
        this.col3Size = data[4];
        int curPos2 = 5 + 1;
        if (data[5] == 0) {
            this.col1Name = new String(data, curPos2, this.col1Size);
            curPos = curPos2 + this.col1Size;
        } else {
            this.col1Name = StringHelper.getUnicodeString(data, b2, curPos2);
            curPos = curPos2 + (this.col1Size * 2);
        }
        int i = this.col2Size;
        if (i > 0) {
            int curPos3 = curPos + 1;
            if (data[curPos] == 0) {
                this.col2Name = new String(data, curPos3, this.col2Size);
                curPos = curPos3 + this.col2Size;
            } else {
                this.col2Name = StringHelper.getUnicodeString(data, i, curPos3);
                curPos = curPos3 + (this.col2Size * 2);
            }
        } else {
            this.col2Name = "";
        }
        int i2 = this.col3Size;
        if (i2 > 0) {
            int curPos4 = curPos + 1;
            if (data[curPos] == 0) {
                this.col3Name = new String(data, curPos4, this.col3Size);
                int i3 = curPos4 + this.col3Size;
                return;
            } else {
                this.col3Name = StringHelper.getUnicodeString(data, i2, curPos4);
                int i4 = curPos4 + (this.col3Size * 2);
                return;
            }
        }
        this.col3Name = "";
    }

    public String getSortCol1Name() {
        return this.col1Name;
    }

    public String getSortCol2Name() {
        return this.col2Name;
    }

    public String getSortCol3Name() {
        return this.col3Name;
    }

    public boolean getSortColumns() {
        return this.sortColumns;
    }

    public boolean getSortKey1Desc() {
        return this.sortKey1Desc;
    }

    public boolean getSortKey2Desc() {
        return this.sortKey2Desc;
    }

    public boolean getSortKey3Desc() {
        return this.sortKey3Desc;
    }

    public boolean getSortCaseSensitive() {
        return this.sortCaseSensitive;
    }
}
