package com.android.usbserial.driver;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import com.android.usbserial.driver.UsbSerialPort;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.jvm.internal.ByteCompanionObject;

/* loaded from: classes.dex */
public class Cp21xxSerialDriver implements UsbSerialDriver {
    private static final String TAG = Cp21xxSerialDriver.class.getSimpleName();
    private final UsbDevice mDevice;
    private final List<UsbSerialPort> mPorts = new ArrayList();

    public Cp21xxSerialDriver(UsbDevice device) {
        this.mDevice = device;
        for (int port = 0; port < device.getInterfaceCount(); port++) {
            this.mPorts.add(new Cp21xxSerialPort(this.mDevice, port));
        }
    }

    @Override // com.android.usbserial.driver.UsbSerialDriver
    public UsbDevice getDevice() {
        return this.mDevice;
    }

    @Override // com.android.usbserial.driver.UsbSerialDriver
    public List<UsbSerialPort> getPorts() {
        return this.mPorts;
    }

    @Override // com.android.usbserial.driver.UsbSerialDriver
    public String getDriverName() {
        return TAG;
    }

    public class Cp21xxSerialPort extends CommonUsbSerialPort {
        private static final int DTR_DISABLE = 256;
        private static final int DTR_ENABLE = 257;
        private static final int FLUSH_READ_CODE = 10;
        private static final int FLUSH_WRITE_CODE = 5;
        private static final int REQTYPE_DEVICE_TO_HOST = 193;
        private static final int REQTYPE_HOST_TO_DEVICE = 65;
        private static final int RTS_DISABLE = 512;
        private static final int RTS_ENABLE = 514;
        private static final int SILABSER_FLUSH_REQUEST_CODE = 18;
        private static final int SILABSER_GET_MDMSTS_REQUEST_CODE = 8;
        private static final int SILABSER_IFC_ENABLE_REQUEST_CODE = 0;
        private static final int SILABSER_SET_BAUDRATE = 30;
        private static final int SILABSER_SET_BREAK_REQUEST_CODE = 5;
        private static final int SILABSER_SET_LINE_CTL_REQUEST_CODE = 3;
        private static final int SILABSER_SET_MHS_REQUEST_CODE = 7;
        private static final int STATUS_CD = 128;
        private static final int STATUS_CTS = 16;
        private static final int STATUS_DSR = 32;
        private static final int STATUS_RI = 64;
        private static final int UART_DISABLE = 0;
        private static final int UART_ENABLE = 1;
        private static final int USB_WRITE_TIMEOUT_MILLIS = 5000;
        private boolean dtr;
        private boolean mIsRestrictedPort;
        private boolean rts;

        public Cp21xxSerialPort(UsbDevice device, int portNumber) {
            super(device, portNumber);
            this.dtr = false;
            this.rts = false;
        }

        @Override // com.android.usbserial.driver.UsbSerialPort
        public UsbSerialDriver getDriver() {
            return Cp21xxSerialDriver.this;
        }

        private void setConfigSingle(int request, int value) throws IOException {
            int result = this.mConnection.controlTransfer(65, request, value, this.mPortNumber, null, 0, 5000);
            if (result != 0) {
                throw new IOException("Control transfer failed: " + request + " / " + value + " -> " + result);
            }
        }

        private byte getStatus() throws IOException {
            byte[] buffer = new byte[1];
            int result = this.mConnection.controlTransfer(REQTYPE_DEVICE_TO_HOST, 8, 0, this.mPortNumber, buffer, buffer.length, 5000);
            if (result != 1) {
                throw new IOException("Control transfer failed: 8 / 0 -> " + result);
            }
            return buffer[0];
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort
        protected void openInt(UsbDeviceConnection connection) throws IOException {
            this.mIsRestrictedPort = this.mDevice.getInterfaceCount() == 2 && this.mPortNumber == 1;
            if (this.mPortNumber >= this.mDevice.getInterfaceCount()) {
                throw new IOException("Unknown port number");
            }
            UsbInterface dataIface = this.mDevice.getInterface(this.mPortNumber);
            if (!this.mConnection.claimInterface(dataIface, true)) {
                throw new IOException("Could not claim interface " + this.mPortNumber);
            }
            for (int i = 0; i < dataIface.getEndpointCount(); i++) {
                UsbEndpoint ep = dataIface.getEndpoint(i);
                if (ep.getType() == 2) {
                    if (ep.getDirection() == 128) {
                        this.mReadEndpoint = ep;
                    } else {
                        this.mWriteEndpoint = ep;
                    }
                }
            }
            setConfigSingle(0, 1);
            setConfigSingle(7, (this.dtr ? 257 : 256) | (this.rts ? RTS_ENABLE : 512));
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort
        protected void closeInt() {
            try {
                setConfigSingle(0, 0);
            } catch (Exception e) {
            }
            try {
                this.mConnection.releaseInterface(this.mDevice.getInterface(this.mPortNumber));
            } catch (Exception e2) {
            }
        }

        private void setBaudRate(int baudRate) throws IOException {
            byte[] data = {(byte) (baudRate & 255), (byte) ((baudRate >> 8) & 255), (byte) ((baudRate >> 16) & 255), (byte) ((baudRate >> 24) & 255)};
            int ret = this.mConnection.controlTransfer(65, 30, 0, this.mPortNumber, data, 4, 5000);
            if (ret < 0) {
                throw new IOException("Error setting baud rate");
            }
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public void setParameters(int baudRate, int dataBits, int stopBits, int parity) throws IOException {
            int configDataBits;
            if (baudRate <= 0) {
                throw new IllegalArgumentException("Invalid baud rate: " + baudRate);
            }
            setBaudRate(baudRate);
            if (dataBits != 5) {
                if (dataBits != 6) {
                    if (dataBits != 7) {
                        if (dataBits == 8) {
                            configDataBits = 0 | 2048;
                        } else {
                            throw new IllegalArgumentException("Invalid data bits: " + dataBits);
                        }
                    } else {
                        if (this.mIsRestrictedPort) {
                            throw new UnsupportedOperationException("Unsupported data bits: " + dataBits);
                        }
                        configDataBits = 0 | 1792;
                    }
                } else {
                    if (this.mIsRestrictedPort) {
                        throw new UnsupportedOperationException("Unsupported data bits: " + dataBits);
                    }
                    configDataBits = 0 | 1536;
                }
            } else {
                if (this.mIsRestrictedPort) {
                    throw new UnsupportedOperationException("Unsupported data bits: " + dataBits);
                }
                configDataBits = 0 | 1280;
            }
            if (parity != 0) {
                if (parity == 1) {
                    configDataBits |= 16;
                } else if (parity == 2) {
                    configDataBits |= 32;
                } else if (parity != 3) {
                    if (parity == 4) {
                        if (this.mIsRestrictedPort) {
                            throw new UnsupportedOperationException("Unsupported parity: space");
                        }
                        configDataBits |= 64;
                    } else {
                        throw new IllegalArgumentException("Invalid parity: " + parity);
                    }
                } else {
                    if (this.mIsRestrictedPort) {
                        throw new UnsupportedOperationException("Unsupported parity: mark");
                    }
                    configDataBits |= 48;
                }
            }
            if (stopBits != 1) {
                if (stopBits != 2) {
                    if (stopBits == 3) {
                        throw new UnsupportedOperationException("Unsupported stop bits: 1.5");
                    }
                    throw new IllegalArgumentException("Invalid stop bits: " + stopBits);
                }
                if (this.mIsRestrictedPort) {
                    throw new UnsupportedOperationException("Unsupported stop bits: 2");
                }
                configDataBits |= 2;
            }
            setConfigSingle(3, configDataBits);
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public boolean getCD() throws IOException {
            return (getStatus() & ByteCompanionObject.MIN_VALUE) != 0;
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public boolean getCTS() throws IOException {
            return (getStatus() & 16) != 0;
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public boolean getDSR() throws IOException {
            return (getStatus() & 32) != 0;
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public boolean getDTR() throws IOException {
            return this.dtr;
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public void setDTR(boolean value) throws IOException {
            this.dtr = value;
            setConfigSingle(7, value ? 257 : 256);
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public boolean getRI() throws IOException {
            return (getStatus() & 64) != 0;
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public boolean getRTS() throws IOException {
            return this.rts;
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public void setRTS(boolean value) throws IOException {
            this.rts = value;
            setConfigSingle(7, value ? RTS_ENABLE : 512);
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public EnumSet<UsbSerialPort.ControlLine> getControlLines() throws IOException {
            byte status = getStatus();
            EnumSet<UsbSerialPort.ControlLine> set = EnumSet.noneOf(UsbSerialPort.ControlLine.class);
            if (this.rts) {
                set.add(UsbSerialPort.ControlLine.RTS);
            }
            if ((status & 16) != 0) {
                set.add(UsbSerialPort.ControlLine.CTS);
            }
            if (this.dtr) {
                set.add(UsbSerialPort.ControlLine.DTR);
            }
            if ((status & 32) != 0) {
                set.add(UsbSerialPort.ControlLine.DSR);
            }
            if ((status & ByteCompanionObject.MIN_VALUE) != 0) {
                set.add(UsbSerialPort.ControlLine.CD);
            }
            if ((status & 64) != 0) {
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
            int value = (purgeWriteBuffers ? 5 : 0) | (purgeReadBuffers ? 10 : 0);
            if (value != 0) {
                setConfigSingle(18, value);
            }
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public void setBreak(boolean z) throws IOException {
            setConfigSingle(5, z ? 1 : 0);
        }
    }

    public static Map<Integer, int[]> getSupportedDevices() {
        Map<Integer, int[]> supportedDevices = new LinkedHashMap<>();
        supportedDevices.put(Integer.valueOf(UsbId.VENDOR_SILABS), new int[]{UsbId.SILABS_CP2102, UsbId.SILABS_CP2105, UsbId.SILABS_CP2108});
        return supportedDevices;
    }
}
