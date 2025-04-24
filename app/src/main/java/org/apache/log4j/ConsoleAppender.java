package org.apache.log4j;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.log4j.helpers.LogLog;

/* loaded from: classes.dex */
public class ConsoleAppender extends WriterAppender {
    public static final String SYSTEM_ERR = "System.err";
    public static final String SYSTEM_OUT = "System.out";
    private boolean follow;
    protected String target;

    public ConsoleAppender() {
        this.target = SYSTEM_OUT;
        this.follow = false;
    }

    public ConsoleAppender(Layout layout) {
        this(layout, SYSTEM_OUT);
    }

    public ConsoleAppender(Layout layout, String target) {
        this.target = SYSTEM_OUT;
        this.follow = false;
        setLayout(layout);
        setTarget(target);
        activateOptions();
    }

    public void setTarget(String value) {
        String v = value.trim();
        if (SYSTEM_OUT.equalsIgnoreCase(v)) {
            this.target = SYSTEM_OUT;
        } else if (SYSTEM_ERR.equalsIgnoreCase(v)) {
            this.target = SYSTEM_ERR;
        } else {
            targetWarn(value);
        }
    }

    public String getTarget() {
        return this.target;
    }

    public final void setFollow(boolean newValue) {
        this.follow = newValue;
    }

    public final boolean getFollow() {
        return this.follow;
    }

    void targetWarn(String val) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[");
        stringBuffer.append(val);
        stringBuffer.append("] should be System.out or System.err.");
        LogLog.warn(stringBuffer.toString());
        LogLog.warn("Using previously set target, System.out by default.");
    }

    @Override // org.apache.log4j.WriterAppender, org.apache.log4j.AppenderSkeleton, org.apache.log4j.spi.OptionHandler
    public void activateOptions() {
        if (this.follow) {
            if (this.target.equals(SYSTEM_ERR)) {
                setWriter(createWriter(new SystemErrStream()));
            } else {
                setWriter(createWriter(new SystemOutStream()));
            }
        } else if (this.target.equals(SYSTEM_ERR)) {
            setWriter(createWriter(System.err));
        } else {
            setWriter(createWriter(System.out));
        }
        super.activateOptions();
    }

    @Override // org.apache.log4j.WriterAppender
    protected final void closeWriter() {
        if (this.follow) {
            super.closeWriter();
        }
    }

    private static class SystemErrStream extends OutputStream {
        @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() {
        }

        @Override // java.io.OutputStream, java.io.Flushable
        public void flush() {
            System.err.flush();
        }

        @Override // java.io.OutputStream
        public void write(byte[] b) throws IOException {
            System.err.write(b);
        }

        @Override // java.io.OutputStream
        public void write(byte[] b, int off, int len) throws IOException {
            System.err.write(b, off, len);
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            System.err.write(b);
        }
    }

    private static class SystemOutStream extends OutputStream {
        @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() {
        }

        @Override // java.io.OutputStream, java.io.Flushable
        public void flush() {
            System.out.flush();
        }

        @Override // java.io.OutputStream
        public void write(byte[] b) throws IOException {
            System.out.write(b);
        }

        @Override // java.io.OutputStream
        public void write(byte[] b, int off, int len) throws IOException {
            System.out.write(b, off, len);
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            System.out.write(b);
        }
    }
}
