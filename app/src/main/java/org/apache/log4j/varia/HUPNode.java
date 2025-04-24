package org.apache.log4j.varia;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import org.apache.log4j.helpers.LogLog;

/* compiled from: ExternallyRolledFileAppender.java */
/* loaded from: classes.dex */
class HUPNode implements Runnable {
    DataInputStream dis;
    DataOutputStream dos;
    ExternallyRolledFileAppender er;
    Socket socket;

    public HUPNode(Socket socket, ExternallyRolledFileAppender er) {
        this.socket = socket;
        this.er = er;
        try {
            this.dis = new DataInputStream(socket.getInputStream());
            this.dos = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            String line = this.dis.readUTF();
            LogLog.debug("Got external roll over signal.");
            if (ExternallyRolledFileAppender.ROLL_OVER.equals(line)) {
                synchronized (this.er) {
                    this.er.rollOver();
                }
                this.dos.writeUTF(ExternallyRolledFileAppender.OK);
            } else {
                this.dos.writeUTF("Expecting [RollOver] string.");
            }
            this.dos.close();
        } catch (Exception e) {
            LogLog.error("Unexpected exception. Exiting HUPNode.", e);
        }
    }
}
