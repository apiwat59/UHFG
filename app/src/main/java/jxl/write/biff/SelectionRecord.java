package jxl.write.biff;

import jxl.biff.IntegerHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class SelectionRecord extends WritableRecordData {
    private int column;
    private PaneType pane;
    private int row;
    public static final PaneType lowerRight = new PaneType(0);
    public static final PaneType upperRight = new PaneType(1);
    public static final PaneType lowerLeft = new PaneType(2);
    public static final PaneType upperLeft = new PaneType(3);

    /* JADX INFO: Access modifiers changed from: private */
    static class PaneType {
        int val;

        PaneType(int v) {
            this.val = v;
        }
    }

    public SelectionRecord(PaneType pt, int col, int r) {
        super(Type.SELECTION);
        this.column = col;
        this.row = r;
        this.pane = pt;
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        byte[] data = new byte[15];
        data[0] = (byte) this.pane.val;
        IntegerHelper.getTwoBytes(this.row, data, 1);
        IntegerHelper.getTwoBytes(this.column, data, 3);
        data[7] = 1;
        IntegerHelper.getTwoBytes(this.row, data, 9);
        IntegerHelper.getTwoBytes(this.row, data, 11);
        int i = this.column;
        data[13] = (byte) i;
        data[14] = (byte) i;
        return data;
    }
}
