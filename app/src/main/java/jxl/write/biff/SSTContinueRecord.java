package jxl.write.biff;

import java.util.ArrayList;
import java.util.Iterator;
import jxl.biff.IntegerHelper;
import jxl.biff.StringHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class SSTContinueRecord extends WritableRecordData {
    private static int maxBytes = 8224;
    private int byteCount;
    private byte[] data;
    private String firstString;
    private int firstStringLength;
    private boolean includeLength;
    private ArrayList stringLengths;
    private ArrayList strings;

    public SSTContinueRecord() {
        super(Type.CONTINUE);
        this.byteCount = 0;
        this.strings = new ArrayList(50);
        this.stringLengths = new ArrayList(50);
    }

    public int setFirstString(String s, boolean b) {
        int bytes;
        this.includeLength = b;
        this.firstStringLength = s.length();
        if (!this.includeLength) {
            bytes = (s.length() * 2) + 1;
        } else {
            int bytes2 = s.length();
            bytes = (bytes2 * 2) + 3;
        }
        int bytes3 = maxBytes;
        if (bytes <= bytes3) {
            this.firstString = s;
            this.byteCount += bytes;
            return 0;
        }
        int charsAvailable = (this.includeLength ? bytes3 - 4 : bytes3 - 2) / 2;
        this.firstString = s.substring(0, charsAvailable);
        this.byteCount = maxBytes - 1;
        return s.length() - charsAvailable;
    }

    public int getOffset() {
        return this.byteCount;
    }

    public int add(String s) {
        int bytes = (s.length() * 2) + 3;
        if (this.byteCount >= maxBytes - 5) {
            return s.length();
        }
        this.stringLengths.add(new Integer(s.length()));
        int i = this.byteCount;
        int i2 = bytes + i;
        int i3 = maxBytes;
        if (i2 < i3) {
            this.strings.add(s);
            this.byteCount += bytes;
            return 0;
        }
        int bytesLeft = (i3 - 3) - i;
        int charsAvailable = bytesLeft % 2 == 0 ? bytesLeft / 2 : (bytesLeft - 1) / 2;
        this.strings.add(s.substring(0, charsAvailable));
        this.byteCount += (charsAvailable * 2) + 3;
        return s.length() - charsAvailable;
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        int pos;
        byte[] bArr = new byte[this.byteCount];
        this.data = bArr;
        if (this.includeLength) {
            IntegerHelper.getTwoBytes(this.firstStringLength, bArr, 0);
            this.data[2] = 1;
            pos = 3;
        } else {
            bArr[0] = 1;
            pos = 1;
        }
        StringHelper.getUnicodeBytes(this.firstString, this.data, pos);
        int pos2 = pos + (this.firstString.length() * 2);
        Iterator i = this.strings.iterator();
        int count = 0;
        while (i.hasNext()) {
            String s = (String) i.next();
            int length = ((Integer) this.stringLengths.get(count)).intValue();
            IntegerHelper.getTwoBytes(length, this.data, pos2);
            byte[] bArr2 = this.data;
            bArr2[pos2 + 2] = 1;
            StringHelper.getUnicodeBytes(s, bArr2, pos2 + 3);
            pos2 += (s.length() * 2) + 3;
            count++;
        }
        return this.data;
    }
}
