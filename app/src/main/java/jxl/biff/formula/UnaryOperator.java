package jxl.biff.formula;

import java.util.Stack;

/* loaded from: classes.dex */
abstract class UnaryOperator extends Operator implements ParsedThing {
    abstract String getSymbol();

    abstract Token getToken();

    @Override // jxl.biff.formula.ParsedThing
    public int read(byte[] data, int pos) {
        return 0;
    }

    @Override // jxl.biff.formula.Operator
    public void getOperands(Stack s) {
        ParseItem o1 = (ParseItem) s.pop();
        add(o1);
    }

    @Override // jxl.biff.formula.ParseItem
    public void getString(StringBuffer buf) {
        ParseItem[] operands = getOperands();
        buf.append(getSymbol());
        operands[0].getString(buf);
    }

    @Override // jxl.biff.formula.ParseItem
    public void adjustRelativeCellReferences(int colAdjust, int rowAdjust) {
        ParseItem[] operands = getOperands();
        operands[0].adjustRelativeCellReferences(colAdjust, rowAdjust);
    }

    @Override // jxl.biff.formula.ParseItem
    void columnInserted(int sheetIndex, int col, boolean currentSheet) {
        ParseItem[] operands = getOperands();
        operands[0].columnInserted(sheetIndex, col, currentSheet);
    }

    @Override // jxl.biff.formula.ParseItem
    void columnRemoved(int sheetIndex, int col, boolean currentSheet) {
        ParseItem[] operands = getOperands();
        operands[0].columnRemoved(sheetIndex, col, currentSheet);
    }

    @Override // jxl.biff.formula.ParseItem
    void rowInserted(int sheetIndex, int row, boolean currentSheet) {
        ParseItem[] operands = getOperands();
        operands[0].rowInserted(sheetIndex, row, currentSheet);
    }

    @Override // jxl.biff.formula.ParseItem
    void rowRemoved(int sheetIndex, int row, boolean currentSheet) {
        ParseItem[] operands = getOperands();
        operands[0].rowRemoved(sheetIndex, row, currentSheet);
    }

    @Override // jxl.biff.formula.ParseItem
    byte[] getBytes() {
        ParseItem[] operands = getOperands();
        byte[] data = operands[0].getBytes();
        byte[] newdata = new byte[data.length + 1];
        System.arraycopy(data, 0, newdata, 0, data.length);
        newdata[data.length] = getToken().getCode();
        return newdata;
    }

    @Override // jxl.biff.formula.ParseItem
    void handleImportedCellReferences() {
        ParseItem[] operands = getOperands();
        operands[0].handleImportedCellReferences();
    }
}
