package jxl.biff.formula;

import jxl.biff.IntegerHelper;

/* loaded from: classes.dex */
class MemArea extends SubExpression {
    @Override // jxl.biff.formula.ParseItem
    public void getString(StringBuffer buf) {
        ParseItem[] subExpression = getSubExpression();
        if (subExpression.length == 1) {
            subExpression[0].getString(buf);
        } else if (subExpression.length == 2) {
            subExpression[1].getString(buf);
            buf.append(':');
            subExpression[0].getString(buf);
        }
    }

    @Override // jxl.biff.formula.SubExpression, jxl.biff.formula.ParsedThing
    public int read(byte[] data, int pos) {
        setLength(IntegerHelper.getInt(data[pos + 4], data[pos + 5]));
        return 6;
    }

    @Override // jxl.biff.formula.ParseItem
    void handleImportedCellReferences() {
    }
}
