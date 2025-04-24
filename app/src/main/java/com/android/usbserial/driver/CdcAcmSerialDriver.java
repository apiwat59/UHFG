package com.android.usbserial.driver;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.util.Log;
import androidx.core.internal.view.SupportMenu;
import com.android.usbserial.driver.UsbSerialPort;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Level;

/* loaded from: classes.dex */
public class CdcAcmSerialDriver implements UsbSerialDriver {
    private final UsbDevice mDevice;
    private final String TAG = CdcAcmSerialDriver.class.getSimpleName();
    private final List<UsbSerialPort> mPorts = new ArrayList();

    public CdcAcmSerialDriver(UsbDevice device) {
        this.mDevice = device;
        int controlInterfaceCount = 0;
        int dataInterfaceCount = 0;
        for (int i = 0; i < device.getInterfaceCount(); i++) {
            controlInterfaceCount = device.getInterface(i).getInterfaceClass() == 2 ? controlInterfaceCount + 1 : controlInterfaceCount;
            if (device.getInterface(i).getInterfaceClass() == 10) {
                dataInterfaceCount++;
            }
        }
        for (int port = 0; port < Math.min(controlInterfaceCount, dataInterfaceCount); port++) {
            this.mPorts.add(new CdcAcmSerialPort(this.mDevice, port));
        }
        if (this.mPorts.size() == 0) {
            this.mPorts.add(new CdcAcmSerialPort(this.mDevice, -1));
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
        return this.TAG;
    }

    public class CdcAcmSerialPort extends CommonUsbSerialPort {
        private static final int GET_LINE_CODING = 33;
        private static final int SEND_BREAK = 35;
        private static final int SET_CONTROL_LINE_STATE = 34;
        private static final int SET_LINE_CODING = 32;
        private static final int USB_RECIP_INTERFACE = 1;
        private static final int USB_RT_ACM = 33;
        private UsbEndpoint mControlEndpoint;
        private int mControlIndex;
        private UsbInterface mControlInterface;
        private UsbInterface mDataInterface;
        private boolean mDtr;
        private boolean mRts;

        public CdcAcmSerialPort(UsbDevice device, int portNumber) {
            super(device, portNumber);
            this.mRts = false;
            this.mDtr = false;
        }

        @Override // com.android.usbserial.driver.UsbSerialPort
        public UsbSerialDriver getDriver() {
            return CdcAcmSerialDriver.this;
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort
        protected void openInt(UsbDeviceConnection connection) throws IOException {
            if (this.mPortNumber == -1) {
                Log.d(CdcAcmSerialDriver.this.TAG, "device might be castrated ACM device, trying single interface logic");
                openSingleInterface();
            } else {
                Log.d(CdcAcmSerialDriver.this.TAG, "trying default interface logic");
                openInterface();
            }
        }

        private void openSingleInterface() throws IOException {
            this.mControlIndex = 0;
            this.mControlInterface = this.mDevice.getInterface(0);
            this.mDataInterface = this.mDevice.getInterface(0);
            if (!this.mConnection.claimInterface(this.mControlInterface, true)) {
                throw new IOException("Could not claim shared control/data interface");
            }
            for (int i = 0; i < this.mControlInterface.getEndpointCount(); i++) {
                UsbEndpoint ep = this.mControlInterface.getEndpoint(i);
                if (ep.getDirection() == 128 && ep.getType() == 3) {
                    this.mControlEndpoint = ep;
                } else if (ep.getDirection() == 128 && ep.getType() == 2) {
                    this.mReadEndpoint = ep;
                } else if (ep.getDirection() == 0 && ep.getType() == 2) {
                    this.mWriteEndpoint = ep;
                }
            }
            if (this.mControlEndpoint == null) {
                throw new IOException("No control endpoint");
            }
        }

        private void openInterface() throws IOException {
            Log.d(CdcAcmSerialDriver.this.TAG, "claiming interfaces, count=" + this.mDevice.getInterfaceCount());
            int controlInterfaceCount = 0;
            int dataInterfaceCount = 0;
            this.mControlInterface = null;
            this.mDataInterface = null;
            for (int i = 0; i < this.mDevice.getInterfaceCount(); i++) {
                UsbInterface usbInterface = this.mDevice.getInterface(i);
                if (usbInterface.getInterfaceClass() == 2) {
                    if (controlInterfaceCount == this.mPortNumber) {
                        this.mControlIndex = i;
                        this.mControlInterface = usbInterface;
                    }
                    controlInterfaceCount++;
                }
                if (usbInterface.getInterfaceClass() == 10) {
                    if (dataInterfaceCount == this.mPortNumber) {
                        this.mDataInterface = usbInterface;
                    }
                    dataInterfaceCount++;
                }
            }
            if (this.mControlInterface != null) {
                Log.d(CdcAcmSerialDriver.this.TAG, "Control iface=" + this.mControlInterface);
                if (!this.mConnection.claimInterface(this.mControlInterface, true)) {
                    throw new IOException("Could not claim control interface");
                }
                UsbEndpoint endpoint = this.mControlInterface.getEndpoint(0);
                this.mControlEndpoint = endpoint;
                if (endpoint.getDirection() != 128 || this.mControlEndpoint.getType() != 3) {
                    throw new IOException("Invalid control endpoint");
                }
                if (this.mDataInterface != null) {
                    Log.d(CdcAcmSerialDriver.this.TAG, "data iface=" + this.mDataInterface);
                    if (!this.mConnection.claimInterface(this.mDataInterface, true)) {
                        throw new IOException("Could not claim data interface");
                    }
                    for (int i2 = 0; i2 < this.mDataInterface.getEndpointCount(); i2++) {
                        UsbEndpoint ep = this.mDataInterface.getEndpoint(i2);
                        if (ep.getDirection() == 128 && ep.getType() == 2) {
                            this.mReadEndpoint = ep;
                        }
                        if (ep.getDirection() == 0 && ep.getType() == 2) {
                            this.mWriteEndpoint = ep;
                        }
                    }
                    return;
                }
                throw new IOException("No data interface");
            }
            throw new IOException("No control interface");
        }

        private int sendAcmControlMessage(int request, int value, byte[] buf) throws IOException {
            int len = this.mConnection.controlTransfer(33, request, value, this.mControlIndex, buf, buf != null ? buf.length : 0, Level.TRACE_INT);
            if (len < 0) {
                throw new IOException("controlTransfer failed");
            }
            return len;
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort
        protected void closeInt() {
            try {
                this.mConnection.releaseInterface(this.mControlInterface);
                this.mConnection.releaseInterface(this.mDataInterface);
            } catch (Exception e) {
            }
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public void setParameters(int baudRate, int dataBits, int stopBits, int parity) throws IOException {
            byte stopBitsByte;
            byte parityBitesByte;
            if (baudRate <= 0) {
                throw new IllegalArgumentException("Invalid baud rate: " + baudRate);
            }
            if (dataBits < 5 || dataBits > 8) {
                throw new IllegalArgumentException("Invalid data bits: " + dataBits);
            }
            if (stopBits == 1) {
                stopBitsByte = 0;
            } else if (stopBits != 2) {
                if (stopBits == 3) {
                    stopBitsByte = 1;
                } else {
                    throw new IllegalArgumentException("Invalid stop bits: " + stopBits);
                }
            } else {
                stopBitsByte = 2;
            }
            if (parity == 0) {
                parityBitesByte = 0;
            } else if (parity == 1) {
                parityBitesByte = 1;
            } else if (parity != 2) {
                if (parity != 3) {
                    if (parity == 4) {
                        parityBitesByte = 4;
                    } else {
                        throw new IllegalArgumentException("Invalid parity: " + parity);
                    }
                } else {
                    parityBitesByte = 3;
                }
            } else {
                parityBitesByte = 2;
            }
            byte[] msg = {(byte) (baudRate & 255), (byte) ((baudRate >> 8) & 255), (byte) ((baudRate >> 16) & 255), (byte) ((baudRate >> 24) & 255), stopBitsByte, parityBitesByte, (byte) dataBits};
            sendAcmControlMessage(32, 0, msg);
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public boolean getDTR() throws IOException {
            return this.mDtr;
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public void setDTR(boolean value) throws IOException {
            this.mDtr = value;
            setDtrRts();
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public boolean getRTS() throws IOException {
            return this.mRts;
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public void setRTS(boolean value) throws IOException {
            this.mRts = value;
            setDtrRts();
        }

        private void setDtrRts() throws IOException {
            sendAcmControlMessage(34, (this.mRts ? 2 : 0) | (this.mDtr ? 1 : 0), null);
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public EnumSet<UsbSerialPort.ControlLine> getControlLines() throws IOException {
            EnumSet<UsbSerialPort.ControlLine> set = EnumSet.noneOf(UsbSerialPort.ControlLine.class);
            if (this.mRts) {
                set.add(UsbSerialPort.ControlLine.RTS);
            }
            if (this.mDtr) {
                set.add(UsbSerialPort.ControlLine.DTR);
            }
            return set;
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public EnumSet<UsbSerialPort.ControlLine> getSupportedControlLines() throws IOException {
            return EnumSet.of(UsbSerialPort.ControlLine.RTS, UsbSerialPort.ControlLine.DTR);
        }

        @Override // com.android.usbserial.driver.CommonUsbSerialPort, com.android.usbserial.driver.UsbSerialPort
        public void setBreak(boolean value) throws IOException {
            sendAcmControlMessage(35, value ? SupportMenu.USER_MASK : 0, null);
        }
    }

    public static Map<Integer, int[]> getSupportedDevices() {
        Map<Integer, int[]> supportedDevices = new LinkedHashMap<>();
        supportedDevices.put(Integer.valueOf(UsbId.VENDOR_ARDUINO), new int[]{1, 67, 16, 66, 59, 68, 63, 68, UsbId.ARDUINO_LEONARDO, UsbId.ARDUINO_MICRO});
        supportedDevices.put(Integer.valueOf(UsbId.VENDOR_VAN_OOIJEN_TECH), new int[]{1155});
        supportedDevices.put(1003, new int[]{UsbId.ATMEL_LUFA_CDC_DEMO_APP});
        supportedDevices.put(Integer.valueOf(UsbId.VENDOR_LEAFLABS), new int[]{4});
        supportedDevices.put(Integer.valueOf(UsbId.VENDOR_ARM), new int[]{UsbId.ARM_MBED});
        supportedDevices.put(1155, new int[]{UsbId.ST_CDC});
        return supportedDevices;
    }
}
