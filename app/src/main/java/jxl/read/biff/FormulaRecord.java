package jxl.read.biff;

import jxl.CellType;
import jxl.WorkbookSettings;
import jxl.biff.DoubleHelper;
import jxl.biff.FormattingRecords;
import jxl.biff.IntegerHelper;
import jxl.biff.WorkbookMethods;
import jxl.biff.formula.ExternalSheet;
import jxl.common.Assert;
import jxl.common.Logger;

/* loaded from: classes.dex */
class FormulaRecord extends CellValue {
    private CellValue formula;
    private boolean shared;
    private static Logger logger = Logger.getLogger(FormulaRecord.class);
    public static final IgnoreSharedFormula ignoreSharedFormula = new IgnoreSharedFormula();

    /* JADX INFO: Access modifiers changed from: private */
    static class IgnoreSharedFormula {
        private IgnoreSharedFormula() {
        }
    }

    public FormulaRecord(Record t, File excelFile, FormattingRecords fr, ExternalSheet es, WorkbookMethods nt, SheetImpl si, WorkbookSettings ws) {
        super(t, fr, si);
        byte[] data = getRecord().getData();
        this.shared = false;
        int grbit = IntegerHelper.getInt(data[14], data[15]);
        if ((grbit & 8) != 0) {
            this.shared = true;
            if (data[6] == 0 && data[12] == -1 && data[13] == -1) {
                this.formula = new SharedStringFormulaRecord(t, excelFile, fr, es, nt, si, ws);
                return;
            }
            if (data[6] == 3 && data[12] == -1 && data[13] == -1) {
                this.formula = new SharedStringFormulaRecord(t, excelFile, fr, es, nt, si, SharedStringFormulaRecord.EMPTY_STRING);
                return;
            }
            if (data[6] == 2 && data[12] == -1 && data[13] == -1) {
                int errorCode = data[8];
                this.formula = new SharedErrorFormulaRecord(t, excelFile, errorCode, fr, es, nt, si);
                return;
            } else if (data[6] == 1 && data[12] == -1 && data[13] == -1) {
                boolean value = data[8] == 1;
                this.formula = new SharedBooleanFormulaRecord(t, excelFile, value, fr, es, nt, si);
                return;
            } else {
                double value2 = DoubleHelper.getIEEEDouble(data, 6);
                SharedNumberFormulaRecord snfr = new SharedNumberFormulaRecord(t, excelFile, value2, fr, es, nt, si);
                snfr.setNumberFormat(fr.getNumberFormat(getXFIndex()));
                this.formula = snfr;
                return;
            }
        }
        if (data[6] == 0 && data[12] == -1 && data[13] == -1) {
            this.formula = new StringFormulaRecord(t, excelFile, fr, es, nt, si, ws);
            return;
        }
        if (data[6] == 1 && data[12] == -1 && data[13] == -1) {
            this.formula = new BooleanFormulaRecord(t, fr, es, nt, si);
            return;
        }
        if (data[6] == 2 && data[12] == -1 && data[13] == -1) {
            this.formula = new ErrorFormulaRecord(t, fr, es, nt, si);
        } else if (data[6] != 3 || data[12] != -1 || data[13] != -1) {
            this.formula = new NumberFormulaRecord(t, fr, es, nt, si);
        } else {
            this.formula = new StringFormulaRecord(t, fr, es, nt, si);
        }
    }

    public FormulaRecord(Record t, File excelFile, FormattingRecords fr, ExternalSheet es, WorkbookMethods nt, IgnoreSharedFormula i, SheetImpl si, WorkbookSettings ws) {
        super(t, fr, si);
        byte[] data = getRecord().getData();
        this.shared = false;
        if (data[6] == 0 && data[12] == -1 && data[13] == -1) {
            this.formula = new StringFormulaRecord(t, excelFile, fr, es, nt, si, ws);
            return;
        }
        if (data[6] == 1 && data[12] == -1 && data[13] == -1) {
            this.formula = new BooleanFormulaRecord(t, fr, es, nt, si);
        } else if (data[6] != 2 || data[12] != -1 || data[13] != -1) {
            this.formula = new NumberFormulaRecord(t, fr, es, nt, si);
        } else {
            this.formula = new ErrorFormulaRecord(t, fr, es, nt, si);
        }
    }

    @Override // jxl.Cell
    public String getContents() {
        Assert.verify(false);
        return "";
    }

    @Override // jxl.Cell
    public CellType getType() {
        Assert.verify(false);
        return CellType.EMPTY;
    }

    final CellValue getFormula() {
        return this.formula;
    }

    final boolean isShared() {
        return this.shared;
    }
}
