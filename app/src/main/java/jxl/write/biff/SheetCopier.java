package jxl.write.biff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeSet;
import jxl.BooleanCell;
import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.Hyperlink;
import jxl.LabelCell;
import jxl.NumberCell;
import jxl.Range;
import jxl.Sheet;
import jxl.WorkbookSettings;
import jxl.biff.AutoFilter;
import jxl.biff.CellReferenceHelper;
import jxl.biff.ConditionalFormat;
import jxl.biff.DataValidation;
import jxl.biff.FormattingRecords;
import jxl.biff.FormulaData;
import jxl.biff.NumFormatRecordsException;
import jxl.biff.SheetRangeImpl;
import jxl.biff.XFRecord;
import jxl.biff.drawing.Button;
import jxl.biff.drawing.Chart;
import jxl.biff.drawing.CheckBox;
import jxl.biff.drawing.ComboBox;
import jxl.biff.drawing.Comment;
import jxl.biff.drawing.Drawing;
import jxl.biff.drawing.DrawingGroupObject;
import jxl.biff.formula.FormulaException;
import jxl.common.Assert;
import jxl.common.Logger;
import jxl.format.CellFormat;
import jxl.read.biff.NameRecord;
import jxl.read.biff.SheetImpl;
import jxl.read.biff.WorkbookParser;
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
class SheetCopier {
    private static Logger logger = Logger.getLogger(SheetCopier.class);
    private AutoFilter autoFilter;
    private ButtonPropertySetRecord buttonPropertySet;
    private boolean chartOnly = false;
    private ArrayList columnBreaks;
    private TreeSet columnFormats;
    private ComboBox comboBox;
    private ArrayList conditionalFormats;
    private DataValidation dataValidation;
    private ArrayList drawings;
    private HashMap fonts;
    private FormattingRecords formatRecords;
    private HashMap formats;
    private SheetImpl fromSheet;
    private ArrayList hyperlinks;
    private ArrayList images;
    private int maxColumnOutlineLevel;
    private int maxRowOutlineLevel;
    private MergedCells mergedCells;
    private int numRows;
    private PLSRecord plsRecord;
    private ArrayList rowBreaks;
    private SheetWriter sheetWriter;
    private WritableSheetImpl toSheet;
    private ArrayList validatedCells;
    private WorkbookSettings workbookSettings;
    private HashMap xfRecords;

    public SheetCopier(Sheet f, WritableSheet t) {
        this.fromSheet = (SheetImpl) f;
        WritableSheetImpl writableSheetImpl = (WritableSheetImpl) t;
        this.toSheet = writableSheetImpl;
        this.workbookSettings = writableSheetImpl.getWorkbook().getSettings();
    }

    void setColumnFormats(TreeSet cf) {
        this.columnFormats = cf;
    }

    void setFormatRecords(FormattingRecords fr) {
        this.formatRecords = fr;
    }

    void setHyperlinks(ArrayList h) {
        this.hyperlinks = h;
    }

    void setMergedCells(MergedCells mc) {
        this.mergedCells = mc;
    }

    void setRowBreaks(ArrayList rb) {
        this.rowBreaks = rb;
    }

    void setColumnBreaks(ArrayList cb) {
        this.columnBreaks = cb;
    }

    void setSheetWriter(SheetWriter sw) {
        this.sheetWriter = sw;
    }

    void setDrawings(ArrayList d) {
        this.drawings = d;
    }

    void setImages(ArrayList i) {
        this.images = i;
    }

    void setConditionalFormats(ArrayList cf) {
        this.conditionalFormats = cf;
    }

    void setValidatedCells(ArrayList vc) {
        this.validatedCells = vc;
    }

    AutoFilter getAutoFilter() {
        return this.autoFilter;
    }

    DataValidation getDataValidation() {
        return this.dataValidation;
    }

    ComboBox getComboBox() {
        return this.comboBox;
    }

    PLSRecord getPLSRecord() {
        return this.plsRecord;
    }

    boolean isChartOnly() {
        return this.chartOnly;
    }

    ButtonPropertySetRecord getButtonPropertySet() {
        return this.buttonPropertySet;
    }

    public void copySheet() {
        shallowCopyCells();
        jxl.read.biff.ColumnInfoRecord[] readCirs = this.fromSheet.getColumnInfos();
        for (jxl.read.biff.ColumnInfoRecord rcir : readCirs) {
            for (int j = rcir.getStartColumn(); j <= rcir.getEndColumn(); j++) {
                ColumnInfoRecord cir = new ColumnInfoRecord(rcir, j, this.formatRecords);
                cir.setHidden(rcir.getHidden());
                this.columnFormats.add(cir);
            }
        }
        Hyperlink[] hls = this.fromSheet.getHyperlinks();
        for (Hyperlink hyperlink : hls) {
            WritableHyperlink hr = new WritableHyperlink(hyperlink, this.toSheet);
            this.hyperlinks.add(hr);
        }
        Range[] merged = this.fromSheet.getMergedCells();
        for (Range range : merged) {
            this.mergedCells.add(new SheetRangeImpl((SheetRangeImpl) range, this.toSheet));
        }
        try {
            jxl.read.biff.RowRecord[] rowprops = this.fromSheet.getRowProperties();
            for (int i = 0; i < rowprops.length; i++) {
                RowRecord rr = this.toSheet.getRowRecord(rowprops[i].getRowNumber());
                XFRecord format = rowprops[i].hasDefaultFormat() ? this.formatRecords.getXFRecord(rowprops[i].getXFIndex()) : null;
                rr.setRowDetails(rowprops[i].getRowHeight(), rowprops[i].matchesDefaultFontHeight(), rowprops[i].isCollapsed(), rowprops[i].getOutlineLevel(), rowprops[i].getGroupStart(), format);
                this.numRows = Math.max(this.numRows, rowprops[i].getRowNumber() + 1);
            }
        } catch (RowsExceededException e) {
            Assert.verify(false);
        }
        int[] rowbreaks = this.fromSheet.getRowPageBreaks();
        if (rowbreaks != null) {
            for (int i2 : rowbreaks) {
                this.rowBreaks.add(new Integer(i2));
            }
        }
        int[] columnbreaks = this.fromSheet.getColumnPageBreaks();
        if (columnbreaks != null) {
            for (int i3 : columnbreaks) {
                this.columnBreaks.add(new Integer(i3));
            }
        }
        this.sheetWriter.setCharts(this.fromSheet.getCharts());
        DrawingGroupObject[] dr = this.fromSheet.getDrawings();
        for (int i4 = 0; i4 < dr.length; i4++) {
            if (dr[i4] instanceof Drawing) {
                WritableImage wi = new WritableImage(dr[i4], this.toSheet.getWorkbook().getDrawingGroup());
                this.drawings.add(wi);
                this.images.add(wi);
            } else if (dr[i4] instanceof Comment) {
                Comment c = new Comment(dr[i4], this.toSheet.getWorkbook().getDrawingGroup(), this.workbookSettings);
                this.drawings.add(c);
                CellValue cv = (CellValue) this.toSheet.getWritableCell(c.getColumn(), c.getRow());
                Assert.verify(cv.getCellFeatures() != null);
                cv.getWritableCellFeatures().setCommentDrawing(c);
            } else if (dr[i4] instanceof Button) {
                Button b = new Button(dr[i4], this.toSheet.getWorkbook().getDrawingGroup(), this.workbookSettings);
                this.drawings.add(b);
            } else if (dr[i4] instanceof ComboBox) {
                ComboBox cb = new ComboBox(dr[i4], this.toSheet.getWorkbook().getDrawingGroup(), this.workbookSettings);
                this.drawings.add(cb);
            } else if (dr[i4] instanceof CheckBox) {
                CheckBox cb2 = new CheckBox(dr[i4], this.toSheet.getWorkbook().getDrawingGroup(), this.workbookSettings);
                this.drawings.add(cb2);
            }
        }
        DataValidation rdv = this.fromSheet.getDataValidation();
        if (rdv != null) {
            DataValidation dataValidation = new DataValidation(rdv, this.toSheet.getWorkbook(), this.toSheet.getWorkbook(), this.workbookSettings);
            this.dataValidation = dataValidation;
            int objid = dataValidation.getComboBoxObjectId();
            if (objid != 0) {
                this.comboBox = (ComboBox) this.drawings.get(objid);
            }
        }
        ConditionalFormat[] cf = this.fromSheet.getConditionalFormats();
        if (cf.length > 0) {
            for (ConditionalFormat conditionalFormat : cf) {
                this.conditionalFormats.add(conditionalFormat);
            }
        }
        this.autoFilter = this.fromSheet.getAutoFilter();
        this.sheetWriter.setWorkspaceOptions(this.fromSheet.getWorkspaceOptions());
        if (this.fromSheet.getSheetBof().isChart()) {
            this.chartOnly = true;
            this.sheetWriter.setChartOnly();
        }
        if (this.fromSheet.getPLS() != null) {
            if (!this.fromSheet.getWorkbookBof().isBiff7()) {
                this.plsRecord = new PLSRecord(this.fromSheet.getPLS());
            } else {
                logger.warn("Cannot copy Biff7 print settings record - ignoring");
            }
        }
        if (this.fromSheet.getButtonPropertySet() != null) {
            this.buttonPropertySet = new ButtonPropertySetRecord(this.fromSheet.getButtonPropertySet());
        }
        this.maxRowOutlineLevel = this.fromSheet.getMaxRowOutlineLevel();
        this.maxColumnOutlineLevel = this.fromSheet.getMaxColumnOutlineLevel();
    }

    public void copyWritableSheet() {
        shallowCopyCells();
    }

    public void importSheet() {
        this.xfRecords = new HashMap();
        this.fonts = new HashMap();
        this.formats = new HashMap();
        deepCopyCells();
        jxl.read.biff.ColumnInfoRecord[] readCirs = this.fromSheet.getColumnInfos();
        for (jxl.read.biff.ColumnInfoRecord rcir : readCirs) {
            for (int j = rcir.getStartColumn(); j <= rcir.getEndColumn(); j++) {
                ColumnInfoRecord cir = new ColumnInfoRecord(rcir, j);
                int xfIndex = cir.getXfIndex();
                XFRecord cf = (WritableCellFormat) this.xfRecords.get(new Integer(xfIndex));
                if (cf == null) {
                    CellFormat readFormat = this.fromSheet.getColumnView(j).getFormat();
                    copyCellFormat(readFormat);
                }
                cir.setCellFormat(cf);
                cir.setHidden(rcir.getHidden());
                this.columnFormats.add(cir);
            }
        }
        Hyperlink[] hls = this.fromSheet.getHyperlinks();
        for (Hyperlink hyperlink : hls) {
            WritableHyperlink hr = new WritableHyperlink(hyperlink, this.toSheet);
            this.hyperlinks.add(hr);
        }
        Range[] merged = this.fromSheet.getMergedCells();
        for (Range range : merged) {
            this.mergedCells.add(new SheetRangeImpl((SheetRangeImpl) range, this.toSheet));
        }
        try {
            jxl.read.biff.RowRecord[] rowprops = this.fromSheet.getRowProperties();
            for (int i = 0; i < rowprops.length; i++) {
                RowRecord rr = this.toSheet.getRowRecord(rowprops[i].getRowNumber());
                XFRecord format = null;
                jxl.read.biff.RowRecord rowrec = rowprops[i];
                if (rowrec.hasDefaultFormat() && (format = (WritableCellFormat) this.xfRecords.get(new Integer(rowrec.getXFIndex()))) == null) {
                    int rownum = rowrec.getRowNumber();
                    CellFormat readFormat2 = this.fromSheet.getRowView(rownum).getFormat();
                    copyCellFormat(readFormat2);
                }
                int rownum2 = rowrec.getRowHeight();
                rr.setRowDetails(rownum2, rowrec.matchesDefaultFontHeight(), rowrec.isCollapsed(), rowrec.getOutlineLevel(), rowrec.getGroupStart(), format);
                this.numRows = Math.max(this.numRows, rowprops[i].getRowNumber() + 1);
            }
        } catch (RowsExceededException e) {
            Assert.verify(false);
        }
        int[] rowbreaks = this.fromSheet.getRowPageBreaks();
        if (rowbreaks != null) {
            for (int i2 : rowbreaks) {
                this.rowBreaks.add(new Integer(i2));
            }
        }
        int[] columnbreaks = this.fromSheet.getColumnPageBreaks();
        if (columnbreaks != null) {
            for (int i3 : columnbreaks) {
                this.columnBreaks.add(new Integer(i3));
            }
        }
        Chart[] fromCharts = this.fromSheet.getCharts();
        if (fromCharts != null && fromCharts.length > 0) {
            logger.warn("Importing of charts is not supported");
        }
        DrawingGroupObject[] dr = this.fromSheet.getDrawings();
        if (dr.length > 0 && this.toSheet.getWorkbook().getDrawingGroup() == null) {
            this.toSheet.getWorkbook().createDrawingGroup();
        }
        for (int i4 = 0; i4 < dr.length; i4++) {
            if (dr[i4] instanceof Drawing) {
                WritableImage wi = new WritableImage(dr[i4].getX(), dr[i4].getY(), dr[i4].getWidth(), dr[i4].getHeight(), dr[i4].getImageData());
                this.toSheet.getWorkbook().addDrawing(wi);
                this.drawings.add(wi);
                this.images.add(wi);
            } else if (dr[i4] instanceof Comment) {
                Comment c = new Comment(dr[i4], this.toSheet.getWorkbook().getDrawingGroup(), this.workbookSettings);
                this.drawings.add(c);
                CellValue cv = (CellValue) this.toSheet.getWritableCell(c.getColumn(), c.getRow());
                Assert.verify(cv.getCellFeatures() != null);
                cv.getWritableCellFeatures().setCommentDrawing(c);
            } else if (dr[i4] instanceof Button) {
                Button b = new Button(dr[i4], this.toSheet.getWorkbook().getDrawingGroup(), this.workbookSettings);
                this.drawings.add(b);
            } else if (dr[i4] instanceof ComboBox) {
                ComboBox cb = new ComboBox(dr[i4], this.toSheet.getWorkbook().getDrawingGroup(), this.workbookSettings);
                this.drawings.add(cb);
            }
        }
        DataValidation rdv = this.fromSheet.getDataValidation();
        if (rdv != null) {
            DataValidation dataValidation = new DataValidation(rdv, this.toSheet.getWorkbook(), this.toSheet.getWorkbook(), this.workbookSettings);
            this.dataValidation = dataValidation;
            int objid = dataValidation.getComboBoxObjectId();
            if (objid != 0) {
                this.comboBox = (ComboBox) this.drawings.get(objid);
            }
        }
        this.sheetWriter.setWorkspaceOptions(this.fromSheet.getWorkspaceOptions());
        if (this.fromSheet.getSheetBof().isChart()) {
            this.chartOnly = true;
            this.sheetWriter.setChartOnly();
        }
        if (this.fromSheet.getPLS() != null) {
            if (!this.fromSheet.getWorkbookBof().isBiff7()) {
                this.plsRecord = new PLSRecord(this.fromSheet.getPLS());
            } else {
                logger.warn("Cannot copy Biff7 print settings record - ignoring");
            }
        }
        if (this.fromSheet.getButtonPropertySet() != null) {
            this.buttonPropertySet = new ButtonPropertySetRecord(this.fromSheet.getButtonPropertySet());
        }
        importNames();
        this.maxRowOutlineLevel = this.fromSheet.getMaxRowOutlineLevel();
        this.maxColumnOutlineLevel = this.fromSheet.getMaxColumnOutlineLevel();
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
                        if (c.getCellFeatures() != null && c.getCellFeatures().hasDataValidation()) {
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

    private void importNames() {
        SheetCopier sheetCopier = this;
        WorkbookParser fromWorkbook = sheetCopier.fromSheet.getWorkbook();
        WritableWorkbook toWorkbook = sheetCopier.toSheet.getWorkbook();
        int fromSheetIndex = fromWorkbook.getIndex(sheetCopier.fromSheet);
        jxl.read.biff.NameRecord[] nameRecords = fromWorkbook.getNameRecords();
        String[] names = toWorkbook.getRangeNames();
        int i = 0;
        while (i < nameRecords.length) {
            NameRecord.NameRange[] nameRanges = nameRecords[i].getRanges();
            int j = 0;
            while (j < nameRanges.length) {
                int nameSheetIndex = fromWorkbook.getExternalSheetIndex(nameRanges[j].getExternalSheet());
                if (fromSheetIndex == nameSheetIndex) {
                    String name = nameRecords[i].getName();
                    if (Arrays.binarySearch(names, name) < 0) {
                        WritableSheet writableSheet = sheetCopier.toSheet;
                        int firstColumn = nameRanges[j].getFirstColumn();
                        int firstRow = nameRanges[j].getFirstRow();
                        int nameSheetIndex2 = nameRanges[j].getLastColumn();
                        toWorkbook.addNameArea(name, writableSheet, firstColumn, firstRow, nameSheetIndex2, nameRanges[j].getLastRow());
                    } else {
                        logger.warn("Named range " + name + " is already present in the destination workbook");
                    }
                }
                j++;
                sheetCopier = this;
            }
            i++;
            sheetCopier = this;
        }
    }

    int getRows() {
        return this.numRows;
    }

    public int getMaxColumnOutlineLevel() {
        return this.maxColumnOutlineLevel;
    }

    public int getMaxRowOutlineLevel() {
        return this.maxRowOutlineLevel;
    }
}
