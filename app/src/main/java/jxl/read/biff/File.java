package jxl.read.biff;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import jxl.WorkbookSettings;
import jxl.biff.BaseCompoundFile;
import jxl.biff.IntegerHelper;
import jxl.biff.Type;
import jxl.common.Logger;

/* loaded from: classes.dex */
public class File {
    private static Logger logger = Logger.getLogger(File.class);
    private int arrayGrowSize;
    private CompoundFile compoundFile;
    private byte[] data;
    private int filePos;
    private int initialFileSize;
    private int oldPos;
    private WorkbookSettings workbookSettings;

    public File(InputStream is, WorkbookSettings ws) throws IOException, BiffException {
        this.workbookSettings = ws;
        this.initialFileSize = ws.getInitialFileSize();
        this.arrayGrowSize = this.workbookSettings.getArrayGrowSize();
        byte[] d = new byte[this.initialFileSize];
        int bytesRead = is.read(d);
        int pos = bytesRead;
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedIOException();
        }
        while (bytesRead != -1) {
            if (pos >= d.length) {
                byte[] newArray = new byte[d.length + this.arrayGrowSize];
                System.arraycopy(d, 0, newArray, 0, d.length);
                d = newArray;
            }
            bytesRead = is.read(d, pos, d.length - pos);
            pos += bytesRead;
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedIOException();
            }
        }
        if (pos + 1 == 0) {
            throw new BiffException(BiffException.excelFileNotFound);
        }
        CompoundFile cf = new CompoundFile(d, ws);
        try {
            this.data = cf.getStream("workbook");
        } catch (BiffException e) {
            this.data = cf.getStream("book");
        }
        if (!this.workbookSettings.getPropertySetsDisabled() && cf.getNumberOfPropertySets() > BaseCompoundFile.STANDARD_PROPERTY_SETS.length) {
            this.compoundFile = cf;
        }
        if (!this.workbookSettings.getGCDisabled()) {
            System.gc();
        }
    }

    public File(byte[] d) {
        this.data = d;
    }

    Record next() {
        Record r = new Record(this.data, this.filePos, this);
        return r;
    }

    Record peek() {
        int tempPos = this.filePos;
        Record r = new Record(this.data, this.filePos, this);
        this.filePos = tempPos;
        return r;
    }

    public void skip(int bytes) {
        this.filePos += bytes;
    }

    public byte[] read(int pos, int length) {
        byte[] ret = new byte[length];
        try {
            System.arraycopy(this.data, pos, ret, 0, length);
            return ret;
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.error("Array index out of bounds at position " + pos + " record length " + length);
            throw e;
        }
    }

    public int getPos() {
        return this.filePos;
    }

    public void setPos(int p) {
        this.oldPos = this.filePos;
        this.filePos = p;
    }

    public void restorePos() {
        this.filePos = this.oldPos;
    }

    private void moveToFirstBof() {
        boolean bofFound = false;
        while (!bofFound) {
            byte[] bArr = this.data;
            int i = this.filePos;
            int code = IntegerHelper.getInt(bArr[i], bArr[i + 1]);
            if (code == Type.BOF.value) {
                bofFound = true;
            } else {
                skip(128);
            }
        }
    }

    public void close() {
    }

    public void clear() {
        this.data = null;
    }

    public boolean hasNext() {
        return this.filePos < this.data.length + (-4);
    }

    CompoundFile getCompoundFile() {
        return this.compoundFile;
    }
}
