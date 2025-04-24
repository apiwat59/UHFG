package com.pda.uhf_g.entity;

import java.io.Serializable;

/* loaded from: classes.dex */
public class TagInfo implements Serializable {
    private Long count;
    private int ctesius;
    private String epc;
    private String epcData;
    private Long index;
    private boolean isShowTid;
    private Integer ltu27;
    private Integer ltu31;
    private String moisture;
    private String nmv2d;
    private String reservedData;
    private String rssi;
    private String tid;
    private String type;
    private String userData;

    public TagInfo() {
    }

    public TagInfo(Long index, String type, String epc, String tid, String rssi) {
        this.index = index;
        this.type = type;
        this.epc = epc;
        this.tid = tid;
        this.rssi = rssi;
    }

    public Long getIndex() {
        return this.index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEpc() {
        return this.epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public Long getCount() {
        return this.count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getTid() {
        return this.tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getRssi() {
        return this.rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getUserData() {
        return this.userData;
    }

    public void setUserData(String userData) {
        this.userData = userData;
    }

    public String getReservedData() {
        return this.reservedData;
    }

    public void setReservedData(String reservedData) {
        this.reservedData = reservedData;
    }

    public String getNmv2d() {
        return this.nmv2d;
    }

    public void setNmv2d(String nmv2d) {
        this.nmv2d = nmv2d;
    }

    public String getEpcData() {
        return this.epcData;
    }

    public void setEpcData(String epcData) {
        this.epcData = epcData;
    }

    public Integer getLtu27() {
        return this.ltu27;
    }

    public void setLtu27(Integer ltu27) {
        this.ltu27 = ltu27;
    }

    public Integer getLtu31() {
        return this.ltu31;
    }

    public void setLtu31(Integer ltu31) {
        this.ltu31 = ltu31;
    }

    public String getMoisture() {
        return this.moisture;
    }

    public void setMoisture(String moisture) {
        this.moisture = moisture;
    }

    public int getCtesius() {
        return this.ctesius;
    }

    public void setCtesius(int ctesius) {
        this.ctesius = ctesius;
    }

    public boolean getIsShowTid() {
        return this.isShowTid;
    }

    public void setIsShowTid(boolean isShowTid) {
        this.isShowTid = isShowTid;
    }

    public String toString() {
        return "TagInfo{index=" + this.index + ", type='" + this.type + "', epc='" + this.epc + "', count=" + this.count + ", tid='" + this.tid + "', rssi='" + this.rssi + "', userData='" + this.userData + "', reservedData='" + this.reservedData + "'}";
    }
}
