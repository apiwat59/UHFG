package jxl.write.biff;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import jxl.CellType;
import jxl.DateCell;
import jxl.biff.DoubleHelper;
import jxl.biff.Type;
import jxl.common.Logger;
import jxl.format.CellFormat;
import jxl.write.DateFormats;
import jxl.write.WritableCellFormat;

/* loaded from: classes.dex */
public abstract class DateRecord extends CellValue {
    private static final long msInADay = 86400000;
    private static final int nonLeapDay = 61;
    private static final int utcOffsetDays = 25569;
    private Date date;
    private boolean time;
    private double value;
    private static Logger logger = Logger.getLogger(DateRecord.class);
    static final WritableCellFormat defaultDateFormat = new WritableCellFormat(DateFormats.DEFAULT);

    /* JADX INFO: Access modifiers changed from: protected */
    public static final class GMTDate {
    }

    protected DateRecord(int c, int r, Date d) {
        this(c, r, d, (CellFormat) defaultDateFormat, false);
    }

    protected DateRecord(int c, int r, Date d, GMTDate a) {
        this(c, r, d, (CellFormat) defaultDateFormat, false);
    }

    protected DateRecord(int c, int r, Date d, CellFormat st) {
        super(Type.NUMBER, c, r, st);
        this.date = d;
        calculateValue(true);
    }

    protected DateRecord(int c, int r, Date d, CellFormat st, GMTDate a) {
        super(Type.NUMBER, c, r, st);
        this.date = d;
        calculateValue(false);
    }

    protected DateRecord(int c, int r, Date d, CellFormat st, boolean tim) {
        super(Type.NUMBER, c, r, st);
        this.date = d;
        this.time = tim;
        calculateValue(false);
    }

    protected DateRecord(DateCell dc) {
        super(Type.NUMBER, dc);
        this.date = dc.getDate();
        this.time = dc.isTime();
        calculateValue(false);
    }

    protected DateRecord(int c, int r, DateRecord dr) {
        super(Type.NUMBER, c, r, dr);
        this.value = dr.value;
        this.time = dr.time;
        this.date = dr.date;
    }

    private void calculateValue(boolean adjust) {
        long zoneOffset = 0;
        long dstOffset = 0;
        if (adjust) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(this.date);
            zoneOffset = cal.get(15);
            dstOffset = cal.get(16);
        }
        long utcValue = this.date.getTime() + zoneOffset + dstOffset;
        double d = utcValue;
        Double.isNaN(d);
        double utcDays = d / 8.64E7d;
        double d2 = 25569.0d + utcDays;
        this.value = d2;
        boolean z = this.time;
        if (!z && d2 < 61.0d) {
            this.value = d2 - 1.0d;
        }
        if (z) {
            double d3 = this.value;
            double d4 = (int) d3;
            Double.isNaN(d4);
            this.value = d3 - d4;
        }
    }

    @Override // jxl.Cell
    public CellType getType() {
        return CellType.DATE;
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
        return this.date.toString();
    }

    protected void setDate(Date d) {
        this.date = d;
        calculateValue(true);
    }

    protected void setDate(Date d, GMTDate a) {
        this.date = d;
        calculateValue(false);
    }

    public Date getDate() {
        return this.date;
    }

    public boolean isTime() {
        return this.time;
    }

    public DateFormat getDateFormat() {
        return null;
    }
}
