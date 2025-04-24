package com.gg.reader.api.dal.communication;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Handler;
import com.gg.reader.api.protocol.gx.Message;
import com.gg.reader.api.protocol.gx.MsgAppGetBaseVersion;
import com.gg.reader.api.utils.GLog;
import com.gg.reader.api.utils.ThreadPoolUtils;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class AndroidUsbHidClient extends CommunicationInterface {
    private static final String ACTION_USB_PERMISSION = "com.android.gx.USB_PERMISSION";
    public OnUsbHidDeviceListener deviceListener;
    private UsbDeviceConnection mConnection;
    private Context mContext;
    private Handler mHandler;
    private UsbEndpoint mInUsbEndpoint;
    private UsbEndpoint mOutUsbEndpoint;
    private UsbDevice mUsbDevice;
    private UsbInterface mUsbInterface;
    private UsbManager mUsbManager;
    private String mUsbName;
    public OnUsbHidStateListener stateListener;
    private int readTimeout = 1000;
    private int writeTimeout = 1000;
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() { // from class: com.gg.reader.api.dal.communication.AndroidUsbHidClient.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (AndroidUsbHidClient.ACTION_USB_PERMISSION.equals(action)) {
                context.unregisterReceiver(this);
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra("device");
                    if (!intent.getBooleanExtra("permission", false) || device == null) {
                        AndroidUsbHidClient.this.onConnectFailed();
                    } else {
                        AndroidUsbHidClient.this.openDevice();
                    }
                }
            }
        }
    };
    private final BroadcastReceiver mUsbStateChange = new BroadcastReceiver() { // from class: com.gg.reader.api.dal.communication.AndroidUsbHidClient.5
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.hardware.usb.action.USB_DEVICE_ATTACHED".equals(action)) {
                if (AndroidUsbHidClient.this.stateListener != null) {
                    AndroidUsbHidClient.this.stateListener.onDeviceAttached();
                }
            } else if ("android.hardware.usb.action.USB_DEVICE_DETACHED".equals(action) && AndroidUsbHidClient.this.stateListener != null) {
                AndroidUsbHidClient.this.stateListener.onDeviceDetached();
            }
        }
    };

    public static Map<String, AndroidUsbHidClient> enumerate(Context context, int vid, int pid) {
        UsbManager usbManager = (UsbManager) context.getApplicationContext().getSystemService("usb");
        if (usbManager == null) {
            return null;
        }
        Map<String, UsbDevice> devices = usbManager.getDeviceList();
        Map<String, AndroidUsbHidClient> usbHidDevices = new HashMap<>();
        for (String key : devices.keySet()) {
            UsbDevice device = devices.get(key);
            if (device != null && device.getVendorId() == vid) {
                if (device.getProductId() == pid) {
                    for (int i = 0; i < device.getInterfaceCount(); i++) {
                        UsbInterface usbInterface = device.getInterface(i);
                        if (usbInterface.getInterfaceClass() == 3 && usbInterface.getInterfaceProtocol() == 0) {
                            AndroidUsbHidClient hidDevice = new AndroidUsbHidClient(device, usbInterface, usbManager, context, key);
                            usbHidDevices.put(key, hidDevice);
                        }
                    }
                }
            }
        }
        return usbHidDevices;
    }

    public static Map<String, AndroidUsbHidClient> enumerate(Context context) {
        UsbManager usbManager = (UsbManager) context.getApplicationContext().getSystemService("usb");
        if (usbManager == null) {
            return null;
        }
        Map<String, UsbDevice> devices = usbManager.getDeviceList();
        Map<String, AndroidUsbHidClient> usbHidDevices = new HashMap<>();
        for (String key : devices.keySet()) {
            UsbDevice device = devices.get(key);
            if (device != null && device.getVendorId() == 1003 && device.getProductId() == 9249) {
                for (int i = 0; i < device.getInterfaceCount(); i++) {
                    UsbInterface usbInterface = device.getInterface(i);
                    if (usbInterface.getInterfaceClass() == 3 && usbInterface.getInterfaceProtocol() == 0) {
                        AndroidUsbHidClient hidDevice = new AndroidUsbHidClient(device, usbInterface, usbManager, context, key);
                        usbHidDevices.put(key, hidDevice);
                    }
                }
            }
        }
        return usbHidDevices;
    }

    public static List<AndroidUsbHidClient> getUsbHidList(Context context) {
        Map<String, AndroidUsbHidClient> enumerate = enumerate(context);
        if (enumerate == null) {
            return null;
        }
        return new ArrayList(enumerate.values());
    }

    public static List<AndroidUsbHidClient> getUsbHidList(Context context, int vid, int pid) {
        Map<String, AndroidUsbHidClient> enumerate = enumerate(context, vid, pid);
        if (enumerate == null) {
            return null;
        }
        return new ArrayList(enumerate.values());
    }

    private AndroidUsbHidClient(UsbDevice usbDevice, UsbInterface usbInterface, UsbManager usbManager, Context context, String usbName) {
        this.mUsbDevice = usbDevice;
        this.mUsbInterface = usbInterface;
        this.mUsbManager = usbManager;
        this.mContext = context;
        this.mUsbName = usbName;
        for (int i = 0; i < this.mUsbInterface.getEndpointCount(); i++) {
            UsbEndpoint endpoint = this.mUsbInterface.getEndpoint(i);
            int dir = endpoint.getDirection();
            int type = endpoint.getType();
            if (this.mInUsbEndpoint == null && dir == 128 && type == 3) {
                this.mInUsbEndpoint = endpoint;
            }
            if (this.mOutUsbEndpoint == null && dir == 0 && type == 3) {
                this.mOutUsbEndpoint = endpoint;
            }
        }
    }

    public UsbDevice getUsbDevice() {
        return this.mUsbDevice;
    }

    public int getReadTimeout() {
        return this.readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getWriteTimeout() {
        return this.writeTimeout;
    }

    public void setWriteTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public String getUsbName() {
        return this.mUsbName;
    }

    public void hasPermission() {
        this.mHandler = new Handler(this.mContext.getMainLooper());
        if (!this.mUsbManager.hasPermission(this.mUsbDevice)) {
            PendingIntent permissionIntent = PendingIntent.getBroadcast(this.mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            this.mContext.registerReceiver(this.mUsbReceiver, filter);
            this.mUsbManager.requestPermission(this.mUsbDevice, permissionIntent);
            return;
        }
        openDevice();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openDevice() {
        UsbDeviceConnection openDevice = this.mUsbManager.openDevice(this.mUsbDevice);
        this.mConnection = openDevice;
        if (openDevice == null) {
            onConnectFailed();
            return;
        }
        if (!openDevice.claimInterface(this.mUsbInterface, true)) {
            onConnectFailed();
            return;
        }
        if (Build.VERSION.SDK_INT >= 21) {
            this.mConnection.setInterface(this.mUsbInterface);
        }
        this.keepReceived = true;
        startReceive();
        startProcess();
        this.mHandler.post(new Runnable() { // from class: com.gg.reader.api.dal.communication.AndroidUsbHidClient.2
            @Override // java.lang.Runnable
            public void run() {
                if (AndroidUsbHidClient.this.deviceListener != null) {
                    AndroidUsbHidClient.this.send(new MsgAppGetBaseVersion());
                    AndroidUsbHidClient.this.send(new MsgAppGetBaseVersion());
                    AndroidUsbHidClient.this.deviceListener.onDeviceConnected(AndroidUsbHidClient.this);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onConnectFailed() {
        this.mHandler.post(new Runnable() { // from class: com.gg.reader.api.dal.communication.AndroidUsbHidClient.3
            @Override // java.lang.Runnable
            public void run() {
                if (AndroidUsbHidClient.this.deviceListener != null) {
                    AndroidUsbHidClient.this.deviceListener.onDeviceConnectFailed(AndroidUsbHidClient.this);
                }
            }
        });
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
        hasPermission();
        return true;
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public void close() {
        try {
            this.keepReceived = false;
            UsbDeviceConnection usbDeviceConnection = this.mConnection;
            if (usbDeviceConnection != null) {
                UsbInterface usbInterface = this.mUsbInterface;
                if (usbInterface != null) {
                    usbDeviceConnection.releaseInterface(usbInterface);
                }
                this.mConnection.close();
                this.mConnection = null;
            }
            synchronized (this.lockRingBuffer) {
                this.lockRingBuffer.notifyAll();
                this.ringBuffer.Clear();
            }
        } catch (Exception e) {
        }
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public void send(byte[] data) {
        synchronized (AndroidUsbHidClient.class) {
            try {
                int maxPacketSize = this.mOutUsbEndpoint.getMaxPacketSize();
                int pack = data.length / maxPacketSize;
                if (data.length % maxPacketSize > 0) {
                    pack++;
                }
                for (int i = 0; i < pack; i++) {
                    byte[] newBuffer = Arrays.copyOfRange(data, i * maxPacketSize, (i * maxPacketSize) + maxPacketSize);
                    this.mConnection.bulkTransfer(this.mOutUsbEndpoint, newBuffer, newBuffer.length, 0);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public void send(Message msg) {
        synchronized (AndroidUsbHidClient.class) {
            try {
                if (this.isRs485) {
                    msg.msgType.mt_13 = "1";
                    msg.rs485Address = getRs485Address();
                }
                msg.pack();
                byte[] sendData = msg.toBytes(this.isRs485);
                send(sendData);
            } catch (Exception ex) {
                GLog.e("[AndroidUsbHidClient]send error:" + ex.getMessage());
            }
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

    public void startReceive() {
        ThreadPoolUtils.run(new Runnable() { // from class: com.gg.reader.api.dal.communication.AndroidUsbHidClient.4
            @Override // java.lang.Runnable
            public void run() {
                while (AndroidUsbHidClient.this.keepReceived) {
                    try {
                        byte[] buffer = new byte[AndroidUsbHidClient.this.mInUsbEndpoint.getMaxPacketSize()];
                        int bytesRead = AndroidUsbHidClient.this.mConnection.bulkTransfer(AndroidUsbHidClient.this.mInUsbEndpoint, buffer, buffer.length, AndroidUsbHidClient.this.readTimeout);
                        if (bytesRead <= 0) {
                            Thread.sleep(50L);
                        } else {
                            synchronized (AndroidUsbHidClient.this.lockRingBuffer) {
                                while (AndroidUsbHidClient.this.ringBuffer.getDataCount() + bytesRead > 1048576) {
                                    AndroidUsbHidClient.this.lockRingBuffer.wait(10000L);
                                }
                                AndroidUsbHidClient.this.ringBuffer.WriteBuffer(buffer, 0, bytesRead);
                                AndroidUsbHidClient.this.lockRingBuffer.notify();
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

    public void registerUsbState(Context context) {
        this.mUsbManager = (UsbManager) context.getSystemService("usb");
        IntentFilter usbFilter = new IntentFilter();
        usbFilter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        usbFilter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        context.registerReceiver(this.mUsbStateChange, usbFilter);
    }

    public void unregisterState(Context context) {
        context.unregisterReceiver(this.mUsbStateChange);
    }
}
