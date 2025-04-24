package jxl.biff.formula;

import org.apache.log4j.spi.LocationInfo;

/* loaded from: classes.dex */
public class FormulaErrorCode {
    private String description;
    private int errorCode;
    private static FormulaErrorCode[] codes = new FormulaErrorCode[0];
    public static final FormulaErrorCode UNKNOWN = new FormulaErrorCode(255, LocationInfo.NA);
    public static final FormulaErrorCode NULL = new FormulaErrorCode(0, "#NULL!");
    public static final FormulaErrorCode DIV0 = new FormulaErrorCode(7, "#DIV/0!");
    public static final FormulaErrorCode VALUE = new FormulaErrorCode(15, "#VALUE!");
    public static final FormulaErrorCode REF = new FormulaErrorCode(23, "#REF!");
    public static final FormulaErrorCode NAME = new FormulaErrorCode(29, "#NAME?");
    public static final FormulaErrorCode NUM = new FormulaErrorCode(36, "#NUM!");
    public static final FormulaErrorCode NA = new FormulaErrorCode(42, "#N/A!");

    FormulaErrorCode(int code, String desc) {
        this.errorCode = code;
        this.description = desc;
        FormulaErrorCode[] formulaErrorCodeArr = codes;
        FormulaErrorCode[] newcodes = new FormulaErrorCode[formulaErrorCodeArr.length + 1];
        System.arraycopy(formulaErrorCodeArr, 0, newcodes, 0, formulaErrorCodeArr.length);
        newcodes[codes.length] = this;
        codes = newcodes;
    }

    public int getCode() {
        return this.errorCode;
    }

    public String getDescription() {
        return this.description;
    }

    public static FormulaErrorCode getErrorCode(int code) {
        boolean found = false;
        FormulaErrorCode ec = UNKNOWN;
        int i = 0;
        while (true) {
            FormulaErrorCode[] formulaErrorCodeArr = codes;
            if (i >= formulaErrorCodeArr.length || found) {
                break;
            }
            if (formulaErrorCodeArr[i].errorCode == code) {
                found = true;
                ec = formulaErrorCodeArr[i];
            }
            i++;
        }
        return ec;
    }

    public static FormulaErrorCode getErrorCode(String code) {
        boolean found = false;
        FormulaErrorCode ec = UNKNOWN;
        if (code == null || code.length() == 0) {
            return ec;
        }
        int i = 0;
        while (true) {
            FormulaErrorCode[] formulaErrorCodeArr = codes;
            if (i >= formulaErrorCodeArr.length || found) {
                break;
            }
            if (formulaErrorCodeArr[i].description.equals(code)) {
                found = true;
                ec = codes[i];
            }
            i++;
        }
        return ec;
    }
}
