package jxl.write.biff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;
import jxl.BooleanCell;
import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.LabelCell;
import jxl.NumberCell;
import jxl.Range;
import jxl.WorkbookSettings;
import jxl.biff.CellReferenceHelper;
import jxl.biff.DataValidation;
import jxl.biff.FormattingRecords;
import jxl.biff.FormulaData;
import jxl.biff.NumFormatRecordsException;
import jxl.biff.SheetRangeImpl;
import jxl.biff.WorkspaceInformationRecord;
import jxl.biff.XFRecord;
import jxl.biff.drawing.Drawing;
import jxl.biff.formula.FormulaException;
import jxl.common.Assert;
import jxl.common.Logger;
import jxl.format.CellFormat;
import jxl.write.Blank;
import jxl.write.Boolean;
import jxl.write.DateTime;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableHyperlink;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/* loaded from: classes.dex */
class WritableSheetCopier {
    private static Logger logger = Logger.getLogger(SheetCopier.class);
    private boolean chartOnly = false;
    private HashMap fonts;
    private FormattingRecords formatRecords;
    private HashMap formats;
    private ButtonPropertySetRecord fromButtonPropertySet;
    private ArrayList fromColumnBreaks;
    private TreeSet fromColumnFormats;
    private DataValidation fromDataValidation;
    private ArrayList fromDrawings;
    private ArrayList fromHyperlinks;
    private MergedCells fromMergedCells;
    private PLSRecord fromPLSRecord;
    private ArrayList fromRowBreaks;
    private RowRecord[] fromRows;
    private WritableSheetImpl fromSheet;
    private WorkspaceInformationRecord fromWorkspaceOptions;
    private int maxColumnOutlineLevel;
    private int maxRowOutlineLevel;
    private int numRows;
    private SheetWriter sheetWriter;
    private ButtonPropertySetRecord toButtonPropertySet;
    private ArrayList toColumnBreaks;
    private TreeSet toColumnFormats;
    private DataValidation toDataValidation;
    private ArrayList toDrawings;
    private ArrayList toHyperlinks;
    private ArrayList toImages;
    private MergedCells toMergedCells;
    private PLSRecord toPLSRecord;
    private ArrayList toRowBreaks;
    private WritableSheetImpl toSheet;
    private ArrayList validatedCells;
    private WorkbookSettings workbookSettings;
    private HashMap xfRecords;

    public WritableSheetCopier(WritableSheet f, WritableSheet t) {
        this.fromSheet = (WritableSheetImpl) f;
        WritableSheetImpl writableSheetImpl = (WritableSheetImpl) t;
        this.toSheet = writableSheetImpl;
        this.workbookSettings = writableSheetImpl.getWorkbook().getSettings();
    }

    void setColumnFormats(TreeSet fcf, TreeSet tcf) {
        this.fromColumnFormats = fcf;
        this.toColumnFormats = tcf;
    }

    void setMergedCells(MergedCells fmc, MergedCells tmc) {
        this.fromMergedCells = fmc;
        this.toMergedCells = tmc;
    }

    void setRows(RowRecord[] r) {
        this.fromRows = r;
    }

    void setValidatedCells(ArrayList vc) {
        this.validatedCells = vc;
    }

    void setRowBreaks(ArrayList frb, ArrayList trb) {
        this.fromRowBreaks = frb;
        this.toRowBreaks = trb;
    }

    void setColumnBreaks(ArrayList fcb, ArrayList tcb) {
        this.fromColumnBreaks = fcb;
        this.toColumnBreaks = tcb;
    }

    void setDrawings(ArrayList fd, ArrayList td, ArrayList ti) {
        this.fromDrawings = fd;
        this.toDrawings = td;
        this.toImages = ti;
    }

    void setHyperlinks(ArrayList fh, ArrayList th) {
        this.fromHyperlinks = fh;
        this.toHyperlinks = th;
    }

    void setWorkspaceOptions(WorkspaceInformationRecord wir) {
        this.fromWorkspaceOptions = wir;
    }

    void setDataValidation(DataValidation dv) {
        this.fromDataValidation = dv;
    }

    void setPLSRecord(PLSRecord plsr) {
        this.fromPLSRecord = plsr;
    }

    void setButtonPropertySetRecord(ButtonPropertySetRecord bpsr) {
        this.fromButtonPropertySet = bpsr;
    }

    void setSheetWriter(SheetWriter sw) {
        this.sheetWriter = sw;
    }

    DataValidation getDataValidation() {
        return this.toDataValidation;
    }

    PLSRecord getPLSRecord() {
        return this.toPLSRecord;
    }

    boolean isChartOnly() {
        return this.chartOnly;
    }

    ButtonPropertySetRecord getButtonPropertySet() {
        return this.toButtonPropertySet;
    }

    public void copySheet() {
        shallowCopyCells();
        Iterator cfit = this.fromColumnFormats.iterator();
        while (cfit.hasNext()) {
            ColumnInfoRecord cv = new ColumnInfoRecord((ColumnInfoRecord) cfit.next());
            this.toColumnFormats.add(cv);
        }
        Range[] merged = this.fromMergedCells.getMergedCells();
        for (Range range : merged) {
            this.toMergedCells.add(new SheetRangeImpl((SheetRangeImpl) range, this.toSheet));
        }
        int i = 0;
        while (true) {
            try {
                RowRecord[] rowRecordArr = this.fromRows;
                if (i >= rowRecordArr.length) {
                    break;
                }
                RowRecord row = rowRecordArr[i];
                if (row != null && (!row.isDefaultHeight() || row.isCollapsed())) {
                    RowRecord newRow = this.toSheet.getRowRecord(i);
                    newRow.setRowDetails(row.getRowHeight(), row.matchesDefaultFontHeight(), row.isCollapsed(), row.getOutlineLevel(), row.getGroupStart(), row.getStyle());
                }
                i++;
            } catch (RowsExceededException e) {
                Assert.verify(false);
            }
        }
        this.toRowBreaks = new ArrayList(this.fromRowBreaks);
        this.toColumnBreaks = new ArrayList(this.fromColumnBreaks);
        if (this.fromDataValidation != null) {
            this.toDataValidation = new DataValidation(this.fromDataValidation, this.toSheet.getWorkbook(), this.toSheet.getWorkbook(), this.toSheet.getWorkbook().getSettings());
        }
        this.sheetWriter.setCharts(this.fromSheet.getCharts());
        Iterator i2 = this.fromDrawings.iterator();
        while (i2.hasNext()) {
            Object o = i2.next();
            if (o instanceof Drawing) {
                WritableImage wi = new WritableImage((Drawing) o, this.toSheet.getWorkbook().getDrawingGroup());
                this.toDrawings.add(wi);
                this.toImages.add(wi);
            }
        }
        this.sheetWriter.setWorkspaceOptions(this.fromWorkspaceOptions);
        if (this.fromPLSRecord != null) {
            this.toPLSRecord = new PLSRecord(this.fromPLSRecord);
        }
        if (this.fromButtonPropertySet != null) {
            this.toButtonPropertySet = new ButtonPropertySetRecord(this.fromButtonPropertySet);
        }
        Iterator i3 = this.fromHyperlinks.iterator();
        while (i3.hasNext()) {
            WritableHyperlink hr = new WritableHyperlink((WritableHyperlink) i3.next(), this.toSheet);
            this.toHyperlinks.add(hr);
        }
    }

    private WritableCell shallowCopyCell(Cell cell) {
        CellType ct = cell.getType();
        if (ct == CellType.LABEL) {
            WritableCell newCell = new Label((LabelCell) cell);
            return newCell;
        }
        if (ct == CellType.NUMBER) {
            WritableCell newCell2 = new Number((NumberCell) cell);
            return newCell2;
        }
        if (ct == CellType.DATE) {
            WritableCell newCell3 = new DateTime((DateCell) cell);
            return newCell3;
        }
        if (ct == CellType.BOOLEAN) {
            WritableCell newCell4 = new Boolean((BooleanCell) cell);
            return newCell4;
        }
        if (ct == CellType.NUMBER_FORMULA) {
            WritableCell newCell5 = new ReadNumberFormulaRecord((FormulaData) cell);
            return newCell5;
        }
        if (ct == CellType.STRING_FORMULA) {
            WritableCell newCell6 = new ReadStringFormulaRecord((FormulaData) cell);
            return newCell6;
        }
        if (ct == CellType.BOOLEAN_FORMULA) {
            WritableCell newCell7 = new ReadBooleanFormulaRecord((FormulaData) cell);
            return newCell7;
        }
        if (ct == CellType.DATE_FORMULA) {
            WritableCell newCell8 = new ReadDateFormulaRecord((FormulaData) cell);
            return newCell8;
        }
        if (ct == CellType.FORMULA_ERROR) {
            WritableCell newCell9 = new ReadErrorFormulaRecord((FormulaData) cell);
            return newCell9;
        }
        if (ct != CellType.EMPTY || cell.getCellFormat() == null) {
            return null;
        }
        WritableCell newCell10 = new Blank(cell);
        return newCell10;
    }

    private WritableCell deepCopyCell(Cell cell) {
        WritableCell c = shallowCopyCell(cell);
        if (c == null) {
            return c;
        }
        if (c instanceof ReadFormulaRecord) {
            ReadFormulaRecord rfr = (ReadFormulaRecord) c;
            boolean crossSheetReference = !rfr.handleImportedCellReferences(this.fromSheet.getWorkbook(), this.fromSheet.getWorkbook(), this.workbookSettings);
            if (crossSheetReference) {
                try {
                    logger.warn("Formula " + rfr.getFormula() + " in cell " + CellReferenceHelper.getCellReference(cell.getColumn(), cell.getRow()) + " cannot be imported because it references another  sheet from the source workbook");
                } catch (FormulaException e) {
                    logger.warn("Formula  in cell " + CellReferenceHelper.getCellReference(cell.getColumn(), cell.getRow()) + " cannot be imported:  " + e.getMessage());
                }
                c = new Formula(cell.getColumn(), cell.getRow(), "\"ERROR\"");
            }
        }
        CellFormat cf = c.getCellFormat();
        int index = ((XFRecord) cf).getXFIndex();
        WritableCellFormat wcf = (WritableCellFormat) this.xfRecords.get(new Integer(index));
        if (wcf == null) {
            wcf = copyCellFormat(cf);
        }
        c.setCellFormat(wcf);
        return c;
    }

    void shallowCopyCells() {
        int cells = this.fromSheet.getRows();
        for (int i = 0; i < cells; i++) {
            Cell[] row = this.fromSheet.getRow(i);
            for (Cell cell : row) {
                WritableCell c = shallowCopyCell(cell);
                if (c != null) {
                    try {
                        this.toSheet.addCell(c);
                        if ((c.getCellFeatures() != null) & c.getCellFeatures().hasDataValidation()) {
                            this.validatedCells.add(c);
                        }
                    } catch (WriteException e) {
                        Assert.verify(false);
                    }
                }
            }
        }
        this.numRows = this.toSheet.getRows();
    }

    void deepCopyCells() {
        int cells = this.fromSheet.getRows();
        for (int i = 0; i < cells; i++) {
            Cell[] row = this.fromSheet.getRow(i);
            for (Cell cell : row) {
                WritableCell c = deepCopyCell(cell);
                if (c != null) {
                    try {
                        this.toSheet.addCell(c);
                        if ((c.getCellFeatures() != null) & c.getCellFeatures().hasDataValidation()) {
                            this.validatedCells.add(c);
                        }
                    } catch (WriteException e) {
                        Assert.verify(false);
                    }
                }
            }
        }
    }

    private WritableCellFormat copyCellFormat(CellFormat cf) {
        try {
            XFRecord xfr = (XFRecord) cf;
            WritableCellFormat f = new WritableCellFormat(xfr);
            this.formatRecords.addStyle(f);
            int xfIndex = xfr.getXFIndex();
            this.xfRecords.put(new Integer(xfIndex), f);
            int fontIndex = xfr.getFontIndex();
            this.fonts.put(new Integer(fontIndex), new Integer(f.getFontIndex()));
            int formatIndex = xfr.getFormatRecord();
            this.formats.put(new Integer(formatIndex), new Integer(f.getFormatRecord()));
            return f;
        } catch (NumFormatRecordsException e) {
            logger.warn("Maximum number of format records exceeded.  Using default format.");
            return WritableWorkbook.NORMAL_STYLE;
        }
    }

    public int getMaxColumnOutlineLevel() {
        return this.maxColumnOutlineLevel;
    }

    public int getMaxRowOutlineLevel() {
        return this.maxRowOutlineLevel;
    }
}
