package jxl.biff.formula;

import java.util.Stack;
import jxl.WorkbookSettings;
import jxl.biff.IntegerHelper;
import jxl.common.Assert;
import jxl.common.Logger;

/* loaded from: classes.dex */
class BuiltInFunction extends Operator implements ParsedThing {
    private static Logger logger = Logger.getLogger(BuiltInFunction.class);
    private Function function;
    private WorkbookSettings settings;

    public BuiltInFunction(WorkbookSettings ws) {
        this.settings = ws;
    }

    public BuiltInFunction(Function f, WorkbookSettings ws) {
        this.function = f;
        this.settings = ws;
    }

    @Override // jxl.biff.formula.ParsedThing
    public int read(byte[] data, int pos) {
        int index = IntegerHelper.getInt(data[pos], data[pos + 1]);
        Function function = Function.getFunction(index);
        this.function = function;
        Assert.verify(function != Function.UNKNOWN, "function code " + index);
        return 2;
    }

    @Override // jxl.biff.formula.Operator
    public void getOperands(Stack s) {
        ParseItem[] items = new ParseItem[this.function.getNumArgs()];
        for (int i = this.function.getNumArgs() - 1; i >= 0; i--) {
            ParseItem pi = (ParseItem) s.pop();
            items[i] = pi;
        }
        for (int i2 = 0; i2 < this.function.getNumArgs(); i2++) {
            add(items[i2]);
        }
    }

    @Override // jxl.biff.formula.ParseItem
    public void getString(StringBuffer buf) {
        buf.append(this.function.getName(this.settings));
        buf.append('(');
        int numArgs = this.function.getNumArgs();
        if (numArgs > 0) {
            ParseItem[] operands = getOperands();
            operands[0].getString(buf);
            for (int i = 1; i < numArgs; i++) {
                buf.append(',');
                operands[i].getString(buf);
            }
        }
        buf.append(')');
    }

    @Override // jxl.biff.formula.ParseItem
    public void adjustRelativeCellReferences(int colAdjust, int rowAdjust) {
        ParseItem[] operands = getOperands();
        for (ParseItem parseItem : operands) {
            parseItem.adjustRelativeCellReferences(colAdjust, rowAdjust);
        }
    }

    @Override // jxl.biff.formula.ParseItem
    void columnInserted(int sheetIndex, int col, boolean currentSheet) {
        ParseItem[] operands = getOperands();
        for (ParseItem parseItem : operands) {
            parseItem.columnInserted(sheetIndex, col, currentSheet);
        }
    }

    @Override // jxl.biff.formula.ParseItem
    void columnRemoved(int sheetIndex, int col, boolean currentSheet) {
        ParseItem[] operands = getOperands();
        for (ParseItem parseItem : operands) {
            parseItem.columnRemoved(sheetIndex, col, currentSheet);
        }
    }

    @Override // jxl.biff.formula.ParseItem
    void rowInserted(int sheetIndex, int row, boolean currentSheet) {
        ParseItem[] operands = getOperands();
        for (ParseItem parseItem : operands) {
            parseItem.rowInserted(sheetIndex, row, currentSheet);
        }
    }

    @Override // jxl.biff.formula.ParseItem
    void rowRemoved(int sheetIndex, int row, boolean currentSheet) {
        ParseItem[] operands = getOperands();
        for (ParseItem parseItem : operands) {
            parseItem.rowRemoved(sheetIndex, row, currentSheet);
        }
    }

    @Override // jxl.biff.formula.ParseItem
    void handleImportedCellReferences() {
        ParseItem[] operands = getOperands();
        for (ParseItem parseItem : operands) {
            parseItem.handleImportedCellReferences();
        }
    }

    @Override // jxl.biff.formula.ParseItem
    byte[] getBytes() {
        ParseItem[] operands = getOperands();
        byte[] data = new byte[0];
        for (ParseItem parseItem : operands) {
            byte[] opdata = parseItem.getBytes();
            byte[] newdata = new byte[data.length + opdata.length];
            System.arraycopy(data, 0, newdata, 0, data.length);
            System.arraycopy(opdata, 0, newdata, data.length, opdata.length);
            data = newdata;
        }
        int i = data.length;
        byte[] newdata2 = new byte[i + 3];
        System.arraycopy(data, 0, newdata2, 0, data.length);
        newdata2[data.length] = !useAlternateCode() ? Token.FUNCTION.getCode() : Token.FUNCTION.getCode2();
        IntegerHelper.getTwoBytes(this.function.getCode(), newdata2, data.length + 1);
        return newdata2;
    }

    @Override // jxl.biff.formula.Operator
    int getPrecedence() {
        return 3;
    }
}
