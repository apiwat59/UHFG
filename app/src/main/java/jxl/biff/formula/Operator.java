package jxl.biff.formula;

import java.util.Stack;

/* loaded from: classes.dex */
abstract class Operator extends ParseItem {
    private ParseItem[] operands = new ParseItem[0];

    public abstract void getOperands(Stack stack);

    abstract int getPrecedence();

    protected void setOperandAlternateCode() {
        int i = 0;
        while (true) {
            ParseItem[] parseItemArr = this.operands;
            if (i < parseItemArr.length) {
                parseItemArr[i].setAlternateCode();
                i++;
            } else {
                return;
            }
        }
    }

    protected void add(ParseItem n) {
        n.setParent(this);
        ParseItem[] parseItemArr = this.operands;
        ParseItem[] newOperands = new ParseItem[parseItemArr.length + 1];
        System.arraycopy(parseItemArr, 0, newOperands, 0, parseItemArr.length);
        newOperands[this.operands.length] = n;
        this.operands = newOperands;
    }

    protected ParseItem[] getOperands() {
        return this.operands;
    }
}
