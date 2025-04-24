package jxl.write.biff;

import jxl.biff.IntegerHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class PaneRecord extends WritableRecordData {
    private static final int bottomLeftPane = 2;
    private static final int bottomRightPane = 0;
    private static final int topLeftPane = 3;
    private static final int topRightPane = 1;
    private int columnsVisible;
    private int rowsVisible;

    public PaneRecord(int cols, int rows) {
        super(Type.PANE);
        this.rowsVisible = rows;
        this.columnsVisible = cols;
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        byte[] data = new byte[10];
        IntegerHelper.getTwoBytes(this.columnsVisible, data, 0);
        IntegerHelper.getTwoBytes(this.rowsVisible, data, 2);
        int i = this.rowsVisible;
        if (i > 0) {
            IntegerHelper.getTwoBytes(i, data, 4);
        }
        int i2 = this.columnsVisible;
        if (i2 > 0) {
            IntegerHelper.getTwoBytes(i2, data, 6);
        }
        int activePane = 3;
        int i3 = this.rowsVisible;
        if (i3 > 0 && this.columnsVisible == 0) {
            activePane = 2;
        } else if (i3 == 0 && this.columnsVisible > 0) {
            activePane = 1;
        } else if (i3 > 0 && this.columnsVisible > 0) {
            activePane = 0;
        }
        IntegerHelper.getTwoBytes(activePane, data, 8);
        return data;
    }
}
