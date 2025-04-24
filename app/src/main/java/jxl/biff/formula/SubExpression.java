package jxl.biff.formula;

import java.util.Stack;
import jxl.biff.IntegerHelper;

/* loaded from: classes.dex */
abstract class SubExpression extends Operand implements ParsedThing {
    private int length;
    private ParseItem[] subExpression;

    protected SubExpression() {
    }

    public int read(byte[] data, int pos) {
        this.length = IntegerHelper.getInt(data[pos], data[pos + 1]);
        return 2;
    }

    public void getOperands(Stack s) {
    }

    @Override // jxl.biff.formula.ParseItem
    byte[] getBytes() {
        return null;
    }

    int getPrecedence() {
        return 5;
    }

    public int getLength() {
        return this.length;
    }

    protected final void setLength(int l) {
        this.length = l;
    }

    public void setSubExpression(ParseItem[] pi) {
        this.subExpression = pi;
    }

    protected ParseItem[] getSubExpression() {
        return this.subExpression;
    }
}
