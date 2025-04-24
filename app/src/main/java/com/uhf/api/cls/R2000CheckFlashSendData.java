package com.uhf.api.cls;

import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class R2000CheckFlashSendData {
    byte[] crc;
    int data1;
    int data2;
    int data3;
    int data4;
    int paddress;
    int plength;

    public R2000CheckFlashSendData(int address, byte[] data) {
        this.plength = data.length / 4;
        this.paddress = address;
        for (int i = 0; i < data.length; i++) {
            if (i % 4 == 0) {
                this.data1 += data[i];
            } else if (i % 4 == 1) {
                this.data2 += data[i];
            } else if (i % 4 == 2) {
                this.data3 += data[i];
            } else if (i % 4 == 3) {
                this.data4 += data[i];
            }
        }
        this.crc = new byte[]{(byte) (this.data1 & 255), (byte) (this.data2 & 255), (byte) (this.data3 & 255), (byte) (this.data4 & 255)};
    }

    public byte[] To_CmdData() {
        List<Byte> arrbytedata = new ArrayList<>();
        int i = this.paddress;
        byte[] adds = {(byte) ((i & ViewCompat.MEASURED_STATE_MASK) >> 24), (byte) ((i & 16711680) >> 16), (byte) ((i & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8), (byte) (i & 255)};
        int i2 = this.plength;
        byte[] len = {(byte) (((-16777216) & i2) >> 24), (byte) ((i2 & 16711680) >> 16), (byte) ((i2 & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8), (byte) (i2 & 255)};
        for (byte b : adds) {
            arrbytedata.add(Byte.valueOf(b));
        }
        for (byte b2 : len) {
            arrbytedata.add(Byte.valueOf(b2));
        }
        int i3 = 0;
        while (true) {
            byte[] bArr = this.crc;
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
