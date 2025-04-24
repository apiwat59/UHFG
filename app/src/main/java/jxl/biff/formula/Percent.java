package jxl.biff.formula;

/* loaded from: classes.dex */
class Percent extends UnaryOperator implements ParsedThing {
    @Override // jxl.biff.formula.UnaryOperator
    public String getSymbol() {
        return "%";
    }

    @Override // jxl.biff.formula.UnaryOperator, jxl.biff.formula.ParseItem
    public void getString(StringBuffer buf) {
        ParseItem[] operands = getOperands();
        operands[0].getString(buf);
        buf.append(getSymbol());
    }

    @Override // jxl.biff.formula.UnaryOperator, jxl.biff.formula.ParseItem
    void handleImportedCellReferences() {
        ParseItem[] operands = getOperands();
        operands[0].handleImportedCellReferences();
    }

    @Override // jxl.biff.formula.UnaryOperator
    Token getToken() {
        return Token.PERCENT;
    }

    @Override // jxl.biff.formula.Operator
    int getPrecedence() {
        return 5;
    }
}
