package jxl.demo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import jxl.WorkbookSettings;
import jxl.biff.BaseCompoundFile;
import jxl.read.biff.BiffException;
import jxl.read.biff.CompoundFile;

/* loaded from: classes.dex */
class PropertySetsReader {
    private CompoundFile compoundFile;
    private BufferedWriter writer;

    public PropertySetsReader(File file, String propertySet, OutputStream os) throws IOException, BiffException {
        this.writer = new BufferedWriter(new OutputStreamWriter(os));
        FileInputStream fis = new FileInputStream(file);
        byte[] d = new byte[1048576];
        int bytesRead = fis.read(d);
        int pos = bytesRead;
        while (bytesRead != -1) {
            if (pos >= d.length) {
                byte[] newArray = new byte[d.length + 1048576];
                System.arraycopy(d, 0, newArray, 0, d.length);
                d = newArray;
            }
            bytesRead = fis.read(d, pos, d.length - pos);
            pos += bytesRead;
        }
        int bytesRead2 = pos + 1;
        this.compoundFile = new CompoundFile(d, new WorkbookSettings());
        fis.close();
        if (propertySet == null) {
            displaySets();
        } else {
            displayPropertySet(propertySet, os);
        }
    }

    void displaySets() throws IOException {
        int numSets = this.compoundFile.getNumberOfPropertySets();
        for (int i = 0; i < numSets; i++) {
            BaseCompoundFile.PropertyStorage ps = this.compoundFile.getPropertySet(i);
            this.writer.write(Integer.toString(i));
            this.writer.write(") ");
            this.writer.write(ps.name);
            this.writer.write("(type ");
            this.writer.write(Integer.toString(ps.type));
            this.writer.write(" size ");
            this.writer.write(Integer.toString(ps.size));
            this.writer.write(" prev ");
            this.writer.write(Integer.toString(ps.previous));
            this.writer.write(" next ");
            this.writer.write(Integer.toString(ps.next));
            this.writer.write(" child ");
            this.writer.write(Integer.toString(ps.child));
            this.writer.write(" start block ");
            this.writer.write(Integer.toString(ps.startBlock));
            this.writer.write(")");
            this.writer.newLine();
        }
        this.writer.flush();
        this.writer.close();
    }

    void displayPropertySet(String ps, OutputStream os) throws IOException, BiffException {
        if (ps.equalsIgnoreCase("SummaryInformation")) {
            ps = BaseCompoundFile.SUMMARY_INFORMATION_NAME;
        } else if (ps.equalsIgnoreCase("DocumentSummaryInformation")) {
            ps = BaseCompoundFile.DOCUMENT_SUMMARY_INFORMATION_NAME;
        } else if (ps.equalsIgnoreCase("CompObj")) {
            ps = BaseCompoundFile.COMP_OBJ_NAME;
        }
        byte[] stream = this.compoundFile.getStream(ps);
        os.write(stream);
    }
}
