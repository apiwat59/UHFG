package org.apache.log4j;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.LoggingEvent;

/* loaded from: classes.dex */
public class RollingFileAppender extends FileAppender {
    protected int maxBackupIndex;
    protected long maxFileSize;

    public RollingFileAppender() {
        this.maxFileSize = 10485760L;
        this.maxBackupIndex = 1;
    }

    public RollingFileAppender(Layout layout, String filename, boolean append) throws IOException {
        super(layout, filename, append);
        this.maxFileSize = 10485760L;
        this.maxBackupIndex = 1;
    }

    public RollingFileAppender(Layout layout, String filename) throws IOException {
        super(layout, filename);
        this.maxFileSize = 10485760L;
        this.maxBackupIndex = 1;
    }

    public int getMaxBackupIndex() {
        return this.maxBackupIndex;
    }

    public long getMaximumFileSize() {
        return this.maxFileSize;
    }

    public void rollOver() {
        if (this.qw != null) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("rolling over count=");
            stringBuffer.append(((CountingQuietWriter) this.qw).getCount());
            LogLog.debug(stringBuffer.toString());
        }
        StringBuffer stringBuffer2 = new StringBuffer();
        stringBuffer2.append("maxBackupIndex=");
        stringBuffer2.append(this.maxBackupIndex);
        LogLog.debug(stringBuffer2.toString());
        if (this.maxBackupIndex > 0) {
            StringBuffer stringBuffer3 = new StringBuffer();
            stringBuffer3.append(this.fileName);
            stringBuffer3.append('.');
            stringBuffer3.append(this.maxBackupIndex);
            File file = new File(stringBuffer3.toString());
            if (file.exists()) {
                file.delete();
            }
            for (int i = this.maxBackupIndex - 1; i >= 1; i--) {
                StringBuffer stringBuffer4 = new StringBuffer();
                stringBuffer4.append(this.fileName);
                stringBuffer4.append(".");
                stringBuffer4.append(i);
                File file2 = new File(stringBuffer4.toString());
                if (file2.exists()) {
                    StringBuffer stringBuffer5 = new StringBuffer();
                    stringBuffer5.append(this.fileName);
                    stringBuffer5.append('.');
                    stringBuffer5.append(i + 1);
                    File target = new File(stringBuffer5.toString());
                    StringBuffer stringBuffer6 = new StringBuffer();
                    stringBuffer6.append("Renaming file ");
                    stringBuffer6.append(file2);
                    stringBuffer6.append(" to ");
                    stringBuffer6.append(target);
                    LogLog.debug(stringBuffer6.toString());
                    file2.renameTo(target);
                }
            }
            StringBuffer stringBuffer7 = new StringBuffer();
            stringBuffer7.append(this.fileName);
            stringBuffer7.append(".");
            stringBuffer7.append(1);
            File target2 = new File(stringBuffer7.toString());
            closeFile();
            File file3 = new File(this.fileName);
            StringBuffer stringBuffer8 = new StringBuffer();
            stringBuffer8.append("Renaming file ");
            stringBuffer8.append(file3);
            stringBuffer8.append(" to ");
            stringBuffer8.append(target2);
            LogLog.debug(stringBuffer8.toString());
            file3.renameTo(target2);
        }
        try {
            setFile(this.fileName, false, this.bufferedIO, this.bufferSize);
        } catch (IOException e) {
            StringBuffer stringBuffer9 = new StringBuffer();
            stringBuffer9.append("setFile(");
            stringBuffer9.append(this.fileName);
            stringBuffer9.append(", false) call failed.");
            LogLog.error(stringBuffer9.toString(), e);
        }
    }

    @Override // org.apache.log4j.FileAppender
    public synchronized void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize) throws IOException {
        super.setFile(fileName, append, this.bufferedIO, this.bufferSize);
        if (append) {
            File f = new File(fileName);
            ((CountingQuietWriter) this.qw).setCount(f.length());
        }
    }

    public void setMaxBackupIndex(int maxBackups) {
        this.maxBackupIndex = maxBackups;
    }

    public void setMaximumFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public void setMaxFileSize(String value) {
        this.maxFileSize = OptionConverter.toFileSize(value, this.maxFileSize + 1);
    }

    @Override // org.apache.log4j.FileAppender
    protected void setQWForFiles(Writer writer) {
        this.qw = new CountingQuietWriter(writer, this.errorHandler);
    }

    @Override // org.apache.log4j.WriterAppender
    protected void subAppend(LoggingEvent event) {
        super.subAppend(event);
        if (this.fileName != null && ((CountingQuietWriter) this.qw).getCount() >= this.maxFileSize) {
            rollOver();
        }
    }
}
