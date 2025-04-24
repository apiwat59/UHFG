package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.StringUtils;

/* loaded from: classes.dex */
public class MsgAppGetWifiConnectStatus extends Message {
    private String hotspotName;

    public MsgAppGetWifiConnectStatus() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 52;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppGetWifiConnectStatus(byte[] data) {
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
            int hnLen = buffer.getIntUnsigned(16);
            if (hnLen > 0) {
                this.hotspotName = new String(buffer.get(new byte[hnLen]), "ASCII");
            }
        } catch (Exception e) {
        }
    }

    public String getHotspotName() {
        return this.hotspotName;
    }

    public void setHotspotName(String hotspotName) {
        this.hotspotName = hotspotName;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            if (!StringUtils.isNullOfEmpty(this.hotspotName)) {
                buffer.putInt(this.hotspotName.length(), 16);
                buffer.put(this.hotspotName);
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
            int hnLen = buffer.getIntUnsigned(16);
            try {
                if (hnLen > 0) {
                    this.hotspotName = new String(buffer.get(new byte[hnLen]), "ASCII");
                } else {
                    this.hotspotName = "未连接";
                }
            } catch (Exception e) {
            }
            setRtCode((byte) 0);
        }
    }

    public String toString() {
        return "MsgAppGetWifiConnectStatus{hotspotName='" + this.hotspotName + "'}";
    }
}
