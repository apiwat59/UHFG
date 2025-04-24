package jxl.biff;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import jxl.WorkbookSettings;
import jxl.common.Logger;
import jxl.format.Format;
import jxl.read.biff.Record;

/* loaded from: classes.dex */
public class FormatRecord extends WritableRecordData implements DisplayFormat, Format {
    public static final BiffType biff7;
    public static final BiffType biff8;
    private byte[] data;
    private boolean date;
    private java.text.Format format;
    private String formatString;
    private int indexCode;
    private boolean initialized;
    private boolean number;
    public static Logger logger = Logger.getLogger(FormatRecord.class);
    private static String[] dateStrings = {"dd", "mm", "yy", "hh", "ss", "m/", "/d"};

    static {
        biff8 = new BiffType();
        biff7 = new BiffType();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static class BiffType {
        private BiffType() {
        }
    }

    FormatRecord(String fmt, int refno) {
        super(Type.FORMAT);
        this.formatString = fmt;
        this.indexCode = refno;
        this.initialized = true;
    }

    protected FormatRecord() {
        super(Type.FORMAT);
        this.initialized = false;
    }

    protected FormatRecord(FormatRecord fr) {
        super(Type.FORMAT);
        this.initialized = false;
        this.formatString = fr.formatString;
        this.date = fr.date;
        this.number = fr.number;
    }

    public FormatRecord(Record t, WorkbookSettings ws, BiffType biffType) {
        super(t);
        byte[] data = getRecord().getData();
        this.indexCode = IntegerHelper.getInt(data[0], data[1]);
        this.initialized = true;
        if (biffType == biff8) {
            int numchars = IntegerHelper.getInt(data[2], data[3]);
            if (data[4] == 0) {
                this.formatString = StringHelper.getString(data, numchars, 5, ws);
            } else {
                this.formatString = StringHelper.getUnicodeString(data, numchars, 5);
            }
        } else {
            byte[] chars = new byte[data[2]];
            System.arraycopy(data, 3, chars, 0, chars.length);
            this.formatString = new String(chars);
        }
        this.date = false;
        this.number = false;
        int i = 0;
        while (true) {
            String[] strArr = dateStrings;
            if (i >= strArr.length) {
                break;
            }
            String dateString = strArr[i];
            if (this.formatString.indexOf(dateString) != -1 || this.formatString.indexOf(dateString.toUpperCase()) != -1) {
                break;
            } else {
                i++;
            }
        }
        this.date = true;
        if (!this.date) {
            if (this.formatString.indexOf(35) != -1 || this.formatString.indexOf(48) != -1) {
                this.number = true;
            }
        }
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        byte[] bArr = new byte[(this.formatString.length() * 2) + 3 + 2];
        this.data = bArr;
        IntegerHelper.getTwoBytes(this.indexCode, bArr, 0);
        IntegerHelper.getTwoBytes(this.formatString.length(), this.data, 2);
        byte[] bArr2 = this.data;
        bArr2[4] = 1;
        StringHelper.getUnicodeBytes(this.formatString, bArr2, 5);
        return this.data;
    }

    @Override // jxl.biff.DisplayFormat
    public int getFormatIndex() {
        return this.indexCode;
    }

    @Override // jxl.biff.DisplayFormat
    public boolean isInitialized() {
        return this.initialized;
    }

    @Override // jxl.biff.DisplayFormat
    public void initialize(int pos) {
        this.indexCode = pos;
        this.initialized = true;
    }

    protected final String replace(String input, String search, String replace) {
        String fmtstr = input;
        int pos = fmtstr.indexOf(search);
        while (pos != -1) {
            StringBuffer tmp = new StringBuffer(fmtstr.substring(0, pos));
            tmp.append(replace);
            tmp.append(fmtstr.substring(search.length() + pos));
            fmtstr = tmp.toString();
            pos = fmtstr.indexOf(search);
        }
        return fmtstr;
    }

    protected final void setFormatString(String s) {
        this.formatString = s;
    }

    public final boolean isDate() {
        return this.date;
    }

    public final boolean isNumber() {
        return this.number;
    }

    public final NumberFormat getNumberFormat() {
        java.text.Format format = this.format;
        if (format != null && (format instanceof NumberFormat)) {
            return (NumberFormat) format;
        }
        try {
            String fs = this.formatString;
            this.format = new DecimalFormat(replace(replace(replace(replace(replace(fs, "E+", "E"), "_)", ""), "_", ""), "[Red]", ""), "\\", ""));
        } catch (IllegalArgumentException e) {
            this.format = new DecimalFormat("#.###");
        }
        return (NumberFormat) this.format;
    }

    public final DateFormat getDateFormat() {
        char ind;
        int end;
        java.text.Format format = this.format;
        if (format != null && (format instanceof DateFormat)) {
            return (DateFormat) format;
        }
        String fmt = this.formatString;
        int pos = fmt.indexOf("AM/PM");
        while (pos != -1) {
            StringBuffer sb = new StringBuffer(fmt.substring(0, pos));
            sb.append('a');
            sb.append(fmt.substring(pos + 5));
            fmt = sb.toString();
            pos = fmt.indexOf("AM/PM");
        }
        int pos2 = fmt.indexOf("ss.0");
        while (pos2 != -1) {
            StringBuffer sb2 = new StringBuffer(fmt.substring(0, pos2));
            sb2.append("ss.SSS");
            int pos3 = pos2 + 4;
            while (pos3 < fmt.length() && fmt.charAt(pos3) == '0') {
                pos3++;
            }
            sb2.append(fmt.substring(pos3));
            fmt = sb2.toString();
            pos2 = fmt.indexOf("ss.0");
        }
        StringBuffer sb3 = new StringBuffer();
        for (int i = 0; i < fmt.length(); i++) {
            if (fmt.charAt(i) != '\\') {
                sb3.append(fmt.charAt(i));
            }
        }
        String fmt2 = sb3.toString();
        if (fmt2.charAt(0) == '[' && (end = fmt2.indexOf(93)) != -1) {
            fmt2 = fmt2.substring(end + 1);
        }
        char[] formatBytes = replace(fmt2, ";@", "").toCharArray();
        for (int i2 = 0; i2 < formatBytes.length; i2++) {
            if (formatBytes[i2] == 'm') {
                if (i2 > 0 && (formatBytes[i2 - 1] == 'm' || formatBytes[i2 - 1] == 'M')) {
                    formatBytes[i2] = formatBytes[i2 - 1];
                } else {
                    int minuteDist = Integer.MAX_VALUE;
                    int j = i2 - 1;
                    while (true) {
                        if (j <= 0) {
                            break;
                        }
                        if (formatBytes[j] == 'h') {
                            minuteDist = i2 - j;
                            break;
                        }
                        j--;
                    }
                    int j2 = i2 + 1;
                    while (true) {
                        if (j2 >= formatBytes.length) {
                            break;
                        }
                        if (formatBytes[j2] != 'h') {
                            j2++;
                        } else {
                            minuteDist = Math.min(minuteDist, j2 - i2);
                            break;
                        }
                    }
                    int j3 = i2 - 1;
                    while (true) {
                        if (j3 <= 0) {
                            break;
                        }
                        if (formatBytes[j3] == 'H') {
                            minuteDist = i2 - j3;
                            break;
                        }
                        j3--;
                    }
                    int j4 = i2 + 1;
                    while (true) {
                        if (j4 >= formatBytes.length) {
                            break;
                        }
                        if (formatBytes[j4] != 'H') {
                            j4++;
                        } else {
                            minuteDist = Math.min(minuteDist, j4 - i2);
                            break;
                        }
                    }
                    int j5 = i2 - 1;
                    while (true) {
                        if (j5 <= 0) {
                            break;
                        }
                        if (formatBytes[j5] == 's') {
                            minuteDist = Math.min(minuteDist, i2 - j5);
                            break;
                        }
                        j5--;
                    }
                    int j6 = i2 + 1;
                    while (true) {
                        if (j6 >= formatBytes.length) {
                            break;
                        }
                        if (formatBytes[j6] != 's') {
                            j6++;
                        } else {
                            minuteDist = Math.min(minuteDist, j6 - i2);
                            break;
                        }
                    }
                    int monthDist = Integer.MAX_VALUE;
                    int j7 = i2 - 1;
                    while (true) {
                        if (j7 <= 0) {
                            break;
                        }
                        if (formatBytes[j7] == 'd') {
                            monthDist = i2 - j7;
                            break;
                        }
                        j7--;
                    }
                    int j8 = i2 + 1;
                    while (true) {
                        if (j8 >= formatBytes.length) {
                            break;
                        }
                        if (formatBytes[j8] != 'd') {
                            j8++;
                        } else {
                            monthDist = Math.min(monthDist, j8 - i2);
                            break;
                        }
                    }
                    int j9 = i2 - 1;
                    while (true) {
                        if (j9 <= 0) {
                            break;
                        }
                        if (formatBytes[j9] == 'y') {
                            monthDist = Math.min(monthDist, i2 - j9);
                            break;
                        }
                        j9--;
                    }
                    int j10 = i2 + 1;
                    while (true) {
                        if (j10 >= formatBytes.length) {
                            break;
                        }
                        if (formatBytes[j10] != 'y') {
                            j10++;
                        } else {
                            monthDist = Math.min(monthDist, j10 - i2);
                            break;
                        }
                    }
                    if (monthDist < minuteDist) {
                        formatBytes[i2] = Character.toUpperCase(formatBytes[i2]);
                    } else if (monthDist == minuteDist && monthDist != Integer.MAX_VALUE && ((ind = formatBytes[i2 - monthDist]) == 'y' || ind == 'd')) {
                        formatBytes[i2] = Character.toUpperCase(formatBytes[i2]);
                    }
                }
            }
        }
        try {
            this.format = new SimpleDateFormat(new String(formatBytes));
        } catch (IllegalArgumentException e) {
            this.format = new SimpleDateFormat("dd MM yyyy hh:mm:ss");
        }
        return (DateFormat) this.format;
    }

    public int getIndexCode() {
        return this.indexCode;
    }

    @Override // jxl.format.Format
    public String getFormatString() {
        return this.formatString;
    }

    @Override // jxl.biff.DisplayFormat
    public boolean isBuiltIn() {
        return false;
    }

    public int hashCode() {
        return this.formatString.hashCode();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof FormatRecord)) {
            return false;
        }
        FormatRecord fr = (FormatRecord) o;
        if (this.initialized && fr.initialized) {
            if (this.date == fr.date && this.number == fr.number) {
                return this.formatString.equals(fr.formatString);
            }
            return false;
        }
        return this.formatString.equals(fr.formatString);
    }
}
