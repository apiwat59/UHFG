package org.apache.log4j.helpers;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import org.apache.log4j.spi.ErrorHandler;

/* loaded from: classes.dex */
public class QuietWriter extends FilterWriter {
    protected ErrorHandler errorHandler;

    public QuietWriter(Writer writer, ErrorHandler errorHandler) {
        super(writer);
        setErrorHandler(errorHandler);
    }

    @Override // java.io.Writer
    public void write(String string) {
        try {
            ((FilterWriter) this).out.write(string);
        } catch (IOException e) {
            ErrorHandler errorHandler = this.errorHandler;
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Failed to write [");
            stringBuffer.append(string);
            stringBuffer.append("].");
            errorHandler.error(stringBuffer.toString(), e, 1);
        }
    }

    @Override // java.io.FilterWriter, java.io.Writer, java.io.Flushable
    public void flush() {
        try {
            ((FilterWriter) this).out.flush();
        } catch (IOException e) {
            this.errorHandler.error("Failed to flush writer,", e, 2);
        }
    }

    public void setErrorHandler(ErrorHandler eh) {
        if (eh == null) {
            throw new IllegalArgumentException("Attempted to set null ErrorHandler.");
        }
        this.errorHandler = eh;
    }
}
