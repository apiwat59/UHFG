package com.rscja.deviceapi;

import android.os.Build;
import android.util.Log;
import com.rscja.deviceapi.exception.ConfigurationException;

/* loaded from: classes.dex */
final class a {
    private static String a;
    private String b;
    private String c;
    private int d;

    static {
        String lowerCase = Build.MODEL.toLowerCase();
        a = lowerCase;
        if (!lowerCase.toUpperCase().equals("C4000")) {
            String upperCase = Build.DISPLAY.toUpperCase();
            if (upperCase.contains("C4000")) {
                a = "c4000";
            } else if (upperCase.contains("40006577")) {
                a = "c4000";
            } else if (upperCase.contains("40006582")) {
                a = "c40006582";
            }
        }
    }

    public static String a() {
        if (a.equals("i760")) {
            return "C4000";
        }
        return a.toUpperCase();
    }

    public static a b() throws ConfigurationException {
        if (a.equals("c4000")) {
            return new a("C4000", "/dev/ttyMT3", 9600);
        }
        if (a.equals("i760")) {
            return new a("C4000", "/dev/ttyMT3", 9600);
        }
        Log.e("DeviceConfiguration", "builder1DConfiguration() Unknow device");
        throw new ConfigurationException();
    }

    public static a c() throws ConfigurationException {
        if (a.equals("c4000")) {
            return new a("C4000", "/dev/ttyMT3", 9600);
        }
        if (a.equals("i760")) {
            return new a("C4000", "/dev/ttyMT3", 9600);
        }
        Log.e("DeviceConfiguration", "builder1DConfiguration() Unknow device");
        throw new ConfigurationException();
    }

    public static a d() throws ConfigurationException {
        if (a.equals("c4000")) {
            return new a("C4000", "/dev/ttyMT3", 115200);
        }
        if (a.equals("i760")) {
            return new a("C4000", "/dev/ttyMT3", 115200);
        }
        Log.e("DeviceConfiguration", "builderRFIDConfiguration() Unknow device");
        throw new ConfigurationException();
    }

    public static a e() throws ConfigurationException {
        if (a.equals("c4000")) {
            return new a("C4000", "/dev/ttyMT3", 115200);
        }
        if (a.equals("i760")) {
            return new a("C4000", "/dev/ttyMT3", 115200);
        }
        Log.e("DeviceConfiguration", "builderUHFConfiguration() Unknow device");
        throw new ConfigurationException();
    }

    public static a f() throws ConfigurationException {
        if (a.equals("c4000")) {
            return new a("C4000", "/dev/ttyMT0", 57600);
        }
        if (a.equals("i760")) {
            return new a("C4000", "/dev/ttyMT0", 57600);
        }
        Log.e("DeviceConfiguration", "builderFingerprintConfiguration() Unknow device");
        throw new ConfigurationException();
    }

    public static a g() throws ConfigurationException {
        if (a.equals("c4000")) {
            return new a("C4000", "/dev/ttyMT3", 115200);
        }
        if (a.equals("i760")) {
            return new a("C4000", "/dev/ttyMT3", 115200);
        }
        Log.e("DeviceConfiguration", "builderLFConfiguration() Unknow device");
        throw new ConfigurationException();
    }

    public static a h() throws ConfigurationException {
        if (a.equals("c4000")) {
            return new a("C4000", "/dev/ttyMT1", 9600);
        }
        if (a.equals("i760")) {
            return new a("C4000", "/dev/ttyMT1", 9600);
        }
        Log.e("DeviceConfiguration", "builderBDConfiguration() Unknow device");
        throw new ConfigurationException();
    }

    private a(String str, String str2, int i) {
        this.b = str;
        this.c = str2;
        this.d = i;
    }

    public final String i() {
        return this.b;
    }

    public final String j() {
        return this.c;
    }

    public final int k() {
        return this.d;
    }
}
