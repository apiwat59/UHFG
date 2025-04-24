package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.DateTimeUtils;
import com.gg.reader.api.utils.HexUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.TimeZone;

/* loaded from: classes.dex */
public class LogBaseEpcInfo extends Message {
    private int antId;
    private byte[] bEpc;
    private byte[] bEpcData;
    private byte[] bRes;
    private byte[] bTid;
    private byte[] bUser;
    private int childAntId;
    private int crc;
    private int ctesiusLtu27;
    private int ctesiusLtu31;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String epc;
    private String epcData;
    private Long frequencyPoint;
    private int kunYue;
    private int pc;
    private int phase;
    private String readerSerialNumber;
    private Long replySerialNumber;
    private String reserved;
    private int result;
    private int rssi;
    private int rssidBm;
    private String strUtc;
    private String tid;
    private String userdata;

    public String getEpc() {
        return this.epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public byte[] getbEpc() {
        return this.bEpc;
    }

    public void setbEpc(byte[] bEpc) {
        this.bEpc = bEpc;
    }

    public int getPc() {
        return this.pc;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public int getAntId() {
        return this.antId;
    }

    public void setAntId(int antId) {
        this.antId = antId;
    }

    public int getRssi() {
        return this.rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getResult() {
        return this.result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getTid() {
        return this.tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public byte[] getbTid() {
        return this.bTid;
    }

    public void setbTid(byte[] bTid) {
        this.bTid = bTid;
    }

    public String getUserdata() {
        return this.userdata;
    }

    public void setUserdata(String userdata) {
        this.userdata = userdata;
    }

    public byte[] getbUser() {
        return this.bUser;
    }

    public void setbUser(byte[] bUser) {
        this.bUser = bUser;
    }

    public String getReserved() {
        return this.reserved;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved;
    }

    public byte[] getbRes() {
        return this.bRes;
    }

    public void setbRes(byte[] bRes) {
        this.bRes = bRes;
    }

    public int getChildAntId() {
        return this.childAntId;
    }

    public void setChildAntId(int childAntId) {
        this.childAntId = childAntId;
    }

    public String getStrUtc() {
        return this.strUtc;
    }

    public void setStrUtc(String strUtc) {
        this.strUtc = strUtc;
    }

    public Long getFrequencyPoint() {
        return this.frequencyPoint;
    }

    public void setFrequencyPoint(Long frequencyPoint) {
        this.frequencyPoint = frequencyPoint;
    }

    public int getPhase() {
        return this.phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public String getEpcData() {
        return this.epcData;
    }

    public void setEpcData(String epcData) {
        this.epcData = epcData;
    }

    public byte[] getbEpcData() {
        return this.bEpcData;
    }

    public void setbEpcData(byte[] bEpcData) {
        this.bEpcData = bEpcData;
    }

    public int getCtesiusLtu27() {
        return this.ctesiusLtu27;
    }

    public void setCtesiusLtu27(int ctesiusLtu27) {
        this.ctesiusLtu27 = ctesiusLtu27;
    }

    public int getCtesiusLtu31() {
        return this.ctesiusLtu31;
    }

    public void setCtesiusLtu31(int ctesiusLtu31) {
        this.ctesiusLtu31 = ctesiusLtu31;
    }

    public String getReaderSerialNumber() {
        return this.readerSerialNumber;
    }

    public void setReaderSerialNumber(String readerSerialNumber) {
        this.readerSerialNumber = readerSerialNumber;
    }

    public Long getReplySerialNumber() {
        return this.replySerialNumber;
    }

    public void setReplySerialNumber(Long replySerialNumber) {
        this.replySerialNumber = replySerialNumber;
    }

    public int getRssidBm() {
        return this.rssidBm;
    }

    public void setRssidBm(int rssidBm) {
        this.rssidBm = rssidBm;
    }

    public int getKunYue() {
        return this.kunYue;
    }

    public void setKunYue(int kunYue) {
        this.kunYue = kunYue;
    }

    public int getCrc() {
        return this.crc;
    }

    public void setCrc(int crc) {
        this.crc = crc;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        super.pack();
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        if (this.cData != null && this.cData.length > 0) {
            BitBuffer buffer = BitBuffer.wrap(this.cData);
            buffer.position(0);
            int epcLen = buffer.getIntUnsigned(16);
            byte[] bArr = new byte[epcLen];
            this.bEpc = bArr;
            byte[] bArr2 = buffer.get(bArr);
            this.bEpc = bArr2;
            if (bArr2.length > 0) {
                this.epc = HexUtils.bytes2HexString(bArr2);
            }
            this.pc = buffer.getIntUnsigned(16);
            this.antId = buffer.getIntUnsigned(8);
            while (buffer.position() / 8 < this.cData.length) {
                int index = buffer.getIntUnsigned(8);
                if (index == 32) {
                    try {
                        int snLen = buffer.getIntUnsigned(16);
                        if (snLen > 0) {
                            this.readerSerialNumber = new String(buffer.get(new byte[snLen]), "ASCII");
                        }
                    } catch (Exception e) {
                    }
                } else if (index != 34) {
                    switch (index) {
                        case 1:
                            this.rssi = buffer.getIntUnsigned(8);
                            break;
                        case 2:
                            this.result = buffer.getIntUnsigned(8);
                            break;
                        case 3:
                            try {
                                int tidLen = buffer.getIntUnsigned(16);
                                if (tidLen <= 0) {
                                    break;
                                } else {
                                    byte[] bArr3 = new byte[tidLen];
                                    this.bTid = bArr3;
                                    byte[] bArr4 = buffer.get(bArr3);
                                    this.bTid = bArr4;
                                    this.tid = HexUtils.bytes2HexString(bArr4);
                                    break;
                                }
                            } catch (Exception e2) {
                                break;
                            }
                        case 4:
                            try {
                                int userLen = buffer.getIntUnsigned(16);
                                if (userLen <= 0) {
                                    break;
                                } else {
                                    byte[] bArr5 = new byte[userLen];
                                    this.bUser = bArr5;
                                    byte[] bArr6 = buffer.get(bArr5);
                                    this.bUser = bArr6;
                                    this.userdata = HexUtils.bytes2HexString(bArr6);
                                    break;
                                }
                            } catch (Exception e3) {
                                break;
                            }
                        case 5:
                            try {
                                int resLen = buffer.getIntUnsigned(16);
                                if (resLen <= 0) {
                                    break;
                                } else {
                                    byte[] bArr7 = new byte[resLen];
                                    this.bRes = bArr7;
                                    byte[] bArr8 = buffer.get(bArr7);
                                    this.bRes = bArr8;
                                    this.reserved = HexUtils.bytes2HexString(bArr8);
                                    break;
                                }
                            } catch (Exception e4) {
                                break;
                            }
                        case 6:
                            this.childAntId = buffer.getIntUnsigned(8);
                            break;
                        case 7:
                            long utcSecond = buffer.getLong(32) * 1000;
                            long utcMicrosecond = buffer.getLong(32) / 1000;
                            long ms = utcSecond + utcMicrosecond;
                            this.strUtc = this.dateFormat.format(DateTimeUtils.fromUtcToTimeZone(ms, TimeZone.getDefault()));
                            break;
                        case 8:
                            this.frequencyPoint = Long.valueOf(buffer.getLongUnsigned(32));
                            break;
                        case 9:
                            this.phase = buffer.getIntUnsigned(8);
                            break;
                        case 10:
                            try {
                                int epcDataLen = buffer.getIntUnsigned(16);
                                if (epcDataLen <= 0) {
                                    break;
                                } else {
                                    byte[] bArr9 = new byte[epcDataLen];
                                    this.bEpcData = bArr9;
                                    byte[] bArr10 = buffer.get(bArr9);
                                    this.bEpcData = bArr10;
                                    this.epcData = HexUtils.bytes2HexString(bArr10);
                                    break;
                                }
                            } catch (Exception e5) {
                                break;
                            }
                        default:
                            switch (index) {
                                case 17:
                                    this.ctesiusLtu27 = buffer.getIntUnsigned(16);
                                    break;
                                case 18:
                                    this.ctesiusLtu31 = buffer.getInt(16);
                                    break;
                                case 19:
                                    this.kunYue = buffer.getIntUnsigned(16);
                                    break;
                                case 20:
                                    this.rssidBm = buffer.getInt(16);
                                    break;
                                case 21:
                                    this.crc = buffer.getIntUnsigned(16);
                                    break;
                            }
                    }
                } else {
                    this.replySerialNumber = Long.valueOf(buffer.getLongUnsigned(32));
                }
            }
        }
    }

    public String toString() {
        return "LogBaseEpcInfo{epc='" + this.epc + "', bEpc=" + Arrays.toString(this.bEpc) + ", pc=" + this.pc + ", antId=" + this.antId + ", rssi=" + this.rssi + ", result=" + this.result + ", tid='" + this.tid + "', bTid=" + Arrays.toString(this.bTid) + ", userdata='" + this.userdata + "', bUser=" + Arrays.toString(this.bUser) + ", reserved='" + this.reserved + "', bRes=" + Arrays.toString(this.bRes) + ", childAntId=" + this.childAntId + ", strUtc='" + this.strUtc + "', frequencyPoint=" + this.frequencyPoint + ", phase=" + this.phase + ", epcData='" + this.epcData + "', bEpcData=" + Arrays.toString(this.bEpcData) + ", ctesiusLtu27=" + this.ctesiusLtu27 + ", ctesiusLtu31=" + this.ctesiusLtu31 + ", readerSerialNumber='" + this.readerSerialNumber + "', replySerialNumber=" + this.replySerialNumber + '}';
    }

    public String toHexString() {
        return "HexString{" + HexUtils.bytes2HexString(this.cData) + '}';
    }
}
