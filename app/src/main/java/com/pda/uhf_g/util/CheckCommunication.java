package com.pda.uhf_g.util;

import com.gg.reader.api.protocol.gx.MsgBaseStop;

/* loaded from: classes.dex */
public class CheckCommunication {
    public static boolean check() {
        MsgBaseStop msg = new MsgBaseStop();
        GlobalClient.getClient().sendSynMsg(msg);
        if (msg.getRtCode() == 0) {
            return true;
        }
        return false;
    }
}
