package jxl.biff.formula;

/* loaded from: classes.dex */
abstract class Operand extends ParseItem {
    @Override // jxl.biff.formula.ParseItem
    public void adjustRelativeCellReferences(int colAdjust, int rowAdjust) {
    }

    @Override // jxl.biff.formula.ParseItem
    void columnInserted(int sheetIndex, int col, boolean currentSheet) {
    }

    @Override // jxl.biff.formula.ParseItem
    void columnRemoved(int sheetIndex, int col, boolean currentSheet) {
    }

    @Override // jxl.biff.formula.ParseItem
    void rowInserted(int sheetIndex, int row, boolean currentSheet) {
    }

    @Override // jxl.biff.formula.ParseItem
    void rowRemoved(int sheetIndex, int row, boolean currentSheet) {
    }
}
