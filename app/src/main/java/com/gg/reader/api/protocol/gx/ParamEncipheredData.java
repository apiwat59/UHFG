package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.HexUtils;
import com.gg.reader.api.utils.StringUtils;

/* loaded from: classes.dex */
public class ParamEncipheredData extends Parameter {
    private byte[] bData;
    private int bitLength;
    private String hexData;

    public ParamEncipheredData() {
    }

    public ParamEncipheredData(int bitLength, String hexData) {
        this.bitLength = bitLength;
        this.hexData = hexData;
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

    public ParamEncipheredData(byte[] data) {
        if (data == null) {
            return;
        }
        try {
            BitBuffer buffer = BitBuffer.wrap(data);
            buffer.position(0);
            int intUnsigned = buffer.getIntUnsigned(16);
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
        buffer.putLong(this.bitLength, 16);
        buffer.put(this.bData);
        return buffer.asByteArray();
    }
}
