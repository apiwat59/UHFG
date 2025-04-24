package jxl.read.biff;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;
import jxl.Cell;
import jxl.CellView;
import jxl.Hyperlink;
import jxl.Image;
import jxl.LabelCell;
import jxl.Range;
import jxl.Sheet;
import jxl.SheetSettings;
import jxl.WorkbookSettings;
import jxl.biff.AutoFilter;
import jxl.biff.BuiltInName;
import jxl.biff.CellFinder;
import jxl.biff.CellReferenceHelper;
import jxl.biff.ConditionalFormat;
import jxl.biff.DataValidation;
import jxl.biff.EmptyCell;
import jxl.biff.FormattingRecords;
import jxl.biff.Type;
import jxl.biff.WorkspaceInformationRecord;
import jxl.biff.drawing.Chart;
import jxl.biff.drawing.Drawing;
import jxl.biff.drawing.DrawingData;
import jxl.biff.drawing.DrawingGroupObject;
import jxl.common.Logger;
import jxl.format.CellFormat;
import jxl.read.biff.NameRecord;

/* loaded from: classes.dex */
public class SheetImpl implements Sheet {
    private static Logger logger = Logger.getLogger(SheetImpl.class);
    private AutoFilter autoFilter;
    private ButtonPropertySetRecord buttonPropertySet;
    private Cell[][] cells;
    private ArrayList charts;
    private int[] columnBreaks;
    private ColumnInfoRecord[] columnInfos;
    private ArrayList conditionalFormats;
    private DataValidation dataValidation;
    private ArrayList drawings;
    private File excelFile;
    private FormattingRecords formattingRecords;
    private boolean hidden;
    private ArrayList images;
    private ArrayList localNames;
    private int maxColumnOutlineLevel;
    private int maxRowOutlineLevel;
    private Range[] mergedCells;
    private String name;
    private boolean nineteenFour;
    private int numCols;
    private int numRows;
    private PLSRecord plsRecord;
    private int[] rowBreaks;
    private RowRecord[] rowRecords;
    private SheetSettings settings;
    private SSTRecord sharedStrings;
    private BOFRecord sheetBof;
    private int startPosition;
    private WorkbookParser workbook;
    private BOFRecord workbookBof;
    private WorkbookSettings workbookSettings;
    private WorkspaceInformationRecord workspaceOptions;
    private ArrayList columnInfosArray = new ArrayList();
    private ArrayList sharedFormulas = new ArrayList();
    private ArrayList hyperlinks = new ArrayList();
    private ArrayList rowProperties = new ArrayList(10);
    private boolean columnInfosInitialized = false;
    private boolean rowRecordsInitialized = false;

    SheetImpl(File f, SSTRecord sst, FormattingRecords fr, BOFRecord sb, BOFRecord wb, boolean nf, WorkbookParser wp) throws BiffException {
        this.excelFile = f;
        this.sharedStrings = sst;
        this.formattingRecords = fr;
        this.sheetBof = sb;
        this.workbookBof = wb;
        this.nineteenFour = nf;
        this.workbook = wp;
        this.workbookSettings = wp.getSettings();
        this.startPosition = f.getPos();
        if (this.sheetBof.isChart()) {
            this.startPosition -= this.sheetBof.getLength() + 4;
        }
        int bofs = 1;
        while (bofs >= 1) {
            Record r = f.next();
            bofs = r.getCode() == Type.EOF.value ? bofs - 1 : bofs;
            if (r.getCode() == Type.BOF.value) {
                bofs++;
            }
        }
    }

    @Override // jxl.Sheet
    public Cell getCell(String loc) {
        return getCell(CellReferenceHelper.getColumn(loc), CellReferenceHelper.getRow(loc));
    }

    @Override // jxl.Sheet
    public Cell getCell(int column, int row) {
        if (this.cells == null) {
            readSheet();
        }
        Cell c = this.cells[row][column];
        if (c == null) {
            Cell c2 = new EmptyCell(column, row);
            this.cells[row][column] = c2;
            return c2;
        }
        return c;
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
    public int getRows() {
        if (this.cells == null) {
            readSheet();
        }
        return this.numRows;
    }

    @Override // jxl.Sheet
    public int getColumns() {
        if (this.cells == null) {
            readSheet();
        }
        return this.numCols;
    }

    @Override // jxl.Sheet
    public Cell[] getRow(int row) {
        if (this.cells == null) {
            readSheet();
        }
        boolean found = false;
        int col = this.numCols - 1;
        while (col >= 0 && !found) {
            if (this.cells[row][col] != null) {
                found = true;
            } else {
                col--;
            }
        }
        Cell[] c = new Cell[col + 1];
        for (int i = 0; i <= col; i++) {
            c[i] = getCell(i, row);
        }
        return c;
    }

    @Override // jxl.Sheet
    public Cell[] getColumn(int col) {
        if (this.cells == null) {
            readSheet();
        }
        boolean found = false;
        int row = this.numRows - 1;
        while (row >= 0 && !found) {
            if (this.cells[row][col] != null) {
                found = true;
            } else {
                row--;
            }
        }
        Cell[] c = new Cell[row + 1];
        for (int i = 0; i <= row; i++) {
            c[i] = getCell(col, i);
        }
        return c;
    }

    @Override // jxl.Sheet
    public String getName() {
        return this.name;
    }

    final void setName(String s) {
        this.name = s;
    }

    @Override // jxl.Sheet
    public boolean isHidden() {
        return this.hidden;
    }

    public ColumnInfoRecord getColumnInfo(int col) {
        if (!this.columnInfosInitialized) {
            Iterator i = this.columnInfosArray.iterator();
            while (i.hasNext()) {
                ColumnInfoRecord cir = (ColumnInfoRecord) i.next();
                int startcol = Math.max(0, cir.getStartColumn());
                int endcol = Math.min(this.columnInfos.length - 1, cir.getEndColumn());
                for (int c = startcol; c <= endcol; c++) {
                    this.columnInfos[c] = cir;
                }
                if (endcol < startcol) {
                    this.columnInfos[startcol] = cir;
                }
            }
            this.columnInfosInitialized = true;
        }
        ColumnInfoRecord[] columnInfoRecordArr = this.columnInfos;
        if (col < columnInfoRecordArr.length) {
            return columnInfoRecordArr[col];
        }
        return null;
    }

    public ColumnInfoRecord[] getColumnInfos() {
        ColumnInfoRecord[] infos = new ColumnInfoRecord[this.columnInfosArray.size()];
        for (int i = 0; i < this.columnInfosArray.size(); i++) {
            infos[i] = (ColumnInfoRecord) this.columnInfosArray.get(i);
        }
        return infos;
    }

    final void setHidden(boolean h) {
        this.hidden = h;
    }

    final void clear() {
        this.cells = (Cell[][]) null;
        this.mergedCells = null;
        this.columnInfosArray.clear();
        this.sharedFormulas.clear();
        this.hyperlinks.clear();
        this.columnInfosInitialized = false;
        if (!this.workbookSettings.getGCDisabled()) {
            System.gc();
        }
    }

    final void readSheet() {
        if (!this.sheetBof.isWorksheet()) {
            this.numRows = 0;
            this.numCols = 0;
            this.cells = (Cell[][]) Array.newInstance((Class<?>) Cell.class, 0, 0);
        }
        SheetReader reader = new SheetReader(this.excelFile, this.sharedStrings, this.formattingRecords, this.sheetBof, this.workbookBof, this.nineteenFour, this.workbook, this.startPosition, this);
        reader.read();
        this.numRows = reader.getNumRows();
        this.numCols = reader.getNumCols();
        this.cells = reader.getCells();
        this.rowProperties = reader.getRowProperties();
        this.columnInfosArray = reader.getColumnInfosArray();
        this.hyperlinks = reader.getHyperlinks();
        this.conditionalFormats = reader.getConditionalFormats();
        this.autoFilter = reader.getAutoFilter();
        this.charts = reader.getCharts();
        this.drawings = reader.getDrawings();
        this.dataValidation = reader.getDataValidation();
        this.mergedCells = reader.getMergedCells();
        SheetSettings settings = reader.getSettings();
        this.settings = settings;
        settings.setHidden(this.hidden);
        this.rowBreaks = reader.getRowBreaks();
        this.columnBreaks = reader.getColumnBreaks();
        this.workspaceOptions = reader.getWorkspaceOptions();
        this.plsRecord = reader.getPLS();
        this.buttonPropertySet = reader.getButtonPropertySet();
        this.maxRowOutlineLevel = reader.getMaxRowOutlineLevel();
        this.maxColumnOutlineLevel = reader.getMaxColumnOutlineLevel();
        if (!this.workbookSettings.getGCDisabled()) {
            System.gc();
        }
        if (this.columnInfosArray.size() > 0) {
            ColumnInfoRecord cir = (ColumnInfoRecord) this.columnInfosArray.get(r2.size() - 1);
            this.columnInfos = new ColumnInfoRecord[cir.getEndColumn() + 1];
        } else {
            this.columnInfos = new ColumnInfoRecord[0];
        }
        ArrayList arrayList = this.localNames;
        if (arrayList != null) {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                NameRecord nr = (NameRecord) it.next();
                if (nr.getBuiltInName() == BuiltInName.PRINT_AREA) {
                    if (nr.getRanges().length > 0) {
                        NameRecord.NameRange rng = nr.getRanges()[0];
                        this.settings.setPrintArea(rng.getFirstColumn(), rng.getFirstRow(), rng.getLastColumn(), rng.getLastRow());
                    }
                } else if (nr.getBuiltInName() == BuiltInName.PRINT_TITLES) {
                    for (int i = 0; i < nr.getRanges().length; i++) {
                        NameRecord.NameRange rng2 = nr.getRanges()[i];
                        if (rng2.getFirstColumn() == 0 && rng2.getLastColumn() == 255) {
                            this.settings.setPrintTitlesRow(rng2.getFirstRow(), rng2.getLastRow());
                        } else {
                            this.settings.setPrintTitlesCol(rng2.getFirstColumn(), rng2.getLastColumn());
                        }
                    }
                }
            }
        }
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
        Range[] rangeArr = this.mergedCells;
        if (rangeArr == null) {
            return new Range[0];
        }
        return rangeArr;
    }

    public RowRecord[] getRowProperties() {
        RowRecord[] rp = new RowRecord[this.rowProperties.size()];
        for (int i = 0; i < rp.length; i++) {
            rp[i] = (RowRecord) this.rowProperties.get(i);
        }
        return rp;
    }

    public DataValidation getDataValidation() {
        return this.dataValidation;
    }

    RowRecord getRowInfo(int r) {
        if (!this.rowRecordsInitialized) {
            this.rowRecords = new RowRecord[getRows()];
            Iterator i = this.rowProperties.iterator();
            while (i.hasNext()) {
                RowRecord rr = (RowRecord) i.next();
                int rownum = rr.getRowNumber();
                RowRecord[] rowRecordArr = this.rowRecords;
                if (rownum < rowRecordArr.length) {
                    rowRecordArr[rownum] = rr;
                }
            }
            this.rowRecordsInitialized = true;
        }
        RowRecord[] rowRecordArr2 = this.rowRecords;
        if (r < rowRecordArr2.length) {
            return rowRecordArr2[r];
        }
        return null;
    }

    @Override // jxl.Sheet
    public final int[] getRowPageBreaks() {
        return this.rowBreaks;
    }

    @Override // jxl.Sheet
    public final int[] getColumnPageBreaks() {
        return this.columnBreaks;
    }

    public final Chart[] getCharts() {
        Chart[] ch = new Chart[this.charts.size()];
        for (int i = 0; i < ch.length; i++) {
            ch[i] = (Chart) this.charts.get(i);
        }
        return ch;
    }

    public final DrawingGroupObject[] getDrawings() {
        DrawingGroupObject[] dr = new DrawingGroupObject[this.drawings.size()];
        return (DrawingGroupObject[]) this.drawings.toArray(dr);
    }

    @Override // jxl.Sheet
    public boolean isProtected() {
        return this.settings.isProtected();
    }

    public WorkspaceInformationRecord getWorkspaceOptions() {
        return this.workspaceOptions;
    }

    @Override // jxl.Sheet
    public SheetSettings getSettings() {
        return this.settings;
    }

    public WorkbookParser getWorkbook() {
        return this.workbook;
    }

    @Override // jxl.Sheet
    public CellFormat getColumnFormat(int col) {
        CellView cv = getColumnView(col);
        return cv.getFormat();
    }

    @Override // jxl.Sheet
    public int getColumnWidth(int col) {
        return getColumnView(col).getSize() / 256;
    }

    @Override // jxl.Sheet
    public CellView getColumnView(int col) {
        ColumnInfoRecord cir = getColumnInfo(col);
        CellView cv = new CellView();
        if (cir != null) {
            cv.setDimension(cir.getWidth() / 256);
            cv.setSize(cir.getWidth());
            cv.setHidden(cir.getHidden());
            cv.setFormat(this.formattingRecords.getXFRecord(cir.getXFIndex()));
        } else {
            cv.setDimension(this.settings.getDefaultColumnWidth());
            cv.setSize(this.settings.getDefaultColumnWidth() * 256);
        }
        return cv;
    }

    @Override // jxl.Sheet
    public int getRowHeight(int row) {
        return getRowView(row).getDimension();
    }

    @Override // jxl.Sheet
    public CellView getRowView(int row) {
        RowRecord rr = getRowInfo(row);
        CellView cv = new CellView();
        if (rr != null) {
            cv.setDimension(rr.getRowHeight());
            cv.setSize(rr.getRowHeight());
            cv.setHidden(rr.isCollapsed());
            if (rr.hasDefaultFormat()) {
                cv.setFormat(this.formattingRecords.getXFRecord(rr.getXFIndex()));
            }
        } else {
            cv.setDimension(this.settings.getDefaultRowHeight());
            cv.setSize(this.settings.getDefaultRowHeight());
        }
        return cv;
    }

    public BOFRecord getSheetBof() {
        return this.sheetBof;
    }

    public BOFRecord getWorkbookBof() {
        return this.workbookBof;
    }

    public PLSRecord getPLS() {
        return this.plsRecord;
    }

    public ButtonPropertySetRecord getButtonPropertySet() {
        return this.buttonPropertySet;
    }

    @Override // jxl.Sheet
    public int getNumberOfImages() {
        if (this.images == null) {
            initializeImages();
        }
        return this.images.size();
    }

    @Override // jxl.Sheet
    public Image getDrawing(int i) {
        if (this.images == null) {
            initializeImages();
        }
        return (Image) this.images.get(i);
    }

    private void initializeImages() {
        if (this.images != null) {
            return;
        }
        this.images = new ArrayList();
        DrawingGroupObject[] dgos = getDrawings();
        for (int i = 0; i < dgos.length; i++) {
            if (dgos[i] instanceof Drawing) {
                this.images.add(dgos[i]);
            }
        }
    }

    public DrawingData getDrawingData() {
        SheetReader reader = new SheetReader(this.excelFile, this.sharedStrings, this.formattingRecords, this.sheetBof, this.workbookBof, this.nineteenFour, this.workbook, this.startPosition, this);
        reader.read();
        return reader.getDrawingData();
    }

    void addLocalName(NameRecord nr) {
        if (this.localNames == null) {
            this.localNames = new ArrayList();
        }
        this.localNames.add(nr);
    }

    public ConditionalFormat[] getConditionalFormats() {
        ConditionalFormat[] formats = new ConditionalFormat[this.conditionalFormats.size()];
        return (ConditionalFormat[]) this.conditionalFormats.toArray(formats);
    }

    public AutoFilter getAutoFilter() {
        return this.autoFilter;
    }

    public int getMaxColumnOutlineLevel() {
        return this.maxColumnOutlineLevel;
    }

    public int getMaxRowOutlineLevel() {
        return this.maxRowOutlineLevel;
    }
}
