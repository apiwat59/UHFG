package cn.com.example.rfid.driver;

/* loaded from: classes.dex */
public class RfidDriver extends Driver {
    private static boolean loadinglib;

    private native int CloseNet();

    private native int CloseUart();

    private native int DownLoad(String str, String str2);

    private native String GetAntStatus();

    private native String GetDataFromBuf();

    private native int GetLoss();

    private native String GetRFIDAccessControlEAS();

    private native String GetRFIDAccessControlNumberOfPeople();

    private native String GetRFIDAccessControlTriggerPara();

    private native String GetRFIDAntenna();

    private native String GetRFIDCWStatus();

    private native int GetRFIDDualSingelStatus();

    private native float GetRFIDEnvRssi();

    private native int GetRFIDFastIDStatus();

    private native String GetRFIDFreqTable();

    private native int GetRFIDGPIOStatus(int i);

    private native String GetRFIDGen2Para();

    private native String GetRFIDInventoryBankDataTogether();

    private native int GetRFIDLinkCombination();

    private native int GetRFIDQuerWorkingMode();

    private native int GetRFIDTagFocusStatus();

    private native String GetRFIDWhiteList(int i);

    private native String GetScanWorkWaitTime();

    private native float GetTemp();

    private native int GetTempProt();

    private native String GetUm7Fw();

    private native String GetUm7Hw();

    private native String GetUm7ModuleID();

    private native String GetUm7Region();

    private native String InventorySingle(int i);

    private native int Kill(String str, int i, int i2, int i3, String str2);

    private native int LockMemory(String str, int i, int i2, int i3, String str2, int i4, int i5);

    private native String NetInit(String str, int i);

    private native int RFIDAuthenticate(String str, int i, int i2, int i3, String str2, int i4, int i5, int i6, int i7, int i8, String str3);

    private native int RFIDBlockEraseMemory(String str, int i, int i2, int i3, String str2, int i4, int i5, int i6);

    private native int RFIDBlockPerMalock(String str, int i, int i2, int i3, String str2, int i4, int i5, int i6, int i7, String str3, int i8);

    private native int RFIDBlockWriteMemory(String str, int i, int i2, int i3, String str2, int i4, int i5, int i6, String str3);

    private native int RFIDGetQTPara(String str, int i, int i2, int i3, String str2);

    private native String RFIDQTReadMemory(String str, int i, int i2, int i3, String str2, int i4, int i5, int i6, int i7);

    private native int RFIDQTWriteMemory(String str, int i, int i2, int i3, String str2, int i4, int i5, int i6, int i7, String str3);

    private native int RFIDSetQTPara(String str, int i, int i2, int i3, String str2, int i4);

    private native String RFIDTransmissionCMD(byte[] bArr, int i);

    private native int RFIDUntraceable(String str, int i, int i2, int i3, String str2, int i4, int i5, int i6, int i7, int i8, int i9);

    private native int RFIDWriteEPCRFUUSRAndLock(byte[] bArr, int i);

    private native String Read_Data(String str, int i, int i2, int i3, String str2, int i4, int i5, int i6);

    private native int SetRFIDAccessControlEAS(int i, int i2, int i3, byte[] bArr, byte[] bArr2, int i4);

    private native int SetRFIDAccessControlNumberOfPeople(int i, int i2, int i3);

    private native int SetRFIDAccessControlTriggerPara(int i, int i2, int i3, int i4);

    private native int SetRFIDAntenna(int[] iArr, int i, int i2);

    private native int SetRFIDBuzzerRing(int i);

    private native int SetRFIDCWStatus(int i);

    private native int SetRFIDCommunicationBaud(int i);

    private native int SetRFIDDualSingelStatus(int i, int i2);

    private native int SetRFIDFastIDStatus(int i);

    private native int SetRFIDFreqTable(int i, int i2, int[] iArr);

    private native int SetRFIDGPIOStatus(int i, int i2, int i3);

    private native int SetRFIDGen2Para(int i, int[] iArr);

    private native int SetRFIDInventoryBankDataTogether(int i, int i2, int i3, int i4);

    private native int SetRFIDInventoryFilter(int i, int i2, int i3, String str, int i4);

    private native int SetRFIDLinkCombination(int i, int i2);

    private native String SetRFIDNetPara(byte[] bArr, int i);

    private native int SetRFIDQuerWorkingMode(int i, boolean z);

    private native int SetRFIDRestoreFactory();

    private native int SetRFIDSoftReset();

    private native String SetRFIDSubCommand(byte[] bArr, int i);

    private native int SetRFIDTagFocusStatus(int i);

    private native int SetRFIDWhiteList(int i, int i2, byte[] bArr);

    private native String SetRFIDWiFiPara(byte[] bArr, int i);

    private native int SetScanWorkWaitTime(int i, int i2, boolean z);

    private native int SetTempProt(int i);

    private native int SetUm7Region(int i);

    private native void StopContinueRead();

    private native int Uartinit(String str, int i);

    private native int Write_Data(String str, int i, int i2, int i3, String str2, int i4, int i5, int i6, String str3);

    private native int Write_Epc(String str, int i, int i2, String str2);

    private native String getTxPower();

    private native int readmore(int i);

    private native int setRFIDTxPower(int i, int i2, int i3, int i4);

    private native int unLockMemory(String str, int i, int i2, int i3, String str2, int i4, int i5);

    static {
        loadinglib = false;
        try {
            System.loadLibrary("jni_rfid_driver");
            loadinglib = true;
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int initRFID(String dev, int baudrate) {
        if (!loadinglib) {
            return -1;
        }
        return Uartinit(dev, baudrate);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int SetTxPower(int rdp, int wtp, int antid, int saveflag) {
        return setRFIDTxPower(rdp, wtp, antid, saveflag);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public String readUM7hwOnce() {
        return GetUm7Hw();
    }

    @Override // cn.com.example.rfid.driver.Driver
    public String readUM7fwOnce() {
        return GetUm7Fw();
    }

    @Override // cn.com.example.rfid.driver.Driver
    public String GetModuleID() {
        return GetUm7ModuleID();
    }

    @Override // cn.com.example.rfid.driver.Driver
    public String GetTxPower() {
        return getTxPower();
    }

    @Override // cn.com.example.rfid.driver.Driver
    public String GetFreqTable() {
        return GetRFIDFreqTable();
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int SetFreqTable(int save, int num, int[] freqlist) {
        return SetRFIDFreqTable(save, num, freqlist);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public String GetGen2Para() {
        return GetRFIDGen2Para();
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int SetGen2Para(int save, int[] gen2list) {
        return SetRFIDGen2Para(save, gen2list);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int SetCWStatus(int status) {
        return SetRFIDCWStatus(status);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public String GetCWStatus() {
        return GetRFIDCWStatus();
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int Set_Ant_More(int[] ant, int len, int save) {
        int[] temp = new int[100];
        for (int i = 0; i < len; i++) {
            temp[i] = ant[i];
        }
        int i2 = SetRFIDAntenna(temp, len, save);
        return i2;
    }

    @Override // cn.com.example.rfid.driver.Driver
    public String Get_Ant() {
        return GetRFIDAntenna();
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int readMore(int times) {
        return readmore(times);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public String getRssiOnce() {
        return Float.toString(GetRFIDEnvRssi());
    }

    @Override // cn.com.example.rfid.driver.Driver
    public String getRegion() {
        return GetUm7Region();
    }

    @Override // cn.com.example.rfid.driver.Driver
    public String GetBufData() {
        return GetDataFromBuf();
    }

    @Override // cn.com.example.rfid.driver.Driver
    public void stopRead() {
        StopContinueRead();
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int SetRegion(int data) {
        return SetUm7Region(data);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public String Net_Init(String ip, int port) {
        return NetInit(ip, port);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int GetAntLoss() {
        return GetLoss();
    }

    @Override // cn.com.example.rfid.driver.Driver
    public String Get_AntStatus() {
        return GetAntStatus();
    }

    @Override // cn.com.example.rfid.driver.Driver
    public float Get_Temp() {
        return GetTemp();
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int Set_TmpProt(int val) {
        return SetTempProt(val);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int Get_TmpProt() {
        return GetTempProt();
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int SetGPIOStatus(int mask, int val, int save) {
        return SetRFIDGPIOStatus(mask, val, save);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int GetGPIOStatus(int mask) {
        return GetRFIDGPIOStatus(mask);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int SetLinkCombination(int value, int save) {
        return SetRFIDLinkCombination(value, save);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int GetLinkCombination() {
        return GetRFIDLinkCombination();
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int SetFastIDStatus(int value) {
        return SetRFIDFastIDStatus(value);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int GetFastIDStatus() {
        return GetRFIDFastIDStatus();
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int SetTagFocusStatus(int value) {
        return SetRFIDTagFocusStatus(value);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int GetTagFocusStatus() {
        return GetRFIDTagFocusStatus();
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int Down_LoadFw(String filePath, String fileName) {
        return DownLoad(filePath, fileName);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int SetCommunicationBaud(int value) {
        return SetRFIDCommunicationBaud(value);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int Reset() {
        return SetRFIDSoftReset();
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int RestoreFactory() {
        return SetRFIDRestoreFactory();
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int Close_Com() {
        return CloseUart();
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int Close_Net() {
        return CloseNet();
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int Write_Epc_Data(String pwd, int ads, int len, String EpcData) {
        return Write_Epc(pwd, ads, len, EpcData);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int Set_Filter_Data(int bank, int ads, int len, String data, int save) {
        return SetRFIDInventoryFilter(bank, ads, len, data, save);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int Lock_Tag_Data(String pwd, int bank, int ads, int len, String EpcData, int bankvalue, int locktype) {
        return LockMemory(pwd, bank, ads, len, EpcData, bankvalue, locktype);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int unLock_Tag_Data(String pwd, int bank, int ads, int len, String EpcData, int bankvalue, int locktype) {
        return unLockMemory(pwd, bank, ads, len, EpcData, bankvalue, locktype);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int Write_Data_Tag(String pwd, int bank, int ads, int len, String data, int bank1, int ads1, int len1, String data1) {
        return Write_Data(pwd, bank, ads, len, data, bank1, ads1, len1, data1);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public String Read_Data_Tag(String pwd, int bank, int ads, int len, String data, int bank1, int ads1, int len1) {
        return Read_Data(pwd, bank, ads, len, data, bank1, ads1, len1);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int Kill_Tag(String pwd, int bank, int ads, int len, String data) {
        return Kill(pwd, bank, ads, len, data);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int BlockWriteMemory(String pwd, int bank, int ads, int len, String data, int bank1, int ads1, int len1, String data1) {
        return RFIDBlockWriteMemory(pwd, bank, ads, len, data, bank1, ads1, len1, data1);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int BlockEraseMemory(String pwd, int bank, int ads, int len, String data, int bank1, int ads1, int len1) {
        return RFIDBlockEraseMemory(pwd, bank, ads, len, data, bank1, ads1, len1);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int SetQTPara(String pwd, int bank, int ads, int len, String data, int qtvalue) {
        return RFIDSetQTPara(pwd, bank, ads, len, data, qtvalue);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int GetQTPara(String pwd, int bank, int ads, int len, String data) {
        return RFIDGetQTPara(pwd, bank, ads, len, data);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public String QTReadMemory(String pwd, int bank, int ads, int len, String data, int qtvalue, int bank1, int ads1, int len1) {
        return RFIDQTReadMemory(pwd, bank, ads, len, data, qtvalue, bank1, ads1, len1);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int QTWriteMemory(String pwd, int bank, int ads, int len, String data, int qtvalue, int bank1, int ads1, int len1, String data1) {
        return RFIDQTWriteMemory(pwd, bank, ads, len, data, qtvalue, bank1, ads1, len1, data1);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int BlockPerMalock(String pwd, int bank, int ads, int len, String data, int readlock, int mb, int blockPtr, int blockRange, String mask, int maskbytelen) {
        return RFIDBlockPerMalock(pwd, bank, ads, len, data, readlock, mb, blockPtr, blockRange, mask, maskbytelen);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int Untraceable(String pwd, int bank, int ads, int len, String data, int rfu, int u, int epc, int tid, int usr, int range) {
        return RFIDUntraceable(pwd, bank, ads, len, data, rfu, u, epc, tid, usr, range);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int Authenticate(String pwd, int bank, int ads, int len, String data, int rfu, int senrep, int increplen, int csi, int msgbitlen, String message) {
        return RFIDAuthenticate(pwd, bank, ads, len, data, rfu, senrep, increplen, csi, msgbitlen, message);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int WriteEPCRFUUSRAndLock(byte[] value, int bytelen) {
        return RFIDWriteEPCRFUUSRAndLock(value, bytelen);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public boolean Read_Tag_Mode_Set(int Model, int startaddr, int wordcnt, int save) {
        int res = SetRFIDInventoryBankDataTogether(Model, startaddr, wordcnt, save);
        if (1 == res) {
            return true;
        }
        return false;
    }

    @Override // cn.com.example.rfid.driver.Driver
    public String Read_Tag_Mode_Get() {
        return GetRFIDInventoryBankDataTogether();
    }

    @Override // cn.com.example.rfid.driver.Driver
    public boolean Inventory_Model_Set(int Model, boolean save) {
        int res = SetRFIDQuerWorkingMode(Model, save);
        if (1 == res) {
            return true;
        }
        return false;
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int Inventory_Model_Get() {
        return GetRFIDQuerWorkingMode();
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int SetWhiteList(int index, int totalbits, byte[] input) {
        return SetRFIDWhiteList(index, totalbits, input);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public String GetWhiteList(int index) {
        return GetRFIDWhiteList(index);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int SetAccessControlEAS(int enableEAS, int enableAlarm, int totalbits, byte[] mask, byte[] match, int alarmTime) {
        return SetRFIDAccessControlEAS(enableEAS, enableAlarm, totalbits, mask, match, alarmTime);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public String GetAccessControlEAS() {
        return GetRFIDAccessControlEAS();
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int SetAccessControlTriggerPara(int enable, int delayStopTime, int in1, int in2) {
        return SetRFIDAccessControlTriggerPara(enable, delayStopTime, in1, in2);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public String GetAccessControlTriggerPara() {
        return GetRFIDAccessControlTriggerPara();
    }

    @Override // cn.com.example.rfid.driver.Driver
    public int SetAccessControlNumberOfPeople(int InNum, int OutNum, int RemainNum) {
        return SetRFIDAccessControlNumberOfPeople(InNum, OutNum, RemainNum);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public String GetAccessControlNumberOfPeople() {
        return GetRFIDAccessControlNumberOfPeople();
    }

    @Override // cn.com.example.rfid.driver.Driver
    public String SingleRead(int value) {
        String temp = InventorySingle(value);
        if (temp.length() < 8) {
            return "获取失败";
        }
        String epctmp = temp.substring(4);
        String len = temp.substring(0, 2);
        int epclen = (Integer.parseInt(len, 16) / 8) * 4;
        String epc = epctmp.substring(0, epclen);
        return epc;
    }

    @Override // cn.com.example.rfid.driver.Driver
    public boolean ScanWaitTime_Set(int ScanTime, int WaitTime, boolean save) {
        return 1 == SetScanWorkWaitTime(ScanTime, WaitTime, save);
    }

    @Override // cn.com.example.rfid.driver.Driver
    public String ScanWaitTime_Get() {
        return GetScanWorkWaitTime();
    }

    @Override // cn.com.example.rfid.driver.Driver
    public String TransmissionCMD(byte[] frameData, int bytelen) {
        return RFIDTransmissionCMD(frameData, bytelen);
    }
}
