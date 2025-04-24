package com.rscja.deviceapi;

import android.util.Log;
import com.rscja.deviceapi.entity.AnimalEntity;
import com.rscja.deviceapi.exception.ConfigurationException;
import com.rscja.utility.StringUtility;
import java.util.Arrays;

/* loaded from: classes.dex */
public class RFIDWithLF {
    private static RFIDWithLF a = null;
    protected a config = a.g();

    private RFIDWithLF() throws ConfigurationException {
    }

    public static synchronized RFIDWithLF getInstance() throws ConfigurationException {
        RFIDWithLF rFIDWithLF;
        synchronized (RFIDWithLF.class) {
            if (a == null) {
                a = new RFIDWithLF();
            }
            rFIDWithLF = a;
        }
        return rFIDWithLF;
    }

    protected DeviceAPI getDeviceAPI() {
        return DeviceAPI.a();
    }

    public synchronized boolean init() {
        int EM125k_init = getDeviceAPI().EM125k_init(this.config.i(), this.config.j(), this.config.k());
        if (EM125k_init == 0) {
            return true;
        }
        Log.e("RFIDWithLF", "init() err:" + EM125k_init);
        return false;
    }

    public synchronized boolean free() {
        int EM125k_free = getDeviceAPI().EM125k_free(this.config.i());
        if (EM125k_free == 0) {
            return true;
        }
        Log.e("RFIDWithLF", "free() err:" + EM125k_free);
        return false;
    }

    public synchronized String getHardwareVersion() {
        char[] HardwareVersion_125k = getDeviceAPI().HardwareVersion_125k();
        if (HardwareVersion_125k[0] != 0) {
            Log.e("RFIDWithLF", "getHardwareVersion() err:" + Integer.valueOf(HardwareVersion_125k[0]));
            return null;
        }
        return new String(HardwareVersion_125k).trim();
    }

    public synchronized String readDataWithIDCard(int iMode) {
        char[] a2 = a(iMode);
        if (a2 == null) {
            return null;
        }
        return StringUtility.chars2HexString(a2, a2.length);
    }

    private char[] a(int i) {
        if (i > 2 || i < 0) {
            return null;
        }
        char[] EM125k_read = getDeviceAPI().EM125k_read(i);
        if (EM125k_read[0] != 0) {
            Log.e("RFIDWithLF", "read() err:" + Integer.valueOf(EM125k_read[0]));
            return null;
        }
        return Arrays.copyOfRange(EM125k_read, 2, EM125k_read[1] + 2);
    }

    public synchronized AnimalEntity readAnimalTags(int iMode) {
        char[] a2 = a(iMode);
        if (a2 == null) {
            return null;
        }
        AnimalEntity animalEntity = new AnimalEntity();
        char[] cArr = new char[8];
        for (int i = 0; i < 8; i++) {
            cArr[i] = a2[7 - i];
        }
        char[] cArr2 = {a2[9], a2[8]};
        char[] cArr3 = {a2[11], a2[10]};
        animalEntity.setNationalID(StringUtility.charArrayTolong(cArr));
        animalEntity.setCountryID(StringUtility.charArrayTolong(cArr2));
        animalEntity.setReserved(StringUtility.charArrayTolong(cArr3));
        return animalEntity;
    }

    public synchronized String readDataWithHitagS(int nPage) {
        char[] EM125k_ReadHitag = getDeviceAPI().EM125k_ReadHitag(nPage);
        if (EM125k_ReadHitag[0] != 0) {
            Log.e("RFIDWithLF", "readWithHitagS() err:" + Integer.valueOf(EM125k_ReadHitag[0]));
            return null;
        }
        char[] copyOfRange = Arrays.copyOfRange(EM125k_ReadHitag, 2, EM125k_ReadHitag[1] + 2);
        return StringUtility.chars2HexString(copyOfRange, copyOfRange.length);
    }

    public synchronized boolean writeDataWithHitagS(int nPage, String hexData) {
        if (!StringUtility.isEmpty(hexData) && StringUtility.isHexNumberRex(hexData)) {
            int nPage2 = getDeviceAPI().EM125k_WriteHitagPage(nPage, StringUtility.hexString2Chars(hexData));
            if (nPage2 == 0) {
                return true;
            }
            Log.e("RFIDWithLF", "writeDataWithHitagS() err:" + nPage2);
            return false;
        }
        return false;
    }

    public synchronized String readDataWith4305Card(int nPage) {
        char[] EM125k_Read4305 = getDeviceAPI().EM125k_Read4305(nPage);
        if (EM125k_Read4305[0] != 0) {
            Log.e("RFIDWithLF", "readDataWith4305Card() err:" + Integer.valueOf(EM125k_Read4305[0]));
            return null;
        }
        char[] copyOfRange = Arrays.copyOfRange(EM125k_Read4305, 2, EM125k_Read4305[1] + 2);
        return StringUtility.chars2HexString(copyOfRange, copyOfRange.length);
    }

    public synchronized boolean writeDataWith4305Card(int nPage, String hexData) {
        if (!StringUtility.isEmpty(hexData) && StringUtility.isHexNumberRex(hexData)) {
            int nPage2 = getDeviceAPI().EM125k_Write4305(nPage, StringUtility.hexString2Chars(hexData));
            if (nPage2 == 0) {
                return true;
            }
            Log.e("RFIDWithLF", "writeDataWith4305Card() err:" + nPage2);
            return false;
        }
        return false;
    }

    public synchronized int getUIDWithHID() {
        return getDeviceAPI().HID_GetUid();
    }

    public synchronized String getUIDWithHitagS() {
        char[] EM125k_UID_REQ = getDeviceAPI().EM125k_UID_REQ();
        if (EM125k_UID_REQ[0] != 0) {
            Log.e("RFIDWithLF", "getUIDWithHitagS() err:" + Integer.valueOf(EM125k_UID_REQ[0]));
            return null;
        }
        char[] copyOfRange = Arrays.copyOfRange(EM125k_UID_REQ, 2, EM125k_UID_REQ[1] + 2);
        return StringUtility.chars2HexString(copyOfRange, copyOfRange.length);
    }

    public synchronized String getUIDWith4450Card() {
        char[] EM125K_GetEm4450UID = getDeviceAPI().EM125K_GetEm4450UID();
        if (EM125K_GetEm4450UID[0] != 0) {
            Log.e("RFIDWithLF", "getUIDWith4450Card() err:" + Integer.valueOf(EM125K_GetEm4450UID[0]));
            return null;
        }
        char[] copyOfRange = Arrays.copyOfRange(EM125K_GetEm4450UID, 2, EM125K_GetEm4450UID[1] + 2);
        return StringUtility.chars2HexString(copyOfRange, copyOfRange.length);
    }

    public synchronized boolean initWithNeedleTag() {
        int EM125k_init_Ex = getDeviceAPI().EM125k_init_Ex(this.config.i(), this.config.j(), this.config.k());
        if (EM125k_init_Ex == 0) {
            return true;
        }
        Log.e("RFIDWithLF", "init() err:" + EM125k_init_Ex);
        return false;
    }

    public synchronized String readWithNeedleTag() {
        char[] EM125k_read_Ex = getDeviceAPI().EM125k_read_Ex();
        if (EM125k_read_Ex[0] != 0) {
            Log.e("RFIDWithLF", "readWithNeedleTag() err:" + Integer.valueOf(EM125k_read_Ex[0]));
            return null;
        }
        return new String(Arrays.copyOfRange(EM125k_read_Ex, 2, EM125k_read_Ex[1] + 2));
    }
}
