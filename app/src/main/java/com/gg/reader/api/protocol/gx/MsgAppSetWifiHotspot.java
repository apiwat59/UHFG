package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.StringUtils;
import com.pda.uhf_g.util.ExcelUtil;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgAppSetWifiHotspot extends Message {
    private int certificationType;
    private String connectPassword;
    private int encryptionAlgorithm;
    private String hotspotName;

    public MsgAppSetWifiHotspot() {
        this.certificationType = Integer.MAX_VALUE;
        this.encryptionAlgorithm = Integer.MAX_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 51;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppSetWifiHotspot(byte[] data) {
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
            int hnLen = buffer.getIntUnsigned(16);
            if (hnLen > 0) {
                this.hotspotName = new String(buffer.get(new byte[hnLen]), ExcelUtil.UTF8_ENCODING);
            }
            while (buffer.position() / 8 < data.length) {
                byte pid = buffer.getByte();
                if (pid == 1) {
                    int pasLen = buffer.getIntUnsigned(16);
                    if (pasLen > 0) {
                        this.connectPassword = new String(buffer.get(new byte[pasLen]), "ASCII");
                    }
                } else if (pid == 2) {
                    this.certificationType = buffer.getIntUnsigned(8);
                } else if (pid == 3) {
                    this.encryptionAlgorithm = buffer.getIntUnsigned(8);
                }
            }
        } catch (Exception e) {
        }
    }

    public String getHotspotName() {
        return this.hotspotName;
    }

    public void setHotspotName(String hotspotName) {
        this.hotspotName = hotspotName;
    }

    public String getConnectPassword() {
        return this.connectPassword;
    }

    public void setConnectPassword(String connectPassword) {
        this.connectPassword = connectPassword;
    }

    public int getCertificationType() {
        return this.certificationType;
    }

    public void setCertificationType(int certificationType) {
        this.certificationType = certificationType;
    }

    public int getEncryptionAlgorithm() {
        return this.encryptionAlgorithm;
    }

    public void setEncryptionAlgorithm(int encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        if (!StringUtils.isNullOfEmpty(this.hotspotName)) {
            buffer.putInt(this.hotspotName.length(), 16);
            buffer.put(this.hotspotName);
        }
        if (!StringUtils.isNullOfEmpty(this.connectPassword)) {
            buffer.putInt(1, 8);
            buffer.putInt(this.connectPassword.length(), 16);
            buffer.put(this.connectPassword);
        }
        if (Integer.MAX_VALUE != this.certificationType) {
            buffer.putInt(2, 8);
            buffer.putInt(this.certificationType, 8);
        }
        if (Integer.MAX_VALUE != this.encryptionAlgorithm) {
            buffer.putInt(3, 8);
            buffer.putInt(this.encryptionAlgorithm, 8);
        }
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgAppSetWifiHotspot.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Set Fail.");
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
