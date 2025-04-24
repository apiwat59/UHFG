package jxl.write.biff;

import jxl.CellReferenceHelper;
import jxl.CellType;
import jxl.Sheet;
import jxl.WorkbookSettings;
import jxl.biff.FormattingRecords;
import jxl.biff.FormulaData;
import jxl.biff.IntegerHelper;
import jxl.biff.Type;
import jxl.biff.WorkbookMethods;
import jxl.biff.formula.ExternalSheet;
import jxl.biff.formula.FormulaException;
import jxl.biff.formula.FormulaParser;
import jxl.common.Assert;
import jxl.common.Logger;
import jxl.format.CellFormat;
import jxl.write.WritableCell;

/* loaded from: classes.dex */
public class FormulaRecord extends CellValue implements FormulaData {
    private static Logger logger = Logger.getLogger(FormulaRecord.class);
    private CellValue copiedFrom;
    private byte[] formulaBytes;
    private String formulaString;
    private String formulaToParse;
    private FormulaParser parser;

    public FormulaRecord(int c, int r, String f) {
        super(Type.FORMULA2, c, r);
        this.formulaToParse = f;
        this.copiedFrom = null;
    }

    public FormulaRecord(int c, int r, String f, CellFormat st) {
        super(Type.FORMULA, c, r, st);
        this.formulaToParse = f;
        this.copiedFrom = null;
    }

    protected FormulaRecord(int c, int r, FormulaRecord fr) {
        super(Type.FORMULA, c, r, fr);
        this.copiedFrom = fr;
        byte[] bArr = new byte[fr.formulaBytes.length];
        this.formulaBytes = bArr;
        System.arraycopy(fr.formulaBytes, 0, bArr, 0, bArr.length);
    }

    protected FormulaRecord(int c, int r, ReadFormulaRecord rfr) {
        super(Type.FORMULA, c, r, rfr);
        try {
            this.copiedFrom = rfr;
            this.formulaBytes = rfr.getFormulaBytes();
        } catch (FormulaException e) {
            logger.error("", e);
        }
    }

    private void initialize(WorkbookSettings ws, ExternalSheet es, WorkbookMethods nt) {
        if (this.copiedFrom != null) {
            initializeCopiedFormula(ws, es, nt);
            return;
        }
        FormulaParser formulaParser = new FormulaParser(this.formulaToParse, es, nt, ws);
        this.parser = formulaParser;
        try {
            formulaParser.parse();
            this.formulaString = this.parser.getFormula();
            this.formulaBytes = this.parser.getBytes();
        } catch (FormulaException e) {
            logger.warn(e.getMessage() + " when parsing formula " + this.formulaToParse + " in cell " + getSheet().getName() + "!" + CellReferenceHelper.getCellReference(getColumn(), getRow()));
            try {
                this.formulaToParse = "ERROR(1)";
                FormulaParser formulaParser2 = new FormulaParser(this.formulaToParse, es, nt, ws);
                this.parser = formulaParser2;
                formulaParser2.parse();
                this.formulaString = this.parser.getFormula();
                this.formulaBytes = this.parser.getBytes();
            } catch (FormulaException e2) {
                logger.error("", e2);
            }
        }
    }

    private void initializeCopiedFormula(WorkbookSettings ws, ExternalSheet es, WorkbookMethods nt) {
        try {
            FormulaParser formulaParser = new FormulaParser(this.formulaBytes, this, es, nt, ws);
            this.parser = formulaParser;
            formulaParser.parse();
            this.parser.adjustRelativeCellReferences(getColumn() - this.copiedFrom.getColumn(), getRow() - this.copiedFrom.getRow());
            this.formulaString = this.parser.getFormula();
            this.formulaBytes = this.parser.getBytes();
        } catch (FormulaException e) {
            try {
                this.formulaToParse = "ERROR(1)";
                FormulaParser formulaParser2 = new FormulaParser(this.formulaToParse, es, nt, ws);
                this.parser = formulaParser2;
                formulaParser2.parse();
                this.formulaString = this.parser.getFormula();
                this.formulaBytes = this.parser.getBytes();
            } catch (FormulaException e2) {
                logger.error("", e2);
            }
        }
    }

    @Override // jxl.write.biff.CellValue
    void setCellDetails(FormattingRecords fr, SharedStrings ss, WritableSheetImpl s) {
        super.setCellDetails(fr, ss, s);
        initialize(s.getWorkbookSettings(), s.getWorkbook(), s.getWorkbook());
        s.getWorkbook().addRCIRCell(this);
    }

    @Override // jxl.write.biff.CellValue, jxl.biff.WritableRecordData
    public byte[] getData() {
        byte[] celldata = super.getData();
        byte[] formulaData = getFormulaData();
        byte[] data = new byte[formulaData.length + celldata.length];
        System.arraycopy(celldata, 0, data, 0, celldata.length);
        System.arraycopy(formulaData, 0, data, celldata.length, formulaData.length);
        return data;
    }

    @Override // jxl.Cell
    public CellType getType() {
        return CellType.ERROR;
    }

    @Override // jxl.Cell
    public String getContents() {
        return this.formulaString;
    }

    @Override // jxl.biff.FormulaData
    public byte[] getFormulaData() {
        byte[] bArr = this.formulaBytes;
        byte[] data = new byte[bArr.length + 16];
        System.arraycopy(bArr, 0, data, 16, bArr.length);
        data[6] = 16;
        data[7] = 64;
        data[12] = -32;
        data[13] = -4;
        data[8] = (byte) (data[8] | 2);
        IntegerHelper.getTwoBytes(this.formulaBytes.length, data, 14);
        return data;
    }

    public WritableCell copyTo(int col, int row) {
        Assert.verify(false);
        return null;
    }

    @Override // jxl.write.biff.CellValue
    void columnInserted(Sheet s, int sheetIndex, int col) {
        this.parser.columnInserted(sheetIndex, col, s == getSheet());
        this.formulaBytes = this.parser.getBytes();
    }

    @Override // jxl.write.biff.CellValue
    void columnRemoved(Sheet s, int sheetIndex, int col) {
        this.parser.columnRemoved(sheetIndex, col, s == getSheet());
        this.formulaBytes = this.parser.getBytes();
    }

    @Override // jxl.write.biff.CellValue
    void rowInserted(Sheet s, int sheetIndex, int row) {
        this.parser.rowInserted(sheetIndex, row, s == getSheet());
        this.formulaBytes = this.parser.getBytes();
    }

    @Override // jxl.write.biff.CellValue
    void rowRemoved(Sheet s, int sheetIndex, int row) {
        this.parser.rowRemoved(sheetIndex, row, s == getSheet());
        this.formulaBytes = this.parser.getBytes();
    }
}
