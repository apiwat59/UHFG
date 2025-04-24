package com.rfid.trans;

import android.content.Context;
import android.media.SoundPool;
import android.os.SystemClock;
import cn.pda.serialport.SerialPort;
import com.gg.reader.api.protocol.gx.EnumG;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import kotlin.jvm.internal.ByteCompanionObject;

/* loaded from: classes.dex */
public class ReaderHelp implements CReader {
    public static volatile boolean isSound = false;
    private TagCallback callback;
    private SerialPort mSerialPort;
    private BaseReader reader = new BaseReader();
    private ReaderParameter param = new ReaderParameter();
    private volatile boolean mWorking = true;
    private volatile Thread mThread = null;
    private volatile boolean soundworking = true;
    private volatile Thread sThread = null;
    private byte[] pOUcharIDList = new byte[25600];
    private volatile int NoCardCOunt = 0;
    private Integer soundid = null;
    private SoundPool soundPool = null;
    private boolean isOpen = false;
    private String devName = "";
    private int logswitch = 0;
    private int RF_Ctrl = 5;
    private int Cur_Ctrl = 5;
    public int ModuleType = 0;
    private int ReTryCount = 0;
    private boolean PermitControl = false;
    long beginTime = 0;
    private int Cfg_Power = 0;
    private List<MaskClass> MaskList = new ArrayList();
    private byte MatchType = 0;
    byte CurSession = 0;
    int CurPower = 0;
    boolean firstTime = true;
    private byte Target = 0;
    private byte QValue = 4;
    private int Session = 1;
    private int CardCount = 0;
    private int ReadSpeed = 0;
    private int maskIndex = 0;
    volatile boolean isfinish = false;
    byte[] szbuff = null;
    Thread nThread = null;
    String RecvStr = "";

    public ReaderHelp() {
        this.param.ComAddr = (byte) -1;
        this.param.IvtType = 0;
        this.param.Memory = 2;
        this.param.Password = "00000000";
        this.param.ScanTime = 50;
        this.param.Session = 1;
        this.param.Target = 0;
        this.param.QValue = 6;
        this.param.WordPtr = 0;
        this.param.Length = 6;
        this.param.Antenna = 128;
        this.param.Interval = 0;
        this.param.MaskLen = (byte) 0;
    }

    public void AddMaskList(MaskClass maskTag) {
        this.MaskList.add(maskTag);
    }

    public void ClearMaskList() {
        this.MaskList.clear();
        this.param.MaskLen = (byte) 0;
        this.reader.SetInventoryMatchData(this.param.ComAddr, (byte) 0, null);
    }

    public void SetMatchType(byte match) {
        if (match > 1) {
            match = 1;
        }
        this.MatchType = match;
    }

    @Override // com.rfid.trans.CReader
    public boolean isConnect() {
        return this.isOpen;
    }

    private void open() {
        try {
            SerialPort serialPort = new SerialPort();
            this.mSerialPort = serialPort;
            serialPort.power_5Von();
            try {
                Thread.sleep(10L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                SerialPort serialPort2 = new SerialPort(13, 115200, 0);
                this.mSerialPort = serialPort2;
                serialPort2.close(13);
            } catch (Exception e2) {
            }
            try {
                Thread.sleep(10L);
            } catch (InterruptedException e3) {
                e3.printStackTrace();
            }
        } catch (Exception e4) {
        }
    }

    private void close() {
        SerialPort mSerialPort = new SerialPort();
        mSerialPort.power_5Voff();
    }

    @Override // com.rfid.trans.CReader
    public void PowerControll(Context mContext, boolean enable) {
        if (enable) {
            open();
        } else {
            close();
        }
    }

    @Override // com.rfid.trans.CReader
    public int Connect(String ComPort, int BaudRate, int logswitch) {
        int result = this.reader.Connect(ComPort, BaudRate, logswitch);
        if (result == 0) {
            this.devName = ComPort;
            return InitReaderApi();
        }
        return result;
    }

    private int InitReaderApi() {
        SystemClock.sleep(20L);
        byte[] Version = new byte[2];
        byte[] Power = new byte[1];
        byte[] band = new byte[1];
        byte[] MaxFre = new byte[1];
        byte[] MinFre = new byte[1];
        int result = GetReaderInformation(Version, Power, band, MaxFre, MinFre);
        if (result != 0) {
            this.reader.DisConnect();
            return result;
        }
        byte[] ctrl = {0};
        if (this.ModuleType == 2 && (result = this.reader.OperateControl(this.param.ComAddr, ctrl)) == 0) {
            this.RF_Ctrl = ctrl[0];
            this.Cur_Ctrl = ctrl[0];
        }
        this.logswitch = this.logswitch;
        this.isOpen = true;
        isSound = false;
        this.soundworking = true;
        if (this.sThread == null) {
            this.sThread = new Thread(new Runnable() { // from class: com.rfid.trans.ReaderHelp.1
                @Override // java.lang.Runnable
                public void run() {
                    while (ReaderHelp.this.soundworking) {
                        if (ReaderHelp.isSound && ReaderHelp.this.mWorking) {
                            ReaderHelp.this.playSound();
                            SystemClock.sleep(50L);
                        }
                    }
                    ReaderHelp.this.sThread = null;
                }
            });
            this.sThread.start();
        }
        return result;
    }

    @Override // com.rfid.trans.CReader
    public int DisConnect() {
        try {
            isSound = false;
            this.soundworking = false;
            this.mWorking = false;
            this.isOpen = false;
            Thread.sleep(100L);
        } catch (Exception e) {
        }
        return this.reader.DisConnect();
    }

    @Override // com.rfid.trans.CReader
    public int GetReaderInformation(byte[] Version, byte[] Power, byte[] band, byte[] MaxFre, byte[] MinFre) {
        byte[] ReaderType = new byte[1];
        byte[] TrType = new byte[1];
        byte[] OutputRep = new byte[1];
        byte[] CheckAnt = new byte[1];
        byte[] Ant = new byte[1];
        byte[] BeepEn = new byte[1];
        byte[] ComAddr = {-1};
        byte[] ScanTime = new byte[1];
        this.ModuleType = 0;
        int result = this.reader.GetReaderInformation(ComAddr, Version, ReaderType, TrType, band, MaxFre, MinFre, Power, ScanTime, Ant, BeepEn, OutputRep, CheckAnt);
        if (result == 0) {
            this.Cfg_Power = Power[0];
            this.param.ComAddr = ComAddr[0];
            if ((ReaderType[0] & 255) == 112 || (ReaderType[0] & 255) == 113 || (ReaderType[0] & 255) == 49) {
                this.ModuleType = 2;
            } else if ((ReaderType[0] & 255) == 15 || (ReaderType[0] & 255) == 16 || (ReaderType[0] & 255) == 80 || (ReaderType[0] & 255) == 81 || (ReaderType[0] & 255) == 82) {
                this.ModuleType = 1;
            }
        }
        return result;
    }

    public int GetReaderType() {
        byte[] Version = new byte[2];
        byte[] Power = new byte[1];
        byte[] band = new byte[1];
        byte[] MaxFre = new byte[1];
        byte[] MinFre = new byte[1];
        byte[] ReaderType = new byte[1];
        byte[] TrType = new byte[1];
        byte[] OutputRep = new byte[1];
        byte[] CheckAnt = new byte[1];
        byte[] Ant = new byte[1];
        byte[] BeepEn = new byte[1];
        byte[] ComAddr = {-1};
        byte[] ScanTime = new byte[1];
        this.ModuleType = 0;
        int result = this.reader.GetReaderInformation(ComAddr, Version, ReaderType, TrType, band, MaxFre, MinFre, Power, ScanTime, Ant, BeepEn, OutputRep, CheckAnt);
        if (result != 0) {
            return -1;
        }
        this.Cfg_Power = Power[0];
        this.param.ComAddr = ComAddr[0];
        if ((ReaderType[0] & 255) == 112 || (ReaderType[0] & 255) == 113 || (ReaderType[0] & 255) == 49) {
            this.ModuleType = 2;
        } else if ((ReaderType[0] & 255) == 15 || (ReaderType[0] & 255) == 16 || (ReaderType[0] & 255) == 80 || (ReaderType[0] & 255) == 81 || (ReaderType[0] & 255) == 82) {
            this.ModuleType = 1;
        }
        return ReaderType[0] & 255;
    }

    @Override // com.rfid.trans.CReader
    public int SetRfPower(byte Power) {
        int result = this.reader.SetRfPower(this.param.ComAddr, Power);
        if (result == 0) {
            this.Cfg_Power = Power;
        }
        return result;
    }

    @Override // com.rfid.trans.CReader
    public int SetRegion(byte band, byte maxfre, byte minfre) {
        return this.reader.SetRegion(this.param.ComAddr, band, maxfre, minfre);
    }

    public int SetRegion(int opt, int band, int maxfre, int minfre) {
        return this.reader.SetRegion(this.param.ComAddr, opt, band, maxfre, minfre);
    }

    @Override // com.rfid.trans.CReader
    public int SetGPIO(byte GPIO) {
        return this.reader.SetGPIO(this.param.ComAddr, GPIO);
    }

    @Override // com.rfid.trans.CReader
    public int GetGPIOStatus(byte[] OutputPin) {
        if (OutputPin.length < 1) {
            return 255;
        }
        return this.reader.GetGPIOStatus(this.param.ComAddr, OutputPin);
    }

    @Override // com.rfid.trans.CReader
    public String GetDeviceID() {
        byte[] btArr = new byte[4];
        int result = this.reader.GetDeviceID(this.param.ComAddr, btArr);
        if (result == 0) {
            String temp = this.reader.bytesToHexString(btArr, 0, btArr.length);
            return temp;
        }
        return null;
    }

    @Override // com.rfid.trans.CReader
    public int SetWritePower(byte WritePower) {
        return this.reader.SetWritePower(this.param.ComAddr, WritePower);
    }

    @Override // com.rfid.trans.CReader
    public int GetWritePower(byte[] WritePower) {
        if (WritePower.length < 1) {
            return 255;
        }
        return this.reader.GetWritePower(this.param.ComAddr, WritePower);
    }

    @Override // com.rfid.trans.CReader
    public int SetRetryTimes(byte times) {
        byte[] nTimes = {(byte) (times | ByteCompanionObject.MIN_VALUE)};
        return this.reader.RetryTimes(this.param.ComAddr, nTimes);
    }

    @Override // com.rfid.trans.CReader
    public int GetRetryTimes(byte[] times) {
        if (times.length < 1) {
            return 255;
        }
        times[0] = 0;
        return this.reader.RetryTimes(this.param.ComAddr, times);
    }

    @Override // com.rfid.trans.CReader
    public void SetSoundID(int Soundid, SoundPool soundPool) {
        this.soundid = Integer.valueOf(Soundid);
        this.soundPool = soundPool;
    }

    @Override // com.rfid.trans.CReader
    public String GetRFIDTempreture() {
        byte[] temp = new byte[2];
        int result = this.reader.MeasureTemperature(this.param.ComAddr, temp);
        if (result != 0) {
            return null;
        }
        String tempstr = "";
        if (temp[0] == 0) {
            tempstr = "-";
        }
        return tempstr + ((int) temp[1]);
    }

    @Override // com.rfid.trans.CReader
    public int SetProfile(byte b) {
        byte[] profile = {(byte) (b | ByteCompanionObject.MIN_VALUE)};
        int result = this.reader.SetProfile(this.param.ComAddr, profile);
        if (result == 0) {
            this.RF_Ctrl = profile[0];
            this.Cur_Ctrl = profile[0];
        }
        return result;
    }

    @Override // com.rfid.trans.CReader
    public int GetProfile(byte[] bytes) {
        if (bytes.length < 1) {
            return 255;
        }
        bytes[0] = 0;
        this.reader.SetProfile(this.param.ComAddr, bytes);
        return 0;
    }

    @Override // com.rfid.trans.CReader
    public void SetMessageBack(RFIDLogCallBack rfidLogCallBack) {
        this.reader.SetMsgCallBack(rfidLogCallBack);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playSound() {
        SoundPool soundPool;
        Integer num = this.soundid;
        if (num == null || (soundPool = this.soundPool) == null) {
            return;
        }
        try {
            soundPool.play(num.intValue(), 1.0f, 1.0f, 1, 0, 1.0f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.rfid.trans.CReader
    public void SetCallBack(TagCallback callback) {
        this.callback = callback;
        this.reader.SetCallBack(callback);
    }

    @Override // com.rfid.trans.CReader
    public void SetInventoryPatameter(ReaderParameter param) {
        this.param = param;
    }

    @Override // com.rfid.trans.CReader
    public ReaderParameter GetInventoryPatameter() {
        return this.param;
    }

    @Override // com.rfid.trans.CReader
    public int StartRead() {
        if (this.mThread == null) {
            this.CurSession = (byte) this.param.Session;
            this.PermitControl = false;
            if (this.ModuleType == 2) {
                this.reader.SetInventoryMatchData(this.param.ComAddr, this.MatchType, this.MaskList);
                if (this.param.Session == 254 || this.param.Session == 253 || this.param.Session == 252 || this.param.Session == 251) {
                    if (this.Session == 254) {
                        this.Session = 253;
                        this.CurSession = (byte) 2;
                    } else if (this.param.Session == 251) {
                        this.Session = 1;
                        this.CurSession = (byte) 1;
                    } else {
                        this.Session = 254;
                        this.CurSession = (byte) 3;
                    }
                    this.PermitControl = true;
                } else {
                    int i = this.param.Session;
                    this.Session = i;
                    this.CurSession = (byte) i;
                    if (i == 1) {
                        this.PermitControl = true;
                    }
                }
            } else {
                int i2 = this.param.Session;
                this.Session = i2;
                this.CurSession = (byte) i2;
            }
            if ((this.param.Session > 0 && this.param.Session < 4 && this.param.Target > 1) || this.param.Session > 3) {
                this.reader.SelectCMDByTime(this.param.ComAddr, ByteCompanionObject.MIN_VALUE, this.CurSession, (byte) 0, (byte) 0, (byte) 0);
                this.reader.SelectCMDByTime(this.param.ComAddr, ByteCompanionObject.MIN_VALUE, this.CurSession, (byte) 0, (byte) 0, (byte) 0);
                this.reader.SelectCMDByTime(this.param.ComAddr, ByteCompanionObject.MIN_VALUE, this.CurSession, (byte) 0, (byte) 0, (byte) 0);
            }
            if (this.ModuleType == 2) {
                if (this.param.Session == 252 || this.param.Session == 251) {
                    this.reader.SetRegionTable(this.param.ComAddr, (byte) 1);
                } else {
                    this.reader.SetRegionTable(this.param.ComAddr, (byte) 0);
                }
                byte[] ctrl = new byte[1];
                if (!this.PermitControl) {
                    ctrl[0] = (byte) (this.RF_Ctrl | 192);
                } else {
                    if (this.param.Session == 254) {
                        ctrl[0] = -59;
                    } else if (this.param.Session == 253) {
                        ctrl[0] = -63;
                    } else if (this.param.Session == 251 || this.param.Session == 252) {
                        ctrl[0] = -13;
                    }
                    this.firstTime = true;
                }
                int result = this.reader.OperateControl(this.param.ComAddr, ctrl);
                if (result == 0) {
                    this.Cur_Ctrl = ctrl[0];
                }
            }
            this.maskIndex = 0;
            this.mWorking = true;
            this.mThread = new Thread(new Runnable() { // from class: com.rfid.trans.ReaderHelp.2
                @Override // java.lang.Runnable
                public void run() {
                    ReaderHelp readerHelp = ReaderHelp.this;
                    readerHelp.Target = (byte) readerHelp.param.Target;
                    if (ReaderHelp.this.Target > 1) {
                        ReaderHelp.this.Target = (byte) 0;
                    }
                    ReaderHelp readerHelp2 = ReaderHelp.this;
                    readerHelp2.QValue = (byte) readerHelp2.param.QValue;
                    while (ReaderHelp.this.mWorking) {
                        ReaderHelp.this.ReadRfid();
                        if (ReaderHelp.this.PermitControl && ReaderHelp.this.ModuleType == 2) {
                            if (ReaderHelp.this.param.Session != 253 || ReaderHelp.this.Cur_Ctrl != 1) {
                                if (ReaderHelp.this.param.Session != 252 || ReaderHelp.this.Cur_Ctrl != 51) {
                                    if (ReaderHelp.this.NoCardCOunt <= 0 || ReaderHelp.this.Cur_Ctrl != 5) {
                                        if (ReaderHelp.this.param.Session == 251 && ReaderHelp.this.Cur_Ctrl == 13) {
                                            byte[] ctrl2 = new byte[1];
                                            if (ReaderHelp.this.CardCount < 50) {
                                                if (ReaderHelp.this.CardCount > 10 && ReaderHelp.this.CardCount < 50) {
                                                    ctrl2[0] = -59;
                                                }
                                            } else {
                                                ctrl2[0] = -13;
                                            }
                                            int result2 = ReaderHelp.this.reader.OperateControl(ReaderHelp.this.param.ComAddr, ctrl2);
                                            if (result2 == 0) {
                                                ReaderHelp.this.Cur_Ctrl = ctrl2[0];
                                            }
                                        }
                                    } else {
                                        byte[] ctrl3 = {-51};
                                        int result3 = ReaderHelp.this.reader.OperateControl(ReaderHelp.this.param.ComAddr, ctrl3);
                                        if (result3 == 0) {
                                            ReaderHelp.this.Cur_Ctrl = ctrl3[0];
                                        }
                                    }
                                } else if (ReaderHelp.this.CardCount < 150 || ReaderHelp.this.ReadSpeed < 150) {
                                    if (!ReaderHelp.this.firstTime) {
                                        byte[] ctrl4 = {-59};
                                        int result4 = ReaderHelp.this.reader.OperateControl(ReaderHelp.this.param.ComAddr, ctrl4);
                                        if (result4 == 0) {
                                            ReaderHelp.this.Cur_Ctrl = ctrl4[0];
                                        }
                                    } else {
                                        ReaderHelp.this.firstTime = false;
                                    }
                                }
                            } else if (ReaderHelp.this.CardCount < 150 || ReaderHelp.this.ReadSpeed < 150) {
                                if (!ReaderHelp.this.firstTime) {
                                    byte[] ctrl5 = {-59};
                                    int result5 = ReaderHelp.this.reader.OperateControl(ReaderHelp.this.param.ComAddr, ctrl5);
                                    if (result5 == 0) {
                                        ReaderHelp.this.Cur_Ctrl = ctrl5[0];
                                    }
                                } else {
                                    ReaderHelp.this.firstTime = false;
                                }
                            }
                        }
                        if (ReaderHelp.this.ModuleType != 2) {
                            SystemClock.sleep(ReaderHelp.this.param.Interval * 10);
                        }
                    }
                    ReaderHelp.isSound = false;
                    if (ReaderHelp.this.CurSession > 3 || (ReaderHelp.this.CurSession > 0 && ReaderHelp.this.CurSession < 4 && ReaderHelp.this.param.Target > 1)) {
                        ReaderHelp.this.SelectBySession((byte) 2);
                        SystemClock.sleep(5L);
                        ReaderHelp.this.SelectBySession((byte) 3);
                    } else if (ReaderHelp.this.CurSession == 1 && ReaderHelp.this.PermitControl) {
                        ReaderHelp.this.SelectBySession((byte) 1);
                    }
                    if (ReaderHelp.this.ModuleType == 2 && ReaderHelp.this.PermitControl) {
                        byte[] ctrl6 = {(byte) (ReaderHelp.this.RF_Ctrl | 192)};
                        int result6 = ReaderHelp.this.reader.OperateControl(ReaderHelp.this.param.ComAddr, ctrl6);
                        if (result6 == 0) {
                            ReaderHelp.this.Cur_Ctrl = ctrl6[0];
                        }
                    }
                    ReaderHelp.this.mThread = null;
                    if (ReaderHelp.this.callback != null) {
                        ReaderHelp.this.callback.StopReadCallBack();
                    }
                }
            });
            this.mThread.start();
            return 0;
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void SelectBySession(byte session) {
        for (int m = 0; m < 8; m++) {
            this.reader.SelectCMDByTime(this.param.ComAddr, ByteCompanionObject.MIN_VALUE, session, (byte) 0, (byte) 0, (byte) 0);
            SystemClock.sleep(5L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v0 */
    /* JADX WARN: Type inference failed for: r3v14 */
    /* JADX WARN: Type inference failed for: r3v22 */
    /* JADX WARN: Type inference failed for: r3v26, types: [boolean, int] */
    /* JADX WARN: Type inference failed for: r3v27 */
    /* JADX WARN: Type inference failed for: r3v5 */
    /* JADX WARN: Type inference failed for: r3v6 */
    /* JADX WARN: Type inference failed for: r3v7 */
    /* JADX WARN: Type inference failed for: r3v9 */
    /* JADX WARN: Type inference failed for: r4v13 */
    /* JADX WARN: Type inference failed for: r4v19 */
    /* JADX WARN: Type inference failed for: r4v20 */
    /* JADX WARN: Type inference failed for: r4v21 */
    /* JADX WARN: Type inference failed for: r4v3 */
    /* JADX WARN: Type inference failed for: r4v4 */
    /* JADX WARN: Type inference failed for: r4v6 */
    /* JADX WARN: Type inference failed for: r4v9 */
    public void ReadRfid() {
        int[] iArr;
        int i;
        int i2;
        int i3;
        byte b;
        byte b2;
        byte b3;
        ?? r3 = 0;
        int[] iArr2 = {0};
        if (this.param.IvtType != 0) {
            if (this.param.IvtType == 1) {
                this.QValue = (byte) this.param.QValue;
                byte[] bArr = {(byte) (this.param.WordPtr >> 8), (byte) (this.param.WordPtr & 255)};
                if (this.param.Length == 0) {
                    this.param.Length = 6;
                }
                byte b4 = (byte) this.param.Length;
                byte[] hexStringToBytes = this.reader.hexStringToBytes(this.param.Password);
                if (this.param.Session == 255) {
                    this.Session = 0;
                }
                if (this.ModuleType == 1) {
                    ArrayList arrayList = new ArrayList();
                    iArr2[0] = 0;
                    if (this.Session == 255) {
                        b2 = 0;
                    } else {
                        b2 = (byte) this.param.ScanTime;
                    }
                    ArrayList arrayList2 = arrayList;
                    this.reader.Inventory_NoCallback(this.param.ComAddr, this.QValue, (byte) this.Session, (byte) 0, (byte) 0, this.Target, ByteCompanionObject.MIN_VALUE, b2, this.param.MaskMem, this.param.MaskAdr, (byte) 0, this.param.MaskData, arrayList2, iArr2);
                    isSound = false;
                    if (iArr2[0] > 0) {
                        int i4 = 0;
                        while (i4 < arrayList2.size()) {
                            ArrayList arrayList3 = arrayList2;
                            ReadTag readTag = (ReadTag) arrayList3.get(i4);
                            byte[] bArr2 = bArr;
                            byte b5 = b4;
                            int[] iArr3 = iArr2;
                            String ReadData_G2 = ReadData_G2(readTag.epcId, (byte) this.param.Memory, (byte) this.param.WordPtr, b5, this.param.Password);
                            if (ReadData_G2 != null && ReadData_G2.length() > 0) {
                                readTag.memId = ReadData_G2;
                                TagCallback tagCallback = this.callback;
                                if (tagCallback != null) {
                                    tagCallback.tagCallback(readTag);
                                }
                                playSound();
                            }
                            i4++;
                            b4 = b5;
                            iArr2 = iArr3;
                            bArr = bArr2;
                            arrayList2 = arrayList3;
                        }
                        iArr = iArr2;
                    } else {
                        iArr = iArr2;
                    }
                } else {
                    iArr = iArr2;
                    this.reader.Inventory_Mix(this.param.ComAddr, this.QValue, (byte) this.Session, this.param.MaskMem, this.param.MaskAdr, (byte) 0, this.param.MaskData, (byte) this.param.Memory, bArr, b4, hexStringToBytes, this.Target, ByteCompanionObject.MIN_VALUE, (byte) this.param.ScanTime, null, iArr);
                }
                i = 2;
                r3 = 0;
                i2 = 1;
                i3 = 3;
            } else {
                iArr = iArr2;
                if (this.param.IvtType == 2) {
                    this.QValue = (byte) this.param.QValue;
                    if (this.param.Length == 0) {
                        this.param.Length = 6;
                    }
                    byte b6 = (byte) this.param.Length;
                    if (this.param.Session == 255) {
                        this.Session = 0;
                    }
                    if (this.ModuleType != 1) {
                        iArr[0] = 0;
                        i = 2;
                        this.reader.Inventory_G2(this.param.ComAddr, (byte) (this.QValue | 32), (byte) this.Session, (byte) this.param.WordPtr, (byte) 0, this.Target, ByteCompanionObject.MIN_VALUE, (byte) 10, this.param.MaskMem, this.param.MaskAdr, this.param.MaskLen, this.param.MaskData, null, iArr);
                        if (this.Session == 0 && iArr[0] < 5) {
                            this.QValue = (byte) 2;
                        }
                        this.QValue = (byte) this.param.QValue;
                    } else {
                        ArrayList arrayList4 = new ArrayList();
                        iArr[0] = 0;
                        if (this.Session == 255) {
                            b = 0;
                        } else {
                            b = (byte) this.param.ScanTime;
                        }
                        this.reader.Inventory_NoCallback(this.param.ComAddr, this.QValue, (byte) this.Session, (byte) 0, (byte) 0, this.Target, ByteCompanionObject.MIN_VALUE, b, this.param.MaskMem, this.param.MaskAdr, (byte) 0, this.param.MaskData, arrayList4, iArr);
                        isSound = false;
                        if (iArr[0] > 0) {
                            int i5 = 0;
                            while (i5 < arrayList4.size()) {
                                ReadTag readTag2 = (ReadTag) arrayList4.get(i5);
                                ArrayList arrayList5 = arrayList4;
                                byte b7 = b6;
                                String ReadData_G22 = ReadData_G2(readTag2.epcId, (byte) this.param.Memory, (byte) this.param.WordPtr, b7, this.param.Password);
                                if (ReadData_G22 != null && ReadData_G22.length() > 0) {
                                    readTag2.memId = ReadData_G22;
                                    TagCallback tagCallback2 = this.callback;
                                    if (tagCallback2 != null) {
                                        tagCallback2.tagCallback(readTag2);
                                    }
                                    playSound();
                                }
                                i5++;
                                arrayList4 = arrayList5;
                                b6 = b7;
                            }
                        }
                        i = 2;
                    }
                    r3 = 0;
                    i2 = 1;
                    i3 = 3;
                } else if (this.param.IvtType == 3) {
                    this.reader.Inventory_GB(this.param.ComAddr, ByteCompanionObject.MIN_VALUE, (byte) 10, null, iArr);
                    i = 2;
                    r3 = 0;
                    i2 = 1;
                    i3 = 3;
                } else if (this.param.IvtType == 4) {
                    i2 = 1;
                    i = 2;
                    i3 = 3;
                    this.reader.Inventory_GJB(this.param.ComAddr, (byte) 0, ByteCompanionObject.MIN_VALUE, (byte) 10, null, iArr);
                    r3 = 0;
                } else {
                    i = 2;
                    i2 = 1;
                    i2 = 1;
                    i2 = 1;
                    i3 = 3;
                    if (this.param.IvtType != 5) {
                        r3 = 0;
                    } else {
                        byte[] bArr3 = new byte[256];
                        iArr[0] = 0;
                        r3 = 0;
                        r3 = 0;
                        this.reader.InventoryMutiple_6B(this.param.ComAddr, (byte) 1, (byte) 0, (byte) -1, new byte[]{0, 0, 0, 0, 0, 0, 0, 0}, bArr3, iArr);
                        if (iArr[0] > 0) {
                            for (int i6 = 0; i6 < iArr[0]; i6++) {
                                String bytesToHexString = this.reader.bytesToHexString(bArr3, (i6 * 10) + 1, 8);
                                byte b8 = bArr3[(i6 * 10) + 9];
                                ReadTag readTag3 = new ReadTag();
                                readTag3.epcId = bytesToHexString;
                                readTag3.memId = "";
                                readTag3.rssi = b8 & 255;
                                readTag3.phase = 0;
                                readTag3.antId = 1;
                            }
                            isSound = true;
                        }
                    }
                }
            }
        } else {
            iArr2[0] = 0;
            if (this.Session == 255) {
                b3 = 0;
            } else {
                b3 = (byte) this.param.ScanTime;
            }
            long currentTimeMillis = System.currentTimeMillis();
            this.CardCount = 0;
            this.ReadSpeed = 0;
            this.reader.Inventory_G2(this.param.ComAddr, this.QValue, (byte) this.Session, (byte) this.param.WordPtr, (byte) 0, this.Target, ByteCompanionObject.MIN_VALUE, b3, this.param.MaskMem, this.param.MaskAdr, (byte) 0, this.param.MaskData, null, iArr2);
            long currentTimeMillis2 = System.currentTimeMillis() - currentTimeMillis;
            this.CardCount = iArr2[0];
            if (currentTimeMillis2 > 0) {
                this.ReadSpeed = (int) ((r9 * 1000) / currentTimeMillis2);
            }
            iArr = iArr2;
            i = 2;
            i2 = 1;
            i3 = 3;
        }
        if (iArr[r3] == 0) {
            if (this.param.Session <= i2 || this.param.Session >= 255 || this.param.IvtType >= i) {
                this.NoCardCOunt += i2;
                if (this.NoCardCOunt > i) {
                    isSound = r3;
                    return;
                }
                return;
            }
            if (iArr[r3] == 0) {
                isSound = r3;
            }
            this.NoCardCOunt += i2;
            if (this.NoCardCOunt > i3) {
                if ((this.param.Target > i2 && (this.param.Session == i || this.param.Session == i3)) || (this.param.Session > i3 && this.param.Session < 255)) {
                    this.Target = (byte) (1 - this.Target);
                }
                this.NoCardCOunt = r3;
                if (this.PermitControl) {
                    byte[] bArr4 = new byte[i2];
                    if (this.param.Session == 254) {
                        bArr4[r3] = -59;
                    } else if (this.param.Session == 253) {
                        bArr4[r3] = -63;
                    } else if (this.param.Session == 252) {
                        bArr4[r3] = -13;
                    }
                    if (this.reader.OperateControl(this.param.ComAddr, bArr4) == 0) {
                        this.Cur_Ctrl = bArr4[r3];
                    }
                    this.firstTime = i2;
                    return;
                }
                return;
            }
            return;
        }
        this.NoCardCOunt = r3;
    }

    @Override // com.rfid.trans.CReader
    public void StopRead() {
        if (this.ModuleType == 2) {
            this.reader.StopInventory(this.param.ComAddr);
        }
        this.mWorking = false;
        isSound = false;
    }

    /* JADX WARN: Type inference failed for: r4v1 */
    /* JADX WARN: Type inference failed for: r4v10 */
    /* JADX WARN: Type inference failed for: r4v11 */
    /* JADX WARN: Type inference failed for: r4v12 */
    /* JADX WARN: Type inference failed for: r4v13 */
    /* JADX WARN: Type inference failed for: r4v2 */
    /* JADX WARN: Type inference failed for: r4v3 */
    /* JADX WARN: Type inference failed for: r4v7, types: [boolean] */
    /* JADX WARN: Type inference failed for: r4v8 */
    /* JADX WARN: Type inference failed for: r4v9 */
    @Override // com.rfid.trans.CReader
    public void ScanRfid() {
        int[] iArr;
        ?? r4;
        byte b;
        this.reader.SetInventoryMatchData(this.param.ComAddr, this.MatchType, this.MaskList);
        if (this.param.Session == 0) {
            this.Target = (byte) 0;
            this.NoCardCOunt = 0;
        } else if (this.param.Session == 1) {
            this.Target = (byte) 0;
        } else {
            byte b2 = (byte) this.param.Target;
            this.Target = b2;
            if (b2 > 1) {
                this.Target = (byte) 0;
            }
        }
        int[] iArr2 = new int[1];
        this.QValue = (byte) this.param.QValue;
        if (this.param.IvtType == 0) {
            iArr2[0] = 0;
            this.reader.Inventory_G2(this.param.ComAddr, this.QValue, (byte) this.param.Session, (byte) this.param.WordPtr, (byte) 0, this.Target, ByteCompanionObject.MIN_VALUE, (byte) 10, this.param.MaskMem, this.param.MaskAdr, this.param.MaskLen, this.param.MaskData, null, iArr2);
            iArr = iArr2;
            r4 = 0;
        } else if (this.param.IvtType == 1) {
            this.QValue = (byte) this.param.QValue;
            byte[] bArr = {(byte) (this.param.WordPtr >> 8), (byte) (this.param.WordPtr & 255)};
            if (this.param.Length == 0) {
                this.param.Length = 6;
            }
            byte b3 = (byte) this.param.Length;
            byte[] hexStringToBytes = this.reader.hexStringToBytes(this.param.Password);
            if (this.param.Session == 255) {
                this.Session = 0;
            }
            if (this.ModuleType == 1) {
                ArrayList arrayList = new ArrayList();
                iArr2[0] = 0;
                if (this.Session == 255) {
                    b = 0;
                } else {
                    b = (byte) this.param.ScanTime;
                }
                this.reader.Inventory_NoCallback(this.param.ComAddr, this.QValue, (byte) this.Session, (byte) 0, (byte) 0, this.Target, ByteCompanionObject.MIN_VALUE, b, this.param.MaskMem, this.param.MaskAdr, this.param.MaskLen, this.param.MaskData, arrayList, iArr2);
                isSound = false;
                if (iArr2[0] > 0) {
                    int i = 0;
                    while (i < arrayList.size()) {
                        ReadTag readTag = (ReadTag) arrayList.get(i);
                        byte[] bArr2 = bArr;
                        ArrayList arrayList2 = arrayList;
                        byte b4 = b3;
                        int[] iArr3 = iArr2;
                        String ReadData_G2 = ReadData_G2(readTag.epcId, (byte) this.param.Memory, (byte) this.param.WordPtr, b4, this.param.Password);
                        if (ReadData_G2 != null && ReadData_G2.length() > 0) {
                            readTag.memId = ReadData_G2;
                            TagCallback tagCallback = this.callback;
                            if (tagCallback != null) {
                                tagCallback.tagCallback(readTag);
                            }
                            playSound();
                        }
                        i++;
                        arrayList = arrayList2;
                        bArr = bArr2;
                        b3 = b4;
                        iArr2 = iArr3;
                    }
                    iArr = iArr2;
                } else {
                    iArr = iArr2;
                }
                r4 = 0;
            } else {
                iArr = iArr2;
                r4 = 0;
                this.reader.Inventory_Mix(this.param.ComAddr, this.QValue, (byte) this.Session, this.param.MaskMem, this.param.MaskAdr, this.param.MaskLen, this.param.MaskData, (byte) this.param.Memory, bArr, b3, hexStringToBytes, this.Target, ByteCompanionObject.MIN_VALUE, (byte) this.param.ScanTime, null, iArr);
            }
        } else {
            iArr = iArr2;
            r4 = 0;
            r4 = 0;
            r4 = 0;
            r4 = 0;
            r4 = 0;
            r4 = 0;
            if (this.param.IvtType == 2) {
                iArr[0] = 0;
                this.reader.Inventory_G2(this.param.ComAddr, (byte) (this.QValue | 32), (byte) this.param.Session, (byte) this.param.WordPtr, (byte) 0, this.Target, ByteCompanionObject.MIN_VALUE, (byte) 10, this.param.MaskMem, this.param.MaskAdr, this.param.MaskLen, this.param.MaskData, null, iArr);
            } else if (this.param.IvtType == 3) {
                this.reader.Inventory_GB(this.param.ComAddr, ByteCompanionObject.MIN_VALUE, (byte) 10, null, iArr);
            } else if (this.param.IvtType == 4) {
                this.reader.Inventory_GJB(this.param.ComAddr, (byte) 0, ByteCompanionObject.MIN_VALUE, (byte) 10, null, iArr);
            } else if (this.param.IvtType == 5) {
                byte[] bArr3 = new byte[256];
                iArr[0] = 0;
                this.reader.InventoryMutiple_6B(this.param.ComAddr, (byte) 1, (byte) 0, (byte) -1, new byte[]{0, 0, 0, 0, 0, 0, 0, 0}, bArr3, iArr);
                if (iArr[0] > 0) {
                    for (int i2 = 0; i2 < iArr[0]; i2++) {
                        String bytesToHexString = this.reader.bytesToHexString(bArr3, (i2 * 10) + 1, 8);
                        byte b5 = bArr3[(i2 * 10) + 9];
                        ReadTag readTag2 = new ReadTag();
                        readTag2.epcId = bytesToHexString;
                        readTag2.memId = "";
                        readTag2.rssi = b5 & 255;
                        readTag2.phase = 0;
                        readTag2.antId = 1;
                    }
                    isSound = true;
                }
            }
        }
        isSound = r4;
        if (iArr[r4] > 0) {
            playSound();
        }
    }

    public int ScanRfid(int scanTime) {
        if (this.mThread == null) {
            this.reader.SetInventoryMatchData(this.param.ComAddr, this.MatchType, this.MaskList);
            long beginTime = System.currentTimeMillis();
            this.isfinish = false;
            if (this.param.Session == 0) {
                this.Target = (byte) 0;
                this.NoCardCOunt = 0;
            } else if (this.param.Session == 1) {
                this.Target = (byte) 0;
            } else {
                byte b = (byte) this.param.Target;
                this.Target = b;
                if (b > 1) {
                    this.Target = (byte) 0;
                }
            }
            this.mThread = new Thread(new Runnable() { // from class: com.rfid.trans.ReaderHelp.3
                @Override // java.lang.Runnable
                public void run() {
                    int[] CardNum;
                    int[] CardNum2 = {0};
                    if (ReaderHelp.this.param.IvtType == 0) {
                        CardNum = CardNum2;
                        ReaderHelp.this.reader.Inventory_G2(ReaderHelp.this.param.ComAddr, (byte) ReaderHelp.this.param.QValue, (byte) ReaderHelp.this.param.Session, (byte) ReaderHelp.this.param.WordPtr, (byte) 0, ReaderHelp.this.Target, ByteCompanionObject.MIN_VALUE, (byte) ReaderHelp.this.param.ScanTime, ReaderHelp.this.param.MaskMem, ReaderHelp.this.param.MaskAdr, ReaderHelp.this.param.MaskLen, ReaderHelp.this.param.MaskData, null, CardNum);
                    } else {
                        CardNum = CardNum2;
                        if (ReaderHelp.this.param.IvtType != 1) {
                            if (ReaderHelp.this.param.IvtType == 2) {
                                CardNum[0] = 0;
                                ReaderHelp.this.reader.Inventory_G2(ReaderHelp.this.param.ComAddr, (byte) (ReaderHelp.this.QValue | 32), (byte) ReaderHelp.this.param.Session, (byte) ReaderHelp.this.param.WordPtr, (byte) 0, ReaderHelp.this.Target, ByteCompanionObject.MIN_VALUE, (byte) ReaderHelp.this.param.ScanTime, ReaderHelp.this.param.MaskMem, ReaderHelp.this.param.MaskAdr, ReaderHelp.this.param.MaskLen, ReaderHelp.this.param.MaskData, null, CardNum);
                            }
                        } else {
                            byte[] ReadAdr = {(byte) (ReaderHelp.this.param.WordPtr >> 8), (byte) (ReaderHelp.this.param.WordPtr & 255)};
                            if (ReaderHelp.this.param.Length == 0) {
                                ReaderHelp.this.param.Length = 6;
                            }
                            byte ReadLen = (byte) ReaderHelp.this.param.Length;
                            byte[] Pwd = ReaderHelp.this.reader.hexStringToBytes(ReaderHelp.this.param.Password);
                            ReaderHelp.this.reader.Inventory_Mix(ReaderHelp.this.param.ComAddr, (byte) ReaderHelp.this.param.QValue, (byte) ReaderHelp.this.param.Session, ReaderHelp.this.param.MaskMem, ReaderHelp.this.param.MaskAdr, ReaderHelp.this.param.MaskLen, ReaderHelp.this.param.MaskData, (byte) ReaderHelp.this.param.Memory, ReadAdr, ReadLen, Pwd, ReaderHelp.this.Target, ByteCompanionObject.MIN_VALUE, (byte) ReaderHelp.this.param.ScanTime, null, CardNum);
                        }
                    }
                    ReaderHelp.isSound = false;
                    if (CardNum[0] > 0) {
                        ReaderHelp.this.playSound();
                    }
                    ReaderHelp.this.isfinish = true;
                }
            });
            this.mThread.start();
            while (System.currentTimeMillis() - beginTime < scanTime && !this.isfinish) {
                SystemClock.sleep(5L);
            }
            if (!this.isfinish) {
                this.reader.StopInventory(this.param.ComAddr);
            }
            try {
                this.mThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.mThread = null;
            return 0;
        }
        return -1;
    }

    @Override // com.rfid.trans.CReader
    public int InventoryOnce(byte session, byte qvalue, byte tidAddr, byte tidLen, byte antenna, byte target, byte scantime, List<ReadTag> tagList) {
        List<ReadTag> tagList2;
        if (tagList == null) {
            tagList2 = new ArrayList<>();
        } else {
            tagList.clear();
            tagList2 = tagList;
        }
        int[] CardNum = {0};
        this.reader.SetInventoryMatchData(this.param.ComAddr, this.MatchType, this.MaskList);
        int result = this.reader.Inventory_G2(this.param.ComAddr, qvalue, session, tidAddr, tidLen, target, antenna, scantime, this.param.MaskMem, this.param.MaskAdr, this.param.MaskLen, this.param.MaskData, tagList2, CardNum);
        isSound = false;
        if (CardNum[0] > 0) {
            playSound();
        }
        return result;
    }

    public String InventorySingle_G2(byte AdrTID, byte LenTID) {
        int[] CardNum = {0};
        List<ReadTag> tagList = new ArrayList<>();
        this.reader.InventorySingle_G2(this.param.ComAddr, (byte) 68, (byte) 0, AdrTID, LenTID, tagList, CardNum);
        if (CardNum[0] > 0) {
            playSound();
            return tagList.get(0).epcId;
        }
        return null;
    }

    @Override // com.rfid.trans.CReader
    public int SetAddress(byte ComAdrData) {
        int result = this.reader.SetAddress(this.param.ComAddr, ComAdrData);
        if (result == 0) {
            this.param.ComAddr = ComAdrData;
        }
        return result;
    }

    @Override // com.rfid.trans.CReader
    public int SetBaudRate(int baudRate) {
        byte baud;
        switch (baudRate) {
            case 9600:
                baud = 0;
                break;
            case 19200:
                baud = 1;
                break;
            case 38400:
                baud = 3;
                break;
            case 57600:
                baud = 5;
                break;
            case 115200:
                baud = 6;
                break;
            case 921600:
                baud = 7;
                break;
            default:
                baud = 5;
                break;
        }
        int result = this.reader.SetBaudRate(this.param.ComAddr, baud);
        if (result == 0) {
            this.reader.DisConnect();
            this.reader.Connect(this.devName, baudRate, this.logswitch);
        }
        return result;
    }

    @Override // com.rfid.trans.CReader
    public int ReadData_G2(byte ENum, byte[] EPC, byte Mem, int WordPtr, byte Num, byte[] Password, byte[] Data, byte[] Errorcode) {
        int epclen = ENum & 255;
        if (epclen > 15 && epclen < 255) {
            return 255;
        }
        if (epclen == 255 || EPC != null) {
            if (epclen < 16 && EPC.length < epclen * 2) {
                return 255;
            }
        }
        if (Data == null || Data.length < (Num & 255) * 2 || Password == null || Password.length < 4 || Errorcode == null || Errorcode.length < 1) {
            return 255;
        }
        int result = 48;
        int m = 0;
        while (m < 10) {
            int m2 = m;
            result = this.reader.ReadData_G2(this.param.ComAddr, ENum, EPC, Mem, (byte) WordPtr, Num, Password, this.param.MaskMem, this.param.MaskAdr, this.param.MaskLen, this.param.MaskData, Data, Errorcode);
            if (result == 0) {
                break;
            }
            m = m2 + 1;
        }
        return result;
    }

    public String ReadDataByTID(String TIDStr, byte Mem, byte WordPtr, byte Num, byte[] Password) {
        byte[] Data;
        int i;
        if (TIDStr.length() == 0 || TIDStr.length() % 4 != 0) {
            return null;
        }
        byte[] EPC = new byte[12];
        byte[] TID = this.reader.hexStringToBytes(TIDStr);
        byte[] MaskAdr = {0, 0};
        byte MaskLen = (byte) (TIDStr.length() * 4);
        byte[] MaskData = new byte[TIDStr.length()];
        System.arraycopy(TID, 0, MaskData, 0, TID.length);
        byte[] Data2 = new byte[(Num & 255) * 2];
        byte[] Errorcode = new byte[1];
        int result = 48;
        int m = 0;
        while (true) {
            if (m >= 10) {
                Data = Data2;
                i = 0;
                break;
            }
            int m2 = m;
            byte[] Errorcode2 = Errorcode;
            byte[] Errorcode3 = EPC;
            Data = Data2;
            byte[] MaskData2 = MaskData;
            byte MaskLen2 = MaskLen;
            i = 0;
            byte[] MaskAdr2 = MaskAdr;
            byte[] TID2 = TID;
            byte[] EPC2 = EPC;
            result = this.reader.ReadData_G2(this.param.ComAddr, (byte) -1, Errorcode3, Mem, WordPtr, Num, Password, (byte) 2, MaskAdr, MaskLen2, MaskData2, Data, Errorcode2);
            if (result == 0) {
                break;
            }
            m = m2 + 1;
            Errorcode = Errorcode2;
            Data2 = Data;
            MaskData = MaskData2;
            MaskLen = MaskLen2;
            MaskAdr = MaskAdr2;
            TID = TID2;
            EPC = EPC2;
        }
        if (result == 0) {
            byte[] Data3 = Data;
            return this.reader.bytesToHexString(Data3, i, Data3.length);
        }
        return null;
    }

    @Override // com.rfid.trans.CReader
    public int WriteData_G2(byte WNum, byte ENum, byte[] EPC, byte Mem, int WordPtr, byte[] Writedata, byte[] Password, byte[] Errorcode) {
        int epclen = ENum & 255;
        if (epclen > 15 && epclen < 255) {
            return 255;
        }
        if (epclen == 255 || EPC != null) {
            if (epclen < 16 && EPC.length < epclen * 2) {
                return 255;
            }
        }
        if (Writedata == null || Writedata.length != (WNum & 255) * 2 || Password == null || Password.length < 4 || Errorcode == null || Errorcode.length < 1) {
            return 255;
        }
        int result = 48;
        int m = 0;
        while (m < 10) {
            int m2 = m;
            result = this.reader.WriteData_G2(this.param.ComAddr, WNum, ENum, EPC, Mem, (byte) WordPtr, Writedata, Password, this.param.MaskMem, this.param.MaskAdr, this.param.MaskLen, this.param.MaskData, Errorcode);
            if (result == 0) {
                break;
            }
            m = m2 + 1;
        }
        return result;
    }

    public int WriteDataByTID(String TIDStr, byte Mem, byte WordPtr, byte[] Password, String wdata) {
        if (TIDStr.length() == 0 || TIDStr.length() % 4 != 0 || wdata.length() == 0 || wdata.length() % 4 != 0) {
            return 255;
        }
        byte WNum = (byte) (wdata.length() / 4);
        byte[] EPC = new byte[12];
        byte[] data = this.reader.hexStringToBytes(wdata);
        byte[] TID = this.reader.hexStringToBytes(TIDStr);
        byte[] MaskAdr = {0, 0};
        byte MaskLen = (byte) (TIDStr.length() * 4);
        byte[] MaskData = new byte[TIDStr.length()];
        System.arraycopy(TID, 0, MaskData, 0, TID.length);
        byte[] Errorcode = new byte[1];
        int result = 48;
        int m = 0;
        while (m < 10) {
            int m2 = m;
            byte[] Errorcode2 = Errorcode;
            byte[] MaskData2 = MaskData;
            byte MaskLen2 = MaskLen;
            byte[] MaskAdr2 = MaskAdr;
            byte[] TID2 = TID;
            byte[] EPC2 = EPC;
            result = this.reader.WriteData_G2(this.param.ComAddr, WNum, (byte) -1, EPC, Mem, WordPtr, data, Password, (byte) 2, MaskAdr2, MaskLen2, MaskData2, Errorcode2);
            if (result == 0) {
                break;
            }
            m = m2 + 1;
            Errorcode = Errorcode2;
            MaskData = MaskData2;
            MaskLen = MaskLen2;
            MaskAdr = MaskAdr2;
            TID = TID2;
            EPC = EPC2;
        }
        return result;
    }

    @Override // com.rfid.trans.CReader
    public int WriteEPC_G2(byte epclen, byte[] epc, byte[] Password, byte[] errcode) {
        int ENum = epclen & 255;
        if (ENum > 31 || epc == null || epc.length != ENum * 2 || Password == null || Password.length < 4 || errcode == null || errcode.length < 1) {
            return 255;
        }
        int result = 48;
        for (int i = 0; i < 10 && (result = this.reader.WriteEPC_G2(this.param.ComAddr, epclen, Password, epc, errcode)) != 0; i++) {
        }
        return result;
    }

    @Override // com.rfid.trans.CReader
    public int Lock_G2(byte epclen, byte[] epc, byte select, byte setprotect, byte[] Password, byte[] errcode) {
        int ENum = epclen & 255;
        if (ENum > 15) {
            return 255;
        }
        if (epc != null && epc.length < ENum * 2) {
            return 255;
        }
        if (Password == null || Password.length < 4 || errcode == null || errcode.length < 1) {
            return 255;
        }
        int result = 48;
        for (int m = 0; m < 10 && (result = this.reader.Lock_G2(this.param.ComAddr, epclen, epc, select, setprotect, Password, errcode)) != 0; m++) {
        }
        return result;
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x001a, code lost:
    
        if (r14 == null) goto L26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x001e, code lost:
    
        if (r14.length >= 1) goto L18;
     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0021, code lost:
    
        r1 = 48;
        r2 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x0026, code lost:
    
        if (r2 >= 10) goto L29;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0028, code lost:
    
        r1 = r10.reader.Kill_G2(r10.param.ComAddr, r11, r12, r13, r14);
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0036, code lost:
    
        if (r1 != 0) goto L24;
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x0039, code lost:
    
        r2 = r2 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x003c, code lost:
    
        return r1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x003d, code lost:
    
        return 255;
     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0017, code lost:
    
        if (r13.length >= 4) goto L14;
     */
    @Override // com.rfid.trans.CReader
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public int Kill_G2(byte r11, byte[] r12, byte[] r13, byte[] r14) {
        /*
            r10 = this;
            r0 = r11 & 255(0xff, float:3.57E-43)
            r1 = 255(0xff, float:3.57E-43)
            r2 = 15
            if (r0 <= r2) goto L9
            return r1
        L9:
            if (r12 != 0) goto Ld
            r0 = 0
            goto L13
        Ld:
            int r2 = r12.length
            int r3 = r0 * 2
            if (r2 >= r3) goto L13
            return r1
        L13:
            if (r13 == 0) goto L3e
            int r2 = r13.length
            r3 = 4
            if (r2 >= r3) goto L1a
            goto L3e
        L1a:
            if (r14 == 0) goto L3d
            int r2 = r14.length
            r3 = 1
            if (r2 >= r3) goto L21
            goto L3d
        L21:
            r1 = 48
            r2 = 0
        L24:
            r3 = 10
            if (r2 >= r3) goto L3c
            com.rfid.trans.BaseReader r4 = r10.reader
            com.rfid.trans.ReaderParameter r3 = r10.param
            byte r5 = r3.ComAddr
            r6 = r11
            r7 = r12
            r8 = r13
            r9 = r14
            int r1 = r4.Kill_G2(r5, r6, r7, r8, r9)
            if (r1 != 0) goto L39
            goto L3c
        L39:
            int r2 = r2 + 1
            goto L24
        L3c:
            return r1
        L3d:
            return r1
        L3e:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.rfid.trans.ReaderHelp.Kill_G2(byte, byte[], byte[], byte[]):int");
    }

    @Override // com.rfid.trans.CReader
    public int BlockWrite_G2(byte WNum, byte ENum, byte[] EPC, byte Mem, byte WordPtr, byte[] Writedata, byte[] Password, byte[] Errorcode) {
        byte ENum2;
        int epclen = ENum & 255;
        if (epclen > 15) {
            return 255;
        }
        if (EPC == null) {
            ENum2 = 0;
        } else {
            if (EPC.length < epclen * 2) {
                return 255;
            }
            ENum2 = ENum;
        }
        if (Writedata != null && Writedata.length == (WNum & 255) * 2 && Password != null && Password.length >= 4 && Errorcode != null && Errorcode.length >= 1) {
            int result = 48;
            int m = 0;
            while (m < 10) {
                int m2 = m;
                int epclen2 = epclen;
                result = this.reader.BlockWrite_G2(this.param.ComAddr, WNum, ENum2, EPC, Mem, WordPtr, Writedata, Password, this.param.MaskMem, this.param.MaskAdr, this.param.MaskLen, this.param.MaskData, Errorcode);
                if (result == 0) {
                    break;
                }
                m = m2 + 1;
                epclen = epclen2;
            }
            return result;
        }
        return 255;
    }

    @Override // com.rfid.trans.CReader
    public String ReadData_G2(String epcid, byte Mem, int WordPtr, byte Num, String Password) {
        byte[] Data;
        byte ENum = 0;
        byte[] EPC = null;
        if (Password != null && Password.length() == 8) {
            byte[] Pwd = this.reader.hexStringToBytes(Password);
            if (epcid != null && epcid.length() > 0) {
                if (epcid.length() % 4 != 0) {
                    return null;
                }
                EPC = this.reader.hexStringToBytes(epcid);
                ENum = (byte) (EPC.length / 2);
            }
            byte[] Data2 = new byte[Num * 2];
            byte[] Errorcode = new byte[1];
            int result = 48;
            int m = 0;
            while (true) {
                if (m >= 10) {
                    Data = Data2;
                    break;
                }
                int m2 = m;
                byte[] Errorcode2 = Errorcode;
                Data = Data2;
                result = this.reader.ReadData_G2(this.param.ComAddr, ENum, EPC, Mem, (byte) WordPtr, Num, Pwd, this.param.MaskMem, this.param.MaskAdr, this.param.MaskLen, this.param.MaskData, Data, Errorcode2);
                if (result == 0) {
                    break;
                }
                m = m2 + 1;
                Errorcode = Errorcode2;
                Data2 = Data;
            }
            if (result != 0) {
                return null;
            }
            byte[] Data3 = Data;
            String StrData = this.reader.bytesToHexString(Data3, 0, Data3.length);
            return StrData;
        }
        return null;
    }

    @Override // com.rfid.trans.CReader
    public int WriteData_G2(String WData, String epcid, byte Mem, int WordPtr, String Password) {
        byte ENum = 0;
        byte[] EPC = null;
        if (Password == null || Password.length() != 8) {
            return 255;
        }
        byte[] Pwd = this.reader.hexStringToBytes(Password);
        if (epcid != null && epcid.length() > 0) {
            if (epcid.length() % 4 != 0) {
                return 255;
            }
            EPC = this.reader.hexStringToBytes(epcid);
            ENum = (byte) (EPC.length / 2);
        }
        if (WData == null || WData.length() <= 0 || WData.length() % 4 != 0) {
            return 255;
        }
        byte[] wArray = this.reader.hexStringToBytes(WData);
        byte[] Errorcode = new byte[1];
        byte WNum = (byte) (wArray.length / 2);
        int result = 48;
        int m = 0;
        while (m < 10) {
            int m2 = m;
            byte WNum2 = WNum;
            result = this.reader.WriteData_G2(this.param.ComAddr, WNum, ENum, EPC, Mem, (byte) WordPtr, wArray, Pwd, this.param.MaskMem, this.param.MaskAdr, this.param.MaskLen, this.param.MaskData, Errorcode);
            if (result == 0) {
                break;
            }
            m = m2 + 1;
            WNum = WNum2;
        }
        return result;
    }

    @Override // com.rfid.trans.CReader
    public int WriteEPC_G2(String epcid, String Password) {
        if (Password == null || Password.length() != 8) {
            return 255;
        }
        byte[] Pwd = this.reader.hexStringToBytes(Password);
        if (epcid == null || epcid.length() <= 0 || epcid.length() % 4 != 0) {
            return 255;
        }
        byte[] EPC = this.reader.hexStringToBytes(epcid);
        byte ENum = (byte) (EPC.length / 2);
        byte[] Errorcode = new byte[1];
        int result = 48;
        for (int m = 0; m < 10 && (result = this.reader.WriteEPC_G2(this.param.ComAddr, ENum, Pwd, EPC, Errorcode)) != 0; m++) {
        }
        return result;
    }

    @Override // com.rfid.trans.CReader
    public int SetDRM(byte DRM) {
        byte[] DEMArr = {(byte) (DRM | ByteCompanionObject.MIN_VALUE)};
        return this.reader.ConfigDRM(this.param.ComAddr, DEMArr);
    }

    @Override // com.rfid.trans.CReader
    public int GetDRM(byte[] DRM) {
        if (DRM.length < 1) {
            return 255;
        }
        DRM[0] = 0;
        return this.reader.ConfigDRM(this.param.ComAddr, DRM);
    }

    @Override // com.rfid.trans.CReader
    public int BlockErase_G2(byte epclen, byte[] EPC, byte Mem, byte WordPtr, byte Num, byte[] Password, byte[] Errorcode) {
        int ENum = epclen & 255;
        if (ENum > 15) {
            return 255;
        }
        if (EPC != null && EPC.length < ENum * 2) {
            return 255;
        }
        if (Password.length < 4 || Errorcode.length < 1) {
            return 255;
        }
        int result = 48;
        int m = 0;
        while (m < 10) {
            int m2 = m;
            result = this.reader.BlockErase_G2(this.param.ComAddr, epclen, EPC, Mem, WordPtr, Num, Password, Errorcode);
            if (result == 0) {
                break;
            }
            m = m2 + 1;
        }
        return result;
    }

    @Override // com.rfid.trans.CReader
    public int FST_TranImage(byte b, byte[] bytes, byte[] bytes1) {
        int len = b & 255;
        if (bytes == null || bytes.length < 2 || bytes1 == null || bytes1.length < len) {
            return 255;
        }
        return this.reader.FST_TranImage(this.param.ComAddr, b, bytes, bytes1);
    }

    @Override // com.rfid.trans.CReader
    public int FST_ShowImage(byte b, byte[] bytes) {
        int ENum = b & 255;
        if (ENum > 15) {
            return 255;
        }
        if (bytes != null && bytes.length < ENum * 2) {
            return 255;
        }
        return this.reader.FST_ShowImage(this.param.ComAddr, b, bytes);
    }

    @Override // com.rfid.trans.CReader
    public int LedOn_kx2005x(String epcid, String Password, byte OnTime) {
        byte ENum = 0;
        byte[] EPC = null;
        if (Password == null || Password.length() != 8) {
            return 255;
        }
        byte[] Pwd = this.reader.hexStringToBytes(Password);
        if (epcid != null && epcid.length() > 0) {
            if (epcid.length() % 4 != 0) {
                return 255;
            }
            EPC = this.reader.hexStringToBytes(epcid);
            ENum = (byte) (EPC.length / 2);
        }
        return this.reader.LedOn_kx2005x(this.param.ComAddr, ENum, EPC, OnTime, Pwd, this.param.MaskMem, this.param.MaskAdr, this.param.MaskLen, this.param.MaskData);
    }

    @Override // com.rfid.trans.CReader
    public int Fd_InitRegfile(String epcid, String Password) {
        byte ENum = 0;
        byte[] EPC = null;
        byte[] Errorcode = new byte[1];
        if (Password == null || Password.length() != 8) {
            return 255;
        }
        byte[] Pwd = this.reader.hexStringToBytes(Password);
        if (epcid != null && epcid.length() > 0) {
            if (epcid.length() % 4 != 0) {
                return 255;
            }
            EPC = this.reader.hexStringToBytes(epcid);
            ENum = (byte) (EPC.length / 2);
        }
        return this.reader.Fd_InitRegfile(this.param.ComAddr, ENum, EPC, Pwd, this.param.MaskMem, this.param.MaskAdr, this.param.MaskLen, this.param.MaskData, Errorcode);
    }

    @Override // com.rfid.trans.CReader
    public int Fd_ReadReg(String epcid, int RegAddr, String Password, byte[] data) {
        byte ENum;
        byte[] EPC;
        byte[] Errorcode = new byte[1];
        if (Password != null && Password.length() == 8 && data != null && data.length >= 2) {
            byte[] Pwd = this.reader.hexStringToBytes(Password);
            if (epcid != null && epcid.length() > 0) {
                if (epcid.length() % 4 != 0) {
                    return 255;
                }
                byte[] EPC2 = this.reader.hexStringToBytes(epcid);
                byte ENum2 = (byte) (EPC2.length / 2);
                ENum = ENum2;
                EPC = EPC2;
            } else {
                ENum = 0;
                EPC = null;
            }
            byte[] regAdr = {(byte) (RegAddr >> 8), (byte) (RegAddr & 255)};
            return this.reader.Fd_ReadReg(this.param.ComAddr, ENum, EPC, regAdr, Pwd, this.param.MaskMem, this.param.MaskAdr, this.param.MaskLen, this.param.MaskData, data, Errorcode);
        }
        return 255;
    }

    @Override // com.rfid.trans.CReader
    public int Fd_WriteReg(String epcid, int RegAddr, byte[] RegData, String Password) {
        byte ENum;
        byte[] EPC;
        byte[] Errorcode = new byte[1];
        if (Password != null && Password.length() == 8 && RegData != null && RegData.length == 2) {
            byte[] Pwd = this.reader.hexStringToBytes(Password);
            if (epcid != null && epcid.length() > 0) {
                if (epcid.length() % 4 != 0) {
                    return 255;
                }
                byte[] EPC2 = this.reader.hexStringToBytes(epcid);
                byte ENum2 = (byte) (EPC2.length / 2);
                ENum = ENum2;
                EPC = EPC2;
            } else {
                ENum = 0;
                EPC = null;
            }
            byte[] regAdr = {(byte) (RegAddr >> 8), (byte) (RegAddr & 255)};
            return this.reader.Fd_WriteReg(this.param.ComAddr, ENum, EPC, regAdr, RegData, Pwd, this.param.MaskMem, this.param.MaskAdr, this.param.MaskLen, this.param.MaskData, Errorcode);
        }
        return 255;
    }

    @Override // com.rfid.trans.CReader
    public int Fd_ReadMemory(String epcid, int StartAddr, byte ReadLen, String Password, byte AuthType, String AuthPwd, byte[] data) {
        byte ENum;
        byte[] EPC;
        byte[] Errorcode = new byte[1];
        if (Password != null && Password.length() == 8 && AuthPwd != null && AuthPwd.length() == 8 && data != null && data.length >= (ReadLen & 255)) {
            byte[] Pwd = this.reader.hexStringToBytes(Password);
            byte[] aPwd = this.reader.hexStringToBytes(AuthPwd);
            if (epcid != null && epcid.length() > 0) {
                if (epcid.length() % 4 != 0) {
                    return 255;
                }
                byte[] EPC2 = this.reader.hexStringToBytes(epcid);
                byte ENum2 = (byte) (EPC2.length / 2);
                ENum = ENum2;
                EPC = EPC2;
            } else {
                ENum = 0;
                EPC = null;
            }
            byte[] startAdr = {(byte) (StartAddr >> 8), (byte) (StartAddr & 255)};
            return this.reader.Fd_ReadMemory(this.param.ComAddr, ENum, EPC, startAdr, ReadLen, Pwd, AuthType, aPwd, this.param.MaskMem, this.param.MaskAdr, this.param.MaskLen, this.param.MaskData, data, Errorcode);
        }
        return 255;
    }

    @Override // com.rfid.trans.CReader
    public int Fd_WriteMemory(String epcid, int StartAddr, byte[] data, String Password, byte AuthType, String AuthPwd) {
        byte ENum;
        byte[] EPC;
        byte[] Errorcode = new byte[1];
        if (Password != null && Password.length() == 8 && AuthPwd != null && AuthPwd.length() == 8 && data != null && data.length % 4 == 0) {
            byte[] Pwd = this.reader.hexStringToBytes(Password);
            byte[] aPwd = this.reader.hexStringToBytes(AuthPwd);
            if (epcid != null && epcid.length() > 0) {
                if (epcid.length() % 4 != 0) {
                    return 255;
                }
                byte[] EPC2 = this.reader.hexStringToBytes(epcid);
                byte ENum2 = (byte) (EPC2.length / 2);
                ENum = ENum2;
                EPC = EPC2;
            } else {
                ENum = 0;
                EPC = null;
            }
            byte[] startAdr = {(byte) (StartAddr >> 8), (byte) (StartAddr & 255)};
            return this.reader.Fd_WriteMemory(this.param.ComAddr, ENum, EPC, startAdr, data.length, data, Pwd, AuthType, aPwd, this.param.MaskMem, this.param.MaskAdr, this.param.MaskLen, this.param.MaskData, Errorcode);
        }
        return 255;
    }

    @Override // com.rfid.trans.CReader
    public int Fd_GetTemperature(String epcid, byte MeaType, byte ResultSel, byte FieldChkEn, byte EPStorageEn, byte UserBlockAddr, String Password, byte[] Temp) {
        byte ENum;
        byte[] EPC;
        byte[] Errorcode = new byte[1];
        if (Password != null && Password.length() == 8 && Temp != null && Temp.length >= 2) {
            byte[] Pwd = this.reader.hexStringToBytes(Password);
            if (epcid != null && epcid.length() > 0) {
                if (epcid.length() % 4 != 0) {
                    return 255;
                }
                byte[] EPC2 = this.reader.hexStringToBytes(epcid);
                byte ENum2 = (byte) (EPC2.length / 2);
                ENum = ENum2;
                EPC = EPC2;
            } else {
                ENum = 0;
                EPC = null;
            }
            return this.reader.Fd_GetTemperature(this.param.ComAddr, ENum, EPC, MeaType, ResultSel, FieldChkEn, EPStorageEn, UserBlockAddr, Pwd, this.param.MaskMem, this.param.MaskAdr, this.param.MaskLen, this.param.MaskData, Temp, Errorcode);
        }
        return 255;
    }

    @Override // com.rfid.trans.CReader
    public int Fd_StartLogging(String epcid, int StartDelay, int VdetStep, String Password) {
        byte ENum = 0;
        byte[] EPC = null;
        byte[] Errorcode = new byte[1];
        if (Password != null && Password.length() == 8) {
            byte[] Pwd = this.reader.hexStringToBytes(Password);
            if (epcid != null && epcid.length() > 0) {
                if (epcid.length() % 4 != 0) {
                    return 255;
                }
                EPC = this.reader.hexStringToBytes(epcid);
                ENum = (byte) (EPC.length / 2);
            }
            byte[] btDelay = {(byte) (StartDelay >> 8), (byte) (StartDelay & 255)};
            byte[] btStep = {(byte) (VdetStep >> 8), (byte) (VdetStep & 255)};
            return this.reader.Fd_StartLogging(this.param.ComAddr, ENum, EPC, btDelay, btStep, Pwd, this.param.MaskMem, this.param.MaskAdr, this.param.MaskLen, this.param.MaskData, Errorcode);
        }
        return 255;
    }

    @Override // com.rfid.trans.CReader
    public int Fd_StopLogging(String epcid, String Password, String StopPwd) {
        byte ENum = 0;
        byte[] EPC = null;
        byte[] Errorcode = new byte[1];
        if (Password == null || Password.length() != 8 || StopPwd == null || StopPwd.length() != 8) {
            return 255;
        }
        byte[] Pwd = this.reader.hexStringToBytes(Password);
        byte[] sPwd = this.reader.hexStringToBytes(StopPwd);
        if (epcid != null && epcid.length() > 0) {
            if (epcid.length() % 4 != 0) {
                return 255;
            }
            EPC = this.reader.hexStringToBytes(epcid);
            ENum = (byte) (EPC.length / 2);
        }
        return this.reader.Fd_StopLogging(this.param.ComAddr, ENum, EPC, Pwd, sPwd, this.param.MaskMem, this.param.MaskAdr, this.param.MaskLen, this.param.MaskData, Errorcode);
    }

    @Override // com.rfid.trans.CReader
    public int Fd_ExtReadMemory(String epcid, int StartAddr, int ReadLen, String Password, byte AuthType, String AuthPwd, byte[] data, int[] dalen) {
        byte ENum;
        byte[] EPC;
        byte[] Errorcode = new byte[1];
        if (Password != null && Password.length() == 8 && AuthPwd != null && AuthPwd.length() == 8 && data != null && data.length >= ReadLen) {
            byte[] Pwd = this.reader.hexStringToBytes(Password);
            byte[] aPwd = this.reader.hexStringToBytes(AuthPwd);
            if (epcid != null && epcid.length() > 0) {
                if (epcid.length() % 4 != 0) {
                    return 255;
                }
                byte[] EPC2 = this.reader.hexStringToBytes(epcid);
                byte ENum2 = (byte) (EPC2.length / 2);
                ENum = ENum2;
                EPC = EPC2;
            } else {
                ENum = 0;
                EPC = null;
            }
            byte[] startAdr = {(byte) (StartAddr >> 8), (byte) (StartAddr & 255)};
            byte[] btLen = {(byte) (ReadLen >> 8), (byte) (ReadLen & 255)};
            return this.reader.Fd_ExtReadMemory(this.param.ComAddr, ENum, EPC, startAdr, btLen, Pwd, AuthType, aPwd, this.param.MaskMem, this.param.MaskAdr, this.param.MaskLen, this.param.MaskData, data, dalen, Errorcode);
        }
        return 255;
    }

    @Override // com.rfid.trans.CReader
    public int Fd_OP_Mode_Chk(String epcid, byte cfg, String Password, byte[] ModeStatus) {
        byte ENum;
        byte[] EPC;
        byte[] Errorcode = new byte[1];
        if (Password != null && Password.length() == 8 && ModeStatus != null && ModeStatus.length >= 2) {
            byte[] Pwd = this.reader.hexStringToBytes(Password);
            if (epcid != null && epcid.length() > 0) {
                if (epcid.length() % 4 != 0) {
                    return 255;
                }
                byte[] EPC2 = this.reader.hexStringToBytes(epcid);
                byte ENum2 = (byte) (EPC2.length / 2);
                ENum = ENum2;
                EPC = EPC2;
            } else {
                ENum = 0;
                EPC = null;
            }
            return this.reader.Fd_OP_Mode_Chk(this.param.ComAddr, ENum, EPC, cfg, Pwd, this.param.MaskMem, this.param.MaskAdr, this.param.MaskLen, this.param.MaskData, ModeStatus, Errorcode);
        }
        return 255;
    }

    public String ReadData_GB(String TagID, byte ReadMem, int WordPtr, byte Num, String StrPassword) {
        byte TNum;
        byte[] EPC;
        if (StrPassword == null || StrPassword.length() != 8) {
            return null;
        }
        byte[] Password = this.reader.hexStringToBytes(StrPassword);
        if (TagID != null && TagID.length() > 0) {
            byte[] EPC2 = this.reader.hexStringToBytes(TagID);
            byte TNum2 = (byte) (TagID.length() / 4);
            TNum = TNum2;
            EPC = EPC2;
        } else {
            TNum = 0;
            EPC = null;
        }
        byte[] Data = new byte[Num * 2];
        byte[] Errorcode = new byte[1];
        byte[] StartLen = {(byte) (WordPtr >> 8), (byte) (WordPtr & 255)};
        int result = this.reader.ReadData_GB(this.param.ComAddr, TNum, EPC, ReadMem, StartLen, Num, Password, Data, Errorcode);
        if (result == 0) {
            return this.reader.bytesToHexString(Data, 0, Data.length);
        }
        return null;
    }

    public int WriteData_GB(String TagID, byte WMem, int WordPtr, String StrPassword, String wdata) {
        byte TNum;
        byte[] EPC;
        if (StrPassword == null || StrPassword.length() != 8) {
            return 255;
        }
        byte[] Password = this.reader.hexStringToBytes(StrPassword);
        if (TagID != null && TagID.length() > 0) {
            byte[] EPC2 = this.reader.hexStringToBytes(TagID);
            byte TNum2 = (byte) (TagID.length() / 4);
            TNum = TNum2;
            EPC = EPC2;
        } else {
            TNum = 0;
            EPC = null;
        }
        if (wdata == null || wdata.length() == 0 || wdata.length() % 4 != 0) {
            return 255;
        }
        byte WNum = (byte) (wdata.length() / 4);
        byte[] Writedata = this.reader.hexStringToBytes(wdata);
        byte[] Errorcode = new byte[1];
        byte[] StartLen = {(byte) (WordPtr >> 8), (byte) (WordPtr & 255)};
        return this.reader.WriteData_GB(this.param.ComAddr, WNum, TNum, EPC, WMem, StartLen, Writedata, Password, Errorcode);
    }

    public int Lock_GB(String TagID, byte LocMem, byte Cfg, byte Action, String StrPassword) {
        byte TNum;
        byte[] EPC;
        if (StrPassword == null || StrPassword.length() != 8) {
            return 255;
        }
        byte[] Password = this.reader.hexStringToBytes(StrPassword);
        if (TagID != null && TagID.length() > 0) {
            byte[] EPC2 = this.reader.hexStringToBytes(TagID);
            byte TNum2 = (byte) (TagID.length() / 4);
            TNum = TNum2;
            EPC = EPC2;
        } else {
            TNum = 0;
            EPC = null;
        }
        byte[] Errorcode = new byte[1];
        return this.reader.Lock_GB(this.param.ComAddr, TNum, EPC, LocMem, Cfg, Action, Password, Errorcode);
    }

    public int Kill_GB(String TagID, String StrPassword) {
        byte TNum;
        byte[] EPC;
        if (StrPassword == null || StrPassword.length() != 8) {
            return 255;
        }
        byte[] Password = this.reader.hexStringToBytes(StrPassword);
        if (TagID != null && TagID.length() > 0) {
            byte[] EPC2 = this.reader.hexStringToBytes(TagID);
            byte TNum2 = (byte) (TagID.length() / 4);
            TNum = TNum2;
            EPC = EPC2;
        } else {
            TNum = 0;
            EPC = null;
        }
        byte[] Errorcode = new byte[1];
        return this.reader.Kill_GB(this.param.ComAddr, TNum, EPC, Password, Errorcode);
    }

    public int EraseData_GB(String TagID, byte EMem, int WordPtr, int Elen, String StrPassword) {
        byte TNum;
        byte[] EPC;
        if (StrPassword == null || StrPassword.length() != 8) {
            return 255;
        }
        byte[] Password = this.reader.hexStringToBytes(StrPassword);
        if (TagID != null && TagID.length() > 0) {
            byte[] EPC2 = this.reader.hexStringToBytes(TagID);
            byte TNum2 = (byte) (TagID.length() / 4);
            TNum = TNum2;
            EPC = EPC2;
        } else {
            TNum = 0;
            EPC = null;
        }
        byte[] Errorcode = new byte[1];
        byte[] StartLen = {(byte) (WordPtr >> 8), (byte) (WordPtr & 255)};
        byte[] Count = {(byte) (Elen >> 8), (byte) (Elen & 255)};
        return this.reader.EraseData_GB(this.param.ComAddr, TNum, EPC, EMem, StartLen, Count, Password, Errorcode);
    }

    public String ReadData_GJB(String TagID, byte ReadMem, int WordPtr, byte Num, String StrPassword) {
        byte TNum;
        byte[] EPC;
        if (StrPassword == null || StrPassword.length() != 8) {
            return null;
        }
        byte[] Password = this.reader.hexStringToBytes(StrPassword);
        if (TagID != null && TagID.length() > 0) {
            byte[] EPC2 = this.reader.hexStringToBytes(TagID);
            byte TNum2 = (byte) (TagID.length() / 4);
            TNum = TNum2;
            EPC = EPC2;
        } else {
            TNum = 0;
            EPC = null;
        }
        byte[] Data = new byte[Num * 2];
        byte[] Errorcode = new byte[1];
        byte[] StartLen = {(byte) (WordPtr >> 8), (byte) (WordPtr & 255)};
        int result = this.reader.ReadData_GJB(this.param.ComAddr, TNum, EPC, ReadMem, StartLen, Num, Password, Data, Errorcode);
        if (result == 0) {
            return this.reader.bytesToHexString(Data, 0, Data.length);
        }
        return null;
    }

    public int WriteData_GJB(String TagID, byte WMem, int WordPtr, String StrPassword, String wdata) {
        byte TNum;
        byte[] EPC;
        if (StrPassword == null || StrPassword.length() != 8) {
            return 255;
        }
        byte[] Password = this.reader.hexStringToBytes(StrPassword);
        if (TagID != null && TagID.length() > 0) {
            byte[] EPC2 = this.reader.hexStringToBytes(TagID);
            byte TNum2 = (byte) (TagID.length() / 4);
            TNum = TNum2;
            EPC = EPC2;
        } else {
            TNum = 0;
            EPC = null;
        }
        if (wdata == null || wdata.length() == 0 || wdata.length() % 4 != 0) {
            return 255;
        }
        byte WNum = (byte) (wdata.length() / 4);
        byte[] Writedata = this.reader.hexStringToBytes(wdata);
        byte[] Errorcode = new byte[1];
        byte[] StartLen = {(byte) (WordPtr >> 8), (byte) (WordPtr & 255)};
        return this.reader.WriteData_GJB(this.param.ComAddr, WNum, TNum, EPC, WMem, StartLen, Writedata, Password, Errorcode);
    }

    public int Lock_GJB(String TagID, byte LocMem, byte Cfg, byte Action, String StrPassword) {
        byte TNum;
        byte[] EPC;
        if (StrPassword == null || StrPassword.length() != 8) {
            return 255;
        }
        byte[] Password = this.reader.hexStringToBytes(StrPassword);
        if (TagID != null && TagID.length() > 0) {
            byte[] EPC2 = this.reader.hexStringToBytes(TagID);
            byte TNum2 = (byte) (TagID.length() / 4);
            TNum = TNum2;
            EPC = EPC2;
        } else {
            TNum = 0;
            EPC = null;
        }
        byte[] Errorcode = new byte[1];
        return this.reader.Lock_GJB(this.param.ComAddr, TNum, EPC, LocMem, Cfg, Action, Password, Errorcode);
    }

    public int Kill_GJB(String TagID, String StrPassword) {
        byte TNum;
        byte[] EPC;
        if (StrPassword == null || StrPassword.length() != 8) {
            return 255;
        }
        byte[] Password = this.reader.hexStringToBytes(StrPassword);
        if (TagID != null && TagID.length() > 0) {
            byte[] EPC2 = this.reader.hexStringToBytes(TagID);
            byte TNum2 = (byte) (TagID.length() / 4);
            TNum = TNum2;
            EPC = EPC2;
        } else {
            TNum = 0;
            EPC = null;
        }
        byte[] Errorcode = new byte[1];
        return this.reader.Kill_GJB(this.param.ComAddr, TNum, EPC, Password, Errorcode);
    }

    public int EraseData_GJB(String TagID, byte EMem, int WordPtr, int Elen, String StrPassword) {
        byte TNum;
        byte[] EPC;
        if (StrPassword == null || StrPassword.length() != 8) {
            return 255;
        }
        byte[] Password = this.reader.hexStringToBytes(StrPassword);
        if (TagID != null && TagID.length() > 0) {
            byte[] EPC2 = this.reader.hexStringToBytes(TagID);
            byte TNum2 = (byte) (TagID.length() / 4);
            TNum = TNum2;
            EPC = EPC2;
        } else {
            TNum = 0;
            EPC = null;
        }
        byte[] Errorcode = new byte[1];
        byte[] StartLen = {(byte) (WordPtr >> 8), (byte) (WordPtr & 255)};
        byte[] Count = {(byte) (Elen >> 8), (byte) (Elen & 255)};
        return this.reader.EraseData_GJB(this.param.ComAddr, TNum, EPC, EMem, StartLen, Count, Password, Errorcode);
    }

    public int Inventory_GJB(byte Algo, List<ReadTag> tagList) {
        int[] CardNum = new int[1];
        return this.reader.Inventory_GJB(this.param.ComAddr, Algo, ByteCompanionObject.MIN_VALUE, (byte) 10, tagList, CardNum);
    }

    public int Inventory_GB(List<ReadTag> tagList) {
        int[] CardNum = new int[1];
        return this.reader.Inventory_GB(this.param.ComAddr, ByteCompanionObject.MIN_VALUE, (byte) 10, tagList, CardNum);
    }

    public int InventorySingle_6B(byte[] id) {
        return this.reader.InventorySingle_6B(this.param.ComAddr, id);
    }

    public int InventoryMutiple_6B(byte Condition, byte Address, byte Mask, byte[] Word_data, byte[] id, int[] number) {
        return this.reader.InventoryMutiple_6B(this.param.ComAddr, Condition, Address, Mask, Word_data, id, number);
    }

    public int ReadData_6B(byte Address, byte[] id, byte num, byte[] data) {
        return this.reader.ReadData_6B(this.param.ComAddr, Address, id, num, data);
    }

    public int WriteData_6B(byte Address, byte[] id, byte num, byte[] data) {
        return this.reader.WriteData_6B(this.param.ComAddr, Address, id, num, data);
    }

    public int Lock_6B(byte Address, byte[] id) {
        return this.reader.Lock_6B(this.param.ComAddr, Address, id);
    }

    public int CheckLock_6B(byte Address, byte[] id, byte[] LockState) {
        return this.reader.CheckLock_6B(this.param.ComAddr, Address, id, LockState);
    }

    public int RfOutput(byte OnOff) {
        return this.reader.RfOutput(this.param.ComAddr, OnOff);
    }

    public int SetCfgParameter(byte opt, byte cfgNo, byte[] cfgData, int len) {
        return this.reader.SetCfgParameter(this.param.ComAddr, opt, cfgNo, cfgData, len);
    }

    public int GetCfgParameter(byte cfgNo, byte[] cfgData, int[] len) {
        return this.reader.GetCfgParameter(this.param.ComAddr, cfgNo, cfgData, len);
    }

    public int MeasureReturnLoss(byte[] TestFreq, byte Ant, byte[] ReturnLoss) {
        return this.reader.MeasureReturnLoss(this.param.ComAddr, TestFreq, Ant, ReturnLoss);
    }

    public int SetCustomRegion(byte flags, int band, int FreSpace, int FreNum, int StartFre) {
        return this.reader.SetCustomRegion(this.param.ComAddr, flags, band, FreSpace, FreNum, StartFre);
    }

    public int GetCustomRegion(int[] band, int[] FreSpace, int[] FreNum, int[] StartFre) {
        return this.reader.GetCustomRegion(this.param.ComAddr, band, FreSpace, FreNum, StartFre);
    }

    public int GetModuleDescribe(byte[] Describe) {
        return this.reader.GetModuleDescribe(this.param.ComAddr, Describe);
    }

    public class EpcTemp {
        public String EPC;
        public float temp;

        public EpcTemp() {
        }
    }

    public List<EpcTemp> MeasureTemp(int mType, byte epcNum, byte[] EPC) {
        if (mType == 0) {
            List<EpcTemp> mlist = ReadYHTemp(epcNum, EPC);
            return mlist;
        }
        if (mType != 1) {
            return null;
        }
        List<EpcTemp> mlist2 = ReadYLTemp(epcNum, EPC);
        return mlist2;
    }

    public List<EpcTemp> ReadYLTemp(byte epcNum, byte[] EPC) {
        List<EpcTemp> epctemp = new ArrayList<>();
        byte[] Password = {0, 0, 0, 0};
        byte[] data = new byte[256];
        byte[] errorcode = new byte[1];
        int result = 48;
        int index = 0;
        while (index < 5) {
            int index2 = index;
            result = this.reader.Inventory_temp_YL(this.param.ComAddr, epcNum, EPC, Password, data, errorcode);
            if (result == 0) {
                break;
            }
            index = index2 + 1;
        }
        if (result == 0) {
            float temp = (((data[0] & 255) * 256) + (data[1] & 255)) / 256.0f;
            EpcTemp mtag = new EpcTemp();
            mtag.EPC = this.reader.bytesToHexString(EPC, 0, EPC.length);
            mtag.temp = temp;
            epctemp.add(mtag);
        }
        return epctemp;
    }

    public List<EpcTemp> ReadYHTemp(byte epcNum, byte[] EPC) {
        List<ReadTag> taglist;
        List<EpcTemp> epctemp;
        List<EpcTemp> epctemp2;
        byte QValue = (byte) (this.param.QValue | 128);
        List<EpcTemp> epctemp3 = new ArrayList<>();
        List<ReadTag> taglist2 = new ArrayList<>();
        int index = 0;
        while (true) {
            if (index >= 5) {
                taglist = taglist2;
                epctemp = epctemp3;
                break;
            }
            int index2 = index;
            taglist = taglist2;
            epctemp = epctemp3;
            byte QValue2 = QValue;
            this.reader.Inventory_Temp_YH(this.param.ComAddr, epcNum, EPC, (byte) 7, (byte) 4, (byte) 4, QValue, (byte) 0, (byte) 0, ByteCompanionObject.MIN_VALUE, (byte) 10, taglist);
            if (taglist.size() > 0) {
                break;
            }
            index = index2 + 1;
            taglist2 = taglist;
            QValue = QValue2;
            epctemp3 = epctemp;
        }
        if (taglist.size() > 0) {
            int m = 0;
            while (m < taglist.size()) {
                List<ReadTag> taglist3 = taglist;
                ReadTag arg0 = taglist3.get(m);
                if (arg0.epcId.length() != 32) {
                    epctemp2 = epctemp;
                } else if (arg0.epcId.substring(arg0.epcId.length() - 16, arg0.epcId.length() - 15).equals("F")) {
                    String temp = arg0.epcId.toUpperCase();
                    String epc = temp.substring(0, 4);
                    String adjdata = temp.substring(temp.length() - 8, temp.length());
                    String sensedata = temp.substring(temp.length() - 16, temp.length() - 8);
                    byte[] btAdj = this.reader.hexStringToBytes(adjdata);
                    byte[] btSense = this.reader.hexStringToBytes(sensedata);
                    if (CheckSense(btSense) != 0) {
                        epctemp2 = epctemp;
                    } else {
                        float tagtemp = CalculateTemperature(btSense, btAdj);
                        EpcTemp mtag = new EpcTemp();
                        mtag.EPC = epc;
                        mtag.temp = tagtemp;
                        epctemp2 = epctemp;
                        epctemp2.add(mtag);
                    }
                } else {
                    epctemp2 = epctemp;
                }
                m++;
                taglist = taglist3;
                epctemp = epctemp2;
            }
            return epctemp;
        }
        return epctemp;
    }

    public int CheckSense(byte[] sense) {
        int SEN_DATA0 = (((sense[0] & 255) & 15) << 20) + ((sense[1] & 255) << 12) + (((sense[2] & 255) & 15) << 8) + (sense[3] & 255);
        int b2 = (((((SEN_DATA0 >> 14) & 1) ^ ((SEN_DATA0 >> 11) & 1)) ^ ((SEN_DATA0 >> 8) & 1)) ^ ((SEN_DATA0 >> 5) & 1)) & 255;
        int b1 = (((((SEN_DATA0 >> 13) & 1) ^ ((SEN_DATA0 >> 10) & 1)) ^ ((SEN_DATA0 >> 7) & 1)) ^ ((SEN_DATA0 >> 4) & 1)) & 255;
        int b0 = (((((SEN_DATA0 >> 12) & 1) ^ ((SEN_DATA0 >> 9) & 1)) ^ ((SEN_DATA0 >> 6) & 1)) ^ ((SEN_DATA0 >> 3) & 1)) & 255;
        return (((SEN_DATA0 >> 2) & 1) == 1 - b2 && ((SEN_DATA0 >> 1) & 1) == 1 - b1 && (SEN_DATA0 & 1) == 1 - b0) ? 0 : 1;
    }

    public float CalculateTemperature(byte[] sense, byte[] adj) {
        if (adj.length != 4) {
            return 0.0f;
        }
        if (((sense[0] & 240) >> 4) != 15) {
            return 9999.0f;
        }
        long sensedata = (((sense[0] & 255) & 15) << 20) + ((sense[1] & 255) << 12) + ((15 & (sense[2] & 255)) << 8) + (sense[3] & 255);
        short adjdata = (short) (((adj[0] & 255) << 8) + (adj[1] & 255));
        int M = (int) (sensedata >> 19);
        int L = (int) ((sensedata >> 3) & 65535);
        if ((adj[2] & 255 & EnumG.BaseMid_SafeCertification) == 0 || (adj[2] & 255 & EnumG.BaseMid_SafeCertification) == 16) {
            float adj2 = adjdata;
            return (11984.47f / (((M + 21.25f) + (L / 2752.0f)) + ((adj2 / 100.0f) - 101.0f))) - 301.57f;
        }
        if ((adj[2] & 255 & EnumG.BaseMid_SafeCertification) != 32) {
            return 0.0f;
        }
        if (M != 4) {
            return 9999.0f;
        }
        float adj22 = adjdata;
        float tt = (11109.6f / (((L + adj22) / 375.3f) + 24.0f)) - 290.0f;
        if (tt >= 125.0d) {
            double d = tt;
            Double.isNaN(d);
            return (float) ((d * 1.2d) - 25.0d);
        }
        return tt;
    }

    public int updateFirmware(final UpdateCallback myCallback, String binPath, String DevPort) throws IOException {
        if (this.nThread != null) {
            return 48;
        }
        this.szbuff = null;
        int result = this.reader.Connect(DevPort, 115200, 0);
        if (result != 0) {
            return 48;
        }
        FileInputStream fin = new FileInputStream(binPath);
        int length = fin.available();
        byte[] bArr = new byte[length];
        this.szbuff = bArr;
        fin.read(bArr);
        fin.close();
        close();
        SystemClock.sleep(2000L);
        open();
        Thread thread = new Thread(new Runnable() { // from class: com.rfid.trans.ReaderHelp.4
            @Override // java.lang.Runnable
            public void run() {
                boolean success = false;
                try {
                    byte[] RecvData = new byte[10];
                    byte[] CMD = new byte[5];
                    ReaderHelp.this.RecvStr = "";
                    long beginTime = System.currentTimeMillis();
                    while (true) {
                        if (System.currentTimeMillis() - beginTime >= 2000) {
                            break;
                        }
                        CMD[4] = 43;
                        CMD[3] = 43;
                        CMD[2] = 43;
                        CMD[1] = 43;
                        CMD[0] = 43;
                        ReaderHelp.this.reader.msg.mOutStream.write(CMD);
                        SystemClock.sleep(10L);
                        int count = ReaderHelp.this.reader.msg.mInStream.read(RecvData);
                        if (count > 1) {
                            byte[] daw = new byte[count];
                            System.arraycopy(RecvData, 0, daw, 0, count);
                            StringBuilder sb = new StringBuilder();
                            ReaderHelp readerHelp = ReaderHelp.this;
                            sb.append(readerHelp.RecvStr);
                            sb.append(new String(daw));
                            readerHelp.RecvStr = sb.toString();
                            if (ReaderHelp.this.RecvStr.indexOf("good") != -1) {
                                success = true;
                                break;
                            }
                        }
                    }
                    if (!success) {
                        myCallback.updateBackResult(1);
                        ReaderHelp.this.reader.DisConnect();
                    } else {
                        myCallback.updateBackResult(2);
                        ReaderHelp readerHelp2 = ReaderHelp.this;
                        readerHelp2.refreshrfid(readerHelp2.szbuff, myCallback);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    myCallback.updateBackResult(3);
                    ReaderHelp.this.reader.DisConnect();
                }
                ReaderHelp.this.nThread = null;
            }
        });
        this.nThread = thread;
        thread.start();
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshrfid(byte[] szbuff, UpdateCallback myCallback) {
        int nfilesize = szbuff.length;
        int nturn = nfilesize / 256;
        for (int i = 0; i < nturn; i++) {
            byte[] page = new byte[512];
            int nindex = i;
            page[0] = 2;
            page[1] = (byte) ((65280 & nindex) >> 8);
            page[2] = (byte) (nindex & 255);
            for (int m = 0; m < 256; m++) {
                page[m + 3] = szbuff[(i * 256) + m];
            }
            this.reader.getCRC(page, 259);
            int ntime = 0;
            while (1 != 0) {
                SystemClock.sleep(2L);
                int fCmdRet = this.reader.SendPage(page);
                ntime++;
                if (ntime > 20) {
                    if (myCallback != null) {
                        myCallback.updateBackResult(3);
                    }
                    this.reader.DisConnect();
                    return;
                } else if (fCmdRet != 1 && fCmdRet == 0) {
                    break;
                }
            }
            if (i % 10 == 0) {
                String mProcess = String.valueOf((i * 100) / nturn);
                if (myCallback != null) {
                    myCallback.updateBackProcess(mProcess);
                }
            }
        }
        if (myCallback != null) {
            myCallback.updateBackResult(0);
        }
        this.reader.DisConnect();
    }
}
