package com.uhf.api.cls;

import com.uhf.api.cls.R2000Command;

/* loaded from: classes.dex */
public class R2000DataCmdReturn {
    private byte[] cmdcrc;
    private int command;
    private byte[] data;
    private int datalength;
    private int headercode;
    private boolean onebyte;
    private byte sdata;
    private int status = -1;

    public R2000Command.R2000CmdSatus Status() {
        return R2000Command.R2000CmdSatus.valueOf(this.status);
    }

    public int DataLength() {
        return this.datalength;
    }

    public byte SData() {
        return this.sdata;
    }

    public byte[] Data() {
        return this.data;
    }

    public void GetData(byte[] bArr) {
        this.cmdcrc = new byte[2];
        this.headercode = bArr[0];
        int i = bArr[1];
        this.datalength = i;
        this.command = bArr[2];
        this.status = (bArr[3] << 8) | bArr[4];
        if (i == 1) {
            byte b = bArr[5];
            this.sdata = b;
            this.data = new byte[]{b};
            this.onebyte = true;
        } else if (i > 1) {
            byte[] bArr2 = new byte[i];
            this.data = bArr2;
            System.arraycopy(bArr, 5, bArr2, 0, i);
            this.onebyte = false;
        } else {
            this.sdata = (byte) 0;
            this.data = null;
        }
        byte[] bArr3 = this.cmdcrc;
        bArr3[0] = bArr[bArr.length - 2];
        bArr3[1] = bArr[bArr.length - 1];
    }
}
