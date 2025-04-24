package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.StringUtils;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgTestSerialNoSet extends Message {
    private String readerSerialNumber;
    private String tuYaAuthKey;
    private String tuYaPid;
    private String tuYaShortUrl;
    private String tuYaUuid;

    public MsgTestSerialNoSet() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_TEST;
            this.msgType.msgId = (byte) 16;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgTestSerialNoSet(byte[] data) {
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
            int snLen = buffer.getIntUnsigned(16);
            if (snLen > 0) {
                this.readerSerialNumber = new String(buffer.get(new byte[snLen]), "ASCII");
            }
        } catch (Exception e) {
        }
    }

    public String getReaderSerialNumber() {
        return this.readerSerialNumber;
    }

    public void setReaderSerialNumber(String readerSerialNumber) {
        this.readerSerialNumber = readerSerialNumber;
    }

    public String getTuYaPid() {
        return this.tuYaPid;
    }

    public void setTuYaPid(String tuYaPid) {
        this.tuYaPid = tuYaPid;
    }

    public String getTuYaUuid() {
        return this.tuYaUuid;
    }

    public void setTuYaUuid(String tuYaUuid) {
        this.tuYaUuid = tuYaUuid;
    }

    public String getTuYaAuthKey() {
        return this.tuYaAuthKey;
    }

    public void setTuYaAuthKey(String tuYaAuthKey) {
        this.tuYaAuthKey = tuYaAuthKey;
    }

    public String getTuYaShortUrl() {
        return this.tuYaShortUrl;
    }

    public void setTuYaShortUrl(String tuYaShortUrl) {
        this.tuYaShortUrl = tuYaShortUrl;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        if (!StringUtils.isNullOfEmpty(this.readerSerialNumber)) {
            buffer.putInt(this.readerSerialNumber.length(), 16);
            buffer.put(this.readerSerialNumber);
            if (!StringUtils.isNullOfEmpty(this.tuYaPid)) {
                buffer.putInt(1, 8);
                buffer.putInt(this.tuYaPid.length(), 16);
                buffer.put(this.tuYaPid);
            }
            if (!StringUtils.isNullOfEmpty(this.tuYaUuid)) {
                buffer.putInt(2, 8);
                buffer.putInt(this.tuYaUuid.length(), 16);
                buffer.put(this.tuYaUuid);
            }
            if (!StringUtils.isNullOfEmpty(this.tuYaAuthKey)) {
                buffer.putInt(3, 8);
                buffer.putInt(this.tuYaAuthKey.length(), 16);
                buffer.put(this.tuYaAuthKey);
            }
            if (!StringUtils.isNullOfEmpty(this.tuYaShortUrl)) {
                buffer.putInt(4, 8);
                buffer.putInt(this.tuYaShortUrl.length(), 16);
                buffer.put(this.tuYaShortUrl);
            }
            this.cData = buffer.asByteArray();
            this.dataLen = this.cData.length;
        }
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgTestSerialNoSet.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Other error.");
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
