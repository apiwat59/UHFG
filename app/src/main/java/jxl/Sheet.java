package jxl;

import java.util.regex.Pattern;

/* loaded from: classes.dex */
public interface Sheet {
    Cell findCell(String str);

    Cell findCell(String str, int i, int i2, int i3, int i4, boolean z);

    Cell findCell(Pattern pattern, int i, int i2, int i3, int i4, boolean z);

    LabelCell findLabelCell(String str);

    Cell getCell(int i, int i2);

    Cell getCell(String str);

    Cell[] getColumn(int i);

    jxl.format.CellFormat getColumnFormat(int i);

    int[] getColumnPageBreaks();

    CellView getColumnView(int i);

    int getColumnWidth(int i);

    int getColumns();

    Image getDrawing(int i);

    Hyperlink[] getHyperlinks();

    Range[] getMergedCells();

    String getName();

    int getNumberOfImages();

    Cell[] getRow(int i);

    int getRowHeight(int i);

    int[] getRowPageBreaks();

    CellView getRowView(int i);

    int getRows();

    SheetSettings getSettings();

    boolean isHidden();

    boolean isProtected();
}
