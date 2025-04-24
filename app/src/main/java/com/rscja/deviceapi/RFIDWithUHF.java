package com.rscja.deviceapi;

import android.util.Log;
import com.rscja.deviceapi.entity.SimpleRFIDEntity;
import com.rscja.deviceapi.exception.ConfigurationException;
import com.rscja.utility.StringUtility;
import java.util.Arrays;
import kotlin.jvm.internal.ByteCompanionObject;

/* loaded from: classes.dex */
public class RFIDWithUHF {
    private static RFIDWithUHF a = null;
    protected a config = a.e();

    protected DeviceAPI getDeviceAPI() {
        return DeviceAPI.a();
    }

    public static synchronized RFIDWithUHF getInstance() throws ConfigurationException {
        RFIDWithUHF rFIDWithUHF;
        synchronized (RFIDWithUHF.class) {
            if (a == null) {
                a = new RFIDWithUHF();
            }
            rFIDWithUHF = a;
        }
        return rFIDWithUHF;
    }

    private RFIDWithUHF() throws ConfigurationException {
    }

    public enum BankEnum {
        RESERVED((byte) 0),
        UII((byte) 1),
        TID((byte) 2),
        USER((byte) 3);

        private final byte a;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static BankEnum[] valuesCustom() {
            BankEnum[] valuesCustom = values();
            int length = valuesCustom.length;
            BankEnum[] bankEnumArr = new BankEnum[length];
            System.arraycopy(valuesCustom, 0, bankEnumArr, 0, length);
            return bankEnumArr;
        }

        public final byte getValue() {
            return this.a;
        }

        BankEnum(byte value) {
            this.a = value;
        }
    }

    public enum SingelModeEnum {
        SINGEL((byte) 0),
        MORE((byte) 1);

        private final byte a;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static SingelModeEnum[] valuesCustom() {
            SingelModeEnum[] valuesCustom = values();
            int length = valuesCustom.length;
            SingelModeEnum[] singelModeEnumArr = new SingelModeEnum[length];
            System.arraycopy(valuesCustom, 0, singelModeEnumArr, 0, length);
            return singelModeEnumArr;
        }

        public final byte getValue() {
            return this.a;
        }

        SingelModeEnum(byte value) {
            this.a = value;
        }
    }

    public enum LockModeEnum {
        HOLD((byte) 0),
        LOCK((byte) 1),
        UNLOCK((byte) 2),
        PLOCK((byte) 3),
        PUNLOCK((byte) 4);

        private final byte a;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static LockModeEnum[] valuesCustom() {
            LockModeEnum[] valuesCustom = values();
            int length = valuesCustom.length;
            LockModeEnum[] lockModeEnumArr = new LockModeEnum[length];
            System.arraycopy(valuesCustom, 0, lockModeEnumArr, 0, length);
            return lockModeEnumArr;
        }

        public final byte getValue() {
            return this.a;
        }

        LockModeEnum(byte value) {
            this.a = value;
        }
    }

    public synchronized boolean init() {
        int UHFInit = getDeviceAPI().UHFInit();
        if (UHFInit == 0) {
            int UHFOpenAndConnect = getDeviceAPI().UHFOpenAndConnect(this.config.j());
            if (UHFOpenAndConnect == 0) {
                return true;
            }
            Log.e("RFIDWithUHF", "init() err UHFOpenAndConnect result:" + UHFOpenAndConnect);
        } else {
            Log.e("RFIDWithUHF", "init() err UHFInit result:" + UHFInit);
        }
        return false;
    }

    public synchronized boolean free() {
        getDeviceAPI().UHFCloseAndDisconnect();
        int UHFFree = getDeviceAPI().UHFFree();
        if (UHFFree == 0) {
            return true;
        }
        Log.e("RFIDWithUHF", "free() err UHFFree result:" + UHFFree);
        return false;
    }

    public synchronized void crcOn() {
        getDeviceAPI().UHFFlagCrcOn();
    }

    public synchronized void crcOff() {
        getDeviceAPI().UHFFlafCrcOff();
    }

    public String convertUiiToEPC(String uii) {
        if (StringUtility.isEmpty(uii)) {
            return "";
        }
        String upperCase = uii.replace("-", "").toUpperCase();
        return upperCase.substring(4, upperCase.length());
    }

    public synchronized String getHardwareType() {
        char[] UHFGetHwType = getDeviceAPI().UHFGetHwType();
        if (UHFGetHwType == null || UHFGetHwType[0] != 0) {
            Log.e("RFIDWithUHF", "getHardwareType() err:" + UHFGetHwType[0]);
            return null;
        }
        return new String(Arrays.copyOfRange(UHFGetHwType, 2, UHFGetHwType[1] + 2));
    }

    public synchronized int getPower() {
        char[] UHFGetPower = getDeviceAPI().UHFGetPower();
        if (UHFGetPower == null || UHFGetPower[0] != 0) {
            Log.e("RFIDWithUHF", "getPower() err :" + ((int) UHFGetPower[0]));
            return -1;
        }
        return UHFGetPower[1];
    }

    public synchronized boolean setPower(int power) {
        int UHFSetPower = getDeviceAPI().UHFSetPower((char) power);
        if (UHFSetPower == 0) {
            return true;
        }
        Log.e("RFIDWithUHF", "setPower() err :" + UHFSetPower);
        return false;
    }

    public synchronized int getFrequencyMode() {
        char[] UHFGetFrequency_Ex = getDeviceAPI().UHFGetFrequency_Ex();
        if (UHFGetFrequency_Ex == null || UHFGetFrequency_Ex[0] != 0) {
            Log.e("RFIDWithUHF", "getFrequencyMode() err :" + ((int) UHFGetFrequency_Ex[0]));
            return -1;
        }
        return UHFGetFrequency_Ex[1];
    }

    public synchronized boolean setFrequencyMode(byte freMode) {
        int UHFSetFrequency_EX = getDeviceAPI().UHFSetFrequency_EX((char) freMode);
        if (UHFSetFrequency_EX == 0) {
            return true;
        }
        Log.e("RFIDWithUHF", "setFrequencyMode() err :" + UHFSetFrequency_EX);
        return false;
    }

    public synchronized boolean startInventory(int flagAnti, int initQ) {
        int UHFInventory = getDeviceAPI().UHFInventory((char) flagAnti, (char) initQ);
        if (UHFInventory == 0) {
            return true;
        }
        Log.e("RFIDWithUHF", "startInventory() err :" + UHFInventory);
        return false;
    }

    public synchronized String readUidFormBuffer() {
        char[] UHFGetReceived = getDeviceAPI().UHFGetReceived();
        if (UHFGetReceived != null && UHFGetReceived[0] == 0) {
            char[] copyOfRange = Arrays.copyOfRange(UHFGetReceived, 2, UHFGetReceived[1] + 2);
            return StringUtility.chars2HexString(copyOfRange, copyOfRange.length);
        }
        Log.e("RFIDWithUHF", "readUidFormBuffer() err :" + ((int) UHFGetReceived[0]));
        return null;
    }

    public synchronized boolean stopInventory() {
        int UHFStopGet = getDeviceAPI().UHFStopGet();
        if (UHFStopGet == 0) {
            return true;
        }
        Log.e("RFIDWithUHF", "stopInventory() err :" + UHFStopGet);
        return false;
    }

    public synchronized boolean setPwm(int WorkTime, int WaitTime) {
        int WorkTime2 = getDeviceAPI().UHFSetPwm(WorkTime, WaitTime);
        if (WorkTime2 == 0) {
            return true;
        }
        Log.e("RFIDWithUHF", "setPwm() err :" + WorkTime2);
        return false;
    }

    public synchronized int[] getPwm() {
        int[] UHFGetPwm = getDeviceAPI().UHFGetPwm();
        if (UHFGetPwm != null && UHFGetPwm[0] == 0) {
            return Arrays.copyOfRange(UHFGetPwm, 1, UHFGetPwm[1] + 1);
        }
        Log.e("RFIDWithUHF", "getPwm() err :" + UHFGetPwm[0]);
        return null;
    }

    public synchronized boolean setReadMode(SingelModeEnum mode) {
        int UHFSetSingelMode = getDeviceAPI().UHFSetSingelMode((char) mode.a);
        if (UHFSetSingelMode == 0) {
            return true;
        }
        Log.e("RFIDWithUHF", "setReadMode() err :" + UHFSetSingelMode);
        return false;
    }

    public synchronized int getReadMode() {
        char[] UHFGetSingelMode = getDeviceAPI().UHFGetSingelMode();
        if (UHFGetSingelMode != null && UHFGetSingelMode[0] == 0) {
            return UHFGetSingelMode[1];
        }
        Log.e("RFIDWithUHF", "getReadMode() err :" + ((int) UHFGetSingelMode[0]));
        return -1;
    }

    public synchronized String lockMem(String accessPwd, String lockCode) {
        if (StringUtility.isEmpty(accessPwd)) {
            return null;
        }
        char[] UHFLockMemSingle = getDeviceAPI().UHFLockMemSingle(StringUtility.hexString2Chars(accessPwd), StringUtility.hexString2Chars(lockCode));
        if (UHFLockMemSingle[0] != 0) {
            Log.e("RFIDWithUHF", "lockMem() err :" + ((int) UHFLockMemSingle[0]));
            return null;
        }
        char[] copyOfRange = Arrays.copyOfRange(UHFLockMemSingle, 2, UHFLockMemSingle[1] + 2);
        return StringUtility.chars2HexString(copyOfRange, copyOfRange.length);
    }

    public synchronized boolean lockMem(String accessPwd, String lockCode, String uii) {
        if (!StringUtility.isEmpty(accessPwd) && !StringUtility.isEmpty(uii)) {
            int UHFLockMem = getDeviceAPI().UHFLockMem(StringUtility.hexString2Chars(accessPwd), StringUtility.hexString2Chars(lockCode), StringUtility.hexString2Chars(uii));
            if (UHFLockMem == 0) {
                return true;
            }
            Log.e("RFIDWithUHF", "lockMem() err :" + UHFLockMem);
            return false;
        }
        return false;
    }

    public synchronized String inventorySingleTag() {
        char[] UHFInventorySingle = getDeviceAPI().UHFInventorySingle();
        if (UHFInventorySingle[0] != 0) {
            Log.e("RFIDWithUHF", "inventorySingleTag() err :" + ((int) UHFInventorySingle[0]));
            return null;
        }
        char[] copyOfRange = Arrays.copyOfRange(UHFInventorySingle, 2, UHFInventorySingle[1] + 2);
        return StringUtility.chars2HexString(copyOfRange, copyOfRange.length);
    }

    public synchronized SimpleRFIDEntity readData(String accessPwd, BankEnum bank, int ptr, int cnt) {
        if (StringUtility.isEmpty(accessPwd)) {
            return null;
        }
        char[] UHFReadDataSingle = getDeviceAPI().UHFReadDataSingle(StringUtility.hexString2Chars(accessPwd), (char) bank.a, ptr, (char) cnt);
        if (UHFReadDataSingle[0] != 0) {
            Log.e("RFIDWithUHF", "readData() err :" + ((int) UHFReadDataSingle[0]));
            return null;
        }
        char[] copyOfRange = Arrays.copyOfRange(UHFReadDataSingle, 3, UHFReadDataSingle[2] + 3);
        SimpleRFIDEntity simpleRFIDEntity = new SimpleRFIDEntity(StringUtility.chars2HexString(copyOfRange, copyOfRange.length), "UHF");
        simpleRFIDEntity.setData(StringUtility.chars2HexString(Arrays.copyOfRange(UHFReadDataSingle, copyOfRange.length + 4, UHFReadDataSingle[copyOfRange.length + 3] + 4 + copyOfRange.length), cnt << 1));
        return simpleRFIDEntity;
    }

    public synchronized String readData(String accessPwd, BankEnum bank, int ptr, int cnt, String uii) {
        if (!StringUtility.isEmpty(accessPwd) && !StringUtility.isEmpty(uii)) {
            char[] UHFReadData = getDeviceAPI().UHFReadData(StringUtility.hexString2Chars(accessPwd), (char) bank.a, (char) ptr, (char) cnt, StringUtility.hexString2Chars(uii));
            if (UHFReadData[0] != 0) {
                Log.e("RFIDWithUHF", "readData() err :" + ((int) UHFReadData[0]));
                return null;
            }
            char[] copyOfRange = Arrays.copyOfRange(UHFReadData, 2, UHFReadData[1] + 2);
            return StringUtility.chars2HexString(copyOfRange, copyOfRange.length);
        }
        return null;
    }

    public synchronized String writeData(String accessPwd, BankEnum bank, int ptr, int cnt, String data) {
        if (!StringUtility.isEmpty(accessPwd) && !StringUtility.isEmpty(data)) {
            char[] hexString2Chars = StringUtility.hexString2Chars(accessPwd);
            char[] hexString2Chars2 = StringUtility.hexString2Chars(data);
            int i = cnt << 1;
            char[] cArr = new char[i];
            for (int i2 = 0; i2 < i; i2++) {
                if (i2 < hexString2Chars2.length) {
                    cArr[i2] = hexString2Chars2[i2];
                } else {
                    cArr[i2] = 0;
                }
            }
            char[] cArr2 = null;
            for (int i3 = 0; i3 < cnt; i3++) {
                int i4 = i3 << 1;
                cArr2 = getDeviceAPI().UHFWriteDataSingle(hexString2Chars, (char) bank.a, i3 + ptr, (char) 1, Arrays.copyOfRange(cArr, i4, i4 + 2));
                if (cArr2[0] != 0) {
                    Log.e("RFIDWithUHF", "writeData() err :" + ((int) cArr2[0]));
                    return null;
                }
            }
            if (cArr2 == null) {
                return null;
            }
            char[] copyOfRange = Arrays.copyOfRange(cArr2, 2, cArr2[1] + 2);
            return StringUtility.chars2HexString(copyOfRange, copyOfRange.length);
        }
        return null;
    }

    public synchronized boolean writeData(String accessPwd, BankEnum bank, int ptr, int cnt, String data, String uii) {
        if (!StringUtility.isEmpty(accessPwd) && !StringUtility.isEmpty(data) && !StringUtility.isEmpty(uii)) {
            int UHFWriteData = getDeviceAPI().UHFWriteData(StringUtility.hexString2Chars(accessPwd), (char) bank.a, ptr, (char) cnt, StringUtility.hexString2Chars(uii), StringUtility.hexString2Chars(data));
            if (UHFWriteData == 0) {
                return true;
            }
            Log.e("RFIDWithUHF", "writeData() err :" + UHFWriteData);
            return false;
        }
        return false;
    }

    public synchronized String eraseData(String accessPwd, BankEnum bank, int ptr, int cnt) {
        if (StringUtility.isEmpty(accessPwd)) {
            return null;
        }
        char[] UHFEraseDataSingle = getDeviceAPI().UHFEraseDataSingle(StringUtility.hexString2Chars(accessPwd), (char) bank.a, ptr, (char) cnt);
        if (UHFEraseDataSingle[0] != 0) {
            Log.e("RFIDWithUHF", "eraseData() err :" + ((int) UHFEraseDataSingle[0]));
            return null;
        }
        char[] copyOfRange = Arrays.copyOfRange(UHFEraseDataSingle, 2, UHFEraseDataSingle[1] + 2);
        return StringUtility.chars2HexString(copyOfRange, copyOfRange.length);
    }

    public synchronized boolean eraseData(String accessPwd, BankEnum bank, int ptr, int cnt, String uii) {
        if (!StringUtility.isEmpty(accessPwd) && !StringUtility.isEmpty(uii)) {
            int UHFEraseData = getDeviceAPI().UHFEraseData(StringUtility.hexString2Chars(accessPwd), (char) bank.a, ptr, (char) cnt, StringUtility.hexString2Chars(uii));
            if (UHFEraseData == 0) {
                return true;
            }
            Log.e("RFIDWithUHF", "eraseData() err :" + UHFEraseData);
            return false;
        }
        return false;
    }

    public synchronized String killTag(String killPwd) {
        if (StringUtility.isEmpty(killPwd)) {
            return null;
        }
        char[] UHFKillTagSingle = getDeviceAPI().UHFKillTagSingle(StringUtility.hexString2Chars(killPwd));
        if (UHFKillTagSingle[0] != 0) {
            Log.e("RFIDWithUHF", "killTag() err :" + ((int) UHFKillTagSingle[0]));
            return null;
        }
        char[] copyOfRange = Arrays.copyOfRange(UHFKillTagSingle, 2, UHFKillTagSingle[1] + 2);
        return StringUtility.chars2HexString(copyOfRange, copyOfRange.length);
    }

    public synchronized boolean killTag(String killPwd, String uii) {
        if (!StringUtility.isEmpty(killPwd) && !StringUtility.isEmpty(uii)) {
            int UHFKillTag = getDeviceAPI().UHFKillTag(StringUtility.hexString2Chars(killPwd), StringUtility.hexString2Chars(uii));
            if (UHFKillTag == 0) {
                return true;
            }
            Log.e("RFIDWithUHF", "killTag() err :" + UHFKillTag);
            return false;
        }
        return false;
    }

    public synchronized String generateLockCode(LockModeEnum killPwd, LockModeEnum accessPwd, LockModeEnum uii, LockModeEnum tid, LockModeEnum user) {
        byte[] bArr;
        byte b = killPwd.a;
        byte b2 = accessPwd.a;
        byte b3 = uii.a;
        byte b4 = tid.a;
        byte b5 = user.a;
        bArr = new byte[]{0, 0, 0};
        if (b == 1) {
            bArr[0] = (byte) (bArr[0] | 12);
            bArr[1] = (byte) (bArr[1] | 2);
        } else if (b != 2) {
            if (b == 3) {
                bArr[0] = (byte) (bArr[0] | 12);
                bArr[1] = (byte) (bArr[1] | 3);
            } else if (b == 4) {
                bArr[0] = (byte) (bArr[0] | 12);
                bArr[1] = (byte) (bArr[1] | 1);
            } else {
                bArr[0] = (byte) (bArr[0] & 3);
                bArr[1] = (byte) (bArr[1] & 252);
            }
        } else {
            bArr[0] = (byte) (bArr[0] | 12);
            bArr[1] = bArr[1];
        }
        if (b2 == 1) {
            bArr[0] = (byte) (bArr[0] | 3);
            bArr[2] = (byte) (bArr[2] | ByteCompanionObject.MIN_VALUE);
        } else if (b2 != 2) {
            if (b2 == 3) {
                bArr[0] = (byte) (bArr[0] | 3);
                bArr[2] = (byte) (bArr[2] | 192);
            } else if (b2 == 4) {
                bArr[0] = (byte) (bArr[0] | 3);
                bArr[2] = (byte) (bArr[2] | 64);
            } else {
                bArr[0] = (byte) (bArr[0] & 12);
                bArr[2] = (byte) (bArr[2] & 63);
            }
        } else {
            bArr[0] = (byte) (bArr[0] | 3);
            bArr[2] = bArr[2];
        }
        if (b3 == 1) {
            bArr[1] = (byte) (bArr[1] | 192);
            bArr[2] = (byte) (bArr[2] | 32);
        } else if (b3 != 2) {
            if (b3 == 3) {
                bArr[1] = (byte) (bArr[1] | 192);
                bArr[2] = (byte) (bArr[2] | 48);
            } else if (b3 == 4) {
                bArr[1] = (byte) (bArr[1] | 192);
                bArr[2] = (byte) (bArr[2] | 16);
            } else {
                bArr[1] = (byte) (bArr[1] & 63);
                bArr[2] = (byte) (bArr[2] & 207);
            }
        } else {
            bArr[1] = (byte) (bArr[1] | 192);
            bArr[2] = bArr[2];
        }
        if (b4 == 1) {
            bArr[1] = (byte) (bArr[1] | 48);
            bArr[2] = (byte) (bArr[2] | 8);
        } else if (b4 != 2) {
            if (b4 == 3) {
                bArr[1] = (byte) (bArr[1] | 48);
                bArr[2] = (byte) (bArr[2] | 12);
            } else if (b4 == 4) {
                bArr[1] = (byte) (bArr[1] | 48);
                bArr[2] = (byte) (bArr[2] | 4);
            } else {
                bArr[1] = (byte) (bArr[1] & 207);
                bArr[2] = (byte) (bArr[2] & 243);
            }
        } else {
            bArr[1] = (byte) (bArr[1] | 48);
            bArr[2] = bArr[2];
        }
        if (b5 == 1) {
            bArr[1] = (byte) (bArr[1] | 12);
            bArr[2] = (byte) (bArr[2] | 2);
        } else if (b5 != 2) {
            if (b5 == 3) {
                bArr[1] = (byte) (bArr[1] | 12);
                bArr[2] = (byte) (bArr[2] | 3);
            } else if (b5 == 4) {
                bArr[1] = (byte) (bArr[1] | 12);
                bArr[2] = (byte) (bArr[2] | 1);
            } else {
                bArr[1] = (byte) (bArr[1] & 243);
                bArr[2] = (byte) (bArr[2] & 252);
            }
        } else {
            bArr[1] = (byte) (bArr[1] | 12);
            bArr[2] = bArr[2];
        }
        return StringUtility.bytes2HexString(bArr, 3);
    }
}
