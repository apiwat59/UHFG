package jxl.write.biff;

import java.util.ArrayList;
import java.util.Iterator;
import jxl.biff.IntegerHelper;
import jxl.biff.StringHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class SSTRecord extends WritableRecordData {
    private static int maxBytes = 8216;
    private int byteCount;
    private byte[] data;
    private int numReferences;
    private int numStrings;
    private ArrayList stringLengths;
    private ArrayList strings;

    public SSTRecord(int numRefs, int s) {
        super(Type.SST);
        this.numReferences = numRefs;
        this.numStrings = s;
        this.byteCount = 0;
        this.strings = new ArrayList(50);
        this.stringLengths = new ArrayList(50);
    }

    public int add(String s) {
        int bytes = (s.length() * 2) + 3;
        if (this.byteCount >= maxBytes - 5) {
            if (s.length() > 0) {
                return s.length();
            }
            return -1;
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

    public int getOffset() {
        return this.byteCount + 8;
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        byte[] bArr = new byte[this.byteCount + 8];
        this.data = bArr;
        IntegerHelper.getFourBytes(this.numReferences, bArr, 0);
        IntegerHelper.getFourBytes(this.numStrings, this.data, 4);
        int pos = 8;
        int count = 0;
        Iterator i = this.strings.iterator();
        while (i.hasNext()) {
            String s = (String) i.next();
            int length = ((Integer) this.stringLengths.get(count)).intValue();
            IntegerHelper.getTwoBytes(length, this.data, pos);
            byte[] bArr2 = this.data;
            bArr2[pos + 2] = 1;
            StringHelper.getUnicodeBytes(s, bArr2, pos + 3);
            pos += (s.length() * 2) + 3;
            count++;
        }
        return this.data;
    }
}
