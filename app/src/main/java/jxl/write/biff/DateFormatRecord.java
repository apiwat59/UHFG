package jxl.write.biff;

import jxl.biff.FormatRecord;

/* loaded from: classes.dex */
public class DateFormatRecord extends FormatRecord {
    protected DateFormatRecord(String fmt) {
        String fs = replace(fmt, "a", "AM/PM");
        setFormatString(replace(fs, "S", "0"));
    }
}
