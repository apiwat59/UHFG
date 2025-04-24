package jxl.write.biff;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import jxl.Cell;
import jxl.CellFeatures;
import jxl.Range;
import jxl.SheetSettings;
import jxl.WorkbookSettings;
import jxl.biff.AutoFilter;
import jxl.biff.ConditionalFormat;
import jxl.biff.DataValidation;
import jxl.biff.DataValiditySettingsRecord;
import jxl.biff.WorkspaceInformationRecord;
import jxl.biff.XFRecord;
import jxl.biff.drawing.Chart;
import jxl.biff.drawing.SheetDrawingWriter;
import jxl.common.Assert;
import jxl.common.Logger;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Blank;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableHyperlink;
import jxl.write.WriteException;

/* loaded from: classes.dex */
final class SheetWriter {
    private static Logger logger = Logger.getLogger(SheetWriter.class);
    private AutoFilter autoFilter;
    private ButtonPropertySetRecord buttonPropertySet;
    private ArrayList columnBreaks;
    private TreeSet columnFormats;
    private ArrayList conditionalFormats;
    private DataValidation dataValidation;
    private SheetDrawingWriter drawingWriter;
    private FooterRecord footer;
    private HeaderRecord header;
    private ArrayList hyperlinks;
    private int maxColumnOutlineLevel;
    private int maxRowOutlineLevel;
    private MergedCells mergedCells;
    private int numCols;
    private int numRows;
    private File outputFile;
    private PLSRecord plsRecord;
    private ArrayList rowBreaks;
    private RowRecord[] rows;
    private SheetSettings settings;
    private WritableSheetImpl sheet;
    private ArrayList validatedCells;
    private WorkbookSettings workbookSettings;
    private WorkspaceInformationRecord workspaceOptions = new WorkspaceInformationRecord();
    private boolean chartOnly = false;

    public SheetWriter(File of, WritableSheetImpl wsi, WorkbookSettings ws) {
        this.outputFile = of;
        this.sheet = wsi;
        this.workbookSettings = ws;
        this.drawingWriter = new SheetDrawingWriter(ws);
    }

    public void write() throws IOException {
        boolean z;
        FooterRecord footer;
        WritableCellFormat defaultDateFormat;
        int numBlocks;
        RefModeRecord rmr;
        Assert.verify(this.rows != null);
        if (this.chartOnly) {
            this.drawingWriter.write(this.outputFile);
            return;
        }
        BOFRecord bof = new BOFRecord(BOFRecord.sheet);
        this.outputFile.write(bof);
        int i = this.numRows;
        int numBlocks2 = i / 32;
        if (i - (numBlocks2 * 32) != 0) {
            numBlocks2++;
        }
        int indexPos = this.outputFile.getPos();
        IndexRecord indexRecord = new IndexRecord(0, this.numRows, numBlocks2);
        this.outputFile.write(indexRecord);
        if (this.settings.getAutomaticFormulaCalculation()) {
            CalcModeRecord cmr = new CalcModeRecord(CalcModeRecord.automatic);
            this.outputFile.write(cmr);
        } else {
            CalcModeRecord cmr2 = new CalcModeRecord(CalcModeRecord.manual);
            this.outputFile.write(cmr2);
        }
        CalcCountRecord ccr = new CalcCountRecord(100);
        this.outputFile.write(ccr);
        RefModeRecord rmr2 = new RefModeRecord();
        this.outputFile.write(rmr2);
        IterationRecord itr = new IterationRecord(false);
        this.outputFile.write(itr);
        DeltaRecord dtr = new DeltaRecord(0.001d);
        this.outputFile.write(dtr);
        SaveRecalcRecord srr = new SaveRecalcRecord(this.settings.getRecalculateFormulasBeforeSave());
        this.outputFile.write(srr);
        PrintHeadersRecord phr = new PrintHeadersRecord(this.settings.getPrintHeaders());
        this.outputFile.write(phr);
        PrintGridLinesRecord pglr = new PrintGridLinesRecord(this.settings.getPrintGridLines());
        this.outputFile.write(pglr);
        GridSetRecord gsr = new GridSetRecord(true);
        this.outputFile.write(gsr);
        GuttersRecord gutr = new GuttersRecord();
        gutr.setMaxColumnOutline(this.maxColumnOutlineLevel + 1);
        gutr.setMaxRowOutline(this.maxRowOutlineLevel + 1);
        this.outputFile.write(gutr);
        DefaultRowHeightRecord drhr = new DefaultRowHeightRecord(this.settings.getDefaultRowHeight(), this.settings.getDefaultRowHeight() != 255);
        this.outputFile.write(drhr);
        if (this.maxRowOutlineLevel <= 0) {
            z = true;
        } else {
            z = true;
            this.workspaceOptions.setRowOutlines(true);
        }
        if (this.maxColumnOutlineLevel > 0) {
            this.workspaceOptions.setColumnOutlines(z);
        }
        this.workspaceOptions.setFitToPages(this.settings.getFitToPages());
        this.outputFile.write(this.workspaceOptions);
        if (this.rowBreaks.size() > 0) {
            int[] rb = new int[this.rowBreaks.size()];
            for (int i2 = 0; i2 < rb.length; i2++) {
                rb[i2] = ((Integer) this.rowBreaks.get(i2)).intValue();
            }
            HorizontalPageBreaksRecord hpbr = new HorizontalPageBreaksRecord(rb);
            this.outputFile.write(hpbr);
        }
        if (this.columnBreaks.size() > 0) {
            int[] rb2 = new int[this.columnBreaks.size()];
            for (int i3 = 0; i3 < rb2.length; i3++) {
                rb2[i3] = ((Integer) this.columnBreaks.get(i3)).intValue();
            }
            VerticalPageBreaksRecord hpbr2 = new VerticalPageBreaksRecord(rb2);
            this.outputFile.write(hpbr2);
        }
        HeaderRecord header = new HeaderRecord(this.settings.getHeader().toString());
        this.outputFile.write(header);
        FooterRecord footer2 = new FooterRecord(this.settings.getFooter().toString());
        this.outputFile.write(footer2);
        HorizontalCentreRecord hcr = new HorizontalCentreRecord(this.settings.isHorizontalCentre());
        this.outputFile.write(hcr);
        VerticalCentreRecord vcr = new VerticalCentreRecord(this.settings.isVerticalCentre());
        this.outputFile.write(vcr);
        if (this.settings.getLeftMargin() != this.settings.getDefaultWidthMargin()) {
            MarginRecord mr = new LeftMarginRecord(this.settings.getLeftMargin());
            this.outputFile.write(mr);
        }
        if (this.settings.getRightMargin() == this.settings.getDefaultWidthMargin()) {
            footer = footer2;
        } else {
            footer = footer2;
            MarginRecord mr2 = new RightMarginRecord(this.settings.getRightMargin());
            this.outputFile.write(mr2);
        }
        if (this.settings.getTopMargin() != this.settings.getDefaultHeightMargin()) {
            MarginRecord mr3 = new TopMarginRecord(this.settings.getTopMargin());
            this.outputFile.write(mr3);
        }
        if (this.settings.getBottomMargin() != this.settings.getDefaultHeightMargin()) {
            MarginRecord mr4 = new BottomMarginRecord(this.settings.getBottomMargin());
            this.outputFile.write(mr4);
        }
        PLSRecord pLSRecord = this.plsRecord;
        if (pLSRecord != null) {
            this.outputFile.write(pLSRecord);
        }
        SetupRecord setup = new SetupRecord(this.settings);
        this.outputFile.write(setup);
        if (this.settings.isProtected()) {
            ProtectRecord pr = new ProtectRecord(this.settings.isProtected());
            this.outputFile.write(pr);
            ScenarioProtectRecord spr = new ScenarioProtectRecord(this.settings.isProtected());
            this.outputFile.write(spr);
            ObjectProtectRecord opr = new ObjectProtectRecord(this.settings.isProtected());
            this.outputFile.write(opr);
            if (this.settings.getPassword() == null) {
                if (this.settings.getPasswordHash() != 0) {
                    PasswordRecord pw = new PasswordRecord(this.settings.getPasswordHash());
                    this.outputFile.write(pw);
                }
            } else {
                PasswordRecord pw2 = new PasswordRecord(this.settings.getPassword());
                this.outputFile.write(pw2);
            }
        }
        indexRecord.setDataStartPosition(this.outputFile.getPos());
        DefaultColumnWidth dcw = new DefaultColumnWidth(this.settings.getDefaultColumnWidth());
        this.outputFile.write(dcw);
        WritableCellFormat normalStyle = this.sheet.getWorkbook().getStyles().getNormalStyle();
        WritableCellFormat defaultDateFormat2 = this.sheet.getWorkbook().getStyles().getDefaultDateFormat();
        Iterator colit = this.columnFormats.iterator();
        while (colit.hasNext()) {
            Iterator colit2 = colit;
            ColumnInfoRecord cir = (ColumnInfoRecord) colit.next();
            FooterRecord footer3 = footer;
            GuttersRecord gutr2 = gutr;
            if (cir.getColumn() < 256) {
                this.outputFile.write(cir);
            }
            XFRecord xfr = cir.getCellFormat();
            if (xfr != normalStyle) {
                rmr = rmr2;
                if (cir.getColumn() < 256) {
                    Cell[] cells = getColumn(cir.getColumn());
                    int i4 = 0;
                    while (true) {
                        ColumnInfoRecord cir2 = cir;
                        if (i4 < cells.length) {
                            if (cells[i4] != null && (cells[i4].getCellFormat() == normalStyle || cells[i4].getCellFormat() == defaultDateFormat2)) {
                                ((WritableCell) cells[i4]).setCellFormat(xfr);
                            }
                            i4++;
                            cir = cir2;
                        }
                    }
                }
            } else {
                rmr = rmr2;
            }
            footer = footer3;
            colit = colit2;
            gutr = gutr2;
            rmr2 = rmr;
        }
        AutoFilter autoFilter = this.autoFilter;
        if (autoFilter != null) {
            autoFilter.write(this.outputFile);
        }
        DimensionRecord dr = new DimensionRecord(this.numRows, this.numCols);
        this.outputFile.write(dr);
        int block = 0;
        while (block < numBlocks2) {
            DBCellRecord dbcell = new DBCellRecord(this.outputFile.getPos());
            DimensionRecord dr2 = dr;
            int blockRows = Math.min(32, this.numRows - (block * 32));
            boolean firstRow = true;
            WritableCellFormat normalStyle2 = normalStyle;
            int i5 = block * 32;
            while (true) {
                defaultDateFormat = defaultDateFormat2;
                if (i5 >= (block * 32) + blockRows) {
                    break;
                }
                RowRecord[] rowRecordArr = this.rows;
                if (rowRecordArr[i5] != null) {
                    numBlocks = numBlocks2;
                    rowRecordArr[i5].write(this.outputFile);
                    if (firstRow) {
                        dbcell.setCellOffset(this.outputFile.getPos());
                        firstRow = false;
                    }
                } else {
                    numBlocks = numBlocks2;
                }
                i5++;
                numBlocks2 = numBlocks;
                defaultDateFormat2 = defaultDateFormat;
            }
            int numBlocks3 = numBlocks2;
            for (int i6 = block * 32; i6 < (block * 32) + blockRows; i6++) {
                if (this.rows[i6] != null) {
                    dbcell.addCellRowPosition(this.outputFile.getPos());
                    this.rows[i6].writeCells(this.outputFile);
                }
            }
            indexRecord.addBlockPosition(this.outputFile.getPos());
            dbcell.setPosition(this.outputFile.getPos());
            this.outputFile.write(dbcell);
            block++;
            dr = dr2;
            normalStyle = normalStyle2;
            numBlocks2 = numBlocks3;
            defaultDateFormat2 = defaultDateFormat;
        }
        if (!this.workbookSettings.getDrawingsDisabled()) {
            this.drawingWriter.write(this.outputFile);
        }
        Window2Record w2r = new Window2Record(this.settings);
        this.outputFile.write(w2r);
        if (this.settings.getHorizontalFreeze() != 0 || this.settings.getVerticalFreeze() != 0) {
            PaneRecord pr2 = new PaneRecord(this.settings.getHorizontalFreeze(), this.settings.getVerticalFreeze());
            this.outputFile.write(pr2);
            SelectionRecord sr = new SelectionRecord(SelectionRecord.upperLeft, 0, 0);
            this.outputFile.write(sr);
            if (this.settings.getHorizontalFreeze() != 0) {
                SelectionRecord sr2 = new SelectionRecord(SelectionRecord.upperRight, this.settings.getHorizontalFreeze(), 0);
                this.outputFile.write(sr2);
            }
            if (this.settings.getVerticalFreeze() != 0) {
                SelectionRecord sr3 = new SelectionRecord(SelectionRecord.lowerLeft, 0, this.settings.getVerticalFreeze());
                this.outputFile.write(sr3);
            }
            if (this.settings.getHorizontalFreeze() != 0 && this.settings.getVerticalFreeze() != 0) {
                SelectionRecord sr4 = new SelectionRecord(SelectionRecord.lowerRight, this.settings.getHorizontalFreeze(), this.settings.getVerticalFreeze());
                this.outputFile.write(sr4);
            }
            Weird1Record w1r = new Weird1Record();
            this.outputFile.write(w1r);
        } else {
            SelectionRecord sr5 = new SelectionRecord(SelectionRecord.upperLeft, 0, 0);
            this.outputFile.write(sr5);
        }
        if (this.settings.getZoomFactor() != 100) {
            SCLRecord sclr = new SCLRecord(this.settings.getZoomFactor());
            this.outputFile.write(sclr);
        }
        this.mergedCells.write(this.outputFile);
        Iterator hi = this.hyperlinks.iterator();
        while (hi.hasNext()) {
            WritableHyperlink hlr = (WritableHyperlink) hi.next();
            this.outputFile.write(hlr);
        }
        ButtonPropertySetRecord buttonPropertySetRecord = this.buttonPropertySet;
        if (buttonPropertySetRecord != null) {
            this.outputFile.write(buttonPropertySetRecord);
        }
        if (this.dataValidation != null || this.validatedCells.size() > 0) {
            writeDataValidation();
        }
        ArrayList arrayList = this.conditionalFormats;
        if (arrayList != null && arrayList.size() > 0) {
            Iterator i7 = this.conditionalFormats.iterator();
            while (i7.hasNext()) {
                ConditionalFormat cf = (ConditionalFormat) i7.next();
                cf.write(this.outputFile);
            }
        }
        EOFRecord eof = new EOFRecord();
        this.outputFile.write(eof);
        this.outputFile.setData(indexRecord.getData(), indexPos + 4);
    }

    final HeaderRecord getHeader() {
        return this.header;
    }

    final FooterRecord getFooter() {
        return this.footer;
    }

    void setWriteData(RowRecord[] rws, ArrayList rb, ArrayList cb, ArrayList hl, MergedCells mc, TreeSet cf, int mrol, int mcol) {
        this.rows = rws;
        this.rowBreaks = rb;
        this.columnBreaks = cb;
        this.hyperlinks = hl;
        this.mergedCells = mc;
        this.columnFormats = cf;
        this.maxRowOutlineLevel = mrol;
        this.maxColumnOutlineLevel = mcol;
    }

    void setDimensions(int rws, int cls) {
        this.numRows = rws;
        this.numCols = cls;
    }

    void setSettings(SheetSettings sr) {
        this.settings = sr;
    }

    WorkspaceInformationRecord getWorkspaceOptions() {
        return this.workspaceOptions;
    }

    void setWorkspaceOptions(WorkspaceInformationRecord wo) {
        if (wo != null) {
            this.workspaceOptions = wo;
        }
    }

    void setCharts(Chart[] ch) {
        this.drawingWriter.setCharts(ch);
    }

    void setDrawings(ArrayList dr, boolean mod) {
        this.drawingWriter.setDrawings(dr, mod);
    }

    Chart[] getCharts() {
        return this.drawingWriter.getCharts();
    }

    void checkMergedBorders() {
        Range[] mcells = this.mergedCells.getMergedCells();
        ArrayList borderFormats = new ArrayList();
        for (Range range : mcells) {
            Cell topLeft = range.getTopLeft();
            XFRecord tlformat = (XFRecord) topLeft.getCellFormat();
            if (tlformat != null && tlformat.hasBorders() && !tlformat.isRead()) {
                try {
                    CellXFRecord cf1 = new CellXFRecord(tlformat);
                    Cell bottomRight = range.getBottomRight();
                    cf1.setBorder(Border.ALL, BorderLineStyle.NONE, Colour.BLACK);
                    cf1.setBorder(Border.LEFT, tlformat.getBorderLine(Border.LEFT), tlformat.getBorderColour(Border.LEFT));
                    cf1.setBorder(Border.TOP, tlformat.getBorderLine(Border.TOP), tlformat.getBorderColour(Border.TOP));
                    if (topLeft.getRow() == bottomRight.getRow()) {
                        cf1.setBorder(Border.BOTTOM, tlformat.getBorderLine(Border.BOTTOM), tlformat.getBorderColour(Border.BOTTOM));
                    }
                    if (topLeft.getColumn() == bottomRight.getColumn()) {
                        cf1.setBorder(Border.RIGHT, tlformat.getBorderLine(Border.RIGHT), tlformat.getBorderColour(Border.RIGHT));
                    }
                    int index = borderFormats.indexOf(cf1);
                    if (index != -1) {
                        cf1 = (CellXFRecord) borderFormats.get(index);
                    } else {
                        borderFormats.add(cf1);
                    }
                    ((WritableCell) topLeft).setCellFormat(cf1);
                    if (bottomRight.getRow() > topLeft.getRow()) {
                        if (bottomRight.getColumn() != topLeft.getColumn()) {
                            CellXFRecord cf2 = new CellXFRecord(tlformat);
                            cf2.setBorder(Border.ALL, BorderLineStyle.NONE, Colour.BLACK);
                            cf2.setBorder(Border.LEFT, tlformat.getBorderLine(Border.LEFT), tlformat.getBorderColour(Border.LEFT));
                            cf2.setBorder(Border.BOTTOM, tlformat.getBorderLine(Border.BOTTOM), tlformat.getBorderColour(Border.BOTTOM));
                            int index2 = borderFormats.indexOf(cf2);
                            if (index2 != -1) {
                                cf2 = (CellXFRecord) borderFormats.get(index2);
                            } else {
                                borderFormats.add(cf2);
                            }
                            this.sheet.addCell(new Blank(topLeft.getColumn(), bottomRight.getRow(), cf2));
                        }
                        for (int i = topLeft.getRow() + 1; i < bottomRight.getRow(); i++) {
                            CellXFRecord cf3 = new CellXFRecord(tlformat);
                            cf3.setBorder(Border.ALL, BorderLineStyle.NONE, Colour.BLACK);
                            cf3.setBorder(Border.LEFT, tlformat.getBorderLine(Border.LEFT), tlformat.getBorderColour(Border.LEFT));
                            if (topLeft.getColumn() == bottomRight.getColumn()) {
                                cf3.setBorder(Border.RIGHT, tlformat.getBorderLine(Border.RIGHT), tlformat.getBorderColour(Border.RIGHT));
                            }
                            int index3 = borderFormats.indexOf(cf3);
                            if (index3 != -1) {
                                cf3 = (CellXFRecord) borderFormats.get(index3);
                            } else {
                                borderFormats.add(cf3);
                            }
                            this.sheet.addCell(new Blank(topLeft.getColumn(), i, cf3));
                        }
                    }
                    int i2 = bottomRight.getColumn();
                    if (i2 > topLeft.getColumn()) {
                        if (bottomRight.getRow() != topLeft.getRow()) {
                            CellXFRecord cf6 = new CellXFRecord(tlformat);
                            cf6.setBorder(Border.ALL, BorderLineStyle.NONE, Colour.BLACK);
                            cf6.setBorder(Border.RIGHT, tlformat.getBorderLine(Border.RIGHT), tlformat.getBorderColour(Border.RIGHT));
                            cf6.setBorder(Border.TOP, tlformat.getBorderLine(Border.TOP), tlformat.getBorderColour(Border.TOP));
                            int index4 = borderFormats.indexOf(cf6);
                            if (index4 != -1) {
                                cf6 = (CellXFRecord) borderFormats.get(index4);
                            } else {
                                borderFormats.add(cf6);
                            }
                            this.sheet.addCell(new Blank(bottomRight.getColumn(), topLeft.getRow(), cf6));
                        }
                        for (int i3 = topLeft.getRow() + 1; i3 < bottomRight.getRow(); i3++) {
                            CellXFRecord cf7 = new CellXFRecord(tlformat);
                            cf7.setBorder(Border.ALL, BorderLineStyle.NONE, Colour.BLACK);
                            cf7.setBorder(Border.RIGHT, tlformat.getBorderLine(Border.RIGHT), tlformat.getBorderColour(Border.RIGHT));
                            int index5 = borderFormats.indexOf(cf7);
                            if (index5 != -1) {
                                cf7 = (CellXFRecord) borderFormats.get(index5);
                            } else {
                                borderFormats.add(cf7);
                            }
                            this.sheet.addCell(new Blank(bottomRight.getColumn(), i3, cf7));
                        }
                        int i4 = topLeft.getColumn();
                        for (int i5 = i4 + 1; i5 < bottomRight.getColumn(); i5++) {
                            CellXFRecord cf8 = new CellXFRecord(tlformat);
                            cf8.setBorder(Border.ALL, BorderLineStyle.NONE, Colour.BLACK);
                            cf8.setBorder(Border.TOP, tlformat.getBorderLine(Border.TOP), tlformat.getBorderColour(Border.TOP));
                            if (topLeft.getRow() == bottomRight.getRow()) {
                                cf8.setBorder(Border.BOTTOM, tlformat.getBorderLine(Border.BOTTOM), tlformat.getBorderColour(Border.BOTTOM));
                            }
                            int index6 = borderFormats.indexOf(cf8);
                            if (index6 != -1) {
                                cf8 = (CellXFRecord) borderFormats.get(index6);
                            } else {
                                borderFormats.add(cf8);
                            }
                            this.sheet.addCell(new Blank(i5, topLeft.getRow(), cf8));
                        }
                    }
                    int i6 = bottomRight.getColumn();
                    if (i6 > topLeft.getColumn() || bottomRight.getRow() > topLeft.getRow()) {
                        CellXFRecord cf4 = new CellXFRecord(tlformat);
                        cf4.setBorder(Border.ALL, BorderLineStyle.NONE, Colour.BLACK);
                        cf4.setBorder(Border.RIGHT, tlformat.getBorderLine(Border.RIGHT), tlformat.getBorderColour(Border.RIGHT));
                        cf4.setBorder(Border.BOTTOM, tlformat.getBorderLine(Border.BOTTOM), tlformat.getBorderColour(Border.BOTTOM));
                        if (bottomRight.getRow() == topLeft.getRow()) {
                            cf4.setBorder(Border.TOP, tlformat.getBorderLine(Border.TOP), tlformat.getBorderColour(Border.TOP));
                        }
                        if (bottomRight.getColumn() == topLeft.getColumn()) {
                            cf4.setBorder(Border.LEFT, tlformat.getBorderLine(Border.LEFT), tlformat.getBorderColour(Border.LEFT));
                        }
                        int index7 = borderFormats.indexOf(cf4);
                        if (index7 != -1) {
                            cf4 = (CellXFRecord) borderFormats.get(index7);
                        } else {
                            borderFormats.add(cf4);
                        }
                        this.sheet.addCell(new Blank(bottomRight.getColumn(), bottomRight.getRow(), cf4));
                        for (int i7 = topLeft.getColumn() + 1; i7 < bottomRight.getColumn(); i7++) {
                            CellXFRecord cf5 = new CellXFRecord(tlformat);
                            cf5.setBorder(Border.ALL, BorderLineStyle.NONE, Colour.BLACK);
                            cf5.setBorder(Border.BOTTOM, tlformat.getBorderLine(Border.BOTTOM), tlformat.getBorderColour(Border.BOTTOM));
                            if (topLeft.getRow() == bottomRight.getRow()) {
                                cf5.setBorder(Border.TOP, tlformat.getBorderLine(Border.TOP), tlformat.getBorderColour(Border.TOP));
                            }
                            int index8 = borderFormats.indexOf(cf5);
                            if (index8 != -1) {
                                cf5 = (CellXFRecord) borderFormats.get(index8);
                            } else {
                                borderFormats.add(cf5);
                            }
                            this.sheet.addCell(new Blank(i7, bottomRight.getRow(), cf5));
                        }
                    }
                } catch (WriteException e) {
                    logger.warn(e.toString());
                }
            }
        }
    }

    private Cell[] getColumn(int col) {
        boolean found = false;
        int row = this.numRows - 1;
        while (row >= 0 && !found) {
            RowRecord[] rowRecordArr = this.rows;
            if (rowRecordArr[row] == null || rowRecordArr[row].getCell(col) == null) {
                row--;
            } else {
                found = true;
            }
        }
        Cell[] cells = new Cell[row + 1];
        for (int i = 0; i <= row; i++) {
            RowRecord[] rowRecordArr2 = this.rows;
            cells[i] = rowRecordArr2[i] != null ? rowRecordArr2[i].getCell(col) : null;
        }
        return cells;
    }

    void setChartOnly() {
        this.chartOnly = true;
    }

    void setPLS(PLSRecord pls) {
        this.plsRecord = pls;
    }

    void setButtonPropertySet(ButtonPropertySetRecord bps) {
        this.buttonPropertySet = bps;
    }

    void setDataValidation(DataValidation dv, ArrayList vc) {
        this.dataValidation = dv;
        this.validatedCells = vc;
    }

    void setConditionalFormats(ArrayList cf) {
        this.conditionalFormats = cf;
    }

    void setAutoFilter(AutoFilter af) {
        this.autoFilter = af;
    }

    private void writeDataValidation() throws IOException {
        if (this.dataValidation != null && this.validatedCells.size() == 0) {
            this.dataValidation.write(this.outputFile);
            return;
        }
        if (this.dataValidation == null && this.validatedCells.size() > 0) {
            int comboBoxId = this.sheet.getComboBox() != null ? this.sheet.getComboBox().getObjectId() : -1;
            this.dataValidation = new DataValidation(comboBoxId, this.sheet.getWorkbook(), this.sheet.getWorkbook(), this.workbookSettings);
        }
        Iterator i = this.validatedCells.iterator();
        while (i.hasNext()) {
            CellValue cv = (CellValue) i.next();
            CellFeatures cf = cv.getCellFeatures();
            if (!cf.getDVParser().copied()) {
                if (!cf.getDVParser().extendedCellsValidation()) {
                    DataValiditySettingsRecord dvsr = new DataValiditySettingsRecord(cf.getDVParser());
                    this.dataValidation.add(dvsr);
                } else if (cv.getColumn() == cf.getDVParser().getFirstColumn() && cv.getRow() == cf.getDVParser().getFirstRow()) {
                    DataValiditySettingsRecord dvsr2 = new DataValiditySettingsRecord(cf.getDVParser());
                    this.dataValidation.add(dvsr2);
                }
            }
        }
        this.dataValidation.write(this.outputFile);
    }
}
