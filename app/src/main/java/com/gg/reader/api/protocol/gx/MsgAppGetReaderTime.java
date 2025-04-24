package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.DateTimeUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.TimeZone;

/* loaded from: classes.dex */
public class MsgAppGetReaderTime extends Message {
    private Date systemTime;

    public MsgAppGetReaderTime() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 17;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppGetReaderTime(byte[] data) {
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

    public String getFormatTime() {
        return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(getSystemTime());
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            Formatter formatter = new Formatter();
            Object[] objArr = new Object[1];
            double UtcFromTimeZone = DateTimeUtils.UtcFromTimeZone(this.systemTime, TimeZone.getDefault());
            Double.isNaN(UtcFromTimeZone);
            objArr[0] = Double.valueOf(UtcFromTimeZone / 1000.0d);
            String utc = formatter.format("%.3f", objArr).toString();
            String[] split = utc.split("\\.");
            buffer.putLong(Integer.parseInt(split[0]), 32);
            buffer.putLong(Integer.parseInt(split[1]) * 1000, 32);
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
            long utcSecond = buffer.getLong(32) * 1000;
            long utcMicrosecond = buffer.getLong(32) / 1000;
            this.systemTime = DateTimeUtils.fromUtcToTimeZone(utcSecond + utcMicrosecond, TimeZone.getDefault());
            setRtCode((byte) 0);
        }
    }
}
