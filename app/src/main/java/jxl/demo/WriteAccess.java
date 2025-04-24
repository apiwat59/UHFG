package jxl.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import jxl.WorkbookSettings;
import jxl.biff.StringHelper;
import jxl.biff.Type;
import jxl.read.biff.BiffException;
import jxl.read.biff.BiffRecordReader;
import jxl.read.biff.Record;

/* loaded from: classes.dex */
class WriteAccess {
    private BiffRecordReader reader;

    public WriteAccess(File file) throws IOException, BiffException {
        WorkbookSettings ws = new WorkbookSettings();
        FileInputStream fis = new FileInputStream(file);
        jxl.read.biff.File f = new jxl.read.biff.File(fis, ws);
        this.reader = new BiffRecordReader(f);
        display(ws);
        fis.close();
    }

    private void display(WorkbookSettings ws) throws IOException {
        Record r = null;
        boolean found = false;
        while (this.reader.hasNext() && !found) {
            r = this.reader.next();
            if (r.getType() == Type.WRITEACCESS) {
                found = true;
            }
        }
        if (!found) {
            System.err.println("Warning:  could not find write access record");
            return;
        }
        byte[] data = r.getData();
        String s = StringHelper.getString(data, data.length, 0, ws);
        System.out.println(s);
    }
}
