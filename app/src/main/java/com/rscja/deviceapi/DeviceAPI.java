package com.rscja.deviceapi;

import java.io.FileDescriptor;

/* loaded from: classes.dex */
final class DeviceAPI {
    private static DeviceAPI a = null;
    public FileDescriptor mFd;

    public final native int Barcode_1D_Close(String str);

    public final native int Barcode_1D_Open(String str, String str2, int i);

    public final native byte[] Barcode_1D_Scan(String str);

    public final native int Barcode_2D_Close(String str);

    public final native int Barcode_2D_Open(String str, String str2, int i);

    public final native byte[] Barcode_2D_Scan(String str);

    public final native char[] EM125K_GetEm4450UID();

    public final native char[] EM125k_Read4305(int i);

    public final native char[] EM125k_ReadHitag(int i);

    public final native char[] EM125k_UID_REQ();

    public final native int EM125k_Write4305(int i, char[] cArr);

    public final native int EM125k_WriteHitagPage(int i, char[] cArr);

    public final native int EM125k_free(String str);

    public final native int EM125k_init(String str, String str2, int i);

    public final native int EM125k_init_Ex(String str, String str2, int i);

    public final native char[] EM125k_read(int i);

    public final native char[] EM125k_read_Ex();

    public final native int[] EMAutoEnroll(int i, int i2);

    public final native int[] EMAutoMatch(int i, int i2, int i3);

    public final native int EMDeletChar(int i, int i2);

    public final native int EMDownChar(int i, char[] cArr);

    public final native int EMEmpty();

    public final native int EMFingerFree(String str);

    public final native int EMFingerInit(String str, String str2, int i);

    public final native int EMGenChar(int i);

    public final native int EMGetImage();

    public final native char[] EMGetRandomData();

    public final native int EMLoadChar(int i, int i2);

    public final native int[] EMMatch();

    public final native char[] EMReadChipSN();

    public final native char[] EMReadSysPara();

    public final native int EMRegModel();

    public final native int[] EMSearch(int i, int i2, int i3);

    public final native int EMSetDeviceName(char[] cArr);

    public final native int EMSetManuFacture(char[] cArr);

    public final native int EMSetReg(int i, int i2);

    public final native int EMStorChar(int i, int i2);

    public final native char[] EMUpChar(int i);

    public final native int[] EMUpImage(int i, String str);

    public final native int[] EMValidTempleteNum();

    public final native int HID_GetUid();

    public final native char[] HardwareVersion_125k();

    public final native int ISO14443A_authentication(int i, int i2, char[] cArr, int i3);

    public final native char[] ISO14443A_cpu_command(char[] cArr, int i);

    public final native char[] ISO14443A_cpu_rats();

    public final native char[] ISO14443A_cpu_reset();

    public final native char[] ISO14443A_read(int i);

    public final native byte[] ISO14443A_request(String str, int i);

    public final native char[] ISO14443A_ul_read(int i);

    public final native int ISO14443A_ul_write(int i, char[] cArr, int i2);

    public final native int ISO14443A_write(int i, char[] cArr, int i2);

    public final native char[] ISO14443B_cpu_command(char[] cArr, int i);

    public final native char[] ISO14443B_cpu_reset();

    public final native char[] ISO15693_getSystemInformation(int i, char[] cArr, int i2);

    public final native char[] ISO15693_inventory(int i, int i2);

    public final native int ISO15693_lockAFI(int i, char[] cArr, int i2);

    public final native int ISO15693_lockDSFID(int i, char[] cArr, int i2);

    public final native char[] ISO15693_read_sm(int i, char[] cArr, int i2, int i3, int i4);

    public final native int ISO15693_writeAFI(int i, char[] cArr, int i2, int i3);

    public final native int ISO15693_writeDSFID(int i, char[] cArr, int i2, int i3);

    public final native int ISO15693_write_sm(int i, char[] cArr, int i2, int i3, int i4, char[] cArr2, int i5);

    public final native int LedOff(String str, int i);

    public final native int LedOn(String str, int i);

    public final native int ModulePowerOff(String str, int i);

    public final native int ModulePowerOn(String str, int i);

    public final native byte[] Psam_Cmd(String str, char c, char[] cArr, int i);

    public final native int Psam_Free(String str);

    public final native int Psam_Init(String str);

    public final native byte[] RFID_GetVer();

    public final native int RFID_free(String str);

    public final native int RFID_init(String str, String str2, int i);

    public final native int RF_ISO14443A_DESFIRE_AddApp(char[] cArr, int i, int i2);

    public final native int RF_ISO14443A_DESFIRE_AddStdFile(int i, int i2, char[] cArr, int i3);

    public final native int RF_ISO14443A_DESFIRE_AddValueFile(int i, int i2, char[] cArr, int i3, int i4, int i5);

    public final native int RF_ISO14443A_DESFIRE_Auth(int i, char[] cArr, int i2);

    public final native int RF_ISO14443A_DESFIRE_ChangeFileSetting(int i, int i2, char[] cArr);

    public final native int RF_ISO14443A_DESFIRE_ChangeKey(int i, char[] cArr, int i2);

    public final native int RF_ISO14443A_DESFIRE_ChangeKeySetting(int i);

    public final native void RF_ISO14443A_DESFIRE_Cpysel(int i);

    public final native int RF_ISO14443A_DESFIRE_CreditValueFile(int i, int i2);

    public final native int RF_ISO14443A_DESFIRE_DebitValueFile(int i, int i2);

    public final native int RF_ISO14443A_DESFIRE_DelApp(char[] cArr);

    public final native int RF_ISO14443A_DESFIRE_DelFile(int i);

    public final native int RF_ISO14443A_DESFIRE_FormatCard();

    public final native byte[] RF_ISO14443A_DESFIRE_GetApps();

    public final native byte[] RF_ISO14443A_DESFIRE_GetFileIds();

    public final native byte[] RF_ISO14443A_DESFIRE_GetFileSetting(int i);

    public final native char[] RF_ISO14443A_DESFIRE_GetKeySetting();

    public final native char[] RF_ISO14443A_DESFIRE_GetPiccInfo();

    public final native int[] RF_ISO14443A_DESFIRE_GetValueFile(int i);

    public final native int RF_ISO14443A_DESFIRE_RatPss();

    public final native char[] RF_ISO14443A_DESFIRE_ReadStdFile(int i, int i2, int i3);

    public final native int RF_ISO14443A_DESFIRE_SelApp(byte[] bArr);

    public final native int RF_ISO14443A_DESFIRE_WriteStdFile(int i, int i2, int i3, char[] cArr);

    public final native void UHFCloseAndDisconnect();

    public final native int UHFEraseData(char[] cArr, char c, int i, char c2, char[] cArr2);

    public final native char[] UHFEraseDataSingle(char[] cArr, char c, int i, char c2);

    public final native void UHFFlafCrcOff();

    public final native void UHFFlagCrcOn();

    public final native int UHFFree();

    public final native char[] UHFGetFrequency_Ex();

    public final native char[] UHFGetHwType();

    public final native char[] UHFGetPower();

    public final native int[] UHFGetPwm();

    public final native char[] UHFGetReceived();

    public final native char[] UHFGetSingelMode();

    public final native int UHFInit();

    public final native int UHFInventory(char c, char c2);

    public final native char[] UHFInventorySingle();

    public final native int UHFKillTag(char[] cArr, char[] cArr2);

    public final native char[] UHFKillTagSingle(char[] cArr);

    public final native int UHFLockMem(char[] cArr, char[] cArr2, char[] cArr3);

    public final native char[] UHFLockMemSingle(char[] cArr, char[] cArr2);

    public final native int UHFOpenAndConnect(String str);

    public final native char[] UHFReadData(char[] cArr, char c, int i, char c2, char[] cArr2);

    public final native char[] UHFReadDataSingle(char[] cArr, char c, int i, char c2);

    public final native int UHFSetFrequency_EX(char c);

    public final native int UHFSetPower(char c);

    public final native int UHFSetPwm(int i, int i2);

    public final native int UHFSetSingelMode(char c);

    public final native int UHFStopGet();

    public final native int UHFWriteData(char[] cArr, char c, int i, char c2, char[] cArr2, char[] cArr3);

    public final native char[] UHFWriteDataSingle(char[] cArr, char c, int i, char c2, char[] cArr2);

    public final native int UartSwitch(String str, int i);

    public final native int bdOff(String str);

    public final native int bdOn(String str);

    public final native void spClose();

    public final native FileDescriptor spOpen(String str, int i, int i2);

    private DeviceAPI() {
    }

    static {
        System.loadLibrary("DeviceAPI");
    }

    public static synchronized DeviceAPI a() {
        DeviceAPI deviceAPI;
        synchronized (DeviceAPI.class) {
            if (a == null) {
                a = new DeviceAPI();
            }
            deviceAPI = a;
        }
        return deviceAPI;
    }
}
