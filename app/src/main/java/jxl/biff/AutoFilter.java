package jxl.biff;

import java.io.IOException;
import jxl.write.biff.File;

/* loaded from: classes.dex */
public class AutoFilter {
    private AutoFilterRecord autoFilter;
    private AutoFilterInfoRecord autoFilterInfo;
    private FilterModeRecord filterMode;

    public AutoFilter(FilterModeRecord fmr, AutoFilterInfoRecord afir) {
        this.filterMode = fmr;
        this.autoFilterInfo = afir;
    }

    public void add(AutoFilterRecord af) {
        this.autoFilter = af;
    }

    public void write(File outputFile) throws IOException {
        FilterModeRecord filterModeRecord = this.filterMode;
        if (filterModeRecord != null) {
            outputFile.write(filterModeRecord);
        }
        AutoFilterInfoRecord autoFilterInfoRecord = this.autoFilterInfo;
        if (autoFilterInfoRecord != null) {
            outputFile.write(autoFilterInfoRecord);
        }
        AutoFilterRecord autoFilterRecord = this.autoFilter;
        if (autoFilterRecord != null) {
            outputFile.write(autoFilterRecord);
        }
    }
}
