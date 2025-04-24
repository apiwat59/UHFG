package com.rscja.deviceapi.entity;

/* loaded from: classes.dex */
public class SimpleRFIDEntity {
    private String a;
    private String b;
    private String c = "";

    public SimpleRFIDEntity(String id, String type) {
        this.a = "";
        this.b = "";
        this.a = id;
        this.b = type;
    }

    public String getId() {
        return this.a;
    }

    public String getType() {
        return this.b;
    }

    public String getData() {
        return this.c;
    }

    public void setData(String data) {
        this.c = data;
    }

    public String toString() {
        String str = "ID:" + this.a;
        String str2 = this.b;
        if (str2 != null && str2.length() > 0) {
            return String.valueOf(str) + "   TYPE:" + this.b;
        }
        return str;
    }
}
