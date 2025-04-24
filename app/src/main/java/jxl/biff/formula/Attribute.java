package jxl.biff.formula;

import java.util.Stack;
import jxl.WorkbookSettings;
import jxl.biff.IntegerHelper;
import jxl.common.Logger;

/* loaded from: classes.dex */
class Attribute extends Operator implements ParsedThing {
    private static final int CHOOSE_MASK = 4;
    private static final int GOTO_MASK = 8;
    private static final int IF_MASK = 2;
    private static final int SUM_MASK = 16;
    private static Logger logger = Logger.getLogger(Attribute.class);
    private VariableArgFunction ifConditions;
    private int options;
    private WorkbookSettings settings;
    private int word;

    public Attribute(WorkbookSettings ws) {
        this.settings = ws;
    }

    public Attribute(StringFunction sf, WorkbookSettings ws) {
        this.settings = ws;
        if (sf.getFunction(ws) == Function.SUM) {
            this.options |= 16;
        } else if (sf.getFunction(this.settings) == Function.IF) {
            this.options |= 2;
        }
    }

    void setIfConditions(VariableArgFunction vaf) {
        this.ifConditions = vaf;
        this.options |= 2;
    }

    @Override // jxl.biff.formula.ParsedThing
    public int read(byte[] data, int pos) {
        this.options = data[pos];
        this.word = IntegerHelper.getInt(data[pos + 1], data[pos + 2]);
        if (isChoose()) {
            return ((this.word + 1) * 2) + 3;
        }
        return 3;
    }

    public boolean isFunction() {
        return (this.options & 18) != 0;
    }

    public boolean isSum() {
        return (this.options & 16) != 0;
    }

    public boolean isIf() {
        return (this.options & 2) != 0;
    }

    public boolean isGoto() {
        return (this.options & 8) != 0;
    }

    public boolean isChoose() {
        return (this.options & 4) != 0;
    }

    @Override // jxl.biff.formula.Operator
    public void getOperands(Stack s) {
        int i = this.options;
        if ((i & 16) != 0) {
            ParseItem o1 = (ParseItem) s.pop();
            add(o1);
        } else if ((i & 2) != 0) {
            ParseItem o12 = (ParseItem) s.pop();
            add(o12);
        }
    }

    @Override // jxl.biff.formula.ParseItem
    public void getString(StringBuffer buf) {
        int i = this.options;
        if ((i & 16) != 0) {
            ParseItem[] operands = getOperands();
            buf.append(Function.SUM.getName(this.settings));
            buf.append('(');
            operands[0].getString(buf);
            buf.append(')');
            return;
        }
        if ((i & 2) != 0) {
            buf.append(Function.IF.getName(this.settings));
            buf.append('(');
            ParseItem[] operands2 = this.ifConditions.getOperands();
            for (int i2 = 0; i2 < operands2.length - 1; i2++) {
                operands2[i2].getString(buf);
                buf.append(',');
            }
            int i3 = operands2.length;
            operands2[i3 - 1].getString(buf);
            buf.append(')');
        }
    }

    @Override // jxl.biff.formula.ParseItem
    byte[] getBytes() {
        byte[] data = new byte[0];
        if (isSum()) {
            ParseItem[] operands = getOperands();
            for (int i = operands.length - 1; i >= 0; i--) {
                byte[] opdata = operands[i].getBytes();
                byte[] newdata = new byte[data.length + opdata.length];
                System.arraycopy(data, 0, newdata, 0, data.length);
                System.arraycopy(opdata, 0, newdata, data.length, opdata.length);
                data = newdata;
            }
            int i2 = data.length;
            byte[] newdata2 = new byte[i2 + 4];
            System.arraycopy(data, 0, newdata2, 0, data.length);
            newdata2[data.length] = Token.ATTRIBUTE.getCode();
            newdata2[data.length + 1] = 16;
            return newdata2;
        }
        if (isIf()) {
            return getIf();
        }
        return data;
    }

    private byte[] getIf() {
        ParseItem[] operands = this.ifConditions.getOperands();
        int numArgs = operands.length;
        byte[] data = operands[0].getBytes();
        int pos = data.length;
        byte[] newdata = new byte[data.length + 4];
        System.arraycopy(data, 0, newdata, 0, data.length);
        newdata[pos] = Token.ATTRIBUTE.getCode();
        newdata[pos + 1] = 2;
        int falseOffsetPos = pos + 2;
        byte[] truedata = operands[1].getBytes();
        byte[] newdata2 = new byte[newdata.length + truedata.length];
        System.arraycopy(newdata, 0, newdata2, 0, newdata.length);
        System.arraycopy(truedata, 0, newdata2, newdata.length, truedata.length);
        int pos2 = newdata2.length;
        byte[] newdata3 = new byte[newdata2.length + 4];
        System.arraycopy(newdata2, 0, newdata3, 0, newdata2.length);
        byte[] data2 = newdata3;
        data2[pos2] = Token.ATTRIBUTE.getCode();
        data2[pos2 + 1] = 8;
        int gotoEndPos = pos2 + 2;
        if (numArgs > 2) {
            IntegerHelper.getTwoBytes((data2.length - falseOffsetPos) - 2, data2, falseOffsetPos);
            byte[] falsedata = operands[numArgs - 1].getBytes();
            byte[] newdata4 = new byte[data2.length + falsedata.length];
            System.arraycopy(data2, 0, newdata4, 0, data2.length);
            System.arraycopy(falsedata, 0, newdata4, data2.length, falsedata.length);
            int pos3 = newdata4.length;
            byte[] newdata5 = new byte[newdata4.length + 4];
            System.arraycopy(newdata4, 0, newdata5, 0, newdata4.length);
            data2 = newdata5;
            data2[pos3] = Token.ATTRIBUTE.getCode();
            data2[pos3 + 1] = 8;
            data2[pos3 + 2] = 3;
        }
        int pos4 = data2.length;
        byte[] newdata6 = new byte[data2.length + 4];
        System.arraycopy(data2, 0, newdata6, 0, data2.length);
        newdata6[pos4] = Token.FUNCTIONVARARG.getCode();
        newdata6[pos4 + 1] = (byte) numArgs;
        newdata6[pos4 + 2] = 1;
        newdata6[pos4 + 3] = 0;
        int endPos = newdata6.length - 1;
        if (numArgs < 3) {
            IntegerHelper.getTwoBytes((endPos - falseOffsetPos) - 5, newdata6, falseOffsetPos);
        }
        IntegerHelper.getTwoBytes((endPos - gotoEndPos) - 2, newdata6, gotoEndPos);
        return newdata6;
    }

    @Override // jxl.biff.formula.Operator
    int getPrecedence() {
        return 3;
    }

    @Override // jxl.biff.formula.ParseItem
    public void adjustRelativeCellReferences(int colAdjust, int rowAdjust) {
        ParseItem[] operands;
        if (isIf()) {
            operands = this.ifConditions.getOperands();
        } else {
            operands = getOperands();
        }
        for (ParseItem parseItem : operands) {
            parseItem.adjustRelativeCellReferences(colAdjust, rowAdjust);
        }
    }

    @Override // jxl.biff.formula.ParseItem
    void columnInserted(int sheetIndex, int col, boolean currentSheet) {
        ParseItem[] operands;
        if (isIf()) {
            operands = this.ifConditions.getOperands();
        } else {
            operands = getOperands();
        }
        for (ParseItem parseItem : operands) {
            parseItem.columnInserted(sheetIndex, col, currentSheet);
        }
    }

    @Override // jxl.biff.formula.ParseItem
    void columnRemoved(int sheetIndex, int col, boolean currentSheet) {
        ParseItem[] operands;
        if (isIf()) {
            operands = this.ifConditions.getOperands();
        } else {
            operands = getOperands();
        }
        for (ParseItem parseItem : operands) {
            parseItem.columnRemoved(sheetIndex, col, currentSheet);
        }
    }

    @Override // jxl.biff.formula.ParseItem
    void rowInserted(int sheetIndex, int row, boolean currentSheet) {
        ParseItem[] operands;
        if (isIf()) {
            operands = this.ifConditions.getOperands();
        } else {
            operands = getOperands();
        }
        for (ParseItem parseItem : operands) {
            parseItem.rowInserted(sheetIndex, row, currentSheet);
        }
    }

    @Override // jxl.biff.formula.ParseItem
    void rowRemoved(int sheetIndex, int row, boolean currentSheet) {
        ParseItem[] operands;
        if (isIf()) {
            operands = this.ifConditions.getOperands();
        } else {
            operands = getOperands();
        }
        for (ParseItem parseItem : operands) {
            parseItem.rowRemoved(sheetIndex, row, currentSheet);
        }
    }

    @Override // jxl.biff.formula.ParseItem
    void handleImportedCellReferences() {
        ParseItem[] operands;
        if (isIf()) {
            operands = this.ifConditions.getOperands();
        } else {
            operands = getOperands();
        }
        for (ParseItem parseItem : operands) {
            parseItem.handleImportedCellReferences();
        }
    }
}
