package jxl.write;

import java.text.SimpleDateFormat;
import jxl.biff.DisplayFormat;
import jxl.write.biff.DateFormatRecord;

/* loaded from: classes.dex */
public class DateFormat extends DateFormatRecord implements DisplayFormat {
    public DateFormat(String format) {
        super(format);
        new SimpleDateFormat(format);
    }
}
