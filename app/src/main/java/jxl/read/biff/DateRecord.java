package jxl.read.biff;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import jxl.CellFeatures;
import jxl.CellType;
import jxl.DateCell;
import jxl.NumberCell;
import jxl.biff.FormattingRecords;
import jxl.common.Assert;
import jxl.common.Logger;
import jxl.format.CellFormat;

/* loaded from: classes.dex */
class DateRecord implements DateCell, CellFeaturesAccessor {
    private static final long msInADay = 86400000;
    private static final long msInASecond = 1000;
    private static final int nonLeapDay = 61;
    private static final long secondsInADay = 86400;
    private static final int utcOffsetDays = 25569;
    private static final int utcOffsetDays1904 = 24107;
    private CellFormat cellFormat;
    private int column;
    private Date date;
    private CellFeatures features;
    private DateFormat format;
    private FormattingRecords formattingRecords;
    private boolean initialized = false;
    private int row;
    private SheetImpl sheet;
    private boolean time;
    private int xfIndex;
    private static Logger logger = Logger.getLogger(DateRecord.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private static final TimeZone gmtZone = TimeZone.getTimeZone("GMT");

    public DateRecord(NumberCell num, int xfi, FormattingRecords fr, boolean nf, SheetImpl si) {
        this.row = num.getRow();
        this.column = num.getColumn();
        this.xfIndex = xfi;
        this.formattingRecords = fr;
        this.sheet = si;
        this.format = fr.getDateFormat(xfi);
        double numValue = num.getValue();
        if (Math.abs(numValue) < 1.0d) {
            if (this.format == null) {
                this.format = timeFormat;
            }
            this.time = true;
        } else {
            if (this.format == null) {
                this.format = dateFormat;
            }
            this.time = false;
        }
        if (!nf && !this.time && numValue < 61.0d) {
            numValue += 1.0d;
        }
        this.format.setTimeZone(gmtZone);
        int offsetDays = nf ? utcOffsetDays1904 : utcOffsetDays;
        double d = offsetDays;
        Double.isNaN(d);
        double utcDays = numValue - d;
        long utcValue = Math.round(86400.0d * utcDays) * msInASecond;
        this.date = new Date(utcValue);
    }

    @Override // jxl.Cell
    public final int getRow() {
        return this.row;
    }

    @Override // jxl.Cell
    public final int getColumn() {
        return this.column;
    }

    @Override // jxl.DateCell
    public Date getDate() {
        return this.date;
    }

    @Override // jxl.Cell
    public String getContents() {
        return this.format.format(this.date);
    }

    @Override // jxl.Cell
    public CellType getType() {
        return CellType.DATE;
    }

    @Override // jxl.DateCell
    public boolean isTime() {
        return this.time;
    }

    @Override // jxl.DateCell
    public DateFormat getDateFormat() {
        Assert.verify(this.format != null);
        return this.format;
    }

    @Override // jxl.Cell
    public CellFormat getCellFormat() {
        if (!this.initialized) {
            this.cellFormat = this.formattingRecords.getXFRecord(this.xfIndex);
            this.initialized = true;
        }
        return this.cellFormat;
    }

    @Override // jxl.Cell
    public boolean isHidden() {
        ColumnInfoRecord cir = this.sheet.getColumnInfo(this.column);
        if (cir != null && cir.getWidth() == 0) {
            return true;
        }
        RowRecord rr = this.sheet.getRowInfo(this.row);
        if (rr == null) {
            return false;
        }
        if (rr.getRowHeight() == 0 || rr.isCollapsed()) {
            return true;
        }
        return false;
    }

    protected final SheetImpl getSheet() {
        return this.sheet;
    }

    @Override // jxl.Cell
    public CellFeatures getCellFeatures() {
        return this.features;
    }

    @Override // jxl.read.biff.CellFeaturesAccessor
    public void setCellFeatures(CellFeatures cf) {
        this.features = cf;
    }
}
