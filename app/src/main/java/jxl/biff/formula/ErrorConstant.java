package jxl.biff.formula;

/* loaded from: classes.dex */
class ErrorConstant extends Operand implements ParsedThing {
    private FormulaErrorCode error;

    public ErrorConstant() {
    }

    public ErrorConstant(String s) {
        this.error = FormulaErrorCode.getErrorCode(s);
    }

    @Override // jxl.biff.formula.ParsedThing
    public int read(byte[] data, int pos) {
        int code = data[pos];
        this.error = FormulaErrorCode.getErrorCode(code);
        return 1;
    }

    @Override // jxl.biff.formula.ParseItem
    byte[] getBytes() {
        byte[] data = {Token.ERR.getCode(), (byte) this.error.getCode()};
        return data;
    }

    @Override // jxl.biff.formula.ParseItem
    public void getString(StringBuffer buf) {
        buf.append(this.error.getDescription());
    }

    @Override // jxl.biff.formula.ParseItem
    void handleImportedCellReferences() {
    }
}
