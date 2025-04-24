package jxl.biff.drawing;

import jxl.common.Logger;

/* loaded from: classes.dex */
class ClientData extends EscherAtom {
    private static Logger logger = Logger.getLogger(ClientData.class);
    private byte[] data;

    public ClientData(EscherRecordData erd) {
        super(erd);
    }

    public ClientData() {
        super(EscherRecordType.CLIENT_DATA);
    }

    @Override // jxl.biff.drawing.EscherAtom, jxl.biff.drawing.EscherRecord
    byte[] getData() {
        byte[] bArr = new byte[0];
        this.data = bArr;
        return setHeaderData(bArr);
    }
}
