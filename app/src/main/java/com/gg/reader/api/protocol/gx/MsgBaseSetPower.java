package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import java.util.Hashtable;
import java.util.Iterator;

/* loaded from: classes.dex */
public class MsgBaseSetPower extends Message {
    private Hashtable<Integer, Integer> dicPower;
    private int powerDownSave;

    public MsgBaseSetPower() {
        this.powerDownSave = Integer.MAX_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_BASE;
            this.msgType.msgId = (byte) 1;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgBaseSetPower(byte[] data) {
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
                if (key == 255) {
                    this.powerDownSave = buffer.getIntUnsigned(8);
                } else {
                    int value = buffer.getIntUnsigned(8);
                    if (this.dicPower == null) {
                        this.dicPower = new Hashtable<>();
                    }
                    this.dicPower.put(Integer.valueOf(key), Integer.valueOf(value));
                }
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

    public int getPowerDownSave() {
        return this.powerDownSave;
    }

    public void setPowerDownSave(int powerDownSave) {
        this.powerDownSave = powerDownSave;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        Hashtable<Integer, Integer> hashtable = this.dicPower;
        if (hashtable != null && hashtable.size() > 0) {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            Iterator<Integer> it = this.dicPower.keySet().iterator();
            while (it.hasNext()) {
                int iKey = it.next().intValue();
                buffer.putLong(iKey, 8);
                buffer.putLong(this.dicPower.get(Integer.valueOf(iKey)).intValue(), 8);
            }
            if (Integer.MAX_VALUE != this.powerDownSave) {
                buffer.putInt(255, 8);
                buffer.putInt(this.powerDownSave, 8);
            }
            this.cData = buffer.asByteArray();
            this.dataLen = this.cData.length;
        }
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgBaseSetPower.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Antenna port parameter not supported.");
                put((byte) 2, "Power parameter not supported.");
                put((byte) 3, "Save failure.");
            }
        };
        if (this.cData != null && this.cData.length == 1) {
            setRtCode(this.cData[0]);
            if (dicErrorMsg.containsKey(Byte.valueOf(this.cData[0]))) {
                setRtMsg(dicErrorMsg.get(Byte.valueOf(this.cData[0])));
            }
        }
    }
}
