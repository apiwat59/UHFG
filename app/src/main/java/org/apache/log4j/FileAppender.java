package org.apache.log4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.QuietWriter;
import org.apache.log4j.spi.ErrorHandler;

/* loaded from: classes.dex */
public class FileAppender extends WriterAppender {
    protected int bufferSize;
    protected boolean bufferedIO;
    protected boolean fileAppend;
    protected String fileName;

    public FileAppender() {
        this.fileAppend = true;
        this.fileName = null;
        this.bufferedIO = false;
        this.bufferSize = 8192;
    }

    public FileAppender(Layout layout, String filename, boolean append, boolean bufferedIO, int bufferSize) throws IOException {
        this.fileAppend = true;
        this.fileName = null;
        this.bufferedIO = false;
        this.bufferSize = 8192;
        this.layout = layout;
        setFile(filename, append, bufferedIO, bufferSize);
    }

    public FileAppender(Layout layout, String filename, boolean append) throws IOException {
        this.fileAppend = true;
        this.fileName = null;
        this.bufferedIO = false;
        this.bufferSize = 8192;
        this.layout = layout;
        setFile(filename, append, false, this.bufferSize);
    }

    public FileAppender(Layout layout, String filename) throws IOException {
        this(layout, filename, true);
    }

    public void setFile(String file) {
        String val = file.trim();
        this.fileName = val;
    }

    public boolean getAppend() {
        return this.fileAppend;
    }

    public String getFile() {
        return this.fileName;
    }

    @Override // org.apache.log4j.WriterAppender, org.apache.log4j.AppenderSkeleton, org.apache.log4j.spi.OptionHandler
    public void activateOptions() {
        String str = this.fileName;
        if (str != null) {
            try {
                setFile(str, this.fileAppend, this.bufferedIO, this.bufferSize);
                return;
            } catch (IOException e) {
                ErrorHandler errorHandler = this.errorHandler;
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("setFile(");
                stringBuffer.append(this.fileName);
                stringBuffer.append(",");
                stringBuffer.append(this.fileAppend);
                stringBuffer.append(") call failed.");
                errorHandler.error(stringBuffer.toString(), e, 4);
                return;
            }
        }
        StringBuffer stringBuffer2 = new StringBuffer();
        stringBuffer2.append("File option not set for appender [");
        stringBuffer2.append(this.name);
        stringBuffer2.append("].");
        LogLog.warn(stringBuffer2.toString());
        LogLog.warn("Are you using FileAppender instead of ConsoleAppender?");
    }

    protected void closeFile() {
        if (this.qw != null) {
            try {
                this.qw.close();
            } catch (IOException e) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("Could not close ");
                stringBuffer.append(this.qw);
                LogLog.error(stringBuffer.toString(), e);
            }
        }
    }

    public boolean getBufferedIO() {
        return this.bufferedIO;
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    public void setAppend(boolean flag) {
        this.fileAppend = flag;
    }

    public void setBufferedIO(boolean bufferedIO) {
        this.bufferedIO = bufferedIO;
        if (bufferedIO) {
            this.immediateFlush = false;
        }
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public synchronized void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize) throws IOException {
        FileOutputStream ostream;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("setFile called: ");
        stringBuffer.append(fileName);
        stringBuffer.append(", ");
        stringBuffer.append(append);
        LogLog.debug(stringBuffer.toString());
        if (bufferedIO) {
            setImmediateFlush(false);
        }
        reset();
        try {
            ostream = new FileOutputStream(fileName, append);
        } catch (FileNotFoundException ex) {
            String parentName = new File(fileName).getParent();
            if (parentName != null) {
                File parentDir = new File(parentName);
                if (!parentDir.exists() && parentDir.mkdirs()) {
                    ostream = new FileOutputStream(fileName, append);
                } else {
                    throw ex;
                }
            } else {
                throw ex;
            }
        }
        Writer fw = createWriter(ostream);
        if (bufferedIO) {
            fw = new BufferedWriter(fw, bufferSize);
        }
        setQWForFiles(fw);
        this.fileName = fileName;
        this.fileAppend = append;
        this.bufferedIO = bufferedIO;
        this.bufferSize = bufferSize;
        writeHeader();
        LogLog.debug("setFile ended");
    }

    protected void setQWForFiles(Writer writer) {
        this.qw = new QuietWriter(writer, this.errorHandler);
    }

    @Override // org.apache.log4j.WriterAppender
    protected void reset() {
        closeFile();
        this.fileName = null;
        super.reset();
    }
}
