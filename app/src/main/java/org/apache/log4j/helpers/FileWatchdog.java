package org.apache.log4j.helpers;

import java.io.File;

/* loaded from: classes.dex */
public abstract class FileWatchdog extends Thread {
    public static final long DEFAULT_DELAY = 60000;
    File file;
    protected String filename;
    protected long delay = DEFAULT_DELAY;
    long lastModif = 0;
    boolean warnedAlready = false;
    boolean interrupted = false;

    protected abstract void doOnChange();

    protected FileWatchdog(String filename) {
        this.filename = filename;
        this.file = new File(filename);
        setDaemon(true);
        checkAndConfigure();
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    protected void checkAndConfigure() {
        try {
            boolean fileExists = this.file.exists();
            if (fileExists) {
                long l = this.file.lastModified();
                if (l > this.lastModif) {
                    this.lastModif = l;
                    doOnChange();
                    this.warnedAlready = false;
                    return;
                }
                return;
            }
            if (!this.warnedAlready) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("[");
                stringBuffer.append(this.filename);
                stringBuffer.append("] does not exist.");
                LogLog.debug(stringBuffer.toString());
                this.warnedAlready = true;
            }
        } catch (SecurityException e) {
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2.append("Was not allowed to read check file existance, file:[");
            stringBuffer2.append(this.filename);
            stringBuffer2.append("].");
            LogLog.warn(stringBuffer2.toString());
            this.interrupted = true;
        }
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        while (!this.interrupted) {
            try {
                Thread.currentThread();
                Thread.sleep(this.delay);
            } catch (InterruptedException e) {
            }
            checkAndConfigure();
        }
    }
}
