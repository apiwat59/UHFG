package org.apache.log4j;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.OptionHandler;

/* loaded from: classes.dex */
public abstract class Layout implements OptionHandler {
    public static final String LINE_SEP;
    public static final int LINE_SEP_LEN;

    @Override // org.apache.log4j.spi.OptionHandler
    public abstract void activateOptions();

    public abstract String format(LoggingEvent loggingEvent);

    public abstract boolean ignoresThrowable();

    static {
        String property = System.getProperty("line.separator");
        LINE_SEP = property;
        LINE_SEP_LEN = property.length();
    }

    public String getContentType() {
        return "text/plain";
    }

    public String getHeader() {
        return null;
    }

    public String getFooter() {
        return null;
    }
}
