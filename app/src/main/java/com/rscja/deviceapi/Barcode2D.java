package com.rscja.deviceapi;

import android.util.Log;
import com.rscja.deviceapi.exception.ConfigurationException;

/* loaded from: classes.dex */
public class Barcode2D {
    private static final String a = Barcode2D.class.getSimpleName();
    private static Barcode2D b = null;
    protected a config = a.c();

    private Barcode2D() throws ConfigurationException {
    }

    public static synchronized Barcode2D getInstance() throws ConfigurationException {
        Barcode2D barcode2D;
        synchronized (Barcode2D.class) {
            if (b == null) {
                b = new Barcode2D();
            }
            barcode2D = b;
        }
        return barcode2D;
    }

    public synchronized boolean open() {
        int Barcode_2D_Open = DeviceAPI.a().Barcode_2D_Open(this.config.i(), this.config.j(), this.config.k());
        if (Barcode_2D_Open == 1) {
            return true;
        }
        Log.e(a, "open() err:" + Barcode_2D_Open);
        return false;
    }

    public synchronized String scan() {
        return new String(DeviceAPI.a().Barcode_2D_Scan(this.config.i()));
    }

    public synchronized boolean close() {
        int Barcode_2D_Close = DeviceAPI.a().Barcode_2D_Close(this.config.i());
        if (Barcode_2D_Close == 1) {
            return true;
        }
        Log.e(a, "close() err:" + Barcode_2D_Close);
        return false;
    }
}
