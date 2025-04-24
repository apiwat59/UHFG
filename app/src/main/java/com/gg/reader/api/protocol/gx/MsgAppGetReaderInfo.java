package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.StringUtils;
import org.apache.log4j.helpers.FileWatchdog;

/* loaded from: classes.dex */
public class MsgAppGetReaderInfo extends Message {
    private String appCompileTime;
    private String appVersions;
    private String baseCompileTime;
    private long powerOnTime;
    private String readerSerialNumber;
    private String systemVersions;
    private String tuYaAuthKey;
    private String tuYaPid;
    private String tuYaShortUrl;
    private String tuYaUuid;

    public MsgAppGetReaderInfo() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 0;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppGetReaderInfo(byte[] data) {
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
            this.powerOnTime = buffer.getLongUnsigned(32);
            int btLen = buffer.getIntUnsigned(16);
            if (btLen > 0) {
                this.baseCompileTime = new String(buffer.get(new byte[btLen]), "ASCII");
            }
            while (buffer.position() / 8 < data.length) {
                byte pid = buffer.getByte();
                switch (pid) {
                    case 1:
                        this.appVersions = buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8);
                        break;
                    case 2:
                        int svLen = buffer.getIntUnsigned(16);
                        if (svLen <= 0) {
                            break;
                        } else {
                            this.systemVersions = new String(buffer.get(new byte[svLen]), "ASCII");
                            break;
                        }
                    case 3:
                        int atLen = buffer.getIntUnsigned(16);
                        if (atLen <= 0) {
                            break;
                        } else {
                            this.appCompileTime = new String(buffer.get(new byte[atLen]), "ASCII");
                            break;
                        }
                    case 4:
                        int pidLen = buffer.getIntUnsigned(16);
                        if (pidLen <= 0) {
                            break;
                        } else {
                            this.tuYaPid = new String(buffer.get(new byte[pidLen]), "ASCII");
                            break;
                        }
                    case 5:
                        int uuidLen = buffer.getIntUnsigned(16);
                        if (uuidLen <= 0) {
                            break;
                        } else {
                            this.tuYaUuid = new String(buffer.get(new byte[uuidLen]), "ASCII");
                            break;
                        }
                    case 6:
                        int authKeyLen = buffer.getIntUnsigned(16);
                        if (authKeyLen <= 0) {
                            break;
                        } else {
                            this.tuYaAuthKey = new String(buffer.get(new byte[authKeyLen]), "ASCII");
                            break;
                        }
                    case 7:
                        int shortUrlLen = buffer.getIntUnsigned(16);
                        if (shortUrlLen <= 0) {
                            break;
                        } else {
                            this.tuYaShortUrl = new String(buffer.get(new byte[shortUrlLen]), "ASCII");
                            break;
                        }
                }
            }
        } catch (Exception e) {
        }
    }

    public String getAppVersions() {
        return this.appVersions;
    }

    public void setAppVersions(String appVersions) {
        this.appVersions = appVersions;
    }

    public String getSystemVersions() {
        return this.systemVersions;
    }

    public void setSystemVersions(String systemVersions) {
        this.systemVersions = systemVersions;
    }

    public String getReaderSerialNumber() {
        return this.readerSerialNumber;
    }

    public void setReaderSerialNumber(String readerSerialNumber) {
        this.readerSerialNumber = readerSerialNumber;
    }

    public long getPowerOnTime() {
        return this.powerOnTime;
    }

    public void setPowerOnTime(long powerOnTime) {
        this.powerOnTime = powerOnTime;
    }

    public String getBaseCompileTime() {
        return this.baseCompileTime;
    }

    public void setBaseCompileTime(String baseCompileTime) {
        this.baseCompileTime = baseCompileTime;
    }

    public String getAppCompileTime() {
        return this.appCompileTime;
    }

    public void setAppCompileTime(String appCompileTime) {
        this.appCompileTime = appCompileTime;
    }

    public String getFormatPowerOnTime() {
        long microseconds = getPowerOnTime() * 1000;
        long days = microseconds / 86400000;
        long hours = (microseconds % 86400000) / 3600000;
        long minutes = (microseconds % 3600000) / FileWatchdog.DEFAULT_DELAY;
        long seconds = (microseconds % FileWatchdog.DEFAULT_DELAY) / 1000;
        return days + " days " + hours + " hours " + minutes + " minutes " + seconds + " seconds ";
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
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            String str = this.readerSerialNumber;
            if (str != null) {
                buffer.putInt(str.length(), 16);
                buffer.put(this.readerSerialNumber);
            }
            buffer.put(this.powerOnTime, 32);
            String str2 = this.baseCompileTime;
            if (str2 != null) {
                buffer.putInt(str2.length(), 16);
                buffer.put(this.baseCompileTime);
            }
            if (this.appVersions != null) {
                buffer.putInt(1, 8);
                String[] visions = this.appVersions.split("\\.");
                for (String vs : visions) {
                    buffer.putInt(Integer.parseInt(vs), 8);
                }
            }
            if (this.systemVersions != null) {
                buffer.putInt(2, 8);
                buffer.putInt(this.systemVersions.length(), 16);
                buffer.put(this.systemVersions);
            }
            if (this.appCompileTime != null) {
                buffer.putInt(3, 8);
                buffer.putInt(this.appCompileTime.length(), 16);
                buffer.put(this.appCompileTime);
            }
            if (!StringUtils.isNullOfEmpty(this.tuYaPid)) {
                buffer.putInt(4, 8);
                buffer.putInt(this.tuYaPid.length(), 16);
                buffer.put(this.tuYaPid);
            }
            if (!StringUtils.isNullOfEmpty(this.tuYaUuid)) {
                buffer.putInt(5, 8);
                buffer.putInt(this.tuYaUuid.length(), 16);
                buffer.put(this.tuYaUuid);
            }
            if (!StringUtils.isNullOfEmpty(this.tuYaAuthKey)) {
                buffer.putInt(6, 8);
                buffer.putInt(this.tuYaAuthKey.length(), 16);
                buffer.put(this.tuYaAuthKey);
            }
            if (!StringUtils.isNullOfEmpty(this.tuYaShortUrl)) {
                buffer.putInt(7, 8);
                buffer.putInt(this.tuYaShortUrl.length(), 16);
                buffer.put(this.tuYaShortUrl);
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
            try {
                int snLen = buffer.getIntUnsigned(16);
                if (snLen > 0) {
                    this.readerSerialNumber = new String(buffer.get(new byte[snLen]), "ASCII");
                }
                this.powerOnTime = buffer.getLongUnsigned(32);
                int btLen = buffer.getIntUnsigned(16);
                if (btLen > 0) {
                    this.baseCompileTime = new String(buffer.get(new byte[btLen]), "ASCII");
                }
                while (buffer.position() / 8 < this.cData.length) {
                    byte pid = buffer.getByte();
                    switch (pid) {
                        case 1:
                            this.appVersions = buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8);
                            break;
                        case 2:
                            int svLen = buffer.getIntUnsigned(16);
                            if (svLen <= 0) {
                                break;
                            } else {
                                this.systemVersions = new String(buffer.get(new byte[svLen]), "ASCII");
                                break;
                            }
                        case 3:
                            int atLen = buffer.getIntUnsigned(16);
                            if (atLen <= 0) {
                                break;
                            } else {
                                this.appCompileTime = new String(buffer.get(new byte[atLen]), "ASCII");
                                break;
                            }
                        case 4:
                            int pidLen = buffer.getIntUnsigned(16);
                            if (pidLen <= 0) {
                                break;
                            } else {
                                this.tuYaPid = new String(buffer.get(new byte[pidLen]), "ASCII");
                                break;
                            }
                        case 5:
                            int uuidLen = buffer.getIntUnsigned(16);
                            if (uuidLen <= 0) {
                                break;
                            } else {
                                this.tuYaUuid = new String(buffer.get(new byte[uuidLen]), "ASCII");
                                break;
                            }
                        case 6:
                            int authKeyLen = buffer.getIntUnsigned(16);
                            if (authKeyLen <= 0) {
                                break;
                            } else {
                                this.tuYaAuthKey = new String(buffer.get(new byte[authKeyLen]), "ASCII");
                                break;
                            }
                        case 7:
                            int shortUrlLen = buffer.getIntUnsigned(16);
                            if (shortUrlLen <= 0) {
                                break;
                            } else {
                                this.tuYaShortUrl = new String(buffer.get(new byte[shortUrlLen]), "ASCII");
                                break;
                            }
                    }
                }
            } catch (Exception e) {
            }
            setRtCode((byte) 0);
        }
    }

    public String toString() {
        return "MsgAppGetReaderInfo{readerSerialNumber='" + this.readerSerialNumber + "', powerOnTime=" + this.powerOnTime + ", baseCompileTime='" + this.baseCompileTime + "', appVersions='" + this.appVersions + "', systemVersions='" + this.systemVersions + "', appCompileTime='" + this.appCompileTime + "', tuYaPid='" + this.tuYaPid + "', tuYaUuid='" + this.tuYaUuid + "', tuYaAuthKey='" + this.tuYaAuthKey + "', tuYaShortUrl='" + this.tuYaShortUrl + "'}";
    }
}
