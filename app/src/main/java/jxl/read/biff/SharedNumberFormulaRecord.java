package jxl.read.biff;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import jxl.CellType;
import jxl.NumberCell;
import jxl.NumberFormulaCell;
import jxl.biff.DoubleHelper;
import jxl.biff.FormattingRecords;
import jxl.biff.FormulaData;
import jxl.biff.IntegerHelper;
import jxl.biff.WorkbookMethods;
import jxl.biff.formula.ExternalSheet;
import jxl.biff.formula.FormulaException;
import jxl.biff.formula.FormulaParser;
import jxl.common.Logger;

/* loaded from: classes.dex */
public class SharedNumberFormulaRecord extends BaseSharedFormulaRecord implements NumberCell, FormulaData, NumberFormulaCell {
    private NumberFormat format;
    private FormattingRecords formattingRecords;
    private double value;
    private static Logger logger = Logger.getLogger(SharedNumberFormulaRecord.class);
    private static DecimalFormat defaultFormat = new DecimalFormat("#.###");

    public SharedNumberFormulaRecord(Record t, File excelFile, double v, FormattingRecords fr, ExternalSheet es, WorkbookMethods nt, SheetImpl si) {
        super(t, fr, es, nt, si, excelFile.getPos());
        this.value = v;
        this.format = defaultFormat;
    }

    final void setNumberFormat(NumberFormat f) {
        if (f != null) {
            this.format = f;
        }
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
        FormulaParser fp = new FormulaParser(getTokens(), this, getExternalSheet(), getNameTable(), getSheet().getWorkbook().getSettings());
        fp.parse();
        byte[] rpnTokens = fp.getBytes();
        byte[] data = new byte[rpnTokens.length + 22];
        IntegerHelper.getTwoBytes(getRow(), data, 0);
        IntegerHelper.getTwoBytes(getColumn(), data, 2);
        IntegerHelper.getTwoBytes(getXFIndex(), data, 4);
        DoubleHelper.getIEEEBytes(this.value, data, 6);
        System.arraycopy(rpnTokens, 0, data, 22, rpnTokens.length);
        IntegerHelper.getTwoBytes(rpnTokens.length, data, 20);
        byte[] d = new byte[data.length - 6];
        System.arraycopy(data, 6, d, 0, data.length - 6);
        return d;
    }

    @Override // jxl.NumberCell
    public NumberFormat getNumberFormat() {
        return this.format;
    }
}
