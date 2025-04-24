package com.gg.reader.api.dal.communication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import androidx.lifecycle.CoroutineLiveDataKt;
import com.gg.reader.api.dal.communication.BleClientCallback;
import com.gg.reader.api.protocol.gx.Message;
import com.gg.reader.api.utils.HexUtils;
import com.gg.reader.api.utils.StringUtils;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/* loaded from: classes.dex */
public class BleBluetoothClient extends CommunicationInterface {
    private static final UUID DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private BluetoothAdapter adapter;
    private BluetoothGatt bluetoothGatt;
    public BleClientCallback.OnBlueConnectCallBack connectCallBack;
    private Context context;
    private BluetoothDevice device;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothGattCharacteristic mWriteCharacteristic;
    private BluetoothManager manager;
    public BleClientCallback.OnBlueScanCallBack scanCallBack;
    private final String TAG = BleBluetoothClient.class.getName();
    private UUID SERVER_UUID = UUID.fromString("0000fff0-0000-1000-8000-00805F9B34FB");
    private UUID NOTIFY_UUID = UUID.fromString("0000fff1-0000-1000-8000-00805F9B34FB");
    private UUID WRITE_UUID = UUID.fromString("0000fff2-0000-1000-8000-00805F9B34FB");
    private Map<String, BluetoothDevice> deviceMap = new HashMap();
    private Handler scanHandler = new Handler();
    private long writeTime = 50;
    private int mtu = 512;
    private boolean isPackage = false;
    BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() { // from class: com.gg.reader.api.dal.communication.BleBluetoothClient.1
        @Override // android.bluetooth.BluetoothAdapter.LeScanCallback
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            BleBluetoothClient.this.deviceMap.put(device.getAddress(), device);
            BleBluetoothClient.this.scanCallBack.onBlueFind(device);
        }
    };
    private final Runnable mScanRunnable = new Runnable() { // from class: com.gg.reader.api.dal.communication.BleBluetoothClient.2
        @Override // java.lang.Runnable
        public void run() {
            BleBluetoothClient.this.scanBluetooth(false, CoroutineLiveDataKt.DEFAULT_TIMEOUT);
        }
    };
    BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() { // from class: com.gg.reader.api.dal.communication.BleBluetoothClient.3
        @Override // android.bluetooth.BluetoothGattCallback
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState != 2) {
                if (newState == 0) {
                    Log.e(BleBluetoothClient.this.TAG, "STATE_DISCONNECTED");
                    BleBluetoothClient.this.connectCallBack.onDisconnect();
                    return;
                } else {
                    if (newState == 3) {
                        Log.e(BleBluetoothClient.this.TAG, "STATE_DISCONNECTING");
                        return;
                    }
                    return;
                }
            }
            Log.e(BleBluetoothClient.this.TAG, "STATE_CONNECTED");
            BleBluetoothClient.this.bluetoothGatt.discoverServices();
            boolean b = BleBluetoothClient.this.requestMtu(gatt);
            Log.e("requestMtu-->", b + "");
            try {
                Thread.sleep(200L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == 0) {
                BleBluetoothClient.this.connectCallBack.onConnectSuccess();
                BleBluetoothClient.this.enableTxNotification();
            } else {
                BleBluetoothClient.this.connectCallBack.onConnectFailure();
            }
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == 0 && characteristic.getValue().length > 0) {
                Log.e(BleBluetoothClient.this.TAG, "onCharacteristicRead" + Arrays.toString(characteristic.getValue()));
            }
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == 0 && characteristic.getValue().length > 0) {
                Log.e(BleBluetoothClient.this.TAG, "onCharacteristicWrite" + Arrays.toString(characteristic.getValue()));
            }
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (characteristic.getValue().length <= 0) {
                Log.e(BleBluetoothClient.this.TAG, "receive data is null");
                return;
            }
            Log.e("ble receive--->", HexUtils.bytes2HexString(characteristic.getValue()));
            try {
                byte[] data = characteristic.getValue();
                synchronized (BleBluetoothClient.this.lockRingBuffer) {
                    while (data.length + BleBluetoothClient.this.ringBuffer.getDataCount() > 1048576) {
                        BleBluetoothClient.this.lockRingBuffer.wait(10000L);
                    }
                    BleBluetoothClient.this.ringBuffer.WriteBuffer(data, 0, data.length);
                    BleBluetoothClient.this.lockRingBuffer.notify();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }
    };

    public BleBluetoothClient(Context context) {
        this.context = context;
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService("bluetooth");
        this.manager = bluetoothManager;
        this.adapter = bluetoothManager.getAdapter();
    }

    public void setWriteTime(long writeTime) {
        this.writeTime = writeTime;
    }

    public boolean isSupportBle(Context context) {
        return (context == null || !context.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le") || this.adapter == null) ? false : true;
    }

    public boolean isBleEnable(Context context) {
        if (!isSupportBle(context)) {
            return false;
        }
        return this.adapter.isEnabled();
    }

    public BluetoothAdapter getAdapter() {
        return this.adapter;
    }

    public UUID getSERVER_UUID() {
        return this.SERVER_UUID;
    }

    public void setSERVER_UUID(UUID SERVER_UUID) {
        this.SERVER_UUID = SERVER_UUID;
    }

    public UUID getNOTIFY_UUID() {
        return this.NOTIFY_UUID;
    }

    public void setNOTIFY_UUID(UUID NOTIFY_UUID) {
        this.NOTIFY_UUID = NOTIFY_UUID;
    }

    public UUID getWRITE_UUID() {
        return this.WRITE_UUID;
    }

    public void setWRITE_UUID(UUID WRITE_UUID) {
        this.WRITE_UUID = WRITE_UUID;
    }

    public void enableBluetooth() {
        this.adapter.enable();
    }

    public BluetoothGatt getBluetoothGatt() {
        return this.bluetoothGatt;
    }

    public void openBluetoothSetting() {
        Intent intent = new Intent();
        intent.setAction("android.settings.BLUETOOTH_SETTINGS");
        intent.setFlags(268435456);
        this.context.startActivity(intent);
    }

    public void scanBluetooth(boolean enable, long scanTime) {
        if (enable) {
            if (this.adapter.isEnabled()) {
                this.scanHandler.postDelayed(this.mScanRunnable, scanTime);
                this.adapter.startLeScan(this.leScanCallback);
                return;
            }
            return;
        }
        this.adapter.stopLeScan(this.leScanCallback);
        this.scanHandler.removeCallbacks(this.mScanRunnable);
    }

    public void stopScanBluetooth() {
        this.adapter.stopLeScan(this.leScanCallback);
    }

    public void enableTxNotification() {
        BluetoothGattService service = this.bluetoothGatt.getService(this.SERVER_UUID);
        this.mWriteCharacteristic = service.getCharacteristic(this.WRITE_UUID);
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(this.NOTIFY_UUID);
        this.mNotifyCharacteristic = characteristic;
        this.bluetoothGatt.setCharacteristicNotification(characteristic, true);
        BluetoothGattDescriptor mDescriptor = this.mNotifyCharacteristic.getDescriptor(DESCRIPTOR);
        if (mDescriptor != null) {
            if ((this.mNotifyCharacteristic.getProperties() & 16) > 0) {
                mDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            } else if ((this.mNotifyCharacteristic.getProperties() & 32) > 0) {
                mDescriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
            }
        }
        this.bluetoothGatt.writeDescriptor(mDescriptor);
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public boolean open(String s, int i) {
        if (StringUtils.isNullOfEmpty(s)) {
            return false;
        }
        if (this.adapter.isDiscovering()) {
            this.adapter.cancelDiscovery();
        }
        BluetoothGatt bluetoothGatt = this.bluetoothGatt;
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            this.bluetoothGatt.discoverServices();
        }
        if (this.deviceMap.containsKey(s)) {
            this.device = this.deviceMap.get(s);
        } else {
            this.device = this.adapter.getRemoteDevice(s);
        }
        if (this.device == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= 26) {
            this.bluetoothGatt = this.device.connectGatt(this.context, false, this.mBluetoothGattCallback, 2, 1);
        } else if (Build.VERSION.SDK_INT >= 23) {
            this.bluetoothGatt = this.device.connectGatt(this.context, false, this.mBluetoothGattCallback, 2);
        } else {
            this.bluetoothGatt = this.device.connectGatt(this.context, false, this.mBluetoothGattCallback);
        }
        this.keepReceived = true;
        startProcess();
        return true;
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
        return false;
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public void close() {
        this.keepReceived = false;
        BluetoothGatt bluetoothGatt = this.bluetoothGatt;
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            this.bluetoothGatt.close();
            this.mWriteCharacteristic = null;
            this.mNotifyCharacteristic = null;
            this.bluetoothGatt = null;
        }
        this.device = null;
        synchronized (this.lockRingBuffer) {
            this.lockRingBuffer.notifyAll();
        }
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public void send(byte[] data) {
        synchronized (BleBluetoothClient.class) {
            try {
                BluetoothGattCharacteristic bluetoothGattCharacteristic = this.mWriteCharacteristic;
                if (bluetoothGattCharacteristic != null) {
                    if (this.isPackage) {
                        int size = data.length / 20;
                        if (data.length % 20 != 0) {
                            size++;
                        }
                        for (int i = 0; i < size; i++) {
                            byte[] newBuffer = Arrays.copyOfRange(data, i * 20, (i * 20) + 20);
                            this.mWriteCharacteristic.setValue(newBuffer);
                            this.bluetoothGatt.writeCharacteristic(this.mWriteCharacteristic);
                            Thread.sleep(this.writeTime);
                        }
                    } else {
                        bluetoothGattCharacteristic.setValue(data);
                        this.bluetoothGatt.writeCharacteristic(this.mWriteCharacteristic);
                        Thread.sleep(this.writeTime);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public void send(Message message) {
        synchronized (BleBluetoothClient.class) {
            try {
                message.pack();
                send(message.toBytes(this.isRs485));
            } catch (Exception ex) {
                ex.printStackTrace();
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

    /* JADX INFO: Access modifiers changed from: private */
    public boolean requestMtu(BluetoothGatt gatt) {
        if (gatt != null) {
            return gatt.requestMtu(this.mtu);
        }
        return false;
    }

    public void setMtu(int value) {
        this.mtu = value;
    }

    public void setPartPackage(boolean isValue) {
        this.isPackage = isValue;
    }
}
