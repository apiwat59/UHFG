package org.apache.log4j.helpers;

import org.apache.log4j.spi.LoggingEvent;

/* loaded from: classes.dex */
public class CyclicBuffer {
    LoggingEvent[] ea;
    int first;
    int last;
    int maxSize;
    int numElems;

    public CyclicBuffer(int maxSize) throws IllegalArgumentException {
        if (maxSize < 1) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("The maxSize argument (");
            stringBuffer.append(maxSize);
            stringBuffer.append(") is not a positive integer.");
            throw new IllegalArgumentException(stringBuffer.toString());
        }
        this.maxSize = maxSize;
        this.ea = new LoggingEvent[maxSize];
        this.first = 0;
        this.last = 0;
        this.numElems = 0;
    }

    public void add(LoggingEvent event) {
        LoggingEvent[] loggingEventArr = this.ea;
        int i = this.last;
        loggingEventArr[i] = event;
        int i2 = i + 1;
        this.last = i2;
        int i3 = this.maxSize;
        if (i2 == i3) {
            this.last = 0;
        }
        int i4 = this.numElems;
        if (i4 < i3) {
            this.numElems = i4 + 1;
            return;
        }
        int i5 = this.first + 1;
        this.first = i5;
        if (i5 == i3) {
            this.first = 0;
        }
    }

    public LoggingEvent get(int i) {
        if (i < 0 || i >= this.numElems) {
            return null;
        }
        return this.ea[(this.first + i) % this.maxSize];
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public LoggingEvent get() {
        LoggingEvent r = null;
        int i = this.numElems;
        if (i > 0) {
            this.numElems = i - 1;
            LoggingEvent[] loggingEventArr = this.ea;
            int i2 = this.first;
            r = loggingEventArr[i2];
            loggingEventArr[i2] = null;
            int i3 = i2 + 1;
            this.first = i3;
            if (i3 == this.maxSize) {
                this.first = 0;
            }
        }
        return r;
    }

    public int length() {
        return this.numElems;
    }

    public void resize(int newSize) {
        if (newSize < 0) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Negative array size [");
            stringBuffer.append(newSize);
            stringBuffer.append("] not allowed.");
            throw new IllegalArgumentException(stringBuffer.toString());
        }
        int loopLen = this.numElems;
        if (newSize == loopLen) {
            return;
        }
        LoggingEvent[] temp = new LoggingEvent[newSize];
        if (newSize < loopLen) {
            loopLen = newSize;
        }
        for (int i = 0; i < loopLen; i++) {
            LoggingEvent[] loggingEventArr = this.ea;
            int i2 = this.first;
            temp[i] = loggingEventArr[i2];
            loggingEventArr[i2] = null;
            int i3 = i2 + 1;
            this.first = i3;
            if (i3 == this.numElems) {
                this.first = 0;
            }
        }
        this.ea = temp;
        this.first = 0;
        this.numElems = loopLen;
        this.maxSize = newSize;
        if (loopLen == newSize) {
            this.last = 0;
        } else {
            this.last = loopLen;
        }
    }
}
