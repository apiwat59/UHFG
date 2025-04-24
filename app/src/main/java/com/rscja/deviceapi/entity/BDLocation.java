package com.rscja.deviceapi.entity;

/* loaded from: classes.dex */
public class BDLocation {
    private double a;
    private double b;
    private String c;
    private double d;

    public BDLocation(double lat, double lon, double altitude, String navType) {
        this.a = lat;
        this.b = lon;
        this.d = altitude;
        this.c = navType;
    }

    public double getLat() {
        return this.a;
    }

    public double getLon() {
        return this.b;
    }

    public String getNavType() {
        return this.c;
    }

    public double getAltitude() {
        return this.d;
    }

    public String toString() {
        return "type:" + this.c + " lat:" + this.a + " lon:" + this.b;
    }

    public boolean equals(Object o) {
        if (o instanceof BDLocation) {
            BDLocation bDLocation = (BDLocation) o;
            if (bDLocation.getNavType().equals(this.c) && bDLocation.getLat() == this.a && bDLocation.getLon() == this.b) {
                return true;
            }
            return false;
        }
        return false;
    }
}
