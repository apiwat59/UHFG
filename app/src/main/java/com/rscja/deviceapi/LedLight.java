package com.rscja.deviceapi;

import android.util.Log;

/* loaded from: classes.dex */
public class LedLight {
    private static final String a = LedLight.class.getSimpleName();
    private static LedLight b = null;

    public static synchronized LedLight getInstance() {
        LedLight ledLight;
        synchronized (LedLight.class) {
            if (b == null) {
                b = new LedLight();
            }
            ledLight = b;
        }
        return ledLight;
    }

    public synchronized boolean open() {
        int LedOn = DeviceAPI.a().LedOn(a.a(), 1);
        if (LedOn > 0) {
            return true;
        }
        Log.e(a, "open() err:" + LedOn);
        return false;
    }

    public synchronized boolean close() {
        int LedOff = DeviceAPI.a().LedOff(a.a(), 1);
        if (LedOff > 0) {
            return true;
        }
        Log.e(a, "close() err:" + LedOff);
        return false;
    }
}
