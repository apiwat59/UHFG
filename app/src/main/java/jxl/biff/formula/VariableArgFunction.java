package jxl.biff.formula;

import java.util.Stack;
import jxl.WorkbookSettings;
import jxl.biff.IntegerHelper;
import jxl.common.Logger;

/* loaded from: classes.dex */
class VariableArgFunction extends Operator implements ParsedThing {
    private static Logger logger = Logger.getLogger(VariableArgFunction.class);
    private int arguments;
    private Function function;
    private boolean readFromSheet = true;
    private WorkbookSettings settings;

    public VariableArgFunction(WorkbookSettings ws) {
        this.settings = ws;
    }

    public VariableArgFunction(Function f, int a, WorkbookSettings ws) {
        this.function = f;
        this.arguments = a;
        this.settings = ws;
    }

    @Override // jxl.biff.formula.ParsedThing
    public int read(byte[] data, int pos) throws FormulaException {
        this.arguments = data[pos];
        int index = IntegerHelper.getInt(data[pos + 1], data[pos + 2]);
        Function function = Function.getFunction(index);
        this.function = function;
        if (function == Function.UNKNOWN) {
            throw new FormulaException(FormulaException.UNRECOGNIZED_FUNCTION, index);
        }
        return 3;
    }

    @Override // jxl.biff.formula.Operator
    public void getOperands(Stack s) {
        int i = this.arguments;
        ParseItem[] items = new ParseItem[i];
        for (int i2 = i - 1; i2 >= 0; i2--) {
            ParseItem pi = (ParseItem) s.pop();
            items[i2] = pi;
        }
        for (int i3 = 0; i3 < this.arguments; i3++) {
            add(items[i3]);
        }
    }

    @Override // jxl.biff.formula.ParseItem
    public void getString(StringBuffer buf) {
        buf.append(this.function.getName(this.settings));
        buf.append('(');
        if (this.arguments > 0) {
            ParseItem[] operands = getOperands();
            if (this.readFromSheet) {
                operands[0].getString(buf);
                for (int i = 1; i < this.arguments; i++) {
                    buf.append(',');
                    operands[i].getString(buf);
                }
            } else {
                operands[this.arguments - 1].getString(buf);
                for (int i2 = this.arguments - 2; i2 >= 0; i2--) {
                    buf.append(',');
                    operands[i2].getString(buf);
                }
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

    Function getFunction() {
        return this.function;
    }

    @Override // jxl.biff.formula.ParseItem
    byte[] getBytes() {
        handleSpecialCases();
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
        byte[] newdata2 = new byte[i + 4];
        System.arraycopy(data, 0, newdata2, 0, data.length);
        newdata2[data.length] = !useAlternateCode() ? Token.FUNCTIONVARARG.getCode() : Token.FUNCTIONVARARG.getCode2();
        newdata2[data.length + 1] = (byte) this.arguments;
        IntegerHelper.getTwoBytes(this.function.getCode(), newdata2, data.length + 2);
        return newdata2;
    }

    @Override // jxl.biff.formula.Operator
    int getPrecedence() {
        return 3;
    }

    private void handleSpecialCases() {
        if (this.function == Function.SUMPRODUCT) {
            ParseItem[] operands = getOperands();
            for (int i = operands.length - 1; i >= 0; i--) {
                if (operands[i] instanceof Area) {
                    operands[i].setAlternateCode();
                }
            }
        }
    }
}
