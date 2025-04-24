package com.rscja.deviceapi;

import android.util.Log;
import com.rscja.deviceapi.exception.ConfigurationException;

/* loaded from: classes.dex */
public class Barcode1D {
    private static final String a = Barcode1D.class.getSimpleName();
    private static Barcode1D b = null;
    protected a config = a.b();

    private Barcode1D() throws ConfigurationException {
    }

    public static synchronized Barcode1D getInstance() throws ConfigurationException {
        Barcode1D barcode1D;
        synchronized (Barcode1D.class) {
            if (b == null) {
                b = new Barcode1D();
            }
            barcode1D = b;
        }
        return barcode1D;
    }

    public synchronized boolean open() {
        int Barcode_1D_Open = DeviceAPI.a().Barcode_1D_Open(this.config.i(), this.config.j(), this.config.k());
        if (Barcode_1D_Open == 1) {
            return true;
        }
        Log.e(a, "open() err:" + Barcode_1D_Open);
        return false;
    }

    public synchronized String scan() {
        return new String(DeviceAPI.a().Barcode_1D_Scan(this.config.i()));
    }

    public synchronized boolean close() {
        int Barcode_1D_Close = DeviceAPI.a().Barcode_1D_Close(this.config.i());
        if (Barcode_1D_Close == 1) {
            return true;
        }
        Log.e(a, "close() err:" + Barcode_1D_Close);
        return false;
    }
}
