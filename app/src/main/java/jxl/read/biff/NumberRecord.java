package jxl.read.biff;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import jxl.CellType;
import jxl.NumberCell;
import jxl.biff.DoubleHelper;
import jxl.biff.FormattingRecords;
import jxl.common.Logger;

/* loaded from: classes.dex */
class NumberRecord extends CellValue implements NumberCell {
    private NumberFormat format;
    private double value;
    private static Logger logger = Logger.getLogger(NumberRecord.class);
    private static DecimalFormat defaultFormat = new DecimalFormat("#.###");

    public NumberRecord(Record t, FormattingRecords fr, SheetImpl si) {
        super(t, fr, si);
        byte[] data = getRecord().getData();
        this.value = DoubleHelper.getIEEEDouble(data, 6);
        NumberFormat numberFormat = fr.getNumberFormat(getXFIndex());
        this.format = numberFormat;
        if (numberFormat == null) {
            this.format = defaultFormat;
        }
    }

    @Override // jxl.NumberCell
    public double getValue() {
        return this.value;
    }

    @Override // jxl.Cell
    public String getContents() {
        return this.format.format(this.value);
    }

    @Override // jxl.Cell
    public CellType getType() {
        return CellType.NUMBER;
    }

    @Override // jxl.NumberCell
    public NumberFormat getNumberFormat() {
        return this.format;
    }
}
