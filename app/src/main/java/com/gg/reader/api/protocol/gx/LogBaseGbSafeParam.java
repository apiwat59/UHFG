package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;

/* loaded from: classes.dex */
public class LogBaseGbSafeParam extends Message {
    private int antId;
    private ParamEncipheredData encipheredData;
    private int random;
    private String readerSerialNumber;
    private int safeParam;
    private int tagIdentifier;
    private int token2;

    public int getAntId() {
        return this.antId;
    }

    public void setAntId(int antId) {
        this.antId = antId;
    }

    public int getSafeParam() {
        return this.safeParam;
    }

    public void setSafeParam(int safeParam) {
        this.safeParam = safeParam;
    }

    public int getTagIdentifier() {
        return this.tagIdentifier;
    }

    public void setTagIdentifier(int tagIdentifier) {
        this.tagIdentifier = tagIdentifier;
    }

    public int getRandom() {
        return this.random;
    }

    public void setRandom(int random) {
        this.random = random;
    }

    public int getToken2() {
        return this.token2;
    }

    public void setToken2(int token2) {
        this.token2 = token2;
    }

    public ParamEncipheredData getEncipheredData() {
        return this.encipheredData;
    }

    public void setEncipheredData(ParamEncipheredData encipheredData) {
        this.encipheredData = encipheredData;
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
            this.antId = buffer.getIntUnsigned(8);
            while (buffer.position() / 8 < this.cData.length) {
                int index = buffer.getIntUnsigned(8);
                if (index == 1) {
                    this.safeParam = buffer.getIntUnsigned(48);
                } else if (index == 2) {
                    this.tagIdentifier = buffer.getIntUnsigned(64);
                } else if (index == 3) {
                    this.random = buffer.getIntUnsigned(32);
                } else if (index == 4) {
                    this.token2 = buffer.getIntUnsigned(64);
                } else if (index == 5) {
                    int length = buffer.getIntUnsigned(16);
                    byte[] bytes = new byte[length];
                    if (length > 0) {
                        buffer.get(bytes);
                        this.encipheredData = new ParamEncipheredData(bytes);
                    }
                }
            }
        }
    }

    public String toString() {
        return "LogBaseGbSafeParam{antId=" + this.antId + ", safeParam=" + this.safeParam + ", tagIdentifier=" + this.tagIdentifier + ", random=" + this.random + ", token2=" + this.token2 + ", encipheredData=" + this.encipheredData + ", readerSerialNumber='" + this.readerSerialNumber + "'}";
    }
}
