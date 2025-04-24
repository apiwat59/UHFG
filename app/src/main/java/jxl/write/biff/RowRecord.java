package jxl.write.biff;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import jxl.CellType;
import jxl.biff.CellReferenceHelper;
import jxl.biff.IndexMapping;
import jxl.biff.IntegerHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;
import jxl.biff.XFRecord;
import jxl.common.Logger;
import jxl.write.Number;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableSheet;

/* loaded from: classes.dex */
class RowRecord extends WritableRecordData {
    private static final int growSize = 10;
    private static final int maxRKValue = 536870911;
    private static final int minRKValue = -536870912;
    private CellValue[] cells;
    private boolean collapsed;
    private byte[] data;
    private boolean defaultFormat;
    private boolean groupStart;
    private boolean matchesDefFontHeight;
    private int numColumns;
    private int outlineLevel;
    private int rowHeight;
    private int rowNumber;
    private WritableSheet sheet;
    private XFRecord style;
    private int xfIndex;
    private static final Logger logger = Logger.getLogger(RowRecord.class);
    private static int defaultHeightIndicator = 255;
    private static int maxColumns = 256;

    public RowRecord(int rn, WritableSheet ws) {
        super(Type.ROW);
        this.rowNumber = rn;
        this.cells = new CellValue[0];
        this.numColumns = 0;
        this.rowHeight = defaultHeightIndicator;
        this.collapsed = false;
        this.matchesDefFontHeight = true;
        this.sheet = ws;
    }

    public void setRowHeight(int h) {
        if (h == 0) {
            setCollapsed(true);
            this.matchesDefFontHeight = false;
        } else {
            this.rowHeight = h;
            this.matchesDefFontHeight = false;
        }
    }

    void setRowDetails(int height, boolean mdfh, boolean col, int ol, boolean gs, XFRecord xfr) {
        this.rowHeight = height;
        this.collapsed = col;
        this.matchesDefFontHeight = mdfh;
        this.outlineLevel = ol;
        this.groupStart = gs;
        if (xfr != null) {
            this.defaultFormat = true;
            this.style = xfr;
            this.xfIndex = xfr.getXFIndex();
        }
    }

    public void setCollapsed(boolean c) {
        this.collapsed = c;
    }

    public int getRowNumber() {
        return this.rowNumber;
    }

    public void addCell(CellValue cv) {
        WritableCellFeatures wcf;
        int col = cv.getColumn();
        if (col >= maxColumns) {
            logger.warn("Could not add cell at " + CellReferenceHelper.getCellReference(cv.getRow(), cv.getColumn()) + " because it exceeds the maximum column limit");
            return;
        }
        if (col >= this.cells.length) {
            CellValue[] oldCells = this.cells;
            CellValue[] cellValueArr = new CellValue[Math.max(oldCells.length + 10, col + 1)];
            this.cells = cellValueArr;
            System.arraycopy(oldCells, 0, cellValueArr, 0, oldCells.length);
        }
        CellValue[] oldCells2 = this.cells;
        if (oldCells2[col] != null && (wcf = oldCells2[col].getWritableCellFeatures()) != null) {
            wcf.removeComment();
            if (wcf.getDVParser() != null && !wcf.getDVParser().extendedCellsValidation()) {
                wcf.removeDataValidation();
            }
        }
        this.cells[col] = cv;
        this.numColumns = Math.max(col + 1, this.numColumns);
    }

    public void removeCell(int col) {
        if (col >= this.numColumns) {
            return;
        }
        this.cells[col] = null;
    }

    public void write(File outputFile) throws IOException {
        outputFile.write(this);
    }

    public void writeCells(File outputFile) throws IOException {
        ArrayList integerValues = new ArrayList();
        for (int i = 0; i < this.numColumns; i++) {
            boolean integerValue = false;
            CellValue[] cellValueArr = this.cells;
            if (cellValueArr[i] != null) {
                if (cellValueArr[i].getType() == CellType.NUMBER) {
                    Number nc = (Number) this.cells[i];
                    if (nc.getValue() == ((int) nc.getValue()) && nc.getValue() < 5.36870911E8d && nc.getValue() > -5.36870912E8d && nc.getCellFeatures() == null) {
                        integerValue = true;
                    }
                }
                if (integerValue) {
                    integerValues.add(this.cells[i]);
                } else {
                    writeIntegerValues(integerValues, outputFile);
                    outputFile.write(this.cells[i]);
                    if (this.cells[i].getType() == CellType.STRING_FORMULA) {
                        StringRecord sr = new StringRecord(this.cells[i].getContents());
                        outputFile.write(sr);
                    }
                }
            } else {
                writeIntegerValues(integerValues, outputFile);
            }
        }
        writeIntegerValues(integerValues, outputFile);
    }

    private void writeIntegerValues(ArrayList integerValues, File outputFile) throws IOException {
        if (integerValues.size() == 0) {
            return;
        }
        if (integerValues.size() >= 3) {
            MulRKRecord mulrk = new MulRKRecord(integerValues);
            outputFile.write(mulrk);
        } else {
            Iterator i = integerValues.iterator();
            while (i.hasNext()) {
                outputFile.write((CellValue) i.next());
            }
        }
        integerValues.clear();
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        byte[] data = new byte[16];
        int rh = this.rowHeight;
        if (this.sheet.getSettings().getDefaultRowHeight() != 255 && rh == defaultHeightIndicator) {
            rh = this.sheet.getSettings().getDefaultRowHeight();
        }
        IntegerHelper.getTwoBytes(this.rowNumber, data, 0);
        IntegerHelper.getTwoBytes(this.numColumns, data, 4);
        IntegerHelper.getTwoBytes(rh, data, 6);
        int options = this.outlineLevel + 256;
        if (this.groupStart) {
            options |= 16;
        }
        if (this.collapsed) {
            options |= 32;
        }
        if (!this.matchesDefFontHeight) {
            options |= 64;
        }
        if (this.defaultFormat) {
            options = options | 128 | (this.xfIndex << 16);
        }
        IntegerHelper.getFourBytes(options, data, 12);
        return data;
    }

    public int getMaxColumn() {
        return this.numColumns;
    }

    public CellValue getCell(int col) {
        if (col < 0 || col >= this.numColumns) {
            return null;
        }
        return this.cells[col];
    }

    void incrementRow() {
        this.rowNumber++;
        int i = 0;
        while (true) {
            CellValue[] cellValueArr = this.cells;
            if (i < cellValueArr.length) {
                if (cellValueArr[i] != null) {
                    cellValueArr[i].incrementRow();
                }
                i++;
            } else {
                return;
            }
        }
    }

    void decrementRow() {
        this.rowNumber--;
        int i = 0;
        while (true) {
            CellValue[] cellValueArr = this.cells;
            if (i < cellValueArr.length) {
                if (cellValueArr[i] != null) {
                    cellValueArr[i].decrementRow();
                }
                i++;
            } else {
                return;
            }
        }
    }

    void insertColumn(int col) {
        int i = this.numColumns;
        if (col >= i) {
            return;
        }
        CellValue[] oldCells = this.cells;
        if (i >= this.cells.length - 1) {
            this.cells = new CellValue[oldCells.length + 10];
        } else {
            this.cells = new CellValue[oldCells.length];
        }
        System.arraycopy(oldCells, 0, this.cells, 0, col);
        System.arraycopy(oldCells, col, this.cells, col + 1, this.numColumns - col);
        int i2 = col + 1;
        while (true) {
            int i3 = this.numColumns;
            if (i2 <= i3) {
                CellValue[] cellValueArr = this.cells;
                if (cellValueArr[i2] != null) {
                    cellValueArr[i2].incrementColumn();
                }
                i2++;
            } else {
                this.numColumns = Math.min(i3 + 1, maxColumns);
                return;
            }
        }
    }

    void removeColumn(int col) {
        if (col >= this.numColumns) {
            return;
        }
        CellValue[] oldCells = this.cells;
        CellValue[] cellValueArr = new CellValue[oldCells.length];
        this.cells = cellValueArr;
        System.arraycopy(oldCells, 0, cellValueArr, 0, col);
        System.arraycopy(oldCells, col + 1, this.cells, col, this.numColumns - (col + 1));
        int i = col;
        while (true) {
            int i2 = this.numColumns;
            if (i < i2) {
                CellValue[] cellValueArr2 = this.cells;
                if (cellValueArr2[i] != null) {
                    cellValueArr2[i].decrementColumn();
                }
                i++;
            } else {
                this.numColumns = i2 - 1;
                return;
            }
        }
    }

    public boolean isDefaultHeight() {
        return this.rowHeight == defaultHeightIndicator;
    }

    public int getRowHeight() {
        return this.rowHeight;
    }

    public boolean isCollapsed() {
        return this.collapsed;
    }

    void rationalize(IndexMapping xfmapping) {
        if (this.defaultFormat) {
            this.xfIndex = xfmapping.getNewIndex(this.xfIndex);
        }
    }

    XFRecord getStyle() {
        return this.style;
    }

    boolean hasDefaultFormat() {
        return this.defaultFormat;
    }

    boolean matchesDefaultFontHeight() {
        return this.matchesDefFontHeight;
    }

    public int getOutlineLevel() {
        return this.outlineLevel;
    }

    public boolean getGroupStart() {
        return this.groupStart;
    }

    public void incrementOutlineLevel() {
        this.outlineLevel++;
    }

    public void decrementOutlineLevel() {
        int i = this.outlineLevel;
        if (i > 0) {
            this.outlineLevel = i - 1;
        }
        if (this.outlineLevel == 0) {
            this.collapsed = false;
        }
    }

    public void setOutlineLevel(int level) {
        this.outlineLevel = level;
    }

    public void setGroupStart(boolean value) {
        this.groupStart = value;
    }
}
