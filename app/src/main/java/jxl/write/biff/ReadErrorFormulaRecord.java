package jxl.write.biff;

import jxl.ErrorFormulaCell;
import jxl.biff.FormulaData;
import jxl.biff.IntegerHelper;
import jxl.biff.formula.FormulaErrorCode;
import jxl.biff.formula.FormulaException;
import jxl.biff.formula.FormulaParser;
import jxl.common.Logger;

/* loaded from: classes.dex */
class ReadErrorFormulaRecord extends ReadFormulaRecord implements ErrorFormulaCell {
    private static Logger logger = Logger.getLogger(ReadErrorFormulaRecord.class);

    public ReadErrorFormulaRecord(FormulaData f) {
        super(f);
    }

    @Override // jxl.ErrorCell
    public int getErrorCode() {
        return ((ErrorFormulaCell) getReadFormula()).getErrorCode();
    }

    @Override // jxl.write.biff.ReadFormulaRecord
    protected byte[] handleFormulaException() {
        String formulaString;
        byte[] celldata = super.getCellData();
        int errorCode = getErrorCode();
        if (errorCode == FormulaErrorCode.DIV0.getCode()) {
            formulaString = "1/0";
        } else if (errorCode == FormulaErrorCode.VALUE.getCode()) {
            formulaString = "\"\"/0";
        } else if (errorCode == FormulaErrorCode.REF.getCode()) {
            formulaString = "\"#REF!\"";
        } else {
            formulaString = "\"ERROR\"";
        }
        WritableWorkbookImpl w = getSheet().getWorkbook();
        FormulaParser parser = new FormulaParser(formulaString, w, w, w.getSettings());
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
        data[6] = 2;
        data[12] = -1;
        data[13] = -1;
        data[8] = (byte) errorCode;
        return data;
    }
}
