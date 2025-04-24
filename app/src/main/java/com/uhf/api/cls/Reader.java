package com.uhf.api.cls;

import android.util.Log;
import androidx.core.view.InputDeviceCompat;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.uhf.api.cls.R2000_calibration;
import com.uhf.api.cls.R2000_calibration.FilterS2inA_DATA;
import com.uhf.api.cls.R2000_calibration.TagLED_DATA;
import com.uhf.api.cls.R2000_calibration.Tagtemperture_DATA;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* loaded from: classes.dex */
public class Reader {
    public static final int HOPTABLECNT = 100;
    public static final int MAXANTCNT = 16;
    public static final int MAXEMBDATALEN = 128;
    public static final int MAXEPCBYTESCNT = 62;
    public static final int MAXINVPOTLSCNT = 6;
    public static final int MAXIPSTRLEN = 50;
    int IT_CT_c;
    long IT_CT_start;
    int IT_CT_step;
    Thread IT_CT_thread;
    boolean IT_E7_istargetA;
    Region_Conf IT_E7_rg;
    long IT_E7_start;
    int IT_E7_step;
    Thread IT_E7_thread;
    boolean IT_S2_istargetA;
    long IT_S2_start;
    int IT_S2_step;
    Thread IT_S2_thread;
    String addr;
    boolean isIT_CT_run;
    boolean isIT_E7_run;
    boolean isIT_S2_run;
    IT_MODE it_mode_V;
    int m_BackReadAntsCnt;
    BackReadOption m_BackReadOp;
    int m_FastReadOption;
    boolean m_IsReadThRunning;
    boolean m_IsReadingForAll;
    Thread m_ThreadForAll;
    long m_ThreadForAllid;
    int m_gError;
    int pantcnt;
    int[] pants;
    int poption;
    AntPowerConf setpower;
    int totalcount;
    long vstaticstarttick;
    private final int JniBytesBufferLength = 500;
    int[] m_BackReadAnts = new int[16];
    protected int IT_CT_m1_keep = 15;
    protected int IT_CT_m1_toma = 5;
    protected int IT_CT_m1_cycle = 1000;
    protected int IT_CT_m2_keepcount = 10;
    protected int IT_CT_m2_cycle = 500;
    protected int IT_CT_m2_toma = 20;
    protected int IT_CT_m2_tomc = 10;
    protected int IT_CT_m3_cycleread = 300;
    protected int IT_CT_m3_cyclestop = ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION;
    protected int IT_CT_m3_toma = 1;
    protected final int IT_CT_M1_STEP1 = 0;
    final int IT_CT_M1_STEP2 = 1;
    final int IT_CT_M2_STEP1 = 2;
    final int IT_CT_M2_STEP2 = 3;
    final int IT_CT_M3 = 4;
    protected final int IT_CT_M1_TESTFORDJ_STEP1 = 5;
    final int IT_CT_M1_TESTFORDJ_STEP2 = 6;
    protected int IT_CT_step_init = 0;
    int totalcountlast = 0;
    protected final int IT_S2_M1_STEP1 = 0;
    final int IT_S2_M1_STEP2 = 1;
    protected int IT_S2_m1_cycle = 500;
    protected int IT_S2_ctagcount = 50;
    protected int IT_S2_m1_cycle2 = 100;
    protected int IT_S2_ctagcount2 = 2;
    protected final int IT_E7_M1_STEP1 = 0;
    final int IT_E7_M1_STEP2 = 1;
    final int IT_E7_M1_STEP3 = 2;
    final int IT_E7_M1_STEP4 = 3;
    final int IT_E7_M1_STEP5 = 4;
    protected int IT_E7_rfm = 101;
    protected int IT_E7_m1_cycle = 500;
    protected int IT_E7_ctagcount = 50;
    protected int IT_E7_m1_cycle2 = 100;
    protected int IT_E7_ctagcount2 = 2;
    protected int IT_E7_m1_cycle3 = 70;
    protected int IT_E7_ctagcount3 = 1;
    int IT_E7_centrefre = 915250;
    int IT_E7_lowfre = 902250;
    int IT_E7_highfre = 927250;
    int IT_E7_pow = 3300;
    Lock lockobj_newadd = new ReentrantLock();
    Vector<String> quetagstr = new Vector<>();
    int[] IT_CT_fres_NA = {915750, 927250, 902750, 914250, 925250, 904750, 916250, 926750, 903250, 913250, 921250, 906250, 917250, 924250, 905750, 915250, 923750, 907750, 919250, 926250, 908750, 910250, 922750, 912250};
    int[] IT_CT_fres_cn = {922625, 924375, 920625, 922375, 924125, 920875, 922875, 923875, 921125, 922125, 923625, 921375, 923375, 921875, 923125, 921625};
    boolean isfilterpw = false;
    int[] hReader = new int[1];
    JniModuleAPI japi = new JniModuleAPI();
    protected List<ReadListener> readListeners = new Vector();
    protected List<ReadExceptionListener> readExceptionListeners = new Vector();
    protected List<GpiTriggerListener> gpitriListener = new Vector();
    protected List<GpiTriggerBoundaryListener> gpitriboundListener = new Vector();

    public enum IT_MODE {
        IT_MODE_CT,
        IT_MODE_S2,
        IT_MODE_E7,
        IT_MODE_E7v2
    }

    public enum MaindBoard_Type {
        MAINBOARD_NONE,
        MAINBOARD_ARM7,
        MAINBOARD_SERIAL,
        MAINBOARD_WIFI,
        MAINBOARD_ARM9,
        MAINBOARD_ARM9_WIFI
    }

    public static class deviceVersion {
        public String hardwareVer = "";
        public String softwareVer = "";
    }

    private void CLOGS() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ALOGS(String mess) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toDlogAPI(String mess) {
        Log.d("ModuleAPI", mess);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toDlog(String mess) {
        Log.d("MYINFO", mess);
    }

    public enum Module_Type {
        MODOULE_NONE(0),
        MODOULE_R902_M1S(1),
        MODOULE_R902_M2S(2),
        MODOULE_M5E(3),
        MODOULE_M5E_C(4),
        MODOULE_M6E(5),
        MODOULE_PR9000(6),
        MODOULE_M5E_PRC(7),
        MODOULE_M6E_PRC(8),
        MODOULE_M6E_MICRO(9),
        MODOULE_SLR1100(10),
        MODOULE_SLR1200(11),
        MODOULE_SLR1300(12),
        MODOULE_SLR3000(13),
        MODOULE_SLR5100(14),
        MODOULE_SLR5200(15),
        MODOULE_SLR3100(16),
        MODOULE_SLR3200(17),
        MODOULE_SLR5300(18),
        MODOULE_SLR5900(19),
        MODOULE_SLR5800(20),
        MODOULE_SLR6000(21),
        MODOULE_SLR6100(22),
        MODOULE_SIM7100(23),
        MODOULE_SIM7200(24),
        MODOULE_SIM7300(25),
        MODOULE_SIM7400(26),
        MODOULE_SIM7500(27),
        MODOULE_SIM3100(28),
        MODOULE_SIM3200(29),
        MODOULE_SIM3300(30),
        MODOULE_SIM3400(31),
        MODOULE_SIM3500(32),
        MODOULE_SIM3600(33),
        MODOULE_SIM5100(34),
        MODOULE_SIM5200(35),
        MODOULE_SIM5300(36),
        MODOULE_SIM5400(37),
        MODOULE_SIM5500(38),
        MODOULE_SIM5600(39);

        private int value;

        Module_Type(int value) {
            this.value = 0;
            this.value = value;
        }

        public static Module_Type valueOf(int value) {
            switch (value) {
                case 0:
                    return MODOULE_NONE;
                case 1:
                    return MODOULE_R902_M1S;
                case 2:
                    return MODOULE_R902_M2S;
                case 3:
                    return MODOULE_M5E;
                case 4:
                    return MODOULE_M5E_C;
                case 5:
                    return MODOULE_M6E;
                case 6:
                    return MODOULE_PR9000;
                case 7:
                    return MODOULE_M5E_PRC;
                case 8:
                    return MODOULE_M6E_PRC;
                case 9:
                    return MODOULE_M6E_MICRO;
                case 10:
                    return MODOULE_SLR1100;
                case 11:
                    return MODOULE_SLR1200;
                case 12:
                    return MODOULE_SLR1300;
                case 13:
                    return MODOULE_SLR3000;
                case 14:
                    return MODOULE_SLR5100;
                case 15:
                    return MODOULE_SLR5200;
                case 16:
                    return MODOULE_SLR3100;
                case 17:
                    return MODOULE_SLR3200;
                case 18:
                    return MODOULE_SLR5300;
                case 19:
                    return MODOULE_SLR5900;
                case 20:
                    return MODOULE_SLR5800;
                case 21:
                    return MODOULE_SLR6000;
                case 22:
                    return MODOULE_SLR6100;
                case 23:
                    return MODOULE_SIM7100;
                case 24:
                    return MODOULE_SIM7200;
                case 25:
                    return MODOULE_SIM7300;
                case 26:
                    return MODOULE_SIM7400;
                case 27:
                    return MODOULE_SIM7500;
                case 28:
                    return MODOULE_SIM3100;
                case 29:
                    return MODOULE_SIM3200;
                case 30:
                    return MODOULE_SIM3300;
                case 31:
                    return MODOULE_SIM3400;
                case 32:
                    return MODOULE_SIM3500;
                case 33:
                    return MODOULE_SIM3600;
                case 34:
                    return MODOULE_SIM5100;
                case 35:
                    return MODOULE_SIM5200;
                case 36:
                    return MODOULE_SIM5300;
                case 37:
                    return MODOULE_SIM5400;
                case 38:
                    return MODOULE_SIM5500;
                case 39:
                    return MODOULE_SIM5600;
                default:
                    return null;
            }
        }

        public int value() {
            return this.value;
        }
    }

    public enum Region_Conf {
        RG_NONE(0),
        RG_NA(1),
        RG_EU(2),
        RG_EU2(7),
        RG_EU3(8),
        RG_KR(3),
        RG_PRC(6),
        RG_PRC2(10),
        RG_OPEN(255);

        int p_v;

        Region_Conf(int v) {
            this.p_v = v;
        }

        public int value() {
            return this.p_v;
        }

        public static Region_Conf valueOf(int value) {
            if (value == 0) {
                return RG_NONE;
            }
            if (value == 1) {
                return RG_NA;
            }
            if (value == 2) {
                return RG_EU;
            }
            if (value == 3) {
                return RG_KR;
            }
            if (value == 6) {
                return RG_PRC;
            }
            if (value == 7) {
                return RG_EU2;
            }
            if (value == 8) {
                return RG_EU3;
            }
            if (value == 10) {
                return RG_PRC2;
            }
            if (value == 255) {
                return RG_OPEN;
            }
            return null;
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
            if (value == 8) {
                return SL_TAG_PROTOCOL_IPX256;
            }
            return null;
        }
    }

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

    public enum Reader_Type {
        MODULE_TWO_ANTS(0),
        MODULE_FOUR_ANTS(1),
        MODULE_THREE_ANTS(3),
        MODULE_ONE_ANT(4),
        PR9000(5),
        MODULE_ARM7_TWO_ANTS(6),
        MODULE_ARM7_FOUR_ANTS(7),
        M6E_ARM7_FOUR_ANTS(8),
        M56_ARM7_FOUR_ANTS(9),
        R902_M1S(10),
        R902_M2S(11),
        ARM7_16ANTS(12),
        SL_COMMN_READER(13);

        private int value;

        Reader_Type(int value) {
            this.value = 0;
            this.value = value;
        }

        public static Reader_Type valueOf(int value) {
            switch (value) {
                case 0:
                    return MODULE_TWO_ANTS;
                case 1:
                    return MODULE_FOUR_ANTS;
                case 2:
                    return MODULE_THREE_ANTS;
                case 3:
                    return MODULE_ONE_ANT;
                case 4:
                    return PR9000;
                case 5:
                    return MODULE_ARM7_TWO_ANTS;
                case 6:
                    return MODULE_ARM7_FOUR_ANTS;
                case 7:
                    return M6E_ARM7_FOUR_ANTS;
                case 8:
                    return M56_ARM7_FOUR_ANTS;
                case 9:
                    return R902_M1S;
                case 10:
                    return R902_M2S;
                case 11:
                    return ARM7_16ANTS;
                case 12:
                    return SL_COMMN_READER;
                default:
                    return null;
            }
        }

        public int value() {
            return this.value;
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

    public enum CustomCmdType {
        NXP_SetReadProtect(0),
        NXP_ResetReadProtect(1),
        NXP_ChangeEAS(2),
        NXP_EASAlarm(3),
        NXP_Calibrate(4),
        ALIEN_Higgs2_PartialLoadImage(5),
        ALIEN_Higgs2_FullLoadImage(6),
        ALIEN_Higgs3_FastLoadImage(7),
        ALIEN_Higgs3_LoadImage(8),
        ALIEN_Higgs3_BlockReadLock(9),
        ALIEN_Higgs3_BlockPermaLock(10),
        IMPINJ_M4_Qt(11),
        NXP_U8_InventoryMode(20);

        private int value;

        CustomCmdType(int value) {
            this.value = 0;
            this.value = value;
        }

        public static CustomCmdType valueOf(int value) {
            if (value != 20) {
                switch (value) {
                    case 0:
                        return NXP_SetReadProtect;
                    case 1:
                        return NXP_ResetReadProtect;
                    case 2:
                        return NXP_ChangeEAS;
                    case 3:
                        return NXP_EASAlarm;
                    case 4:
                        return NXP_Calibrate;
                    case 5:
                        return ALIEN_Higgs2_PartialLoadImage;
                    case 6:
                        return ALIEN_Higgs2_FullLoadImage;
                    case 7:
                        return ALIEN_Higgs3_FastLoadImage;
                    case 8:
                        return ALIEN_Higgs3_LoadImage;
                    case 9:
                        return ALIEN_Higgs3_BlockReadLock;
                    case 10:
                        return ALIEN_Higgs3_BlockPermaLock;
                    case 11:
                        return IMPINJ_M4_Qt;
                    default:
                        return null;
                }
            }
            return NXP_U8_InventoryMode;
        }

        public int value() {
            return this.value;
        }
    }

    public enum Mtr_Param {
        MTR_PARAM_POTL_GEN2_SESSION(0),
        MTR_PARAM_POTL_GEN2_Q(1),
        MTR_PARAM_POTL_GEN2_TAGENCODING(2),
        MTR_PARAM_POTL_GEN2_MAXEPCLEN(3),
        MTR_PARAM_RF_ANTPOWER(4),
        MTR_PARAM_RF_MAXPOWER(5),
        MTR_PARAM_RF_MINPOWER(6),
        MTR_PARAM_TAG_FILTER(7),
        MTR_PARAM_TAG_EMBEDEDDATA(8),
        MTR_PARAM_TAG_INVPOTL(9),
        MTR_PARAM_READER_CONN_ANTS(10),
        MTR_PARAM_READER_AVAILABLE_ANTPORTS(11),
        MTR_PARAM_READER_IS_CHK_ANT(12),
        MTR_PARAM_READER_VERSION(13),
        MTR_PARAM_READER_IP(14),
        MTR_PARAM_FREQUENCY_REGION(15),
        MTR_PARAM_FREQUENCY_HOPTABLE(16),
        MTR_PARAM_POTL_GEN2_BLF(17),
        MTR_PARAM_POTL_GEN2_WRITEMODE(18),
        MTR_PARAM_POTL_GEN2_TARGET(19),
        MTR_PARAM_TAGDATA_UNIQUEBYANT(20),
        MTR_PARAM_TAGDATA_UNIQUEBYEMDDATA(21),
        MTR_PARAM_TAGDATA_RECORDHIGHESTRSSI(22),
        MTR_PARAM_RF_TEMPERATURE(23),
        MTR_PARAM_RF_HOPTIME(24),
        MTR_PARAM_RF_LBT_ENABLE(25),
        MTR_PARAM_RF_SUPPORTEDREGIONS(26),
        MTR_PARAM_POTL_SUPPORTEDPROTOCOLS(27),
        MTR_PARAM_POTL_ISO180006B_BLF(28),
        MTR_PARAM_POTL_GEN2_TARI(29),
        MTR_PARAM_TRANS_TIMEOUT(30),
        MTR_PARAM_TAG_EMDSECUREREAD(31),
        MTR_PARAM_TRANSMIT_MODE(32),
        MTR_PARAM_POWERSAVE_MODE(33),
        MTR_PARAM_TAG_SEARCH_MODE(34),
        MTR_PARAM_POTL_ISO180006B_MODULATION_DEPTH(35),
        MTR_PARAM_POTL_ISO180006B_DELIMITER(36),
        MTR_PARAM_RF_ANTPORTS_VSWR(37),
        MTR_PARAM_MAXINDEX(39),
        MTR_PARAM_CUSTOM(41),
        MTR_PARAM_READER_WATCHDOG(42),
        MTR_PARAM_READER_ERRORDATA(43),
        MTR_PARAM_RF_HOPANTTIME(44),
        MTR_PARAM_TAG_MULTISELECTORS(45),
        MTR_PARAM_SAVEINMODULE(46),
        MTR_PARAM_SAVEINMODULE_BAUD(47);

        private int value;

        Mtr_Param(int value) {
            this.value = 0;
            this.value = value;
        }

        public static Mtr_Param valueOf(int value) {
            switch (value) {
                case 0:
                    return MTR_PARAM_POTL_GEN2_SESSION;
                case 1:
                    return MTR_PARAM_POTL_GEN2_Q;
                case 2:
                    return MTR_PARAM_POTL_GEN2_TAGENCODING;
                case 3:
                    return MTR_PARAM_POTL_GEN2_MAXEPCLEN;
                case 4:
                    return MTR_PARAM_RF_ANTPOWER;
                case 5:
                    return MTR_PARAM_RF_MAXPOWER;
                case 6:
                    return MTR_PARAM_RF_MINPOWER;
                case 7:
                    return MTR_PARAM_TAG_FILTER;
                case 8:
                    return MTR_PARAM_TAG_EMBEDEDDATA;
                case 9:
                    return MTR_PARAM_TAG_INVPOTL;
                case 10:
                    return MTR_PARAM_READER_CONN_ANTS;
                case 11:
                    return MTR_PARAM_READER_AVAILABLE_ANTPORTS;
                case 12:
                    return MTR_PARAM_READER_IS_CHK_ANT;
                case 13:
                    return MTR_PARAM_READER_VERSION;
                case 14:
                    return MTR_PARAM_READER_IP;
                case 15:
                    return MTR_PARAM_FREQUENCY_REGION;
                case 16:
                    return MTR_PARAM_FREQUENCY_HOPTABLE;
                case 17:
                    return MTR_PARAM_POTL_GEN2_BLF;
                case 18:
                    return MTR_PARAM_POTL_GEN2_WRITEMODE;
                case 19:
                    return MTR_PARAM_POTL_GEN2_TARGET;
                case 20:
                    return MTR_PARAM_TAGDATA_UNIQUEBYANT;
                case 21:
                    return MTR_PARAM_TAGDATA_UNIQUEBYEMDDATA;
                case 22:
                    return MTR_PARAM_TAGDATA_RECORDHIGHESTRSSI;
                case 23:
                    return MTR_PARAM_RF_TEMPERATURE;
                case 24:
                    return MTR_PARAM_RF_HOPTIME;
                case 25:
                    return MTR_PARAM_RF_LBT_ENABLE;
                case 26:
                    return MTR_PARAM_RF_SUPPORTEDREGIONS;
                case 27:
                    return MTR_PARAM_POTL_SUPPORTEDPROTOCOLS;
                case 28:
                    return MTR_PARAM_POTL_ISO180006B_BLF;
                case 29:
                    return MTR_PARAM_POTL_GEN2_TARI;
                case 30:
                    return MTR_PARAM_TRANS_TIMEOUT;
                case 31:
                    return MTR_PARAM_TAG_EMDSECUREREAD;
                case 32:
                    return MTR_PARAM_TRANSMIT_MODE;
                case 33:
                    return MTR_PARAM_POWERSAVE_MODE;
                case 34:
                    return MTR_PARAM_TAG_SEARCH_MODE;
                case 35:
                    return MTR_PARAM_POTL_ISO180006B_MODULATION_DEPTH;
                case 36:
                    return MTR_PARAM_POTL_ISO180006B_DELIMITER;
                case 37:
                    return MTR_PARAM_RF_ANTPORTS_VSWR;
                case 38:
                case 40:
                default:
                    return null;
                case 39:
                    return MTR_PARAM_MAXINDEX;
                case 41:
                    return MTR_PARAM_CUSTOM;
                case 42:
                    return MTR_PARAM_READER_WATCHDOG;
                case 43:
                    return MTR_PARAM_READER_ERRORDATA;
                case 44:
                    return MTR_PARAM_RF_HOPANTTIME;
                case 45:
                    return MTR_PARAM_TAG_MULTISELECTORS;
                case 46:
                    return MTR_PARAM_SAVEINMODULE;
                case 47:
                    return MTR_PARAM_SAVEINMODULE_BAUD;
            }
        }

        public int value() {
            return this.value;
        }
    }

    public enum BackReadGpiTriState {
        BackReadGpi_WaitStart(0),
        BackReadGpi_WaitStop(1),
        BackReadGpi_WaitTimeout(2);

        private int value;

        BackReadGpiTriState(int value) {
            this.value = 0;
            this.value = value;
        }

        public static BackReadGpiTriState valueOf(int value) {
            if (value == 0) {
                return BackReadGpi_WaitStart;
            }
            if (value == 1) {
                return BackReadGpi_WaitStop;
            }
            if (value == 2) {
                return BackReadGpi_WaitTimeout;
            }
            return null;
        }

        public int value() {
            return this.value;
        }
    }

    public class TAGINFO implements Cloneable {
        public byte AntennaID;
        public short EmbededDatalen;
        public short Epclen;
        public int Frequency;
        public int Phase;
        public int RSSI;
        public int ReadCnt;
        public int TimeStamp;
        public SL_TagProtocol protocol;
        public byte[] EmbededData = null;
        public byte[] Res = new byte[2];
        public byte[] PC = new byte[2];
        public byte[] CRC = new byte[2];
        public byte[] EpcId = null;

        public TAGINFO() {
        }

        public Object clone() {
            try {
                Object o = (TAGINFO) super.clone();
                return o;
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }
    }

    public class IMPINJM4QtPara {
        public byte[] AccessPwd = new byte[4];
        public int CmdType;
        public int MemType;
        public int PersistType;
        public int RangeType;
        public short TimeOut;

        public IMPINJM4QtPara() {
        }
    }

    public class CustomParam_ST {
        public String ParamName;
        public byte[] ParamVal;

        public CustomParam_ST() {
        }
    }

    public class IMPINJM4QtResult {
        public int MemType;
        public int RangeType;

        public IMPINJM4QtResult() {
        }
    }

    public class NXPChangeEASPara {
        public byte[] AccessPwd = new byte[4];
        public short TimeOut;
        public int isSet;

        public NXPChangeEASPara() {
        }
    }

    public class NXPEASAlarmPara {
        public byte DR;
        public byte MC;
        public short TimeOut;
        public byte TrExt;

        public NXPEASAlarmPara() {
        }
    }

    public class NXPEASAlarmResult {
        public byte[] EASdata = new byte[8];

        public NXPEASAlarmResult() {
        }
    }

    public class ALIENHiggs3BlockReadLockPara {
        public byte[] AccessPwd = new byte[4];
        public byte BlkBits;
        public short TimeOut;

        public ALIENHiggs3BlockReadLockPara() {
        }
    }

    public class NXP_U8_InventoryModePara {
        public byte[] Mode = new byte[1];

        public NXP_U8_InventoryModePara() {
        }
    }

    public class AntPower {
        public int antid;
        public short readPower;
        public short writePower;

        public AntPower() {
        }
    }

    public class AntPowerConf {
        public AntPower[] Powers = new AntPower[16];
        public int antcnt;

        public AntPowerConf() {
        }
    }

    public class HardwareDetails {
        public MaindBoard_Type board;
        public Reader_Type logictype;
        public Module_Type module;

        public HardwareDetails() {
        }
    }

    public class EmbededSecureRead_ST {
        public int ApIndexBitsNumInEpc;
        public int ApIndexStartBitsInEpc;
        public int accesspwd;
        public int address;
        public int bank;
        public int blkcnt;
        public int pwdtype;
        public int tagtype;

        public EmbededSecureRead_ST() {
        }
    }

    public class Reader_Ip {
        public byte[] gateway;
        public byte[] ip;
        public byte[] mask;

        public Reader_Ip() {
        }
    }

    public class TagFilter_ST {
        public int bank;
        public byte[] fdata = new byte[255];
        public int flen;
        public int isInvert;
        public int startaddr;

        public TagFilter_ST() {
        }
    }

    public class TagSelector_ST {
        public int bank;
        public byte[] sdata = new byte[24];
        public int slen;
        public int startaddr;

        public TagSelector_ST() {
        }
    }

    public class Default_Param {
        public boolean isdefault;
        public Mtr_Param key;
        public String subkey;
        public Object val;

        public Default_Param() {
        }
    }

    public class MultiTagSelectors_ST {
        public int tagselectorcnt;
        public TagSelector_ST[] tagselectors = new TagSelector_ST[16];

        public MultiTagSelectors_ST() {
            for (int i = 0; i < 16; i++) {
                this.tagselectors[i] = Reader.this.new TagSelector_ST();
            }
        }
    }

    public class EmbededData_ST {
        public byte[] accesspwd;
        public int bank;
        public int bytecnt;
        public int startaddr;

        public EmbededData_ST() {
        }
    }

    public class ConnAnts_ST {
        public int antcnt;
        public int[] connectedants = new int[16];

        public ConnAnts_ST() {
        }
    }

    public class ReaderVersion {
        public String hardwareVer = "";
        public String softwareVer = "";

        public ReaderVersion() {
        }
    }

    public class HoptableData_ST {
        public int[] htb = new int[100];
        public int lenhtb;

        public HoptableData_ST() {
        }
    }

    public class Inv_Potl {
        public SL_TagProtocol potl;
        public int weight;

        public Inv_Potl() {
        }
    }

    public class Inv_Potls_ST {
        public int potlcnt;
        public Inv_Potl[] potls = new Inv_Potl[6];

        public Inv_Potls_ST() {
        }
    }

    public class FrequencyVSWR {
        public int frequency;
        public float vswr;

        public FrequencyVSWR() {
        }
    }

    public class AntPortsVSWR {
        public int andid;
        public int frecount;
        public short power;
        public Region_Conf region;
        public FrequencyVSWR[] vswrs = new FrequencyVSWR[100];

        public AntPortsVSWR() {
            for (int i = 0; i < 100; i++) {
                this.vswrs[i] = Reader.this.new FrequencyVSWR();
            }
        }

        public String toString() {
            String ret = "";
            for (int i = 0; i < this.frecount; i++) {
                ret = ret + this.vswrs[i].frequency + ":" + String.format("%.2f", Float.valueOf(this.vswrs[i].vswr)) + " ";
            }
            return ret;
        }
    }

    public boolean Set_IT_Params(IT_MODE it_mode, Object[] objs) throws Exception {
        if (it_mode == IT_MODE.IT_MODE_CT) {
            int p = 0 + 1;
            try {
                this.IT_CT_m1_keep = ((Integer) objs[0]).intValue();
                int p2 = p + 1;
                this.IT_CT_m1_toma = ((Integer) objs[p]).intValue();
                int p3 = p2 + 1;
                this.IT_CT_m3_cycleread = ((Integer) objs[p2]).intValue();
                int p4 = p3 + 1;
                this.IT_CT_m3_cyclestop = ((Integer) objs[p3]).intValue();
                int i = p4 + 1;
                this.IT_CT_m3_toma = ((Integer) objs[p4]).intValue();
                return true;
            } catch (Exception e) {
                throw new Exception("Length must be 5 and type must be Integer.");
            }
        }
        if (it_mode == IT_MODE.IT_MODE_S2) {
            int p5 = 0 + 1;
            try {
                this.IT_S2_m1_cycle = ((Integer) objs[0]).intValue();
                int p6 = p5 + 1;
                this.IT_S2_ctagcount = ((Integer) objs[p5]).intValue();
                int p7 = p6 + 1;
                this.IT_S2_m1_cycle2 = ((Integer) objs[p6]).intValue();
                int i2 = p7 + 1;
                this.IT_S2_ctagcount2 = ((Integer) objs[p7]).intValue();
                return true;
            } catch (Exception e2) {
                throw new Exception("Length must be 4 and type must be Integer.");
            }
        }
        if (it_mode == IT_MODE.IT_MODE_E7 || it_mode == IT_MODE.IT_MODE_E7v2) {
            int p8 = 0 + 1;
            try {
                this.IT_E7_m1_cycle = ((Integer) objs[0]).intValue();
                int p9 = p8 + 1;
                this.IT_E7_ctagcount = ((Integer) objs[p8]).intValue();
                int p10 = p9 + 1;
                this.IT_E7_m1_cycle2 = ((Integer) objs[p9]).intValue();
                int p11 = p10 + 1;
                this.IT_E7_ctagcount2 = ((Integer) objs[p10]).intValue();
                int i3 = p11 + 1;
                this.IT_E7_rfm = ((Integer) objs[p11]).intValue();
                return true;
            } catch (Exception e3) {
                throw new Exception("Length must be 4 and type must be Integer.");
            }
        }
        return false;
    }

    public void Setdutycycle(int option) {
        if (option < 0 || option > 11) {
            return;
        }
        double d = option;
        Double.isNaN(d);
        int i = (int) (d * 0.05d * 500.0d);
        this.IT_CT_m3_cyclestop = i;
        this.IT_CT_m3_cycleread = 500 - i;
    }

    public void Hex2Str(byte[] buf, int len, char[] out) {
        char[] hexc = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        for (int i = 0; i < len; i++) {
            out[i * 2] = hexc[(buf[i] & 255) / 16];
            if ((i * 2) + 1 < out.length) {
                out[(i * 2) + 1] = hexc[(buf[i] & 255) % 16];
            }
        }
    }

    private char[] bytestochars(byte[] bdata) {
        char[] cdata = new char[bdata.length];
        for (int i = 0; i < bdata.length; i++) {
            cdata[i] = (char) bdata[i];
        }
        return cdata;
    }

    private byte[] charstobytes(char[] cdata) {
        if (cdata == null) {
            return null;
        }
        int len = 0;
        for (int i = 0; i < cdata.length && cdata[i] != 0; i++) {
            len++;
        }
        byte[] bdata = new byte[len];
        for (int i2 = 0; i2 < len; i2++) {
            bdata[i2] = (byte) cdata[i2];
        }
        return bdata;
    }

    public READER_ERR InitReader(String src, Reader_Type rtype) {
        int re = this.japi.InitReader(this.hReader, src, rtype.value());
        READER_ERR ERR = READER_ERR.valueOf(re);
        return ERR;
    }

    public READER_ERR InitReader_Notype(String src, int rtype) {
        READER_ERR ERR;
        toDlogAPI("InitReader_Notype-" + src + " " + String.valueOf(rtype));
        synchronized (this) {
            int re = this.japi.InitReader_Notype(this.hReader, src, rtype);
            ERR = READER_ERR.valueOf(re);
            if (ERR == READER_ERR.MT_OK_ERR) {
                this.addr = src;
                Inv_Potls_ST ipst = new Inv_Potls_ST();
                ipst.potlcnt = 1;
                ipst.potls = new Inv_Potl[1];
                ipst.potls[0] = new Inv_Potl();
                ipst.potls[0].weight = 30;
                ipst.potls[0].potl = SL_TagProtocol.SL_TAG_PROTOCOL_GEN2;
                ParamSet(Mtr_Param.MTR_PARAM_TAG_INVPOTL, ipst);
            } else {
                this.hReader[0] = 0;
            }
        }
        return ERR;
    }

    public String GetReaderAddress() {
        return this.addr;
    }

    public READER_ERR GetHardwareDetails(HardwareDetails val) {
        READER_ERR ERR;
        synchronized (this) {
            byte[] data = new byte[500];
            int re = this.japi.GetHardwareDetails_BaseType(this.hReader[0], data);
            ERR = READER_ERR.valueOf(re);
            if (ERR == READER_ERR.MT_OK_ERR) {
                val.module = Module_Type.valueOf(data[0]);
                byte b = data[1];
                if (b != 0) {
                    if (b == 1) {
                        val.board = MaindBoard_Type.MAINBOARD_ARM7;
                    } else if (b == 2) {
                        val.board = MaindBoard_Type.MAINBOARD_SERIAL;
                    } else if (b == 3) {
                        val.board = MaindBoard_Type.MAINBOARD_WIFI;
                    } else if (b == 4) {
                        val.board = MaindBoard_Type.MAINBOARD_ARM9;
                    } else if (b == 5) {
                        val.board = MaindBoard_Type.MAINBOARD_ARM9_WIFI;
                    }
                } else {
                    val.board = MaindBoard_Type.MAINBOARD_NONE;
                }
                val.logictype = Reader_Type.valueOf(data[2]);
            }
        }
        return ERR;
    }

    public void CloseReader() {
        toDlogAPI("CloseReader-");
        synchronized (this) {
            this.japi.CloseReader(this.hReader[0]);
            this.addr = "";
            this.hReader[0] = 0;
            this.readListeners.clear();
            this.readExceptionListeners.clear();
            this.gpitriListener.clear();
            this.gpitriboundListener.clear();
        }
    }

    public READER_ERR GetTagData(int ant, char bank, int address, int blkcnt, byte[] data, byte[] accesspasswd, short timeout) {
        READER_ERR ERR;
        toDlogAPI("GetTagData-" + String.valueOf(ant) + " " + String.valueOf(bank) + " " + String.valueOf(address) + " " + String.valueOf(blkcnt));
        synchronized (this) {
            int re = this.japi.GetTagData(this.hReader[0], ant, bank, address, blkcnt, data, accesspasswd, timeout);
            ERR = READER_ERR.valueOf(re);
        }
        return ERR;
    }

    public READER_ERR WriteTagData(int ant, char bank, int address, byte[] data, int datalen, byte[] accesspasswd, short timeout) {
        READER_ERR ERR;
        synchronized (this) {
            int re = this.japi.WriteTagData(this.hReader[0], ant, bank, address, data, datalen, accesspasswd, timeout);
            ERR = READER_ERR.valueOf(re);
        }
        return ERR;
    }

    public READER_ERR WriteTagEpcEx(int ant, byte[] Epc, int epclen, byte[] accesspwd, short timeout) {
        READER_ERR ERR;
        synchronized (this) {
            int re = this.japi.WriteTagEpcEx(this.hReader[0], ant, Epc, epclen, accesspwd, timeout);
            ERR = READER_ERR.valueOf(re);
        }
        return ERR;
    }

    public READER_ERR TagInventory(int[] ants, int antcnt, short timeout, TAGINFO[] pTInfo, int[] tagcnt) {
        READER_ERR ERR;
        synchronized (this) {
            int re = this.japi.TagInventory_Raw(this.hReader[0], ants, antcnt, timeout, tagcnt);
            ERR = READER_ERR.valueOf(re);
            if (ERR == READER_ERR.MT_OK_ERR) {
                for (int i = 0; i < tagcnt[0]; i++) {
                    TAGINFO pTInfoa = new TAGINFO();
                    READER_ERR er = GetNextTag(pTInfoa);
                    if (er == READER_ERR.MT_OK_ERR) {
                        pTInfo[i] = pTInfoa;
                    }
                }
            }
        }
        return ERR;
    }

    public READER_ERR TagInventory_Raw(int[] ants, int antcnt, short timeout, int[] tagcnt) {
        READER_ERR ERR;
        synchronized (this) {
            int re = this.japi.TagInventory_Raw(this.hReader[0], ants, antcnt, timeout, tagcnt);
            ERR = READER_ERR.valueOf(re);
        }
        return ERR;
    }

    public READER_ERR TagInventory_Single(int[] ants, int antcnt, short timeout, int[] tagcnt, TAGINFO TI) {
        boolean isemd;
        READER_ERR ERR;
        EmbededData_ST edst2 = new EmbededData_ST();
        READER_ERR err = ParamGet(Mtr_Param.MTR_PARAM_TAG_EMBEDEDDATA, edst2);
        if (err == READER_ERR.MT_OK_ERR && edst2.bytecnt != 0) {
            isemd = true;
        } else {
            isemd = false;
        }
        synchronized (this) {
            long st = System.currentTimeMillis();
            READER_ERR ERR2 = READER_ERR.MT_OK_ERR;
            boolean isbreak = false;
            while (true) {
                char c = 0;
                int re = this.japi.TagInventory_Raw(this.hReader[0], ants, antcnt, (short) 20, tagcnt);
                ERR = READER_ERR.valueOf(re);
                if ((ERR != READER_ERR.MT_OK_ERR || tagcnt[0] <= 0) && System.currentTimeMillis() - st < timeout) {
                    ERR2 = ERR;
                }
                if (ERR == READER_ERR.MT_OK_ERR) {
                    int i = 0;
                    while (true) {
                        if (i >= tagcnt[c]) {
                            break;
                        }
                        TAGINFO pTInfoa = new TAGINFO();
                        READER_ERR er = GetNextTag(pTInfoa);
                        if (er == READER_ERR.MT_OK_ERR) {
                            if (isemd) {
                                if (pTInfoa.EmbededDatalen > 0) {
                                    TI.ReadCnt = pTInfoa.ReadCnt;
                                    TI.RSSI = pTInfoa.RSSI;
                                    TI.AntennaID = pTInfoa.AntennaID;
                                    TI.Frequency = pTInfoa.Frequency;
                                    TI.TimeStamp = pTInfoa.TimeStamp;
                                    System.arraycopy(pTInfoa.PC, 0, TI.PC, 0, 2);
                                    System.arraycopy(pTInfoa.PC, 0, TI.PC, 0, 2);
                                    TI.EpcId = new byte[pTInfoa.Epclen];
                                    TI.Epclen = pTInfoa.Epclen;
                                    System.arraycopy(pTInfoa.EpcId, 0, TI.EpcId, 0, TI.Epclen);
                                    System.arraycopy(pTInfoa.CRC, 0, TI.CRC, 0, 2);
                                    TI.EmbededData = new byte[pTInfoa.EmbededDatalen];
                                    TI.EmbededDatalen = pTInfoa.EmbededDatalen;
                                    System.arraycopy(pTInfoa.EmbededData, 0, TI.EmbededData, 0, TI.EmbededDatalen);
                                    tagcnt[0] = 1;
                                    isbreak = true;
                                    break;
                                }
                            } else {
                                TI.ReadCnt = pTInfoa.ReadCnt;
                                TI.RSSI = pTInfoa.RSSI;
                                TI.AntennaID = pTInfoa.AntennaID;
                                TI.Frequency = pTInfoa.Frequency;
                                TI.TimeStamp = pTInfoa.TimeStamp;
                                System.arraycopy(pTInfoa.PC, 0, TI.PC, 0, 2);
                                System.arraycopy(pTInfoa.PC, 0, TI.PC, 0, 2);
                                TI.EpcId = new byte[pTInfoa.Epclen];
                                TI.Epclen = pTInfoa.Epclen;
                                System.arraycopy(pTInfoa.EpcId, 0, TI.EpcId, 0, TI.Epclen);
                                System.arraycopy(pTInfoa.CRC, 0, TI.CRC, 0, 2);
                                TI.EmbededData = new byte[pTInfoa.EmbededDatalen];
                                TI.EmbededDatalen = pTInfoa.EmbededDatalen;
                                if (pTInfoa.EmbededDatalen > 0) {
                                    System.arraycopy(pTInfoa.EmbededData, 0, TI.EmbededData, 0, TI.EmbededDatalen);
                                }
                                tagcnt[0] = 1;
                                isbreak = true;
                            }
                        }
                        i++;
                        c = 0;
                    }
                }
                if (isbreak || System.currentTimeMillis() - st >= timeout) {
                    break;
                }
                ERR2 = ERR;
            }
            if (!isbreak) {
                tagcnt[0] = 0;
            }
        }
        return ERR;
    }

    public READER_ERR TagInventory_BaseType(int[] ants, int antcnt, short timeout, byte[] outbuf, int[] tagcnt) {
        READER_ERR ERR;
        synchronized (this) {
            int re = this.japi.TagInventory_BaseType(this.hReader[0], ants, antcnt, timeout, outbuf, tagcnt);
            ERR = READER_ERR.valueOf(re);
        }
        return ERR;
    }

    public READER_ERR GetNextTag(TAGINFO TI) {
        READER_ERR ERR;
        synchronized (this) {
            byte[] tagbuf = new byte[230];
            int re = this.japi.GetNextTag_BaseType(this.hReader[0], tagbuf);
            ERR = READER_ERR.valueOf(re);
            if (ERR == READER_ERR.MT_OK_ERR) {
                int pos = 0 + 1;
                TI.ReadCnt = tagbuf[0] & 255;
                int pos2 = pos + 1;
                TI.RSSI = tagbuf[pos];
                int pos3 = pos2 + 1;
                TI.AntennaID = tagbuf[pos2];
                TI.Frequency = ((tagbuf[pos3] & 255) << 24) | ((tagbuf[pos3 + 1] & 255) << 16) | ((tagbuf[pos3 + 2] & 255) << 8) | (tagbuf[pos3 + 3] & 255);
                int pos4 = pos3 + 4;
                TI.TimeStamp = ((tagbuf[pos4] & 255) << 24) | ((tagbuf[pos4 + 1] & 255) << 16) | ((tagbuf[pos4 + 2] & 255) << 8) | (tagbuf[pos4 + 3] & 255);
                int pos5 = pos4 + 4;
                int pos6 = pos5 + 1;
                TI.Res[0] = tagbuf[pos5];
                int pos7 = pos6 + 1;
                TI.Res[1] = tagbuf[pos6];
                int epclen = (tagbuf[pos7] << 8) | tagbuf[pos7 + 1];
                int pos8 = pos7 + 2;
                int pos9 = pos8 + 1;
                TI.PC[0] = tagbuf[pos8];
                int pos10 = pos9 + 1;
                TI.PC[1] = tagbuf[pos9];
                TI.EpcId = new byte[epclen];
                TI.Epclen = (short) epclen;
                System.arraycopy(tagbuf, pos10, TI.EpcId, 0, epclen);
                int pos11 = pos10 + epclen;
                int pos12 = pos11 + 1;
                TI.CRC[0] = tagbuf[pos11];
                int pos13 = pos12 + 1;
                TI.CRC[1] = tagbuf[pos12];
                int emddatalen = (tagbuf[pos13] << 8) | tagbuf[pos13 + 1];
                int pos14 = pos13 + 2;
                TI.EmbededData = new byte[emddatalen];
                TI.EmbededDatalen = (short) emddatalen;
                if (emddatalen > 0) {
                    System.arraycopy(tagbuf, pos14, TI.EmbededData, 0, emddatalen);
                }
            }
        }
        return ERR;
    }

    public READER_ERR GetNextTag_BaseType(byte[] outbuf) {
        READER_ERR ERR;
        synchronized (this) {
            int re = this.japi.GetNextTag_BaseType(this.hReader[0], outbuf);
            ERR = READER_ERR.valueOf(re);
        }
        return ERR;
    }

    public READER_ERR LockTag(int ant, byte lockobjects, short locktypes, byte[] accesspasswd, short timeout) {
        READER_ERR ERR;
        synchronized (this) {
            int re = this.japi.LockTag(this.hReader[0], ant, lockobjects, locktypes, accesspasswd, timeout);
            ERR = READER_ERR.valueOf(re);
        }
        return ERR;
    }

    public READER_ERR KillTag(int ant, byte[] killpasswd, short timeout) {
        READER_ERR ERR;
        synchronized (this) {
            int re = this.japi.KillTag(this.hReader[0], ant, killpasswd, timeout);
            ERR = READER_ERR.valueOf(re);
        }
        return ERR;
    }

    public READER_ERR Lock180006BTag(int ant, int startblk, int blkcnt, short timeout) {
        READER_ERR ERR;
        synchronized (this) {
            int re = this.japi.Lock180006BTag(this.hReader[0], ant, startblk, blkcnt, timeout);
            ERR = READER_ERR.valueOf(re);
        }
        return ERR;
    }

    public READER_ERR BlockPermaLock(int ant, int readlock, int startblk, int blkrange, byte[] mask, byte[] pwd, short timeout) {
        READER_ERR ERR;
        synchronized (this) {
            int re = this.japi.BlockPermaLock(this.hReader[0], ant, readlock, startblk, blkrange, mask, pwd, timeout);
            ERR = READER_ERR.valueOf(re);
        }
        return ERR;
    }

    public READER_ERR BlockErase(int ant, int bank, int wordaddr, int wordcnt, byte[] pwd, short timeout) {
        READER_ERR ERR;
        synchronized (this) {
            int re = this.japi.BlockErase(this.hReader[0], ant, bank, wordaddr, wordcnt, pwd, timeout);
            ERR = READER_ERR.valueOf(re);
        }
        return ERR;
    }

    public READER_ERR EraseDataOnReader() {
        READER_ERR ERR;
        synchronized (this) {
            int re = this.japi.EraseDataOnReader(this.hReader[0]);
            ERR = READER_ERR.valueOf(re);
        }
        return ERR;
    }

    public READER_ERR SaveDataOnReader(int address, byte[] data, int datalen) {
        READER_ERR ERR;
        synchronized (this) {
            int re = this.japi.SaveDataOnReader(this.hReader[0], address, data, datalen);
            ERR = READER_ERR.valueOf(re);
        }
        return ERR;
    }

    public READER_ERR ReadDataOnReader(int address, byte[] data, int datalen) {
        READER_ERR ERR;
        synchronized (this) {
            int re = this.japi.ReadDataOnReader(this.hReader[0], address, data, datalen);
            ERR = READER_ERR.valueOf(re);
        }
        return ERR;
    }

    public READER_ERR CustomCmd(int ant, CustomCmdType cmdtype, Object CustomPara, Object CustomRet) {
        byte[] para;
        byte[] ret;
        synchronized (this) {
            int i = AnonymousClass1.$SwitchMap$com$uhf$api$cls$Reader$CustomCmdType[cmdtype.ordinal()];
            if (i == 1) {
                IMPINJM4QtPara CustomPara2 = (IMPINJM4QtPara) CustomPara;
                ret = new byte[10];
                para = new byte[]{0, 0, 0, 0, (byte) ((CustomPara2.CmdType & ViewCompat.MEASURED_STATE_MASK) >> 24), (byte) ((CustomPara2.CmdType & 16711680) >> 16), (byte) ((CustomPara2.CmdType & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8), (byte) (CustomPara2.CmdType & 255), (byte) ((CustomPara2.MemType & ViewCompat.MEASURED_STATE_MASK) >> 24), (byte) ((CustomPara2.MemType & 16711680) >> 16), (byte) ((CustomPara2.MemType & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8), (byte) (CustomPara2.MemType & 255), (byte) ((CustomPara2.PersistType & ViewCompat.MEASURED_STATE_MASK) >> 24), (byte) ((CustomPara2.PersistType & 16711680) >> 16), (byte) ((CustomPara2.PersistType & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8), (byte) (CustomPara2.PersistType & 255), (byte) ((CustomPara2.RangeType & ViewCompat.MEASURED_STATE_MASK) >> 24), (byte) ((CustomPara2.RangeType & 16711680) >> 16), (byte) ((CustomPara2.RangeType & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8), (byte) (CustomPara2.RangeType & 255), (byte) ((CustomPara2.TimeOut & 65280) >> 8), (byte) (CustomPara2.TimeOut & 255)};
                System.arraycopy(CustomPara2.AccessPwd, 0, para, 0, 4);
            } else if (i == 2) {
                ret = new byte[7];
                ALIENHiggs3BlockReadLockPara CustomPara22 = (ALIENHiggs3BlockReadLockPara) CustomPara;
                para = new byte[]{0, 0, 0, 0, CustomPara22.BlkBits, (byte) ((CustomPara22.TimeOut & 65280) >> 8), (byte) (CustomPara22.TimeOut & 255)};
                System.arraycopy(CustomPara22.AccessPwd, 0, para, 0, 4);
            } else if (i == 3) {
                ret = new byte[7];
                NXPChangeEASPara CustomPara23 = (NXPChangeEASPara) CustomPara;
                para = new byte[]{0, 0, 0, 0, (byte) CustomPara23.isSet, (byte) ((CustomPara23.TimeOut & 65280) >> 8), (byte) (CustomPara23.TimeOut & 255)};
                System.arraycopy(CustomPara23.AccessPwd, 0, para, 0, 4);
            } else if (i == 4) {
                ret = new byte[5];
                NXPEASAlarmPara CustomPara24 = (NXPEASAlarmPara) CustomPara;
                CustomPara24.MC = (byte) 11;
                para = new byte[]{CustomPara24.DR, 11, CustomPara24.TrExt, (byte) ((CustomPara24.TimeOut & 65280) >> 8), (byte) (CustomPara24.TimeOut & 255)};
            } else if (i == 5) {
                NXP_U8_InventoryModePara ctpara = (NXP_U8_InventoryModePara) CustomPara;
                para = ctpara.Mode;
                ret = new byte[1];
            } else {
                return READER_ERR.MT_OP_NOT_SUPPORTED;
            }
            int re = this.japi.CustomCmd_BaseType(this.hReader[0], ant, cmdtype.value(), para, ret);
            READER_ERR ERR = READER_ERR.valueOf(re);
            return ERR;
        }
    }

    public READER_ERR CustomCmd_BaseType(int ant, int cmdtype, byte[] CustomPara, byte[] CustomRet) {
        READER_ERR ERR;
        synchronized (this) {
            int re = this.japi.CustomCmd_BaseType(this.hReader[0], ant, cmdtype, CustomPara, CustomRet);
            ERR = READER_ERR.valueOf(re);
        }
        return ERR;
    }

    public READER_ERR SetGPO(int gpoid, int val) {
        READER_ERR ERR;
        synchronized (this) {
            int re = this.japi.SetGPO(this.hReader[0], gpoid, val);
            ERR = READER_ERR.valueOf(re);
        }
        return ERR;
    }

    public READER_ERR GetGPI(int gpoid, int[] val) {
        READER_ERR ERR;
        synchronized (this) {
            int re = this.japi.GetGPI(this.hReader[0], gpoid, val);
            ERR = READER_ERR.valueOf(re);
        }
        return ERR;
    }

    public READER_ERR GetGPIEx(GpiInfo_ST gpist) {
        READER_ERR ERR;
        synchronized (this) {
            byte[] gpibytes = new byte[500];
            int re = this.japi.GetGPIEx_BaseType(this.hReader[0], gpibytes);
            ERR = READER_ERR.valueOf(re);
            if (ERR == READER_ERR.MT_OK_ERR) {
                gpist.gpiCount = gpibytes[0];
                for (int i = 0; i < gpist.gpiCount; i++) {
                    gpist.gpiStats[i].GpiId = gpibytes[(i * 2) + 1];
                    gpist.gpiStats[i].State = gpibytes[(i * 2) + 2];
                }
            }
        }
        return ERR;
    }

    public READER_ERR PsamTransceiver(int soltid, int coslen, byte[] cos, int[] cosresplen, byte[] cosresp, byte[] errcode, short timeout) {
        READER_ERR ERR;
        synchronized (this) {
            int re = this.japi.PsamTransceiver(this.hReader[0], soltid, coslen, cos, cosresplen, cosresp, errcode, timeout);
            ERR = READER_ERR.valueOf(re);
        }
        return ERR;
    }

    private int GetIntFrByteArray(byte[] byarray, int offset) {
        return ((byarray[offset] & 255) << 24) | ((byarray[offset + 1] & 255) << 16) | ((byarray[offset + 2] & 255) << 8) | (byarray[offset + 3] & 255);
    }

    public READER_ERR ParamGet(Mtr_Param key, Object val) {
        int re;
        AntPowerConf antPowerConf;
        synchronized (this) {
            byte[] bArr = new byte[500];
            int i = 4;
            int i2 = 2;
            int i3 = 8;
            switch (AnonymousClass1.$SwitchMap$com$uhf$api$cls$Reader$Mtr_Param[key.ordinal()]) {
                case 1:
                    re = this.japi.ParamGet_BaseType(this.hReader[0], key.value(), bArr);
                    if (re == 0) {
                        ((AntPowerConf) val).antcnt = bArr[0];
                        for (int i4 = 0; i4 < bArr[0]; i4++) {
                            AntPower apcf = new AntPower();
                            apcf.antid = bArr[(i4 * 5) + 1];
                            apcf.readPower = (short) ((bArr[(i4 * 5) + 2] << 8) | (bArr[(i4 * 5) + 3] & 255));
                            apcf.writePower = (short) ((bArr[(i4 * 5) + 4] << 8) | (bArr[(i4 * 5) + 5] & 255));
                            ((AntPowerConf) val).Powers[i4] = apcf;
                        }
                        if (this.isfilterpw && (antPowerConf = this.setpower) != null) {
                            ((AntPowerConf) val).antcnt = antPowerConf.antcnt;
                            for (int i5 = 0; i5 < this.setpower.antcnt; i5++) {
                                ((AntPowerConf) val).Powers[i5].writePower = this.setpower.Powers[i5].writePower;
                                ((AntPowerConf) val).Powers[i5].readPower = this.setpower.Powers[i5].readPower;
                            }
                            break;
                        }
                    }
                    break;
                case 2:
                    re = this.japi.ParamGet_BaseType(this.hReader[0], key.value(), bArr);
                    if (re == 0) {
                        ((TagFilter_ST) val).bank = bArr[0];
                        ((TagFilter_ST) val).startaddr = ((bArr[1] & 255) << 24) | (bArr[2] << 16) | (bArr[3] << 8) | (bArr[4] & 255);
                        ((TagFilter_ST) val).flen = (bArr[5] << 24) | (bArr[6] << 16) | (bArr[7] << 8) | (bArr[8] & 255);
                        int ilen = ((TagFilter_ST) val).flen / 8;
                        if (((TagFilter_ST) val).flen % 8 != 0) {
                            ilen++;
                        }
                        System.arraycopy(bArr, 9, ((TagFilter_ST) val).fdata, 0, ilen);
                        ((TagFilter_ST) val).isInvert = bArr[ilen + 9];
                        break;
                    }
                    break;
                case 3:
                    re = this.japi.ParamGet_BaseType(this.hReader[0], key.value(), bArr);
                    if (re == 0) {
                        ((EmbededData_ST) val).bank = bArr[1];
                        ((EmbededData_ST) val).startaddr = (bArr[2] << 24) | (bArr[3] << 16) | (bArr[4] << 8) | (bArr[5] & 255);
                        ((EmbededData_ST) val).bytecnt = (bArr[6] << 24) | (bArr[7] << 16) | (bArr[8] << 8) | (bArr[9] & 255);
                        if (bArr[0] == 14) {
                            System.arraycopy(bArr, 10, ((EmbededData_ST) val).accesspwd, 0, 4);
                            break;
                        } else if (bArr[0] == 10) {
                            ((EmbededData_ST) val).accesspwd = null;
                            break;
                        }
                    }
                    break;
                case 4:
                    re = this.japi.ParamGet_BaseType(this.hReader[0], key.value(), bArr);
                    if (re == 0) {
                        ((Inv_Potls_ST) val).potlcnt = bArr[0];
                        ((Inv_Potls_ST) val).potls = new Inv_Potl[bArr[0]];
                        for (int i6 = 0; i6 < bArr[0]; i6++) {
                            ((Inv_Potls_ST) val).potls[i6] = new Inv_Potl();
                            ((Inv_Potls_ST) val).potls[i6].potl = SL_TagProtocol.valueOf(bArr[(i6 * 5) + 1]);
                            ((Inv_Potls_ST) val).potls[i6].weight = GetIntFrByteArray(bArr, (i6 * 5) + 2);
                        }
                        break;
                    }
                    break;
                case 5:
                    re = this.japi.ParamGet_BaseType(this.hReader[0], key.value(), bArr);
                    if (re == 0) {
                        ((ConnAnts_ST) val).antcnt = bArr[0];
                        for (int i7 = 0; i7 < bArr[0]; i7++) {
                            ((ConnAnts_ST) val).connectedants[i7] = bArr[i7 + 1];
                        }
                        break;
                    }
                    break;
                case 6:
                    re = this.japi.ParamGet_BaseType(this.hReader[0], key.value(), bArr);
                    if (re == 0) {
                        ReaderVersion rdrver = (ReaderVersion) val;
                        if (bArr[0] == 1 && bArr[1] == 0 && bArr[2] == 0) {
                            rdrver.hardwareVer = ((int) bArr[0]) + ".";
                            rdrver.hardwareVer += ((int) bArr[1]) + ".";
                            rdrver.hardwareVer += ((int) bArr[2]) + ".";
                            rdrver.hardwareVer += ((int) bArr[3]);
                            rdrver.softwareVer = ((int) bArr[4]) + ".";
                            rdrver.softwareVer += ((int) bArr[5]) + ".";
                            rdrver.softwareVer += ((int) bArr[6]) + ".";
                            rdrver.softwareVer += ((int) bArr[7]);
                        } else {
                            byte[] bArr2 = {bArr[0]};
                            rdrver.hardwareVer = bytes_Hexstr(bArr2) + ".";
                            bArr2[0] = bArr[1];
                            rdrver.hardwareVer += bytes_Hexstr(bArr2) + ".";
                            bArr2[0] = bArr[2];
                            rdrver.hardwareVer += bytes_Hexstr(bArr2) + ".";
                            bArr2[0] = bArr[3];
                            rdrver.hardwareVer += bytes_Hexstr(bArr2);
                            bArr2[0] = bArr[4];
                            rdrver.softwareVer = bytes_Hexstr(bArr2) + ".";
                            bArr2[0] = bArr[5];
                            rdrver.softwareVer += bytes_Hexstr(bArr2) + ".";
                            bArr2[0] = bArr[6];
                            rdrver.softwareVer += bytes_Hexstr(bArr2) + ".";
                            bArr2[0] = bArr[7];
                            rdrver.softwareVer += bytes_Hexstr(bArr2);
                        }
                        break;
                    }
                    break;
                case 7:
                    re = this.japi.ParamGet_BaseType(this.hReader[0], key.value(), bArr);
                    if (re == 0) {
                        int rgint = ((bArr[0] & 255) << 24) | ((bArr[1] & 255) << 16) | ((bArr[2] & 255) << 8) | (bArr[3] & 255);
                        ((Region_Conf[]) val)[0] = Region_Conf.valueOf(rgint);
                        break;
                    }
                    break;
                case 8:
                    re = this.japi.ParamGet_BaseType(this.hReader[0], key.value(), bArr);
                    if (re == 0) {
                        ((HoptableData_ST) val).lenhtb = bArr[0];
                        for (int i8 = 0; i8 < bArr[0]; i8++) {
                            ((HoptableData_ST) val).htb[i8] = ((bArr[(i8 * 4) + 1] & 255) << 24) | ((bArr[(i8 * 4) + 2] & 255) << 16) | ((bArr[(i8 * 4) + 3] & 255) << 8) | (bArr[(i8 * 4) + 4] & 255);
                        }
                        break;
                    }
                    break;
                case 9:
                    AntPortsVSWR apvswr = (AntPortsVSWR) val;
                    bArr[0] = (byte) apvswr.andid;
                    bArr[1] = (byte) apvswr.region.value();
                    bArr[2] = (byte) ((65280 & apvswr.power) >> 8);
                    bArr[3] = (byte) (apvswr.power & 255);
                    bArr[4] = (byte) ((apvswr.frecount >> 24) & 255);
                    bArr[5] = (byte) ((apvswr.frecount >> 16) & 255);
                    bArr[6] = (byte) ((apvswr.frecount >> 8) & 255);
                    bArr[7] = (byte) (apvswr.frecount & 255);
                    boolean islbt = (apvswr.frecount & InputDeviceCompat.SOURCE_ANY) > 0;
                    int count = apvswr.frecount & 255;
                    for (int i9 = 0; i9 < count; i9++) {
                        bArr[(i9 * 4) + 8] = (byte) ((apvswr.vswrs[i9].frequency >> 24) & 255);
                        bArr[(i9 * 4) + 8 + 1] = (byte) ((apvswr.vswrs[i9].frequency >> 16) & 255);
                        bArr[(i9 * 4) + 8 + 2] = (byte) ((apvswr.vswrs[i9].frequency >> 8) & 255);
                        bArr[(i9 * 4) + 8 + 3] = (byte) ((apvswr.vswrs[i9].frequency >> 0) & 255);
                    }
                    re = this.japi.ParamGet_BaseType(this.hReader[0], key.value(), bArr);
                    if (re == 0) {
                        apvswr.frecount = bArr[0] & 255;
                        int i10 = 0;
                        while (i10 < apvswr.frecount) {
                            apvswr.vswrs[i10].frequency = ((bArr[(i10 * 5) + 1] & 255) << 24) | ((bArr[((i10 * 5) + 1) + 1] & 255) << 16) | ((bArr[((i10 * 5) + 1) + i2] & 255) << i3) | (bArr[(i10 * 5) + 1 + 3] & 255);
                            if (islbt) {
                                apvswr.vswrs[i10].vswr = bArr[(i10 * 5) + 1 + i];
                            } else {
                                float rl = (float) Math.pow(10.0d, ((bArr[((i10 * 5) + 1) + i] & 255) / 10.0f) / 20.0f);
                                apvswr.vswrs[i10].vswr = (rl + 1.0f) / (rl - 1.0f);
                            }
                            i10++;
                            i = 4;
                            i2 = 2;
                            i3 = 8;
                        }
                    }
                    break;
                case 10:
                    re = this.japi.ParamGet_BaseType(this.hReader[0], key.value(), bArr);
                    if (re == 0) {
                        ((Reader_Ip) val).ip = new byte[bArr[0]];
                        ((Reader_Ip) val).mask = new byte[bArr[1]];
                        ((Reader_Ip) val).gateway = new byte[bArr[2]];
                        System.arraycopy(bArr, 3, ((Reader_Ip) val).ip, 0, bArr[0]);
                        System.arraycopy(bArr, bArr[0] + 3, ((Reader_Ip) val).mask, 0, bArr[1]);
                        System.arraycopy(bArr, bArr[0] + 3 + bArr[1], ((Reader_Ip) val).gateway, 0, bArr[2]);
                        break;
                    }
                    break;
                case 11:
                    CustomParam_ST cpst = (CustomParam_ST) val;
                    byte[] tmppname = null;
                    try {
                        tmppname = cpst.ParamName.getBytes("US-ASCII");
                    } catch (Exception e) {
                    }
                    System.arraycopy(tmppname, 0, bArr, 0, tmppname.length);
                    bArr[tmppname.length] = 0;
                    re = this.japi.ParamGet_BaseType(this.hReader[0], key.value(), bArr);
                    if (re == 0) {
                        int CParamlen = ((bArr[0] & 255) << 24) | ((bArr[1] & 255) << 16) | ((bArr[2] & 255) << 8) | (bArr[3] & 255);
                        cpst.ParamVal = new byte[CParamlen - 50];
                        System.arraycopy(bArr, 50, cpst.ParamVal, 0, CParamlen - 50);
                    }
                    break;
                case 12:
                case 13:
                    byte[] retdata = (byte[]) val;
                    re = this.japi.ParamGet_BaseType(this.hReader[0], key.value(), retdata);
                    break;
                case 14:
                    Default_Param dp = (Default_Param) val;
                    int mtspos = 0 + 1;
                    bArr[0] = (byte) dp.key.value();
                    int i11 = mtspos + 1;
                    bArr[mtspos] = (byte) (dp.isdefault ? 1 : 0);
                    if (dp.key != Mtr_Param.MTR_PARAM_SAVEINMODULE_BAUD && dp.key != Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION && dp.key != Mtr_Param.MTR_PARAM_POTL_GEN2_Q && dp.key != Mtr_Param.MTR_PARAM_RF_ANTPOWER && dp.key != Mtr_Param.MTR_PARAM_FREQUENCY_REGION) {
                        if (dp.key == Mtr_Param.MTR_PARAM_SAVEINMODULE) {
                            byte[] subb = dp.subkey.getBytes();
                            int mtspos2 = i11 + 1;
                            bArr[i11] = (byte) subb.length;
                            int i12 = 0;
                            while (i12 < subb.length) {
                                bArr[mtspos2] = subb[i12];
                                i12++;
                                mtspos2++;
                            }
                            i11 = mtspos2;
                        } else {
                            return READER_ERR.MT_INVALID_PARA;
                        }
                    }
                    re = this.japi.ParamGet_BaseType(this.hReader[0], key.value(), bArr);
                    if (re == 0) {
                        if (dp.key != Mtr_Param.MTR_PARAM_SAVEINMODULE_BAUD && dp.key != Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION && dp.key != Mtr_Param.MTR_PARAM_POTL_GEN2_Q) {
                            if (dp.key == Mtr_Param.MTR_PARAM_RF_ANTPOWER) {
                                AntPowerConf apc = new AntPowerConf();
                                int mtspos3 = i11 + 1;
                                apc.antcnt = bArr[i11];
                                AntPower[] ap = new AntPower[apc.antcnt];
                                for (int i13 = 0; i13 < apc.antcnt; i13++) {
                                    ap[i13] = new AntPower();
                                    int mtspos4 = mtspos3 + 1;
                                    ap[i13].antid = bArr[mtspos3];
                                    ap[i13].readPower = (short) (((bArr[mtspos4] & 255) << 8) | (bArr[mtspos4 + 1] & 255));
                                    int mtspos5 = mtspos4 + 2;
                                    ap[i13].writePower = (short) (((bArr[mtspos5] & 255) << 8) | (bArr[mtspos5 + 1] & 255));
                                    mtspos3 = mtspos5 + 2 + 2;
                                }
                                apc.Powers = ap;
                                dp.val = apc;
                            } else if (dp.key == Mtr_Param.MTR_PARAM_FREQUENCY_REGION) {
                                int i14 = i11 + 1;
                                dp.val = Byte.valueOf(bArr[i11]);
                            } else if (dp.key == Mtr_Param.MTR_PARAM_SAVEINMODULE) {
                                int mtspos6 = i11 + dp.subkey.getBytes().length + 1;
                                if (dp.subkey.equals("modulesave/hpupload")) {
                                    int[] pval = new int[6];
                                    for (int i15 = 0; i15 < 6; i15++) {
                                        int mtspos7 = mtspos6 + 1;
                                        bArr[mtspos6] = (byte) ((pval[i15] & ViewCompat.MEASURED_STATE_MASK) >> 24);
                                        int mtspos8 = mtspos7 + 1;
                                        bArr[mtspos7] = (byte) ((pval[i15] & 16711680) >> 16);
                                        int mtspos9 = mtspos8 + 1;
                                        bArr[mtspos8] = (byte) ((pval[i15] & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8);
                                        mtspos6 = mtspos9 + 1;
                                        bArr[mtspos9] = (byte) (pval[i15] & 255);
                                    }
                                    dp.val = pval;
                                } else {
                                    return READER_ERR.MT_INVALID_PARA;
                                }
                            }
                        }
                        int valint = ((bArr[i11] & 255) << 24) | ((bArr[i11 + 1] & 255) << 16) | ((bArr[i11 + 2] & 255) << 8) | (bArr[i11 + 3] & 255);
                        dp.val = Integer.valueOf(valint);
                    }
                    break;
                default:
                    re = this.japi.ParamGet_BaseType(this.hReader[0], key.value(), bArr);
                    if (re == 0) {
                        int[] resint = (int[]) val;
                        resint[0] = ((bArr[0] & 255) << 24) | ((bArr[1] & 255) << 16) | ((bArr[2] & 255) << 8) | (bArr[3] & 255);
                        break;
                    }
                    break;
            }
            READER_ERR ERR = READER_ERR.valueOf(re);
            return ERR;
        }
    }

    /* renamed from: com.uhf.api.cls.Reader$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$uhf$api$cls$Reader$CustomCmdType;
        static final /* synthetic */ int[] $SwitchMap$com$uhf$api$cls$Reader$Mtr_Param;

        static {
            int[] iArr = new int[Mtr_Param.values().length];
            $SwitchMap$com$uhf$api$cls$Reader$Mtr_Param = iArr;
            try {
                iArr[Mtr_Param.MTR_PARAM_RF_ANTPOWER.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$uhf$api$cls$Reader$Mtr_Param[Mtr_Param.MTR_PARAM_TAG_FILTER.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$uhf$api$cls$Reader$Mtr_Param[Mtr_Param.MTR_PARAM_TAG_EMBEDEDDATA.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$uhf$api$cls$Reader$Mtr_Param[Mtr_Param.MTR_PARAM_TAG_INVPOTL.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$uhf$api$cls$Reader$Mtr_Param[Mtr_Param.MTR_PARAM_READER_CONN_ANTS.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$uhf$api$cls$Reader$Mtr_Param[Mtr_Param.MTR_PARAM_READER_VERSION.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$uhf$api$cls$Reader$Mtr_Param[Mtr_Param.MTR_PARAM_FREQUENCY_REGION.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$uhf$api$cls$Reader$Mtr_Param[Mtr_Param.MTR_PARAM_FREQUENCY_HOPTABLE.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$uhf$api$cls$Reader$Mtr_Param[Mtr_Param.MTR_PARAM_RF_ANTPORTS_VSWR.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$uhf$api$cls$Reader$Mtr_Param[Mtr_Param.MTR_PARAM_READER_IP.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$uhf$api$cls$Reader$Mtr_Param[Mtr_Param.MTR_PARAM_CUSTOM.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$uhf$api$cls$Reader$Mtr_Param[Mtr_Param.MTR_PARAM_READER_WATCHDOG.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$com$uhf$api$cls$Reader$Mtr_Param[Mtr_Param.MTR_PARAM_READER_ERRORDATA.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$com$uhf$api$cls$Reader$Mtr_Param[Mtr_Param.MTR_PARAM_SAVEINMODULE.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$com$uhf$api$cls$Reader$Mtr_Param[Mtr_Param.MTR_PARAM_TAG_MULTISELECTORS.ordinal()] = 15;
            } catch (NoSuchFieldError e15) {
            }
            int[] iArr2 = new int[CustomCmdType.values().length];
            $SwitchMap$com$uhf$api$cls$Reader$CustomCmdType = iArr2;
            try {
                iArr2[CustomCmdType.IMPINJ_M4_Qt.ordinal()] = 1;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$com$uhf$api$cls$Reader$CustomCmdType[CustomCmdType.ALIEN_Higgs3_BlockReadLock.ordinal()] = 2;
            } catch (NoSuchFieldError e17) {
            }
            try {
                $SwitchMap$com$uhf$api$cls$Reader$CustomCmdType[CustomCmdType.NXP_ChangeEAS.ordinal()] = 3;
            } catch (NoSuchFieldError e18) {
            }
            try {
                $SwitchMap$com$uhf$api$cls$Reader$CustomCmdType[CustomCmdType.NXP_EASAlarm.ordinal()] = 4;
            } catch (NoSuchFieldError e19) {
            }
            try {
                $SwitchMap$com$uhf$api$cls$Reader$CustomCmdType[CustomCmdType.NXP_U8_InventoryMode.ordinal()] = 5;
            } catch (NoSuchFieldError e20) {
            }
            try {
                $SwitchMap$com$uhf$api$cls$Reader$CustomCmdType[CustomCmdType.NXP_SetReadProtect.ordinal()] = 6;
            } catch (NoSuchFieldError e21) {
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:165:0x062b A[Catch: all -> 0x0741, TryCatch #0 {all -> 0x0741, blocks: (B:6:0x0006, B:7:0x0024, B:8:0x0027, B:9:0x070d, B:15:0x002d, B:17:0x0033, B:19:0x0044, B:21:0x004c, B:24:0x0051, B:25:0x0055, B:27:0x0058, B:47:0x005c, B:48:0x005e, B:29:0x0060, B:31:0x0068, B:33:0x0072, B:35:0x0080, B:37:0x00bc, B:38:0x0095, B:42:0x00c1, B:43:0x00c3, B:51:0x00c5, B:52:0x00cf, B:54:0x00d3, B:56:0x012a, B:58:0x013c, B:59:0x0133, B:62:0x014a, B:63:0x015d, B:64:0x015f, B:66:0x0161, B:69:0x0179, B:71:0x0182, B:73:0x0188, B:76:0x0190, B:78:0x0196, B:79:0x01a2, B:81:0x01a6, B:84:0x029c, B:85:0x01f7, B:87:0x01fd, B:88:0x020b, B:90:0x0211, B:91:0x021e, B:93:0x0221, B:95:0x022b, B:97:0x0235, B:99:0x023c, B:102:0x0269, B:103:0x026b, B:105:0x026d, B:106:0x026f, B:108:0x0271, B:110:0x02af, B:111:0x02c6, B:114:0x02cb, B:116:0x02d6, B:119:0x030e, B:120:0x0360, B:121:0x036a, B:123:0x036e, B:125:0x03b6, B:126:0x03c9, B:127:0x03ff, B:128:0x0409, B:130:0x040d, B:132:0x0472, B:133:0x0485, B:135:0x048b, B:137:0x0495, B:138:0x049c, B:140:0x0512, B:141:0x051c, B:142:0x0498, B:143:0x052f, B:144:0x0542, B:146:0x0548, B:148:0x05c6, B:149:0x05c8, B:150:0x05f1, B:151:0x0604, B:154:0x060e, B:156:0x0612, B:158:0x061c, B:160:0x0625, B:165:0x062b, B:166:0x0637, B:168:0x063b, B:171:0x0666, B:173:0x066a, B:175:0x0674, B:176:0x067a, B:178:0x0684, B:180:0x068a, B:191:0x068f), top: B:5:0x0006 }] */
    /* JADX WARN: Removed duplicated region for block: B:187:0x06a0 A[Catch: all -> 0x0747, LOOP:10: B:185:0x069c->B:187:0x06a0, LOOP_END, TryCatch #1 {all -> 0x0747, blocks: (B:11:0x073b, B:12:0x073f, B:184:0x0693, B:185:0x069c, B:187:0x06a0, B:189:0x06fd, B:197:0x0745), top: B:4:0x0006 }] */
    /* JADX WARN: Removed duplicated region for block: B:191:0x068f A[Catch: all -> 0x0741, TRY_LEAVE, TryCatch #0 {all -> 0x0741, blocks: (B:6:0x0006, B:7:0x0024, B:8:0x0027, B:9:0x070d, B:15:0x002d, B:17:0x0033, B:19:0x0044, B:21:0x004c, B:24:0x0051, B:25:0x0055, B:27:0x0058, B:47:0x005c, B:48:0x005e, B:29:0x0060, B:31:0x0068, B:33:0x0072, B:35:0x0080, B:37:0x00bc, B:38:0x0095, B:42:0x00c1, B:43:0x00c3, B:51:0x00c5, B:52:0x00cf, B:54:0x00d3, B:56:0x012a, B:58:0x013c, B:59:0x0133, B:62:0x014a, B:63:0x015d, B:64:0x015f, B:66:0x0161, B:69:0x0179, B:71:0x0182, B:73:0x0188, B:76:0x0190, B:78:0x0196, B:79:0x01a2, B:81:0x01a6, B:84:0x029c, B:85:0x01f7, B:87:0x01fd, B:88:0x020b, B:90:0x0211, B:91:0x021e, B:93:0x0221, B:95:0x022b, B:97:0x0235, B:99:0x023c, B:102:0x0269, B:103:0x026b, B:105:0x026d, B:106:0x026f, B:108:0x0271, B:110:0x02af, B:111:0x02c6, B:114:0x02cb, B:116:0x02d6, B:119:0x030e, B:120:0x0360, B:121:0x036a, B:123:0x036e, B:125:0x03b6, B:126:0x03c9, B:127:0x03ff, B:128:0x0409, B:130:0x040d, B:132:0x0472, B:133:0x0485, B:135:0x048b, B:137:0x0495, B:138:0x049c, B:140:0x0512, B:141:0x051c, B:142:0x0498, B:143:0x052f, B:144:0x0542, B:146:0x0548, B:148:0x05c6, B:149:0x05c8, B:150:0x05f1, B:151:0x0604, B:154:0x060e, B:156:0x0612, B:158:0x061c, B:160:0x0625, B:165:0x062b, B:166:0x0637, B:168:0x063b, B:171:0x0666, B:173:0x066a, B:175:0x0674, B:176:0x067a, B:178:0x0684, B:180:0x068a, B:191:0x068f), top: B:5:0x0006 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public com.uhf.api.cls.Reader.READER_ERR ParamSet(com.uhf.api.cls.Reader.Mtr_Param r19, java.lang.Object r20) {
        /*
            Method dump skipped, instructions count: 1900
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.uhf.api.cls.Reader.ParamSet(com.uhf.api.cls.Reader$Mtr_Param, java.lang.Object):com.uhf.api.cls.Reader$READER_ERR");
    }

    public static String bytes_Hexstr(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        for (byte b : bArray) {
            String sTemp = Integer.toHexString(b & 255);
            if (sTemp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    public void Str2Hex(String buf, int len, byte[] hexbuf) {
        if (len == 0) {
            return;
        }
        for (int i = 0; i < len; i += 2) {
            byte hnx = (byte) "0123456789ABCDEF".indexOf(buf.toUpperCase().substring(i, i + 1));
            byte lnx = 0;
            if (i + 2 <= len) {
                lnx = (byte) "0123456789ABCDEF".indexOf(buf.toUpperCase().substring(i + 1, i + 2));
            }
            hexbuf[i % 2 == 0 ? i / 2 : (i / 2) + 1] = (byte) (((hnx << 4) & 255) | (lnx & 255));
        }
    }

    public void Str2Binary(String buf, int len, byte[] binarybuf) {
        if (len % 8 != 0) {
            return;
        }
        for (int i = 0; i < len; i += 8) {
            byte temp = 0;
            for (int j = 0; j < 8; j++) {
                temp = (byte) (((byte) (Byte.parseByte(buf.substring(i + j, (i + j) + 1)) << (7 - j))) | temp);
            }
            int j2 = i / 8;
            binarybuf[j2] = (byte) (binarybuf[j2] | temp);
        }
    }

    public READER_ERR AsyncStartReading(int[] ants, int antcnt, int option) {
        READER_ERR ERR;
        toDlogAPI("AsyncStartReading- [] " + String.valueOf(antcnt) + " " + String.valueOf(option));
        synchronized (this) {
            int re = this.japi.AsyncStartReading(this.hReader[0], ants, antcnt, option);
            ERR = READER_ERR.valueOf(re);
        }
        return ERR;
    }

    public READER_ERR AsyncStartReadingEx(int[] ants, int antcnt, int option, byte[] bdata) {
        READER_ERR ERR;
        toDlogAPI("AsyncStartReadingEx- [] " + String.valueOf(antcnt) + " " + String.valueOf(option));
        synchronized (this) {
            int re = this.japi.AsyncStartReadingEx(this.hReader[0], ants, antcnt, option, bdata);
            ERR = READER_ERR.valueOf(re);
        }
        return ERR;
    }

    public READER_ERR AsyncStopReading() {
        READER_ERR ERR;
        toDlogAPI("AsyncStopReading");
        synchronized (this) {
            int re = this.japi.AsyncStopReading(this.hReader[0]);
            ERR = READER_ERR.valueOf(re);
        }
        return ERR;
    }

    public READER_ERR AsyncStopReadingEx() {
        READER_ERR ERR;
        toDlogAPI("AsyncStopReadingEx");
        synchronized (this) {
            int re = this.japi.AsyncStopReadingEx(this.hReader[0]);
            ERR = READER_ERR.valueOf(re);
        }
        return ERR;
    }

    public READER_ERR AsyncGetTagCount(int[] tagcnt) {
        READER_ERR ERR;
        synchronized (this) {
            int re = this.japi.AsyncGetTagCount(this.hReader[0], tagcnt);
            ERR = READER_ERR.valueOf(re);
        }
        return ERR;
    }

    public READER_ERR AsyncGetNextTag(TAGINFO TI) {
        READER_ERR ERR;
        synchronized (this) {
            byte[] tagbuf = new byte[500];
            int re = this.japi.AsyncGetNextTag_BaseType(this.hReader[0], tagbuf);
            ERR = READER_ERR.valueOf(re);
            if (ERR == READER_ERR.MT_OK_ERR) {
                int pos = 0 + 1;
                TI.ReadCnt = tagbuf[0];
                int pos2 = pos + 1;
                TI.RSSI = tagbuf[pos];
                int pos3 = pos2 + 1;
                TI.AntennaID = tagbuf[pos2];
                TI.Frequency = ((tagbuf[pos3] & 255) << 24) | ((tagbuf[pos3 + 1] & 255) << 16) | ((tagbuf[pos3 + 2] & 255) << 8) | (tagbuf[pos3 + 3] & 255);
                int pos4 = pos3 + 4;
                TI.TimeStamp = ((tagbuf[pos4] & 255) << 24) | ((tagbuf[pos4 + 1] & 255) << 16) | ((tagbuf[pos4 + 2] & 255) << 8) | (tagbuf[pos4 + 3] & 255);
                int pos5 = pos4 + 4;
                int pos6 = pos5 + 1;
                TI.Res[0] = tagbuf[pos5];
                int pos7 = pos6 + 1;
                TI.Res[1] = tagbuf[pos6];
                int epclen = ((tagbuf[pos7] & 255) << 8) | (tagbuf[pos7 + 1] & 255);
                int pos8 = pos7 + 2;
                int pos9 = pos8 + 1;
                TI.PC[0] = tagbuf[pos8];
                int pos10 = pos9 + 1;
                TI.PC[1] = tagbuf[pos9];
                TI.EpcId = new byte[epclen];
                TI.Epclen = (short) epclen;
                System.arraycopy(tagbuf, pos10, TI.EpcId, 0, epclen);
                int pos11 = pos10 + epclen;
                int pos12 = pos11 + 1;
                TI.CRC[0] = tagbuf[pos11];
                int pos13 = pos12 + 1;
                TI.CRC[1] = tagbuf[pos12];
                int pos14 = pos13 + 1;
                TI.protocol = SL_TagProtocol.valueOf(tagbuf[pos13]);
                int emddatalen = ((tagbuf[pos14] & 255) << 8) | (tagbuf[pos14 + 1] & 255);
                int pos15 = pos14 + 2;
                TI.EmbededData = new byte[emddatalen];
                TI.EmbededDatalen = (short) emddatalen;
                if (emddatalen > 0) {
                    System.arraycopy(tagbuf, pos15, TI.EmbededData, 0, emddatalen);
                }
            }
        }
        return ERR;
    }

    public static String GetSDKVersion() {
        String ver = "jarVersion:20220322soVersion:" + JniModuleAPI.GetSDKVersion();
        return ver;
    }

    public static READER_ERR GetDeviceVersion(String serialpath, deviceVersion rdrver) {
        byte[] verdata = new byte[9];
        int ret = JniModuleAPI.GetDeviceVersion(serialpath, verdata);
        READER_ERR err = READER_ERR.valueOf(ret);
        if (err == READER_ERR.MT_OK_ERR) {
            if (verdata[0] == 1) {
                rdrver.hardwareVer = ((int) verdata[1]) + ".";
                rdrver.hardwareVer += ((int) verdata[2]) + ".";
                rdrver.hardwareVer += ((int) verdata[3]) + ".";
                rdrver.hardwareVer += ((int) verdata[4]);
                rdrver.softwareVer = ((int) verdata[5]) + ".";
                rdrver.softwareVer += ((int) verdata[6]) + ".";
                rdrver.softwareVer += ((int) verdata[7]) + ".";
                rdrver.softwareVer += ((int) verdata[8]);
            } else {
                byte[] v1by = {verdata[1]};
                rdrver.hardwareVer = bytes_Hexstr(v1by) + ".";
                v1by[0] = verdata[2];
                rdrver.hardwareVer += bytes_Hexstr(v1by) + ".";
                v1by[0] = verdata[3];
                rdrver.hardwareVer += bytes_Hexstr(v1by) + ".";
                v1by[0] = verdata[4];
                rdrver.hardwareVer += bytes_Hexstr(v1by);
                v1by[0] = verdata[5];
                rdrver.softwareVer = bytes_Hexstr(v1by) + ".";
                v1by[0] = verdata[6];
                rdrver.softwareVer += bytes_Hexstr(v1by) + ".";
                v1by[0] = verdata[7];
                rdrver.softwareVer += bytes_Hexstr(v1by) + ".";
                v1by[0] = verdata[8];
                rdrver.softwareVer += bytes_Hexstr(v1by);
            }
        }
        return err;
    }

    public class DeviceSerialNumber {
        public String serailNumber = "";

        public DeviceSerialNumber() {
        }
    }

    public READER_ERR GetSerialNumber(DeviceSerialNumber devsn) {
        String sn;
        synchronized (this) {
            CustomParam_ST cpara = new CustomParam_ST();
            cpara.ParamName = "reader/rdrdetails";
            READER_ERR ret = ParamGet(Mtr_Param.MTR_PARAM_CUSTOM, cpara);
            if (ret != READER_ERR.MT_OK_ERR) {
                return ret;
            }
            byte[] binhv = new byte[4];
            System.arraycopy(cpara.ParamVal, 28, binhv, 0, 4);
            String sn2 = "" + bytes_Hexstr(binhv);
            for (int i = 0; i < 12; i++) {
                sn2 = sn2 + ((int) cpara.ParamVal[i + 16]);
            }
            HardwareDetails hd = new HardwareDetails();
            GetHardwareDetails(hd);
            if (hd.board == MaindBoard_Type.MAINBOARD_ARM9) {
                sn = "A9" + sn2;
            } else if (hd.board == MaindBoard_Type.MAINBOARD_ARM7) {
                sn = "A7" + sn2;
            } else {
                sn = "SE" + sn2;
            }
            devsn.serailNumber = sn;
            return ret;
        }
    }

    public READER_ERR GetLastDetailError(ErrInfo ei) {
        READER_ERR ERR;
        synchronized (this) {
            byte[] bArr = new byte[500];
            int re = this.japi.GetLastDetailError_BaseType(this.hReader[0], bArr);
            ERR = READER_ERR.valueOf(re);
            if (ERR == READER_ERR.MT_OK_ERR) {
                ei.derrcode = ((bArr[0] & 255) << 24) | ((bArr[1] & 255) << 16) | ((bArr[2] & 255) << 8) | (bArr[3] & 255);
                byte[] estrbytes = new byte[bArr[4]];
                System.arraycopy(bArr, 5, estrbytes, 0, bArr[4]);
                try {
                    ei.errstr = new String(estrbytes, "ascii");
                } catch (Exception e) {
                }
            }
        }
        return ERR;
    }

    public READER_ERR ResetRfidModule() {
        READER_ERR ERR;
        synchronized (this) {
            int re = this.japi.ResetRfidModule(this.hReader[0]);
            ERR = READER_ERR.valueOf(re);
        }
        return ERR;
    }

    public static READER_ERR FirmwareLoadFromSerialPort(String serialportPath, String firmwarePath) {
        int re = JniModuleAPI.FirmwareLoadFromSerialPort(serialportPath, firmwarePath);
        READER_ERR ERR = READER_ERR.valueOf(re);
        return ERR;
    }

    public static READER_ERR RebootReader(String readeraddr) {
        int re = JniModuleAPI.RebootReader(readeraddr);
        READER_ERR ERR = READER_ERR.valueOf(re);
        return ERR;
    }

    public int DataTransportSend(byte[] data, int datalen, int timeout) {
        int DataTransportSend;
        synchronized (this) {
            DataTransportSend = this.japi.DataTransportSend(this.hReader[0], data, datalen, timeout);
        }
        return DataTransportSend;
    }

    public int DataTransportRecv(byte[] data, int datalen, int timeout) {
        int DataTransportRecv;
        synchronized (this) {
            DataTransportRecv = this.japi.DataTransportRecv(this.hReader[0], data, datalen, timeout);
        }
        return DataTransportRecv;
    }

    public READER_ERR ReadTagTemperature(int ant, char bank, int address, int wordcnt, int timeout, int timeselwait, int timereadwait, short metaflag, byte[] accesspasswd, R2000_calibration.Tagtemperture_DATA tagtemp) {
        synchronized (this) {
            try {
                try {
                    byte[] data = new byte[300];
                    int[] datalen = new int[1];
                    int re = this.japi.ReadTagTemperature(this.hReader[0], ant, bank, address, wordcnt, timeout + timeselwait + timereadwait, timeselwait, timereadwait, metaflag, accesspasswd, data, datalen);
                    if (re == 0) {
                        R2000_calibration.Tagtemperture_DATA tagtemp2 = new R2000_calibration().new Tagtemperture_DATA(data, wordcnt);
                        tagtemp.pvtAntenna = tagtemp2.pvtAntenna;
                        tagtemp.pvtFrequency = tagtemp2.pvtFrequency;
                        tagtemp.pvtLqi = tagtemp2.pvtLqi;
                        tagtemp.pvtPhase = tagtemp2.pvtPhase;
                        tagtemp.pvtPro = tagtemp2.pvtPro;
                        tagtemp.pvtReadCount = tagtemp2.pvtReadCount;
                        tagtemp.pvtTsmp = tagtemp2.pvtTsmp;
                        tagtemp.tagcrc = new byte[2];
                        tagtemp.tagpc = new byte[2];
                        tagtemp.tagepc = new byte[tagtemp2.tagepc.length];
                        System.arraycopy(tagtemp2.tagcrc, 0, tagtemp.tagcrc, 0, 2);
                        System.arraycopy(tagtemp2.tagepc, 0, tagtemp.tagepc, 0, tagtemp2.tagepc.length);
                        System.arraycopy(tagtemp2.tagpc, 0, tagtemp.tagpc, 0, 2);
                        if (tagtemp2.BankData != null) {
                            tagtemp.BankData = new byte[tagtemp2.BankData.length];
                            System.arraycopy(tagtemp2.BankData, 0, tagtemp.BankData, 0, tagtemp2.BankData.length);
                        }
                        tagtemp.temperdata = new byte[tagtemp2.temperdata.length];
                        System.arraycopy(tagtemp2.temperdata, 0, tagtemp.temperdata, 0, tagtemp2.temperdata.length);
                    }
                    READER_ERR ERR = READER_ERR.valueOf(re);
                    return ERR;
                } catch (Throwable th) {
                    th = th;
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                throw th;
            }
        }
    }

    public READER_ERR ReadTagLED(int ant, short timeout, short metaflag, R2000_calibration.TagLED_DATA tagled) {
        READER_ERR ERR;
        synchronized (this) {
            byte[] data = new byte[300];
            int[] datalen = new int[1];
            int re = this.japi.ReadTagLED(this.hReader[0], ant, metaflag, timeout, data, datalen);
            if (re == 0) {
                R2000_calibration.TagLED_DATA tagled2 = new R2000_calibration().new TagLED_DATA(data, datalen[0]);
                tagled.pvtAntenna = tagled2.pvtAntenna;
                tagled.pvtFrequency = tagled2.pvtFrequency;
                tagled.pvtLqi = tagled2.pvtLqi;
                tagled.pvtPhase = tagled2.pvtPhase;
                tagled.pvtPro = tagled2.pvtPro;
                tagled.pvtReadCount = tagled2.pvtReadCount;
                tagled.pvtTsmp = tagled2.pvtTsmp;
                tagled.tagcrc = new byte[2];
                tagled.tagpc = new byte[2];
                tagled.tagepc = new byte[tagled2.tagepc.length];
                System.arraycopy(tagled2.tagcrc, 0, tagled.tagcrc, 0, 2);
                System.arraycopy(tagled2.tagepc, 0, tagled.tagepc, 0, tagled2.tagepc.length);
                System.arraycopy(tagled2.tagpc, 0, tagled.tagpc, 0, 2);
                if (tagled2.BankData != null) {
                    tagled.BankData = new byte[tagled2.BankData.length];
                    System.arraycopy(tagled2.BankData, 0, tagled.BankData, 0, tagled2.BankData.length);
                }
            }
            ERR = READER_ERR.valueOf(re);
        }
        return ERR;
    }

    int SetFilterSessioninTargetA(int[] ants, int fre, int power) {
        R2000_calibration.FilterS2inA_DATA fsa = new R2000_calibration().new FilterS2inA_DATA(ants, fre, power);
        R2000_calibration r2cb = new R2000_calibration();
        byte[] data = r2cb.GetSendCmd(R2000_calibration.R2000cmd.S2TA, fsa.ToByteData());
        MsgObj hMsg = new MsgObj();
        int re = SendandRev(data, 1000, hMsg);
        if (re != 0) {
            return re;
        }
        return (hMsg.status[0] << 8) | hMsg.status[1];
    }

    READER_ERR FlushDummyData2Mod() {
        byte[] zerobuf = new byte[255];
        zerobuf[0] = -1;
        zerobuf[1] = -6;
        zerobuf[2] = 0;
        for (int i = 3; i < 255; i++) {
            zerobuf[i] = 0;
        }
        DataTransportSend(zerobuf, 255, 2000);
        return READER_ERR.MT_OK_ERR;
    }

    class MsgObj {
        public byte[] soh = new byte[1];
        public byte[] dataLen = new byte[1];
        public byte[] opCode = new byte[1];
        public byte[] status = new byte[2];
        public byte[] crc = new byte[2];
        public byte[] data = new byte[ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION];

        MsgObj() {
        }

        public byte[] getcheckcrcdata() {
            byte[] bArr = this.dataLen;
            byte[] crcb = new byte[bArr[0] + 4];
            crcb[0] = bArr[0];
            int p = 0 + 1;
            int p2 = p + 1;
            crcb[p] = this.opCode[0];
            int p3 = p2 + 1;
            byte[] bArr2 = this.status;
            crcb[p2] = bArr2[0];
            int p4 = p3 + 1;
            crcb[p3] = bArr2[1];
            int i = 0;
            while (i < this.dataLen[0]) {
                crcb[p4] = this.data[i];
                i++;
                p4++;
            }
            return crcb;
        }
    }

    READER_ERR TestModLive() {
        READER_ERR err = READER_ERR.MT_OK_ERR;
        byte[] cmd = {-1, 0, 3, 29, 12};
        byte[] resp = new byte[50];
        byte[] resp2 = new byte[50];
        DataTransportSend(cmd, cmd.length, 1000);
        if (DataTransportRecv(resp, 5, 1000) == -1) {
            return READER_ERR.MT_CMD_FAILED_ERR;
        }
        if (DataTransportRecv(resp2, resp[1] + 2, 1000) == -1) {
            return READER_ERR.MT_CMD_FAILED_ERR;
        }
        return err;
    }

    private int SendandRev(byte[] data, int timeout, MsgObj hMsg) {
        toDlogAPI("send:" + bytes_Hexstr(data));
        int re = DataTransportSend(data, data.length, timeout);
        if (re != 0) {
            return 65277;
        }
        READER_ERR err = READER_ERR.MT_OK_ERR;
        int ret = DataTransportRecv(hMsg.soh, 1, 1000);
        String revstr = "" + bytes_Hexstr(hMsg.soh);
        if (ret == -2 || ret == -3) {
            return 65277;
        }
        if (ret == -1) {
            return READER_ERR.MT_IO_ERR.value();
        }
        if (ret == -4) {
            if (FlushDummyData2Mod() != READER_ERR.MT_OK_ERR) {
                return READER_ERR.MT_IO_ERR.value();
            }
            return TestModLive() == READER_ERR.MT_OK_ERR ? 65277 : 65278;
        }
        if ((hMsg.soh[0] & 255) == 255) {
            if (DataTransportRecv(hMsg.dataLen, 1, 1000) == -1) {
                return 65277;
            }
            String revstr2 = revstr + bytes_Hexstr(hMsg.dataLen);
            if (DataTransportRecv(hMsg.opCode, 1, 1000) == -1) {
                return 65277;
            }
            String revstr3 = revstr2 + bytes_Hexstr(hMsg.opCode);
            if (DataTransportRecv(hMsg.status, 2, 1000) == -1) {
                return 65277;
            }
            String revstr4 = revstr3 + bytes_Hexstr(hMsg.status);
            if (hMsg.dataLen[0] > 0) {
                if (DataTransportRecv(hMsg.data, hMsg.dataLen[0], 1000) == -1) {
                    return 65277;
                }
                byte[] fdata = new byte[hMsg.dataLen[0]];
                System.arraycopy(hMsg.data, 0, fdata, 0, hMsg.dataLen[0]);
                revstr4 = revstr4 + bytes_Hexstr(fdata);
            }
            if (DataTransportRecv(hMsg.crc, 2, 1000) == -1) {
                return 65277;
            }
            toDlogAPI("revd:" + revstr4 + bytes_Hexstr(hMsg.crc));
            short scrc = (short) (((hMsg.crc[0] & 255) << 8) | (255 & hMsg.crc[1]));
            if (R2000_calibration.calcCrc_short(hMsg.getcheckcrcdata()) != scrc) {
                if (FlushDummyData2Mod() != READER_ERR.MT_OK_ERR) {
                    return READER_ERR.MT_IO_ERR.value();
                }
                return 65277;
            }
            if (err != READER_ERR.MT_OK_ERR && FlushDummyData2Mod() != READER_ERR.MT_OK_ERR) {
                return READER_ERR.MT_IO_ERR.value();
            }
            return 0;
        }
        if (FlushDummyData2Mod() != READER_ERR.MT_OK_ERR) {
            return READER_ERR.MT_IO_ERR.value();
        }
        return 65277;
    }

    public void addReadListener(ReadListener listener) {
        this.readListeners.add(listener);
    }

    public void removeReadListener(ReadListener listener) {
        this.readListeners.remove(listener);
    }

    public void addReadExceptionListener(ReadExceptionListener listener) {
        this.readExceptionListeners.add(listener);
    }

    public void removeReadExceptionListener(ReadExceptionListener listener) {
        this.readExceptionListeners.remove(listener);
    }

    public void addGpiTriggerListener(GpiTriggerListener listener) {
        this.gpitriListener.add(listener);
    }

    public void removeGpiTriggerListener(GpiTriggerListener listener) {
        this.gpitriListener.remove(listener);
    }

    public void addGpiTriggerBoundaryListener(GpiTriggerBoundaryListener listener) {
        this.gpitriboundListener.add(listener);
    }

    public void removeGpiTriggerBoundaryListener(GpiTriggerBoundaryListener listener) {
        this.gpitriboundListener.remove(listener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean GpiTriContains(GpiInfo_ST littleGInfo, GpiInfo_ST bigGInfo) {
        for (int i = 0; i < littleGInfo.gpiCount; i++) {
            if (bigGInfo.gpiStats[littleGInfo.gpiStats[i].GpiId - 1].State != littleGInfo.gpiStats[i].State) {
                return false;
            }
        }
        return true;
    }

    public READER_ERR StartReading(int[] ants, int antcnt, BackReadOption pBRO) {
        int maxgpiid;
        StringBuilder sb = new StringBuilder();
        sb.append("StartReading- [] ");
        sb.append(String.valueOf(antcnt));
        sb.append(" ");
        sb.append(pBRO.IsFastRead ? "t" : "f");
        toDlogAPI(sb.toString());
        CLOGS();
        READER_ERR ERR = READER_ERR.MT_OK_ERR;
        if (this.m_IsReadingForAll) {
            return READER_ERR.MT_OP_EXECING;
        }
        this.m_IsReadingForAll = true;
        this.m_BackReadOp = pBRO;
        if (pBRO.IsGPITrigger) {
            if ((pBRO.GpiTrigger.TriggerType == GpiTrigger_Type.GPITRIGGER_TRI1START_TIMEOUTSTOP || pBRO.GpiTrigger.TriggerType == GpiTrigger_Type.GPITRIGGER_TRI1ORTRI2START_TIMEOUTSTOP) && pBRO.GpiTrigger.StopTriggerTimeout < 5) {
                return READER_ERR.MT_INVALID_PARA;
            }
            HardwareDetails hd = new HardwareDetails();
            GetHardwareDetails(hd);
            if (hd.logictype == Reader_Type.MODULE_ARM7_FOUR_ANTS || hd.logictype == Reader_Type.M6E_ARM7_FOUR_ANTS || hd.logictype == Reader_Type.MODULE_ARM7_TWO_ANTS || hd.logictype == Reader_Type.SL_COMMN_READER) {
                maxgpiid = 4;
            } else {
                maxgpiid = 2;
            }
            if (pBRO.GpiTrigger.GpiTrigger1States.gpiCount > maxgpiid) {
                return READER_ERR.MT_INVALID_PARA;
            }
            for (int i = 0; i < pBRO.GpiTrigger.GpiTrigger1States.gpiCount; i++) {
                if (pBRO.GpiTrigger.GpiTrigger1States.gpiStats[i].GpiId < 1 || pBRO.GpiTrigger.GpiTrigger1States.gpiStats[i].GpiId > maxgpiid) {
                    return READER_ERR.MT_INVALID_PARA;
                }
            }
            if (pBRO.GpiTrigger.TriggerType == GpiTrigger_Type.GPITRIGGER_TRI1START_TRI2STOP) {
                if (pBRO.GpiTrigger.GpiTrigger2States.gpiCount > maxgpiid) {
                    return READER_ERR.MT_INVALID_PARA;
                }
                for (int i2 = 0; i2 < pBRO.GpiTrigger.GpiTrigger2States.gpiCount; i2++) {
                    if (pBRO.GpiTrigger.GpiTrigger2States.gpiStats[i2].GpiId < 1 || pBRO.GpiTrigger.GpiTrigger2States.gpiStats[i2].GpiId > maxgpiid) {
                        return READER_ERR.MT_INVALID_PARA;
                    }
                }
            }
        }
        if (this.m_BackReadOp.IsFastRead) {
            short flags = 0;
            if (this.m_BackReadOp.TMFlags.IsReadCnt) {
                flags = (short) (0 | 1);
            }
            if (this.m_BackReadOp.TMFlags.IsRSSI) {
                flags = (short) (flags | 2);
            }
            if (this.m_BackReadOp.TMFlags.IsAntennaID) {
                flags = (short) (flags | 4);
            }
            if (this.m_BackReadOp.TMFlags.IsFrequency) {
                flags = (short) (flags | 8);
            }
            if (this.m_BackReadOp.TMFlags.IsTimestamp) {
                flags = (short) (flags | 16);
            }
            if (this.m_BackReadOp.TMFlags.IsRFU) {
                flags = (short) (flags | 32);
            }
            if (this.m_BackReadOp.TMFlags.IsEmdData) {
                flags = (short) (flags | 128);
            }
            this.m_BackReadOp.ReadDuration = (short) 0;
            this.m_BackReadOp.ReadInterval = 50;
            this.m_FastReadOption = (flags << 8) | this.m_BackReadOp.FastReadDutyRation | 128;
            if (!this.m_BackReadOp.IsGPITrigger) {
                int re = this.japi.AsyncStartReading(this.hReader[0], ants, antcnt, this.m_FastReadOption);
                ERR = errhandle(re);
                if (ERR != READER_ERR.MT_OK_ERR) {
                    return ERR;
                }
            }
        }
        this.m_BackReadAntsCnt = antcnt;
        for (int i3 = 0; i3 < antcnt; i3++) {
            this.m_BackReadAnts[i3] = ants[i3];
        }
        Tagnotify tf = new Tagnotify(this);
        Thread thread = new Thread(tf);
        this.m_ThreadForAll = thread;
        this.m_ThreadForAllid = thread.getId();
        this.m_ThreadForAll.start();
        return ERR;
    }

    public READER_ERR StopReading() {
        READER_ERR ERR;
        READER_ERR ERR2;
        toDlogAPI("StopReading");
        if (!this.m_IsReadingForAll) {
            return READER_ERR.MT_OK_ERR;
        }
        this.m_IsReadingForAll = false;
        Thread current = Thread.currentThread();
        long id = current.getId();
        this.m_ThreadForAllid = id;
        if (id == this.m_ThreadForAll.getId()) {
            if (this.m_BackReadOp.IsFastRead && (ERR2 = AsyncStopReading()) != READER_ERR.MT_OK_ERR) {
                ALOGS("err 1892" + ERR2.toString());
                return ERR2;
            }
            return READER_ERR.MT_OK_ERR;
        }
        while (this.m_IsReadThRunning) {
            try {
                Thread.sleep(20L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.m_ThreadForAll = null;
        if (this.m_BackReadOp.IsFastRead && (ERR = AsyncStopReading()) != READER_ERR.MT_OK_ERR) {
            ALOGS("err 1920" + ERR.toString());
            return ERR;
        }
        return READER_ERR.MT_OK_ERR;
    }

    private class Exceptionotify implements Runnable {
        READER_ERR re;
        Reader reader;

        public Exceptionotify(Reader rd, READER_ERR rer) {
            this.reader = rd;
            this.re = rer;
        }

        @Override // java.lang.Runnable
        public void run() {
            for (ReadExceptionListener rel : Reader.this.readExceptionListeners) {
                rel.tagReadException(this.reader, this.re);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public READER_ERR errhandle(int re) {
        READER_ERR err = READER_ERR.valueOf(re);
        if (err != READER_ERR.MT_OK_ERR) {
            this.m_gError = re;
            if (this.m_BackReadOp.IsFastRead) {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                }
                AsyncStopReading();
            }
            this.m_IsReadingForAll = false;
            if (this.readExceptionListeners.size() > 0) {
                Exceptionotify exfy = new Exceptionotify(this, err);
                Thread tread = new Thread(exfy);
                tread.start();
            }
        }
        return err;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public READER_ERR errhandle2(READER_ERR err) {
        if (err != READER_ERR.MT_OK_ERR) {
            if (this.m_BackReadOp.IsFastRead) {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                }
                AsyncStopReading();
            }
            this.m_IsReadingForAll = false;
            if (this.readExceptionListeners.size() > 0) {
                Exceptionotify exfy = new Exceptionotify(this, err);
                Thread tread = new Thread(exfy);
                tread.start();
            }
        }
        return err;
    }

    private class Tagnotify implements Runnable {
        Reader reader;

        public Tagnotify(Reader rd) {
            this.reader = rd;
        }

        /* JADX WARN: Removed duplicated region for block: B:104:0x04a9 A[Catch: all -> 0x04dd, LOOP:4: B:102:0x04a3->B:104:0x04a9, LOOP_END, TRY_LEAVE, TryCatch #2 {, blocks: (B:5:0x0005, B:7:0x0025, B:10:0x0033, B:12:0x003a, B:15:0x0042, B:185:0x0048, B:188:0x006f, B:190:0x0079, B:192:0x0084, B:193:0x008c, B:195:0x0092, B:204:0x00f2, B:206:0x00fc, B:207:0x0104, B:209:0x010a, B:213:0x0135, B:214:0x013f, B:216:0x0147, B:70:0x0361, B:126:0x036c, B:130:0x03a7, B:79:0x0403, B:80:0x0409, B:82:0x040e, B:84:0x041b, B:86:0x0423, B:88:0x0433, B:90:0x0479, B:92:0x046b, B:93:0x0471, B:97:0x0482, B:99:0x0498, B:101:0x049b, B:102:0x04a3, B:104:0x04a9, B:106:0x04b5, B:109:0x04be, B:118:0x0453, B:73:0x03c9, B:218:0x0138, B:221:0x00aa, B:223:0x00b2, B:225:0x00bc, B:227:0x00c7, B:228:0x00cf, B:230:0x00d5, B:18:0x0175, B:143:0x017f, B:149:0x01a5, B:152:0x01af, B:154:0x01b9, B:155:0x01c1, B:157:0x01c7, B:160:0x01dd, B:162:0x01e8, B:166:0x020b, B:168:0x0215, B:169:0x021d, B:171:0x0223, B:21:0x024e, B:23:0x0252, B:25:0x0258, B:34:0x0286, B:35:0x0290, B:37:0x0298, B:39:0x02a2, B:40:0x02aa, B:42:0x02b0, B:46:0x02d9, B:49:0x02ee, B:51:0x02f9, B:54:0x031b, B:56:0x0325, B:57:0x032d, B:59:0x0333, B:136:0x028d, B:243:0x04d6, B:244:0x04db), top: B:4:0x0005 }] */
        /* JADX WARN: Removed duplicated region for block: B:123:0x047f A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:125:0x036c A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:200:0x016a A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:203:0x00f2 A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:72:0x03c9 A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:82:0x040e A[Catch: all -> 0x04dd, TryCatch #2 {, blocks: (B:5:0x0005, B:7:0x0025, B:10:0x0033, B:12:0x003a, B:15:0x0042, B:185:0x0048, B:188:0x006f, B:190:0x0079, B:192:0x0084, B:193:0x008c, B:195:0x0092, B:204:0x00f2, B:206:0x00fc, B:207:0x0104, B:209:0x010a, B:213:0x0135, B:214:0x013f, B:216:0x0147, B:70:0x0361, B:126:0x036c, B:130:0x03a7, B:79:0x0403, B:80:0x0409, B:82:0x040e, B:84:0x041b, B:86:0x0423, B:88:0x0433, B:90:0x0479, B:92:0x046b, B:93:0x0471, B:97:0x0482, B:99:0x0498, B:101:0x049b, B:102:0x04a3, B:104:0x04a9, B:106:0x04b5, B:109:0x04be, B:118:0x0453, B:73:0x03c9, B:218:0x0138, B:221:0x00aa, B:223:0x00b2, B:225:0x00bc, B:227:0x00c7, B:228:0x00cf, B:230:0x00d5, B:18:0x0175, B:143:0x017f, B:149:0x01a5, B:152:0x01af, B:154:0x01b9, B:155:0x01c1, B:157:0x01c7, B:160:0x01dd, B:162:0x01e8, B:166:0x020b, B:168:0x0215, B:169:0x021d, B:171:0x0223, B:21:0x024e, B:23:0x0252, B:25:0x0258, B:34:0x0286, B:35:0x0290, B:37:0x0298, B:39:0x02a2, B:40:0x02aa, B:42:0x02b0, B:46:0x02d9, B:49:0x02ee, B:51:0x02f9, B:54:0x031b, B:56:0x0325, B:57:0x032d, B:59:0x0333, B:136:0x028d, B:243:0x04d6, B:244:0x04db), top: B:4:0x0005 }] */
        @Override // java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void run() {
            /*
                Method dump skipped, instructions count: 1250
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: com.uhf.api.cls.Reader.Tagnotify.run():void");
        }
    }

    public READER_ERR AsyncStartReading_IT(IT_MODE it_mode, int[] ants, int antcnt, int option) {
        this.it_mode_V = it_mode;
        if (it_mode == IT_MODE.IT_MODE_CT) {
            return AsyncStartReading_IT_CT(ants, antcnt, option);
        }
        if (this.it_mode_V == IT_MODE.IT_MODE_E7) {
            return AsyncStartReading_IT_E7(ants, antcnt, option);
        }
        if (this.it_mode_V == IT_MODE.IT_MODE_E7v2) {
            return AsyncStartReading_IT_E7v2(ants, antcnt, option);
        }
        return AsyncStartReading_IT_S2(ants, antcnt, option);
    }

    public READER_ERR AsyncStartReading_IT_CT(int[] ants, int antcnt, int option) {
        Region_Conf[] rcf2 = new Region_Conf[1];
        READER_ERR er = ParamGet(Mtr_Param.MTR_PARAM_FREQUENCY_REGION, rcf2);
        if (er == READER_ERR.MT_OK_ERR && (rcf2[0] == Region_Conf.RG_NA || rcf2[0] == Region_Conf.RG_PRC)) {
            HoptableData_ST hdst2 = new HoptableData_ST();
            READER_ERR er2 = ParamGet(Mtr_Param.MTR_PARAM_FREQUENCY_HOPTABLE, hdst2);
            boolean isrestart = false;
            if (er2 == READER_ERR.MT_OK_ERR) {
                if (rcf2[0] == Region_Conf.RG_NA) {
                    if (hdst2.lenhtb == 50) {
                        isrestart = true;
                    } else if (hdst2.lenhtb == this.IT_CT_fres_NA.length) {
                        for (int i = 0; i < hdst2.lenhtb; i++) {
                            boolean iscontain = false;
                            int j = 0;
                            while (true) {
                                if (j >= this.IT_CT_fres_NA.length) {
                                    break;
                                }
                                if (hdst2.htb[i] != this.IT_CT_fres_NA[j]) {
                                    j++;
                                } else {
                                    iscontain = true;
                                    break;
                                }
                            }
                            if (!iscontain) {
                                break;
                            }
                            if (i == hdst2.lenhtb - 1) {
                                isrestart = true;
                            }
                        }
                    }
                    if (isrestart) {
                        HoptableData_ST hdst = new HoptableData_ST();
                        hdst.lenhtb = this.IT_CT_fres_NA.length;
                        hdst.htb = this.IT_CT_fres_NA;
                        er2 = ParamSet(Mtr_Param.MTR_PARAM_FREQUENCY_HOPTABLE, hdst);
                        if (er2 != READER_ERR.MT_OK_ERR) {
                            return er2;
                        }
                    }
                } else if (rcf2[0] == Region_Conf.RG_PRC) {
                    if (hdst2.lenhtb == 16) {
                        isrestart = true;
                    } else if (hdst2.lenhtb == this.IT_CT_fres_cn.length) {
                        for (int i2 = 0; i2 < hdst2.lenhtb; i2++) {
                            boolean iscontain2 = false;
                            int j2 = 0;
                            while (true) {
                                if (j2 >= this.IT_CT_fres_NA.length) {
                                    break;
                                }
                                if (hdst2.htb[i2] != this.IT_CT_fres_NA[j2]) {
                                    j2++;
                                } else {
                                    iscontain2 = true;
                                    break;
                                }
                            }
                            if (!iscontain2) {
                                break;
                            }
                            if (i2 == hdst2.lenhtb - 1) {
                                isrestart = true;
                            }
                        }
                    }
                    if (isrestart) {
                        HoptableData_ST hdst3 = new HoptableData_ST();
                        hdst3.lenhtb = this.IT_CT_fres_cn.length;
                        hdst3.htb = this.IT_CT_fres_cn;
                        er2 = ParamSet(Mtr_Param.MTR_PARAM_FREQUENCY_HOPTABLE, hdst3);
                        if (er2 != READER_ERR.MT_OK_ERR) {
                            return er2;
                        }
                    }
                }
                if (er2 != READER_ERR.MT_OK_ERR) {
                    return er2;
                }
            }
        }
        this.pants = ants;
        this.pantcnt = antcnt;
        this.poption = option;
        READER_ERR er3 = AsyncStartReading(ants, antcnt, option);
        Reset_IT_CT();
        this.isIT_CT_run = true;
        IT_CT_notify tf = new IT_CT_notify(this);
        Thread thread = new Thread(tf);
        this.IT_CT_thread = thread;
        this.IT_CT_step = this.IT_CT_step_init;
        thread.start();
        return er3;
    }

    private READER_ERR AsyncStartReading_IT_S2(int[] ants, int antcnt, int option) {
        Object[] rcf2 = new Region_Conf[1];
        READER_ERR er = ParamGet(Mtr_Param.MTR_PARAM_FREQUENCY_REGION, rcf2);
        if (er != READER_ERR.MT_OK_ERR) {
            return er;
        }
        READER_ERR er2 = ParamSet(Mtr_Param.MTR_PARAM_FREQUENCY_REGION, rcf2[0]);
        if (er2 != READER_ERR.MT_OK_ERR) {
            return er2;
        }
        int[] vala = {0};
        READER_ERR er3 = ParamGet(Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION, vala);
        if (er3 != READER_ERR.MT_OK_ERR) {
            return er3;
        }
        if (vala[0] == 0 || vala[0] == 1) {
            toDlog("set S2");
            vala[0] = 2;
            READER_ERR er4 = ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION, vala);
            if (er4 != READER_ERR.MT_OK_ERR) {
                return er4;
            }
        } else if (vala[0] == 2) {
            toDlog("set S3");
            vala[0] = 3;
            READER_ERR er5 = ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION, vala);
            if (er5 != READER_ERR.MT_OK_ERR) {
                return er5;
            }
        } else if (vala[0] == 3) {
            toDlog("set S2");
            vala[0] = 2;
            READER_ERR er6 = ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION, vala);
            if (er6 != READER_ERR.MT_OK_ERR) {
                return er6;
            }
        }
        vala[0] = 0;
        READER_ERR er7 = ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_TARGET, vala);
        if (er7 != READER_ERR.MT_OK_ERR) {
            return er7;
        }
        this.IT_S2_istargetA = true;
        vala[0] = 19;
        READER_ERR er8 = ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_TAGENCODING, vala);
        if (er8 != READER_ERR.MT_OK_ERR) {
            return er8;
        }
        this.pants = ants;
        this.pantcnt = antcnt;
        this.poption = option;
        READER_ERR er9 = AsyncStartReading(ants, antcnt, option);
        if (er9 != READER_ERR.MT_OK_ERR) {
            return er9;
        }
        Reset_IT();
        this.isIT_S2_run = true;
        toDlog("---init");
        IT_S2_notify tf = new IT_S2_notify(this);
        Thread thread = new Thread(tf);
        this.IT_S2_thread = thread;
        this.IT_S2_step = 0;
        thread.start();
        return er9;
    }

    int[] Sort(int[] array, int len) {
        for (int xIndex = 0; xIndex < len; xIndex++) {
            for (int yIndex = 0; yIndex < len; yIndex++) {
                if (array[xIndex] < array[yIndex]) {
                    int tmpIntValue = Integer.valueOf(array[xIndex]).intValue();
                    array[xIndex] = array[yIndex];
                    array[yIndex] = tmpIntValue;
                }
            }
        }
        int[] reary = new int[len];
        System.arraycopy(array, 0, reary, 0, len);
        return reary;
    }

    private READER_ERR AsyncStartReading_IT_E7(int[] ants, int antcnt, int option) {
        Object[] rcf2 = new Region_Conf[1];
        READER_ERR er = ParamGet(Mtr_Param.MTR_PARAM_FREQUENCY_REGION, rcf2);
        if (er != READER_ERR.MT_OK_ERR) {
            return er;
        }
        READER_ERR er2 = ParamSet(Mtr_Param.MTR_PARAM_FREQUENCY_REGION, rcf2[0]);
        if (er2 != READER_ERR.MT_OK_ERR) {
            return er2;
        }
        int[] vala = {0};
        toDlog("set S2,tarA,gen2code 107");
        vala[0] = 2;
        READER_ERR er3 = ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION, vala);
        if (er3 != READER_ERR.MT_OK_ERR) {
            return er3;
        }
        vala[0] = 0;
        READER_ERR er4 = ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_TARGET, vala);
        if (er4 != READER_ERR.MT_OK_ERR) {
            return er4;
        }
        this.IT_E7_istargetA = true;
        vala[0] = 107;
        READER_ERR er5 = ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_TAGENCODING, vala);
        if (er5 != READER_ERR.MT_OK_ERR) {
            return er5;
        }
        int[] mp = new int[1];
        READER_ERR er6 = ParamGet(Mtr_Param.MTR_PARAM_RF_MAXPOWER, mp);
        if (er6 != READER_ERR.MT_OK_ERR) {
            return er6;
        }
        HoptableData_ST hdst2 = new HoptableData_ST();
        READER_ERR er7 = ParamGet(Mtr_Param.MTR_PARAM_FREQUENCY_HOPTABLE, hdst2);
        toDlog("set S2,tarA, pw " + String.valueOf(this.IT_E7_pow) + " frec " + String.valueOf(this.IT_E7_centrefre) + " rf " + String.valueOf(this.IT_E7_rfm) + " set gen2code:" + String.valueOf(this.IT_E7_rfm));
        if (er7 != READER_ERR.MT_OK_ERR) {
            return er7;
        }
        int[] tablefre = Sort(hdst2.htb, hdst2.lenhtb);
        int i = tablefre[tablefre.length / 2];
        this.IT_E7_centrefre = i;
        int i2 = mp[0];
        this.IT_E7_pow = i2;
        SetFilterSessioninTargetA(ants, i, i2);
        vala[0] = this.IT_E7_rfm;
        READER_ERR er8 = ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_TAGENCODING, vala);
        if (er8 != READER_ERR.MT_OK_ERR) {
            return er8;
        }
        this.pants = ants;
        this.pantcnt = antcnt;
        this.poption = option;
        READER_ERR er9 = AsyncStartReading(ants, antcnt, option);
        if (er9 != READER_ERR.MT_OK_ERR) {
            return er9;
        }
        Reset_IT();
        this.isIT_E7_run = true;
        toDlog("---init");
        IT_E7_notify tf = new IT_E7_notify(this);
        Thread thread = new Thread(tf);
        this.IT_E7_thread = thread;
        this.IT_E7_step = 0;
        thread.start();
        return er9;
    }

    private READER_ERR AsyncStartReading_IT_E7v2(int[] ants, int antcnt, int option) {
        Region_Conf[] rcf2 = new Region_Conf[1];
        READER_ERR er = ParamGet(Mtr_Param.MTR_PARAM_FREQUENCY_REGION, rcf2);
        if (er != READER_ERR.MT_OK_ERR) {
            return er;
        }
        this.IT_E7_rg = rcf2[0];
        READER_ERR er2 = ParamSet(Mtr_Param.MTR_PARAM_FREQUENCY_REGION, rcf2[0]);
        if (er2 != READER_ERR.MT_OK_ERR) {
            return er2;
        }
        int[] vala = {0};
        toDlog("set S2,tarA,gen2code 107");
        vala[0] = 2;
        READER_ERR er3 = ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION, vala);
        if (er3 == READER_ERR.MT_OK_ERR) {
            vala[0] = -1;
            READER_ERR er4 = ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_Q, vala);
            if (er4 != READER_ERR.MT_OK_ERR) {
                return er4;
            }
            vala[0] = 0;
            READER_ERR er5 = ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_TARGET, vala);
            if (er5 != READER_ERR.MT_OK_ERR) {
                return er5;
            }
            this.IT_E7_istargetA = true;
            vala[0] = 107;
            READER_ERR er6 = ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_TAGENCODING, vala);
            if (er6 != READER_ERR.MT_OK_ERR) {
                return er6;
            }
            int[] mp = new int[1];
            READER_ERR er7 = ParamGet(Mtr_Param.MTR_PARAM_RF_MAXPOWER, mp);
            if (er7 != READER_ERR.MT_OK_ERR) {
                return er7;
            }
            HoptableData_ST hdst2 = new HoptableData_ST();
            READER_ERR er8 = ParamGet(Mtr_Param.MTR_PARAM_FREQUENCY_HOPTABLE, hdst2);
            if (er8 != READER_ERR.MT_OK_ERR) {
                return er8;
            }
            int[] tablefre = Sort(hdst2.htb, hdst2.lenhtb);
            this.IT_E7_highfre = tablefre[tablefre.length - 1];
            this.IT_E7_lowfre = tablefre[0];
            this.IT_E7_centrefre = tablefre[tablefre.length / 2];
            this.IT_E7_pow = mp[0];
            toDlog("set S2,tarA, pw " + String.valueOf(this.IT_E7_pow) + " frec " + String.valueOf(this.IT_E7_centrefre) + " frel " + String.valueOf(this.IT_E7_lowfre) + " freh " + String.valueOf(this.IT_E7_highfre) + " rf " + String.valueOf(this.IT_E7_rfm));
            SetFilterSessioninTargetA(ants, this.IT_E7_centrefre, this.IT_E7_pow);
            SetFilterSessioninTargetA(ants, this.IT_E7_lowfre, this.IT_E7_pow);
            SetFilterSessioninTargetA(ants, this.IT_E7_highfre, this.IT_E7_pow);
            vala[0] = this.IT_E7_rfm;
            READER_ERR er9 = ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_TAGENCODING, vala);
            if (er9 != READER_ERR.MT_OK_ERR) {
                return er9;
            }
            this.pants = ants;
            this.pantcnt = antcnt;
            this.poption = option;
            READER_ERR er10 = AsyncStartReading(ants, antcnt, option);
            if (er10 != READER_ERR.MT_OK_ERR) {
                return er10;
            }
            Reset_IT();
            this.isIT_E7_run = true;
            toDlog("---init");
            IT_E7v2_notify tf = new IT_E7v2_notify(this);
            Thread thread = new Thread(tf);
            this.IT_E7_thread = thread;
            this.IT_E7_step = 0;
            thread.start();
            return er10;
        }
        return er3;
    }

    public READER_ERR AsyncStopReading_IT_CT() {
        this.isIT_CT_run = false;
        READER_ERR err = AsyncStopReading();
        Thread thread = this.IT_CT_thread;
        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return err;
    }

    public READER_ERR AsyncStopReading_IT() {
        if (this.it_mode_V == IT_MODE.IT_MODE_CT) {
            return AsyncStopReading_IT_CT();
        }
        if (this.it_mode_V == IT_MODE.IT_MODE_E7 || this.it_mode_V == IT_MODE.IT_MODE_E7v2) {
            this.isIT_E7_run = false;
            READER_ERR err = AsyncStopReading();
            Thread thread = this.IT_E7_thread;
            if (thread != null) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            SetFilterSessioninTargetA(this.pants, this.IT_E7_centrefre, this.IT_E7_pow);
            if (this.it_mode_V == IT_MODE.IT_MODE_E7v2) {
                SetFilterSessioninTargetA(this.pants, this.IT_E7_lowfre, this.IT_E7_pow);
                SetFilterSessioninTargetA(this.pants, this.IT_E7_highfre, this.IT_E7_pow);
            }
            return err;
        }
        if (this.it_mode_V == IT_MODE.IT_MODE_S2) {
            this.isIT_S2_run = false;
            READER_ERR err2 = AsyncStopReading();
            Thread thread2 = this.IT_S2_thread;
            if (thread2 != null) {
                try {
                    thread2.join();
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
            }
            return err2;
        }
        READER_ERR err3 = READER_ERR.MT_OP_INVALID;
        return err3;
    }

    public void Reset_IT_CT() {
        this.quetagstr.clear();
        this.totalcount = 0;
        this.totalcountlast = 0;
        int i = this.IT_CT_step_init;
        if (i != -1) {
            this.IT_CT_step = i;
        } else {
            this.IT_CT_step = 0;
        }
        this.IT_CT_c = 0;
        this.IT_CT_start = System.currentTimeMillis();
        this.vstaticstarttick = System.currentTimeMillis() - this.IT_CT_start;
    }

    public void Reset_IT() {
        if (this.it_mode_V == IT_MODE.IT_MODE_CT) {
            Reset_IT_CT();
            return;
        }
        if (this.it_mode_V == IT_MODE.IT_MODE_E7 || this.it_mode_V == IT_MODE.IT_MODE_E7v2) {
            this.IT_E7_start = System.currentTimeMillis();
            this.vstaticstarttick = System.currentTimeMillis() - this.IT_E7_start;
        } else if (this.it_mode_V == IT_MODE.IT_MODE_S2) {
            this.IT_S2_start = System.currentTimeMillis();
            this.vstaticstarttick = System.currentTimeMillis() - this.IT_S2_start;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public READER_ERR errhandle_IT(READER_ERR err) {
        if (err != READER_ERR.MT_OK_ERR) {
            if (this.it_mode_V == IT_MODE.IT_MODE_CT) {
                this.isIT_CT_run = false;
            } else if (this.it_mode_V == IT_MODE.IT_MODE_S2) {
                this.isIT_S2_run = false;
            } else if (this.it_mode_V == IT_MODE.IT_MODE_E7 || this.it_mode_V == IT_MODE.IT_MODE_E7v2) {
                this.isIT_E7_run = false;
            }
            AsyncStopReading();
            if (this.readExceptionListeners.size() > 0) {
                Exceptionotify exfy = new Exceptionotify(this, err);
                Thread tread = new Thread(exfy);
                tread.start();
            }
        }
        return err;
    }

    private class IT_CT_notify implements Runnable {
        Reader reader;

        public IT_CT_notify(Reader rd) {
            this.reader = rd;
        }

        @Override // java.lang.Runnable
        public void run() {
            int nowcyc;
            READER_ERR er;
            while (Reader.this.isIT_CT_run) {
                int[] tagcnt = {0};
                READER_ERR er2 = Reader.this.AsyncGetTagCount(tagcnt);
                if (er2 != READER_ERR.MT_OK_ERR) {
                    Reader.this.errhandle_IT(er2);
                    return;
                }
                if (tagcnt[0] > 0) {
                    Vector<TAGINFO> tagv = new Vector<>();
                    int i = 0;
                    while (true) {
                        if (i >= tagcnt[0]) {
                            break;
                        }
                        TAGINFO tfs = Reader.this.new TAGINFO();
                        er2 = Reader.this.AsyncGetNextTag(tfs);
                        if (er2 != READER_ERR.MT_OK_ERR) {
                            Reader.this.errhandle_IT(er2);
                            break;
                        }
                        if (!Reader.this.quetagstr.contains(Reader.bytes_Hexstr(tfs.EpcId))) {
                            Reader.this.quetagstr.add(Reader.bytes_Hexstr(tfs.EpcId));
                            Reader.this.totalcount++;
                        }
                        tagv.add(tfs);
                        i++;
                    }
                    int i2 = tagv.size();
                    TAGINFO[] tag = (TAGINFO[]) tagv.toArray(new TAGINFO[i2]);
                    if (Reader.this.readListeners.size() > 0 && tag.length > 0) {
                        for (ReadListener rl : Reader.this.readListeners) {
                            rl.tagRead(this.reader, tag);
                        }
                    }
                }
                long readtime_spp = System.currentTimeMillis() - Reader.this.IT_CT_start;
                if (Reader.this.IT_CT_step == 0 && Reader.this.isIT_CT_run && readtime_spp >= Reader.this.IT_CT_m1_keep * 1000) {
                    Reader.this.IT_CT_step = 1;
                    Reader.this.IT_CT_start = System.currentTimeMillis();
                    Reader.this.vstaticstarttick = System.currentTimeMillis() - Reader.this.IT_CT_start;
                }
                if (Reader.this.IT_CT_step == 5 && Reader.this.isIT_CT_run && readtime_spp >= Reader.this.IT_CT_m1_keep * 1000) {
                    Reader.this.IT_CT_step = 6;
                    Reader.this.IT_CT_start = System.currentTimeMillis();
                    Reader.this.vstaticstarttick = System.currentTimeMillis() - Reader.this.IT_CT_start;
                }
                if (Reader.this.IT_CT_step == 0 || Reader.this.IT_CT_step == 1 || Reader.this.IT_CT_step == 5) {
                    nowcyc = Reader.this.IT_CT_m1_cycle;
                } else if (Reader.this.IT_CT_step == 2 || Reader.this.IT_CT_step == 3) {
                    nowcyc = Reader.this.IT_CT_m2_cycle;
                } else if (Reader.this.IT_CT_step == 4 || Reader.this.IT_CT_step == 6) {
                    nowcyc = Reader.this.IT_CT_m3_cycleread;
                } else {
                    nowcyc = 1000;
                }
                if (readtime_spp - Reader.this.vstaticstarttick >= nowcyc) {
                    Reader.this.vstaticstarttick = readtime_spp;
                    if (Reader.this.IT_CT_step == 1 && Reader.this.isIT_CT_run) {
                        if (Reader.this.totalcount - Reader.this.totalcountlast < Reader.this.IT_CT_m1_toma) {
                            Reader.this.IT_CT_step = 4;
                            Reader.this.IT_CT_c = 0;
                            Reader.this.IT_CT_start = System.currentTimeMillis();
                            Reader.this.vstaticstarttick = System.currentTimeMillis() - Reader.this.IT_CT_start;
                        }
                    } else if (Reader.this.IT_CT_step == 2 && Reader.this.isIT_CT_run) {
                        READER_ERR er3 = Reader.this.AsyncStopReading();
                        if (er3 != READER_ERR.MT_OK_ERR) {
                            Reader.this.errhandle_IT(er3);
                        }
                        if (Reader.this.isIT_CT_run) {
                            Reader reader = Reader.this;
                            er3 = reader.AsyncStartReading(reader.pants, Reader.this.pants.length, Reader.this.poption);
                        }
                        if (er3 != READER_ERR.MT_OK_ERR) {
                            Reader.this.errhandle_IT(er3);
                        }
                        Reader reader2 = Reader.this;
                        int i3 = reader2.IT_CT_c + 1;
                        reader2.IT_CT_c = i3;
                        if (i3 >= Reader.this.IT_CT_m2_keepcount) {
                            Reader.this.IT_CT_step = 3;
                            Reader.this.IT_CT_start = System.currentTimeMillis();
                            Reader.this.vstaticstarttick = System.currentTimeMillis() - Reader.this.IT_CT_start;
                        }
                    } else if (Reader.this.IT_CT_step == 3 && Reader.this.isIT_CT_run) {
                        if (Reader.this.totalcount - Reader.this.totalcountlast < Reader.this.IT_CT_m2_tomc) {
                            Reader.this.IT_CT_step = 4;
                            Reader.this.IT_CT_start = System.currentTimeMillis();
                            Reader.this.vstaticstarttick = System.currentTimeMillis() - Reader.this.IT_CT_start;
                        } else if (Reader.this.totalcount - Reader.this.totalcountlast > Reader.this.IT_CT_m2_toma) {
                            Reader.this.IT_CT_step = 0;
                            Reader.this.IT_CT_start = System.currentTimeMillis();
                            Reader.this.vstaticstarttick = System.currentTimeMillis() - Reader.this.IT_CT_start;
                        }
                    } else if ((Reader.this.IT_CT_step == 4 || Reader.this.IT_CT_step == 6) && Reader.this.isIT_CT_run) {
                        if (Reader.this.totalcount - Reader.this.totalcountlast > Reader.this.IT_CT_m3_toma) {
                            Reader.this.IT_CT_step = 0;
                        } else {
                            READER_ERR er4 = Reader.this.AsyncStopReading();
                            if (er4 != READER_ERR.MT_OK_ERR) {
                                Reader.this.errhandle_IT(er4);
                            }
                            long st_time = System.currentTimeMillis();
                            do {
                                if (!Reader.this.isIT_CT_run) {
                                    break;
                                } else {
                                    Thread.sleep(50L);
                                }
                            } while (System.currentTimeMillis() - st_time <= Reader.this.IT_CT_m3_cyclestop);
                            if (!Reader.this.isIT_CT_run) {
                                er = er4;
                            } else {
                                Reader reader3 = Reader.this;
                                er = reader3.AsyncStartReading(reader3.pants, Reader.this.pantcnt, Reader.this.poption);
                            }
                            if (er != READER_ERR.MT_OK_ERR) {
                                Reader.this.errhandle_IT(er);
                            }
                        }
                        Reader.this.IT_CT_start = System.currentTimeMillis();
                        Reader.this.vstaticstarttick = System.currentTimeMillis() - Reader.this.IT_CT_start;
                    }
                    Reader reader4 = Reader.this;
                    reader4.totalcountlast = reader4.totalcount;
                }
                if (!Reader.this.isIT_CT_run) {
                    return;
                }
            }
        }
    }

    private class IT_S2_notify implements Runnable {
        Reader reader;

        public IT_S2_notify(Reader rd) {
            this.reader = rd;
        }

        @Override // java.lang.Runnable
        public void run() {
            while (Reader.this.isIT_S2_run) {
                int[] tagcnt = {0};
                READER_ERR er = Reader.this.AsyncGetTagCount(tagcnt);
                if (er != READER_ERR.MT_OK_ERR) {
                    Reader.this.errhandle_IT(er);
                    return;
                }
                if (tagcnt[0] > 0) {
                    Reader.this.totalcount += tagcnt[0];
                    Vector<TAGINFO> tagv = new Vector<>();
                    int i = 0;
                    while (true) {
                        if (i >= tagcnt[0]) {
                            break;
                        }
                        TAGINFO tfs = Reader.this.new TAGINFO();
                        READER_ERR er2 = Reader.this.AsyncGetNextTag(tfs);
                        if (er2 != READER_ERR.MT_OK_ERR) {
                            Reader.this.errhandle_IT(er2);
                            break;
                        } else {
                            tagv.add(tfs);
                            i++;
                        }
                    }
                    int i2 = tagv.size();
                    TAGINFO[] tag = (TAGINFO[]) tagv.toArray(new TAGINFO[i2]);
                    if (Reader.this.readListeners.size() > 0 && tag.length > 0) {
                        for (ReadListener rl : Reader.this.readListeners) {
                            rl.tagRead(this.reader, tag);
                        }
                    }
                }
                long readtime_spp = System.currentTimeMillis() - Reader.this.IT_S2_start;
                int nowcyc = Reader.this.IT_S2_m1_cycle;
                if (Reader.this.IT_S2_step != 0) {
                    if (Reader.this.IT_S2_step == 1) {
                        nowcyc = Reader.this.IT_S2_m1_cycle2;
                    }
                } else {
                    nowcyc = Reader.this.IT_S2_m1_cycle;
                }
                if (readtime_spp - Reader.this.vstaticstarttick >= nowcyc) {
                    Reader.this.toDlog("Nowcyc:" + String.valueOf(nowcyc) + " tick:" + String.valueOf(readtime_spp - Reader.this.vstaticstarttick));
                    Reader.this.vstaticstarttick = readtime_spp;
                    if (Reader.this.IT_S2_step == 0 && Reader.this.isIT_S2_run) {
                        Reader.this.toDlog("IT_S2_M1_STEP1---" + String.valueOf(Reader.this.totalcount));
                        if (Reader.this.totalcount < Reader.this.IT_S2_ctagcount) {
                            Reader.this.toDlog("stop:" + String.valueOf(Reader.this.IT_S2_ctagcount));
                            READER_ERR er3 = Reader.this.AsyncStopReading();
                            if (er3 != READER_ERR.MT_OK_ERR) {
                                Reader.this.errhandle_IT(er3);
                            }
                            Reader.this.toDlog("set pro1 to start");
                            READER_ERR er4 = Reader.this.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_TAGENCODING, new int[]{17});
                            if (er4 != READER_ERR.MT_OK_ERR) {
                                Reader.this.errhandle_IT(er4);
                            }
                            if (Reader.this.isIT_S2_run) {
                                Reader reader = Reader.this;
                                er4 = reader.AsyncStartReading(reader.pants, Reader.this.pants.length, Reader.this.poption);
                            }
                            if (er4 != READER_ERR.MT_OK_ERR) {
                                Reader.this.errhandle_IT(er4);
                            }
                            Reader.this.IT_S2_step = 1;
                            Reader.this.IT_S2_start = System.currentTimeMillis();
                            Reader.this.vstaticstarttick = System.currentTimeMillis() - Reader.this.IT_S2_start;
                        }
                    } else if (Reader.this.IT_S2_step == 1 && Reader.this.isIT_S2_run && Reader.this.totalcount < Reader.this.IT_S2_ctagcount2) {
                        Reader.this.toDlog("stop:" + String.valueOf(Reader.this.IT_S2_ctagcount2));
                        READER_ERR er5 = Reader.this.AsyncStopReading();
                        if (er5 != READER_ERR.MT_OK_ERR) {
                            Reader.this.errhandle_IT(er5);
                        }
                        int[] vala = {0};
                        if (Reader.this.IT_S2_istargetA) {
                            Reader.this.toDlog("set to A");
                        } else {
                            vala[0] = 1;
                            Reader.this.toDlog("set to B");
                        }
                        READER_ERR er6 = Reader.this.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_TARGET, vala);
                        if (er6 != READER_ERR.MT_OK_ERR) {
                            Reader.this.errhandle_IT(er6);
                        }
                        Reader reader2 = Reader.this;
                        reader2.IT_S2_istargetA = true ^ reader2.IT_S2_istargetA;
                        vala[0] = 19;
                        READER_ERR er7 = Reader.this.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_TAGENCODING, vala);
                        if (er7 != READER_ERR.MT_OK_ERR) {
                            Reader.this.errhandle_IT(er7);
                        }
                        Reader.this.toDlog("set pro3 to start");
                        if (Reader.this.isIT_S2_run) {
                            Reader reader3 = Reader.this;
                            er7 = reader3.AsyncStartReading(reader3.pants, Reader.this.pants.length, Reader.this.poption);
                        }
                        if (er7 != READER_ERR.MT_OK_ERR) {
                            Reader.this.errhandle_IT(er7);
                        }
                        Reader.this.IT_S2_step = 0;
                        Reader.this.IT_S2_start = System.currentTimeMillis();
                        Reader.this.vstaticstarttick = System.currentTimeMillis() - Reader.this.IT_S2_start;
                    }
                    Reader reader4 = Reader.this;
                    reader4.totalcountlast = reader4.totalcount;
                    Reader.this.totalcount = 0;
                }
                if (!Reader.this.isIT_S2_run) {
                    return;
                }
            }
        }
    }

    private class IT_E7_notify implements Runnable {
        Reader reader;

        public IT_E7_notify(Reader rd) {
            this.reader = rd;
        }

        @Override // java.lang.Runnable
        public void run() {
            while (Reader.this.isIT_E7_run) {
                int[] tagcnt = {0};
                READER_ERR er = Reader.this.AsyncGetTagCount(tagcnt);
                if (er != READER_ERR.MT_OK_ERR) {
                    Reader.this.errhandle_IT(er);
                    return;
                }
                if (tagcnt[0] > 0) {
                    Reader.this.toDlog("gettagcount:" + String.valueOf(tagcnt[0]));
                    Reader reader = Reader.this;
                    reader.totalcount = reader.totalcount + tagcnt[0];
                    Vector<TAGINFO> tagv = new Vector<>();
                    int i = 0;
                    while (true) {
                        if (i >= tagcnt[0]) {
                            break;
                        }
                        TAGINFO tfs = Reader.this.new TAGINFO();
                        READER_ERR er2 = Reader.this.AsyncGetNextTag(tfs);
                        if (er2 != READER_ERR.MT_OK_ERR) {
                            Reader.this.errhandle_IT(er2);
                            break;
                        } else {
                            tagv.add(tfs);
                            i++;
                        }
                    }
                    int i2 = tagv.size();
                    TAGINFO[] tag = (TAGINFO[]) tagv.toArray(new TAGINFO[i2]);
                    if (Reader.this.readListeners.size() > 0 && tag.length > 0) {
                        for (ReadListener rl : Reader.this.readListeners) {
                            rl.tagRead(this.reader, tag);
                        }
                    }
                }
                long readtime_spp = System.currentTimeMillis() - Reader.this.IT_E7_start;
                int nowcyc = Reader.this.IT_E7_m1_cycle;
                if (Reader.this.IT_E7_step != 0) {
                    if (Reader.this.IT_E7_step == 1) {
                        nowcyc = Reader.this.IT_E7_m1_cycle2;
                    }
                } else {
                    nowcyc = Reader.this.IT_E7_m1_cycle;
                }
                if (readtime_spp - Reader.this.vstaticstarttick >= nowcyc) {
                    Reader.this.toDlog("Nowcyc:" + String.valueOf(nowcyc) + " tick:" + String.valueOf(readtime_spp - Reader.this.vstaticstarttick));
                    Reader.this.vstaticstarttick = readtime_spp;
                    if (Reader.this.IT_E7_step != 0 || !Reader.this.isIT_E7_run) {
                        if (Reader.this.IT_E7_step == 1 && Reader.this.isIT_E7_run && Reader.this.totalcount < Reader.this.IT_E7_ctagcount2) {
                            Reader.this.toDlog("stop:IT_E7_M1_STEP2 " + String.valueOf(Reader.this.IT_E7_ctagcount2));
                            READER_ERR er3 = Reader.this.AsyncStopReading();
                            if (er3 != READER_ERR.MT_OK_ERR) {
                                Reader.this.errhandle_IT(er3);
                            }
                            Reader.this.IT_E7_istargetA = !r8.IT_E7_istargetA;
                            int[] vala = {0};
                            if (Reader.this.IT_E7_istargetA) {
                                Reader.this.toDlog("set to A");
                            } else {
                                vala[0] = 1;
                                Reader.this.toDlog("set to B");
                            }
                            READER_ERR er4 = Reader.this.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_TARGET, vala);
                            if (er4 != READER_ERR.MT_OK_ERR) {
                                Reader.this.errhandle_IT(er4);
                            }
                            if (Reader.this.IT_E7_istargetA) {
                                Reader.this.toDlog("set to 107,S2 to A");
                                vala[0] = 107;
                                READER_ERR er5 = Reader.this.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_TAGENCODING, vala);
                                if (er5 != READER_ERR.MT_OK_ERR) {
                                    Reader.this.errhandle_IT(er5);
                                }
                                Reader reader2 = Reader.this;
                                reader2.SetFilterSessioninTargetA(reader2.pants, Reader.this.IT_E7_centrefre, Reader.this.IT_E7_pow);
                            }
                            Reader.this.toDlog(" set gen2code:" + String.valueOf(Reader.this.IT_E7_rfm));
                            vala[0] = Reader.this.IT_E7_rfm;
                            READER_ERR er6 = Reader.this.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_TAGENCODING, vala);
                            if (er6 != READER_ERR.MT_OK_ERR) {
                                Reader.this.errhandle_IT(er6);
                            }
                            Reader.this.toDlog(" to start");
                            if (Reader.this.isIT_E7_run) {
                                Reader reader3 = Reader.this;
                                er6 = reader3.AsyncStartReading(reader3.pants, Reader.this.pants.length, Reader.this.poption);
                            }
                            if (er6 != READER_ERR.MT_OK_ERR) {
                                Reader.this.errhandle_IT(er6);
                            }
                            Reader.this.IT_E7_step = 0;
                            Reader.this.IT_E7_start = System.currentTimeMillis();
                            Reader.this.vstaticstarttick = System.currentTimeMillis() - Reader.this.IT_E7_start;
                        }
                    } else if (Reader.this.totalcount < Reader.this.IT_E7_ctagcount) {
                        Reader.this.toDlog("stop:IT_E7_M1_STEP1 " + String.valueOf(Reader.this.IT_E7_ctagcount));
                        READER_ERR er7 = Reader.this.AsyncStopReading();
                        if (er7 != READER_ERR.MT_OK_ERR) {
                            Reader.this.errhandle_IT(er7);
                        }
                        Reader.this.toDlog("set gen2 code 107");
                        READER_ERR er8 = Reader.this.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_TAGENCODING, new int[]{107});
                        if (er8 != READER_ERR.MT_OK_ERR) {
                            Reader.this.errhandle_IT(er8);
                        }
                        if (Reader.this.isIT_E7_run) {
                            Reader reader4 = Reader.this;
                            er8 = reader4.AsyncStartReading(reader4.pants, Reader.this.pants.length, Reader.this.poption);
                        }
                        if (er8 != READER_ERR.MT_OK_ERR) {
                            Reader.this.errhandle_IT(er8);
                        }
                        Reader.this.IT_E7_step = 1;
                        Reader.this.IT_E7_start = System.currentTimeMillis();
                        Reader.this.vstaticstarttick = System.currentTimeMillis() - Reader.this.IT_E7_start;
                    }
                    Reader reader5 = Reader.this;
                    reader5.totalcountlast = reader5.totalcount;
                    Reader.this.totalcount = 0;
                }
                if (!Reader.this.isIT_E7_run) {
                    return;
                }
            }
        }
    }

    private class IT_E7v2_notify implements Runnable {
        Reader reader;

        public IT_E7v2_notify(Reader rd) {
            this.reader = rd;
        }

        @Override // java.lang.Runnable
        public void run() {
            while (Reader.this.isIT_E7_run) {
                int[] tagcnt = {0};
                READER_ERR er = Reader.this.AsyncGetTagCount(tagcnt);
                boolean isautostop = false;
                if (er != READER_ERR.MT_OK_ERR) {
                    Reader.this.errhandle_IT(er);
                    return;
                }
                if (tagcnt[0] > 0) {
                    Reader.this.toDlog("gettagcount:" + String.valueOf(tagcnt[0]));
                    Vector<TAGINFO> tagv = new Vector<>();
                    int i = 0;
                    while (true) {
                        if (i >= tagcnt[0]) {
                            break;
                        }
                        TAGINFO tfs = Reader.this.new TAGINFO();
                        READER_ERR er2 = Reader.this.AsyncGetNextTag(tfs);
                        if (er2 != READER_ERR.MT_OK_ERR) {
                            Reader.this.errhandle_IT(er2);
                            break;
                        }
                        if (tfs.Epclen % 2 == 0) {
                            tagv.add(tfs);
                            Reader.this.totalcount++;
                        } else if (tfs.Epclen == 3) {
                            isautostop = true;
                        }
                        i++;
                    }
                    int i2 = tagv.size();
                    TAGINFO[] tag = (TAGINFO[]) tagv.toArray(new TAGINFO[i2]);
                    if (Reader.this.readListeners.size() > 0 && tag.length > 0) {
                        for (ReadListener rl : Reader.this.readListeners) {
                            rl.tagRead(this.reader, tag);
                        }
                    }
                }
                long readtime_spp = System.currentTimeMillis() - Reader.this.IT_E7_start;
                int nowcyc = Reader.this.IT_E7_m1_cycle;
                if (Reader.this.IT_E7_step != 0) {
                    if (Reader.this.IT_E7_step == 1) {
                        nowcyc = Reader.this.IT_E7_m1_cycle2;
                    } else if (Reader.this.IT_E7_step == 2 || Reader.this.IT_E7_step == 3 || Reader.this.IT_E7_step == 4) {
                        nowcyc = Reader.this.IT_E7_m1_cycle3;
                    }
                } else {
                    nowcyc = Reader.this.IT_E7_m1_cycle;
                }
                if (readtime_spp - Reader.this.vstaticstarttick >= nowcyc) {
                    Reader.this.toDlog("Nowcyc:" + String.valueOf(nowcyc) + " tick:" + String.valueOf(readtime_spp - Reader.this.vstaticstarttick));
                    Reader.this.vstaticstarttick = readtime_spp;
                    if (Reader.this.IT_E7_step == 0 && Reader.this.isIT_E7_run) {
                        Reader.this.toDlog("stop IT_E7_M1_STEP1---" + String.valueOf(Reader.this.totalcount));
                        if (Reader.this.totalcount < Reader.this.IT_E7_ctagcount) {
                            Reader.this.toDlog("stop:" + String.valueOf(Reader.this.IT_E7_ctagcount));
                            READER_ERR er3 = Reader.this.AsyncStopReading();
                            if (er3 != READER_ERR.MT_OK_ERR) {
                                Reader.this.errhandle_IT(er3);
                            }
                            Reader.this.toDlog("set gen2 code 107");
                            READER_ERR er4 = Reader.this.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_TAGENCODING, new int[]{107});
                            if (er4 != READER_ERR.MT_OK_ERR) {
                                Reader.this.errhandle_IT(er4);
                            }
                            if (Reader.this.isIT_E7_run) {
                                Reader reader = Reader.this;
                                er4 = reader.AsyncStartReading(reader.pants, Reader.this.pants.length, Reader.this.poption);
                            }
                            if (er4 != READER_ERR.MT_OK_ERR) {
                                Reader.this.errhandle_IT(er4);
                            }
                            Reader.this.IT_E7_step = 1;
                            Reader.this.IT_E7_start = System.currentTimeMillis();
                            Reader.this.vstaticstarttick = System.currentTimeMillis() - Reader.this.IT_E7_start;
                        }
                    } else if (Reader.this.IT_E7_step == 1 && Reader.this.isIT_E7_run) {
                        if (Reader.this.totalcount < Reader.this.IT_E7_ctagcount2) {
                            Reader.this.toDlog("stop IT_E7_M1_STEP2---" + String.valueOf(Reader.this.IT_E7_ctagcount2));
                            READER_ERR er5 = Reader.this.AsyncStopReading();
                            if (er5 != READER_ERR.MT_OK_ERR) {
                                Reader.this.errhandle_IT(er5);
                            }
                            Reader.this.toDlog("set gen2 code 113,fre " + String.valueOf(Reader.this.IT_E7_centrefre) + " Q 4");
                            READER_ERR er6 = Reader.this.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_TAGENCODING, new int[]{113});
                            if (er6 != READER_ERR.MT_OK_ERR) {
                                Reader.this.errhandle_IT(er6);
                            }
                            HoptableData_ST hdst2 = Reader.this.new HoptableData_ST();
                            hdst2.lenhtb = 1;
                            hdst2.htb[0] = Reader.this.IT_E7_lowfre;
                            READER_ERR er7 = Reader.this.ParamSet(Mtr_Param.MTR_PARAM_FREQUENCY_HOPTABLE, hdst2);
                            if (er7 != READER_ERR.MT_OK_ERR) {
                                Reader.this.errhandle_IT(er7);
                            }
                            int[] val = {4};
                            READER_ERR er8 = Reader.this.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_Q, val);
                            if (er8 == READER_ERR.MT_OK_ERR) {
                                Reader.this.toDlog(" to start");
                                if (Reader.this.isIT_E7_run) {
                                    Reader reader2 = Reader.this;
                                    er8 = reader2.AsyncStartReading(reader2.pants, Reader.this.pants.length, Reader.this.poption);
                                }
                                if (er8 != READER_ERR.MT_OK_ERR) {
                                    Reader.this.errhandle_IT(er8);
                                }
                                Reader.this.IT_E7_step = 2;
                                Reader.this.IT_E7_start = System.currentTimeMillis();
                                Reader.this.vstaticstarttick = System.currentTimeMillis() - Reader.this.IT_E7_start;
                            }
                        }
                    } else if ((Reader.this.IT_E7_step == 2 || Reader.this.IT_E7_step == 3 || Reader.this.IT_E7_step == 4) && Reader.this.isIT_E7_run && (Reader.this.totalcount < Reader.this.IT_E7_ctagcount3 || isautostop)) {
                        Reader.this.toDlog("stop: IT_E7_M1_STEP" + String.valueOf(Reader.this.IT_E7_step + 1) + " " + String.valueOf(Reader.this.totalcount) + " autostop:" + String.valueOf(isautostop));
                        READER_ERR er9 = Reader.this.AsyncStopReading();
                        if (er9 != READER_ERR.MT_OK_ERR) {
                            Reader.this.errhandle_IT(er9);
                        }
                        if (Reader.this.IT_E7_step == 2) {
                            Reader.this.toDlog("set fre " + String.valueOf(Reader.this.IT_E7_lowfre));
                            HoptableData_ST hdst22 = Reader.this.new HoptableData_ST();
                            hdst22.lenhtb = 1;
                            hdst22.htb[0] = Reader.this.IT_E7_lowfre;
                            er9 = Reader.this.ParamSet(Mtr_Param.MTR_PARAM_FREQUENCY_HOPTABLE, hdst22);
                            if (er9 != READER_ERR.MT_OK_ERR) {
                                Reader.this.errhandle_IT(er9);
                            }
                            Reader.this.IT_E7_step = 3;
                        } else if (Reader.this.IT_E7_step == 3) {
                            Reader.this.toDlog("set fre " + String.valueOf(Reader.this.IT_E7_highfre));
                            HoptableData_ST hdst23 = Reader.this.new HoptableData_ST();
                            hdst23.lenhtb = 1;
                            hdst23.htb[0] = Reader.this.IT_E7_highfre;
                            er9 = Reader.this.ParamSet(Mtr_Param.MTR_PARAM_FREQUENCY_HOPTABLE, hdst23);
                            if (er9 != READER_ERR.MT_OK_ERR) {
                                Reader.this.errhandle_IT(er9);
                            }
                            Reader.this.IT_E7_step = 4;
                        } else if (Reader.this.IT_E7_step == 4) {
                            Reader.this.IT_E7_istargetA = !r5.IT_E7_istargetA;
                            int[] vala = {0};
                            vala[0] = -1;
                            READER_ERR er10 = Reader.this.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_Q, vala);
                            if (er10 != READER_ERR.MT_OK_ERR) {
                                Reader.this.errhandle_IT(er10);
                            }
                            READER_ERR er11 = Reader.this.ParamSet(Mtr_Param.MTR_PARAM_FREQUENCY_REGION, Reader.this.IT_E7_rg);
                            if (er11 != READER_ERR.MT_OK_ERR) {
                                Reader.this.errhandle_IT(er11);
                            }
                            vala[0] = 0;
                            if (Reader.this.IT_E7_istargetA) {
                                Reader.this.toDlog("set to A");
                            } else {
                                vala[0] = 1;
                                Reader.this.toDlog("set to B");
                            }
                            READER_ERR er12 = Reader.this.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_TARGET, vala);
                            if (er12 != READER_ERR.MT_OK_ERR) {
                                Reader.this.errhandle_IT(er12);
                            }
                            if (Reader.this.IT_E7_istargetA) {
                                Reader.this.toDlog("set to 107,S2 to A 3 fre");
                                vala[0] = 107;
                                READER_ERR er13 = Reader.this.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_TAGENCODING, vala);
                                if (er13 != READER_ERR.MT_OK_ERR) {
                                    Reader.this.errhandle_IT(er13);
                                }
                                Reader reader3 = Reader.this;
                                reader3.SetFilterSessioninTargetA(reader3.pants, Reader.this.IT_E7_centrefre, Reader.this.IT_E7_pow);
                                Reader reader4 = Reader.this;
                                reader4.SetFilterSessioninTargetA(reader4.pants, Reader.this.IT_E7_lowfre, Reader.this.IT_E7_pow);
                                Reader reader5 = Reader.this;
                                reader5.SetFilterSessioninTargetA(reader5.pants, Reader.this.IT_E7_highfre, Reader.this.IT_E7_pow);
                            }
                            Reader.this.toDlog("gen2ode " + String.valueOf(Reader.this.IT_E7_rfm));
                            vala[0] = Reader.this.IT_E7_rfm;
                            er9 = Reader.this.ParamSet(Mtr_Param.MTR_PARAM_POTL_GEN2_TAGENCODING, vala);
                            if (er9 != READER_ERR.MT_OK_ERR) {
                                Reader.this.errhandle_IT(er9);
                            }
                            Reader.this.IT_E7_step = 0;
                        }
                        Reader.this.toDlog(" to start");
                        if (Reader.this.isIT_E7_run) {
                            if (Reader.this.IT_E7_step == 0) {
                                Reader reader6 = Reader.this;
                                er9 = reader6.AsyncStartReading(reader6.pants, Reader.this.pants.length, Reader.this.poption);
                            } else {
                                Reader reader7 = Reader.this;
                                er9 = reader7.AsyncStartReading(reader7.pants, Reader.this.pants.length, Reader.this.poption | 64);
                            }
                        }
                        if (er9 != READER_ERR.MT_OK_ERR) {
                            Reader.this.errhandle_IT(er9);
                        }
                        Reader.this.IT_E7_start = System.currentTimeMillis();
                        Reader.this.vstaticstarttick = System.currentTimeMillis() - Reader.this.IT_E7_start;
                    }
                    Reader reader8 = Reader.this;
                    reader8.totalcountlast = reader8.totalcount;
                    Reader.this.totalcount = 0;
                }
                if (!Reader.this.isIT_E7_run) {
                    return;
                }
            }
        }
    }
}
