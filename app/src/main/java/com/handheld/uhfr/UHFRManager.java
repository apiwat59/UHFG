package com.handheld.uhfr;

import android.os.SystemClock;
import android.util.Log;
import androidx.core.view.InputDeviceCompat;
import cn.com.example.rfid.driver.Driver;
import cn.com.example.rfid.driver.RfidDriver;
import cn.pda.serialport.SerialPort;
import cn.pda.serialport.Tools;
import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.dal.HandlerTag6bLog;
import com.gg.reader.api.dal.HandlerTag6bOver;
import com.gg.reader.api.dal.HandlerTagEpcLog;
import com.gg.reader.api.dal.HandlerTagEpcOver;
import com.gg.reader.api.dal.HandlerTagGJbLog;
import com.gg.reader.api.dal.HandlerTagGJbOver;
import com.gg.reader.api.dal.HandlerTagGbLog;
import com.gg.reader.api.dal.HandlerTagGbOver;
import com.gg.reader.api.protocol.gx.EnumG;
import com.gg.reader.api.protocol.gx.LogBase6bInfo;
import com.gg.reader.api.protocol.gx.LogBase6bOver;
import com.gg.reader.api.protocol.gx.LogBaseEpcInfo;
import com.gg.reader.api.protocol.gx.LogBaseEpcOver;
import com.gg.reader.api.protocol.gx.LogBaseGJbInfo;
import com.gg.reader.api.protocol.gx.LogBaseGJbOver;
import com.gg.reader.api.protocol.gx.LogBaseGbInfo;
import com.gg.reader.api.protocol.gx.LogBaseGbOver;
import com.gg.reader.api.protocol.gx.MsgAppGetBaseVersion;
import com.gg.reader.api.protocol.gx.MsgBaseDestroyEpc;
import com.gg.reader.api.protocol.gx.MsgBaseGetBaseband;
import com.gg.reader.api.protocol.gx.MsgBaseGetFreqRange;
import com.gg.reader.api.protocol.gx.MsgBaseGetFrequency;
import com.gg.reader.api.protocol.gx.MsgBaseGetPower;
import com.gg.reader.api.protocol.gx.MsgBaseInventory6b;
import com.gg.reader.api.protocol.gx.MsgBaseInventoryEpc;
import com.gg.reader.api.protocol.gx.MsgBaseInventoryGJb;
import com.gg.reader.api.protocol.gx.MsgBaseInventoryGb;
import com.gg.reader.api.protocol.gx.MsgBaseLock6b;
import com.gg.reader.api.protocol.gx.MsgBaseLock6bGet;
import com.gg.reader.api.protocol.gx.MsgBaseLockGJb;
import com.gg.reader.api.protocol.gx.MsgBaseSetBaseband;
import com.gg.reader.api.protocol.gx.MsgBaseSetFreqRange;
import com.gg.reader.api.protocol.gx.MsgBaseSetFrequency;
import com.gg.reader.api.protocol.gx.MsgBaseSetPower;
import com.gg.reader.api.protocol.gx.MsgBaseStop;
import com.gg.reader.api.protocol.gx.MsgBaseWrite6b;
import com.gg.reader.api.protocol.gx.MsgBaseWriteEpc;
import com.gg.reader.api.protocol.gx.MsgBaseWriteGJb;
import com.gg.reader.api.protocol.gx.Param6bReadUserdata;
import com.gg.reader.api.protocol.gx.ParamEpcFilter;
import com.gg.reader.api.protocol.gx.ParamEpcReadEpc;
import com.gg.reader.api.protocol.gx.ParamEpcReadReserved;
import com.gg.reader.api.protocol.gx.ParamEpcReadTid;
import com.gg.reader.api.protocol.gx.ParamEpcReadUserdata;
import com.gg.reader.api.protocol.gx.ParamFastId;
import com.gg.reader.api.utils.HexUtils;
import com.handheld.uhfr.Reader;
import com.handheld.uhfr.RrReader;
import com.rfid.trans.ReadTag;
import com.rfid.trans.TagCallback;
import com.uhf.api.cls.R2000_calibration;
import com.uhf.api.cls.ReadListener;
import com.uhf.api.cls.Reader;
import com.uhf.api.cls.Reader.AntPower;
import com.uhf.api.cls.Reader.AntPowerConf;
import com.uhf.api.cls.Reader.CustomParam_ST;
import com.uhf.api.cls.Reader.Default_Param;
import com.uhf.api.cls.Reader.EmbededData_ST;
import com.uhf.api.cls.Reader.HardwareDetails;
import com.uhf.api.cls.Reader.HoptableData_ST;
import com.uhf.api.cls.Reader.Inv_Potl;
import com.uhf.api.cls.Reader.Inv_Potls_ST;
import com.uhf.api.cls.Reader.TAGINFO;
import com.uhf.api.cls.Reader.TagFilter_ST;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import kotlinx.coroutines.scheduling.WorkQueueKt;
import org.apache.log4j.spi.Configurator;

/* loaded from: classes.dex */
public class UHFRManager {
    private static GClient client = null;
    private static Driver driver = null;
    private static final int port = 13;
    private static com.uhf.api.cls.Reader reader = null;
    private static SerialPort sSerialPort = null;
    private static final String tag = "UHFRManager";
    int Emboption;
    private int Q;
    int count;
    public Reader.deviceVersion dv;
    private CusParamFilter filter;
    private boolean isEmb;
    Reader.TAGINFO taginfo;
    private static List<LogBaseEpcInfo> epcList = new ArrayList();
    private static List<LogBaseGbInfo> gbepcList = new ArrayList();
    private static List<LogBaseGJbInfo> gjbepcList = new ArrayList();
    private static List<LogBase6bInfo> tag6bList = new ArrayList();
    private static final List<ReadTag> rrTagList = new ArrayList();
    public static final Object waitLock = new Object();
    private static final MsgCallback callback = new MsgCallback();
    public static Reader.READER_ERR mErr = Reader.READER_ERR.MT_CMD_FAILED_ERR;
    private static int type = -1;
    private static boolean DEBUG = false;
    private static UHFRManager uhfrManager = null;
    private static boolean isE710 = false;
    private static long lastEnterTime = SystemClock.elapsedRealtime();
    private final int[] ants = {1};
    private final int ant = 1;
    String[] spiperst = {"0%", "5%", "10%", "15%", "20%", "25%", "30%", "35%", "40%", "45%", "50%"};
    private ParamFastId fastId = new ParamFastId();
    private int rPower = 0;
    private int wPower = 0;
    private List<Reader.TAGINFO> listTag = new ArrayList();
    private ReadListener readListener = new ReadListener() { // from class: com.handheld.uhfr.UHFRManager.1
        @Override // com.uhf.api.cls.ReadListener
        public void tagRead(com.uhf.api.cls.Reader reader2, Reader.TAGINFO[] taginfos) {
            synchronized (UHFRManager.this.listTag) {
                if (taginfos != null) {
                    if (taginfos.length > 0) {
                        Collections.addAll(UHFRManager.this.listTag, taginfos);
                    }
                }
            }
        }
    };

    public UHFRManager() {
        com.uhf.api.cls.Reader reader2 = new com.uhf.api.cls.Reader();
        reader2.getClass();
        this.taginfo = reader2.new TAGINFO();
        this.count = 0;
        this.Emboption = 0;
        this.isEmb = false;
        this.Q = 0;
    }

    public static void setDebuggable(boolean debuggable) {
        DEBUG = debuggable;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void logPrint(String content) {
        if (DEBUG) {
            Log.i(tag, content);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void logPrint(String tag2, String content) {
        logPrint("[" + tag2 + "]->" + content);
    }

    public static UHFRManager getInstance() {
        long enterTime = SystemClock.elapsedRealtime();
        if (uhfrManager == null) {
            if (connect()) {
                uhfrManager = new UHFRManager();
            } else {
                logPrint("First connect failed, try it again");
                boolean reconnect = connect();
                if (reconnect) {
                    uhfrManager = new UHFRManager();
                }
            }
        }
        long outTime = SystemClock.elapsedRealtime();
        logPrint("Init uhf time: " + (outTime - enterTime));
        return uhfrManager;
    }

    public boolean close() {
        int i = type;
        if (i == 0) {
            GClient gClient = client;
            if (gClient != null) {
                gClient.close();
                client.hdPowerOff();
            }
            client = null;
        } else if (i == 1) {
            com.uhf.api.cls.Reader reader2 = reader;
            if (reader2 != null) {
                reader2.CloseReader();
            }
            reader = null;
        } else if (i == 2) {
            logPrint("zeng-", "type2-close");
            driver.Close_Com();
        } else if (i == 3) {
            int disconnectResult = RrReader.rrlib.DisConnect();
            if (disconnectResult == 0) {
                new SerialPort().power_5Voff();
                uhfrManager = null;
                logPrint("Close power of rr reader");
                return true;
            }
            logPrint("Rr close error: " + disconnectResult);
        }
        new SerialPort().power_5Voff();
        uhfrManager = null;
        logPrint("Close power of reader");
        return true;
    }

    public String getHardware() {
        int i = type;
        if (i == 0) {
            Objects.requireNonNull(client);
            MsgAppGetBaseVersion msg = new MsgAppGetBaseVersion();
            client.sendSynMsg(msg);
            logPrint("MsgAppGetBaseVersion", msg.getRtMsg());
            if (msg.getRtCode() != 0) {
                return null;
            }
            String[] arrays = msg.getBaseVersions().split("\\.");
            if (arrays.length <= 2) {
                return null;
            }
            String version = "1.1.01." + arrays[2];
            return version;
        }
        if (i == 1) {
            com.uhf.api.cls.Reader reader2 = reader;
            reader2.getClass();
            Reader.HardwareDetails val = reader2.new HardwareDetails();
            Reader.READER_ERR er = reader.GetHardwareDetails(val);
            if (er != Reader.READER_ERR.MT_OK_ERR) {
                return null;
            }
            String version2 = "1.1.02." + val.module.value();
            return version2;
        }
        if (i == 2) {
            return "1.1.03";
        }
        if (i != 3) {
            return null;
        }
        String str = RrReader.getVersion();
        String version3 = String.format("1.1.04.%s", str);
        return version3;
    }

    private static boolean connect() {
        Reader.READER_ERR InitReader_Notype;
        type = -1;
        isE710 = false;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        SerialPort serialPort = null;
        try {
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            try {
                new SerialPort().power_5Von();
                Thread.sleep(500L);
                SerialPort serialPort2 = new SerialPort(13, 115200, 0);
                OutputStream outputStream2 = serialPort2.getOutputStream();
                outputStream2.write(Tools.HexString2Bytes("5A000101010000EBD5"));
                outputStream2.flush();
                Thread.sleep(20L);
                byte[] bArr = new byte[128];
                InputStream inputStream2 = serialPort2.getInputStream();
                String Bytes2HexString = Tools.Bytes2HexString(bArr, inputStream2.read(bArr));
                logPrint("zeng-", "retStr0:" + Bytes2HexString);
                if (Bytes2HexString.length() >= 10 && Bytes2HexString.contains("5A00010101")) {
                    type = 0;
                } else {
                    outputStream2.write(Tools.HexString2Bytes("FF00031D0C"));
                    outputStream2.flush();
                    Thread.sleep(20L);
                    String Bytes2HexString2 = Tools.Bytes2HexString(bArr, inputStream2.read(bArr));
                    logPrint("zeng-", "retStr1:" + Bytes2HexString2);
                    if (Bytes2HexString2.length() > 40) {
                        type = 1;
                        isE710 = false;
                    } else {
                        serialPort2.close(13);
                        serialPort2 = new SerialPort(13, 921600, 0);
                        outputStream2 = serialPort2.getOutputStream();
                        inputStream2 = serialPort2.getInputStream();
                        outputStream2.write(Tools.HexString2Bytes("FF00031D0C"));
                        outputStream2.flush();
                        Thread.sleep(20L);
                        String Bytes2HexString3 = Tools.Bytes2HexString(bArr, inputStream2.read(bArr));
                        logPrint("zeng-", "retStr2:" + Bytes2HexString3);
                        if (Bytes2HexString3.length() > 40) {
                            type = 1;
                            isE710 = true;
                        } else {
                            SystemClock.sleep(80L);
                            serialPort2 = new SerialPort(13, 921600, 0);
                            outputStream2 = serialPort2.getOutputStream();
                            inputStream2 = serialPort2.getInputStream();
                            outputStream2.write(Tools.HexString2Bytes("04004C3AD2"));
                            outputStream2.flush();
                            SystemClock.sleep(10L);
                            String Bytes2HexString4 = Tools.Bytes2HexString(bArr, inputStream2.read(bArr));
                            logPrint("connect", "retStr3(921600): " + Bytes2HexString4);
                            if (Bytes2HexString4.length() > 10) {
                                type = 3;
                                outputStream2.write(Tools.HexString2Bytes("05002806B3E5"));
                                outputStream2.flush();
                                SystemClock.sleep(50L);
                                logPrint("connect", "rr switch to 115200: " + Tools.Bytes2HexString(bArr, inputStream2.read(bArr)));
                            } else {
                                serialPort2 = new SerialPort(13, 115200, 0);
                                outputStream2 = serialPort2.getOutputStream();
                                inputStream2 = serialPort2.getInputStream();
                                outputStream2.write(Tools.HexString2Bytes("04004C3AD2"));
                                outputStream2.flush();
                                SystemClock.sleep(10L);
                                String Bytes2HexString5 = Tools.Bytes2HexString(bArr, inputStream2.read(bArr));
                                logPrint("connect", "retStr3(115200): " + Bytes2HexString5);
                                if (Bytes2HexString5.length() > 10) {
                                    type = 3;
                                } else {
                                    outputStream2.write(Tools.HexString2Bytes("A55A000902000B0D0A"));
                                    outputStream2.flush();
                                    Thread.sleep(20L);
                                    String Bytes2HexString6 = Tools.Bytes2HexString(bArr, inputStream2.read(bArr));
                                    logPrint("connect", "retStr4: " + Bytes2HexString6);
                                    if (Bytes2HexString6.length() > 10) {
                                        type = 2;
                                    }
                                }
                            }
                        }
                    }
                }
                if (outputStream2 != null) {
                    outputStream2.close();
                }
                if (inputStream2 != null) {
                    inputStream2.close();
                }
                serialPort2.close(13);
            } catch (IOException | InterruptedException e2) {
                e2.printStackTrace();
                if (0 != 0) {
                    outputStream.close();
                }
                if (0 != 0) {
                    inputStream.close();
                }
                if (0 != 0) {
                    serialPort.close(13);
                }
            }
            logPrint("Zeng-", "type:" + type);
            int i = type;
            if (i == 0) {
                GClient gClient = new GClient();
                client = gClient;
                if (gClient.openHdSerial("13:115200", 0)) {
                    onTagHandler();
                    client.hdPowerOn();
                    try {
                        Thread.sleep(100L);
                        return true;
                    } catch (InterruptedException e3) {
                        e3.printStackTrace();
                        return true;
                    }
                }
            } else if (i == 1) {
                reader = new com.uhf.api.cls.Reader();
                long elapsedRealtime = SystemClock.elapsedRealtime();
                logPrint("Zeng-", "isE710:" + isE710);
                if (isE710) {
                    InitReader_Notype = reader.InitReader_Notype("/dev/ttyMT1:921600", 1);
                } else {
                    InitReader_Notype = reader.InitReader_Notype("/dev/ttyMT1", 1);
                }
                Log.i("zeng-", "InitReader cusTime: " + (SystemClock.elapsedRealtime() - elapsedRealtime));
                if (InitReader_Notype == Reader.READER_ERR.MT_OK_ERR && connect2()) {
                    return true;
                }
                reader.CloseReader();
            } else if (i == 2) {
                RfidDriver rfidDriver = new RfidDriver();
                driver = rfidDriver;
                int initRFID = rfidDriver.initRFID("/dev/ttyMT1", 115200);
                logPrint("zeng-", "init+status:" + initRFID);
                if (-1000 != initRFID) {
                    return true;
                }
            } else if (i == 3) {
                int connect = RrReader.connect("/dev/ttyMT1", 115200, DEBUG ? 1 : 0);
                if (connect == 0) {
                    RrReader.rrlib.SetCallBack(callback);
                    return true;
                }
                logPrint("Rr connect error: " + connect);
            }
            new SerialPort().power_5Voff();
            return false;
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    outputStream.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                    throw th;
                }
            }
            if (0 != 0) {
                inputStream.close();
            }
            if (0 == 0) {
                throw th;
            }
            serialPort.close(13);
            throw th;
        }
    }

    private static boolean connectE710() {
        try {
            new SerialPort().power_5Von();
            Thread.sleep(500L);
            SerialPort serialPort = new SerialPort(13, 921600, 0);
            OutputStream outputStream = serialPort.getOutputStream();
            InputStream inputStream = serialPort.getInputStream();
            outputStream.write(Tools.HexString2Bytes("FF00031D0C"));
            outputStream.flush();
            Thread.sleep(20L);
            byte[] bytes = new byte[128];
            int read = inputStream.read(bytes);
            String retStr = Tools.Bytes2HexString(bytes, read);
            if (retStr.length() > 10) {
                logPrint("connect", "connectE710 xinlian retStr: " + retStr);
                type = 1;
            }
            serialPort.close(13);
        } catch (Exception e) {
        }
        if (type == 1) {
            com.uhf.api.cls.Reader reader2 = new com.uhf.api.cls.Reader();
            reader = reader2;
            Reader.READER_ERR er = reader2.InitReader_Notype("/dev/ttyMT1:921600", 1);
            logPrint("connect", "connectE710 xinlian retStr: " + er.name());
            if (er == Reader.READER_ERR.MT_OK_ERR) {
                connect2();
                isE710 = false;
                return true;
            }
        }
        return false;
    }

    private static boolean connect2() {
        long enterTime = SystemClock.elapsedRealtime();
        com.uhf.api.cls.Reader reader2 = reader;
        reader2.getClass();
        Reader.Inv_Potls_ST ipst = reader2.new Inv_Potls_ST();
        List<Reader.SL_TagProtocol> ltp = new ArrayList<>();
        ltp.add(Reader.SL_TagProtocol.SL_TAG_PROTOCOL_GEN2);
        ipst.potlcnt = ltp.size();
        ipst.potls = new Reader.Inv_Potl[ipst.potlcnt];
        Reader.SL_TagProtocol[] stp = (Reader.SL_TagProtocol[]) ltp.toArray(new Reader.SL_TagProtocol[ipst.potlcnt]);
        for (int i = 0; i < ipst.potlcnt; i++) {
            com.uhf.api.cls.Reader reader3 = reader;
            reader3.getClass();
            Reader.Inv_Potl ipl = reader3.new Inv_Potl();
            ipl.weight = 30;
            ipl.potl = stp[i];
            ipst.potls[0] = ipl;
        }
        Reader.READER_ERR er = reader.ParamSet(Reader.Mtr_Param.MTR_PARAM_TAG_INVPOTL, ipst);
        long outTime = SystemClock.elapsedRealtime();
        Log.i("zeng-", "connect2 cusTime: " + (outTime - enterTime));
        return er == Reader.READER_ERR.MT_OK_ERR;
    }

    public static boolean setBaudrate(int baudtrate) {
        com.uhf.api.cls.Reader reader2 = reader;
        reader2.getClass();
        Reader.Default_Param dp = reader2.new Default_Param();
        dp.isdefault = false;
        dp.key = Reader.Mtr_Param.MTR_PARAM_SAVEINMODULE_BAUD;
        dp.val = Integer.valueOf(baudtrate);
        Reader.READER_ERR er = reader.ParamSet(Reader.Mtr_Param.MTR_PARAM_SAVEINMODULE_BAUD, dp);
        return er == Reader.READER_ERR.MT_OK_ERR;
    }

    public Reader.READER_ERR asyncStartReading() {
        int i = type;
        if (i == 0) {
            MsgBaseGetBaseband getBaseband = new MsgBaseGetBaseband();
            client.sendSynMsg(getBaseband);
            if (getBaseband.getRtCode() == 0) {
                MsgBaseInventoryEpc inventoryEpc = new MsgBaseInventoryEpc();
                inventoryEpc.setAntennaEnable(1L);
                inventoryEpc.setInventoryMode(1);
                CusParamFilter cusParamFilter = this.filter;
                if (cusParamFilter != null && cusParamFilter.isMatching()) {
                    inventoryEpc.setFilter(this.filter.getFilter());
                }
                if (this.fastId.getFastId() != 0) {
                    inventoryEpc.setParamFastId(this.fastId);
                }
                client.sendSynMsg(inventoryEpc);
                logPrint("MsgBaseInventoryEpc", inventoryEpc.getRtMsg());
                return inventoryEpc.getRtCode() == 0 ? Reader.READER_ERR.MT_OK_ERR : Reader.READER_ERR.MT_CMD_FAILED_ERR;
            }
            return Reader.READER_ERR.MT_CMD_FAILED_ERR;
        }
        if (i == 1) {
            if (isE710 && !this.isEmb) {
                logPrint("pang", "E710 AsyncStartReading");
                com.uhf.api.cls.Reader reader2 = reader;
                reader2.getClass();
                Reader.CustomParam_ST cpst = reader2.new CustomParam_ST();
                cpst.ParamName = "Reader/Ex10fastmode";
                byte[] vals = new byte[22];
                vals[0] = 1;
                vals[1] = 20;
                for (int i2 = 0; i2 < 20; i2++) {
                    vals[i2 + 2] = 0;
                }
                cpst.ParamVal = vals;
                reader.ParamSet(Reader.Mtr_Param.MTR_PARAM_CUSTOM, cpst);
                return reader.AsyncStartReading(this.ants, 1, 0);
            }
            int session = getGen2session();
            logPrint("pang", "AsyncStartReading");
            int option = 16;
            if (session == 1) {
                if (this.isEmb) {
                    option = this.Emboption;
                }
                return reader.AsyncStartReading(this.ants, 1, option);
            }
            int option2 = 0;
            if (this.isEmb) {
                option2 = this.Emboption;
            }
            return reader.AsyncStartReading(this.ants, 1, option2);
        }
        if (i == 2) {
            driver.Inventory_Model_Set(0, true);
            int Status = driver.readMore(0);
            if (Status != 1020) {
                return Reader.READER_ERR.MT_CMD_FAILED_ERR;
            }
            return Reader.READER_ERR.MT_OK_ERR;
        }
        if (i == 3) {
            List<ReadTag> list = rrTagList;
            synchronized (list) {
                list.clear();
            }
            int startReadResult = RrReader.startRead();
            if (startReadResult == 0) {
                return Reader.READER_ERR.MT_OK_ERR;
            }
            logPrint("Rr async start reading error:" + startReadResult);
        }
        return Reader.READER_ERR.MT_CMD_FAILED_ERR;
    }

    public Reader.READER_ERR asyncStartReading(int option) {
        int i = type;
        if (i == 0) {
            MsgBaseGetBaseband getBaseband = new MsgBaseGetBaseband();
            client.sendSynMsg(getBaseband);
            if (getBaseband.getRtCode() == 0) {
                MsgBaseInventoryEpc inventoryEpc = new MsgBaseInventoryEpc();
                inventoryEpc.setAntennaEnable(1L);
                inventoryEpc.setInventoryMode(1);
                CusParamFilter cusParamFilter = this.filter;
                if (cusParamFilter != null && cusParamFilter.isMatching()) {
                    inventoryEpc.setFilter(this.filter.getFilter());
                }
                if (this.fastId.getFastId() != 0) {
                    inventoryEpc.setParamFastId(this.fastId);
                }
                client.sendSynMsg(inventoryEpc);
                logPrint("MsgBaseInventoryEpc", inventoryEpc.getRtMsg());
                return inventoryEpc.getRtCode() == 0 ? Reader.READER_ERR.MT_OK_ERR : Reader.READER_ERR.MT_CMD_FAILED_ERR;
            }
            return Reader.READER_ERR.MT_CMD_FAILED_ERR;
        }
        if (i == 1) {
            if (isE710) {
                logPrint("pang", "AsyncStartReading");
                com.uhf.api.cls.Reader reader2 = reader;
                reader2.getClass();
                Reader.CustomParam_ST cpst = reader2.new CustomParam_ST();
                cpst.ParamName = "Reader/Ex10fastmode";
                byte[] vals = new byte[22];
                vals[0] = 1;
                vals[1] = 20;
                for (int i2 = 0; i2 < 20; i2++) {
                    vals[i2 + 2] = 0;
                }
                cpst.ParamVal = vals;
                reader.ParamSet(Reader.Mtr_Param.MTR_PARAM_CUSTOM, cpst);
                return reader.AsyncStartReading(this.ants, 1, 0);
            }
            return reader.AsyncStartReading(this.ants, 1, option);
        }
        if (i == 2) {
            setGen2("0140FD5300000000");
            int Status = driver.readMore(0);
            if (Status != 1020) {
                return Reader.READER_ERR.MT_CMD_FAILED_ERR;
            }
            return Reader.READER_ERR.MT_OK_ERR;
        }
        if (i == 3) {
            return asyncStartReading();
        }
        return Reader.READER_ERR.MT_CMD_FAILED_ERR;
    }

    public Reader.READER_ERR asyncStopReading() {
        int i = type;
        if (i == 0) {
            MsgBaseStop stop = new MsgBaseStop();
            client.sendSynMsg(stop);
            logPrint("MsgBaseStop", stop.getRtMsg());
            return stop.getRtCode() == 0 ? Reader.READER_ERR.MT_OK_ERR : Reader.READER_ERR.MT_CMD_FAILED_ERR;
        }
        if (i == 1) {
            if (isE710) {
                Reader.READER_ERR er = reader.AsyncStopReading();
                logPrint("pang", "asyncStopReading");
                return er;
            }
            return reader.AsyncStopReading();
        }
        if (i == 2) {
            driver.stopRead();
            logPrint("zeng-", "cont:" + this.count);
            return Reader.READER_ERR.MT_OK_ERR;
        }
        if (i == 3) {
            RrReader.stopRead();
            return Reader.READER_ERR.MT_OK_ERR;
        }
        return Reader.READER_ERR.MT_CMD_FAILED_ERR;
    }

    public Reader.READER_ERR InventoryFilters() {
        if (type == 1) {
            int session = getGen2session();
            logPrint("pang", "AsyncStartReading");
            int option = 16;
            if (session == 1) {
                if (this.isEmb) {
                    option = this.Emboption;
                }
                return reader.AsyncStartReading(this.ants, 1, option);
            }
            int option2 = 0;
            if (this.isEmb) {
                option2 = this.Emboption;
            }
            return reader.AsyncStartReading(this.ants, 1, option2);
        }
        return Reader.READER_ERR.MT_CMD_FAILED_ERR;
    }

    public boolean setInventoryFilters(String[] mepc) {
        int i = type;
        if (i != 0) {
            if (i == 1) {
                Reader.READER_ERR er = reader.ParamSet(Reader.Mtr_Param.MTR_PARAM_TAG_MULTISELECTORS, mepc);
                if (er == Reader.READER_ERR.MT_OK_ERR) {
                    return true;
                }
                logPrint("setInventoryFilters, ParamSet MTR_PARAM_TAG_FILTER result: " + er.toString());
                return false;
            }
            if (i == 2 || i == 3) {
                return true;
            }
        }
        return false;
    }

    public boolean setInventoryFilter(byte[] fdata, int fbank, int fstartaddr, boolean matching) {
        int i = type;
        if (i == 0) {
            ParamEpcFilter paramEpcFilter = new ParamEpcFilter();
            paramEpcFilter.setArea(fbank);
            paramEpcFilter.setBitStart(fstartaddr * 16);
            paramEpcFilter.setbData(fdata);
            paramEpcFilter.setBitLength(fdata.length * 8);
            this.filter = new CusParamFilter(paramEpcFilter, matching);
            return true;
        }
        if (i == 1) {
            com.uhf.api.cls.Reader reader2 = reader;
            reader2.getClass();
            Reader.TagFilter_ST g2tf = reader2.new TagFilter_ST();
            g2tf.fdata = fdata;
            g2tf.flen = fdata.length * 8;
            if (matching) {
                g2tf.isInvert = 0;
            } else {
                g2tf.isInvert = 1;
            }
            g2tf.bank = fbank;
            g2tf.startaddr = fstartaddr * 16;
            Reader.READER_ERR er = reader.ParamSet(Reader.Mtr_Param.MTR_PARAM_TAG_FILTER, g2tf);
            if (er == Reader.READER_ERR.MT_OK_ERR) {
                return true;
            }
            logPrint("setInventoryFilter, ParamSet MTR_PARAM_TAG_FILTER result: " + er.toString());
            return false;
        }
        if (i == 2) {
            return true;
        }
        if (i != 3) {
            return false;
        }
        RrReader.setInvMask(fdata, fbank, fstartaddr, matching);
        return true;
    }

    public boolean setCancleInventoryFilter() {
        int i = type;
        if (i == 0) {
            this.filter = null;
            return true;
        }
        if (i == 1) {
            Reader.READER_ERR er = reader.ParamSet(Reader.Mtr_Param.MTR_PARAM_TAG_FILTER, null);
            if (er == Reader.READER_ERR.MT_OK_ERR) {
                return true;
            }
            logPrint("setCancleInventoryFilter, ParamSet MTR_PARAM_TAG_FILTER result: " + er.toString());
            return false;
        }
        if (i == 2) {
            return true;
        }
        if (i != 3) {
            return false;
        }
        RrReader.rrlib.ClearMaskList();
        return true;
    }

    public List<LogBaseGbInfo> formatGBData(int bank) {
        List<LogBaseGbInfo> mgblist;
        synchronized (gbepcList) {
            mgblist = new ArrayList<>();
            mgblist.clear();
            List<LogBaseGbInfo> list = gbepcList;
            if (list != null && !list.isEmpty()) {
                mgblist.addAll(gbepcList);
            }
            gbepcList.clear();
        }
        return mgblist;
    }

    public List<LogBase6bInfo> format6BData() {
        List<LogBase6bInfo> mgblist;
        synchronized (tag6bList) {
            mgblist = new ArrayList<>();
            mgblist.clear();
            List<LogBase6bInfo> list = tag6bList;
            if (list != null && !list.isEmpty()) {
                mgblist.addAll(tag6bList);
            }
            tag6bList.clear();
        }
        return mgblist;
    }

    public List<LogBaseGJbInfo> formatGJBData(int bank) {
        List<LogBaseGJbInfo> mgblist;
        synchronized (gjbepcList) {
            mgblist = new ArrayList<>();
            mgblist.clear();
            List<LogBaseGJbInfo> list = gjbepcList;
            if (list != null && !list.isEmpty()) {
                mgblist.addAll(gjbepcList);
            }
            gjbepcList.clear();
        }
        return mgblist;
    }

    public List<Reader.TAGINFO> formatData(int bank) {
        ArrayList arrayList;
        synchronized (epcList) {
            HashMap<String, Reader.TAGINFO> tagMap = new HashMap<>();
            for (LogBaseEpcInfo info : epcList) {
                com.uhf.api.cls.Reader reader2 = new com.uhf.api.cls.Reader();
                reader2.getClass();
                Reader.TAGINFO taginfo = reader2.new TAGINFO();
                taginfo.AntennaID = (byte) info.getAntId();
                if (info.getFrequencyPoint() != null) {
                    taginfo.Frequency = info.getFrequencyPoint().intValue();
                }
                if (info.getReplySerialNumber() != null) {
                    taginfo.TimeStamp = info.getReplySerialNumber().intValue();
                }
                if (bank != 0) {
                    if (bank != 1) {
                        if (bank == 2) {
                            if (info.getTid() != null) {
                                taginfo.EmbededData = info.getbTid();
                                taginfo.EmbededDatalen = (short) info.getbTid().length;
                            }
                        } else if (bank == 3 && info.getUserdata() != null) {
                            taginfo.EmbededData = info.getbUser();
                            taginfo.EmbededDatalen = (short) info.getbUser().length;
                        }
                    } else if (info.getEpcData() != null) {
                        taginfo.EmbededData = info.getbEpcData();
                        taginfo.EmbededDatalen = (short) info.getbEpcData().length;
                    }
                } else if (info.getReserved() != null) {
                    taginfo.EmbededData = info.getbRes();
                    taginfo.EmbededDatalen = (short) info.getbRes().length;
                }
                taginfo.EpcId = info.getbEpc();
                taginfo.Epclen = (short) info.getbEpc().length;
                taginfo.PC = HexUtils.int2Bytes(info.getPc());
                if (info.getCrc() != 0) {
                    taginfo.CRC = HexUtils.int2Bytes(info.getCrc());
                }
                taginfo.protocol = Reader.SL_TagProtocol.SL_TAG_PROTOCOL_GEN2;
                taginfo.Phase = info.getPhase();
                double v = (log2(info.getRssi()) * 6.0d) - 39.9d;
                taginfo.RSSI = (int) Math.round(v);
                if (info.getTid() != null) {
                    if (!tagMap.containsKey(info.getTid())) {
                        taginfo.ReadCnt = 1;
                    } else {
                        Reader.TAGINFO temp = tagMap.get(info.getTid());
                        if (temp != null) {
                            temp.ReadCnt++;
                            tagMap.put(info.getTid(), temp);
                        }
                    }
                    tagMap.put(info.getTid(), taginfo);
                } else if (!tagMap.containsKey(info.getEpc())) {
                    taginfo.ReadCnt = 1;
                    tagMap.put(info.getEpc(), taginfo);
                } else {
                    Reader.TAGINFO temp2 = tagMap.get(info.getEpc());
                    if (temp2 != null) {
                        temp2.ReadCnt++;
                        tagMap.put(info.getEpc(), temp2);
                    }
                }
            }
            epcList.clear();
            arrayList = new ArrayList(tagMap.values());
        }
        return arrayList;
    }

    private double log2(double N) {
        return Math.log(N / 190.0d) / Math.log(2.0d);
    }

    public List<Reader.TEMPTAGINFO> formatData() {
        ArrayList arrayList;
        synchronized (epcList) {
            HashMap<String, Reader.TEMPTAGINFO> tagMap = new HashMap<>();
            for (LogBaseEpcInfo info : epcList) {
                Reader.TEMPTAGINFO taginfo = new Reader.TEMPTAGINFO();
                taginfo.AntennaID = (byte) info.getAntId();
                if (info.getFrequencyPoint() != null) {
                    taginfo.Frequency = info.getFrequencyPoint().intValue();
                }
                if (info.getReplySerialNumber() != null) {
                    taginfo.TimeStamp = info.getReplySerialNumber().intValue();
                }
                if (info.getUserdata() != null) {
                    logPrint("pang", "pang, " + info.getUserdata());
                    String userdata = info.getUserdata();
                    if (userdata != null && !EnumG.MSG_TYPE_BIT_ERROR.equals(userdata) && userdata.length() > 2) {
                        int integer = Integer.parseInt(userdata.substring(0, 2), 16);
                        double parseInt = Integer.parseInt(userdata.substring(2, 4), 16);
                        Double.isNaN(parseInt);
                        double round = Math.round((parseInt / 255.0d) * 100.0d);
                        Double.isNaN(round);
                        double decimal = round / 100.0d;
                        if (integer > 45) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("temp = ");
                            double d = integer - 45;
                            Double.isNaN(d);
                            sb.append(d + decimal);
                            logPrint("temp ", sb.toString());
                            double d2 = integer - 45;
                            Double.isNaN(d2);
                            taginfo.Temperature = d2 + decimal;
                        } else {
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("temp = -");
                            double d3 = 45 - integer;
                            Double.isNaN(d3);
                            sb2.append(d3 + decimal);
                            logPrint("temp ", sb2.toString());
                            double d4 = 45 - integer;
                            Double.isNaN(d4);
                            taginfo.Temperature = -(d4 + decimal);
                        }
                        taginfo.EpcId = info.getbEpc();
                        taginfo.Epclen = (short) info.getbEpc().length;
                        taginfo.PC = HexUtils.int2Bytes(info.getPc());
                        if (info.getCrc() != 0) {
                            taginfo.CRC = HexUtils.int2Bytes(info.getCrc());
                        }
                        taginfo.protocol = Reader.SL_TagProtocol.SL_TAG_PROTOCOL_GEN2;
                        taginfo.Phase = info.getPhase();
                        double v = (log2(info.getRssi()) * 6.0d) - 39.9d;
                        taginfo.RSSI = (int) Math.round(v);
                        if (info.getTid() != null) {
                            if (!tagMap.containsKey(info.getTid())) {
                                taginfo.ReadCnt = 1;
                            } else {
                                Reader.TEMPTAGINFO temp = tagMap.get(info.getTid());
                                if (temp != null) {
                                    temp.ReadCnt++;
                                    tagMap.put(info.getTid(), temp);
                                }
                            }
                            tagMap.put(info.getTid(), taginfo);
                        } else if (!tagMap.containsKey(info.getEpc())) {
                            taginfo.ReadCnt = 1;
                            tagMap.put(info.getEpc(), taginfo);
                        } else {
                            Reader.TEMPTAGINFO temp2 = tagMap.get(info.getEpc());
                            if (temp2 != null) {
                                temp2.ReadCnt++;
                                tagMap.put(info.getEpc(), temp2);
                            }
                        }
                    }
                }
            }
            epcList.clear();
            arrayList = new ArrayList(tagMap.values());
        }
        return arrayList;
    }

    public List<Reader.TEMPTAGINFO> formatYueheData() {
        ArrayList arrayList;
        synchronized (epcList) {
            HashMap<String, Reader.TEMPTAGINFO> tagMap = new HashMap<>();
            for (LogBaseEpcInfo info : epcList) {
                Reader.TEMPTAGINFO taginfo = new Reader.TEMPTAGINFO();
                taginfo.AntennaID = (byte) info.getAntId();
                if (info.getFrequencyPoint() != null) {
                    taginfo.Frequency = info.getFrequencyPoint().intValue();
                }
                if (info.getReplySerialNumber() != null) {
                    taginfo.TimeStamp = info.getReplySerialNumber().intValue();
                }
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(2);
                double ctesiusLtu31 = info.getCtesiusLtu31();
                Double.isNaN(ctesiusLtu31);
                taginfo.Temperature = Double.valueOf(nf.format(ctesiusLtu31 * 0.01d)).doubleValue();
                taginfo.EpcId = info.getbEpc();
                taginfo.Epclen = (short) info.getbEpc().length;
                taginfo.PC = HexUtils.int2Bytes(info.getPc());
                if (info.getCrc() != 0) {
                    taginfo.CRC = HexUtils.int2Bytes(info.getCrc());
                }
                taginfo.protocol = Reader.SL_TagProtocol.SL_TAG_PROTOCOL_GEN2;
                taginfo.Phase = info.getPhase();
                double v = (log2(info.getRssi()) * 6.0d) - 39.9d;
                taginfo.RSSI = (int) Math.round(v);
                if (info.getTid() != null) {
                    if (!tagMap.containsKey(info.getTid())) {
                        taginfo.ReadCnt = 1;
                    } else {
                        Reader.TEMPTAGINFO temp = tagMap.get(info.getTid());
                        if (temp != null) {
                            temp.ReadCnt++;
                            tagMap.put(info.getTid(), temp);
                        }
                    }
                    tagMap.put(info.getTid(), taginfo);
                } else if (!tagMap.containsKey(info.getEpc())) {
                    taginfo.ReadCnt = 1;
                    tagMap.put(info.getEpc(), taginfo);
                } else {
                    Reader.TEMPTAGINFO temp2 = tagMap.get(info.getEpc());
                    if (temp2 != null) {
                        temp2.ReadCnt++;
                        tagMap.put(info.getEpc(), temp2);
                    }
                }
            }
            epcList.clear();
            arrayList = new ArrayList(tagMap.values());
        }
        return arrayList;
    }

    private List<Reader.TEMPTAGINFO> handleYilian(int type2, List<Reader.TAGINFO> epclist) {
        List<Reader.TEMPTAGINFO> list = new ArrayList<>();
        if (epclist != null && !epclist.isEmpty()) {
            for (int i = 0; i < epclist.size(); i++) {
                if (epclist.get(i).EmbededData != null) {
                    Reader.TEMPTAGINFO taginfo = new Reader.TEMPTAGINFO();
                    String userdata = Tools.Bytes2HexString(epclist.get(i).EmbededData, epclist.get(i).EmbededData.length);
                    if (userdata != null && !EnumG.MSG_TYPE_BIT_ERROR.equals(userdata) && userdata.length() > 2) {
                        int integer = Integer.parseInt(userdata.substring(0, 2), 16);
                        double parseInt = Integer.parseInt(userdata.substring(2, 4), 16);
                        Double.isNaN(parseInt);
                        double round = Math.round((parseInt / 255.0d) * 100.0d);
                        Double.isNaN(round);
                        double decimal = round / 100.0d;
                        if (integer > 45) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("temp = ");
                            double d = integer - 45;
                            Double.isNaN(d);
                            sb.append(d + decimal);
                            logPrint("temp ", sb.toString());
                            double d2 = integer - 45;
                            Double.isNaN(d2);
                            taginfo.Temperature = d2 + decimal;
                        } else {
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("temp = -");
                            double d3 = 45 - integer;
                            Double.isNaN(d3);
                            sb2.append(d3 + decimal);
                            logPrint("temp ", sb2.toString());
                            double d4 = 45 - integer;
                            Double.isNaN(d4);
                            taginfo.Temperature = -(d4 + decimal);
                        }
                        byte[] epcId = epclist.get(i).EpcId;
                        if (epcId == null) {
                            epcId = new byte[0];
                        }
                        taginfo.EpcId = epcId;
                        taginfo.Epclen = (short) epcId.length;
                        if (type2 == 1) {
                            taginfo.PC = epclist.get(i).PC;
                            taginfo.AntennaID = epclist.get(i).AntennaID;
                            taginfo.Frequency = epclist.get(i).Frequency;
                            taginfo.RSSI = epclist.get(i).RSSI;
                        }
                        list.add(taginfo);
                    }
                }
            }
        }
        return list;
    }

    public List<Reader.TAGINFO> formatExcludeData(int bank, byte[] fData) {
        List<Reader.TAGINFO> tagInfos = formatData(bank);
        List<Reader.TAGINFO> temp = new ArrayList<>();
        for (Reader.TAGINFO info : tagInfos) {
            if (!HexUtils.bytes2HexString(info.EmbededData).equals(HexUtils.bytes2HexString(fData))) {
                temp.add(info);
            }
        }
        return temp;
    }

    public List<Reader.TAGINFO> tagInventoryRealTime() {
        List<Reader.TAGINFO> list = new ArrayList<>();
        int i = type;
        if (i == 0) {
            int bank = 4;
            if (this.fastId.getFastId() != 0) {
                bank = 2;
            }
            CusParamFilter cusParamFilter = this.filter;
            if (cusParamFilter != null && !cusParamFilter.isMatching()) {
                return formatExcludeData(bank, this.filter.getFilter().getbData());
            }
            return formatData(bank);
        }
        if (i == 1) {
            int[] tagcnt = new int[1];
            Reader.READER_ERR er = reader.AsyncGetTagCount(tagcnt);
            if (er != Reader.READER_ERR.MT_OK_ERR) {
                mErr = er;
                return null;
            }
            for (int i2 = 0; i2 < tagcnt[0]; i2++) {
                com.uhf.api.cls.Reader reader2 = reader;
                reader2.getClass();
                Reader.TAGINFO tfs = reader2.new TAGINFO();
                if (reader.AsyncGetNextTag(tfs) == Reader.READER_ERR.MT_OK_ERR) {
                    list.add(tfs);
                }
            }
        } else if (i == 2) {
            String s = driver.GetBufData();
            logPrint("zeng-", "count = " + this.count + ", s:getBufData:" + s);
            if (s != null && !s.equals(Configurator.NULL)) {
                list.add(getBuf(s));
            }
        } else if (i == 3) {
            return formatRrTagList();
        }
        return list;
    }

    public Reader.TAGINFO getBuf(String getBuffString) {
        com.uhf.api.cls.Reader reader2 = new com.uhf.api.cls.Reader();
        reader2.getClass();
        Reader.TAGINFO tfs = reader2.new TAGINFO();
        int rssi = 0;
        new HashMap();
        String text = getBuffString.substring(4);
        String len = getBuffString.substring(0, 2);
        int epclen = (Integer.parseInt(len, 16) / 8) * 4;
        String[] tmp = {text.substring(epclen, text.length() - 6), text.substring(0, text.length() - 6), text.substring(text.length() - 6, text.length() - 2)};
        if (4 == tmp[2].length()) {
            int Hb = Integer.parseInt(tmp[2].substring(0, 2), 16);
            int Lb = Integer.parseInt(tmp[2].substring(2, 4), 16);
            rssi = ((((Hb + InputDeviceCompat.SOURCE_ANY) + 1) * 256) + (Lb + InputDeviceCompat.SOURCE_ANY)) / 10;
        } else {
            tmp[2] = EnumG.MSG_TYPE_BIT_ERROR;
        }
        tfs.EpcId = Tools.HexString2Bytes(tmp[1]);
        tfs.Epclen = (short) (tmp[1].length() / 4);
        tfs.RSSI = Integer.valueOf(rssi).intValue();
        this.count++;
        return tfs;
    }

    public boolean stopTagInventory() {
        int i = type;
        if (i == 0) {
            Reader.READER_ERR reader_err = asyncStopReading();
            return reader_err.value() == 0;
        }
        if (i == 1) {
            Reader.READER_ERR er = reader.AsyncStopReading();
            if (er == Reader.READER_ERR.MT_OK_ERR) {
                return true;
            }
            logPrint("stopTagInventory, AsyncStopReading result: " + er.toString());
            return false;
        }
        if (i == 2) {
            Reader.READER_ERR reader_err2 = asyncStopReading();
            return reader_err2.value() == 0;
        }
        if (i != 3) {
            return false;
        }
        RrReader.rrlib.StopRead();
        return true;
    }

    public List<Reader.TAGINFO> tagInventoryByTimer(short readtime) {
        int i = type;
        if (i == 0) {
            MsgBaseInventoryEpc msg = new MsgBaseInventoryEpc();
            msg.setAntennaEnable(1L);
            msg.setInventoryMode(1);
            CusParamFilter cusParamFilter = this.filter;
            if (cusParamFilter != null && cusParamFilter.isMatching()) {
                msg.setFilter(this.filter.getFilter());
            }
            client.sendSynMsg(msg);
            logPrint("MsgBaseInventoryEpc", msg.getRtMsg());
            if (msg.getRtCode() == 0) {
                try {
                    Thread.sleep(readtime);
                    MsgBaseStop stop = new MsgBaseStop();
                    client.sendSynMsg(stop);
                    logPrint("MsgBaseStop", ((int) stop.getRtCode()) + "");
                    CusParamFilter cusParamFilter2 = this.filter;
                    if (cusParamFilter2 != null && !cusParamFilter2.isMatching()) {
                        return formatExcludeData(4, this.filter.getFilter().getbData());
                    }
                    return formatData(4);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        if (i == 1) {
            List<Reader.TAGINFO> list = new ArrayList<>();
            int[] tagcnt = new int[1];
            Reader.READER_ERR er = reader.TagInventory_Raw(this.ants, 1, readtime, tagcnt);
            logPrint("tagInventoryByTimer, TagInventory_Raw er: " + er.toString() + "; tagcnt[0]=" + tagcnt[0]);
            if (er == Reader.READER_ERR.MT_OK_ERR) {
                for (int i2 = 0; i2 < tagcnt[0]; i2++) {
                    com.uhf.api.cls.Reader reader2 = reader;
                    reader2.getClass();
                    Reader.TAGINFO tfs = reader2.new TAGINFO();
                    if (reader.GetNextTag(tfs) == Reader.READER_ERR.MT_OK_ERR) {
                        list.add(tfs);
                    } else {
                        return list;
                    }
                }
                return list;
            }
            mErr = er;
            return null;
        }
        if (i == 2) {
            List<Reader.TAGINFO> list2 = new ArrayList<>();
            com.uhf.api.cls.Reader reader3 = new com.uhf.api.cls.Reader();
            reader3.getClass();
            Reader.TAGINFO taginfo = reader3.new TAGINFO();
            String s = driver.SingleRead(10).trim();
            if (!s.equals("获取失败") && !s.equals(Configurator.NULL)) {
                logPrint("zeng-", "s2:" + s);
                taginfo.EpcId = Tools.HexString2Bytes(s);
                taginfo.Epclen = (short) taginfo.EpcId.length;
                list2.add(taginfo);
            }
            return list2;
        }
        if (i == 3) {
            List<ReadTag> list3 = rrTagList;
            synchronized (list3) {
                list3.clear();
            }
            int scanRfidResult = RrReader.scanRfid(0, 1, 0, 0, "00000000", readtime);
            if (scanRfidResult == 0) {
                return formatRrTagList();
            }
            logPrint("Rr inventory tag by timer error: " + scanRfidResult);
        }
        return null;
    }

    public List<LogBase6bInfo> inventory6BTag(short readtime) {
        if (type != 0) {
            return null;
        }
        MsgBaseInventory6b msg = new MsgBaseInventory6b();
        msg.setAntennaEnable(1L);
        msg.setInventoryMode(1);
        client.sendSynMsg(msg);
        if (msg.getRtCode() != 0) {
            return null;
        }
        try {
            Thread.sleep(readtime);
            MsgBaseStop stop = new MsgBaseStop();
            client.sendSynMsg(stop);
            List<LogBase6bInfo> list = format6BData();
            return list;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] read6BUser(boolean isMatch, byte[] tid, int startAddr, int len) {
        if (type != 0) {
            return null;
        }
        MsgBaseInventory6b msg = new MsgBaseInventory6b();
        msg.setAntennaEnable(1L);
        msg.setInventoryMode(1);
        msg.setArea(2);
        Param6bReadUserdata userdata = new Param6bReadUserdata();
        userdata.setStart(startAddr);
        userdata.setLen(len);
        msg.setReadUserdata(userdata);
        if (isMatch && tid != null) {
            msg.setHexMatchTid(Tools.Bytes2HexString(tid, tid.length));
        }
        client.sendSynMsg(msg);
        if (msg.getRtCode() != 0) {
            return null;
        }
        try {
            Thread.sleep(20L);
            MsgBaseStop stop = new MsgBaseStop();
            client.sendSynMsg(stop);
            List<LogBase6bInfo> list6B = format6BData();
            if (list6B == null || list6B.size() <= 0) {
                return null;
            }
            byte[] data = list6B.get(0).getbUser();
            return data;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean write6BUser(byte[] tid, int startAddr, byte[] data) {
        if (type != 0) {
            return false;
        }
        MsgBaseWrite6b msg = new MsgBaseWrite6b();
        msg.setAntennaEnable(1L);
        msg.setStart(startAddr);
        msg.setbMatchTid(tid);
        msg.setBwriteData(data);
        client.sendSynMsg(msg);
        if (msg.getRtCode() != 0) {
            return false;
        }
        return true;
    }

    public boolean lock6B(byte[] tid, int lockIndex) {
        if (type != 0) {
            return false;
        }
        MsgBaseLock6b msg = new MsgBaseLock6b();
        msg.setAntennaEnable(1L);
        msg.setbMatchTid(tid);
        msg.setLockIndex(lockIndex);
        client.sendSynMsg(msg);
        if (msg.getRtCode() != 0) {
            return false;
        }
        return true;
    }

    public boolean lock6BQuery(byte[] tid, int lockIndex) {
        if (type != 0) {
            return false;
        }
        MsgBaseLock6bGet msg = new MsgBaseLock6bGet();
        msg.setAntennaEnable(1L);
        msg.setbMatchTid(tid);
        msg.setLockIndex(lockIndex);
        client.sendSynMsg(msg);
        if (msg.getRtCode() != 0) {
            return false;
        }
        boolean flag = msg.getLockState() == 0;
        return flag;
    }

    public List<LogBaseGbInfo> inventoryGBTag(boolean isInventoryTid, short readtime) {
        if (type != 0) {
            return null;
        }
        MsgBaseInventoryGb msg = new MsgBaseInventoryGb();
        msg.setAntennaEnable(1L);
        msg.setInventoryMode(1);
        if (isInventoryTid) {
            ParamEpcReadTid tid = new ParamEpcReadTid();
            tid.setMode(0);
            tid.setLen(6);
            msg.setReadTid(tid);
        }
        client.sendSynMsg(msg);
        logPrint("inventoryGBTag", msg.getRtMsg());
        if (msg.getRtCode() == 0) {
            try {
                Thread.sleep(readtime);
                MsgBaseStop stop = new MsgBaseStop();
                client.sendSynMsg(stop);
                logPrint("inventoryGBTag", ((int) stop.getRtCode()) + "");
                CusParamFilter cusParamFilter = this.filter;
                if (cusParamFilter != null) {
                    cusParamFilter.isMatching();
                }
                return formatGBData(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public List<LogBaseGJbInfo> inventoryGJBTag(boolean isInventoryTid, short readtime) {
        if (type != 0) {
            return null;
        }
        MsgBaseInventoryGJb msg = new MsgBaseInventoryGJb();
        msg.setAntennaEnable(1L);
        msg.setInventoryMode(1);
        if (isInventoryTid) {
            ParamEpcReadTid tid = new ParamEpcReadTid();
            tid.setMode(0);
            tid.setLen(6);
            msg.setReadTid(tid);
        }
        client.sendSynMsg(msg);
        logPrint("inventoryGBTag", msg.getRtMsg());
        if (msg.getRtCode() == 0) {
            try {
                Thread.sleep(readtime);
                MsgBaseStop stop = new MsgBaseStop();
                client.sendSynMsg(stop);
                logPrint("inventoryGBTag", ((int) stop.getRtCode()) + "");
                CusParamFilter cusParamFilter = this.filter;
                if (cusParamFilter != null) {
                    cusParamFilter.isMatching();
                }
                return formatGJBData(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public byte[] readGJBUser(int matchType, int matchAddr, byte[] matchData, int readAddr, int readLen, byte[] password) {
        MsgBaseInventoryGJb msg = new MsgBaseInventoryGJb();
        msg.setAntennaEnable(1L);
        msg.setInventoryMode(1);
        if (matchType > 0) {
            ParamEpcFilter filter = new ParamEpcFilter();
            filter.setArea(matchType - 1);
            filter.setBitStart(matchAddr);
            if (matchData != null) {
                filter.setbData(matchData);
                filter.setBitLength(matchData.length * 2);
            }
            msg.setFilter(filter);
        }
        ParamEpcReadUserdata paramEpcReadUserdata = new ParamEpcReadUserdata();
        paramEpcReadUserdata.setStart(readAddr);
        paramEpcReadUserdata.setLen(readLen);
        msg.setReadUserdata(paramEpcReadUserdata);
        if (password != null) {
            msg.setHexPassword(Tools.Bytes2HexString(password, password.length));
        }
        client.sendSynMsg(msg);
        if (msg.getRtCode() != 0) {
            return null;
        }
        try {
            Thread.sleep(20L);
            MsgBaseStop stop = new MsgBaseStop();
            client.sendSynMsg(stop);
            List<LogBaseGJbInfo> listGJB = formatGJBData(2);
            if (listGJB == null || listGJB.size() <= 0) {
                return null;
            }
            byte[] readData = listGJB.get(0).getbUser();
            return readData;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean modifyGJBEPC(int matchType, int matchStartAddr, byte[] matchData, byte[] newEPC, byte[] password) {
        MsgBaseWriteGJb msg = new MsgBaseWriteGJb();
        msg.setAntennaEnable(1L);
        msg.setArea(1);
        msg.setStart(0);
        if (matchType > 0) {
            ParamEpcFilter filter = new ParamEpcFilter();
            filter.setArea(matchType - 1);
            filter.setbData(matchData);
            filter.setBitLength(matchData.length * 2);
            filter.setBitStart(matchStartAddr);
            msg.setFilter(filter);
        }
        if (password != null) {
            msg.setHexPassword(Tools.Bytes2HexString(password, password.length));
        }
        if (newEPC != null) {
            String s = HexUtils.bytes2HexString(newEPC);
            int pcLen = PcUtils.getValueLen(s);
            msg.setHexWriteData(PcUtils.getGJBPc(pcLen) + PcUtils.padRight(s, pcLen * 4, '0'));
        }
        client.sendSynMsg(msg);
        if (msg.getRtCode() != 0) {
            return false;
        }
        return true;
    }

    public boolean writeGJB(int matchType, int matchStartAddr, byte[] matchData, int areaIndex, int startAddr, byte[] writeData, byte[] password) {
        MsgBaseWriteGJb msg = new MsgBaseWriteGJb();
        msg.setAntennaEnable(1L);
        msg.setArea(areaIndex);
        msg.setStart(startAddr);
        if (matchType > 0) {
            ParamEpcFilter filter = new ParamEpcFilter();
            filter.setArea(matchType - 1);
            filter.setbData(matchData);
            filter.setBitLength(matchData.length * 2);
            filter.setBitStart(matchStartAddr);
            msg.setFilter(filter);
        }
        if (password != null) {
            msg.setHexPassword(Tools.Bytes2HexString(password, password.length));
        }
        if (writeData != null) {
            msg.setBwriteData(writeData);
        }
        client.sendSynMsg(msg);
        if (msg.getRtCode() != 0) {
            return false;
        }
        return true;
    }

    public boolean lockGJB(int matchType, int matchStartAddr, byte[] matchData, int lockArea, int lockType, byte[] password) {
        MsgBaseLockGJb msg = new MsgBaseLockGJb();
        msg.setAntennaEnable(1L);
        if (matchType > 0) {
            ParamEpcFilter filter = new ParamEpcFilter();
            filter.setArea(matchType - 1);
            filter.setbData(matchData);
            filter.setBitLength(matchData.length * 2);
            filter.setBitStart(matchStartAddr);
            msg.setFilter(filter);
        }
        msg.setArea(lockArea);
        msg.setLockParam(lockType);
        if (password != null) {
            msg.setHexPassword(Tools.Bytes2HexString(password, password.length));
        }
        return false;
    }

    public List<Reader.TAGINFO> tagEpcTidInventoryByTimer(short readtime) {
        int i = type;
        if (i == 0) {
            MsgBaseInventoryEpc msg = new MsgBaseInventoryEpc();
            msg.setAntennaEnable(1L);
            msg.setInventoryMode(1);
            msg.setReadTid(new ParamEpcReadTid(0, 6));
            CusParamFilter cusParamFilter = this.filter;
            if (cusParamFilter != null && cusParamFilter.isMatching()) {
                msg.setFilter(this.filter.getFilter());
            }
            client.sendSynMsg(msg);
            logPrint("MsgBaseInventoryEpc", msg.getRtMsg());
            if (msg.getRtCode() == 0) {
                try {
                    Thread.sleep(readtime);
                    MsgBaseStop stop = new MsgBaseStop();
                    client.sendSynMsg(stop);
                    logPrint("tagInventoryByTimer", ((int) stop.getRtCode()) + "");
                    CusParamFilter cusParamFilter2 = this.filter;
                    if (cusParamFilter2 != null && !cusParamFilter2.isMatching()) {
                        return formatExcludeData(2, this.filter.getFilter().getbData());
                    }
                    return formatData(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        if (i == 1) {
            List<Reader.TAGINFO> list = new ArrayList<>();
            com.uhf.api.cls.Reader reader2 = reader;
            reader2.getClass();
            Reader.EmbededData_ST edst = reader2.new EmbededData_ST();
            edst.accesspwd = null;
            edst.bank = 2;
            edst.startaddr = 0;
            edst.bytecnt = 12;
            reader.ParamSet(Reader.Mtr_Param.MTR_PARAM_TAG_EMBEDEDDATA, edst);
            int[] tagcnt = new int[1];
            Reader.READER_ERR er = reader.TagInventory_Raw(this.ants, 1, readtime, tagcnt);
            if (er != Reader.READER_ERR.MT_OK_ERR) {
                mErr = er;
                return null;
            }
            for (int i2 = 0; i2 < tagcnt[0]; i2++) {
                com.uhf.api.cls.Reader reader3 = reader;
                reader3.getClass();
                Reader.TAGINFO tfs = reader3.new TAGINFO();
                if (reader.GetNextTag(tfs) != Reader.READER_ERR.MT_OK_ERR) {
                    break;
                }
                list.add(tfs);
            }
            return list;
        }
        if (i != 2 && i == 3) {
            List<ReadTag> list2 = rrTagList;
            synchronized (list2) {
                list2.clear();
            }
            int scanRfidResult = RrReader.scanRfid(1, 2, 0, 6, "00000000", readtime);
            if (scanRfidResult == 0) {
                return formatRrTagList();
            }
            logPrint("Rr inventory tag & tid by timer error: " + scanRfidResult);
        }
        return null;
    }

    public List<Reader.TAGINFO> tagEpcOtherInventoryByTimer(short readtime, int bank, int startaddr, int bytecnt, byte[] accesspwd) {
        int i = type;
        if (i == 0) {
            MsgBaseInventoryEpc msg = new MsgBaseInventoryEpc();
            msg.setAntennaEnable(1L);
            msg.setInventoryMode(1);
            if (bank == 0) {
                msg.setReadReserved(new ParamEpcReadReserved(startaddr, bytecnt));
            } else if (bank == 1) {
                msg.setReadEpc(new ParamEpcReadEpc(startaddr + 2, bytecnt));
            } else if (bank == 2) {
                msg.setReadTid(new ParamEpcReadTid(1, bytecnt));
            } else if (bank == 3) {
                msg.setReadUserdata(new ParamEpcReadUserdata(startaddr, bytecnt));
            }
            msg.setHexPassword(HexUtils.bytes2HexString(accesspwd));
            CusParamFilter cusParamFilter = this.filter;
            if (cusParamFilter != null && cusParamFilter.isMatching()) {
                msg.setFilter(this.filter.getFilter());
            }
            if (this.fastId.getFastId() != 0) {
                msg.setParamFastId(this.fastId);
            }
            client.sendSynMsg(msg);
            logPrint("MsgBaseInventoryEpc", msg.getRtMsg());
            if (msg.getRtCode() == 0) {
                try {
                    Thread.sleep(readtime);
                    MsgBaseStop stop = new MsgBaseStop();
                    client.sendSynMsg(stop);
                    logPrint("tagEpcOtherInventory", ((int) stop.getRtCode()) + "");
                    CusParamFilter cusParamFilter2 = this.filter;
                    if (cusParamFilter2 != null && !cusParamFilter2.isMatching()) {
                        return formatExcludeData(bank, this.filter.getFilter().getbData());
                    }
                    return formatData(bank);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        if (i == 1) {
            List<Reader.TAGINFO> list = new ArrayList<>();
            com.uhf.api.cls.Reader reader2 = reader;
            reader2.getClass();
            Reader.EmbededData_ST edst = reader2.new EmbededData_ST();
            edst.bank = bank;
            edst.startaddr = startaddr;
            edst.bytecnt = bytecnt;
            edst.accesspwd = accesspwd;
            reader.ParamSet(Reader.Mtr_Param.MTR_PARAM_TAG_EMBEDEDDATA, edst);
            int[] tagcnt = new int[1];
            Reader.READER_ERR er = reader.TagInventory_Raw(this.ants, 1, readtime, tagcnt);
            if (er != Reader.READER_ERR.MT_OK_ERR) {
                mErr = er;
                return null;
            }
            for (int i2 = 0; i2 < tagcnt[0]; i2++) {
                com.uhf.api.cls.Reader reader3 = reader;
                reader3.getClass();
                Reader.TAGINFO tfs = reader3.new TAGINFO();
                if (reader.GetNextTag(tfs) != Reader.READER_ERR.MT_OK_ERR) {
                    break;
                }
                list.add(tfs);
            }
            return list;
        }
        if (i != 2 && i == 3) {
            List<ReadTag> list2 = rrTagList;
            synchronized (list2) {
                list2.clear();
            }
            int scanRfidResult = RrReader.scanRfid(1, bank, startaddr, bytecnt / 2, Tools.Bytes2HexString(accesspwd, accesspwd.length), readtime);
            if (scanRfidResult == 0) {
                return formatRrTagList();
            }
            logPrint("Rr inventory tag & other by timer error: " + scanRfidResult);
        }
        return null;
    }

    public boolean setEMBEDEDATA(int bank, int startaddr, int bytecnt, byte[] accesspwd) {
        if (type != 1) {
            return false;
        }
        this.isEmb = true;
        this.Emboption = 128;
        this.Emboption = 128 << 8;
        com.uhf.api.cls.Reader reader2 = reader;
        reader2.getClass();
        Reader.EmbededData_ST edst = reader2.new EmbededData_ST();
        edst.bank = bank;
        edst.startaddr = startaddr;
        edst.bytecnt = bytecnt;
        edst.accesspwd = accesspwd;
        Reader.READER_ERR er = reader.ParamSet(Reader.Mtr_Param.MTR_PARAM_TAG_EMBEDEDDATA, edst);
        if (er != Reader.READER_ERR.MT_OK_ERR) {
            return false;
        }
        return true;
    }

    public boolean cancelEMBEDEDATA() {
        if (type != 1) {
            return false;
        }
        Reader.READER_ERR er = reader.ParamSet(Reader.Mtr_Param.MTR_PARAM_TAG_EMBEDEDDATA, null);
        if (er != Reader.READER_ERR.MT_OK_ERR) {
            return false;
        }
        this.isEmb = false;
        return true;
    }

    public Reader.READER_ERR getTagData(int mbank, int startaddr, int len, byte[] rdata, byte[] password, short timeout) {
        int i = type;
        if (i == 0) {
            MsgBaseInventoryEpc msg = new MsgBaseInventoryEpc();
            msg.setAntennaEnable(1L);
            msg.setInventoryMode(1);
            if (mbank == 0) {
                msg.setReadReserved(new ParamEpcReadReserved(startaddr, len));
            } else if (mbank == 1) {
                msg.setReadEpc(new ParamEpcReadEpc(startaddr, len));
            } else if (mbank == 2) {
                msg.setReadTid(new ParamEpcReadTid(1, len));
            } else if (mbank == 3) {
                msg.setReadUserdata(new ParamEpcReadUserdata(startaddr, len));
            }
            msg.setHexPassword(HexUtils.bytes2HexString(password));
            if (this.fastId.getFastId() != 0) {
                msg.setParamFastId(this.fastId);
            }
            client.sendSynMsg(msg);
            logPrint("MsgBaseInventoryEpc", msg.getRtMsg());
            if (msg.getRtCode() == 0) {
                try {
                    Thread.sleep(timeout);
                    MsgBaseStop stop = new MsgBaseStop();
                    client.sendSynMsg(stop);
                    logPrint("tagEpcOtherInventory", ((int) stop.getRtCode()) + "");
                    List<Reader.TAGINFO> taginfos = formatData(mbank);
                    if (taginfos.size() > 0) {
                        try {
                            System.arraycopy(taginfos.get(0).EmbededData, 0, rdata, 0, taginfos.get(0).EmbededData.length);
                            return Reader.READER_ERR.MT_OK_ERR;
                        } catch (InterruptedException e) {
                            e = e;
                            e.printStackTrace();
                            return Reader.READER_ERR.MT_CMD_FAILED_ERR;
                        }
                    }
                } catch (InterruptedException e2) {
                    e = e2;
                }
            }
            return Reader.READER_ERR.MT_CMD_FAILED_ERR;
        }
        if (i == 1) {
            Reader.READER_ERR er = reader.ParamSet(Reader.Mtr_Param.MTR_PARAM_TAG_FILTER, null);
            if (er == Reader.READER_ERR.MT_OK_ERR) {
                int trycount = 3;
                do {
                    er = reader.GetTagData(1, (char) mbank, startaddr, len, rdata, password, timeout);
                    trycount--;
                    if (trycount < 1) {
                        break;
                    }
                } while (er != Reader.READER_ERR.MT_OK_ERR);
                if (er != Reader.READER_ERR.MT_OK_ERR) {
                    logPrint("getTagData, GetTagData result: " + er.toString());
                }
            } else {
                logPrint("getTagData, ParamSet MTR_PARAM_TAG_FILTER result: " + er.toString());
            }
            return er;
        }
        if (i == 2) {
            String Status = driver.Read_Data_Tag(Tools.Bytes2HexString(password, password.length), 0, 0, 0, "", mbank, startaddr, len);
            if (Status != null) {
                Tools.HexString2Bytes(Status);
                logPrint("zeng-", "status:" + Status);
                return Reader.READER_ERR.MT_OK_ERR;
            }
            return Reader.READER_ERR.MT_CMD_FAILED_ERR;
        }
        if (i == 3) {
            int readDataG2Result = RrReader.readG2Data(mbank, startaddr, len, password, timeout, new byte[0], 1, 0, true, rdata);
            if (readDataG2Result == 0) {
                return Reader.READER_ERR.MT_OK_ERR;
            }
            logPrint("Rr get Tag data g2 error: " + readDataG2Result);
        }
        return Reader.READER_ERR.MT_CMD_FAILED_ERR;
    }

    public byte[] getTagDataByFilter(int mbank, int startaddr, int len, byte[] password, short timeout, byte[] fdata, int fbank, int fstartaddr, boolean matching) {
        List<Reader.TAGINFO> taginfos;
        int i = type;
        if (i == 0) {
            MsgBaseInventoryEpc msg = new MsgBaseInventoryEpc();
            msg.setAntennaEnable(1L);
            msg.setInventoryMode(1);
            if (mbank == 0) {
                msg.setReadReserved(new ParamEpcReadReserved(startaddr, len));
            } else if (mbank == 1) {
                msg.setReadEpc(new ParamEpcReadEpc(startaddr, len));
            } else if (mbank == 2) {
                msg.setReadTid(new ParamEpcReadTid(1, len));
            } else if (mbank == 3) {
                msg.setReadUserdata(new ParamEpcReadUserdata(startaddr, len));
            }
            msg.setHexPassword(HexUtils.bytes2HexString(password));
            if (matching) {
                ParamEpcFilter filter = new ParamEpcFilter();
                filter.setArea(fbank);
                filter.setBitStart(fstartaddr * 16);
                filter.setbData(fdata);
                filter.setBitLength(fdata.length * 8);
                msg.setFilter(filter);
            }
            if (this.fastId.getFastId() != 0) {
                msg.setParamFastId(this.fastId);
            }
            client.sendSynMsg(msg);
            logPrint("MsgBaseInventoryEpc", msg.getRtMsg());
            if (msg.getRtCode() == 0) {
                try {
                    Thread.sleep(timeout);
                    MsgBaseStop stop = new MsgBaseStop();
                    client.sendSynMsg(stop);
                    logPrint("tagEpcOtherInventory", ((int) stop.getRtCode()) + "");
                    if (matching) {
                        taginfos = formatData(mbank);
                    } else {
                        taginfos = formatExcludeData(mbank, fdata);
                    }
                    if (taginfos.size() > 0) {
                        return taginfos.get(0).EmbededData;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        if (i == 1) {
            com.uhf.api.cls.Reader reader2 = reader;
            reader2.getClass();
            Reader.TagFilter_ST g2tf = reader2.new TagFilter_ST();
            g2tf.fdata = fdata;
            g2tf.flen = fdata.length * 8;
            if (matching) {
                g2tf.isInvert = 0;
            } else {
                g2tf.isInvert = 1;
            }
            g2tf.bank = fbank;
            g2tf.startaddr = fstartaddr * 16;
            byte[] rdata = new byte[len * 2];
            Reader.READER_ERR er = reader.ParamSet(Reader.Mtr_Param.MTR_PARAM_TAG_FILTER, g2tf);
            if (er == Reader.READER_ERR.MT_OK_ERR) {
                Reader.READER_ERR er2 = reader.GetTagData(1, (char) mbank, startaddr, len, rdata, password, timeout);
                if (er2 == Reader.READER_ERR.MT_OK_ERR) {
                    return rdata;
                }
                logPrint("getTagDataByFilter, GetTagData result: " + er2.toString());
                return null;
            }
            logPrint("getTagDataByFilter, ParamSet MTR_PARAM_TAG_FILTER result: " + er.toString());
            return null;
        }
        if (i != 2) {
            if (i == 3) {
                byte[] rdata2 = new byte[len * 2];
                int readDataG2Result = RrReader.readG2Data(mbank, startaddr, len, password, timeout, fdata, fbank, fstartaddr, matching, rdata2);
                if (readDataG2Result == 0) {
                    return rdata2;
                }
                logPrint("Rr get tag data g2 by filter error: " + readDataG2Result);
            }
            return null;
        }
        String Status = driver.Read_Data_Tag(Tools.Bytes2HexString(password, password.length), fbank, fstartaddr * 16, Tools.Bytes2HexString(fdata, fdata.length).length(), Tools.Bytes2HexString(fdata, fdata.length), mbank, startaddr, len);
        logPrint("zeng-", "fbnk:" + fbank);
        logPrint("zeng-", "fstartaddr:" + fstartaddr);
        logPrint("zeng-", "Tools.Bytes2HexString(fdata, fdata.length).length():" + Tools.Bytes2HexString(fdata, fdata.length).length());
        logPrint("zeng-", "Tools.Bytes2HexString(fdata, fdata.length):" + Tools.Bytes2HexString(fdata, fdata.length));
        logPrint("zeng-", "mbank:" + mbank);
        logPrint("zeng-", "len:" + len);
        logPrint("zeng-getTagDataByFilter", "status:" + Status);
        if (Status != null) {
            return Tools.HexString2Bytes(Status);
        }
        return null;
    }

    public Reader.READER_ERR writeTagData(char mbank, int startaddress, byte[] data, int datalen, byte[] accesspasswd, short timeout) {
        Reader.READER_ERR er;
        int i = type;
        if (i == 0) {
            MsgBaseWriteEpc msg = new MsgBaseWriteEpc();
            msg.setAntennaEnable(1L);
            msg.setArea(mbank);
            msg.setStart(startaddress);
            String s = HexUtils.bytes2HexString(data);
            int pcLen = PcUtils.getValueLen(datalen);
            msg.setHexWriteData(PcUtils.padRight(s, pcLen * 4, '0'));
            msg.setHexPassword(HexUtils.bytes2HexString(accesspasswd));
            client.sendSynMsg(msg);
            logPrint("MsgBaseWriteEpc", msg.getRtMsg());
            return msg.getRtCode() == 0 ? Reader.READER_ERR.MT_OK_ERR : Reader.READER_ERR.MT_CMD_FAILED_ERR;
        }
        if (i == 1) {
            Reader.READER_ERR er2 = reader.ParamSet(Reader.Mtr_Param.MTR_PARAM_TAG_FILTER, null);
            if (er2 == Reader.READER_ERR.MT_OK_ERR) {
                int trycount = 3;
                do {
                    er = reader.WriteTagData(1, mbank, startaddress, data, datalen, accesspasswd, timeout);
                    trycount--;
                    if (trycount < 1) {
                        break;
                    }
                } while (er != Reader.READER_ERR.MT_OK_ERR);
                if (er != Reader.READER_ERR.MT_OK_ERR) {
                    logPrint("writeTagData, WriteTagData result: " + er.toString());
                }
                return er;
            }
            logPrint("writeTagData, ParamSet MTR_PARAM_TAG_FILTER result: " + er2.toString());
            return er2;
        }
        if (i == 2) {
            int Status = driver.Write_Data_Tag(Tools.Bytes2HexString(accesspasswd, accesspasswd.length), 0, 0, 0, "", mbank, startaddress, Tools.Bytes2HexString(data, data.length).length() / 4, Tools.Bytes2HexString(data, data.length));
            return Status == 0 ? Reader.READER_ERR.MT_OK_ERR : Reader.READER_ERR.MT_CMD_FAILED_ERR;
        }
        if (i == 3) {
            int writeDataG2Result = RrReader.writeG2Data(mbank, startaddress, data, datalen, accesspasswd, timeout, new byte[0], 1, 0, true);
            if (writeDataG2Result == 0) {
                return Reader.READER_ERR.MT_OK_ERR;
            }
            logPrint("Write tag data g2 error: " + writeDataG2Result);
        }
        return Reader.READER_ERR.MT_CMD_FAILED_ERR;
    }

    public Reader.READER_ERR writeTagDataByFilter(char mbank, int startaddress, byte[] data, int datalen, byte[] accesspasswd, short timeout, byte[] fdata, int fbank, int fstartaddr, boolean matching) {
        int i = type;
        if (i == 0) {
            MsgBaseWriteEpc msg = new MsgBaseWriteEpc();
            msg.setAntennaEnable(1L);
            msg.setArea(mbank);
            msg.setStart(startaddress);
            String s = HexUtils.bytes2HexString(data);
            int pcLen = PcUtils.getValueLen(datalen);
            msg.setHexWriteData(PcUtils.padRight(s, pcLen * 4, '0'));
            msg.setHexWriteData(PcUtils.padRight(s, pcLen * 4, '0'));
            msg.setHexPassword(HexUtils.bytes2HexString(accesspasswd));
            if (matching) {
                ParamEpcFilter filter = new ParamEpcFilter();
                filter.setArea(fbank);
                filter.setBitStart(fstartaddr * 16);
                filter.setbData(fdata);
                filter.setBitLength(fdata.length * 8);
                msg.setFilter(filter);
            }
            client.sendSynMsg(msg);
            logPrint("MsgBaseWriteEpc", msg.getRtMsg());
            return msg.getRtCode() == 0 ? Reader.READER_ERR.MT_OK_ERR : Reader.READER_ERR.MT_CMD_FAILED_ERR;
        }
        if (i == 1) {
            com.uhf.api.cls.Reader reader2 = reader;
            reader2.getClass();
            Reader.TagFilter_ST g2tf = reader2.new TagFilter_ST();
            g2tf.fdata = fdata;
            g2tf.flen = fdata.length * 8;
            if (matching) {
                g2tf.isInvert = 0;
            } else {
                g2tf.isInvert = 1;
            }
            g2tf.bank = fbank;
            g2tf.startaddr = fstartaddr * 16;
            Reader.READER_ERR er = reader.ParamSet(Reader.Mtr_Param.MTR_PARAM_TAG_FILTER, g2tf);
            if (er == Reader.READER_ERR.MT_OK_ERR) {
                int trycount = 3;
                do {
                    er = reader.WriteTagData(1, mbank, startaddress, data, datalen, accesspasswd, timeout);
                    trycount--;
                    if (trycount < 1) {
                        break;
                    }
                } while (er != Reader.READER_ERR.MT_OK_ERR);
                if (er != Reader.READER_ERR.MT_OK_ERR) {
                    logPrint("writeTagDataByFilter, WriteTagData result: " + er.toString());
                }
            } else {
                logPrint("writeTagDataByFilter, ParamSet MTR_PARAM_TAG_FILTER result: " + er.toString());
            }
            return er;
        }
        if (i == 2) {
            int Status = driver.Write_Data_Tag(Tools.Bytes2HexString(accesspasswd, accesspasswd.length), fbank, fstartaddr * 16, Tools.Bytes2HexString(fdata, fdata.length).length() / 4, Tools.Bytes2HexString(fdata, fdata.length), mbank, startaddress, Tools.Bytes2HexString(data, data.length).length() / 4, Tools.Bytes2HexString(data, data.length));
            return Status == 0 ? Reader.READER_ERR.MT_OK_ERR : Reader.READER_ERR.MT_CMD_FAILED_ERR;
        }
        if (i == 3) {
            int writeG2DataByFilterResult = RrReader.writeG2Data(mbank, startaddress, data, datalen, accesspasswd, timeout, fdata, fbank, fstartaddr, matching);
            if (writeG2DataByFilterResult == 0) {
                return Reader.READER_ERR.MT_OK_ERR;
            }
            logPrint("Write tag data g2 by filter error: " + writeG2DataByFilterResult);
        }
        return Reader.READER_ERR.MT_CMD_FAILED_ERR;
    }

    public Reader.READER_ERR writeTagEPC(byte[] data, byte[] accesspwd, short timeout) {
        Reader.READER_ERR er;
        int i = type;
        if (i == 0) {
            MsgBaseWriteEpc msg = new MsgBaseWriteEpc();
            msg.setAntennaEnable(1L);
            msg.setArea(1);
            msg.setStart(1);
            String s = HexUtils.bytes2HexString(data);
            int pcLen = PcUtils.getValueLen(s);
            msg.setHexWriteData(PcUtils.getPc(pcLen) + PcUtils.padRight(s, pcLen * 4, '0'));
            msg.setHexPassword(HexUtils.bytes2HexString(accesspwd));
            client.sendSynMsg(msg);
            logPrint("MsgBaseWriteEpc", msg.getRtMsg());
            return msg.getRtCode() == 0 ? Reader.READER_ERR.MT_OK_ERR : Reader.READER_ERR.MT_CMD_FAILED_ERR;
        }
        if (i == 1) {
            reader.ParamSet(Reader.Mtr_Param.MTR_PARAM_TAG_FILTER, null);
            int trycount = 3;
            do {
                er = reader.WriteTagEpcEx(1, data, data.length, accesspwd, timeout);
                if (trycount < 1) {
                    break;
                }
                trycount--;
            } while (er != Reader.READER_ERR.MT_OK_ERR);
            if (er != Reader.READER_ERR.MT_OK_ERR) {
                logPrint("writeTagEPC, WriteTagEpcEx result: " + er.toString());
            }
            return er;
        }
        if (i == 2) {
            return Reader.READER_ERR.MT_CMD_FAILED_ERR;
        }
        if (i == 3) {
            int writeTagEpcResult = RrReader.writeTagEpc(data, accesspwd, timeout, new byte[0], 1, 0, true);
            if (writeTagEpcResult == 0) {
                return Reader.READER_ERR.MT_OK_ERR;
            }
            logPrint("Write tag EPC error: " + writeTagEpcResult);
        }
        return Reader.READER_ERR.MT_CMD_FAILED_ERR;
    }

    public Reader.READER_ERR writeTagEPCByFilter(byte[] data, byte[] accesspwd, short timeout, byte[] fdata, int fbank, int fstartaddr, boolean matching) {
        int i = type;
        if (i == 0) {
            MsgBaseWriteEpc msg = new MsgBaseWriteEpc();
            msg.setAntennaEnable(1L);
            msg.setArea(1);
            msg.setStart(1);
            String s = HexUtils.bytes2HexString(data);
            int pcLen = PcUtils.getValueLen(s);
            msg.setHexWriteData(PcUtils.getPc(pcLen) + PcUtils.padRight(s, pcLen * 4, '0'));
            msg.setHexPassword(HexUtils.bytes2HexString(accesspwd));
            if (matching) {
                ParamEpcFilter filter = new ParamEpcFilter();
                filter.setArea(fbank);
                filter.setBitStart(fstartaddr * 16);
                filter.setbData(fdata);
                filter.setBitLength(fdata.length * 8);
                msg.setFilter(filter);
            }
            client.sendSynMsg(msg);
            logPrint("MsgBaseWriteEpc", msg.getRtMsg());
            return msg.getRtCode() == 0 ? Reader.READER_ERR.MT_OK_ERR : Reader.READER_ERR.MT_CMD_FAILED_ERR;
        }
        if (i != 1) {
            if (i == 2) {
                return Reader.READER_ERR.MT_CMD_FAILED_ERR;
            }
            if (i == 3) {
                int writeTagEpcResult = RrReader.writeTagEpc(data, accesspwd, timeout, fdata, fbank, fstartaddr, matching);
                if (writeTagEpcResult == 0) {
                    return Reader.READER_ERR.MT_OK_ERR;
                }
                logPrint("Write tag EPC by filter error: " + writeTagEpcResult);
            }
            return Reader.READER_ERR.MT_CMD_FAILED_ERR;
        }
        com.uhf.api.cls.Reader reader2 = reader;
        reader2.getClass();
        Reader.TagFilter_ST g2tf = reader2.new TagFilter_ST();
        g2tf.fdata = fdata;
        g2tf.flen = fdata.length * 8;
        if (matching) {
            g2tf.isInvert = 0;
        } else {
            g2tf.isInvert = 1;
        }
        g2tf.bank = fbank;
        g2tf.startaddr = fstartaddr * 16;
        Reader.READER_ERR er = reader.ParamSet(Reader.Mtr_Param.MTR_PARAM_TAG_FILTER, g2tf);
        if (er == Reader.READER_ERR.MT_OK_ERR) {
            int trycount = 3;
            do {
                er = reader.WriteTagEpcEx(1, data, data.length, accesspwd, timeout);
                if (trycount < 1) {
                    break;
                }
                trycount--;
            } while (er != Reader.READER_ERR.MT_OK_ERR);
            if (er != Reader.READER_ERR.MT_OK_ERR) {
                logPrint("writeTagEPCByFilter, WriteTagEpcEx result: " + er.toString());
            }
        } else {
            logPrint("writeTagEPCByFilter, ParamSet MTR_PARAM_TAG_FILTER result: " + er.toString());
        }
        return er;
    }

    /* JADX WARN: Code restructure failed: missing block: B:27:0x0089, code lost:
    
        if (r6 != 3) goto L34;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public com.uhf.api.cls.Reader.READER_ERR lockTag(com.uhf.api.cls.Reader.Lock_Obj r11, com.uhf.api.cls.Reader.Lock_Type r12, byte[] r13, short r14) {
        /*
            Method dump skipped, instructions count: 329
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.handheld.uhfr.UHFRManager.lockTag(com.uhf.api.cls.Reader$Lock_Obj, com.uhf.api.cls.Reader$Lock_Type, byte[], short):com.uhf.api.cls.Reader$READER_ERR");
    }

    /* JADX WARN: Code restructure failed: missing block: B:29:0x00aa, code lost:
    
        if (r7 != 3) goto L36;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public com.uhf.api.cls.Reader.READER_ERR lockTagByFilter(com.uhf.api.cls.Reader.Lock_Obj r16, com.uhf.api.cls.Reader.Lock_Type r17, byte[] r18, short r19, byte[] r20, int r21, int r22, boolean r23) {
        /*
            Method dump skipped, instructions count: 442
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.handheld.uhfr.UHFRManager.lockTagByFilter(com.uhf.api.cls.Reader$Lock_Obj, com.uhf.api.cls.Reader$Lock_Type, byte[], short, byte[], int, int, boolean):com.uhf.api.cls.Reader$READER_ERR");
    }

    public Reader.READER_ERR killTag(byte[] killpasswd, short timeout) {
        int i = type;
        if (i == 0) {
            MsgBaseWriteEpc writePas = new MsgBaseWriteEpc();
            writePas.setAntennaEnable(1L);
            writePas.setArea(0);
            writePas.setStart(0);
            writePas.setHexWriteData(HexUtils.bytes2HexString(killpasswd));
            client.sendSynMsg(writePas);
            logPrint("MsgBaseWritePas", writePas.getRtMsg());
            if (writePas.getRtCode() == 0) {
                MsgBaseDestroyEpc msg = new MsgBaseDestroyEpc();
                msg.setAntennaEnable(1L);
                msg.setHexPassword(HexUtils.bytes2HexString(killpasswd));
                client.sendSynMsg(msg);
                logPrint("MsgBaseDestroyEpc", msg.getRtMsg());
                return msg.getRtCode() == 0 ? Reader.READER_ERR.MT_OK_ERR : Reader.READER_ERR.MT_CMD_FAILED_ERR;
            }
            return Reader.READER_ERR.MT_CMD_FAILED_ERR;
        }
        if (i == 1) {
            Reader.READER_ERR er = reader.ParamSet(Reader.Mtr_Param.MTR_PARAM_TAG_FILTER, null);
            if (er == Reader.READER_ERR.MT_OK_ERR) {
                er = reader.KillTag(1, killpasswd, timeout);
            }
            if (er != Reader.READER_ERR.MT_OK_ERR) {
                logPrint("killTag, ParamSet MTR_PARAM_TAG_FILTER result: " + er.toString());
            }
            return er;
        }
        if (i == 2) {
            int status = driver.Kill_Tag(Tools.Bytes2HexString(killpasswd, killpasswd.length), 0, 0, 0, "");
            return status == 0 ? Reader.READER_ERR.MT_OK_ERR : Reader.READER_ERR.MT_CMD_FAILED_ERR;
        }
        if (i == 3) {
            int killG2Result = RrReader.killTag(killpasswd, timeout, new byte[0], 1, 0, true);
            if (killG2Result == 0) {
                return Reader.READER_ERR.MT_OK_ERR;
            }
            logPrint("Rr kill g2 error: " + killG2Result);
        }
        return Reader.READER_ERR.MT_CMD_FAILED_ERR;
    }

    public Reader.READER_ERR killTagByFilter(byte[] killpasswd, short timeout, byte[] fdata, int fbank, int fstartaddr, boolean matching) {
        int i = type;
        if (i == 0) {
            MsgBaseWriteEpc writePas = new MsgBaseWriteEpc();
            writePas.setAntennaEnable(1L);
            writePas.setArea(0);
            writePas.setStart(0);
            writePas.setHexWriteData(HexUtils.bytes2HexString(killpasswd));
            if (matching) {
                ParamEpcFilter filter = new ParamEpcFilter();
                filter.setArea(fbank);
                filter.setBitStart(fstartaddr * 16);
                filter.setbData(fdata);
                filter.setBitLength(fdata.length * 8);
                writePas.setFilter(filter);
            }
            client.sendSynMsg(writePas);
            logPrint("MsgBaseWritePas", writePas.getRtMsg());
            if (writePas.getRtCode() == 0) {
                MsgBaseDestroyEpc msg = new MsgBaseDestroyEpc();
                msg.setAntennaEnable(1L);
                msg.setHexPassword(HexUtils.bytes2HexString(killpasswd));
                if (matching) {
                    ParamEpcFilter filter2 = new ParamEpcFilter();
                    filter2.setArea(fbank);
                    filter2.setBitStart(fstartaddr * 16);
                    filter2.setbData(fdata);
                    filter2.setBitLength(fdata.length * 8);
                    msg.setFilter(filter2);
                }
                client.sendSynMsg(msg);
                logPrint("MsgBaseDestroyEpc", msg.getRtMsg());
                return msg.getRtCode() == 0 ? Reader.READER_ERR.MT_OK_ERR : Reader.READER_ERR.MT_CMD_FAILED_ERR;
            }
            return Reader.READER_ERR.MT_CMD_FAILED_ERR;
        }
        if (i == 1) {
            com.uhf.api.cls.Reader reader2 = reader;
            reader2.getClass();
            Reader.TagFilter_ST g2tf = reader2.new TagFilter_ST();
            g2tf.fdata = fdata;
            g2tf.flen = fdata.length * 8;
            if (matching) {
                g2tf.isInvert = 0;
            } else {
                g2tf.isInvert = 1;
            }
            g2tf.bank = fbank;
            g2tf.startaddr = fstartaddr * 16;
            Reader.READER_ERR er = reader.ParamSet(Reader.Mtr_Param.MTR_PARAM_TAG_FILTER, g2tf);
            if (er == Reader.READER_ERR.MT_OK_ERR) {
                er = reader.KillTag(1, killpasswd, timeout);
            }
            if (er != Reader.READER_ERR.MT_OK_ERR) {
                logPrint("killTagByFilter, ParamSet MTR_PARAM_TAG_FILTER result: " + er.toString());
            }
            return er;
        }
        if (i == 2) {
            String sData = Tools.Bytes2HexString(fdata, fdata.length);
            int status = driver.Kill_Tag(Tools.Bytes2HexString(killpasswd, killpasswd.length), fbank, fstartaddr, sData.length() / 4, sData);
            return status == 0 ? Reader.READER_ERR.MT_OK_ERR : Reader.READER_ERR.MT_CMD_FAILED_ERR;
        }
        if (i == 3) {
            int killG2Result = RrReader.killTag(killpasswd, timeout, fdata, fbank, fstartaddr, matching);
            if (killG2Result == 0) {
                return Reader.READER_ERR.MT_OK_ERR;
            }
            logPrint("Rr kill g2 by filter error: " + killG2Result);
        }
        return Reader.READER_ERR.MT_CMD_FAILED_ERR;
    }

    public Reader.READER_ERR setRegion(Reader.Region_Conf region) {
        int value;
        int[] a = getPower();
        int value2 = type;
        if (value2 == 0) {
            logPrint("zeng-", region.value() + "");
            MsgBaseSetFreqRange msg = new MsgBaseSetFreqRange();
            int value3 = region.value();
            if (value3 == 1) {
                msg.setFreqRangeIndex(3);
            } else if (value3 == 6) {
                msg.setFreqRangeIndex(0);
            } else if (value3 == 8) {
                msg.setFreqRangeIndex(4);
            } else if (value3 == 255) {
                msg.setFreqRangeIndex(9);
            } else {
                msg.setFreqRangeIndex(99);
            }
            client.sendSynMsg(msg);
            logPrint("MsgBaseSetFreqRange", msg.getRtMsg());
            if (msg.getRtCode() == 0) {
                return setPower(a[0], a[1]);
            }
            return Reader.READER_ERR.MT_CMD_FAILED_ERR;
        }
        if (value2 == 1) {
            return reader.ParamSet(Reader.Mtr_Param.MTR_PARAM_FREQUENCY_REGION, region);
        }
        if (value2 == 2) {
            logPrint("zeng-", "value:" + region.value());
            int value4 = region.value();
            if (value4 == 1) {
                value = 8;
            } else if (value4 == 6) {
                value = 1;
            } else if (value4 == 8) {
                value = 4;
            } else if (value4 == 10) {
                value = 2;
            } else {
                value = 0;
            }
            if (value == 0) {
                return Reader.READER_ERR.MT_CMD_FAILED_ERR;
            }
            int status = driver.SetRegion(value);
            if (-1000 == status || -1020 == status || status == 0) {
                return Reader.READER_ERR.MT_CMD_FAILED_ERR;
            }
            return setPower(this.rPower, this.wPower);
        }
        if (value2 == 3) {
            int setRegionResult = RrReader.setRegion(region);
            if (setRegionResult == 0) {
                return Reader.READER_ERR.MT_OK_ERR;
            }
            logPrint("Rr set region error: " + setRegionResult);
        }
        return Reader.READER_ERR.MT_CMD_FAILED_ERR;
    }

    public Reader.Region_Conf getRegion() {
        int i = type;
        if (i == 0) {
            MsgBaseGetFreqRange msg = new MsgBaseGetFreqRange();
            client.sendSynMsg(msg);
            logPrint("MsgBaseGetFreqRange", msg.getRtMsg());
            if (msg.getRtCode() == 0) {
                int freqRangeIndex = msg.getFreqRangeIndex();
                if (freqRangeIndex == 0) {
                    return Reader.Region_Conf.valueOf(6);
                }
                if (freqRangeIndex == 9) {
                    return Reader.Region_Conf.valueOf(255);
                }
                if (freqRangeIndex == 3) {
                    return Reader.Region_Conf.valueOf(1);
                }
                if (freqRangeIndex == 4) {
                    return Reader.Region_Conf.valueOf(8);
                }
            }
            return null;
        }
        if (i == 1) {
            Reader.Region_Conf[] rcf2 = new Reader.Region_Conf[1];
            Reader.READER_ERR er = reader.ParamGet(Reader.Mtr_Param.MTR_PARAM_FREQUENCY_REGION, rcf2);
            if (er == Reader.READER_ERR.MT_OK_ERR) {
                return rcf2[0];
            }
            logPrint("getRegion, ParamGet MTR_PARAM_FREQUENCY_REGION result: " + er.toString());
            return null;
        }
        if (i == 2) {
            String sum = driver.getRegion();
            if (sum.equals("-1000") || sum.equals("-1020")) {
                return null;
            }
            String text1 = sum.substring(2, 4);
            int i2 = Integer.parseInt(text1, 16);
            if (i2 != 1) {
                if (i2 == 2) {
                    return Reader.Region_Conf.valueOf(10);
                }
                if (i2 == 4) {
                    return Reader.Region_Conf.valueOf(8);
                }
                if (i2 != 8) {
                    return null;
                }
                return Reader.Region_Conf.valueOf(1);
            }
            return Reader.Region_Conf.valueOf(6);
        }
        if (i == 3) {
            byte[] band = new byte[1];
            int result = RrReader.rrlib.GetReaderInformation(new byte[2], new byte[1], band, new byte[1], new byte[1]);
            if (result == 0) {
                return RrReader.RrRegion_Conf.convertToClRegion(band[0]);
            }
            logPrint("Rr get region error: " + result);
        }
        return null;
    }

    public int[] getFrequencyPoints() {
        int i = type;
        if (i == 0) {
            MsgBaseGetFrequency msg = new MsgBaseGetFrequency();
            client.sendSynMsg(msg);
            logPrint("MsgBaseGetFrequency", msg.getRtMsg());
            if (msg.getRtCode() != 0) {
                return null;
            }
            int[] temp = new int[msg.getListFreqCursor().size()];
            for (int i2 = 0; i2 < msg.getListFreqCursor().size(); i2++) {
                temp[i2] = msg.getListFreqCursor().get(i2).intValue();
            }
            return temp;
        }
        if (i == 1) {
            com.uhf.api.cls.Reader reader2 = reader;
            reader2.getClass();
            Reader.HoptableData_ST hdst2 = reader2.new HoptableData_ST();
            Reader.READER_ERR er = reader.ParamGet(Reader.Mtr_Param.MTR_PARAM_FREQUENCY_HOPTABLE, hdst2);
            if (er == Reader.READER_ERR.MT_OK_ERR) {
                int[] tablefre = sort(hdst2.htb, hdst2.lenhtb);
                return tablefre;
            }
            logPrint("getFrequencyPoints, ParamGet MTR_PARAM_FREQUENCY_HOPTABLE result: " + er.toString());
            return null;
        }
        String sum = driver.GetFreqTable();
        if (sum.equals("-1000") || sum.equals("-1020")) {
            return null;
        }
        int index = sum.indexOf("}");
        String tmp = sum.substring(index + 1);
        String[] tmps = tmp.split("\\,");
        int[] number = new int[tmps.length];
        for (int i3 = 0; i3 < tmps.length; i3++) {
            number[i3] = Integer.parseInt(tmps[i3]);
        }
        return number;
    }

    public Reader.READER_ERR setFrequencyPoints(int[] frequencyPoints) {
        int i = type;
        if (i == 0) {
            MsgBaseSetFrequency msg = new MsgBaseSetFrequency();
            msg.setAutomatically(false);
            List<Integer> temp = new ArrayList<>();
            for (int i2 : frequencyPoints) {
                temp.add(Integer.valueOf(i2));
            }
            msg.setListFreqCursor(temp);
            client.sendSynMsg(msg);
            logPrint("MsgBaseSetFrequency", msg.getRtMsg());
            return msg.getRtCode() == 0 ? Reader.READER_ERR.MT_OK_ERR : Reader.READER_ERR.MT_CMD_FAILED_ERR;
        }
        if (i == 1) {
            com.uhf.api.cls.Reader reader2 = reader;
            reader2.getClass();
            Reader.HoptableData_ST hdst = reader2.new HoptableData_ST();
            hdst.lenhtb = frequencyPoints.length;
            hdst.htb = frequencyPoints;
            return reader.ParamSet(Reader.Mtr_Param.MTR_PARAM_FREQUENCY_HOPTABLE, hdst);
        }
        if (i == 2) {
            int result = driver.SetFreqTable(1, frequencyPoints.length, frequencyPoints);
            if (result == -1000 || result == -1020) {
                return Reader.READER_ERR.MT_CMD_FAILED_ERR;
            }
            return Reader.READER_ERR.MT_OK_ERR;
        }
        return Reader.READER_ERR.MT_CMD_FAILED_ERR;
    }

    public Reader.READER_ERR setPower(int readPower, int writePower) {
        this.rPower = readPower;
        this.wPower = writePower;
        int i = type;
        if (i == 0) {
            MsgBaseGetPower getPower = new MsgBaseGetPower();
            client.sendSynMsg(getPower);
            if (getPower.getRtCode() == 0) {
                if (getPower.getDicPower().get(1).intValue() == readPower) {
                    return Reader.READER_ERR.MT_OK_ERR;
                }
                MsgBaseSetPower msg = new MsgBaseSetPower();
                Hashtable<Integer, Integer> hashtable = new Hashtable<>();
                hashtable.put(1, Integer.valueOf(readPower));
                msg.setDicPower(hashtable);
                client.sendSynMsg(msg);
                logPrint("MsgBaseSetPower", msg.getRtMsg());
                return msg.getRtCode() == 0 ? Reader.READER_ERR.MT_OK_ERR : Reader.READER_ERR.MT_CMD_FAILED_ERR;
            }
            return Reader.READER_ERR.MT_CMD_FAILED_ERR;
        }
        if (i == 1) {
            com.uhf.api.cls.Reader reader2 = reader;
            reader2.getClass();
            Reader.AntPowerConf antPowerConf = reader2.new AntPowerConf();
            antPowerConf.antcnt = 1;
            com.uhf.api.cls.Reader reader3 = reader;
            reader3.getClass();
            Reader.AntPower antPower = reader3.new AntPower();
            antPower.antid = 1;
            antPower.readPower = (short) (((short) readPower) * 100);
            antPower.writePower = (short) (((short) writePower) * 100);
            antPowerConf.Powers[0] = antPower;
            return reader.ParamSet(Reader.Mtr_Param.MTR_PARAM_RF_ANTPOWER, antPowerConf);
        }
        if (i == 2) {
            logPrint("zeng-", "r:" + readPower + ";w:" + writePower);
            int status = driver.SetTxPower(readPower, writePower, 0, 0);
            StringBuilder sb = new StringBuilder();
            sb.append("setpowe:");
            sb.append(status);
            logPrint("zeng-", sb.toString());
            if (-1000 == status || -1020 == status || status == 0) {
                return Reader.READER_ERR.MT_CMD_FAILED_ERR;
            }
            return Reader.READER_ERR.MT_OK_ERR;
        }
        if (i == 3) {
            int setReadWritePowerResult = RrReader.setReadWritePower(readPower, writePower);
            if (setReadWritePowerResult == 0) {
                return Reader.READER_ERR.MT_OK_ERR;
            }
            logPrint("Rr set power error: " + setReadWritePowerResult);
        }
        return Reader.READER_ERR.MT_CMD_FAILED_ERR;
    }

    public int[] getPower() {
        int i = type;
        if (i == 0) {
            MsgBaseGetPower msg = new MsgBaseGetPower();
            client.sendSynMsg(msg);
            logPrint("MsgBaseGetPower", msg.getRtMsg());
            if (msg.getRtCode() != 0) {
                return null;
            }
            Integer power = msg.getDicPower().get(1);
            return new int[]{power.intValue(), power.intValue()};
        }
        if (i == 1) {
            int[] powers = new int[2];
            com.uhf.api.cls.Reader reader2 = reader;
            reader2.getClass();
            Reader.AntPowerConf apcf2 = reader2.new AntPowerConf();
            Reader.READER_ERR er = reader.ParamGet(Reader.Mtr_Param.MTR_PARAM_RF_ANTPOWER, apcf2);
            if (er == Reader.READER_ERR.MT_OK_ERR) {
                powers[0] = apcf2.Powers[0].readPower / 100;
                powers[1] = apcf2.Powers[0].writePower / 100;
                return powers;
            }
            logPrint("getPower, ParamGet MTR_PARAM_RF_ANTPOWER result: " + er.toString());
            return null;
        }
        if (i == 2) {
            Driver driver2 = driver;
            if (driver2 == null) {
                return null;
            }
            String text = driver2.GetTxPower();
            logPrint("zeng-", "text:" + text);
            if (text.equals("-1020") || text.equals("-1000")) {
                return null;
            }
            String[] PowArrary = text.split(",");
            String text1 = PowArrary[0].substring(6);
            String text2 = PowArrary[1].substring(0, PowArrary.length);
            int ri = Integer.parseInt(text1, 10);
            int wi = Integer.parseInt(text2, 10);
            return new int[]{ri, wi};
        }
        if (i == 3) {
            return RrReader.getReadWritePower();
        }
        return null;
    }

    public List<Reader.TEMPTAGINFO> getYueheTagTemperature(byte[] accesspassword) {
        int i = type;
        if (i == 0) {
            NumberFormat.getNumberInstance();
            MsgBaseInventoryEpc msg = new MsgBaseInventoryEpc();
            msg.setAntennaEnable(1L);
            msg.setInventoryMode(1);
            msg.setCtesius(2);
            client.sendSynMsg(msg);
            logPrint("MsgBaseInventoryEpc", msg.getRtMsg());
            if (msg.getRtCode() != 0) {
                return null;
            }
            try {
                Thread.sleep(50L);
                MsgBaseStop stop = new MsgBaseStop();
                client.sendSynMsg(stop);
                List<Reader.TEMPTAGINFO> taginfos = formatYueheData();
                return taginfos;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }
        if (i == 1 || i == 2 || i != 3) {
            return null;
        }
        List<Reader.TEMPTAGINFO> taginfos2 = RrReader.measureYueHeTemp();
        return taginfos2;
    }

    public List<Reader.TEMPTAGINFO> getYilianTagTemperature() {
        List<Reader.TAGINFO> list;
        int i = type;
        if (i == 0) {
            MsgBaseInventoryEpc msg = new MsgBaseInventoryEpc();
            msg.setAntennaEnable(1L);
            msg.setInventoryMode(1);
            ParamEpcReadUserdata userParam = new ParamEpcReadUserdata();
            userParam.setStart(WorkQueueKt.MASK);
            userParam.setLen(1);
            msg.setReadUserdata(userParam);
            client.sendSynMsg(msg);
            if (msg.getRtCode() != 0) {
                return null;
            }
            try {
                Thread.sleep(50L);
                MsgBaseStop stop = new MsgBaseStop();
                client.sendSynMsg(stop);
                List<Reader.TEMPTAGINFO> taginfos = formatData();
                return taginfos;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }
        if ((i != 1 && i != 3) || (list = tagEpcOtherInventoryByTimer((short) 50, 3, WorkQueueKt.MASK, 2, new byte[4])) == null || list.isEmpty()) {
            return null;
        }
        List<Reader.TEMPTAGINFO> taginfos2 = handleYilian(type, list);
        return taginfos2;
    }

    private int[] sort(int[] array, int len) {
        for (int xIndex = 0; xIndex < len; xIndex++) {
            for (int yIndex = 0; yIndex < len; yIndex++) {
                if (array[xIndex] < array[yIndex]) {
                    int tmpIntValue = array[xIndex];
                    array[xIndex] = array[yIndex];
                    array[yIndex] = tmpIntValue;
                }
            }
        }
        return array;
    }

    public boolean setGen2session(boolean isMulti) {
        int gen2session = type;
        if (gen2session == 0) {
            int gen2session2 = getGen2session();
            if (gen2session2 == -1) {
                return false;
            }
            if (isMulti) {
                if (gen2session2 != 2) {
                    MsgBaseSetBaseband msg = new MsgBaseSetBaseband();
                    msg.setSession(2);
                    msg.setqValue(4);
                    client.sendSynMsg(msg);
                    logPrint("setGen2session", msg.getRtMsg());
                }
                return true;
            }
            MsgBaseSetBaseband msg2 = new MsgBaseSetBaseband();
            msg2.setSession(0);
            msg2.setqValue(4);
            client.sendSynMsg(msg2);
            logPrint("setGen2session", msg2.getRtMsg());
            return msg2.getRtCode() == 0;
        }
        if (gen2session != 1) {
            if (gen2session == 2) {
                int[] gen2 = new int[10];
                String val = driver.GetGen2Para();
                if (val.equals("-1000") || val.equals("-1020")) {
                    return false;
                }
                for (int i = 0; i < 8; i++) {
                    gen2[i] = Integer.parseInt(val.substring(i * 2, (i + 1) * 2), 16);
                }
                if (isMulti) {
                    gen2[3] = gen2[3] & 207;
                    gen2[3] = gen2[3] + 16;
                } else {
                    gen2[3] = gen2[3] & 207;
                    gen2[3] = gen2[3] + 0;
                }
                int status = driver.SetGen2Para(0, gen2);
                return (-1000 == status || -1020 == status || status == 0) ? false : true;
            }
            if (gen2session != 3) {
                return false;
            }
            if (isMulti) {
                return true;
            }
            return setGen2session(0);
        }
        try {
            int[] val2 = {-1};
            if (isMulti) {
                val2[0] = 1;
                if (isE710) {
                    val2[0] = 2;
                    return true;
                }
            } else {
                val2[0] = 0;
            }
            Reader.READER_ERR er = reader.ParamSet(Reader.Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION, val2);
            com.uhf.api.cls.Reader reader2 = reader;
            reader2.getClass();
            Reader.CustomParam_ST cpst = reader2.new CustomParam_ST();
            cpst.ParamName = "Reader/Ex10fastmode";
            byte[] vals = new byte[22];
            vals[0] = 0;
            vals[1] = 20;
            for (int i2 = 0; i2 < 20; i2++) {
                vals[i2 + 2] = 0;
            }
            cpst.ParamVal = vals;
            reader.ParamSet(Reader.Mtr_Param.MTR_PARAM_CUSTOM, cpst);
            return er == Reader.READER_ERR.MT_OK_ERR;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean setGen2session(int session) {
        int gen2session = type;
        if (gen2session == 0) {
            int gen2session2 = getGen2session();
            if (gen2session2 == -1) {
                return false;
            }
            if (gen2session2 == session) {
                return true;
            }
            MsgBaseSetBaseband msg = new MsgBaseSetBaseband();
            msg.setSession(session);
            msg.setqValue(4);
            client.sendSynMsg(msg);
            logPrint("setGen2session", msg.getRtMsg());
            return msg.getRtCode() == 0;
        }
        if (gen2session == 1) {
            try {
                int[] val = {-1};
                val[0] = session;
                Reader.READER_ERR er = reader.ParamSet(Reader.Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION, val);
                return er == Reader.READER_ERR.MT_OK_ERR;
            } catch (Exception e) {
                return false;
            }
        }
        if (gen2session == 2) {
            int[] gen2 = new int[10];
            String val2 = driver.GetGen2Para();
            if (val2.equals("-1000") || val2.equals("-1020")) {
                return false;
            }
            for (int i = 0; i < 8; i++) {
                gen2[i] = Integer.parseInt(val2.substring(i * 2, (i + 1) * 2), 16);
            }
            gen2[3] = gen2[3] & 207;
            gen2[3] = gen2[3] + (session << 4);
            int status = driver.SetGen2Para(0, gen2);
            return (-1000 == status || -1020 == status || status == 0) ? false : true;
        }
        if (gen2session != 3) {
            return false;
        }
        RrReader.setSession(session);
        return true;
    }

    public int getGen2session() {
        int i = type;
        if (i == 0) {
            MsgBaseGetBaseband msg = new MsgBaseGetBaseband();
            client.sendSynMsg(msg);
            logPrint("getGen2session", msg.getRtMsg());
            if (msg.getRtCode() == 0) {
                return msg.getSession();
            }
        } else if (i == 1) {
            int[] val = {-1};
            Reader.READER_ERR er = reader.ParamGet(Reader.Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION, val);
            if (er == Reader.READER_ERR.MT_OK_ERR) {
                logPrint("pang", "getGen2session = " + val[0]);
                return val[0];
            }
        } else {
            if (i == 2) {
                String val2 = driver.GetGen2Para();
                if (val2.equals("-1000") || val2.equals("-1020")) {
                    return -1;
                }
                int tmp = Integer.parseInt(val2.substring(6, 7), 16);
                return tmp & 3;
            }
            if (i == 3) {
                return RrReader.getSession();
            }
        }
        return -1;
    }

    public boolean setQvaule(int qvaule) {
        int i = type;
        if (i == 0) {
            MsgBaseSetBaseband msg = new MsgBaseSetBaseband();
            msg.setqValue(qvaule);
            client.sendSynMsg(msg);
            if (msg.getRtCode() != 0) {
                return false;
            }
            logPrint("setQvaule", msg.getRtMsg());
            return true;
        }
        if (i == 1) {
            this.Q = qvaule;
            return true;
        }
        if (i == 2 || i != 3) {
            return false;
        }
        RrReader.setQ(qvaule);
        return true;
    }

    public int getQvalue() {
        int i = type;
        if (i == 0) {
            MsgBaseGetBaseband getBaseband = new MsgBaseGetBaseband();
            client.sendSynMsg(getBaseband);
            if (getBaseband.getRtCode() != 0) {
                return -1;
            }
            int value = getBaseband.getqValue();
            logPrint("getQvalue", getBaseband.getRtMsg());
            return value;
        }
        if (i == 1) {
            int value2 = this.Q;
            return value2;
        }
        if (i == 2 || i != 3) {
            return -1;
        }
        return RrReader.getQ();
    }

    public int getTarget() {
        int i = type;
        if (i == 0) {
            MsgBaseGetBaseband msg = new MsgBaseGetBaseband();
            client.sendSynMsg(msg);
            if (msg.getRtCode() != 0) {
                return -1;
            }
            int target = msg.getInventoryFlag();
            return target;
        }
        if (i == 1) {
            int[] val = {-1};
            Reader.READER_ERR er = reader.ParamGet(Reader.Mtr_Param.MTR_PARAM_POTL_GEN2_TARGET, val);
            if (er != Reader.READER_ERR.MT_OK_ERR) {
                return -1;
            }
            int target2 = val[0];
            return target2;
        }
        if (i == 2) {
            String val2 = driver.GetGen2Para();
            logPrint("zeng-", val2);
            if (val2.equals("-1000") || val2.equals("-1020")) {
                return -1;
            }
            int target3 = Integer.parseInt(val2.substring(7, 8), 16);
            return (target3 >> 3) & 1;
        }
        if (i != 3) {
            return -1;
        }
        return RrReader.rrlib.GetInventoryPatameter().Target;
    }

    public boolean setTarget(int target) {
        int i = type;
        if (i == 0) {
            MsgBaseSetBaseband msg = new MsgBaseSetBaseband();
            msg.setInventoryFlag(target);
            client.sendSynMsg(msg);
            if (msg.getRtCode() != 0) {
                return false;
            }
            return true;
        }
        if (i == 1) {
            int[] val = {-1};
            val[0] = target;
            Reader.READER_ERR er = reader.ParamSet(Reader.Mtr_Param.MTR_PARAM_POTL_GEN2_TARGET, val);
            if (er != Reader.READER_ERR.MT_OK_ERR) {
                return false;
            }
            return true;
        }
        if (i == 2) {
            int[] gen2 = new int[10];
            String val2 = driver.GetGen2Para();
            if (val2.equals("-1000") || val2.equals("-1020")) {
                return false;
            }
            for (int i2 = 0; i2 < 8; i2++) {
                gen2[i2] = Integer.parseInt(val2.substring(i2 * 2, (i2 + 1) * 2), 16);
            }
            gen2[3] = gen2[3] & 247;
            gen2[3] = gen2[3] + (target << 3);
            int status = driver.SetGen2Para(0, gen2);
            return (-1000 == status || -1020 == status || status == 0) ? false : true;
        }
        if (i != 3) {
            return false;
        }
        RrReader.setTarget(target);
        return true;
    }

    public String getInfo() {
        com.uhf.api.cls.Reader reader2 = reader;
        reader2.getClass();
        Reader.HardwareDetails val = reader2.new HardwareDetails();
        Reader.deviceVersion deviceversion = new Reader.deviceVersion();
        this.dv = deviceversion;
        com.uhf.api.cls.Reader.GetDeviceVersion("/dev/ttyMT1", deviceversion);
        if (reader.GetHardwareDetails(val) == Reader.READER_ERR.MT_OK_ERR) {
            return "module:" + val.module.toString() + "\r\nhard:" + this.dv.hardwareVer + "\r\nsoft:" + this.dv.softwareVer;
        }
        return "";
    }

    public Reader.READER_ERR ReadTagLED(int ant, short timeout, short metaflag, R2000_calibration.TagLED_DATA tagled) {
        if (type == 0) {
            return Reader.READER_ERR.MT_CMD_NO_TAG_ERR;
        }
        return reader.ReadTagLED(ant, timeout, metaflag, tagled);
    }

    public boolean setFastID(boolean z) {
        int SetFastIDStatus;
        int i = type;
        if (i == 0) {
            this.fastId.setFastId(z ? 1 : 0);
            return true;
        }
        if (i != 1) {
            if (i == 2) {
                if (!z) {
                    SetFastIDStatus = driver.SetFastIDStatus(0);
                } else {
                    SetFastIDStatus = driver.SetFastIDStatus(1);
                }
                return (-1000 == SetFastIDStatus || SetFastIDStatus == 0 || -1020 == SetFastIDStatus) ? false : true;
            }
            if (i != 3) {
                return false;
            }
            RrReader.setFastId(z);
            return true;
        }
        if (z) {
            com.uhf.api.cls.Reader reader2 = reader;
            reader2.getClass();
            Reader.CustomParam_ST customParam_ST = reader2.new CustomParam_ST();
            customParam_ST.ParamName = "tagcustomcmd/fastid";
            customParam_ST.ParamVal = new byte[1];
            customParam_ST.ParamVal[0] = 1;
            return reader.ParamSet(Reader.Mtr_Param.MTR_PARAM_CUSTOM, customParam_ST) == Reader.READER_ERR.MT_OK_ERR;
        }
        com.uhf.api.cls.Reader reader3 = reader;
        reader3.getClass();
        Reader.CustomParam_ST customParam_ST2 = reader3.new CustomParam_ST();
        customParam_ST2.ParamName = "tagcustomcmd/fastid";
        customParam_ST2.ParamVal = new byte[1];
        return reader.ParamSet(Reader.Mtr_Param.MTR_PARAM_CUSTOM, customParam_ST2) == Reader.READER_ERR.MT_OK_ERR;
    }

    public int setRrJgDwell(int jgTiem, int dwell) {
        if (type == 3) {
            return RrReader.setJgDwell(jgTiem, dwell);
        }
        return -1;
    }

    public int[] getRrJgDwell() {
        if (type == 3) {
            return RrReader.getJgDwell();
        }
        return new int[]{-1, -1};
    }

    private static void onTagHandler() {
        client.onTagEpcLog = new HandlerTagEpcLog() { // from class: com.handheld.uhfr.UHFRManager.2
            @Override // com.gg.reader.api.dal.HandlerTagEpcLog
            public void log(String readerName, LogBaseEpcInfo info) {
                boolean unused = UHFRManager.DEBUG;
                if (info.getResult() == 0 || info.getResult() == 4) {
                    synchronized (UHFRManager.epcList) {
                        info.setReplySerialNumber(Long.valueOf(System.currentTimeMillis()));
                        UHFRManager.epcList.add(info);
                    }
                }
            }
        };
        client.onTagEpcOver = new HandlerTagEpcOver() { // from class: com.handheld.uhfr.UHFRManager.3
            @Override // com.gg.reader.api.dal.HandlerTagEpcOver
            public void log(String readerName, LogBaseEpcOver info) {
                if (UHFRManager.DEBUG) {
                    UHFRManager.logPrint("onTagEpcOver", "HandlerTagEpcOver");
                }
                synchronized (UHFRManager.epcList) {
                    UHFRManager.epcList.notify();
                }
            }
        };
        client.onTagGbLog = new HandlerTagGbLog() { // from class: com.handheld.uhfr.UHFRManager.4
            @Override // com.gg.reader.api.dal.HandlerTagGbLog
            public void log(String readerName, LogBaseGbInfo info) {
                if (info.getResult() == 0) {
                    UHFRManager.logPrint("pang", "gbepc = " + info.getEpc());
                    synchronized (UHFRManager.gbepcList) {
                        UHFRManager.gbepcList.add(info);
                    }
                }
            }
        };
        client.onTagGbOver = new HandlerTagGbOver() { // from class: com.handheld.uhfr.UHFRManager.5
            @Override // com.gg.reader.api.dal.HandlerTagGbOver
            public void log(String readerName, LogBaseGbOver info) {
                synchronized (UHFRManager.gbepcList) {
                    UHFRManager.gbepcList.notify();
                }
            }
        };
        client.onTagGJbLog = new HandlerTagGJbLog() { // from class: com.handheld.uhfr.UHFRManager.6
            @Override // com.gg.reader.api.dal.HandlerTagGJbLog
            public void log(String readerName, LogBaseGJbInfo info) {
                if (info.getResult() == 0) {
                    UHFRManager.logPrint("pang", "gbepc = " + info.getEpc());
                    synchronized (UHFRManager.gjbepcList) {
                        UHFRManager.gjbepcList.add(info);
                    }
                }
            }
        };
        client.onTagGJbOver = new HandlerTagGJbOver() { // from class: com.handheld.uhfr.UHFRManager.7
            @Override // com.gg.reader.api.dal.HandlerTagGJbOver
            public void log(String s, LogBaseGJbOver logBaseGJbOver) {
                synchronized (UHFRManager.gjbepcList) {
                    UHFRManager.gjbepcList.notify();
                }
            }
        };
        client.onTag6bLog = new HandlerTag6bLog() { // from class: com.handheld.uhfr.UHFRManager.8
            @Override // com.gg.reader.api.dal.HandlerTag6bLog
            public void log(String s, LogBase6bInfo logBase6bInfo) {
                if (logBase6bInfo.getResult() == 0) {
                    synchronized (UHFRManager.tag6bList) {
                        UHFRManager.tag6bList.add(logBase6bInfo);
                    }
                }
            }
        };
        client.onTag6bOver = new HandlerTag6bOver() { // from class: com.handheld.uhfr.UHFRManager.9
            @Override // com.gg.reader.api.dal.HandlerTag6bOver
            public void log(String s, LogBase6bOver logBase6bOver) {
                UHFRManager.tag6bList.notify();
            }
        };
    }

    public boolean setGen2(String val) {
        int[] gen2 = new int[10];
        for (int i = 0; i < 8; i++) {
            gen2[i] = Integer.parseInt(val.substring(i * 2, (i + 1) * 2), 16);
        }
        gen2[3] = gen2[3] & 207;
        gen2[3] = gen2[3] + (2 << 4);
        gen2[3] = gen2[3] & 247;
        gen2[3] = gen2[3] + (0 << 3);
        int status = driver.SetGen2Para(0, gen2);
        return !(status == 0 && status == -100 && status == -1020) && driver.Inventory_Model_Set(1, false);
    }

    public static class MsgCallback implements TagCallback {
        @Override // com.rfid.trans.TagCallback
        public void tagCallback(ReadTag arg0) {
            synchronized (UHFRManager.rrTagList) {
                UHFRManager.rrTagList.add(arg0);
            }
        }

        @Override // com.rfid.trans.TagCallback
        public void StopReadCallBack() {
            UHFRManager.logPrint("Rr stop read callback");
        }
    }

    private List<Reader.TAGINFO> formatRrTagList() {
        ArrayList arrayList;
        List<ReadTag> list = rrTagList;
        synchronized (list) {
            HashMap<String, Reader.TAGINFO> tagMap = new HashMap<>();
            for (ReadTag info : list) {
                com.uhf.api.cls.Reader reader2 = new com.uhf.api.cls.Reader();
                reader2.getClass();
                Reader.TAGINFO taginfo = reader2.new TAGINFO();
                taginfo.AntennaID = (byte) info.antId;
                byte[] epcIdBytes = Tools.HexString2Bytes(info.epcId);
                taginfo.EpcId = epcIdBytes;
                taginfo.Epclen = (short) epcIdBytes.length;
                if (info.memId != null && info.memId.length() > 0) {
                    int ivtType = RrReader.rrlib.GetInventoryPatameter().IvtType;
                    if (ivtType == 2) {
                        byte[] fastIdBytes = Tools.HexString2Bytes(info.epcId + info.memId);
                        taginfo.EpcId = fastIdBytes;
                        taginfo.Epclen = (short) fastIdBytes.length;
                    } else {
                        byte[] embededDataBytes = Tools.HexString2Bytes(info.memId);
                        taginfo.EmbededData = embededDataBytes;
                        taginfo.EmbededDatalen = (short) embededDataBytes.length;
                    }
                }
                taginfo.protocol = Reader.SL_TagProtocol.SL_TAG_PROTOCOL_GEN2;
                taginfo.Phase = info.phase;
                double v = info.rssi - 130;
                taginfo.RSSI = (int) Math.round(v);
                if (!tagMap.containsKey(info.epcId)) {
                    taginfo.ReadCnt = 1;
                    tagMap.put(info.epcId, taginfo);
                } else {
                    Reader.TAGINFO temp = tagMap.get(info.epcId);
                    if (temp != null) {
                        temp.ReadCnt++;
                        tagMap.put(info.epcId, temp);
                    }
                }
            }
            rrTagList.clear();
            arrayList = new ArrayList(tagMap.values());
        }
        return arrayList;
    }

    public Reader.READER_ERR getTemperature(byte[] rdata) {
        if (type == 0) {
            MsgBaseInventoryEpc msg = new MsgBaseInventoryEpc();
            msg.setAntennaEnable(1L);
            msg.setInventoryMode(1);
            msg.setQuanray(1);
            client.sendSynMsg(msg);
            logPrint("MsgBaseInventoryEpc", msg.getRtMsg());
            if (msg.getRtCode() == 0) {
                try {
                    Thread.sleep(1000L);
                    MsgBaseStop stop = new MsgBaseStop();
                    client.sendSynMsg(stop);
                    logPrint("tagEpcOtherInventory", ((int) stop.getRtCode()) + "");
                    List<Reader.TAGINFO> taginfos = formatKRData();
                    if (taginfos.size() > 0) {
                        System.arraycopy(taginfos.get(0).EmbededData, 0, rdata, 0, taginfos.get(0).EmbededData.length);
                        return Reader.READER_ERR.MT_OK_ERR;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return Reader.READER_ERR.MT_CMD_FAILED_ERR;
            }
            return Reader.READER_ERR.MT_CMD_FAILED_ERR;
        }
        return Reader.READER_ERR.MT_CMD_FAILED_ERR;
    }

    public Reader.READER_ERR getOpen(byte[] rdata) {
        if (type == 0) {
            MsgBaseInventoryEpc msg = new MsgBaseInventoryEpc();
            msg.setAntennaEnable(1L);
            msg.setInventoryMode(1);
            msg.setQuanray(2);
            client.sendSynMsg(msg);
            logPrint("MsgBaseInventoryEpc", msg.getRtMsg());
            if (msg.getRtCode() == 0) {
                try {
                    Thread.sleep(100L);
                    MsgBaseStop stop = new MsgBaseStop();
                    client.sendSynMsg(stop);
                    List<Reader.TAGINFO> taginfos = formatKRData();
                    if (taginfos.size() > 0) {
                        System.arraycopy(taginfos.get(0).EmbededData, 0, rdata, 0, taginfos.get(0).EmbededData.length);
                        return Reader.READER_ERR.MT_OK_ERR;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return Reader.READER_ERR.MT_CMD_FAILED_ERR;
        }
        return Reader.READER_ERR.MT_CMD_FAILED_ERR;
    }

    public List<Reader.TAGINFO> formatKRData() {
        ArrayList arrayList;
        HashMap<String, Reader.TAGINFO> tagMap = new HashMap<>();
        synchronized (epcList) {
            for (LogBaseEpcInfo info : epcList) {
                com.uhf.api.cls.Reader reader2 = new com.uhf.api.cls.Reader();
                reader2.getClass();
                Reader.TAGINFO taginfo = reader2.new TAGINFO();
                taginfo.AntennaID = (byte) info.getAntId();
                if (info.getFrequencyPoint() != null) {
                    taginfo.Frequency = info.getFrequencyPoint().intValue();
                }
                if (info.getReplySerialNumber() != null) {
                    taginfo.TimeStamp = info.getReplySerialNumber().intValue();
                }
                if (info.getUserdata() != null) {
                    taginfo.EmbededData = info.getbUser();
                    taginfo.EmbededDatalen = (short) info.getbUser().length;
                }
                taginfo.EpcId = info.getbEpc();
                taginfo.Epclen = (short) info.getbEpc().length;
                taginfo.PC = HexUtils.int2Bytes(info.getPc());
                double v = info.getRssi() - 130;
                taginfo.RSSI = (int) Math.round(v);
                if (info.getCrc() != 0) {
                    taginfo.CRC = HexUtils.int2Bytes(info.getCrc());
                }
            }
            epcList.clear();
            arrayList = new ArrayList(tagMap.values());
        }
        return arrayList;
    }

    public Reader.READER_ERR LEDKR(byte[] fdata, int fbank, int fstartaddr) {
        if (type == 0) {
            MsgBaseWriteEpc msg = new MsgBaseWriteEpc();
            msg.setAntennaEnable(1L);
            msg.setArea(3);
            msg.setStart(128);
            msg.setHexWriteData(EnumG.MSG_TYPE_BIT_APP);
            ParamEpcFilter filter = new ParamEpcFilter();
            filter.setArea(fbank);
            filter.setBitStart(fstartaddr * 16);
            filter.setbData(fdata);
            filter.setBitLength(fdata.length * 8);
            msg.setFilter(filter);
            msg.setStayCarrierWave(1);
            if (msg.getRtCode() == 0) {
                return Reader.READER_ERR.MT_OK_ERR;
            }
        }
        return Reader.READER_ERR.MT_CMD_FAILED_ERR;
    }

    public Reader.READER_ERR StopLEDKR() {
        if (type == 0) {
            MsgBaseStop stop = new MsgBaseStop();
            client.sendSynMsg(stop);
            return Reader.READER_ERR.MT_OK_ERR;
        }
        return Reader.READER_ERR.MT_CMD_FAILED_ERR;
    }
}
