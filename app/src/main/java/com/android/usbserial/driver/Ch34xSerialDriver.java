package com.android.usbserial.driver;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import com.android.usbserial.driver.UsbSerialPort;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class Ch34xSerialDriver implements UsbSerialDriver {
    private static final int GCL_CD = 8;
    private static final int GCL_CTS = 1;
    private static final int GCL_DSR = 2;
    private static final int GCL_RI = 4;
    private static final int LCR_CS5 = 0;
    private static final int LCR_CS6 = 1;
    private static final int LCR_CS7 = 2;
    private static final int LCR_CS8 = 3;
    private static final int LCR_ENABLE_PAR = 8;
    private static final int LCR_ENABLE_RX = 128;
    private static final int LCR_ENABLE_TX = 64;
    private static final int LCR_MARK_SPACE = 32;
    private static final int LCR_PAR_EVEN = 16;
    private static final int LCR_STOP_BITS_2 = 4;
    private static final int SCL_DTR = 32;
    private static final int SCL_RTS = 64;
    private static final String TAG = Ch34xSerialDriver.class.getSimpleName();
    private final UsbDevice mDevice;
    private final UsbSerialPort mPort;

    public Ch34xSerialDriver(UsbDevice device) {
        this.mDevice = device;
        this.mPort = new Ch340SerialPort(device, 0);
    }

    @Override // com.android.usbserial.driver.UsbSerialDriver
    public UsbDevice getDevice() {
        return this.mDevice;
    }

    @Override // com.android.usbserial.driver.UsbSerialDriver
    public List<UsbSerialPort> getPorts() {
        return Collections.singletonList(this.mPort);
    }

    @Override // com.android.usbserial.driver.UsbSerialDriver
    public String getDriverName() {
        return TAG;
    }

    public class Ch340SerialPort extends CommonUsbSerialPort {
        private static final int USB_TIMEOUT_MILLIS = 5000;
        private final int DEFAULT_BAUD_RATE;
        private boolean dtr;
        private boolean rts;

        public Ch340SerialPort(UsbDevice device, int portNumber) {
            super(device, portNumber);
            this.DEFAULT_BAUD_RATE = 9600;
            this.dtr = false;
            this.rts = false;
        }

        @Override // com.android.usbserial.driver.UsbSerialPort
        public UsbSerialDriver getDriver() {
            return Ch34xSerialDriver.this;
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort
        protected void openInt(UsbDeviceConnection connection) throws IOException {
            for (int i = 0; i < this.mDevice.getInterfaceCount(); i++) {
                UsbInterface usbIface = this.mDevice.getInterface(i);
                if (!this.mConnection.claimInterface(usbIface, true)) {
                    throw new IOException("Could not claim data interface");
                }
            }
            UsbInterface dataIface = this.mDevice.getInterface(this.mDevice.getInterfaceCount() - 1);
            for (int i2 = 0; i2 < dataIface.getEndpointCount(); i2++) {
                UsbEndpoint ep = dataIface.getEndpoint(i2);
                if (ep.getType() == 2) {
                    if (ep.getDirection() == 128) {
                        this.mReadEndpoint = ep;
                    } else {
                        this.mWriteEndpoint = ep;
                    }
                }
            }
            initialize();
            setBaudRate(9600);
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort
        protected void closeInt() {
            for (int i = 0; i < this.mDevice.getInterfaceCount(); i++) {
                try {
                    this.mConnection.releaseInterface(this.mDevice.getInterface(i));
                } catch (Exception e) {
                    return;
                }
            }
        }

        private int controlOut(int request, int value, int index) {
            return this.mConnection.controlTransfer(64, request, value, index, null, 0, 5000);
        }

        private int controlIn(int request, int value, int index, byte[] buffer) {
            return this.mConnection.controlTransfer(192, request, value, index, buffer, buffer.length, 5000);
        }

        private void checkState(String msg, int request, int value, int[] expected) throws IOException {
            int current;
            byte[] buffer = new byte[expected.length];
            int ret = controlIn(request, value, 0, buffer);
            if (ret < 0) {
                throw new IOException("Failed send cmd [" + msg + "]");
            }
            if (ret != expected.length) {
                throw new IOException("Expected " + expected.length + " bytes, but get " + ret + " [" + msg + "]");
            }
            for (int i = 0; i < expected.length; i++) {
                if (expected[i] != -1 && expected[i] != (current = buffer[i] & 255)) {
                    throw new IOException("Expected 0x" + Integer.toHexString(expected[i]) + " byte, but get 0x" + Integer.toHexString(current) + " [" + msg + "]");
                }
            }
        }

        private void setControlLines() throws IOException {
            if (controlOut(164, ((this.dtr ? 32 : 0) | (this.rts ? 64 : 0)) ^ (-1), 0) < 0) {
                throw new IOException("Failed to set control lines");
            }
        }

        private byte getStatus() throws IOException {
            byte[] buffer = new byte[2];
            int ret = controlIn(149, 1798, 0, buffer);
            if (ret < 0) {
                throw new IOException("Error getting control lines");
            }
            return buffer[0];
        }

        private void initialize() throws IOException {
            checkState("init #1", 95, 0, new int[]{-1, 0});
            if (controlOut(161, 0, 0) < 0) {
                throw new IOException("Init failed: #2");
            }
            setBaudRate(9600);
            checkState("init #4", 149, 9496, new int[]{-1, 0});
            if (controlOut(154, 9496, 195) < 0) {
                throw new IOException("Init failed: #5");
            }
            checkState("init #6", 149, 1798, new int[]{-1, -1});
            if (controlOut(161, 20511, 55562) < 0) {
                throw new IOException("Init failed: #7");
            }
            setBaudRate(9600);
            setControlLines();
            checkState("init #10", 149, 1798, new int[]{-1, -1});
        }

        private void setBaudRate(int baudRate) throws IOException {
            long factor = 1532620800 / baudRate;
            int divisor = 3;
            while (factor > 65520 && divisor > 0) {
                factor >>= 3;
                divisor--;
            }
            if (factor > 65520) {
                throw new UnsupportedOperationException("Unsupported baud rate: " + baudRate);
            }
            long factor2 = 65536 - factor;
            int ret = controlOut(154, 4882, (int) ((65280 & factor2) | divisor | 128));
            if (ret < 0) {
                throw new IOException("Error setting baud rate: #1)");
            }
            int ret2 = controlOut(154, 3884, (int) (255 & factor2));
            if (ret2 < 0) {
                throw new IOException("Error setting baud rate: #2");
            }
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public void setParameters(int baudRate, int dataBits, int stopBits, int parity) throws IOException {
            int lcr;
            if (baudRate <= 0) {
                throw new IllegalArgumentException("Invalid baud rate: " + baudRate);
            }
            setBaudRate(baudRate);
            if (dataBits == 5) {
                lcr = 192 | 0;
            } else if (dataBits == 6) {
                lcr = 192 | 1;
            } else if (dataBits == 7) {
                lcr = 192 | 2;
            } else if (dataBits == 8) {
                lcr = 192 | 3;
            } else {
                throw new IllegalArgumentException("Invalid data bits: " + dataBits);
            }
            if (parity != 0) {
                if (parity == 1) {
                    lcr |= 8;
                } else if (parity == 2) {
                    lcr |= 24;
                } else if (parity == 3) {
                    lcr |= 40;
                } else if (parity == 4) {
                    lcr |= 56;
                } else {
                    throw new IllegalArgumentException("Invalid parity: " + parity);
                }
            }
            if (stopBits != 1) {
                if (stopBits != 2) {
                    if (stopBits == 3) {
                        throw new UnsupportedOperationException("Unsupported stop bits: 1.5");
                    }
                    throw new IllegalArgumentException("Invalid stop bits: " + stopBits);
                }
                lcr |= 4;
            }
            int ret = controlOut(154, 9496, lcr);
            if (ret < 0) {
                throw new IOException("Error setting control byte");
            }
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public boolean getCD() throws IOException {
            return (getStatus() & 8) == 0;
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public boolean getCTS() throws IOException {
            return (getStatus() & 1) == 0;
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public boolean getDSR() throws IOException {
            return (getStatus() & 2) == 0;
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public boolean getDTR() throws IOException {
            return this.dtr;
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public void setDTR(boolean value) throws IOException {
            this.dtr = value;
            setControlLines();
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public boolean getRI() throws IOException {
            return (getStatus() & 4) == 0;
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public boolean getRTS() throws IOException {
            return this.rts;
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public void setRTS(boolean value) throws IOException {
            this.rts = value;
            setControlLines();
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public EnumSet<UsbSerialPort.ControlLine> getControlLines() throws IOException {
            int status = getStatus();
            EnumSet<UsbSerialPort.ControlLine> set = EnumSet.noneOf(UsbSerialPort.ControlLine.class);
            if (this.rts) {
                set.add(UsbSerialPort.ControlLine.RTS);
            }
            if ((status & 1) == 0) {
                set.add(UsbSerialPort.ControlLine.CTS);
            }
            if (this.dtr) {
                set.add(UsbSerialPort.ControlLine.DTR);
            }
            if ((status & 2) == 0) {
                set.add(UsbSerialPort.ControlLine.DSR);
            }
            if ((status & 8) == 0) {
                set.add(UsbSerialPort.ControlLine.CD);
            }
            if ((status & 4) == 0) {
                set.add(UsbSerialPort.ControlLine.RI);
            }
            return set;
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public EnumSet<UsbSerialPort.ControlLine> getSupportedControlLines() throws IOException {
            return EnumSet.allOf(UsbSerialPort.ControlLine.class);
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public void setBreak(boolean value) throws IOException {
            byte[] req = new byte[2];
            if (controlIn(149, 6149, 0, req) < 0) {
                throw new IOException("Error getting BREAK condition");
            }
            if (!value) {
                req[0] = (byte) (req[0] | 1);
                req[1] = (byte) (req[1] | 64);
            } else {
                req[0] = (byte) (req[0] & (-2));
                req[1] = (byte) (req[1] & (-65));
            }
            int val = ((req[1] & 255) << 8) | (req[0] & 255);
            if (controlOut(154, 6149, val) < 0) {
                throw new IOException("Error setting BREAK condition");
            }
        }
    }

    public static Map<Integer, int[]> getSupportedDevices() {
        Map<Integer, int[]> supportedDevices = new LinkedHashMap<>();
        supportedDevices.put(Integer.valueOf(UsbId.VENDOR_QINHENG), new int[]{UsbId.QINHENG_CH340, UsbId.QINHENG_CH341A});
        return supportedDevices;
    }
}
