package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.DateTimeUtils;
import java.util.Date;
import java.util.Formatter;
import java.util.Hashtable;
import java.util.TimeZone;

/* loaded from: classes.dex */
public class MsgAppSetReaderTime extends Message {
    private Date systemTime;

    public MsgAppSetReaderTime() {
        this.systemTime = new Date();
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 16;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppSetReaderTime(byte[] data) {
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
            long utcSecond = buffer.getLong(32) * 1000;
            long utcMicrosecond = buffer.getLong(32) / 1000;
            this.systemTime = DateTimeUtils.fromUtcToTimeZone(utcSecond + utcMicrosecond, TimeZone.getDefault());
        } catch (Exception e) {
        }
    }

    public Date getSystemTime() {
        return this.systemTime;
    }

    public void setSystemTime(Date systemTime) {
        this.systemTime = systemTime;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        Formatter formatter = new Formatter();
        double UtcFromTimeZone = DateTimeUtils.UtcFromTimeZone(this.systemTime, TimeZone.getDefault());
        Double.isNaN(UtcFromTimeZone);
        String utc = formatter.format("%.3f", Double.valueOf(UtcFromTimeZone / 1000.0d)).toString();
        String[] split = utc.split("\\.");
        buffer.putLong(Integer.parseInt(split[0]), 32);
        buffer.putLong(Integer.parseInt(split[1]) * 1000, 32);
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgAppSetReaderTime.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "RTC setup failed.");
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
