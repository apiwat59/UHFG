package jxl.write.biff;

import jxl.WorkbookSettings;
import jxl.biff.EncodedURLHelper;
import jxl.biff.IntegerHelper;
import jxl.biff.StringHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;
import jxl.common.Assert;
import jxl.common.Logger;

/* loaded from: classes.dex */
class SupbookRecord extends WritableRecordData {
    public static final SupbookType ADDIN;
    public static final SupbookType EXTERNAL;
    public static final SupbookType INTERNAL;
    public static final SupbookType LINK;
    public static final SupbookType UNKNOWN;
    private static Logger logger = Logger.getLogger(SupbookRecord.class);
    private byte[] data;
    private String fileName;
    private int numSheets;
    private String[] sheetNames;
    private SupbookType type;
    private WorkbookSettings workbookSettings;

    static {
        INTERNAL = new SupbookType();
        EXTERNAL = new SupbookType();
        ADDIN = new SupbookType();
        LINK = new SupbookType();
        UNKNOWN = new SupbookType();
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class SupbookType {
        private SupbookType() {
        }
    }

    public SupbookRecord() {
        super(Type.SUPBOOK);
        this.type = ADDIN;
    }

    public SupbookRecord(int sheets, WorkbookSettings ws) {
        super(Type.SUPBOOK);
        this.numSheets = sheets;
        this.type = INTERNAL;
        this.workbookSettings = ws;
    }

    public SupbookRecord(String fn, WorkbookSettings ws) {
        super(Type.SUPBOOK);
        this.fileName = fn;
        this.numSheets = 1;
        this.sheetNames = new String[0];
        this.workbookSettings = ws;
        this.type = EXTERNAL;
    }

    public SupbookRecord(jxl.read.biff.SupbookRecord sr, WorkbookSettings ws) {
        super(Type.SUPBOOK);
        this.workbookSettings = ws;
        if (sr.getType() == jxl.read.biff.SupbookRecord.INTERNAL) {
            this.type = INTERNAL;
            this.numSheets = sr.getNumberOfSheets();
        } else if (sr.getType() == jxl.read.biff.SupbookRecord.EXTERNAL) {
            this.type = EXTERNAL;
            this.numSheets = sr.getNumberOfSheets();
            this.fileName = sr.getFileName();
            this.sheetNames = new String[this.numSheets];
            for (int i = 0; i < this.numSheets; i++) {
                this.sheetNames[i] = sr.getSheetName(i);
            }
        }
        if (sr.getType() == jxl.read.biff.SupbookRecord.ADDIN) {
            logger.warn("Supbook type is addin");
        }
    }

    private void initInternal(jxl.read.biff.SupbookRecord sr) {
        this.numSheets = sr.getNumberOfSheets();
        initInternal();
    }

    private void initInternal() {
        byte[] bArr = new byte[4];
        this.data = bArr;
        IntegerHelper.getTwoBytes(this.numSheets, bArr, 0);
        byte[] bArr2 = this.data;
        bArr2[2] = 1;
        bArr2[3] = 4;
        this.type = INTERNAL;
    }

    void adjustInternal(int sheets) {
        Assert.verify(this.type == INTERNAL);
        this.numSheets = sheets;
        initInternal();
    }

    private void initExternal() {
        int totalSheetNameLength = 0;
        for (int i = 0; i < this.numSheets; i++) {
            totalSheetNameLength += this.sheetNames[i].length();
        }
        byte[] fileNameData = EncodedURLHelper.getEncodedURL(this.fileName, this.workbookSettings);
        int length = fileNameData.length + 6;
        int i2 = this.numSheets;
        int dataLength = length + (i2 * 3) + (totalSheetNameLength * 2);
        byte[] bArr = new byte[dataLength];
        this.data = bArr;
        IntegerHelper.getTwoBytes(i2, bArr, 0);
        IntegerHelper.getTwoBytes(fileNameData.length + 1, this.data, 2);
        byte[] bArr2 = this.data;
        bArr2[2 + 2] = 0;
        bArr2[2 + 3] = 1;
        System.arraycopy(fileNameData, 0, bArr2, 2 + 4, fileNameData.length);
        int pos = 2 + fileNameData.length + 4;
        int i3 = 0;
        while (true) {
            String[] strArr = this.sheetNames;
            if (i3 < strArr.length) {
                IntegerHelper.getTwoBytes(strArr[i3].length(), this.data, pos);
                byte[] bArr3 = this.data;
                bArr3[pos + 2] = 1;
                StringHelper.getUnicodeBytes(this.sheetNames[i3], bArr3, pos + 3);
                pos += (this.sheetNames[i3].length() * 2) + 3;
                i3++;
            } else {
                return;
            }
        }
    }

    private void initAddin() {
        this.data = new byte[]{1, 0, 1, 58};
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        SupbookType supbookType = this.type;
        if (supbookType == INTERNAL) {
            initInternal();
        } else if (supbookType == EXTERNAL) {
            initExternal();
        } else if (supbookType == ADDIN) {
            initAddin();
        } else {
            logger.warn("unsupported supbook type - defaulting to internal");
            initInternal();
        }
        return this.data;
    }

    public SupbookType getType() {
        return this.type;
    }

    public int getNumberOfSheets() {
        return this.numSheets;
    }

    public String getFileName() {
        return this.fileName;
    }

    public int getSheetIndex(String s) {
        String[] strArr;
        boolean found = false;
        int sheetIndex = 0;
        int i = 0;
        while (true) {
            strArr = this.sheetNames;
            if (i >= strArr.length || found) {
                break;
            }
            if (strArr[i].equals(s)) {
                found = true;
                sheetIndex = 0;
            }
            i++;
        }
        if (found) {
            return sheetIndex;
        }
        String[] names = new String[strArr.length + 1];
        System.arraycopy(strArr, 0, names, 0, strArr.length);
        names[this.sheetNames.length] = s;
        this.sheetNames = names;
        return names.length - 1;
    }

    public String getSheetName(int s) {
        return this.sheetNames[s];
    }
}
