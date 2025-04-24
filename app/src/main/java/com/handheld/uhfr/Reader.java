package com.handheld.uhfr;

/* loaded from: classes.dex */
public class Reader {

    public enum Lock_Obj {
        LOCK_OBJECT_KILL_PASSWORD(1),
        LOCK_OBJECT_ACCESS_PASSWD(2),
        LOCK_OBJECT_BANK1(4),
        LOCK_OBJECT_BANK2(8),
        LOCK_OBJECT_BANK3(16);

        int p_v;

        Lock_Obj(int v) {
            this.p_v = v;
        }

        public int value() {
            return this.p_v;
        }
    }

    public enum Lock_Type {
        KILL_PASSWORD_UNLOCK(0),
        KILL_PASSWORD_LOCK(512),
        KILL_PASSWORD_PERM_LOCK(768),
        ACCESS_PASSWD_UNLOCK(0),
        ACCESS_PASSWD_LOCK(128),
        ACCESS_PASSWD_PERM_LOCK(192),
        BANK1_UNLOCK(0),
        BANK1_LOCK(32),
        BANK1_PERM_LOCK(48),
        BANK2_UNLOCK(0),
        BANK2_LOCK(8),
        BANK2_PERM_LOCK(12),
        BANK3_UNLOCK(0),
        BANK3_LOCK(2),
        BANK3_PERM_LOCK(3);

        int p_v;

        Lock_Type(int v) {
            this.p_v = v;
        }

        public int value() {
            return this.p_v;
        }
    }

    public enum READER_ERR {
        MT_OK_ERR(0),
        MT_IO_ERR(1),
        MT_INTERNAL_DEV_ERR(2),
        MT_CMD_FAILED_ERR(3),
        MT_CMD_NO_TAG_ERR(4),
        MT_M5E_FATAL_ERR(5),
        MT_OP_NOT_SUPPORTED(6),
        MT_INVALID_PARA(7),
        MT_INVALID_READER_HANDLE(8),
        MT_HARDWARE_ALERT_ERR_BY_HIGN_RETURN_LOSS(9),
        MT_HARDWARE_ALERT_ERR_BY_TOO_MANY_RESET(10),
        MT_HARDWARE_ALERT_ERR_BY_NO_ANTENNAS(11),
        MT_HARDWARE_ALERT_ERR_BY_HIGH_TEMPERATURE(12),
        MT_HARDWARE_ALERT_ERR_BY_READER_DOWN(13),
        MT_HARDWARE_ALERT_ERR_BY_UNKNOWN_ERR(14),
        M6E_INIT_FAILED(15),
        MT_OP_EXECING(16),
        MT_UNKNOWN_READER_TYPE(17),
        MT_OP_INVALID(18),
        MT_HARDWARE_ALERT_BY_FAILED_RESET_MODLUE(19),
        MT_MAX_ERR_NUM(20),
        MT_MAX_INT_NUM(21),
        MT_TEST_DEV_FAULT_1(51),
        MT_TEST_DEV_FAULT_2(52),
        MT_TEST_DEV_FAULT_3(53),
        MT_TEST_DEV_FAULT_4(54),
        MT_TEST_DEV_FAULT_5(55),
        MT_UPDFWFROMSP_OPENFILE_FAILED(80),
        MT_UPDFWFROMSP_FILE_FORMAT_ERR(81),
        MT_JNI_INVALID_PARA(101),
        MT_OTHER_ERR(-268435457);

        private int value;

        READER_ERR(int value) {
            this.value = 0;
            this.value = value;
        }

        public static READER_ERR valueOf(int value) {
            if (value == 80) {
                return MT_UPDFWFROMSP_OPENFILE_FAILED;
            }
            if (value == 81) {
                return MT_UPDFWFROMSP_FILE_FORMAT_ERR;
            }
            if (value != 101) {
                switch (value) {
                    case 0:
                        return MT_OK_ERR;
                    case 1:
                        return MT_IO_ERR;
                    case 2:
                        return MT_INTERNAL_DEV_ERR;
                    case 3:
                        return MT_CMD_FAILED_ERR;
                    case 4:
                        return MT_CMD_NO_TAG_ERR;
                    case 5:
                        return MT_M5E_FATAL_ERR;
                    case 6:
                        return MT_OP_NOT_SUPPORTED;
                    case 7:
                        return MT_INVALID_PARA;
                    case 8:
                        return MT_INVALID_READER_HANDLE;
                    case 9:
                        return MT_HARDWARE_ALERT_ERR_BY_HIGN_RETURN_LOSS;
                    case 10:
                        return MT_HARDWARE_ALERT_ERR_BY_TOO_MANY_RESET;
                    case 11:
                        return MT_HARDWARE_ALERT_ERR_BY_NO_ANTENNAS;
                    case 12:
                        return MT_HARDWARE_ALERT_ERR_BY_HIGH_TEMPERATURE;
                    case 13:
                        return MT_HARDWARE_ALERT_ERR_BY_READER_DOWN;
                    case 14:
                        return MT_HARDWARE_ALERT_ERR_BY_UNKNOWN_ERR;
                    case 15:
                        return M6E_INIT_FAILED;
                    case 16:
                        return MT_OP_EXECING;
                    case 17:
                        return MT_UNKNOWN_READER_TYPE;
                    case 18:
                        return MT_OP_INVALID;
                    case 19:
                        return MT_HARDWARE_ALERT_BY_FAILED_RESET_MODLUE;
                    case 20:
                        return MT_OTHER_ERR;
                    case 21:
                        return MT_OTHER_ERR;
                    default:
                        switch (value) {
                            case 51:
                                return MT_TEST_DEV_FAULT_1;
                            case 52:
                                return MT_TEST_DEV_FAULT_2;
                            case 53:
                                return MT_TEST_DEV_FAULT_3;
                            case 54:
                                return MT_TEST_DEV_FAULT_4;
                            case 55:
                                return MT_TEST_DEV_FAULT_5;
                            default:
                                return MT_OTHER_ERR;
                        }
                }
            }
            return MT_JNI_INVALID_PARA;
        }

        public int value() {
            return this.value;
        }
    }

    public enum Region_Conf {
        RG_PRC(0),
        RG_NA(1),
        RG_NONE(2),
        RG_KR(3),
        RG_EU(4),
        RG_EU2(5),
        RG_EU3(6);

        int p_v;

        Region_Conf(int v) {
            this.p_v = v;
        }

        public int value() {
            return this.p_v;
        }

        public static Region_Conf valueOf(int value) {
            switch (value) {
                case 0:
                    return RG_PRC;
                case 1:
                    return RG_NA;
                case 2:
                    return RG_NONE;
                case 3:
                    return RG_KR;
                case 4:
                    return RG_EU;
                case 5:
                    return RG_EU2;
                case 6:
                    return RG_EU3;
                default:
                    return null;
            }
        }
    }

    public enum SL_TagProtocol {
        SL_TAG_PROTOCOL_NONE(0),
        SL_TAG_PROTOCOL_ISO180006B(3),
        SL_TAG_PROTOCOL_GEN2(5),
        SL_TAG_PROTOCOL_ISO180006B_UCODE(6),
        SL_TAG_PROTOCOL_IPX64(7),
        SL_TAG_PROTOCOL_IPX256(8);

        int p_v;

        SL_TagProtocol(int v) {
            this.p_v = v;
        }

        public int value() {
            return this.p_v;
        }

        public static SL_TagProtocol valueOf(int value) {
            if (value == 0) {
                return SL_TAG_PROTOCOL_NONE;
            }
            if (value == 3) {
                return SL_TAG_PROTOCOL_ISO180006B;
            }
            if (value == 5) {
                return SL_TAG_PROTOCOL_GEN2;
            }
            if (value == 6) {
                return SL_TAG_PROTOCOL_ISO180006B_UCODE;
            }
            if (value == 7) {
                return SL_TAG_PROTOCOL_IPX64;
            }
            if (value != 8) {
                return null;
            }
            return SL_TAG_PROTOCOL_IPX256;
        }
    }

    public static class TEMPTAGINFO implements Cloneable {
        public byte AntennaID;
        public byte[] EmbededData;
        public short EmbededDatalen;
        public byte[] EpcId;
        public short Epclen;
        public int Frequency;
        public int Phase;
        public int RSSI;
        public int ReadCnt;
        public double Temperature;
        public int TimeStamp;
        public int count;
        public int index;
        public SL_TagProtocol protocol;
        public byte[] Res = new byte[2];
        public byte[] PC = new byte[2];
        public byte[] CRC = new byte[2];

        public Object clone() {
            try {
                TEMPTAGINFO o = (TEMPTAGINFO) super.clone();
                return o;
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }
    }
}
