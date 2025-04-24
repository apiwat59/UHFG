package jxl.biff.formula;

/* loaded from: classes.dex */
class Name extends Operand implements ParsedThing {
    @Override // jxl.biff.formula.ParsedThing
    public int read(byte[] data, int pos) {
        return 6;
    }

    @Override // jxl.biff.formula.ParseItem
    byte[] getBytes() {
        byte[] data = new byte[6];
        return data;
    }

    @Override // jxl.biff.formula.ParseItem
    public void getString(StringBuffer buf) {
        buf.append("[Name record not implemented]");
    }

    @Override // jxl.biff.formula.ParseItem
    void handleImportedCellReferences() {
        setInvalid();
    }
}
