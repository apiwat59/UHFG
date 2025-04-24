package com.rscja.deviceapi.entity;

import com.rscja.deviceapi.RFIDWithISO14443A;

/* loaded from: classes.dex */
public class DESFireFile {
    private int a;
    private RFIDWithISO14443A.DESFireFileTypekEnum b;
    private int c;
    private RFIDWithISO14443A.DESFireEncryptionTypekEnum d;
    private String e;
    private String f;
    private String g;
    private String h;
    private String i;
    private int j;
    private int k;

    public DESFireFile(int fileNo, RFIDWithISO14443A.DESFireFileTypekEnum fileType, RFIDWithISO14443A.DESFireEncryptionTypekEnum encryptionType, String readPermissions, String writePermissions, String readWritePermissions, String updatePermissions) {
        this.a = fileNo;
        this.b = fileType;
        this.b = fileType;
        this.d = encryptionType;
        this.e = readPermissions;
        this.f = writePermissions;
        this.h = updatePermissions;
        this.g = readWritePermissions;
    }

    public int getFileNo() {
        return this.a;
    }

    public RFIDWithISO14443A.DESFireFileTypekEnum getFileType() {
        return this.b;
    }

    public int getFileSize() {
        return this.c;
    }

    public void setFileSize(int fileSize) {
        this.c = fileSize;
    }

    public RFIDWithISO14443A.DESFireEncryptionTypekEnum getEncryptionType() {
        return this.d;
    }

    public String getReadPermissions() {
        return this.e;
    }

    public String getWritePermissions() {
        return this.f;
    }

    public String getReadWritePermissions() {
        return this.g;
    }

    public String getUpdatePermissions() {
        return this.h;
    }

    public void setMinValue(int minValue) {
        this.j = minValue;
    }

    public void setMaxValue(int maxValue) {
        this.k = maxValue;
    }

    public int getMinValue() {
        return this.j;
    }

    public int getMaxValue() {
        return this.k;
    }

    public String getData() {
        return this.i;
    }

    public void setData(String str) {
    }
}
