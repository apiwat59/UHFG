package org.apache.log4j.net;

import java.io.PrintStream;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.SyslogQuietWriter;
import org.apache.log4j.helpers.SyslogWriter;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.LoggingEvent;

/* loaded from: classes.dex */
public class SyslogAppender extends AppenderSkeleton {
    protected static final int FACILITY_OI = 1;
    public static final int LOG_AUTH = 32;
    public static final int LOG_AUTHPRIV = 80;
    public static final int LOG_CRON = 72;
    public static final int LOG_DAEMON = 24;
    public static final int LOG_FTP = 88;
    public static final int LOG_KERN = 0;
    public static final int LOG_LOCAL0 = 128;
    public static final int LOG_LOCAL1 = 136;
    public static final int LOG_LOCAL2 = 144;
    public static final int LOG_LOCAL3 = 152;
    public static final int LOG_LOCAL4 = 160;
    public static final int LOG_LOCAL5 = 168;
    public static final int LOG_LOCAL6 = 176;
    public static final int LOG_LOCAL7 = 184;
    public static final int LOG_LPR = 48;
    public static final int LOG_MAIL = 16;
    public static final int LOG_NEWS = 56;
    public static final int LOG_SYSLOG = 40;
    public static final int LOG_USER = 8;
    public static final int LOG_UUCP = 64;
    protected static final int SYSLOG_HOST_OI = 0;
    static final String TAB = "    ";
    boolean facilityPrinting;
    String facilityStr;
    SyslogQuietWriter sqw;
    int syslogFacility;
    String syslogHost;

    public SyslogAppender() {
        this.syslogFacility = 8;
        this.facilityPrinting = false;
        initSyslogFacilityStr();
    }

    public SyslogAppender(Layout layout, int syslogFacility) {
        this.syslogFacility = 8;
        this.facilityPrinting = false;
        this.layout = layout;
        this.syslogFacility = syslogFacility;
        initSyslogFacilityStr();
    }

    public SyslogAppender(Layout layout, String syslogHost, int syslogFacility) {
        this(layout, syslogFacility);
        setSyslogHost(syslogHost);
    }

    @Override // org.apache.log4j.AppenderSkeleton, org.apache.log4j.Appender
    public synchronized void close() {
        this.closed = true;
        this.sqw = null;
    }

    private void initSyslogFacilityStr() {
        String facilityString = getFacilityString(this.syslogFacility);
        this.facilityStr = facilityString;
        if (facilityString == null) {
            PrintStream printStream = System.err;
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("\"");
            stringBuffer.append(this.syslogFacility);
            stringBuffer.append("\" is an unknown syslog facility. Defaulting to \"USER\".");
            printStream.println(stringBuffer.toString());
            this.syslogFacility = 8;
            this.facilityStr = "user:";
            return;
        }
        StringBuffer stringBuffer2 = new StringBuffer();
        stringBuffer2.append(this.facilityStr);
        stringBuffer2.append(":");
        this.facilityStr = stringBuffer2.toString();
    }

    public static String getFacilityString(int syslogFacility) {
        switch (syslogFacility) {
            case 0:
                return "kern";
            case 8:
                return "user";
            case 16:
                return "mail";
            case 24:
                return "daemon";
            case 32:
                return "auth";
            case 40:
                return "syslog";
            case 48:
                return "lpr";
            case 56:
                return "news";
            case 64:
                return "uucp";
            case 72:
                return "cron";
            case 80:
                return "authpriv";
            case 88:
                return "ftp";
            case 128:
                return "local0";
            case LOG_LOCAL1 /* 136 */:
                return "local1";
            case LOG_LOCAL2 /* 144 */:
                return "local2";
            case LOG_LOCAL3 /* 152 */:
                return "local3";
            case 160:
                return "local4";
            case LOG_LOCAL5 /* 168 */:
                return "local5";
            case LOG_LOCAL6 /* 176 */:
                return "local6";
            case LOG_LOCAL7 /* 184 */:
                return "local7";
            default:
                return null;
        }
    }

    public static int getFacility(String facilityName) {
        if (facilityName != null) {
            facilityName = facilityName.trim();
        }
        if ("KERN".equalsIgnoreCase(facilityName)) {
            return 0;
        }
        if ("USER".equalsIgnoreCase(facilityName)) {
            return 8;
        }
        if ("MAIL".equalsIgnoreCase(facilityName)) {
            return 16;
        }
        if ("DAEMON".equalsIgnoreCase(facilityName)) {
            return 24;
        }
        if ("AUTH".equalsIgnoreCase(facilityName)) {
            return 32;
        }
        if ("SYSLOG".equalsIgnoreCase(facilityName)) {
            return 40;
        }
        if ("LPR".equalsIgnoreCase(facilityName)) {
            return 48;
        }
        if ("NEWS".equalsIgnoreCase(facilityName)) {
            return 56;
        }
        if ("UUCP".equalsIgnoreCase(facilityName)) {
            return 64;
        }
        if ("CRON".equalsIgnoreCase(facilityName)) {
            return 72;
        }
        if ("AUTHPRIV".equalsIgnoreCase(facilityName)) {
            return 80;
        }
        if ("FTP".equalsIgnoreCase(facilityName)) {
            return 88;
        }
        if ("LOCAL0".equalsIgnoreCase(facilityName)) {
            return 128;
        }
        if ("LOCAL1".equalsIgnoreCase(facilityName)) {
            return LOG_LOCAL1;
        }
        if ("LOCAL2".equalsIgnoreCase(facilityName)) {
            return LOG_LOCAL2;
        }
        if ("LOCAL3".equalsIgnoreCase(facilityName)) {
            return LOG_LOCAL3;
        }
        if ("LOCAL4".equalsIgnoreCase(facilityName)) {
            return 160;
        }
        if ("LOCAL5".equalsIgnoreCase(facilityName)) {
            return LOG_LOCAL5;
        }
        if ("LOCAL6".equalsIgnoreCase(facilityName)) {
            return LOG_LOCAL6;
        }
        if ("LOCAL7".equalsIgnoreCase(facilityName)) {
            return LOG_LOCAL7;
        }
        return -1;
    }

    @Override // org.apache.log4j.AppenderSkeleton
    public void append(LoggingEvent event) {
        String[] s;
        int len;
        if (!isAsSevereAsThreshold(event.getLevel())) {
            return;
        }
        if (this.sqw == null) {
            ErrorHandler errorHandler = this.errorHandler;
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("No syslog host is set for SyslogAppedender named \"");
            stringBuffer.append(this.name);
            stringBuffer.append("\".");
            errorHandler.error(stringBuffer.toString());
            return;
        }
        StringBuffer stringBuffer2 = new StringBuffer();
        stringBuffer2.append(this.facilityPrinting ? this.facilityStr : "");
        stringBuffer2.append(this.layout.format(event));
        String buffer = stringBuffer2.toString();
        this.sqw.setLevel(event.getLevel().getSyslogEquivalent());
        this.sqw.write(buffer);
        if (this.layout.ignoresThrowable() && (s = event.getThrowableStrRep()) != null && (len = s.length) > 0) {
            this.sqw.write(s[0]);
            for (int i = 1; i < len; i++) {
                SyslogQuietWriter syslogQuietWriter = this.sqw;
                StringBuffer stringBuffer3 = new StringBuffer();
                stringBuffer3.append(TAB);
                stringBuffer3.append(s[i].substring(1));
                syslogQuietWriter.write(stringBuffer3.toString());
            }
        }
    }

    @Override // org.apache.log4j.AppenderSkeleton, org.apache.log4j.spi.OptionHandler
    public void activateOptions() {
    }

    @Override // org.apache.log4j.AppenderSkeleton, org.apache.log4j.Appender
    public boolean requiresLayout() {
        return true;
    }

    public void setSyslogHost(String syslogHost) {
        this.sqw = new SyslogQuietWriter(new SyslogWriter(syslogHost), this.syslogFacility, this.errorHandler);
        this.syslogHost = syslogHost;
    }

    public String getSyslogHost() {
        return this.syslogHost;
    }

    public void setFacility(String facilityName) {
        if (facilityName == null) {
            return;
        }
        int facility = getFacility(facilityName);
        this.syslogFacility = facility;
        if (facility == -1) {
            PrintStream printStream = System.err;
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("[");
            stringBuffer.append(facilityName);
            stringBuffer.append("] is an unknown syslog facility. Defaulting to [USER].");
            printStream.println(stringBuffer.toString());
            this.syslogFacility = 8;
        }
        initSyslogFacilityStr();
        SyslogQuietWriter syslogQuietWriter = this.sqw;
        if (syslogQuietWriter != null) {
            syslogQuietWriter.setSyslogFacility(this.syslogFacility);
        }
    }

    public String getFacility() {
        return getFacilityString(this.syslogFacility);
    }

    public void setFacilityPrinting(boolean on) {
        this.facilityPrinting = on;
    }

    public boolean getFacilityPrinting() {
        return this.facilityPrinting;
    }
}
