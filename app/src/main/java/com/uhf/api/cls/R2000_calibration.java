package com.uhf.api.cls;

import androidx.core.internal.view.SupportMenu;
import androidx.core.view.MotionEventCompat;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class R2000_calibration {
    private static int[] crcTable = {0, 4129, 8258, 12387, 16516, 20645, 24774, 28903, 33032, 37161, 41290, 45419, 49548, 53677, 57806, 61935};
    private byte[] cmdname;
    final short TAG_METADATA_NONE = 0;
    final short TAG_METADATA_READCOUNT = 1;
    final short TAG_METADATA_RSSI = 2;
    final short TAG_METADATA_ANTENNAID = 4;
    final short TAG_METADATA_FREQUENCY = 8;
    final short TAG_METADATA_TIMESTAMP = 16;
    final short TAG_METADATA_PHASE = 32;
    final short TAG_METADATA_PROTOCOL = 64;
    final short TAG_EMBEDED_DATA = 128;
    private final byte HeaderC = -1;
    private final byte AA = -86;
    private final byte bb = -69;

    public R2000_calibration() {
        this.cmdname = null;
        this.cmdname = new byte[]{77, 111, 100, 117, 108, 101, 116, 101, 99, 104};
    }

    public enum Region {
        UNSPEC(0),
        NA(1),
        EU(2),
        KR(3),
        IN(4),
        JP(5),
        PRC(6),
        EU2(7),
        EU3(8),
        KR2(9),
        PRC2(10),
        OPEN(255);

        private int value;

        Region(int value) {
            this.value = 0;
            this.value = value;
        }
    }

    public enum R2000cmd {
        OEMformat(43562),
        OEMwrite(43522),
        OEMread(43523),
        ENGTEST(43524),
        GROSSGAINSCAL(43547),
        DCOFFSETCAL(43556),
        SetTestFre(43559),
        PABIASCAL(43564),
        writeMAC(43566),
        readMAC(43567),
        carrier(43569),
        ReturnLossTest(43594),
        Regop(43595),
        S2TA(43597);

        private int value;

        R2000cmd(int value) {
            this.value = 0;
            this.value = value;
        }
    }

    public static byte[] calcCrc(byte[] message, int offset, int length) {
        int crc = SupportMenu.USER_MASK;
        for (int i = offset; i < offset + length; i++) {
            int i2 = (crc << 4) | ((message[i] >> 4) & 15);
            int[] iArr = crcTable;
            int crc2 = (i2 ^ iArr[crc >> 12]) & SupportMenu.USER_MASK;
            crc = ((((message[i] >> 0) & 15) | (crc2 << 4)) ^ iArr[crc2 >> 12]) & SupportMenu.USER_MASK;
        }
        byte[] CRC = {(byte) ((65280 & crc) >> 8), (byte) (crc & 255)};
        return CRC;
    }

    public static short calcCrc_short(byte[] message) {
        int crc = SupportMenu.USER_MASK;
        for (int i = 0; i < message.length; i++) {
            int i2 = (crc << 4) | ((message[i] >> 4) & 15);
            int[] iArr = crcTable;
            int crc2 = (i2 ^ iArr[crc >> 12]) & SupportMenu.USER_MASK;
            crc = (((crc2 << 4) | ((message[i] >> 0) & 15)) ^ iArr[crc2 >> 12]) & SupportMenu.USER_MASK;
        }
        return (short) crc;
    }

    public static byte[] ListBtobytes(List<Byte> lb) {
        Byte[] by = new Byte[lb.size()];
        byte[] by2 = new byte[lb.size()];
        lb.toArray(by);
        for (int i = 0; i < by2.length; i++) {
            by2[i] = by[i].byteValue();
        }
        return by2;
    }

    public static byte[] shortTobytes(short data) {
        byte[] redata = {(byte) ((65280 & data) >> 8), (byte) (data & 255)};
        return redata;
    }

    public static byte[] intTobytes(int data) {
        byte[] redata = {(byte) (((-16777216) & data) >> 24), (byte) ((16711680 & data) >> 16), (byte) ((65280 & data) >> 8), (byte) (data & 255)};
        return redata;
    }

    public static List<Byte> shortTolistbytes(short data) {
        List<Byte> redata = new ArrayList<>();
        redata.add(Byte.valueOf((byte) ((65280 & data) >> 8)));
        redata.add(Byte.valueOf((byte) (data & 255)));
        return redata;
    }

    public static List<Byte> intTolistbytes(int data) {
        List<Byte> redata = new ArrayList<>();
        redata.add(Byte.valueOf((byte) (((-16777216) & data) >> 24)));
        redata.add(Byte.valueOf((byte) ((16711680 & data) >> 16)));
        redata.add(Byte.valueOf((byte) ((65280 & data) >> 8)));
        redata.add(Byte.valueOf((byte) (data & 255)));
        return redata;
    }

    public static List<Byte> bytesTolistbytes(byte[] data) {
        List<Byte> redata = new ArrayList<>();
        for (byte b : data) {
            redata.add(Byte.valueOf(b));
        }
        return redata;
    }

    public static List<Integer> intsTolistints(int[] data) {
        List<Integer> redata = new ArrayList<>();
        for (int i : data) {
            redata.add(Integer.valueOf(i));
        }
        return redata;
    }

    public static short bytesToshort(byte[] data) {
        return (short) (((data[1] & 255) << 8) | ((data[0] & 255) << 0));
    }

    public static int bytesToint(byte[] data) {
        return ((data[3] & 255) << 24) | ((data[2] & 255) << 16) | ((data[1] & 255) << 8) | ((data[0] & 255) << 0);
    }

    public static int bytesToint(byte[] data, int st) {
        return ((data[st + 3] & 255) << 24) | ((data[st + 2] & 255) << 16) | ((data[st + 1] & 255) << 8) | ((data[st] & 255) << 0);
    }

    public class OEM_DATA {
        List<Adpair> La;
        List<Short> Lad;

        public class Adpair {
            public short addr;
            public int val;

            public Adpair() {
            }
        }

        public Adpair[] GetAddr() {
            List<Adpair> list = this.La;
            if (list != null) {
                return (Adpair[]) list.toArray(new Adpair[list.size()]);
            }
            return new Adpair[0];
        }

        public OEM_DATA(short addr, int datav) {
            this.La = new ArrayList();
            Adpair ad = new Adpair();
            ad.addr = addr;
            ad.val = datav;
            this.La.add(ad);
        }

        public OEM_DATA(short addr) {
            ArrayList arrayList = new ArrayList();
            this.Lad = arrayList;
            arrayList.add(Short.valueOf(addr));
        }

        public OEM_DATA(byte[] data) {
            int allen = data.length / 6;
            this.La = new ArrayList();
            int p = 0;
            for (int i = 0; i < allen; i++) {
                byte[] addrb = new byte[2];
                byte[] valb = new byte[4];
                System.arraycopy(data, p, addrb, 0, 2);
                int p2 = p + 2;
                System.arraycopy(data, p2, valb, 0, 4);
                p = p2 + 4;
                Adpair ad = new Adpair();
                ad.addr = (short) (((addrb[0] & 255) << 8) | ((addrb[1] & 255) << 0));
                ad.val = ((valb[2] & 255) << 8) | ((valb[0] & 255) << 24) | ((valb[1] & 255) << 16) | ((valb[3] & 255) << 0);
                this.La.add(ad);
            }
        }

        public void AddTo(short addr, int datav) {
            if (this.La != null) {
                Adpair ad = new Adpair();
                ad.addr = addr;
                ad.val = datav;
                this.La.add(ad);
                return;
            }
            List<Short> list = this.Lad;
            if (list != null) {
                list.add(Short.valueOf(addr));
            }
        }

        public byte[] ToByteData() {
            List<Byte> lb = new ArrayList<>();
            if (this.La != null) {
                for (int i = 0; i < this.La.size(); i++) {
                    lb.addAll(R2000_calibration.shortTolistbytes(this.La.get(i).addr));
                    lb.addAll(R2000_calibration.intTolistbytes(this.La.get(i).val));
                }
            }
            if (this.Lad != null) {
                for (int i2 = 0; i2 < this.Lad.size(); i2++) {
                    lb.addAll(R2000_calibration.shortTolistbytes(this.Lad.get(i2).shortValue()));
                }
            }
            return R2000_calibration.ListBtobytes(lb);
        }
    }

    public class MAC_DATA extends OEM_DATA {
        public MAC_DATA(short addr, int datav) {
            super(addr, datav);
        }

        public MAC_DATA(short addr) {
            super(addr);
        }

        public MAC_DATA(byte[] data) {
            super(data);
        }
    }

    public class TestFre_DATA {
        int freq;
        int reserved;

        public TestFre_DATA(int res, int fre) {
            this.reserved = res;
            this.freq = fre;
        }

        public byte[] ToByteData() {
            List<Byte> lb = new ArrayList<>();
            lb.addAll(R2000_calibration.intTolistbytes(this.reserved));
            lb.addAll(R2000_calibration.intTolistbytes(this.freq));
            return R2000_calibration.ListBtobytes(lb);
        }
    }

    public class VSWRReturnloss_DATA {
        List<Integer> ants;
        List<Integer> lifre;
        List<Byte> lires;
        int power;
        Region regb;

        public List<Integer> LiFre() {
            return this.lifre;
        }

        public List<Byte> Lires() {
            return this.lires;
        }

        public List<Integer> Ants() {
            return this.ants;
        }

        public Region Regb() {
            return this.regb;
        }

        public int Power() {
            return this.power;
        }

        public VSWRReturnloss_DATA(int pow, int[] lifreary, int[] antsary, Region reg) {
            this.lifre = new ArrayList();
            this.ants = new ArrayList();
            this.lifre.addAll(R2000_calibration.intsTolistints(lifreary));
            this.ants.addAll(R2000_calibration.intsTolistints(antsary));
            this.power = pow;
            this.regb = reg;
        }

        public VSWRReturnloss_DATA(byte[] bytedata) {
            this.lifre = new ArrayList();
            this.ants = new ArrayList();
            this.lires = new ArrayList();
            this.power = (bytedata[0] << 8) | bytedata[1];
            this.regb = Region.values()[bytedata[3]];
            for (int i = 5; i < bytedata.length; i += 4) {
                this.lifre.add(Integer.valueOf(((bytedata[i] & 255) << 16) | ((bytedata[i + 1] & 255) << 8) | (bytedata[i + 2] & 255)));
                this.lires.add(Byte.valueOf((byte) (bytedata[i + 3] & 255)));
            }
        }

        public byte[] ToByteData() {
            List<Byte> lb = new ArrayList<>();
            lb.add(Byte.valueOf((byte) ((this.power & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8)));
            lb.add(Byte.valueOf((byte) (this.power & 255)));
            lb.add((byte) 0);
            lb.add(Byte.valueOf((byte) this.regb.value));
            if (this.lifre.size() > 0) {
                lb.add(Byte.valueOf((byte) this.lifre.size()));
                for (int i = 0; i < this.lifre.size(); i++) {
                    lb.add(Byte.valueOf((byte) ((this.lifre.get(i).intValue() & 16711680) >> 16)));
                    lb.add(Byte.valueOf((byte) ((this.lifre.get(i).intValue() & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8)));
                    lb.add(Byte.valueOf((byte) (this.lifre.get(i).intValue() & 255)));
                }
            } else {
                lb.add((byte) 0);
            }
            return R2000_calibration.ListBtobytes(lb);
        }
    }

    public class Pabiascal_DATA {
        int paa0;
        int paa1;
        int paa2;

        public Pabiascal_DATA(int a2, int a1, int a0) {
            this.paa2 = a2;
            this.paa1 = a1;
            this.paa0 = a0;
        }

        public byte[] ToByteData() {
            List<Byte> lb = new ArrayList<>();
            lb.addAll(R2000_calibration.intTolistbytes(this.paa2));
            lb.addAll(R2000_calibration.intTolistbytes(this.paa1));
            lb.addAll(R2000_calibration.intTolistbytes(this.paa0));
            return R2000_calibration.ListBtobytes(lb);
        }
    }

    public class GrossGains_DATA {
        List<Integer> Lad;

        public List<Integer> LAD() {
            return this.Lad;
        }

        public GrossGains_DATA(int addr) {
            ArrayList arrayList = new ArrayList();
            this.Lad = arrayList;
            arrayList.add(Integer.valueOf(addr));
        }

        public GrossGains_DATA(byte[] data) {
            this.Lad = new ArrayList();
            int i = 0;
            while (i < data.length) {
                int i2 = i + 1;
                int val = R2000_calibration.bytesToint(data, i2);
                this.Lad.add(Integer.valueOf(val));
                i = i2 + 4;
            }
        }

        public void AddTo(int datav) {
            List<Integer> list = this.Lad;
            if (list != null) {
                list.add(Integer.valueOf(datav));
            }
        }

        public byte[] ToByteData() {
            List<Byte> lb = new ArrayList<>();
            if (this.Lad != null) {
                for (int i = 0; i < this.Lad.size(); i++) {
                    lb.add(Byte.valueOf((byte) i));
                    lb.addAll(R2000_calibration.intTolistbytes(this.Lad.get(i).intValue()));
                }
            }
            return R2000_calibration.ListBtobytes(lb);
        }
    }

    public class Tagtemperture_DATA {
        byte[] BankData;
        int pvtAntenna;
        int pvtFrequency;
        int pvtLqi;
        int pvtPhase;
        int pvtPro;
        int pvtReadCount;
        int pvtTsmp;
        byte[] tagcrc;
        byte[] tagepc;
        int taglen;
        byte[] tagpc;
        byte[] temperdata;

        public int ReadCount() {
            return this.pvtReadCount;
        }

        public int Lqi() {
            return this.pvtLqi;
        }

        public int Frequency() {
            return this.pvtFrequency;
        }

        public int Phase() {
            return this.pvtPhase;
        }

        public int Antenna() {
            return this.pvtAntenna;
        }

        public int Timestamp() {
            return this.pvtTsmp;
        }

        public int Protocol() {
            return this.pvtPro;
        }

        public byte[] Data() {
            return this.temperdata;
        }

        public byte[] TagEpc() {
            return this.tagepc;
        }

        public Tagtemperture_DATA() {
        }

        public Tagtemperture_DATA(byte[] revddata, int wordCount) {
            this.temperdata = new byte[wordCount * 2];
            int i = 0 + 1;
            byte option = revddata[0];
            if ((option & 16) != 0) {
                int i2 = i + 1;
                int i3 = i2 + 1;
                int metaflag = (short) (revddata[i2] | ((short) (revddata[i] << 8)));
                if ((metaflag & 1) != 0) {
                    this.pvtReadCount = revddata[i3];
                    i3++;
                }
                if ((metaflag & 2) != 0) {
                    this.pvtLqi = revddata[i3];
                    i3++;
                }
                if ((metaflag & 4) != 0) {
                    int i4 = i3 + 1;
                    int i5 = revddata[i3] & 15;
                    this.pvtAntenna = i5;
                    if (i5 == 0) {
                        this.pvtAntenna = 16;
                    }
                    i3 = i4;
                }
                if ((metaflag & 8) != 0) {
                    int i6 = i3 + 1;
                    int i7 = (revddata[i3] & 255) << 16;
                    this.pvtFrequency = i7;
                    int i8 = i6 + 1;
                    int i9 = i7 | ((revddata[i6] & 255) << 8);
                    this.pvtFrequency = i9;
                    this.pvtFrequency = i9 | (revddata[i8] & 255);
                    i3 = i8 + 1;
                }
                if ((metaflag & 16) != 0) {
                    int i10 = i3 + 1;
                    int i11 = (revddata[i3] & 255) << 24;
                    this.pvtTsmp = i11;
                    int i12 = i10 + 1;
                    int i13 = i11 | ((revddata[i10] & 255) << 16);
                    this.pvtTsmp = i13;
                    int i14 = i12 + 1;
                    int i15 = i13 | ((revddata[i12] & 255) << 8);
                    this.pvtTsmp = i15;
                    this.pvtTsmp = i15 | (revddata[i14] & 255);
                    i3 = i14 + 1;
                }
                if ((metaflag & 32) != 0) {
                    this.pvtPhase = revddata[i3 + 1];
                    i3 += 2;
                }
                if ((metaflag & 64) != 0) {
                    this.pvtPro = revddata[i3];
                    i3++;
                }
                if ((metaflag & 128) == 0) {
                    i = i3;
                } else {
                    int bdalen = ((revddata[i3] << 8) | revddata[i3 + 1]) / 8;
                    int i16 = i3 + 2;
                    if (bdalen != 0) {
                        byte[] bArr = new byte[bdalen];
                        this.BankData = bArr;
                        System.arraycopy(revddata, i16, bArr, 0, bdalen);
                    }
                    i = i16 + bdalen;
                }
            }
            int idx = i;
            while (true) {
                byte[] bArr2 = this.temperdata;
                if (i >= bArr2.length + idx) {
                    break;
                }
                bArr2[i - idx] = revddata[i];
                i++;
            }
            int i17 = i + 1;
            int i18 = revddata[i];
            this.taglen = i18;
            this.tagpc = new byte[]{revddata[i17], revddata[i]};
            int i19 = i17 + 1;
            int i20 = i19 + 1;
            this.tagepc = new byte[i18 - 4];
            int j = i20;
            while (true) {
                byte[] bArr3 = this.tagepc;
                if (j < bArr3.length + i20) {
                    bArr3[j - i20] = revddata[j];
                    j++;
                } else {
                    this.tagcrc = new byte[]{revddata[i20], revddata[i]};
                    int i21 = i20 + 1;
                    int i22 = i21 + 1;
                    return;
                }
            }
        }
    }

    public class TagLED_DATA {
        byte[] BankData;
        int pvtAntenna;
        int pvtFrequency;
        int pvtLqi;
        int pvtPhase;
        int pvtPro;
        int pvtReadCount;
        int pvtTsmp;
        byte[] tagcrc;
        byte[] tagepc;
        byte[] tagpc;

        public int ReadCount() {
            return this.pvtReadCount;
        }

        public int Lqi() {
            return this.pvtLqi;
        }

        public int Frequency() {
            return this.pvtFrequency;
        }

        public int Phase() {
            return this.pvtPhase;
        }

        public int Antenna() {
            return this.pvtAntenna;
        }

        public int Timestamp() {
            return this.pvtTsmp;
        }

        public int Protocol() {
            return this.pvtPro;
        }

        public byte[] Data() {
            return this.BankData;
        }

        public byte[] TagEpc() {
            return this.tagepc;
        }

        public TagLED_DATA() {
        }

        public TagLED_DATA(byte[] revddata, int len) {
            int i = 0 + 1;
            byte option = revddata[0];
            if ((option & 16) != 0) {
                int i2 = i + 1;
                int i3 = i2 + 1;
                int metaflag = (short) ((revddata[i2] & 255) | ((short) ((revddata[i] << 8) & 255)));
                if ((metaflag & 1) != 0) {
                    this.pvtReadCount = revddata[i3];
                    i3++;
                }
                if ((metaflag & 2) != 0) {
                    this.pvtLqi = revddata[i3];
                    i3++;
                }
                if ((metaflag & 4) != 0) {
                    int i4 = i3 + 1;
                    int i5 = revddata[i3] & 15;
                    this.pvtAntenna = i5;
                    if (i5 == 0) {
                        this.pvtAntenna = 16;
                    }
                    i3 = i4;
                }
                if ((metaflag & 8) != 0) {
                    int i6 = i3 + 1;
                    int i7 = (revddata[i3] & 255) << 16;
                    this.pvtFrequency = i7;
                    int i8 = i6 + 1;
                    int i9 = i7 | ((revddata[i6] & 255) << 8);
                    this.pvtFrequency = i9;
                    this.pvtFrequency = i9 | (revddata[i8] & 255);
                    i3 = i8 + 1;
                }
                if ((metaflag & 16) != 0) {
                    int i10 = i3 + 1;
                    int i11 = (revddata[i3] & 255) << 24;
                    this.pvtTsmp = i11;
                    int i12 = i10 + 1;
                    int i13 = i11 | ((revddata[i10] & 255) << 16);
                    this.pvtTsmp = i13;
                    int i14 = i12 + 1;
                    int i15 = i13 | ((revddata[i12] & 255) << 8);
                    this.pvtTsmp = i15;
                    this.pvtTsmp = i15 | (revddata[i14] & 255);
                    i3 = i14 + 1;
                }
                if ((metaflag & 32) != 0) {
                    this.pvtPhase = revddata[i3 + 1];
                    i3 += 2;
                }
                if ((metaflag & 64) != 0) {
                    this.pvtPro = revddata[i3];
                    i3++;
                }
                if ((metaflag & 128) == 0) {
                    i = i3;
                } else {
                    int bdalen = ((revddata[i3] << 8) | revddata[i3 + 1]) / 8;
                    int i16 = i3 + 2;
                    if (bdalen != 0) {
                        byte[] bArr = new byte[bdalen];
                        this.BankData = bArr;
                        System.arraycopy(revddata, i16, bArr, 0, bdalen);
                    }
                    i = i16 + bdalen;
                }
            }
            this.tagpc = new byte[]{revddata[i], revddata[i]};
            int i17 = i + 1;
            int i18 = i17 + 1;
            this.tagepc = new byte[(len - i18) - 2];
            int j = i18;
            while (true) {
                byte[] bArr2 = this.tagepc;
                if (j < bArr2.length + i18) {
                    bArr2[j - i18] = revddata[j];
                    j++;
                } else {
                    this.tagcrc = new byte[]{revddata[i18], revddata[i]};
                    int i19 = i18 + 1;
                    int i20 = i19 + 1;
                    return;
                }
            }
        }
    }

    public class META_DATA {
        public boolean IsAntennaID;
        public boolean IsEmdData;
        public boolean IsFrequency;
        public boolean IsPro;
        public boolean IsRFU;
        public boolean IsRSSI;
        public boolean IsReadCnt;
        public boolean IsTimestamp;

        public META_DATA() {
        }

        public short getMetaflag() {
            short metaflag = 0;
            if (this.IsReadCnt) {
                metaflag = (short) (0 | 1);
            }
            if (this.IsRSSI) {
                metaflag = (short) (metaflag | 2);
            }
            if (this.IsAntennaID) {
                metaflag = (short) (metaflag | 4);
            }
            if (this.IsFrequency) {
                metaflag = (short) (metaflag | 8);
            }
            if (this.IsTimestamp) {
                metaflag = (short) (metaflag | 16);
            }
            if (this.IsRFU) {
                metaflag = (short) (metaflag | 32);
            }
            if (this.IsPro) {
                metaflag = (short) (metaflag | 64);
            }
            if (this.IsEmdData) {
                return (short) (metaflag | 128);
            }
            return metaflag;
        }
    }

    public enum SubCmd {
        ReadAD(11),
        SetTestAntPow(12),
        SendControl(22),
        En_low_power_gpio(170);

        private int value;

        public int Value() {
            return this.value;
        }

        SubCmd(int value) {
            this.value = 0;
            this.value = value;
        }
    }

    public class ENGTest_DATA {
        int arg0;
        int arg1;
        byte subcmd;

        public ENGTest_DATA(byte scmd, int a0, int a1) {
            this.subcmd = scmd;
            this.arg0 = a0;
            this.arg1 = a1;
        }

        public byte[] ToByteData() {
            List<Byte> lb = new ArrayList<>();
            lb.add(Byte.valueOf(this.subcmd));
            lb.addAll(R2000_calibration.intTolistbytes(this.arg0));
            lb.addAll(R2000_calibration.intTolistbytes(this.arg1));
            return R2000_calibration.ListBtobytes(lb);
        }
    }

    public byte[] GetSendCmd(byte cmd, byte[] data) {
        List<Byte> lb = new ArrayList<>();
        lb.add((byte) -1);
        if (data != null) {
            lb.add(Byte.valueOf((byte) data.length));
        } else {
            lb.add((byte) 0);
        }
        lb.add(Byte.valueOf(cmd));
        if (data != null) {
            lb.addAll(bytesTolistbytes(data));
        }
        lb.add((byte) 0);
        lb.add((byte) 0);
        byte[] crc = calcCrc(ListBtobytes(lb), 1, lb.size() - 1);
        lb.set(lb.size() - 1, Byte.valueOf(crc[1]));
        lb.set(lb.size() - 2, Byte.valueOf(crc[0]));
        return ListBtobytes(lb);
    }

    public byte[] GetSendCmd(R2000cmd cmdot, byte[] data) {
        List<Byte> lbs = new ArrayList<>();
        lbs.add((byte) -1);
        if (data != null) {
            lbs.add(Byte.valueOf((byte) (data.length + 14)));
        } else {
            lbs.add((byte) 14);
        }
        lbs.add((byte) -86);
        lbs.addAll(bytesTolistbytes(this.cmdname));
        byte[] bs = shortTobytes((short) cmdot.value);
        lbs.addAll(bytesTolistbytes(bs));
        if (data != null) {
            lbs.addAll(bytesTolistbytes(data));
        }
        int vall = bs[0] + bs[1];
        if (data != null) {
            for (byte b : data) {
                vall += b;
            }
        }
        int i = vall & 255;
        lbs.add(Byte.valueOf((byte) i));
        lbs.add((byte) -69);
        lbs.add((byte) 0);
        lbs.add((byte) 0);
        byte[] crc = calcCrc(ListBtobytes(lbs), 1, lbs.size() - 3);
        lbs.set(lbs.size() - 1, Byte.valueOf(crc[1]));
        lbs.set(lbs.size() - 2, Byte.valueOf(crc[0]));
        return ListBtobytes(lbs);
    }

    public class FilterS2inA_DATA {
        short antbit;
        int fre_v;
        short power_v;

        public FilterS2inA_DATA(int[] ants, int fre, int power) {
            this.antbit = (short) 0;
            for (int i : ants) {
                this.antbit = (short) (this.antbit | (1 << (i - 1)));
            }
            this.fre_v = fre;
            this.power_v = (short) power;
        }

        public byte[] ToByteData() {
            List<Byte> lb = new ArrayList<>();
            lb.addAll(R2000_calibration.shortTolistbytes(this.antbit));
            lb.addAll(R2000_calibration.intTolistbytes(this.fre_v));
            lb.addAll(R2000_calibration.shortTolistbytes(this.power_v));
            return R2000_calibration.ListBtobytes(lb);
        }
    }
}
