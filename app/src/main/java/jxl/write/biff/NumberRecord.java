package jxl.write.biff;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import jxl.CellType;
import jxl.NumberCell;
import jxl.biff.DoubleHelper;
import jxl.biff.Type;
import jxl.biff.XFRecord;
import jxl.format.CellFormat;

/* loaded from: classes.dex */
public abstract class NumberRecord extends CellValue {
    private static DecimalFormat defaultFormat = new DecimalFormat("#.###");
    private NumberFormat format;
    private double value;

    protected NumberRecord(int c, int r, double val) {
        super(Type.NUMBER, c, r);
        this.value = val;
    }

    protected NumberRecord(int c, int r, double val, CellFormat st) {
        super(Type.NUMBER, c, r, st);
        this.value = val;
    }

    protected NumberRecord(NumberCell nc) {
        super(Type.NUMBER, nc);
        this.value = nc.getValue();
    }

    protected NumberRecord(int c, int r, NumberRecord nr) {
        super(Type.NUMBER, c, r, nr);
        this.value = nr.value;
    }

    @Override // jxl.Cell
    public CellType getType() {
        return CellType.NUMBER;
    }

    @Override // jxl.write.biff.CellValue, jxl.biff.WritableRecordData
    public byte[] getData() {
        byte[] celldata = super.getData();
        byte[] data = new byte[celldata.length + 8];
        System.arraycopy(celldata, 0, data, 0, celldata.length);
        DoubleHelper.getIEEEBytes(this.value, data, celldata.length);
        return data;
    }

    @Override // jxl.Cell
    public String getContents() {
        if (this.format == null) {
            NumberFormat numberFormat = ((XFRecord) getCellFormat()).getNumberFormat();
            this.format = numberFormat;
            if (numberFormat == null) {
                this.format = defaultFormat;
            }
        }
        return this.format.format(this.value);
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double val) {
        this.value = val;
    }

    public NumberFormat getNumberFormat() {
        return null;
    }
}
