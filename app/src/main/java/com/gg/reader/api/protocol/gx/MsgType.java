package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.StringUtils;
import kotlin.UByte;

/* loaded from: classes.dex */
public class MsgType {
    public byte msgId;
    public String mt_12;
    public String mt_13;
    public String mt_14;
    public String mt_15;
    public String mt_8_11;
    public byte pType;
    public byte pVersion;

    public MsgType() {
        this.msgId = (byte) -1;
        this.mt_8_11 = EnumG.MSG_TYPE_BIT_ERROR;
        this.mt_12 = "0";
        this.mt_13 = "0";
        this.mt_14 = "0";
        this.mt_15 = "0";
        this.pVersion = (byte) 1;
        this.pType = (byte) 0;
    }

    public MsgType(byte mid, String mt_8_11, String mt_12, String mt_13) {
        this.msgId = (byte) -1;
        this.mt_8_11 = EnumG.MSG_TYPE_BIT_ERROR;
        this.mt_12 = "0";
        this.mt_13 = "0";
        this.mt_14 = "0";
        this.mt_15 = "0";
        this.pVersion = (byte) 1;
        this.pType = (byte) 0;
        this.msgId = mid;
        this.mt_8_11 = mt_8_11;
        this.mt_12 = mt_12;
        this.mt_13 = mt_13;
    }

    public MsgType(byte[] data) {
        this.msgId = (byte) -1;
        this.mt_8_11 = EnumG.MSG_TYPE_BIT_ERROR;
        this.mt_12 = "0";
        this.mt_13 = "0";
        this.mt_14 = "0";
        this.mt_15 = "0";
        this.pVersion = (byte) 1;
        this.pType = (byte) 0;
        try {
            this.msgId = data[3];
            String sMsgType = StringUtils.padRight(byte2bits(data[2]), '0', 8);
            this.mt_8_11 = sMsgType.substring(4, 8);
            this.mt_12 = sMsgType.substring(3, 4);
            this.mt_13 = sMsgType.substring(2, 3);
            this.mt_14 = sMsgType.substring(1, 2);
            this.mt_15 = sMsgType.substring(0, 1);
            this.pVersion = data[1];
            this.pType = data[0];
        } catch (Exception e) {
        }
    }

    public byte[] toBytes() {
        byte[] rt = new byte[4];
        try {
            String sMsgType = this.mt_15 + this.mt_14 + this.mt_13 + this.mt_12 + this.mt_8_11;
            byte bMsgType = bit2byte(sMsgType);
            rt[3] = this.msgId;
            rt[2] = bMsgType;
            rt[1] = this.pVersion;
            rt[0] = this.pType;
        } catch (Exception e) {
        }
        return rt;
    }

    public int toInt() {
        byte[] bData = toBytes();
        BitBuffer buffer = BitBuffer.wrap(bData);
        buffer.position(0);
        int rt = buffer.getIntUnsigned(32);
        return rt;
    }

    private String byte2bits(byte b) {
        int z = b | UByte.MIN_VALUE;
        String str = Integer.toBinaryString(z);
        int len = str.length();
        return str.substring(len - 8, len);
    }

    public static byte bit2byte(String bString) {
        byte result = 0;
        int i = bString.length() - 1;
        int j = 0;
        while (i >= 0) {
            double parseByte = Byte.parseByte(bString.charAt(i) + "");
            double pow = Math.pow(2.0d, j);
            Double.isNaN(parseByte);
            Double.isNaN(result);
            result = (byte) (r3 + (parseByte * pow));
            i--;
            j++;
        }
        return result;
    }
}
