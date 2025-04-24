package jxl.read.biff;

import jxl.WorkbookSettings;
import jxl.biff.IntegerHelper;
import jxl.biff.RecordData;
import jxl.biff.StringHelper;
import jxl.common.Logger;

/* loaded from: classes.dex */
public class SupbookRecord extends RecordData {
    public static final Type ADDIN;
    public static final Type EXTERNAL;
    public static final Type INTERNAL;
    public static final Type LINK;
    public static final Type UNKNOWN;
    private static Logger logger = Logger.getLogger(SupbookRecord.class);
    private String fileName;
    private int numSheets;
    private String[] sheetNames;
    private Type type;

    static {
        INTERNAL = new Type();
        EXTERNAL = new Type();
        ADDIN = new Type();
        LINK = new Type();
        UNKNOWN = new Type();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX INFO: Access modifiers changed from: private */
    public static class Type {
        private Type() {
        }
    }

    SupbookRecord(Record t, WorkbookSettings ws) {
        super(t);
        byte[] data = getRecord().getData();
        if (data.length == 4) {
            if (data[2] == 1 && data[3] == 4) {
                this.type = INTERNAL;
            } else if (data[2] == 1 && data[3] == 58) {
                this.type = ADDIN;
            } else {
                this.type = UNKNOWN;
            }
        } else if (data[0] == 0 && data[1] == 0) {
            this.type = LINK;
        } else {
            this.type = EXTERNAL;
        }
        if (this.type == INTERNAL) {
            this.numSheets = IntegerHelper.getInt(data[0], data[1]);
        }
        if (this.type == EXTERNAL) {
            readExternal(data, ws);
        }
    }

    private void readExternal(byte[] data, WorkbookSettings ws) {
        int pos;
        this.numSheets = IntegerHelper.getInt(data[0], data[1]);
        int ln = IntegerHelper.getInt(data[2], data[3]) - 1;
        if (data[4] == 0) {
            int encoding = data[5];
            if (encoding == 0) {
                this.fileName = StringHelper.getString(data, ln, 6, ws);
                pos = 6 + ln;
            } else {
                this.fileName = getEncodedFilename(data, ln, 6);
                pos = 6 + ln;
            }
        } else {
            int encoding2 = IntegerHelper.getInt(data[5], data[6]);
            if (encoding2 == 0) {
                this.fileName = StringHelper.getUnicodeString(data, ln, 7);
                pos = 7 + (ln * 2);
            } else {
                this.fileName = getUnicodeEncodedFilename(data, ln, 7);
                pos = 7 + (ln * 2);
            }
        }
        int encoding3 = this.numSheets;
        this.sheetNames = new String[encoding3];
        for (int i = 0; i < this.sheetNames.length; i++) {
            int ln2 = IntegerHelper.getInt(data[pos], data[pos + 1]);
            if (data[pos + 2] == 0) {
                this.sheetNames[i] = StringHelper.getString(data, ln2, pos + 3, ws);
                pos += ln2 + 3;
            } else if (data[pos + 2] == 1) {
                this.sheetNames[i] = StringHelper.getUnicodeString(data, ln2, pos + 3);
                pos += (ln2 * 2) + 3;
            }
        }
    }

    public Type getType() {
        return this.type;
    }

    public int getNumberOfSheets() {
        return this.numSheets;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getSheetName(int i) {
        return this.sheetNames[i];
    }

    public byte[] getData() {
        return getRecord().getData();
    }

    private String getEncodedFilename(byte[] data, int ln, int pos) {
        StringBuffer buf = new StringBuffer();
        int endpos = pos + ln;
        while (pos < endpos) {
            char c = (char) data[pos];
            if (c == 1) {
                pos++;
                buf.append((char) data[pos]);
                buf.append(":\\\\");
            } else if (c == 2) {
                buf.append('\\');
            } else if (c == 3) {
                buf.append('\\');
            } else if (c == 4) {
                buf.append("..\\");
            } else {
                buf.append(c);
            }
            pos++;
        }
        return buf.toString();
    }

    private String getUnicodeEncodedFilename(byte[] data, int ln, int pos) {
        StringBuffer buf = new StringBuffer();
        int endpos = (ln * 2) + pos;
        while (pos < endpos) {
            char c = (char) IntegerHelper.getInt(data[pos], data[pos + 1]);
            if (c == 1) {
                pos += 2;
                buf.append((char) IntegerHelper.getInt(data[pos], data[pos + 1]));
                buf.append(":\\\\");
            } else if (c == 2) {
                buf.append('\\');
            } else if (c == 3) {
                buf.append('\\');
            } else if (c == 4) {
                buf.append("..\\");
            } else {
                buf.append(c);
            }
            pos += 2;
        }
        return buf.toString();
    }
}
