package com.pda.uhf_g.util;

import com.gg.reader.api.dal.GClient;

/* loaded from: classes.dex */
public class GlobalClient {
    private static GClient instance;

    static {
        instance = null;
        try {
            instance = new GClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static GClient getClient() {
        return instance;
    }
}
