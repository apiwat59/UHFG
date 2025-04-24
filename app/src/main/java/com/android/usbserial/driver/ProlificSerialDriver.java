package com.android.usbserial.driver;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.util.Log;
import androidx.core.internal.view.SupportMenu;
import com.android.usbserial.driver.ProlificSerialDriver;
import com.android.usbserial.driver.UsbSerialPort;
import com.android.usbserial.util.MonotonicClock;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.jvm.internal.ByteCompanionObject;

/* loaded from: classes.dex */
public class ProlificSerialDriver implements UsbSerialDriver {
    private static final int[] standardBaudRates = {75, 150, 300, 600, 1200, 1800, 2400, 3600, 4800, 7200, 9600, 14400, 19200, 28800, 38400, 57600, 115200, 128000, 134400, 161280, 201600, 230400, 268800, 403200, 460800, 614400, 806400, 921600, 1228800, 2457600, 3000000, 6000000};
    private final String TAG = ProlificSerialDriver.class.getSimpleName();
    private final UsbDevice mDevice;
    private final UsbSerialPort mPort;

    protected enum DeviceType {
        DEVICE_TYPE_01,
        DEVICE_TYPE_T,
        DEVICE_TYPE_HX,
        DEVICE_TYPE_HXN
    }

    public ProlificSerialDriver(UsbDevice device) {
        this.mDevice = device;
        this.mPort = new ProlificSerialPort(device, 0);
    }

    @Override // com.android.usbserial.driver.UsbSerialDriver
    public List<UsbSerialPort> getPorts() {
        return Collections.singletonList(this.mPort);
    }

    @Override // com.android.usbserial.driver.UsbSerialDriver
    public String getDriverName() {
        return this.TAG;
    }

    @Override // com.android.usbserial.driver.UsbSerialDriver
    public UsbDevice getDevice() {
        return this.mDevice;
    }

    class ProlificSerialPort extends CommonUsbSerialPort {
        private static final int CONTROL_DTR = 1;
        private static final int CONTROL_RTS = 2;
        private static final int CTRL_OUT_REQTYPE = 33;
        private static final int FLUSH_RX_REQUEST = 8;
        private static final int FLUSH_TX_REQUEST = 9;
        private static final int GET_CONTROL_FLAG_CD = 2;
        private static final int GET_CONTROL_FLAG_CTS = 8;
        private static final int GET_CONTROL_FLAG_DSR = 4;
        private static final int GET_CONTROL_FLAG_RI = 1;
        private static final int GET_CONTROL_HXN_FLAG_CD = 64;
        private static final int GET_CONTROL_HXN_FLAG_CTS = 8;
        private static final int GET_CONTROL_HXN_FLAG_DSR = 32;
        private static final int GET_CONTROL_HXN_FLAG_RI = 128;
        private static final int GET_CONTROL_HXN_REQUEST = 128;
        private static final int GET_CONTROL_REQUEST = 135;
        private static final int INTERRUPT_ENDPOINT = 129;
        private static final int READ_ENDPOINT = 131;
        private static final int RESET_HXN_REQUEST = 7;
        private static final int RESET_HXN_RX_PIPE = 1;
        private static final int RESET_HXN_TX_PIPE = 2;
        private static final int SEND_BREAK_REQUEST = 35;
        private static final int SET_CONTROL_REQUEST = 34;
        private static final int SET_LINE_REQUEST = 32;
        private static final int STATUS_BUFFER_SIZE = 10;
        private static final int STATUS_BYTE_IDX = 8;
        private static final int STATUS_FLAG_CD = 1;
        private static final int STATUS_FLAG_CTS = 128;
        private static final int STATUS_FLAG_DSR = 2;
        private static final int STATUS_FLAG_RI = 8;
        private static final int STATUS_NOTIFICATION = 161;
        private static final int USB_READ_TIMEOUT_MILLIS = 1000;
        private static final int USB_RECIP_INTERFACE = 1;
        private static final int USB_WRITE_TIMEOUT_MILLIS = 5000;
        private static final int VENDOR_IN_REQTYPE = 192;
        private static final int VENDOR_OUT_REQTYPE = 64;
        private static final int VENDOR_READ_HXN_REQUEST = 129;
        private static final int VENDOR_READ_REQUEST = 1;
        private static final int VENDOR_WRITE_HXN_REQUEST = 128;
        private static final int VENDOR_WRITE_REQUEST = 1;
        private static final int WRITE_ENDPOINT = 2;
        private int mBaudRate;
        private int mControlLinesValue;
        private int mDataBits;
        protected DeviceType mDeviceType;
        private UsbEndpoint mInterruptEndpoint;
        private int mParity;
        private IOException mReadStatusException;
        private volatile Thread mReadStatusThread;
        private final Object mReadStatusThreadLock;
        private int mStatus;
        private int mStopBits;
        private boolean mStopReadStatusThread;

        public ProlificSerialPort(UsbDevice device, int portNumber) {
            super(device, portNumber);
            this.mDeviceType = DeviceType.DEVICE_TYPE_HX;
            this.mControlLinesValue = 0;
            this.mBaudRate = -1;
            this.mDataBits = -1;
            this.mStopBits = -1;
            this.mParity = -1;
            this.mStatus = 0;
            this.mReadStatusThread = null;
            this.mReadStatusThreadLock = new Object();
            this.mStopReadStatusThread = false;
            this.mReadStatusException = null;
        }

        @Override // com.android.usbserial.driver.UsbSerialPort
        public UsbSerialDriver getDriver() {
            return ProlificSerialDriver.this;
        }

        private byte[] inControlTransfer(int requestType, int request, int value, int index, int length) throws IOException {
            byte[] buffer = new byte[length];
            int result = this.mConnection.controlTransfer(requestType, request, value, index, buffer, length, 1000);
            if (result != length) {
                throw new IOException(String.format("ControlTransfer 0x%x failed: %d", Integer.valueOf(value), Integer.valueOf(result)));
            }
            return buffer;
        }

        private void outControlTransfer(int requestType, int request, int value, int index, byte[] data) throws IOException {
            int length = data == null ? 0 : data.length;
            int result = this.mConnection.controlTransfer(requestType, request, value, index, data, length, 5000);
            if (result != length) {
                throw new IOException(String.format("ControlTransfer 0x%x failed: %d", Integer.valueOf(value), Integer.valueOf(result)));
            }
        }

        private byte[] vendorIn(int value, int index, int length) throws IOException {
            int request = this.mDeviceType == DeviceType.DEVICE_TYPE_HXN ? 129 : 1;
            return inControlTransfer(VENDOR_IN_REQTYPE, request, value, index, length);
        }

        private void vendorOut(int value, int index, byte[] data) throws IOException {
            int request = this.mDeviceType == DeviceType.DEVICE_TYPE_HXN ? 128 : 1;
            outControlTransfer(64, request, value, index, data);
        }

        private void resetDevice() throws IOException {
            purgeHwBuffers(true, true);
        }

        private void ctrlOut(int request, int value, int index, byte[] data) throws IOException {
            outControlTransfer(33, request, value, index, data);
        }

        private boolean testHxStatus() {
            try {
                inControlTransfer(VENDOR_IN_REQTYPE, 1, 32896, 0, 1);
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        private void doBlackMagic() throws IOException {
            if (this.mDeviceType == DeviceType.DEVICE_TYPE_HXN) {
                return;
            }
            vendorIn(33924, 0, 1);
            vendorOut(1028, 0, null);
            vendorIn(33924, 0, 1);
            vendorIn(33667, 0, 1);
            vendorIn(33924, 0, 1);
            vendorOut(1028, 1, null);
            vendorIn(33924, 0, 1);
            vendorIn(33667, 0, 1);
            vendorOut(0, 1, null);
            vendorOut(1, 0, null);
            vendorOut(2, this.mDeviceType == DeviceType.DEVICE_TYPE_01 ? 36 : 68, null);
        }

        private void setControlLines(int newControlLinesValue) throws IOException {
            ctrlOut(34, newControlLinesValue, 0, null);
            this.mControlLinesValue = newControlLinesValue;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void readStatusThreadFunction() {
            while (!this.mStopReadStatusThread) {
                try {
                    byte[] buffer = new byte[10];
                    long endTime = MonotonicClock.millis() + 500;
                    int readBytesCount = this.mConnection.bulkTransfer(this.mInterruptEndpoint, buffer, 10, 500);
                    if (readBytesCount == -1 && MonotonicClock.millis() < endTime) {
                        testConnection();
                    }
                    if (readBytesCount > 0) {
                        if (readBytesCount != 10) {
                            throw new IOException("Invalid status notification, expected 10 bytes, got " + readBytesCount);
                        }
                        if (buffer[0] != -95) {
                            throw new IOException("Invalid status notification, expected 161 request, got " + ((int) buffer[0]));
                        }
                        this.mStatus = buffer[8] & 255;
                    }
                } catch (IOException e) {
                    this.mReadStatusException = e;
                    return;
                }
            }
        }

        private int getStatus() throws IOException {
            if (this.mReadStatusThread == null && this.mReadStatusException == null) {
                synchronized (this.mReadStatusThreadLock) {
                    if (this.mReadStatusThread == null) {
                        this.mStatus = 0;
                        if (this.mDeviceType != DeviceType.DEVICE_TYPE_HXN) {
                            byte[] data = vendorIn(GET_CONTROL_REQUEST, 0, 1);
                            if ((data[0] & 8) == 0) {
                                this.mStatus |= 128;
                            }
                            if ((data[0] & 4) == 0) {
                                this.mStatus |= 2;
                            }
                            if ((data[0] & 2) == 0) {
                                this.mStatus |= 1;
                            }
                            if ((data[0] & 1) == 0) {
                                this.mStatus |= 8;
                            }
                        } else {
                            byte[] data2 = vendorIn(128, 0, 1);
                            if ((data2[0] & 8) == 0) {
                                this.mStatus |= 128;
                            }
                            if ((data2[0] & 32) == 0) {
                                this.mStatus |= 2;
                            }
                            if ((data2[0] & 64) == 0) {
                                this.mStatus |= 1;
                            }
                            if ((data2[0] & ByteCompanionObject.MIN_VALUE) == 0) {
                                this.mStatus |= 8;
                            }
                        }
                        this.mReadStatusThread = new Thread(new Runnable() { // from class: com.android.usbserial.driver.-$$Lambda$ProlificSerialDriver$ProlificSerialPort$yoscb6Ecn3SZM-iDmz-mg6uoGgM
                            @Override // java.lang.Runnable
                            public final void run() {
                                ProlificSerialDriver.ProlificSerialPort.this.readStatusThreadFunction();
                            }
                        });
                        this.mReadStatusThread.setDaemon(true);
                        this.mReadStatusThread.start();
                    }
                }
            }
            IOException readStatusException = this.mReadStatusException;
            if (this.mReadStatusException != null) {
                this.mReadStatusException = null;
                throw new IOException(readStatusException);
            }
            return this.mStatus;
        }

        private boolean testStatusFlag(int flag) throws IOException {
            return (getStatus() & flag) == flag;
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort
        public void openInt(UsbDeviceConnection connection) throws IOException {
            UsbInterface usbInterface = this.mDevice.getInterface(0);
            if (!connection.claimInterface(usbInterface, true)) {
                throw new IOException("Error claiming Prolific interface 0");
            }
            for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
                UsbEndpoint currentEndpoint = usbInterface.getEndpoint(i);
                int address = currentEndpoint.getAddress();
                if (address == 2) {
                    this.mWriteEndpoint = currentEndpoint;
                } else if (address == 129) {
                    this.mInterruptEndpoint = currentEndpoint;
                } else if (address == READ_ENDPOINT) {
                    this.mReadEndpoint = currentEndpoint;
                }
            }
            byte[] rawDescriptors = connection.getRawDescriptors();
            if (rawDescriptors == null || rawDescriptors.length < 14) {
                throw new IOException("Could not get device descriptors");
            }
            int usbVersion = (rawDescriptors[3] << 8) + rawDescriptors[2];
            int deviceVersion = (rawDescriptors[13] << 8) + rawDescriptors[12];
            byte maxPacketSize0 = rawDescriptors[7];
            if (this.mDevice.getDeviceClass() == 2 || maxPacketSize0 != 64) {
                this.mDeviceType = DeviceType.DEVICE_TYPE_01;
            } else if (deviceVersion == 768 && usbVersion == 512) {
                this.mDeviceType = DeviceType.DEVICE_TYPE_T;
            } else if (deviceVersion == 1280) {
                this.mDeviceType = DeviceType.DEVICE_TYPE_T;
            } else if (usbVersion == 512 && !testHxStatus()) {
                this.mDeviceType = DeviceType.DEVICE_TYPE_HXN;
            } else {
                this.mDeviceType = DeviceType.DEVICE_TYPE_HX;
            }
            resetDevice();
            doBlackMagic();
            setControlLines(this.mControlLinesValue);
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort
        public void closeInt() {
            try {
                synchronized (this.mReadStatusThreadLock) {
                    if (this.mReadStatusThread != null) {
                        try {
                            this.mStopReadStatusThread = true;
                            this.mReadStatusThread.join();
                        } catch (Exception e) {
                            Log.w(ProlificSerialDriver.this.TAG, "An error occured while waiting for status read thread", e);
                        }
                        this.mStopReadStatusThread = false;
                        this.mReadStatusThread = null;
                        this.mReadStatusException = null;
                    }
                }
                resetDevice();
            } catch (Exception e2) {
            }
            try {
                this.mConnection.releaseInterface(this.mDevice.getInterface(0));
            } catch (Exception e3) {
            }
        }

        private int filterBaudRate(int baudRate) {
            int buf;
            int effectiveBaudRate;
            if ((1610612736 & baudRate) == 536870912) {
                return (-536870913) & baudRate;
            }
            if (baudRate <= 0) {
                throw new IllegalArgumentException("Invalid baud rate: " + baudRate);
            }
            if (this.mDeviceType != DeviceType.DEVICE_TYPE_HXN) {
                for (int br : ProlificSerialDriver.standardBaudRates) {
                    if (br == baudRate) {
                        return baudRate;
                    }
                }
                int mantissa = 384000000 / baudRate;
                if (mantissa == 0) {
                    throw new UnsupportedOperationException("Baud rate to high");
                }
                int exponent = 0;
                if (this.mDeviceType == DeviceType.DEVICE_TYPE_T) {
                    while (mantissa >= 2048) {
                        if (exponent < 15) {
                            mantissa >>= 1;
                            exponent++;
                        } else {
                            throw new UnsupportedOperationException("Baud rate to low");
                        }
                    }
                    buf = ((((exponent & (-2)) << 12) + mantissa) + ((exponent & 1) << 16)) - 2147483648;
                    effectiveBaudRate = (384000000 / mantissa) >> exponent;
                } else {
                    while (mantissa >= 512) {
                        if (exponent < 7) {
                            mantissa >>= 2;
                            exponent++;
                        } else {
                            throw new UnsupportedOperationException("Baud rate to low");
                        }
                    }
                    buf = ((exponent << 9) + mantissa) - 2147483648;
                    effectiveBaudRate = (384000000 / mantissa) >> (exponent << 1);
                }
                double d = effectiveBaudRate;
                double d2 = baudRate;
                Double.isNaN(d);
                Double.isNaN(d2);
                double baudRateError = Math.abs(1.0d - (d / d2));
                if (baudRateError >= 0.031d) {
                    throw new UnsupportedOperationException(String.format("Baud rate deviation %.1f%% is higher than allowed 3%%", Double.valueOf(100.0d * baudRateError)));
                }
                Log.d(ProlificSerialDriver.this.TAG, String.format("baud rate=%d, effective=%d, error=%.1f%%, value=0x%08x, mantissa=%d, exponent=%d", Integer.valueOf(baudRate), Integer.valueOf(effectiveBaudRate), Double.valueOf(100.0d * baudRateError), Integer.valueOf(buf), Integer.valueOf(mantissa), Integer.valueOf(exponent)));
                return buf;
            }
            return baudRate;
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public void setParameters(int baudRate, int dataBits, int stopBits, int parity) throws IOException {
            int baudRate2 = filterBaudRate(baudRate);
            if (this.mBaudRate == baudRate2 && this.mDataBits == dataBits && this.mStopBits == stopBits && this.mParity == parity) {
                return;
            }
            byte[] lineRequestData = new byte[7];
            lineRequestData[0] = (byte) (baudRate2 & 255);
            lineRequestData[1] = (byte) ((baudRate2 >> 8) & 255);
            lineRequestData[2] = (byte) ((baudRate2 >> 16) & 255);
            lineRequestData[3] = (byte) ((baudRate2 >> 24) & 255);
            if (stopBits == 1) {
                lineRequestData[4] = 0;
            } else if (stopBits == 2) {
                lineRequestData[4] = 2;
            } else if (stopBits == 3) {
                lineRequestData[4] = 1;
            } else {
                throw new IllegalArgumentException("Invalid stop bits: " + stopBits);
            }
            if (parity == 0) {
                lineRequestData[5] = 0;
            } else if (parity == 1) {
                lineRequestData[5] = 1;
            } else if (parity == 2) {
                lineRequestData[5] = 2;
            } else if (parity != 3) {
                if (parity == 4) {
                    lineRequestData[5] = 4;
                } else {
                    throw new IllegalArgumentException("Invalid parity: " + parity);
                }
            } else {
                lineRequestData[5] = 3;
            }
            if (dataBits < 5 || dataBits > 8) {
                throw new IllegalArgumentException("Invalid data bits: " + dataBits);
            }
            lineRequestData[6] = (byte) dataBits;
            ctrlOut(32, 0, 0, lineRequestData);
            resetDevice();
            this.mBaudRate = baudRate2;
            this.mDataBits = dataBits;
            this.mStopBits = stopBits;
            this.mParity = parity;
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public boolean getCD() throws IOException {
            return testStatusFlag(1);
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public boolean getCTS() throws IOException {
            return testStatusFlag(128);
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public boolean getDSR() throws IOException {
            return testStatusFlag(2);
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public boolean getDTR() throws IOException {
            return (this.mControlLinesValue & 1) != 0;
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public void setDTR(boolean value) throws IOException {
            int newControlLinesValue;
            if (value) {
                newControlLinesValue = this.mControlLinesValue | 1;
            } else {
                int newControlLinesValue2 = this.mControlLinesValue;
                newControlLinesValue = newControlLinesValue2 & (-2);
            }
            setControlLines(newControlLinesValue);
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public boolean getRI() throws IOException {
            return testStatusFlag(8);
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public boolean getRTS() throws IOException {
            return (this.mControlLinesValue & 2) != 0;
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public void setRTS(boolean value) throws IOException {
            int newControlLinesValue;
            if (value) {
                newControlLinesValue = this.mControlLinesValue | 2;
            } else {
                int newControlLinesValue2 = this.mControlLinesValue;
                newControlLinesValue = newControlLinesValue2 & (-3);
            }
            setControlLines(newControlLinesValue);
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public EnumSet<UsbSerialPort.ControlLine> getControlLines() throws IOException {
            int status = getStatus();
            EnumSet<UsbSerialPort.ControlLine> set = EnumSet.noneOf(UsbSerialPort.ControlLine.class);
            if ((this.mControlLinesValue & 2) != 0) {
                set.add(UsbSerialPort.ControlLine.RTS);
            }
            if ((status & 128) != 0) {
                set.add(UsbSerialPort.ControlLine.CTS);
            }
            if ((this.mControlLinesValue & 1) != 0) {
                set.add(UsbSerialPort.ControlLine.DTR);
            }
            if ((status & 2) != 0) {
                set.add(UsbSerialPort.ControlLine.DSR);
            }
            if ((status & 1) != 0) {
                set.add(UsbSerialPort.ControlLine.CD);
            }
            if ((status & 8) != 0) {
                set.add(UsbSerialPort.ControlLine.RI);
            }
            return set;
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public EnumSet<UsbSerialPort.ControlLine> getSupportedControlLines() throws IOException {
            return EnumSet.allOf(UsbSerialPort.ControlLine.class);
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public void purgeHwBuffers(boolean purgeWriteBuffers, boolean purgeReadBuffers) throws IOException {
            if (this.mDeviceType == DeviceType.DEVICE_TYPE_HXN) {
                int index = purgeWriteBuffers ? 0 | 1 : 0;
                if (purgeReadBuffers) {
                    index |= 2;
                }
                if (index != 0) {
                    vendorOut(7, index, null);
                    return;
                }
                return;
            }
            if (purgeWriteBuffers) {
                vendorOut(8, 0, null);
            }
            if (purgeReadBuffers) {
                vendorOut(9, 0, null);
            }
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public void setBreak(boolean value) throws IOException {
            ctrlOut(35, value ? SupportMenu.USER_MASK : 0, 0, null);
        }
    }

    public static Map<Integer, int[]> getSupportedDevices() {
        Map<Integer, int[]> supportedDevices = new LinkedHashMap<>();
        supportedDevices.put(Integer.valueOf(UsbId.VENDOR_PROLIFIC), new int[]{UsbId.PROLIFIC_PL2303, UsbId.PROLIFIC_PL23C3, UsbId.PROLIFIC_PL2303GC, UsbId.PROLIFIC_PL2303GB, UsbId.PROLIFIC_PL2303GT, 9187, 9187, UsbId.PROLIFIC_PL2303GS});
        return supportedDevices;
    }
}
