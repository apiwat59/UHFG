package jxl.biff;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import jxl.write.biff.File;

/* loaded from: classes.dex */
public class ConditionalFormat {
    private ArrayList conditions = new ArrayList();
    private ConditionalFormatRangeRecord range;

    public ConditionalFormat(ConditionalFormatRangeRecord cfrr) {
        this.range = cfrr;
    }

    public void addCondition(ConditionalFormatRecord cond) {
        this.conditions.add(cond);
    }

    public void insertColumn(int col) {
        this.range.insertColumn(col);
    }

    public void removeColumn(int col) {
        this.range.removeColumn(col);
    }

    public void removeRow(int row) {
        this.range.removeRow(row);
    }

    public void insertRow(int row) {
        this.range.insertRow(row);
    }

    public void write(File outputFile) throws IOException {
        outputFile.write(this.range);
        Iterator i = this.conditions.iterator();
        while (i.hasNext()) {
            ConditionalFormatRecord cfr = (ConditionalFormatRecord) i.next();
            outputFile.write(cfr);
        }
    }
}
