package jxl.write.biff;

import jxl.biff.IntegerHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class PasswordRecord extends WritableRecordData {
    private byte[] data;
    private String password;

    public PasswordRecord(String pw) {
        super(Type.PASSWORD);
        this.password = pw;
        if (pw == null) {
            byte[] bArr = new byte[2];
            this.data = bArr;
            IntegerHelper.getTwoBytes(0, bArr, 0);
            return;
        }
        byte[] passwordBytes = pw.getBytes();
        int passwordHash = 0;
        for (int a = 0; a < passwordBytes.length; a++) {
            int shifted = rotLeft15Bit(passwordBytes[a], a + 1);
            passwordHash ^= shifted;
        }
        int a2 = passwordBytes.length;
        byte[] bArr2 = new byte[2];
        this.data = bArr2;
        IntegerHelper.getTwoBytes((passwordHash ^ a2) ^ 52811, bArr2, 0);
    }

    public PasswordRecord(int ph) {
        super(Type.PASSWORD);
        byte[] bArr = new byte[2];
        this.data = bArr;
        IntegerHelper.getTwoBytes(ph, bArr, 0);
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        return this.data;
    }

    private int rotLeft15Bit(int val, int rotate) {
        int val2 = val & 32767;
        while (rotate > 0) {
            if ((val2 & 16384) != 0) {
                val2 = ((val2 << 1) & 32767) + 1;
            } else {
                val2 = (val2 << 1) & 32767;
            }
            rotate--;
        }
        return val2;
    }
}
