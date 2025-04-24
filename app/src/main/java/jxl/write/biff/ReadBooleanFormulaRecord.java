package jxl.write.biff;

import jxl.BooleanFormulaCell;
import jxl.biff.FormulaData;

/* loaded from: classes.dex */
class ReadBooleanFormulaRecord extends ReadFormulaRecord implements BooleanFormulaCell {
    public ReadBooleanFormulaRecord(FormulaData f) {
        super(f);
    }

    @Override // jxl.BooleanCell
    public boolean getValue() {
        return ((BooleanFormulaCell) getReadFormula()).getValue();
    }
}
