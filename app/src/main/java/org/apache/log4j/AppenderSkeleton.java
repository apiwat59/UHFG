package org.apache.log4j;

import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OnlyOnceErrorHandler;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.OptionHandler;

/* loaded from: classes.dex */
public abstract class AppenderSkeleton implements Appender, OptionHandler {
    protected Filter headFilter;
    protected Layout layout;
    protected String name;
    protected Filter tailFilter;
    protected Priority threshold;
    protected ErrorHandler errorHandler = new OnlyOnceErrorHandler();
    protected boolean closed = false;

    protected abstract void append(LoggingEvent loggingEvent);

    @Override // org.apache.log4j.Appender
    public abstract void close();

    @Override // org.apache.log4j.Appender
    public abstract boolean requiresLayout();

    @Override // org.apache.log4j.spi.OptionHandler
    public void activateOptions() {
    }

    @Override // org.apache.log4j.Appender
    public void addFilter(Filter newFilter) {
        if (this.headFilter == null) {
            this.tailFilter = newFilter;
            this.headFilter = newFilter;
        } else {
            this.tailFilter.next = newFilter;
            this.tailFilter = newFilter;
        }
    }

    @Override // org.apache.log4j.Appender
    public void clearFilters() {
        this.tailFilter = null;
        this.headFilter = null;
    }

    public void finalize() {
        if (this.closed) {
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Finalizing appender named [");
        stringBuffer.append(this.name);
        stringBuffer.append("].");
        LogLog.debug(stringBuffer.toString());
        close();
    }

    @Override // org.apache.log4j.Appender
    public ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    @Override // org.apache.log4j.Appender
    public Filter getFilter() {
        return this.headFilter;
    }

    public final Filter getFirstFilter() {
        return this.headFilter;
    }

    @Override // org.apache.log4j.Appender
    public Layout getLayout() {
        return this.layout;
    }

    @Override // org.apache.log4j.Appender
    public final String getName() {
        return this.name;
    }

    public Priority getThreshold() {
        return this.threshold;
    }

    public boolean isAsSevereAsThreshold(Priority priority) {
        Priority priority2 = this.threshold;
        return priority2 == null || priority.isGreaterOrEqual(priority2);
    }

    @Override // org.apache.log4j.Appender
    public synchronized void doAppend(LoggingEvent event) {
        if (this.closed) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Attempted to append to closed appender named [");
            stringBuffer.append(this.name);
            stringBuffer.append("].");
            LogLog.error(stringBuffer.toString());
            return;
        }
        if (isAsSevereAsThreshold(event.getLevel())) {
            Filter f = this.headFilter;
            while (f != null) {
                int decide = f.decide(event);
                if (decide == -1) {
                    return;
                }
                if (decide == 0) {
                    f = f.next;
                } else if (decide == 1) {
                    break;
                }
            }
            append(event);
        }
    }

    @Override // org.apache.log4j.Appender
    public synchronized void setErrorHandler(ErrorHandler eh) {
        if (eh == null) {
            LogLog.warn("You have tried to set a null error-handler.");
        } else {
            this.errorHandler = eh;
        }
    }

    @Override // org.apache.log4j.Appender
    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    @Override // org.apache.log4j.Appender
    public void setName(String name) {
        this.name = name;
    }

    public void setThreshold(Priority threshold) {
        this.threshold = threshold;
    }
}
