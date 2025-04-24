package jxl.biff.formula;

/* loaded from: classes.dex */
class GreaterThan extends BinaryOperator implements ParsedThing {
    @Override // jxl.biff.formula.BinaryOperator
    public String getSymbol() {
        return ">";
    }

    @Override // jxl.biff.formula.BinaryOperator
    Token getToken() {
        return Token.GREATER_THAN;
    }

    @Override // jxl.biff.formula.Operator
    int getPrecedence() {
        return 5;
    }
}
