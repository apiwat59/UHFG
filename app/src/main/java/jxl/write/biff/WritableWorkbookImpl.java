package jxl.write.biff;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import jxl.Range;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.biff.BuiltInName;
import jxl.biff.CellReferenceHelper;
import jxl.biff.CountryCode;
import jxl.biff.Fonts;
import jxl.biff.FormattingRecords;
import jxl.biff.IndexMapping;
import jxl.biff.IntegerHelper;
import jxl.biff.RangeImpl;
import jxl.biff.WorkbookMethods;
import jxl.biff.XCTRecord;
import jxl.biff.drawing.Drawing;
import jxl.biff.drawing.DrawingGroup;
import jxl.biff.drawing.DrawingGroupObject;
import jxl.biff.drawing.Origin;
import jxl.biff.formula.ExternalSheet;
import jxl.common.Assert;
import jxl.common.Logger;
import jxl.format.Colour;
import jxl.format.RGB;
import jxl.read.biff.WorkbookParser;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.biff.NameRecord;

/* loaded from: classes.dex */
public class WritableWorkbookImpl extends WritableWorkbook implements ExternalSheet, WorkbookMethods {
    private String[] addInFunctionNames;
    private ButtonPropertySetRecord buttonPropertySet;
    private boolean closeStream;
    private boolean containsMacros;
    private CountryRecord countryRecord;
    private DrawingGroup drawingGroup;
    private ExternalSheetRecord externSheet;
    private Fonts fonts;
    private FormattingRecords formatRecords;
    private HashMap nameRecords;
    private ArrayList names;
    private File outputFile;
    private ArrayList rcirCells;
    private WorkbookSettings settings;
    private SharedStrings sharedStrings;
    private ArrayList sheets;
    private Styles styles;
    private ArrayList supbooks;
    private boolean wbProtected;
    private XCTRecord[] xctRecords;
    private static Logger logger = Logger.getLogger(WritableWorkbookImpl.class);
    private static Object SYNCHRONIZER = new Object();

    public WritableWorkbookImpl(OutputStream os, boolean cs, WorkbookSettings ws) throws IOException {
        this.outputFile = new File(os, ws, null);
        this.sheets = new ArrayList();
        this.sharedStrings = new SharedStrings();
        this.nameRecords = new HashMap();
        this.closeStream = cs;
        this.wbProtected = false;
        this.containsMacros = false;
        this.settings = ws;
        this.rcirCells = new ArrayList();
        this.styles = new Styles();
        synchronized (SYNCHRONIZER) {
            WritableWorkbook.ARIAL_10_PT.uninitialize();
            WritableWorkbook.HYPERLINK_FONT.uninitialize();
            WritableWorkbook.NORMAL_STYLE.uninitialize();
            WritableWorkbook.HYPERLINK_STYLE.uninitialize();
            WritableWorkbook.HIDDEN_STYLE.uninitialize();
            DateRecord.defaultDateFormat.uninitialize();
        }
        WritableFonts wf = new WritableFonts(this);
        this.fonts = wf;
        WritableFormattingRecords wfr = new WritableFormattingRecords(this.fonts, this.styles);
        this.formatRecords = wfr;
    }

    public WritableWorkbookImpl(OutputStream os, Workbook w, boolean cs, WorkbookSettings ws) throws IOException {
        WorkbookParser wp = (WorkbookParser) w;
        synchronized (SYNCHRONIZER) {
            WritableWorkbook.ARIAL_10_PT.uninitialize();
            WritableWorkbook.HYPERLINK_FONT.uninitialize();
            WritableWorkbook.NORMAL_STYLE.uninitialize();
            WritableWorkbook.HYPERLINK_STYLE.uninitialize();
            WritableWorkbook.HIDDEN_STYLE.uninitialize();
            DateRecord.defaultDateFormat.uninitialize();
        }
        this.closeStream = cs;
        this.sheets = new ArrayList();
        this.sharedStrings = new SharedStrings();
        this.nameRecords = new HashMap();
        this.fonts = wp.getFonts();
        this.formatRecords = wp.getFormattingRecords();
        this.wbProtected = false;
        this.settings = ws;
        this.rcirCells = new ArrayList();
        this.styles = new Styles();
        this.outputFile = new File(os, ws, wp.getCompoundFile());
        this.containsMacros = false;
        if (!ws.getPropertySetsDisabled()) {
            this.containsMacros = wp.containsMacros();
        }
        if (wp.getCountryRecord() != null) {
            this.countryRecord = new CountryRecord(wp.getCountryRecord());
        }
        this.addInFunctionNames = wp.getAddInFunctionNames();
        this.xctRecords = wp.getXCTRecords();
        if (wp.getExternalSheetRecord() != null) {
            this.externSheet = new ExternalSheetRecord(wp.getExternalSheetRecord());
            jxl.read.biff.SupbookRecord[] readsr = wp.getSupbookRecords();
            this.supbooks = new ArrayList(readsr.length);
            for (jxl.read.biff.SupbookRecord readSupbook : readsr) {
                if (readSupbook.getType() == jxl.read.biff.SupbookRecord.INTERNAL || readSupbook.getType() == jxl.read.biff.SupbookRecord.EXTERNAL) {
                    this.supbooks.add(new SupbookRecord(readSupbook, this.settings));
                } else if (readSupbook.getType() != jxl.read.biff.SupbookRecord.ADDIN) {
                    logger.warn("unsupported supbook type - ignoring");
                }
            }
        }
        if (wp.getDrawingGroup() != null) {
            this.drawingGroup = new DrawingGroup(wp.getDrawingGroup());
        }
        if (this.containsMacros && wp.getButtonPropertySet() != null) {
            this.buttonPropertySet = new ButtonPropertySetRecord(wp.getButtonPropertySet());
        }
        if (!this.settings.getNamesDisabled()) {
            jxl.read.biff.NameRecord[] na = wp.getNameRecords();
            this.names = new ArrayList(na.length);
            for (int i = 0; i < na.length; i++) {
                if (na[i].isBiff8()) {
                    NameRecord n = new NameRecord(na[i], i);
                    this.names.add(n);
                    String name = n.getName();
                    this.nameRecords.put(name, n);
                } else {
                    logger.warn("Cannot copy Biff7 name records - ignoring");
                }
            }
        }
        copyWorkbook(w);
        DrawingGroup drawingGroup = this.drawingGroup;
        if (drawingGroup != null) {
            drawingGroup.updateData(wp.getDrawingGroup());
        }
    }

    @Override // jxl.write.WritableWorkbook
    public WritableSheet[] getSheets() {
        WritableSheet[] sheetArray = new WritableSheet[getNumberOfSheets()];
        for (int i = 0; i < getNumberOfSheets(); i++) {
            sheetArray[i] = getSheet(i);
        }
        return sheetArray;
    }

    @Override // jxl.write.WritableWorkbook
    public String[] getSheetNames() {
        String[] sheetNames = new String[getNumberOfSheets()];
        for (int i = 0; i < sheetNames.length; i++) {
            sheetNames[i] = getSheet(i).getName();
        }
        return sheetNames;
    }

    @Override // jxl.biff.WorkbookMethods
    public Sheet getReadSheet(int index) {
        return getSheet(index);
    }

    @Override // jxl.write.WritableWorkbook
    public WritableSheet getSheet(int index) {
        return (WritableSheet) this.sheets.get(index);
    }

    @Override // jxl.write.WritableWorkbook
    public WritableSheet getSheet(String name) {
        boolean found = false;
        Iterator i = this.sheets.iterator();
        WritableSheet s = null;
        while (i.hasNext() && !found) {
            s = (WritableSheet) i.next();
            if (s.getName().equals(name)) {
                found = true;
            }
        }
        if (found) {
            return s;
        }
        return null;
    }

    @Override // jxl.write.WritableWorkbook
    public int getNumberOfSheets() {
        return this.sheets.size();
    }

    @Override // jxl.write.WritableWorkbook
    public void close() throws IOException, JxlWriteException {
        this.outputFile.close(this.closeStream);
    }

    @Override // jxl.write.WritableWorkbook
    public void setOutputFile(java.io.File fileName) throws IOException {
        FileOutputStream fos = new FileOutputStream(fileName);
        this.outputFile.setOutputFile(fos);
    }

    private WritableSheet createSheet(String name, int index, boolean handleRefs) {
        ExternalSheetRecord externalSheetRecord;
        WritableSheetImpl writableSheetImpl = new WritableSheetImpl(name, this.outputFile, this.formatRecords, this.sharedStrings, this.settings, this);
        int pos = index;
        if (index <= 0) {
            pos = 0;
            this.sheets.add(0, writableSheetImpl);
        } else if (index > this.sheets.size()) {
            pos = this.sheets.size();
            this.sheets.add(writableSheetImpl);
        } else {
            this.sheets.add(index, writableSheetImpl);
        }
        if (handleRefs && (externalSheetRecord = this.externSheet) != null) {
            externalSheetRecord.sheetInserted(pos);
        }
        ArrayList arrayList = this.supbooks;
        if (arrayList != null && arrayList.size() > 0) {
            SupbookRecord supbook = (SupbookRecord) this.supbooks.get(0);
            if (supbook.getType() == SupbookRecord.INTERNAL) {
                supbook.adjustInternal(this.sheets.size());
            }
        }
        return writableSheetImpl;
    }

    @Override // jxl.write.WritableWorkbook
    public WritableSheet createSheet(String name, int index) {
        return createSheet(name, index, true);
    }

    @Override // jxl.write.WritableWorkbook
    public void removeSheet(int index) {
        int pos = index;
        if (index <= 0) {
            pos = 0;
            this.sheets.remove(0);
        } else if (index >= this.sheets.size()) {
            pos = this.sheets.size() - 1;
            ArrayList arrayList = this.sheets;
            arrayList.remove(arrayList.size() - 1);
        } else {
            this.sheets.remove(index);
        }
        ExternalSheetRecord externalSheetRecord = this.externSheet;
        if (externalSheetRecord != null) {
            externalSheetRecord.sheetRemoved(pos);
        }
        ArrayList arrayList2 = this.supbooks;
        if (arrayList2 != null && arrayList2.size() > 0) {
            SupbookRecord supbook = (SupbookRecord) this.supbooks.get(0);
            if (supbook.getType() == SupbookRecord.INTERNAL) {
                supbook.adjustInternal(this.sheets.size());
            }
        }
        ArrayList arrayList3 = this.names;
        if (arrayList3 != null && arrayList3.size() > 0) {
            for (int i = 0; i < this.names.size(); i++) {
                NameRecord n = (NameRecord) this.names.get(i);
                int oldRef = n.getSheetRef();
                if (oldRef == pos + 1) {
                    n.setSheetRef(0);
                } else if (oldRef > pos + 1) {
                    if (oldRef < 1) {
                        oldRef = 1;
                    }
                    n.setSheetRef(oldRef - 1);
                }
            }
        }
    }

    @Override // jxl.write.WritableWorkbook
    public WritableSheet moveSheet(int fromIndex, int toIndex) {
        int fromIndex2 = Math.min(Math.max(fromIndex, 0), this.sheets.size() - 1);
        int toIndex2 = Math.min(Math.max(toIndex, 0), this.sheets.size() - 1);
        WritableSheet sheet = (WritableSheet) this.sheets.remove(fromIndex2);
        this.sheets.add(toIndex2, sheet);
        return sheet;
    }

    @Override // jxl.write.WritableWorkbook
    public void write() throws IOException {
        int selectedSheetIndex;
        WritableSheetImpl wsi = null;
        int i = 0;
        while (i < getNumberOfSheets()) {
            WritableSheetImpl wsi2 = (WritableSheetImpl) getSheet(i);
            wsi2.checkMergedBorders();
            Range range = wsi2.getSettings().getPrintArea();
            if (range != null) {
                addNameArea(BuiltInName.PRINT_AREA, (WritableSheet) wsi2, range.getTopLeft().getColumn(), range.getTopLeft().getRow(), range.getBottomRight().getColumn(), range.getBottomRight().getRow(), false);
            }
            Range rangeR = wsi2.getSettings().getPrintTitlesRow();
            Range rangeC = wsi2.getSettings().getPrintTitlesCol();
            if (rangeR != null && rangeC != null) {
                addNameArea(BuiltInName.PRINT_TITLES, wsi2, rangeR.getTopLeft().getColumn(), rangeR.getTopLeft().getRow(), rangeR.getBottomRight().getColumn(), rangeR.getBottomRight().getRow(), rangeC.getTopLeft().getColumn(), rangeC.getTopLeft().getRow(), rangeC.getBottomRight().getColumn(), rangeC.getBottomRight().getRow(), false);
            } else if (rangeR != null) {
                addNameArea(BuiltInName.PRINT_TITLES, (WritableSheet) wsi2, rangeR.getTopLeft().getColumn(), rangeR.getTopLeft().getRow(), rangeR.getBottomRight().getColumn(), rangeR.getBottomRight().getRow(), false);
            } else if (rangeC != null) {
                addNameArea(BuiltInName.PRINT_TITLES, (WritableSheet) wsi2, rangeC.getTopLeft().getColumn(), rangeC.getTopLeft().getRow(), rangeC.getBottomRight().getColumn(), rangeC.getBottomRight().getRow(), false);
            }
            i++;
            wsi = wsi2;
        }
        if (!this.settings.getRationalizationDisabled()) {
            rationalize();
        }
        BOFRecord bof = new BOFRecord(BOFRecord.workbookGlobals);
        this.outputFile.write(bof);
        if (this.settings.getTemplate()) {
            TemplateRecord trec = new TemplateRecord();
            this.outputFile.write(trec);
        }
        InterfaceHeaderRecord ihr = new InterfaceHeaderRecord();
        this.outputFile.write(ihr);
        MMSRecord mms = new MMSRecord(0, 0);
        this.outputFile.write(mms);
        InterfaceEndRecord ier = new InterfaceEndRecord();
        this.outputFile.write(ier);
        WriteAccessRecord wr = new WriteAccessRecord(this.settings.getWriteAccess());
        this.outputFile.write(wr);
        CodepageRecord cp = new CodepageRecord();
        this.outputFile.write(cp);
        DSFRecord dsf = new DSFRecord();
        this.outputFile.write(dsf);
        if (this.settings.getExcel9File()) {
            Excel9FileRecord e9rec = new Excel9FileRecord();
            this.outputFile.write(e9rec);
        }
        TabIdRecord tabid = new TabIdRecord(getNumberOfSheets());
        this.outputFile.write(tabid);
        if (this.containsMacros) {
            ObjProjRecord objproj = new ObjProjRecord();
            this.outputFile.write(objproj);
        }
        ButtonPropertySetRecord buttonPropertySetRecord = this.buttonPropertySet;
        if (buttonPropertySetRecord != null) {
            this.outputFile.write(buttonPropertySetRecord);
        }
        FunctionGroupCountRecord fgcr = new FunctionGroupCountRecord();
        this.outputFile.write(fgcr);
        WindowProtectRecord wpr = new WindowProtectRecord(this.settings.getWindowProtected());
        this.outputFile.write(wpr);
        ProtectRecord pr = new ProtectRecord(this.wbProtected);
        this.outputFile.write(pr);
        PasswordRecord pw = new PasswordRecord((String) null);
        this.outputFile.write(pw);
        Prot4RevRecord p4r = new Prot4RevRecord(false);
        this.outputFile.write(p4r);
        Prot4RevPassRecord p4rp = new Prot4RevPassRecord();
        this.outputFile.write(p4rp);
        boolean sheetSelected = false;
        int selectedSheetIndex2 = 0;
        int i2 = 0;
        while (true) {
            InterfaceHeaderRecord ihr2 = ihr;
            if (i2 >= getNumberOfSheets() || sheetSelected) {
                break;
            }
            WritableSheetImpl wsheet = (WritableSheetImpl) getSheet(i2);
            if (wsheet.getSettings().isSelected()) {
                sheetSelected = true;
                selectedSheetIndex2 = i2;
            }
            i2++;
            ihr = ihr2;
        }
        if (!sheetSelected) {
            WritableSheetImpl wsheet2 = (WritableSheetImpl) getSheet(0);
            wsheet2.getSettings().setSelected(true);
            selectedSheetIndex = 0;
        } else {
            selectedSheetIndex = selectedSheetIndex2;
        }
        Window1Record w1r = new Window1Record(selectedSheetIndex);
        this.outputFile.write(w1r);
        BackupRecord bkr = new BackupRecord(false);
        this.outputFile.write(bkr);
        HideobjRecord ho = new HideobjRecord(this.settings.getHideobj());
        this.outputFile.write(ho);
        NineteenFourRecord nf = new NineteenFourRecord(false);
        this.outputFile.write(nf);
        PrecisionRecord pc = new PrecisionRecord(false);
        this.outputFile.write(pc);
        RefreshAllRecord rar = new RefreshAllRecord(this.settings.getRefreshAll());
        this.outputFile.write(rar);
        BookboolRecord bb = new BookboolRecord(true);
        this.outputFile.write(bb);
        this.fonts.write(this.outputFile);
        this.formatRecords.write(this.outputFile);
        if (this.formatRecords.getPalette() != null) {
            this.outputFile.write(this.formatRecords.getPalette());
        }
        UsesElfsRecord uer = new UsesElfsRecord();
        this.outputFile.write(uer);
        int[] boundsheetPos = new int[getNumberOfSheets()];
        int i3 = 0;
        while (true) {
            int selectedSheetIndex3 = selectedSheetIndex;
            int selectedSheetIndex4 = getNumberOfSheets();
            if (i3 >= selectedSheetIndex4) {
                break;
            }
            boundsheetPos[i3] = this.outputFile.getPos();
            WritableSheet sheet = getSheet(i3);
            MMSRecord mms2 = mms;
            BoundsheetRecord br = new BoundsheetRecord(sheet.getName());
            if (sheet.getSettings().isHidden()) {
                br.setHidden();
            }
            if (((WritableSheetImpl) this.sheets.get(i3)).isChartOnly()) {
                br.setChartOnly();
            }
            this.outputFile.write(br);
            i3++;
            selectedSheetIndex = selectedSheetIndex3;
            mms = mms2;
        }
        if (this.countryRecord == null) {
            CountryCode lang = CountryCode.getCountryCode(this.settings.getExcelDisplayLanguage());
            if (lang == CountryCode.UNKNOWN) {
                logger.warn("Unknown country code " + this.settings.getExcelDisplayLanguage() + " using " + CountryCode.USA.getCode());
                lang = CountryCode.USA;
            }
            CountryCode region = CountryCode.getCountryCode(this.settings.getExcelRegionalSettings());
            this.countryRecord = new CountryRecord(lang, region);
            if (region == CountryCode.UNKNOWN) {
                logger.warn("Unknown country code " + this.settings.getExcelDisplayLanguage() + " using " + CountryCode.UK.getCode());
                CountryCode countryCode = CountryCode.UK;
            }
        }
        this.outputFile.write(this.countryRecord);
        String[] strArr = this.addInFunctionNames;
        if (strArr != null && strArr.length > 0) {
            for (int i4 = 0; i4 < this.addInFunctionNames.length; i4++) {
                ExternalNameRecord enr = new ExternalNameRecord(this.addInFunctionNames[i4]);
                this.outputFile.write(enr);
            }
        }
        if (this.xctRecords != null) {
            int i5 = 0;
            while (true) {
                XCTRecord[] xCTRecordArr = this.xctRecords;
                if (i5 >= xCTRecordArr.length) {
                    break;
                }
                this.outputFile.write(xCTRecordArr[i5]);
                i5++;
            }
        }
        if (this.externSheet != null) {
            for (int i6 = 0; i6 < this.supbooks.size(); i6++) {
                SupbookRecord supbook = (SupbookRecord) this.supbooks.get(i6);
                this.outputFile.write(supbook);
            }
            this.outputFile.write(this.externSheet);
        }
        if (this.names != null) {
            for (int i7 = 0; i7 < this.names.size(); i7++) {
                NameRecord n = (NameRecord) this.names.get(i7);
                this.outputFile.write(n);
            }
        }
        DrawingGroup drawingGroup = this.drawingGroup;
        if (drawingGroup != null) {
            drawingGroup.write(this.outputFile);
        }
        this.sharedStrings.write(this.outputFile);
        EOFRecord eof = new EOFRecord();
        this.outputFile.write(eof);
        for (int i8 = 0; i8 < getNumberOfSheets(); i8++) {
            File file = this.outputFile;
            file.setData(IntegerHelper.getFourBytes(file.getPos()), boundsheetPos[i8] + 4);
            WritableSheetImpl wsheet3 = (WritableSheetImpl) getSheet(i8);
            wsheet3.write();
        }
    }

    private void copyWorkbook(Workbook w) {
        int numSheets = w.getNumberOfSheets();
        this.wbProtected = w.isProtected();
        for (int i = 0; i < numSheets; i++) {
            Sheet s = w.getSheet(i);
            WritableSheetImpl ws = (WritableSheetImpl) createSheet(s.getName(), i, false);
            ws.copy(s);
        }
    }

    @Override // jxl.write.WritableWorkbook
    public void copySheet(int s, String name, int index) {
        WritableSheet sheet = getSheet(s);
        WritableSheetImpl ws = (WritableSheetImpl) createSheet(name, index);
        ws.copy(sheet);
    }

    @Override // jxl.write.WritableWorkbook
    public void copySheet(String s, String name, int index) {
        WritableSheet sheet = getSheet(s);
        WritableSheetImpl ws = (WritableSheetImpl) createSheet(name, index);
        ws.copy(sheet);
    }

    @Override // jxl.write.WritableWorkbook
    public void setProtected(boolean prot) {
        this.wbProtected = prot;
    }

    private void rationalize() {
        IndexMapping fontMapping = this.formatRecords.rationalizeFonts();
        IndexMapping formatMapping = this.formatRecords.rationalizeDisplayFormats();
        IndexMapping xfMapping = this.formatRecords.rationalize(fontMapping, formatMapping);
        for (int i = 0; i < this.sheets.size(); i++) {
            WritableSheetImpl wsi = (WritableSheetImpl) this.sheets.get(i);
            wsi.rationalize(xfMapping, fontMapping, formatMapping);
        }
    }

    private int getInternalSheetIndex(String name) {
        String[] names = getSheetNames();
        for (int i = 0; i < names.length; i++) {
            if (name.equals(names[i])) {
                int index = i;
                return index;
            }
        }
        return -1;
    }

    @Override // jxl.biff.formula.ExternalSheet
    public String getExternalSheetName(int index) {
        int supbookIndex = this.externSheet.getSupbookIndex(index);
        SupbookRecord sr = (SupbookRecord) this.supbooks.get(supbookIndex);
        int firstTab = this.externSheet.getFirstTabIndex(index);
        if (sr.getType() == SupbookRecord.INTERNAL) {
            WritableSheet ws = getSheet(firstTab);
            return ws.getName();
        }
        if (sr.getType() == SupbookRecord.EXTERNAL) {
            String name = sr.getFileName() + sr.getSheetName(firstTab);
            return name;
        }
        logger.warn("Unknown Supbook 1");
        return "[UNKNOWN]";
    }

    public String getLastExternalSheetName(int index) {
        int supbookIndex = this.externSheet.getSupbookIndex(index);
        SupbookRecord sr = (SupbookRecord) this.supbooks.get(supbookIndex);
        int lastTab = this.externSheet.getLastTabIndex(index);
        if (sr.getType() == SupbookRecord.INTERNAL) {
            WritableSheet ws = getSheet(lastTab);
            return ws.getName();
        }
        if (sr.getType() == SupbookRecord.EXTERNAL) {
            Assert.verify(false);
        }
        logger.warn("Unknown Supbook 2");
        return "[UNKNOWN]";
    }

    @Override // jxl.biff.formula.ExternalSheet
    public jxl.read.biff.BOFRecord getWorkbookBof() {
        return null;
    }

    @Override // jxl.biff.formula.ExternalSheet
    public int getExternalSheetIndex(int index) {
        ExternalSheetRecord externalSheetRecord = this.externSheet;
        if (externalSheetRecord == null) {
            return index;
        }
        Assert.verify(externalSheetRecord != null);
        int firstTab = this.externSheet.getFirstTabIndex(index);
        return firstTab;
    }

    @Override // jxl.biff.formula.ExternalSheet
    public int getLastExternalSheetIndex(int index) {
        ExternalSheetRecord externalSheetRecord = this.externSheet;
        if (externalSheetRecord == null) {
            return index;
        }
        Assert.verify(externalSheetRecord != null);
        int lastTab = this.externSheet.getLastTabIndex(index);
        return lastTab;
    }

    @Override // jxl.biff.formula.ExternalSheet
    public int getExternalSheetIndex(String sheetName) {
        if (this.externSheet == null) {
            this.externSheet = new ExternalSheetRecord();
            ArrayList arrayList = new ArrayList();
            this.supbooks = arrayList;
            arrayList.add(new SupbookRecord(getNumberOfSheets(), this.settings));
        }
        boolean found = false;
        Iterator i = this.sheets.iterator();
        int sheetpos = 0;
        while (i.hasNext() && !found) {
            WritableSheetImpl s = (WritableSheetImpl) i.next();
            if (s.getName().equals(sheetName)) {
                found = true;
            } else {
                sheetpos++;
            }
        }
        if (found) {
            SupbookRecord supbook = (SupbookRecord) this.supbooks.get(0);
            if (supbook.getType() != SupbookRecord.INTERNAL || supbook.getNumberOfSheets() != getNumberOfSheets()) {
                logger.warn("Cannot find sheet " + sheetName + " in supbook record");
            }
            return this.externSheet.getIndex(0, sheetpos);
        }
        int closeSquareBracketsIndex = sheetName.lastIndexOf(93);
        int openSquareBracketsIndex = sheetName.lastIndexOf(91);
        if (closeSquareBracketsIndex != -1 && openSquareBracketsIndex != -1) {
            String worksheetName = sheetName.substring(closeSquareBracketsIndex + 1);
            String workbookName = sheetName.substring(openSquareBracketsIndex + 1, closeSquareBracketsIndex);
            String path = sheetName.substring(0, openSquareBracketsIndex);
            String fileName = path + workbookName;
            boolean supbookFound = false;
            SupbookRecord externalSupbook = null;
            int supbookIndex = -1;
            int ind = 0;
            while (ind < this.supbooks.size() && !supbookFound) {
                externalSupbook = (SupbookRecord) this.supbooks.get(ind);
                boolean found2 = found;
                if (externalSupbook.getType() == SupbookRecord.EXTERNAL && externalSupbook.getFileName().equals(fileName)) {
                    supbookFound = true;
                    supbookIndex = ind;
                }
                ind++;
                found = found2;
            }
            if (!supbookFound) {
                externalSupbook = new SupbookRecord(fileName, this.settings);
                supbookIndex = this.supbooks.size();
                this.supbooks.add(externalSupbook);
            }
            int sheetIndex = externalSupbook.getSheetIndex(worksheetName);
            return this.externSheet.getIndex(supbookIndex, sheetIndex);
        }
        logger.warn("Square brackets");
        return -1;
    }

    @Override // jxl.biff.formula.ExternalSheet
    public int getLastExternalSheetIndex(String sheetName) {
        if (this.externSheet == null) {
            this.externSheet = new ExternalSheetRecord();
            ArrayList arrayList = new ArrayList();
            this.supbooks = arrayList;
            arrayList.add(new SupbookRecord(getNumberOfSheets(), this.settings));
        }
        boolean found = false;
        Iterator i = this.sheets.iterator();
        int sheetpos = 0;
        while (i.hasNext() && !found) {
            WritableSheetImpl s = (WritableSheetImpl) i.next();
            if (s.getName().equals(sheetName)) {
                found = true;
            } else {
                sheetpos++;
            }
        }
        if (!found) {
            return -1;
        }
        SupbookRecord supbook = (SupbookRecord) this.supbooks.get(0);
        Assert.verify(supbook.getType() == SupbookRecord.INTERNAL && supbook.getNumberOfSheets() == getNumberOfSheets());
        return this.externSheet.getIndex(0, sheetpos);
    }

    @Override // jxl.write.WritableWorkbook
    public void setColourRGB(Colour c, int r, int g, int b) {
        this.formatRecords.setColourRGB(c, r, g, b);
    }

    public RGB getColourRGB(Colour c) {
        return this.formatRecords.getColourRGB(c);
    }

    @Override // jxl.biff.WorkbookMethods
    public String getName(int index) {
        Assert.verify(index >= 0 && index < this.names.size());
        NameRecord n = (NameRecord) this.names.get(index);
        return n.getName();
    }

    @Override // jxl.biff.WorkbookMethods
    public int getNameIndex(String name) {
        NameRecord nr = (NameRecord) this.nameRecords.get(name);
        if (nr != null) {
            return nr.getIndex();
        }
        return -1;
    }

    void addRCIRCell(CellValue cv) {
        this.rcirCells.add(cv);
    }

    void columnInserted(WritableSheetImpl s, int col) {
        int externalSheetIndex = getExternalSheetIndex(s.getName());
        Iterator i = this.rcirCells.iterator();
        while (i.hasNext()) {
            CellValue cv = (CellValue) i.next();
            cv.columnInserted(s, externalSheetIndex, col);
        }
        ArrayList arrayList = this.names;
        if (arrayList != null) {
            Iterator i2 = arrayList.iterator();
            while (i2.hasNext()) {
                NameRecord nameRecord = (NameRecord) i2.next();
                nameRecord.columnInserted(externalSheetIndex, col);
            }
        }
    }

    void columnRemoved(WritableSheetImpl s, int col) {
        int externalSheetIndex = getExternalSheetIndex(s.getName());
        Iterator i = this.rcirCells.iterator();
        while (i.hasNext()) {
            CellValue cv = (CellValue) i.next();
            cv.columnRemoved(s, externalSheetIndex, col);
        }
        ArrayList removedNames = new ArrayList();
        ArrayList arrayList = this.names;
        if (arrayList != null) {
            Iterator i2 = arrayList.iterator();
            while (i2.hasNext()) {
                NameRecord nameRecord = (NameRecord) i2.next();
                boolean removeName = nameRecord.columnRemoved(externalSheetIndex, col);
                if (removeName) {
                    removedNames.add(nameRecord);
                }
            }
            Iterator i3 = removedNames.iterator();
            while (i3.hasNext()) {
                NameRecord nameRecord2 = (NameRecord) i3.next();
                boolean removed = this.names.remove(nameRecord2);
                Assert.verify(removed, "Could not remove name " + nameRecord2.getName());
            }
        }
    }

    void rowInserted(WritableSheetImpl s, int row) {
        int externalSheetIndex = getExternalSheetIndex(s.getName());
        Iterator i = this.rcirCells.iterator();
        while (i.hasNext()) {
            CellValue cv = (CellValue) i.next();
            cv.rowInserted(s, externalSheetIndex, row);
        }
        ArrayList arrayList = this.names;
        if (arrayList != null) {
            Iterator i2 = arrayList.iterator();
            while (i2.hasNext()) {
                NameRecord nameRecord = (NameRecord) i2.next();
                nameRecord.rowInserted(externalSheetIndex, row);
            }
        }
    }

    void rowRemoved(WritableSheetImpl s, int row) {
        int externalSheetIndex = getExternalSheetIndex(s.getName());
        Iterator i = this.rcirCells.iterator();
        while (i.hasNext()) {
            CellValue cv = (CellValue) i.next();
            cv.rowRemoved(s, externalSheetIndex, row);
        }
        ArrayList removedNames = new ArrayList();
        ArrayList arrayList = this.names;
        if (arrayList != null) {
            Iterator i2 = arrayList.iterator();
            while (i2.hasNext()) {
                NameRecord nameRecord = (NameRecord) i2.next();
                boolean removeName = nameRecord.rowRemoved(externalSheetIndex, row);
                if (removeName) {
                    removedNames.add(nameRecord);
                }
            }
            Iterator i3 = removedNames.iterator();
            while (i3.hasNext()) {
                NameRecord nameRecord2 = (NameRecord) i3.next();
                boolean removed = this.names.remove(nameRecord2);
                Assert.verify(removed, "Could not remove name " + nameRecord2.getName());
            }
        }
    }

    @Override // jxl.write.WritableWorkbook
    public WritableCell findCellByName(String name) {
        NameRecord nr = (NameRecord) this.nameRecords.get(name);
        if (nr == null) {
            return null;
        }
        NameRecord.NameRange[] ranges = nr.getRanges();
        int sheetIndex = getExternalSheetIndex(ranges[0].getExternalSheet());
        WritableSheet s = getSheet(sheetIndex);
        WritableCell cell = s.getWritableCell(ranges[0].getFirstColumn(), ranges[0].getFirstRow());
        return cell;
    }

    @Override // jxl.write.WritableWorkbook
    public Range[] findByName(String name) {
        NameRecord nr = (NameRecord) this.nameRecords.get(name);
        if (nr == null) {
            return null;
        }
        NameRecord.NameRange[] ranges = nr.getRanges();
        Range[] cellRanges = new Range[ranges.length];
        for (int i = 0; i < ranges.length; i++) {
            cellRanges[i] = new RangeImpl(this, getExternalSheetIndex(ranges[i].getExternalSheet()), ranges[i].getFirstColumn(), ranges[i].getFirstRow(), getLastExternalSheetIndex(ranges[i].getExternalSheet()), ranges[i].getLastColumn(), ranges[i].getLastRow());
        }
        return cellRanges;
    }

    void addDrawing(DrawingGroupObject d) {
        if (this.drawingGroup == null) {
            this.drawingGroup = new DrawingGroup(Origin.WRITE);
        }
        this.drawingGroup.add(d);
    }

    void removeDrawing(Drawing d) {
        Assert.verify(this.drawingGroup != null);
        this.drawingGroup.remove(d);
    }

    DrawingGroup getDrawingGroup() {
        return this.drawingGroup;
    }

    DrawingGroup createDrawingGroup() {
        if (this.drawingGroup == null) {
            this.drawingGroup = new DrawingGroup(Origin.WRITE);
        }
        return this.drawingGroup;
    }

    @Override // jxl.write.WritableWorkbook
    public String[] getRangeNames() {
        ArrayList arrayList = this.names;
        if (arrayList == null) {
            return new String[0];
        }
        String[] n = new String[arrayList.size()];
        for (int i = 0; i < this.names.size(); i++) {
            NameRecord nr = (NameRecord) this.names.get(i);
            n[i] = nr.getName();
        }
        return n;
    }

    @Override // jxl.write.WritableWorkbook
    public void removeRangeName(String name) {
        int pos = 0;
        boolean found = false;
        Iterator i = this.names.iterator();
        while (i.hasNext() && !found) {
            NameRecord nr = (NameRecord) i.next();
            if (nr.getName().equals(name)) {
                found = true;
            } else {
                pos++;
            }
        }
        if (found) {
            this.names.remove(pos);
            if (this.nameRecords.remove(name) == null) {
                logger.warn("Could not remove " + name + " from index lookups");
            }
        }
    }

    Styles getStyles() {
        return this.styles;
    }

    @Override // jxl.write.WritableWorkbook
    public void addNameArea(String name, WritableSheet sheet, int firstCol, int firstRow, int lastCol, int lastRow) {
        addNameArea(name, sheet, firstCol, firstRow, lastCol, lastRow, true);
    }

    void addNameArea(String name, WritableSheet sheet, int firstCol, int firstRow, int lastCol, int lastRow, boolean global) {
        if (this.names == null) {
            this.names = new ArrayList();
        }
        int externalSheetIndex = getExternalSheetIndex(sheet.getName());
        NameRecord nr = new NameRecord(name, this.names.size(), externalSheetIndex, firstRow, lastRow, firstCol, lastCol, global);
        this.names.add(nr);
        this.nameRecords.put(name, nr);
    }

    void addNameArea(BuiltInName name, WritableSheet sheet, int firstCol, int firstRow, int lastCol, int lastRow, boolean global) {
        if (this.names == null) {
            this.names = new ArrayList();
        }
        int index = getInternalSheetIndex(sheet.getName());
        int externalSheetIndex = getExternalSheetIndex(sheet.getName());
        NameRecord nr = new NameRecord(name, index, externalSheetIndex, firstRow, lastRow, firstCol, lastCol, global);
        this.names.add(nr);
        this.nameRecords.put(name, nr);
    }

    void addNameArea(BuiltInName name, WritableSheet sheet, int firstCol, int firstRow, int lastCol, int lastRow, int firstCol2, int firstRow2, int lastCol2, int lastRow2, boolean global) {
        if (this.names == null) {
            this.names = new ArrayList();
        }
        int index = getInternalSheetIndex(sheet.getName());
        int externalSheetIndex = getExternalSheetIndex(sheet.getName());
        NameRecord nr = new NameRecord(name, index, externalSheetIndex, firstRow2, lastRow2, firstCol2, lastCol2, firstRow, lastRow, firstCol, lastCol, global);
        this.names.add(nr);
        this.nameRecords.put(name, nr);
    }

    WorkbookSettings getSettings() {
        return this.settings;
    }

    @Override // jxl.write.WritableWorkbook
    public WritableCell getWritableCell(String loc) {
        WritableSheet s = getSheet(CellReferenceHelper.getSheet(loc));
        return s.getWritableCell(loc);
    }

    @Override // jxl.write.WritableWorkbook
    public WritableSheet importSheet(String name, int index, Sheet sheet) {
        WritableSheet ws = createSheet(name, index);
        ((WritableSheetImpl) ws).importSheet(sheet);
        return ws;
    }
}
