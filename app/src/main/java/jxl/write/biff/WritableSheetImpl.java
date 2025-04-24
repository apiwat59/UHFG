package jxl.write.biff;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.regex.Pattern;
import jxl.Cell;
import jxl.CellFeatures;
import jxl.CellReferenceHelper;
import jxl.CellType;
import jxl.CellView;
import jxl.HeaderFooter;
import jxl.Hyperlink;
import jxl.Image;
import jxl.LabelCell;
import jxl.Range;
import jxl.Sheet;
import jxl.SheetSettings;
import jxl.WorkbookSettings;
import jxl.biff.AutoFilter;
import jxl.biff.CellFinder;
import jxl.biff.ConditionalFormat;
import jxl.biff.DVParser;
import jxl.biff.DataValidation;
import jxl.biff.EmptyCell;
import jxl.biff.FormattingRecords;
import jxl.biff.IndexMapping;
import jxl.biff.NumFormatRecordsException;
import jxl.biff.SheetRangeImpl;
import jxl.biff.WorkspaceInformationRecord;
import jxl.biff.XFRecord;
import jxl.biff.drawing.Chart;
import jxl.biff.drawing.ComboBox;
import jxl.biff.drawing.Drawing;
import jxl.biff.drawing.DrawingGroupObject;
import jxl.common.Assert;
import jxl.common.Logger;
import jxl.format.CellFormat;
import jxl.format.Font;
import jxl.format.PageOrientation;
import jxl.format.PaperSize;
import jxl.write.Blank;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableCellFormat;
import jxl.write.WritableHyperlink;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.apache.log4j.spi.LocationInfo;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class WritableSheetImpl implements WritableSheet {
    private static final int maxSheetNameLength = 31;
    private static final int numRowsPerSheet = 65536;
    private static final int rowGrowSize = 10;
    private AutoFilter autoFilter;
    private ButtonPropertySetRecord buttonPropertySet;
    private ComboBox comboBox;
    private DataValidation dataValidation;
    private FormattingRecords formatRecords;
    private int maxColumnOutlineLevel;
    private int maxRowOutlineLevel;
    private String name;
    private File outputFile;
    private PLSRecord plsRecord;
    private SharedStrings sharedStrings;
    private SheetWriter sheetWriter;
    private WritableWorkbookImpl workbook;
    private WorkbookSettings workbookSettings;
    private static Logger logger = Logger.getLogger(WritableSheetImpl.class);
    private static final char[] illegalSheetNameCharacters = {'*', ':', '?', '\\'};
    private static final String[] imageTypes = {"png"};
    private RowRecord[] rows = new RowRecord[0];
    private int numRows = 0;
    private int numColumns = 0;
    private boolean chartOnly = false;
    private boolean drawingsModified = false;
    private TreeSet columnFormats = new TreeSet(new ColumnInfoComparator());
    private TreeSet autosizedColumns = new TreeSet();
    private ArrayList hyperlinks = new ArrayList();
    private MergedCells mergedCells = new MergedCells(this);
    private ArrayList rowBreaks = new ArrayList();
    private ArrayList columnBreaks = new ArrayList();
    private ArrayList drawings = new ArrayList();
    private ArrayList images = new ArrayList();
    private ArrayList conditionalFormats = new ArrayList();
    private ArrayList validatedCells = new ArrayList();
    private SheetSettings settings = new SheetSettings(this);

    private static class ColumnInfoComparator implements Comparator {
        private ColumnInfoComparator() {
        }

        @Override // java.util.Comparator
        public boolean equals(Object o) {
            return o == this;
        }

        @Override // java.util.Comparator
        public int compare(Object o1, Object o2) {
            if (o1 == o2) {
                return 0;
            }
            Assert.verify(o1 instanceof ColumnInfoRecord);
            Assert.verify(o2 instanceof ColumnInfoRecord);
            ColumnInfoRecord ci1 = (ColumnInfoRecord) o1;
            ColumnInfoRecord ci2 = (ColumnInfoRecord) o2;
            return ci1.getColumn() - ci2.getColumn();
        }
    }

    public WritableSheetImpl(String n, File of, FormattingRecords fr, SharedStrings ss, WorkbookSettings ws, WritableWorkbookImpl ww) {
        this.name = validateName(n);
        this.outputFile = of;
        this.workbook = ww;
        this.formatRecords = fr;
        this.sharedStrings = ss;
        this.workbookSettings = ws;
        this.sheetWriter = new SheetWriter(this.outputFile, this, this.workbookSettings);
    }

    @Override // jxl.Sheet
    public Cell getCell(String loc) {
        return getCell(CellReferenceHelper.getColumn(loc), CellReferenceHelper.getRow(loc));
    }

    @Override // jxl.Sheet
    public Cell getCell(int column, int row) {
        return getWritableCell(column, row);
    }

    @Override // jxl.write.WritableSheet
    public WritableCell getWritableCell(String loc) {
        return getWritableCell(CellReferenceHelper.getColumn(loc), CellReferenceHelper.getRow(loc));
    }

    @Override // jxl.write.WritableSheet
    public WritableCell getWritableCell(int column, int row) {
        WritableCell c = null;
        RowRecord[] rowRecordArr = this.rows;
        if (row < rowRecordArr.length && rowRecordArr[row] != null) {
            c = rowRecordArr[row].getCell(column);
        }
        if (c == null) {
            return new EmptyCell(column, row);
        }
        return c;
    }

    @Override // jxl.Sheet
    public int getRows() {
        return this.numRows;
    }

    @Override // jxl.Sheet
    public int getColumns() {
        return this.numColumns;
    }

    @Override // jxl.Sheet
    public Cell findCell(String contents) {
        CellFinder cellFinder = new CellFinder(this);
        return cellFinder.findCell(contents);
    }

    @Override // jxl.Sheet
    public Cell findCell(String contents, int firstCol, int firstRow, int lastCol, int lastRow, boolean reverse) {
        CellFinder cellFinder = new CellFinder(this);
        return cellFinder.findCell(contents, firstCol, firstRow, lastCol, lastRow, reverse);
    }

    @Override // jxl.Sheet
    public Cell findCell(Pattern pattern, int firstCol, int firstRow, int lastCol, int lastRow, boolean reverse) {
        CellFinder cellFinder = new CellFinder(this);
        return cellFinder.findCell(pattern, firstCol, firstRow, lastCol, lastRow, reverse);
    }

    @Override // jxl.Sheet
    public LabelCell findLabelCell(String contents) {
        CellFinder cellFinder = new CellFinder(this);
        return cellFinder.findLabelCell(contents);
    }

    @Override // jxl.Sheet
    public Cell[] getRow(int row) {
        boolean found = false;
        int col = this.numColumns - 1;
        while (col >= 0 && !found) {
            if (getCell(col, row).getType() != CellType.EMPTY) {
                found = true;
            } else {
                col--;
            }
        }
        Cell[] cells = new Cell[col + 1];
        for (int i = 0; i <= col; i++) {
            cells[i] = getCell(i, row);
        }
        return cells;
    }

    @Override // jxl.Sheet
    public Cell[] getColumn(int col) {
        boolean found = false;
        int row = this.numRows - 1;
        while (row >= 0 && !found) {
            if (getCell(col, row).getType() != CellType.EMPTY) {
                found = true;
            } else {
                row--;
            }
        }
        Cell[] cells = new Cell[row + 1];
        for (int i = 0; i <= row; i++) {
            cells[i] = getCell(col, i);
        }
        return cells;
    }

    @Override // jxl.Sheet
    public String getName() {
        return this.name;
    }

    @Override // jxl.write.WritableSheet
    public void insertRow(int row) {
        int i;
        if (row < 0 || row >= (i = this.numRows)) {
            return;
        }
        RowRecord[] oldRows = this.rows;
        if (i == this.rows.length) {
            this.rows = new RowRecord[oldRows.length + 10];
        } else {
            this.rows = new RowRecord[oldRows.length];
        }
        System.arraycopy(oldRows, 0, this.rows, 0, row);
        System.arraycopy(oldRows, row, this.rows, row + 1, this.numRows - row);
        for (int i2 = row + 1; i2 <= this.numRows; i2++) {
            RowRecord[] rowRecordArr = this.rows;
            if (rowRecordArr[i2] != null) {
                rowRecordArr[i2].incrementRow();
            }
        }
        Iterator i3 = this.hyperlinks.iterator();
        while (i3.hasNext()) {
            HyperlinkRecord hr = (HyperlinkRecord) i3.next();
            hr.insertRow(row);
        }
        DataValidation dataValidation = this.dataValidation;
        if (dataValidation != null) {
            dataValidation.insertRow(row);
        }
        ArrayList arrayList = this.validatedCells;
        if (arrayList != null && arrayList.size() > 0) {
            Iterator vci = this.validatedCells.iterator();
            while (vci.hasNext()) {
                CellValue cv = (CellValue) vci.next();
                CellFeatures cf = cv.getCellFeatures();
                if (cf.getDVParser() != null) {
                    cf.getDVParser().insertRow(row);
                }
            }
        }
        this.mergedCells.insertRow(row);
        ArrayList newRowBreaks = new ArrayList();
        Iterator ri = this.rowBreaks.iterator();
        while (ri.hasNext()) {
            int val = ((Integer) ri.next()).intValue();
            if (val >= row) {
                val++;
            }
            newRowBreaks.add(new Integer(val));
        }
        this.rowBreaks = newRowBreaks;
        Iterator cfit = this.conditionalFormats.iterator();
        while (cfit.hasNext()) {
            ((ConditionalFormat) cfit.next()).insertRow(row);
        }
        if (this.workbookSettings.getFormulaAdjust()) {
            this.workbook.rowInserted(this, row);
        }
        this.numRows++;
    }

    @Override // jxl.write.WritableSheet
    public void insertColumn(int col) {
        if (col < 0 || col >= this.numColumns) {
            return;
        }
        for (int i = 0; i < this.numRows; i++) {
            RowRecord[] rowRecordArr = this.rows;
            if (rowRecordArr[i] != null) {
                rowRecordArr[i].insertColumn(col);
            }
        }
        Iterator i2 = this.hyperlinks.iterator();
        while (i2.hasNext()) {
            HyperlinkRecord hr = (HyperlinkRecord) i2.next();
            hr.insertColumn(col);
        }
        Iterator i3 = this.columnFormats.iterator();
        while (i3.hasNext()) {
            ColumnInfoRecord cir = (ColumnInfoRecord) i3.next();
            if (cir.getColumn() >= col) {
                cir.incrementColumn();
            }
        }
        if (this.autosizedColumns.size() > 0) {
            TreeSet newAutosized = new TreeSet();
            Iterator i4 = this.autosizedColumns.iterator();
            while (i4.hasNext()) {
                Integer colnumber = (Integer) i4.next();
                if (colnumber.intValue() >= col) {
                    newAutosized.add(new Integer(colnumber.intValue() + 1));
                } else {
                    newAutosized.add(colnumber);
                }
            }
            this.autosizedColumns = newAutosized;
        }
        DataValidation dataValidation = this.dataValidation;
        if (dataValidation != null) {
            dataValidation.insertColumn(col);
        }
        ArrayList arrayList = this.validatedCells;
        if (arrayList != null && arrayList.size() > 0) {
            Iterator vci = this.validatedCells.iterator();
            while (vci.hasNext()) {
                CellValue cv = (CellValue) vci.next();
                CellFeatures cf = cv.getCellFeatures();
                if (cf.getDVParser() != null) {
                    cf.getDVParser().insertColumn(col);
                }
            }
        }
        this.mergedCells.insertColumn(col);
        ArrayList newColumnBreaks = new ArrayList();
        Iterator ri = this.columnBreaks.iterator();
        while (ri.hasNext()) {
            int val = ((Integer) ri.next()).intValue();
            if (val >= col) {
                val++;
            }
            newColumnBreaks.add(new Integer(val));
        }
        this.columnBreaks = newColumnBreaks;
        Iterator cfit = this.conditionalFormats.iterator();
        while (cfit.hasNext()) {
            ((ConditionalFormat) cfit.next()).insertColumn(col);
        }
        if (this.workbookSettings.getFormulaAdjust()) {
            this.workbook.columnInserted(this, col);
        }
        this.numColumns++;
    }

    @Override // jxl.write.WritableSheet
    public void removeColumn(int col) {
        if (col < 0 || col >= this.numColumns) {
            return;
        }
        for (int i = 0; i < this.numRows; i++) {
            RowRecord[] rowRecordArr = this.rows;
            if (rowRecordArr[i] != null) {
                rowRecordArr[i].removeColumn(col);
            }
        }
        Iterator i2 = this.hyperlinks.iterator();
        while (i2.hasNext()) {
            HyperlinkRecord hr = (HyperlinkRecord) i2.next();
            if (hr.getColumn() == col && hr.getLastColumn() == col) {
                i2.remove();
            } else {
                hr.removeColumn(col);
            }
        }
        DataValidation dataValidation = this.dataValidation;
        if (dataValidation != null) {
            dataValidation.removeColumn(col);
        }
        ArrayList arrayList = this.validatedCells;
        if (arrayList != null && arrayList.size() > 0) {
            Iterator vci = this.validatedCells.iterator();
            while (vci.hasNext()) {
                CellValue cv = (CellValue) vci.next();
                CellFeatures cf = cv.getCellFeatures();
                if (cf.getDVParser() != null) {
                    cf.getDVParser().removeColumn(col);
                }
            }
        }
        this.mergedCells.removeColumn(col);
        ArrayList newColumnBreaks = new ArrayList();
        Iterator ri = this.columnBreaks.iterator();
        while (ri.hasNext()) {
            int val = ((Integer) ri.next()).intValue();
            if (val != col) {
                if (val > col) {
                    val--;
                }
                newColumnBreaks.add(new Integer(val));
            }
        }
        this.columnBreaks = newColumnBreaks;
        Iterator i3 = this.columnFormats.iterator();
        ColumnInfoRecord removeColumn = null;
        while (i3.hasNext()) {
            ColumnInfoRecord cir = (ColumnInfoRecord) i3.next();
            if (cir.getColumn() == col) {
                removeColumn = cir;
            } else if (cir.getColumn() > col) {
                cir.decrementColumn();
            }
        }
        if (removeColumn != null) {
            this.columnFormats.remove(removeColumn);
        }
        if (this.autosizedColumns.size() > 0) {
            TreeSet newAutosized = new TreeSet();
            Iterator i4 = this.autosizedColumns.iterator();
            while (i4.hasNext()) {
                Integer colnumber = (Integer) i4.next();
                if (colnumber.intValue() != col) {
                    if (colnumber.intValue() > col) {
                        newAutosized.add(new Integer(colnumber.intValue() - 1));
                    } else {
                        newAutosized.add(colnumber);
                    }
                }
            }
            this.autosizedColumns = newAutosized;
        }
        Iterator cfit = this.conditionalFormats.iterator();
        while (cfit.hasNext()) {
            ((ConditionalFormat) cfit.next()).removeColumn(col);
        }
        if (this.workbookSettings.getFormulaAdjust()) {
            this.workbook.columnRemoved(this, col);
        }
        this.numColumns--;
    }

    @Override // jxl.write.WritableSheet
    public void removeRow(int row) {
        if (row < 0 || row >= this.numRows) {
            if (this.workbookSettings.getFormulaAdjust()) {
                this.workbook.rowRemoved(this, row);
                return;
            }
            return;
        }
        RowRecord[] oldRows = this.rows;
        RowRecord[] rowRecordArr = new RowRecord[oldRows.length];
        this.rows = rowRecordArr;
        System.arraycopy(oldRows, 0, rowRecordArr, 0, row);
        System.arraycopy(oldRows, row + 1, this.rows, row, this.numRows - (row + 1));
        for (int i = row; i < this.numRows; i++) {
            RowRecord[] rowRecordArr2 = this.rows;
            if (rowRecordArr2[i] != null) {
                rowRecordArr2[i].decrementRow();
            }
        }
        Iterator i2 = this.hyperlinks.iterator();
        while (i2.hasNext()) {
            HyperlinkRecord hr = (HyperlinkRecord) i2.next();
            if (hr.getRow() == row && hr.getLastRow() == row) {
                i2.remove();
            } else {
                hr.removeRow(row);
            }
        }
        DataValidation dataValidation = this.dataValidation;
        if (dataValidation != null) {
            dataValidation.removeRow(row);
        }
        ArrayList arrayList = this.validatedCells;
        if (arrayList != null && arrayList.size() > 0) {
            Iterator vci = this.validatedCells.iterator();
            while (vci.hasNext()) {
                CellValue cv = (CellValue) vci.next();
                CellFeatures cf = cv.getCellFeatures();
                if (cf.getDVParser() != null) {
                    cf.getDVParser().removeRow(row);
                }
            }
        }
        this.mergedCells.removeRow(row);
        ArrayList newRowBreaks = new ArrayList();
        Iterator ri = this.rowBreaks.iterator();
        while (ri.hasNext()) {
            int val = ((Integer) ri.next()).intValue();
            if (val != row) {
                if (val > row) {
                    val--;
                }
                newRowBreaks.add(new Integer(val));
            }
        }
        this.rowBreaks = newRowBreaks;
        Iterator cfit = this.conditionalFormats.iterator();
        while (cfit.hasNext()) {
            ((ConditionalFormat) cfit.next()).removeRow(row);
        }
        if (this.workbookSettings.getFormulaAdjust()) {
            this.workbook.rowRemoved(this, row);
        }
        this.numRows--;
    }

    @Override // jxl.write.WritableSheet
    public void addCell(WritableCell cell) throws WriteException, RowsExceededException {
        if (cell.getType() == CellType.EMPTY && cell != null && cell.getCellFormat() == null) {
            return;
        }
        CellValue cv = (CellValue) cell;
        if (cv.isReferenced()) {
            throw new JxlWriteException(JxlWriteException.cellReferenced);
        }
        int row = cell.getRow();
        RowRecord rowrec = getRowRecord(row);
        CellValue curcell = rowrec.getCell(cv.getColumn());
        boolean curSharedValidation = (curcell == null || curcell.getCellFeatures() == null || curcell.getCellFeatures().getDVParser() == null || !curcell.getCellFeatures().getDVParser().extendedCellsValidation()) ? false : true;
        if (cell.getCellFeatures() != null && cell.getCellFeatures().hasDataValidation() && curSharedValidation) {
            DVParser dvp = curcell.getCellFeatures().getDVParser();
            logger.warn("Cannot add cell at " + CellReferenceHelper.getCellReference(cv) + " because it is part of the shared cell validation group " + CellReferenceHelper.getCellReference(dvp.getFirstColumn(), dvp.getFirstRow()) + "-" + CellReferenceHelper.getCellReference(dvp.getLastColumn(), dvp.getLastRow()));
            return;
        }
        if (curSharedValidation) {
            WritableCellFeatures wcf = cell.getWritableCellFeatures();
            if (wcf == null) {
                wcf = new WritableCellFeatures();
                cell.setCellFeatures(wcf);
            }
            wcf.shareDataValidation(curcell.getCellFeatures());
        }
        rowrec.addCell(cv);
        this.numRows = Math.max(row + 1, this.numRows);
        this.numColumns = Math.max(this.numColumns, rowrec.getMaxColumn());
        cv.setCellDetails(this.formatRecords, this.sharedStrings, this);
    }

    RowRecord getRowRecord(int row) throws RowsExceededException {
        if (row >= 65536) {
            throw new RowsExceededException();
        }
        if (row >= this.rows.length) {
            RowRecord[] oldRows = this.rows;
            RowRecord[] rowRecordArr = new RowRecord[Math.max(oldRows.length + 10, row + 1)];
            this.rows = rowRecordArr;
            System.arraycopy(oldRows, 0, rowRecordArr, 0, oldRows.length);
        }
        RowRecord rowrec = this.rows[row];
        if (rowrec == null) {
            RowRecord rowrec2 = new RowRecord(row, this);
            this.rows[row] = rowrec2;
            return rowrec2;
        }
        return rowrec;
    }

    RowRecord getRowInfo(int r) {
        if (r < 0) {
            return null;
        }
        RowRecord[] rowRecordArr = this.rows;
        if (r > rowRecordArr.length) {
            return null;
        }
        return rowRecordArr[r];
    }

    ColumnInfoRecord getColumnInfo(int c) {
        Iterator i = this.columnFormats.iterator();
        ColumnInfoRecord cir = null;
        boolean stop = false;
        while (i.hasNext() && !stop) {
            cir = (ColumnInfoRecord) i.next();
            if (cir.getColumn() >= c) {
                stop = true;
            }
        }
        if (stop && cir.getColumn() == c) {
            return cir;
        }
        return null;
    }

    @Override // jxl.write.WritableSheet
    public void setName(String n) {
        this.name = n;
    }

    @Override // jxl.write.WritableSheet
    public void setHidden(boolean h) {
        this.settings.setHidden(h);
    }

    @Override // jxl.write.WritableSheet
    public void setProtected(boolean prot) {
        this.settings.setProtected(prot);
    }

    public void setSelected() {
        this.settings.setSelected();
    }

    @Override // jxl.Sheet
    public boolean isHidden() {
        return this.settings.isHidden();
    }

    @Override // jxl.write.WritableSheet
    public void setColumnView(int col, int width) {
        CellView cv = new CellView();
        cv.setSize(width * 256);
        setColumnView(col, cv);
    }

    @Override // jxl.write.WritableSheet
    public void setColumnView(int col, int width, CellFormat format) {
        CellView cv = new CellView();
        cv.setSize(width * 256);
        cv.setFormat(format);
        setColumnView(col, cv);
    }

    @Override // jxl.write.WritableSheet
    public void setColumnView(int col, CellView view) {
        XFRecord xfr = (XFRecord) view.getFormat();
        if (xfr == null) {
            Styles styles = getWorkbook().getStyles();
            xfr = styles.getNormalStyle();
        }
        try {
            if (!xfr.isInitialized()) {
                this.formatRecords.addStyle(xfr);
            }
            int width = view.depUsed() ? view.getDimension() * 256 : view.getSize();
            if (view.isAutosize()) {
                this.autosizedColumns.add(new Integer(col));
            }
            ColumnInfoRecord cir = new ColumnInfoRecord(col, width, xfr);
            if (view.isHidden()) {
                cir.setHidden(true);
            }
            if (!this.columnFormats.contains(cir)) {
                this.columnFormats.add(cir);
            } else {
                this.columnFormats.remove(cir);
                this.columnFormats.add(cir);
            }
        } catch (NumFormatRecordsException e) {
            logger.warn("Maximum number of format records exceeded.  Using default format.");
            ColumnInfoRecord cir2 = new ColumnInfoRecord(col, view.getDimension() * 256, WritableWorkbook.NORMAL_STYLE);
            if (!this.columnFormats.contains(cir2)) {
                this.columnFormats.add(cir2);
            }
        }
    }

    @Override // jxl.write.WritableSheet
    public void setRowView(int row, int height) throws RowsExceededException {
        CellView cv = new CellView();
        cv.setSize(height);
        cv.setHidden(false);
        setRowView(row, cv);
    }

    @Override // jxl.write.WritableSheet
    public void setRowView(int row, boolean collapsed) throws RowsExceededException {
        CellView cv = new CellView();
        cv.setHidden(collapsed);
        setRowView(row, cv);
    }

    @Override // jxl.write.WritableSheet
    public void setRowView(int row, int height, boolean collapsed) throws RowsExceededException {
        CellView cv = new CellView();
        cv.setSize(height);
        cv.setHidden(collapsed);
        setRowView(row, cv);
    }

    @Override // jxl.write.WritableSheet
    public void setRowView(int row, CellView view) throws RowsExceededException {
        XFRecord xfr;
        RowRecord rowrec = getRowRecord(row);
        XFRecord xfr2 = (XFRecord) view.getFormat();
        if (xfr2 != null) {
            try {
                if (!xfr2.isInitialized()) {
                    this.formatRecords.addStyle(xfr2);
                }
            } catch (NumFormatRecordsException e) {
                logger.warn("Maximum number of format records exceeded.  Using default format.");
                xfr = null;
            }
        }
        xfr = xfr2;
        rowrec.setRowDetails(view.getSize(), false, view.isHidden(), 0, false, xfr);
        this.numRows = Math.max(this.numRows, row + 1);
    }

    public void write() throws IOException {
        boolean dmod = this.drawingsModified;
        if (this.workbook.getDrawingGroup() != null) {
            dmod |= this.workbook.getDrawingGroup().hasDrawingsOmitted();
        }
        if (this.autosizedColumns.size() > 0) {
            autosizeColumns();
        }
        this.sheetWriter.setWriteData(this.rows, this.rowBreaks, this.columnBreaks, this.hyperlinks, this.mergedCells, this.columnFormats, this.maxRowOutlineLevel, this.maxColumnOutlineLevel);
        this.sheetWriter.setDimensions(getRows(), getColumns());
        this.sheetWriter.setSettings(this.settings);
        this.sheetWriter.setPLS(this.plsRecord);
        this.sheetWriter.setDrawings(this.drawings, dmod);
        this.sheetWriter.setButtonPropertySet(this.buttonPropertySet);
        this.sheetWriter.setDataValidation(this.dataValidation, this.validatedCells);
        this.sheetWriter.setConditionalFormats(this.conditionalFormats);
        this.sheetWriter.setAutoFilter(this.autoFilter);
        this.sheetWriter.write();
    }

    void copy(Sheet s) {
        this.settings = new SheetSettings(s.getSettings(), this);
        SheetCopier si = new SheetCopier(s, this);
        si.setColumnFormats(this.columnFormats);
        si.setFormatRecords(this.formatRecords);
        si.setHyperlinks(this.hyperlinks);
        si.setMergedCells(this.mergedCells);
        si.setRowBreaks(this.rowBreaks);
        si.setColumnBreaks(this.columnBreaks);
        si.setSheetWriter(this.sheetWriter);
        si.setDrawings(this.drawings);
        si.setImages(this.images);
        si.setConditionalFormats(this.conditionalFormats);
        si.setValidatedCells(this.validatedCells);
        si.copySheet();
        this.dataValidation = si.getDataValidation();
        this.comboBox = si.getComboBox();
        this.plsRecord = si.getPLSRecord();
        this.chartOnly = si.isChartOnly();
        this.buttonPropertySet = si.getButtonPropertySet();
        this.numRows = si.getRows();
        this.autoFilter = si.getAutoFilter();
        this.maxRowOutlineLevel = si.getMaxRowOutlineLevel();
        this.maxColumnOutlineLevel = si.getMaxColumnOutlineLevel();
    }

    void copy(WritableSheet s) {
        this.settings = new SheetSettings(s.getSettings(), this);
        WritableSheetImpl si = (WritableSheetImpl) s;
        WritableSheetCopier sc = new WritableSheetCopier(s, this);
        sc.setColumnFormats(si.columnFormats, this.columnFormats);
        sc.setMergedCells(si.mergedCells, this.mergedCells);
        sc.setRows(si.rows);
        sc.setRowBreaks(si.rowBreaks, this.rowBreaks);
        sc.setColumnBreaks(si.columnBreaks, this.columnBreaks);
        sc.setDataValidation(si.dataValidation);
        sc.setSheetWriter(this.sheetWriter);
        sc.setDrawings(si.drawings, this.drawings, this.images);
        sc.setWorkspaceOptions(si.getWorkspaceOptions());
        sc.setPLSRecord(si.plsRecord);
        sc.setButtonPropertySetRecord(si.buttonPropertySet);
        sc.setHyperlinks(si.hyperlinks, this.hyperlinks);
        sc.setValidatedCells(this.validatedCells);
        sc.copySheet();
        this.dataValidation = sc.getDataValidation();
        this.plsRecord = sc.getPLSRecord();
        this.buttonPropertySet = sc.getButtonPropertySet();
    }

    final HeaderRecord getHeader() {
        return this.sheetWriter.getHeader();
    }

    final FooterRecord getFooter() {
        return this.sheetWriter.getFooter();
    }

    @Override // jxl.Sheet
    public boolean isProtected() {
        return this.settings.isProtected();
    }

    @Override // jxl.Sheet
    public Hyperlink[] getHyperlinks() {
        Hyperlink[] hl = new Hyperlink[this.hyperlinks.size()];
        for (int i = 0; i < this.hyperlinks.size(); i++) {
            hl[i] = (Hyperlink) this.hyperlinks.get(i);
        }
        return hl;
    }

    @Override // jxl.Sheet
    public Range[] getMergedCells() {
        return this.mergedCells.getMergedCells();
    }

    @Override // jxl.write.WritableSheet
    public WritableHyperlink[] getWritableHyperlinks() {
        WritableHyperlink[] hl = new WritableHyperlink[this.hyperlinks.size()];
        for (int i = 0; i < this.hyperlinks.size(); i++) {
            hl[i] = (WritableHyperlink) this.hyperlinks.get(i);
        }
        return hl;
    }

    @Override // jxl.write.WritableSheet
    public void removeHyperlink(WritableHyperlink h) {
        removeHyperlink(h, false);
    }

    @Override // jxl.write.WritableSheet
    public void removeHyperlink(WritableHyperlink h, boolean preserveLabel) {
        ArrayList arrayList = this.hyperlinks;
        arrayList.remove(arrayList.indexOf(h));
        if (!preserveLabel) {
            Assert.verify(this.rows.length > h.getRow() && this.rows[h.getRow()] != null);
            this.rows[h.getRow()].removeCell(h.getColumn());
        }
    }

    @Override // jxl.write.WritableSheet
    public void addHyperlink(WritableHyperlink h) throws WriteException, RowsExceededException {
        Cell c = getCell(h.getColumn(), h.getRow());
        String contents = null;
        if (h.isFile() || h.isUNC()) {
            String cnts = h.getContents();
            if (cnts == null) {
                contents = h.getFile().getPath();
            } else {
                contents = cnts;
            }
        } else if (h.isURL()) {
            String cnts2 = h.getContents();
            if (cnts2 == null) {
                contents = h.getURL().toString();
            } else {
                contents = cnts2;
            }
        } else if (h.isLocation()) {
            contents = h.getContents();
        }
        if (c.getType() == CellType.LABEL) {
            Label l = (Label) c;
            l.setString(contents);
            WritableCellFormat wcf = new WritableCellFormat(l.getCellFormat());
            wcf.setFont(WritableWorkbook.HYPERLINK_FONT);
            l.setCellFormat(wcf);
        } else {
            addCell(new Label(h.getColumn(), h.getRow(), contents, WritableWorkbook.HYPERLINK_STYLE));
        }
        for (int i = h.getRow(); i <= h.getLastRow(); i++) {
            for (int j = h.getColumn(); j <= h.getLastColumn(); j++) {
                if (i != h.getRow() && j != h.getColumn() && this.rows.length < h.getLastColumn()) {
                    RowRecord[] rowRecordArr = this.rows;
                    if (rowRecordArr[i] != null) {
                        rowRecordArr[i].removeCell(j);
                    }
                }
            }
        }
        h.initialize(this);
        this.hyperlinks.add(h);
    }

    @Override // jxl.write.WritableSheet
    public Range mergeCells(int col1, int row1, int col2, int row2) throws WriteException, RowsExceededException {
        if (col2 < col1 || row2 < row1) {
            logger.warn("Cannot merge cells - top left and bottom right incorrectly specified");
        }
        if (col2 >= this.numColumns || row2 >= this.numRows) {
            addCell(new Blank(col2, row2));
        }
        SheetRangeImpl range = new SheetRangeImpl(this, col1, row1, col2, row2);
        this.mergedCells.add(range);
        return range;
    }

    @Override // jxl.write.WritableSheet
    public void setRowGroup(int row1, int row2, boolean collapsed) throws WriteException, RowsExceededException {
        if (row2 < row1) {
            logger.warn("Cannot merge cells - top and bottom rows incorrectly specified");
        }
        for (int i = row1; i <= row2; i++) {
            RowRecord row = getRowRecord(i);
            this.numRows = Math.max(i + 1, this.numRows);
            row.incrementOutlineLevel();
            row.setCollapsed(collapsed);
            this.maxRowOutlineLevel = Math.max(this.maxRowOutlineLevel, row.getOutlineLevel());
        }
    }

    @Override // jxl.write.WritableSheet
    public void unsetRowGroup(int row1, int row2) throws WriteException, RowsExceededException {
        if (row2 < row1) {
            logger.warn("Cannot merge cells - top and bottom rows incorrectly specified");
        }
        if (row2 >= this.numRows) {
            logger.warn("" + row2 + " is greater than the sheet bounds");
            row2 = this.numRows + (-1);
        }
        for (int i = row1; i <= row2; i++) {
            this.rows[i].decrementOutlineLevel();
        }
        this.maxRowOutlineLevel = 0;
        int i2 = this.rows.length;
        while (true) {
            int i3 = i2 - 1;
            if (i2 > 0) {
                this.maxRowOutlineLevel = Math.max(this.maxRowOutlineLevel, this.rows[i3].getOutlineLevel());
                i2 = i3;
            } else {
                return;
            }
        }
    }

    @Override // jxl.write.WritableSheet
    public void setColumnGroup(int col1, int col2, boolean collapsed) throws WriteException, RowsExceededException {
        if (col2 < col1) {
            logger.warn("Cannot merge cells - top and bottom rows incorrectly specified");
        }
        for (int i = col1; i <= col2; i++) {
            ColumnInfoRecord cir = getColumnInfo(i);
            if (cir == null) {
                setColumnView(i, new CellView());
                cir = getColumnInfo(i);
            }
            cir.incrementOutlineLevel();
            cir.setCollapsed(collapsed);
            this.maxColumnOutlineLevel = Math.max(this.maxColumnOutlineLevel, cir.getOutlineLevel());
        }
    }

    @Override // jxl.write.WritableSheet
    public void unsetColumnGroup(int col1, int col2) throws WriteException, RowsExceededException {
        if (col2 < col1) {
            logger.warn("Cannot merge cells - top and bottom rows incorrectly specified");
        }
        for (int i = col1; i <= col2; i++) {
            ColumnInfoRecord cir = getColumnInfo(i);
            cir.decrementOutlineLevel();
        }
        this.maxColumnOutlineLevel = 0;
        Iterator it = this.columnFormats.iterator();
        while (it.hasNext()) {
            ColumnInfoRecord cir2 = (ColumnInfoRecord) it.next();
            this.maxColumnOutlineLevel = Math.max(this.maxColumnOutlineLevel, cir2.getOutlineLevel());
        }
    }

    @Override // jxl.write.WritableSheet
    public void unmergeCells(Range r) {
        this.mergedCells.unmergeCells(r);
    }

    @Override // jxl.write.WritableSheet
    public void setHeader(String l, String c, String r) {
        HeaderFooter header = new HeaderFooter();
        header.getLeft().append(l);
        header.getCentre().append(c);
        header.getRight().append(r);
        this.settings.setHeader(header);
    }

    @Override // jxl.write.WritableSheet
    public void setFooter(String l, String c, String r) {
        HeaderFooter footer = new HeaderFooter();
        footer.getLeft().append(l);
        footer.getCentre().append(c);
        footer.getRight().append(r);
        this.settings.setFooter(footer);
    }

    @Override // jxl.write.WritableSheet
    public void setPageSetup(PageOrientation p) {
        this.settings.setOrientation(p);
    }

    @Override // jxl.write.WritableSheet
    public void setPageSetup(PageOrientation p, double hm, double fm) {
        this.settings.setOrientation(p);
        this.settings.setHeaderMargin(hm);
        this.settings.setFooterMargin(fm);
    }

    @Override // jxl.write.WritableSheet
    public void setPageSetup(PageOrientation p, PaperSize ps, double hm, double fm) {
        this.settings.setPaperSize(ps);
        this.settings.setOrientation(p);
        this.settings.setHeaderMargin(hm);
        this.settings.setFooterMargin(fm);
    }

    @Override // jxl.Sheet
    public SheetSettings getSettings() {
        return this.settings;
    }

    WorkbookSettings getWorkbookSettings() {
        return this.workbookSettings;
    }

    @Override // jxl.write.WritableSheet
    public void addRowPageBreak(int row) {
        Iterator i = this.rowBreaks.iterator();
        boolean found = false;
        while (i.hasNext() && !found) {
            if (((Integer) i.next()).intValue() == row) {
                found = true;
            }
        }
        if (!found) {
            this.rowBreaks.add(new Integer(row));
        }
    }

    @Override // jxl.write.WritableSheet
    public void addColumnPageBreak(int col) {
        Iterator i = this.columnBreaks.iterator();
        boolean found = false;
        while (i.hasNext() && !found) {
            if (((Integer) i.next()).intValue() == col) {
                found = true;
            }
        }
        if (!found) {
            this.columnBreaks.add(new Integer(col));
        }
    }

    Chart[] getCharts() {
        return this.sheetWriter.getCharts();
    }

    private DrawingGroupObject[] getDrawings() {
        DrawingGroupObject[] dr = new DrawingGroupObject[this.drawings.size()];
        return (DrawingGroupObject[]) this.drawings.toArray(dr);
    }

    void checkMergedBorders() {
        this.sheetWriter.setWriteData(this.rows, this.rowBreaks, this.columnBreaks, this.hyperlinks, this.mergedCells, this.columnFormats, this.maxRowOutlineLevel, this.maxColumnOutlineLevel);
        this.sheetWriter.setDimensions(getRows(), getColumns());
        this.sheetWriter.checkMergedBorders();
    }

    private WorkspaceInformationRecord getWorkspaceOptions() {
        return this.sheetWriter.getWorkspaceOptions();
    }

    void rationalize(IndexMapping xfMapping, IndexMapping fontMapping, IndexMapping formatMapping) {
        Iterator i = this.columnFormats.iterator();
        while (i.hasNext()) {
            ColumnInfoRecord cir = (ColumnInfoRecord) i.next();
            cir.rationalize(xfMapping);
        }
        int i2 = 0;
        while (true) {
            RowRecord[] rowRecordArr = this.rows;
            if (i2 >= rowRecordArr.length) {
                break;
            }
            if (rowRecordArr[i2] != null) {
                rowRecordArr[i2].rationalize(xfMapping);
            }
            i2++;
        }
        Chart[] charts = getCharts();
        for (Chart chart : charts) {
            chart.rationalize(xfMapping, fontMapping, formatMapping);
        }
    }

    WritableWorkbookImpl getWorkbook() {
        return this.workbook;
    }

    @Override // jxl.Sheet
    public CellFormat getColumnFormat(int col) {
        return getColumnView(col).getFormat();
    }

    @Override // jxl.Sheet
    public int getColumnWidth(int col) {
        return getColumnView(col).getDimension();
    }

    @Override // jxl.Sheet
    public int getRowHeight(int row) {
        return getRowView(row).getDimension();
    }

    boolean isChartOnly() {
        return this.chartOnly;
    }

    @Override // jxl.Sheet
    public CellView getRowView(int row) {
        CellView cv = new CellView();
        try {
            RowRecord rr = getRowRecord(row);
            if (rr != null && !rr.isDefaultHeight()) {
                if (rr.isCollapsed()) {
                    cv.setHidden(true);
                } else {
                    cv.setDimension(rr.getRowHeight());
                    cv.setSize(rr.getRowHeight());
                }
                return cv;
            }
            cv.setDimension(this.settings.getDefaultRowHeight());
            cv.setSize(this.settings.getDefaultRowHeight());
            return cv;
        } catch (RowsExceededException e) {
            cv.setDimension(this.settings.getDefaultRowHeight());
            cv.setSize(this.settings.getDefaultRowHeight());
            return cv;
        }
    }

    @Override // jxl.Sheet
    public CellView getColumnView(int col) {
        ColumnInfoRecord cir = getColumnInfo(col);
        CellView cv = new CellView();
        if (cir != null) {
            cv.setDimension(cir.getWidth() / 256);
            cv.setSize(cir.getWidth());
            cv.setHidden(cir.getHidden());
            cv.setFormat(cir.getCellFormat());
        } else {
            cv.setDimension(this.settings.getDefaultColumnWidth() / 256);
            cv.setSize(this.settings.getDefaultColumnWidth() * 256);
        }
        return cv;
    }

    @Override // jxl.write.WritableSheet
    public void addImage(WritableImage image) {
        boolean supported = false;
        java.io.File imageFile = image.getImageFile();
        String fileType = LocationInfo.NA;
        if (imageFile != null) {
            String fileName = imageFile.getName();
            int fileTypeIndex = fileName.lastIndexOf(46);
            fileType = fileTypeIndex != -1 ? fileName.substring(fileTypeIndex + 1) : "";
            int i = 0;
            while (true) {
                String[] strArr = imageTypes;
                if (i >= strArr.length || supported) {
                    break;
                }
                if (fileType.equalsIgnoreCase(strArr[i])) {
                    supported = true;
                }
                i++;
            }
        } else {
            supported = true;
        }
        if (supported) {
            this.workbook.addDrawing(image);
            this.drawings.add(image);
            this.images.add(image);
            return;
        }
        StringBuffer message = new StringBuffer("Image type ");
        message.append(fileType);
        message.append(" not supported.  Supported types are ");
        message.append(imageTypes[0]);
        int i2 = 1;
        while (true) {
            String[] strArr2 = imageTypes;
            if (i2 < strArr2.length) {
                message.append(", ");
                message.append(strArr2[i2]);
                i2++;
            } else {
                logger.warn(message.toString());
                return;
            }
        }
    }

    @Override // jxl.write.WritableSheet, jxl.Sheet
    public int getNumberOfImages() {
        return this.images.size();
    }

    @Override // jxl.write.WritableSheet
    public WritableImage getImage(int i) {
        return (WritableImage) this.images.get(i);
    }

    @Override // jxl.Sheet
    public Image getDrawing(int i) {
        return (Image) this.images.get(i);
    }

    @Override // jxl.write.WritableSheet
    public void removeImage(WritableImage wi) {
        this.drawings.remove(wi);
        this.images.remove(wi);
        this.drawingsModified = true;
        this.workbook.removeDrawing(wi);
    }

    private String validateName(String n) {
        if (n.length() > 31) {
            logger.warn("Sheet name " + n + " too long - truncating");
            n = n.substring(0, 31);
        }
        if (n.charAt(0) == '\'') {
            logger.warn("Sheet naming cannot start with ' - removing");
            n = n.substring(1);
        }
        int i = 0;
        while (true) {
            char[] cArr = illegalSheetNameCharacters;
            if (i < cArr.length) {
                String newname = n.replace(cArr[i], '@');
                if (n != newname) {
                    logger.warn(cArr[i] + " is not a valid character within a sheet name - replacing");
                }
                n = newname;
                i++;
            } else {
                return n;
            }
        }
    }

    void addDrawing(DrawingGroupObject o) {
        this.drawings.add(o);
        Assert.verify(!(o instanceof Drawing));
    }

    void removeDrawing(DrawingGroupObject o) {
        int origSize = this.drawings.size();
        this.drawings.remove(o);
        int newSize = this.drawings.size();
        this.drawingsModified = true;
        Assert.verify(newSize == origSize + (-1));
    }

    void removeDataValidation(CellValue cv) {
        DataValidation dataValidation = this.dataValidation;
        if (dataValidation != null) {
            dataValidation.removeDataValidation(cv.getColumn(), cv.getRow());
        }
        ArrayList arrayList = this.validatedCells;
        if (arrayList != null) {
            boolean result = arrayList.remove(cv);
            if (!result) {
                logger.warn("Could not remove validated cell " + CellReferenceHelper.getCellReference(cv));
            }
        }
    }

    @Override // jxl.Sheet
    public int[] getRowPageBreaks() {
        int[] rb = new int[this.rowBreaks.size()];
        int pos = 0;
        Iterator i = this.rowBreaks.iterator();
        while (i.hasNext()) {
            rb[pos] = ((Integer) i.next()).intValue();
            pos++;
        }
        return rb;
    }

    @Override // jxl.Sheet
    public int[] getColumnPageBreaks() {
        int[] rb = new int[this.columnBreaks.size()];
        int pos = 0;
        Iterator i = this.columnBreaks.iterator();
        while (i.hasNext()) {
            rb[pos] = ((Integer) i.next()).intValue();
            pos++;
        }
        return rb;
    }

    void addValidationCell(CellValue cv) {
        this.validatedCells.add(cv);
    }

    ComboBox getComboBox() {
        return this.comboBox;
    }

    void setComboBox(ComboBox cb) {
        this.comboBox = cb;
    }

    public DataValidation getDataValidation() {
        return this.dataValidation;
    }

    private void autosizeColumns() {
        Iterator i = this.autosizedColumns.iterator();
        while (i.hasNext()) {
            Integer col = (Integer) i.next();
            autosizeColumn(col.intValue());
        }
    }

    private void autosizeColumn(int col) {
        int maxWidth = 0;
        ColumnInfoRecord cir = getColumnInfo(col);
        Font columnFont = cir.getCellFormat().getFont();
        Font defaultFont = WritableWorkbook.NORMAL_STYLE.getFont();
        for (int i = 0; i < this.numRows; i++) {
            Cell cell = null;
            RowRecord[] rowRecordArr = this.rows;
            if (rowRecordArr[i] != null) {
                cell = rowRecordArr[i].getCell(col);
            }
            if (cell != null) {
                String contents = cell.getContents();
                Font font = cell.getCellFormat().getFont();
                Font activeFont = font.equals(defaultFont) ? columnFont : font;
                int pointSize = activeFont.getPointSize();
                int numChars = contents.length();
                if (activeFont.isItalic() || activeFont.getBoldWeight() > 400) {
                    numChars += 2;
                }
                int points = numChars * pointSize;
                maxWidth = Math.max(maxWidth, points * 256);
            }
        }
        int i2 = defaultFont.getPointSize();
        cir.setWidth(maxWidth / i2);
    }

    void importSheet(Sheet s) {
        this.settings = new SheetSettings(s.getSettings(), this);
        SheetCopier si = new SheetCopier(s, this);
        si.setColumnFormats(this.columnFormats);
        si.setFormatRecords(this.formatRecords);
        si.setHyperlinks(this.hyperlinks);
        si.setMergedCells(this.mergedCells);
        si.setRowBreaks(this.rowBreaks);
        si.setColumnBreaks(this.columnBreaks);
        si.setSheetWriter(this.sheetWriter);
        si.setDrawings(this.drawings);
        si.setImages(this.images);
        si.setValidatedCells(this.validatedCells);
        si.importSheet();
        this.dataValidation = si.getDataValidation();
        this.comboBox = si.getComboBox();
        this.plsRecord = si.getPLSRecord();
        this.chartOnly = si.isChartOnly();
        this.buttonPropertySet = si.getButtonPropertySet();
        this.numRows = si.getRows();
        this.maxRowOutlineLevel = si.getMaxRowOutlineLevel();
        this.maxColumnOutlineLevel = si.getMaxColumnOutlineLevel();
    }

    @Override // jxl.write.WritableSheet
    public void applySharedDataValidation(WritableCell c, int extraCols, int extraRows) throws WriteException {
        WritableCell c2;
        if (c.getWritableCellFeatures() == null || !c.getWritableCellFeatures().hasDataValidation()) {
            logger.warn("Cannot extend data validation for " + CellReferenceHelper.getCellReference(c.getColumn(), c.getRow()) + " as it has no data validation");
            return;
        }
        int startColumn = c.getColumn();
        int startRow = c.getRow();
        int endRow = Math.min(this.numRows - 1, startRow + extraRows);
        for (int y = startRow; y <= endRow; y++) {
            if (this.rows[y] != null) {
                int endCol = Math.min(r4[y].getMaxColumn() - 1, startColumn + extraCols);
                for (int x = startColumn; x <= endCol; x++) {
                    if ((x != startColumn || y != startRow) && (c2 = this.rows[y].getCell(x)) != null && c2.getWritableCellFeatures() != null && c2.getWritableCellFeatures().hasDataValidation()) {
                        logger.warn("Cannot apply data validation from " + CellReferenceHelper.getCellReference(startColumn, startRow) + " to " + CellReferenceHelper.getCellReference(startColumn + extraCols, startRow + extraRows) + " as cell " + CellReferenceHelper.getCellReference(x, y) + " already has a data validation");
                        return;
                    }
                }
            }
        }
        WritableCellFeatures sourceDataValidation = c.getWritableCellFeatures();
        sourceDataValidation.getDVParser().extendCellValidation(extraCols, extraRows);
        for (int y2 = startRow; y2 <= startRow + extraRows; y2++) {
            RowRecord rowrec = getRowRecord(y2);
            for (int x2 = startColumn; x2 <= startColumn + extraCols; x2++) {
                if (x2 != startColumn || y2 != startRow) {
                    WritableCell c22 = rowrec.getCell(x2);
                    if (c22 == null) {
                        Blank b = new Blank(x2, y2);
                        WritableCellFeatures validation = new WritableCellFeatures();
                        validation.shareDataValidation(sourceDataValidation);
                        b.setCellFeatures(validation);
                        addCell(b);
                    } else {
                        WritableCellFeatures validation2 = c22.getWritableCellFeatures();
                        if (validation2 != null) {
                            validation2.shareDataValidation(sourceDataValidation);
                        } else {
                            WritableCellFeatures validation3 = new WritableCellFeatures();
                            validation3.shareDataValidation(sourceDataValidation);
                            c22.setCellFeatures(validation3);
                        }
                    }
                }
            }
        }
    }

    @Override // jxl.write.WritableSheet
    public void removeSharedDataValidation(WritableCell cell) throws WriteException {
        WritableCellFeatures wcf = cell.getWritableCellFeatures();
        if (wcf == null || !wcf.hasDataValidation()) {
            return;
        }
        DVParser dvp = wcf.getDVParser();
        if (!dvp.extendedCellsValidation()) {
            wcf.removeDataValidation();
            return;
        }
        if (dvp.extendedCellsValidation() && (cell.getColumn() != dvp.getFirstColumn() || cell.getRow() != dvp.getFirstRow())) {
            logger.warn("Cannot remove data validation from " + CellReferenceHelper.getCellReference(dvp.getFirstColumn(), dvp.getFirstRow()) + "-" + CellReferenceHelper.getCellReference(dvp.getLastColumn(), dvp.getLastRow()) + " because the selected cell " + CellReferenceHelper.getCellReference(cell) + " is not the top left cell in the range");
            return;
        }
        for (int y = dvp.getFirstRow(); y <= dvp.getLastRow(); y++) {
            for (int x = dvp.getFirstColumn(); x <= dvp.getLastColumn(); x++) {
                CellValue c2 = this.rows[y].getCell(x);
                if (c2 != null) {
                    c2.getWritableCellFeatures().removeSharedDataValidation();
                    c2.removeCellFeatures();
                }
            }
        }
        DataValidation dataValidation = this.dataValidation;
        if (dataValidation != null) {
            dataValidation.removeSharedDataValidation(dvp.getFirstColumn(), dvp.getFirstRow(), dvp.getLastColumn(), dvp.getLastRow());
        }
    }
}
