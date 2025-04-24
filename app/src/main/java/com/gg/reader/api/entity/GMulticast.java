package com.gg.reader.api.entity;

/* loaded from: classes.dex */
public class GMulticast {
    private String connectMode;
    private String deviceType;
    private String dhcp;
    private String gateway;
    private String ip;
    private String mac;
    private String mask;
    private String remoteIP;
    private String remotePort;
    private String serverPort;
    private String workingMode;

    public String getMac() {
        return this.mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getServerPort() {
        return this.serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getRemoteIP() {
        return this.remoteIP;
    }

    public void setRemoteIP(String remoteIP) {
        this.remoteIP = remoteIP;
    }

    public String getRemotePort() {
        return this.remotePort;
    }

    public void setRemotePort(String remotePort) {
        this.remotePort = remotePort;
    }

    public String getWorkingMode() {
        return this.workingMode;
    }

    public void setWorkingMode(String workingMode) {
        this.workingMode = workingMode;
    }

    public String getConnectMode() {
        return this.connectMode;
    }

    public void setConnectMode(String connectMode) {
        this.connectMode = connectMode;
    }

    public String getDeviceType() {
        return this.deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDhcp() {
        return this.dhcp;
    }

    public void setDhcp(String dhcp) {
        this.dhcp = dhcp;
    }

    public String getMask() {
        return this.mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getGateway() {
        return this.gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public GMulticast() {
        this.mac = "";
        this.ip = "";
        this.serverPort = "";
        this.remoteIP = "";
        this.remotePort = "";
        this.workingMode = "";
        this.connectMode = "";
        this.deviceType = "";
        this.dhcp = "";
        this.mask = "";
        this.gateway = "";
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x0093, code lost:
    
        if (r9.equals("MASK") != false) goto L49;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public GMulticast(java.lang.String r14) {
        /*
            Method dump skipped, instructions count: 354
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.gg.reader.api.entity.GMulticast.<init>(java.lang.String):void");
    }

    public String toString() {
        return "GMulticast{mac='" + this.mac + "', ip='" + this.ip + "', serverPort='" + this.serverPort + "', remoteIP='" + this.remoteIP + "', remotePort='" + this.remotePort + "', workingMode='" + this.workingMode + "', connectMode='" + this.connectMode + "', deviceType='" + this.deviceType + "', dhcp='" + this.dhcp + "', mask='" + this.mask + "', gateway='" + this.gateway + "'}";
    }
}
