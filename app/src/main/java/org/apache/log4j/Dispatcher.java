package org.apache.log4j;

import org.apache.log4j.helpers.AppenderAttachableImpl;
import org.apache.log4j.helpers.BoundedFIFO;

/* loaded from: classes.dex */
class Dispatcher extends Thread {
    private AppenderAttachableImpl aai;
    private BoundedFIFO bf;
    AsyncAppender container;
    private boolean interrupted = false;

    Dispatcher(BoundedFIFO bf, AsyncAppender container) {
        this.bf = bf;
        this.container = container;
        this.aai = container.aai;
        setDaemon(true);
        setPriority(1);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Dispatcher-");
        stringBuffer.append(getName());
        setName(stringBuffer.toString());
    }

    void close() {
        synchronized (this.bf) {
            this.interrupted = true;
            if (this.bf.length() == 0) {
                this.bf.notify();
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:25:0x0034, code lost:
    
        r1 = r3.container.aai;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0038, code lost:
    
        monitor-enter(r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0039, code lost:
    
        r2 = r3.aai;
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x003b, code lost:
    
        if (r2 == null) goto L49;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x003d, code lost:
    
        if (r0 == null) goto L50;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x003f, code lost:
    
        r2.appendLoopOnAppenders(r0);
     */
    @Override // java.lang.Thread, java.lang.Runnable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void run() {
        /*
            r3 = this;
            r0 = 0
        L1:
            org.apache.log4j.helpers.BoundedFIFO r1 = r3.bf
            monitor-enter(r1)
            org.apache.log4j.helpers.BoundedFIFO r2 = r3.bf     // Catch: java.lang.Throwable -> L49
            int r2 = r2.length()     // Catch: java.lang.Throwable -> L49
            if (r2 != 0) goto L20
            boolean r2 = r3.interrupted     // Catch: java.lang.Throwable -> L49
            if (r2 == 0) goto L12
            monitor-exit(r1)     // Catch: java.lang.Throwable -> L49
            goto L1a
        L12:
            org.apache.log4j.helpers.BoundedFIFO r2 = r3.bf     // Catch: java.lang.InterruptedException -> L18 java.lang.Throwable -> L49
            r2.wait()     // Catch: java.lang.InterruptedException -> L18 java.lang.Throwable -> L49
            goto L20
        L18:
            r2 = move-exception
            monitor-exit(r1)     // Catch: java.lang.Throwable -> L49
        L1a:
            org.apache.log4j.helpers.AppenderAttachableImpl r1 = r3.aai
            r1.removeAllAppenders()
            return
        L20:
            org.apache.log4j.helpers.BoundedFIFO r2 = r3.bf     // Catch: java.lang.Throwable -> L49
            org.apache.log4j.spi.LoggingEvent r0 = r2.get()     // Catch: java.lang.Throwable -> L49
            org.apache.log4j.helpers.BoundedFIFO r2 = r3.bf     // Catch: java.lang.Throwable -> L47
            boolean r2 = r2.wasFull()     // Catch: java.lang.Throwable -> L47
            if (r2 == 0) goto L33
            org.apache.log4j.helpers.BoundedFIFO r2 = r3.bf     // Catch: java.lang.Throwable -> L47
            r2.notify()     // Catch: java.lang.Throwable -> L47
        L33:
            monitor-exit(r1)     // Catch: java.lang.Throwable -> L47
            org.apache.log4j.AsyncAppender r1 = r3.container
            org.apache.log4j.helpers.AppenderAttachableImpl r1 = r1.aai
            monitor-enter(r1)
            org.apache.log4j.helpers.AppenderAttachableImpl r2 = r3.aai     // Catch: java.lang.Throwable -> L44
            if (r2 == 0) goto L42
            if (r0 == 0) goto L42
            r2.appendLoopOnAppenders(r0)     // Catch: java.lang.Throwable -> L44
        L42:
            monitor-exit(r1)     // Catch: java.lang.Throwable -> L44
            goto L1
        L44:
            r2 = move-exception
            monitor-exit(r1)
            throw r2
        L47:
            r2 = move-exception
            goto L4a
        L49:
            r2 = move-exception
        L4a:
            monitor-exit(r1)
            goto L4d
        L4c:
            throw r2
        L4d:
            goto L4c
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.log4j.Dispatcher.run():void");
    }
}
