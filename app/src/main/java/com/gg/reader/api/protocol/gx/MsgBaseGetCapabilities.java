package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class MsgBaseGetCapabilities extends Message {
    private int antennaCount;
    private List<Integer> frequencyArray;
    private int maxPower;
    private int minPower;
    private List<Integer> protocolArray;

    public MsgBaseGetCapabilities() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_BASE;
            this.msgType.msgId = (byte) 0;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgBaseGetCapabilities(byte[] data) {
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
            this.minPower = buffer.getIntUnsigned(8);
            this.maxPower = buffer.getIntUnsigned(8);
            this.antennaCount = buffer.getIntUnsigned(8);
            int freLen = buffer.getIntUnsigned(16);
            if (this.frequencyArray == null) {
                this.frequencyArray = new ArrayList();
            }
            for (int i = 0; i < freLen; i++) {
                this.frequencyArray.add(Integer.valueOf(buffer.getIntUnsigned(8)));
            }
            int proLen = buffer.getIntUnsigned(16);
            if (this.protocolArray == null) {
                this.protocolArray = new ArrayList();
            }
            for (int i2 = 0; i2 < proLen; i2++) {
                this.protocolArray.add(Integer.valueOf(buffer.getIntUnsigned(8)));
            }
        } catch (Exception e) {
        }
    }

    public int getMaxPower() {
        return this.maxPower;
    }

    public void setMaxPower(int maxPower) {
        this.maxPower = maxPower;
    }

    public int getMinPower() {
        return this.minPower;
    }

    public void setMinPower(int minPower) {
        this.minPower = minPower;
    }

    public int getAntennaCount() {
        return this.antennaCount;
    }

    public void setAntennaCount(int antennaCount) {
        this.antennaCount = antennaCount;
    }

    public List<Integer> getFrequencyArray() {
        return this.frequencyArray;
    }

    public void setFrequencyArray(List<Integer> frequencyArray) {
        this.frequencyArray = frequencyArray;
    }

    public List<Integer> getProtocolArray() {
        return this.protocolArray;
    }

    public void setProtocolArray(List<Integer> protocolArray) {
        this.protocolArray = protocolArray;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            buffer.putLong(this.minPower, 8);
            buffer.putLong(this.maxPower, 8);
            buffer.putLong(this.antennaCount, 8);
            List<Integer> list = this.frequencyArray;
            if (list != null && list.size() > 0) {
                buffer.putInt(this.frequencyArray.size(), 16);
                Iterator<Integer> it = this.frequencyArray.iterator();
                while (it.hasNext()) {
                    int b = it.next().intValue();
                    buffer.putLong(b, 8);
                }
            }
            List<Integer> list2 = this.protocolArray;
            if (list2 != null && list2.size() > 0) {
                buffer.putInt(this.protocolArray.size(), 16);
                Iterator<Integer> it2 = this.protocolArray.iterator();
                while (it2.hasNext()) {
                    int b2 = it2.next().intValue();
                    buffer.putLong(b2, 8);
                }
            }
            this.cData = buffer.asByteArray();
            this.dataLen = this.cData.length;
        } catch (Exception e) {
        }
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        if (this.cData != null && this.cData.length > 8) {
            BitBuffer buffer = BitBuffer.wrap(this.cData);
            buffer.position(0);
            this.minPower = buffer.getIntUnsigned(8);
            this.maxPower = buffer.getIntUnsigned(8);
            this.antennaCount = buffer.getIntUnsigned(8);
            int freqLen = buffer.getIntUnsigned(16);
            if (this.frequencyArray == null) {
                this.frequencyArray = new ArrayList();
            }
            for (int i = 0; i < freqLen; i++) {
                this.frequencyArray.add(Integer.valueOf(buffer.getIntUnsigned(8)));
            }
            int proLen = buffer.getIntUnsigned(16);
            if (this.protocolArray == null) {
                this.protocolArray = new ArrayList();
            }
            for (int i2 = 0; i2 < proLen; i2++) {
                this.protocolArray.add(Integer.valueOf(buffer.getIntUnsigned(8)));
            }
            setRtCode((byte) 0);
        }
    }

    public String toString() {
        return "MsgBaseGetCapabilities{minPower=" + this.minPower + ", maxPower=" + this.maxPower + ", antennaCount=" + this.antennaCount + ", frequencyArray=" + this.frequencyArray + ", protocolArray=" + this.protocolArray + '}';
    }
}
