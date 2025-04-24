package com.gg.reader.api.utils;

import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.protocol.gx.MsgAppImportWhiteList;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

/* loaded from: classes.dex */
public class WhiteListUtils {
    public static void importData(File dbFile, GClient client) throws Exception {
        if (dbFile != null && !dbFile.exists()) {
            throw new Exception("dbFile not found!!!");
        }
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(dbFile));
        byte[] buffer = new byte[256];
        long count = 1;
        MsgAppImportWhiteList msg = new MsgAppImportWhiteList();
        msg.setPacketNumber(0L);
        client.sendSynMsg(msg);
        if (msg.getRtCode() == 0) {
            while (bis.read(buffer) != -1) {
                msg.setPacketNumber(Long.valueOf(count));
                msg.setPacketContent(buffer);
                client.sendSynMsg(msg);
                if (msg.getRtCode() == 0 && msg.getPacketNumber().longValue() == count) {
                    msg.setRtCode((byte) -1);
                    count++;
                    try {
                        Thread.sleep(50L);
                    } catch (InterruptedException e) {
                    }
                } else {
                    throw new Exception("Import white list Failure");
                }
            }
            msg.setPacketNumber(Long.valueOf(Long.parseLong("FFFFFFFF", 16)));
            msg.setPacketContent(null);
            client.sendSynMsg(msg);
            if (msg.getRtCode() != 0) {
                throw new Exception("Import white list Failure");
            }
            bis.close();
            return;
        }
        throw new Exception("Import white list Failure");
    }
}
