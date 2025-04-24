package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.HexUtils;
import com.gg.reader.api.utils.StringUtils;
import java.util.Arrays;

/* loaded from: classes.dex */
public class MsgAppGetGpiTrigger extends Message {
    private int gpiPort;
    private String hexTriggerCommand;
    private int levelUploadSwitch;
    private int overDelayTime;
    private byte[] triggerCommand;
    private int triggerOver;
    private int triggerStart;

    public MsgAppGetGpiTrigger() {
        this.overDelayTime = Integer.MAX_VALUE;
        this.levelUploadSwitch = Integer.MAX_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 12;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppGetGpiTrigger(byte[] data) {
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
            this.triggerStart = buffer.getIntUnsigned(8);
            int cmdLen = buffer.getIntUnsigned(16);
            if (cmdLen > 0) {
                byte[] triggerCommandData = new byte[cmdLen];
                byte[] bArr = buffer.get(triggerCommandData);
                this.triggerCommand = bArr;
                this.hexTriggerCommand = HexUtils.bytes2HexString(bArr);
            }
            this.triggerOver = buffer.getIntUnsigned(8);
            this.overDelayTime = buffer.getIntUnsigned(16);
            this.levelUploadSwitch = buffer.getIntUnsigned(8);
        } catch (Exception e) {
        }
    }

    public int getGpiPort() {
        return this.gpiPort;
    }

    public void setGpiPort(int gpiPort) {
        this.gpiPort = gpiPort;
    }

    public int getTriggerStart() {
        return this.triggerStart;
    }

    public void setTriggerStart(int triggerStart) {
        this.triggerStart = triggerStart;
    }

    public byte[] getTriggerCommand() {
        return this.triggerCommand;
    }

    public void setTriggerCommand(byte[] triggerCommand) {
        this.triggerCommand = triggerCommand;
    }

    public String getHexTriggerCommand() {
        return this.hexTriggerCommand;
    }

    public void setHexTriggerCommand(String hexTriggerCommand) {
        if (!StringUtils.isNullOfEmpty(hexTriggerCommand)) {
            this.hexTriggerCommand = hexTriggerCommand;
            this.triggerCommand = HexUtils.hexString2Bytes(hexTriggerCommand);
        }
    }

    public int getTriggerOver() {
        return this.triggerOver;
    }

    public void setTriggerOver(int triggerOver) {
        this.triggerOver = triggerOver;
    }

    public int getOverDelayTime() {
        return this.overDelayTime;
    }

    public void setOverDelayTime(int overDelayTime) {
        this.overDelayTime = overDelayTime;
    }

    public int getLevelUploadSwitch() {
        return this.levelUploadSwitch;
    }

    public void setLevelUploadSwitch(int levelUploadSwitch) {
        this.levelUploadSwitch = levelUploadSwitch;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            buffer.putLong(this.gpiPort, 8);
            this.cData = buffer.asByteArray();
            this.dataLen = this.cData.length;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            buffer.putLong(this.triggerStart, 8);
            byte[] bArr = this.triggerCommand;
            if (bArr != null && bArr.length >= 0) {
                buffer.putInt(bArr.length, 16);
                buffer.put(this.triggerCommand);
            } else {
                buffer.putInt(0, 16);
            }
            buffer.putLong(this.triggerOver, 8);
            int i = this.overDelayTime;
            if (Integer.MAX_VALUE != i) {
                buffer.put(i, 16);
            }
            int i2 = this.levelUploadSwitch;
            if (Integer.MAX_VALUE != i2) {
                buffer.putLong(i2, 8);
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
            this.triggerStart = buffer.getIntUnsigned(8);
            int cmdLen = buffer.getIntUnsigned(16);
            if (cmdLen > 0) {
                byte[] triggerCommandData = new byte[cmdLen];
                byte[] bArr = buffer.get(triggerCommandData);
                this.triggerCommand = bArr;
                this.hexTriggerCommand = HexUtils.bytes2HexString(bArr);
            }
            this.triggerOver = buffer.getIntUnsigned(8);
            this.overDelayTime = buffer.getIntUnsigned(16);
            this.levelUploadSwitch = buffer.getIntUnsigned(8);
            setRtCode((byte) 0);
        }
    }

    public String toString() {
        return "MsgAppGetGpiTrigger{gpiPort=" + this.gpiPort + ", triggerStart=" + this.triggerStart + ", triggerCommand=" + Arrays.toString(this.triggerCommand) + ", hexTriggerCommand='" + this.hexTriggerCommand + "', triggerOver=" + this.triggerOver + ", overDelayTime=" + this.overDelayTime + ", levelUploadSwitch=" + this.levelUploadSwitch + '}';
    }
}
