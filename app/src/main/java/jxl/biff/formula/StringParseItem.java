package jxl.biff.formula;

/* loaded from: classes.dex */
class StringParseItem extends ParseItem {
    protected StringParseItem() {
    }

    @Override // jxl.biff.formula.ParseItem
    void getString(StringBuffer buf) {
    }

    @Override // jxl.biff.formula.ParseItem
    byte[] getBytes() {
        return new byte[0];
    }

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

    @Override // jxl.biff.formula.ParseItem
    void handleImportedCellReferences() {
    }
}
