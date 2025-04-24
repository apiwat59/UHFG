package com.gg.reader.api.dal;

import com.android.usbserial.client.AndroidUsbSerialClient;
import com.gg.reader.api.dal.communication.AndroidPdaSerialClient;
import com.gg.reader.api.dal.communication.AndroidSerialClient;
import com.gg.reader.api.dal.communication.AndroidSerialCusClient;
import com.gg.reader.api.dal.communication.AndroidUsbHidClient;
import com.gg.reader.api.dal.communication.BleBluetoothClient;
import com.gg.reader.api.dal.communication.BluetoothClient;
import com.gg.reader.api.dal.communication.CWSerialClient;
import com.gg.reader.api.dal.communication.CommunicationInterface;
import com.gg.reader.api.dal.communication.HandlerDisconnected;
import com.gg.reader.api.dal.communication.HandlerMessageReceived;
import com.gg.reader.api.dal.communication.TcpClient;
import com.gg.reader.api.protocol.gx.EnumG;
import com.gg.reader.api.protocol.gx.LogAppGpiOver;
import com.gg.reader.api.protocol.gx.LogAppGpiStart;
import com.gg.reader.api.protocol.gx.LogBase6bInfo;
import com.gg.reader.api.protocol.gx.LogBase6bOver;
import com.gg.reader.api.protocol.gx.LogBaseEpcInfo;
import com.gg.reader.api.protocol.gx.LogBaseEpcOver;
import com.gg.reader.api.protocol.gx.LogBaseGJbInfo;
import com.gg.reader.api.protocol.gx.LogBaseGJbOver;
import com.gg.reader.api.protocol.gx.LogBaseGbInfo;
import com.gg.reader.api.protocol.gx.LogBaseGbOver;
import com.gg.reader.api.protocol.gx.LogBaseGbSafeParam;
import com.gg.reader.api.protocol.gx.Message;
import com.gg.reader.api.protocol.gx.MsgAppGetCacheTagData;
import com.gg.reader.api.protocol.gx.MsgAppHeartbeat;
import com.gg.reader.api.utils.GLog;
import com.gg.reader.api.utils.HexUtils;
import java.util.HashMap;

/* loaded from: classes.dex */
public class GClient {
    private static final int MSG_TIME_OUT = 3000;
    public HandlerCacheDataOver cacheDataOver;
    private CommunicationInterface ci;
    public HandlerDebugLog debugLog;
    private HashMap<Integer, ClientManualResetEvent> dicMre = new HashMap<>();
    private boolean isPrint = true;
    private String name;
    public HandlerTcpDisconnected onDisconnected;
    public HandlerGpiOver onGpiOver;
    public HandlerGpiStart onGpiStart;
    public HandlerHeartbeatLog onHeartbeatLog;
    public HandlerTag6bLog onTag6bLog;
    public HandlerTag6bOver onTag6bOver;
    public HandlerTagEpcLog onTagEpcLog;
    public HandlerTagEpcOver onTagEpcOver;
    public HandlerTagGJbLog onTagGJbLog;
    public HandlerTagGJbOver onTagGJbOver;
    public HandlerTagGJbRn11Log onTagGJbRn11Log;
    public HandlerTagGbLog onTagGbLog;
    public HandlerTagGbOver onTagGbOver;
    public HandlerTagGbSafeParam onTagGbSafeParam;
    private String serialNumber;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerialNumber() {
        return this.serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public int getConnectType() {
        CommunicationInterface communicationInterface = this.ci;
        if (communicationInterface == null) {
            return 255;
        }
        return communicationInterface.getConnectType();
    }

    public void setSendHeartBeat(boolean _isSendHeartbeat) {
        CommunicationInterface communicationInterface = this.ci;
        if (communicationInterface != null) {
            communicationInterface._isSendHeartbeat = _isSendHeartbeat;
        }
    }

    public void setPrint(boolean print) {
        this.isPrint = print;
    }

    protected void triggerTagEpcLogEvent(LogBaseEpcInfo msg) {
        try {
            HandlerTagEpcLog handlerTagEpcLog = this.onTagEpcLog;
            if (handlerTagEpcLog != null) {
                synchronized (handlerTagEpcLog) {
                    String str = this.serialNumber;
                    if (str != null) {
                        msg.setReaderSerialNumber(str);
                    }
                    this.onTagEpcLog.log(this.name, msg);
                }
            }
        } catch (Exception e) {
        }
    }

    protected void triggerTagEpcOverEvent(LogBaseEpcOver msg) {
        try {
            HandlerTagEpcOver handlerTagEpcOver = this.onTagEpcOver;
            if (handlerTagEpcOver != null) {
                synchronized (handlerTagEpcOver) {
                    String str = this.serialNumber;
                    if (str != null) {
                        msg.setReaderSerialNumber(str);
                    }
                    this.onTagEpcOver.log(this.name, msg);
                }
            }
        } catch (Exception e) {
        }
    }

    protected void triggerTag6bLogEvent(LogBase6bInfo msg) {
        try {
            HandlerTag6bLog handlerTag6bLog = this.onTag6bLog;
            if (handlerTag6bLog != null) {
                synchronized (handlerTag6bLog) {
                    String str = this.serialNumber;
                    if (str != null) {
                        msg.setReaderSerialNumber(str);
                    }
                    this.onTag6bLog.log(this.name, msg);
                }
            }
        } catch (Exception e) {
        }
    }

    protected void triggerTag6bOverEvent(LogBase6bOver msg) {
        try {
            HandlerTag6bOver handlerTag6bOver = this.onTag6bOver;
            if (handlerTag6bOver != null) {
                synchronized (handlerTag6bOver) {
                    String str = this.serialNumber;
                    if (str != null) {
                        msg.setReaderSerialNumber(str);
                    }
                    this.onTag6bOver.log(this.name, msg);
                }
            }
        } catch (Exception e) {
        }
    }

    protected void triggerTagGbLogEvent(LogBaseGbInfo msg) {
        try {
            HandlerTagGbLog handlerTagGbLog = this.onTagGbLog;
            if (handlerTagGbLog != null) {
                synchronized (handlerTagGbLog) {
                    String str = this.serialNumber;
                    if (str != null) {
                        msg.setReaderSerialNumber(str);
                    }
                    this.onTagGbLog.log(this.name, msg);
                }
            }
        } catch (Exception e) {
        }
    }

    protected void triggerTagGbOverEvent(LogBaseGbOver msg) {
        try {
            HandlerTagGbOver handlerTagGbOver = this.onTagGbOver;
            if (handlerTagGbOver != null) {
                synchronized (handlerTagGbOver) {
                    String str = this.serialNumber;
                    if (str != null) {
                        msg.setReaderSerialNumber(str);
                    }
                    this.onTagGbOver.log(this.name, msg);
                }
            }
        } catch (Exception e) {
        }
    }

    protected void triggerTagGJbLogEvent(LogBaseGJbInfo msg) {
        try {
            HandlerTagGJbLog handlerTagGJbLog = this.onTagGJbLog;
            if (handlerTagGJbLog != null) {
                synchronized (handlerTagGJbLog) {
                    String str = this.serialNumber;
                    if (str != null) {
                        msg.setReaderSerialNumber(str);
                    }
                    this.onTagGJbLog.log(this.name, msg);
                }
            }
        } catch (Exception e) {
        }
    }

    protected void triggerTagGJbOverEvent(LogBaseGJbOver msg) {
        try {
            HandlerTagGJbOver handlerTagGJbOver = this.onTagGJbOver;
            if (handlerTagGJbOver != null) {
                synchronized (handlerTagGJbOver) {
                    String str = this.serialNumber;
                    if (str != null) {
                        msg.setReaderSerialNumber(str);
                    }
                    this.onTagGJbOver.log(this.name, msg);
                }
            }
        } catch (Exception e) {
        }
    }

    protected void triggerTagGJbRn11LogEvent(byte[] data) {
        try {
            HandlerTagGJbRn11Log handlerTagGJbRn11Log = this.onTagGJbRn11Log;
            if (handlerTagGJbRn11Log != null) {
                synchronized (handlerTagGJbRn11Log) {
                    this.onTagGJbRn11Log.log(this.name, data);
                }
            }
        } catch (Exception e) {
        }
    }

    protected void triggerTagGbSafeParamEvent(LogBaseGbSafeParam msg) {
        try {
            HandlerTagGbSafeParam handlerTagGbSafeParam = this.onTagGbSafeParam;
            if (handlerTagGbSafeParam != null) {
                synchronized (handlerTagGbSafeParam) {
                    this.onTagGbSafeParam.log(this.name, msg);
                }
            }
        } catch (Exception e) {
        }
    }

    protected void triggerGpiStart(LogAppGpiStart msg) {
        try {
            HandlerGpiStart handlerGpiStart = this.onGpiStart;
            if (handlerGpiStart != null) {
                synchronized (handlerGpiStart) {
                    String str = this.serialNumber;
                    if (str != null) {
                        msg.setReaderSerialNumber(str);
                    }
                    this.onGpiStart.log(this.name, msg);
                }
            }
        } catch (Exception e) {
        }
    }

    protected void triggerGpiOver(LogAppGpiOver msg) {
        try {
            HandlerGpiOver handlerGpiOver = this.onGpiOver;
            if (handlerGpiOver != null) {
                synchronized (handlerGpiOver) {
                    String str = this.serialNumber;
                    if (str != null) {
                        msg.setReaderSerialNumber(str);
                    }
                    this.onGpiOver.log(this.name, msg);
                }
            }
        } catch (Exception e) {
        }
    }

    protected void triggerCacheDataOver(MsgAppGetCacheTagData msg) {
        try {
            HandlerCacheDataOver handlerCacheDataOver = this.cacheDataOver;
            if (handlerCacheDataOver != null) {
                synchronized (handlerCacheDataOver) {
                    this.cacheDataOver.log(this.name, msg);
                }
            }
        } catch (Exception e) {
        }
    }

    protected void triggerDisconnected() {
        try {
            HandlerTcpDisconnected handlerTcpDisconnected = this.onDisconnected;
            if (handlerTcpDisconnected != null) {
                synchronized (handlerTcpDisconnected) {
                    this.onDisconnected.log(this.name);
                }
            }
        } catch (Exception e) {
        }
    }

    public boolean open(String readerName, CommunicationInterface client, int timeout) {
        if (client == null) {
            return false;
        }
        this.ci = client;
        client.onMessageReceived = new HandlerMessageReceived() { // from class: com.gg.reader.api.dal.GClient.1
            @Override // com.gg.reader.api.dal.communication.HandlerMessageReceived
            public void received(Message msg) {
                GClient.this.processMessage(msg);
            }
        };
        this.ci.onDisconnected = new HandlerDisconnected() { // from class: com.gg.reader.api.dal.GClient.2
            @Override // com.gg.reader.api.dal.communication.HandlerDisconnected
            public void log() {
                GClient.this.triggerDisconnected();
            }
        };
        if (!this.ci.isConnected()) {
            return false;
        }
        this.name = readerName;
        this.ci.setConnectType(3);
        return true;
    }

    public boolean openAndroidSerial(String readerName, int timeout) {
        try {
            AndroidSerialClient androidSerialClient = new AndroidSerialClient();
            this.ci = androidSerialClient;
            androidSerialClient.onMessageReceived = new HandlerMessageReceived() { // from class: com.gg.reader.api.dal.GClient.3
                @Override // com.gg.reader.api.dal.communication.HandlerMessageReceived
                public void received(Message msg) {
                    GClient.this.processMessage(msg);
                }
            };
            if (this.ci.open(readerName)) {
                this.name = readerName;
                this.ci.setConnectType(0);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public boolean openCusAndroidSerial(String readerName, int packageSize, int freeWait) {
        try {
            AndroidSerialCusClient androidSerialCusClient = new AndroidSerialCusClient(packageSize, freeWait);
            this.ci = androidSerialCusClient;
            androidSerialCusClient.onMessageReceived = new HandlerMessageReceived() { // from class: com.gg.reader.api.dal.GClient.4
                @Override // com.gg.reader.api.dal.communication.HandlerMessageReceived
                public void received(Message msg) {
                    GClient.this.processMessage(msg);
                }
            };
            if (this.ci.open(readerName)) {
                this.name = readerName;
                this.ci.setConnectType(0);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public boolean openCusAndroidRs485(String readerName, int packageSize, int freeWait) {
        String[] param;
        try {
            param = readerName.split(":");
        } catch (Exception e) {
        }
        if (param != null && param.length == 3) {
            AndroidSerialCusClient androidSerialCusClient = new AndroidSerialCusClient(packageSize, freeWait);
            this.ci = androidSerialCusClient;
            androidSerialCusClient.onMessageReceived = new HandlerMessageReceived() { // from class: com.gg.reader.api.dal.GClient.5
                @Override // com.gg.reader.api.dal.communication.HandlerMessageReceived
                public void received(Message msg) {
                    GClient.this.processMessage(msg);
                }
            };
            if (this.ci.open(param[0] + ":" + param[1])) {
                this.name = readerName;
                this.ci.setConnectType(1);
                this.ci.setRs485(true);
                this.ci.setRs485Address(Integer.parseInt(param[2]));
                return true;
            }
            return false;
        }
        return false;
    }

    public boolean openAndroidRs485(String readerName, int timeout) {
        String[] param;
        try {
            param = readerName.split(":");
        } catch (Exception e) {
        }
        if (param != null && param.length == 3) {
            AndroidSerialClient androidSerialClient = new AndroidSerialClient();
            this.ci = androidSerialClient;
            androidSerialClient.onMessageReceived = new HandlerMessageReceived() { // from class: com.gg.reader.api.dal.GClient.6
                @Override // com.gg.reader.api.dal.communication.HandlerMessageReceived
                public void received(Message msg) {
                    GClient.this.processMessage(msg);
                }
            };
            if (this.ci.open(param[0] + ":" + param[1])) {
                this.name = readerName;
                this.ci.setConnectType(1);
                this.ci.setRs485(true);
                this.ci.setRs485Address(Integer.parseInt(param[2]));
                return true;
            }
            return false;
        }
        return false;
    }

    public boolean openHdSerial(String readerName, int timeout) {
        try {
            AndroidPdaSerialClient androidPdaSerialClient = new AndroidPdaSerialClient();
            this.ci = androidPdaSerialClient;
            androidPdaSerialClient.onMessageReceived = new HandlerMessageReceived() { // from class: com.gg.reader.api.dal.GClient.7
                @Override // com.gg.reader.api.dal.communication.HandlerMessageReceived
                public void received(Message msg) {
                    GClient.this.processMessage(msg);
                }
            };
            if (this.ci.open(readerName)) {
                this.name = readerName;
                this.ci.setConnectType(0);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public void hdPowerOn() {
        CommunicationInterface communicationInterface = this.ci;
        if (communicationInterface != null && (communicationInterface instanceof AndroidPdaSerialClient)) {
            communicationInterface.hdPowerOn();
        }
    }

    public void hdPowerOff() {
        CommunicationInterface communicationInterface = this.ci;
        if (communicationInterface != null && (communicationInterface instanceof AndroidPdaSerialClient)) {
            communicationInterface.hdPowerOff();
        }
    }

    public boolean openTcp(String readerName, int timeout) {
        try {
            TcpClient tcpClient = new TcpClient();
            this.ci = tcpClient;
            tcpClient.onMessageReceived = new HandlerMessageReceived() { // from class: com.gg.reader.api.dal.GClient.8
                @Override // com.gg.reader.api.dal.communication.HandlerMessageReceived
                public void received(Message msg) {
                    GClient.this.processMessage(msg);
                }
            };
            this.ci.onDisconnected = new HandlerDisconnected() { // from class: com.gg.reader.api.dal.GClient.9
                @Override // com.gg.reader.api.dal.communication.HandlerDisconnected
                public void log() {
                    GClient.this.triggerDisconnected();
                }
            };
            if (this.ci.open(readerName)) {
                this.name = readerName;
                this.ci.setConnectType(2);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean openAndroidUsbSerial(AndroidUsbSerialClient usbClient) {
        try {
            this.ci = usbClient;
            usbClient.onMessageReceived = new HandlerMessageReceived() { // from class: com.gg.reader.api.dal.GClient.10
                @Override // com.gg.reader.api.dal.communication.HandlerMessageReceived
                public void received(Message msg) {
                    GClient.this.processMessage(msg);
                }
            };
            this.name = usbClient.getUsbName();
            this.ci.setConnectType(4);
            this.ci.open("");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean openAndroidUsbRs485(AndroidUsbSerialClient usbClient, int address) {
        try {
            this.ci = usbClient;
            usbClient.onMessageReceived = new HandlerMessageReceived() { // from class: com.gg.reader.api.dal.GClient.11
                @Override // com.gg.reader.api.dal.communication.HandlerMessageReceived
                public void received(Message msg) {
                    GClient.this.processMessage(msg);
                }
            };
            this.name = usbClient.getUsbName();
            this.ci.setRs485(true);
            this.ci.setRs485Address(address);
            this.ci.setConnectType(4);
            this.ci.open("");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean openBluetooth(String address, int timeout, int way, BluetoothClient bluetoothClient) {
        try {
            this.ci = bluetoothClient;
            bluetoothClient.onMessageReceived = new HandlerMessageReceived() { // from class: com.gg.reader.api.dal.GClient.12
                @Override // com.gg.reader.api.dal.communication.HandlerMessageReceived
                public void received(Message msg) {
                    GClient.this.processMessage(msg);
                }
            };
            this.ci.onDisconnected = new HandlerDisconnected() { // from class: com.gg.reader.api.dal.GClient.13
                @Override // com.gg.reader.api.dal.communication.HandlerDisconnected
                public void log() {
                    GClient.this.triggerDisconnected();
                }
            };
            if (this.ci.open(address, way)) {
                this.name = address;
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean openCwSerial(String readerName, int timeout, CWSerialClient cwSerialClient) {
        try {
            this.ci = cwSerialClient;
            cwSerialClient.onMessageReceived = new HandlerMessageReceived() { // from class: com.gg.reader.api.dal.GClient.14
                @Override // com.gg.reader.api.dal.communication.HandlerMessageReceived
                public void received(Message msg) {
                    GClient.this.processMessage(msg);
                }
            };
            if (this.ci.open(readerName)) {
                this.name = readerName;
                this.ci.setConnectType(0);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public boolean openBleBluetooth(String address, int auto, BleBluetoothClient bleClient) {
        try {
            this.ci = bleClient;
            bleClient.onMessageReceived = new HandlerMessageReceived() { // from class: com.gg.reader.api.dal.GClient.15
                @Override // com.gg.reader.api.dal.communication.HandlerMessageReceived
                public void received(Message msg) {
                    GClient.this.processMessage(msg);
                }
            };
            this.ci.onDisconnected = new HandlerDisconnected() { // from class: com.gg.reader.api.dal.GClient.16
                @Override // com.gg.reader.api.dal.communication.HandlerDisconnected
                public void log() {
                    GClient.this.triggerDisconnected();
                }
            };
            if (this.ci.open(address, auto)) {
                this.name = address;
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean openAndroidUsbHid(AndroidUsbHidClient hidClient) {
        try {
            this.ci = hidClient;
            hidClient.onMessageReceived = new HandlerMessageReceived() { // from class: com.gg.reader.api.dal.GClient.17
                @Override // com.gg.reader.api.dal.communication.HandlerMessageReceived
                public void received(Message msg) {
                    GClient.this.processMessage(msg);
                }
            };
            this.name = hidClient.getUsbName();
            this.ci.setConnectType(4);
            this.ci.open("");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean close() {
        try {
            CommunicationInterface communicationInterface = this.ci;
            if (communicationInterface != null) {
                communicationInterface.close();
                this.ci.onMessageReceived = null;
                this.ci.onDisconnected = null;
                this.ci = null;
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void sendSynMsg(Message msg, int timeout) {
        HandlerDebugLog handlerDebugLog;
        HandlerDebugLog handlerDebugLog2;
        if (msg == null) {
            return;
        }
        if (getConnectType() == 1) {
            msg.msgType.mt_13 = "1";
        }
        int mreKey = msg.msgType.toInt();
        if (this.dicMre == null) {
            this.dicMre = new HashMap<>();
        }
        if (!this.dicMre.containsKey(Integer.valueOf(mreKey))) {
            ClientManualResetEvent cmre = new ClientManualResetEvent(false);
            cmre.data = null;
            this.dicMre.put(Integer.valueOf(mreKey), cmre);
        } else {
            this.dicMre.get(Integer.valueOf(mreKey)).data = null;
            this.dicMre.get(Integer.valueOf(mreKey)).evt.reset();
        }
        try {
            this.ci.send(msg);
            if (this.isPrint && (handlerDebugLog2 = this.debugLog) != null) {
                handlerDebugLog2.sendDebugLog("send-[" + msg.getClass().getName() + "]-[" + HexUtils.bytes2HexString(msg.msgData) + "]");
            }
            this.dicMre.get(Integer.valueOf(mreKey)).evt.waitOne(timeout);
            if (this.dicMre.get(Integer.valueOf(mreKey)).data != null) {
                msg.msgData = this.dicMre.get(Integer.valueOf(mreKey)).data.msgData;
                msg.ackUnpack(this.dicMre.get(Integer.valueOf(mreKey)).data.cData);
                if (this.isPrint && (handlerDebugLog = this.debugLog) != null) {
                    handlerDebugLog.receiveDebugLog("receive-[" + msg.getClass().getName() + "]-[" + HexUtils.bytes2HexString(msg.msgData) + "]");
                }
            }
        } catch (Exception e) {
        }
    }

    public void sendSynMsg(Message msg) {
        sendSynMsg(msg, 3000);
    }

    public void sendSynMsgRetry(Message msg, int timeout, int retry) {
        for (int i = 0; i < retry; i++) {
            sendSynMsg(msg, timeout);
            if (msg.getRtCode() == 0) {
                return;
            }
        }
    }

    public void sendUnsynMsg(Message msg) {
        HandlerDebugLog handlerDebugLog;
        if (msg == null) {
            return;
        }
        this.ci.send(msg);
        if (this.isPrint && (handlerDebugLog = this.debugLog) != null) {
            handlerDebugLog.sendDebugLog("send-[" + msg.getClass().getName() + "]-[" + HexUtils.bytes2HexString(msg.msgData) + "]");
        }
    }

    public void sendUnsynMsg(byte[] msg) {
        HandlerDebugLog handlerDebugLog;
        if (msg == null) {
            return;
        }
        this.ci.send(msg);
        if (this.isPrint && (handlerDebugLog = this.debugLog) != null) {
            handlerDebugLog.sendDebugLog("send-[custom]-[" + HexUtils.bytes2HexString(msg) + "]");
        }
    }

    public void sendUnsynMsgRetry(Message msg, int retry) {
        for (int i = 0; i < retry; i++) {
            sendUnsynMsg(msg);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processMessage(Message msg) {
        if (msg == null) {
            return;
        }
        try {
            if (msg.msgType.mt_12.equals("0")) {
                int mreKey = msg.msgType.toInt();
                if (this.dicMre.containsKey(Integer.valueOf(mreKey))) {
                    this.dicMre.get(Integer.valueOf(mreKey)).data = msg;
                    this.dicMre.get(Integer.valueOf(mreKey)).evt.set();
                }
                return;
            }
            if (msg.msgType.mt_8_11.equals(EnumG.MSG_TYPE_BIT_BASE)) {
                if (msg.msgType.msgId == 0) {
                    LogBaseEpcInfo log = new LogBaseEpcInfo();
                    log.ackUnpack(msg.cData);
                    triggerTagEpcLogEvent(log);
                    return;
                }
                if (32 == msg.msgType.msgId) {
                    LogBase6bInfo log2 = new LogBase6bInfo();
                    log2.ackUnpack(msg.cData);
                    triggerTag6bLogEvent(log2);
                    return;
                }
                if (48 == msg.msgType.msgId) {
                    LogBaseGbInfo log3 = new LogBaseGbInfo();
                    log3.ackUnpack(msg.cData);
                    triggerTagGbLogEvent(log3);
                    return;
                }
                if (64 == msg.msgType.msgId) {
                    LogBaseGJbInfo log4 = new LogBaseGJbInfo();
                    log4.ackUnpack(msg.cData);
                    triggerTagGJbLogEvent(log4);
                    return;
                }
                if (1 == msg.msgType.msgId) {
                    LogBaseEpcOver log5 = new LogBaseEpcOver();
                    log5.ackUnpack(msg.cData);
                    triggerTagEpcOverEvent(log5);
                    return;
                }
                if (33 == msg.msgType.msgId) {
                    LogBase6bOver log6 = new LogBase6bOver();
                    log6.ackUnpack(msg.cData);
                    triggerTag6bOverEvent(log6);
                    return;
                }
                if (49 == msg.msgType.msgId) {
                    LogBaseGbOver log7 = new LogBaseGbOver();
                    log7.ackUnpack(msg.cData);
                    triggerTagGbOverEvent(log7);
                    return;
                } else {
                    if (65 == msg.msgType.msgId) {
                        LogBaseGJbOver log8 = new LogBaseGJbOver();
                        log8.ackUnpack(msg.cData);
                        triggerTagGJbOverEvent(log8);
                        return;
                    }
                    if (50 == msg.msgType.msgId) {
                        LogBaseGbSafeParam log9 = new LogBaseGbSafeParam();
                        log9.ackUnpack(msg.cData);
                        triggerTagGbSafeParamEvent(log9);
                    } else if (67 == msg.msgType.msgId) {
                        triggerTagGJbRn11LogEvent(msg.cData);
                        return;
                    }
                    return;
                }
            }
            if (msg.msgType.mt_8_11.equals(EnumG.MSG_TYPE_BIT_APP)) {
                if (18 == msg.msgType.msgId) {
                    if (this.isPrint) {
                        GLog.d("[heartbeat]");
                    }
                    MsgAppHeartbeat msgAppHeartbeat = new MsgAppHeartbeat();
                    msgAppHeartbeat.msgType = msg.msgType;
                    msgAppHeartbeat.ackUnpack(msg.cData);
                    sendUnsynMsg(msgAppHeartbeat);
                    HandlerHeartbeatLog handlerHeartbeatLog = this.onHeartbeatLog;
                    if (handlerHeartbeatLog != null) {
                        handlerHeartbeatLog.log(this.name);
                    }
                }
                if (msg.msgType.msgId == 0) {
                    LogAppGpiStart log10 = new LogAppGpiStart();
                    log10.ackUnpack(msg.cData);
                    triggerGpiStart(log10);
                } else if (1 == msg.msgType.msgId) {
                    LogAppGpiOver log11 = new LogAppGpiOver();
                    log11.ackUnpack(msg.cData);
                    triggerGpiOver(log11);
                } else if (27 == msg.msgType.msgId) {
                    MsgAppGetCacheTagData cacheTagData = new MsgAppGetCacheTagData();
                    cacheTagData.ackUnpack(msg.cData);
                    triggerCacheDataOver(cacheTagData);
                }
            }
        } catch (Exception e) {
        }
    }
}
