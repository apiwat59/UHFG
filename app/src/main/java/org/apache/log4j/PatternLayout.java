package org.apache.log4j;

import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.helpers.PatternParser;
import org.apache.log4j.spi.LoggingEvent;

/* loaded from: classes.dex */
public class PatternLayout extends Layout {
    public static final String DEFAULT_CONVERSION_PATTERN = "%m%n";
    public static final String TTCC_CONVERSION_PATTERN = "%r [%t] %p %c %x - %m%n";
    protected final int BUF_SIZE;
    protected final int MAX_CAPACITY;
    private PatternConverter head;
    private String pattern;
    private StringBuffer sbuf;
    private String timezone;

    public PatternLayout() {
        this(DEFAULT_CONVERSION_PATTERN);
    }

    public PatternLayout(String pattern) {
        this.BUF_SIZE = 256;
        this.MAX_CAPACITY = 1024;
        this.sbuf = new StringBuffer(256);
        this.pattern = pattern;
        this.head = createPatternParser(pattern == null ? DEFAULT_CONVERSION_PATTERN : pattern).parse();
    }

    public void setConversionPattern(String conversionPattern) {
        this.pattern = conversionPattern;
        this.head = createPatternParser(conversionPattern).parse();
    }

    public String getConversionPattern() {
        return this.pattern;
    }

    @Override // org.apache.log4j.Layout, org.apache.log4j.spi.OptionHandler
    public void activateOptions() {
    }

    @Override // org.apache.log4j.Layout
    public boolean ignoresThrowable() {
        return true;
    }

    protected PatternParser createPatternParser(String pattern) {
        return new PatternParser(pattern);
    }

    @Override // org.apache.log4j.Layout
    public String format(LoggingEvent event) {
        if (this.sbuf.capacity() > 1024) {
            this.sbuf = new StringBuffer(256);
        } else {
            this.sbuf.setLength(0);
        }
        for (PatternConverter c = this.head; c != null; c = c.next) {
            c.format(this.sbuf, event);
        }
        return this.sbuf.toString();
    }
}
