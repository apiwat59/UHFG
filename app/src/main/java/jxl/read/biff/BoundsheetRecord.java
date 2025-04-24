package jxl.read.biff;

import jxl.WorkbookSettings;
import jxl.biff.IntegerHelper;
import jxl.biff.RecordData;
import jxl.biff.StringHelper;

/* loaded from: classes.dex */
class BoundsheetRecord extends RecordData {
    public static Biff7 biff7 = new Biff7();
    private int length;
    private String name;
    private int offset;
    private byte typeFlag;
    private byte visibilityFlag;

    /* JADX INFO: Access modifiers changed from: private */
    static class Biff7 {
        private Biff7() {
        }
    }

    public BoundsheetRecord(Record t, WorkbookSettings s) {
        super(t);
        byte[] data = getRecord().getData();
        this.offset = IntegerHelper.getInt(data[0], data[1], data[2], data[3]);
        this.typeFlag = data[5];
        this.visibilityFlag = data[4];
        int i = data[6];
        this.length = i;
        if (data[7] == 0) {
            byte[] bytes = new byte[i];
            System.arraycopy(data, 8, bytes, 0, i);
            this.name = StringHelper.getString(bytes, this.length, 0, s);
        } else {
            byte[] bytes2 = new byte[i * 2];
            System.arraycopy(data, 8, bytes2, 0, i * 2);
            this.name = StringHelper.getUnicodeString(bytes2, this.length, 0);
        }
    }

    public BoundsheetRecord(Record t, Biff7 biff72) {
        super(t);
        byte[] data = getRecord().getData();
        this.offset = IntegerHelper.getInt(data[0], data[1], data[2], data[3]);
        this.typeFlag = data[5];
        this.visibilityFlag = data[4];
        int i = data[6];
        this.length = i;
        byte[] bytes = new byte[i];
        System.arraycopy(data, 7, bytes, 0, i);
        this.name = new String(bytes);
    }

    public String getName() {
        return this.name;
    }

    public boolean isHidden() {
        return this.visibilityFlag != 0;
    }

    public boolean isSheet() {
        return this.typeFlag == 0;
    }

    public boolean isChart() {
        return this.typeFlag == 2;
    }
}
