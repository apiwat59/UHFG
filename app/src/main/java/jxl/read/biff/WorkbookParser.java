package jxl.read.biff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import jxl.Cell;
import jxl.Range;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.biff.BuiltInName;
import jxl.biff.CellReferenceHelper;
import jxl.biff.EmptyCell;
import jxl.biff.FontRecord;
import jxl.biff.Fonts;
import jxl.biff.FormatRecord;
import jxl.biff.FormattingRecords;
import jxl.biff.NameRangeException;
import jxl.biff.NumFormatRecordsException;
import jxl.biff.RangeImpl;
import jxl.biff.StringHelper;
import jxl.biff.Type;
import jxl.biff.WorkbookMethods;
import jxl.biff.XCTRecord;
import jxl.biff.XFRecord;
import jxl.biff.drawing.DrawingGroup;
import jxl.biff.drawing.MsoDrawingGroupRecord;
import jxl.biff.drawing.Origin;
import jxl.biff.formula.ExternalSheet;
import jxl.common.Assert;
import jxl.common.Logger;
import jxl.read.biff.NameRecord;

/* loaded from: classes.dex */
public class WorkbookParser extends Workbook implements ExternalSheet, WorkbookMethods {
    private static Logger logger = Logger.getLogger(WorkbookParser.class);
    private ArrayList addInFunctions;
    private int bofs;
    private ButtonPropertySetRecord buttonPropertySet;
    private CountryRecord countryRecord;
    private DrawingGroup drawingGroup;
    private File excelFile;
    private ExternalSheetRecord externSheet;
    private SheetImpl lastSheet;
    private MsoDrawingGroupRecord msoDrawingGroup;
    private ArrayList nameTable;
    private boolean nineteenFour;
    private WorkbookSettings settings;
    private SSTRecord sharedStrings;
    private BOFRecord workbookBof;
    private ArrayList boundsheets = new ArrayList(10);
    private Fonts fonts = new Fonts();
    private FormattingRecords formattingRecords = new FormattingRecords(this.fonts);
    private ArrayList sheets = new ArrayList(10);
    private ArrayList supbooks = new ArrayList(10);
    private HashMap namedRecords = new HashMap();
    private int lastSheetIndex = -1;
    private boolean wbProtected = false;
    private boolean containsMacros = false;
    private ArrayList xctRecords = new ArrayList(10);

    public WorkbookParser(File f, WorkbookSettings s) {
        this.excelFile = f;
        this.settings = s;
    }

    @Override // jxl.Workbook
    public Sheet[] getSheets() {
        Sheet[] sheetArray = new Sheet[getNumberOfSheets()];
        return (Sheet[]) this.sheets.toArray(sheetArray);
    }

    @Override // jxl.biff.WorkbookMethods
    public Sheet getReadSheet(int index) {
        return getSheet(index);
    }

    @Override // jxl.Workbook
    public Sheet getSheet(int index) {
        SheetImpl sheetImpl = this.lastSheet;
        if (sheetImpl != null && this.lastSheetIndex == index) {
            return sheetImpl;
        }
        if (sheetImpl != null) {
            sheetImpl.clear();
            if (!this.settings.getGCDisabled()) {
                System.gc();
            }
        }
        SheetImpl sheetImpl2 = (SheetImpl) this.sheets.get(index);
        this.lastSheet = sheetImpl2;
        this.lastSheetIndex = index;
        sheetImpl2.readSheet();
        return this.lastSheet;
    }

    @Override // jxl.Workbook
    public Sheet getSheet(String name) {
        int pos = 0;
        boolean found = false;
        Iterator i = this.boundsheets.iterator();
        while (i.hasNext() && !found) {
            BoundsheetRecord br = (BoundsheetRecord) i.next();
            if (br.getName().equals(name)) {
                found = true;
            } else {
                pos++;
            }
        }
        if (found) {
            return getSheet(pos);
        }
        return null;
    }

    @Override // jxl.Workbook
    public String[] getSheetNames() {
        String[] names = new String[this.boundsheets.size()];
        for (int i = 0; i < names.length; i++) {
            BoundsheetRecord br = (BoundsheetRecord) this.boundsheets.get(i);
            names[i] = br.getName();
        }
        return names;
    }

    @Override // jxl.biff.formula.ExternalSheet
    public int getExternalSheetIndex(int index) {
        if (this.workbookBof.isBiff7()) {
            return index;
        }
        Assert.verify(this.externSheet != null);
        int firstTab = this.externSheet.getFirstTabIndex(index);
        return firstTab;
    }

    @Override // jxl.biff.formula.ExternalSheet
    public int getLastExternalSheetIndex(int index) {
        if (this.workbookBof.isBiff7()) {
            return index;
        }
        Assert.verify(this.externSheet != null);
        int lastTab = this.externSheet.getLastTabIndex(index);
        return lastTab;
    }

    @Override // jxl.biff.formula.ExternalSheet
    public String getExternalSheetName(int index) {
        String firstTabName;
        String lastTabName;
        String sheetName;
        if (this.workbookBof.isBiff7()) {
            BoundsheetRecord br = (BoundsheetRecord) this.boundsheets.get(index);
            return br.getName();
        }
        int supbookIndex = this.externSheet.getSupbookIndex(index);
        SupbookRecord sr = (SupbookRecord) this.supbooks.get(supbookIndex);
        int firstTab = this.externSheet.getFirstTabIndex(index);
        int lastTab = this.externSheet.getLastTabIndex(index);
        if (sr.getType() == SupbookRecord.INTERNAL) {
            if (firstTab == 65535) {
                firstTabName = "#REF";
            } else {
                BoundsheetRecord br2 = (BoundsheetRecord) this.boundsheets.get(firstTab);
                firstTabName = br2.getName();
            }
            if (lastTab == 65535) {
                lastTabName = "#REF";
            } else {
                BoundsheetRecord br3 = (BoundsheetRecord) this.boundsheets.get(lastTab);
                lastTabName = br3.getName();
            }
            if (firstTab == lastTab) {
                sheetName = firstTabName;
            } else {
                sheetName = firstTabName + ':' + lastTabName;
            }
            String sheetName2 = sheetName.indexOf(39) == -1 ? sheetName : StringHelper.replace(sheetName, "'", "''");
            if (sheetName2.indexOf(32) == -1) {
                return sheetName2;
            }
            return '\'' + sheetName2 + '\'';
        }
        if (sr.getType() == SupbookRecord.EXTERNAL) {
            StringBuffer sb = new StringBuffer();
            java.io.File fl = new java.io.File(sr.getFileName());
            sb.append("'");
            sb.append(fl.getAbsolutePath());
            sb.append("[");
            sb.append(fl.getName());
            sb.append("]");
            sb.append(firstTab == 65535 ? "#REF" : sr.getSheetName(firstTab));
            if (lastTab != firstTab) {
                sb.append(sr.getSheetName(lastTab));
            }
            sb.append("'");
            return sb.toString();
        }
        logger.warn("Unknown Supbook 3");
        return "[UNKNOWN]";
    }

    public String getLastExternalSheetName(int index) {
        if (this.workbookBof.isBiff7()) {
            BoundsheetRecord br = (BoundsheetRecord) this.boundsheets.get(index);
            return br.getName();
        }
        int supbookIndex = this.externSheet.getSupbookIndex(index);
        SupbookRecord sr = (SupbookRecord) this.supbooks.get(supbookIndex);
        int lastTab = this.externSheet.getLastTabIndex(index);
        if (sr.getType() == SupbookRecord.INTERNAL) {
            if (lastTab == 65535) {
                return "#REF";
            }
            BoundsheetRecord br2 = (BoundsheetRecord) this.boundsheets.get(lastTab);
            return br2.getName();
        }
        if (sr.getType() == SupbookRecord.EXTERNAL) {
            StringBuffer sb = new StringBuffer();
            java.io.File fl = new java.io.File(sr.getFileName());
            sb.append("'");
            sb.append(fl.getAbsolutePath());
            sb.append("[");
            sb.append(fl.getName());
            sb.append("]");
            sb.append(lastTab != 65535 ? sr.getSheetName(lastTab) : "#REF");
            sb.append("'");
            return sb.toString();
        }
        logger.warn("Unknown Supbook 4");
        return "[UNKNOWN]";
    }

    @Override // jxl.Workbook
    public int getNumberOfSheets() {
        return this.sheets.size();
    }

    @Override // jxl.Workbook
    public void close() {
        SheetImpl sheetImpl = this.lastSheet;
        if (sheetImpl != null) {
            sheetImpl.clear();
        }
        this.excelFile.clear();
        if (!this.settings.getGCDisabled()) {
            System.gc();
        }
    }

    final void addSheet(Sheet s) {
        this.sheets.add(s);
    }

    @Override // jxl.Workbook
    protected void parse() throws BiffException, PasswordException {
        NameRecord nr;
        FontRecord fontRecord;
        FormatRecord formatRecord;
        XFRecord xFRecord;
        BoundsheetRecord br;
        Record r = null;
        BOFRecord bof = new BOFRecord(this.excelFile.next());
        this.workbookBof = bof;
        this.bofs++;
        if (!bof.isBiff8() && !bof.isBiff7()) {
            throw new BiffException(BiffException.unrecognizedBiffVersion);
        }
        if (!bof.isWorkbookGlobals()) {
            throw new BiffException(BiffException.expectedGlobals);
        }
        ArrayList continueRecords = new ArrayList();
        ArrayList localNames = new ArrayList();
        this.nameTable = new ArrayList();
        this.addInFunctions = new ArrayList();
        while (this.bofs == 1) {
            r = this.excelFile.next();
            if (r.getType() == Type.SST) {
                continueRecords.clear();
                Record nextrec = this.excelFile.peek();
                while (nextrec.getType() == Type.CONTINUE) {
                    continueRecords.add(this.excelFile.next());
                    nextrec = this.excelFile.peek();
                }
                Record[] records = new Record[continueRecords.size()];
                this.sharedStrings = new SSTRecord(r, (Record[]) continueRecords.toArray(records), this.settings);
            } else {
                if (r.getType() == Type.FILEPASS) {
                    throw new PasswordException();
                }
                if (r.getType() == Type.NAME) {
                    if (bof.isBiff8()) {
                        nr = new NameRecord(r, this.settings, this.nameTable.size());
                    } else {
                        nr = new NameRecord(r, this.settings, this.nameTable.size(), NameRecord.biff7);
                    }
                    this.nameTable.add(nr);
                    if (nr.isGlobal()) {
                        this.namedRecords.put(nr.getName(), nr);
                    } else {
                        localNames.add(nr);
                    }
                } else if (r.getType() == Type.FONT) {
                    if (bof.isBiff8()) {
                        fontRecord = new FontRecord(r, this.settings);
                    } else {
                        fontRecord = new FontRecord(r, this.settings, FontRecord.biff7);
                    }
                    FontRecord fr = fontRecord;
                    this.fonts.addFont(fr);
                } else if (r.getType() == Type.PALETTE) {
                    jxl.biff.PaletteRecord palette = new jxl.biff.PaletteRecord(r);
                    this.formattingRecords.setPalette(palette);
                } else if (r.getType() == Type.NINETEENFOUR) {
                    this.nineteenFour = new NineteenFourRecord(r).is1904();
                } else if (r.getType() == Type.FORMAT) {
                    if (bof.isBiff8()) {
                        formatRecord = new FormatRecord(r, this.settings, FormatRecord.biff8);
                    } else {
                        formatRecord = new FormatRecord(r, this.settings, FormatRecord.biff7);
                    }
                    try {
                        FormatRecord fr2 = formatRecord;
                        this.formattingRecords.addFormat(fr2);
                    } catch (NumFormatRecordsException e) {
                        Assert.verify(false, e.getMessage());
                    }
                } else if (r.getType() == Type.XF) {
                    if (bof.isBiff8()) {
                        xFRecord = new XFRecord(r, this.settings, XFRecord.biff8);
                    } else {
                        xFRecord = new XFRecord(r, this.settings, XFRecord.biff7);
                    }
                    try {
                        XFRecord xfr = xFRecord;
                        this.formattingRecords.addStyle(xfr);
                    } catch (NumFormatRecordsException e2) {
                        Assert.verify(false, e2.getMessage());
                    }
                } else if (r.getType() == Type.BOUNDSHEET) {
                    if (bof.isBiff8()) {
                        br = new BoundsheetRecord(r, this.settings);
                    } else {
                        br = new BoundsheetRecord(r, BoundsheetRecord.biff7);
                    }
                    if (br.isSheet()) {
                        this.boundsheets.add(br);
                    } else if (br.isChart() && !this.settings.getDrawingsDisabled()) {
                        this.boundsheets.add(br);
                    }
                } else if (r.getType() == Type.EXTERNSHEET) {
                    if (bof.isBiff8()) {
                        this.externSheet = new ExternalSheetRecord(r, this.settings);
                    } else {
                        this.externSheet = new ExternalSheetRecord(r, this.settings, ExternalSheetRecord.biff7);
                    }
                } else if (r.getType() == Type.XCT) {
                    XCTRecord xctr = new XCTRecord(r);
                    this.xctRecords.add(xctr);
                } else if (r.getType() == Type.CODEPAGE) {
                    CodepageRecord cr = new CodepageRecord(r);
                    this.settings.setCharacterSet(cr.getCharacterSet());
                } else if (r.getType() == Type.SUPBOOK) {
                    Record nextrec2 = this.excelFile.peek();
                    while (nextrec2.getType() == Type.CONTINUE) {
                        r.addContinueRecord(this.excelFile.next());
                        nextrec2 = this.excelFile.peek();
                    }
                    SupbookRecord sr = new SupbookRecord(r, this.settings);
                    this.supbooks.add(sr);
                } else if (r.getType() == Type.EXTERNNAME) {
                    ExternalNameRecord enr = new ExternalNameRecord(r, this.settings);
                    if (enr.isAddInFunction()) {
                        this.addInFunctions.add(enr.getName());
                    }
                } else if (r.getType() == Type.PROTECT) {
                    ProtectRecord pr = new ProtectRecord(r);
                    this.wbProtected = pr.isProtected();
                } else if (r.getType() == Type.OBJPROJ) {
                    this.containsMacros = true;
                } else if (r.getType() == Type.COUNTRY) {
                    this.countryRecord = new CountryRecord(r);
                } else if (r.getType() == Type.MSODRAWINGGROUP) {
                    if (!this.settings.getDrawingsDisabled()) {
                        this.msoDrawingGroup = new MsoDrawingGroupRecord(r);
                        if (this.drawingGroup == null) {
                            this.drawingGroup = new DrawingGroup(Origin.READ);
                        }
                        this.drawingGroup.add(this.msoDrawingGroup);
                        Record nextrec3 = this.excelFile.peek();
                        while (nextrec3.getType() == Type.CONTINUE) {
                            this.drawingGroup.add(this.excelFile.next());
                            nextrec3 = this.excelFile.peek();
                        }
                    }
                } else if (r.getType() == Type.BUTTONPROPERTYSET) {
                    this.buttonPropertySet = new ButtonPropertySetRecord(r);
                } else if (r.getType() == Type.EOF) {
                    this.bofs--;
                } else if (r.getType() == Type.REFRESHALL) {
                    RefreshAllRecord rfm = new RefreshAllRecord(r);
                    this.settings.setRefreshAll(rfm.getRefreshAll());
                } else if (r.getType() == Type.TEMPLATE) {
                    TemplateRecord rfm2 = new TemplateRecord(r);
                    this.settings.setTemplate(rfm2.getTemplate());
                } else if (r.getType() == Type.EXCEL9FILE) {
                    Excel9FileRecord e9f = new Excel9FileRecord(r);
                    this.settings.setExcel9File(e9f.getExcel9File());
                } else if (r.getType() == Type.WINDOWPROTECT) {
                    WindowProtectedRecord winp = new WindowProtectedRecord(r);
                    this.settings.setWindowProtected(winp.getWindowProtected());
                } else if (r.getType() == Type.HIDEOBJ) {
                    HideobjRecord hobj = new HideobjRecord(r);
                    this.settings.setHideobj(hobj.getHideMode());
                } else if (r.getType() == Type.WRITEACCESS) {
                    WriteAccessRecord war = new WriteAccessRecord(r, bof.isBiff8(), this.settings);
                    this.settings.setWriteAccess(war.getWriteAccess());
                }
            }
        }
        BOFRecord bof2 = null;
        if (this.excelFile.hasNext()) {
            r = this.excelFile.next();
            if (r.getType() == Type.BOF) {
                bof2 = new BOFRecord(r);
            }
        }
        while (bof2 != null && getNumberOfSheets() < this.boundsheets.size()) {
            if (!bof2.isBiff8() && !bof2.isBiff7()) {
                throw new BiffException(BiffException.unrecognizedBiffVersion);
            }
            if (bof2.isWorksheet()) {
                SheetImpl s = new SheetImpl(this.excelFile, this.sharedStrings, this.formattingRecords, bof2, this.workbookBof, this.nineteenFour, this);
                BoundsheetRecord br2 = (BoundsheetRecord) this.boundsheets.get(getNumberOfSheets());
                s.setName(br2.getName());
                s.setHidden(br2.isHidden());
                addSheet(s);
            } else if (bof2.isChart()) {
                SheetImpl s2 = new SheetImpl(this.excelFile, this.sharedStrings, this.formattingRecords, bof2, this.workbookBof, this.nineteenFour, this);
                BoundsheetRecord br3 = (BoundsheetRecord) this.boundsheets.get(getNumberOfSheets());
                s2.setName(br3.getName());
                s2.setHidden(br3.isHidden());
                addSheet(s2);
            } else {
                logger.warn("BOF is unrecognized");
                while (this.excelFile.hasNext() && r.getType() != Type.EOF) {
                    r = this.excelFile.next();
                }
            }
            bof2 = null;
            if (this.excelFile.hasNext()) {
                r = this.excelFile.next();
                if (r.getType() == Type.BOF) {
                    bof2 = new BOFRecord(r);
                }
            }
        }
        Iterator it = localNames.iterator();
        while (it.hasNext()) {
            NameRecord nr2 = (NameRecord) it.next();
            if (nr2.getBuiltInName() == null) {
                logger.warn("Usage of a local non-builtin name");
            } else if (nr2.getBuiltInName() == BuiltInName.PRINT_AREA || nr2.getBuiltInName() == BuiltInName.PRINT_TITLES) {
                ((SheetImpl) this.sheets.get(nr2.getSheetRef() - 1)).addLocalName(nr2);
            }
        }
    }

    public FormattingRecords getFormattingRecords() {
        return this.formattingRecords;
    }

    public ExternalSheetRecord getExternalSheetRecord() {
        return this.externSheet;
    }

    public MsoDrawingGroupRecord getMsoDrawingGroupRecord() {
        return this.msoDrawingGroup;
    }

    public SupbookRecord[] getSupbookRecords() {
        SupbookRecord[] sr = new SupbookRecord[this.supbooks.size()];
        return (SupbookRecord[]) this.supbooks.toArray(sr);
    }

    public NameRecord[] getNameRecords() {
        NameRecord[] na = new NameRecord[this.nameTable.size()];
        return (NameRecord[]) this.nameTable.toArray(na);
    }

    public Fonts getFonts() {
        return this.fonts;
    }

    @Override // jxl.Workbook
    public Cell getCell(String loc) {
        Sheet s = getSheet(CellReferenceHelper.getSheet(loc));
        return s.getCell(loc);
    }

    @Override // jxl.Workbook
    public Cell findCellByName(String name) {
        NameRecord nr = (NameRecord) this.namedRecords.get(name);
        if (nr == null) {
            return null;
        }
        NameRecord.NameRange[] ranges = nr.getRanges();
        Sheet s = getSheet(getExternalSheetIndex(ranges[0].getExternalSheet()));
        int col = ranges[0].getFirstColumn();
        int row = ranges[0].getFirstRow();
        if (col > s.getColumns() || row > s.getRows()) {
            return new EmptyCell(col, row);
        }
        Cell cell = s.getCell(col, row);
        return cell;
    }

    @Override // jxl.Workbook
    public Range[] findByName(String name) {
        NameRecord nr = (NameRecord) this.namedRecords.get(name);
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

    @Override // jxl.Workbook
    public String[] getRangeNames() {
        Object[] keys = this.namedRecords.keySet().toArray();
        String[] names = new String[keys.length];
        System.arraycopy(keys, 0, names, 0, keys.length);
        return names;
    }

    @Override // jxl.biff.formula.ExternalSheet
    public BOFRecord getWorkbookBof() {
        return this.workbookBof;
    }

    @Override // jxl.Workbook
    public boolean isProtected() {
        return this.wbProtected;
    }

    public WorkbookSettings getSettings() {
        return this.settings;
    }

    @Override // jxl.biff.formula.ExternalSheet
    public int getExternalSheetIndex(String sheetName) {
        return 0;
    }

    @Override // jxl.biff.formula.ExternalSheet
    public int getLastExternalSheetIndex(String sheetName) {
        return 0;
    }

    @Override // jxl.biff.WorkbookMethods
    public String getName(int index) throws NameRangeException {
        if (index < 0 || index >= this.nameTable.size()) {
            throw new NameRangeException();
        }
        return ((NameRecord) this.nameTable.get(index)).getName();
    }

    @Override // jxl.biff.WorkbookMethods
    public int getNameIndex(String name) {
        NameRecord nr = (NameRecord) this.namedRecords.get(name);
        if (nr != null) {
            return nr.getIndex();
        }
        return 0;
    }

    public DrawingGroup getDrawingGroup() {
        return this.drawingGroup;
    }

    public CompoundFile getCompoundFile() {
        return this.excelFile.getCompoundFile();
    }

    public boolean containsMacros() {
        return this.containsMacros;
    }

    public ButtonPropertySetRecord getButtonPropertySet() {
        return this.buttonPropertySet;
    }

    public CountryRecord getCountryRecord() {
        return this.countryRecord;
    }

    public String[] getAddInFunctionNames() {
        String[] addins = new String[0];
        return (String[]) this.addInFunctions.toArray(addins);
    }

    public int getIndex(Sheet sheet) {
        String name = sheet.getName();
        int index = -1;
        int pos = 0;
        Iterator i = this.boundsheets.iterator();
        while (i.hasNext() && index == -1) {
            BoundsheetRecord br = (BoundsheetRecord) i.next();
            if (br.getName().equals(name)) {
                index = pos;
            } else {
                pos++;
            }
        }
        return index;
    }

    public XCTRecord[] getXCTRecords() {
        XCTRecord[] xctr = new XCTRecord[0];
        return (XCTRecord[]) this.xctRecords.toArray(xctr);
    }
}
