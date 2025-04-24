package jxl.read.biff;

import jxl.CellType;
import jxl.ErrorCell;
import jxl.ErrorFormulaCell;
import jxl.biff.FormattingRecords;
import jxl.biff.FormulaData;
import jxl.biff.IntegerHelper;
import jxl.biff.WorkbookMethods;
import jxl.biff.formula.ExternalSheet;
import jxl.biff.formula.FormulaErrorCode;
import jxl.biff.formula.FormulaException;
import jxl.biff.formula.FormulaParser;
import jxl.common.Logger;

/* loaded from: classes.dex */
public class SharedErrorFormulaRecord extends BaseSharedFormulaRecord implements ErrorCell, FormulaData, ErrorFormulaCell {
    private static Logger logger = Logger.getLogger(SharedErrorFormulaRecord.class);
    private byte[] data;
    private FormulaErrorCode error;
    private int errorCode;

    public SharedErrorFormulaRecord(Record t, File excelFile, int ec, FormattingRecords fr, ExternalSheet es, WorkbookMethods nt, SheetImpl si) {
        super(t, fr, es, nt, si, excelFile.getPos());
        this.errorCode = ec;
    }

    @Override // jxl.ErrorCell
    public int getErrorCode() {
        return this.errorCode;
    }

    @Override // jxl.Cell
    public String getContents() {
        if (this.error == null) {
            this.error = FormulaErrorCode.getErrorCode(this.errorCode);
        }
        if (this.error != FormulaErrorCode.UNKNOWN) {
            return this.error.getDescription();
        }
        return "ERROR " + this.errorCode;
    }

    @Override // jxl.Cell
    public CellType getType() {
        return CellType.FORMULA_ERROR;
    }

    @Override // jxl.biff.FormulaData
    public byte[] getFormulaData() throws FormulaException {
        if (!getSheet().getWorkbookBof().isBiff8()) {
            throw new FormulaException(FormulaException.BIFF8_SUPPORTED);
        }
        FormulaParser fp = new FormulaParser(getTokens(), this, getExternalSheet(), getNameTable(), getSheet().getWorkbook().getSettings());
        fp.parse();
        byte[] rpnTokens = fp.getBytes();
        byte[] data = new byte[rpnTokens.length + 22];
        IntegerHelper.getTwoBytes(getRow(), data, 0);
        IntegerHelper.getTwoBytes(getColumn(), data, 2);
        IntegerHelper.getTwoBytes(getXFIndex(), data, 4);
        data[6] = 2;
        data[8] = (byte) this.errorCode;
        data[12] = -1;
        data[13] = -1;
        System.arraycopy(rpnTokens, 0, data, 22, rpnTokens.length);
        IntegerHelper.getTwoBytes(rpnTokens.length, data, 20);
        byte[] d = new byte[data.length - 6];
        System.arraycopy(data, 6, d, 0, data.length - 6);
        return d;
    }
}
