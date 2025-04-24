package jxl.write.biff;

import jxl.biff.Fonts;
import jxl.biff.FormattingRecords;
import jxl.biff.NumFormatRecordsException;
import jxl.common.Assert;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;

/* loaded from: classes.dex */
public class WritableFormattingRecords extends FormattingRecords {
    public static WritableCellFormat normalStyle;

    public WritableFormattingRecords(Fonts f, Styles styles) {
        super(f);
        try {
            StyleXFRecord sxf = new StyleXFRecord(styles.getArial10Pt(), NumberFormats.DEFAULT);
            sxf.setLocked(true);
            addStyle(sxf);
            StyleXFRecord sxf2 = new StyleXFRecord(getFonts().getFont(1), NumberFormats.DEFAULT);
            sxf2.setLocked(true);
            sxf2.setCellOptions(62464);
            addStyle(sxf2);
            StyleXFRecord sxf3 = new StyleXFRecord(getFonts().getFont(1), NumberFormats.DEFAULT);
            sxf3.setLocked(true);
            sxf3.setCellOptions(62464);
            addStyle(sxf3);
            StyleXFRecord sxf4 = new StyleXFRecord(getFonts().getFont(1), NumberFormats.DEFAULT);
            sxf4.setLocked(true);
            sxf4.setCellOptions(62464);
            addStyle(sxf4);
            StyleXFRecord sxf5 = new StyleXFRecord(getFonts().getFont(2), NumberFormats.DEFAULT);
            sxf5.setLocked(true);
            sxf5.setCellOptions(62464);
            addStyle(sxf5);
            StyleXFRecord sxf6 = new StyleXFRecord(getFonts().getFont(3), NumberFormats.DEFAULT);
            sxf6.setLocked(true);
            sxf6.setCellOptions(62464);
            addStyle(sxf6);
            StyleXFRecord sxf7 = new StyleXFRecord(styles.getArial10Pt(), NumberFormats.DEFAULT);
            sxf7.setLocked(true);
            sxf7.setCellOptions(62464);
            addStyle(sxf7);
            StyleXFRecord sxf8 = new StyleXFRecord(styles.getArial10Pt(), NumberFormats.DEFAULT);
            sxf8.setLocked(true);
            sxf8.setCellOptions(62464);
            addStyle(sxf8);
            StyleXFRecord sxf9 = new StyleXFRecord(styles.getArial10Pt(), NumberFormats.DEFAULT);
            sxf9.setLocked(true);
            sxf9.setCellOptions(62464);
            addStyle(sxf9);
            StyleXFRecord sxf10 = new StyleXFRecord(styles.getArial10Pt(), NumberFormats.DEFAULT);
            sxf10.setLocked(true);
            sxf10.setCellOptions(62464);
            addStyle(sxf10);
            StyleXFRecord sxf11 = new StyleXFRecord(styles.getArial10Pt(), NumberFormats.DEFAULT);
            sxf11.setLocked(true);
            sxf11.setCellOptions(62464);
            addStyle(sxf11);
            StyleXFRecord sxf12 = new StyleXFRecord(styles.getArial10Pt(), NumberFormats.DEFAULT);
            sxf12.setLocked(true);
            sxf12.setCellOptions(62464);
            addStyle(sxf12);
            StyleXFRecord sxf13 = new StyleXFRecord(styles.getArial10Pt(), NumberFormats.DEFAULT);
            sxf13.setLocked(true);
            sxf13.setCellOptions(62464);
            addStyle(sxf13);
            StyleXFRecord sxf14 = new StyleXFRecord(styles.getArial10Pt(), NumberFormats.DEFAULT);
            sxf14.setLocked(true);
            sxf14.setCellOptions(62464);
            addStyle(sxf14);
            StyleXFRecord sxf15 = new StyleXFRecord(styles.getArial10Pt(), NumberFormats.DEFAULT);
            sxf15.setLocked(true);
            sxf15.setCellOptions(62464);
            addStyle(sxf15);
            addStyle(styles.getNormalStyle());
            StyleXFRecord sxf16 = new StyleXFRecord(getFonts().getFont(1), NumberFormats.FORMAT7);
            sxf16.setLocked(true);
            sxf16.setCellOptions(63488);
            addStyle(sxf16);
            StyleXFRecord sxf17 = new StyleXFRecord(getFonts().getFont(1), NumberFormats.FORMAT5);
            sxf17.setLocked(true);
            sxf17.setCellOptions(63488);
            addStyle(sxf17);
            StyleXFRecord sxf18 = new StyleXFRecord(getFonts().getFont(1), NumberFormats.FORMAT8);
            sxf18.setLocked(true);
            sxf18.setCellOptions(63488);
            addStyle(sxf18);
            StyleXFRecord sxf19 = new StyleXFRecord(getFonts().getFont(1), NumberFormats.FORMAT6);
            sxf19.setLocked(true);
            sxf19.setCellOptions(63488);
            addStyle(sxf19);
            StyleXFRecord sxf20 = new StyleXFRecord(getFonts().getFont(1), NumberFormats.PERCENT_INTEGER);
            sxf20.setLocked(true);
            sxf20.setCellOptions(63488);
            addStyle(sxf20);
        } catch (NumFormatRecordsException e) {
            Assert.verify(false, e.getMessage());
        }
    }
}
