package jxl.read.biff;

import java.util.ArrayList;
import jxl.Cell;
import jxl.CellType;
import jxl.biff.FormattingRecords;
import jxl.biff.IntegerHelper;
import jxl.biff.WorkbookMethods;
import jxl.biff.formula.ExternalSheet;
import jxl.common.Logger;

/* loaded from: classes.dex */
class SharedFormulaRecord {
    private static Logger logger = Logger.getLogger(SharedFormulaRecord.class);
    private ExternalSheet externalSheet;
    private int firstCol;
    private int firstRow;
    private ArrayList formulas;
    private int lastCol;
    private int lastRow;
    private SheetImpl sheet;
    private BaseSharedFormulaRecord templateFormula;
    private byte[] tokens;

    public SharedFormulaRecord(Record t, BaseSharedFormulaRecord fr, ExternalSheet es, WorkbookMethods nt, SheetImpl si) {
        this.sheet = si;
        byte[] data = t.getData();
        this.firstRow = IntegerHelper.getInt(data[0], data[1]);
        this.lastRow = IntegerHelper.getInt(data[2], data[3]);
        this.firstCol = data[4] & 255;
        this.lastCol = data[5] & 255;
        this.formulas = new ArrayList();
        this.templateFormula = fr;
        byte[] bArr = new byte[data.length - 10];
        this.tokens = bArr;
        System.arraycopy(data, 10, bArr, 0, bArr.length);
    }

    public boolean add(BaseSharedFormulaRecord fr) {
        int c;
        int r = fr.getRow();
        if (r < this.firstRow || r > this.lastRow || (c = fr.getColumn()) < this.firstCol || c > this.lastCol) {
            return false;
        }
        this.formulas.add(fr);
        return true;
    }

    Cell[] getFormulas(FormattingRecords fr, boolean nf) {
        Cell[] sfs = new Cell[this.formulas.size() + 1];
        BaseSharedFormulaRecord baseSharedFormulaRecord = this.templateFormula;
        if (baseSharedFormulaRecord == null) {
            logger.warn("Shared formula template formula is null");
            return new Cell[0];
        }
        baseSharedFormulaRecord.setTokens(this.tokens);
        if (this.templateFormula.getType() == CellType.NUMBER_FORMULA) {
            SharedNumberFormulaRecord snfr = (SharedNumberFormulaRecord) this.templateFormula;
            snfr.getNumberFormat();
            if (fr.isDate(this.templateFormula.getXFIndex())) {
                SharedDateFormulaRecord sharedDateFormulaRecord = new SharedDateFormulaRecord(snfr, fr, nf, this.sheet, snfr.getFilePos());
                this.templateFormula = sharedDateFormulaRecord;
                sharedDateFormulaRecord.setTokens(snfr.getTokens());
            }
        }
        sfs[0] = this.templateFormula;
        for (int i = 0; i < this.formulas.size(); i++) {
            BaseSharedFormulaRecord f = (BaseSharedFormulaRecord) this.formulas.get(i);
            if (f.getType() == CellType.NUMBER_FORMULA) {
                SharedNumberFormulaRecord snfr2 = (SharedNumberFormulaRecord) f;
                if (fr.isDate(f.getXFIndex())) {
                    f = new SharedDateFormulaRecord(snfr2, fr, nf, this.sheet, snfr2.getFilePos());
                }
            }
            f.setTokens(this.tokens);
            sfs[i + 1] = f;
        }
        return sfs;
    }

    BaseSharedFormulaRecord getTemplateFormula() {
        return this.templateFormula;
    }
}
