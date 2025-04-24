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
public class SharedStringFormulaRecord extends BaseSharedFormulaRecord implements LabelCell, FormulaData, StringFormulaCell {
    private String value;
    private static Logger logger = Logger.getLogger(SharedStringFormulaRecord.class);
    protected static final EmptyString EMPTY_STRING = new EmptyString();

    /* JADX INFO: Access modifiers changed from: private */
    static final class EmptyString {
        private EmptyString() {
        }
    }

    public SharedStringFormulaRecord(Record t, File excelFile, FormattingRecords fr, ExternalSheet es, WorkbookMethods nt, SheetImpl si, WorkbookSettings ws) {
        super(t, fr, es, nt, si, excelFile.getPos());
        int startpos;
        boolean unicode;
        int pos = excelFile.getPos();
        int filepos = excelFile.getPos();
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
        int chars = IntegerHelper.getInt(stringData[0], stringData[1]);
        if (stringData.length == chars + 2) {
            startpos = 2;
            unicode = false;
        } else if (stringData[2] == 1) {
            startpos = 3;
            unicode = true;
        } else {
            startpos = 3;
            unicode = false;
        }
        if (!unicode) {
            this.value = StringHelper.getString(stringData, chars, startpos, ws);
        } else {
            this.value = StringHelper.getUnicodeString(stringData, chars, startpos);
        }
        excelFile.setPos(filepos);
    }

    public SharedStringFormulaRecord(Record t, File excelFile, FormattingRecords fr, ExternalSheet es, WorkbookMethods nt, SheetImpl si, EmptyString dummy) {
        super(t, fr, es, nt, si, excelFile.getPos());
        this.value = "";
    }

    @Override // jxl.LabelCell
    public String getString() {
        return this.value;
    }

    @Override // jxl.Cell
    public String getContents() {
        return this.value;
    }

    @Override // jxl.Cell
    public CellType getType() {
        return CellType.STRING_FORMULA;
    }

    @Override // jxl.biff.FormulaData
    public byte[] getFormulaData() throws FormulaException {
        if (!getSheet().getWorkbookBof().isBiff8()) {
            throw new FormulaException(FormulaException.BIFF8_SUPPORTED);
        }
        FormulaParser fp = new FormulaParser(getTokens(), this, getExternalSheet(), getNameTable(), getSheet().getWorkbook().getSettings());
        fp.parse();
        byte[] rpnTokens = fp.getBytes();
        byte[] data = new byte[rpnTokens.length + 22];
        IntegerHelper.getTwoBytes(getRow(), data, 0);
        IntegerHelper.getTwoBytes(getColumn(), data, 2);
        IntegerHelper.getTwoBytes(getXFIndex(), data, 4);
        data[6] = 0;
        data[12] = -1;
        data[13] = -1;
        System.arraycopy(rpnTokens, 0, data, 22, rpnTokens.length);
        IntegerHelper.getTwoBytes(rpnTokens.length, data, 20);
        byte[] d = new byte[data.length - 6];
        System.arraycopy(data, 6, d, 0, data.length - 6);
        return d;
    }
}
