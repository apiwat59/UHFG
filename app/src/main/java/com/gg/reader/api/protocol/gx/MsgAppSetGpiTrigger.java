package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.HexUtils;
import com.gg.reader.api.utils.StringUtils;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgAppSetGpiTrigger extends Message {
    private int gpiPort;
    private String hexTriggerCommand;
    private int levelUploadSwitch;
    private int overDelayTime;
    private byte[] triggerCommand;
    private int triggerOver;
    private int triggerStart;

    public MsgAppSetGpiTrigger() {
        this.overDelayTime = Integer.MAX_VALUE;
        this.levelUploadSwitch = Integer.MAX_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 11;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppSetGpiTrigger(byte[] data) {
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
            this.gpiPort = buffer.getIntUnsigned(8);
            this.triggerStart = buffer.getIntUnsigned(8);
            int cmdLen = buffer.getIntUnsigned(16);
            if (cmdLen > 0) {
                byte[] triggerCommandData = new byte[cmdLen];
                byte[] bArr = buffer.get(triggerCommandData);
                this.triggerCommand = bArr;
                this.hexTriggerCommand = HexUtils.bytes2HexString(bArr);
            }
            this.triggerOver = buffer.getIntUnsigned(8);
            while (buffer.position() / 8 < data.length) {
                byte pid = buffer.getByte();
                if (pid == 1) {
                    this.overDelayTime = buffer.getIntUnsigned(16);
                } else if (pid == 2) {
                    this.levelUploadSwitch = buffer.getIntUnsigned(8);
                }
            }
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

    public String getHexTriggerCommand() {
        return this.hexTriggerCommand;
    }

    public void setHexTriggerCommand(String hexTriggerCommand) {
        if (!StringUtils.isNullOfEmpty(hexTriggerCommand)) {
            this.hexTriggerCommand = hexTriggerCommand;
            this.triggerCommand = HexUtils.hexString2Bytes(hexTriggerCommand);
        }
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.putLong(this.gpiPort, 8);
        buffer.putLong(this.triggerStart, 8);
        byte[] bArr = this.triggerCommand;
        if (bArr != null && bArr.length >= 0) {
            buffer.putInt(bArr.length, 16);
            buffer.put(this.triggerCommand);
        } else {
            buffer.putInt(0, 16);
        }
        buffer.putLong(this.triggerOver, 8);
        if (Integer.MAX_VALUE != this.overDelayTime) {
            buffer.putInt(1, 8);
            buffer.put(this.overDelayTime, 16);
        }
        if (Integer.MAX_VALUE != this.levelUploadSwitch) {
            buffer.putInt(2, 8);
            buffer.putLong(this.levelUploadSwitch, 8);
        }
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgAppSetGpiTrigger.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Port parameter reader hardware is not supported .");
                put((byte) 2, "Parameters are missing .");
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
