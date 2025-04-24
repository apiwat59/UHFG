package org.apache.log4j.spi;

/* loaded from: classes.dex */
public abstract class Filter implements OptionHandler {
    public static final int ACCEPT = 1;
    public static final int DENY = -1;
    public static final int NEUTRAL = 0;
    public Filter next;

    public abstract int decide(LoggingEvent loggingEvent);

    @Override // org.apache.log4j.spi.OptionHandler
    public void activateOptions() {
    }

    public void setNext(Filter next) {
        this.next = next;
    }

    public Filter getNext() {
        return this.next;
    }
}
