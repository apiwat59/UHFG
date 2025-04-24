package jxl.write;

import jxl.CellView;
import jxl.Range;
import jxl.Sheet;
import jxl.format.CellFormat;
import jxl.format.PageOrientation;
import jxl.format.PaperSize;
import jxl.write.biff.RowsExceededException;

/* loaded from: classes.dex */
public interface WritableSheet extends Sheet {
    void addCell(WritableCell writableCell) throws WriteException, RowsExceededException;

    void addColumnPageBreak(int i);

    void addHyperlink(WritableHyperlink writableHyperlink) throws WriteException, RowsExceededException;

    void addImage(WritableImage writableImage);

    void addRowPageBreak(int i);

    void applySharedDataValidation(WritableCell writableCell, int i, int i2) throws WriteException;

    WritableImage getImage(int i);

    @Override // jxl.Sheet
    int getNumberOfImages();

    WritableCell getWritableCell(int i, int i2);

    WritableCell getWritableCell(String str);

    WritableHyperlink[] getWritableHyperlinks();

    void insertColumn(int i);

    void insertRow(int i);

    Range mergeCells(int i, int i2, int i3, int i4) throws WriteException, RowsExceededException;

    void removeColumn(int i);

    void removeHyperlink(WritableHyperlink writableHyperlink);

    void removeHyperlink(WritableHyperlink writableHyperlink, boolean z);

    void removeImage(WritableImage writableImage);

    void removeRow(int i);

    void removeSharedDataValidation(WritableCell writableCell) throws WriteException;

    void setColumnGroup(int i, int i2, boolean z) throws WriteException, RowsExceededException;

    void setColumnView(int i, int i2);

    void setColumnView(int i, int i2, CellFormat cellFormat);

    void setColumnView(int i, CellView cellView);

    void setFooter(String str, String str2, String str3);

    void setHeader(String str, String str2, String str3);

    void setHidden(boolean z);

    void setName(String str);

    void setPageSetup(PageOrientation pageOrientation);

    void setPageSetup(PageOrientation pageOrientation, double d, double d2);

    void setPageSetup(PageOrientation pageOrientation, PaperSize paperSize, double d, double d2);

    void setProtected(boolean z);

    void setRowGroup(int i, int i2, boolean z) throws WriteException, RowsExceededException;

    void setRowView(int i, int i2) throws RowsExceededException;

    void setRowView(int i, int i2, boolean z) throws RowsExceededException;

    void setRowView(int i, CellView cellView) throws RowsExceededException;

    void setRowView(int i, boolean z) throws RowsExceededException;

    void unmergeCells(Range range);

    void unsetColumnGroup(int i, int i2) throws WriteException, RowsExceededException;

    void unsetRowGroup(int i, int i2) throws WriteException, RowsExceededException;
}
