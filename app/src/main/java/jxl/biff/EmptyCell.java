package jxl.biff;

import jxl.CellFeatures;
import jxl.CellType;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.format.VerticalAlignment;
import jxl.write.WritableCell;
import jxl.write.WritableCellFeatures;

/* loaded from: classes.dex */
public class EmptyCell implements WritableCell {
    private int col;
    private int row;

    public EmptyCell(int c, int r) {
        this.row = r;
        this.col = c;
    }

    @Override // jxl.Cell
    public int getRow() {
        return this.row;
    }

    @Override // jxl.Cell
    public int getColumn() {
        return this.col;
    }

    @Override // jxl.Cell
    public CellType getType() {
        return CellType.EMPTY;
    }

    @Override // jxl.Cell
    public String getContents() {
        return "";
    }

    @Override // jxl.Cell
    public CellFormat getCellFormat() {
        return null;
    }

    public void setHidden(boolean flag) {
    }

    public void setLocked(boolean flag) {
    }

    public void setAlignment(Alignment align) {
    }

    public void setVerticalAlignment(VerticalAlignment valign) {
    }

    public void setBorder(Border border, BorderLineStyle line) {
    }

    @Override // jxl.write.WritableCell
    public void setCellFormat(CellFormat cf) {
    }

    public void setCellFormat(jxl.CellFormat cf) {
    }

    @Override // jxl.Cell
    public boolean isHidden() {
        return false;
    }

    @Override // jxl.write.WritableCell
    public WritableCell copyTo(int c, int r) {
        return new EmptyCell(c, r);
    }

    @Override // jxl.Cell
    public CellFeatures getCellFeatures() {
        return null;
    }

    @Override // jxl.write.WritableCell
    public WritableCellFeatures getWritableCellFeatures() {
        return null;
    }

    @Override // jxl.write.WritableCell
    public void setCellFeatures(WritableCellFeatures wcf) {
    }
}
