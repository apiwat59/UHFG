package jxl.write;

import java.util.Collection;
import jxl.CellFeatures;
import jxl.biff.BaseCellFeatures;

/* loaded from: classes.dex */
public class WritableCellFeatures extends CellFeatures {
    public static final BaseCellFeatures.ValidationCondition BETWEEN = BaseCellFeatures.BETWEEN;
    public static final BaseCellFeatures.ValidationCondition NOT_BETWEEN = BaseCellFeatures.NOT_BETWEEN;
    public static final BaseCellFeatures.ValidationCondition EQUAL = BaseCellFeatures.EQUAL;
    public static final BaseCellFeatures.ValidationCondition NOT_EQUAL = BaseCellFeatures.NOT_EQUAL;
    public static final BaseCellFeatures.ValidationCondition GREATER_THAN = BaseCellFeatures.GREATER_THAN;
    public static final BaseCellFeatures.ValidationCondition LESS_THAN = BaseCellFeatures.LESS_THAN;
    public static final BaseCellFeatures.ValidationCondition GREATER_EQUAL = BaseCellFeatures.GREATER_EQUAL;
    public static final BaseCellFeatures.ValidationCondition LESS_EQUAL = BaseCellFeatures.LESS_EQUAL;

    public WritableCellFeatures() {
    }

    public WritableCellFeatures(CellFeatures cf) {
        super(cf);
    }

    @Override // jxl.biff.BaseCellFeatures
    public void setComment(String s) {
        super.setComment(s);
    }

    @Override // jxl.biff.BaseCellFeatures
    public void setComment(String s, double width, double height) {
        super.setComment(s, width, height);
    }

    @Override // jxl.biff.BaseCellFeatures
    public void removeComment() {
        super.removeComment();
    }

    @Override // jxl.biff.BaseCellFeatures
    public void removeDataValidation() {
        super.removeDataValidation();
    }

    @Override // jxl.biff.BaseCellFeatures
    public void setDataValidationList(Collection c) {
        super.setDataValidationList(c);
    }

    @Override // jxl.biff.BaseCellFeatures
    public void setDataValidationRange(int col1, int row1, int col2, int row2) {
        super.setDataValidationRange(col1, row1, col2, row2);
    }

    @Override // jxl.biff.BaseCellFeatures
    public void setDataValidationRange(String namedRange) {
        super.setDataValidationRange(namedRange);
    }

    @Override // jxl.biff.BaseCellFeatures
    public void setNumberValidation(double val, BaseCellFeatures.ValidationCondition c) {
        super.setNumberValidation(val, c);
    }

    @Override // jxl.biff.BaseCellFeatures
    public void setNumberValidation(double val1, double val2, BaseCellFeatures.ValidationCondition c) {
        super.setNumberValidation(val1, val2, c);
    }
}
