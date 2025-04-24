package jxl.read.biff;

import java.net.MalformedURLException;
import java.net.URL;
import jxl.CellReferenceHelper;
import jxl.Hyperlink;
import jxl.Range;
import jxl.Sheet;
import jxl.WorkbookSettings;
import jxl.biff.IntegerHelper;
import jxl.biff.RecordData;
import jxl.biff.SheetRangeImpl;
import jxl.biff.StringHelper;
import jxl.common.Logger;
import kotlin.text.Typography;

/* loaded from: classes.dex */
public class HyperlinkRecord extends RecordData implements Hyperlink {
    private static final LinkType fileLink;
    private static Logger logger = Logger.getLogger(HyperlinkRecord.class);
    private static final LinkType unknown;
    private static final LinkType urlLink;
    private static final LinkType workbookLink;
    private java.io.File file;
    private int firstColumn;
    private int firstRow;
    private int lastColumn;
    private int lastRow;
    private LinkType linkType;
    private String location;
    private SheetRangeImpl range;
    private URL url;

    static {
        urlLink = new LinkType();
        fileLink = new LinkType();
        workbookLink = new LinkType();
        unknown = new LinkType();
    }

    private static class LinkType {
        private LinkType() {
        }
    }

    HyperlinkRecord(Record t, Sheet s, WorkbookSettings ws) {
        super(t);
        int upLevelCount;
        int chars;
        this.linkType = unknown;
        byte[] data = getRecord().getData();
        this.firstRow = IntegerHelper.getInt(data[0], data[1]);
        this.lastRow = IntegerHelper.getInt(data[2], data[3]);
        this.firstColumn = IntegerHelper.getInt(data[4], data[5]);
        int i = IntegerHelper.getInt(data[6], data[7]);
        this.lastColumn = i;
        this.range = new SheetRangeImpl(s, this.firstColumn, this.firstRow, i, this.lastRow);
        int options = IntegerHelper.getInt(data[28], data[29], data[30], data[31]);
        boolean description = (options & 20) != 0;
        int descbytes = 0;
        if (description) {
            int descchars = IntegerHelper.getInt(data[32], data[32 + 1], data[32 + 2], data[32 + 3]);
            descbytes = (descchars * 2) + 4;
        }
        int startpos = 32 + descbytes;
        boolean targetFrame = (options & 128) != 0;
        int targetbytes = 0;
        if (targetFrame) {
            int targetchars = IntegerHelper.getInt(data[startpos], data[startpos + 1], data[startpos + 2], data[startpos + 3]);
            targetbytes = (targetchars * 2) + 4;
        }
        int startpos2 = startpos + targetbytes;
        if ((options & 3) == 3) {
            this.linkType = urlLink;
            if (data[startpos2] == 3) {
                this.linkType = fileLink;
            }
        } else if ((options & 1) != 0) {
            this.linkType = fileLink;
            if (data[startpos2] == -32) {
                this.linkType = urlLink;
            }
        } else if ((options & 8) != 0) {
            this.linkType = workbookLink;
        }
        LinkType linkType = this.linkType;
        if (linkType == urlLink) {
            String urlString = null;
            int startpos3 = startpos2 + 16;
            try {
                int bytes = IntegerHelper.getInt(data[startpos3], data[startpos3 + 1], data[startpos3 + 2], data[startpos3 + 3]);
                urlString = StringHelper.getUnicodeString(data, (bytes / 2) - 1, startpos3 + 4);
                this.url = new URL(urlString);
            } catch (MalformedURLException e) {
                logger.warn("URL " + urlString + " is malformed.  Trying a file");
                try {
                    this.linkType = fileLink;
                    this.file = new java.io.File(urlString);
                } catch (Exception e2) {
                    logger.warn("Cannot set to file.  Setting a default URL");
                    try {
                        this.linkType = urlLink;
                        this.url = new URL("http://www.andykhan.com/jexcelapi/index.html");
                    } catch (MalformedURLException e3) {
                    }
                }
            } catch (Throwable e4) {
                StringBuffer sb1 = new StringBuffer();
                StringBuffer sb2 = new StringBuffer();
                CellReferenceHelper.getCellReference(this.firstColumn, this.firstRow, sb1);
                CellReferenceHelper.getCellReference(this.lastColumn, this.lastRow, sb2);
                sb1.insert(0, "Exception when parsing URL ");
                sb1.append(Typography.quote);
                sb1.append(sb2.toString());
                sb1.append("\".  Using default.");
                logger.warn(sb1, e4);
                try {
                    this.url = new URL("http://www.andykhan.com/jexcelapi/index.html");
                } catch (MalformedURLException e5) {
                }
            }
            return;
        }
        if (linkType != fileLink) {
            if (linkType == workbookLink) {
                int chars2 = IntegerHelper.getInt(data[32], data[33], data[34], data[35]);
                this.location = StringHelper.getUnicodeString(data, chars2 - 1, 36);
                return;
            } else {
                logger.warn("Cannot determine link type");
                return;
            }
        }
        int startpos4 = startpos2 + 16;
        try {
            upLevelCount = IntegerHelper.getInt(data[startpos4], data[startpos4 + 1]);
            chars = IntegerHelper.getInt(data[startpos4 + 2], data[startpos4 + 3], data[startpos4 + 4], data[startpos4 + 5]);
        } catch (Throwable th) {
            e = th;
        }
        try {
            String fileName = StringHelper.getString(data, chars - 1, startpos4 + 6, ws);
            StringBuffer sb = new StringBuffer();
            for (int i2 = 0; i2 < upLevelCount; i2++) {
                sb.append("..\\");
            }
            sb.append(fileName);
            this.file = new java.io.File(sb.toString());
        } catch (Throwable th2) {
            e = th2;
            logger.warn("Exception when parsing file " + e.getClass().getName() + ".");
            this.file = new java.io.File(".");
        }
    }

    @Override // jxl.Hyperlink
    public boolean isFile() {
        return this.linkType == fileLink;
    }

    @Override // jxl.Hyperlink
    public boolean isURL() {
        return this.linkType == urlLink;
    }

    @Override // jxl.Hyperlink
    public boolean isLocation() {
        return this.linkType == workbookLink;
    }

    @Override // jxl.Hyperlink
    public int getRow() {
        return this.firstRow;
    }

    @Override // jxl.Hyperlink
    public int getColumn() {
        return this.firstColumn;
    }

    @Override // jxl.Hyperlink
    public int getLastRow() {
        return this.lastRow;
    }

    @Override // jxl.Hyperlink
    public int getLastColumn() {
        return this.lastColumn;
    }

    @Override // jxl.Hyperlink
    public URL getURL() {
        return this.url;
    }

    @Override // jxl.Hyperlink
    public java.io.File getFile() {
        return this.file;
    }

    @Override // jxl.biff.RecordData
    public Record getRecord() {
        return super.getRecord();
    }

    @Override // jxl.Hyperlink
    public Range getRange() {
        return this.range;
    }

    public String getLocation() {
        return this.location;
    }
}
