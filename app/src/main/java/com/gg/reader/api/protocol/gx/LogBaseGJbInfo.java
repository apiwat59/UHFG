package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.HexUtils;
import java.util.Arrays;

/* loaded from: classes.dex */
public class LogBaseGJbInfo extends Message {
    private int antId;
    private byte[] bEpc;
    private byte[] bTid;
    private byte[] bUser;
    private String epc;
    private int pc;
    private String readerSerialNumber;
    private int result;
    private int rssi;
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

    public String getReaderSerialNumber() {
        return this.readerSerialNumber;
    }

    public void setReaderSerialNumber(String readerSerialNumber) {
        this.readerSerialNumber = readerSerialNumber;
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
                if (index == 1) {
                    this.rssi = buffer.getIntUnsigned(8);
                } else if (index == 2) {
                    this.result = buffer.getIntUnsigned(8);
                } else if (index == 3) {
                    int tidLen = buffer.getIntUnsigned(16);
                    byte[] bArr3 = new byte[tidLen];
                    this.bTid = bArr3;
                    if (tidLen > 0) {
                        byte[] bArr4 = buffer.get(bArr3);
                        this.bTid = bArr4;
                        this.tid = HexUtils.bytes2HexString(bArr4);
                    }
                } else if (index == 4) {
                    int userLen = buffer.getIntUnsigned(16);
                    byte[] bArr5 = new byte[userLen];
                    this.bUser = bArr5;
                    if (userLen > 0) {
                        byte[] bArr6 = buffer.get(bArr5);
                        this.bUser = bArr6;
                        this.userdata = HexUtils.bytes2HexString(bArr6);
                    }
                }
            }
        }
    }

    public String toString() {
        return "LogBaseGJbInfo{epc='" + this.epc + "', bEpc=" + Arrays.toString(this.bEpc) + ", tid='" + this.tid + "', bTid=" + Arrays.toString(this.bTid) + ", pc=" + this.pc + ", antId=" + this.antId + ", rssi=" + this.rssi + ", result=" + this.result + ", userdata='" + this.userdata + "', bUser=" + Arrays.toString(this.bUser) + ", readerSerialNumber='" + this.readerSerialNumber + "'}";
    }

    public String toHexString() {
        return "HexString{" + HexUtils.bytes2HexString(this.cData) + '}';
    }
}
