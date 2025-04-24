package jxl.biff.drawing;

import jxl.common.Logger;

/* loaded from: classes.dex */
class BStoreContainer extends EscherContainer {
    private static Logger logger = Logger.getLogger(BStoreContainer.class);
    private int numBlips;

    public BStoreContainer(EscherRecordData erd) {
        super(erd);
        this.numBlips = getInstance();
    }

    public BStoreContainer() {
        super(EscherRecordType.BSTORE_CONTAINER);
    }

    void setNumBlips(int count) {
        this.numBlips = count;
        setInstance(count);
    }

    public int getNumBlips() {
        return this.numBlips;
    }

    public BlipStoreEntry getDrawing(int i) {
        EscherRecord[] children = getChildren();
        BlipStoreEntry bse = (BlipStoreEntry) children[i];
        return bse;
    }
}
