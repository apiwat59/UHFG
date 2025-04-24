package jxl.write.biff;

import jxl.biff.FormattingRecords;
import jxl.biff.IndexMapping;
import jxl.biff.IntegerHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;
import jxl.biff.XFRecord;

/* loaded from: classes.dex */
class ColumnInfoRecord extends WritableRecordData {
    private boolean collapsed;
    private int column;
    private byte[] data;
    private boolean hidden;
    private int outlineLevel;
    private XFRecord style;
    private int width;
    private int xfIndex;

    public ColumnInfoRecord(int col, int w, XFRecord xf) {
        super(Type.COLINFO);
        this.column = col;
        this.width = w;
        this.style = xf;
        this.xfIndex = xf.getXFIndex();
        this.hidden = false;
    }

    public ColumnInfoRecord(ColumnInfoRecord cir) {
        super(Type.COLINFO);
        this.column = cir.column;
        this.width = cir.width;
        this.style = cir.style;
        this.xfIndex = cir.xfIndex;
        this.hidden = cir.hidden;
        this.outlineLevel = cir.outlineLevel;
        this.collapsed = cir.collapsed;
    }

    public ColumnInfoRecord(jxl.read.biff.ColumnInfoRecord cir, int col, FormattingRecords fr) {
        super(Type.COLINFO);
        this.column = col;
        this.width = cir.getWidth();
        int xFIndex = cir.getXFIndex();
        this.xfIndex = xFIndex;
        this.style = fr.getXFRecord(xFIndex);
        this.outlineLevel = cir.getOutlineLevel();
        this.collapsed = cir.getCollapsed();
    }

    public ColumnInfoRecord(jxl.read.biff.ColumnInfoRecord cir, int col) {
        super(Type.COLINFO);
        this.column = col;
        this.width = cir.getWidth();
        this.xfIndex = cir.getXFIndex();
        this.outlineLevel = cir.getOutlineLevel();
        this.collapsed = cir.getCollapsed();
    }

    public int getColumn() {
        return this.column;
    }

    public void incrementColumn() {
        this.column++;
    }

    public void decrementColumn() {
        this.column--;
    }

    int getWidth() {
        return this.width;
    }

    void setWidth(int w) {
        this.width = w;
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        byte[] bArr = new byte[12];
        this.data = bArr;
        IntegerHelper.getTwoBytes(this.column, bArr, 0);
        IntegerHelper.getTwoBytes(this.column, this.data, 2);
        IntegerHelper.getTwoBytes(this.width, this.data, 4);
        IntegerHelper.getTwoBytes(this.xfIndex, this.data, 6);
        int options = (this.outlineLevel << 8) | 6;
        if (this.hidden) {
            options |= 1;
        }
        this.outlineLevel = (options & 1792) / 256;
        if (this.collapsed) {
            options |= 4096;
        }
        IntegerHelper.getTwoBytes(options, this.data, 8);
        return this.data;
    }

    public XFRecord getCellFormat() {
        return this.style;
    }

    public void setCellFormat(XFRecord xfr) {
        this.style = xfr;
    }

    public int getXfIndex() {
        return this.xfIndex;
    }

    void rationalize(IndexMapping xfmapping) {
        this.xfIndex = xfmapping.getNewIndex(this.xfIndex);
    }

    void setHidden(boolean h) {
        this.hidden = h;
    }

    boolean getHidden() {
        return this.hidden;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ColumnInfoRecord)) {
            return false;
        }
        ColumnInfoRecord cir = (ColumnInfoRecord) o;
        if (this.column != cir.column || this.xfIndex != cir.xfIndex || this.width != cir.width || this.hidden != cir.hidden || this.outlineLevel != cir.outlineLevel || this.collapsed != cir.collapsed) {
            return false;
        }
        XFRecord xFRecord = this.style;
        if ((xFRecord != null || cir.style == null) && (xFRecord == null || cir.style != null)) {
            return xFRecord.equals(cir.style);
        }
        return false;
    }

    public int hashCode() {
        int i = (((((((137 * 79) + this.column) * 79) + this.xfIndex) * 79) + this.width) * 79) + (this.hidden ? 1 : 0);
        XFRecord xFRecord = this.style;
        if (xFRecord != null) {
            return i ^ xFRecord.hashCode();
        }
        return i;
    }

    public int getOutlineLevel() {
        return this.outlineLevel;
    }

    public boolean getCollapsed() {
        return this.collapsed;
    }

    public void incrementOutlineLevel() {
        this.outlineLevel++;
    }

    public void decrementOutlineLevel() {
        int i = this.outlineLevel;
        if (i > 0) {
            this.outlineLevel = i - 1;
        }
        if (this.outlineLevel == 0) {
            this.collapsed = false;
        }
    }

    public void setOutlineLevel(int level) {
        this.outlineLevel = level;
    }

    public void setCollapsed(boolean value) {
        this.collapsed = value;
    }
}
