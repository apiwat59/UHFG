package com.gg.reader.api.dal.communication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import androidx.vectordrawable.graphics.drawable.PathInterpolatorCompat;
import com.gg.reader.api.protocol.gx.Message;
import com.gg.reader.api.protocol.gx.MsgAppGetReaderInfo;
import com.gg.reader.api.utils.StringUtils;
import com.gg.reader.api.utils.ThreadPoolUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/* loaded from: classes.dex */
public class BluetoothClient extends CommunicationInterface {
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public BluetoothHandler bluetoothHandler;
    private BluetoothDevice device;
    private InputStream inputStream;
    private OutputStream outputStream;
    private BluetoothSocket socket;
    private String TAG = BluetoothClient.class.getName();
    private Date lastUrgentData = null;
    private int count = 1;
    private int reconnection = 10;
    private int tempReConCount = 0;
    private BroadcastReceiver receiver = new BroadcastReceiver() { // from class: com.gg.reader.api.dal.communication.BluetoothClient.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Set<BluetoothDevice> bondDevice = BluetoothClient.this.bluetoothAdapter.getBondedDevices();
            if (bondDevice.size() > 0) {
                for (BluetoothDevice info : bondDevice) {
                    if (BluetoothClient.this.bluetoothHandler != null) {
                        BluetoothClient.this.bluetoothHandler.dispense(info);
                    }
                }
            }
            if ("android.bluetooth.device.action.FOUND".equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                if (device.getBondState() != 12 && BluetoothClient.this.bluetoothHandler != null) {
                    if (!StringUtils.isNullOfEmpty(device.getName())) {
                        BluetoothClient.this.bluetoothHandler.dispense(device);
                        return;
                    } else {
                        BluetoothClient.this.bluetoothHandler.dispense(device);
                        return;
                    }
                }
                return;
            }
            if ("android.bluetooth.adapter.action.DISCOVERY_FINISHED".equals(action)) {
                if (BluetoothClient.this.bluetoothHandler != null) {
                    BluetoothClient.this.bluetoothHandler.finishDiscover();
                }
            } else if ("android.bluetooth.adapter.action.DISCOVERY_STARTED".equals(action) && BluetoothClient.this.bluetoothHandler != null) {
                BluetoothClient.this.bluetoothHandler.startDiscover();
            }
        }
    };
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    public BluetoothAdapter getAdapter() {
        return this.bluetoothAdapter;
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public boolean open(String s, int i) {
        try {
            if (this.socket == null && !StringUtils.isNullOfEmpty(s)) {
                if (this.bluetoothAdapter.isDiscovering()) {
                    this.bluetoothAdapter.cancelDiscovery();
                }
                BluetoothDevice remoteDevice = this.bluetoothAdapter.getRemoteDevice(s);
                this.device = remoteDevice;
                if (i == 0) {
                    this.socket = remoteDevice.createInsecureRfcommSocketToServiceRecord(SPP_UUID);
                } else {
                    this.socket = remoteDevice.createRfcommSocketToServiceRecord(SPP_UUID);
                }
                this.socket.connect();
                this.inputStream = this.socket.getInputStream();
                this.outputStream = this.socket.getOutputStream();
                this.keepReceived = true;
                this.lastUrgentData = new Date();
                startReceive();
                startProcess();
                return true;
            }
            return false;
        } catch (Exception e) {
            try {
                this.socket.close();
                this.socket = null;
            } catch (IOException e2) {
                Log.e(BluetoothClient.class.getName(), e2.getMessage());
            }
            return false;
        }
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
    public boolean open(String param) {
        try {
            if (this.socket == null && !StringUtils.isNullOfEmpty(param)) {
                if (this.bluetoothAdapter.isDiscovering()) {
                    this.bluetoothAdapter.cancelDiscovery();
                }
                BluetoothDevice remoteDevice = this.bluetoothAdapter.getRemoteDevice(param);
                this.device = remoteDevice;
                BluetoothSocket createInsecureRfcommSocketToServiceRecord = remoteDevice.createInsecureRfcommSocketToServiceRecord(SPP_UUID);
                this.socket = createInsecureRfcommSocketToServiceRecord;
                createInsecureRfcommSocketToServiceRecord.connect();
                this.inputStream = this.socket.getInputStream();
                this.outputStream = this.socket.getOutputStream();
                this.keepReceived = true;
                this.lastUrgentData = new Date();
                startReceive();
                startProcess();
                return true;
            }
            return false;
        } catch (Exception e) {
            try {
                this.socket.close();
                this.socket = null;
            } catch (IOException e2) {
                Log.e(BluetoothClient.class.getName(), e2.getMessage());
            }
            return false;
        }
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public void close() {
        try {
            this.keepReceived = false;
            this.onDisconnected = null;
            BluetoothSocket bluetoothSocket = this.socket;
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
                this.inputStream = null;
                this.outputStream = null;
            }
            this.socket = null;
            synchronized (this.lockRingBuffer) {
                this.lockRingBuffer.notifyAll();
            }
        } catch (Exception e) {
        }
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public void send(byte[] bytes) {
        synchronized (this) {
            try {
                OutputStream outputStream = this.outputStream;
                if (outputStream != null) {
                    outputStream.write(bytes);
                }
            } catch (IOException e) {
            }
        }
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public void send(Message message) {
        try {
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

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isRemoteClosed() {
        if (this.socket == null) {
            return true;
        }
        Date now = new Date();
        long time = now.getTime() - this.lastUrgentData.getTime();
        int i = this.count;
        if (time > i * PathInterpolatorCompat.MAX_NUM_POINTS) {
            try {
                this.count = i + 1;
                send(new MsgAppGetReaderInfo());
            } catch (Exception e) {
            }
        }
        return now.getTime() - this.lastUrgentData.getTime() > 15000;
    }

    public void startReceive() {
        ThreadPoolUtils.run(new Runnable() { // from class: com.gg.reader.api.dal.communication.BluetoothClient.1
            @Override // java.lang.Runnable
            public void run() {
                while (BluetoothClient.this.keepReceived) {
                    try {
                        int len = BluetoothClient.this.inputStream.available();
                        if (len <= 0) {
                            Thread.sleep(100L);
                        }
                        if (len > 0) {
                            len = BluetoothClient.this.inputStream.read(BluetoothClient.this.rcvBuff, 0, BluetoothClient.this.rcvBuff.length);
                            synchronized (BluetoothClient.this.lockRingBuffer) {
                                while (BluetoothClient.this.ringBuffer.getDataCount() + len > 1048576) {
                                    BluetoothClient.this.lockRingBuffer.wait(10000L);
                                }
                                BluetoothClient.this.ringBuffer.WriteBuffer(BluetoothClient.this.rcvBuff, 0, len);
                                BluetoothClient.this.lockRingBuffer.notify();
                            }
                        }
                        if (!BluetoothClient.this._isSendHeartbeat) {
                            continue;
                        } else if (len <= 0) {
                            if (BluetoothClient.this.isRemoteClosed()) {
                                throw new Exception("remote closed.");
                            }
                        } else {
                            BluetoothClient.this.lastUrgentData = new Date();
                            BluetoothClient.this.count = 1;
                        }
                    } catch (Exception e) {
                        try {
                            BluetoothClient.this.triggerDisconnected();
                            Thread.sleep(3000L);
                        } catch (InterruptedException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public void scanBluetooth(Context context) {
        BluetoothAdapter bluetoothAdapter = this.bluetoothAdapter;
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                this.bluetoothAdapter.enable();
                return;
            } else {
                this.bluetoothAdapter.startDiscovery();
                return;
            }
        }
        Log.e(this.TAG, "当前设备没有蓝牙模块");
    }

    public void registerBluetoothScanReceiver(Context context) {
        IntentFilter filter = new IntentFilter("android.bluetooth.device.action.FOUND");
        filter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        filter.addAction("android.bluetooth.device.action.FOUND");
        filter.addAction("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
        filter.addAction("android.bluetooth.adapter.action.DISCOVERY_STARTED");
        filter.addAction("android.bluetooth.device.action.BOND_STATE_CHANGED");
        context.registerReceiver(this.receiver, filter);
    }

    public void unRegisterBluetoothScanReceiver(Context context) {
        context.unregisterReceiver(this.receiver);
    }

    public void stopScanner() {
        BluetoothAdapter bluetoothAdapter = this.bluetoothAdapter;
        if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering()) {
            this.bluetoothAdapter.cancelDiscovery();
        }
    }

    public void startScanner() {
        BluetoothAdapter bluetoothAdapter = this.bluetoothAdapter;
        if (bluetoothAdapter != null && !bluetoothAdapter.isDiscovering()) {
            this.bluetoothAdapter.startDiscovery();
        }
    }

    public Set<BluetoothDevice> getBondDevice() {
        BluetoothAdapter bluetoothAdapter = this.bluetoothAdapter;
        if (bluetoothAdapter != null) {
            return bluetoothAdapter.getBondedDevices();
        }
        return new HashSet();
    }

    public void setDisPlay(Context context, int time) {
        Intent displayIntent = new Intent("android.bluetooth.adapter.action.REQUEST_DISCOVERABLE");
        displayIntent.putExtra("android.bluetooth.adapter.extra.DISCOVERABLE_DURATION", time);
        context.startActivity(displayIntent);
    }

    public void openBluetooth() {
        BluetoothAdapter bluetoothAdapter = this.bluetoothAdapter;
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            this.bluetoothAdapter.enable();
        }
    }

    public void closeBluetooth() {
        BluetoothAdapter bluetoothAdapter = this.bluetoothAdapter;
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            this.bluetoothAdapter.disable();
        }
    }

    private void openBluetoothSetting(Context context) {
        Intent intent = new Intent();
        intent.setAction("android.settings.BLUETOOTH_SETTINGS");
        intent.setFlags(268435456);
        context.startActivity(intent);
    }
}
