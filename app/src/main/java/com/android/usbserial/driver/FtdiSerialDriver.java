package com.android.usbserial.driver;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.util.Log;
import com.android.usbserial.driver.UsbSerialPort;
import com.android.usbserial.util.MonotonicClock;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class FtdiSerialDriver implements UsbSerialDriver {
    private static final String TAG = FtdiSerialPort.class.getSimpleName();
    private final UsbDevice mDevice;
    private final List<UsbSerialPort> mPorts = new ArrayList();

    public FtdiSerialDriver(UsbDevice device) {
        this.mDevice = device;
        for (int port = 0; port < device.getInterfaceCount(); port++) {
            this.mPorts.add(new FtdiSerialPort(this.mDevice, port));
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

    public class FtdiSerialPort extends CommonUsbSerialPort {
        private static final int GET_LATENCY_TIMER_REQUEST = 10;
        private static final int GET_MODEM_STATUS_REQUEST = 5;
        private static final int MODEM_CONTROL_DTR_DISABLE = 256;
        private static final int MODEM_CONTROL_DTR_ENABLE = 257;
        private static final int MODEM_CONTROL_REQUEST = 1;
        private static final int MODEM_CONTROL_RTS_DISABLE = 512;
        private static final int MODEM_CONTROL_RTS_ENABLE = 514;
        private static final int MODEM_STATUS_CD = 128;
        private static final int MODEM_STATUS_CTS = 16;
        private static final int MODEM_STATUS_DSR = 32;
        private static final int MODEM_STATUS_RI = 64;
        private static final int READ_HEADER_LENGTH = 2;
        private static final int REQTYPE_DEVICE_TO_HOST = 192;
        private static final int REQTYPE_HOST_TO_DEVICE = 64;
        private static final int RESET_ALL = 0;
        private static final int RESET_PURGE_RX = 1;
        private static final int RESET_PURGE_TX = 2;
        private static final int RESET_REQUEST = 0;
        private static final int SET_BAUD_RATE_REQUEST = 3;
        private static final int SET_DATA_REQUEST = 4;
        private static final int SET_LATENCY_TIMER_REQUEST = 9;
        private static final int USB_WRITE_TIMEOUT_MILLIS = 5000;
        private boolean baudRateWithPort;
        private int breakConfig;
        private boolean dtr;
        private boolean rts;

        public FtdiSerialPort(UsbDevice device, int portNumber) {
            super(device, portNumber);
            this.baudRateWithPort = false;
            this.dtr = false;
            this.rts = false;
            this.breakConfig = 0;
        }

        @Override // com.android.usbserial.driver.UsbSerialPort
        public UsbSerialDriver getDriver() {
            return FtdiSerialDriver.this;
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort
        protected void openInt(UsbDeviceConnection connection) throws IOException {
            boolean z = true;
            if (!connection.claimInterface(this.mDevice.getInterface(this.mPortNumber), true)) {
                throw new IOException("Could not claim interface " + this.mPortNumber);
            }
            if (this.mDevice.getInterface(this.mPortNumber).getEndpointCount() < 2) {
                throw new IOException("Not enough endpoints");
            }
            this.mReadEndpoint = this.mDevice.getInterface(this.mPortNumber).getEndpoint(0);
            this.mWriteEndpoint = this.mDevice.getInterface(this.mPortNumber).getEndpoint(1);
            int result = this.mConnection.controlTransfer(64, 0, 0, this.mPortNumber + 1, null, 0, 5000);
            if (result != 0) {
                throw new IOException("Reset failed: result=" + result);
            }
            int result2 = this.mConnection.controlTransfer(64, 1, (this.dtr ? 257 : 256) | (this.rts ? MODEM_CONTROL_RTS_ENABLE : 512), this.mPortNumber + 1, null, 0, 5000);
            if (result2 != 0) {
                throw new IOException("Init RTS,DTR failed: result=" + result2);
            }
            byte[] rawDescriptors = connection.getRawDescriptors();
            if (rawDescriptors == null || rawDescriptors.length < 14) {
                throw new IOException("Could not get device descriptors");
            }
            int deviceType = rawDescriptors[13];
            if (deviceType != 7 && deviceType != 8 && deviceType != 9 && this.mDevice.getInterfaceCount() <= 1) {
                z = false;
            }
            this.baudRateWithPort = z;
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort
        protected void closeInt() {
            try {
                this.mConnection.releaseInterface(this.mDevice.getInterface(this.mPortNumber));
            } catch (Exception e) {
            }
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public int read(byte[] dest, int timeout) throws IOException {
            int nread;
            if (dest.length <= 2) {
                throw new IllegalArgumentException("Read buffer to small");
            }
            if (timeout != 0) {
                long endTime = MonotonicClock.millis() + timeout;
                do {
                    nread = super.read(dest, Math.max(1, (int) (endTime - MonotonicClock.millis())), false);
                    if (nread != 2) {
                        break;
                    }
                } while (MonotonicClock.millis() < endTime);
                if (nread <= 0 && MonotonicClock.millis() < endTime) {
                    testConnection();
                }
            } else {
                do {
                    nread = super.read(dest, timeout, false);
                } while (nread == 2);
            }
            return readFilter(dest, nread);
        }

        protected int readFilter(byte[] buffer, int totalBytesRead) throws IOException {
            int maxPacketSize = this.mReadEndpoint.getMaxPacketSize();
            int destPos = 0;
            for (int srcPos = 0; srcPos < totalBytesRead; srcPos += maxPacketSize) {
                int length = Math.min(srcPos + maxPacketSize, totalBytesRead) - (srcPos + 2);
                if (length < 0) {
                    throw new IOException("Expected at least 2 bytes");
                }
                System.arraycopy(buffer, srcPos + 2, buffer, destPos, length);
                destPos += length;
            }
            return destPos;
        }

        private void setBaudrate(int baudRate) throws IOException {
            int subdivisor;
            int effectiveBaudRate;
            int divisor;
            if (baudRate > 3500000) {
                throw new UnsupportedOperationException("Baud rate to high");
            }
            if (baudRate >= 2500000) {
                effectiveBaudRate = 0;
                divisor = 0;
                subdivisor = 3000000;
            } else if (baudRate >= 1750000) {
                effectiveBaudRate = 1;
                divisor = 0;
                subdivisor = 2000000;
            } else {
                int divisor2 = ((48000000 / baudRate) + 1) >> 1;
                int subdivisor2 = divisor2 & 7;
                int divisor3 = divisor2 >> 3;
                if (divisor3 <= 16383) {
                    int effectiveBaudRate2 = 48000000 / ((divisor3 << 3) + subdivisor2);
                    subdivisor = (effectiveBaudRate2 + 1) >> 1;
                    effectiveBaudRate = divisor3;
                    divisor = subdivisor2;
                } else {
                    throw new UnsupportedOperationException("Baud rate to low");
                }
            }
            double d = subdivisor;
            double d2 = baudRate;
            Double.isNaN(d);
            Double.isNaN(d2);
            double baudRateError = Math.abs(1.0d - (d / d2));
            if (baudRateError >= 0.031d) {
                throw new UnsupportedOperationException(String.format("Baud rate deviation %.1f%% is higher than allowed 3%%", Double.valueOf(100.0d * baudRateError)));
            }
            int value = effectiveBaudRate;
            int index = 0;
            switch (divisor) {
                case 1:
                    value |= 49152;
                    break;
                case 2:
                    value |= 32768;
                    break;
                case 3:
                    value |= 0;
                    index = 0 | 1;
                    break;
                case 4:
                    value |= 16384;
                    break;
                case 5:
                    value |= 16384;
                    index = 0 | 1;
                    break;
                case 6:
                    value |= 32768;
                    index = 0 | 1;
                    break;
                case 7:
                    value |= 49152;
                    index = 0 | 1;
                    break;
            }
            if (this.baudRateWithPort) {
                index = (index << 8) | (this.mPortNumber + 1);
            }
            Log.d(FtdiSerialDriver.TAG, String.format("baud rate=%d, effective=%d, error=%.1f%%, value=0x%04x, index=0x%04x, divisor=%d, subdivisor=%d", Integer.valueOf(baudRate), Integer.valueOf(subdivisor), Double.valueOf(100.0d * baudRateError), Integer.valueOf(value), Integer.valueOf(index), Integer.valueOf(effectiveBaudRate), Integer.valueOf(divisor)));
            int result = this.mConnection.controlTransfer(64, 3, value, index, null, 0, 5000);
            if (result != 0) {
                throw new IOException("Setting baudrate failed: result=" + result);
            }
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public void setParameters(int baudRate, int dataBits, int stopBits, int parity) throws IOException {
            if (baudRate <= 0) {
                throw new IllegalArgumentException("Invalid baud rate: " + baudRate);
            }
            setBaudrate(baudRate);
            if (dataBits == 5 || dataBits == 6) {
                throw new UnsupportedOperationException("Unsupported data bits: " + dataBits);
            }
            if (dataBits == 7 || dataBits == 8) {
                int config = 0 | dataBits;
                if (parity != 0) {
                    if (parity == 1) {
                        config |= 256;
                    } else if (parity != 2) {
                        if (parity == 3) {
                            config |= 768;
                        } else if (parity == 4) {
                            config |= 1024;
                        } else {
                            throw new IllegalArgumentException("Invalid parity: " + parity);
                        }
                    } else {
                        config |= 512;
                    }
                }
                if (stopBits != 1) {
                    if (stopBits != 2) {
                        if (stopBits == 3) {
                            throw new UnsupportedOperationException("Unsupported stop bits: 1.5");
                        }
                        throw new IllegalArgumentException("Invalid stop bits: " + stopBits);
                    }
                    config |= 4096;
                }
                int result = this.mConnection.controlTransfer(64, 4, config, this.mPortNumber + 1, null, 0, 5000);
                if (result != 0) {
                    throw new IOException("Setting parameters failed: result=" + result);
                }
                this.breakConfig = config;
                return;
            }
            throw new IllegalArgumentException("Invalid data bits: " + dataBits);
        }

        private int getStatus() throws IOException {
            byte[] data = new byte[2];
            int result = this.mConnection.controlTransfer(REQTYPE_DEVICE_TO_HOST, 5, 0, this.mPortNumber + 1, data, data.length, 5000);
            if (result != 2) {
                throw new IOException("Get modem status failed: result=" + result);
            }
            return data[0];
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public boolean getCD() throws IOException {
            return (getStatus() & 128) != 0;
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
            int result = this.mConnection.controlTransfer(64, 1, value ? 257 : 256, this.mPortNumber + 1, null, 0, 5000);
            if (result != 0) {
                throw new IOException("Set DTR failed: result=" + result);
            }
            this.dtr = value;
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
            int result = this.mConnection.controlTransfer(64, 1, value ? MODEM_CONTROL_RTS_ENABLE : 512, this.mPortNumber + 1, null, 0, 5000);
            if (result != 0) {
                throw new IOException("Set DTR failed: result=" + result);
            }
            this.rts = value;
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public EnumSet<UsbSerialPort.ControlLine> getControlLines() throws IOException {
            int status = getStatus();
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
            if ((status & 128) != 0) {
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
            int result;
            int result2;
            if (purgeWriteBuffers && (result2 = this.mConnection.controlTransfer(64, 0, 1, this.mPortNumber + 1, null, 0, 5000)) != 0) {
                throw new IOException("Purge write buffer failed: result=" + result2);
            }
            if (purgeReadBuffers && (result = this.mConnection.controlTransfer(64, 0, 2, this.mPortNumber + 1, null, 0, 5000)) != 0) {
                throw new IOException("Purge read buffer failed: result=" + result);
            }
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public void setBreak(boolean value) throws IOException {
            int config = this.breakConfig;
            if (value) {
                config |= 16384;
            }
            int result = this.mConnection.controlTransfer(64, 4, config, this.mPortNumber + 1, null, 0, 5000);
            if (result != 0) {
                throw new IOException("Setting BREAK failed: result=" + result);
            }
        }

        public void setLatencyTimer(int latencyTime) throws IOException {
            int result = this.mConnection.controlTransfer(64, 9, latencyTime, this.mPortNumber + 1, null, 0, 5000);
            if (result != 0) {
                throw new IOException("Set latency timer failed: result=" + result);
            }
        }

        public int getLatencyTimer() throws IOException {
            byte[] data = new byte[1];
            int result = this.mConnection.controlTransfer(REQTYPE_DEVICE_TO_HOST, 10, 0, this.mPortNumber + 1, data, data.length, 5000);
            if (result != 1) {
                throw new IOException("Get latency timer failed: result=" + result);
            }
            return data[0];
        }
    }

    public static Map<Integer, int[]> getSupportedDevices() {
        Map<Integer, int[]> supportedDevices = new LinkedHashMap<>();
        supportedDevices.put(Integer.valueOf(UsbId.VENDOR_FTDI), new int[]{UsbId.FTDI_FT232R, UsbId.FTDI_FT232H, UsbId.FTDI_FT2232H, UsbId.FTDI_FT4232H, UsbId.FTDI_FT231X});
        return supportedDevices;
    }
}
