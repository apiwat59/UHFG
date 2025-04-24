package jxl.demo;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import jxl.Cell;
import jxl.CellType;
import jxl.Range;
import jxl.Workbook;
import jxl.common.Logger;
import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.read.biff.BiffException;
import jxl.write.Blank;
import jxl.write.DateFormat;
import jxl.write.DateFormats;
import jxl.write.DateTime;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormat;
import jxl.write.WritableCell;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableHyperlink;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.apache.log4j.net.SyslogAppender;

/* loaded from: classes.dex */
public class ReadWrite {
    private static Logger logger = Logger.getLogger(ReadWrite.class);
    private File inputWorkbook;
    private File outputWorkbook;

    public ReadWrite(String input, String output) {
        this.inputWorkbook = new File(input);
        this.outputWorkbook = new File(output);
        logger.setSuppressWarnings(Boolean.getBoolean("jxl.nowarnings"));
        logger.info("Input file:  " + input);
        logger.info("Output file:  " + output);
    }

    public void readWrite() throws IOException, BiffException, WriteException {
        logger.info("Reading...");
        Workbook w1 = Workbook.getWorkbook(this.inputWorkbook);
        logger.info("Copying...");
        WritableWorkbook w2 = Workbook.createWorkbook(this.outputWorkbook, w1);
        if (this.inputWorkbook.getName().equals("jxlrwtest.xls")) {
            modify(w2);
        }
        w2.write();
        w2.close();
        logger.info("Done");
    }

    private void modify(WritableWorkbook w) throws WriteException {
        WritableCell cell;
        logger.info("Modifying...");
        WritableSheet sheet = w.getSheet("modified");
        int i = 1;
        WritableCell cell2 = sheet.getWritableCell(1, 3);
        WritableFont bold = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
        CellFormat cf = new WritableCellFormat(bold);
        cell2.setCellFormat(cf);
        WritableCell cell3 = sheet.getWritableCell(1, 4);
        WritableFont underline = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.SINGLE);
        CellFormat cf2 = new WritableCellFormat(underline);
        cell3.setCellFormat(cf2);
        WritableCell cell4 = sheet.getWritableCell(1, 5);
        WritableFont tenpoint = new WritableFont(WritableFont.ARIAL, 10);
        CellFormat cf3 = new WritableCellFormat(tenpoint);
        cell4.setCellFormat(cf3);
        WritableCell cell5 = sheet.getWritableCell(1, 6);
        if (cell5.getType() == CellType.LABEL) {
            Label lc = (Label) cell5;
            lc.setString(lc.getString() + " - mod");
        }
        WritableCell cell6 = sheet.getWritableCell(1, 9);
        NumberFormat sevendps = new NumberFormat("#.0000000");
        CellFormat cf4 = new WritableCellFormat(sevendps);
        cell6.setCellFormat(cf4);
        WritableCell cell7 = sheet.getWritableCell(1, 10);
        NumberFormat exp4 = new NumberFormat("0.####E0");
        CellFormat cf5 = new WritableCellFormat(exp4);
        cell7.setCellFormat(cf5);
        WritableCell cell8 = sheet.getWritableCell(1, 11);
        cell8.setCellFormat(WritableWorkbook.NORMAL_STYLE);
        WritableCell cell9 = sheet.getWritableCell(1, 12);
        if (cell9.getType() == CellType.NUMBER) {
            Number n = (Number) cell9;
            n.setValue(42.0d);
        }
        WritableCell cell10 = sheet.getWritableCell(1, 13);
        if (cell10.getType() == CellType.NUMBER) {
            Number n2 = (Number) cell10;
            n2.setValue(n2.getValue() + 0.1d);
        }
        WritableCell cell11 = sheet.getWritableCell(1, 16);
        DateFormat df = new DateFormat("dd MMM yyyy HH:mm:ss");
        CellFormat cf6 = new WritableCellFormat(df);
        cell11.setCellFormat(cf6);
        WritableCell cell12 = sheet.getWritableCell(1, 17);
        CellFormat cf7 = new WritableCellFormat(DateFormats.FORMAT9);
        cell12.setCellFormat(cf7);
        WritableCell cell13 = sheet.getWritableCell(1, 18);
        if (cell13.getType() == CellType.DATE) {
            DateTime dt = (DateTime) cell13;
            Calendar cal = Calendar.getInstance();
            cal.set(1998, 1, 18, 11, 23, 28);
            Date d = cal.getTime();
            dt.setDate(d);
        }
        WritableCell cell14 = sheet.getWritableCell(1, 22);
        if (cell14.getType() == CellType.NUMBER) {
            Number n3 = (Number) cell14;
            n3.setValue(6.8d);
        }
        WritableCell cell15 = sheet.getWritableCell(1, 29);
        if (cell15.getType() == CellType.LABEL) {
            Label l = (Label) cell15;
            l.setString("Modified string contents");
        }
        sheet.insertRow(34);
        sheet.removeRow(38);
        sheet.insertColumn(9);
        sheet.removeColumn(11);
        sheet.removeRow(43);
        sheet.insertRow(43);
        WritableHyperlink[] hyperlinks = sheet.getWritableHyperlinks();
        int i2 = 0;
        while (i2 < hyperlinks.length) {
            WritableHyperlink wh = hyperlinks[i2];
            if (wh.getColumn() != i || wh.getRow() != 39) {
                cell = cell15;
                if (wh.getColumn() == 1 && wh.getRow() == 40) {
                    wh.setFile(new File("../jexcelapi/docs/overview-summary.html"));
                } else if (wh.getColumn() == 1 && wh.getRow() == 41) {
                    wh.setFile(new File("d:/home/jexcelapi/docs/jxl/package-summary.html"));
                } else if (wh.getColumn() == 1 && wh.getRow() == 44) {
                    sheet.removeHyperlink(wh);
                }
            } else {
                try {
                    wh.setURL(new URL("http://www.andykhan.com/jexcelapi/index.html"));
                    cell = cell15;
                } catch (MalformedURLException e) {
                    cell = cell15;
                    logger.warn(e.toString());
                }
            }
            i2++;
            cell15 = cell;
            i = 1;
        }
        WritableCell c = sheet.getWritableCell(5, 30);
        WritableCellFormat newFormat = new WritableCellFormat(c.getCellFormat());
        newFormat.setBackground(Colour.RED);
        c.setCellFormat(newFormat);
        Label l2 = new Label(0, 49, "Modified merged cells");
        sheet.addCell(l2);
        Number n4 = (Number) sheet.getWritableCell(0, 70);
        n4.setValue(9.0d);
        Number n5 = (Number) sheet.getWritableCell(0, 71);
        n5.setValue(10.0d);
        Number n6 = (Number) sheet.getWritableCell(0, 73);
        n6.setValue(4.0d);
        Formula f = new Formula(1, 80, "ROUND(COS(original!B10),2)");
        sheet.addCell(f);
        Formula f2 = new Formula(1, 83, "value1+value2");
        sheet.addCell(f2);
        Formula f3 = new Formula(1, 84, "AVERAGE(value1,value1*4,value2)");
        sheet.addCell(f3);
        Label label = new Label(0, 88, "Some copied cells", cf7);
        sheet.addCell(label);
        Label label2 = new Label(0, 89, "Number from B9");
        sheet.addCell(label2);
        WritableCell wc = sheet.getWritableCell(1, 9).copyTo(1, 89);
        sheet.addCell(wc);
        Label label3 = new Label(0, 90, "Label from B4 (modified format)");
        sheet.addCell(label3);
        WritableCell wc2 = sheet.getWritableCell(1, 3).copyTo(1, 90);
        sheet.addCell(wc2);
        Label label4 = new Label(0, 91, "Date from B17");
        sheet.addCell(label4);
        WritableCell wc3 = sheet.getWritableCell(1, 16).copyTo(1, 91);
        sheet.addCell(wc3);
        Label label5 = new Label(0, 92, "Boolean from E16");
        sheet.addCell(label5);
        WritableCell wc4 = sheet.getWritableCell(4, 15).copyTo(1, 92);
        sheet.addCell(wc4);
        Label label6 = new Label(0, 93, "URL from B40");
        sheet.addCell(label6);
        WritableCell wc5 = sheet.getWritableCell(1, 39).copyTo(1, 93);
        sheet.addCell(wc5);
        int i3 = 0;
        while (i3 < 6) {
            Label label7 = label6;
            double d2 = i3 + 1;
            WritableCellFormat newFormat2 = newFormat;
            double d3 = i3;
            Double.isNaN(d3);
            Double.isNaN(d2);
            Number number = new Number(1, i3 + 94, d2 + (d3 / 8.0d));
            sheet.addCell(number);
            i3++;
            label6 = label7;
            wc5 = wc5;
            hyperlinks = hyperlinks;
            newFormat = newFormat2;
        }
        Label label8 = new Label(0, 100, "Formula from B27");
        sheet.addCell(label8);
        WritableCell wc6 = sheet.getWritableCell(1, 26).copyTo(1, 100);
        sheet.addCell(wc6);
        Label label9 = new Label(0, 101, "A brand new formula");
        sheet.addCell(label9);
        Formula formula = new Formula(1, 101, "SUM(B94:B96)");
        sheet.addCell(formula);
        Label label10 = new Label(0, 102, "A copy of it");
        sheet.addCell(label10);
        WritableCell wc7 = sheet.getWritableCell(1, 101).copyTo(1, 102);
        sheet.addCell(wc7);
        WritableImage wi = sheet.getImage(1);
        sheet.removeImage(wi);
        WritableImage wi2 = new WritableImage(1.0d, 116.0d, 2.0d, 9.0d, new File("resources/littlemoretonhall.png"));
        sheet.addImage(wi2);
        Label label11 = new Label(0, 151, "Added drop down validation");
        sheet.addCell(label11);
        Blank b = new Blank(1, 151);
        WritableCellFeatures wcf = new WritableCellFeatures();
        ArrayList al = new ArrayList();
        al.add("The Fellowship of the Ring");
        al.add("The Two Towers");
        al.add("The Return of the King");
        wcf.setDataValidationList(al);
        b.setCellFeatures(wcf);
        sheet.addCell(b);
        Label label12 = new Label(0, SyslogAppender.LOG_LOCAL3, "Added number validation 2.718 < x < 3.142");
        sheet.addCell(label12);
        Blank b2 = new Blank(1, SyslogAppender.LOG_LOCAL3);
        WritableCellFeatures wcf2 = new WritableCellFeatures();
        wcf2.setNumberValidation(2.718d, 3.142d, WritableCellFeatures.BETWEEN);
        b2.setCellFeatures(wcf2);
        sheet.addCell(b2);
        WritableCell cell16 = sheet.getWritableCell(0, 156);
        Label l3 = (Label) cell16;
        l3.setString("Label text modified");
        WritableCell cell17 = sheet.getWritableCell(0, 157);
        cell17.getWritableCellFeatures().setComment("modified comment text");
        WritableCell cell18 = sheet.getWritableCell(0, 158);
        cell18.getWritableCellFeatures().removeComment();
        WritableCell cell19 = sheet.getWritableCell(0, 172);
        WritableCellFeatures wcf3 = cell19.getWritableCellFeatures();
        Range r = wcf3.getSharedDataValidationRange();
        Cell botright = r.getBottomRight();
        sheet.removeSharedDataValidation(cell19);
        ArrayList al2 = new ArrayList();
        al2.add("Stanley Featherstonehaugh Ukridge");
        al2.add("Major Plank");
        al2.add("Earl of Ickenham");
        al2.add("Sir Gregory Parsloe-Parsloe");
        al2.add("Honoria Glossop");
        al2.add("Stiffy Byng");
        al2.add("Bingo Little");
        wcf3.setDataValidationList(al2);
        cell19.setCellFeatures(wcf3);
        sheet.applySharedDataValidation(cell19, botright.getColumn() - cell19.getColumn(), 1);
    }
}
