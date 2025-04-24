package jxl.biff.formula;

import jxl.WorkbookSettings;
import jxl.biff.StringHelper;
import jxl.common.Logger;

/* loaded from: classes.dex */
class StringValue extends Operand implements ParsedThing {
    private static final Logger logger = Logger.getLogger(StringValue.class);
    private WorkbookSettings settings;
    private String value;

    public StringValue(WorkbookSettings ws) {
        this.settings = ws;
    }

    public StringValue(String s) {
        this.value = s;
    }

    @Override // jxl.biff.formula.ParsedThing
    public int read(byte[] data, int pos) {
        int length = data[pos] & 255;
        if ((data[pos + 1] & 1) == 0) {
            this.value = StringHelper.getString(data, length, pos + 2, this.settings);
            int consumed = 2 + length;
            return consumed;
        }
        this.value = StringHelper.getUnicodeString(data, length, pos + 2);
        int consumed2 = 2 + (length * 2);
        return consumed2;
    }

    @Override // jxl.biff.formula.ParseItem
    byte[] getBytes() {
        byte[] data = new byte[(this.value.length() * 2) + 3];
        data[0] = Token.STRING.getCode();
        data[1] = (byte) this.value.length();
        data[2] = 1;
        StringHelper.getUnicodeBytes(this.value, data, 3);
        return data;
    }

    @Override // jxl.biff.formula.ParseItem
    public void getString(StringBuffer buf) {
        buf.append("\"");
        buf.append(this.value);
        buf.append("\"");
    }

    @Override // jxl.biff.formula.ParseItem
    void handleImportedCellReferences() {
    }
}
