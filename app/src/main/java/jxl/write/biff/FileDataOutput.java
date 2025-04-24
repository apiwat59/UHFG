package jxl.write.biff;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import jxl.common.Logger;

/* loaded from: classes.dex */
class FileDataOutput implements ExcelDataOutput {
    private static Logger logger = Logger.getLogger(FileDataOutput.class);
    private RandomAccessFile data;
    private java.io.File temporaryFile;

    public FileDataOutput(java.io.File tmpdir) throws IOException {
        java.io.File createTempFile = java.io.File.createTempFile("jxl", ".tmp", tmpdir);
        this.temporaryFile = createTempFile;
        createTempFile.deleteOnExit();
        this.data = new RandomAccessFile(this.temporaryFile, "rw");
    }

    @Override // jxl.write.biff.ExcelDataOutput
    public void write(byte[] bytes) throws IOException {
        this.data.write(bytes);
    }

    @Override // jxl.write.biff.ExcelDataOutput
    public int getPosition() throws IOException {
        return (int) this.data.getFilePointer();
    }

    @Override // jxl.write.biff.ExcelDataOutput
    public void setData(byte[] newdata, int pos) throws IOException {
        long curpos = this.data.getFilePointer();
        this.data.seek(pos);
        this.data.write(newdata);
        this.data.seek(curpos);
    }

    @Override // jxl.write.biff.ExcelDataOutput
    public void writeData(OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        this.data.seek(0L);
        while (true) {
            int length = this.data.read(buffer);
            if (length != -1) {
                out.write(buffer, 0, length);
            } else {
                return;
            }
        }
    }

    @Override // jxl.write.biff.ExcelDataOutput
    public void close() throws IOException {
        this.data.close();
        this.temporaryFile.delete();
    }
}
