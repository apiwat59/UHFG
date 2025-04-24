package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;

/* loaded from: classes.dex */
public class MsgAppGetBaseVersion extends Message {
    private String baseVersions;

    public MsgAppGetBaseVersion() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 1;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppGetBaseVersion(byte[] data) {
        this();
        if (data == null) {
            return;
        }
        try {
            if (data.length <= 0) {
                return;
            }
            BitBuffer buffer = BitBuffer.wrap(data);
            buffer.position(0);
            this.baseVersions = buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8);
        } catch (Exception e) {
        }
    }

    public String getBaseVersions() {
        return this.baseVersions;
    }

    public void setBaseVersions(String baseVersions) {
        this.baseVersions = baseVersions;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            String str = this.baseVersions;
            if (str != null) {
                String[] visions = str.split("\\.");
                for (String vs : visions) {
                    buffer.putInt(Integer.parseInt(vs), 8);
                }
            }
            this.cData = buffer.asByteArray();
            this.dataLen = this.cData.length;
        } catch (Exception e) {
        }
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        if (this.cData != null && this.cData.length > 0) {
            BitBuffer buffer = BitBuffer.wrap(this.cData);
            buffer.position(0);
            this.baseVersions = buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8);
            setRtCode((byte) 0);
        }
    }

    public String toString() {
        return "MsgAppGetBaseVersion{baseVersions='" + this.baseVersions + "'}";
    }
}
