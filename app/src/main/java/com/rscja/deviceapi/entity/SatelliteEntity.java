package com.rscja.deviceapi.entity;

/* loaded from: classes.dex */
public class SatelliteEntity {
    private String a;
    private String b;

    public SatelliteEntity(String number, String signal) {
        this.a = number;
        this.b = signal;
    }

    public String getNumber() {
        return this.a;
    }

    public void setNumber(String number) {
        this.a = number;
    }

    public String getSignal() {
        return this.b;
    }

    public void setSignal(String signal) {
        this.b = signal;
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        return this.a.equals(((SatelliteEntity) o).getNumber());
    }
}
