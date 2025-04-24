package jxl.biff.formula;

import jxl.common.Logger;

/* loaded from: classes.dex */
class Minus extends StringOperator {
    private static Logger logger = Logger.getLogger(StringOperator.class);

    @Override // jxl.biff.formula.StringOperator
    Operator getBinaryOperator() {
        return new Subtract();
    }

    @Override // jxl.biff.formula.StringOperator
    Operator getUnaryOperator() {
        return new UnaryMinus();
    }

    @Override // jxl.biff.formula.ParseItem
    void handleImportedCellReferences() {
    }
}
