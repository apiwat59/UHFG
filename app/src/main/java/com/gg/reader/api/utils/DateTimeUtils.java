package com.gg.reader.api.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/* loaded from: classes.dex */
public class DateTimeUtils {
    public static long duration(Date end, Date start) {
        return end.getTime() - start.getTime();
    }

    public static long durationS(Date end, Date start) {
        return duration(end, start) / 1000;
    }

    public static long elapse(Date begin) {
        return duration(new Date(), begin);
    }

    public static long elapseS(Date begin) {
        return durationS(new Date(), begin);
    }

    public static long UTC(Date date) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(1970, 0, 1, 0, 0, 0);
        Date start = calendar.getTime();
        return duration(date, start);
    }

    public static long UtcFromTimeZone(Date date, TimeZone tz) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(1970, 0, 1, 0, 0, 0);
        Date start = calendar.getTime();
        Calendar sCalendar = Calendar.getInstance(tz);
        sCalendar.setTime(date);
        sCalendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        return duration(sCalendar.getTime(), start);
    }

    public static long UTCS(Date date) {
        return (UTC(date) + 500) / 1000;
    }

    public static Date fromUTC(long utc) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.set(1970, 0, 1, 0, 0, 0);
        Date start = calendar.getTime();
        long begin = start.getTime();
        calendar.setTimeInMillis(begin + utc);
        return calendar.getTime();
    }

    public static Date fromUtcToTimeZone(long utc, TimeZone tz) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.set(1970, 0, 1, 0, 0, 0);
        Date start = calendar.getTime();
        long begin = start.getTime();
        calendar.setTimeInMillis(begin + utc);
        calendar.setTimeZone(tz);
        return calendar.getTime();
    }

    public static Date fromUTCS(long utc) {
        return fromUTC(1000 * utc);
    }
}
