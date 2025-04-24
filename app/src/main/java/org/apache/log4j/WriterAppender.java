package org.apache.log4j;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.QuietWriter;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.LoggingEvent;

/* loaded from: classes.dex */
public class WriterAppender extends AppenderSkeleton {
    protected String encoding;
    protected boolean immediateFlush;
    protected QuietWriter qw;

    public WriterAppender() {
        this.immediateFlush = true;
    }

    public WriterAppender(Layout layout, OutputStream os) {
        this(layout, new OutputStreamWriter(os));
    }

    public WriterAppender(Layout layout, Writer writer) {
        this.immediateFlush = true;
        this.layout = layout;
        setWriter(writer);
    }

    public void setImmediateFlush(boolean value) {
        this.immediateFlush = value;
    }

    public boolean getImmediateFlush() {
        return this.immediateFlush;
    }

    @Override // org.apache.log4j.AppenderSkeleton, org.apache.log4j.spi.OptionHandler
    public void activateOptions() {
    }

    @Override // org.apache.log4j.AppenderSkeleton
    public void append(LoggingEvent event) {
        if (!checkEntryConditions()) {
            return;
        }
        subAppend(event);
    }

    protected boolean checkEntryConditions() {
        if (this.closed) {
            LogLog.warn("Not allowed to write to a closed appender.");
            return false;
        }
        if (this.qw == null) {
            ErrorHandler errorHandler = this.errorHandler;
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("No output stream or file set for the appender named [");
            stringBuffer.append(this.name);
            stringBuffer.append("].");
            errorHandler.error(stringBuffer.toString());
            return false;
        }
        if (this.layout == null) {
            ErrorHandler errorHandler2 = this.errorHandler;
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2.append("No layout set for the appender named [");
            stringBuffer2.append(this.name);
            stringBuffer2.append("].");
            errorHandler2.error(stringBuffer2.toString());
            return false;
        }
        return true;
    }

    @Override // org.apache.log4j.AppenderSkeleton, org.apache.log4j.Appender
    public synchronized void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        writeFooter();
        reset();
    }

    protected void closeWriter() {
        QuietWriter quietWriter = this.qw;
        if (quietWriter != null) {
            try {
                quietWriter.close();
            } catch (IOException e) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("Could not close ");
                stringBuffer.append(this.qw);
                LogLog.error(stringBuffer.toString(), e);
            }
        }
    }

    protected OutputStreamWriter createWriter(OutputStream os) {
        OutputStreamWriter retval = null;
        String enc = getEncoding();
        if (enc != null) {
            try {
                retval = new OutputStreamWriter(os, enc);
            } catch (IOException e) {
                LogLog.warn("Error initializing output writer.");
                LogLog.warn("Unsupported encoding?");
            }
        }
        if (retval == null) {
            return new OutputStreamWriter(os);
        }
        return retval;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String value) {
        this.encoding = value;
    }

    @Override // org.apache.log4j.AppenderSkeleton, org.apache.log4j.Appender
    public synchronized void setErrorHandler(ErrorHandler eh) {
        if (eh == null) {
            LogLog.warn("You have tried to set a null error-handler.");
        } else {
            this.errorHandler = eh;
            QuietWriter quietWriter = this.qw;
            if (quietWriter != null) {
                quietWriter.setErrorHandler(eh);
            }
        }
    }

    public synchronized void setWriter(Writer writer) {
        reset();
        this.qw = new QuietWriter(writer, this.errorHandler);
        writeHeader();
    }

    protected void subAppend(LoggingEvent event) {
        String[] s;
        this.qw.write(this.layout.format(event));
        if (this.layout.ignoresThrowable() && (s = event.getThrowableStrRep()) != null) {
            for (String str : s) {
                this.qw.write(str);
                this.qw.write(Layout.LINE_SEP);
            }
        }
        if (this.immediateFlush) {
            this.qw.flush();
        }
    }

    @Override // org.apache.log4j.AppenderSkeleton, org.apache.log4j.Appender
    public boolean requiresLayout() {
        return true;
    }

    protected void reset() {
        closeWriter();
        this.qw = null;
    }

    protected void writeFooter() {
        String f;
        QuietWriter quietWriter;
        if (this.layout != null && (f = this.layout.getFooter()) != null && (quietWriter = this.qw) != null) {
            quietWriter.write(f);
            this.qw.flush();
        }
    }

    protected void writeHeader() {
        String h;
        QuietWriter quietWriter;
        if (this.layout != null && (h = this.layout.getHeader()) != null && (quietWriter = this.qw) != null) {
            quietWriter.write(h);
        }
    }
}
