package jxl.biff.formula;

/* loaded from: classes.dex */
class NotEqual extends BinaryOperator implements ParsedThing {
    @Override // jxl.biff.formula.BinaryOperator
    public String getSymbol() {
        return "<>";
    }

    @Override // jxl.biff.formula.BinaryOperator
    Token getToken() {
        return Token.NOT_EQUAL;
    }

    @Override // jxl.biff.formula.Operator
    int getPrecedence() {
        return 5;
    }
}
