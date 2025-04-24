package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgAppSetGpo extends Message {
    private int gpo1;
    private int gpo10;
    private int gpo11;
    private int gpo12;
    private int gpo13;
    private int gpo14;
    private int gpo15;
    private int gpo16;
    private int gpo17;
    private int gpo18;
    private int gpo19;
    private int gpo2;
    private int gpo20;
    private int gpo21;
    private int gpo22;
    private int gpo23;
    private int gpo24;
    private int gpo25;
    private int gpo26;
    private int gpo27;
    private int gpo28;
    private int gpo29;
    private int gpo3;
    private int gpo30;
    private int gpo31;
    private int gpo32;
    private int gpo4;
    private int gpo5;
    private int gpo6;
    private int gpo7;
    private int gpo8;
    private int gpo9;

    public MsgAppSetGpo() {
        this.gpo1 = Integer.MAX_VALUE;
        this.gpo2 = Integer.MAX_VALUE;
        this.gpo3 = Integer.MAX_VALUE;
        this.gpo4 = Integer.MAX_VALUE;
        this.gpo5 = Integer.MAX_VALUE;
        this.gpo6 = Integer.MAX_VALUE;
        this.gpo7 = Integer.MAX_VALUE;
        this.gpo8 = Integer.MAX_VALUE;
        this.gpo9 = Integer.MAX_VALUE;
        this.gpo10 = Integer.MAX_VALUE;
        this.gpo11 = Integer.MAX_VALUE;
        this.gpo12 = Integer.MAX_VALUE;
        this.gpo13 = Integer.MAX_VALUE;
        this.gpo14 = Integer.MAX_VALUE;
        this.gpo15 = Integer.MAX_VALUE;
        this.gpo16 = Integer.MAX_VALUE;
        this.gpo17 = Integer.MAX_VALUE;
        this.gpo18 = Integer.MAX_VALUE;
        this.gpo19 = Integer.MAX_VALUE;
        this.gpo20 = Integer.MAX_VALUE;
        this.gpo21 = Integer.MAX_VALUE;
        this.gpo22 = Integer.MAX_VALUE;
        this.gpo23 = Integer.MAX_VALUE;
        this.gpo24 = Integer.MAX_VALUE;
        this.gpo25 = Integer.MAX_VALUE;
        this.gpo26 = Integer.MAX_VALUE;
        this.gpo27 = Integer.MAX_VALUE;
        this.gpo28 = Integer.MAX_VALUE;
        this.gpo29 = Integer.MAX_VALUE;
        this.gpo30 = Integer.MAX_VALUE;
        this.gpo31 = Integer.MAX_VALUE;
        this.gpo32 = Integer.MAX_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 9;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppSetGpo(byte[] data) {
        this();
        if (data == null) {
            return;
        }
        try {
            if (data.length <= 0) {
                return;
            }
            BitBuffer buffer = BitBuffer.wrap(data);
            buffer.position(0);
            while (buffer.position() / 8 < data.length) {
                int pid = buffer.getIntUnsigned(8);
                switch (pid) {
                    case 1:
                        this.gpo1 = buffer.getIntUnsigned(8);
                        break;
                    case 2:
                        this.gpo2 = buffer.getIntUnsigned(8);
                        break;
                    case 3:
                        this.gpo3 = buffer.getIntUnsigned(8);
                        break;
                    case 4:
                        this.gpo4 = buffer.getIntUnsigned(8);
                        break;
                    case 5:
                        this.gpo5 = buffer.getIntUnsigned(8);
                        break;
                    case 6:
                        this.gpo6 = buffer.getIntUnsigned(8);
                        break;
                    case 7:
                        this.gpo7 = buffer.getIntUnsigned(8);
                        break;
                    case 8:
                        this.gpo8 = buffer.getIntUnsigned(8);
                        break;
                    case 9:
                        this.gpo9 = buffer.getIntUnsigned(8);
                        break;
                    case 10:
                        this.gpo10 = buffer.getIntUnsigned(8);
                        break;
                    case 11:
                        this.gpo11 = buffer.getIntUnsigned(8);
                        break;
                    case 12:
                        this.gpo12 = buffer.getIntUnsigned(8);
                        break;
                    case 13:
                        this.gpo13 = buffer.getIntUnsigned(8);
                        break;
                    case 14:
                        this.gpo14 = buffer.getIntUnsigned(8);
                        break;
                    case 15:
                        this.gpo15 = buffer.getIntUnsigned(8);
                        break;
                    case 16:
                        this.gpo16 = buffer.getIntUnsigned(8);
                        break;
                    case 17:
                        this.gpo17 = buffer.getIntUnsigned(8);
                        break;
                    case 18:
                        this.gpo18 = buffer.getIntUnsigned(8);
                        break;
                    case 19:
                        this.gpo19 = buffer.getIntUnsigned(8);
                        break;
                    case 20:
                        this.gpo20 = buffer.getIntUnsigned(8);
                        break;
                    case 21:
                        this.gpo21 = buffer.getIntUnsigned(8);
                        break;
                    case 22:
                        this.gpo22 = buffer.getIntUnsigned(8);
                        break;
                    case 23:
                        this.gpo23 = buffer.getIntUnsigned(8);
                        break;
                    case 24:
                        this.gpo24 = buffer.getIntUnsigned(8);
                        break;
                    case 25:
                        this.gpo25 = buffer.getIntUnsigned(8);
                        break;
                    case 26:
                        this.gpo26 = buffer.getIntUnsigned(8);
                        break;
                    case 27:
                        this.gpo27 = buffer.getIntUnsigned(8);
                        break;
                    case 28:
                        this.gpo28 = buffer.getIntUnsigned(8);
                        break;
                    case 29:
                        this.gpo29 = buffer.getIntUnsigned(8);
                        break;
                    case 30:
                        this.gpo30 = buffer.getIntUnsigned(8);
                        break;
                    case 31:
                        this.gpo31 = buffer.getIntUnsigned(8);
                        break;
                    case 32:
                        this.gpo32 = buffer.getIntUnsigned(8);
                        break;
                }
            }
        } catch (Exception e) {
        }
    }

    public int getGpo1() {
        return this.gpo1;
    }

    public void setGpo1(int gpo1) {
        this.gpo1 = gpo1;
    }

    public int getGpo2() {
        return this.gpo2;
    }

    public void setGpo2(int gpo2) {
        this.gpo2 = gpo2;
    }

    public int getGpo3() {
        return this.gpo3;
    }

    public void setGpo3(int gpo3) {
        this.gpo3 = gpo3;
    }

    public int getGpo4() {
        return this.gpo4;
    }

    public void setGpo4(int gpo4) {
        this.gpo4 = gpo4;
    }

    public int getGpo5() {
        return this.gpo5;
    }

    public void setGpo5(int gpo5) {
        this.gpo5 = gpo5;
    }

    public int getGpo6() {
        return this.gpo6;
    }

    public void setGpo6(int gpo6) {
        this.gpo6 = gpo6;
    }

    public int getGpo7() {
        return this.gpo7;
    }

    public void setGpo7(int gpo7) {
        this.gpo7 = gpo7;
    }

    public int getGpo8() {
        return this.gpo8;
    }

    public void setGpo8(int gpo8) {
        this.gpo8 = gpo8;
    }

    public int getGpo9() {
        return this.gpo9;
    }

    public void setGpo9(int gpo9) {
        this.gpo9 = gpo9;
    }

    public int getGpo10() {
        return this.gpo10;
    }

    public void setGpo10(int gpo10) {
        this.gpo10 = gpo10;
    }

    public int getGpo11() {
        return this.gpo11;
    }

    public void setGpo11(int gpo11) {
        this.gpo11 = gpo11;
    }

    public int getGpo12() {
        return this.gpo12;
    }

    public void setGpo12(int gpo12) {
        this.gpo12 = gpo12;
    }

    public int getGpo13() {
        return this.gpo13;
    }

    public void setGpo13(int gpo13) {
        this.gpo13 = gpo13;
    }

    public int getGpo14() {
        return this.gpo14;
    }

    public void setGpo14(int gpo14) {
        this.gpo14 = gpo14;
    }

    public int getGpo15() {
        return this.gpo15;
    }

    public void setGpo15(int gpo15) {
        this.gpo15 = gpo15;
    }

    public int getGpo16() {
        return this.gpo16;
    }

    public void setGpo16(int gpo16) {
        this.gpo16 = gpo16;
    }

    public int getGpo17() {
        return this.gpo17;
    }

    public void setGpo17(int gpo17) {
        this.gpo17 = gpo17;
    }

    public int getGpo18() {
        return this.gpo18;
    }

    public void setGpo18(int gpo18) {
        this.gpo18 = gpo18;
    }

    public int getGpo19() {
        return this.gpo19;
    }

    public void setGpo19(int gpo19) {
        this.gpo19 = gpo19;
    }

    public int getGpo20() {
        return this.gpo20;
    }

    public void setGpo20(int gpo20) {
        this.gpo20 = gpo20;
    }

    public int getGpo21() {
        return this.gpo21;
    }

    public void setGpo21(int gpo21) {
        this.gpo21 = gpo21;
    }

    public int getGpo22() {
        return this.gpo22;
    }

    public void setGpo22(int gpo22) {
        this.gpo22 = gpo22;
    }

    public int getGpo23() {
        return this.gpo23;
    }

    public void setGpo23(int gpo23) {
        this.gpo23 = gpo23;
    }

    public int getGpo24() {
        return this.gpo24;
    }

    public void setGpo24(int gpo24) {
        this.gpo24 = gpo24;
    }

    public int getGpo25() {
        return this.gpo25;
    }

    public void setGpo25(int gpo25) {
        this.gpo25 = gpo25;
    }

    public int getGpo26() {
        return this.gpo26;
    }

    public void setGpo26(int gpo26) {
        this.gpo26 = gpo26;
    }

    public int getGpo27() {
        return this.gpo27;
    }

    public void setGpo27(int gpo27) {
        this.gpo27 = gpo27;
    }

    public int getGpo28() {
        return this.gpo28;
    }

    public void setGpo28(int gpo28) {
        this.gpo28 = gpo28;
    }

    public int getGpo29() {
        return this.gpo29;
    }

    public void setGpo29(int gpo29) {
        this.gpo29 = gpo29;
    }

    public int getGpo30() {
        return this.gpo30;
    }

    public void setGpo30(int gpo30) {
        this.gpo30 = gpo30;
    }

    public int getGpo31() {
        return this.gpo31;
    }

    public void setGpo31(int gpo31) {
        this.gpo31 = gpo31;
    }

    public int getGpo32() {
        return this.gpo32;
    }

    public void setGpo32(int gpo32) {
        this.gpo32 = gpo32;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        if (Integer.MAX_VALUE != this.gpo1) {
            buffer.putInt(1, 8);
            buffer.putLong(this.gpo1, 8);
        }
        if (Integer.MAX_VALUE != this.gpo2) {
            buffer.putInt(2, 8);
            buffer.putLong(this.gpo2, 8);
        }
        if (Integer.MAX_VALUE != this.gpo3) {
            buffer.putInt(3, 8);
            buffer.putLong(this.gpo3, 8);
        }
        if (Integer.MAX_VALUE != this.gpo4) {
            buffer.putInt(4, 8);
            buffer.putLong(this.gpo4, 8);
        }
        if (Integer.MAX_VALUE != this.gpo5) {
            buffer.putInt(5, 8);
            buffer.putLong(this.gpo5, 8);
        }
        if (Integer.MAX_VALUE != this.gpo6) {
            buffer.putInt(6, 8);
            buffer.putLong(this.gpo6, 8);
        }
        if (Integer.MAX_VALUE != this.gpo7) {
            buffer.putInt(7, 8);
            buffer.putLong(this.gpo7, 8);
        }
        if (Integer.MAX_VALUE != this.gpo8) {
            buffer.putInt(8, 8);
            buffer.putLong(this.gpo8, 8);
        }
        if (Integer.MAX_VALUE != this.gpo9) {
            buffer.putInt(9, 8);
            buffer.putLong(this.gpo9, 8);
        }
        if (Integer.MAX_VALUE != this.gpo10) {
            buffer.putInt(10, 8);
            buffer.putLong(this.gpo10, 8);
        }
        if (Integer.MAX_VALUE != this.gpo11) {
            buffer.putInt(11, 8);
            buffer.putLong(this.gpo11, 8);
        }
        if (Integer.MAX_VALUE != this.gpo12) {
            buffer.putInt(12, 8);
            buffer.putLong(this.gpo12, 8);
        }
        if (Integer.MAX_VALUE != this.gpo13) {
            buffer.putInt(13, 8);
            buffer.putLong(this.gpo13, 8);
        }
        if (Integer.MAX_VALUE != this.gpo14) {
            buffer.putInt(14, 8);
            buffer.putLong(this.gpo14, 8);
        }
        if (Integer.MAX_VALUE != this.gpo15) {
            buffer.putInt(15, 8);
            buffer.putLong(this.gpo15, 8);
        }
        if (Integer.MAX_VALUE != this.gpo16) {
            buffer.putInt(16, 8);
            buffer.putLong(this.gpo16, 8);
        }
        if (Integer.MAX_VALUE != this.gpo17) {
            buffer.putInt(17, 8);
            buffer.putLong(this.gpo17, 8);
        }
        if (Integer.MAX_VALUE != this.gpo18) {
            buffer.putInt(18, 8);
            buffer.putLong(this.gpo18, 8);
        }
        if (Integer.MAX_VALUE != this.gpo19) {
            buffer.putInt(19, 8);
            buffer.putLong(this.gpo19, 8);
        }
        if (Integer.MAX_VALUE != this.gpo20) {
            buffer.putInt(20, 8);
            buffer.putLong(this.gpo20, 8);
        }
        if (Integer.MAX_VALUE != this.gpo21) {
            buffer.putInt(21, 8);
            buffer.putLong(this.gpo21, 8);
        }
        if (Integer.MAX_VALUE != this.gpo22) {
            buffer.putInt(22, 8);
            buffer.putLong(this.gpo22, 8);
        }
        if (Integer.MAX_VALUE != this.gpo23) {
            buffer.putInt(23, 8);
            buffer.putLong(this.gpo23, 8);
        }
        if (Integer.MAX_VALUE != this.gpo24) {
            buffer.putInt(24, 8);
            buffer.putLong(this.gpo24, 8);
        }
        if (Integer.MAX_VALUE != this.gpo25) {
            buffer.putInt(25, 8);
            buffer.putLong(this.gpo25, 8);
        }
        if (Integer.MAX_VALUE != this.gpo26) {
            buffer.putInt(26, 8);
            buffer.putLong(this.gpo26, 8);
        }
        if (Integer.MAX_VALUE != this.gpo27) {
            buffer.putInt(27, 8);
            buffer.putLong(this.gpo27, 8);
        }
        if (Integer.MAX_VALUE != this.gpo28) {
            buffer.putInt(28, 8);
            buffer.putLong(this.gpo28, 8);
        }
        if (Integer.MAX_VALUE != this.gpo29) {
            buffer.putInt(29, 8);
            buffer.putLong(this.gpo29, 8);
        }
        if (Integer.MAX_VALUE != this.gpo30) {
            buffer.putInt(30, 8);
            buffer.putLong(this.gpo30, 8);
        }
        if (Integer.MAX_VALUE != this.gpo31) {
            buffer.putInt(31, 8);
            buffer.putLong(this.gpo31, 8);
        }
        if (Integer.MAX_VALUE != this.gpo32) {
            buffer.putInt(32, 8);
            buffer.putLong(this.gpo32, 8);
        }
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgAppSetGpo.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Port parameter reader hardware is not supported .");
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
