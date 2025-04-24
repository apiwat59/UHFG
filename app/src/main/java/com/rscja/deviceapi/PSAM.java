package com.rscja.deviceapi;

import android.util.Log;
import com.rscja.deviceapi.exception.ConfigurationException;
import com.rscja.utility.StringUtility;

/* loaded from: classes.dex */
public class PSAM {
    private static final String a = PSAM.class.getSimpleName();
    private static PSAM b = null;
    protected a config = a.d();

    private PSAM() throws ConfigurationException {
    }

    public static synchronized PSAM getInstance() {
        PSAM psam;
        synchronized (PSAM.class) {
            if (b == null) {
                try {
                    b = new PSAM();
                } catch (ConfigurationException e) {
                    e.printStackTrace();
                }
            }
            psam = b;
        }
        return psam;
    }

    protected DeviceAPI getDeviceAPI() {
        return DeviceAPI.a();
    }

    public boolean init() {
        int Psam_Init = getDeviceAPI().Psam_Init(this.config.i());
        if (Psam_Init == 0) {
            return true;
        }
        Log.e(a, "init() err :" + Psam_Init);
        return false;
    }

    public boolean free() {
        int Psam_Free = getDeviceAPI().Psam_Free(this.config.i());
        if (Psam_Free == 0) {
            return true;
        }
        Log.e(a, "free() err :" + Psam_Free);
        return false;
    }

    public String executeCmd(String hexCmd, String hexData) {
        if (StringUtility.isEmpty(hexCmd)) {
            return null;
        }
        char[] hexString2Chars = StringUtility.hexString2Chars(hexCmd);
        char[] hexString2Chars2 = StringUtility.hexString2Chars(hexData);
        byte[] Psam_Cmd = getDeviceAPI().Psam_Cmd(this.config.i(), hexString2Chars[0], hexString2Chars2, hexString2Chars2.length);
        if (Psam_Cmd != null && Psam_Cmd.length > 1) {
            if (Psam_Cmd[Psam_Cmd.length - 1] != 0) {
                Log.e(a, "Psam_Cmd() err :" + ((int) Psam_Cmd[Psam_Cmd.length - 1]));
                return null;
            }
            return StringUtility.bytes2HexString(Psam_Cmd, Psam_Cmd.length - 1);
        }
        Log.e(a, "Psam_Cmd() err result == null or result.length<2");
        return null;
    }
}
