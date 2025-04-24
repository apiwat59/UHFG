package com.gg.reader.api.dal;

import com.gg.reader.api.entity.GMulticast;
import com.gg.reader.api.utils.ThreadPoolUtils;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/* loaded from: classes.dex */
public class GUdpMulticast {
    private String _GroupIP;
    private int _GroupPort;
    private InetAddress address;
    public HandlerDebugLog debugLog;
    public HandlerUdpMulticast handlerUdpMulticast;
    private MulticastSocket ms;
    private DatagramPacket dataPacket = null;
    private Boolean keepReceive = false;
    private int index = 0;

    static /* synthetic */ int access$208(GUdpMulticast x0) {
        int i = x0.index;
        x0.index = i + 1;
        return i;
    }

    public GUdpMulticast(String _GroupIP, int _GroupPort, int timeout) {
        this.ms = null;
        this._GroupIP = "230.1.1.168";
        this._GroupPort = 8161;
        try {
            this._GroupIP = _GroupIP;
            this._GroupPort = _GroupPort;
            MulticastSocket multicastSocket = new MulticastSocket(this._GroupPort);
            this.ms = multicastSocket;
            multicastSocket.setSoTimeout(timeout);
            this.address = InetAddress.getByName(this._GroupIP);
        } catch (IOException e) {
        }
    }

    public void triggerOnUdpMulticast(GMulticast gMulticast) {
        try {
            HandlerUdpMulticast handlerUdpMulticast = this.handlerUdpMulticast;
            if (handlerUdpMulticast != null) {
                handlerUdpMulticast.log(gMulticast);
            }
        } catch (Exception e) {
        }
    }

    public void start() {
        if (!this.keepReceive.booleanValue()) {
            this.keepReceive = true;
            final List<String> nif = getAllNif();
            ThreadPoolUtils.run(new Runnable() { // from class: com.gg.reader.api.dal.GUdpMulticast.1
                @Override // java.lang.Runnable
                public void run() {
                    while (GUdpMulticast.this.keepReceive.booleanValue()) {
                        try {
                            GUdpMulticast.this.ms.joinGroup(new InetSocketAddress(GUdpMulticast.this.address, 8161), NetworkInterface.getByName((String) nif.get(GUdpMulticast.this.index % nif.size())));
                            byte[] temp = new byte[1024];
                            GUdpMulticast.this.dataPacket = new DatagramPacket(temp, temp.length);
                            if (GUdpMulticast.this.debugLog != null) {
                                GUdpMulticast.this.debugLog.receiveDebugLog("[Udp]-->" + ((String) nif.get(GUdpMulticast.this.index % nif.size())) + ":Receive");
                            }
                            GUdpMulticast.this.ms.receive(GUdpMulticast.this.dataPacket);
                            if (GUdpMulticast.this.dataPacket.getLength() > 0 && GUdpMulticast.this.debugLog != null) {
                                GUdpMulticast.this.debugLog.receiveDebugLog("[Udp]-->[" + new String(GUdpMulticast.this.dataPacket.getData(), "ASCII") + "]");
                            }
                            GMulticast gMulticast = new GMulticast(new String(GUdpMulticast.this.dataPacket.getData(), "ASCII").trim());
                            GUdpMulticast.this.triggerOnUdpMulticast(gMulticast);
                            GUdpMulticast.this.ms.leaveGroup(GUdpMulticast.this.address);
                        } catch (Exception e) {
                            try {
                                GUdpMulticast.this.ms.leaveGroup(GUdpMulticast.this.address);
                                if (GUdpMulticast.this.debugLog != null) {
                                    GUdpMulticast.this.debugLog.receiveDebugLog("[Udp]-->Next network adapter");
                                }
                            } catch (IOException e2) {
                            }
                        }
                        GUdpMulticast.access$208(GUdpMulticast.this);
                    }
                }
            });
        }
    }

    public void close() {
        this.keepReceive = false;
        this.ms.close();
    }

    private List<String> getAllNif() {
        List<String> nifS = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
            while (nifs.hasMoreElements()) {
                NetworkInterface nif = nifs.nextElement();
                Enumeration<InetAddress> addresses = nif.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if ((addr instanceof Inet4Address) && !addr.getHostAddress().equals("127.0.0.1")) {
                        nifS.add(nif.getName());
                    }
                }
            }
        } catch (Exception e) {
        }
        return nifS;
    }
}
