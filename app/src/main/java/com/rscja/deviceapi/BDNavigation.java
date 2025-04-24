package com.rscja.deviceapi;

import android.util.Log;
import com.rscja.deviceapi.entity.BDLocation;
import com.rscja.deviceapi.entity.SatelliteEntity;
import com.rscja.deviceapi.exception.ConfigurationException;
import com.rscja.utility.StringUtility;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/* loaded from: classes.dex */
public class BDNavigation {
    private static final String a = BDNavigation.class.getSimpleName();
    private static BDNavigation b = null;
    private FileInputStream c;
    private FileOutputStream d;
    private BDLocationListener e;
    private BDStatusListener f;
    private a g;
    private Date m;
    private long q;
    private BDProviderEnum u;
    private BDLocation h = null;
    private int i = 0;
    private int j = 0;
    private int k = 0;
    private int l = 0;
    private ArrayList<SatelliteEntity> n = new ArrayList<>();
    private int o = 0;
    private int p = 0;
    private int r = -1;
    private int s = 0;
    private String t = "";
    protected com.rscja.deviceapi.a config = com.rscja.deviceapi.a.h();

    public interface BDLocationListener {
        void onDataResult(String str);

        void onLocationChanged(BDLocation bDLocation);
    }

    public enum BDProviderEnum {
        GPS,
        BD,
        GPSandBD;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static BDProviderEnum[] valuesCustom() {
            BDProviderEnum[] valuesCustom = values();
            int length = valuesCustom.length;
            BDProviderEnum[] bDProviderEnumArr = new BDProviderEnum[length];
            System.arraycopy(valuesCustom, 0, bDProviderEnumArr, 0, length);
            return bDProviderEnumArr;
        }
    }

    public enum BDStartModeEnum {
        COLD,
        WARM,
        HOT;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static BDStartModeEnum[] valuesCustom() {
            BDStartModeEnum[] valuesCustom = values();
            int length = valuesCustom.length;
            BDStartModeEnum[] bDStartModeEnumArr = new BDStartModeEnum[length];
            System.arraycopy(valuesCustom, 0, bDStartModeEnumArr, 0, length);
            return bDStartModeEnumArr;
        }
    }

    public interface BDStatusListener {
        void onBDSatelliteChanged(ArrayList<SatelliteEntity> arrayList);

        void onBDSatelliteFIX(int i);

        void onBDSatelliteLocating();

        void onBDSatelliteUsedChanged(int i);

        void onBDSatelliteViewChanged(int i);
    }

    protected BDNavigation() throws ConfigurationException {
    }

    public Date getLastUTCDateTime() {
        return this.m;
    }

    public int getLastSatelliteUCount() {
        return this.p;
    }

    public int getLastsatelliteVCount() {
        return this.l;
    }

    public int getTimeToFirstFix() {
        return this.r;
    }

    public void addBDStatusListener(BDStatusListener listener) {
        if (this.c == null || this.d == null || listener == null) {
            return;
        }
        this.f = listener;
    }

    public static synchronized BDNavigation getInstance() throws ConfigurationException {
        BDNavigation bDNavigation;
        synchronized (BDNavigation.class) {
            if (b == null) {
                b = new BDNavigation();
            }
            bDNavigation = b;
        }
        return bDNavigation;
    }

    public BDLocation getLastLocation() {
        return this.h;
    }

    public synchronized boolean open() throws SecurityException, IOException {
        int bdOn = DeviceAPI.a().bdOn(this.config.i());
        if (bdOn > 0) {
            File file = new File(this.config.j());
            int k = this.config.k();
            if (!file.canRead() || !file.canWrite()) {
                try {
                    Process exec = Runtime.getRuntime().exec("/system/bin/su");
                    exec.getOutputStream().write(("chmod 666 " + file.getAbsolutePath() + "\nexit\n").getBytes());
                    if (exec.waitFor() != 0 || !file.canRead() || !file.canWrite()) {
                        throw new SecurityException();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new SecurityException();
                }
            }
            DeviceAPI.a().mFd = DeviceAPI.a().spOpen(file.getAbsolutePath(), k, 0);
            if (DeviceAPI.a().mFd == null) {
                Log.e(a, "native open returns null");
                throw new IOException();
            }
            this.c = new FileInputStream(DeviceAPI.a().mFd);
            this.d = new FileOutputStream(DeviceAPI.a().mFd);
            try {
                Thread.sleep(300L);
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
            return true;
        }
        Log.e(a, "open() err:" + bdOn);
        return false;
    }

    public synchronized boolean close() {
        int bdOff = DeviceAPI.a().bdOff(this.config.i());
        a aVar = this.g;
        if (aVar != null) {
            aVar.interrupt();
            this.g = null;
        }
        try {
            FileInputStream fileInputStream = this.c;
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            FileOutputStream fileOutputStream = this.d;
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (DeviceAPI.a().mFd != null) {
            DeviceAPI.a().spClose();
        }
        if (bdOff > 0) {
            return true;
        }
        Log.e(a, "close() err:" + bdOff);
        return false;
    }

    private class a extends Thread {
        private long a;

        private a(long j) {
            this.a = 0L;
            BDNavigation.this.q = System.currentTimeMillis();
            this.a = j;
            if (BDNavigation.this.f != null) {
                BDNavigation.this.f.onBDSatelliteLocating();
            }
        }

        /* synthetic */ a(BDNavigation bDNavigation, long j, byte b) {
            this(1000L);
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public final void run() {
            super.run();
            while (!isInterrupted()) {
                try {
                    byte[] bArr = new byte[256];
                    if (BDNavigation.this.c != null) {
                        int read = BDNavigation.this.c.read(bArr);
                        if (read > 0) {
                            BDNavigation.this.a(bArr, read);
                        }
                        try {
                            sleep(this.a);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        return;
                    }
                } catch (IOException e2) {
                    e2.printStackTrace();
                    return;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void a(byte[] bArr, int i) {
        a(new String(bArr, 0, i));
    }

    /* JADX WARN: Code restructure failed: missing block: B:55:0x02c5, code lost:
    
        r0 = r25.f;
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x02c7, code lost:
    
        if (r0 == null) goto L157;
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x02cc, code lost:
    
        if (r25.r != (-1)) goto L134;
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x02ce, code lost:
    
        r0.onBDSatelliteLocating();
     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x02d2, code lost:
    
        r2 = r25.s + 1;
        r25.s = r2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x02da, code lost:
    
        if (r2 <= 50) goto L157;
     */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x02dc, code lost:
    
        r0.onBDSatelliteLocating();
        r25.s = 0;
     */
    /* JADX WARN: Removed duplicated region for block: B:32:0x00d5  */
    /* JADX WARN: Removed duplicated region for block: B:48:0x014f  */
    /* JADX WARN: Removed duplicated region for block: B:96:0x02c3 A[ADDED_TO_REGION, EDGE_INSN: B:96:0x02c3->B:54:0x02c3 BREAK  A[LOOP:0: B:9:0x0038->B:51:0x02bd], SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void a(java.lang.String r26) {
        /*
            Method dump skipped, instructions count: 825
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.rscja.deviceapi.BDNavigation.a(java.lang.String):void");
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0048, code lost:
    
        if (r5 < 33) goto L23;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private synchronized void a(java.lang.String[] r8, int r9) {
        /*
            r7 = this;
            monitor-enter(r7)
            java.lang.String r0 = com.rscja.deviceapi.BDNavigation.a     // Catch: java.lang.Throwable -> L78
            java.lang.String r1 = "proccSatelliteList()"
            android.util.Log.i(r0, r1)     // Catch: java.lang.Throwable -> L78
            r0 = 1
            r1 = 1
        Lb:
            r2 = 5
            r3 = 0
            if (r1 < r2) goto L37
            java.util.ArrayList<com.rscja.deviceapi.entity.SatelliteEntity> r8 = r7.n     // Catch: java.lang.Throwable -> L78
            int r8 = r8.size()     // Catch: java.lang.Throwable -> L78
            int r8 = r8 - r9
            if (r8 <= 0) goto L2c
        L19:
            if (r3 < r8) goto L1c
            goto L2c
        L1c:
            java.util.ArrayList<com.rscja.deviceapi.entity.SatelliteEntity> r9 = r7.n     // Catch: java.lang.Throwable -> L78
            int r9 = r9.size()     // Catch: java.lang.Throwable -> L78
            if (r3 >= r9) goto L2c
            java.util.ArrayList<com.rscja.deviceapi.entity.SatelliteEntity> r9 = r7.n     // Catch: java.lang.Throwable -> L78
            r9.remove(r3)     // Catch: java.lang.Throwable -> L78
            int r3 = r3 + 1
            goto L19
        L2c:
            com.rscja.deviceapi.BDNavigation$BDStatusListener r8 = r7.f     // Catch: java.lang.Throwable -> L78
            if (r8 == 0) goto L35
            java.util.ArrayList<com.rscja.deviceapi.entity.SatelliteEntity> r9 = r7.n     // Catch: java.lang.Throwable -> L78
            r8.onBDSatelliteChanged(r9)     // Catch: java.lang.Throwable -> L78
        L35:
            monitor-exit(r7)
            return
        L37:
            int r2 = r1 << 2
            r4 = r8[r2]     // Catch: java.lang.Throwable -> L78
            int r2 = r2 + 3
            r2 = r8[r2]     // Catch: java.lang.Throwable -> L78
            double r5 = b(r4)     // Catch: java.lang.Throwable -> L78
            int r5 = (int) r5     // Catch: java.lang.Throwable -> L78
            if (r5 <= 0) goto L4c
            r6 = 33
            if (r5 >= r6) goto L4c
        L4a:
            r3 = 1
            goto L55
        L4c:
            r6 = 160(0xa0, float:2.24E-43)
            if (r5 <= r6) goto L55
            r6 = 198(0xc6, float:2.77E-43)
            if (r5 >= r6) goto L55
            goto L4a
        L55:
            if (r3 == 0) goto L75
            com.rscja.deviceapi.entity.SatelliteEntity r3 = new com.rscja.deviceapi.entity.SatelliteEntity     // Catch: java.lang.Throwable -> L78
            r3.<init>(r4, r2)     // Catch: java.lang.Throwable -> L78
            java.util.ArrayList<com.rscja.deviceapi.entity.SatelliteEntity> r4 = r7.n     // Catch: java.lang.Throwable -> L78
            int r4 = r4.indexOf(r3)     // Catch: java.lang.Throwable -> L78
            if (r4 < 0) goto L70
            java.util.ArrayList<com.rscja.deviceapi.entity.SatelliteEntity> r3 = r7.n     // Catch: java.lang.Throwable -> L78
            java.lang.Object r3 = r3.get(r4)     // Catch: java.lang.Throwable -> L78
            com.rscja.deviceapi.entity.SatelliteEntity r3 = (com.rscja.deviceapi.entity.SatelliteEntity) r3     // Catch: java.lang.Throwable -> L78
            r3.setSignal(r2)     // Catch: java.lang.Throwable -> L78
            goto L75
        L70:
            java.util.ArrayList<com.rscja.deviceapi.entity.SatelliteEntity> r2 = r7.n     // Catch: java.lang.Throwable -> L78
            r2.add(r3)     // Catch: java.lang.Throwable -> L78
        L75:
            int r1 = r1 + 1
            goto Lb
        L78:
            r8 = move-exception
            monitor-exit(r7)
            goto L7c
        L7b:
            throw r8
        L7c:
            goto L7b
        */
        throw new UnsupportedOperationException("Method not decompiled: com.rscja.deviceapi.BDNavigation.a(java.lang.String[], int):void");
    }

    private static double b(String str) {
        if (StringUtility.isEmpty(str) || !StringUtility.isNum(str) || str.trim().equals("-")) {
            return 0.0d;
        }
        return Double.parseDouble(str.trim());
    }

    public void addBDLocationListener(BDProviderEnum provider, BDLocationListener listener) {
        if (this.c == null || this.d == null || listener == null) {
            return;
        }
        this.e = listener;
        changeBDProvider(provider);
    }

    public void changeBDProvider(BDProviderEnum provider) {
        String str;
        if (this.c == null || this.d == null) {
            return;
        }
        if (provider == BDProviderEnum.GPS) {
            str = "h01";
        } else {
            str = provider == BDProviderEnum.BD ? "h10" : "h11";
        }
        this.u = provider;
        Log.i(a, "changeBDProvider() snavSys:" + str);
        try {
            this.d.write(new String("$CFGSYS," + str + "\r\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.g = new a(this, 1000L, (byte) 0);
        a();
        this.g.start();
    }

    public void changeBDStartMode(BDStartModeEnum mode) {
        String str;
        if (mode == BDStartModeEnum.WARM) {
            str = "h01";
        } else {
            str = mode == BDStartModeEnum.COLD ? "h9D" : "h00";
        }
        try {
            this.d.write(new String("$RESET,0," + str + "\r\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.g = new a(this, 1000L, (byte) 0);
        a();
        this.g.start();
    }

    private void a() {
        this.n.clear();
        this.j = 0;
        this.k = 0;
        this.l = 0;
        this.o = 0;
        this.q = System.currentTimeMillis();
        this.r = -1;
        BDStatusListener bDStatusListener = this.f;
        if (bDStatusListener != null) {
            bDStatusListener.onBDSatelliteLocating();
            this.f.onBDSatelliteChanged(this.n);
        }
        int i = this.i;
        if (i != this.l) {
            this.l = i;
            BDStatusListener bDStatusListener2 = this.f;
            if (bDStatusListener2 != null) {
                bDStatusListener2.onBDSatelliteViewChanged(i);
            }
        }
        int i2 = this.o;
        if (i2 != this.p) {
            this.p = i2;
            BDStatusListener bDStatusListener3 = this.f;
            if (bDStatusListener3 != null) {
                bDStatusListener3.onBDSatelliteUsedChanged(i2);
            }
        }
        BDLocationListener bDLocationListener = this.e;
        if (bDLocationListener != null) {
            bDLocationListener.onLocationChanged(null);
            this.e.onDataResult("============resetData==============");
        }
    }

    public String getResultData() {
        return this.t;
    }
}
