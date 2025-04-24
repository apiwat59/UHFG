package jxl.read.biff;

import jxl.biff.IntegerHelper;
import jxl.biff.RecordData;
import jxl.common.Logger;

/* loaded from: classes.dex */
class Window2Record extends RecordData {
    private boolean displayZeroValues;
    private boolean frozenNotSplit;
    private boolean frozenPanes;
    private int normalMagnification;
    private int pageBreakPreviewMagnification;
    private boolean pageBreakPreviewMode;
    private boolean selected;
    private boolean showGridLines;
    private static Logger logger = Logger.getLogger(Window2Record.class);
    public static final Biff7 biff7 = new Biff7();

    /* JADX INFO: Access modifiers changed from: private */
    static class Biff7 {
        private Biff7() {
        }
    }

    public Window2Record(Record t) {
        super(t);
        byte[] data = t.getData();
        int options = IntegerHelper.getInt(data[0], data[1]);
        this.selected = (options & 512) != 0;
        this.showGridLines = (options & 2) != 0;
        this.frozenPanes = (options & 8) != 0;
        this.displayZeroValues = (options & 16) != 0;
        this.frozenNotSplit = (options & 256) != 0;
        this.pageBreakPreviewMode = (options & 2048) != 0;
        this.pageBreakPreviewMagnification = IntegerHelper.getInt(data[10], data[11]);
        this.normalMagnification = IntegerHelper.getInt(data[12], data[13]);
    }

    public Window2Record(Record t, Biff7 biff72) {
        super(t);
        byte[] data = t.getData();
        int options = IntegerHelper.getInt(data[0], data[1]);
        this.selected = (options & 512) != 0;
        this.showGridLines = (options & 2) != 0;
        this.frozenPanes = (options & 8) != 0;
        this.displayZeroValues = (options & 16) != 0;
        this.frozenNotSplit = (options & 256) != 0;
        this.pageBreakPreviewMode = (options & 2048) != 0;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public boolean getShowGridLines() {
        return this.showGridLines;
    }

    public boolean getDisplayZeroValues() {
        return this.displayZeroValues;
    }

    public boolean getFrozen() {
        return this.frozenPanes;
    }

    public boolean getFrozenNotSplit() {
        return this.frozenNotSplit;
    }

    public boolean isPageBreakPreview() {
        return this.pageBreakPreviewMode;
    }

    public int getPageBreakPreviewMagnificaiton() {
        return this.pageBreakPreviewMagnification;
    }

    public int getNormalMagnificaiton() {
        return this.normalMagnification;
    }
}
