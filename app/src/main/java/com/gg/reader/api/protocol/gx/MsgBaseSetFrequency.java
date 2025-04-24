package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class MsgBaseSetFrequency extends Message {
    private Boolean automatically;
    private List<Integer> listFreqCursor;
    private int powerDownSave;

    public MsgBaseSetFrequency() {
        this.powerDownSave = Integer.MAX_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_BASE;
            this.msgType.msgId = (byte) 5;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgBaseSetFrequency(byte[] data) {
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
            this.automatically = Boolean.valueOf(buffer.getIntUnsigned(8) == 1);
            while (buffer.position() / 8 < data.length) {
                byte pid = buffer.getByte();
                if (pid == 1) {
                    if (this.listFreqCursor == null) {
                        this.listFreqCursor = new ArrayList();
                    }
                    int len = buffer.getIntUnsigned(16);
                    for (int i = 0; i < len; i++) {
                        this.listFreqCursor.add(Integer.valueOf(buffer.getIntUnsigned(8)));
                    }
                } else if (pid == 2) {
                    this.powerDownSave = buffer.getIntUnsigned(8);
                }
            }
        } catch (Exception e) {
        }
    }

    public Boolean getAutomatically() {
        return this.automatically;
    }

    public void setAutomatically(Boolean automatically) {
        this.automatically = automatically;
    }

    public List<Integer> getListFreqCursor() {
        return this.listFreqCursor;
    }

    public void setListFreqCursor(List<Integer> listFreqCursor) {
        this.listFreqCursor = listFreqCursor;
    }

    public int getPowerDownSave() {
        return this.powerDownSave;
    }

    public void setPowerDownSave(int powerDownSave) {
        this.powerDownSave = powerDownSave;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        BitBuffer allocateDynamic = BitBuffer.allocateDynamic();
        allocateDynamic.putInt(this.automatically.booleanValue() ? 1 : 0, 8);
        List<Integer> list = this.listFreqCursor;
        if (list != null && list.size() > 0) {
            allocateDynamic.putInt(1, 8);
            allocateDynamic.putInt(this.listFreqCursor.size(), 16);
            Iterator<Integer> it = this.listFreqCursor.iterator();
            while (it.hasNext()) {
                allocateDynamic.putLong(it.next().intValue(), 8);
            }
        }
        if (Integer.MAX_VALUE != this.powerDownSave) {
            allocateDynamic.putInt(2, 8);
            allocateDynamic.putInt(this.powerDownSave, 8);
        }
        this.cData = allocateDynamic.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgBaseSetFrequency.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "The channel number is not in the current frequency band.");
                put((byte) 2, "Invalid frequency points.");
                put((byte) 3, "Other error.");
                put((byte) 4, "Save failure.");
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
