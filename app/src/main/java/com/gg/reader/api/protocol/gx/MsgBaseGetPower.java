package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import java.util.Hashtable;
import java.util.Iterator;

/* loaded from: classes.dex */
public class MsgBaseGetPower extends Message {
    private Hashtable<Integer, Integer> dicPower;

    public MsgBaseGetPower() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_BASE;
            this.msgType.msgId = (byte) 2;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgBaseGetPower(byte[] data) {
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
                int key = buffer.getIntUnsigned(8);
                int value = buffer.getIntUnsigned(8);
                if (this.dicPower == null) {
                    this.dicPower = new Hashtable<>();
                }
                this.dicPower.put(Integer.valueOf(key), Integer.valueOf(value));
            }
        } catch (Exception e) {
        }
    }

    public Hashtable<Integer, Integer> getDicPower() {
        return this.dicPower;
    }

    public void setDicPower(Hashtable<Integer, Integer> dicPower) {
        this.dicPower = dicPower;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            Hashtable<Integer, Integer> hashtable = this.dicPower;
            if (hashtable != null && hashtable.size() > 0) {
                BitBuffer buffer = BitBuffer.allocateDynamic();
                Iterator<Integer> it = this.dicPower.keySet().iterator();
                while (it.hasNext()) {
                    int iKey = it.next().intValue();
                    buffer.putLong(iKey, 8);
                    buffer.putLong(this.dicPower.get(Integer.valueOf(iKey)).intValue(), 8);
                }
                this.cData = buffer.asByteArray();
                this.dataLen = this.cData.length;
            }
        } catch (Exception e) {
        }
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        if (this.cData != null && this.cData.length > 0) {
            BitBuffer buffer = BitBuffer.wrap(this.cData);
            buffer.position(0);
            for (int i = 0; i < this.cData.length / 2; i++) {
                int key = buffer.getIntUnsigned(8);
                int value = buffer.getIntUnsigned(8);
                if (this.dicPower == null) {
                    this.dicPower = new Hashtable<>();
                }
                this.dicPower.put(Integer.valueOf(key), Integer.valueOf(value));
            }
            setRtCode((byte) 0);
        }
    }

    public String toString() {
        return "MsgBaseGetPower{dicPower=" + this.dicPower + '}';
    }
}
