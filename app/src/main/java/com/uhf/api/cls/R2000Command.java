package com.uhf.api.cls;

import androidx.core.internal.view.SupportMenu;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.log4j.net.SyslogAppender;

/* loaded from: classes.dex */
public class R2000Command {
    private static int[] crcTable = {0, 4129, 8258, 12387, 16516, 20645, 24774, 28903, 33032, 37161, 41290, 45419, 49548, 53677, 57806, 61935};
    public static FileInputStream fis = null;
    public static final byte headerCode = -1;
    public static InputStream is;
    public static OutputStream os;
    public FileOutputStream fos;

    public enum R2000CmdCode {
        RFPwdSet(15),
        FrequencySelect(16),
        CustomFrequencySelect(17),
        SetSession(19),
        SingleTagInventry(33),
        MuliteTagInventry(34),
        WriteTagMem(36),
        ReadTagMem(40),
        FetchTag(41),
        GetRFPower(65),
        GetCurrentFrequency(66),
        GetCurrentSession(67),
        GetVersion(68),
        CarrytoProgram(4),
        LockTag(37),
        KillTag(38),
        SetIO(80),
        GetIOStatus(81),
        AntSet(20),
        AntGet(21),
        WriteFlash(1),
        ReadyWriteFlash(9),
        CheckFlash(8);

        private int value;

        public int Value() {
            return this.value;
        }

        R2000CmdCode(int value) {
            this.value = 0;
            this.value = value;
        }
    }

    public enum R2000CmdSatus {
        Non_Identifiability_CMD(SyslogAppender.LOG_LOCAL1),
        OK(0),
        Error_Or_Failed(255),
        NonTag(1),
        RSSI_High(255),
        OptionCodeError(2),
        AntSetError(254),
        PWD_ERR_OR_TAG_NO_RESPONE(80),
        TagNonsupportedOrLowPwr(32),
        MemOverranging(33),
        TagLocked(34),
        TagOpFailed(48),
        PswZeroError(81),
        ModuleFatalError(238),
        Temperatrue_High(253);

        private int value;

        R2000CmdSatus(int value) {
            this.value = 0;
            this.value = value;
        }

        public static R2000CmdSatus valueOf(int value) {
            if (value == 0) {
                return OK;
            }
            if (value == 1) {
                return NonTag;
            }
            if (value == 2) {
                return OptionCodeError;
            }
            if (value == 48) {
                return TagOpFailed;
            }
            if (value == 136) {
                return Non_Identifiability_CMD;
            }
            if (value == 238) {
                return ModuleFatalError;
            }
            if (value == 251) {
                return Error_Or_Failed;
            }
            if (value == 80) {
                return PWD_ERR_OR_TAG_NO_RESPONE;
            }
            if (value == 81) {
                return PswZeroError;
            }
            switch (value) {
                case 32:
                    return TagNonsupportedOrLowPwr;
                case 33:
                    return MemOverranging;
                case 34:
                    return TagLocked;
                default:
                    switch (value) {
                        case 253:
                            return Temperatrue_High;
                        case 254:
                            return AntSetError;
                        case 255:
                            return RSSI_High;
                        default:
                            return OK;
                    }
            }
        }
    }

    public static byte[] Get_Build_ToOp_Cmds(R2000CmdCode Rcc) {
        byte[] br2000cmd = new byte[5];
        br2000cmd[0] = -1;
        br2000cmd[1] = 0;
        br2000cmd[2] = (byte) Rcc.value;
        byte[] crc = calcCRC(br2000cmd, 1, br2000cmd[1] + 4);
        br2000cmd[br2000cmd.length - 2] = crc[0];
        br2000cmd[br2000cmd.length - 1] = crc[1];
        return br2000cmd;
    }

    public static byte[] Get_Build_ToOp_Cmds(R2000CmdCode Rcc, byte[] data) {
        byte[] br2000cmd = new byte[data.length + 5];
        br2000cmd[0] = -1;
        br2000cmd[1] = (byte) data.length;
        br2000cmd[2] = (byte) Rcc.value;
        for (int i = 0; i < data.length; i++) {
            br2000cmd[i + 3] = data[i];
        }
        int i2 = br2000cmd[1];
        byte[] crc = calcCRC(br2000cmd, 1, i2 + 4);
        br2000cmd[br2000cmd.length - 2] = crc[0];
        br2000cmd[br2000cmd.length - 1] = crc[1];
        return br2000cmd;
    }

    public int getError(byte[] data) {
        byte error = data[3];
        byte b = data[1];
        byte b2 = data[2];
        return error;
    }

    public static byte[] calcCRC(byte[] message, int offset, int length) {
        int crc = SupportMenu.USER_MASK;
        for (int i = offset; i < offset + length; i++) {
            int i2 = (crc << 4) | ((message[i] >> 4) & 15);
            int[] iArr = crcTable;
            int crc2 = (i2 ^ iArr[crc >> 12]) & SupportMenu.USER_MASK;
            crc = ((((message[i] >> 0) & 15) | (crc2 << 4)) ^ iArr[crc2 >> 12]) & SupportMenu.USER_MASK;
        }
        int i3 = (short) crc;
        byte[] bcrc = {(byte) ((65280 & i3) >> 8), (byte) (i3 & 255)};
        return bcrc;
    }

    public static byte[] getResp(byte opcode) throws Exception {
        byte[] CmdBuf = new byte[1000];
        int doct = 0;
        while (is.available() < 1) {
            Thread.sleep(20L);
            doct++;
            if (doct >= 250) {
                break;
            }
        }
        int isr = is.read(CmdBuf, 0, 5);
        if (isr < 5) {
            is.read(CmdBuf, 0 + isr, 5 - isr);
        }
        int startpos = 0;
        int i = 0;
        while (true) {
            if (i >= 3) {
                break;
            }
            if (CmdBuf[i] != -1) {
                i++;
            } else {
                startpos = i;
                break;
            }
        }
        int i2 = CmdBuf[0];
        if (i2 != -1) {
            Thread.sleep(1500L);
            throw new Exception("interal_Msg_Format_err");
        }
        if (CmdBuf[startpos + 2] != opcode) {
            Thread.sleep(1500L);
            throw new Exception("opcode_err");
        }
        int pos = 0 + 5;
        int doct2 = 0;
        while (is.available() < 1) {
            Thread.sleep(20L);
            doct2++;
            if (doct2 >= 250) {
                break;
            }
        }
        int isr2 = 0;
        if (CmdBuf[startpos + 2] == 2) {
            int doct3 = 0;
            while (isr2 < (CmdBuf[startpos + 1] * 4) + 2) {
                isr2 += is.read(CmdBuf, pos + isr2, ((CmdBuf[startpos + 1] * 4) + 2) - isr2);
                doct3++;
                if (doct3 >= 5) {
                    break;
                }
            }
        } else {
            int doct4 = 0;
            while (isr2 < CmdBuf[startpos + 1] + 2) {
                isr2 += is.read(CmdBuf, pos + isr2, (CmdBuf[startpos + 1] + 2) - isr2);
                doct4++;
                if (doct4 >= 5) {
                    break;
                }
            }
        }
        byte[] getdata = new byte[CmdBuf[startpos + 1] + 7];
        System.arraycopy(CmdBuf, startpos, getdata, 0, getdata.length);
        byte[] bArr = new byte[2];
        byte[] inputBufferNoCRC = new byte[getdata.length - 2];
        System.arraycopy(getdata, 0, inputBufferNoCRC, 0, inputBufferNoCRC.length);
        byte[] returnCRC = calcCRC(inputBufferNoCRC, 1, inputBufferNoCRC[1] + 4);
        if (getdata[getdata.length - 2] != returnCRC[0] || getdata[getdata.length - 1] != returnCRC[1]) {
            try {
                Thread.sleep(1500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            throw new Exception("MSG_CRC_ERROR");
        }
        int err = new R2000Command().getError(getdata);
        if (err != 0) {
            throw new Exception("ERROR:" + String.valueOf(err));
        }
        return getdata;
    }

    private static byte[] SendAndRecvSlMsg(byte[] data) throws Exception {
        os.write(data, 0, data.length);
        byte[] resbytes = getResp(data[2]);
        return resbytes;
    }

    public static byte[] Build_ToOp_Cmds(R2000CmdCode Rcc) throws Exception {
        byte[] br2000cmd = new byte[5];
        br2000cmd[0] = -1;
        br2000cmd[1] = 0;
        br2000cmd[2] = (byte) Rcc.Value();
        byte[] crc = calcCRC(br2000cmd, 1, (br2000cmd[1] & 255) + 2);
        br2000cmd[br2000cmd.length - 2] = crc[0];
        br2000cmd[br2000cmd.length - 1] = crc[1];
        byte[] databack = SendAndRecvSlMsg(br2000cmd);
        return databack;
    }

    public static byte[] Build_ToOp_Cmds(R2000CmdCode Rcc, byte[] data) throws Exception {
        byte[] br2000cmd = new byte[data.length + 5];
        br2000cmd[0] = -1;
        br2000cmd[1] = (byte) data.length;
        br2000cmd[2] = (byte) Rcc.Value();
        for (int i = 0; i < data.length; i++) {
            br2000cmd[i + 3] = data[i];
        }
        int i2 = br2000cmd[1];
        byte[] crc = calcCRC(br2000cmd, 1, (i2 & 255) + 2);
        br2000cmd[br2000cmd.length - 2] = crc[0];
        br2000cmd[br2000cmd.length - 1] = crc[1];
        byte[] backdata = SendAndRecvSlMsg(br2000cmd);
        return backdata;
    }

    /* JADX WARN: Can't wrap try/catch for region: R(14:3|(12:5|8|9|10|12|(6:15|16|17|(2:19|(2:21|22)(1:24))(2:25|(2:27|28)(3:29|30|31))|23|13)|36|37|(2:39|(2:41|42))|43|(1:45)|(1:47)(2:48|49))|54|8|9|10|12|(1:13)|36|37|(0)|43|(0)|(0)(0)) */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x0061, code lost:
    
        java.lang.Thread.sleep(1000);
        r0.GetData(Build_ToOp_Cmds(com.uhf.api.cls.R2000Command.R2000CmdCode.ReadyWriteFlash));
     */
    /* JADX WARN: Removed duplicated region for block: B:15:0x0072  */
    /* JADX WARN: Removed duplicated region for block: B:39:0x00c8  */
    /* JADX WARN: Removed duplicated region for block: B:45:0x00ef  */
    /* JADX WARN: Removed duplicated region for block: B:47:0x00fc A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:48:0x00fd  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static void Updatebyserial(java.lang.String r16) throws java.lang.Exception {
        /*
            Method dump skipped, instructions count: 347
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.uhf.api.cls.R2000Command.Updatebyserial(java.lang.String):void");
    }
}
