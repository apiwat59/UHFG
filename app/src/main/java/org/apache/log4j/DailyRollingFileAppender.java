package org.apache.log4j;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.LoggingEvent;

/* loaded from: classes.dex */
public class DailyRollingFileAppender extends FileAppender {
    static final int HALF_DAY = 2;
    static final int TOP_OF_DAY = 3;
    static final int TOP_OF_HOUR = 1;
    static final int TOP_OF_MINUTE = 0;
    static final int TOP_OF_MONTH = 5;
    static final int TOP_OF_TROUBLE = -1;
    static final int TOP_OF_WEEK = 4;
    static final TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");
    int checkPeriod;
    private String datePattern;
    private long nextCheck;
    Date now;
    RollingCalendar rc;
    private String scheduledFilename;
    SimpleDateFormat sdf;

    public DailyRollingFileAppender() {
        this.datePattern = "'.'yyyy-MM-dd";
        this.nextCheck = System.currentTimeMillis() - 1;
        this.now = new Date();
        this.rc = new RollingCalendar();
        this.checkPeriod = -1;
    }

    public DailyRollingFileAppender(Layout layout, String filename, String datePattern) throws IOException {
        super(layout, filename, true);
        this.datePattern = "'.'yyyy-MM-dd";
        this.nextCheck = System.currentTimeMillis() - 1;
        this.now = new Date();
        this.rc = new RollingCalendar();
        this.checkPeriod = -1;
        this.datePattern = datePattern;
        activateOptions();
    }

    public void setDatePattern(String pattern) {
        this.datePattern = pattern;
    }

    public String getDatePattern() {
        return this.datePattern;
    }

    @Override // org.apache.log4j.FileAppender, org.apache.log4j.WriterAppender, org.apache.log4j.AppenderSkeleton, org.apache.log4j.spi.OptionHandler
    public void activateOptions() {
        super.activateOptions();
        if (this.datePattern != null && this.fileName != null) {
            this.now.setTime(System.currentTimeMillis());
            this.sdf = new SimpleDateFormat(this.datePattern);
            int type = computeCheckPeriod();
            printPeriodicity(type);
            this.rc.setType(type);
            File file = new File(this.fileName);
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(this.fileName);
            stringBuffer.append(this.sdf.format(new Date(file.lastModified())));
            this.scheduledFilename = stringBuffer.toString();
            return;
        }
        StringBuffer stringBuffer2 = new StringBuffer();
        stringBuffer2.append("Either File or DatePattern options are not set for appender [");
        stringBuffer2.append(this.name);
        stringBuffer2.append("].");
        LogLog.error(stringBuffer2.toString());
    }

    void printPeriodicity(int type) {
        if (type == 0) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Appender [");
            stringBuffer.append(this.name);
            stringBuffer.append("] to be rolled every minute.");
            LogLog.debug(stringBuffer.toString());
            return;
        }
        if (type == 1) {
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2.append("Appender [");
            stringBuffer2.append(this.name);
            stringBuffer2.append("] to be rolled on top of every hour.");
            LogLog.debug(stringBuffer2.toString());
            return;
        }
        if (type == 2) {
            StringBuffer stringBuffer3 = new StringBuffer();
            stringBuffer3.append("Appender [");
            stringBuffer3.append(this.name);
            stringBuffer3.append("] to be rolled at midday and midnight.");
            LogLog.debug(stringBuffer3.toString());
            return;
        }
        if (type == 3) {
            StringBuffer stringBuffer4 = new StringBuffer();
            stringBuffer4.append("Appender [");
            stringBuffer4.append(this.name);
            stringBuffer4.append("] to be rolled at midnight.");
            LogLog.debug(stringBuffer4.toString());
            return;
        }
        if (type == 4) {
            StringBuffer stringBuffer5 = new StringBuffer();
            stringBuffer5.append("Appender [");
            stringBuffer5.append(this.name);
            stringBuffer5.append("] to be rolled at start of week.");
            LogLog.debug(stringBuffer5.toString());
            return;
        }
        if (type == 5) {
            StringBuffer stringBuffer6 = new StringBuffer();
            stringBuffer6.append("Appender [");
            stringBuffer6.append(this.name);
            stringBuffer6.append("] to be rolled at start of every month.");
            LogLog.debug(stringBuffer6.toString());
            return;
        }
        StringBuffer stringBuffer7 = new StringBuffer();
        stringBuffer7.append("Unknown periodicity for appender [");
        stringBuffer7.append(this.name);
        stringBuffer7.append("].");
        LogLog.warn(stringBuffer7.toString());
    }

    int computeCheckPeriod() {
        RollingCalendar rollingCalendar = new RollingCalendar(gmtTimeZone, Locale.ENGLISH);
        Date epoch = new Date(0L);
        if (this.datePattern != null) {
            for (int i = 0; i <= 5; i++) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(this.datePattern);
                simpleDateFormat.setTimeZone(gmtTimeZone);
                String r0 = simpleDateFormat.format(epoch);
                rollingCalendar.setType(i);
                Date next = new Date(rollingCalendar.getNextCheckMillis(epoch));
                String r1 = simpleDateFormat.format(next);
                if (r0 != null && r1 != null && !r0.equals(r1)) {
                    return i;
                }
            }
            return -1;
        }
        return -1;
    }

    void rollOver() throws IOException {
        if (this.datePattern == null) {
            this.errorHandler.error("Missing DatePattern option in rollOver().");
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.fileName);
        stringBuffer.append(this.sdf.format(this.now));
        String datedFilename = stringBuffer.toString();
        if (this.scheduledFilename.equals(datedFilename)) {
            return;
        }
        closeFile();
        File target = new File(this.scheduledFilename);
        if (target.exists()) {
            target.delete();
        }
        File file = new File(this.fileName);
        boolean result = file.renameTo(target);
        if (result) {
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2.append(this.fileName);
            stringBuffer2.append(" -> ");
            stringBuffer2.append(this.scheduledFilename);
            LogLog.debug(stringBuffer2.toString());
        } else {
            StringBuffer stringBuffer3 = new StringBuffer();
            stringBuffer3.append("Failed to rename [");
            stringBuffer3.append(this.fileName);
            stringBuffer3.append("] to [");
            stringBuffer3.append(this.scheduledFilename);
            stringBuffer3.append("].");
            LogLog.error(stringBuffer3.toString());
        }
        try {
            setFile(this.fileName, false, this.bufferedIO, this.bufferSize);
        } catch (IOException e) {
            ErrorHandler errorHandler = this.errorHandler;
            StringBuffer stringBuffer4 = new StringBuffer();
            stringBuffer4.append("setFile(");
            stringBuffer4.append(this.fileName);
            stringBuffer4.append(", false) call failed.");
            errorHandler.error(stringBuffer4.toString());
        }
        this.scheduledFilename = datedFilename;
    }

    @Override // org.apache.log4j.WriterAppender
    protected void subAppend(LoggingEvent event) {
        long n = System.currentTimeMillis();
        if (n >= this.nextCheck) {
            this.now.setTime(n);
            this.nextCheck = this.rc.getNextCheckMillis(this.now);
            try {
                rollOver();
            } catch (IOException ioe) {
                LogLog.error("rollOver() failed.", ioe);
            }
        }
        super.subAppend(event);
    }
}
