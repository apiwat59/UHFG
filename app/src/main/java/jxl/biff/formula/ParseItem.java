package jxl.biff.formula;

import jxl.common.Logger;

/* loaded from: classes.dex */
abstract class ParseItem {
    private static Logger logger = Logger.getLogger(ParseItem.class);
    private ParseItem parent;
    private boolean volatileFunction = false;
    private boolean alternateCode = false;
    private boolean valid = true;
    private ParseContext parseContext = ParseContext.DEFAULT;

    abstract void adjustRelativeCellReferences(int i, int i2);

    abstract void columnInserted(int i, int i2, boolean z);

    abstract void columnRemoved(int i, int i2, boolean z);

    abstract byte[] getBytes();

    abstract void getString(StringBuffer stringBuffer);

    abstract void handleImportedCellReferences();

    abstract void rowInserted(int i, int i2, boolean z);

    abstract void rowRemoved(int i, int i2, boolean z);

    protected void setParent(ParseItem p) {
        this.parent = p;
    }

    protected void setVolatile() {
        this.volatileFunction = true;
        ParseItem parseItem = this.parent;
        if (parseItem != null && !parseItem.isVolatile()) {
            this.parent.setVolatile();
        }
    }

    protected final void setInvalid() {
        this.valid = false;
        ParseItem parseItem = this.parent;
        if (parseItem != null) {
            parseItem.setInvalid();
        }
    }

    final boolean isVolatile() {
        return this.volatileFunction;
    }

    final boolean isValid() {
        return this.valid;
    }

    protected void setAlternateCode() {
        this.alternateCode = true;
    }

    protected final boolean useAlternateCode() {
        return this.alternateCode;
    }

    protected void setParseContext(ParseContext pc) {
        this.parseContext = pc;
    }

    protected final ParseContext getParseContext() {
        return this.parseContext;
    }
}
