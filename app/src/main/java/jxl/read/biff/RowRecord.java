package jxl.read.biff;

import jxl.biff.IntegerHelper;
import jxl.biff.RecordData;
import jxl.common.Logger;

/* loaded from: classes.dex */
public class RowRecord extends RecordData {
    private static final int defaultHeightIndicator = 255;
    private static Logger logger = Logger.getLogger(RowRecord.class);
    private boolean collapsed;
    private boolean defaultFormat;
    private boolean groupStart;
    private boolean matchesDefFontHeight;
    private int outlineLevel;
    private int rowHeight;
    private int rowNumber;
    private int xfIndex;

    RowRecord(Record t) {
        super(t);
        byte[] data = getRecord().getData();
        this.rowNumber = IntegerHelper.getInt(data[0], data[1]);
        this.rowHeight = IntegerHelper.getInt(data[6], data[7]);
        int options = IntegerHelper.getInt(data[12], data[13], data[14], data[15]);
        this.outlineLevel = options & 7;
        this.groupStart = (options & 16) != 0;
        this.collapsed = (options & 32) != 0;
        this.matchesDefFontHeight = (options & 64) == 0;
        this.defaultFormat = (options & 128) != 0;
        this.xfIndex = (268369920 & options) >> 16;
    }

    boolean isDefaultHeight() {
        return this.rowHeight == 255;
    }

    public boolean matchesDefaultFontHeight() {
        return this.matchesDefFontHeight;
    }

    public int getRowNumber() {
        return this.rowNumber;
    }

    public int getOutlineLevel() {
        return this.outlineLevel;
    }

    public boolean getGroupStart() {
        return this.groupStart;
    }

    public int getRowHeight() {
        return this.rowHeight;
    }

    public boolean isCollapsed() {
        return this.collapsed;
    }

    public int getXFIndex() {
        return this.xfIndex;
    }

    public boolean hasDefaultFormat() {
        return this.defaultFormat;
    }
}
