package com.uhf.api.cls;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class R2000WriteFlashSendData {
    byte[] data;
    int writeaddress;
    byte writeflag;
    int writelength;

    public R2000WriteFlashSendData(int wflag, int address, byte[] flashdata) {
        this.writeaddress = address;
        this.writeflag = (byte) wflag;
        this.writelength = flashdata.length / 4;
        this.data = flashdata;
    }

    public byte[] To_CmdData() {
        List<Byte> arrbytedata = new ArrayList<>();
        arrbytedata.add(Byte.valueOf(this.writeflag));
        int i = this.writeaddress;
        byte[] adds = {(byte) (((-16777216) & i) >> 24), (byte) ((16711680 & i) >> 16), (byte) ((65280 & i) >> 8), (byte) (i & 255)};
        for (byte b : adds) {
            arrbytedata.add(Byte.valueOf(b));
        }
        int i2 = this.writelength;
        arrbytedata.add(Byte.valueOf((byte) i2));
        int i3 = 0;
        while (true) {
            byte[] bArr = this.data;
            if (i3 >= bArr.length) {
                break;
            }
            arrbytedata.add(Byte.valueOf(bArr[i3]));
            i3++;
        }
        int i4 = arrbytedata.size();
        byte[] arrb = new byte[i4];
        for (int i5 = 0; i5 < arrbytedata.size(); i5++) {
            arrb[i5] = arrbytedata.get(i5).byteValue();
        }
        return arrb;
    }
}
