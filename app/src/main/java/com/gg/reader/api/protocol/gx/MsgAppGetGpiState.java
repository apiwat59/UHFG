package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import java.util.HashMap;
import java.util.Iterator;

/* loaded from: classes.dex */
public class MsgAppGetGpiState extends Message {
    private HashMap<Integer, Integer> hpGpiState;

    public MsgAppGetGpiState() {
        this.hpGpiState = new HashMap<>();
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 10;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppGetGpiState(byte[] data) {
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
            for (int i = 0; i < data.length / 2; i++) {
                this.hpGpiState.put(Integer.valueOf(buffer.getIntUnsigned(8)), Integer.valueOf(buffer.getIntUnsigned(8)));
            }
        } catch (Exception e) {
        }
    }

    public HashMap<Integer, Integer> getHpGpiState() {
        return this.hpGpiState;
    }

    public void setHpGpiState(HashMap<Integer, Integer> hpGpiState) {
        this.hpGpiState = hpGpiState;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        HashMap<Integer, Integer> hashMap = this.hpGpiState;
        if (hashMap != null && hashMap.size() > 0) {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            Iterator<Integer> it = this.hpGpiState.keySet().iterator();
            while (it.hasNext()) {
                int iKey = it.next().intValue();
                buffer.putInt(iKey, 8);
                buffer.putInt(this.hpGpiState.get(Integer.valueOf(iKey)).intValue(), 8);
            }
            this.cData = buffer.asByteArray();
            this.dataLen = this.cData.length;
        }
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        if (this.cData != null && this.cData.length > 0) {
            BitBuffer buffer = BitBuffer.wrap(this.cData);
            buffer.position(0);
            for (int i = 0; i < this.cData.length / 2; i++) {
                this.hpGpiState.put(Integer.valueOf(buffer.getIntUnsigned(8)), Integer.valueOf(buffer.getIntUnsigned(8)));
            }
            setRtCode((byte) 0);
        }
    }

    public String toString() {
        return "MsgAppGetGpiState{hpGpiState=" + this.hpGpiState + '}';
    }
}
