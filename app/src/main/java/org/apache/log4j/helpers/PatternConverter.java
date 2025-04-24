package org.apache.log4j.helpers;

import org.apache.log4j.spi.LoggingEvent;

/* loaded from: classes.dex */
public abstract class PatternConverter {
    static String[] SPACES = {" ", "  ", "    ", "        ", "                ", "                                "};
    boolean leftAlign;
    int max;
    int min;
    public PatternConverter next;

    protected abstract String convert(LoggingEvent loggingEvent);

    protected PatternConverter() {
        this.min = -1;
        this.max = Integer.MAX_VALUE;
        this.leftAlign = false;
    }

    protected PatternConverter(FormattingInfo fi) {
        this.min = -1;
        this.max = Integer.MAX_VALUE;
        this.leftAlign = false;
        this.min = fi.min;
        this.max = fi.max;
        this.leftAlign = fi.leftAlign;
    }

    public void format(StringBuffer sbuf, LoggingEvent e) {
        String s = convert(e);
        if (s == null) {
            int i = this.min;
            if (i > 0) {
                spacePad(sbuf, i);
                return;
            }
            return;
        }
        int len = s.length();
        int i2 = this.max;
        if (len > i2) {
            sbuf.append(s.substring(len - i2));
            return;
        }
        int i3 = this.min;
        if (len < i3) {
            if (this.leftAlign) {
                sbuf.append(s);
                spacePad(sbuf, this.min - len);
                return;
            } else {
                spacePad(sbuf, i3 - len);
                sbuf.append(s);
                return;
            }
        }
        sbuf.append(s);
    }

    public void spacePad(StringBuffer sbuf, int length) {
        while (length >= 32) {
            sbuf.append(SPACES[5]);
            length -= 32;
        }
        for (int i = 4; i >= 0; i--) {
            if (((1 << i) & length) != 0) {
                sbuf.append(SPACES[i]);
            }
        }
    }
}
