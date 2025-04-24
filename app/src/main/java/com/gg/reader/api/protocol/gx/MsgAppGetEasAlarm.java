package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.HexUtils;
import com.gg.reader.api.utils.StringUtils;
import java.util.Arrays;

/* loaded from: classes.dex */
public class MsgAppGetEasAlarm extends Message {
    private int alarmSwitch;
    private byte[] byteContent;
    private byte[] byteMask;
    private ActionParamFail fail;
    private int filterData;
    private String hexContent;
    private String hexMask;
    private int start;
    private ActionParamSuccess success;

    public MsgAppGetEasAlarm() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 64;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppGetEasAlarm(byte[] data) {
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
            this.alarmSwitch = buffer.getIntUnsigned(8);
            this.filterData = buffer.getIntUnsigned(8);
            this.start = buffer.getIntUnsigned(16);
            int dataLen = buffer.getIntUnsigned(16);
            if (dataLen > 0) {
                byte[] bArr = buffer.get(new byte[dataLen]);
                this.byteContent = bArr;
                this.hexContent = HexUtils.bytes2HexString(bArr);
            }
            int maskLen = buffer.getIntUnsigned(16);
            if (maskLen > 0) {
                byte[] bArr2 = buffer.get(new byte[maskLen]);
                this.byteMask = bArr2;
                this.hexMask = HexUtils.bytes2HexString(bArr2);
            }
            while (buffer.position() / 8 < data.length) {
                int pid = buffer.getIntUnsigned(8);
                if (pid == 1) {
                    int sucLen = buffer.getIntUnsigned(16);
                    byte[] paramData = new byte[sucLen];
                    buffer.get(paramData);
                    this.success = new ActionParamSuccess(paramData);
                } else if (pid == 2) {
                    int failLen = buffer.getIntUnsigned(16);
                    byte[] paramData2 = new byte[failLen];
                    buffer.get(paramData2);
                    this.fail = new ActionParamFail(paramData2);
                }
            }
        } catch (Exception e) {
        }
    }

    public int getAlarmSwitch() {
        return this.alarmSwitch;
    }

    public void setAlarmSwitch(int alarmSwitch) {
        this.alarmSwitch = alarmSwitch;
    }

    public int getFilterData() {
        return this.filterData;
    }

    public void setFilterData(int filterData) {
        this.filterData = filterData;
    }

    public int getStart() {
        return this.start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public String getHexContent() {
        return this.hexContent;
    }

    public void setHexContent(String hexContent) {
        this.hexContent = hexContent;
        if (!StringUtils.isNullOfEmpty(hexContent)) {
            this.byteContent = HexUtils.hexString2Bytes(this.hexContent);
        }
    }

    public byte[] getByteContent() {
        return this.byteContent;
    }

    public void setByteContent(byte[] byteContent) {
        this.byteContent = byteContent;
    }

    public String getHexMask() {
        return this.hexMask;
    }

    public void setHexMask(String hexMask) {
        this.hexMask = hexMask;
        if (!StringUtils.isNullOfEmpty(hexMask)) {
            this.byteMask = HexUtils.hexString2Bytes(this.hexMask);
        }
    }

    public byte[] getByteMask() {
        return this.byteMask;
    }

    public void setByteMask(byte[] byteMask) {
        this.byteMask = byteMask;
    }

    public ActionParamSuccess getSuccess() {
        return this.success;
    }

    public void setSuccess(ActionParamSuccess success) {
        this.success = success;
    }

    public ActionParamFail getFail() {
        return this.fail;
    }

    public void setFail(ActionParamFail fail) {
        this.fail = fail;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            buffer.putInt(this.alarmSwitch, 8);
            buffer.putInt(this.filterData, 8);
            buffer.putInt(this.start, 16);
            String str = this.hexContent;
            if (str != null) {
                buffer.putInt(str.length(), 16);
                buffer.put(this.hexContent);
            }
            String str2 = this.hexMask;
            if (str2 != null) {
                buffer.putInt(str2.length(), 16);
                buffer.put(this.hexMask);
            }
            if (this.success != null) {
                buffer.putInt(1, 8);
                byte[] tmpByte = this.success.toBytes();
                buffer.putInt(tmpByte.length, 16);
                buffer.put(tmpByte);
            }
            if (this.fail != null) {
                buffer.putInt(2, 8);
                byte[] tmpByte2 = this.fail.toBytes();
                buffer.putInt(tmpByte2.length, 16);
                buffer.put(tmpByte2);
            }
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
            this.alarmSwitch = buffer.getIntUnsigned(8);
            this.filterData = buffer.getIntUnsigned(8);
            this.start = buffer.getIntUnsigned(16);
            int dataLen = buffer.getIntUnsigned(16);
            if (dataLen > 0) {
                byte[] bArr = buffer.get(new byte[dataLen]);
                this.byteContent = bArr;
                this.hexContent = HexUtils.bytes2HexString(bArr);
            }
            int maskLen = buffer.getIntUnsigned(16);
            if (maskLen > 0) {
                byte[] bArr2 = buffer.get(new byte[maskLen]);
                this.byteMask = bArr2;
                this.hexMask = HexUtils.bytes2HexString(bArr2);
            }
            while (buffer.position() / 8 < this.cData.length) {
                int pid = buffer.getIntUnsigned(8);
                if (pid == 1) {
                    int sucLen = buffer.getIntUnsigned(16);
                    byte[] paramData = new byte[sucLen];
                    buffer.get(paramData);
                    this.success = new ActionParamSuccess(paramData);
                } else if (pid == 2) {
                    int failLen = buffer.getIntUnsigned(16);
                    byte[] paramData2 = new byte[failLen];
                    buffer.get(paramData2);
                    this.fail = new ActionParamFail(paramData2);
                }
            }
            setRtCode((byte) 0);
        }
    }

    public String toString() {
        return "MsgAppGetEasAlarm{alarmSwitch=" + this.alarmSwitch + ", filterData=" + this.filterData + ", start=" + this.start + ", hexContent='" + this.hexContent + "', byteContent=" + Arrays.toString(this.byteContent) + ", hexMask='" + this.hexMask + "', byteMask=" + Arrays.toString(this.byteMask) + ", success=" + this.success + ", fail=" + this.fail + '}';
    }
}
