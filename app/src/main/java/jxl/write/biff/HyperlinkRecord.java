package jxl.write.biff;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import jxl.CellType;
import jxl.Hyperlink;
import jxl.Range;
import jxl.biff.CellReferenceHelper;
import jxl.biff.IntegerHelper;
import jxl.biff.SheetRangeImpl;
import jxl.biff.StringHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;
import jxl.common.Assert;
import jxl.common.Logger;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;

/* loaded from: classes.dex */
public class HyperlinkRecord extends WritableRecordData {
    private static final LinkType fileLink;
    private static Logger logger = Logger.getLogger(HyperlinkRecord.class);
    private static final LinkType uncLink;
    private static final LinkType unknown;
    private static final LinkType urlLink;
    private static final LinkType workbookLink;
    private String contents;
    private byte[] data;
    private java.io.File file;
    private int firstColumn;
    private int firstRow;
    private int lastColumn;
    private int lastRow;
    private LinkType linkType;
    private String location;
    private boolean modified;
    private Range range;
    private WritableSheet sheet;
    private URL url;

    static {
        urlLink = new LinkType();
        fileLink = new LinkType();
        uncLink = new LinkType();
        workbookLink = new LinkType();
        unknown = new LinkType();
    }

    private static class LinkType {
        private LinkType() {
        }
    }

    protected HyperlinkRecord(Hyperlink h, WritableSheet s) {
        super(Type.HLINK);
        if (h instanceof jxl.read.biff.HyperlinkRecord) {
            copyReadHyperlink(h, s);
        } else {
            copyWritableHyperlink(h, s);
        }
    }

    private void copyReadHyperlink(Hyperlink h, WritableSheet s) {
        jxl.read.biff.HyperlinkRecord hl = (jxl.read.biff.HyperlinkRecord) h;
        this.data = hl.getRecord().getData();
        this.sheet = s;
        this.firstRow = hl.getRow();
        this.firstColumn = hl.getColumn();
        this.lastRow = hl.getLastRow();
        int lastColumn = hl.getLastColumn();
        this.lastColumn = lastColumn;
        this.range = new SheetRangeImpl(s, this.firstColumn, this.firstRow, lastColumn, this.lastRow);
        this.linkType = unknown;
        if (hl.isFile()) {
            this.linkType = fileLink;
            this.file = hl.getFile();
        } else if (hl.isURL()) {
            this.linkType = urlLink;
            this.url = hl.getURL();
        } else if (hl.isLocation()) {
            this.linkType = workbookLink;
            this.location = hl.getLocation();
        }
        this.modified = false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void copyWritableHyperlink(Hyperlink hyperlink, WritableSheet s) {
        HyperlinkRecord h = (HyperlinkRecord) hyperlink;
        this.firstRow = h.firstRow;
        this.lastRow = h.lastRow;
        this.firstColumn = h.firstColumn;
        this.lastColumn = h.lastColumn;
        if (h.url != null) {
            try {
                this.url = new URL(h.url.toString());
            } catch (MalformedURLException e) {
                Assert.verify(false);
            }
        }
        if (h.file != null) {
            this.file = new java.io.File(h.file.getPath());
        }
        this.location = h.location;
        this.contents = h.contents;
        this.linkType = h.linkType;
        this.modified = true;
        this.sheet = s;
        this.range = new SheetRangeImpl(s, this.firstColumn, this.firstRow, this.lastColumn, this.lastRow);
    }

    protected HyperlinkRecord(int col, int row, int lastcol, int lastrow, URL url, String desc) {
        super(Type.HLINK);
        this.firstColumn = col;
        this.firstRow = row;
        this.lastColumn = Math.max(col, lastcol);
        this.lastRow = Math.max(this.firstRow, lastrow);
        this.url = url;
        this.contents = desc;
        this.linkType = urlLink;
        this.modified = true;
    }

    protected HyperlinkRecord(int col, int row, int lastcol, int lastrow, java.io.File file, String desc) {
        super(Type.HLINK);
        this.firstColumn = col;
        this.firstRow = row;
        this.lastColumn = Math.max(col, lastcol);
        this.lastRow = Math.max(this.firstRow, lastrow);
        this.contents = desc;
        this.file = file;
        if (file.getPath().startsWith("\\\\")) {
            this.linkType = uncLink;
        } else {
            this.linkType = fileLink;
        }
        this.modified = true;
    }

    protected HyperlinkRecord(int col, int row, int lastcol, int lastrow, String desc, WritableSheet s, int destcol, int destrow, int lastdestcol, int lastdestrow) {
        super(Type.HLINK);
        this.firstColumn = col;
        this.firstRow = row;
        this.lastColumn = Math.max(col, lastcol);
        this.lastRow = Math.max(this.firstRow, lastrow);
        setLocation(s, destcol, destrow, lastdestcol, lastdestrow);
        this.contents = desc;
        this.linkType = workbookLink;
        this.modified = true;
    }

    public boolean isFile() {
        return this.linkType == fileLink;
    }

    public boolean isUNC() {
        return this.linkType == uncLink;
    }

    public boolean isURL() {
        return this.linkType == urlLink;
    }

    public boolean isLocation() {
        return this.linkType == workbookLink;
    }

    public int getRow() {
        return this.firstRow;
    }

    public int getColumn() {
        return this.firstColumn;
    }

    public int getLastRow() {
        return this.lastRow;
    }

    public int getLastColumn() {
        return this.lastColumn;
    }

    public URL getURL() {
        return this.url;
    }

    public java.io.File getFile() {
        return this.file;
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        if (!this.modified) {
            return this.data;
        }
        byte[] commonData = {0, 0, 0, 0, 0, 0, 0, 0, -48, -55, -22, 121, -7, -70, -50, 17, -116, -126, 0, -86, 0, 75, -87, 11, 2, 0, 0, 0, 0, 0, 0, 0};
        IntegerHelper.getTwoBytes(this.firstRow, commonData, 0);
        IntegerHelper.getTwoBytes(this.lastRow, commonData, 2);
        IntegerHelper.getTwoBytes(this.firstColumn, commonData, 4);
        IntegerHelper.getTwoBytes(this.lastColumn, commonData, 6);
        int optionFlags = 0;
        if (isURL()) {
            optionFlags = 3;
            if (this.contents != null) {
                optionFlags = 3 | 20;
            }
        } else if (isFile()) {
            optionFlags = 1;
            if (this.contents != null) {
                optionFlags = 1 | 20;
            }
        } else if (isLocation()) {
            optionFlags = 8;
        } else if (isUNC()) {
            optionFlags = 259;
        }
        IntegerHelper.getFourBytes(optionFlags, commonData, 28);
        if (isURL()) {
            this.data = getURLData(commonData);
        } else if (isFile()) {
            this.data = getFileData(commonData);
        } else if (isLocation()) {
            this.data = getLocationData(commonData);
        } else if (isUNC()) {
            this.data = getUNCData(commonData);
        }
        return this.data;
    }

    public String toString() {
        if (isFile()) {
            return this.file.toString();
        }
        if (isURL()) {
            return this.url.toString();
        }
        if (isUNC()) {
            return this.file.toString();
        }
        return "";
    }

    public Range getRange() {
        return this.range;
    }

    public void setURL(URL url) {
        URL prevurl = this.url;
        this.linkType = urlLink;
        this.file = null;
        this.location = null;
        this.contents = null;
        this.url = url;
        this.modified = true;
        WritableSheet writableSheet = this.sheet;
        if (writableSheet == null) {
            return;
        }
        WritableCell wc = writableSheet.getWritableCell(this.firstColumn, this.firstRow);
        if (wc.getType() == CellType.LABEL) {
            Label l = (Label) wc;
            String prevurlString = prevurl.toString();
            String prevurlString2 = "";
            if (prevurlString.charAt(prevurlString.length() - 1) == '/' || prevurlString.charAt(prevurlString.length() - 1) == '\\') {
                prevurlString2 = prevurlString.substring(0, prevurlString.length() - 1);
            }
            if (l.getString().equals(prevurlString) || l.getString().equals(prevurlString2)) {
                l.setString(url.toString());
            }
        }
    }

    public void setFile(java.io.File file) {
        this.linkType = fileLink;
        this.url = null;
        this.location = null;
        this.contents = null;
        this.file = file;
        this.modified = true;
        WritableSheet writableSheet = this.sheet;
        if (writableSheet == null) {
            return;
        }
        WritableCell wc = writableSheet.getWritableCell(this.firstColumn, this.firstRow);
        Assert.verify(wc.getType() == CellType.LABEL);
        Label l = (Label) wc;
        l.setString(file.toString());
    }

    protected void setLocation(String desc, WritableSheet sheet, int destcol, int destrow, int lastdestcol, int lastdestrow) {
        this.linkType = workbookLink;
        this.url = null;
        this.file = null;
        this.modified = true;
        this.contents = desc;
        setLocation(sheet, destcol, destrow, lastdestcol, lastdestrow);
        if (sheet == null) {
            return;
        }
        WritableCell wc = sheet.getWritableCell(this.firstColumn, this.firstRow);
        Assert.verify(wc.getType() == CellType.LABEL);
        Label l = (Label) wc;
        l.setString(desc);
    }

    private void setLocation(WritableSheet sheet, int destcol, int destrow, int lastdestcol, int lastdestrow) {
        StringBuffer sb = new StringBuffer();
        sb.append('\'');
        if (sheet.getName().indexOf(39) == -1) {
            sb.append(sheet.getName());
        } else {
            String sheetName = sheet.getName();
            int pos = 0;
            int nextPos = sheetName.indexOf(39, 0);
            while (nextPos != -1 && pos < sheetName.length()) {
                sb.append(sheetName.substring(pos, nextPos));
                sb.append("''");
                pos = nextPos + 1;
                nextPos = sheetName.indexOf(39, pos);
            }
            sb.append(sheetName.substring(pos));
        }
        sb.append('\'');
        sb.append('!');
        int lastdestcol2 = Math.max(destcol, lastdestcol);
        int lastdestrow2 = Math.max(destrow, lastdestrow);
        CellReferenceHelper.getCellReference(destcol, destrow, sb);
        sb.append(':');
        CellReferenceHelper.getCellReference(lastdestcol2, lastdestrow2, sb);
        this.location = sb.toString();
    }

    void insertRow(int r) {
        Assert.verify((this.sheet == null || this.range == null) ? false : true);
        int i = this.lastRow;
        if (r > i) {
            return;
        }
        int i2 = this.firstRow;
        if (r <= i2) {
            this.firstRow = i2 + 1;
            this.modified = true;
        }
        if (r <= i) {
            this.lastRow = i + 1;
            this.modified = true;
        }
        if (this.modified) {
            this.range = new SheetRangeImpl(this.sheet, this.firstColumn, this.firstRow, this.lastColumn, this.lastRow);
        }
    }

    void insertColumn(int c) {
        Assert.verify((this.sheet == null || this.range == null) ? false : true);
        int i = this.lastColumn;
        if (c > i) {
            return;
        }
        int i2 = this.firstColumn;
        if (c <= i2) {
            this.firstColumn = i2 + 1;
            this.modified = true;
        }
        if (c <= i) {
            this.lastColumn = i + 1;
            this.modified = true;
        }
        if (this.modified) {
            this.range = new SheetRangeImpl(this.sheet, this.firstColumn, this.firstRow, this.lastColumn, this.lastRow);
        }
    }

    void removeRow(int r) {
        Assert.verify((this.sheet == null || this.range == null) ? false : true);
        int i = this.lastRow;
        if (r > i) {
            return;
        }
        int i2 = this.firstRow;
        if (r < i2) {
            this.firstRow = i2 - 1;
            this.modified = true;
        }
        if (r < i) {
            this.lastRow = i - 1;
            this.modified = true;
        }
        if (this.modified) {
            Assert.verify(this.range != null);
            this.range = new SheetRangeImpl(this.sheet, this.firstColumn, this.firstRow, this.lastColumn, this.lastRow);
        }
    }

    void removeColumn(int c) {
        Assert.verify((this.sheet == null || this.range == null) ? false : true);
        int i = this.lastColumn;
        if (c > i) {
            return;
        }
        int i2 = this.firstColumn;
        if (c < i2) {
            this.firstColumn = i2 - 1;
            this.modified = true;
        }
        if (c < i) {
            this.lastColumn = i - 1;
            this.modified = true;
        }
        if (this.modified) {
            Assert.verify(this.range != null);
            this.range = new SheetRangeImpl(this.sheet, this.firstColumn, this.firstRow, this.lastColumn, this.lastRow);
        }
    }

    private byte[] getURLData(byte[] cd) {
        String urlString = this.url.toString();
        int dataLength = cd.length + 20 + ((urlString.length() + 1) * 2);
        String str = this.contents;
        if (str != null) {
            dataLength += ((str.length() + 1) * 2) + 4;
        }
        byte[] d = new byte[dataLength];
        System.arraycopy(cd, 0, d, 0, cd.length);
        int urlPos = cd.length;
        String str2 = this.contents;
        if (str2 != null) {
            IntegerHelper.getFourBytes(str2.length() + 1, d, urlPos);
            StringHelper.getUnicodeBytes(this.contents, d, urlPos + 4);
            urlPos += ((this.contents.length() + 1) * 2) + 4;
        }
        d[urlPos] = -32;
        d[urlPos + 1] = -55;
        d[urlPos + 2] = -22;
        d[urlPos + 3] = 121;
        d[urlPos + 4] = -7;
        d[urlPos + 5] = -70;
        d[urlPos + 6] = -50;
        d[urlPos + 7] = 17;
        d[urlPos + 8] = -116;
        d[urlPos + 9] = -126;
        d[urlPos + 10] = 0;
        d[urlPos + 11] = -86;
        d[urlPos + 12] = 0;
        d[urlPos + 13] = 75;
        d[urlPos + 14] = -87;
        d[urlPos + 15] = 11;
        IntegerHelper.getFourBytes((urlString.length() + 1) * 2, d, urlPos + 16);
        StringHelper.getUnicodeBytes(urlString, d, urlPos + 20);
        return d;
    }

    private byte[] getUNCData(byte[] cd) {
        String uncString = this.file.getPath();
        byte[] d = new byte[cd.length + (uncString.length() * 2) + 2 + 4];
        System.arraycopy(cd, 0, d, 0, cd.length);
        int urlPos = cd.length;
        int length = uncString.length() + 1;
        IntegerHelper.getFourBytes(length, d, urlPos);
        StringHelper.getUnicodeBytes(uncString, d, urlPos + 4);
        return d;
    }

    private byte[] getFileData(byte[] cd) {
        char driveLetter;
        ArrayList path = new ArrayList();
        ArrayList shortFileName = new ArrayList();
        path.add(this.file.getName());
        shortFileName.add(getShortName(this.file.getName()));
        for (java.io.File parent = this.file.getParentFile(); parent != null; parent = parent.getParentFile()) {
            path.add(parent.getName());
            shortFileName.add(getShortName(parent.getName()));
        }
        int upLevelCount = 0;
        int pos = path.size() - 1;
        boolean upDir = true;
        while (upDir) {
            String s = (String) path.get(pos);
            if (s.equals("..")) {
                upLevelCount++;
                path.remove(pos);
                shortFileName.remove(pos);
            } else {
                upDir = false;
            }
            pos--;
        }
        StringBuffer filePathSB = new StringBuffer();
        StringBuffer shortFilePathSB = new StringBuffer();
        if (this.file.getPath().charAt(1) == ':' && (driveLetter = this.file.getPath().charAt(0)) != 'C' && driveLetter != 'c') {
            filePathSB.append(driveLetter);
            filePathSB.append(':');
            shortFilePathSB.append(driveLetter);
            shortFilePathSB.append(':');
        }
        for (int i = path.size() - 1; i >= 0; i--) {
            filePathSB.append((String) path.get(i));
            shortFilePathSB.append((String) shortFileName.get(i));
            if (i != 0) {
                filePathSB.append("\\");
                shortFilePathSB.append("\\");
            }
        }
        String filePath = filePathSB.toString();
        String shortFilePath = shortFilePathSB.toString();
        int dataLength = cd.length + 4 + shortFilePath.length() + 1 + 16 + 2 + 8 + ((filePath.length() + 1) * 2) + 24;
        String str = this.contents;
        if (str != null) {
            dataLength += ((str.length() + 1) * 2) + 4;
        }
        byte[] d = new byte[dataLength];
        System.arraycopy(cd, 0, d, 0, cd.length);
        int filePos = cd.length;
        String str2 = this.contents;
        if (str2 != null) {
            IntegerHelper.getFourBytes(str2.length() + 1, d, filePos);
            StringHelper.getUnicodeBytes(this.contents, d, filePos + 4);
            filePos += ((this.contents.length() + 1) * 2) + 4;
        }
        int curPos = filePos;
        d[curPos] = 3;
        d[curPos + 1] = 3;
        d[curPos + 2] = 0;
        d[curPos + 3] = 0;
        d[curPos + 4] = 0;
        d[curPos + 5] = 0;
        d[curPos + 6] = 0;
        d[curPos + 7] = 0;
        d[curPos + 8] = -64;
        d[curPos + 9] = 0;
        d[curPos + 10] = 0;
        d[curPos + 11] = 0;
        d[curPos + 12] = 0;
        d[curPos + 13] = 0;
        d[curPos + 14] = 0;
        d[curPos + 15] = 70;
        int curPos2 = curPos + 16;
        IntegerHelper.getTwoBytes(upLevelCount, d, curPos2);
        int curPos3 = curPos2 + 2;
        IntegerHelper.getFourBytes(shortFilePath.length() + 1, d, curPos3);
        StringHelper.getBytes(shortFilePath, d, curPos3 + 4);
        int curPos4 = curPos3 + shortFilePath.length() + 1 + 4;
        d[curPos4] = -1;
        d[curPos4 + 1] = -1;
        d[curPos4 + 2] = -83;
        d[curPos4 + 3] = -34;
        d[curPos4 + 4] = 0;
        d[curPos4 + 5] = 0;
        d[curPos4 + 6] = 0;
        d[curPos4 + 7] = 0;
        d[curPos4 + 8] = 0;
        d[curPos4 + 9] = 0;
        d[curPos4 + 10] = 0;
        d[curPos4 + 11] = 0;
        d[curPos4 + 12] = 0;
        d[curPos4 + 13] = 0;
        d[curPos4 + 14] = 0;
        d[curPos4 + 15] = 0;
        d[curPos4 + 16] = 0;
        d[curPos4 + 17] = 0;
        d[curPos4 + 18] = 0;
        d[curPos4 + 19] = 0;
        d[curPos4 + 20] = 0;
        d[curPos4 + 21] = 0;
        d[curPos4 + 22] = 0;
        d[curPos4 + 23] = 0;
        int curPos5 = curPos4 + 24;
        int size = (filePath.length() * 2) + 6;
        IntegerHelper.getFourBytes(size, d, curPos5);
        int curPos6 = curPos5 + 4;
        IntegerHelper.getFourBytes(filePath.length() * 2, d, curPos6);
        int curPos7 = curPos6 + 4;
        d[curPos7] = 3;
        d[curPos7 + 1] = 0;
        int curPos8 = curPos7 + 2;
        StringHelper.getUnicodeBytes(filePath, d, curPos8);
        int length = curPos8 + ((filePath.length() + 1) * 2);
        return d;
    }

    private String getShortName(String s) {
        String prefix;
        String suffix;
        int sep = s.indexOf(46);
        if (sep == -1) {
            prefix = s;
            suffix = "";
        } else {
            prefix = s.substring(0, sep);
            suffix = s.substring(sep + 1);
        }
        if (prefix.length() > 8) {
            prefix = (prefix.substring(0, 6) + "~" + (prefix.length() - 8)).substring(0, 8);
        }
        String suffix2 = suffix.substring(0, Math.min(3, suffix.length()));
        if (suffix2.length() > 0) {
            return prefix + '.' + suffix2;
        }
        return prefix;
    }

    private byte[] getLocationData(byte[] cd) {
        byte[] d = new byte[cd.length + 4 + ((this.location.length() + 1) * 2)];
        System.arraycopy(cd, 0, d, 0, cd.length);
        int locPos = cd.length;
        IntegerHelper.getFourBytes(this.location.length() + 1, d, locPos);
        StringHelper.getUnicodeBytes(this.location, d, locPos + 4);
        return d;
    }

    void initialize(WritableSheet s) {
        this.sheet = s;
        this.range = new SheetRangeImpl(s, this.firstColumn, this.firstRow, this.lastColumn, this.lastRow);
    }

    String getContents() {
        return this.contents;
    }

    protected void setContents(String desc) {
        this.contents = desc;
        this.modified = true;
    }
}
