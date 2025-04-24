package jxl.write;

import java.text.DecimalFormat;
import jxl.biff.DisplayFormat;
import jxl.write.biff.NumberFormatRecord;

/* loaded from: classes.dex */
public class NumberFormat extends NumberFormatRecord implements DisplayFormat {
    public static final NumberFormatRecord.NonValidatingFormat COMPLEX_FORMAT = new NumberFormatRecord.NonValidatingFormat();
    public static final String CURRENCY_DOLLAR = "[$$-409]";
    public static final String CURRENCY_EURO_PREFIX = "[$�-2]";
    public static final String CURRENCY_EURO_SUFFIX = "[$�-1]";
    public static final String CURRENCY_JAPANESE_YEN = "[$�-411]";
    public static final String CURRENCY_POUND = "�";
    public static final String FRACTIONS_EIGHTHS = "?/8";
    public static final String FRACTION_HALVES = "?/2";
    public static final String FRACTION_HUNDREDTHS = "?/100";
    public static final String FRACTION_QUARTERS = "?/4";
    public static final String FRACTION_SIXTEENTHS = "?/16";
    public static final String FRACTION_TENTHS = "?/10";
    public static final String FRACTION_THREE_DIGITS = "???/???";

    public NumberFormat(String format) {
        super(format);
        new DecimalFormat(format);
    }

    public NumberFormat(String format, NumberFormatRecord.NonValidatingFormat dummy) {
        super(format, dummy);
    }
}
