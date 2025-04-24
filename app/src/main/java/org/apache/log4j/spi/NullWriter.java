package org.apache.log4j.spi;

import java.io.Writer;

/* compiled from: ThrowableInformation.java */
/* loaded from: classes.dex */
class NullWriter extends Writer {
    NullWriter() {
    }

    @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
    }

    @Override // java.io.Writer, java.io.Flushable
    public void flush() {
    }

    @Override // java.io.Writer
    public void write(char[] cbuf, int off, int len) {
    }
}
