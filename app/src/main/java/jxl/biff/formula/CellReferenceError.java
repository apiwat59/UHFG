package jxl.biff.formula;

import jxl.common.Logger;

/* loaded from: classes.dex */
class CellReferenceError extends Operand implements ParsedThing {
    private static Logger logger = Logger.getLogger(CellReferenceError.class);

    @Override // jxl.biff.formula.ParsedThing
    public int read(byte[] data, int pos) {
        return 4;
    }

    @Override // jxl.biff.formula.ParseItem
    public void getString(StringBuffer buf) {
        buf.append(FormulaErrorCode.REF.getDescription());
    }

    @Override // jxl.biff.formula.ParseItem
    byte[] getBytes() {
        byte[] data = new byte[5];
        data[0] = Token.REFERR.getCode();
        return data;
    }

    @Override // jxl.biff.formula.ParseItem
    void handleImportedCellReferences() {
    }
}
