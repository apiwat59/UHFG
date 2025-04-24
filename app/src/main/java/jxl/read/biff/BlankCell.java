package jxl.read.biff;

import jxl.CellType;
import jxl.biff.FormattingRecords;

/* loaded from: classes.dex */
public class BlankCell extends CellValue {
    BlankCell(Record t, FormattingRecords fr, SheetImpl si) {
        super(t, fr, si);
    }

    @Override // jxl.Cell
    public String getContents() {
        return "";
    }

    @Override // jxl.Cell
    public CellType getType() {
        return CellType.EMPTY;
    }
}
