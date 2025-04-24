package jxl;

/* loaded from: classes.dex */
public final class CellView {
    private boolean autosize;
    private boolean depUsed;
    private int dimension;
    private jxl.format.CellFormat format;
    private boolean hidden;
    private int size;

    public CellView() {
        this.hidden = false;
        this.depUsed = false;
        this.dimension = 1;
        this.size = 1;
        this.autosize = false;
    }

    public CellView(CellView cv) {
        this.hidden = cv.hidden;
        this.depUsed = cv.depUsed;
        this.dimension = cv.dimension;
        this.size = cv.size;
        this.autosize = cv.autosize;
    }

    public void setHidden(boolean h) {
        this.hidden = h;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setDimension(int d) {
        this.dimension = d;
        this.depUsed = true;
    }

    public void setSize(int d) {
        this.size = d;
        this.depUsed = false;
    }

    public int getDimension() {
        return this.dimension;
    }

    public int getSize() {
        return this.size;
    }

    public void setFormat(jxl.format.CellFormat cf) {
        this.format = cf;
    }

    public jxl.format.CellFormat getFormat() {
        return this.format;
    }

    public boolean depUsed() {
        return this.depUsed;
    }

    public void setAutosize(boolean a) {
        this.autosize = a;
    }

    public boolean isAutosize() {
        return this.autosize;
    }
}
