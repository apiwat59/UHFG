package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import java.util.Hashtable;
import java.util.Iterator;

/* loaded from: classes.dex */
public class MsgBaseSetAntennaHub extends Message {
    private Hashtable<Integer, Integer> dicHub;

    public MsgBaseSetAntennaHub() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_BASE;
            this.msgType.msgId = (byte) 7;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgBaseSetAntennaHub(byte[] data) {
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
            for (int i = 0; i < data.length / 3; i++) {
                int key = buffer.getIntUnsigned(8);
                int value = buffer.getIntUnsigned(16);
                if (this.dicHub == null) {
                    this.dicHub = new Hashtable<>();
                }
                this.dicHub.put(Integer.valueOf(key), Integer.valueOf(value));
            }
        } catch (Exception e) {
        }
    }

    public Hashtable<Integer, Integer> getDicHub() {
        return this.dicHub;
    }

    public void setDicHub(Hashtable<Integer, Integer> dicHub) {
        this.dicHub = dicHub;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        Hashtable<Integer, Integer> hashtable = this.dicHub;
        if (hashtable != null && hashtable.size() > 0) {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            Iterator<Integer> it = this.dicHub.keySet().iterator();
            while (it.hasNext()) {
                int iKey = it.next().intValue();
                buffer.putLong(iKey, 8);
                buffer.putLong(this.dicHub.get(Integer.valueOf(iKey)).intValue(), 16);
            }
            this.cData = buffer.asByteArray();
            this.dataLen = this.cData.length;
        }
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgBaseSetAntennaHub.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Antenna port parameter is not supported.");
                put((byte) 2, "Save failure.");
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
