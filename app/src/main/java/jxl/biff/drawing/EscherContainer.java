package jxl.biff.drawing;

import java.util.ArrayList;
import java.util.Iterator;
import jxl.common.Logger;

/* loaded from: classes.dex */
class EscherContainer extends EscherRecord {
    private static Logger logger = Logger.getLogger(EscherContainer.class);
    private ArrayList children;
    private boolean initialized;

    public EscherContainer(EscherRecordData erd) {
        super(erd);
        this.initialized = false;
        this.children = new ArrayList();
    }

    protected EscherContainer(EscherRecordType type) {
        super(type);
        setContainer(true);
        this.children = new ArrayList();
    }

    public EscherRecord[] getChildren() {
        if (!this.initialized) {
            initialize();
        }
        ArrayList arrayList = this.children;
        Object[] ca = arrayList.toArray(new EscherRecord[arrayList.size()]);
        return (EscherRecord[]) ca;
    }

    public void add(EscherRecord child) {
        this.children.add(child);
    }

    public void remove(EscherRecord child) {
        this.children.remove(child);
    }

    private void initialize() {
        EscherRecord escherAtom;
        int curpos = getPos() + 8;
        int endpos = Math.min(getPos() + getLength(), getStreamLength());
        while (curpos < endpos) {
            EscherRecordData erd = new EscherRecordData(getEscherStream(), curpos);
            EscherRecordType type = erd.getType();
            if (type == EscherRecordType.DGG) {
                escherAtom = new Dgg(erd);
            } else if (type == EscherRecordType.DG) {
                escherAtom = new Dg(erd);
            } else if (type == EscherRecordType.BSTORE_CONTAINER) {
                escherAtom = new BStoreContainer(erd);
            } else if (type == EscherRecordType.SPGR_CONTAINER) {
                escherAtom = new SpgrContainer(erd);
            } else if (type == EscherRecordType.SP_CONTAINER) {
                escherAtom = new SpContainer(erd);
            } else if (type == EscherRecordType.SPGR) {
                escherAtom = new Spgr(erd);
            } else if (type == EscherRecordType.SP) {
                escherAtom = new Sp(erd);
            } else if (type == EscherRecordType.CLIENT_ANCHOR) {
                escherAtom = new ClientAnchor(erd);
            } else if (type == EscherRecordType.CLIENT_DATA) {
                escherAtom = new ClientData(erd);
            } else if (type == EscherRecordType.BSE) {
                escherAtom = new BlipStoreEntry(erd);
            } else if (type == EscherRecordType.OPT) {
                escherAtom = new Opt(erd);
            } else if (type == EscherRecordType.SPLIT_MENU_COLORS) {
                escherAtom = new SplitMenuColors(erd);
            } else if (type == EscherRecordType.CLIENT_TEXT_BOX) {
                escherAtom = new ClientTextBox(erd);
            } else {
                escherAtom = new EscherAtom(erd);
            }
            EscherRecord newRecord = escherAtom;
            this.children.add(newRecord);
            curpos += newRecord.getLength();
        }
        this.initialized = true;
    }

    @Override // jxl.biff.drawing.EscherRecord
    byte[] getData() {
        if (!this.initialized) {
            initialize();
        }
        byte[] data = new byte[0];
        Iterator i = this.children.iterator();
        while (i.hasNext()) {
            EscherRecord er = (EscherRecord) i.next();
            byte[] childData = er.getData();
            if (childData != null) {
                byte[] newData = new byte[data.length + childData.length];
                System.arraycopy(data, 0, newData, 0, data.length);
                System.arraycopy(childData, 0, newData, data.length, childData.length);
                data = newData;
            }
        }
        return setHeaderData(data);
    }
}
