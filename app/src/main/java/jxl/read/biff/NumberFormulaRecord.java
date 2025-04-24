package jxl.read.biff;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import jxl.CellType;
import jxl.NumberCell;
import jxl.NumberFormulaCell;
import jxl.biff.DoubleHelper;
import jxl.biff.FormattingRecords;
import jxl.biff.FormulaData;
import jxl.biff.WorkbookMethods;
import jxl.biff.formula.ExternalSheet;
import jxl.biff.formula.FormulaException;
import jxl.biff.formula.FormulaParser;
import jxl.common.Logger;

/* loaded from: classes.dex */
class NumberFormulaRecord extends CellValue implements NumberCell, FormulaData, NumberFormulaCell {
    private byte[] data;
    private ExternalSheet externalSheet;
    private NumberFormat format;
    private String formulaString;
    private WorkbookMethods nameTable;
    private double value;
    private static Logger logger = Logger.getLogger(NumberFormulaRecord.class);
    private static final DecimalFormat defaultFormat = new DecimalFormat("#.###");

    public NumberFormulaRecord(Record t, FormattingRecords fr, ExternalSheet es, WorkbookMethods nt, SheetImpl si) {
        super(t, fr, si);
        this.externalSheet = es;
        this.nameTable = nt;
        this.data = getRecord().getData();
        NumberFormat numberFormat = fr.getNumberFormat(getXFIndex());
        this.format = numberFormat;
        if (numberFormat == null) {
            this.format = defaultFormat;
        }
        this.value = DoubleHelper.getIEEEDouble(this.data, 6);
    }

    @Override // jxl.NumberCell
    public double getValue() {
        return this.value;
    }

    @Override // jxl.Cell
    public String getContents() {
        return !Double.isNaN(this.value) ? this.format.format(this.value) : "";
    }

    @Override // jxl.Cell
    public CellType getType() {
        return CellType.NUMBER_FORMULA;
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

    @Override // jxl.NumberCell
    public NumberFormat getNumberFormat() {
        return this.format;
    }
}
