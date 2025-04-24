package jxl.read.biff;

import jxl.WorkbookSettings;
import jxl.biff.IntegerHelper;
import jxl.biff.RecordData;
import jxl.biff.StringHelper;
import jxl.common.Assert;

/* loaded from: classes.dex */
class SSTRecord extends RecordData {
    private int[] continuationBreaks;
    private String[] strings;
    private int totalStrings;
    private int uniqueStrings;

    private static class ByteArrayHolder {
        public byte[] bytes;

        private ByteArrayHolder() {
        }
    }

    private static class BooleanHolder {
        public boolean value;

        private BooleanHolder() {
        }
    }

    public SSTRecord(Record t, Record[] continuations, WorkbookSettings ws) {
        super(t);
        int totalRecordLength = 0;
        for (Record record : continuations) {
            totalRecordLength += record.getLength();
        }
        byte[] data = new byte[totalRecordLength + getRecord().getLength()];
        System.arraycopy(getRecord().getData(), 0, data, 0, getRecord().getLength());
        int pos = 0 + getRecord().getLength();
        this.continuationBreaks = new int[continuations.length];
        for (int i = 0; i < continuations.length; i++) {
            Record r = continuations[i];
            System.arraycopy(r.getData(), 0, data, pos, r.getLength());
            this.continuationBreaks[i] = pos;
            pos += r.getLength();
        }
        this.totalStrings = IntegerHelper.getInt(data[0], data[1], data[2], data[3]);
        int i2 = IntegerHelper.getInt(data[4], data[5], data[6], data[7]);
        this.uniqueStrings = i2;
        this.strings = new String[i2];
        readStrings(data, 8, ws);
    }

    private void readStrings(byte[] data, int offset, WorkbookSettings ws) {
        int formattingRuns;
        int pos;
        int extendedRunLength;
        int pos2 = offset;
        int formattingRuns2 = 0;
        int i = 0;
        int extendedRunLength2 = 0;
        while (i < this.uniqueStrings) {
            int numChars = IntegerHelper.getInt(data[pos2], data[pos2 + 1]);
            int pos3 = pos2 + 2;
            int optionFlags = data[pos3];
            int pos4 = pos3 + 1;
            boolean extendedString = (optionFlags & 4) != 0;
            boolean richString = (optionFlags & 8) != 0;
            if (!richString) {
                formattingRuns = formattingRuns2;
            } else {
                int formattingRuns3 = IntegerHelper.getInt(data[pos4], data[pos4 + 1]);
                pos4 += 2;
                formattingRuns = formattingRuns3;
            }
            if (!extendedString) {
                pos = pos4;
                extendedRunLength = extendedRunLength2;
            } else {
                int extendedRunLength3 = IntegerHelper.getInt(data[pos4], data[pos4 + 1], data[pos4 + 2], data[pos4 + 3]);
                pos = pos4 + 4;
                extendedRunLength = extendedRunLength3;
            }
            int pos5 = optionFlags & 1;
            boolean asciiEncoding = pos5 == 0;
            ByteArrayHolder bah = new ByteArrayHolder();
            BooleanHolder bh = new BooleanHolder();
            bh.value = asciiEncoding;
            int pos6 = pos + getChars(data, bah, pos, bh, numChars);
            boolean asciiEncoding2 = bh.value;
            String s = asciiEncoding2 ? StringHelper.getString(bah.bytes, numChars, 0, ws) : StringHelper.getUnicodeString(bah.bytes, numChars, 0);
            this.strings[i] = s;
            if (richString) {
                pos6 += formattingRuns * 4;
            }
            if (extendedString) {
                pos6 += extendedRunLength;
            }
            if (pos6 > data.length) {
                Assert.verify(false, "pos exceeds record length");
            }
            i++;
            pos2 = pos6;
            formattingRuns2 = formattingRuns;
            extendedRunLength2 = extendedRunLength;
        }
    }

    private int getChars(byte[] source, ByteArrayHolder bah, int pos, BooleanHolder ascii, int numChars) {
        int[] iArr;
        int charsRead;
        if (ascii.value) {
            bah.bytes = new byte[numChars];
        } else {
            bah.bytes = new byte[numChars * 2];
        }
        int i = 0;
        boolean spansBreak = false;
        while (true) {
            iArr = this.continuationBreaks;
            boolean z = false;
            if (i >= iArr.length || spansBreak) {
                break;
            }
            if (pos <= iArr[i] && bah.bytes.length + pos > this.continuationBreaks[i]) {
                z = true;
            }
            spansBreak = z;
            if (!spansBreak) {
                i++;
            }
        }
        if (!spansBreak) {
            System.arraycopy(source, pos, bah.bytes, 0, bah.bytes.length);
            return bah.bytes.length;
        }
        int breakpos = iArr[i];
        System.arraycopy(source, pos, bah.bytes, 0, breakpos - pos);
        int bytesRead = breakpos - pos;
        if (ascii.value) {
            charsRead = bytesRead;
        } else {
            int charsRead2 = bytesRead / 2;
            charsRead = charsRead2;
        }
        return bytesRead + getContinuedString(source, bah, bytesRead, i, ascii, numChars - charsRead);
    }

    private int getContinuedString(byte[] source, ByteArrayHolder bah, int destPos, int contBreakIndex, BooleanHolder ascii, int charsLeft) {
        int breakpos = this.continuationBreaks[contBreakIndex];
        int bytesRead = 0;
        while (charsLeft > 0) {
            Assert.verify(contBreakIndex < this.continuationBreaks.length, "continuation break index");
            if (ascii.value && source[breakpos] == 0) {
                int[] iArr = this.continuationBreaks;
                int length = contBreakIndex == iArr.length - 1 ? charsLeft : Math.min(charsLeft, (iArr[contBreakIndex + 1] - breakpos) - 1);
                System.arraycopy(source, breakpos + 1, bah.bytes, destPos, length);
                destPos += length;
                bytesRead += length + 1;
                charsLeft -= length;
                ascii.value = true;
            } else if (!ascii.value && source[breakpos] != 0) {
                int[] iArr2 = this.continuationBreaks;
                int length2 = contBreakIndex == iArr2.length - 1 ? charsLeft * 2 : Math.min(charsLeft * 2, (iArr2[contBreakIndex + 1] - breakpos) - 1);
                System.arraycopy(source, breakpos + 1, bah.bytes, destPos, length2);
                destPos += length2;
                bytesRead += length2 + 1;
                charsLeft -= length2 / 2;
                ascii.value = false;
            } else if (!ascii.value && source[breakpos] == 0) {
                int[] iArr3 = this.continuationBreaks;
                int chars = contBreakIndex == iArr3.length - 1 ? charsLeft : Math.min(charsLeft, (iArr3[contBreakIndex + 1] - breakpos) - 1);
                for (int j = 0; j < chars; j++) {
                    bah.bytes[destPos] = source[breakpos + j + 1];
                    destPos += 2;
                }
                bytesRead += chars + 1;
                charsLeft -= chars;
                ascii.value = false;
            } else {
                byte[] oldBytes = bah.bytes;
                bah.bytes = new byte[(destPos * 2) + (charsLeft * 2)];
                for (int j2 = 0; j2 < destPos; j2++) {
                    bah.bytes[j2 * 2] = oldBytes[j2];
                }
                int destPos2 = destPos * 2;
                int[] iArr4 = this.continuationBreaks;
                int length3 = contBreakIndex == iArr4.length - 1 ? charsLeft * 2 : Math.min(charsLeft * 2, (iArr4[contBreakIndex + 1] - breakpos) - 1);
                System.arraycopy(source, breakpos + 1, bah.bytes, destPos2, length3);
                destPos = destPos2 + length3;
                bytesRead += length3 + 1;
                charsLeft -= length3 / 2;
                ascii.value = false;
            }
            contBreakIndex++;
            int[] iArr5 = this.continuationBreaks;
            if (contBreakIndex < iArr5.length) {
                breakpos = iArr5[contBreakIndex];
            }
        }
        return bytesRead;
    }

    public String getString(int index) {
        Assert.verify(index < this.uniqueStrings);
        return this.strings[index];
    }
}
