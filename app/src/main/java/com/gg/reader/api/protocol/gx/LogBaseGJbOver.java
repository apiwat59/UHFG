package com.gg.reader.api.protocol.gx;

import java.util.Hashtable;

/* loaded from: classes.dex */
public class LogBaseGJbOver extends Message {
    private String readerSerialNumber;

    public String getReaderSerialNumber() {
        return this.readerSerialNumber;
    }

    public void setReaderSerialNumber(String readerSerialNumber) {
        this.readerSerialNumber = readerSerialNumber;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        if (this.cData != null && this.cData.length > 0) {
            Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.LogBaseGJbOver.1
                {
                    put((byte) 0, "Single operation complete.");
                    put((byte) 1, "Receive stop instruction.");
                    put((byte) 2, "A hardware failure causes an interrupt.");
                }
            };
            if (this.cData != null && this.cData.length == 1) {
                setRtCode(this.cData[0]);
                if (dicErrorMsg.containsKey(Byte.valueOf(this.cData[0]))) {
                    setRtMsg(dicErrorMsg.get(Byte.valueOf(this.cData[0])));
                }
            }
        }
    }

    public String toString() {
        return "LogBaseGJbOver{readerSerialNumber='" + this.readerSerialNumber + "'}";
    }
}
