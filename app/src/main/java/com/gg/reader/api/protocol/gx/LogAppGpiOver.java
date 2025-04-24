package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.DateTimeUtils;
import java.util.Date;
import java.util.Formatter;
import java.util.TimeZone;

/* loaded from: classes.dex */
public class LogAppGpiOver extends Message {
    private int gpiPort;
    private int gpiPortLevel;
    private String readerSerialNumber;
    private Date systemTime = new Date();

    public LogAppGpiOver() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 1;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public int getGpiPort() {
        return this.gpiPort;
    }

    public void setGpiPort(int gpiPort) {
        this.gpiPort = gpiPort;
    }

    public int getGpiPortLevel() {
        return this.gpiPortLevel;
    }

    public void setGpiPortLevel(int gpiPortLevel) {
        this.gpiPortLevel = gpiPortLevel;
    }

    public Date getSystemTime() {
        return this.systemTime;
    }

    public void setSystemTime(Date systemTime) {
        this.systemTime = systemTime;
    }

    public String getReaderSerialNumber() {
        return this.readerSerialNumber;
    }

    public void setReaderSerialNumber(String readerSerialNumber) {
        this.readerSerialNumber = readerSerialNumber;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        super.pack();
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.putLong(this.gpiPort, 8);
        buffer.putLong(this.gpiPortLevel, 8);
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
        if (this.cData != null && this.cData.length > 0) {
            BitBuffer buffer = BitBuffer.wrap(this.cData);
            buffer.position(0);
            this.gpiPort = buffer.getIntUnsigned(8);
            this.gpiPortLevel = buffer.getIntUnsigned(8);
            long utcSecond = buffer.getLong(32) * 1000;
            long utcMicrosecond = buffer.getLong(32) / 1000;
            long ms = utcSecond + utcMicrosecond;
            this.systemTime = DateTimeUtils.fromUtcToTimeZone(ms, TimeZone.getDefault());
        }
    }

    public String toString() {
        return "LogAppGpiOver{gpiPort=" + this.gpiPort + ", gpiPortLevel=" + this.gpiPortLevel + ", systemTime=" + this.systemTime + ", readerSerialNumber='" + this.readerSerialNumber + "'}";
    }
}
