package jxl.biff.drawing;

import jxl.biff.IntegerHelper;
import jxl.common.Logger;

/* loaded from: classes.dex */
final class EscherRecordData {
    private static Logger logger = Logger.getLogger(EscherRecordData.class);
    private boolean container;
    private EscherStream escherStream;
    private int instance;
    private int length;
    private int pos;
    private int recordId;
    private int streamLength;
    private EscherRecordType type;
    private int version;

    public EscherRecordData(EscherStream dg, int p) {
        this.escherStream = dg;
        this.pos = p;
        byte[] data = dg.getData();
        this.streamLength = data.length;
        int i = this.pos;
        int value = IntegerHelper.getInt(data[i], data[i + 1]);
        this.instance = (65520 & value) >> 4;
        this.version = value & 15;
        int i2 = this.pos;
        this.recordId = IntegerHelper.getInt(data[i2 + 2], data[i2 + 3]);
        int i3 = this.pos;
        this.length = IntegerHelper.getInt(data[i3 + 4], data[i3 + 5], data[i3 + 6], data[i3 + 7]);
        if (this.version == 15) {
            this.container = true;
        } else {
            this.container = false;
        }
    }

    public EscherRecordData(EscherRecordType t) {
        this.type = t;
        this.recordId = t.getValue();
    }

    public boolean isContainer() {
        return this.container;
    }

    public int getLength() {
        return this.length;
    }

    public int getRecordId() {
        return this.recordId;
    }

    EscherStream getDrawingGroup() {
        return this.escherStream;
    }

    int getPos() {
        return this.pos;
    }

    EscherRecordType getType() {
        if (this.type == null) {
            this.type = EscherRecordType.getType(this.recordId);
        }
        return this.type;
    }

    int getInstance() {
        return this.instance;
    }

    void setContainer(boolean c) {
        this.container = c;
    }

    void setInstance(int inst) {
        this.instance = inst;
    }

    void setLength(int l) {
        this.length = l;
    }

    void setVersion(int v) {
        this.version = v;
    }

    byte[] setHeaderData(byte[] d) {
        byte[] data = new byte[d.length + 8];
        System.arraycopy(d, 0, data, 8, d.length);
        if (this.container) {
            this.version = 15;
        }
        int value = this.instance << 4;
        IntegerHelper.getTwoBytes(value | this.version, data, 0);
        IntegerHelper.getTwoBytes(this.recordId, data, 2);
        IntegerHelper.getFourBytes(d.length, data, 4);
        return data;
    }

    EscherStream getEscherStream() {
        return this.escherStream;
    }

    byte[] getBytes() {
        byte[] d = new byte[this.length];
        System.arraycopy(this.escherStream.getData(), this.pos + 8, d, 0, this.length);
        return d;
    }

    int getStreamLength() {
        return this.streamLength;
    }
}
