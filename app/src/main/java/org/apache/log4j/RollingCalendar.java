package org.apache.log4j;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/* compiled from: DailyRollingFileAppender.java */
/* loaded from: classes.dex */
class RollingCalendar extends GregorianCalendar {
    int type;

    RollingCalendar() {
        this.type = -1;
    }

    RollingCalendar(TimeZone tz, Locale locale) {
        super(tz, locale);
        this.type = -1;
    }

    void setType(int type) {
        this.type = type;
    }

    public long getNextCheckMillis(Date now) {
        return getNextCheckDate(now).getTime();
    }

    public Date getNextCheckDate(Date now) {
        setTime(now);
        int i = this.type;
        if (i == 0) {
            set(13, 0);
            set(14, 0);
            add(12, 1);
        } else if (i == 1) {
            set(12, 0);
            set(13, 0);
            set(14, 0);
            add(11, 1);
        } else if (i == 2) {
            set(12, 0);
            set(13, 0);
            set(14, 0);
            int hour = get(11);
            if (hour < 12) {
                set(11, 12);
            } else {
                set(11, 0);
                add(5, 1);
            }
        } else if (i == 3) {
            set(11, 0);
            set(12, 0);
            set(13, 0);
            set(14, 0);
            add(5, 1);
        } else if (i == 4) {
            set(7, getFirstDayOfWeek());
            set(11, 0);
            set(12, 0);
            set(13, 0);
            set(14, 0);
            add(3, 1);
        } else {
            if (i != 5) {
                throw new IllegalStateException("Unknown periodicity type.");
            }
            set(5, 1);
            set(11, 0);
            set(12, 0);
            set(13, 0);
            set(14, 0);
            add(2, 1);
        }
        return getTime();
    }
}
