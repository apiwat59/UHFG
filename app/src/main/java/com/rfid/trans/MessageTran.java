package com.rfid.trans;

import com.rfid.serialport.SerialPort;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

/* loaded from: classes.dex */
public class MessageTran {
    public InputStream mInStream = null;
    public OutputStream mOutStream = null;
    public SerialPort mSerialPort = null;
    private boolean connected = false;

    public boolean isOpen() {
        return this.connected;
    }

    public int open(String ComPort, int BaudRate) {
        try {
            this.mSerialPort = new SerialPort(new File(ComPort), BaudRate, 0);
        } catch (IOException e) {
        } catch (SecurityException e2) {
        } catch (InvalidParameterException e3) {
        }
        SerialPort serialPort = this.mSerialPort;
        if (serialPort != null) {
            this.mInStream = serialPort.getInputStream();
            this.mOutStream = this.mSerialPort.getOutputStream();
            this.connected = true;
            return 0;
        }
        return -1;
    }

    public int close() {
        if (this.mSerialPort != null) {
            try {
                InputStream inputStream = this.mInStream;
                if (inputStream != null) {
                    inputStream.close();
                    this.mInStream = null;
                }
                OutputStream outputStream = this.mOutStream;
                if (outputStream != null) {
                    outputStream.close();
                    this.mOutStream = null;
                }
                this.mSerialPort.close();
                this.mSerialPort = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.connected = false;
        return 0;
    }

    public byte[] Read() {
        if (!this.connected) {
            return null;
        }
        try {
            byte[] RecvBuff = new byte[256];
            int len = this.mInStream.read(RecvBuff);
            if (len > 0) {
                byte[] buff = new byte[len];
                System.arraycopy(RecvBuff, 0, buff, 0, len);
                return buff;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int Write(byte[] buffer) {
        if (!this.connected || buffer.length != (buffer[0] & 255) + 1) {
            return -1;
        }
        try {
            byte[] cmd = new byte[(buffer[0] & 255) + 1];
            System.arraycopy(buffer, 0, cmd, 0, cmd.length);
            this.mOutStream.write(cmd);
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public String bytesToHexString(byte[] src, int offset, int length) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src != null) {
            try {
                if (src.length > 0) {
                    for (int i = offset; i < length; i++) {
                        int v = src[i] & 255;
                        String hv = Integer.toHexString(v);
                        if (hv.length() == 1) {
                            stringBuilder.append(0);
                        }
                        stringBuilder.append(hv);
                    }
                    return stringBuilder.toString().toUpperCase();
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public byte[] hexStringToBytes(String hexString) {
        if (hexString != null) {
            try {
                if (!hexString.equals("")) {
                    String hexString2 = hexString.toUpperCase();
                    int length = hexString2.length() / 2;
                    char[] hexChars = hexString2.toCharArray();
                    byte[] d = new byte[length];
                    for (int i = 0; i < length; i++) {
                        int pos = i * 2;
                        d[i] = (byte) ((charToByte(hexChars[pos]) << 4) | charToByte(hexChars[pos + 1]));
                    }
                    return d;
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}
