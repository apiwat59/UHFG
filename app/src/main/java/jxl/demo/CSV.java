package jxl.demo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

/* loaded from: classes.dex */
public class CSV {
    public CSV(Workbook w, OutputStream out, String encoding, boolean hide) throws IOException {
        try {
            OutputStreamWriter osw = new OutputStreamWriter(out, (encoding == null || !encoding.equals("UnicodeBig")) ? "UTF8" : encoding);
            BufferedWriter bw = new BufferedWriter(osw);
            for (int sheet = 0; sheet < w.getNumberOfSheets(); sheet++) {
                Sheet s = w.getSheet(sheet);
                if (!hide || !s.getSettings().isHidden()) {
                    bw.write("*** " + s.getName() + " ****");
                    bw.newLine();
                    for (int i = 0; i < s.getRows(); i++) {
                        Cell[] row = s.getRow(i);
                        if (row.length > 0) {
                            if (!hide || !row[0].isHidden()) {
                                bw.write(row[0].getContents());
                            }
                            for (int j = 1; j < row.length; j++) {
                                bw.write(44);
                                if (!hide || !row[j].isHidden()) {
                                    bw.write(row[j].getContents());
                                }
                            }
                        }
                        bw.newLine();
                    }
                }
            }
            bw.flush();
            bw.close();
        } catch (UnsupportedEncodingException e) {
            System.err.println(e.toString());
        }
    }
}
