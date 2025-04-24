package com.apkfuns.logutils.file;

/* loaded from: classes.dex */
public class LogFileParam {
    private int logLevel;
    private String tagName;
    private String threadName;
    private long time;

    public LogFileParam(long time, int logLevel, String threadName, String tagName) {
        this.time = time;
        this.logLevel = logLevel;
        this.threadName = threadName;
        this.tagName = tagName;
    }

    public long getTime() {
        return this.time;
    }

    public int getLogLevel() {
        return this.logLevel;
    }

    public String getThreadName() {
        return this.threadName;
    }

    public String getTagName() {
        return this.tagName;
    }
}
