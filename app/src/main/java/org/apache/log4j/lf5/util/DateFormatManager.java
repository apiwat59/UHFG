package org.apache.log4j.lf5.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/* loaded from: classes.dex */
public class DateFormatManager {
    private DateFormat _dateFormat;
    private Locale _locale;
    private String _pattern;
    private TimeZone _timeZone;

    public DateFormatManager() {
        this._timeZone = null;
        this._locale = null;
        this._pattern = null;
        this._dateFormat = null;
        configure();
    }

    public DateFormatManager(TimeZone timeZone) {
        this._timeZone = null;
        this._locale = null;
        this._pattern = null;
        this._dateFormat = null;
        this._timeZone = timeZone;
        configure();
    }

    public DateFormatManager(Locale locale) {
        this._timeZone = null;
        this._locale = null;
        this._pattern = null;
        this._dateFormat = null;
        this._locale = locale;
        configure();
    }

    public DateFormatManager(String pattern) {
        this._timeZone = null;
        this._locale = null;
        this._pattern = null;
        this._dateFormat = null;
        this._pattern = pattern;
        configure();
    }

    public DateFormatManager(TimeZone timeZone, Locale locale) {
        this._timeZone = null;
        this._locale = null;
        this._pattern = null;
        this._dateFormat = null;
        this._timeZone = timeZone;
        this._locale = locale;
        configure();
    }

    public DateFormatManager(TimeZone timeZone, String pattern) {
        this._timeZone = null;
        this._locale = null;
        this._pattern = null;
        this._dateFormat = null;
        this._timeZone = timeZone;
        this._pattern = pattern;
        configure();
    }

    public DateFormatManager(Locale locale, String pattern) {
        this._timeZone = null;
        this._locale = null;
        this._pattern = null;
        this._dateFormat = null;
        this._locale = locale;
        this._pattern = pattern;
        configure();
    }

    public DateFormatManager(TimeZone timeZone, Locale locale, String pattern) {
        this._timeZone = null;
        this._locale = null;
        this._pattern = null;
        this._dateFormat = null;
        this._timeZone = timeZone;
        this._locale = locale;
        this._pattern = pattern;
        configure();
    }

    public synchronized TimeZone getTimeZone() {
        TimeZone timeZone = this._timeZone;
        if (timeZone != null) {
            return timeZone;
        }
        return TimeZone.getDefault();
    }

    public synchronized void setTimeZone(TimeZone timeZone) {
        this._timeZone = timeZone;
        configure();
    }

    public synchronized Locale getLocale() {
        Locale locale = this._locale;
        if (locale != null) {
            return locale;
        }
        return Locale.getDefault();
    }

    public synchronized void setLocale(Locale locale) {
        this._locale = locale;
        configure();
    }

    public synchronized String getPattern() {
        return this._pattern;
    }

    public synchronized void setPattern(String pattern) {
        this._pattern = pattern;
        configure();
    }

    public synchronized String getOutputFormat() {
        return this._pattern;
    }

    public synchronized void setOutputFormat(String pattern) {
        this._pattern = pattern;
        configure();
    }

    public synchronized DateFormat getDateFormatInstance() {
        return this._dateFormat;
    }

    public synchronized void setDateFormatInstance(DateFormat dateFormat) {
        this._dateFormat = dateFormat;
    }

    public String format(Date date) {
        return getDateFormatInstance().format(date);
    }

    public String format(Date date, String pattern) {
        DateFormat formatter = getDateFormatInstance();
        if (formatter instanceof SimpleDateFormat) {
            formatter = (SimpleDateFormat) formatter.clone();
            ((SimpleDateFormat) formatter).applyPattern(pattern);
        }
        return formatter.format(date);
    }

    public Date parse(String date) throws ParseException {
        return getDateFormatInstance().parse(date);
    }

    public Date parse(String date, String pattern) throws ParseException {
        DateFormat formatter = getDateFormatInstance();
        if (formatter instanceof SimpleDateFormat) {
            formatter = (SimpleDateFormat) formatter.clone();
            ((SimpleDateFormat) formatter).applyPattern(pattern);
        }
        return formatter.parse(date);
    }

    private synchronized void configure() {
        DateFormat dateTimeInstance = DateFormat.getDateTimeInstance(0, 0, getLocale());
        this._dateFormat = dateTimeInstance;
        dateTimeInstance.setTimeZone(getTimeZone());
        String str = this._pattern;
        if (str != null) {
            ((SimpleDateFormat) this._dateFormat).applyPattern(str);
        }
    }
}
