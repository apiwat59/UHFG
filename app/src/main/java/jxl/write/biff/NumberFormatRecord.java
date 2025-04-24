package jxl.write.biff;

import jxl.biff.FormatRecord;
import jxl.common.Logger;

/* loaded from: classes.dex */
public class NumberFormatRecord extends FormatRecord {
    private static Logger logger = Logger.getLogger(NumberFormatRecord.class);

    /* JADX INFO: Access modifiers changed from: protected */
    public static class NonValidatingFormat {
    }

    protected NumberFormatRecord(String fmt) {
        String fs = replace(fmt, "E0", "E+0");
        setFormatString(trimInvalidChars(fs));
    }

    protected NumberFormatRecord(String fmt, NonValidatingFormat dummy) {
        String fs = replace(fmt, "E0", "E+0");
        setFormatString(fs);
    }

    private String trimInvalidChars(String fs) {
        int firstHash = fs.indexOf(35);
        int firstZero = fs.indexOf(48);
        if (firstHash == -1 && firstZero == -1) {
            return "#.###";
        }
        if (firstHash != 0 && firstZero != 0 && firstHash != 1 && firstZero != 1) {
            if (firstHash == -1) {
                firstHash = Integer.MAX_VALUE;
            }
            if (firstZero == -1) {
                firstZero = Integer.MAX_VALUE;
            }
            int firstValidChar = Math.min(firstHash, firstZero);
            StringBuffer tmp = new StringBuffer();
            tmp.append(fs.charAt(0));
            tmp.append(fs.substring(firstValidChar));
            fs = tmp.toString();
        }
        int lastHash = fs.lastIndexOf(35);
        int lastZero = fs.lastIndexOf(48);
        if (lastHash == fs.length() || lastZero == fs.length()) {
            return fs;
        }
        int lastValidChar = Math.max(lastHash, lastZero);
        while (fs.length() > lastValidChar + 1 && (fs.charAt(lastValidChar + 1) == ')' || fs.charAt(lastValidChar + 1) == '%')) {
            lastValidChar++;
        }
        return fs.substring(0, lastValidChar + 1);
    }
}
