package jxl;

/* loaded from: classes.dex */
public interface Cell {
    CellFeatures getCellFeatures();

    jxl.format.CellFormat getCellFormat();

    int getColumn();

    String getContents();

    int getRow();

    CellType getType();

    boolean isHidden();
}
