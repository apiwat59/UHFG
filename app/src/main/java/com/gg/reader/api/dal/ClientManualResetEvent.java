package com.gg.reader.api.dal;

import com.gg.reader.api.protocol.gx.Message;
import com.gg.reader.api.utils.ManualResetEvent;

/* loaded from: classes.dex */
public class ClientManualResetEvent {
    public Message data;
    public ManualResetEvent evt;

    public ClientManualResetEvent(boolean status) {
        this.evt = new ManualResetEvent(status);
    }
}
