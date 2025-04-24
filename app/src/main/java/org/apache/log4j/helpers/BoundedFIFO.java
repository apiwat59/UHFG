package org.apache.log4j.helpers;

import org.apache.log4j.spi.LoggingEvent;

/* loaded from: classes.dex */
public class BoundedFIFO {
    LoggingEvent[] buf;
    int maxSize;
    int numElements = 0;
    int first = 0;
    int next = 0;

    public BoundedFIFO(int maxSize) {
        if (maxSize < 1) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("The maxSize argument (");
            stringBuffer.append(maxSize);
            stringBuffer.append(") is not a positive integer.");
            throw new IllegalArgumentException(stringBuffer.toString());
        }
        this.maxSize = maxSize;
        this.buf = new LoggingEvent[maxSize];
    }

    public LoggingEvent get() {
        int i = this.numElements;
        if (i == 0) {
            return null;
        }
        LoggingEvent[] loggingEventArr = this.buf;
        int i2 = this.first;
        LoggingEvent r = loggingEventArr[i2];
        loggingEventArr[i2] = null;
        int i3 = i2 + 1;
        this.first = i3;
        if (i3 == this.maxSize) {
            this.first = 0;
        }
        this.numElements = i - 1;
        return r;
    }

    public void put(LoggingEvent o) {
        int i = this.numElements;
        int i2 = this.maxSize;
        if (i != i2) {
            LoggingEvent[] loggingEventArr = this.buf;
            int i3 = this.next;
            loggingEventArr[i3] = o;
            int i4 = i3 + 1;
            this.next = i4;
            if (i4 == i2) {
                this.next = 0;
            }
            this.numElements = i + 1;
        }
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public boolean isFull() {
        return this.numElements == this.maxSize;
    }

    public int length() {
        return this.numElements;
    }

    int min(int a, int b) {
        return a < b ? a : b;
    }

    public synchronized void resize(int newSize) {
        int i = this.maxSize;
        if (newSize == i) {
            return;
        }
        LoggingEvent[] tmp = new LoggingEvent[newSize];
        int len1 = min(min(i - this.first, newSize), this.numElements);
        System.arraycopy(this.buf, this.first, tmp, 0, len1);
        int len2 = 0;
        int i2 = this.numElements;
        if (len1 < i2 && len1 < newSize) {
            len2 = min(i2 - len1, newSize - len1);
            System.arraycopy(this.buf, 0, tmp, len1, len2);
        }
        this.buf = tmp;
        this.maxSize = newSize;
        this.first = 0;
        int i3 = len1 + len2;
        this.numElements = i3;
        this.next = i3;
        if (i3 == newSize) {
            this.next = 0;
        }
    }

    public boolean wasEmpty() {
        return this.numElements == 1;
    }

    public boolean wasFull() {
        return this.numElements + 1 == this.maxSize;
    }
}
