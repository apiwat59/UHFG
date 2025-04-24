package com.android.usbserial.client;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import com.android.usbserial.driver.UsbSerialDriver;
import com.android.usbserial.driver.UsbSerialPort;
import com.android.usbserial.driver.UsbSerialProber;
import com.android.usbserial.util.SerialInputOutputManager;
import com.gg.reader.api.dal.communication.CommunicationInterface;
import com.gg.reader.api.dal.communication.OnUsbSerialStateListener;
import com.gg.reader.api.protocol.gx.Message;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class AndroidUsbSerialClient extends CommunicationInterface implements SerialInputOutputManager.Listener {
    private static final String ACTION_USB_PERMISSION = "com.android.gx.USB_PERMISSION";
    public OnUsbSerialDeviceListener deviceListener;
    private Context mContext;
    private Handler mHandler;
    private SerialInputOutputManager mSerialIoManager;
    private UsbManager mUsbManager;
    private String mUsbName;
    public OnUsbSerialStateListener stateListener;
    private UsbSerialPort usbSerialPort;
    private int mBaudRate = 115200;
    private int writeTimeout = 100;
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() { // from class: com.android.usbserial.client.AndroidUsbSerialClient.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (AndroidUsbSerialClient.ACTION_USB_PERMISSION.equals(action)) {
                context.unregisterReceiver(this);
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra("device");
                    if (!intent.getBooleanExtra("permission", false) || device == null) {
                        AndroidUsbSerialClient.this.onConnectFailed();
                    } else {
                        AndroidUsbSerialClient.this.openDevice();
                    }
                }
            }
        }
    };
    private final BroadcastReceiver mUsbStateChange = new BroadcastReceiver() { // from class: com.android.usbserial.client.AndroidUsbSerialClient.4
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.hardware.usb.action.USB_DEVICE_ATTACHED".equals(action)) {
                if (AndroidUsbSerialClient.this.stateListener != null) {
                    AndroidUsbSerialClient.this.stateListener.onDeviceAttached();
                }
            } else if ("android.hardware.usb.action.USB_DEVICE_DETACHED".equals(action) && AndroidUsbSerialClient.this.stateListener != null) {
                AndroidUsbSerialClient.this.stateListener.onDeviceDetached();
            }
        }
    };

    public UsbDevice getUsbDevice() {
        return this.usbSerialPort.getDriver().getDevice();
    }

    public int getBaudRate() {
        return this.mBaudRate;
    }

    public void setBaudRate(int mBaudRate) {
        this.mBaudRate = mBaudRate;
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

    public void setmUsbName(String mUsbName) {
        this.mUsbName = mUsbName;
    }

    public static Map<String, AndroidUsbSerialClient> getUsbDevicesMap(Context context) {
        UsbManager mUsbManager = (UsbManager) context.getApplicationContext().getSystemService("usb");
        if (mUsbManager == null) {
            return null;
        }
        List<UsbSerialDriver> drivers = UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);
        Map<String, AndroidUsbSerialClient> driverMap = new HashMap<>();
        for (UsbSerialDriver driver : drivers) {
            List<UsbSerialPort> ports = driver.getPorts();
            for (UsbSerialPort port : ports) {
                UsbDevice device = port.getDriver().getDevice();
                String s = driver.getDriverName() + "_vid_" + device.getVendorId() + "&pid_" + device.getProductId();
                AndroidUsbSerialClient usbSerialClient = new AndroidUsbSerialClient(port, context, mUsbManager, s);
                driverMap.put(s, usbSerialClient);
            }
        }
        return driverMap;
    }

    public static Map<String, AndroidUsbSerialClient> getUsbDevicesMap(Context context, int vid, int pid) {
        UsbManager mUsbManager = (UsbManager) context.getApplicationContext().getSystemService("usb");
        if (mUsbManager == null) {
            return null;
        }
        List<UsbSerialDriver> drivers = UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);
        Map<String, AndroidUsbSerialClient> driverMap = new HashMap<>();
        for (UsbSerialDriver driver : drivers) {
            List<UsbSerialPort> ports = driver.getPorts();
            for (UsbSerialPort port : ports) {
                UsbDevice device = port.getDriver().getDevice();
                if (device.getVendorId() == vid && device.getProductId() == pid) {
                    String s = driver.getDriverName() + "_vid_" + device.getVendorId() + "&pid_" + device.getProductId();
                    AndroidUsbSerialClient usbSerialClient = new AndroidUsbSerialClient(port, context, mUsbManager, s);
                    driverMap.put(s, usbSerialClient);
                }
            }
        }
        return driverMap;
    }

    public static List<AndroidUsbSerialClient> getUsbDevicesList(Context context) {
        Map<String, AndroidUsbSerialClient> usbDevicesMap = getUsbDevicesMap(context);
        if (usbDevicesMap == null) {
            return null;
        }
        return new ArrayList(usbDevicesMap.values());
    }

    public static List<AndroidUsbSerialClient> getUsbDevicesList(Context context, int vid, int pid) {
        Map<String, AndroidUsbSerialClient> usbDevicesMap = getUsbDevicesMap(context, vid, pid);
        if (usbDevicesMap == null) {
            return null;
        }
        return new ArrayList(usbDevicesMap.values());
    }

    private AndroidUsbSerialClient(UsbSerialPort serialPort, Context context, UsbManager usbManager, String usbName) {
        this.usbSerialPort = serialPort;
        this.mContext = context;
        this.mUsbManager = usbManager;
        this.mUsbName = usbName;
    }

    private void hasPermission() {
        this.mHandler = new Handler(this.mContext.getMainLooper());
        if (!this.mUsbManager.hasPermission(getUsbDevice())) {
            PendingIntent permissionIntent = PendingIntent.getBroadcast(this.mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            this.mContext.registerReceiver(this.mUsbReceiver, filter);
            this.mUsbManager.requestPermission(getUsbDevice(), permissionIntent);
            return;
        }
        openDevice();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openDevice() {
        UsbDeviceConnection connection = this.mUsbManager.openDevice(getUsbDevice());
        if (connection == null) {
            onConnectFailed();
            return;
        }
        try {
            this.usbSerialPort.open(connection);
            this.usbSerialPort.setParameters(this.mBaudRate, 8, 1, 0);
            this.mSerialIoManager = new SerialInputOutputManager(this.usbSerialPort, this);
            this.keepReceived = true;
            this.mSerialIoManager.start();
            startProcess();
            this.mHandler.post(new Runnable() { // from class: com.android.usbserial.client.AndroidUsbSerialClient.2
                @Override // java.lang.Runnable
                public void run() {
                    if (AndroidUsbSerialClient.this.deviceListener != null) {
                        AndroidUsbSerialClient.this.deviceListener.onDeviceConnected();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onConnectFailed() {
        this.mHandler.post(new Runnable() { // from class: com.android.usbserial.client.AndroidUsbSerialClient.3
            @Override // java.lang.Runnable
            public void run() {
                if (AndroidUsbSerialClient.this.deviceListener != null) {
                    AndroidUsbSerialClient.this.deviceListener.onDeviceConnectFailed();
                }
            }
        });
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public boolean open(String device_name, int port) {
        return false;
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public boolean open(Socket sConn) {
        return false;
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public boolean open(String device_name, int port, int timeout) {
        return false;
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public boolean open(String param) {
        hasPermission();
        return true;
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public void close() {
        this.keepReceived = false;
        if (this.usbSerialPort != null) {
            try {
                this.mSerialIoManager.stop();
                this.usbSerialPort.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            synchronized (this.lockRingBuffer) {
                this.lockRingBuffer.notify();
                this.ringBuffer.Clear();
            }
        }
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public void send(byte[] data) {
        UsbSerialPort usbSerialPort = this.usbSerialPort;
        if (usbSerialPort != null) {
            int maxPacketSize = usbSerialPort.getWriteEndpoint().getMaxPacketSize();
            int pack = data.length / maxPacketSize;
            if (data.length % maxPacketSize > 0) {
                pack++;
            }
            for (int i = 0; i < pack; i++) {
                byte[] newBuffer = Arrays.copyOfRange(data, i * maxPacketSize, (i * maxPacketSize) + maxPacketSize);
                try {
                    this.usbSerialPort.write(newBuffer, this.writeTimeout);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public void send(Message msg) {
        try {
            msg.pack();
            send(msg.toBytes(this.isRs485));
        } catch (Exception e) {
        }
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public int receive(byte[] buffer) {
        return 0;
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public boolean setBufferSize(int size) {
        return false;
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public void dispose() {
    }

    @Override // com.android.usbserial.util.SerialInputOutputManager.Listener
    public void onNewData(byte[] data) {
        try {
            synchronized (this.lockRingBuffer) {
                while (data.length + this.ringBuffer.getDataCount() > 1048576) {
                    this.lockRingBuffer.wait(10000L);
                }
                this.ringBuffer.WriteBuffer(data, 0, data.length);
                this.lockRingBuffer.notify();
            }
        } catch (Exception e) {
        }
    }

    @Override // com.android.usbserial.util.SerialInputOutputManager.Listener
    public void onRunError(Exception e) {
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
