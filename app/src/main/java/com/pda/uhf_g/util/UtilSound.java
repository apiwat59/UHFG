package com.pda.uhf_g.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.SystemClock;
import com.pda.uhf_g.R;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class UtilSound {
    public static Context context;
    public static SoundPool sp;
    public static Map<Integer, Integer> suondMap;
    private static long time = 0;
    private static long currentTime = 0;
    private static long lastTime = SystemClock.elapsedRealtime();

    public static void initSoundPool(Context context2) {
        context = context2;
        sp = new SoundPool(1, 3, 100);
        HashMap hashMap = new HashMap();
        suondMap = hashMap;
        hashMap.put(1, Integer.valueOf(sp.load(context2, R.raw.barcodebeep, 1)));
        suondMap.put(2, Integer.valueOf(sp.load(context2, R.raw.beep, 1)));
        suondMap.put(3, Integer.valueOf(sp.load(context2, R.raw.beeps, 1)));
    }

    public static void play(int sound, int number) {
        if (System.currentTimeMillis() - time > 30) {
            AudioManager am = (AudioManager) context.getSystemService("audio");
            float audioMaxVolume = am.getStreamMaxVolume(3);
            float audioCurrentVolume = am.getStreamVolume(3);
            float f = audioCurrentVolume / audioMaxVolume;
            sp.play(3, 1.0f, 1.0f, 0, 0, 2.0f);
            time = System.currentTimeMillis();
        }
    }

    public static void voiceTips(int type, int size, boolean asyncFlag) {
        if (type == 0 && !asyncFlag) {
            for (int i = 0; i < size; i++) {
                long elapsedRealtime = SystemClock.elapsedRealtime();
                currentTime = elapsedRealtime;
                if (elapsedRealtime - lastTime < 40) {
                    try {
                        Thread.sleep(10L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    play(1, 0);
                    lastTime = currentTime;
                }
            }
            return;
        }
        long elapsedRealtime2 = SystemClock.elapsedRealtime();
        currentTime = elapsedRealtime2;
        long l = elapsedRealtime2 - lastTime;
        if (l < 40) {
            return;
        }
        play(1, 0);
        lastTime = currentTime;
    }
}
