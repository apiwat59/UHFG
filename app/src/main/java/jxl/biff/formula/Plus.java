package jxl.biff.formula;

/* loaded from: classes.dex */
class Plus extends StringOperator {
    @Override // jxl.biff.formula.StringOperator
    Operator getBinaryOperator() {
        return new Add();
    }

    @Override // jxl.biff.formula.StringOperator
    Operator getUnaryOperator() {
        return new UnaryPlus();
    }

    @Override // jxl.biff.formula.ParseItem
    void handleImportedCellReferences() {
    }
}
