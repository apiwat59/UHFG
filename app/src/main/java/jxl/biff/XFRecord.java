package jxl.biff;

import androidx.core.internal.view.SupportMenu;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import jxl.WorkbookSettings;
import jxl.common.Assert;
import jxl.common.Logger;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.format.Font;
import jxl.format.Format;
import jxl.format.Orientation;
import jxl.format.Pattern;
import jxl.format.VerticalAlignment;
import jxl.read.biff.Record;
import kotlin.jvm.internal.ByteCompanionObject;
import kotlinx.coroutines.scheduling.WorkQueueKt;

/* loaded from: classes.dex */
public class XFRecord extends WritableRecordData implements CellFormat {
    private static final int USE_ALIGNMENT = 16;
    private static final int USE_BACKGROUND = 64;
    private static final int USE_BORDER = 32;
    private static final int USE_DEFAULT_VALUE = 248;
    private static final int USE_FONT = 4;
    private static final int USE_FORMAT = 8;
    private static final int USE_PROTECTION = 128;
    public static final BiffType biff7;
    public static final BiffType biff8;
    protected static final XFType cell;
    protected static final XFType style;
    private Alignment align;
    private Colour backgroundColour;
    private BiffType biffType;
    private BorderLineStyle bottomBorder;
    private Colour bottomBorderColour;
    private boolean copied;
    private boolean date;
    private DateFormat dateFormat;
    private Format excelFormat;
    private FontRecord font;
    private int fontIndex;
    private DisplayFormat format;
    public int formatIndex;
    private boolean formatInfoInitialized;
    private FormattingRecords formattingRecords;
    private boolean hidden;
    private int indentation;
    private boolean initialized;
    private BorderLineStyle leftBorder;
    private Colour leftBorderColour;
    private boolean locked;
    private boolean number;
    private NumberFormat numberFormat;
    private int options;
    private Orientation orientation;
    private int parentFormat;
    private Pattern pattern;
    private boolean read;
    private BorderLineStyle rightBorder;
    private Colour rightBorderColour;
    private boolean shrinkToFit;
    private BorderLineStyle topBorder;
    private Colour topBorderColour;
    private byte usedAttributes;
    private VerticalAlignment valign;
    private boolean wrap;
    private XFType xfFormatType;
    private int xfIndex;
    private static Logger logger = Logger.getLogger(XFRecord.class);
    private static final int[] dateFormats = {14, 15, 16, 17, 18, 19, 20, 21, 22, 45, 46, 47};
    private static final DateFormat[] javaDateFormats = {SimpleDateFormat.getDateInstance(3), SimpleDateFormat.getDateInstance(2), new SimpleDateFormat("d-MMM"), new SimpleDateFormat("MMM-yy"), new SimpleDateFormat("h:mm a"), new SimpleDateFormat("h:mm:ss a"), new SimpleDateFormat("H:mm"), new SimpleDateFormat("H:mm:ss"), new SimpleDateFormat("M/d/yy H:mm"), new SimpleDateFormat("mm:ss"), new SimpleDateFormat("H:mm:ss"), new SimpleDateFormat("mm:ss.S")};
    private static int[] numberFormats = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 37, 38, 39, 40, 41, 42, 43, 44, 48};
    private static NumberFormat[] javaNumberFormats = {new DecimalFormat("0"), new DecimalFormat("0.00"), new DecimalFormat("#,##0"), new DecimalFormat("#,##0.00"), new DecimalFormat("$#,##0;($#,##0)"), new DecimalFormat("$#,##0;($#,##0)"), new DecimalFormat("$#,##0.00;($#,##0.00)"), new DecimalFormat("$#,##0.00;($#,##0.00)"), new DecimalFormat("0%"), new DecimalFormat("0.00%"), new DecimalFormat("0.00E00"), new DecimalFormat("#,##0;(#,##0)"), new DecimalFormat("#,##0;(#,##0)"), new DecimalFormat("#,##0.00;(#,##0.00)"), new DecimalFormat("#,##0.00;(#,##0.00)"), new DecimalFormat("#,##0;(#,##0)"), new DecimalFormat("$#,##0;($#,##0)"), new DecimalFormat("#,##0.00;(#,##0.00)"), new DecimalFormat("$#,##0.00;($#,##0.00)"), new DecimalFormat("##0.0E0")};

    static {
        biff8 = new BiffType();
        biff7 = new BiffType();
        cell = new XFType();
        style = new XFType();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static class BiffType {
        private BiffType() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    protected static class XFType {
        private XFType() {
        }
    }

    public XFRecord(Record t, WorkbookSettings ws, BiffType bt) {
        super(t);
        this.biffType = bt;
        byte[] data = getRecord().getData();
        this.fontIndex = IntegerHelper.getInt(data[0], data[1]);
        this.formatIndex = IntegerHelper.getInt(data[2], data[3]);
        this.date = false;
        this.number = false;
        int i = 0;
        while (true) {
            int[] iArr = dateFormats;
            if (i >= iArr.length || this.date) {
                break;
            }
            if (this.formatIndex == iArr[i]) {
                this.date = true;
                this.dateFormat = javaDateFormats[i];
            }
            i++;
        }
        int i2 = 0;
        while (true) {
            int[] iArr2 = numberFormats;
            if (i2 >= iArr2.length || this.number) {
                break;
            }
            if (this.formatIndex == iArr2[i2]) {
                this.number = true;
                DecimalFormat df = (DecimalFormat) javaNumberFormats[i2].clone();
                DecimalFormatSymbols symbols = new DecimalFormatSymbols(ws.getLocale());
                df.setDecimalFormatSymbols(symbols);
                this.numberFormat = df;
            }
            i2++;
        }
        int cellAttributes = IntegerHelper.getInt(data[4], data[5]);
        int i3 = (65520 & cellAttributes) >> 4;
        this.parentFormat = i3;
        int formatType = cellAttributes & 4;
        XFType xFType = formatType == 0 ? cell : style;
        this.xfFormatType = xFType;
        this.locked = (cellAttributes & 1) != 0;
        this.hidden = (cellAttributes & 2) != 0;
        if (xFType == cell && (i3 & 4095) == 4095) {
            this.parentFormat = 0;
            logger.warn("Invalid parent format found - ignoring");
        }
        this.initialized = false;
        this.read = true;
        this.formatInfoInitialized = false;
        this.copied = false;
    }

    public XFRecord(FontRecord fnt, DisplayFormat form) {
        super(Type.XF);
        this.initialized = false;
        this.locked = true;
        this.hidden = false;
        this.align = Alignment.GENERAL;
        this.valign = VerticalAlignment.BOTTOM;
        this.orientation = Orientation.HORIZONTAL;
        this.wrap = false;
        this.leftBorder = BorderLineStyle.NONE;
        this.rightBorder = BorderLineStyle.NONE;
        this.topBorder = BorderLineStyle.NONE;
        this.bottomBorder = BorderLineStyle.NONE;
        this.leftBorderColour = Colour.AUTOMATIC;
        this.rightBorderColour = Colour.AUTOMATIC;
        this.topBorderColour = Colour.AUTOMATIC;
        this.bottomBorderColour = Colour.AUTOMATIC;
        this.pattern = Pattern.NONE;
        this.backgroundColour = Colour.DEFAULT_BACKGROUND;
        this.indentation = 0;
        this.shrinkToFit = false;
        this.usedAttributes = (byte) 124;
        this.parentFormat = 0;
        this.xfFormatType = null;
        this.font = fnt;
        this.format = form;
        this.biffType = biff8;
        this.read = false;
        this.copied = false;
        this.formatInfoInitialized = true;
        Assert.verify(fnt != null);
        Assert.verify(this.format != null);
    }

    protected XFRecord(XFRecord fmt) {
        super(Type.XF);
        this.initialized = false;
        this.locked = fmt.locked;
        this.hidden = fmt.hidden;
        this.align = fmt.align;
        this.valign = fmt.valign;
        this.orientation = fmt.orientation;
        this.wrap = fmt.wrap;
        this.leftBorder = fmt.leftBorder;
        this.rightBorder = fmt.rightBorder;
        this.topBorder = fmt.topBorder;
        this.bottomBorder = fmt.bottomBorder;
        this.leftBorderColour = fmt.leftBorderColour;
        this.rightBorderColour = fmt.rightBorderColour;
        this.topBorderColour = fmt.topBorderColour;
        this.bottomBorderColour = fmt.bottomBorderColour;
        this.pattern = fmt.pattern;
        this.xfFormatType = fmt.xfFormatType;
        this.indentation = fmt.indentation;
        this.shrinkToFit = fmt.shrinkToFit;
        this.parentFormat = fmt.parentFormat;
        this.backgroundColour = fmt.backgroundColour;
        this.font = fmt.font;
        this.format = fmt.format;
        this.fontIndex = fmt.fontIndex;
        this.formatIndex = fmt.formatIndex;
        this.formatInfoInitialized = fmt.formatInfoInitialized;
        this.biffType = biff8;
        this.read = false;
        this.copied = true;
    }

    protected XFRecord(CellFormat cellFormat) {
        super(Type.XF);
        Assert.verify(cellFormat != null);
        Assert.verify(cellFormat instanceof XFRecord);
        XFRecord fmt = (XFRecord) cellFormat;
        if (!fmt.formatInfoInitialized) {
            fmt.initializeFormatInformation();
        }
        this.locked = fmt.locked;
        this.hidden = fmt.hidden;
        this.align = fmt.align;
        this.valign = fmt.valign;
        this.orientation = fmt.orientation;
        this.wrap = fmt.wrap;
        this.leftBorder = fmt.leftBorder;
        this.rightBorder = fmt.rightBorder;
        this.topBorder = fmt.topBorder;
        this.bottomBorder = fmt.bottomBorder;
        this.leftBorderColour = fmt.leftBorderColour;
        this.rightBorderColour = fmt.rightBorderColour;
        this.topBorderColour = fmt.topBorderColour;
        this.bottomBorderColour = fmt.bottomBorderColour;
        this.pattern = fmt.pattern;
        this.xfFormatType = fmt.xfFormatType;
        this.parentFormat = fmt.parentFormat;
        this.indentation = fmt.indentation;
        this.shrinkToFit = fmt.shrinkToFit;
        this.backgroundColour = fmt.backgroundColour;
        this.font = new FontRecord(fmt.getFont());
        if (fmt.getFormat() == null) {
            if (fmt.format.isBuiltIn()) {
                this.format = fmt.format;
            } else {
                this.format = new FormatRecord((FormatRecord) fmt.format);
            }
        } else if (fmt.getFormat() instanceof BuiltInFormat) {
            this.excelFormat = (BuiltInFormat) fmt.excelFormat;
            this.format = (BuiltInFormat) fmt.excelFormat;
        } else {
            Assert.verify(fmt.formatInfoInitialized);
            Assert.verify(fmt.excelFormat instanceof FormatRecord);
            FormatRecord fr = new FormatRecord((FormatRecord) fmt.excelFormat);
            this.excelFormat = fr;
            this.format = fr;
        }
        this.biffType = biff8;
        this.formatInfoInitialized = true;
        this.read = false;
        this.copied = false;
        this.initialized = false;
    }

    public DateFormat getDateFormat() {
        return this.dateFormat;
    }

    public NumberFormat getNumberFormat() {
        return this.numberFormat;
    }

    public int getFormatRecord() {
        return this.formatIndex;
    }

    public boolean isDate() {
        return this.date;
    }

    public boolean isNumber() {
        return this.number;
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        if (!this.formatInfoInitialized) {
            initializeFormatInformation();
        }
        byte[] data = new byte[20];
        IntegerHelper.getTwoBytes(this.fontIndex, data, 0);
        IntegerHelper.getTwoBytes(this.formatIndex, data, 2);
        int cellAttributes = 0;
        if (getLocked()) {
            cellAttributes = 0 | 1;
        }
        if (getHidden()) {
            cellAttributes |= 2;
        }
        if (this.xfFormatType == style) {
            cellAttributes |= 4;
            this.parentFormat = SupportMenu.USER_MASK;
        }
        IntegerHelper.getTwoBytes(cellAttributes | (this.parentFormat << 4), data, 4);
        int alignMask = this.align.getValue();
        if (this.wrap) {
            alignMask |= 8;
        }
        IntegerHelper.getTwoBytes(alignMask | (this.valign.getValue() << 4) | (this.orientation.getValue() << 8), data, 6);
        data[9] = 16;
        int borderMask = (this.rightBorder.getValue() << 4) | this.leftBorder.getValue() | (this.topBorder.getValue() << 8) | (this.bottomBorder.getValue() << 12);
        IntegerHelper.getTwoBytes(borderMask, data, 10);
        if (borderMask != 0) {
            byte lc = (byte) this.leftBorderColour.getValue();
            byte rc = (byte) this.rightBorderColour.getValue();
            byte tc = (byte) this.topBorderColour.getValue();
            byte bc = (byte) this.bottomBorderColour.getValue();
            int sideColourMask = (lc & ByteCompanionObject.MAX_VALUE) | ((rc & ByteCompanionObject.MAX_VALUE) << 7);
            int topColourMask = (tc & ByteCompanionObject.MAX_VALUE) | ((bc & ByteCompanionObject.MAX_VALUE) << 7);
            IntegerHelper.getTwoBytes(sideColourMask, data, 12);
            IntegerHelper.getTwoBytes(topColourMask, data, 14);
        }
        int patternVal = this.pattern.getValue() << 10;
        IntegerHelper.getTwoBytes(patternVal, data, 16);
        int colourPaletteMask = this.backgroundColour.getValue();
        IntegerHelper.getTwoBytes(colourPaletteMask | 8192, data, 18);
        int i = this.options | (this.indentation & 15);
        this.options = i;
        if (this.shrinkToFit) {
            this.options = 16 | i;
        } else {
            this.options = i & 239;
        }
        data[8] = (byte) this.options;
        if (this.biffType == biff8) {
            data[9] = this.usedAttributes;
        }
        return data;
    }

    protected final boolean getLocked() {
        return this.locked;
    }

    protected final boolean getHidden() {
        return this.hidden;
    }

    protected final void setXFLocked(boolean l) {
        this.locked = l;
        this.usedAttributes = (byte) (this.usedAttributes | ByteCompanionObject.MIN_VALUE);
    }

    protected final void setXFCellOptions(int opt) {
        this.options |= opt;
    }

    protected void setXFAlignment(Alignment a) {
        Assert.verify(!this.initialized);
        this.align = a;
        this.usedAttributes = (byte) (this.usedAttributes | 16);
    }

    protected void setXFIndentation(int i) {
        Assert.verify(!this.initialized);
        this.indentation = i;
        this.usedAttributes = (byte) (this.usedAttributes | 16);
    }

    protected void setXFShrinkToFit(boolean s) {
        Assert.verify(!this.initialized);
        this.shrinkToFit = s;
        this.usedAttributes = (byte) (this.usedAttributes | 16);
    }

    @Override // jxl.format.CellFormat
    public Alignment getAlignment() {
        if (!this.formatInfoInitialized) {
            initializeFormatInformation();
        }
        return this.align;
    }

    @Override // jxl.format.CellFormat
    public int getIndentation() {
        if (!this.formatInfoInitialized) {
            initializeFormatInformation();
        }
        return this.indentation;
    }

    @Override // jxl.format.CellFormat
    public boolean isShrinkToFit() {
        if (!this.formatInfoInitialized) {
            initializeFormatInformation();
        }
        return this.shrinkToFit;
    }

    @Override // jxl.format.CellFormat
    public boolean isLocked() {
        if (!this.formatInfoInitialized) {
            initializeFormatInformation();
        }
        return this.locked;
    }

    @Override // jxl.format.CellFormat
    public VerticalAlignment getVerticalAlignment() {
        if (!this.formatInfoInitialized) {
            initializeFormatInformation();
        }
        return this.valign;
    }

    @Override // jxl.format.CellFormat
    public Orientation getOrientation() {
        if (!this.formatInfoInitialized) {
            initializeFormatInformation();
        }
        return this.orientation;
    }

    protected void setXFBackground(Colour c, Pattern p) {
        Assert.verify(!this.initialized);
        this.backgroundColour = c;
        this.pattern = p;
        this.usedAttributes = (byte) (this.usedAttributes | 64);
    }

    @Override // jxl.format.CellFormat
    public Colour getBackgroundColour() {
        if (!this.formatInfoInitialized) {
            initializeFormatInformation();
        }
        return this.backgroundColour;
    }

    @Override // jxl.format.CellFormat
    public Pattern getPattern() {
        if (!this.formatInfoInitialized) {
            initializeFormatInformation();
        }
        return this.pattern;
    }

    protected void setXFVerticalAlignment(VerticalAlignment va) {
        Assert.verify(!this.initialized);
        this.valign = va;
        this.usedAttributes = (byte) (this.usedAttributes | 16);
    }

    protected void setXFOrientation(Orientation o) {
        Assert.verify(!this.initialized);
        this.orientation = o;
        this.usedAttributes = (byte) (this.usedAttributes | 16);
    }

    protected void setXFWrap(boolean w) {
        Assert.verify(!this.initialized);
        this.wrap = w;
        this.usedAttributes = (byte) (this.usedAttributes | 16);
    }

    @Override // jxl.format.CellFormat
    public boolean getWrap() {
        if (!this.formatInfoInitialized) {
            initializeFormatInformation();
        }
        return this.wrap;
    }

    protected void setXFBorder(Border b, BorderLineStyle ls, Colour c) {
        Assert.verify(!this.initialized);
        if (c == Colour.BLACK || c == Colour.UNKNOWN) {
            c = Colour.PALETTE_BLACK;
        }
        if (b == Border.LEFT) {
            this.leftBorder = ls;
            this.leftBorderColour = c;
        } else if (b == Border.RIGHT) {
            this.rightBorder = ls;
            this.rightBorderColour = c;
        } else if (b == Border.TOP) {
            this.topBorder = ls;
            this.topBorderColour = c;
        } else if (b == Border.BOTTOM) {
            this.bottomBorder = ls;
            this.bottomBorderColour = c;
        }
        this.usedAttributes = (byte) (this.usedAttributes | 32);
    }

    @Override // jxl.format.CellFormat
    public BorderLineStyle getBorder(Border border) {
        return getBorderLine(border);
    }

    @Override // jxl.format.CellFormat
    public BorderLineStyle getBorderLine(Border border) {
        if (border == Border.NONE || border == Border.ALL) {
            return BorderLineStyle.NONE;
        }
        if (!this.formatInfoInitialized) {
            initializeFormatInformation();
        }
        if (border == Border.LEFT) {
            return this.leftBorder;
        }
        if (border == Border.RIGHT) {
            return this.rightBorder;
        }
        if (border == Border.TOP) {
            return this.topBorder;
        }
        if (border == Border.BOTTOM) {
            return this.bottomBorder;
        }
        return BorderLineStyle.NONE;
    }

    @Override // jxl.format.CellFormat
    public Colour getBorderColour(Border border) {
        if (border == Border.NONE || border == Border.ALL) {
            return Colour.PALETTE_BLACK;
        }
        if (!this.formatInfoInitialized) {
            initializeFormatInformation();
        }
        if (border == Border.LEFT) {
            return this.leftBorderColour;
        }
        if (border == Border.RIGHT) {
            return this.rightBorderColour;
        }
        if (border == Border.TOP) {
            return this.topBorderColour;
        }
        if (border == Border.BOTTOM) {
            return this.bottomBorderColour;
        }
        return Colour.BLACK;
    }

    @Override // jxl.format.CellFormat
    public final boolean hasBorders() {
        if (!this.formatInfoInitialized) {
            initializeFormatInformation();
        }
        if (this.leftBorder == BorderLineStyle.NONE && this.rightBorder == BorderLineStyle.NONE && this.topBorder == BorderLineStyle.NONE && this.bottomBorder == BorderLineStyle.NONE) {
            return false;
        }
        return true;
    }

    public final void initialize(int pos, FormattingRecords fr, Fonts fonts) throws NumFormatRecordsException {
        this.xfIndex = pos;
        this.formattingRecords = fr;
        if (this.read || this.copied) {
            this.initialized = true;
            return;
        }
        if (!this.font.isInitialized()) {
            fonts.addFont(this.font);
        }
        if (!this.format.isInitialized()) {
            fr.addFormat(this.format);
        }
        this.fontIndex = this.font.getFontIndex();
        this.formatIndex = this.format.getFormatIndex();
        this.initialized = true;
    }

    public final void uninitialize() {
        if (this.initialized) {
            logger.warn("A default format has been initialized");
        }
        this.initialized = false;
    }

    final void setXFIndex(int xfi) {
        this.xfIndex = xfi;
    }

    public final int getXFIndex() {
        return this.xfIndex;
    }

    public final boolean isInitialized() {
        return this.initialized;
    }

    public final boolean isRead() {
        return this.read;
    }

    @Override // jxl.format.CellFormat
    public Format getFormat() {
        if (!this.formatInfoInitialized) {
            initializeFormatInformation();
        }
        return this.excelFormat;
    }

    @Override // jxl.format.CellFormat
    public Font getFont() {
        if (!this.formatInfoInitialized) {
            initializeFormatInformation();
        }
        return this.font;
    }

    private void initializeFormatInformation() {
        if (this.formatIndex < BuiltInFormat.builtIns.length && BuiltInFormat.builtIns[this.formatIndex] != null) {
            this.excelFormat = BuiltInFormat.builtIns[this.formatIndex];
        } else {
            this.excelFormat = this.formattingRecords.getFormatRecord(this.formatIndex);
        }
        this.font = this.formattingRecords.getFonts().getFont(this.fontIndex);
        byte[] data = getRecord().getData();
        int cellAttributes = IntegerHelper.getInt(data[4], data[5]);
        int i = (65520 & cellAttributes) >> 4;
        this.parentFormat = i;
        int formatType = cellAttributes & 4;
        XFType xFType = formatType == 0 ? cell : style;
        this.xfFormatType = xFType;
        this.locked = (cellAttributes & 1) != 0;
        this.hidden = (cellAttributes & 2) != 0;
        if (xFType == cell && (i & 4095) == 4095) {
            this.parentFormat = 0;
            logger.warn("Invalid parent format found - ignoring");
        }
        int alignMask = IntegerHelper.getInt(data[6], data[7]);
        if ((alignMask & 8) != 0) {
            this.wrap = true;
        }
        this.align = Alignment.getAlignment(alignMask & 7);
        this.valign = VerticalAlignment.getAlignment((alignMask >> 4) & 7);
        this.orientation = Orientation.getOrientation((alignMask >> 8) & 255);
        int attr = IntegerHelper.getInt(data[8], data[9]);
        this.indentation = attr & 15;
        this.shrinkToFit = (attr & 16) != 0;
        BiffType biffType = this.biffType;
        BiffType biffType2 = biff8;
        if (biffType == biffType2) {
            this.usedAttributes = data[9];
        }
        int borderMask = IntegerHelper.getInt(data[10], data[11]);
        this.leftBorder = BorderLineStyle.getStyle(borderMask & 7);
        this.rightBorder = BorderLineStyle.getStyle((borderMask >> 4) & 7);
        this.topBorder = BorderLineStyle.getStyle((borderMask >> 8) & 7);
        this.bottomBorder = BorderLineStyle.getStyle((borderMask >> 12) & 7);
        int borderColourMask = IntegerHelper.getInt(data[12], data[13]);
        this.leftBorderColour = Colour.getInternalColour(borderColourMask & WorkQueueKt.MASK);
        this.rightBorderColour = Colour.getInternalColour((borderColourMask & 16256) >> 7);
        int borderColourMask2 = IntegerHelper.getInt(data[14], data[15]);
        this.topBorderColour = Colour.getInternalColour(borderColourMask2 & WorkQueueKt.MASK);
        this.bottomBorderColour = Colour.getInternalColour((borderColourMask2 & 16256) >> 7);
        if (this.biffType == biffType2) {
            int patternVal = IntegerHelper.getInt(data[16], data[17]);
            this.pattern = Pattern.getPattern((patternVal & 64512) >> 10);
            int colourPaletteMask = IntegerHelper.getInt(data[18], data[19]);
            Colour internalColour = Colour.getInternalColour(colourPaletteMask & 63);
            this.backgroundColour = internalColour;
            if (internalColour == Colour.UNKNOWN || this.backgroundColour == Colour.DEFAULT_BACKGROUND1) {
                this.backgroundColour = Colour.DEFAULT_BACKGROUND;
            }
        } else {
            this.pattern = Pattern.NONE;
            this.backgroundColour = Colour.DEFAULT_BACKGROUND;
        }
        this.formatInfoInitialized = true;
    }

    public int hashCode() {
        if (!this.formatInfoInitialized) {
            initializeFormatInformation();
        }
        int i = (37 * ((37 * ((37 * ((37 * 17) + (this.hidden ? 1 : 0))) + (this.locked ? 1 : 0))) + (this.wrap ? 1 : 0))) + (this.shrinkToFit ? 1 : 0);
        XFType xFType = this.xfFormatType;
        if (xFType == cell) {
            i = (37 * i) + 1;
        } else if (xFType == style) {
            i = (37 * i) + 2;
        }
        return (37 * ((37 * ((37 * ((37 * ((37 * ((37 * ((37 * ((37 * ((37 * ((37 * ((37 * ((((this.leftBorder.getDescription().hashCode() ^ ((37 * ((37 * ((37 * i) + (this.align.getValue() + 1))) + (this.valign.getValue() + 1))) + this.orientation.getValue())) ^ this.rightBorder.getDescription().hashCode()) ^ this.topBorder.getDescription().hashCode()) ^ this.bottomBorder.getDescription().hashCode())) + this.leftBorderColour.getValue())) + this.rightBorderColour.getValue())) + this.topBorderColour.getValue())) + this.bottomBorderColour.getValue())) + this.backgroundColour.getValue())) + this.pattern.getValue() + 1)) + this.usedAttributes)) + this.parentFormat)) + this.fontIndex)) + this.formatIndex)) + this.indentation;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof XFRecord)) {
            return false;
        }
        XFRecord xfr = (XFRecord) o;
        if (!this.formatInfoInitialized) {
            initializeFormatInformation();
        }
        if (!xfr.formatInfoInitialized) {
            xfr.initializeFormatInformation();
        }
        if (this.xfFormatType != xfr.xfFormatType || this.parentFormat != xfr.parentFormat || this.locked != xfr.locked || this.hidden != xfr.hidden || this.usedAttributes != xfr.usedAttributes || this.align != xfr.align || this.valign != xfr.valign || this.orientation != xfr.orientation || this.wrap != xfr.wrap || this.shrinkToFit != xfr.shrinkToFit || this.indentation != xfr.indentation || this.leftBorder != xfr.leftBorder || this.rightBorder != xfr.rightBorder || this.topBorder != xfr.topBorder || this.bottomBorder != xfr.bottomBorder || this.leftBorderColour != xfr.leftBorderColour || this.rightBorderColour != xfr.rightBorderColour || this.topBorderColour != xfr.topBorderColour || this.bottomBorderColour != xfr.bottomBorderColour || this.backgroundColour != xfr.backgroundColour || this.pattern != xfr.pattern) {
            return false;
        }
        if (this.initialized && xfr.initialized) {
            if (this.fontIndex != xfr.fontIndex || this.formatIndex != xfr.formatIndex) {
                return false;
            }
        } else if (!this.font.equals(xfr.font) || !this.format.equals(xfr.format)) {
            return false;
        }
        return true;
    }

    void setFormatIndex(int newindex) {
        this.formatIndex = newindex;
    }

    public int getFontIndex() {
        return this.fontIndex;
    }

    void setFontIndex(int newindex) {
        this.fontIndex = newindex;
    }

    protected void setXFDetails(XFType t, int pf) {
        this.xfFormatType = t;
        this.parentFormat = pf;
    }

    void rationalize(IndexMapping xfMapping) {
        this.xfIndex = xfMapping.getNewIndex(this.xfIndex);
        if (this.xfFormatType == cell) {
            this.parentFormat = xfMapping.getNewIndex(this.parentFormat);
        }
    }

    public void setFont(FontRecord f) {
        this.font = f;
    }
}
