package org.apache.log4j.helpers;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import org.apache.log4j.spi.ErrorHandler;

/* loaded from: classes.dex */
public class CountingQuietWriter extends QuietWriter {
    protected long count;

    public CountingQuietWriter(Writer writer, ErrorHandler eh) {
        super(writer, eh);
    }

    @Override // org.apache.log4j.helpers.QuietWriter, java.io.Writer
    public void write(String string) {
        try {
            ((FilterWriter) this).out.write(string);
            this.count += string.length();
        } catch (IOException e) {
            this.errorHandler.error("Write failure.", e, 1);
        }
    }

    public long getCount() {
        return this.count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
