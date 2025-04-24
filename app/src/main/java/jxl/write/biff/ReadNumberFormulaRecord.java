package jxl.write.biff;

import java.text.NumberFormat;
import jxl.NumberFormulaCell;
import jxl.biff.DoubleHelper;
import jxl.biff.FormulaData;
import jxl.biff.IntegerHelper;
import jxl.biff.formula.FormulaException;
import jxl.biff.formula.FormulaParser;
import jxl.common.Logger;

/* loaded from: classes.dex */
class ReadNumberFormulaRecord extends ReadFormulaRecord implements NumberFormulaCell {
    private static Logger logger = Logger.getLogger(ReadNumberFormulaRecord.class);

    public ReadNumberFormulaRecord(FormulaData f) {
        super(f);
    }

    @Override // jxl.NumberCell
    public double getValue() {
        return ((NumberFormulaCell) getReadFormula()).getValue();
    }

    @Override // jxl.NumberCell
    public NumberFormat getNumberFormat() {
        return ((NumberFormulaCell) getReadFormula()).getNumberFormat();
    }

    @Override // jxl.write.biff.ReadFormulaRecord
    protected byte[] handleFormulaException() {
        byte[] celldata = super.getCellData();
        WritableWorkbookImpl w = getSheet().getWorkbook();
        FormulaParser parser = new FormulaParser(Double.toString(getValue()), w, w, w.getSettings());
        try {
            parser.parse();
        } catch (FormulaException e2) {
            logger.warn(e2.getMessage());
        }
        byte[] formulaBytes = parser.getBytes();
        byte[] expressiondata = new byte[formulaBytes.length + 16];
        IntegerHelper.getTwoBytes(formulaBytes.length, expressiondata, 14);
        System.arraycopy(formulaBytes, 0, expressiondata, 16, formulaBytes.length);
        expressiondata[8] = (byte) (expressiondata[8] | 2);
        byte[] data = new byte[celldata.length + expressiondata.length];
        System.arraycopy(celldata, 0, data, 0, celldata.length);
        System.arraycopy(expressiondata, 0, data, celldata.length, expressiondata.length);
        DoubleHelper.getIEEEBytes(getValue(), data, 6);
        return data;
    }
}
