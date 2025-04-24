package jxl.write.biff;

import jxl.CellReferenceHelper;
import jxl.CellType;
import jxl.FormulaCell;
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
import jxl.write.WritableCell;

/* loaded from: classes.dex */
class ReadFormulaRecord extends CellValue implements FormulaData {
    private static Logger logger = Logger.getLogger(ReadFormulaRecord.class);
    private FormulaData formula;
    private FormulaParser parser;

    protected ReadFormulaRecord(FormulaData f) {
        super(Type.FORMULA, f);
        this.formula = f;
    }

    protected final byte[] getCellData() {
        return super.getData();
    }

    protected byte[] handleFormulaException() {
        byte[] celldata = super.getData();
        WritableWorkbookImpl w = getSheet().getWorkbook();
        FormulaParser formulaParser = new FormulaParser(getContents(), w, w, w.getSettings());
        this.parser = formulaParser;
        try {
            formulaParser.parse();
        } catch (FormulaException e2) {
            logger.warn(e2.getMessage());
            FormulaParser formulaParser2 = new FormulaParser("\"ERROR\"", w, w, w.getSettings());
            this.parser = formulaParser2;
            try {
                formulaParser2.parse();
            } catch (FormulaException e) {
                Assert.verify(false);
            }
        }
        byte[] formulaBytes = this.parser.getBytes();
        byte[] expressiondata = new byte[formulaBytes.length + 16];
        IntegerHelper.getTwoBytes(formulaBytes.length, expressiondata, 14);
        System.arraycopy(formulaBytes, 0, expressiondata, 16, formulaBytes.length);
        expressiondata[8] = (byte) (expressiondata[8] | 2);
        byte[] data = new byte[celldata.length + expressiondata.length];
        System.arraycopy(celldata, 0, data, 0, celldata.length);
        System.arraycopy(expressiondata, 0, data, celldata.length, expressiondata.length);
        return data;
    }

    @Override // jxl.write.biff.CellValue, jxl.biff.WritableRecordData
    public byte[] getData() {
        byte[] expressiondata;
        byte[] celldata = super.getData();
        try {
            FormulaParser formulaParser = this.parser;
            if (formulaParser == null) {
                expressiondata = this.formula.getFormulaData();
            } else {
                byte[] formulaBytes = formulaParser.getBytes();
                expressiondata = new byte[formulaBytes.length + 16];
                IntegerHelper.getTwoBytes(formulaBytes.length, expressiondata, 14);
                System.arraycopy(formulaBytes, 0, expressiondata, 16, formulaBytes.length);
            }
            expressiondata[8] = (byte) (expressiondata[8] | 2);
            byte[] data = new byte[celldata.length + expressiondata.length];
            System.arraycopy(celldata, 0, data, 0, celldata.length);
            System.arraycopy(expressiondata, 0, data, celldata.length, expressiondata.length);
            return data;
        } catch (FormulaException e) {
            logger.warn(CellReferenceHelper.getCellReference(getColumn(), getRow()) + " " + e.getMessage());
            return handleFormulaException();
        }
    }

    @Override // jxl.Cell
    public CellType getType() {
        return this.formula.getType();
    }

    @Override // jxl.Cell
    public String getContents() {
        return this.formula.getContents();
    }

    @Override // jxl.biff.FormulaData
    public byte[] getFormulaData() throws FormulaException {
        byte[] d = this.formula.getFormulaData();
        byte[] data = new byte[d.length];
        System.arraycopy(d, 0, data, 0, d.length);
        data[8] = (byte) (data[8] | 2);
        return data;
    }

    public byte[] getFormulaBytes() throws FormulaException {
        FormulaParser formulaParser = this.parser;
        if (formulaParser != null) {
            return formulaParser.getBytes();
        }
        byte[] readFormulaData = getFormulaData();
        byte[] formulaBytes = new byte[readFormulaData.length - 16];
        System.arraycopy(readFormulaData, 16, formulaBytes, 0, formulaBytes.length);
        return formulaBytes;
    }

    @Override // jxl.write.WritableCell
    public WritableCell copyTo(int col, int row) {
        return new FormulaRecord(col, row, this);
    }

    @Override // jxl.write.biff.CellValue
    void setCellDetails(FormattingRecords fr, SharedStrings ss, WritableSheetImpl s) {
        super.setCellDetails(fr, ss, s);
        s.getWorkbook().addRCIRCell(this);
    }

    @Override // jxl.write.biff.CellValue
    void columnInserted(Sheet s, int sheetIndex, int col) {
        try {
            if (this.parser == null) {
                byte[] formulaData = this.formula.getFormulaData();
                byte[] formulaBytes = new byte[formulaData.length - 16];
                System.arraycopy(formulaData, 16, formulaBytes, 0, formulaBytes.length);
                FormulaParser formulaParser = new FormulaParser(formulaBytes, this, getSheet().getWorkbook(), getSheet().getWorkbook(), getSheet().getWorkbookSettings());
                this.parser = formulaParser;
                formulaParser.parse();
            }
            this.parser.columnInserted(sheetIndex, col, s == getSheet());
        } catch (FormulaException e) {
            logger.warn("cannot insert column within formula:  " + e.getMessage());
        }
    }

    @Override // jxl.write.biff.CellValue
    void columnRemoved(Sheet s, int sheetIndex, int col) {
        try {
            if (this.parser == null) {
                byte[] formulaData = this.formula.getFormulaData();
                byte[] formulaBytes = new byte[formulaData.length - 16];
                System.arraycopy(formulaData, 16, formulaBytes, 0, formulaBytes.length);
                FormulaParser formulaParser = new FormulaParser(formulaBytes, this, getSheet().getWorkbook(), getSheet().getWorkbook(), getSheet().getWorkbookSettings());
                this.parser = formulaParser;
                formulaParser.parse();
            }
            this.parser.columnRemoved(sheetIndex, col, s == getSheet());
        } catch (FormulaException e) {
            logger.warn("cannot remove column within formula:  " + e.getMessage());
        }
    }

    @Override // jxl.write.biff.CellValue
    void rowInserted(Sheet s, int sheetIndex, int row) {
        try {
            if (this.parser == null) {
                byte[] formulaData = this.formula.getFormulaData();
                byte[] formulaBytes = new byte[formulaData.length - 16];
                System.arraycopy(formulaData, 16, formulaBytes, 0, formulaBytes.length);
                FormulaParser formulaParser = new FormulaParser(formulaBytes, this, getSheet().getWorkbook(), getSheet().getWorkbook(), getSheet().getWorkbookSettings());
                this.parser = formulaParser;
                formulaParser.parse();
            }
            this.parser.rowInserted(sheetIndex, row, s == getSheet());
        } catch (FormulaException e) {
            logger.warn("cannot insert row within formula:  " + e.getMessage());
        }
    }

    @Override // jxl.write.biff.CellValue
    void rowRemoved(Sheet s, int sheetIndex, int row) {
        try {
            if (this.parser == null) {
                byte[] formulaData = this.formula.getFormulaData();
                byte[] formulaBytes = new byte[formulaData.length - 16];
                System.arraycopy(formulaData, 16, formulaBytes, 0, formulaBytes.length);
                FormulaParser formulaParser = new FormulaParser(formulaBytes, this, getSheet().getWorkbook(), getSheet().getWorkbook(), getSheet().getWorkbookSettings());
                this.parser = formulaParser;
                formulaParser.parse();
            }
            this.parser.rowRemoved(sheetIndex, row, s == getSheet());
        } catch (FormulaException e) {
            logger.warn("cannot remove row within formula:  " + e.getMessage());
        }
    }

    protected FormulaData getReadFormula() {
        return this.formula;
    }

    public String getFormula() throws FormulaException {
        return ((FormulaCell) this.formula).getFormula();
    }

    public boolean handleImportedCellReferences(ExternalSheet es, WorkbookMethods mt, WorkbookSettings ws) {
        try {
            if (this.parser == null) {
                byte[] formulaData = this.formula.getFormulaData();
                byte[] formulaBytes = new byte[formulaData.length - 16];
                System.arraycopy(formulaData, 16, formulaBytes, 0, formulaBytes.length);
                FormulaParser formulaParser = new FormulaParser(formulaBytes, this, es, mt, ws);
                this.parser = formulaParser;
                formulaParser.parse();
            }
            return this.parser.handleImportedCellReferences();
        } catch (FormulaException e) {
            logger.warn("cannot import formula:  " + e.getMessage());
            return false;
        }
    }
}
