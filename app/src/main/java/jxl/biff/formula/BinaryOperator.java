package jxl.biff.formula;

import java.util.Stack;
import jxl.common.Logger;

/* loaded from: classes.dex */
abstract class BinaryOperator extends Operator implements ParsedThing {
    private static final Logger logger = Logger.getLogger(BinaryOperator.class);

    abstract String getSymbol();

    abstract Token getToken();

    @Override // jxl.biff.formula.ParsedThing
    public int read(byte[] data, int pos) {
        return 0;
    }

    @Override // jxl.biff.formula.Operator
    public void getOperands(Stack s) {
        ParseItem o1 = (ParseItem) s.pop();
        ParseItem o2 = (ParseItem) s.pop();
        add(o1);
        add(o2);
    }

    @Override // jxl.biff.formula.ParseItem
    public void getString(StringBuffer buf) {
        ParseItem[] operands = getOperands();
        operands[1].getString(buf);
        buf.append(getSymbol());
        operands[0].getString(buf);
    }

    @Override // jxl.biff.formula.ParseItem
    public void adjustRelativeCellReferences(int colAdjust, int rowAdjust) {
        ParseItem[] operands = getOperands();
        operands[1].adjustRelativeCellReferences(colAdjust, rowAdjust);
        operands[0].adjustRelativeCellReferences(colAdjust, rowAdjust);
    }

    @Override // jxl.biff.formula.ParseItem
    void columnInserted(int sheetIndex, int col, boolean currentSheet) {
        ParseItem[] operands = getOperands();
        operands[1].columnInserted(sheetIndex, col, currentSheet);
        operands[0].columnInserted(sheetIndex, col, currentSheet);
    }

    @Override // jxl.biff.formula.ParseItem
    void columnRemoved(int sheetIndex, int col, boolean currentSheet) {
        ParseItem[] operands = getOperands();
        operands[1].columnRemoved(sheetIndex, col, currentSheet);
        operands[0].columnRemoved(sheetIndex, col, currentSheet);
    }

    @Override // jxl.biff.formula.ParseItem
    void rowInserted(int sheetIndex, int row, boolean currentSheet) {
        ParseItem[] operands = getOperands();
        operands[1].rowInserted(sheetIndex, row, currentSheet);
        operands[0].rowInserted(sheetIndex, row, currentSheet);
    }

    @Override // jxl.biff.formula.ParseItem
    void rowRemoved(int sheetIndex, int row, boolean currentSheet) {
        ParseItem[] operands = getOperands();
        operands[1].rowRemoved(sheetIndex, row, currentSheet);
        operands[0].rowRemoved(sheetIndex, row, currentSheet);
    }

    @Override // jxl.biff.formula.ParseItem
    byte[] getBytes() {
        ParseItem[] operands = getOperands();
        byte[] data = new byte[0];
        for (int i = operands.length - 1; i >= 0; i--) {
            byte[] opdata = operands[i].getBytes();
            byte[] newdata = new byte[data.length + opdata.length];
            System.arraycopy(data, 0, newdata, 0, data.length);
            System.arraycopy(opdata, 0, newdata, data.length, opdata.length);
            data = newdata;
        }
        int i2 = data.length;
        byte[] newdata2 = new byte[i2 + 1];
        System.arraycopy(data, 0, newdata2, 0, data.length);
        newdata2[data.length] = getToken().getCode();
        return newdata2;
    }

    @Override // jxl.biff.formula.ParseItem
    void handleImportedCellReferences() {
        ParseItem[] operands = getOperands();
        operands[0].handleImportedCellReferences();
        operands[1].handleImportedCellReferences();
    }
}
