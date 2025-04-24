package jxl.biff;

import androidx.core.internal.view.SupportMenu;
import jxl.common.Logger;

/* loaded from: classes.dex */
public class CountryCode {
    private String code;
    private String description;
    private int value;
    private static Logger logger = Logger.getLogger(CountryCode.class);
    private static CountryCode[] codes = new CountryCode[0];
    public static final CountryCode USA = new CountryCode(1, "US", "USA");
    public static final CountryCode CANADA = new CountryCode(2, "CA", "Canada");
    public static final CountryCode GREECE = new CountryCode(30, "GR", "Greece");
    public static final CountryCode NETHERLANDS = new CountryCode(31, "NE", "Netherlands");
    public static final CountryCode BELGIUM = new CountryCode(32, "BE", "Belgium");
    public static final CountryCode FRANCE = new CountryCode(33, "FR", "France");
    public static final CountryCode SPAIN = new CountryCode(34, "ES", "Spain");
    public static final CountryCode ITALY = new CountryCode(39, "IT", "Italy");
    public static final CountryCode SWITZERLAND = new CountryCode(41, "CH", "Switzerland");
    public static final CountryCode UK = new CountryCode(44, "UK", "United Kingdowm");
    public static final CountryCode DENMARK = new CountryCode(45, "DK", "Denmark");
    public static final CountryCode SWEDEN = new CountryCode(46, "SE", "Sweden");
    public static final CountryCode NORWAY = new CountryCode(47, "NO", "Norway");
    public static final CountryCode GERMANY = new CountryCode(49, "DE", "Germany");
    public static final CountryCode PHILIPPINES = new CountryCode(63, "PH", "Philippines");
    public static final CountryCode CHINA = new CountryCode(86, "CN", "China");
    public static final CountryCode INDIA = new CountryCode(91, "IN", "India");
    public static final CountryCode UNKNOWN = new CountryCode(SupportMenu.USER_MASK, "??", "Unknown");

    private CountryCode(int v, String c, String d) {
        this.value = v;
        this.code = c;
        this.description = d;
        CountryCode[] countryCodeArr = codes;
        CountryCode[] newcodes = new CountryCode[countryCodeArr.length + 1];
        System.arraycopy(countryCodeArr, 0, newcodes, 0, countryCodeArr.length);
        newcodes[codes.length] = this;
        codes = newcodes;
    }

    private CountryCode(int v) {
        this.value = v;
        this.description = "Arbitrary";
        this.code = "??";
    }

    public int getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static CountryCode getCountryCode(String s) {
        if (s == null || s.length() != 2) {
            logger.warn("Please specify two character ISO 3166 country code");
            return USA;
        }
        CountryCode code = UNKNOWN;
        int i = 0;
        while (true) {
            CountryCode[] countryCodeArr = codes;
            if (i >= countryCodeArr.length || code != UNKNOWN) {
                break;
            }
            if (countryCodeArr[i].code.equals(s)) {
                code = codes[i];
            }
            i++;
        }
        return code;
    }

    public static CountryCode createArbitraryCode(int i) {
        return new CountryCode(i);
    }
}
