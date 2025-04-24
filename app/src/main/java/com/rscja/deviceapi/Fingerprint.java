package com.rscja.deviceapi;

import android.util.Log;
import com.rscja.deviceapi.exception.ConfigurationException;
import com.rscja.utility.StringUtility;

/* loaded from: classes.dex */
public class Fingerprint {
    private static Fingerprint a = null;
    protected a config = a.f();

    protected Fingerprint() throws ConfigurationException {
    }

    public static synchronized Fingerprint getInstance() throws ConfigurationException {
        Fingerprint fingerprint;
        synchronized (Fingerprint.class) {
            if (a == null) {
                a = new Fingerprint();
            }
            fingerprint = a;
        }
        return fingerprint;
    }

    protected DeviceAPI getDeviceAPI() {
        return DeviceAPI.a();
    }

    public synchronized boolean init() {
        int EMFingerInit = getDeviceAPI().EMFingerInit(this.config.i(), this.config.j(), this.config.k());
        if (EMFingerInit == 0) {
            Log.i("Fingerprint", "init() succ");
            return true;
        }
        Log.e("Fingerprint", "init() err:" + EMFingerInit);
        return false;
    }

    public synchronized boolean free() {
        int EMFingerFree = getDeviceAPI().EMFingerFree(this.config.i());
        if (EMFingerFree == 0) {
            Log.i("Fingerprint", "free() succ");
            return true;
        }
        Log.e("Fingerprint", "free() err:" + EMFingerFree);
        return false;
    }

    public enum BufferEnum {
        B1(1),
        B2(2),
        B11(17),
        B12(18);

        private final int a;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static BufferEnum[] valuesCustom() {
            BufferEnum[] valuesCustom = values();
            int length = valuesCustom.length;
            BufferEnum[] bufferEnumArr = new BufferEnum[length];
            System.arraycopy(valuesCustom, 0, bufferEnumArr, 0, length);
            return bufferEnumArr;
        }

        public final int getValue() {
            return this.a;
        }

        BufferEnum(int value) {
            this.a = value;
        }
    }

    public synchronized String getRandomData() {
        char[] EMGetRandomData = getDeviceAPI().EMGetRandomData();
        if (EMGetRandomData[0] != 0) {
            Log.e("Fingerprint", "getRandomData() err:" + Integer.valueOf(EMGetRandomData[0]));
            return null;
        }
        char c = EMGetRandomData[1];
        char[] cArr = new char[c];
        for (int i = 0; i < EMGetRandomData[1]; i++) {
            cArr[i] = EMGetRandomData[i + 2];
        }
        return StringUtility.chars2HexString(cArr, c);
    }

    public synchronized boolean getImage() {
        int EMGetImage = getDeviceAPI().EMGetImage();
        if (EMGetImage == 0) {
            return true;
        }
        Log.e("Fingerprint", "getImage() err:" + EMGetImage);
        return false;
    }

    public synchronized boolean genChar(BufferEnum buffer) {
        int EMGenChar = getDeviceAPI().EMGenChar(buffer.a);
        if (EMGenChar == 0) {
            return true;
        }
        Log.e("Fingerprint", "genChar() err:" + EMGenChar);
        return false;
    }

    public synchronized int[] search(BufferEnum buffer, int startPage, int pageNum) {
        int[] iArr = {-1, -1};
        int[] EMSearch = getDeviceAPI().EMSearch(buffer.a, startPage, pageNum);
        if (EMSearch != null && EMSearch[0] == 0) {
            iArr[0] = EMSearch[1];
            iArr[1] = EMSearch[2];
            return iArr;
        }
        Log.e("Fingerprint", "search() err:" + Integer.valueOf(EMSearch[0]));
        return null;
    }

    public synchronized int match() {
        int[] EMMatch = getDeviceAPI().EMMatch();
        if (EMMatch == null || EMMatch[0] != 0) {
            Log.e("Fingerprint", "match() err:" + Integer.valueOf(EMMatch[0]));
            return -1;
        }
        return EMMatch[1];
    }

    public synchronized boolean regModel() {
        int EMRegModel = getDeviceAPI().EMRegModel();
        if (EMRegModel == 0) {
            return true;
        }
        Log.e("Fingerprint", "regModel() err:" + EMRegModel);
        return false;
    }

    public synchronized boolean storChar(BufferEnum buffer, int pageID) {
        int EMStorChar = getDeviceAPI().EMStorChar(buffer.a, pageID);
        if (EMStorChar == 0) {
            return true;
        }
        Log.e("Fingerprint", "storChar() err:" + EMStorChar);
        return false;
    }

    public synchronized boolean loadChar(BufferEnum buffer, int pageID) {
        int EMLoadChar = getDeviceAPI().EMLoadChar(buffer.a, pageID);
        if (EMLoadChar == 0) {
            return true;
        }
        Log.e("Fingerprint", "loadChar() err:" + EMLoadChar);
        return false;
    }

    public synchronized String upChar(BufferEnum buffer) {
        char[] EMUpChar = getDeviceAPI().EMUpChar(buffer.a);
        int i = 0;
        if (EMUpChar[0] != 0) {
            Log.e("Fingerprint", "upChar() err:" + Integer.valueOf(EMUpChar[0]));
            return null;
        }
        char[] cArr = new char[512];
        while (i < 512) {
            int i2 = i + 1;
            cArr[i] = EMUpChar[i2];
            i = i2;
        }
        return StringUtility.chars2HexString(cArr, 512);
    }

    public synchronized boolean downChar(BufferEnum buffer, String hexStr) {
        if (StringUtility.isEmpty(hexStr)) {
            return false;
        }
        if (!StringUtility.isHexNumber(hexStr)) {
            return false;
        }
        int EMDownChar = getDeviceAPI().EMDownChar(buffer.a, StringUtility.hexString2Chars(hexStr));
        if (EMDownChar == 0) {
            return true;
        }
        Log.e("Fingerprint", "downChar() err:" + EMDownChar);
        return false;
    }

    public synchronized boolean deletChar(int pageID, int num) {
        int pageID2 = getDeviceAPI().EMDeletChar(pageID, num);
        if (pageID2 == 0) {
            return true;
        }
        Log.e("Fingerprint", "deletChar() err:" + pageID2);
        return false;
    }

    public synchronized boolean empty() {
        int EMEmpty = getDeviceAPI().EMEmpty();
        if (EMEmpty == 0) {
            return true;
        }
        Log.e("Fingerprint", "empty() err:" + EMEmpty);
        return false;
    }

    public synchronized boolean setReg(int regID, int value) {
        int regID2 = getDeviceAPI().EMSetReg(regID, value);
        if (regID2 == 0) {
            return true;
        }
        Log.e("Fingerprint", "setReg() err:" + regID2);
        return false;
    }

    public synchronized int autoEnroll(int count, int userID) {
        int[] EMAutoEnroll = getDeviceAPI().EMAutoEnroll(count, userID);
        if (EMAutoEnroll[0] != 0) {
            Log.e("Fingerprint", "autoEnroll() err:" + Integer.valueOf(EMAutoEnroll[0]));
            return -1;
        }
        return EMAutoEnroll[1];
    }

    public synchronized int[] autoMatch(int count, int startPage, int pageNum) {
        int[] EMAutoMatch = getDeviceAPI().EMAutoMatch(count, startPage, pageNum);
        int[] iArr = {-1, -1};
        if (EMAutoMatch != null && EMAutoMatch[0] == 0) {
            iArr[0] = EMAutoMatch[1];
            iArr[1] = EMAutoMatch[2];
            return iArr;
        }
        Log.e("Fingerprint", "autoMatch() err:" + Integer.valueOf(EMAutoMatch[0]));
        return null;
    }

    public synchronized int validTempleteNum() {
        int[] EMValidTempleteNum = getDeviceAPI().EMValidTempleteNum();
        if (EMValidTempleteNum[0] != 0) {
            Log.e("Fingerprint", "validTempleteNum() err:" + Integer.valueOf(EMValidTempleteNum[0]));
            return -1;
        }
        return EMValidTempleteNum[1];
    }

    public synchronized String readChipSN() {
        char[] EMReadChipSN = getDeviceAPI().EMReadChipSN();
        if (EMReadChipSN[0] != 0) {
            Log.e("Fingerprint", "readChipSN() err:" + Integer.valueOf(EMReadChipSN[0]));
            return null;
        }
        char c = EMReadChipSN[1];
        char[] cArr = new char[c];
        for (int i = 0; i < EMReadChipSN[1]; i++) {
            cArr[i] = EMReadChipSN[i + 2];
        }
        return StringUtility.chars2HexString(cArr, c);
    }

    public synchronized boolean setManuFacture(String name) {
        if (StringUtility.isEmpty(name)) {
            return false;
        }
        int EMSetManuFacture = getDeviceAPI().EMSetManuFacture(StringUtility.hexString2Chars(name));
        if (EMSetManuFacture == 0) {
            return true;
        }
        Log.e("Fingerprint", "setManuFacture() err:" + EMSetManuFacture);
        return false;
    }

    public synchronized boolean setDeviceName(String name) {
        if (StringUtility.isEmpty(name)) {
            return false;
        }
        int EMSetDeviceName = getDeviceAPI().EMSetDeviceName(StringUtility.hexString2Chars(name));
        if (EMSetDeviceName == 0) {
            return true;
        }
        Log.e("Fingerprint", "setDeviceName() err:" + EMSetDeviceName);
        return false;
    }

    public synchronized String readSysPara() {
        char[] EMReadSysPara = getDeviceAPI().EMReadSysPara();
        if (EMReadSysPara[0] != 0) {
            Log.e("Fingerprint", "readSysPara() err:" + Integer.valueOf(EMReadSysPara[0]));
            return null;
        }
        char c = EMReadSysPara[1];
        char[] cArr = new char[c];
        for (int i = 0; i < EMReadSysPara[1]; i++) {
            cArr[i] = EMReadSysPara[i + 2];
        }
        return StringUtility.chars2HexString(cArr, c);
    }

    public synchronized String getVersion() {
        char[] EMReadSysPara = getDeviceAPI().EMReadSysPara();
        if (EMReadSysPara[0] != 0) {
            Log.e("Fingerprint", "getVersion() err:" + Integer.valueOf(EMReadSysPara[0]));
            return null;
        }
        char[] cArr = new char[EMReadSysPara[1]];
        for (int i = 0; i < EMReadSysPara[1]; i++) {
            cArr[i] = EMReadSysPara[i + 2];
        }
        return new String(cArr);
    }

    public synchronized int upImage(int mode, String fileName) {
        int[] EMUpImage = getDeviceAPI().EMUpImage(mode, fileName);
        if (EMUpImage[0] != 0) {
            Log.e("Fingerprint", "upImage() err:" + Integer.valueOf(EMUpImage[0]));
            return -1;
        }
        return EMUpImage[1];
    }
}
