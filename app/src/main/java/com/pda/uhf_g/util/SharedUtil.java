package com.pda.uhf_g.util;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.constraintlayout.core.motion.utils.TypedValues;

/* loaded from: classes.dex */
public class SharedUtil {
    private final SharedPreferences mSharedPreferences;

    public SharedUtil(Context context) {
        this.mSharedPreferences = context.getSharedPreferences("UHF", 0);
    }

    public void saveWorkFreq(int workFreq) {
        SharedPreferences.Editor editor = this.mSharedPreferences.edit();
        editor.putInt("workFreq", workFreq);
        editor.commit();
    }

    public int getWorkFreq() {
        return this.mSharedPreferences.getInt("workFreq", 1);
    }

    public void savePower(int power) {
        SharedPreferences.Editor editor = this.mSharedPreferences.edit();
        editor.putInt("power", power);
        editor.commit();
    }

    public int getPower() {
        return this.mSharedPreferences.getInt("power", 33);
    }

    public void saveSession(int session) {
        SharedPreferences.Editor editor = this.mSharedPreferences.edit();
        editor.putInt("session", session);
        editor.commit();
    }

    public int getSession() {
        return this.mSharedPreferences.getInt("session", 0);
    }

    public void saveQvalue(int qvalue) {
        SharedPreferences.Editor editor = this.mSharedPreferences.edit();
        editor.putInt("qvalue", qvalue);
        editor.commit();
    }

    public int getQvalue() {
        return this.mSharedPreferences.getInt("qvalue", 0);
    }

    public void saveTarget(int target) {
        SharedPreferences.Editor editor = this.mSharedPreferences.edit();
        editor.putInt(TypedValues.AttributesType.S_TARGET, target);
        editor.commit();
    }

    public int getTarget() {
        return this.mSharedPreferences.getInt(TypedValues.AttributesType.S_TARGET, 0);
    }

    public void saveFastId(boolean openFlag) {
        SharedPreferences.Editor editor = this.mSharedPreferences.edit();
        editor.putBoolean("FastId", openFlag);
        editor.apply();
    }

    public boolean getFastId() {
        return this.mSharedPreferences.getBoolean("FastId", false);
    }
}
