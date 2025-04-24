package jxl.biff.formula;

import jxl.biff.IntegerHelper;

/* loaded from: classes.dex */
class RangeSeparator extends BinaryOperator implements ParsedThing {
    @Override // jxl.biff.formula.BinaryOperator
    public String getSymbol() {
        return ":";
    }

    @Override // jxl.biff.formula.BinaryOperator
    Token getToken() {
        return Token.RANGE;
    }

    @Override // jxl.biff.formula.Operator
    int getPrecedence() {
        return 1;
    }

    @Override // jxl.biff.formula.BinaryOperator, jxl.biff.formula.ParseItem
    byte[] getBytes() {
        setVolatile();
        setOperandAlternateCode();
        byte[] funcBytes = super.getBytes();
        byte[] bytes = new byte[funcBytes.length + 3];
        System.arraycopy(funcBytes, 0, bytes, 3, funcBytes.length);
        bytes[0] = Token.MEM_FUNC.getCode();
        IntegerHelper.getTwoBytes(funcBytes.length, bytes, 1);
        return bytes;
    }
}
