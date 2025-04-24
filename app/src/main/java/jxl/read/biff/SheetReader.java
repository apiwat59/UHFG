package jxl.read.biff;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import jxl.Cell;
import jxl.CellFeatures;
import jxl.CellReferenceHelper;
import jxl.CellType;
import jxl.HeaderFooter;
import jxl.Range;
import jxl.SheetSettings;
import jxl.WorkbookSettings;
import jxl.biff.AutoFilter;
import jxl.biff.AutoFilterInfoRecord;
import jxl.biff.AutoFilterRecord;
import jxl.biff.ConditionalFormat;
import jxl.biff.ConditionalFormatRangeRecord;
import jxl.biff.ConditionalFormatRecord;
import jxl.biff.ContinueRecord;
import jxl.biff.DataValidation;
import jxl.biff.DataValidityListRecord;
import jxl.biff.DataValiditySettingsRecord;
import jxl.biff.FilterModeRecord;
import jxl.biff.FormattingRecords;
import jxl.biff.Type;
import jxl.biff.WorkspaceInformationRecord;
import jxl.biff.drawing.Chart;
import jxl.biff.drawing.Comment;
import jxl.biff.drawing.Drawing2;
import jxl.biff.drawing.DrawingData;
import jxl.biff.drawing.MsoDrawingRecord;
import jxl.biff.drawing.NoteRecord;
import jxl.biff.drawing.ObjRecord;
import jxl.biff.formula.FormulaException;
import jxl.common.Assert;
import jxl.common.Logger;
import jxl.format.PageOrder;
import jxl.format.PageOrientation;
import jxl.format.PaperSize;

/* loaded from: classes.dex */
final class SheetReader {
    private static Logger logger = Logger.getLogger(SheetReader.class);
    private AutoFilter autoFilter;
    private ButtonPropertySetRecord buttonPropertySet;
    private Cell[][] cells;
    private int[] columnBreaks;
    private DataValidation dataValidation;
    private DrawingData drawingData;
    private File excelFile;
    private FormattingRecords formattingRecords;
    private int maxColumnOutlineLevel;
    private int maxRowOutlineLevel;
    private Range[] mergedCells;
    private boolean nineteenFour;
    private int numCols;
    private int numRows;
    private PLSRecord plsRecord;
    private int[] rowBreaks;
    private SheetSettings settings;
    private SSTRecord sharedStrings;
    private SheetImpl sheet;
    private BOFRecord sheetBof;
    private int startPosition;
    private WorkbookParser workbook;
    private BOFRecord workbookBof;
    private WorkbookSettings workbookSettings;
    private WorkspaceInformationRecord workspaceOptions;
    private ArrayList columnInfosArray = new ArrayList();
    private ArrayList sharedFormulas = new ArrayList();
    private ArrayList hyperlinks = new ArrayList();
    private ArrayList conditionalFormats = new ArrayList();
    private ArrayList rowProperties = new ArrayList(10);
    private ArrayList charts = new ArrayList();
    private ArrayList drawings = new ArrayList();
    private ArrayList outOfBoundsCells = new ArrayList();

    SheetReader(File f, SSTRecord sst, FormattingRecords fr, BOFRecord sb, BOFRecord wb, boolean nf, WorkbookParser wp, int sp, SheetImpl sh) {
        this.excelFile = f;
        this.sharedStrings = sst;
        this.formattingRecords = fr;
        this.sheetBof = sb;
        this.workbookBof = wb;
        this.nineteenFour = nf;
        this.workbook = wp;
        this.startPosition = sp;
        this.sheet = sh;
        this.settings = new SheetSettings(sh);
        this.workbookSettings = this.workbook.getSettings();
    }

    private void addCell(Cell cell) {
        if (cell.getRow() < this.numRows && cell.getColumn() < this.numCols) {
            if (this.cells[cell.getRow()][cell.getColumn()] != null) {
                StringBuffer sb = new StringBuffer();
                CellReferenceHelper.getCellReference(cell.getColumn(), cell.getRow(), sb);
                logger.warn("Cell " + sb.toString() + " already contains data");
            }
            this.cells[cell.getRow()][cell.getColumn()] = cell;
            return;
        }
        this.outOfBoundsCells.add(cell);
    }

    final void read() {
        AutoFilterInfoRecord autoFilterInfo;
        ObjRecord objRecord;
        boolean sharedFormulaAdded;
        PrintGridLinesRecord printGridLinesRecord;
        MsoDrawingRecord msoRecord;
        HashMap comments;
        ArrayList objectIds;
        FilterModeRecord filterMode;
        Record r;
        ConditionalFormat condFormat;
        AutoFilterInfoRecord autoFilterInfo2;
        MsoDrawingRecord msoRecord2;
        boolean sharedFormulaAdded2;
        AutoFilterInfoRecord autoFilterInfo3;
        MsoDrawingRecord msoRecord3;
        PrintGridLinesRecord printGridLinesRecord2;
        int i;
        int i2;
        HashMap comments2;
        MsoDrawingRecord msoRecord4;
        MsoDrawingRecord msoRecord5;
        ArrayList objectIds2;
        MsoDrawingRecord msoRecord6;
        VerticalPageBreaksRecord dr;
        HorizontalPageBreaksRecord dr2;
        FooterRecord fr;
        HeaderRecord hr;
        Cell lr;
        AutoFilterInfoRecord autoFilterInfo4;
        MsoDrawingRecord msoRecord7;
        Window2Record window2Record;
        int num;
        ConditionalFormat condFormat2;
        AutoFilterInfoRecord autoFilterInfo5;
        ObjRecord objRecord2;
        DimensionRecord dr3;
        this.excelFile.setPos(this.startPosition);
        HashMap comments3 = new HashMap();
        ArrayList objectIds3 = new ArrayList();
        Window2Record window2Record2 = null;
        ContinueRecord continueRecord = null;
        boolean sharedFormulaAdded3 = false;
        PrintGridLinesRecord printGridLinesRecord3 = 1;
        MsoDrawingRecord msoRecord8 = null;
        boolean firstMsoRecord = true;
        AutoFilterInfoRecord autoFilterInfo6 = null;
        BaseSharedFormulaRecord sharedFormula = null;
        ObjRecord objRecord3 = null;
        FilterModeRecord filterMode2 = null;
        ConditionalFormat condFormat3 = null;
        while (printGridLinesRecord3 != null) {
            Record r2 = this.excelFile.next();
            Type type = r2.getType();
            if (type == Type.UNKNOWN && r2.getCode() == 0) {
                logger.warn("Biff code zero found");
                if (r2.getLength() == 10) {
                    logger.warn("Biff code zero found - trying a dimension record.");
                    r2.setType(Type.DIMENSION);
                } else {
                    logger.warn("Biff code zero found - Ignoring.");
                }
            }
            if (type == Type.DIMENSION) {
                if (this.workbookBof.isBiff8()) {
                    dr3 = new DimensionRecord(r2);
                } else {
                    dr3 = new DimensionRecord(r2, DimensionRecord.biff7);
                }
                this.numRows = dr3.getNumberOfRows();
                int numberOfColumns = dr3.getNumberOfColumns();
                this.numCols = numberOfColumns;
                this.cells = (Cell[][]) Array.newInstance((Class<?>) Cell.class, this.numRows, numberOfColumns);
                autoFilterInfo = autoFilterInfo6;
                objRecord = objRecord3;
                sharedFormulaAdded = sharedFormulaAdded3;
                printGridLinesRecord = printGridLinesRecord3;
                msoRecord = msoRecord8;
                comments = comments3;
                objectIds = objectIds3;
                r = r2;
                filterMode = filterMode2;
                condFormat = condFormat3;
            } else if (type == Type.LABELSST) {
                Cell label = new LabelSSTRecord(r2, this.sharedStrings, this.formattingRecords, this.sheet);
                addCell(label);
                autoFilterInfo = autoFilterInfo6;
                objRecord = objRecord3;
                sharedFormulaAdded = sharedFormulaAdded3;
                printGridLinesRecord = printGridLinesRecord3;
                msoRecord = msoRecord8;
                comments = comments3;
                objectIds = objectIds3;
                filterMode = filterMode2;
                r = r2;
                condFormat = condFormat3;
            } else {
                FilterModeRecord filterMode3 = filterMode2;
                if (type == Type.RK) {
                    autoFilterInfo = autoFilterInfo6;
                    objRecord = objRecord3;
                    sharedFormulaAdded = sharedFormulaAdded3;
                    printGridLinesRecord = printGridLinesRecord3;
                    msoRecord = msoRecord8;
                    comments = comments3;
                    objectIds = objectIds3;
                    filterMode = filterMode3;
                    r = r2;
                    condFormat = condFormat3;
                } else if (type == Type.RK2) {
                    autoFilterInfo = autoFilterInfo6;
                    objRecord = objRecord3;
                    sharedFormulaAdded = sharedFormulaAdded3;
                    printGridLinesRecord = printGridLinesRecord3;
                    msoRecord = msoRecord8;
                    comments = comments3;
                    objectIds = objectIds3;
                    filterMode = filterMode3;
                    r = r2;
                    condFormat = condFormat3;
                } else if (type == Type.HLINK) {
                    HyperlinkRecord hr2 = new HyperlinkRecord(r2, this.sheet, this.workbookSettings);
                    this.hyperlinks.add(hr2);
                    autoFilterInfo = autoFilterInfo6;
                    objRecord = objRecord3;
                    sharedFormulaAdded = sharedFormulaAdded3;
                    printGridLinesRecord = printGridLinesRecord3;
                    msoRecord = msoRecord8;
                    comments = comments3;
                    objectIds = objectIds3;
                    filterMode = filterMode3;
                    r = r2;
                    condFormat = condFormat3;
                } else if (type == Type.MERGEDCELLS) {
                    MergedCellsRecord mc = new MergedCellsRecord(r2, this.sheet);
                    Range[] rangeArr = this.mergedCells;
                    if (rangeArr == null) {
                        this.mergedCells = mc.getRanges();
                        autoFilterInfo5 = autoFilterInfo6;
                        objRecord2 = objRecord3;
                    } else {
                        Range[] newMergedCells = new Range[rangeArr.length + mc.getRanges().length];
                        Range[] rangeArr2 = this.mergedCells;
                        autoFilterInfo5 = autoFilterInfo6;
                        System.arraycopy(rangeArr2, 0, newMergedCells, 0, rangeArr2.length);
                        objRecord2 = objRecord3;
                        System.arraycopy(mc.getRanges(), 0, newMergedCells, this.mergedCells.length, mc.getRanges().length);
                        this.mergedCells = newMergedCells;
                    }
                    sharedFormulaAdded = sharedFormulaAdded3;
                    printGridLinesRecord = printGridLinesRecord3;
                    msoRecord = msoRecord8;
                    comments = comments3;
                    objectIds = objectIds3;
                    objRecord = objRecord2;
                    filterMode = filterMode3;
                    autoFilterInfo = autoFilterInfo5;
                    r = r2;
                    condFormat = condFormat3;
                } else {
                    AutoFilterInfoRecord autoFilterInfo7 = autoFilterInfo6;
                    ObjRecord objRecord4 = objRecord3;
                    if (type == Type.MULRK) {
                        MulRKRecord mulrk = new MulRKRecord(r2);
                        int num2 = mulrk.getNumberOfColumns();
                        int i3 = 0;
                        while (i3 < num2) {
                            int ixf = mulrk.getXFIndex(i3);
                            MulRKRecord mulrk2 = mulrk;
                            NumberValue nv = new NumberValue(mulrk.getRow(), mulrk.getFirstColumn() + i3, RKHelper.getDouble(mulrk.getRKNumber(i3)), ixf, this.formattingRecords, this.sheet);
                            if (this.formattingRecords.isDate(ixf)) {
                                num = num2;
                                condFormat2 = condFormat3;
                                Cell dc = new DateRecord(nv, ixf, this.formattingRecords, this.nineteenFour, this.sheet);
                                addCell(dc);
                            } else {
                                num = num2;
                                condFormat2 = condFormat3;
                                nv.setNumberFormat(this.formattingRecords.getNumberFormat(ixf));
                                addCell(nv);
                            }
                            i3++;
                            mulrk = mulrk2;
                            num2 = num;
                            condFormat3 = condFormat2;
                        }
                        sharedFormulaAdded = sharedFormulaAdded3;
                        printGridLinesRecord = printGridLinesRecord3;
                        msoRecord = msoRecord8;
                        comments = comments3;
                        objectIds = objectIds3;
                        filterMode = filterMode3;
                        autoFilterInfo = autoFilterInfo7;
                        condFormat = condFormat3;
                        r = r2;
                        objRecord = objRecord4;
                    } else {
                        ConditionalFormat condFormat4 = condFormat3;
                        if (type == Type.NUMBER) {
                            NumberRecord nr = new NumberRecord(r2, this.formattingRecords, this.sheet);
                            if (this.formattingRecords.isDate(nr.getXFIndex())) {
                                Cell dc2 = new DateRecord(nr, nr.getXFIndex(), this.formattingRecords, this.nineteenFour, this.sheet);
                                addCell(dc2);
                            } else {
                                addCell(nr);
                            }
                            sharedFormulaAdded = sharedFormulaAdded3;
                            printGridLinesRecord = printGridLinesRecord3;
                            msoRecord = msoRecord8;
                            comments = comments3;
                            objectIds = objectIds3;
                            filterMode = filterMode3;
                            autoFilterInfo = autoFilterInfo7;
                            condFormat = condFormat4;
                            r = r2;
                            objRecord = objRecord4;
                        } else if (type == Type.BOOLERR) {
                            BooleanRecord br = new BooleanRecord(r2, this.formattingRecords, this.sheet);
                            if (br.isError()) {
                                Cell er = new ErrorRecord(br.getRecord(), this.formattingRecords, this.sheet);
                                addCell(er);
                            } else {
                                addCell(br);
                            }
                            sharedFormulaAdded = sharedFormulaAdded3;
                            printGridLinesRecord = printGridLinesRecord3;
                            msoRecord = msoRecord8;
                            comments = comments3;
                            objectIds = objectIds3;
                            filterMode = filterMode3;
                            autoFilterInfo = autoFilterInfo7;
                            condFormat = condFormat4;
                            r = r2;
                            objRecord = objRecord4;
                        } else {
                            if (type == Type.PRINTGRIDLINES) {
                                PrintGridLinesRecord printGridLinesRecord4 = new PrintGridLinesRecord(r2);
                                this.settings.setPrintGridLines(printGridLinesRecord4.getPrintGridLines());
                                comments = comments3;
                                objectIds = objectIds3;
                                objRecord3 = objRecord4;
                                filterMode2 = filterMode3;
                                autoFilterInfo6 = autoFilterInfo7;
                                condFormat3 = condFormat4;
                                r = r2;
                            } else if (type == Type.PRINTHEADERS) {
                                PrintHeadersRecord printHeadersRecord = new PrintHeadersRecord(r2);
                                this.settings.setPrintHeaders(printHeadersRecord.getPrintHeaders());
                                comments = comments3;
                                objectIds = objectIds3;
                                objRecord3 = objRecord4;
                                filterMode2 = filterMode3;
                                autoFilterInfo6 = autoFilterInfo7;
                                condFormat3 = condFormat4;
                                r = r2;
                            } else if (type == Type.WINDOW2) {
                                if (this.workbookBof.isBiff8()) {
                                    window2Record = new Window2Record(r2);
                                } else {
                                    window2Record = new Window2Record(r2, Window2Record.biff7);
                                }
                                this.settings.setShowGridLines(window2Record.getShowGridLines());
                                this.settings.setDisplayZeroValues(window2Record.getDisplayZeroValues());
                                this.settings.setSelected(true);
                                this.settings.setPageBreakPreviewMode(window2Record.isPageBreakPreview());
                                window2Record2 = window2Record;
                                comments = comments3;
                                objectIds = objectIds3;
                                objRecord3 = objRecord4;
                                filterMode2 = filterMode3;
                                autoFilterInfo6 = autoFilterInfo7;
                                condFormat3 = condFormat4;
                                r = r2;
                            } else if (type == Type.PANE) {
                                PaneRecord pr = new PaneRecord(r2);
                                if (window2Record2 != null && window2Record2.getFrozen()) {
                                    this.settings.setVerticalFreeze(pr.getRowsVisible());
                                    this.settings.setHorizontalFreeze(pr.getColumnsVisible());
                                }
                                sharedFormulaAdded = sharedFormulaAdded3;
                                printGridLinesRecord = printGridLinesRecord3;
                                msoRecord = msoRecord8;
                                comments = comments3;
                                objectIds = objectIds3;
                                filterMode = filterMode3;
                                autoFilterInfo = autoFilterInfo7;
                                condFormat = condFormat4;
                                r = r2;
                                objRecord = objRecord4;
                            } else if (type == Type.CONTINUE) {
                                continueRecord = new ContinueRecord(r2);
                                comments = comments3;
                                objectIds = objectIds3;
                                objRecord3 = objRecord4;
                                filterMode2 = filterMode3;
                                autoFilterInfo6 = autoFilterInfo7;
                                condFormat3 = condFormat4;
                                r = r2;
                            } else if (type == Type.NOTE) {
                                if (this.workbookSettings.getDrawingsDisabled()) {
                                    sharedFormulaAdded = sharedFormulaAdded3;
                                    printGridLinesRecord = printGridLinesRecord3;
                                    MsoDrawingRecord msoRecord9 = msoRecord8;
                                    comments = comments3;
                                    objectIds = objectIds3;
                                    filterMode = filterMode3;
                                    condFormat = condFormat4;
                                    r = r2;
                                    objRecord = objRecord4;
                                    autoFilterInfo = autoFilterInfo7;
                                    msoRecord = msoRecord9;
                                } else {
                                    NoteRecord nr2 = new NoteRecord(r2);
                                    Comment comment = (Comment) comments3.remove(new Integer(nr2.getObjectId()));
                                    if (comment == null) {
                                        logger.warn(" cannot find comment for note id " + nr2.getObjectId() + "...ignoring");
                                        sharedFormulaAdded = sharedFormulaAdded3;
                                        printGridLinesRecord = printGridLinesRecord3;
                                        msoRecord7 = msoRecord8;
                                        comments = comments3;
                                        objectIds = objectIds3;
                                        filterMode = filterMode3;
                                        autoFilterInfo4 = autoFilterInfo7;
                                        condFormat = condFormat4;
                                        r = r2;
                                        objRecord = objRecord4;
                                    } else {
                                        comment.setNote(nr2);
                                        this.drawings.add(comment);
                                        sharedFormulaAdded = sharedFormulaAdded3;
                                        printGridLinesRecord = printGridLinesRecord3;
                                        comments = comments3;
                                        filterMode = filterMode3;
                                        autoFilterInfo4 = autoFilterInfo7;
                                        r = r2;
                                        msoRecord7 = msoRecord8;
                                        objectIds = objectIds3;
                                        condFormat = condFormat4;
                                        objRecord = objRecord4;
                                        addCellComment(comment.getColumn(), comment.getRow(), comment.getText(), comment.getWidth(), comment.getHeight());
                                    }
                                    MsoDrawingRecord msoDrawingRecord = msoRecord7;
                                    autoFilterInfo = autoFilterInfo4;
                                    msoRecord = msoDrawingRecord;
                                }
                            } else {
                                sharedFormulaAdded = sharedFormulaAdded3;
                                printGridLinesRecord = printGridLinesRecord3;
                                MsoDrawingRecord msoRecord10 = msoRecord8;
                                comments = comments3;
                                objectIds = objectIds3;
                                filterMode = filterMode3;
                                condFormat = condFormat4;
                                r = r2;
                                objRecord = objRecord4;
                                if (type == Type.ARRAY) {
                                    autoFilterInfo = autoFilterInfo7;
                                    msoRecord = msoRecord10;
                                } else if (type == Type.PROTECT) {
                                    ProtectRecord pr2 = new ProtectRecord(r);
                                    this.settings.setProtected(pr2.isProtected());
                                    autoFilterInfo = autoFilterInfo7;
                                    msoRecord = msoRecord10;
                                } else if (type == Type.SHAREDFORMULA) {
                                    if (sharedFormula == null) {
                                        logger.warn("Shared template formula is null - trying most recent formula template");
                                        ArrayList arrayList = this.sharedFormulas;
                                        SharedFormulaRecord lastSharedFormula = (SharedFormulaRecord) arrayList.get(arrayList.size() - 1);
                                        if (lastSharedFormula != null) {
                                            sharedFormula = lastSharedFormula.getTemplateFormula();
                                        }
                                    }
                                    WorkbookParser workbookParser = this.workbook;
                                    SharedFormulaRecord sfr = new SharedFormulaRecord(r, sharedFormula, workbookParser, workbookParser, this.sheet);
                                    this.sharedFormulas.add(sfr);
                                    sharedFormula = null;
                                    autoFilterInfo6 = autoFilterInfo7;
                                    filterMode2 = filterMode;
                                    condFormat3 = condFormat;
                                    printGridLinesRecord3 = printGridLinesRecord;
                                    sharedFormulaAdded3 = sharedFormulaAdded;
                                    objRecord3 = objRecord;
                                    msoRecord8 = msoRecord10;
                                } else {
                                    if (type == Type.FORMULA) {
                                        autoFilterInfo2 = autoFilterInfo7;
                                        msoRecord2 = msoRecord10;
                                    } else if (type == Type.FORMULA2) {
                                        autoFilterInfo2 = autoFilterInfo7;
                                        msoRecord2 = msoRecord10;
                                    } else if (type == Type.LABEL) {
                                        if (this.workbookBof.isBiff8()) {
                                            lr = new LabelRecord(r, this.formattingRecords, this.sheet, this.workbookSettings);
                                        } else {
                                            lr = new LabelRecord(r, this.formattingRecords, this.sheet, this.workbookSettings, LabelRecord.biff7);
                                        }
                                        addCell(lr);
                                        autoFilterInfo = autoFilterInfo7;
                                        msoRecord = msoRecord10;
                                    } else if (type == Type.RSTRING) {
                                        Assert.verify(!this.workbookBof.isBiff8());
                                        Cell lr2 = new RStringRecord(r, this.formattingRecords, this.sheet, this.workbookSettings, RStringRecord.biff7);
                                        addCell(lr2);
                                        autoFilterInfo = autoFilterInfo7;
                                        msoRecord = msoRecord10;
                                    } else if (type == Type.NAME) {
                                        autoFilterInfo = autoFilterInfo7;
                                        msoRecord = msoRecord10;
                                    } else if (type == Type.PASSWORD) {
                                        PasswordRecord pr3 = new PasswordRecord(r);
                                        this.settings.setPasswordHash(pr3.getPasswordHash());
                                        autoFilterInfo = autoFilterInfo7;
                                        msoRecord = msoRecord10;
                                    } else if (type == Type.ROW) {
                                        RowRecord rr = new RowRecord(r);
                                        if (!rr.isDefaultHeight() || !rr.matchesDefaultFontHeight() || rr.isCollapsed() || rr.hasDefaultFormat() || rr.getOutlineLevel() != 0) {
                                            this.rowProperties.add(rr);
                                        }
                                        autoFilterInfo = autoFilterInfo7;
                                        msoRecord = msoRecord10;
                                    } else if (type == Type.BLANK) {
                                        if (this.workbookSettings.getIgnoreBlanks()) {
                                            autoFilterInfo = autoFilterInfo7;
                                            msoRecord = msoRecord10;
                                        } else {
                                            Cell bc = new BlankCell(r, this.formattingRecords, this.sheet);
                                            addCell(bc);
                                            autoFilterInfo = autoFilterInfo7;
                                            msoRecord = msoRecord10;
                                        }
                                    } else if (type == Type.MULBLANK) {
                                        if (this.workbookSettings.getIgnoreBlanks()) {
                                            autoFilterInfo = autoFilterInfo7;
                                            msoRecord = msoRecord10;
                                        } else {
                                            MulBlankRecord mulblank = new MulBlankRecord(r);
                                            int num3 = mulblank.getNumberOfColumns();
                                            for (int i4 = 0; i4 < num3; i4++) {
                                                Cell mbc = new MulBlankCell(mulblank.getRow(), mulblank.getFirstColumn() + i4, mulblank.getXFIndex(i4), this.formattingRecords, this.sheet);
                                                addCell(mbc);
                                            }
                                            autoFilterInfo = autoFilterInfo7;
                                            msoRecord = msoRecord10;
                                        }
                                    } else if (type == Type.SCL) {
                                        SCLRecord scl = new SCLRecord(r);
                                        this.settings.setZoomFactor(scl.getZoomFactor());
                                        autoFilterInfo = autoFilterInfo7;
                                        msoRecord = msoRecord10;
                                    } else if (type == Type.COLINFO) {
                                        ColumnInfoRecord cir = new ColumnInfoRecord(r);
                                        this.columnInfosArray.add(cir);
                                        autoFilterInfo = autoFilterInfo7;
                                        msoRecord = msoRecord10;
                                    } else if (type == Type.HEADER) {
                                        if (this.workbookBof.isBiff8()) {
                                            hr = new HeaderRecord(r, this.workbookSettings);
                                        } else {
                                            hr = new HeaderRecord(r, this.workbookSettings, HeaderRecord.biff7);
                                        }
                                        HeaderFooter header = new HeaderFooter(hr.getHeader());
                                        this.settings.setHeader(header);
                                        autoFilterInfo = autoFilterInfo7;
                                        msoRecord = msoRecord10;
                                    } else if (type == Type.FOOTER) {
                                        if (this.workbookBof.isBiff8()) {
                                            fr = new FooterRecord(r, this.workbookSettings);
                                        } else {
                                            fr = new FooterRecord(r, this.workbookSettings, FooterRecord.biff7);
                                        }
                                        HeaderFooter footer = new HeaderFooter(fr.getFooter());
                                        this.settings.setFooter(footer);
                                        autoFilterInfo = autoFilterInfo7;
                                        msoRecord = msoRecord10;
                                    } else if (type == Type.SETUP) {
                                        SetupRecord sr = new SetupRecord(r);
                                        if (sr.getInitialized()) {
                                            if (sr.isPortrait()) {
                                                this.settings.setOrientation(PageOrientation.PORTRAIT);
                                            } else {
                                                this.settings.setOrientation(PageOrientation.LANDSCAPE);
                                            }
                                            if (sr.isRightDown()) {
                                                this.settings.setPageOrder(PageOrder.RIGHT_THEN_DOWN);
                                            } else {
                                                this.settings.setPageOrder(PageOrder.DOWN_THEN_RIGHT);
                                            }
                                            this.settings.setPaperSize(PaperSize.getPaperSize(sr.getPaperSize()));
                                            this.settings.setHeaderMargin(sr.getHeaderMargin());
                                            this.settings.setFooterMargin(sr.getFooterMargin());
                                            this.settings.setScaleFactor(sr.getScaleFactor());
                                            this.settings.setPageStart(sr.getPageStart());
                                            this.settings.setFitWidth(sr.getFitWidth());
                                            this.settings.setFitHeight(sr.getFitHeight());
                                            this.settings.setHorizontalPrintResolution(sr.getHorizontalPrintResolution());
                                            this.settings.setVerticalPrintResolution(sr.getVerticalPrintResolution());
                                            this.settings.setCopies(sr.getCopies());
                                            WorkspaceInformationRecord workspaceInformationRecord = this.workspaceOptions;
                                            if (workspaceInformationRecord != null) {
                                                this.settings.setFitToPages(workspaceInformationRecord.getFitToPages());
                                            }
                                        }
                                        autoFilterInfo = autoFilterInfo7;
                                        msoRecord = msoRecord10;
                                    } else if (type == Type.WSBOOL) {
                                        this.workspaceOptions = new WorkspaceInformationRecord(r);
                                        autoFilterInfo = autoFilterInfo7;
                                        msoRecord = msoRecord10;
                                    } else if (type == Type.DEFCOLWIDTH) {
                                        DefaultColumnWidthRecord dcwr = new DefaultColumnWidthRecord(r);
                                        this.settings.setDefaultColumnWidth(dcwr.getWidth());
                                        autoFilterInfo = autoFilterInfo7;
                                        msoRecord = msoRecord10;
                                    } else if (type == Type.DEFAULTROWHEIGHT) {
                                        DefaultRowHeightRecord drhr = new DefaultRowHeightRecord(r);
                                        if (drhr.getHeight() != 0) {
                                            this.settings.setDefaultRowHeight(drhr.getHeight());
                                        }
                                        autoFilterInfo = autoFilterInfo7;
                                        msoRecord = msoRecord10;
                                    } else if (type == Type.CONDFMT) {
                                        ConditionalFormatRangeRecord cfrr = new ConditionalFormatRangeRecord(r);
                                        ConditionalFormat condFormat5 = new ConditionalFormat(cfrr);
                                        this.conditionalFormats.add(condFormat5);
                                        condFormat3 = condFormat5;
                                        autoFilterInfo6 = autoFilterInfo7;
                                        filterMode2 = filterMode;
                                        printGridLinesRecord3 = printGridLinesRecord;
                                        sharedFormulaAdded3 = sharedFormulaAdded;
                                        objRecord3 = objRecord;
                                        msoRecord8 = msoRecord10;
                                    } else if (type == Type.CF) {
                                        ConditionalFormatRecord cfr = new ConditionalFormatRecord(r);
                                        condFormat.addCondition(cfr);
                                        autoFilterInfo = autoFilterInfo7;
                                        msoRecord = msoRecord10;
                                    } else if (type == Type.FILTERMODE) {
                                        filterMode2 = new FilterModeRecord(r);
                                        autoFilterInfo6 = autoFilterInfo7;
                                        condFormat3 = condFormat;
                                        printGridLinesRecord3 = printGridLinesRecord;
                                        sharedFormulaAdded3 = sharedFormulaAdded;
                                        objRecord3 = objRecord;
                                        msoRecord8 = msoRecord10;
                                    } else if (type == Type.AUTOFILTERINFO) {
                                        autoFilterInfo6 = new AutoFilterInfoRecord(r);
                                        filterMode2 = filterMode;
                                        condFormat3 = condFormat;
                                        printGridLinesRecord3 = printGridLinesRecord;
                                        sharedFormulaAdded3 = sharedFormulaAdded;
                                        objRecord3 = objRecord;
                                        msoRecord8 = msoRecord10;
                                    } else if (type == Type.AUTOFILTER) {
                                        if (this.workbookSettings.getAutoFilterDisabled()) {
                                            autoFilterInfo = autoFilterInfo7;
                                            msoRecord = msoRecord10;
                                        } else {
                                            AutoFilterRecord af = new AutoFilterRecord(r);
                                            if (this.autoFilter == null) {
                                                this.autoFilter = new AutoFilter(filterMode, autoFilterInfo7);
                                                filterMode2 = null;
                                                autoFilterInfo6 = null;
                                            } else {
                                                autoFilterInfo6 = autoFilterInfo7;
                                                filterMode2 = filterMode;
                                            }
                                            this.autoFilter.add(af);
                                            condFormat3 = condFormat;
                                            printGridLinesRecord3 = printGridLinesRecord;
                                            sharedFormulaAdded3 = sharedFormulaAdded;
                                            objRecord3 = objRecord;
                                            msoRecord8 = msoRecord10;
                                        }
                                    } else if (type == Type.LEFTMARGIN) {
                                        MarginRecord m = new LeftMarginRecord(r);
                                        this.settings.setLeftMargin(m.getMargin());
                                        autoFilterInfo = autoFilterInfo7;
                                        msoRecord = msoRecord10;
                                    } else if (type == Type.RIGHTMARGIN) {
                                        MarginRecord m2 = new RightMarginRecord(r);
                                        this.settings.setRightMargin(m2.getMargin());
                                        autoFilterInfo = autoFilterInfo7;
                                        msoRecord = msoRecord10;
                                    } else if (type == Type.TOPMARGIN) {
                                        MarginRecord m3 = new TopMarginRecord(r);
                                        this.settings.setTopMargin(m3.getMargin());
                                        autoFilterInfo = autoFilterInfo7;
                                        msoRecord = msoRecord10;
                                    } else if (type == Type.BOTTOMMARGIN) {
                                        MarginRecord m4 = new BottomMarginRecord(r);
                                        this.settings.setBottomMargin(m4.getMargin());
                                        autoFilterInfo = autoFilterInfo7;
                                        msoRecord = msoRecord10;
                                    } else if (type == Type.HORIZONTALPAGEBREAKS) {
                                        if (this.workbookBof.isBiff8()) {
                                            dr2 = new HorizontalPageBreaksRecord(r);
                                        } else {
                                            dr2 = new HorizontalPageBreaksRecord(r, HorizontalPageBreaksRecord.biff7);
                                        }
                                        this.rowBreaks = dr2.getRowBreaks();
                                        autoFilterInfo = autoFilterInfo7;
                                        msoRecord = msoRecord10;
                                    } else if (type == Type.VERTICALPAGEBREAKS) {
                                        if (this.workbookBof.isBiff8()) {
                                            dr = new VerticalPageBreaksRecord(r);
                                        } else {
                                            dr = new VerticalPageBreaksRecord(r, VerticalPageBreaksRecord.biff7);
                                        }
                                        this.columnBreaks = dr.getColumnBreaks();
                                        autoFilterInfo = autoFilterInfo7;
                                        msoRecord = msoRecord10;
                                    } else if (type == Type.PLS) {
                                        this.plsRecord = new PLSRecord(r);
                                        while (this.excelFile.peek().getType() == Type.CONTINUE) {
                                            r.addContinueRecord(this.excelFile.next());
                                        }
                                        autoFilterInfo = autoFilterInfo7;
                                        msoRecord = msoRecord10;
                                    } else if (type == Type.DVAL) {
                                        if (this.workbookSettings.getCellValidationDisabled()) {
                                            autoFilterInfo = autoFilterInfo7;
                                            msoRecord = msoRecord10;
                                        } else {
                                            DataValidityListRecord dvlr = new DataValidityListRecord(r);
                                            if (dvlr.getObjectId() == -1) {
                                                if (msoRecord10 == null || objRecord != null) {
                                                    msoRecord5 = msoRecord10;
                                                    this.dataValidation = new DataValidation(dvlr);
                                                    objectIds2 = objectIds;
                                                } else {
                                                    if (this.drawingData == null) {
                                                        this.drawingData = new DrawingData();
                                                    }
                                                    Drawing2 d2 = new Drawing2(msoRecord10, this.drawingData, this.workbook.getDrawingGroup());
                                                    this.drawings.add(d2);
                                                    msoRecord6 = null;
                                                    this.dataValidation = new DataValidation(dvlr);
                                                    objectIds2 = objectIds;
                                                    objectIds = objectIds2;
                                                    autoFilterInfo6 = autoFilterInfo7;
                                                    filterMode2 = filterMode;
                                                    condFormat3 = condFormat;
                                                    printGridLinesRecord3 = printGridLinesRecord;
                                                    sharedFormulaAdded3 = sharedFormulaAdded;
                                                    objRecord3 = objRecord;
                                                    msoRecord8 = msoRecord6;
                                                }
                                            } else {
                                                msoRecord5 = msoRecord10;
                                                objectIds2 = objectIds;
                                                if (objectIds2.contains(new Integer(dvlr.getObjectId()))) {
                                                    this.dataValidation = new DataValidation(dvlr);
                                                } else {
                                                    logger.warn("object id " + dvlr.getObjectId() + " referenced  by data validity list record not found - ignoring");
                                                }
                                            }
                                            msoRecord6 = msoRecord5;
                                            objectIds = objectIds2;
                                            autoFilterInfo6 = autoFilterInfo7;
                                            filterMode2 = filterMode;
                                            condFormat3 = condFormat;
                                            printGridLinesRecord3 = printGridLinesRecord;
                                            sharedFormulaAdded3 = sharedFormulaAdded;
                                            objRecord3 = objRecord;
                                            msoRecord8 = msoRecord6;
                                        }
                                    } else if (type == Type.HCENTER) {
                                        CentreRecord hr3 = new CentreRecord(r);
                                        this.settings.setHorizontalCentre(hr3.isCentre());
                                        objectIds = objectIds;
                                        autoFilterInfo = autoFilterInfo7;
                                        msoRecord = msoRecord10;
                                    } else if (type == Type.VCENTER) {
                                        CentreRecord vc = new CentreRecord(r);
                                        this.settings.setVerticalCentre(vc.isCentre());
                                        objectIds = objectIds;
                                        autoFilterInfo = autoFilterInfo7;
                                        msoRecord = msoRecord10;
                                    } else if (type == Type.DV) {
                                        if (!this.workbookSettings.getCellValidationDisabled()) {
                                            WorkbookParser workbookParser2 = this.workbook;
                                            DataValiditySettingsRecord dvsr = new DataValiditySettingsRecord(r, workbookParser2, workbookParser2, workbookParser2.getSettings());
                                            DataValidation dataValidation = this.dataValidation;
                                            if (dataValidation != null) {
                                                dataValidation.add(dvsr);
                                                addCellValidation(dvsr.getFirstColumn(), dvsr.getFirstRow(), dvsr.getLastColumn(), dvsr.getLastRow(), dvsr);
                                            } else {
                                                logger.warn("cannot add data validity settings");
                                            }
                                            objectIds = objectIds;
                                            autoFilterInfo = autoFilterInfo7;
                                            msoRecord = msoRecord10;
                                        } else {
                                            objectIds = objectIds;
                                            autoFilterInfo = autoFilterInfo7;
                                            msoRecord = msoRecord10;
                                        }
                                    } else if (type == Type.OBJ) {
                                        ObjRecord objRecord5 = new ObjRecord(r);
                                        if (this.workbookSettings.getDrawingsDisabled()) {
                                            comments2 = comments;
                                            msoRecord4 = msoRecord10;
                                        } else {
                                            if (msoRecord10 == null && continueRecord != null) {
                                                logger.warn("Cannot find drawing record - using continue record");
                                                msoRecord4 = new MsoDrawingRecord(continueRecord.getRecord());
                                                continueRecord = null;
                                            } else {
                                                msoRecord4 = msoRecord10;
                                            }
                                            comments2 = comments;
                                            handleObjectRecord(objRecord5, msoRecord4, comments2);
                                            objectIds.add(new Integer(objRecord5.getObjectId()));
                                        }
                                        if (objRecord5.getType() != ObjRecord.CHART) {
                                            comments = comments2;
                                            objectIds = objectIds;
                                            autoFilterInfo6 = autoFilterInfo7;
                                            filterMode2 = filterMode;
                                            condFormat3 = condFormat;
                                            printGridLinesRecord3 = printGridLinesRecord;
                                            sharedFormulaAdded3 = sharedFormulaAdded;
                                            objRecord3 = null;
                                            msoRecord8 = null;
                                        } else {
                                            comments = comments2;
                                            objectIds = objectIds;
                                            autoFilterInfo6 = autoFilterInfo7;
                                            filterMode2 = filterMode;
                                            condFormat3 = condFormat;
                                            printGridLinesRecord3 = printGridLinesRecord;
                                            sharedFormulaAdded3 = sharedFormulaAdded;
                                            objRecord3 = objRecord5;
                                            msoRecord8 = msoRecord4;
                                        }
                                    } else if (type == Type.MSODRAWING) {
                                        if (this.workbookSettings.getDrawingsDisabled()) {
                                            comments = comments;
                                            objectIds = objectIds;
                                            autoFilterInfo = autoFilterInfo7;
                                            msoRecord = msoRecord10;
                                        } else {
                                            if (msoRecord10 != null) {
                                                this.drawingData.addRawData(msoRecord10.getData());
                                            }
                                            MsoDrawingRecord msoRecord11 = new MsoDrawingRecord(r);
                                            if (!firstMsoRecord) {
                                                comments = comments;
                                                objectIds = objectIds;
                                                autoFilterInfo6 = autoFilterInfo7;
                                                filterMode2 = filterMode;
                                                condFormat3 = condFormat;
                                                printGridLinesRecord3 = printGridLinesRecord;
                                                sharedFormulaAdded3 = sharedFormulaAdded;
                                                objRecord3 = objRecord;
                                                msoRecord8 = msoRecord11;
                                            } else {
                                                msoRecord11.setFirst();
                                                firstMsoRecord = false;
                                                comments = comments;
                                                objectIds = objectIds;
                                                autoFilterInfo6 = autoFilterInfo7;
                                                filterMode2 = filterMode;
                                                condFormat3 = condFormat;
                                                printGridLinesRecord3 = printGridLinesRecord;
                                                sharedFormulaAdded3 = sharedFormulaAdded;
                                                objRecord3 = objRecord;
                                                msoRecord8 = msoRecord11;
                                            }
                                        }
                                    } else if (type == Type.BUTTONPROPERTYSET) {
                                        this.buttonPropertySet = new ButtonPropertySetRecord(r);
                                        comments = comments;
                                        objectIds = objectIds;
                                        autoFilterInfo = autoFilterInfo7;
                                        msoRecord = msoRecord10;
                                    } else if (type == Type.CALCMODE) {
                                        CalcModeRecord cmr = new CalcModeRecord(r);
                                        this.settings.setAutomaticFormulaCalculation(cmr.isAutomatic());
                                        comments = comments;
                                        objectIds = objectIds;
                                        autoFilterInfo = autoFilterInfo7;
                                        msoRecord = msoRecord10;
                                    } else if (type == Type.SAVERECALC) {
                                        SaveRecalcRecord cmr2 = new SaveRecalcRecord(r);
                                        this.settings.setRecalculateFormulasBeforeSave(cmr2.getRecalculateOnSave());
                                        comments = comments;
                                        objectIds = objectIds;
                                        autoFilterInfo = autoFilterInfo7;
                                        msoRecord = msoRecord10;
                                    } else if (type == Type.GUTS) {
                                        GuttersRecord gr = new GuttersRecord(r);
                                        if (gr.getRowOutlineLevel() > 0) {
                                            i = 1;
                                            i2 = gr.getRowOutlineLevel() - 1;
                                        } else {
                                            i = 1;
                                            i2 = 0;
                                        }
                                        this.maxRowOutlineLevel = i2;
                                        this.maxColumnOutlineLevel = gr.getColumnOutlineLevel() > 0 ? gr.getRowOutlineLevel() - i : 0;
                                        comments = comments;
                                        objectIds = objectIds;
                                        autoFilterInfo = autoFilterInfo7;
                                        msoRecord = msoRecord10;
                                    } else if (type == Type.BOF) {
                                        BOFRecord br2 = new BOFRecord(r);
                                        Assert.verify(!br2.isWorksheet());
                                        int startpos = (this.excelFile.getPos() - r.getLength()) - 4;
                                        Record r22 = this.excelFile.next();
                                        while (r22.getCode() != Type.EOF.value) {
                                            r22 = this.excelFile.next();
                                        }
                                        if (!br2.isChart()) {
                                            comments = comments;
                                            objectIds = objectIds;
                                            autoFilterInfo3 = autoFilterInfo7;
                                            msoRecord3 = msoRecord10;
                                            objRecord3 = objRecord;
                                        } else {
                                            if (!this.workbook.getWorkbookBof().isBiff8()) {
                                                logger.warn("only biff8 charts are supported");
                                                comments = comments;
                                                objectIds = objectIds;
                                                autoFilterInfo3 = autoFilterInfo7;
                                            } else {
                                                if (this.drawingData == null) {
                                                    this.drawingData = new DrawingData();
                                                }
                                                if (!this.workbookSettings.getDrawingsDisabled()) {
                                                    comments = comments;
                                                    objectIds = objectIds;
                                                    autoFilterInfo3 = autoFilterInfo7;
                                                    Chart chart = new Chart(msoRecord10, objRecord, this.drawingData, startpos, this.excelFile.getPos(), this.excelFile, this.workbookSettings);
                                                    this.charts.add(chart);
                                                    if (this.workbook.getDrawingGroup() != null) {
                                                        this.workbook.getDrawingGroup().add(chart);
                                                    }
                                                } else {
                                                    comments = comments;
                                                    objectIds = objectIds;
                                                    autoFilterInfo3 = autoFilterInfo7;
                                                }
                                            }
                                            msoRecord3 = null;
                                            objRecord3 = null;
                                        }
                                        if (!this.sheetBof.isChart()) {
                                            printGridLinesRecord2 = printGridLinesRecord;
                                        } else {
                                            printGridLinesRecord2 = null;
                                        }
                                        filterMode2 = filterMode;
                                        condFormat3 = condFormat;
                                        sharedFormulaAdded3 = sharedFormulaAdded;
                                        autoFilterInfo6 = autoFilterInfo3;
                                        msoRecord8 = msoRecord3;
                                        printGridLinesRecord3 = printGridLinesRecord2;
                                    } else {
                                        comments = comments;
                                        objectIds = objectIds;
                                        autoFilterInfo = autoFilterInfo7;
                                        msoRecord = msoRecord10;
                                        if (type == Type.EOF) {
                                            filterMode2 = filterMode;
                                            condFormat3 = condFormat;
                                            objRecord3 = objRecord;
                                            autoFilterInfo6 = autoFilterInfo;
                                            printGridLinesRecord3 = null;
                                            msoRecord8 = msoRecord;
                                            sharedFormulaAdded3 = sharedFormulaAdded;
                                        }
                                    }
                                    File file = this.excelFile;
                                    FormattingRecords formattingRecords = this.formattingRecords;
                                    WorkbookParser workbookParser3 = this.workbook;
                                    FormulaRecord fr2 = new FormulaRecord(r, file, formattingRecords, workbookParser3, workbookParser3, this.sheet, this.workbookSettings);
                                    if (fr2.isShared()) {
                                        BaseSharedFormulaRecord prevSharedFormula = sharedFormula;
                                        BaseSharedFormulaRecord sharedFormula2 = (BaseSharedFormulaRecord) fr2.getFormula();
                                        sharedFormulaAdded2 = addToSharedFormulas(sharedFormula2);
                                        if (!sharedFormulaAdded2) {
                                            sharedFormula = sharedFormula2;
                                        } else {
                                            sharedFormula = prevSharedFormula;
                                        }
                                        if (!sharedFormulaAdded2 && prevSharedFormula != null) {
                                            addCell(revertSharedFormula(prevSharedFormula));
                                        }
                                    } else {
                                        Cell cell = fr2.getFormula();
                                        try {
                                            if (fr2.getFormula().getType() == CellType.NUMBER_FORMULA) {
                                                NumberFormulaRecord nfr = (NumberFormulaRecord) fr2.getFormula();
                                                if (this.formattingRecords.isDate(nfr.getXFIndex())) {
                                                    FormattingRecords formattingRecords2 = this.formattingRecords;
                                                    WorkbookParser workbookParser4 = this.workbook;
                                                    cell = new DateFormulaRecord(nfr, formattingRecords2, workbookParser4, workbookParser4, this.nineteenFour, this.sheet);
                                                }
                                            }
                                            addCell(cell);
                                        } catch (FormulaException e) {
                                            logger.warn(CellReferenceHelper.getCellReference(cell.getColumn(), cell.getRow()) + " " + e.getMessage());
                                        }
                                        sharedFormulaAdded2 = sharedFormulaAdded;
                                    }
                                    filterMode2 = filterMode;
                                    condFormat3 = condFormat;
                                    printGridLinesRecord3 = printGridLinesRecord;
                                    objRecord3 = objRecord;
                                    autoFilterInfo6 = autoFilterInfo2;
                                    msoRecord8 = msoRecord2;
                                    sharedFormulaAdded3 = sharedFormulaAdded2;
                                }
                            }
                            comments3 = comments;
                            objectIds3 = objectIds;
                        }
                    }
                }
                RKRecord rkr = new RKRecord(r, this.formattingRecords, this.sheet);
                if (this.formattingRecords.isDate(rkr.getXFIndex())) {
                    Cell dc3 = new DateRecord(rkr, rkr.getXFIndex(), this.formattingRecords, this.nineteenFour, this.sheet);
                    addCell(dc3);
                } else {
                    addCell(rkr);
                }
            }
            filterMode2 = filterMode;
            condFormat3 = condFormat;
            printGridLinesRecord3 = printGridLinesRecord;
            objRecord3 = objRecord;
            autoFilterInfo6 = autoFilterInfo;
            msoRecord8 = msoRecord;
            sharedFormulaAdded3 = sharedFormulaAdded;
            comments3 = comments;
            objectIds3 = objectIds;
        }
        ObjRecord objRecord6 = objRecord3;
        boolean sharedFormulaAdded4 = sharedFormulaAdded3;
        MsoDrawingRecord msoRecord12 = msoRecord8;
        HashMap comments4 = comments3;
        this.excelFile.restorePos();
        if (this.outOfBoundsCells.size() > 0) {
            handleOutOfBoundsCells();
        }
        Iterator i5 = this.sharedFormulas.iterator();
        while (i5.hasNext()) {
            SharedFormulaRecord sfr2 = (SharedFormulaRecord) i5.next();
            Cell[] sfnr = sfr2.getFormulas(this.formattingRecords, this.nineteenFour);
            for (Cell cell2 : sfnr) {
                addCell(cell2);
            }
        }
        if (!sharedFormulaAdded4 && sharedFormula != null) {
            addCell(revertSharedFormula(sharedFormula));
        }
        if (msoRecord12 != null && this.workbook.getDrawingGroup() != null) {
            this.workbook.getDrawingGroup().setDrawingsOmitted(msoRecord12, objRecord6);
        }
        if (!comments4.isEmpty()) {
            logger.warn("Not all comments have a corresponding Note record");
        }
    }

    private boolean addToSharedFormulas(BaseSharedFormulaRecord fr) {
        boolean added = false;
        int size = this.sharedFormulas.size();
        for (int i = 0; i < size && !added; i++) {
            SharedFormulaRecord sfr = (SharedFormulaRecord) this.sharedFormulas.get(i);
            added = sfr.add(fr);
        }
        return added;
    }

    private Cell revertSharedFormula(BaseSharedFormulaRecord f) {
        int pos = this.excelFile.getPos();
        this.excelFile.setPos(f.getFilePos());
        Record record = f.getRecord();
        File file = this.excelFile;
        FormattingRecords formattingRecords = this.formattingRecords;
        WorkbookParser workbookParser = this.workbook;
        FormulaRecord fr = new FormulaRecord(record, file, formattingRecords, workbookParser, workbookParser, FormulaRecord.ignoreSharedFormula, this.sheet, this.workbookSettings);
        try {
            Cell cell = fr.getFormula();
            if (fr.getFormula().getType() == CellType.NUMBER_FORMULA) {
                NumberFormulaRecord nfr = (NumberFormulaRecord) fr.getFormula();
                if (this.formattingRecords.isDate(fr.getXFIndex())) {
                    FormattingRecords formattingRecords2 = this.formattingRecords;
                    WorkbookParser workbookParser2 = this.workbook;
                    cell = new DateFormulaRecord(nfr, formattingRecords2, workbookParser2, workbookParser2, this.nineteenFour, this.sheet);
                }
            }
            this.excelFile.setPos(pos);
            return cell;
        } catch (FormulaException e) {
            logger.warn(CellReferenceHelper.getCellReference(fr.getColumn(), fr.getRow()) + " " + e.getMessage());
            return null;
        }
    }

    final int getNumRows() {
        return this.numRows;
    }

    final int getNumCols() {
        return this.numCols;
    }

    final Cell[][] getCells() {
        return this.cells;
    }

    final ArrayList getRowProperties() {
        return this.rowProperties;
    }

    final ArrayList getColumnInfosArray() {
        return this.columnInfosArray;
    }

    final ArrayList getHyperlinks() {
        return this.hyperlinks;
    }

    final ArrayList getConditionalFormats() {
        return this.conditionalFormats;
    }

    final AutoFilter getAutoFilter() {
        return this.autoFilter;
    }

    final ArrayList getCharts() {
        return this.charts;
    }

    final ArrayList getDrawings() {
        return this.drawings;
    }

    final DataValidation getDataValidation() {
        return this.dataValidation;
    }

    final Range[] getMergedCells() {
        return this.mergedCells;
    }

    final SheetSettings getSettings() {
        return this.settings;
    }

    final int[] getRowBreaks() {
        return this.rowBreaks;
    }

    final int[] getColumnBreaks() {
        return this.columnBreaks;
    }

    final WorkspaceInformationRecord getWorkspaceOptions() {
        return this.workspaceOptions;
    }

    final PLSRecord getPLS() {
        return this.plsRecord;
    }

    final ButtonPropertySetRecord getButtonPropertySet() {
        return this.buttonPropertySet;
    }

    private void addCellComment(int col, int row, String text, double width, double height) {
        Cell c = this.cells[row][col];
        if (c == null) {
            logger.warn("Cell at " + CellReferenceHelper.getCellReference(col, row) + " not present - adding a blank");
            MulBlankCell mbc = new MulBlankCell(row, col, 0, this.formattingRecords, this.sheet);
            CellFeatures cf = new CellFeatures();
            cf.setReadComment(text, width, height);
            mbc.setCellFeatures(cf);
            addCell(mbc);
            return;
        }
        if (c instanceof CellFeaturesAccessor) {
            CellFeaturesAccessor cv = (CellFeaturesAccessor) c;
            CellFeatures cf2 = cv.getCellFeatures();
            if (cf2 == null) {
                cf2 = new CellFeatures();
                cv.setCellFeatures(cf2);
            }
            cf2.setReadComment(text, width, height);
            return;
        }
        logger.warn("Not able to add comment to cell type " + c.getClass().getName() + " at " + CellReferenceHelper.getCellReference(col, row));
    }

    private void addCellValidation(int col1, int row1, int col2, int row2, DataValiditySettingsRecord dvsr) {
        Cell c;
        for (int row = row1; row <= row2; row++) {
            for (int col = col1; col <= col2; col++) {
                Cell[][] cellArr = this.cells;
                if (cellArr.length > row && cellArr[row].length > col) {
                    Cell c2 = cellArr[row][col];
                    c = c2;
                } else {
                    c = null;
                }
                if (c == null) {
                    MulBlankCell mbc = new MulBlankCell(row, col, 0, this.formattingRecords, this.sheet);
                    CellFeatures cf = new CellFeatures();
                    cf.setValidationSettings(dvsr);
                    mbc.setCellFeatures(cf);
                    addCell(mbc);
                } else if (c instanceof CellFeaturesAccessor) {
                    CellFeaturesAccessor cv = (CellFeaturesAccessor) c;
                    CellFeatures cf2 = cv.getCellFeatures();
                    if (cf2 == null) {
                        cf2 = new CellFeatures();
                        cv.setCellFeatures(cf2);
                    }
                    cf2.setValidationSettings(dvsr);
                } else {
                    logger.warn("Not able to add comment to cell type " + c.getClass().getName() + " at " + CellReferenceHelper.getCellReference(col, row));
                }
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:101:0x0222  */
    /* JADX WARN: Removed duplicated region for block: B:122:0x02f4  */
    /* JADX WARN: Removed duplicated region for block: B:125:0x0300 A[Catch: DrawingDataException -> 0x035d, TryCatch #0 {DrawingDataException -> 0x035d, blocks: (B:7:0x000b, B:9:0x0013, B:11:0x0017, B:12:0x001e, B:14:0x0036, B:16:0x003f, B:18:0x0043, B:19:0x004a, B:21:0x006a, B:23:0x0081, B:26:0x008c, B:29:0x00a7, B:31:0x00c1, B:32:0x00c9, B:35:0x0072, B:36:0x00d6, B:38:0x00de, B:40:0x00e2, B:41:0x00e9, B:43:0x0101, B:45:0x0109, B:47:0x010d, B:48:0x0114, B:50:0x0134, B:54:0x0140, B:56:0x014b, B:58:0x0162, B:61:0x016d, B:64:0x017f, B:67:0x018f, B:69:0x01a9, B:70:0x01b1, B:73:0x0153, B:75:0x01b7, B:77:0x01bf, B:79:0x01c3, B:80:0x01ca, B:82:0x01ea, B:86:0x01f6, B:88:0x0201, B:90:0x0218, B:93:0x0223, B:96:0x023e, B:98:0x0258, B:99:0x0260, B:102:0x0209, B:104:0x0266, B:107:0x0272, B:109:0x029a, B:110:0x02a1, B:112:0x02b8, B:116:0x02c4, B:118:0x02cf, B:120:0x02ec, B:123:0x02f5, B:125:0x0300, B:128:0x02d7, B:130:0x030a, B:132:0x0312, B:134:0x033a, B:135:0x0341, B:137:0x0352), top: B:6:0x000b }] */
    /* JADX WARN: Removed duplicated region for block: B:127:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:60:0x016a  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x017e A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:64:0x017f A[Catch: DrawingDataException -> 0x035d, TryCatch #0 {DrawingDataException -> 0x035d, blocks: (B:7:0x000b, B:9:0x0013, B:11:0x0017, B:12:0x001e, B:14:0x0036, B:16:0x003f, B:18:0x0043, B:19:0x004a, B:21:0x006a, B:23:0x0081, B:26:0x008c, B:29:0x00a7, B:31:0x00c1, B:32:0x00c9, B:35:0x0072, B:36:0x00d6, B:38:0x00de, B:40:0x00e2, B:41:0x00e9, B:43:0x0101, B:45:0x0109, B:47:0x010d, B:48:0x0114, B:50:0x0134, B:54:0x0140, B:56:0x014b, B:58:0x0162, B:61:0x016d, B:64:0x017f, B:67:0x018f, B:69:0x01a9, B:70:0x01b1, B:73:0x0153, B:75:0x01b7, B:77:0x01bf, B:79:0x01c3, B:80:0x01ca, B:82:0x01ea, B:86:0x01f6, B:88:0x0201, B:90:0x0218, B:93:0x0223, B:96:0x023e, B:98:0x0258, B:99:0x0260, B:102:0x0209, B:104:0x0266, B:107:0x0272, B:109:0x029a, B:110:0x02a1, B:112:0x02b8, B:116:0x02c4, B:118:0x02cf, B:120:0x02ec, B:123:0x02f5, B:125:0x0300, B:128:0x02d7, B:130:0x030a, B:132:0x0312, B:134:0x033a, B:135:0x0341, B:137:0x0352), top: B:6:0x000b }] */
    /* JADX WARN: Removed duplicated region for block: B:72:0x016c  */
    /* JADX WARN: Removed duplicated region for block: B:92:0x0220  */
    /* JADX WARN: Removed duplicated region for block: B:95:0x023d  */
    /* JADX WARN: Removed duplicated region for block: B:98:0x0258 A[Catch: DrawingDataException -> 0x035d, TryCatch #0 {DrawingDataException -> 0x035d, blocks: (B:7:0x000b, B:9:0x0013, B:11:0x0017, B:12:0x001e, B:14:0x0036, B:16:0x003f, B:18:0x0043, B:19:0x004a, B:21:0x006a, B:23:0x0081, B:26:0x008c, B:29:0x00a7, B:31:0x00c1, B:32:0x00c9, B:35:0x0072, B:36:0x00d6, B:38:0x00de, B:40:0x00e2, B:41:0x00e9, B:43:0x0101, B:45:0x0109, B:47:0x010d, B:48:0x0114, B:50:0x0134, B:54:0x0140, B:56:0x014b, B:58:0x0162, B:61:0x016d, B:64:0x017f, B:67:0x018f, B:69:0x01a9, B:70:0x01b1, B:73:0x0153, B:75:0x01b7, B:77:0x01bf, B:79:0x01c3, B:80:0x01ca, B:82:0x01ea, B:86:0x01f6, B:88:0x0201, B:90:0x0218, B:93:0x0223, B:96:0x023e, B:98:0x0258, B:99:0x0260, B:102:0x0209, B:104:0x0266, B:107:0x0272, B:109:0x029a, B:110:0x02a1, B:112:0x02b8, B:116:0x02c4, B:118:0x02cf, B:120:0x02ec, B:123:0x02f5, B:125:0x0300, B:128:0x02d7, B:130:0x030a, B:132:0x0312, B:134:0x033a, B:135:0x0341, B:137:0x0352), top: B:6:0x000b }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void handleObjectRecord(jxl.biff.drawing.ObjRecord r11, jxl.biff.drawing.MsoDrawingRecord r12, java.util.HashMap r13) {
        /*
            Method dump skipped, instructions count: 894
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: jxl.read.biff.SheetReader.handleObjectRecord(jxl.biff.drawing.ObjRecord, jxl.biff.drawing.MsoDrawingRecord, java.util.HashMap):void");
    }

    DrawingData getDrawingData() {
        return this.drawingData;
    }

    private void handleOutOfBoundsCells() {
        int resizedRows = this.numRows;
        int resizedCols = this.numCols;
        Iterator i = this.outOfBoundsCells.iterator();
        while (i.hasNext()) {
            Cell cell = (Cell) i.next();
            resizedRows = Math.max(resizedRows, cell.getRow() + 1);
            resizedCols = Math.max(resizedCols, cell.getColumn() + 1);
        }
        if (resizedCols > this.numCols) {
            for (int r = 0; r < this.numRows; r++) {
                Cell[] newRow = new Cell[resizedCols];
                Cell[] oldRow = this.cells[r];
                System.arraycopy(oldRow, 0, newRow, 0, oldRow.length);
                this.cells[r] = newRow;
            }
        }
        int r2 = this.numRows;
        if (resizedRows > r2) {
            Cell[][] newCells = new Cell[resizedRows][];
            Cell[][] cellArr = this.cells;
            System.arraycopy(cellArr, 0, newCells, 0, cellArr.length);
            this.cells = newCells;
            for (int i2 = this.numRows; i2 < resizedRows; i2++) {
                newCells[i2] = new Cell[resizedCols];
            }
        }
        this.numRows = resizedRows;
        this.numCols = resizedCols;
        Iterator i3 = this.outOfBoundsCells.iterator();
        while (i3.hasNext()) {
            addCell((Cell) i3.next());
        }
        this.outOfBoundsCells.clear();
    }

    public int getMaxColumnOutlineLevel() {
        return this.maxColumnOutlineLevel;
    }

    public int getMaxRowOutlineLevel() {
        return this.maxRowOutlineLevel;
    }
}
