package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.StringUtils;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgAppSetHttpParam extends Message {
    private int format;
    private int onOrOff;
    private int openCache;
    private int period;
    private String reportAddress;
    private int timeout;

    public MsgAppSetHttpParam() {
        this.period = 0;
        this.format = 0;
        this.timeout = 0;
        this.openCache = 0;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 41;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppSetHttpParam(byte[] data) {
        this();
        int reLen;
        if (data == null) {
            return;
        }
        try {
            if (data.length <= 0) {
                return;
            }
            BitBuffer buffer = BitBuffer.wrap(data);
            buffer.position(0);
            this.onOrOff = buffer.getIntUnsigned(8);
            this.period = buffer.getIntUnsigned(16);
            this.format = buffer.getIntUnsigned(8);
            this.timeout = buffer.getIntUnsigned(16);
            this.openCache = buffer.getIntUnsigned(8);
            while (buffer.position() / 8 < data.length) {
                byte pid = buffer.getByte();
                if (pid == 1 && (reLen = buffer.getIntUnsigned(16)) > 0) {
                    this.reportAddress = new String(buffer.get(new byte[reLen]), "ASCII");
                }
            }
        } catch (Exception e) {
        }
    }

    public int getOnOrOff() {
        return this.onOrOff;
    }

    public void setOnOrOff(int onOrOff) {
        this.onOrOff = onOrOff;
    }

    public int getPeriod() {
        return this.period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getFormat() {
        return this.format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getOpenCache() {
        return this.openCache;
    }

    public void setOpenCache(int openCache) {
        this.openCache = openCache;
    }

    public String getReportAddress() {
        return this.reportAddress;
    }

    public void setReportAddress(String reportAddress) {
        this.reportAddress = reportAddress;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.putInt(this.onOrOff, 8);
        buffer.putInt(this.period, 16);
        buffer.put(this.format, 8);
        buffer.put(this.timeout, 16);
        buffer.putInt(this.openCache, 8);
        if (!StringUtils.isNullOfEmpty(this.reportAddress)) {
            buffer.putInt(1, 8);
            buffer.putInt(this.reportAddress.length(), 16);
            buffer.put(this.reportAddress);
        }
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgAppSetHttpParam.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Fail");
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
