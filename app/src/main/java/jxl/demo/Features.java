package jxl.demo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import jxl.Cell;
import jxl.CellFeatures;
import jxl.CellReferenceHelper;
import jxl.Sheet;
import jxl.Workbook;

/* loaded from: classes.dex */
public class Features {
    public Features(Workbook w, OutputStream out, String encoding) throws IOException {
        try {
            OutputStreamWriter osw = new OutputStreamWriter(out, (encoding == null || !encoding.equals("UnicodeBig")) ? "UTF8" : encoding);
            BufferedWriter bw = new BufferedWriter(osw);
            for (int sheet = 0; sheet < w.getNumberOfSheets(); sheet++) {
                Sheet s = w.getSheet(sheet);
                bw.write(s.getName());
                bw.newLine();
                for (int i = 0; i < s.getRows(); i++) {
                    Cell[] row = s.getRow(i);
                    for (Cell c : row) {
                        if (c.getCellFeatures() != null) {
                            CellFeatures features = c.getCellFeatures();
                            StringBuffer sb = new StringBuffer();
                            CellReferenceHelper.getCellReference(c.getColumn(), c.getRow(), sb);
                            bw.write("Cell " + sb.toString() + " contents:  " + c.getContents());
                            bw.flush();
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append(" comment: ");
                            sb2.append(features.getComment());
                            bw.write(sb2.toString());
                            bw.flush();
                            bw.newLine();
                        }
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
