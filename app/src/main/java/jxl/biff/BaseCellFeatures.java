package jxl.biff;

import java.util.Collection;
import jxl.Range;
import jxl.biff.DVParser;
import jxl.biff.drawing.ComboBox;
import jxl.biff.drawing.Comment;
import jxl.common.Assert;
import jxl.common.Logger;
import jxl.write.biff.CellValue;

/* loaded from: classes.dex */
public class BaseCellFeatures {
    private static final double defaultCommentHeight = 4.0d;
    private static final double defaultCommentWidth = 3.0d;
    private ComboBox comboBox;
    private String comment;
    private Comment commentDrawing;
    private double commentHeight;
    private double commentWidth;
    private boolean dataValidation;
    private boolean dropDown;
    private DVParser dvParser;
    private DataValiditySettingsRecord validationSettings;
    private CellValue writableCell;
    public static Logger logger = Logger.getLogger(BaseCellFeatures.class);
    public static final ValidationCondition BETWEEN = new ValidationCondition(DVParser.BETWEEN);
    public static final ValidationCondition NOT_BETWEEN = new ValidationCondition(DVParser.NOT_BETWEEN);
    public static final ValidationCondition EQUAL = new ValidationCondition(DVParser.EQUAL);
    public static final ValidationCondition NOT_EQUAL = new ValidationCondition(DVParser.NOT_EQUAL);
    public static final ValidationCondition GREATER_THAN = new ValidationCondition(DVParser.GREATER_THAN);
    public static final ValidationCondition LESS_THAN = new ValidationCondition(DVParser.LESS_THAN);
    public static final ValidationCondition GREATER_EQUAL = new ValidationCondition(DVParser.GREATER_EQUAL);
    public static final ValidationCondition LESS_EQUAL = new ValidationCondition(DVParser.LESS_EQUAL);

    /* JADX INFO: Access modifiers changed from: protected */
    public static class ValidationCondition {
        private static ValidationCondition[] types = new ValidationCondition[0];
        private DVParser.Condition condition;

        ValidationCondition(DVParser.Condition c) {
            this.condition = c;
            ValidationCondition[] oldtypes = types;
            ValidationCondition[] validationConditionArr = new ValidationCondition[oldtypes.length + 1];
            types = validationConditionArr;
            System.arraycopy(oldtypes, 0, validationConditionArr, 0, oldtypes.length);
            types[oldtypes.length] = this;
        }

        public DVParser.Condition getCondition() {
            return this.condition;
        }
    }

    protected BaseCellFeatures() {
    }

    public BaseCellFeatures(BaseCellFeatures cf) {
        this.comment = cf.comment;
        this.commentWidth = cf.commentWidth;
        this.commentHeight = cf.commentHeight;
        this.dropDown = cf.dropDown;
        this.dataValidation = cf.dataValidation;
        this.validationSettings = cf.validationSettings;
        if (cf.dvParser != null) {
            this.dvParser = new DVParser(cf.dvParser);
        }
    }

    protected String getComment() {
        return this.comment;
    }

    public double getCommentWidth() {
        return this.commentWidth;
    }

    public double getCommentHeight() {
        return this.commentHeight;
    }

    public final void setWritableCell(CellValue wc) {
        this.writableCell = wc;
    }

    public void setReadComment(String s, double w, double h) {
        this.comment = s;
        this.commentWidth = w;
        this.commentHeight = h;
    }

    public void setValidationSettings(DataValiditySettingsRecord dvsr) {
        Assert.verify(dvsr != null);
        this.validationSettings = dvsr;
        this.dataValidation = true;
    }

    public void setComment(String s) {
        setComment(s, defaultCommentWidth, defaultCommentHeight);
    }

    public void setComment(String s, double width, double height) {
        this.comment = s;
        this.commentWidth = width;
        this.commentHeight = height;
        Comment comment = this.commentDrawing;
        if (comment != null) {
            comment.setCommentText(s);
            this.commentDrawing.setWidth(width);
            this.commentDrawing.setWidth(height);
        }
    }

    public void removeComment() {
        this.comment = null;
        Comment comment = this.commentDrawing;
        if (comment != null) {
            this.writableCell.removeComment(comment);
            this.commentDrawing = null;
        }
    }

    public void removeDataValidation() {
        if (!this.dataValidation) {
            return;
        }
        DVParser dvp = getDVParser();
        if (dvp.extendedCellsValidation()) {
            logger.warn("Cannot remove data validation from " + jxl.CellReferenceHelper.getCellReference(this.writableCell) + " as it is part of the shared reference " + jxl.CellReferenceHelper.getCellReference(dvp.getFirstColumn(), dvp.getFirstRow()) + "-" + jxl.CellReferenceHelper.getCellReference(dvp.getLastColumn(), dvp.getLastRow()));
            return;
        }
        this.writableCell.removeDataValidation();
        clearValidationSettings();
    }

    public void removeSharedDataValidation() {
        if (!this.dataValidation) {
            return;
        }
        this.writableCell.removeDataValidation();
        clearValidationSettings();
    }

    public final void setCommentDrawing(Comment c) {
        this.commentDrawing = c;
    }

    public final Comment getCommentDrawing() {
        return this.commentDrawing;
    }

    public String getDataValidationList() {
        DataValiditySettingsRecord dataValiditySettingsRecord = this.validationSettings;
        if (dataValiditySettingsRecord == null) {
            return null;
        }
        return dataValiditySettingsRecord.getValidationFormula();
    }

    public void setDataValidationList(Collection c) {
        if (this.dataValidation && getDVParser().extendedCellsValidation()) {
            logger.warn("Cannot set data validation on " + jxl.CellReferenceHelper.getCellReference(this.writableCell) + " as it is part of a shared data validation");
            return;
        }
        clearValidationSettings();
        this.dvParser = new DVParser(c);
        this.dropDown = true;
        this.dataValidation = true;
    }

    public void setDataValidationRange(int col1, int r1, int col2, int r2) {
        if (this.dataValidation && getDVParser().extendedCellsValidation()) {
            logger.warn("Cannot set data validation on " + jxl.CellReferenceHelper.getCellReference(this.writableCell) + " as it is part of a shared data validation");
            return;
        }
        clearValidationSettings();
        this.dvParser = new DVParser(col1, r1, col2, r2);
        this.dropDown = true;
        this.dataValidation = true;
    }

    public void setDataValidationRange(String namedRange) {
        if (this.dataValidation && getDVParser().extendedCellsValidation()) {
            logger.warn("Cannot set data validation on " + jxl.CellReferenceHelper.getCellReference(this.writableCell) + " as it is part of a shared data validation");
            return;
        }
        clearValidationSettings();
        this.dvParser = new DVParser(namedRange);
        this.dropDown = true;
        this.dataValidation = true;
    }

    public void setNumberValidation(double val, ValidationCondition c) {
        if (this.dataValidation && getDVParser().extendedCellsValidation()) {
            logger.warn("Cannot set data validation on " + jxl.CellReferenceHelper.getCellReference(this.writableCell) + " as it is part of a shared data validation");
            return;
        }
        clearValidationSettings();
        this.dvParser = new DVParser(val, Double.NaN, c.getCondition());
        this.dropDown = false;
        this.dataValidation = true;
    }

    public void setNumberValidation(double val1, double val2, ValidationCondition c) {
        if (this.dataValidation && getDVParser().extendedCellsValidation()) {
            logger.warn("Cannot set data validation on " + jxl.CellReferenceHelper.getCellReference(this.writableCell) + " as it is part of a shared data validation");
            return;
        }
        clearValidationSettings();
        this.dvParser = new DVParser(val1, val2, c.getCondition());
        this.dropDown = false;
        this.dataValidation = true;
    }

    public boolean hasDataValidation() {
        return this.dataValidation;
    }

    private void clearValidationSettings() {
        this.validationSettings = null;
        this.dvParser = null;
        this.dropDown = false;
        this.comboBox = null;
        this.dataValidation = false;
    }

    public boolean hasDropDown() {
        return this.dropDown;
    }

    public void setComboBox(ComboBox cb) {
        this.comboBox = cb;
    }

    public DVParser getDVParser() {
        DVParser dVParser = this.dvParser;
        if (dVParser != null) {
            return dVParser;
        }
        if (this.validationSettings != null) {
            DVParser dVParser2 = new DVParser(this.validationSettings.getDVParser());
            this.dvParser = dVParser2;
            return dVParser2;
        }
        return null;
    }

    public void shareDataValidation(BaseCellFeatures source) {
        if (this.dataValidation) {
            logger.warn("Attempting to share a data validation on cell " + jxl.CellReferenceHelper.getCellReference(this.writableCell) + " which already has a data validation");
            return;
        }
        clearValidationSettings();
        this.dvParser = source.getDVParser();
        this.validationSettings = null;
        this.dataValidation = true;
        this.dropDown = source.dropDown;
        this.comboBox = source.comboBox;
    }

    public Range getSharedDataValidationRange() {
        if (!this.dataValidation) {
            return null;
        }
        DVParser dvp = getDVParser();
        return new SheetRangeImpl(this.writableCell.getSheet(), dvp.getFirstColumn(), dvp.getFirstRow(), dvp.getLastColumn(), dvp.getLastRow());
    }
}
