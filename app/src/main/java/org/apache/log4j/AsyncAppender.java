package org.apache.log4j;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.helpers.AppenderAttachableImpl;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.LoggingEvent;

/* loaded from: classes.dex */
public class AsyncAppender extends AppenderSkeleton implements AppenderAttachable {
    public static final int DEFAULT_BUFFER_SIZE = 128;
    AppenderAttachableImpl aai;
    private final AppenderAttachableImpl appenders;
    private boolean blocking;
    private final List buffer;
    private int bufferSize;
    private final Map discardMap;
    private final Thread dispatcher;
    private boolean locationInfo;

    public AsyncAppender() {
        ArrayList arrayList = new ArrayList();
        this.buffer = arrayList;
        HashMap hashMap = new HashMap();
        this.discardMap = hashMap;
        this.bufferSize = 128;
        this.locationInfo = false;
        this.blocking = true;
        AppenderAttachableImpl appenderAttachableImpl = new AppenderAttachableImpl();
        this.appenders = appenderAttachableImpl;
        this.aai = appenderAttachableImpl;
        Thread thread = new Thread(new Dispatcher(this, arrayList, hashMap, appenderAttachableImpl));
        this.dispatcher = thread;
        thread.setDaemon(true);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Dispatcher-");
        stringBuffer.append(thread.getName());
        thread.setName(stringBuffer.toString());
        thread.start();
    }

    @Override // org.apache.log4j.spi.AppenderAttachable
    public void addAppender(Appender newAppender) {
        synchronized (this.appenders) {
            this.appenders.addAppender(newAppender);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:27:0x005f, code lost:
    
        r3 = r7.getLoggerName();
        r4 = (org.apache.log4j.AsyncAppender.DiscardSummary) r6.discardMap.get(r3);
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x006b, code lost:
    
        if (r4 != null) goto L34;
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x006d, code lost:
    
        r6.discardMap.put(r3, new org.apache.log4j.AsyncAppender.DiscardSummary(r7));
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x0079, code lost:
    
        r4.add(r7);
     */
    @Override // org.apache.log4j.AppenderSkeleton
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void append(org.apache.log4j.spi.LoggingEvent r7) {
        /*
            r6 = this;
            java.lang.Thread r0 = r6.dispatcher
            if (r0 == 0) goto L83
            boolean r0 = r0.isAlive()
            if (r0 == 0) goto L83
            int r0 = r6.bufferSize
            if (r0 > 0) goto L10
            goto L83
        L10:
            r7.getNDC()
            r7.getThreadName()
            r7.getMDCCopy()
            boolean r0 = r6.locationInfo
            if (r0 == 0) goto L20
            r7.getLocationInformation()
        L20:
            java.util.List r0 = r6.buffer
            monitor-enter(r0)
        L23:
            java.util.List r1 = r6.buffer     // Catch: java.lang.Throwable -> L80
            int r1 = r1.size()     // Catch: java.lang.Throwable -> L80
            int r2 = r6.bufferSize     // Catch: java.lang.Throwable -> L80
            if (r1 >= r2) goto L3b
            java.util.List r2 = r6.buffer     // Catch: java.lang.Throwable -> L80
            r2.add(r7)     // Catch: java.lang.Throwable -> L80
            if (r1 != 0) goto L7d
            java.util.List r2 = r6.buffer     // Catch: java.lang.Throwable -> L80
            r2.notifyAll()     // Catch: java.lang.Throwable -> L80
            goto L7d
        L3b:
            r2 = 1
            boolean r3 = r6.blocking     // Catch: java.lang.Throwable -> L80
            if (r3 == 0) goto L5d
            boolean r3 = java.lang.Thread.interrupted()     // Catch: java.lang.Throwable -> L80
            if (r3 != 0) goto L5d
            java.lang.Thread r3 = java.lang.Thread.currentThread()     // Catch: java.lang.Throwable -> L80
            java.lang.Thread r4 = r6.dispatcher     // Catch: java.lang.Throwable -> L80
            if (r3 == r4) goto L5d
            java.util.List r3 = r6.buffer     // Catch: java.lang.InterruptedException -> L55 java.lang.Throwable -> L80
            r3.wait()     // Catch: java.lang.InterruptedException -> L55 java.lang.Throwable -> L80
            r2 = 0
            goto L5d
        L55:
            r3 = move-exception
            java.lang.Thread r4 = java.lang.Thread.currentThread()     // Catch: java.lang.Throwable -> L80
            r4.interrupt()     // Catch: java.lang.Throwable -> L80
        L5d:
            if (r2 == 0) goto L7f
            java.lang.String r3 = r7.getLoggerName()     // Catch: java.lang.Throwable -> L80
            java.util.Map r4 = r6.discardMap     // Catch: java.lang.Throwable -> L80
            java.lang.Object r4 = r4.get(r3)     // Catch: java.lang.Throwable -> L80
            org.apache.log4j.AsyncAppender$DiscardSummary r4 = (org.apache.log4j.AsyncAppender.DiscardSummary) r4     // Catch: java.lang.Throwable -> L80
            if (r4 != 0) goto L79
            org.apache.log4j.AsyncAppender$DiscardSummary r5 = new org.apache.log4j.AsyncAppender$DiscardSummary     // Catch: java.lang.Throwable -> L80
            r5.<init>(r7)     // Catch: java.lang.Throwable -> L80
            r4 = r5
            java.util.Map r5 = r6.discardMap     // Catch: java.lang.Throwable -> L80
            r5.put(r3, r4)     // Catch: java.lang.Throwable -> L80
            goto L7d
        L79:
            r4.add(r7)     // Catch: java.lang.Throwable -> L80
        L7d:
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L80
            return
        L7f:
            goto L23
        L80:
            r1 = move-exception
            monitor-exit(r0)
            throw r1
        L83:
            org.apache.log4j.helpers.AppenderAttachableImpl r0 = r6.appenders
            monitor-enter(r0)
            org.apache.log4j.helpers.AppenderAttachableImpl r1 = r6.appenders     // Catch: java.lang.Throwable -> L8d
            r1.appendLoopOnAppenders(r7)     // Catch: java.lang.Throwable -> L8d
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L8d
            return
        L8d:
            r1 = move-exception
            monitor-exit(r0)
            goto L91
        L90:
            throw r1
        L91:
            goto L90
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.log4j.AsyncAppender.append(org.apache.log4j.spi.LoggingEvent):void");
    }

    @Override // org.apache.log4j.AppenderSkeleton, org.apache.log4j.Appender
    public void close() {
        synchronized (this.buffer) {
            this.closed = true;
            this.buffer.notifyAll();
        }
        try {
            this.dispatcher.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LogLog.error("Got an InterruptedException while waiting for the dispatcher to finish.", e);
        }
        synchronized (this.appenders) {
            Enumeration iter = this.appenders.getAllAppenders();
            if (iter != null) {
                while (iter.hasMoreElements()) {
                    Object next = iter.nextElement();
                    if (next instanceof Appender) {
                        ((Appender) next).close();
                    }
                }
            }
        }
    }

    @Override // org.apache.log4j.spi.AppenderAttachable
    public Enumeration getAllAppenders() {
        Enumeration allAppenders;
        synchronized (this.appenders) {
            allAppenders = this.appenders.getAllAppenders();
        }
        return allAppenders;
    }

    @Override // org.apache.log4j.spi.AppenderAttachable
    public Appender getAppender(String name) {
        Appender appender;
        synchronized (this.appenders) {
            appender = this.appenders.getAppender(name);
        }
        return appender;
    }

    public boolean getLocationInfo() {
        return this.locationInfo;
    }

    @Override // org.apache.log4j.spi.AppenderAttachable
    public boolean isAttached(Appender appender) {
        boolean isAttached;
        synchronized (this.appenders) {
            isAttached = this.appenders.isAttached(appender);
        }
        return isAttached;
    }

    @Override // org.apache.log4j.AppenderSkeleton, org.apache.log4j.Appender
    public boolean requiresLayout() {
        return false;
    }

    @Override // org.apache.log4j.spi.AppenderAttachable
    public void removeAllAppenders() {
        synchronized (this.appenders) {
            this.appenders.removeAllAppenders();
        }
    }

    @Override // org.apache.log4j.spi.AppenderAttachable
    public void removeAppender(Appender appender) {
        synchronized (this.appenders) {
            this.appenders.removeAppender(appender);
        }
    }

    @Override // org.apache.log4j.spi.AppenderAttachable
    public void removeAppender(String name) {
        synchronized (this.appenders) {
            this.appenders.removeAppender(name);
        }
    }

    public void setLocationInfo(boolean flag) {
        this.locationInfo = flag;
    }

    public void setBufferSize(int size) {
        if (size < 0) {
            throw new NegativeArraySizeException("size");
        }
        synchronized (this.buffer) {
            this.bufferSize = size >= 1 ? size : 1;
            this.buffer.notifyAll();
        }
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    public void setBlocking(boolean value) {
        synchronized (this.buffer) {
            this.blocking = value;
            this.buffer.notifyAll();
        }
    }

    public boolean getBlocking() {
        return this.blocking;
    }

    private static final class DiscardSummary {
        private int count = 1;
        private LoggingEvent maxEvent;

        public DiscardSummary(LoggingEvent event) {
            this.maxEvent = event;
        }

        public void add(LoggingEvent event) {
            if (event.getLevel().toInt() > this.maxEvent.getLevel().toInt()) {
                this.maxEvent = event;
            }
            this.count++;
        }

        public LoggingEvent createEvent() {
            String msg = MessageFormat.format("Discarded {0} messages due to full event buffer including: {1}", new Integer(this.count), this.maxEvent.getMessage());
            return new LoggingEvent(null, Logger.getLogger(this.maxEvent.getLoggerName()), this.maxEvent.getLevel(), msg, null);
        }
    }

    private static class Dispatcher implements Runnable {
        private final AppenderAttachableImpl appenders;
        private final List buffer;
        private final Map discardMap;
        private final AsyncAppender parent;

        public Dispatcher(AsyncAppender parent, List buffer, Map discardMap, AppenderAttachableImpl appenders) {
            this.parent = parent;
            this.buffer = buffer;
            this.appenders = appenders;
            this.discardMap = discardMap;
        }

        @Override // java.lang.Runnable
        public void run() {
            boolean isActive = true;
            while (isActive) {
                LoggingEvent[] events = null;
                try {
                    synchronized (this.buffer) {
                        int bufferSize = this.buffer.size();
                        isActive = !this.parent.closed;
                        while (bufferSize == 0 && isActive) {
                            this.buffer.wait();
                            bufferSize = this.buffer.size();
                            isActive = !this.parent.closed;
                        }
                        if (bufferSize > 0) {
                            events = new LoggingEvent[this.discardMap.size() + bufferSize];
                            this.buffer.toArray(events);
                            int index = bufferSize;
                            Iterator iter = this.discardMap.values().iterator();
                            while (iter.hasNext()) {
                                events[index] = ((DiscardSummary) iter.next()).createEvent();
                                index++;
                            }
                            this.buffer.clear();
                            this.discardMap.clear();
                            this.buffer.notifyAll();
                        }
                    }
                    if (events != null) {
                        for (LoggingEvent loggingEvent : events) {
                            synchronized (this.appenders) {
                                this.appenders.appendLoopOnAppenders(loggingEvent);
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }
}
