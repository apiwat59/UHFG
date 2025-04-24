package com.gg.reader.api.entity;

import java.io.Serializable;

/* loaded from: classes.dex */
public class WifiHotspotInfo implements Serializable {
    private String bssid;
    private String capabilities;
    private int frequency;
    private int level;
    private String ssid;
    private int networkId = -1;
    private int status = -1;

    public String getCapabilities() {
        return this.capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public int getFrequency() {
        return this.frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getNetworkId() {
        return this.networkId;
    }

    public void setNetworkId(int networkId) {
        this.networkId = networkId;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getBssid() {
        return this.bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getSsid() {
        return this.ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String toString() {
        return "WifiHotspotInfo{capabilities='" + this.capabilities + "', frequency=" + this.frequency + ", level=" + this.level + ", networkId=" + this.networkId + ", status=" + this.status + ", bssid='" + this.bssid + "', ssid='" + this.ssid + "'}";
    }
}
