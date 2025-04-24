package jxl.write.biff;

import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class MMSRecord extends WritableRecordData {
    private byte[] data;
    private byte numMenuItemsAdded;
    private byte numMenuItemsDeleted;

    public MMSRecord(int menuItemsAdded, int menuItemsDeleted) {
        super(Type.MMS);
        byte b = (byte) menuItemsAdded;
        this.numMenuItemsAdded = b;
        byte b2 = (byte) menuItemsDeleted;
        this.numMenuItemsDeleted = b2;
        this.data = new byte[]{b, b2};
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        return this.data;
    }
}
