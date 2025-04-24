package com.gg.reader.api.dal.communication;

import com.gg.reader.api.protocol.gx.Message;
import com.gg.reader.api.utils.ThreadPoolUtils;
import com.rscja.deviceapi.Module;
import com.rscja.deviceapi.exception.ConfigurationException;
import java.net.Socket;

/* loaded from: classes.dex */
public class CWSerialClient extends CommunicationInterface {
    private Module instance;

    public CWSerialClient() {
        try {
            this.instance = Module.getInstance();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public boolean powerOn(int module) {
        Module module2 = this.instance;
        if (module2 != null) {
            return module2.powerOn(module);
        }
        return false;
    }

    public boolean powerOff(int module) {
        Module module2 = this.instance;
        if (module2 != null) {
            return module2.powerOff(module);
        }
        return false;
    }

    public boolean init(int module) {
        Module module2 = this.instance;
        if (module2 != null) {
            return module2.init(module);
        }
        return false;
    }

    public boolean free() {
        Module module = this.instance;
        if (module != null) {
            return module.free();
        }
        return false;
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public boolean open(String s, int i) {
        return false;
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public boolean open(Socket socket) {
        return false;
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public boolean open(String s, int i, int i1) {
        return false;
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public boolean open(String s) {
        if (s == null) {
            return false;
        }
        String[] split = s.split(":");
        Module module = this.instance;
        if (module != null) {
            boolean isOpen = module.openSerail(split[0], Integer.parseInt(split[1]), 8, 1, 0);
            if (isOpen) {
                this.keepReceived = true;
                startReceive();
                startProcess();
                return true;
            }
        }
        return false;
    }

    public void startReceive() {
        ThreadPoolUtils.run(new Runnable() { // from class: com.gg.reader.api.dal.communication.CWSerialClient.1
            @Override // java.lang.Runnable
            public void run() {
                while (CWSerialClient.this.keepReceived) {
                    try {
                        byte[] bytes = CWSerialClient.this.instance.receiveEx();
                        if (bytes.length <= 0) {
                            Thread.sleep(100L);
                        } else {
                            synchronized (CWSerialClient.this.lockRingBuffer) {
                                while (bytes.length + CWSerialClient.this.ringBuffer.getDataCount() > 1048576) {
                                    CWSerialClient.this.lockRingBuffer.wait(10000L);
                                }
                                CWSerialClient.this.ringBuffer.WriteBuffer(bytes, 0, bytes.length);
                                CWSerialClient.this.lockRingBuffer.notify();
                            }
                        }
                    } catch (Exception e) {
                        try {
                            Thread.sleep(3000L);
                        } catch (InterruptedException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public void close() {
        if (this.instance != null) {
            this.keepReceived = false;
            this.instance.closeSerail();
        }
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public void send(byte[] bytes) {
        synchronized (this) {
            try {
                this.instance.send(bytes);
            } catch (Exception e) {
            }
        }
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public void send(Message message) {
        try {
            if (this.isRs485) {
                message.msgType.mt_13 = "1";
                message.rs485Address = getRs485Address();
            }
            message.pack();
            send(message.toBytes(this.isRs485));
        } catch (Exception e) {
        }
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public int receive(byte[] bytes) {
        return 0;
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public boolean setBufferSize(int i) {
        return false;
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public void dispose() {
    }
}
