package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.HexUtils;
import com.gg.reader.api.utils.StringUtils;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgBaseSafeCertification extends Message {
    private String key;
    private ParamEncipheredData paramEncipheredData;
    private int token1;
    private int token2result;

    public MsgBaseSafeCertification() {
        this.token1 = Integer.MAX_VALUE;
        this.token2result = Integer.MAX_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_BASE;
            this.msgType.msgId = (byte) -16;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgBaseSafeCertification(byte[] data) {
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
            while (buffer.position() / 8 < data.length) {
                byte pid = buffer.getByte();
                if (pid == 1) {
                    int len = buffer.getIntUnsigned(64);
                    byte[] paramData = new byte[len];
                    if (len > 0) {
                        byte[] bytes = buffer.get(paramData);
                        this.token1 = HexUtils.bytes2Int(bytes);
                    }
                } else if (pid == 2) {
                    this.token2result = buffer.getIntUnsigned(8);
                } else if (pid == 3) {
                    byte[] paramData2 = new byte[2];
                    buffer.get(paramData2);
                    this.paramEncipheredData = new ParamEncipheredData(paramData2);
                } else if (pid == 4) {
                    int length = buffer.getIntUnsigned(8);
                    this.key = buffer.getString(length * 8);
                }
            }
        } catch (Exception e) {
        }
    }

    public int getToken1() {
        return this.token1;
    }

    public void setToken1(int token1) {
        this.token1 = token1;
    }

    public int getToken2result() {
        return this.token2result;
    }

    public void setToken2result(int token2result) {
        this.token2result = token2result;
    }

    public ParamEncipheredData getParamEncipheredData() {
        return this.paramEncipheredData;
    }

    public void setParamEncipheredData(ParamEncipheredData paramEncipheredData) {
        this.paramEncipheredData = paramEncipheredData;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            if (Integer.MAX_VALUE != this.token1) {
                buffer.putInt(1, 8);
                buffer.putInt(this.token1, 64);
            }
            if (Integer.MAX_VALUE != this.token2result) {
                buffer.putInt(2, 8);
                buffer.put(this.token2result, 8);
            }
            if (this.paramEncipheredData != null) {
                buffer.putInt(3, 8);
                byte[] tmpByte = this.paramEncipheredData.toBytes();
                buffer.put(tmpByte);
            }
            if (!StringUtils.isNullOfEmpty(this.key)) {
                buffer.putInt(4, 8);
                buffer.put(this.key.length(), 16);
                buffer.put(this.key);
            }
            this.cData = buffer.asByteArray();
            this.dataLen = this.cData.length;
        } catch (Exception e) {
        }
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgBaseSafeCertification.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Failure.");
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
