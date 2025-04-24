package jxl.write.biff;

import jxl.SheetSettings;
import jxl.biff.IntegerHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class Window2Record extends WritableRecordData {
    private byte[] data;

    public Window2Record(SheetSettings settings) {
        super(Type.WINDOW2);
        int options = 0 | 0;
        int options2 = (settings.getShowGridLines() ? options | 2 : options) | 4 | 0;
        int options3 = (settings.getDisplayZeroValues() ? options2 | 16 : options2) | 32 | 128;
        options3 = (settings.getHorizontalFreeze() == 0 && settings.getVerticalFreeze() == 0) ? options3 : options3 | 8 | 256;
        options3 = settings.isSelected() ? options3 | 1536 : options3;
        options3 = settings.getPageBreakPreviewMode() ? options3 | 2048 : options3;
        byte[] bArr = new byte[18];
        this.data = bArr;
        IntegerHelper.getTwoBytes(options3, bArr, 0);
        IntegerHelper.getTwoBytes(64, this.data, 6);
        IntegerHelper.getTwoBytes(settings.getPageBreakPreviewMagnification(), this.data, 10);
        IntegerHelper.getTwoBytes(settings.getNormalMagnification(), this.data, 12);
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        return this.data;
    }
}
