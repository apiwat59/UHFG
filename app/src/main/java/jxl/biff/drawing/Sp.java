package jxl.biff.drawing;

import jxl.biff.IntegerHelper;
import jxl.common.Logger;

/* loaded from: classes.dex */
class Sp extends EscherAtom {
    private static Logger logger = Logger.getLogger(Sp.class);
    private byte[] data;
    private int persistenceFlags;
    private int shapeId;
    private int shapeType;

    public Sp(EscherRecordData erd) {
        super(erd);
        this.shapeType = getInstance();
        byte[] bytes = getBytes();
        this.shapeId = IntegerHelper.getInt(bytes[0], bytes[1], bytes[2], bytes[3]);
        this.persistenceFlags = IntegerHelper.getInt(bytes[4], bytes[5], bytes[6], bytes[7]);
    }

    public Sp(ShapeType st, int sid, int p) {
        super(EscherRecordType.SP);
        setVersion(2);
        int value = st.getValue();
        this.shapeType = value;
        this.shapeId = sid;
        this.persistenceFlags = p;
        setInstance(value);
    }

    int getShapeId() {
        return this.shapeId;
    }

    int getShapeType() {
        return this.shapeType;
    }

    @Override // jxl.biff.drawing.EscherAtom, jxl.biff.drawing.EscherRecord
    byte[] getData() {
        byte[] bArr = new byte[8];
        this.data = bArr;
        IntegerHelper.getFourBytes(this.shapeId, bArr, 0);
        IntegerHelper.getFourBytes(this.persistenceFlags, this.data, 4);
        return setHeaderData(this.data);
    }
}
