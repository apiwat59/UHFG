package jxl.biff.formula;

/* loaded from: classes.dex */
class MissingArg extends Operand implements ParsedThing {
    @Override // jxl.biff.formula.ParsedThing
    public int read(byte[] data, int pos) {
        return 0;
    }

    @Override // jxl.biff.formula.ParseItem
    byte[] getBytes() {
        byte[] data = {Token.MISSING_ARG.getCode()};
        return data;
    }

    @Override // jxl.biff.formula.ParseItem
    public void getString(StringBuffer buf) {
    }

    @Override // jxl.biff.formula.ParseItem
    void handleImportedCellReferences() {
    }
}
