package com.rscja.deviceapi.entity;

/* loaded from: classes.dex */
public class AnimalEntity {
    private long a;
    private long b;
    private long c;
    private long d;
    private long e;

    public AnimalEntity(long nationalID, long countryID, long reserved, long dataBlock, long animalFlag) {
        this.a = nationalID;
        this.b = countryID;
        this.c = reserved;
        this.d = dataBlock;
        this.e = animalFlag;
    }

    public AnimalEntity() {
    }

    public long getNationalID() {
        return this.a;
    }

    public void setNationalID(long nationalID) {
        this.a = nationalID;
    }

    public long getCountryID() {
        return this.b;
    }

    public void setCountryID(long countryID) {
        this.b = countryID;
    }

    public long getReserved() {
        return this.c;
    }

    public void setReserved(long reserved) {
        this.c = reserved;
    }

    public long getDataBlock() {
        return this.d;
    }

    public void setDataBlock(long dataBlock) {
        this.d = dataBlock;
    }

    public long getAnimalFlag() {
        return this.e;
    }

    public void setAnimalFlag(long animalFlag) {
        this.e = animalFlag;
    }
}
