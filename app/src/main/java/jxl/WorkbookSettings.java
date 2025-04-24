package jxl;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import jxl.biff.CountryCode;
import jxl.biff.formula.FunctionNames;
import jxl.common.Logger;

/* loaded from: classes.dex */
public final class WorkbookSettings {
    private static final int DEFAULT_ARRAY_GROW_SIZE = 1048576;
    private static final int DEFAULT_INITIAL_FILE_SIZE = 5242880;
    public static final int HIDEOBJ_HIDE_ALL = 2;
    public static final int HIDEOBJ_SHOW_ALL = 0;
    public static final int HIDEOBJ_SHOW_PLACEHOLDERS = 1;
    private static Logger logger = Logger.getLogger(WorkbookSettings.class);
    private boolean autoFilterDisabled;
    private boolean cellValidationDisabled;
    private int characterSet;
    private boolean drawingsDisabled;
    private String encoding;
    private boolean excel9file;
    private boolean formulaReferenceAdjustDisabled;
    private FunctionNames functionNames;
    private boolean gcDisabled;
    private boolean ignoreBlankCells;
    private Locale locale;
    private boolean mergedCellCheckingDisabled;
    private boolean namesDisabled;
    private boolean propertySetsDisabled;
    private boolean rationalizationDisabled;
    private File temporaryFileDuringWriteDirectory;
    private boolean useTemporaryFileDuringWrite;
    private String writeAccess;
    private int initialFileSize = DEFAULT_INITIAL_FILE_SIZE;
    private int arrayGrowSize = 1048576;
    private HashMap localeFunctionNames = new HashMap();
    private String excelDisplayLanguage = CountryCode.USA.getCode();
    private String excelRegionalSettings = CountryCode.UK.getCode();
    private boolean refreshAll = false;
    private boolean template = false;
    private boolean windowProtected = false;
    private int hideobj = 0;

    /* JADX WARN: Removed duplicated region for block: B:20:0x00e2 A[Catch: SecurityException -> 0x00e9, TRY_LEAVE, TryCatch #1 {SecurityException -> 0x00e9, blocks: (B:12:0x00b9, B:14:0x00bf, B:17:0x00c6, B:18:0x00dc, B:20:0x00e2, B:25:0x00d6), top: B:11:0x00b9 }] */
    /* JADX WARN: Removed duplicated region for block: B:24:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public WorkbookSettings() {
        /*
            r7 = this;
            java.lang.String r0 = "jxl.encoding"
            java.lang.String r1 = "jxl.country"
            java.lang.String r2 = "jxl.lang"
            java.lang.String r3 = "Error accessing system properties."
            r7.<init>()
            r4 = 0
            r7.excel9file = r4
            r5 = 5242880(0x500000, float:7.34684E-39)
            r7.initialFileSize = r5
            r5 = 1048576(0x100000, float:1.469368E-39)
            r7.arrayGrowSize = r5
            java.util.HashMap r5 = new java.util.HashMap
            r5.<init>()
            r7.localeFunctionNames = r5
            jxl.biff.CountryCode r5 = jxl.biff.CountryCode.USA
            java.lang.String r5 = r5.getCode()
            r7.excelDisplayLanguage = r5
            jxl.biff.CountryCode r5 = jxl.biff.CountryCode.UK
            java.lang.String r5 = r5.getCode()
            r7.excelRegionalSettings = r5
            r7.refreshAll = r4
            r7.template = r4
            r7.excel9file = r4
            r7.windowProtected = r4
            r7.hideobj = r4
            java.lang.String r5 = "jxl.nowarnings"
            boolean r5 = java.lang.Boolean.getBoolean(r5)     // Catch: java.lang.SecurityException -> Lb3
            r7.setSuppressWarnings(r5)     // Catch: java.lang.SecurityException -> Lb3
            java.lang.String r6 = "jxl.nodrawings"
            boolean r6 = java.lang.Boolean.getBoolean(r6)     // Catch: java.lang.SecurityException -> Lb3
            r7.drawingsDisabled = r6     // Catch: java.lang.SecurityException -> Lb3
            java.lang.String r6 = "jxl.nonames"
            boolean r6 = java.lang.Boolean.getBoolean(r6)     // Catch: java.lang.SecurityException -> Lb3
            r7.namesDisabled = r6     // Catch: java.lang.SecurityException -> Lb3
            java.lang.String r6 = "jxl.nogc"
            boolean r6 = java.lang.Boolean.getBoolean(r6)     // Catch: java.lang.SecurityException -> Lb3
            r7.gcDisabled = r6     // Catch: java.lang.SecurityException -> Lb3
            java.lang.String r6 = "jxl.norat"
            boolean r6 = java.lang.Boolean.getBoolean(r6)     // Catch: java.lang.SecurityException -> Lb3
            r7.rationalizationDisabled = r6     // Catch: java.lang.SecurityException -> Lb3
            java.lang.String r6 = "jxl.nomergedcellchecks"
            boolean r6 = java.lang.Boolean.getBoolean(r6)     // Catch: java.lang.SecurityException -> Lb3
            r7.mergedCellCheckingDisabled = r6     // Catch: java.lang.SecurityException -> Lb3
            java.lang.String r6 = "jxl.noformulaadjust"
            boolean r6 = java.lang.Boolean.getBoolean(r6)     // Catch: java.lang.SecurityException -> Lb3
            r7.formulaReferenceAdjustDisabled = r6     // Catch: java.lang.SecurityException -> Lb3
            java.lang.String r6 = "jxl.nopropertysets"
            boolean r6 = java.lang.Boolean.getBoolean(r6)     // Catch: java.lang.SecurityException -> Lb3
            r7.propertySetsDisabled = r6     // Catch: java.lang.SecurityException -> Lb3
            java.lang.String r6 = "jxl.ignoreblanks"
            boolean r6 = java.lang.Boolean.getBoolean(r6)     // Catch: java.lang.SecurityException -> Lb3
            r7.ignoreBlankCells = r6     // Catch: java.lang.SecurityException -> Lb3
            java.lang.String r6 = "jxl.nocellvalidation"
            boolean r6 = java.lang.Boolean.getBoolean(r6)     // Catch: java.lang.SecurityException -> Lb3
            r7.cellValidationDisabled = r6     // Catch: java.lang.SecurityException -> Lb3
            java.lang.String r6 = "jxl.autofilter"
            boolean r6 = java.lang.Boolean.getBoolean(r6)     // Catch: java.lang.SecurityException -> Lb3
            if (r6 != 0) goto L91
            r4 = 1
        L91:
            r7.autoFilterDisabled = r4     // Catch: java.lang.SecurityException -> Lb3
            java.lang.String r4 = "jxl.usetemporaryfileduringwrite"
            boolean r4 = java.lang.Boolean.getBoolean(r4)     // Catch: java.lang.SecurityException -> Lb3
            r7.useTemporaryFileDuringWrite = r4     // Catch: java.lang.SecurityException -> Lb3
            java.lang.String r4 = "jxl.temporaryfileduringwritedirectory"
            java.lang.String r4 = java.lang.System.getProperty(r4)     // Catch: java.lang.SecurityException -> Lb3
            if (r4 == 0) goto Laa
            java.io.File r6 = new java.io.File     // Catch: java.lang.SecurityException -> Lb3
            r6.<init>(r4)     // Catch: java.lang.SecurityException -> Lb3
            r7.temporaryFileDuringWriteDirectory = r6     // Catch: java.lang.SecurityException -> Lb3
        Laa:
            java.lang.String r6 = "file.encoding"
            java.lang.String r6 = java.lang.System.getProperty(r6)     // Catch: java.lang.SecurityException -> Lb3
            r7.encoding = r6     // Catch: java.lang.SecurityException -> Lb3
            goto Lb9
        Lb3:
            r4 = move-exception
            jxl.common.Logger r5 = jxl.WorkbookSettings.logger
            r5.warn(r3, r4)
        Lb9:
            java.lang.String r4 = java.lang.System.getProperty(r2)     // Catch: java.lang.SecurityException -> Le9
            if (r4 == 0) goto Ld6
            java.lang.String r4 = java.lang.System.getProperty(r1)     // Catch: java.lang.SecurityException -> Le9
            if (r4 != 0) goto Lc6
            goto Ld6
        Lc6:
            java.util.Locale r4 = new java.util.Locale     // Catch: java.lang.SecurityException -> Le9
            java.lang.String r2 = java.lang.System.getProperty(r2)     // Catch: java.lang.SecurityException -> Le9
            java.lang.String r1 = java.lang.System.getProperty(r1)     // Catch: java.lang.SecurityException -> Le9
            r4.<init>(r2, r1)     // Catch: java.lang.SecurityException -> Le9
            r7.locale = r4     // Catch: java.lang.SecurityException -> Le9
            goto Ldc
        Ld6:
            java.util.Locale r1 = java.util.Locale.getDefault()     // Catch: java.lang.SecurityException -> Le9
            r7.locale = r1     // Catch: java.lang.SecurityException -> Le9
        Ldc:
            java.lang.String r1 = java.lang.System.getProperty(r0)     // Catch: java.lang.SecurityException -> Le9
            if (r1 == 0) goto Le8
            java.lang.String r0 = java.lang.System.getProperty(r0)     // Catch: java.lang.SecurityException -> Le9
            r7.encoding = r0     // Catch: java.lang.SecurityException -> Le9
        Le8:
            goto Lf5
        Le9:
            r0 = move-exception
            jxl.common.Logger r1 = jxl.WorkbookSettings.logger
            r1.warn(r3, r0)
            java.util.Locale r1 = java.util.Locale.getDefault()
            r7.locale = r1
        Lf5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: jxl.WorkbookSettings.<init>():void");
    }

    public void setArrayGrowSize(int sz) {
        this.arrayGrowSize = sz;
    }

    public int getArrayGrowSize() {
        return this.arrayGrowSize;
    }

    public void setInitialFileSize(int sz) {
        this.initialFileSize = sz;
    }

    public int getInitialFileSize() {
        return this.initialFileSize;
    }

    public boolean getDrawingsDisabled() {
        return this.drawingsDisabled;
    }

    public boolean getGCDisabled() {
        return this.gcDisabled;
    }

    public boolean getNamesDisabled() {
        return this.namesDisabled;
    }

    public void setNamesDisabled(boolean b) {
        this.namesDisabled = b;
    }

    public void setDrawingsDisabled(boolean b) {
        this.drawingsDisabled = b;
    }

    public void setRationalization(boolean r) {
        this.rationalizationDisabled = !r;
    }

    public boolean getRationalizationDisabled() {
        return this.rationalizationDisabled;
    }

    public boolean getMergedCellCheckingDisabled() {
        return this.mergedCellCheckingDisabled;
    }

    public void setMergedCellChecking(boolean b) {
        this.mergedCellCheckingDisabled = !b;
    }

    public void setPropertySets(boolean r) {
        this.propertySetsDisabled = !r;
    }

    public boolean getPropertySetsDisabled() {
        return this.propertySetsDisabled;
    }

    public void setSuppressWarnings(boolean w) {
        logger.setSuppressWarnings(w);
    }

    public boolean getFormulaAdjust() {
        return !this.formulaReferenceAdjustDisabled;
    }

    public void setFormulaAdjust(boolean b) {
        this.formulaReferenceAdjustDisabled = !b;
    }

    public void setLocale(Locale l) {
        this.locale = l;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String enc) {
        this.encoding = enc;
    }

    public FunctionNames getFunctionNames() {
        if (this.functionNames == null) {
            FunctionNames functionNames = (FunctionNames) this.localeFunctionNames.get(this.locale);
            this.functionNames = functionNames;
            if (functionNames == null) {
                FunctionNames functionNames2 = new FunctionNames(this.locale);
                this.functionNames = functionNames2;
                this.localeFunctionNames.put(this.locale, functionNames2);
            }
        }
        return this.functionNames;
    }

    public int getCharacterSet() {
        return this.characterSet;
    }

    public void setCharacterSet(int cs) {
        this.characterSet = cs;
    }

    public void setGCDisabled(boolean disabled) {
        this.gcDisabled = disabled;
    }

    public void setIgnoreBlanks(boolean ignoreBlanks) {
        this.ignoreBlankCells = ignoreBlanks;
    }

    public boolean getIgnoreBlanks() {
        return this.ignoreBlankCells;
    }

    public void setCellValidationDisabled(boolean cv) {
        this.cellValidationDisabled = cv;
    }

    public boolean getCellValidationDisabled() {
        return this.cellValidationDisabled;
    }

    public String getExcelDisplayLanguage() {
        return this.excelDisplayLanguage;
    }

    public String getExcelRegionalSettings() {
        return this.excelRegionalSettings;
    }

    public void setExcelDisplayLanguage(String code) {
        this.excelDisplayLanguage = code;
    }

    public void setExcelRegionalSettings(String code) {
        this.excelRegionalSettings = code;
    }

    public boolean getAutoFilterDisabled() {
        return this.autoFilterDisabled;
    }

    public void setAutoFilterDisabled(boolean disabled) {
        this.autoFilterDisabled = disabled;
    }

    public boolean getUseTemporaryFileDuringWrite() {
        return this.useTemporaryFileDuringWrite;
    }

    public void setUseTemporaryFileDuringWrite(boolean temp) {
        this.useTemporaryFileDuringWrite = temp;
    }

    public void setTemporaryFileDuringWriteDirectory(File dir) {
        this.temporaryFileDuringWriteDirectory = dir;
    }

    public File getTemporaryFileDuringWriteDirectory() {
        return this.temporaryFileDuringWriteDirectory;
    }

    public void setRefreshAll(boolean refreshAll) {
        this.refreshAll = refreshAll;
    }

    public boolean getRefreshAll() {
        return this.refreshAll;
    }

    public boolean getTemplate() {
        return this.template;
    }

    public void setTemplate(boolean template) {
        this.template = template;
    }

    public boolean getExcel9File() {
        return this.excel9file;
    }

    public void setExcel9File(boolean excel9file) {
        this.excel9file = excel9file;
    }

    public boolean getWindowProtected() {
        return this.windowProtected;
    }

    public void setWindowProtected(boolean windowprotected) {
        this.windowProtected = this.windowProtected;
    }

    public int getHideobj() {
        return this.hideobj;
    }

    public void setHideobj(int hideobj) {
        this.hideobj = hideobj;
    }

    public String getWriteAccess() {
        return this.writeAccess;
    }

    public void setWriteAccess(String writeAccess) {
        this.writeAccess = writeAccess;
    }
}
