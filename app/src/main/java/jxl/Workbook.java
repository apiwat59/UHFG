package jxl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import jxl.read.biff.BiffException;
import jxl.read.biff.PasswordException;
import jxl.read.biff.WorkbookParser;
import jxl.write.WritableWorkbook;
import jxl.write.biff.WritableWorkbookImpl;

/* loaded from: classes.dex */
public abstract class Workbook {
    private static final String VERSION = "2.6.12";

    public abstract void close();

    public abstract Range[] findByName(String str);

    public abstract Cell findCellByName(String str);

    public abstract Cell getCell(String str);

    public abstract int getNumberOfSheets();

    public abstract String[] getRangeNames();

    public abstract Sheet getSheet(int i) throws IndexOutOfBoundsException;

    public abstract Sheet getSheet(String str);

    public abstract String[] getSheetNames();

    public abstract Sheet[] getSheets();

    public abstract boolean isProtected();

    protected abstract void parse() throws BiffException, PasswordException;

    protected Workbook() {
    }

    public static String getVersion() {
        return VERSION;
    }

    public static Workbook getWorkbook(File file) throws IOException, BiffException {
        return getWorkbook(file, new WorkbookSettings());
    }

    public static Workbook getWorkbook(File file, WorkbookSettings ws) throws IOException, BiffException {
        FileInputStream fis = new FileInputStream(file);
        try {
            jxl.read.biff.File dataFile = new jxl.read.biff.File(fis, ws);
            fis.close();
            Workbook workbook = new WorkbookParser(dataFile, ws);
            workbook.parse();
            return workbook;
        } catch (IOException e) {
            fis.close();
            throw e;
        } catch (BiffException e2) {
            fis.close();
            throw e2;
        }
    }

    public static Workbook getWorkbook(InputStream is) throws IOException, BiffException {
        return getWorkbook(is, new WorkbookSettings());
    }

    public static Workbook getWorkbook(InputStream is, WorkbookSettings ws) throws IOException, BiffException {
        jxl.read.biff.File dataFile = new jxl.read.biff.File(is, ws);
        Workbook workbook = new WorkbookParser(dataFile, ws);
        workbook.parse();
        return workbook;
    }

    public static WritableWorkbook createWorkbook(File file) throws IOException {
        return createWorkbook(file, new WorkbookSettings());
    }

    public static WritableWorkbook createWorkbook(File file, WorkbookSettings ws) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        WritableWorkbook w = new WritableWorkbookImpl(fos, true, ws);
        return w;
    }

    public static WritableWorkbook createWorkbook(File file, Workbook in) throws IOException {
        return createWorkbook(file, in, new WorkbookSettings());
    }

    public static WritableWorkbook createWorkbook(File file, Workbook in, WorkbookSettings ws) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        WritableWorkbook w = new WritableWorkbookImpl(fos, in, true, ws);
        return w;
    }

    public static WritableWorkbook createWorkbook(OutputStream os, Workbook in) throws IOException {
        return createWorkbook(os, in, ((WorkbookParser) in).getSettings());
    }

    public static WritableWorkbook createWorkbook(OutputStream os, Workbook in, WorkbookSettings ws) throws IOException {
        WritableWorkbook w = new WritableWorkbookImpl(os, in, false, ws);
        return w;
    }

    public static WritableWorkbook createWorkbook(OutputStream os) throws IOException {
        return createWorkbook(os, new WorkbookSettings());
    }

    public static WritableWorkbook createWorkbook(OutputStream os, WorkbookSettings ws) throws IOException {
        WritableWorkbook w = new WritableWorkbookImpl(os, false, ws);
        return w;
    }
}
