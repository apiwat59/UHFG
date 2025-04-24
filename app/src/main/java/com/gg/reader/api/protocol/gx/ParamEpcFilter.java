package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.HexUtils;
import com.gg.reader.api.utils.StringUtils;

/* loaded from: classes.dex */
public class ParamEpcFilter extends Parameter {
    private int area;
    private byte[] bData;
    private int bitLength;
    private int bitStart;
    private String hexData;

    public ParamEpcFilter() {
    }

    public ParamEpcFilter(int area, int bitStart, int bitLength, String hexData) {
        this.area = area;
        this.bitStart = bitStart;
        this.bitLength = bitLength;
        this.hexData = hexData;
        if (!StringUtils.isNullOfEmpty(hexData)) {
            this.bData = HexUtils.hexString2Bytes(this.hexData);
        }
    }

    public int getArea() {
        return this.area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public int getBitStart() {
        return this.bitStart;
    }

    public void setBitStart(int bitStart) {
        this.bitStart = bitStart;
    }

    public int getBitLength() {
        return this.bitLength;
    }

    public void setBitLength(int bitLength) {
        this.bitLength = bitLength;
    }

    public String getHexData() {
        return this.hexData;
    }

    public void setHexData(String hexData) {
        if (!StringUtils.isNullOfEmpty(hexData)) {
            this.hexData = hexData;
            this.bData = HexUtils.hexString2Bytes(hexData);
        }
    }

    public byte[] getbData() {
        return this.bData;
    }

    public void setbData(byte[] bData) {
        this.bData = bData;
    }

    public ParamEpcFilter(byte[] data) {
        if (data == null) {
            return;
        }
        try {
            BitBuffer buffer = BitBuffer.wrap(data);
            buffer.position(0);
            this.area = buffer.getIntUnsigned(8);
            this.bitStart = buffer.getIntUnsigned(16);
            int intUnsigned = buffer.getIntUnsigned(8);
            this.bitLength = intUnsigned;
            int byteLength = intUnsigned / 8;
            byte[] bArr = new byte[byteLength];
            this.bData = bArr;
            if (byteLength > 0) {
                byte[] bArr2 = buffer.get(bArr);
                this.bData = bArr2;
                this.hexData = HexUtils.bytes2HexString(bArr2);
            }
        } catch (Exception e) {
        }
    }

    @Override // com.gg.reader.api.protocol.gx.Parameter
    public byte[] toBytes() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.putLong(this.area, 8);
        buffer.putLong(this.bitStart, 16);
        buffer.putLong(this.bitLength, 8);
        buffer.put(this.bData);
        return buffer.asByteArray();
    }
}
