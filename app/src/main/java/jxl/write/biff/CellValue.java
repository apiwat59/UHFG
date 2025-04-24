package jxl.write.biff;

import jxl.Cell;
import jxl.CellFeatures;
import jxl.CellReferenceHelper;
import jxl.Sheet;
import jxl.biff.DVParser;
import jxl.biff.FormattingRecords;
import jxl.biff.IntegerHelper;
import jxl.biff.NumFormatRecordsException;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;
import jxl.biff.XFRecord;
import jxl.biff.drawing.ComboBox;
import jxl.biff.drawing.Comment;
import jxl.biff.formula.FormulaException;
import jxl.common.Assert;
import jxl.common.Logger;
import jxl.format.CellFormat;
import jxl.write.WritableCell;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableWorkbook;

/* loaded from: classes.dex */
public abstract class CellValue extends WritableRecordData implements WritableCell {
    private static Logger logger = Logger.getLogger(CellValue.class);
    private int column;
    private boolean copied;
    private WritableCellFeatures features;
    private XFRecord format;
    private FormattingRecords formattingRecords;
    private boolean referenced;
    private int row;
    private WritableSheetImpl sheet;

    protected CellValue(Type t, int c, int r) {
        this(t, c, r, WritableWorkbook.NORMAL_STYLE);
        this.copied = false;
    }

    protected CellValue(Type t, Cell c) {
        this(t, c.getColumn(), c.getRow());
        this.copied = true;
        this.format = (XFRecord) c.getCellFormat();
        if (c.getCellFeatures() != null) {
            WritableCellFeatures writableCellFeatures = new WritableCellFeatures(c.getCellFeatures());
            this.features = writableCellFeatures;
            writableCellFeatures.setWritableCell(this);
        }
    }

    protected CellValue(Type t, int c, int r, CellFormat st) {
        super(t);
        this.row = r;
        this.column = c;
        this.format = (XFRecord) st;
        this.referenced = false;
        this.copied = false;
    }

    protected CellValue(Type t, int c, int r, CellValue cv) {
        super(t);
        this.row = r;
        this.column = c;
        this.format = cv.format;
        this.referenced = false;
        this.copied = false;
        if (cv.features != null) {
            WritableCellFeatures writableCellFeatures = new WritableCellFeatures(cv.features);
            this.features = writableCellFeatures;
            writableCellFeatures.setWritableCell(this);
        }
    }

    @Override // jxl.write.WritableCell
    public void setCellFormat(CellFormat cf) {
        this.format = (XFRecord) cf;
        if (!this.referenced) {
            return;
        }
        Assert.verify(this.formattingRecords != null);
        addCellFormat();
    }

    @Override // jxl.Cell
    public int getRow() {
        return this.row;
    }

    @Override // jxl.Cell
    public int getColumn() {
        return this.column;
    }

    @Override // jxl.Cell
    public boolean isHidden() {
        ColumnInfoRecord cir = this.sheet.getColumnInfo(this.column);
        if (cir != null && cir.getWidth() == 0) {
            return true;
        }
        RowRecord rr = this.sheet.getRowInfo(this.row);
        if (rr == null) {
            return false;
        }
        if (rr.getRowHeight() == 0 || rr.isCollapsed()) {
            return true;
        }
        return false;
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        byte[] mydata = new byte[6];
        IntegerHelper.getTwoBytes(this.row, mydata, 0);
        IntegerHelper.getTwoBytes(this.column, mydata, 2);
        IntegerHelper.getTwoBytes(this.format.getXFIndex(), mydata, 4);
        return mydata;
    }

    void setCellDetails(FormattingRecords fr, SharedStrings ss, WritableSheetImpl s) {
        this.referenced = true;
        this.sheet = s;
        this.formattingRecords = fr;
        addCellFormat();
        addCellFeatures();
    }

    final boolean isReferenced() {
        return this.referenced;
    }

    final int getXFIndex() {
        return this.format.getXFIndex();
    }

    @Override // jxl.Cell
    public CellFormat getCellFormat() {
        return this.format;
    }

    void incrementRow() {
        Comment c;
        this.row++;
        WritableCellFeatures writableCellFeatures = this.features;
        if (writableCellFeatures != null && (c = writableCellFeatures.getCommentDrawing()) != null) {
            c.setX(this.column);
            c.setY(this.row);
        }
    }

    void decrementRow() {
        this.row--;
        WritableCellFeatures writableCellFeatures = this.features;
        if (writableCellFeatures != null) {
            Comment c = writableCellFeatures.getCommentDrawing();
            if (c != null) {
                c.setX(this.column);
                c.setY(this.row);
            }
            if (this.features.hasDropDown()) {
                logger.warn("need to change value for drop down drawing");
            }
        }
    }

    void incrementColumn() {
        Comment c;
        this.column++;
        WritableCellFeatures writableCellFeatures = this.features;
        if (writableCellFeatures != null && (c = writableCellFeatures.getCommentDrawing()) != null) {
            c.setX(this.column);
            c.setY(this.row);
        }
    }

    void decrementColumn() {
        Comment c;
        this.column--;
        WritableCellFeatures writableCellFeatures = this.features;
        if (writableCellFeatures != null && (c = writableCellFeatures.getCommentDrawing()) != null) {
            c.setX(this.column);
            c.setY(this.row);
        }
    }

    void columnInserted(Sheet s, int sheetIndex, int col) {
    }

    void columnRemoved(Sheet s, int sheetIndex, int col) {
    }

    void rowInserted(Sheet s, int sheetIndex, int row) {
    }

    void rowRemoved(Sheet s, int sheetIndex, int row) {
    }

    public WritableSheetImpl getSheet() {
        return this.sheet;
    }

    private void addCellFormat() {
        Styles styles = this.sheet.getWorkbook().getStyles();
        XFRecord format = styles.getFormat(this.format);
        this.format = format;
        try {
            if (!format.isInitialized()) {
                this.formattingRecords.addStyle(this.format);
            }
        } catch (NumFormatRecordsException e) {
            logger.warn("Maximum number of format records exceeded.  Using default format.");
            this.format = styles.getNormalStyle();
        }
    }

    @Override // jxl.Cell
    public CellFeatures getCellFeatures() {
        return this.features;
    }

    @Override // jxl.write.WritableCell
    public WritableCellFeatures getWritableCellFeatures() {
        return this.features;
    }

    @Override // jxl.write.WritableCell
    public void setCellFeatures(WritableCellFeatures cf) {
        if (this.features != null) {
            logger.warn("current cell features for " + CellReferenceHelper.getCellReference(this) + " not null - overwriting");
            if (this.features.hasDataValidation() && this.features.getDVParser() != null && this.features.getDVParser().extendedCellsValidation()) {
                DVParser dvp = this.features.getDVParser();
                logger.warn("Cannot add cell features to " + CellReferenceHelper.getCellReference(this) + " because it is part of the shared cell validation group " + CellReferenceHelper.getCellReference(dvp.getFirstColumn(), dvp.getFirstRow()) + "-" + CellReferenceHelper.getCellReference(dvp.getLastColumn(), dvp.getLastRow()));
                return;
            }
        }
        this.features = cf;
        cf.setWritableCell(this);
        if (this.referenced) {
            addCellFeatures();
        }
    }

    public final void addCellFeatures() {
        WritableCellFeatures writableCellFeatures = this.features;
        if (writableCellFeatures == null) {
            return;
        }
        if (this.copied) {
            this.copied = false;
            return;
        }
        if (writableCellFeatures.getComment() != null) {
            Comment comment = new Comment(this.features.getComment(), this.column, this.row);
            comment.setWidth(this.features.getCommentWidth());
            comment.setHeight(this.features.getCommentHeight());
            this.sheet.addDrawing(comment);
            this.sheet.getWorkbook().addDrawing(comment);
            this.features.setCommentDrawing(comment);
        }
        if (this.features.hasDataValidation()) {
            try {
                this.features.getDVParser().setCell(this.column, this.row, this.sheet.getWorkbook(), this.sheet.getWorkbook(), this.sheet.getWorkbookSettings());
            } catch (FormulaException e) {
                Assert.verify(false);
            }
            this.sheet.addValidationCell(this);
            if (!this.features.hasDropDown()) {
                return;
            }
            if (this.sheet.getComboBox() == null) {
                ComboBox cb = new ComboBox();
                this.sheet.addDrawing(cb);
                this.sheet.getWorkbook().addDrawing(cb);
                this.sheet.setComboBox(cb);
            }
            this.features.setComboBox(this.sheet.getComboBox());
        }
    }

    public final void removeCellFeatures() {
        this.features = null;
    }

    public final void removeComment(Comment c) {
        this.sheet.removeDrawing(c);
    }

    public final void removeDataValidation() {
        this.sheet.removeDataValidation(this);
    }

    final void setCopied(boolean c) {
        this.copied = c;
    }
}
