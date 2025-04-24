package com.handheld.uhfr;

import android.util.Log;
import cn.pda.serialport.Tools;
import com.handheld.uhfr.Reader;
import com.rfid.trans.MaskClass;
import com.rfid.trans.ReaderHelp;
import com.rfid.trans.ReaderParameter;
import com.uhf.api.cls.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import kotlin.jvm.internal.ByteCompanionObject;

/* loaded from: classes.dex */
public class RrReader {
    public static ReaderHelp rrlib;
    private static int savedSession = -1;
    private static int savedQValue = -1;

    public enum RrRegion_Conf {
        RG_PRC2(1),
        RG_NA(2),
        RG_KR(3),
        RG_EU3(4),
        RG_PRC(8),
        RG_OPEN(0),
        RG_NONE(255);

        private final int value;

        RrRegion_Conf(int i) {
            this.value = i;
        }

        public int value() {
            return this.value;
        }

        public static RrRegion_Conf valueOf(int value) {
            if (value == 1) {
                return RG_NA;
            }
            if (value == 3) {
                return RG_KR;
            }
            if (value == 6) {
                return RG_PRC2;
            }
            if (value == 8) {
                return RG_EU3;
            }
            if (value == 10) {
                return RG_PRC;
            }
            if (value == 255) {
                return RG_OPEN;
            }
            return RG_NONE;
        }

        public static Reader.Region_Conf convertToClRegion(int value) {
            if (value == 0) {
                return Reader.Region_Conf.RG_OPEN;
            }
            if (value == 1) {
                return Reader.Region_Conf.RG_PRC;
            }
            if (value == 2) {
                return Reader.Region_Conf.RG_NA;
            }
            if (value == 3) {
                return Reader.Region_Conf.RG_KR;
            }
            if (value == 4) {
                return Reader.Region_Conf.RG_EU3;
            }
            if (value == 8) {
                return Reader.Region_Conf.RG_PRC2;
            }
            return Reader.Region_Conf.RG_NONE;
        }
    }

    public enum RrLockObj {
        LOCK_OBJECT_KILL_PASSWORD(0),
        LOCK_OBJECT_ACCESS_PASSWD(1),
        LOCK_OBJECT_BANK1(2),
        LOCK_OBJECT_BANK2(3),
        LOCK_OBJECT_BANK3(4),
        LOCK_OBJECT_NONE(255);

        private final int pV;

        RrLockObj(int v) {
            this.pV = v;
        }

        public int value() {
            return this.pV;
        }

        public static RrLockObj valueOf(Reader.Lock_Obj lockObj) {
            int value = lockObj.value();
            if (value == 1) {
                return LOCK_OBJECT_KILL_PASSWORD;
            }
            if (value == 2) {
                return LOCK_OBJECT_ACCESS_PASSWD;
            }
            if (value == 4) {
                return LOCK_OBJECT_BANK1;
            }
            if (value == 8) {
                return LOCK_OBJECT_BANK2;
            }
            if (value == 16) {
                return LOCK_OBJECT_BANK3;
            }
            return LOCK_OBJECT_NONE;
        }
    }

    public enum RrLockType {
        UNLOCK(0),
        PERM_UNLOCK(1),
        LOCK(2),
        PERM_LOCK(3),
        NONE(255);

        private final int pV;

        RrLockType(int v) {
            this.pV = v;
        }

        public int value() {
            return this.pV;
        }

        public static RrLockType valueOf(Reader.Lock_Type lockType) {
            int value = lockType.value();
            if (value == 0) {
                return UNLOCK;
            }
            if (value != 8) {
                if (value != 12) {
                    if (value != 32) {
                        if (value != 48) {
                            if (value != 128) {
                                if (value != 192) {
                                    if (value != 512) {
                                        if (value != 768) {
                                            if (value != 2) {
                                                if (value != 3) {
                                                    return NONE;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return PERM_LOCK;
            }
            return LOCK;
        }
    }

    public static int connect(String comPort, int baudRate, int logswitch) {
        ReaderHelp readerHelp = new ReaderHelp();
        rrlib = readerHelp;
        ReaderParameter param = readerHelp.GetInventoryPatameter();
        param.Session = 0;
        savedSession = 0;
        savedQValue = param.QValue;
        rrlib.SetInventoryPatameter(param);
        int result = rrlib.Connect(comPort, baudRate, logswitch);
        Log.d("huang,UHFRManager", "Rr connect rrlib result = " + result);
        if (result == 0) {
            byte[] readPower = new byte[1];
            int result2 = rrlib.GetReaderInformation(new byte[2], readPower, new byte[1], new byte[1], new byte[1]);
            if (result2 == 0) {
                int writePower = readPower[0] | ByteCompanionObject.MIN_VALUE;
                int result3 = rrlib.SetWritePower((byte) writePower);
                if (result3 == 0) {
                    return setJgDwell(6, 2);
                }
                return result3;
            }
            return result2;
        }
        return result;
    }

    public static int getMaxFrmPoint(int region) {
        if (region == RrRegion_Conf.RG_PRC2.value()) {
            return 19;
        }
        if (region == RrRegion_Conf.RG_NA.value()) {
            return 49;
        }
        if (region == RrRegion_Conf.RG_KR.value()) {
            return 31;
        }
        if (region == RrRegion_Conf.RG_EU3.value()) {
            return 14;
        }
        if (region == RrRegion_Conf.RG_PRC.value()) {
            return 19;
        }
        if (region != RrRegion_Conf.RG_OPEN.value()) {
            return 0;
        }
        return 60;
    }

    public static String getVersion() {
        byte[] version = new byte[2];
        int result = rrlib.GetReaderInformation(version, new byte[1], new byte[1], new byte[1], new byte[1]);
        if (result == 0) {
            String hvn = String.valueOf(version[0] & 255);
            if (hvn.length() == 1) {
                hvn = "0" + hvn;
            }
            String lvn = String.valueOf(version[1] & 255);
            if (lvn.length() == 1) {
                lvn = "0" + lvn;
            }
            int readerType = rrlib.GetReaderType();
            if (readerType == 112 || readerType == 113 || readerType == 49) {
                byte[] describe = new byte[16];
                rrlib.GetModuleDescribe(describe);
                String dscInfo = "";
                if (describe[0] == 0) {
                    dscInfo = "S";
                } else if (describe[0] != 1) {
                    if (describe[0] == 2) {
                        dscInfo = "Pro";
                    }
                } else {
                    dscInfo = "Plus";
                }
                String moduleInfo = hvn + "." + lvn + " (" + Integer.toHexString(readerType) + "-" + dscInfo + ")";
                return moduleInfo;
            }
            String moduleInfo2 = hvn + "." + lvn + " (" + Integer.toHexString(readerType) + ")";
            return moduleInfo2;
        }
        return "";
    }

    public static int setRegion(Reader.Region_Conf region) {
        RrRegion_Conf regionConf = RrRegion_Conf.valueOf(region.value());
        int maxFrmPoint = getMaxFrmPoint(regionConf.value());
        return rrlib.SetRegion((byte) regionConf.value(), (byte) maxFrmPoint, (byte) 0);
    }

    public static int setReadWritePower(int readPower, int writePower) {
        int result = rrlib.SetRfPower((byte) (readPower | 128));
        if (result == 0) {
            return rrlib.SetWritePower((byte) (writePower | 128));
        }
        return result;
    }

    public static int[] getReadWritePower() {
        byte[] writePower = new byte[1];
        int result = rrlib.GetWritePower(writePower);
        if (result == 0) {
            byte[] readPower = new byte[1];
            int result2 = rrlib.GetReaderInformation(new byte[2], readPower, new byte[1], new byte[1], new byte[1]);
            if (result2 == 0) {
                int[] powers = {readPower[0] & ByteCompanionObject.MAX_VALUE, writePower[0] & ByteCompanionObject.MAX_VALUE};
                return powers;
            }
            return null;
        }
        return null;
    }

    public static void setSession(int session) {
        ReaderParameter param = rrlib.GetInventoryPatameter();
        param.Session = session;
        savedSession = session;
        rrlib.SetInventoryPatameter(param);
    }

    public static int getSession() {
        return savedSession;
    }

    public static void setQ(int qValue) {
        ReaderParameter param = rrlib.GetInventoryPatameter();
        param.QValue = qValue;
        savedQValue = qValue;
        rrlib.SetInventoryPatameter(param);
    }

    public static int getQ() {
        return savedQValue;
    }

    public static void setInvMask(byte[] bArr, int i, int i2, boolean z) {
        MaskClass maskClass = new MaskClass();
        maskClass.MaskData = bArr;
        int i3 = i2 * 16;
        maskClass.MaskAdr[0] = (byte) (i3 >> 8);
        maskClass.MaskAdr[1] = (byte) i3;
        maskClass.MaskLen = (byte) (bArr.length * 8);
        maskClass.MaskMem = (byte) i;
        rrlib.AddMaskList(maskClass);
        rrlib.SetMatchType(!z ? (byte) 1 : (byte) 0);
    }

    public static int setJgDwell(int jgTime, int dwell) {
        if (rrlib.ModuleType == 2) {
            byte[] data = {(byte) jgTime, (byte) dwell, 0};
            return rrlib.SetCfgParameter((byte) 0, (byte) 7, data, 3);
        }
        return -1;
    }

    public static int[] getJgDwell() {
        int[] ints = {-1, -1};
        if (rrlib.ModuleType == 2) {
            byte[] data = new byte[30];
            int[] len = new int[1];
            int fCmdRet = rrlib.GetCfgParameter((byte) 7, data, len);
            if (fCmdRet == 0 && len[0] == 3) {
                ints[0] = data[0] & 255;
                ints[1] = data[1] & 255;
            }
        }
        return ints;
    }

    public static int startRead() {
        ReaderParameter parameter = rrlib.GetInventoryPatameter();
        parameter.ScanTime = 50;
        parameter.Session = 253;
        parameter.QValue = 8;
        if (parameter.IvtType != 2) {
            parameter.IvtType = 0;
        }
        rrlib.SetInventoryPatameter(parameter);
        return rrlib.StartRead();
    }

    public static void stopRead() {
        rrlib.StopRead();
        ReaderParameter parameter = rrlib.GetInventoryPatameter();
        parameter.Session = savedSession;
        parameter.QValue = savedQValue;
        rrlib.SetInventoryPatameter(parameter);
    }

    public static int scanRfid(int ivtType, int memory, int wordPtr, int length, String psw, int readTime) {
        int result;
        try {
            synchronized (UHFRManager.waitLock) {
                ReaderParameter parameter = rrlib.GetInventoryPatameter();
                if (parameter.IvtType != 2 || ivtType != 0) {
                    parameter.IvtType = ivtType;
                }
                parameter.Session = savedSession;
                parameter.QValue = savedQValue;
                parameter.Memory = memory;
                parameter.WordPtr = wordPtr;
                parameter.Length = length;
                parameter.Password = psw;
                rrlib.SetInventoryPatameter(parameter);
                result = rrlib.ScanRfid(Math.max(readTime, 100));
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return -2;
        }
    }

    private static void setParameterMask(byte maskMem, byte maskAdr, byte maskLen, byte[] maskData) {
        int maskAdr2 = maskAdr * 16;
        ReaderParameter patameter = rrlib.GetInventoryPatameter();
        patameter.MaskMem = maskMem;
        patameter.MaskAdr[0] = (byte) (maskAdr2 >> 8);
        patameter.MaskAdr[1] = (byte) maskAdr2;
        patameter.MaskLen = (byte) (maskLen * 8);
        patameter.MaskData = maskData;
        rrlib.SetInventoryPatameter(patameter);
    }

    public static void setTarget(int target) {
        ReaderParameter parameter = rrlib.GetInventoryPatameter();
        parameter.Target = target;
        rrlib.SetInventoryPatameter(parameter);
    }

    public static void setFastId(boolean openFlag) {
        ReaderParameter param = rrlib.GetInventoryPatameter();
        param.IvtType = openFlag ? 2 : 0;
        rrlib.SetInventoryPatameter(param);
    }

    public static int readG2Data(int i, int i2, int i3, byte[] bArr, short s, byte[] bArr2, int i4, int i5, boolean z, byte[] bArr3) {
        rrlib.ClearMaskList();
        setParameterMask((byte) i4, (byte) i5, (byte) bArr2.length, bArr2);
        rrlib.SetMatchType(!z ? (byte) 1 : (byte) 0);
        int ReadData_G2 = rrlib.ReadData_G2((byte) (bArr2.length == 0 ? 0 : 255), new byte[0], (byte) i, i2, (byte) i3, bArr, bArr3, new byte[1]);
        setParameterMask((byte) 1, (byte) 0, (byte) 0, new byte[0]);
        return ReadData_G2;
    }

    public static int writeG2Data(char c, int i, byte[] bArr, int i2, byte[] bArr2, short s, byte[] bArr3, int i3, int i4, boolean z) {
        rrlib.ClearMaskList();
        setParameterMask((byte) i3, (byte) i4, (byte) bArr3.length, bArr3);
        rrlib.SetMatchType(!z ? (byte) 1 : (byte) 0);
        int WriteData_G2 = rrlib.WriteData_G2((byte) (i2 / 2), (byte) (bArr3.length == 0 ? 0 : 255), new byte[0], (byte) c, i, bArr, bArr2, new byte[1]);
        setParameterMask((byte) 1, (byte) 0, (byte) 0, new byte[0]);
        return WriteData_G2;
    }

    public static int writeTagEpc(byte[] bArr, byte[] bArr2, short s, byte[] bArr3, int i, int i2, boolean z) {
        rrlib.ClearMaskList();
        setParameterMask((byte) i, (byte) i2, (byte) bArr3.length, bArr3);
        rrlib.SetMatchType(!z ? (byte) 1 : (byte) 0);
        int length = (bArr.length / 2) << 11;
        byte[] bArr4 = {(byte) ((65280 & length) >> 8), (byte) (length & 255)};
        byte[] bArr5 = new byte[bArr.length + 2];
        System.arraycopy(bArr4, 0, bArr5, 0, bArr4.length);
        System.arraycopy(bArr, 0, bArr5, bArr4.length, bArr.length);
        int WriteData_G2 = rrlib.WriteData_G2((byte) (bArr5.length / 2), (byte) (bArr3.length == 0 ? 0 : 255), new byte[0], (byte) 1, 1, bArr5, bArr2, new byte[1]);
        setParameterMask((byte) 1, (byte) 0, (byte) 0, new byte[0]);
        return WriteData_G2;
    }

    public static int lockTag(Reader.Lock_Obj lockobject, Reader.Lock_Type locktype, byte[] accesspasswd, short timeout, byte[] fdata, int fbank, int fstartaddr, boolean matching) {
        byte epclen;
        if (fdata != null && fdata.length > 0) {
            if (fbank == 1 && fstartaddr == 2) {
                byte epclen2 = (byte) (fdata.length / 2);
                epclen = epclen2;
            }
            Log.e("huang,UHFRManager", "Rr lock tag to unsupported fbank or fstartaddr");
            return -2;
        }
        epclen = 0;
        RrLockObj lockObj = RrLockObj.valueOf(lockobject);
        RrLockType lockType = RrLockType.valueOf(locktype);
        return rrlib.Lock_G2(epclen, fdata, (byte) lockObj.value(), (byte) lockType.value(), accesspasswd, new byte[1]);
    }

    public static int killTag(byte[] killpasswd, short timeout, byte[] fdata, int fbank, int fstartaddr, boolean matching) {
        byte epclen = 0;
        if (fdata != null && fdata.length > 0) {
            if (fbank != 1 || fstartaddr != 2) {
                Log.e("huang,UHFRManager", "Rr lock tag to unsupported fbank or fstartaddr");
                return -2;
            }
            epclen = (byte) (fdata.length / 2);
        }
        return rrlib.Kill_G2(epclen, fdata, killpasswd, new byte[1]);
    }

    public static List<Reader.TEMPTAGINFO> measureYueHeTemp() {
        List<Reader.TEMPTAGINFO> taginfos = null;
        List<ReaderHelp.EpcTemp> epcTemps = rrlib.MeasureTemp(0, (byte) 0, new byte[0]);
        if (epcTemps != null && epcTemps.size() > 0) {
            taginfos = new ArrayList<>();
            for (ReaderHelp.EpcTemp epcTemp : epcTemps) {
                Reader.TEMPTAGINFO temptaginfo = new Reader.TEMPTAGINFO();
                if (epcTemp.EPC == null) {
                    epcTemp.EPC = "";
                }
                byte[] epcId = Tools.HexString2Bytes(epcTemp.EPC);
                temptaginfo.EpcId = epcId;
                temptaginfo.Epclen = (short) epcId.length;
                BigDecimal bigDecimal = new BigDecimal(epcTemp.temp);
                temptaginfo.Temperature = bigDecimal.setScale(2, RoundingMode.HALF_UP).doubleValue();
                taginfos.add(temptaginfo);
            }
        }
        return taginfos;
    }
}
