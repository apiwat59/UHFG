package org.apache.log4j.helpers;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;

/* loaded from: classes.dex */
public class RelativeTimeDateFormat extends DateFormat {
    protected final long startTime = System.currentTimeMillis();

    @Override // java.text.DateFormat
    public StringBuffer format(Date date, StringBuffer sbuf, FieldPosition fieldPosition) {
        sbuf.append(date.getTime() - this.startTime);
        return sbuf;
    }

    @Override // java.text.DateFormat
    public Date parse(String s, ParsePosition pos) {
        return null;
    }
}
