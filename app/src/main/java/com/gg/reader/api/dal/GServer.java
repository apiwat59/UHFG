package com.gg.reader.api.dal;

import androidx.vectordrawable.graphics.drawable.PathInterpolatorCompat;
import com.gg.reader.api.dal.communication.TcpClient;
import com.gg.reader.api.dal.communication.TcpServer;
import com.gg.reader.api.protocol.gx.MsgAppGetReaderInfo;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class GServer {
    private int MSG_TIME_OUT = PathInterpolatorCompat.MAX_NUM_POINTS;
    private HashMap<String, GClient> hpClient = new HashMap<>();
    public HandlerGClientConnected onGClientConnected;
    private TcpServer ts;

    public boolean isListend() {
        TcpServer tcpServer = this.ts;
        if (tcpServer == null) {
            return false;
        }
        return tcpServer.keepListen.booleanValue();
    }

    protected void triggerGClientConnectedEvent(GClient client, String serialNumber) {
        try {
            HandlerGClientConnected handlerGClientConnected = this.onGClientConnected;
            if (handlerGClientConnected != null) {
                synchronized (handlerGClientConnected) {
                    this.onGClientConnected.log(client, serialNumber);
                }
            }
        } catch (Exception e) {
        }
    }

    public boolean open(int param) {
        if (this.ts != null) {
            return false;
        }
        TcpServer tcpServer = new TcpServer();
        this.ts = tcpServer;
        tcpServer.onRemoteConnected = new HandlerRemoteConnected() { // from class: com.gg.reader.api.dal.GServer.1
            @Override // com.gg.reader.api.dal.HandlerRemoteConnected
            public void log(TcpClient client) {
                GServer.this.processConnect(client);
            }
        };
        if (!this.ts.open(param)) {
            close();
            return false;
        }
        return true;
    }

    public void close() {
        TcpServer tcpServer = this.ts;
        if (tcpServer != null) {
            tcpServer.close();
            this.ts = null;
        }
    }

    public void closeClient(String readerName) {
        String[] arrName;
        if (readerName != null && readerName != "" && (arrName = readerName.split(":")) != null && arrName.length == 2) {
            synchronized (this.hpClient) {
                if (this.hpClient.containsKey(readerName)) {
                    this.hpClient.get(readerName).close();
                    this.hpClient.remove(readerName);
                }
            }
        }
    }

    public void closeAllClient() {
        synchronized (this.hpClient) {
            for (Map.Entry<String, GClient> item : this.hpClient.entrySet()) {
                item.getValue().close();
            }
            this.hpClient.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processConnect(TcpClient client) {
        if (client == null) {
            return;
        }
        GClient gClient = new GClient();
        String readerName = client.serverIp + ":" + client.serverPort;
        if (gClient.open(readerName, client, this.MSG_TIME_OUT)) {
            gClient.setName(readerName);
            MsgAppGetReaderInfo info = new MsgAppGetReaderInfo();
            gClient.sendSynMsg(info);
            String serialNumber = info.getRtCode() == 0 ? info.getReaderSerialNumber() : null;
            gClient.setSerialNumber(serialNumber);
            triggerGClientConnectedEvent(gClient, serialNumber);
            synchronized (this.hpClient) {
                if (this.hpClient.containsKey(readerName)) {
                    this.hpClient.get(readerName).close();
                }
                this.hpClient.put(readerName, gClient);
            }
            return;
        }
        gClient.close();
        client.close();
    }
}
