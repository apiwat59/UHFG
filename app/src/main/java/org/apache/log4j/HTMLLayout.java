package org.apache.log4j;

import java.util.Date;
import org.apache.log4j.helpers.Transform;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

/* loaded from: classes.dex */
public class HTMLLayout extends Layout {
    public static final String LOCATION_INFO_OPTION = "LocationInfo";
    public static final String TITLE_OPTION = "Title";
    static String TRACE_PREFIX = "<br>&nbsp;&nbsp;&nbsp;&nbsp;";
    protected final int BUF_SIZE = 256;
    protected final int MAX_CAPACITY = 1024;
    private StringBuffer sbuf = new StringBuffer(256);
    boolean locationInfo = false;
    String title = "Log4J Log Messages";

    public void setLocationInfo(boolean flag) {
        this.locationInfo = flag;
    }

    public boolean getLocationInfo() {
        return this.locationInfo;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    @Override // org.apache.log4j.Layout
    public String getContentType() {
        return "text/html";
    }

    @Override // org.apache.log4j.Layout, org.apache.log4j.spi.OptionHandler
    public void activateOptions() {
    }

    @Override // org.apache.log4j.Layout
    public String format(LoggingEvent event) {
        if (this.sbuf.capacity() > 1024) {
            this.sbuf = new StringBuffer(256);
        } else {
            this.sbuf.setLength(0);
        }
        StringBuffer stringBuffer = this.sbuf;
        StringBuffer stringBuffer2 = new StringBuffer();
        stringBuffer2.append(Layout.LINE_SEP);
        stringBuffer2.append("<tr>");
        stringBuffer2.append(Layout.LINE_SEP);
        stringBuffer.append(stringBuffer2.toString());
        this.sbuf.append("<td>");
        this.sbuf.append(event.timeStamp - LoggingEvent.getStartTime());
        StringBuffer stringBuffer3 = this.sbuf;
        StringBuffer stringBuffer4 = new StringBuffer();
        stringBuffer4.append("</td>");
        stringBuffer4.append(Layout.LINE_SEP);
        stringBuffer3.append(stringBuffer4.toString());
        StringBuffer stringBuffer5 = this.sbuf;
        StringBuffer stringBuffer6 = new StringBuffer();
        stringBuffer6.append("<td title=\"");
        stringBuffer6.append(event.getThreadName());
        stringBuffer6.append(" thread\">");
        stringBuffer5.append(stringBuffer6.toString());
        this.sbuf.append(Transform.escapeTags(event.getThreadName()));
        StringBuffer stringBuffer7 = this.sbuf;
        StringBuffer stringBuffer8 = new StringBuffer();
        stringBuffer8.append("</td>");
        stringBuffer8.append(Layout.LINE_SEP);
        stringBuffer7.append(stringBuffer8.toString());
        this.sbuf.append("<td title=\"Level\">");
        if (event.getLevel().equals(Level.DEBUG)) {
            this.sbuf.append("<font color=\"#339933\">");
            this.sbuf.append(event.getLevel());
            this.sbuf.append("</font>");
        } else if (event.getLevel().isGreaterOrEqual(Level.WARN)) {
            this.sbuf.append("<font color=\"#993300\"><strong>");
            this.sbuf.append(event.getLevel());
            this.sbuf.append("</strong></font>");
        } else {
            this.sbuf.append(event.getLevel());
        }
        StringBuffer stringBuffer9 = this.sbuf;
        StringBuffer stringBuffer10 = new StringBuffer();
        stringBuffer10.append("</td>");
        stringBuffer10.append(Layout.LINE_SEP);
        stringBuffer9.append(stringBuffer10.toString());
        StringBuffer stringBuffer11 = this.sbuf;
        StringBuffer stringBuffer12 = new StringBuffer();
        stringBuffer12.append("<td title=\"");
        stringBuffer12.append(event.getLoggerName());
        stringBuffer12.append(" category\">");
        stringBuffer11.append(stringBuffer12.toString());
        this.sbuf.append(Transform.escapeTags(event.getLoggerName()));
        StringBuffer stringBuffer13 = this.sbuf;
        StringBuffer stringBuffer14 = new StringBuffer();
        stringBuffer14.append("</td>");
        stringBuffer14.append(Layout.LINE_SEP);
        stringBuffer13.append(stringBuffer14.toString());
        if (this.locationInfo) {
            LocationInfo locInfo = event.getLocationInformation();
            this.sbuf.append("<td>");
            this.sbuf.append(Transform.escapeTags(locInfo.getFileName()));
            this.sbuf.append(':');
            this.sbuf.append(locInfo.getLineNumber());
            StringBuffer stringBuffer15 = this.sbuf;
            StringBuffer stringBuffer16 = new StringBuffer();
            stringBuffer16.append("</td>");
            stringBuffer16.append(Layout.LINE_SEP);
            stringBuffer15.append(stringBuffer16.toString());
        }
        this.sbuf.append("<td title=\"Message\">");
        this.sbuf.append(Transform.escapeTags(event.getRenderedMessage()));
        StringBuffer stringBuffer17 = this.sbuf;
        StringBuffer stringBuffer18 = new StringBuffer();
        stringBuffer18.append("</td>");
        stringBuffer18.append(Layout.LINE_SEP);
        stringBuffer17.append(stringBuffer18.toString());
        StringBuffer stringBuffer19 = this.sbuf;
        StringBuffer stringBuffer20 = new StringBuffer();
        stringBuffer20.append("</tr>");
        stringBuffer20.append(Layout.LINE_SEP);
        stringBuffer19.append(stringBuffer20.toString());
        if (event.getNDC() != null) {
            this.sbuf.append("<tr><td bgcolor=\"#EEEEEE\" style=\"font-size : xx-small;\" colspan=\"6\" title=\"Nested Diagnostic Context\">");
            StringBuffer stringBuffer21 = this.sbuf;
            StringBuffer stringBuffer22 = new StringBuffer();
            stringBuffer22.append("NDC: ");
            stringBuffer22.append(Transform.escapeTags(event.getNDC()));
            stringBuffer21.append(stringBuffer22.toString());
            StringBuffer stringBuffer23 = this.sbuf;
            StringBuffer stringBuffer24 = new StringBuffer();
            stringBuffer24.append("</td></tr>");
            stringBuffer24.append(Layout.LINE_SEP);
            stringBuffer23.append(stringBuffer24.toString());
        }
        String[] s = event.getThrowableStrRep();
        if (s != null) {
            this.sbuf.append("<tr><td bgcolor=\"#993300\" style=\"color:White; font-size : xx-small;\" colspan=\"6\">");
            appendThrowableAsHTML(s, this.sbuf);
            StringBuffer stringBuffer25 = this.sbuf;
            StringBuffer stringBuffer26 = new StringBuffer();
            stringBuffer26.append("</td></tr>");
            stringBuffer26.append(Layout.LINE_SEP);
            stringBuffer25.append(stringBuffer26.toString());
        }
        return this.sbuf.toString();
    }

    void appendThrowableAsHTML(String[] s, StringBuffer sbuf) {
        int len;
        if (s == null || (len = s.length) == 0) {
            return;
        }
        sbuf.append(Transform.escapeTags(s[0]));
        sbuf.append(Layout.LINE_SEP);
        for (int i = 1; i < len; i++) {
            sbuf.append(TRACE_PREFIX);
            sbuf.append(Transform.escapeTags(s[i]));
            sbuf.append(Layout.LINE_SEP);
        }
    }

    @Override // org.apache.log4j.Layout
    public String getHeader() {
        StringBuffer sbuf = new StringBuffer();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        stringBuffer.append(Layout.LINE_SEP);
        sbuf.append(stringBuffer.toString());
        StringBuffer stringBuffer2 = new StringBuffer();
        stringBuffer2.append("<html>");
        stringBuffer2.append(Layout.LINE_SEP);
        sbuf.append(stringBuffer2.toString());
        StringBuffer stringBuffer3 = new StringBuffer();
        stringBuffer3.append("<head>");
        stringBuffer3.append(Layout.LINE_SEP);
        sbuf.append(stringBuffer3.toString());
        StringBuffer stringBuffer4 = new StringBuffer();
        stringBuffer4.append("<title>");
        stringBuffer4.append(this.title);
        stringBuffer4.append("</title>");
        stringBuffer4.append(Layout.LINE_SEP);
        sbuf.append(stringBuffer4.toString());
        StringBuffer stringBuffer5 = new StringBuffer();
        stringBuffer5.append("<style type=\"text/css\">");
        stringBuffer5.append(Layout.LINE_SEP);
        sbuf.append(stringBuffer5.toString());
        StringBuffer stringBuffer6 = new StringBuffer();
        stringBuffer6.append("<!--");
        stringBuffer6.append(Layout.LINE_SEP);
        sbuf.append(stringBuffer6.toString());
        StringBuffer stringBuffer7 = new StringBuffer();
        stringBuffer7.append("body, table {font-family: arial,sans-serif; font-size: x-small;}");
        stringBuffer7.append(Layout.LINE_SEP);
        sbuf.append(stringBuffer7.toString());
        StringBuffer stringBuffer8 = new StringBuffer();
        stringBuffer8.append("th {background: #336699; color: #FFFFFF; text-align: left;}");
        stringBuffer8.append(Layout.LINE_SEP);
        sbuf.append(stringBuffer8.toString());
        StringBuffer stringBuffer9 = new StringBuffer();
        stringBuffer9.append("-->");
        stringBuffer9.append(Layout.LINE_SEP);
        sbuf.append(stringBuffer9.toString());
        StringBuffer stringBuffer10 = new StringBuffer();
        stringBuffer10.append("</style>");
        stringBuffer10.append(Layout.LINE_SEP);
        sbuf.append(stringBuffer10.toString());
        StringBuffer stringBuffer11 = new StringBuffer();
        stringBuffer11.append("</head>");
        stringBuffer11.append(Layout.LINE_SEP);
        sbuf.append(stringBuffer11.toString());
        StringBuffer stringBuffer12 = new StringBuffer();
        stringBuffer12.append("<body bgcolor=\"#FFFFFF\" topmargin=\"6\" leftmargin=\"6\">");
        stringBuffer12.append(Layout.LINE_SEP);
        sbuf.append(stringBuffer12.toString());
        StringBuffer stringBuffer13 = new StringBuffer();
        stringBuffer13.append("<hr size=\"1\" noshade>");
        stringBuffer13.append(Layout.LINE_SEP);
        sbuf.append(stringBuffer13.toString());
        StringBuffer stringBuffer14 = new StringBuffer();
        stringBuffer14.append("Log session start time ");
        stringBuffer14.append(new Date());
        stringBuffer14.append("<br>");
        stringBuffer14.append(Layout.LINE_SEP);
        sbuf.append(stringBuffer14.toString());
        StringBuffer stringBuffer15 = new StringBuffer();
        stringBuffer15.append("<br>");
        stringBuffer15.append(Layout.LINE_SEP);
        sbuf.append(stringBuffer15.toString());
        StringBuffer stringBuffer16 = new StringBuffer();
        stringBuffer16.append("<table cellspacing=\"0\" cellpadding=\"4\" border=\"1\" bordercolor=\"#224466\" width=\"100%\">");
        stringBuffer16.append(Layout.LINE_SEP);
        sbuf.append(stringBuffer16.toString());
        StringBuffer stringBuffer17 = new StringBuffer();
        stringBuffer17.append("<tr>");
        stringBuffer17.append(Layout.LINE_SEP);
        sbuf.append(stringBuffer17.toString());
        StringBuffer stringBuffer18 = new StringBuffer();
        stringBuffer18.append("<th>Time</th>");
        stringBuffer18.append(Layout.LINE_SEP);
        sbuf.append(stringBuffer18.toString());
        StringBuffer stringBuffer19 = new StringBuffer();
        stringBuffer19.append("<th>Thread</th>");
        stringBuffer19.append(Layout.LINE_SEP);
        sbuf.append(stringBuffer19.toString());
        StringBuffer stringBuffer20 = new StringBuffer();
        stringBuffer20.append("<th>Level</th>");
        stringBuffer20.append(Layout.LINE_SEP);
        sbuf.append(stringBuffer20.toString());
        StringBuffer stringBuffer21 = new StringBuffer();
        stringBuffer21.append("<th>Category</th>");
        stringBuffer21.append(Layout.LINE_SEP);
        sbuf.append(stringBuffer21.toString());
        if (this.locationInfo) {
            StringBuffer stringBuffer22 = new StringBuffer();
            stringBuffer22.append("<th>File:Line</th>");
            stringBuffer22.append(Layout.LINE_SEP);
            sbuf.append(stringBuffer22.toString());
        }
        StringBuffer stringBuffer23 = new StringBuffer();
        stringBuffer23.append("<th>Message</th>");
        stringBuffer23.append(Layout.LINE_SEP);
        sbuf.append(stringBuffer23.toString());
        StringBuffer stringBuffer24 = new StringBuffer();
        stringBuffer24.append("</tr>");
        stringBuffer24.append(Layout.LINE_SEP);
        sbuf.append(stringBuffer24.toString());
        return sbuf.toString();
    }

    @Override // org.apache.log4j.Layout
    public String getFooter() {
        StringBuffer sbuf = new StringBuffer();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("</table>");
        stringBuffer.append(Layout.LINE_SEP);
        sbuf.append(stringBuffer.toString());
        StringBuffer stringBuffer2 = new StringBuffer();
        stringBuffer2.append("<br>");
        stringBuffer2.append(Layout.LINE_SEP);
        sbuf.append(stringBuffer2.toString());
        sbuf.append("</body></html>");
        return sbuf.toString();
    }

    @Override // org.apache.log4j.Layout
    public boolean ignoresThrowable() {
        return false;
    }
}
