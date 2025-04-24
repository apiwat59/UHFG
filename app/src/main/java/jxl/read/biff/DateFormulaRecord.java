package jxl.read.biff;

import java.text.NumberFormat;
import jxl.CellType;
import jxl.DateCell;
import jxl.DateFormulaCell;
import jxl.biff.FormattingRecords;
import jxl.biff.FormulaData;
import jxl.biff.WorkbookMethods;
import jxl.biff.formula.ExternalSheet;
import jxl.biff.formula.FormulaException;
import jxl.biff.formula.FormulaParser;

/* loaded from: classes.dex */
class DateFormulaRecord extends DateRecord implements DateCell, FormulaData, DateFormulaCell {
    private byte[] data;
    private ExternalSheet externalSheet;
    private String formulaString;
    private WorkbookMethods nameTable;

    public DateFormulaRecord(NumberFormulaRecord t, FormattingRecords fr, ExternalSheet es, WorkbookMethods nt, boolean nf, SheetImpl si) throws FormulaException {
        super(t, t.getXFIndex(), fr, nf, si);
        this.externalSheet = es;
        this.nameTable = nt;
        this.data = t.getFormulaData();
    }

    @Override // jxl.read.biff.DateRecord, jxl.Cell
    public CellType getType() {
        return CellType.DATE_FORMULA;
    }

    @Override // jxl.biff.FormulaData
    public byte[] getFormulaData() throws FormulaException {
        if (!getSheet().getWorkbookBof().isBiff8()) {
            throw new FormulaException(FormulaException.BIFF8_SUPPORTED);
        }
        return this.data;
    }

    @Override // jxl.FormulaCell
    public String getFormula() throws FormulaException {
        if (this.formulaString == null) {
            byte[] bArr = this.data;
            byte[] tokens = new byte[bArr.length - 16];
            System.arraycopy(bArr, 16, tokens, 0, tokens.length);
            FormulaParser fp = new FormulaParser(tokens, this, this.externalSheet, this.nameTable, getSheet().getWorkbook().getSettings());
            fp.parse();
            this.formulaString = fp.getFormula();
        }
        return this.formulaString;
    }

    public double getValue() {
        return 0.0d;
    }

    public NumberFormat getNumberFormat() {
        return null;
    }
}
