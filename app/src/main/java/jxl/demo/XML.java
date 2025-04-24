package jxl.demo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.format.Font;
import jxl.format.Pattern;

/* loaded from: classes.dex */
public class XML {
    private String encoding;
    private OutputStream out;
    private Workbook workbook;

    public XML(Workbook w, OutputStream out, String enc, boolean f) throws IOException {
        this.encoding = enc;
        this.workbook = w;
        this.out = out;
        if (enc == null || !enc.equals("UnicodeBig")) {
            this.encoding = "UTF8";
        }
        if (f) {
            writeFormattedXML();
        } else {
            writeXML();
        }
    }

    private void writeXML() throws IOException {
        try {
            OutputStreamWriter osw = new OutputStreamWriter(this.out, this.encoding);
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write("<?xml version=\"1.0\" ?>");
            bw.newLine();
            bw.write("<!DOCTYPE workbook SYSTEM \"workbook.dtd\">");
            bw.newLine();
            bw.newLine();
            bw.write("<workbook>");
            bw.newLine();
            for (int sheet = 0; sheet < this.workbook.getNumberOfSheets(); sheet++) {
                Sheet s = this.workbook.getSheet(sheet);
                bw.write("  <sheet>");
                bw.newLine();
                bw.write("    <name><![CDATA[" + s.getName() + "]]></name>");
                bw.newLine();
                for (int i = 0; i < s.getRows(); i++) {
                    bw.write("    <row number=\"" + i + "\">");
                    bw.newLine();
                    Cell[] row = s.getRow(i);
                    for (int j = 0; j < row.length; j++) {
                        if (row[j].getType() != CellType.EMPTY) {
                            bw.write("      <col number=\"" + j + "\">");
                            bw.write("<![CDATA[" + row[j].getContents() + "]]>");
                            bw.write("</col>");
                            bw.newLine();
                        }
                    }
                    bw.write("    </row>");
                    bw.newLine();
                }
                bw.write("  </sheet>");
                bw.newLine();
            }
            bw.write("</workbook>");
            bw.newLine();
            bw.flush();
            bw.close();
        } catch (UnsupportedEncodingException e) {
            System.err.println(e.toString());
        }
    }

    private void writeFormattedXML() throws IOException {
        try {
            OutputStreamWriter osw = new OutputStreamWriter(this.out, this.encoding);
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write("<?xml version=\"1.0\" ?>");
            bw.newLine();
            bw.write("<!DOCTYPE workbook SYSTEM \"formatworkbook.dtd\">");
            bw.newLine();
            bw.newLine();
            bw.write("<workbook>");
            bw.newLine();
            for (int sheet = 0; sheet < this.workbook.getNumberOfSheets(); sheet++) {
                Sheet s = this.workbook.getSheet(sheet);
                bw.write("  <sheet>");
                bw.newLine();
                bw.write("    <name><![CDATA[" + s.getName() + "]]></name>");
                bw.newLine();
                for (int i = 0; i < s.getRows(); i++) {
                    bw.write("    <row number=\"" + i + "\">");
                    bw.newLine();
                    Cell[] row = s.getRow(i);
                    for (int j = 0; j < row.length; j++) {
                        if (row[j].getType() != CellType.EMPTY || row[j].getCellFormat() != null) {
                            CellFormat format = row[j].getCellFormat();
                            bw.write("      <col number=\"" + j + "\">");
                            bw.newLine();
                            bw.write("        <data>");
                            bw.write("<![CDATA[" + row[j].getContents() + "]]>");
                            bw.write("</data>");
                            bw.newLine();
                            if (row[j].getCellFormat() != null) {
                                bw.write("        <format wrap=\"" + format.getWrap() + "\"");
                                bw.newLine();
                                bw.write("                align=\"" + format.getAlignment().getDescription() + "\"");
                                bw.newLine();
                                bw.write("                valign=\"" + format.getVerticalAlignment().getDescription() + "\"");
                                bw.newLine();
                                bw.write("                orientation=\"" + format.getOrientation().getDescription() + "\"");
                                bw.write(">");
                                bw.newLine();
                                Font font = format.getFont();
                                bw.write("          <font name=\"" + font.getName() + "\"");
                                bw.newLine();
                                bw.write("                point_size=\"" + font.getPointSize() + "\"");
                                bw.newLine();
                                bw.write("                bold_weight=\"" + font.getBoldWeight() + "\"");
                                bw.newLine();
                                bw.write("                italic=\"" + font.isItalic() + "\"");
                                bw.newLine();
                                bw.write("                underline=\"" + font.getUnderlineStyle().getDescription() + "\"");
                                bw.newLine();
                                bw.write("                colour=\"" + font.getColour().getDescription() + "\"");
                                bw.newLine();
                                bw.write("                script=\"" + font.getScriptStyle().getDescription() + "\"");
                                bw.write(" />");
                                bw.newLine();
                                if (format.getBackgroundColour() != Colour.DEFAULT_BACKGROUND || format.getPattern() != Pattern.NONE) {
                                    bw.write("          <background colour=\"" + format.getBackgroundColour().getDescription() + "\"");
                                    bw.newLine();
                                    bw.write("                      pattern=\"" + format.getPattern().getDescription() + "\"");
                                    bw.write(" />");
                                    bw.newLine();
                                }
                                if (format.getBorder(Border.TOP) != BorderLineStyle.NONE || format.getBorder(Border.BOTTOM) != BorderLineStyle.NONE || format.getBorder(Border.LEFT) != BorderLineStyle.NONE || format.getBorder(Border.RIGHT) != BorderLineStyle.NONE) {
                                    bw.write("          <border top=\"" + format.getBorder(Border.TOP).getDescription() + "\"");
                                    bw.newLine();
                                    bw.write("                  bottom=\"" + format.getBorder(Border.BOTTOM).getDescription() + "\"");
                                    bw.newLine();
                                    bw.write("                  left=\"" + format.getBorder(Border.LEFT).getDescription() + "\"");
                                    bw.newLine();
                                    bw.write("                  right=\"" + format.getBorder(Border.RIGHT).getDescription() + "\"");
                                    bw.write(" />");
                                    bw.newLine();
                                }
                                if (!format.getFormat().getFormatString().equals("")) {
                                    bw.write("          <format_string string=\"");
                                    bw.write(format.getFormat().getFormatString());
                                    bw.write("\" />");
                                    bw.newLine();
                                }
                                bw.write("        </format>");
                                bw.newLine();
                            }
                            bw.write("      </col>");
                            bw.newLine();
                        }
                    }
                    bw.write("    </row>");
                    bw.newLine();
                }
                bw.write("  </sheet>");
                bw.newLine();
            }
            bw.write("</workbook>");
            bw.newLine();
            bw.flush();
            bw.close();
        } catch (UnsupportedEncodingException e) {
            System.err.println(e.toString());
        }
    }
}
