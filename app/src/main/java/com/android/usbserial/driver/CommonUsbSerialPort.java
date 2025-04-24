package com.android.usbserial.driver;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbRequest;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.android.usbserial.driver.UsbSerialPort;
import com.android.usbserial.util.MonotonicClock;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.EnumSet;

/* loaded from: classes.dex */
public abstract class CommonUsbSerialPort implements UsbSerialPort {
    private static final int DEFAULT_WRITE_BUFFER_SIZE = 16384;
    private static final int MAX_READ_SIZE = 16384;
    private static final String TAG = CommonUsbSerialPort.class.getSimpleName();
    protected final UsbDevice mDevice;
    protected final int mPortNumber;
    protected UsbEndpoint mReadEndpoint;
    protected UsbRequest mUsbRequest;
    protected UsbEndpoint mWriteEndpoint;
    protected UsbDeviceConnection mConnection = null;
    protected final Object mWriteBufferLock = new Object();
    protected byte[] mWriteBuffer = new byte[16384];

    protected abstract void closeInt();

    @Override // com.android.usbserial.driver.UsbSerialPort
    public abstract EnumSet<UsbSerialPort.ControlLine> getControlLines() throws IOException;

    @Override // com.android.usbserial.driver.UsbSerialPort
    public abstract EnumSet<UsbSerialPort.ControlLine> getSupportedControlLines() throws IOException;

    protected abstract void openInt(UsbDeviceConnection usbDeviceConnection) throws IOException;

    @Override // com.android.usbserial.driver.UsbSerialPort
    public abstract void setParameters(int i, int i2, int i3, int i4) throws IOException;

    public CommonUsbSerialPort(UsbDevice device, int portNumber) {
        this.mDevice = device;
        this.mPortNumber = portNumber;
    }

    public String toString() {
        return String.format("<%s device_name=%s device_id=%s port_number=%s>", getClass().getSimpleName(), this.mDevice.getDeviceName(), Integer.valueOf(this.mDevice.getDeviceId()), Integer.valueOf(this.mPortNumber));
    }

    @Override // com.android.usbserial.driver.UsbSerialPort
    public UsbDevice getDevice() {
        return this.mDevice;
    }

    @Override // com.android.usbserial.driver.UsbSerialPort
    public int getPortNumber() {
        return this.mPortNumber;
    }

    @Override // com.android.usbserial.driver.UsbSerialPort
    public UsbEndpoint getWriteEndpoint() {
        return this.mWriteEndpoint;
    }

    @Override // com.android.usbserial.driver.UsbSerialPort
    public UsbEndpoint getReadEndpoint() {
        return this.mReadEndpoint;
    }

    @Override // com.android.usbserial.driver.UsbSerialPort
    public String getSerial() {
        return this.mConnection.getSerial();
    }

    public final void setWriteBufferSize(int bufferSize) {
        synchronized (this.mWriteBufferLock) {
            if (bufferSize == this.mWriteBuffer.length) {
                return;
            }
            this.mWriteBuffer = new byte[bufferSize];
        }
    }

    @Override // com.android.usbserial.driver.UsbSerialPort
    public void open(UsbDeviceConnection connection) throws IOException {
        if (this.mConnection != null) {
            throw new IOException("Already open");
        }
        if (connection == null) {
            throw new IllegalArgumentException("Connection is null");
        }
        this.mConnection = connection;
        try {
            openInt(connection);
            if (this.mReadEndpoint == null || this.mWriteEndpoint == null) {
                throw new IOException("Could not get read & write endpoints");
            }
            UsbRequest usbRequest = new UsbRequest();
            this.mUsbRequest = usbRequest;
            usbRequest.initialize(this.mConnection, this.mReadEndpoint);
        } catch (Exception e) {
            try {
                close();
            } catch (Exception e2) {
            }
            throw e;
        }
    }

    @Override // com.android.usbserial.driver.UsbSerialPort, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.mConnection == null) {
            throw new IOException("Already closed");
        }
        try {
            this.mUsbRequest.cancel();
        } catch (Exception e) {
        }
        this.mUsbRequest = null;
        try {
            closeInt();
        } catch (Exception e2) {
        }
        try {
            this.mConnection.close();
        } catch (Exception e3) {
        }
        this.mConnection = null;
    }

    protected void testConnection() throws IOException {
        byte[] buf = new byte[2];
        int len = this.mConnection.controlTransfer(128, 0, 0, 0, buf, buf.length, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        if (len < 0) {
            throw new IOException("USB get_status request failed");
        }
    }

    @Override // com.android.usbserial.driver.UsbSerialPort
    public int read(byte[] dest, int timeout) throws IOException {
        return read(dest, timeout, true);
    }

    protected int read(byte[] dest, int timeout, boolean testConnection) throws IOException {
        int nread;
        if (this.mConnection == null) {
            throw new IOException("Connection closed");
        }
        if (dest.length <= 0) {
            throw new IllegalArgumentException("Read buffer to small");
        }
        if (timeout != 0) {
            long endTime = testConnection ? MonotonicClock.millis() + timeout : 0L;
            int readMax = Math.min(dest.length, 16384);
            nread = this.mConnection.bulkTransfer(this.mReadEndpoint, dest, readMax, timeout);
            if (nread == -1 && testConnection && MonotonicClock.millis() < endTime) {
                testConnection();
            }
        } else {
            ByteBuffer buf = ByteBuffer.wrap(dest);
            if (!this.mUsbRequest.queue(buf, dest.length)) {
                throw new IOException("Queueing USB request failed");
            }
            UsbRequest response = this.mConnection.requestWait();
            if (response == null) {
                throw new IOException("Waiting for USB request failed");
            }
            nread = buf.position();
            if (nread == 0) {
                testConnection();
            }
        }
        return Math.max(nread, 0);
    }

    /* JADX WARN: Removed duplicated region for block: B:21:0x003e  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x0040 A[Catch: all -> 0x00e9, TryCatch #0 {, blocks: (B:11:0x0016, B:17:0x0030, B:22:0x0048, B:35:0x0040, B:37:0x0023), top: B:10:0x0016 }] */
    @Override // com.android.usbserial.driver.UsbSerialPort
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void write(byte[] r11, int r12) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 247
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.usbserial.driver.CommonUsbSerialPort.write(byte[], int):void");
    }

    @Override // com.android.usbserial.driver.UsbSerialPort
    public boolean isOpen() {
        return this.mConnection != null;
    }

    @Override // com.android.usbserial.driver.UsbSerialPort
    public boolean getCD() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.usbserial.driver.UsbSerialPort
    public boolean getCTS() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.usbserial.driver.UsbSerialPort
    public boolean getDSR() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.usbserial.driver.UsbSerialPort
    public boolean getDTR() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.usbserial.driver.UsbSerialPort
    public void setDTR(boolean value) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.usbserial.driver.UsbSerialPort
    public boolean getRI() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.usbserial.driver.UsbSerialPort
    public boolean getRTS() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.usbserial.driver.UsbSerialPort
    public void setRTS(boolean value) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.usbserial.driver.UsbSerialPort
    public void purgeHwBuffers(boolean purgeWriteBuffers, boolean purgeReadBuffers) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.usbserial.driver.UsbSerialPort
    public void setBreak(boolean value) throws IOException {
        throw new UnsupportedOperationException();
    }
}
