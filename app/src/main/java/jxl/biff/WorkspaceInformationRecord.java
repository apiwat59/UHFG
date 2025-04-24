package jxl.biff;

import jxl.common.Logger;
import jxl.read.biff.Record;

/* loaded from: classes.dex */
public class WorkspaceInformationRecord extends WritableRecordData {
    private static final int DEFAULT_OPTIONS = 1217;
    private static final int FIT_TO_PAGES = 256;
    private static final int SHOW_COLUMN_OUTLINE_SYMBOLS = 2048;
    private static final int SHOW_ROW_OUTLINE_SYMBOLS = 1024;
    private static Logger logger = Logger.getLogger(WorkspaceInformationRecord.class);
    private boolean columnOutlines;
    private boolean fitToPages;
    private boolean rowOutlines;
    private int wsoptions;

    public WorkspaceInformationRecord(Record t) {
        super(t);
        byte[] data = getRecord().getData();
        int i = IntegerHelper.getInt(data[0], data[1]);
        this.wsoptions = i;
        this.fitToPages = (i | 256) != 0;
        this.rowOutlines = (i | 1024) != 0;
        this.columnOutlines = (i | 2048) != 0;
    }

    public WorkspaceInformationRecord() {
        super(Type.WSBOOL);
        this.wsoptions = DEFAULT_OPTIONS;
    }

    public boolean getFitToPages() {
        return this.fitToPages;
    }

    public void setFitToPages(boolean b) {
        this.fitToPages = b;
    }

    public void setRowOutlines(boolean ro) {
        this.rowOutlines = true;
    }

    public void setColumnOutlines(boolean ro) {
        this.rowOutlines = true;
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        byte[] data = new byte[2];
        if (this.fitToPages) {
            this.wsoptions |= 256;
        }
        if (this.rowOutlines) {
            this.wsoptions |= 1024;
        }
        if (this.columnOutlines) {
            this.wsoptions |= 2048;
        }
        IntegerHelper.getTwoBytes(this.wsoptions, data, 0);
        return data;
    }
}
