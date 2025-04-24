package jxl.biff.formula;

/* loaded from: classes.dex */
class UnaryMinus extends UnaryOperator implements ParsedThing {
    @Override // jxl.biff.formula.UnaryOperator
    public String getSymbol() {
        return "-";
    }

    @Override // jxl.biff.formula.UnaryOperator
    Token getToken() {
        return Token.UNARY_MINUS;
    }

    @Override // jxl.biff.formula.Operator
    int getPrecedence() {
        return 2;
    }

    @Override // jxl.biff.formula.UnaryOperator, jxl.biff.formula.ParseItem
    void handleImportedCellReferences() {
    }
}
