package org.apache.log4j.helpers;

import java.io.IOException;
import java.io.Writer;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;

/* loaded from: classes.dex */
public class SyslogWriter extends Writer {
    static String syslogHost;
    final int SYSLOG_PORT = 514;
    private InetAddress address;
    private DatagramSocket ds;
    private final int port;

    public SyslogWriter(String syslogHost2) {
        syslogHost = syslogHost2;
        if (syslogHost2 == null) {
            throw new NullPointerException("syslogHost");
        }
        String host = syslogHost2;
        int urlPort = -1;
        if (host.indexOf("[") != -1 || host.indexOf(58) == host.lastIndexOf(58)) {
            try {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("http://");
                stringBuffer.append(host);
                URL url = new URL(stringBuffer.toString());
                if (url.getHost() != null) {
                    host = url.getHost();
                    if (host.startsWith("[") && host.charAt(host.length() - 1) == ']') {
                        host = host.substring(1, host.length() - 1);
                    }
                    urlPort = url.getPort();
                }
            } catch (MalformedURLException e) {
                LogLog.error("Malformed URL: will attempt to interpret as InetAddress.", e);
            }
        }
        this.port = urlPort == -1 ? 514 : urlPort;
        try {
            this.address = InetAddress.getByName(host);
        } catch (UnknownHostException e2) {
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2.append("Could not find ");
            stringBuffer2.append(host);
            stringBuffer2.append(". All logging will FAIL.");
            LogLog.error(stringBuffer2.toString(), e2);
        }
        try {
            this.ds = new DatagramSocket();
        } catch (SocketException e3) {
            e3.printStackTrace();
            StringBuffer stringBuffer3 = new StringBuffer();
            stringBuffer3.append("Could not instantiate DatagramSocket to ");
            stringBuffer3.append(host);
            stringBuffer3.append(". All logging will FAIL.");
            LogLog.error(stringBuffer3.toString(), e3);
        }
    }

    @Override // java.io.Writer
    public void write(char[] buf, int off, int len) throws IOException {
        write(new String(buf, off, len));
    }

    @Override // java.io.Writer
    public void write(String string) throws IOException {
        byte[] bytes = string.getBytes();
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, this.address, this.port);
        DatagramSocket datagramSocket = this.ds;
        if (datagramSocket != null && this.address != null) {
            datagramSocket.send(packet);
        }
    }

    @Override // java.io.Writer, java.io.Flushable
    public void flush() {
    }

    @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
    }
}
