package com.rscja.deviceapi;

import android.util.Log;
import com.rscja.deviceapi.entity.DESFireFile;
import com.rscja.deviceapi.entity.SimpleRFIDEntity;
import com.rscja.deviceapi.exception.ConfigurationException;
import com.rscja.deviceapi.exception.RFIDNotFoundException;
import com.rscja.deviceapi.exception.RFIDReadFailureException;
import com.rscja.deviceapi.exception.RFIDVerificationException;
import com.rscja.utility.StringUtility;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public class RFIDWithISO14443A extends b {
    private static RFIDWithISO14443A a = null;

    public enum KeyType {
        TypeA,
        TypeB,
        TypeDes;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static KeyType[] valuesCustom() {
            KeyType[] valuesCustom = values();
            int length = valuesCustom.length;
            KeyType[] keyTypeArr = new KeyType[length];
            System.arraycopy(valuesCustom, 0, keyTypeArr, 0, length);
            return keyTypeArr;
        }
    }

    public enum TagType {
        Ultra_light,
        S50,
        S70,
        Mifare_DESFire,
        Mifare_Pro,
        Mifare_ProX,
        Mifare_plus,
        Mifare_plus_4k,
        Mifare_plus_2k,
        Mifare_Mini,
        Unknow;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static TagType[] valuesCustom() {
            TagType[] valuesCustom = values();
            int length = valuesCustom.length;
            TagType[] tagTypeArr = new TagType[length];
            System.arraycopy(valuesCustom, 0, tagTypeArr, 0, length);
            return tagTypeArr;
        }
    }

    @Override // com.rscja.deviceapi.b
    public /* bridge */ /* synthetic */ boolean free() {
        return super.free();
    }

    @Override // com.rscja.deviceapi.b
    public /* bridge */ /* synthetic */ String getVersion() {
        return super.getVersion();
    }

    @Override // com.rscja.deviceapi.b
    public /* bridge */ /* synthetic */ boolean init() {
        return super.init();
    }

    protected RFIDWithISO14443A() throws ConfigurationException {
    }

    public static synchronized RFIDWithISO14443A getInstance() throws ConfigurationException {
        RFIDWithISO14443A rFIDWithISO14443A;
        synchronized (RFIDWithISO14443A.class) {
            if (a == null) {
                a = new RFIDWithISO14443A();
            }
            rFIDWithISO14443A = a;
        }
        return rFIDWithISO14443A;
    }

    public enum DESFireFileTypekEnum {
        Unknown((byte) 0),
        StandardDataFile((byte) 1),
        BackupDataFile((byte) 2),
        ValueFile((byte) 3),
        LinearRecordFile((byte) 4),
        CyclicRecordFile((byte) 5);

        private final byte a;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static DESFireFileTypekEnum[] valuesCustom() {
            DESFireFileTypekEnum[] valuesCustom = values();
            int length = valuesCustom.length;
            DESFireFileTypekEnum[] dESFireFileTypekEnumArr = new DESFireFileTypekEnum[length];
            System.arraycopy(valuesCustom, 0, dESFireFileTypekEnumArr, 0, length);
            return dESFireFileTypekEnumArr;
        }

        public final byte getValue() {
            return this.a;
        }

        DESFireFileTypekEnum(byte value) {
            this.a = value;
        }
    }

    public enum DESFireEncryptionTypekEnum {
        Unknown((byte) 0),
        Transparent((byte) 1),
        DES((byte) 2);

        private final byte a;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static DESFireEncryptionTypekEnum[] valuesCustom() {
            DESFireEncryptionTypekEnum[] valuesCustom = values();
            int length = valuesCustom.length;
            DESFireEncryptionTypekEnum[] dESFireEncryptionTypekEnumArr = new DESFireEncryptionTypekEnum[length];
            System.arraycopy(valuesCustom, 0, dESFireEncryptionTypekEnumArr, 0, length);
            return dESFireEncryptionTypekEnumArr;
        }

        public final byte getValue() {
            return this.a;
        }

        DESFireEncryptionTypekEnum(byte value) {
            this.a = value;
        }
    }

    public synchronized SimpleRFIDEntity request() {
        byte[] ISO14443A_request = getDeviceAPI().ISO14443A_request(this.config.i(), 1);
        if (ISO14443A_request != null && ISO14443A_request[0] == 0 && ISO14443A_request[4] != 0) {
            int i = ISO14443A_request[4];
            byte[] bArr = new byte[i];
            TagType tagType = TagType.Unknow;
            TagType tagType2 = TagType.Unknow;
            for (int i2 = 0; i2 < i; i2++) {
                bArr[i2] = ISO14443A_request[i2 + 5];
            }
            byte b = ISO14443A_request[i + 5];
            int i3 = (b >> 2) & 1;
            int i4 = (b >> 3) & 1;
            int i5 = (b >> 4) & 1;
            int i6 = (b >> 5) & 1;
            if (ISO14443A_request[2] != 68 || ISO14443A_request[3] != 0) {
                if (ISO14443A_request[2] != 4 || ISO14443A_request[3] != 0) {
                    if (ISO14443A_request[2] != 2 || ISO14443A_request[3] != 0) {
                        if (ISO14443A_request[2] == 68 && ISO14443A_request[3] == 3) {
                            if (i6 == 1 || ((i6 == 1 && i3 == 1) || b == 0)) {
                                tagType = TagType.Mifare_DESFire;
                            }
                        } else {
                            tagType = (ISO14443A_request[2] == 8 && ISO14443A_request[3] == 0) ? TagType.Mifare_Pro : (ISO14443A_request[2] == 4 && ISO14443A_request[3] == 3) ? TagType.Mifare_ProX : TagType.Unknow;
                        }
                    } else if ((i4 == 1 && i5 == 1) || b == 0) {
                        tagType = TagType.S70;
                    }
                } else if (i4 == 1 || b == 0) {
                    tagType = TagType.S50;
                }
            } else {
                if (ISO14443A_request[ISO14443A_request[1] + 1] == 0) {
                    tagType2 = TagType.Ultra_light;
                } else {
                    tagType2 = TagType.Mifare_plus;
                }
                tagType = b == 0 ? TagType.Ultra_light : TagType.Mifare_plus;
            }
            return new SimpleRFIDEntity(StringUtility.bytes2HexString(bArr, i), (tagType2 != tagType && tagType2 != TagType.Unknow) ? String.valueOf(tagType2.toString()) + " or " + tagType.toString() : tagType.toString());
        }
        Log.e("RFIDWithISO1443A", "request() err:" + ((int) ISO14443A_request[0]));
        return null;
    }

    /* JADX WARN: Code restructure failed: missing block: B:31:0x006b, code lost:
    
        r0.setData(com.rscja.utility.StringUtility.chars2HexString(r1, r1.length));
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public synchronized com.rscja.deviceapi.entity.SimpleRFIDEntity read(java.lang.String r3, com.rscja.deviceapi.RFIDWithISO14443A.KeyType r4, int r5, int r6) throws com.rscja.deviceapi.exception.RFIDVerificationException, com.rscja.deviceapi.exception.RFIDReadFailureException {
        /*
            r2 = this;
            monitor-enter(r2)
            com.rscja.deviceapi.entity.SimpleRFIDEntity r0 = r2.request()     // Catch: java.lang.Throwable -> L7b
            r1 = 0
            if (r0 != 0) goto La
            monitor-exit(r2)
            return r1
        La:
            boolean r3 = r2.a(r5, r3, r4)     // Catch: java.lang.Throwable -> L7b
            if (r3 == 0) goto L75
        L12:
            if (r5 < 0) goto L68
            r3 = 40
            if (r5 <= r3) goto L19
            goto L68
        L19:
            r4 = 31
            if (r5 <= r4) goto L26
            if (r5 >= r3) goto L26
            int r5 = r5 + (-32)
            int r3 = r5 << 4
            int r3 = r3 + 128
            goto L28
        L26:
            int r3 = r5 << 2
        L28:
            com.rscja.deviceapi.DeviceAPI r4 = r2.getDeviceAPI()     // Catch: java.lang.Throwable -> L7b
            int r3 = r3 + r6
            char[] r3 = r4.ISO14443A_read(r3)     // Catch: java.lang.Throwable -> L7b
            r4 = 0
            char r5 = r3[r4]     // Catch: java.lang.Throwable -> L7b
            if (r5 != 0) goto L49
            r5 = 1
            char r6 = r3[r5]     // Catch: java.lang.Throwable -> L7b
            char[] r1 = new char[r6]     // Catch: java.lang.Throwable -> L7b
        L3b:
            char r6 = r3[r5]     // Catch: java.lang.Throwable -> L7b
            if (r4 < r6) goto L40
            goto L68
        L40:
            int r6 = r4 + 2
            char r6 = r3[r6]     // Catch: java.lang.Throwable -> L7b
            r1[r4] = r6     // Catch: java.lang.Throwable -> L7b
            int r4 = r4 + 1
            goto L3b
        L49:
            java.lang.String r5 = "RFIDWithISO1443A"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L7b
            java.lang.String r0 = "M1_ReadData() err:"
            r6.<init>(r0)     // Catch: java.lang.Throwable -> L7b
            char r3 = r3[r4]     // Catch: java.lang.Throwable -> L7b
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch: java.lang.Throwable -> L7b
            r6.append(r3)     // Catch: java.lang.Throwable -> L7b
            java.lang.String r3 = r6.toString()     // Catch: java.lang.Throwable -> L7b
            android.util.Log.e(r5, r3)     // Catch: java.lang.Throwable -> L7b
            com.rscja.deviceapi.exception.RFIDReadFailureException r3 = new com.rscja.deviceapi.exception.RFIDReadFailureException     // Catch: java.lang.Throwable -> L7b
            r3.<init>()     // Catch: java.lang.Throwable -> L7b
            throw r3     // Catch: java.lang.Throwable -> L7b
        L68:
            if (r1 == 0) goto L73
        L6b:
            int r3 = r1.length     // Catch: java.lang.Throwable -> L7b
            java.lang.String r3 = com.rscja.utility.StringUtility.chars2HexString(r1, r3)     // Catch: java.lang.Throwable -> L7b
            r0.setData(r3)     // Catch: java.lang.Throwable -> L7b
        L73:
            monitor-exit(r2)
            return r0
        L75:
            com.rscja.deviceapi.exception.RFIDVerificationException r3 = new com.rscja.deviceapi.exception.RFIDVerificationException     // Catch: java.lang.Throwable -> L7b
            r3.<init>()     // Catch: java.lang.Throwable -> L7b
            throw r3     // Catch: java.lang.Throwable -> L7b
        L7b:
            r3 = move-exception
            monitor-exit(r2)
            goto L7f
        L7e:
            throw r3
        L7f:
            goto L7e
        */
        throw new UnsupportedOperationException("Method not decompiled: com.rscja.deviceapi.RFIDWithISO14443A.read(java.lang.String, com.rscja.deviceapi.RFIDWithISO14443A$KeyType, int, int):com.rscja.deviceapi.entity.SimpleRFIDEntity");
    }

    public synchronized SimpleRFIDEntity read(int block) throws RFIDReadFailureException {
        SimpleRFIDEntity request = request();
        if (request == null) {
            return null;
        }
        char[] ISO14443A_ul_read = getDeviceAPI().ISO14443A_ul_read(block);
        if (ISO14443A_ul_read[0] != 0) {
            Log.e("RFIDWithISO1443A", "M1_ReadData() err:" + ISO14443A_ul_read[0]);
            throw new RFIDReadFailureException();
        }
        char c = ISO14443A_ul_read[1];
        char[] cArr = new char[c];
        for (int i = 0; i < ISO14443A_ul_read[1]; i++) {
            cArr[i] = ISO14443A_ul_read[i + 2];
        }
        request.setData(StringUtility.chars2HexString(cArr, c));
        return request;
    }

    public synchronized boolean write(String key, KeyType keyType, int sector, int block, String hexData) throws RFIDVerificationException, RFIDNotFoundException {
        if (StringUtility.isEmpty(hexData)) {
            return false;
        }
        request();
        if (!a(sector, key, keyType)) {
            throw new RFIDVerificationException();
        }
        if (sector >= 0 && sector <= 40) {
            char[] hexString2Chars = StringUtility.hexString2Chars(hexData);
            int length = hexString2Chars.length > 16 ? 16 : hexString2Chars.length;
            char[] cArr = new char[16];
            for (int i = 0; i < 16; i++) {
                cArr[i] = 0;
            }
            for (int i2 = 0; i2 < length; i2++) {
                cArr[i2] = hexString2Chars[i2];
            }
            int ISO14443A_write = getDeviceAPI().ISO14443A_write(((sector <= 31 || sector >= 40) ? sector << 2 : ((sector - 32) << 4) + 128) + block, cArr, 16);
            if (ISO14443A_write == 0) {
                return true;
            }
            Log.e("RFIDWithISO1443A", "M1_WriteData() err:" + ISO14443A_write);
        }
        return false;
    }

    public synchronized boolean write(int block, String hexData) throws RFIDNotFoundException {
        if (StringUtility.isEmpty(hexData)) {
            return false;
        }
        if (request() == null) {
            throw new RFIDNotFoundException();
        }
        char[] hexString2Chars = StringUtility.hexString2Chars(hexData);
        int length = hexString2Chars.length > 4 ? 4 : hexString2Chars.length;
        char[] cArr = new char[4];
        for (int i = 0; i < 4; i++) {
            cArr[i] = 0;
        }
        for (int i2 = 0; i2 < length; i2++) {
            cArr[i2] = hexString2Chars[i2];
        }
        int ISO14443A_ul_write = getDeviceAPI().ISO14443A_ul_write(block, cArr, 4);
        if (ISO14443A_ul_write == 0) {
            return true;
        }
        Log.e("RFIDWithISO1443A", "M1_WriteData() err:" + ISO14443A_ul_write);
        return false;
    }

    private synchronized boolean a(int i, String str, KeyType keyType) {
        int i2;
        if (i > 31 && i < 40) {
            i2 = ((i - 32) << 4) + 128;
        } else {
            i2 = i << 2;
        }
        int ISO14443A_authentication = getDeviceAPI().ISO14443A_authentication(keyType.ordinal(), i2, StringUtility.hexString2Chars(str), 6);
        if (ISO14443A_authentication == 0) {
            return true;
        }
        Log.e("RFIDWithISO1443A", "VerifySector() err:" + ISO14443A_authentication);
        return false;
    }

    public synchronized boolean DESFire_RatsAndPss() {
        int RF_ISO14443A_DESFIRE_RatPss = getDeviceAPI().RF_ISO14443A_DESFIRE_RatPss();
        if (RF_ISO14443A_DESFIRE_RatPss == 0) {
            return true;
        }
        Log.e("RFIDWithISO1443A", "DESFire_RatsAndPss() err:" + RF_ISO14443A_DESFIRE_RatPss);
        return false;
    }

    public synchronized int[] DESFire_GetKeySetting() {
        char[] RF_ISO14443A_DESFIRE_GetKeySetting = getDeviceAPI().RF_ISO14443A_DESFIRE_GetKeySetting();
        if (RF_ISO14443A_DESFIRE_GetKeySetting == null) {
            Log.e("RFIDWithISO1443A", "DESFire_GetKeySetting() err:result==null");
            return null;
        }
        if (RF_ISO14443A_DESFIRE_GetKeySetting[0] != 0) {
            Log.e("RFIDWithISO1443A", "DESFire_GetKeySetting() err:" + Character.getNumericValue(RF_ISO14443A_DESFIRE_GetKeySetting[0]));
            return null;
        }
        return new int[]{RF_ISO14443A_DESFIRE_GetKeySetting[2], RF_ISO14443A_DESFIRE_GetKeySetting[3]};
    }

    public synchronized boolean DESFire_ChangeKeySetting(int keySetting) {
        int keySetting2 = getDeviceAPI().RF_ISO14443A_DESFIRE_ChangeKeySetting(keySetting);
        if (keySetting2 == 0) {
            return true;
        }
        Log.e("RFIDWithISO1443A", "DESFire_ChangeKeySetting() err:" + keySetting2);
        return false;
    }

    public int getIntegerSomeBit(int resource, int mask) {
        return (resource >> mask) & 1;
    }

    public synchronized boolean DESFire_SelApp(String hexAppId) {
        if (StringUtility.isEmpty(hexAppId)) {
            Log.e("RFIDWithISO1443A", "DESFire_SelApp() err:hexAppId==null");
            return false;
        }
        int RF_ISO14443A_DESFIRE_SelApp = getDeviceAPI().RF_ISO14443A_DESFIRE_SelApp(StringUtility.hexString2Bytes(hexAppId));
        if (RF_ISO14443A_DESFIRE_SelApp == 0) {
            return true;
        }
        Log.e("RFIDWithISO1443A", "DESFire_SelApp() err:" + RF_ISO14443A_DESFIRE_SelApp);
        return false;
    }

    public synchronized boolean DESFire_Auth(int keyNo, String key) {
        if (StringUtility.isEmpty(key)) {
            Log.e("RFIDWithISO1443A", "DESFire_Auth() key==null");
            return false;
        }
        char[] hexString2Chars = StringUtility.hexString2Chars(key);
        Log.i("RFIDWithISO1443A", "DESFire_Auth() key:" + key);
        for (char c : hexString2Chars) {
            Log.i("RFIDWithISO1443A", "DESFire_Auth() arrChar:" + Character.getNumericValue(c));
        }
        int RF_ISO14443A_DESFIRE_Auth = getDeviceAPI().RF_ISO14443A_DESFIRE_Auth(keyNo, hexString2Chars, hexString2Chars.length);
        if (RF_ISO14443A_DESFIRE_Auth == 0) {
            return true;
        }
        Log.e("RFIDWithISO1443A", "DESFire_Auth() err:" + RF_ISO14443A_DESFIRE_Auth);
        return false;
    }

    public synchronized void DESFire_selCpy(int cpyType) {
        getDeviceAPI().RF_ISO14443A_DESFIRE_Cpysel(cpyType);
    }

    public synchronized boolean DESFire_ChangeKey(int keyNo, String newKey) {
        if (StringUtility.isEmpty(newKey)) {
            Log.e("RFIDWithISO1443A", "DESFire_ChangeKey() newKey==null");
            return false;
        }
        char[] hexString2Chars = StringUtility.hexString2Chars(newKey);
        int RF_ISO14443A_DESFIRE_ChangeKey = getDeviceAPI().RF_ISO14443A_DESFIRE_ChangeKey(keyNo, hexString2Chars, hexString2Chars.length);
        if (RF_ISO14443A_DESFIRE_ChangeKey == 0) {
            return true;
        }
        Log.e("RFIDWithISO1443A", "DESFire_ChangeKey() err:" + RF_ISO14443A_DESFIRE_ChangeKey);
        return false;
    }

    public synchronized String[] DESFire_GetApps() {
        byte[] RF_ISO14443A_DESFIRE_GetApps = getDeviceAPI().RF_ISO14443A_DESFIRE_GetApps();
        if (RF_ISO14443A_DESFIRE_GetApps == null) {
            Log.e("RFIDWithISO1443A", "DESFire_GetApps() err:result==null");
            return null;
        }
        if (RF_ISO14443A_DESFIRE_GetApps[0] != 0) {
            Log.e("RFIDWithISO1443A", "DESFire_GetApps() err:" + Integer.valueOf(RF_ISO14443A_DESFIRE_GetApps[0]));
            return null;
        }
        int i = RF_ISO14443A_DESFIRE_GetApps[2];
        String[] strArr = new String[i];
        for (int i2 = 0; i2 < i; i2++) {
            int i3 = i2 * 3;
            byte[] copyOfRange = Arrays.copyOfRange(RF_ISO14443A_DESFIRE_GetApps, i3 + 3, i3 + 6);
            strArr[i2] = StringUtility.bytes2HexString(copyOfRange, copyOfRange.length);
        }
        return strArr;
    }

    public synchronized boolean DESFire_DelApp(String hexAppId) {
        if (StringUtility.isEmpty(hexAppId)) {
            Log.e("RFIDWithISO1443A", "DESFire_DelApp() err:hexAppId==null");
            return false;
        }
        int RF_ISO14443A_DESFIRE_DelApp = getDeviceAPI().RF_ISO14443A_DESFIRE_DelApp(StringUtility.hexString2Chars(hexAppId));
        if (RF_ISO14443A_DESFIRE_DelApp == 0) {
            return true;
        }
        Log.e("RFIDWithISO1443A", "DESFire_DelApp() err:" + RF_ISO14443A_DESFIRE_DelApp);
        return false;
    }

    public synchronized boolean DESFire_FormatCard() {
        int RF_ISO14443A_DESFIRE_FormatCard = getDeviceAPI().RF_ISO14443A_DESFIRE_FormatCard();
        if (RF_ISO14443A_DESFIRE_FormatCard == 0) {
            return true;
        }
        Log.e("RFIDWithISO1443A", "DESFire_FormatCard() err:" + RF_ISO14443A_DESFIRE_FormatCard);
        return false;
    }

    public synchronized String[] DESFire_GetPiccInfo() {
        char[] RF_ISO14443A_DESFIRE_GetPiccInfo = getDeviceAPI().RF_ISO14443A_DESFIRE_GetPiccInfo();
        if (RF_ISO14443A_DESFIRE_GetPiccInfo == null) {
            Log.e("RFIDWithISO1443A", "DESFire_GetPiccInfo() err:result==null");
            return null;
        }
        if (RF_ISO14443A_DESFIRE_GetPiccInfo[0] != 0) {
            Log.e("RFIDWithISO1443A", "DESFire_GetPiccInfo() err:" + Character.getNumericValue(RF_ISO14443A_DESFIRE_GetPiccInfo[0]));
            return null;
        }
        char c = RF_ISO14443A_DESFIRE_GetPiccInfo[1];
        Log.i("RFIDWithISO1443A", "DESFire_GetPiccInfo() len=" + ((int) c));
        char[] copyOfRange = Arrays.copyOfRange(RF_ISO14443A_DESFIRE_GetPiccInfo, 2, c + 2);
        for (int i = 0; i < copyOfRange.length; i++) {
            Log.i("RFIDWithISO1443A", "DESFire_GetPiccInfo() result[" + i + "]=" + StringUtility.char2HexString(copyOfRange[i]));
        }
        char c2 = copyOfRange[0];
        int i2 = c2 + 1;
        char c3 = copyOfRange[i2];
        int i3 = c2 + c3 + 2;
        char c4 = copyOfRange[i3];
        Log.i("RFIDWithISO1443A", "DESFire_GetPiccInfo() infoLenPart1=" + ((int) c2) + " infoLenPart2=" + ((int) c3) + " infoLenPart3=" + ((int) c4));
        char[] copyOfRange2 = Arrays.copyOfRange(copyOfRange, 1, i2);
        char[] copyOfRange3 = Arrays.copyOfRange(copyOfRange, c2 + 2, i3);
        char[] copyOfRange4 = Arrays.copyOfRange(copyOfRange, c2 + 3 + c3, c4 + c2 + c3 + 3);
        String[] strArr = {StringUtility.chars2HexString(copyOfRange2, copyOfRange2.length), StringUtility.chars2HexString(copyOfRange3, copyOfRange3.length), StringUtility.chars2HexString(copyOfRange4, copyOfRange4.length)};
        Log.i("RFIDWithISO1443A", "DESFire_GetPiccInfo() infos[0]=" + strArr[0]);
        Log.i("RFIDWithISO1443A", "DESFire_GetPiccInfo() infos[1]=" + strArr[1]);
        Log.i("RFIDWithISO1443A", "DESFire_GetPiccInfo() infos[2]=" + strArr[2]);
        return strArr;
    }

    public synchronized boolean DESFire_AddApp(String hexAppId, int keySetting, int fileNums) {
        if (StringUtility.isEmpty(hexAppId)) {
            Log.e("RFIDWithISO1443A", "DESFire_AddApp() err:hexAppId==null");
            return false;
        }
        int RF_ISO14443A_DESFIRE_AddApp = getDeviceAPI().RF_ISO14443A_DESFIRE_AddApp(StringUtility.hexString2Chars(hexAppId), keySetting, fileNums);
        if (RF_ISO14443A_DESFIRE_AddApp == 0) {
            return true;
        }
        Log.e("RFIDWithISO1443A", "DESFire_AddApp() err:" + RF_ISO14443A_DESFIRE_AddApp);
        return false;
    }

    public synchronized int[] DESFire_GetFileIds() {
        byte[] RF_ISO14443A_DESFIRE_GetFileIds = getDeviceAPI().RF_ISO14443A_DESFIRE_GetFileIds();
        if (RF_ISO14443A_DESFIRE_GetFileIds == null) {
            Log.e("RFIDWithISO1443A", "DESFire_GetFileIds() err:result==null");
            return null;
        }
        if (RF_ISO14443A_DESFIRE_GetFileIds[0] != 0) {
            Log.e("RFIDWithISO1443A", "DESFire_GetFileIds() err:" + ((int) RF_ISO14443A_DESFIRE_GetFileIds[0]));
            return null;
        }
        int i = RF_ISO14443A_DESFIRE_GetFileIds[2];
        int[] iArr = new int[i];
        for (int i2 = 0; i2 < i; i2++) {
            iArr[i2] = RF_ISO14443A_DESFIRE_GetFileIds[i2 + 3];
        }
        return iArr;
    }

    public synchronized List<DESFireFile> DESFire_GetFiles() {
        DESFireFileTypekEnum dESFireFileTypekEnum;
        DESFireEncryptionTypekEnum dESFireEncryptionTypekEnum;
        DESFireFile dESFireFile;
        DESFireFile dESFireFile2;
        int[] DESFire_GetFileIds = DESFire_GetFileIds();
        if (DESFire_GetFileIds == null) {
            Log.e("RFIDWithISO1443A", "DESFire_GetFiles() ids==null");
            return null;
        }
        ArrayList arrayList = new ArrayList();
        int length = DESFire_GetFileIds.length;
        int i = 0;
        int i2 = 0;
        while (i2 < length) {
            int i3 = DESFire_GetFileIds[i2];
            byte[] DESFire_GetFileSetting = DESFire_GetFileSetting(i3);
            if (DESFire_GetFileSetting == null) {
                dESFireFile2 = new DESFireFile(i3, DESFireFileTypekEnum.Unknown, DESFireEncryptionTypekEnum.Unknown, null, null, null, null);
            } else {
                Log.i("RFIDWithISO1443A", "DESFire_GetFiles() setting[0]=" + ((int) DESFire_GetFileSetting[i]));
                byte b = DESFire_GetFileSetting[i];
                if (b == 0) {
                    dESFireFileTypekEnum = DESFireFileTypekEnum.StandardDataFile;
                } else if (b == 1) {
                    dESFireFileTypekEnum = DESFireFileTypekEnum.BackupDataFile;
                } else if (b == 2) {
                    dESFireFileTypekEnum = DESFireFileTypekEnum.ValueFile;
                } else if (b == 3) {
                    dESFireFileTypekEnum = DESFireFileTypekEnum.LinearRecordFile;
                } else if (b == 4) {
                    dESFireFileTypekEnum = DESFireFileTypekEnum.CyclicRecordFile;
                } else {
                    dESFireFileTypekEnum = DESFireFileTypekEnum.Unknown;
                }
                byte b2 = DESFire_GetFileSetting[1];
                if (b2 == 0) {
                    dESFireEncryptionTypekEnum = DESFireEncryptionTypekEnum.Transparent;
                } else if (b2 == 3) {
                    dESFireEncryptionTypekEnum = DESFireEncryptionTypekEnum.DES;
                } else {
                    dESFireEncryptionTypekEnum = DESFireEncryptionTypekEnum.Unknown;
                }
                String byte2HexString = StringUtility.byte2HexString(DESFire_GetFileSetting[2]);
                String byte2HexString2 = StringUtility.byte2HexString(DESFire_GetFileSetting[3]);
                DESFireFileTypekEnum dESFireFileTypekEnum2 = dESFireFileTypekEnum;
                DESFireFile dESFireFile3 = new DESFireFile(i3, dESFireFileTypekEnum, dESFireEncryptionTypekEnum, byte2HexString2.substring(i, 1), byte2HexString2.substring(1, 2), byte2HexString.substring(i, 1), byte2HexString.substring(1, 2));
                if (dESFireFileTypekEnum2 == DESFireFileTypekEnum.StandardDataFile) {
                    dESFireFile = dESFireFile3;
                } else if (dESFireFileTypekEnum2 == DESFireFileTypekEnum.BackupDataFile) {
                    dESFireFile = dESFireFile3;
                } else {
                    if (dESFireFileTypekEnum2 != DESFireFileTypekEnum.ValueFile) {
                        dESFireFile = dESFireFile3;
                    } else {
                        int bytesToInt = StringUtility.bytesToInt(new byte[]{DESFire_GetFileSetting[4], DESFire_GetFileSetting[5], DESFire_GetFileSetting[6], DESFire_GetFileSetting[7]});
                        dESFireFile = dESFireFile3;
                        dESFireFile.setMaxValue(StringUtility.bytesToInt(new byte[]{DESFire_GetFileSetting[8], DESFire_GetFileSetting[9], DESFire_GetFileSetting[10], DESFire_GetFileSetting[11]}));
                        dESFireFile.setMinValue(bytesToInt);
                    }
                    dESFireFile2 = dESFireFile;
                }
                dESFireFile.setFileSize(StringUtility.bytesToInt(new byte[]{DESFire_GetFileSetting[4], DESFire_GetFileSetting[5], DESFire_GetFileSetting[6], 0}));
                dESFireFile2 = dESFireFile;
            }
            arrayList.add(dESFireFile2);
            i2++;
            i = 0;
        }
        return arrayList;
    }

    public synchronized byte[] DESFire_GetFileSetting(int fileNo) {
        byte[] RF_ISO14443A_DESFIRE_GetFileSetting = getDeviceAPI().RF_ISO14443A_DESFIRE_GetFileSetting(fileNo);
        if (RF_ISO14443A_DESFIRE_GetFileSetting == null) {
            Log.e("RFIDWithISO1443A", "DESFire_GetFileSetting() err:result==null");
            return null;
        }
        if (RF_ISO14443A_DESFIRE_GetFileSetting[0] != 0) {
            Log.e("RFIDWithISO1443A", "DESFire_GetFileSetting() err:" + ((int) RF_ISO14443A_DESFIRE_GetFileSetting[0]));
            return null;
        }
        int i = RF_ISO14443A_DESFIRE_GetFileSetting[1];
        byte[] bArr = new byte[i];
        for (int i2 = 0; i2 < i; i2++) {
            bArr[i2] = RF_ISO14443A_DESFIRE_GetFileSetting[i2 + 2];
            Log.i("RFIDWithISO1443A", "DESFire_GetFileSetting() ids[i]=" + ((int) bArr[i2]));
        }
        return bArr;
    }

    public synchronized boolean DESFire_ChangeFileSetting(int fileNo, int commSet, char[] accessRights) {
        int fileNo2 = getDeviceAPI().RF_ISO14443A_DESFIRE_ChangeFileSetting(fileNo, commSet, accessRights);
        if (fileNo2 == 0) {
            return true;
        }
        Log.e("RFIDWithISO1443A", "DESFire_ChangeFileSetting() err:" + fileNo2);
        return false;
    }

    public synchronized boolean DESFire_DelFile(int fileNo) {
        int fileNo2 = getDeviceAPI().RF_ISO14443A_DESFIRE_DelFile(fileNo);
        if (fileNo2 == 0) {
            return true;
        }
        Log.e("RFIDWithISO1443A", "DESFire_DelFile() err:" + fileNo2);
        return false;
    }

    public synchronized boolean DESFire_AddStdFile(int fileNo, int commSet, char[] accessRight, int fileSize) {
        int RF_ISO14443A_DESFIRE_AddStdFile = getDeviceAPI().RF_ISO14443A_DESFIRE_AddStdFile(fileNo, commSet, accessRight, fileSize);
        if (RF_ISO14443A_DESFIRE_AddStdFile == 0) {
            return true;
        }
        Log.e("RFIDWithISO1443A", "DESFire_AddStdFile() err:" + RF_ISO14443A_DESFIRE_AddStdFile);
        return false;
    }

    public synchronized boolean DESFire_WriteStdFile(int fileNo, int offSet, int dataSize, char[] dataBuf) {
        int RF_ISO14443A_DESFIRE_WriteStdFile = getDeviceAPI().RF_ISO14443A_DESFIRE_WriteStdFile(fileNo, offSet, dataSize, dataBuf);
        if (RF_ISO14443A_DESFIRE_WriteStdFile == 0) {
            return true;
        }
        Log.e("RFIDWithISO1443A", "DESFire_WriteStdFile() err:" + RF_ISO14443A_DESFIRE_WriteStdFile);
        return false;
    }

    public synchronized char[] DESFire_ReadStdFile(int fileNo, int offSet, int dataSize) {
        char[] RF_ISO14443A_DESFIRE_ReadStdFile = getDeviceAPI().RF_ISO14443A_DESFIRE_ReadStdFile(fileNo, offSet, dataSize);
        if (RF_ISO14443A_DESFIRE_ReadStdFile == null) {
            Log.e("RFIDWithISO1443A", "DESFire_ReadStdFile() err:result==null");
            return null;
        }
        if (RF_ISO14443A_DESFIRE_ReadStdFile[0] != 0) {
            Log.e("RFIDWithISO1443A", "DESFire_ReadStdFile() err:" + Character.getNumericValue(RF_ISO14443A_DESFIRE_ReadStdFile[0]));
            return null;
        }
        return Arrays.copyOfRange(RF_ISO14443A_DESFIRE_ReadStdFile, 2, RF_ISO14443A_DESFIRE_ReadStdFile[1] + 2);
    }

    public synchronized boolean DESFire_AddValueFile(int fileNo, int commSet, char[] accessRights, int minValue, int maxValue, int initValue) {
        int RF_ISO14443A_DESFIRE_AddValueFile = getDeviceAPI().RF_ISO14443A_DESFIRE_AddValueFile(fileNo, commSet, accessRights, minValue, maxValue, initValue);
        if (RF_ISO14443A_DESFIRE_AddValueFile == 0) {
            return true;
        }
        Log.e("RFIDWithISO1443A", "DESFire_AddValueFile() err:" + RF_ISO14443A_DESFIRE_AddValueFile);
        return false;
    }

    public synchronized int[] DESFire_ReadValueFile(int fileNo) {
        int[] RF_ISO14443A_DESFIRE_GetValueFile = getDeviceAPI().RF_ISO14443A_DESFIRE_GetValueFile(fileNo);
        if (RF_ISO14443A_DESFIRE_GetValueFile == null) {
            Log.e("RFIDWithISO1443A", "DESFire_ReadValueFile() err:result==null");
            return null;
        }
        if (RF_ISO14443A_DESFIRE_GetValueFile[0] != 0) {
            Log.e("RFIDWithISO1443A", "DESFire_ReadValueFile() err:" + Character.getNumericValue(RF_ISO14443A_DESFIRE_GetValueFile[0]));
            return null;
        }
        return new int[]{RF_ISO14443A_DESFIRE_GetValueFile[2]};
    }

    public synchronized boolean DESFire_CreditValueFile(int fileNo, int value) {
        int RF_ISO14443A_DESFIRE_CreditValueFile = getDeviceAPI().RF_ISO14443A_DESFIRE_CreditValueFile(fileNo, value);
        if (RF_ISO14443A_DESFIRE_CreditValueFile == 0) {
            return true;
        }
        Log.e("RFIDWithISO1443A", "DESFire_CreditValueFile() err:" + RF_ISO14443A_DESFIRE_CreditValueFile);
        return false;
    }

    public synchronized boolean DESFire_DebitValueFile(int fileNo, int value) {
        int RF_ISO14443A_DESFIRE_DebitValueFile = getDeviceAPI().RF_ISO14443A_DESFIRE_DebitValueFile(fileNo, value);
        if (RF_ISO14443A_DESFIRE_DebitValueFile == 0) {
            return true;
        }
        Log.e("RFIDWithISO1443A", "DESFire_DebitValueFile() err:" + RF_ISO14443A_DESFIRE_DebitValueFile);
        return false;
    }
}
