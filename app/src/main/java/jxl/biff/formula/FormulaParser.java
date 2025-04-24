package jxl.biff.formula;

import jxl.Cell;
import jxl.WorkbookSettings;
import jxl.biff.WorkbookMethods;
import jxl.common.Assert;
import jxl.common.Logger;

/* loaded from: classes.dex */
public class FormulaParser {
    private static final Logger logger = Logger.getLogger(FormulaParser.class);
    private Parser parser;

    public FormulaParser(byte[] tokens, Cell rt, ExternalSheet es, WorkbookMethods nt, WorkbookSettings ws) throws FormulaException {
        if (es.getWorkbookBof() != null && !es.getWorkbookBof().isBiff8()) {
            throw new FormulaException(FormulaException.BIFF8_SUPPORTED);
        }
        Assert.verify(nt != null);
        this.parser = new TokenFormulaParser(tokens, rt, es, nt, ws, ParseContext.DEFAULT);
    }

    public FormulaParser(byte[] tokens, Cell rt, ExternalSheet es, WorkbookMethods nt, WorkbookSettings ws, ParseContext pc) throws FormulaException {
        if (es.getWorkbookBof() != null && !es.getWorkbookBof().isBiff8()) {
            throw new FormulaException(FormulaException.BIFF8_SUPPORTED);
        }
        Assert.verify(nt != null);
        this.parser = new TokenFormulaParser(tokens, rt, es, nt, ws, pc);
    }

    public FormulaParser(String form, ExternalSheet es, WorkbookMethods nt, WorkbookSettings ws) {
        this.parser = new StringFormulaParser(form, es, nt, ws, ParseContext.DEFAULT);
    }

    public FormulaParser(String form, ExternalSheet es, WorkbookMethods nt, WorkbookSettings ws, ParseContext pc) {
        this.parser = new StringFormulaParser(form, es, nt, ws, pc);
    }

    public void adjustRelativeCellReferences(int colAdjust, int rowAdjust) {
        this.parser.adjustRelativeCellReferences(colAdjust, rowAdjust);
    }

    public void parse() throws FormulaException {
        this.parser.parse();
    }

    public String getFormula() throws FormulaException {
        return this.parser.getFormula();
    }

    public byte[] getBytes() {
        return this.parser.getBytes();
    }

    public void columnInserted(int sheetIndex, int col, boolean currentSheet) {
        this.parser.columnInserted(sheetIndex, col, currentSheet);
    }

    public void columnRemoved(int sheetIndex, int col, boolean currentSheet) {
        this.parser.columnRemoved(sheetIndex, col, currentSheet);
    }

    public void rowInserted(int sheetIndex, int row, boolean currentSheet) {
        this.parser.rowInserted(sheetIndex, row, currentSheet);
    }

    public void rowRemoved(int sheetIndex, int row, boolean currentSheet) {
        this.parser.rowRemoved(sheetIndex, row, currentSheet);
    }

    public boolean handleImportedCellReferences() {
        return this.parser.handleImportedCellReferences();
    }
}
