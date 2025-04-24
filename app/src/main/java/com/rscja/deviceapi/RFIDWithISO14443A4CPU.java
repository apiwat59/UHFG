package com.rscja.deviceapi;

import android.util.Log;
import com.rscja.deviceapi.exception.ConfigurationException;
import com.rscja.utility.StringUtility;
import org.apache.log4j.spi.Configurator;

/* loaded from: classes.dex */
public class RFIDWithISO14443A4CPU extends b {
    private static RFIDWithISO14443A4CPU a = null;

    @Override // com.rscja.deviceapi.b
    public /* bridge */ /* synthetic */ boolean free() {
        return super.free();
    }

    @Override // com.rscja.deviceapi.b
    public /* bridge */ /* synthetic */ String getVersion() {
        return super.getVersion();
    }

    @Override // com.rscja.deviceapi.b
    public /* bridge */ /* synthetic */ boolean init() {
        return super.init();
    }

    public static synchronized RFIDWithISO14443A4CPU getInstance() throws ConfigurationException {
        RFIDWithISO14443A4CPU rFIDWithISO14443A4CPU;
        synchronized (RFIDWithISO14443A4CPU.class) {
            if (a == null) {
                a = new RFIDWithISO14443A4CPU();
            }
            rFIDWithISO14443A4CPU = a;
        }
        return rFIDWithISO14443A4CPU;
    }

    protected RFIDWithISO14443A4CPU() throws ConfigurationException {
    }

    public String sendCommand(String cmd) {
        if (StringUtility.isEmpty(cmd)) {
            Log.e("RFIDWithISO14443A4CPU", "sendCommand() err:cmd  is empty");
            return null;
        }
        if (!StringUtility.isHexNumberRex(cmd)) {
            Log.e("RFIDWithISO14443A4CPU", "sendCommand() err:cmd  not hex");
            return null;
        }
        char[] hexString2Chars = StringUtility.hexString2Chars(cmd);
        char[] ISO14443A_cpu_command = getDeviceAPI().ISO14443A_cpu_command(hexString2Chars, hexString2Chars.length);
        if (ISO14443A_cpu_command == null || ISO14443A_cpu_command[0] != 0 || ISO14443A_cpu_command[1] == 0) {
            StringBuilder sb = new StringBuilder("sendCommand() err:");
            sb.append(ISO14443A_cpu_command == null ? Configurator.NULL : Integer.valueOf(ISO14443A_cpu_command[0]));
            Log.e("RFIDWithISO14443A4CPU", sb.toString());
            return null;
        }
        char c = ISO14443A_cpu_command[1];
        char[] cArr = new char[c];
        for (int i = 0; i < c; i++) {
            cArr[i] = ISO14443A_cpu_command[i + 2];
        }
        return StringUtility.chars2HexString(cArr, c);
    }

    public String reset() {
        char[] ISO14443A_cpu_reset = getDeviceAPI().ISO14443A_cpu_reset();
        if (ISO14443A_cpu_reset == null || ISO14443A_cpu_reset[0] != 0 || ISO14443A_cpu_reset[1] == 0) {
            StringBuilder sb = new StringBuilder("reset() err:");
            sb.append(ISO14443A_cpu_reset == null ? Configurator.NULL : Integer.valueOf(ISO14443A_cpu_reset[0]));
            Log.e("RFIDWithISO14443A4CPU", sb.toString());
            return null;
        }
        char c = ISO14443A_cpu_reset[1];
        char[] cArr = new char[c];
        for (int i = 0; i < c; i++) {
            cArr[i] = ISO14443A_cpu_reset[i + 2];
        }
        return StringUtility.chars2HexString(cArr, c);
    }

    public String rats() {
        char[] ISO14443A_cpu_rats = getDeviceAPI().ISO14443A_cpu_rats();
        if (ISO14443A_cpu_rats == null || ISO14443A_cpu_rats[0] != 0 || ISO14443A_cpu_rats[1] == 0) {
            StringBuilder sb = new StringBuilder("rats() err:");
            sb.append(ISO14443A_cpu_rats == null ? Configurator.NULL : Integer.valueOf(ISO14443A_cpu_rats[0]));
            Log.e("RFIDWithISO14443A4CPU", sb.toString());
            return null;
        }
        char c = ISO14443A_cpu_rats[1];
        char[] cArr = new char[c];
        for (int i = 0; i < c; i++) {
            cArr[i] = ISO14443A_cpu_rats[i + 2];
        }
        return StringUtility.chars2HexString(cArr, c);
    }
}
