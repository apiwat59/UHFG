package jxl.read.biff;

import jxl.CellType;
import jxl.LabelCell;
import jxl.StringFormulaCell;
import jxl.WorkbookSettings;
import jxl.biff.FormattingRecords;
import jxl.biff.FormulaData;
import jxl.biff.IntegerHelper;
import jxl.biff.StringHelper;
import jxl.biff.Type;
import jxl.biff.WorkbookMethods;
import jxl.biff.formula.ExternalSheet;
import jxl.biff.formula.FormulaException;
import jxl.biff.formula.FormulaParser;
import jxl.common.Assert;
import jxl.common.Logger;

/* loaded from: classes.dex */
class StringFormulaRecord extends CellValue implements LabelCell, FormulaData, StringFormulaCell {
    private static Logger logger = Logger.getLogger(StringFormulaRecord.class);
    private byte[] data;
    private ExternalSheet externalSheet;
    private String formulaString;
    private WorkbookMethods nameTable;
    private String value;

    public StringFormulaRecord(Record t, File excelFile, FormattingRecords fr, ExternalSheet es, WorkbookMethods nt, SheetImpl si, WorkbookSettings ws) {
        super(t, fr, si);
        this.externalSheet = es;
        this.nameTable = nt;
        this.data = getRecord().getData();
        int pos = excelFile.getPos();
        Record nextRecord = excelFile.next();
        int count = 0;
        while (nextRecord.getType() != Type.STRING && count < 4) {
            nextRecord = excelFile.next();
            count++;
        }
        Assert.verify(count < 4, " @ " + pos);
        byte[] stringData = nextRecord.getData();
        Record nextRecord2 = excelFile.peek();
        while (nextRecord2.getType() == Type.CONTINUE) {
            Record nextRecord3 = excelFile.next();
            byte[] d = new byte[(stringData.length + nextRecord3.getLength()) - 1];
            System.arraycopy(stringData, 0, d, 0, stringData.length);
            System.arraycopy(nextRecord3.getData(), 1, d, stringData.length, nextRecord3.getLength() - 1);
            stringData = d;
            nextRecord2 = excelFile.peek();
        }
        readString(stringData, ws);
    }

    public StringFormulaRecord(Record t, FormattingRecords fr, ExternalSheet es, WorkbookMethods nt, SheetImpl si) {
        super(t, fr, si);
        this.externalSheet = es;
        this.nameTable = nt;
        this.data = getRecord().getData();
        this.value = "";
    }

    private void readString(byte[] d, WorkbookSettings ws) {
        int chars = IntegerHelper.getInt(d[0], d[1]);
        if (chars == 0) {
            this.value = "";
            return;
        }
        int pos = 0 + 2;
        int optionFlags = d[pos];
        int pos2 = pos + 1;
        if ((optionFlags & 15) != optionFlags) {
            chars = IntegerHelper.getInt(d[0], (byte) 0);
            optionFlags = d[1];
            pos2 = 2;
        }
        boolean extendedString = (optionFlags & 4) != 0;
        boolean richString = (optionFlags & 8) != 0;
        if (richString) {
            pos2 += 2;
        }
        if (extendedString) {
            pos2 += 4;
        }
        boolean asciiEncoding = (optionFlags & 1) == 0;
        if (asciiEncoding) {
            this.value = StringHelper.getString(d, chars, pos2, ws);
        } else {
            this.value = StringHelper.getUnicodeString(d, chars, pos2);
        }
    }

    @Override // jxl.Cell
    public String getContents() {
        return this.value;
    }

    @Override // jxl.LabelCell
    public String getString() {
        return this.value;
    }

    @Override // jxl.Cell
    public CellType getType() {
        return CellType.STRING_FORMULA;
    }

    @Override // jxl.biff.FormulaData
    public byte[] getFormulaData() throws FormulaException {
        if (!getSheet().getWorkbook().getWorkbookBof().isBiff8()) {
            throw new FormulaException(FormulaException.BIFF8_SUPPORTED);
        }
        byte[] bArr = this.data;
        byte[] d = new byte[bArr.length - 6];
        System.arraycopy(bArr, 6, d, 0, bArr.length - 6);
        return d;
    }

    @Override // jxl.FormulaCell
    public String getFormula() throws FormulaException {
        if (this.formulaString == null) {
            byte[] bArr = this.data;
            byte[] tokens = new byte[bArr.length - 22];
            System.arraycopy(bArr, 22, tokens, 0, tokens.length);
            FormulaParser fp = new FormulaParser(tokens, this, this.externalSheet, this.nameTable, getSheet().getWorkbook().getSettings());
            fp.parse();
            this.formulaString = fp.getFormula();
        }
        return this.formulaString;
    }
}
