package com.rscja.deviceapi.entity;

/* loaded from: classes.dex */
public class ISO15693Entity extends SimpleRFIDEntity {
    private char[] a;
    private String b;
    private String c;

    public ISO15693Entity(String id, String type) {
        super(id, type);
        this.b = "";
        this.c = "";
    }

    public ISO15693Entity(String id, String type, char[] originalUID) {
        super(id, type);
        this.b = "";
        this.c = "";
        setOriginalUID(originalUID);
    }

    public ISO15693Entity(String id, String type, char[] originalUID, String afi, String desfid) {
        super(id, type);
        this.b = "";
        this.c = "";
        setOriginalUID(originalUID);
        setAFI(afi);
        setDESFID(desfid);
    }

    public char[] getOriginalUID() {
        return this.a;
    }

    public void setOriginalUID(char[] originalUID) {
        this.a = originalUID;
    }

    public String getAFI() {
        return this.b;
    }

    public void setAFI(String aFI) {
        this.b = aFI;
    }

    public String getDESFID() {
        return this.c;
    }

    public void setDESFID(String dESFID) {
        this.c = dESFID;
    }
}
