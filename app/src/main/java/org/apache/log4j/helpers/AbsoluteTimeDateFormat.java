package org.apache.log4j.helpers;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/* loaded from: classes.dex */
public class AbsoluteTimeDateFormat extends DateFormat {
    public static final String ABS_TIME_DATE_FORMAT = "ABSOLUTE";
    public static final String DATE_AND_TIME_DATE_FORMAT = "DATE";
    public static final String ISO8601_DATE_FORMAT = "ISO8601";
    private static long previousTime;
    private static char[] previousTimeWithoutMillis = new char[9];

    public AbsoluteTimeDateFormat() {
        setCalendar(Calendar.getInstance());
    }

    public AbsoluteTimeDateFormat(TimeZone timeZone) {
        setCalendar(Calendar.getInstance(timeZone));
    }

    @Override // java.text.DateFormat
    public StringBuffer format(Date date, StringBuffer sbuf, FieldPosition fieldPosition) {
        long now = date.getTime();
        int millis = (int) (now % 1000);
        if (now - millis != previousTime) {
            ((DateFormat) this).calendar.setTime(date);
            int start = sbuf.length();
            int hour = ((DateFormat) this).calendar.get(11);
            if (hour < 10) {
                sbuf.append('0');
            }
            sbuf.append(hour);
            sbuf.append(':');
            int mins = ((DateFormat) this).calendar.get(12);
            if (mins < 10) {
                sbuf.append('0');
            }
            sbuf.append(mins);
            sbuf.append(':');
            int secs = ((DateFormat) this).calendar.get(13);
            if (secs < 10) {
                sbuf.append('0');
            }
            sbuf.append(secs);
            sbuf.append(',');
            sbuf.getChars(start, sbuf.length(), previousTimeWithoutMillis, 0);
            previousTime = now - millis;
        } else {
            sbuf.append(previousTimeWithoutMillis);
        }
        if (millis < 100) {
            sbuf.append('0');
        }
        if (millis < 10) {
            sbuf.append('0');
        }
        sbuf.append(millis);
        return sbuf;
    }

    @Override // java.text.DateFormat
    public Date parse(String s, ParsePosition pos) {
        return null;
    }
}
