package com.pda.uhf_g.util;

import android.content.Context;
import android.content.Intent;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class ScanUtil {
    private static WeakReference<Context> sWeakReference;
    private final String ACTION_CLOSE_SCAN;
    private final String ACTION_KEY_SET;
    private final String ACTION_SCAN;
    private final String ACTION_SCAN_CONTINUOUS;
    private final String ACTION_SCAN_END_CHAR;
    private final String ACTION_SCAN_FILTER_BLANK;
    private final String ACTION_SCAN_FILTER_INVISIBLE_CHARS;
    private final String ACTION_SCAN_INIT;
    private final String ACTION_SCAN_INTERVAL;
    private final String ACTION_SCAN_PREFIX;
    private final String ACTION_SCAN_SUFFIX;
    private final String ACTION_SCAN_TIME;
    private final String ACTION_SCAN_VIBERATE;
    private final String ACTION_SCAN_VOICE;
    private final String ACTION_SET_SCAN_MODE;
    private final String ACTION_STOP_SCAN;

    private ScanUtil() {
        this.ACTION_SCAN_INIT = "com.rfid.SCAN_INIT";
        this.ACTION_SET_SCAN_MODE = "com.rfid.SET_SCAN_MODE";
        this.ACTION_SCAN = "com.rfid.SCAN_CMD";
        this.ACTION_STOP_SCAN = "com.rfid.STOP_SCAN";
        this.ACTION_CLOSE_SCAN = "com.rfid.CLOSE_SCAN";
        this.ACTION_SCAN_TIME = "com.rfid.SCAN_TIME";
        this.ACTION_SCAN_VOICE = "com.rfid.SCAN_VOICE";
        this.ACTION_SCAN_VIBERATE = "com.rfid.SCAN_VIBERATE";
        this.ACTION_SCAN_CONTINUOUS = "com.rfid.SCAN_CONTINUOUS";
        this.ACTION_SCAN_INTERVAL = "com.rfid.SCAN_INTERVAL";
        this.ACTION_SCAN_FILTER_BLANK = "com.rfid.SCAN_FILTER_BLANK";
        this.ACTION_SCAN_FILTER_INVISIBLE_CHARS = "com.rfid.SCAN_FILTER_INVISIBLE_CHARS";
        this.ACTION_SCAN_PREFIX = "com.rfid.SCAN_PREFIX";
        this.ACTION_SCAN_SUFFIX = "com.rfid.SCAN_SUFFIX";
        this.ACTION_SCAN_END_CHAR = "com.rfid.SCAN_END_CHAR";
        this.ACTION_KEY_SET = "com.rfid.KEY_SET";
    }

    public static ScanUtil getInstance(Context context) {
        sWeakReference = new WeakReference<>(context);
        return ScanUtilHolder.sScanUtils;
    }

    private static class ScanUtilHolder {
        private static ScanUtil sScanUtils = new ScanUtil();

        private ScanUtilHolder() {
        }
    }

    public void initReader() {
        Intent intent = new Intent("com.rfid.SCAN_INIT");
        sWeakReference.get().sendBroadcast(intent);
    }

    public void setBarcodeSendMode(int barcodeSendMode) {
        Intent intent = new Intent("com.rfid.SET_SCAN_MODE");
        intent.putExtra("mode", barcodeSendMode);
        sWeakReference.get().sendBroadcast(intent);
    }

    public void startScan() {
        Intent intent = new Intent("com.rfid.SCAN_CMD");
        sWeakReference.get().sendBroadcast(intent);
    }

    public void stopScan() {
        Intent intent = new Intent("com.rfid.STOP_SCAN");
        sWeakReference.get().sendBroadcast(intent);
    }

    public void uninitReader() {
        Intent intent = new Intent("com.rfid.CLOSE_SCAN");
        sWeakReference.get().sendBroadcast(intent);
    }

    public void setDecodeTimeout(String timeout) {
        Intent intent = new Intent("com.rfid.SCAN_TIME");
        intent.putExtra("time", timeout);
        sWeakReference.get().sendBroadcast(intent);
    }

    public void setScanVoice(boolean voiceEnable) {
        Intent intent = new Intent("com.rfid.SCAN_VOICE");
        intent.putExtra("sound_play", voiceEnable);
        sWeakReference.get().sendBroadcast(intent);
    }

    public void setScanViberate(boolean viberatEnable) {
        Intent intent = new Intent("com.rfid.SCAN_VIBERATE");
        intent.putExtra("viberate", viberatEnable);
        sWeakReference.get().sendBroadcast(intent);
    }

    public void setScanContinu(boolean continu) {
        Intent intent = new Intent("com.rfid.SCAN_CONTINUOUS");
        intent.putExtra("ContinuousMode", continu);
        sWeakReference.get().sendBroadcast(intent);
    }

    public void setScanContinuInterval(String interval) {
        Intent intent = new Intent("com.rfid.SCAN_INTERVAL");
        intent.putExtra("ContinuousInternal", interval);
        sWeakReference.get().sendBroadcast(intent);
    }

    public void setScanFilterBlank(boolean isFilterBlank) {
        Intent intent = new Intent("com.rfid.SCAN_FILTER_BLANK");
        intent.putExtra("filter_prefix_suffix_blank", isFilterBlank);
        sWeakReference.get().sendBroadcast(intent);
    }

    public void setScanFilterInvisibleChars(boolean isFilterInvisible) {
        Intent intent = new Intent("com.rfid.SCAN_FILTER_INVISIBLE_CHARS");
        intent.putExtra("filter_invisible_chars", isFilterInvisible);
        sWeakReference.get().sendBroadcast(intent);
    }

    public void setScanPrefix(String prefix) {
        Intent intent = new Intent("com.rfid.SCAN_PREFIX");
        intent.putExtra("prefix", prefix);
        sWeakReference.get().sendBroadcast(intent);
    }

    public void setScanSuffix(String suffix) {
        Intent intent = new Intent("com.rfid.SCAN_SUFFIX");
        intent.putExtra("suffix", suffix);
        sWeakReference.get().sendBroadcast(intent);
    }

    public void setScanEndChar(String endChar) {
        Intent intent = new Intent("com.rfid.SCAN_END_CHAR");
        intent.putExtra("endchar", endChar);
        sWeakReference.get().sendBroadcast(intent);
    }

    public void enableScanKey(String... keyValues) {
        Intent intent = new Intent("com.rfid.KEY_SET");
        intent.putExtra("keyValueArray", keyValues);
        for (String value : keyValues) {
            intent.putExtra(value, true);
        }
        sWeakReference.get().sendBroadcast(intent);
    }

    public void disableScanKey(String... keyValues) {
        Intent intent = new Intent("com.rfid.KEY_SET");
        intent.putExtra("keyValueArray", keyValues);
        for (String value : keyValues) {
            intent.putExtra(value, false);
        }
        sWeakReference.get().sendBroadcast(intent);
    }
}
