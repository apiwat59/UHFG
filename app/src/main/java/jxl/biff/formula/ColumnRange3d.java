package jxl.biff.formula;

import androidx.core.internal.view.SupportMenu;
import jxl.biff.CellReferenceHelper;
import jxl.common.Assert;
import jxl.common.Logger;

/* loaded from: classes.dex */
class ColumnRange3d extends Area3d {
    private static Logger logger = Logger.getLogger(ColumnRange3d.class);
    private int sheet;
    private ExternalSheet workbook;

    ColumnRange3d(ExternalSheet es) {
        super(es);
        this.workbook = es;
    }

    ColumnRange3d(String s, ExternalSheet es) throws FormulaException {
        super(es);
        String sheetName;
        this.workbook = es;
        int seppos = s.lastIndexOf(":");
        Assert.verify(seppos != -1);
        s.substring(0, seppos);
        String endcell = s.substring(seppos + 1);
        int sep = s.indexOf(33);
        String cellString = s.substring(sep + 1, seppos);
        int columnFirst = CellReferenceHelper.getColumn(cellString);
        String sheetName2 = s.substring(0, sep);
        sheetName2.lastIndexOf(93);
        if (sheetName2.charAt(0) == '\'' && sheetName2.charAt(sheetName2.length() - 1) == '\'') {
            sheetName = sheetName2.substring(1, sheetName2.length() - 1);
        } else {
            sheetName = sheetName2;
        }
        int externalSheetIndex = es.getExternalSheetIndex(sheetName);
        this.sheet = externalSheetIndex;
        if (externalSheetIndex < 0) {
            throw new FormulaException(FormulaException.SHEET_REF_NOT_FOUND, sheetName);
        }
        int columnLast = CellReferenceHelper.getColumn(endcell);
        setRangeData(this.sheet, columnFirst, columnLast, 0, SupportMenu.USER_MASK, true, true, true, true);
    }

    @Override // jxl.biff.formula.Area3d, jxl.biff.formula.ParseItem
    public void getString(StringBuffer buf) {
        buf.append('\'');
        buf.append(this.workbook.getExternalSheetName(this.sheet));
        buf.append('\'');
        buf.append('!');
        CellReferenceHelper.getColumnReference(getFirstColumn(), buf);
        buf.append(':');
        CellReferenceHelper.getColumnReference(getLastColumn(), buf);
    }
}
