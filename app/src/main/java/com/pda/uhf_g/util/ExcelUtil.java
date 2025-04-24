package com.pda.uhf_g.util;

import android.os.Environment;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import me.weyye.hipermission.PermissionCallback;

/* loaded from: classes.dex */
public class ExcelUtil {
    public static final String GBK_ENCODING = "GBK";
    public static final String UTF8_ENCODING = "UTF-8";
    public static WritableFont arial14font = null;
    public static WritableCellFormat arial14format = null;
    public static WritableFont arial10font = null;
    public static WritableCellFormat arial10format = null;
    public static WritableFont arial12font = null;
    public static WritableCellFormat arial12format = null;

    public static void format() {
        try {
            WritableFont writableFont = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD);
            arial14font = writableFont;
            writableFont.setColour(Colour.LIGHT_BLUE);
            WritableCellFormat writableCellFormat = new WritableCellFormat(arial14font);
            arial14format = writableCellFormat;
            writableCellFormat.setAlignment(Alignment.CENTRE);
            arial14format.setBorder(Border.ALL, BorderLineStyle.THIN);
            arial14format.setBackground(Colour.VERY_LIGHT_YELLOW);
            WritableFont writableFont2 = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            arial10font = writableFont2;
            WritableCellFormat writableCellFormat2 = new WritableCellFormat(writableFont2);
            arial10format = writableCellFormat2;
            writableCellFormat2.setAlignment(Alignment.CENTRE);
            arial10format.setBorder(Border.ALL, BorderLineStyle.THIN);
            arial10format.setBackground(Colour.GRAY_25);
            WritableFont writableFont3 = new WritableFont(WritableFont.ARIAL, 10);
            arial12font = writableFont3;
            arial12format = new WritableCellFormat(writableFont3);
            arial10format.setAlignment(Alignment.CENTRE);
            arial12format.setBorder(Border.ALL, BorderLineStyle.THIN);
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    public static void initExcel(String path, String fileName, String[] colName) {
        format();
        WritableWorkbook workbook = null;
        try {
            try {
                try {
                    File file = new File(path + fileName);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    workbook = Workbook.createWorkbook(file);
                    WritableSheet sheet = workbook.createSheet(fileName, 0);
                    sheet.addCell(new Label(0, 0, fileName, arial14format));
                    for (int col = 0; col < colName.length; col++) {
                        sheet.addCell(new Label(col, 0, colName[col], arial10format));
                    }
                    sheet.setRowView(0, 340);
                    workbook.write();
                    if (workbook != null) {
                        workbook.close();
                    }
                } catch (Throwable th) {
                    if (workbook != null) {
                        try {
                            workbook.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (Exception e2) {
                e2.printStackTrace();
                if (workbook != null) {
                    workbook.close();
                }
            }
        } catch (Exception e3) {
            e3.printStackTrace();
        }
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:56:0x00b9 -> B:22:0x00d2). Please report as a decompilation issue!!! */
    public static <T> void writeObjListToExcel(ArrayList<ArrayList<String>> objList, String fileName, PermissionCallback c) {
        if (objList == null || objList.size() <= 0) {
            return;
        }
        WritableWorkbook writebook = null;
        InputStream in = null;
        try {
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            try {
                WorkbookSettings setEncode = new WorkbookSettings();
                setEncode.setEncoding(UTF8_ENCODING);
                in = new FileInputStream(new File(fileName));
                Workbook workbook = Workbook.getWorkbook(in);
                writebook = Workbook.createWorkbook(new File(fileName), workbook);
                WritableSheet sheet = writebook.getSheet(0);
                for (int j = 0; j < objList.size(); j++) {
                    ArrayList<String> list = objList.get(j);
                    for (int i = 0; i < list.size(); i++) {
                        sheet.addCell(new Label(i, j + 1, list.get(i), arial12format));
                        if (list.get(i).length() <= 5) {
                            sheet.setColumnView(i, list.get(i).length() + 8);
                        } else {
                            sheet.setColumnView(i, list.get(i).length() + 5);
                        }
                    }
                    int i2 = j + 1;
                    sheet.setRowView(i2, 350);
                }
                writebook.write();
                if (writebook != null) {
                    try {
                        writebook.close();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                in.close();
            } catch (Exception e3) {
                e3.printStackTrace();
                if (writebook != null) {
                    try {
                        writebook.close();
                    } catch (Exception e4) {
                        e4.printStackTrace();
                    }
                }
                if (in != null) {
                    in.close();
                }
            }
        } finally {
        }
    }

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals("mounted");
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        String dir = sdDir.toString();
        return dir;
    }

    public static void makeDir(File dir) {
        if (!dir.getParentFile().exists()) {
            makeDir(dir.getParentFile());
        }
        dir.mkdir();
    }
}
