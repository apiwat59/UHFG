package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.entity.WifiHotspotInfo;
import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.JsonReader;
import com.pda.uhf_g.util.ExcelUtil;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/* loaded from: classes.dex */
public class MsgAppGetWifiHotspotSearch extends Message {
    private JsonReader jsonReader;
    private byte[] packetContent;
    private Long searchResultPacketNumber;

    public MsgAppGetWifiHotspotSearch() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 50;
            this.jsonReader = new JsonReader();
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppGetWifiHotspotSearch(byte[] data) {
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
            this.searchResultPacketNumber = Long.valueOf(buffer.getLongUnsigned(32));
            int len = buffer.getIntUnsigned(16);
            byte[] packetData = new byte[len];
            if (len > 0) {
                this.packetContent = buffer.get(packetData);
            }
        } catch (Exception e) {
        }
    }

    public Long getSearchResultPacketNumber() {
        return this.searchResultPacketNumber;
    }

    public void setSearchResultPacketNumber(Long searchResultPacketNumber) {
        this.searchResultPacketNumber = searchResultPacketNumber;
    }

    public byte[] getPacketContent() {
        return this.packetContent;
    }

    public void setPacketContent(byte[] packetContent) {
        this.packetContent = packetContent;
    }

    public WifiHotspotInfo getWifiFormatterParam(byte[] packetContent) {
        if (packetContent != null) {
            try {
                if (packetContent.length > 0) {
                    return (WifiHotspotInfo) this.jsonReader.jsonToClass(new String(packetContent, ExcelUtil.UTF8_ENCODING), WifiHotspotInfo.class);
                }
                return null;
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        }
        return null;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            buffer.putLong(this.searchResultPacketNumber.longValue(), 32);
            this.cData = buffer.asByteArray();
            this.dataLen = this.cData.length;
        } catch (Exception e) {
        }
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            buffer.putLong(this.searchResultPacketNumber.longValue(), 32);
            byte[] bArr = this.packetContent;
            if (bArr != null && bArr.length > 0) {
                buffer.putInt(bArr.length, 16);
                buffer.put(this.packetContent, 8);
            } else {
                buffer.putInt(0, 16);
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
            this.searchResultPacketNumber = Long.valueOf(buffer.getLongUnsigned(32));
            int len = buffer.getIntUnsigned(16);
            byte[] packetData = new byte[len];
            if (len > 0) {
                this.packetContent = buffer.get(packetData);
            }
            setRtCode((byte) 0);
        }
    }

    public String toString() {
        return "MsgAppGetWifiHotspotSearch{searchResultPacketNumber=" + this.searchResultPacketNumber + ", packetContent=" + Arrays.toString(this.packetContent) + '}';
    }
}
