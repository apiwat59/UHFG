package com.gg.reader.api.utils;

import androidx.lifecycle.CoroutineLiveDataKt;
import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.protocol.gx.MsgAppReset;
import com.gg.reader.api.protocol.gx.MsgUpgradeApp;
import com.gg.reader.api.protocol.gx.MsgUpgradeBaseband;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

/* loaded from: classes.dex */
public class UpgradeUtils {
    public static void upgradeBase(File binFile, GClient client) throws Exception {
        if (binFile == null || !binFile.exists()) {
            throw new Exception("binFile not found!!!");
        }
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(binFile));
        byte[] buffer = new byte[256];
        long count = 1;
        MsgUpgradeBaseband msg = new MsgUpgradeBaseband();
        msg.setPacketNumber(0L);
        client.sendSynMsg(msg);
        if (msg.getRtCode() != 0) {
            client.sendSynMsg(msg);
        }
        if (msg.getRtCode() == 0) {
            while (bis.read(buffer) != -1) {
                msg.setPacketNumber(Long.valueOf(count));
                msg.setPacketContent(buffer);
                client.sendSynMsg(msg);
                if (msg.getRtCode() == 0 && msg.getPacketNumber().longValue() == count) {
                    msg.setRtCode((byte) -1);
                    count++;
                } else {
                    throw new Exception("upgrade Failure");
                }
            }
            msg.setPacketNumber(Long.valueOf(Long.parseLong("FFFFFFFF", 16)));
            msg.setPacketContent(null);
            client.sendSynMsg(msg);
            if (msg.getRtCode() != 0) {
                throw new Exception("upgrade Failure");
            }
            Thread.sleep(2000L);
            client.sendSynMsg(new MsgAppReset());
            bis.close();
            return;
        }
        throw new Exception("upgrade Failure");
    }

    public static void upgradeApp(File appFile, GClient client) throws Exception {
        if (appFile == null || !appFile.exists()) {
            throw new Exception("binFile not found!!!");
        }
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(appFile));
        byte[] buffer = new byte[256];
        long count = 1;
        MsgUpgradeApp msg = new MsgUpgradeApp();
        msg.setPacketNumber(0L);
        client.sendSynMsg(msg);
        if (msg.getRtCode() != 0) {
            client.sendSynMsg(msg);
        }
        if (msg.getRtCode() == 0) {
            while (bis.read(buffer) != -1) {
                msg.setPacketNumber(Long.valueOf(count));
                msg.setPacketContent(buffer);
                client.sendSynMsg(msg);
                if (msg.getRtCode() == 0 && msg.getPacketNumber().longValue() == count) {
                    msg.setRtCode((byte) -1);
                    count++;
                } else {
                    throw new Exception("upgrade Failure");
                }
            }
            msg.setPacketNumber(Long.valueOf(Long.parseLong("FFFFFFFF", 16)));
            msg.setPacketContent(null);
            client.sendSynMsg(msg);
            if (msg.getRtCode() != 0) {
                throw new Exception("upgrade Failure");
            }
            Thread.sleep(CoroutineLiveDataKt.DEFAULT_TIMEOUT);
            client.sendSynMsg(new MsgAppReset());
            bis.close();
            return;
        }
        throw new Exception("upgrade Failure");
    }
}
