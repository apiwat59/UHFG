package com.rscja.deviceapi;

import android.util.Log;
import com.rscja.deviceapi.exception.ConfigurationException;
import com.rscja.utility.StringUtility;

/* loaded from: classes.dex */
class b {
    private static b a = null;
    protected a config = a.d();

    protected b() throws ConfigurationException {
    }

    public static synchronized b getInstance() throws ConfigurationException {
        b bVar;
        synchronized (b.class) {
            if (a == null) {
                a = new b();
            }
            bVar = a;
        }
        return bVar;
    }

    protected DeviceAPI getDeviceAPI() {
        return DeviceAPI.a();
    }

    public synchronized boolean init() {
        int RFID_init = getDeviceAPI().RFID_init(this.config.i(), this.config.j(), this.config.k());
        if (RFID_init == 0) {
            return true;
        }
        Log.e("RFIDBase", "init() err:" + RFID_init);
        return false;
    }

    public synchronized boolean free() {
        int RFID_free = getDeviceAPI().RFID_free(this.config.i());
        if (RFID_free == 0) {
            return true;
        }
        Log.e("RFIDBase", "free() err:" + RFID_free);
        return false;
    }

    public synchronized String getVersion() {
        byte[] RFID_GetVer = getDeviceAPI().RFID_GetVer();
        StringBuilder sb = new StringBuilder("getVersion b[0]=");
        sb.append(Integer.valueOf(RFID_GetVer[0]));
        sb.append(" b.length=");
        sb.append(RFID_GetVer.length);
        Log.d("RFIDBase", sb.toString());
        if (RFID_GetVer[0] == 0) {
            Log.d("RFIDBase", "b[1]=" + Integer.valueOf(RFID_GetVer[1]));
            int i = RFID_GetVer[1];
            byte[] bArr = new byte[i];
            for (int i2 = 0; i2 < i; i2++) {
                bArr[i2] = RFID_GetVer[i2 + 2];
            }
            return String.valueOf(StringUtility.getChars(bArr));
        }
        Log.e("RFIDBase", "getVersion() err:" + ((int) RFID_GetVer[0]));
        return null;
    }
}
