package jxl.biff.drawing;

import jxl.common.Logger;

/* loaded from: classes.dex */
class ClientTextBox extends EscherAtom {
    private static Logger logger = Logger.getLogger(ClientTextBox.class);
    private byte[] data;

    public ClientTextBox(EscherRecordData erd) {
        super(erd);
    }

    public ClientTextBox() {
        super(EscherRecordType.CLIENT_TEXT_BOX);
    }

    @Override // jxl.biff.drawing.EscherAtom, jxl.biff.drawing.EscherRecord
    byte[] getData() {
        byte[] bArr = new byte[0];
        this.data = bArr;
        return setHeaderData(bArr);
    }
}
