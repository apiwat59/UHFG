package jxl.write;

import java.io.File;
import java.net.URL;
import jxl.Hyperlink;
import jxl.write.biff.HyperlinkRecord;

/* loaded from: classes.dex */
public class WritableHyperlink extends HyperlinkRecord implements Hyperlink {
    public WritableHyperlink(Hyperlink h, WritableSheet ws) {
        super(h, ws);
    }

    public WritableHyperlink(int col, int row, URL url) {
        this(col, row, col, row, url);
    }

    public WritableHyperlink(int col, int row, int lastcol, int lastrow, URL url) {
        this(col, row, lastcol, lastrow, url, (String) null);
    }

    public WritableHyperlink(int col, int row, int lastcol, int lastrow, URL url, String desc) {
        super(col, row, lastcol, lastrow, url, desc);
    }

    public WritableHyperlink(int col, int row, File file) {
        this(col, row, col, row, file, (String) null);
    }

    public WritableHyperlink(int col, int row, File file, String desc) {
        this(col, row, col, row, file, desc);
    }

    public WritableHyperlink(int col, int row, int lastcol, int lastrow, File file) {
        super(col, row, lastcol, lastrow, file, (String) null);
    }

    public WritableHyperlink(int col, int row, int lastcol, int lastrow, File file, String desc) {
        super(col, row, lastcol, lastrow, file, desc);
    }

    public WritableHyperlink(int col, int row, String desc, WritableSheet sheet, int destcol, int destrow) {
        this(col, row, col, row, desc, sheet, destcol, destrow, destcol, destrow);
    }

    public WritableHyperlink(int col, int row, int lastcol, int lastrow, String desc, WritableSheet sheet, int destcol, int destrow, int lastdestcol, int lastdestrow) {
        super(col, row, lastcol, lastrow, desc, sheet, destcol, destrow, lastdestcol, lastdestrow);
    }

    @Override // jxl.write.biff.HyperlinkRecord
    public void setURL(URL url) {
        super.setURL(url);
    }

    @Override // jxl.write.biff.HyperlinkRecord
    public void setFile(File file) {
        super.setFile(file);
    }

    public void setDescription(String desc) {
        super.setContents(desc);
    }

    @Override // jxl.write.biff.HyperlinkRecord
    public void setLocation(String desc, WritableSheet sheet, int destcol, int destrow, int lastdestcol, int lastdestrow) {
        super.setLocation(desc, sheet, destcol, destrow, lastdestcol, lastdestrow);
    }
}
