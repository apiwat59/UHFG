package jxl.biff.formula;

import java.util.Stack;
import jxl.common.Assert;

/* loaded from: classes.dex */
abstract class StringOperator extends Operator {
    abstract Operator getBinaryOperator();

    abstract Operator getUnaryOperator();

    protected StringOperator() {
    }

    @Override // jxl.biff.formula.Operator
    public void getOperands(Stack s) {
        Assert.verify(false);
    }

    @Override // jxl.biff.formula.Operator
    int getPrecedence() {
        Assert.verify(false);
        return 0;
    }

    @Override // jxl.biff.formula.ParseItem
    byte[] getBytes() {
        Assert.verify(false);
        return null;
    }

    @Override // jxl.biff.formula.ParseItem
    void getString(StringBuffer buf) {
        Assert.verify(false);
    }

    @Override // jxl.biff.formula.ParseItem
    public void adjustRelativeCellReferences(int colAdjust, int rowAdjust) {
        Assert.verify(false);
    }

    @Override // jxl.biff.formula.ParseItem
    void columnInserted(int sheetIndex, int col, boolean currentSheet) {
        Assert.verify(false);
    }

    @Override // jxl.biff.formula.ParseItem
    void columnRemoved(int sheetIndex, int col, boolean currentSheet) {
        Assert.verify(false);
    }

    @Override // jxl.biff.formula.ParseItem
    void rowInserted(int sheetIndex, int row, boolean currentSheet) {
        Assert.verify(false);
    }

    @Override // jxl.biff.formula.ParseItem
    void rowRemoved(int sheetIndex, int row, boolean currentSheet) {
        Assert.verify(false);
    }
}
