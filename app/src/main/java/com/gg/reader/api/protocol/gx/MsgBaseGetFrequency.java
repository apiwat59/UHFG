package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class MsgBaseGetFrequency extends Message {
    private Boolean automatically;
    private List<Integer> listFreqCursor;

    public MsgBaseGetFrequency() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_BASE;
            this.msgType.msgId = (byte) 6;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgBaseGetFrequency(byte[] data) {
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
            if (this.listFreqCursor == null) {
                this.listFreqCursor = new ArrayList();
            }
            int len = buffer.getIntUnsigned(16);
            for (int i = 0; i < len; i++) {
                this.listFreqCursor.add(Integer.valueOf(buffer.getIntUnsigned(8)));
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

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            buffer.putInt(this.automatically.booleanValue() ? 1 : 0, 8);
            List<Integer> list = this.listFreqCursor;
            if (list != null && list.size() > 0) {
                buffer.putInt(this.listFreqCursor.size(), 16);
                Iterator<Integer> it = this.listFreqCursor.iterator();
                while (it.hasNext()) {
                    int b = it.next().intValue();
                    buffer.putLong(b, 8);
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
            this.automatically = Boolean.valueOf(buffer.getIntUnsigned(8) == 1);
            if (this.listFreqCursor == null) {
                this.listFreqCursor = new ArrayList();
            }
            int len = buffer.getIntUnsigned(16);
            for (int i = 0; i < len; i++) {
                this.listFreqCursor.add(Integer.valueOf(buffer.getIntUnsigned(8)));
            }
            setRtCode((byte) 0);
        }
    }

    public String toString() {
        return "MsgBaseGetFrequency{automatically=" + this.automatically + ", listFreqCursor=" + this.listFreqCursor + '}';
    }
}
