package jxl.write.biff;

import jxl.biff.Type;
import jxl.biff.WritableRecordData;
import jxl.common.Logger;

/* loaded from: classes.dex */
class ArbitraryRecord extends WritableRecordData {
    private static Logger logger = Logger.getLogger(ArbitraryRecord.class);
    private byte[] data;

    public ArbitraryRecord(int type, byte[] d) {
        super(Type.createType(type));
        this.data = d;
        logger.warn("ArbitraryRecord of type " + type + " created");
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        return this.data;
    }
}
