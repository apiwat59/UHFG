package jxl.biff.formula;

/* loaded from: classes.dex */
class BooleanValue extends Operand implements ParsedThing {
    private boolean value;

    public BooleanValue() {
    }

    public BooleanValue(String s) {
        this.value = Boolean.valueOf(s).booleanValue();
    }

    @Override // jxl.biff.formula.ParsedThing
    public int read(byte[] data, int pos) {
        this.value = data[pos] == 1;
        return 1;
    }

    @Override // jxl.biff.formula.ParseItem
    byte[] getBytes() {
        byte[] data = new byte[2];
        data[0] = Token.BOOL.getCode();
        data[1] = (byte) (this.value ? 1 : 0);
        return data;
    }

    @Override // jxl.biff.formula.ParseItem
    public void getString(StringBuffer buf) {
        buf.append(new Boolean(this.value).toString());
    }

    @Override // jxl.biff.formula.ParseItem
    void handleImportedCellReferences() {
    }
}
