package jxl;

import jxl.biff.formula.ExternalSheet;
import jxl.write.WritableWorkbook;

/* loaded from: classes.dex */
public final class CellReferenceHelper {
    private CellReferenceHelper() {
    }

    public static void getCellReference(int column, int row, StringBuffer buf) {
        jxl.biff.CellReferenceHelper.getCellReference(column, row, buf);
    }

    public static void getCellReference(int column, boolean colabs, int row, boolean rowabs, StringBuffer buf) {
        jxl.biff.CellReferenceHelper.getCellReference(column, colabs, row, rowabs, buf);
    }

    public static String getCellReference(int column, int row) {
        return jxl.biff.CellReferenceHelper.getCellReference(column, row);
    }

    public static int getColumn(String s) {
        return jxl.biff.CellReferenceHelper.getColumn(s);
    }

    public static String getColumnReference(int c) {
        return jxl.biff.CellReferenceHelper.getColumnReference(c);
    }

    public static int getRow(String s) {
        return jxl.biff.CellReferenceHelper.getRow(s);
    }

    public static boolean isColumnRelative(String s) {
        return jxl.biff.CellReferenceHelper.isColumnRelative(s);
    }

    public static boolean isRowRelative(String s) {
        return jxl.biff.CellReferenceHelper.isRowRelative(s);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static void getCellReference(int sheet, int column, int row, Workbook workbook, StringBuffer buf) {
        jxl.biff.CellReferenceHelper.getCellReference(sheet, column, row, (ExternalSheet) workbook, buf);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static void getCellReference(int sheet, int column, int row, WritableWorkbook writableWorkbook, StringBuffer buf) {
        jxl.biff.CellReferenceHelper.getCellReference(sheet, column, row, (ExternalSheet) writableWorkbook, buf);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static void getCellReference(int sheet, int column, boolean colabs, int row, boolean rowabs, Workbook workbook, StringBuffer buf) {
        jxl.biff.CellReferenceHelper.getCellReference(sheet, column, colabs, row, rowabs, (ExternalSheet) workbook, buf);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static String getCellReference(int sheet, int column, int row, Workbook workbook) {
        return jxl.biff.CellReferenceHelper.getCellReference(sheet, column, row, (ExternalSheet) workbook);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static String getCellReference(int sheet, int column, int row, WritableWorkbook writableWorkbook) {
        return jxl.biff.CellReferenceHelper.getCellReference(sheet, column, row, (ExternalSheet) writableWorkbook);
    }

    public static String getSheet(String ref) {
        return jxl.biff.CellReferenceHelper.getSheet(ref);
    }

    public static String getCellReference(Cell c) {
        return getCellReference(c.getColumn(), c.getRow());
    }

    public static void getCellReference(Cell c, StringBuffer sb) {
        getCellReference(c.getColumn(), c.getRow(), sb);
    }
}
