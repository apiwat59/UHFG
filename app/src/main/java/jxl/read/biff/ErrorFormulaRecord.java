package jxl.read.biff;

import jxl.CellType;
import jxl.ErrorCell;
import jxl.ErrorFormulaCell;
import jxl.biff.FormattingRecords;
import jxl.biff.FormulaData;
import jxl.biff.WorkbookMethods;
import jxl.biff.formula.ExternalSheet;
import jxl.biff.formula.FormulaErrorCode;
import jxl.biff.formula.FormulaException;
import jxl.biff.formula.FormulaParser;
import jxl.common.Assert;

/* loaded from: classes.dex */
class ErrorFormulaRecord extends CellValue implements ErrorCell, FormulaData, ErrorFormulaCell {
    private byte[] data;
    private FormulaErrorCode error;
    private int errorCode;
    private ExternalSheet externalSheet;
    private String formulaString;
    private WorkbookMethods nameTable;

    public ErrorFormulaRecord(Record t, FormattingRecords fr, ExternalSheet es, WorkbookMethods nt, SheetImpl si) {
        super(t, fr, si);
        this.externalSheet = es;
        this.nameTable = nt;
        byte[] data = getRecord().getData();
        this.data = data;
        Assert.verify(data[6] == 2);
        this.errorCode = this.data[8];
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
        byte[] bArr = this.data;
        byte[] d = new byte[bArr.length - 6];
        System.arraycopy(bArr, 6, d, 0, bArr.length - 6);
        return d;
    }

    @Override // jxl.FormulaCell
    public String getFormula() throws FormulaException {
        if (this.formulaString == null) {
            byte[] bArr = this.data;
            byte[] tokens = new byte[bArr.length - 22];
            System.arraycopy(bArr, 22, tokens, 0, tokens.length);
            FormulaParser fp = new FormulaParser(tokens, this, this.externalSheet, this.nameTable, getSheet().getWorkbook().getSettings());
            fp.parse();
            this.formulaString = fp.getFormula();
        }
        return this.formulaString;
    }
}
