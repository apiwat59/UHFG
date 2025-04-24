package com.rfid.trans;

import android.os.SystemClock;
import android.util.Log;
import androidx.core.internal.view.SupportMenu;
import androidx.lifecycle.CoroutineLiveDataKt;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.vectordrawable.graphics.drawable.PathInterpolatorCompat;
import com.gg.reader.api.protocol.gx.Message;
import java.util.List;
import kotlin.jvm.internal.ByteCompanionObject;
import kotlinx.coroutines.scheduling.WorkQueueKt;
import org.apache.log4j.Priority;
import org.apache.log4j.net.SyslogAppender;

/* loaded from: classes.dex */
public class BaseReader {
    private TagCallback callback;
    private RFIDLogCallBack msgCallback;
    public MessageTran msg = new MessageTran();
    private long maxScanTime = 2000;
    private int[] recvLength = new int[1];
    private byte[] recvBuff = new byte[Priority.INFO_INT];
    private int logswitch = 0;
    private int lastPacket = 0;
    private String strEPC = "";
    ReadTag lasttag = new ReadTag();
    int packIndex = -1;

    public void getCRC(byte[] data, int Len) {
        int current_crc_value = SupportMenu.USER_MASK;
        int i = 0;
        while (i < Len) {
            try {
                current_crc_value ^= data[i] & 255;
                for (int j = 0; j < 8; j++) {
                    if ((current_crc_value & 1) != 0) {
                        current_crc_value = (current_crc_value >> 1) ^ 33800;
                    } else {
                        current_crc_value >>= 1;
                    }
                }
                i++;
            } catch (Exception e) {
                return;
            }
        }
        int j2 = i + 1;
        data[i] = (byte) (current_crc_value & 255);
        data[j2] = (byte) ((current_crc_value >> 8) & 255);
    }

    private boolean CheckCRC(byte[] data, int len) {
        try {
            byte[] daw = new byte[256];
            System.arraycopy(data, 0, daw, 0, len);
            getCRC(daw, len);
            if (daw[len + 1] == 0) {
                if (daw[len] == 0) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public String bytesToHexString(byte[] src, int offset, int length) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src != null) {
            try {
                if (src.length > 0) {
                    for (int i = offset; i < length; i++) {
                        int v = src[i] & 255;
                        String hv = Integer.toHexString(v);
                        if (hv.length() == 1) {
                            stringBuilder.append(0);
                        }
                        stringBuilder.append(hv);
                    }
                    return stringBuilder.toString().toUpperCase();
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public byte[] hexStringToBytes(String hexString) {
        if (hexString != null) {
            try {
                if (!hexString.equals("")) {
                    String hexString2 = hexString.toUpperCase();
                    int length = hexString2.length() / 2;
                    char[] hexChars = hexString2.toCharArray();
                    byte[] d = new byte[length];
                    for (int i = 0; i < length; i++) {
                        int pos = i * 2;
                        d[i] = (byte) ((charToByte(hexChars[pos]) << 4) | charToByte(hexChars[pos + 1]));
                    }
                    return d;
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public void SetCallBack(TagCallback callback) {
        this.callback = callback;
    }

    public void SetMsgCallBack(RFIDLogCallBack callback) {
        this.msgCallback = callback;
    }

    public int Connect(String ComPort, int BaudRate, int LogFlag) {
        this.logswitch = LogFlag;
        return this.msg.open(ComPort, BaudRate);
    }

    public void PowerControll(int PowerEn) {
    }

    public int DisConnect() {
        return this.msg.close();
    }

    private int SendCMD(byte[] CMD) {
        if (this.logswitch == 1) {
            Log.d("Send", bytesToHexString(CMD, 0, CMD.length));
            RFIDLogCallBack rFIDLogCallBack = this.msgCallback;
            if (rFIDLogCallBack != null) {
                rFIDLogCallBack.SendMessageCallback(CMD);
            }
        }
        return this.msg.Write(CMD);
    }

    private int GetCMDData(byte[] data, int[] Nlen, int cmd, int endTime) {
        byte[] btArray = new byte[2000];
        long beginTime = System.currentTimeMillis();
        int btLength = 0;
        while (System.currentTimeMillis() - beginTime < endTime) {
            try {
                try {
                    byte[] buffer = this.msg.Read();
                    if (buffer != null) {
                        if (this.logswitch == 1) {
                            Log.d("Recv", bytesToHexString(buffer, 0, buffer.length));
                            RFIDLogCallBack rFIDLogCallBack = this.msgCallback;
                            if (rFIDLogCallBack != null) {
                                rFIDLogCallBack.RecvMessageCallback(buffer);
                            }
                        }
                        int Count = buffer.length;
                        if (Count != 0) {
                            byte[] daw = new byte[Count + btLength];
                            System.arraycopy(btArray, 0, daw, 0, btLength);
                            System.arraycopy(buffer, 0, daw, btLength, Count);
                            int index = 0;
                            while (daw.length - index > 4) {
                                try {
                                    if ((daw[index] & 255) >= 4) {
                                        if ((daw[index + 2] & 255) != cmd) {
                                            try {
                                                if ((daw[index + 2] & 255) != 0 && (daw[index + 2] & 255) != 238) {
                                                }
                                            } catch (Exception e) {
                                                e = e;
                                                e.toString();
                                                return 48;
                                            }
                                        }
                                        int len = daw[index] & 255;
                                        if (daw.length < index + len + 1) {
                                            break;
                                        }
                                        byte[] epcArr = new byte[len + 1];
                                        System.arraycopy(daw, index, epcArr, 0, epcArr.length);
                                        if (CheckCRC(epcArr, epcArr.length)) {
                                            System.arraycopy(epcArr, 0, data, 0, epcArr.length);
                                            Nlen[0] = epcArr.length;
                                            return 0;
                                        }
                                        index++;
                                    }
                                    index++;
                                } catch (Exception e2) {
                                    e = e2;
                                    e.toString();
                                    return 48;
                                }
                            }
                            if (daw.length > index) {
                                int btLength2 = daw.length - index;
                                try {
                                    System.arraycopy(daw, index, btArray, 0, btLength2);
                                    btLength = btLength2;
                                } catch (Exception e3) {
                                    e = e3;
                                    e.toString();
                                    return 48;
                                }
                            } else {
                                btLength = 0;
                            }
                        }
                    }
                } catch (Exception e4) {
                    e = e4;
                }
            } catch (Exception e5) {
                e = e5;
            }
        }
        return 48;
    }

    public int GetReaderInformation(byte[] ComAddr, byte[] TVersionInfo, byte[] ReaderType, byte[] TrType, byte[] band, byte[] dmaxfre, byte[] dminfre, byte[] powerdBm, byte[] ScanTime, byte[] Ant, byte[] BeepEn, byte[] OutputRep, byte[] CheckAnt) {
        byte[] buffer = {4, ComAddr[0], 33, 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 33, 500);
        if (result == 0) {
            byte[] bArr = this.recvBuff;
            ComAddr[0] = bArr[1];
            TVersionInfo[0] = bArr[4];
            TVersionInfo[1] = bArr[5];
            ReaderType[0] = bArr[6];
            TrType[0] = bArr[7];
            dmaxfre[0] = (byte) (bArr[8] & 63);
            dminfre[0] = (byte) (bArr[9] & 63);
            band[0] = (byte) (((bArr[9] & 192) >> 6) | ((bArr[8] & 192) >> 4));
            powerdBm[0] = bArr[10];
            ScanTime[0] = bArr[11];
            this.maxScanTime = (ScanTime[0] & 255) * 100;
            Ant[0] = bArr[12];
            BeepEn[0] = bArr[13];
            OutputRep[0] = bArr[14];
            CheckAnt[0] = bArr[15];
            return 0;
        }
        return 48;
    }

    public int SetInventoryScanTime(byte ComAddr, byte ScanTime) {
        byte[] buffer = {5, ComAddr, 37, ScanTime, 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 37, 500);
        if (result == 0) {
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    /* JADX WARN: Code restructure failed: missing block: B:162:0x0295, code lost:
    
        if (r13.length <= r15) goto L142;
     */
    /* JADX WARN: Code restructure failed: missing block: B:164:0x0298, code lost:
    
        r7 = r13.length - r15;
        r0 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:166:0x029b, code lost:
    
        java.lang.System.arraycopy(r13, r15, r5, 0, r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:174:0x029f, code lost:
    
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:176:0x02a3, code lost:
    
        r0 = 0;
        r7 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:178:0x02a9, code lost:
    
        r0 = e;
     */
    /* JADX WARN: Removed duplicated region for block: B:22:0x02ea A[LOOP:0: B:5:0x0021->B:22:0x02ea, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:23:0x02fc A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private int GetInventoryData(byte r32, int r33, int r34, java.util.List<com.rfid.trans.ReadTag> r35, int[] r36, boolean r37) {
        /*
            Method dump skipped, instructions count: 767
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.rfid.trans.BaseReader.GetInventoryData(byte, int, int, java.util.List, int[], boolean):int");
    }

    public void StopInventory(byte ComAddr) {
        byte[] buffer = {4, ComAddr, -109, 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
    }

    private int GetInventorySingleData(byte ComAddr, int cmd, int scanTime, List<ReadTag> tagList, int[] CardNum) {
        int scanTime2;
        byte[] btArray;
        byte[] btArray2;
        int Count;
        int btLength;
        long beginTime;
        byte[] btArray3;
        int Count2;
        int btLength2;
        long beginTime2;
        byte[] buffer;
        int index;
        byte[] btArray4;
        int index2;
        int len;
        int scanTime3 = scanTime == 0 ? 120000 : scanTime;
        int i = 0;
        CardNum[0] = 0;
        byte[] btArray5 = new byte[2000];
        long beginTime3 = SystemClock.elapsedRealtime();
        int btLength3 = 0;
        byte ComAddr2 = ComAddr;
        while (true) {
            try {
                byte[] buffer2 = this.msg.Read();
                if (buffer2 != null) {
                    if (this.logswitch == 1) {
                        try {
                            Log.d("Recv", bytesToHexString(buffer2, i, buffer2.length));
                            RFIDLogCallBack rFIDLogCallBack = this.msgCallback;
                            if (rFIDLogCallBack != null) {
                                rFIDLogCallBack.RecvMessageCallback(buffer2);
                            }
                        } catch (Exception e) {
                            e = e;
                            e.toString();
                            return 48;
                        }
                    }
                    beginTime3 = SystemClock.elapsedRealtime();
                    try {
                        int Count3 = buffer2.length;
                        if (Count3 == 0) {
                            scanTime2 = scanTime3;
                            btArray = btArray5;
                        } else {
                            try {
                                byte[] daw = new byte[Count3 + btLength3];
                                System.arraycopy(btArray5, i, daw, i, btLength3);
                                System.arraycopy(buffer2, i, daw, btLength3, Count3);
                                int index3 = 0;
                                while (true) {
                                    if (daw.length - index3 <= 5) {
                                        scanTime2 = scanTime3;
                                        btArray2 = btArray5;
                                        Count = Count3;
                                        btLength = btLength3;
                                        beginTime = beginTime3;
                                        break;
                                    }
                                    if ((ComAddr2 & 255) == 255) {
                                        ComAddr2 = 0;
                                    }
                                    try {
                                        if ((daw[index3] & 255) >= 5 && daw[index3 + 1] == ComAddr2 && (daw[index3 + 2] & 255) == cmd) {
                                            int len2 = daw[index3] & 255;
                                            scanTime2 = scanTime3;
                                            if (daw.length < index3 + len2 + 1) {
                                                btArray2 = btArray5;
                                                Count = Count3;
                                                btLength = btLength3;
                                                beginTime = beginTime3;
                                                break;
                                            }
                                            try {
                                                byte[] epcArr = new byte[len2 + 1];
                                                System.arraycopy(daw, index3, epcArr, 0, epcArr.length);
                                                if (CheckCRC(epcArr, epcArr.length)) {
                                                    int nLen = (epcArr[0] & 255) + 1;
                                                    int index4 = index3 + nLen;
                                                    int nLen2 = epcArr[3];
                                                    int status = nLen2 & 255;
                                                    if (status != 1 && status != 2 && status != 3 && status != 4) {
                                                        return status;
                                                    }
                                                    Count2 = Count3;
                                                    int num = epcArr[5] & 255;
                                                    if (num > 0) {
                                                        try {
                                                            CardNum[0] = CardNum[0] + 1;
                                                            int m = 6;
                                                            btLength2 = btLength3;
                                                            int btLength4 = 0;
                                                            while (btLength4 < num) {
                                                                long beginTime4 = beginTime3;
                                                                try {
                                                                    int epclen = epcArr[m] & 255 & WorkQueueKt.MASK;
                                                                    byte[] buffer3 = buffer2;
                                                                    int fastid = (epcArr[m] & 255) >> 7;
                                                                    ReadTag tag = new ReadTag();
                                                                    int num2 = num;
                                                                    tag.antId = 1;
                                                                    if (epclen > 0) {
                                                                        index2 = index4;
                                                                        byte[] btArr = new byte[epclen];
                                                                        len = len2;
                                                                        btArray4 = btArray5;
                                                                        try {
                                                                            System.arraycopy(epcArr, m + 1, btArr, 0, btArr.length);
                                                                            if (fastid == 0) {
                                                                                tag.epcId = bytesToHexString(btArr, 0, btArr.length);
                                                                                tag.memId = null;
                                                                            } else {
                                                                                String epcandid = bytesToHexString(btArr, 0, btArr.length);
                                                                                if (epcandid.length() == 24) {
                                                                                    tag.epcId = "";
                                                                                    tag.memId = epcandid;
                                                                                } else {
                                                                                    tag.epcId = epcandid.substring(0, epcandid.length() - 24);
                                                                                    tag.memId = epcandid.substring(epcandid.length() - 24, epcandid.length());
                                                                                }
                                                                            }
                                                                            StopInventory(ComAddr2);
                                                                        } catch (Exception e2) {
                                                                            e = e2;
                                                                            e.toString();
                                                                            return 48;
                                                                        }
                                                                    } else {
                                                                        btArray4 = btArray5;
                                                                        index2 = index4;
                                                                        len = len2;
                                                                        tag.epcId = "";
                                                                        tag.memId = null;
                                                                    }
                                                                    tag.rssi = epcArr[m + 1 + epclen] & 255;
                                                                    tag.phase = 0;
                                                                    if (tagList != null) {
                                                                        tagList.add(tag);
                                                                    }
                                                                    m = m + 2 + epclen;
                                                                    btLength4++;
                                                                    beginTime3 = beginTime4;
                                                                    buffer2 = buffer3;
                                                                    num = num2;
                                                                    index4 = index2;
                                                                    len2 = len;
                                                                    btArray5 = btArray4;
                                                                } catch (Exception e3) {
                                                                    e = e3;
                                                                }
                                                            }
                                                            btArray3 = btArray5;
                                                            beginTime2 = beginTime3;
                                                            buffer = buffer2;
                                                            index = index4;
                                                        } catch (Exception e4) {
                                                            e = e4;
                                                        }
                                                    } else {
                                                        btArray3 = btArray5;
                                                        btLength2 = btLength3;
                                                        beginTime2 = beginTime3;
                                                        buffer = buffer2;
                                                        index = index4;
                                                    }
                                                    if (status == 1 || status == 2) {
                                                        return 0;
                                                    }
                                                    index3 = index;
                                                } else {
                                                    btArray3 = btArray5;
                                                    Count2 = Count3;
                                                    btLength2 = btLength3;
                                                    beginTime2 = beginTime3;
                                                    buffer = buffer2;
                                                    index3++;
                                                }
                                                Count3 = Count2;
                                                scanTime3 = scanTime2;
                                                btLength3 = btLength2;
                                                beginTime3 = beginTime2;
                                                buffer2 = buffer;
                                                btArray5 = btArray3;
                                            } catch (Exception e5) {
                                                e = e5;
                                            }
                                        } else {
                                            index3++;
                                            Count3 = Count3;
                                            scanTime3 = scanTime3;
                                            btLength3 = btLength3;
                                            beginTime3 = beginTime3;
                                            buffer2 = buffer2;
                                            btArray5 = btArray5;
                                        }
                                    } catch (Exception e6) {
                                        e = e6;
                                    }
                                }
                                try {
                                    if (daw.length > index3) {
                                        btLength3 = daw.length - index3;
                                        btArray = btArray2;
                                        i = 0;
                                        try {
                                            System.arraycopy(daw, index3, btArray, 0, btLength3);
                                        } catch (Exception e7) {
                                            e = e7;
                                            e.toString();
                                            return 48;
                                        }
                                    } else {
                                        btArray = btArray2;
                                        i = 0;
                                        btLength3 = 0;
                                    }
                                    beginTime3 = beginTime;
                                } catch (Exception e8) {
                                    e = e8;
                                }
                            } catch (Exception e9) {
                                e = e9;
                            }
                        }
                    } catch (Exception e10) {
                        e = e10;
                    }
                } else {
                    scanTime2 = scanTime3;
                    btArray = btArray5;
                    int btLength5 = btLength3;
                    try {
                        SystemClock.sleep(5L);
                        btLength3 = btLength5;
                    } catch (Exception e11) {
                        e = e11;
                        e.toString();
                        return 48;
                    }
                }
                try {
                    if (SystemClock.elapsedRealtime() - beginTime3 >= CoroutineLiveDataKt.DEFAULT_TIMEOUT) {
                        return 48;
                    }
                    btArray5 = btArray;
                    scanTime3 = scanTime2;
                } catch (Exception e12) {
                    e = e12;
                    e.toString();
                    return 48;
                }
            } catch (Exception e13) {
                e = e13;
            }
        }
    }

    public int Inventory_G2(byte ComAddr, byte QValue, byte Session, byte AdrTID, byte LenTID, byte Target, byte Ant, byte Scantime, byte MaskMem, byte[] MaskAdr, byte MaskLen, byte[] MaskData, List<ReadTag> tagList, int[] CardNum) {
        byte[] buffer;
        if (MaskLen == 0) {
            if (LenTID == 0) {
                buffer = new byte[]{9, ComAddr, 1, QValue, Session, Target, Ant, Scantime, 0, 0};
            } else {
                buffer = new byte[]{11, ComAddr, 1, QValue, Session, AdrTID, LenTID, Target, Ant, Scantime, 0, 0};
            }
        } else {
            int mLen = MaskLen & 255;
            int maskbyte = (mLen + 7) / 8;
            if (LenTID == 0) {
                byte[] buffer2 = new byte[maskbyte + 14];
                buffer2[0] = (byte) (maskbyte + 13);
                buffer2[1] = ComAddr;
                buffer2[2] = 1;
                buffer2[3] = QValue;
                buffer2[4] = Session;
                buffer2[5] = MaskMem;
                buffer2[6] = MaskAdr[0];
                buffer2[7] = MaskAdr[1];
                buffer2[8] = MaskLen;
                System.arraycopy(MaskData, 0, buffer2, 9, maskbyte);
                buffer2[maskbyte + 9] = Target;
                buffer2[maskbyte + 10] = Ant;
                buffer2[maskbyte + 11] = Scantime;
                buffer = buffer2;
            } else {
                byte[] buffer3 = new byte[maskbyte + 16];
                buffer3[0] = (byte) (maskbyte + 15);
                buffer3[1] = ComAddr;
                buffer3[2] = 1;
                buffer3[3] = QValue;
                buffer3[4] = Session;
                buffer3[5] = MaskMem;
                buffer3[6] = MaskAdr[0];
                buffer3[7] = MaskAdr[1];
                buffer3[8] = MaskLen;
                System.arraycopy(MaskData, 0, buffer3, 9, maskbyte);
                buffer3[maskbyte + 9] = AdrTID;
                buffer3[maskbyte + 10] = LenTID;
                buffer3[maskbyte + 11] = Target;
                buffer3[maskbyte + 12] = Ant;
                buffer3[maskbyte + 13] = Scantime;
                buffer = buffer3;
            }
        }
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetInventoryData(ComAddr, 1, (Scantime & 255) * 100, tagList, CardNum, true);
        return result;
    }

    public int Inventory_NoCallback(byte ComAddr, byte QValue, byte Session, byte AdrTID, byte LenTID, byte Target, byte Ant, byte Scantime, byte MaskMem, byte[] MaskAdr, byte MaskLen, byte[] MaskData, List<ReadTag> tagList, int[] CardNum) {
        byte[] buffer;
        if (MaskLen == 0) {
            if (LenTID == 0) {
                buffer = new byte[]{9, ComAddr, 1, QValue, Session, Target, Ant, Scantime, 0, 0};
            } else {
                buffer = new byte[]{11, ComAddr, 1, QValue, Session, AdrTID, LenTID, Target, Ant, Scantime, 0, 0};
            }
        } else {
            int mLen = MaskLen & 255;
            int maskbyte = (mLen + 7) / 8;
            if (LenTID == 0) {
                byte[] buffer2 = new byte[maskbyte + 14];
                buffer2[0] = (byte) (maskbyte + 13);
                buffer2[1] = ComAddr;
                buffer2[2] = 1;
                buffer2[3] = QValue;
                buffer2[4] = Session;
                buffer2[5] = MaskMem;
                buffer2[6] = MaskAdr[0];
                buffer2[7] = MaskAdr[1];
                buffer2[8] = MaskLen;
                System.arraycopy(MaskData, 0, buffer2, 9, maskbyte);
                buffer2[maskbyte + 9] = Target;
                buffer2[maskbyte + 10] = Ant;
                buffer2[maskbyte + 11] = Scantime;
                buffer = buffer2;
            } else {
                byte[] buffer3 = new byte[maskbyte + 16];
                buffer3[0] = (byte) (maskbyte + 15);
                buffer3[1] = ComAddr;
                buffer3[2] = 1;
                buffer3[3] = QValue;
                buffer3[4] = Session;
                buffer3[5] = MaskMem;
                buffer3[6] = MaskAdr[0];
                buffer3[7] = MaskAdr[1];
                buffer3[8] = MaskLen;
                System.arraycopy(MaskData, 0, buffer3, 9, maskbyte);
                buffer3[maskbyte + 9] = AdrTID;
                buffer3[maskbyte + 10] = LenTID;
                buffer3[maskbyte + 11] = Target;
                buffer3[maskbyte + 12] = Ant;
                buffer3[maskbyte + 13] = Scantime;
                buffer = buffer3;
            }
        }
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetInventoryData(ComAddr, 1, (Scantime & 255) * 100, tagList, CardNum, false);
        return result;
    }

    public int InventorySingle_G2(byte ComAddr, byte QValue, byte Session, byte AdrTID, byte LenTID, List<ReadTag> tagList, int[] CardNum) {
        byte[] buffer;
        if (LenTID == 0) {
            byte[] buffer2 = {9, ComAddr, 1, QValue, Session, 0, ByteCompanionObject.MIN_VALUE, 3, 0, 0};
            buffer = buffer2;
        } else {
            byte[] buffer3 = {11, ComAddr, 1, QValue, Session, AdrTID, LenTID, 0, ByteCompanionObject.MIN_VALUE, 3, 0, 0};
            buffer = buffer3;
        }
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        return GetInventorySingleData(ComAddr, 1, 500, tagList, CardNum);
    }

    public int Inventory_GJB(byte ComAddr, byte Algo, byte Ant, byte Scantime, List<ReadTag> tagList, int[] CardNum) {
        byte[] buffer = {7, ComAddr, 86, Algo, Ant, Scantime, 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        return GetInventoryData(ComAddr, 86, (Scantime & 255) * 100, tagList, CardNum, true);
    }

    public int Inventory_GB(byte ComAddr, byte Ant, byte Scantime, List<ReadTag> tagList, int[] CardNum) {
        byte[] buffer = {6, ComAddr, 86, Ant, Scantime, 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        return GetInventoryData(ComAddr, 86, (Scantime & 255) * 100, tagList, CardNum, true);
    }

    /* JADX WARN: Code restructure failed: missing block: B:100:?, code lost:
    
        return 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:91:0x01a2, code lost:
    
        if (r29.strEPC.length() == 0) goto L168;
     */
    /* JADX WARN: Code restructure failed: missing block: B:92:0x01a4, code lost:
    
        r0 = r29.lasttag;
        r6 = r29.callback;
     */
    /* JADX WARN: Code restructure failed: missing block: B:93:0x01a8, code lost:
    
        if (r6 == null) goto L96;
     */
    /* JADX WARN: Code restructure failed: missing block: B:94:0x01aa, code lost:
    
        r6.tagCallback(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:95:0x01ad, code lost:
    
        if (r33 == null) goto L98;
     */
    /* JADX WARN: Code restructure failed: missing block: B:96:0x01af, code lost:
    
        r33.add(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:97:0x01b2, code lost:
    
        r29.strEPC = "";
     */
    /* JADX WARN: Code restructure failed: missing block: B:98:0x01b4, code lost:
    
        return 0;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private int GetInventoryMixData(byte r30, int r31, int r32, java.util.List<com.rfid.trans.ReadTag> r33, int[] r34) {
        /*
            Method dump skipped, instructions count: 578
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.rfid.trans.BaseReader.GetInventoryMixData(byte, int, int, java.util.List, int[]):int");
    }

    public int Inventory_Mix(byte ComAddr, byte QValue, byte Session, byte MaskMem, byte[] MaskAdr, byte MaskLen, byte[] MaskData, byte ReadMem, byte[] ReadAdr, byte ReadLen, byte[] Pwd, byte Target, byte Ant, byte Scantime, List<ReadTag> tagList, int[] CardNum) {
        byte[] buffer;
        byte[] bArr = new byte[18];
        if (MaskLen == 0) {
            buffer = new byte[]{17, ComAddr, 25, QValue, Session, ReadMem, ReadAdr[0], ReadAdr[1], ReadLen, Pwd[0], Pwd[1], Pwd[2], Pwd[3], Target, Ant, Scantime, 0, 0};
        } else {
            int len = (MaskLen + 7) / 8;
            byte[] buffer2 = new byte[len + 22];
            buffer2[0] = (byte) (len + 21);
            buffer2[1] = ComAddr;
            buffer2[2] = 25;
            buffer2[3] = QValue;
            buffer2[4] = Session;
            buffer2[5] = MaskMem;
            buffer2[6] = MaskAdr[0];
            buffer2[7] = MaskAdr[1];
            buffer2[8] = MaskLen;
            if (len > 0) {
                System.arraycopy(MaskData, 0, buffer2, 9, len);
            }
            buffer2[len + 9] = ReadMem;
            buffer2[len + 10] = ReadAdr[0];
            buffer2[len + 11] = ReadAdr[1];
            buffer2[len + 12] = ReadLen;
            buffer2[len + 13] = Pwd[0];
            buffer2[len + 14] = Pwd[1];
            buffer2[len + 15] = Pwd[2];
            buffer2[len + 16] = Pwd[3];
            buffer2[len + 17] = Target;
            buffer2[len + 18] = Ant;
            buffer2[len + 19] = Scantime;
            buffer = buffer2;
        }
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        return GetInventoryMixData(ComAddr, 25, (Scantime & 255) * 100, tagList, CardNum);
    }

    public int SetRfPower(byte ComAddr, byte power) {
        byte[] buffer = {5, ComAddr, 47, power, 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 47, 500);
        if (result == 0) {
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    public int SetAddress(byte ComAddr, byte newAddr) {
        byte[] buffer = {5, ComAddr, 36, newAddr, 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 36, 500);
        if (result == 0) {
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    public int SetRegion(byte ComAddr, int band, int maxfre, int minfre) {
        byte[] buffer = {6, ComAddr, 34, (byte) (((band & 12) << 4) | (maxfre & 63)), (byte) (((band & 3) << 6) | (minfre & 63)), 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 34, 500);
        if (result == 0) {
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    public int SetRegion(byte ComAddr, int opt, int band, int maxfre, int minfre) {
        byte[] buffer = {8, ComAddr, 34, (byte) opt, (byte) band, (byte) maxfre, (byte) minfre, 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 34, 500);
        if (result == 0) {
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    public int SetAntennaMultiplexing(byte ComAddr, byte SetOnce, byte AntCfg1, byte AntCfg2) {
        byte[] buffer = {7, ComAddr, 63, SetOnce, AntCfg1, AntCfg2, 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 63, 500);
        if (result == 0) {
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    public int SetBaudRate(byte ComAddr, byte baud) {
        byte[] buffer = {5, ComAddr, 40, baud, 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 40, 1000);
        if (result == 0) {
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    public int ConfigDRM(byte ComAddr, byte[] DRM) {
        byte[] buffer = {5, ComAddr, -112, DRM[0], 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, SyslogAppender.LOG_LOCAL2, 400);
        if (result == 0) {
            byte[] bArr = this.recvBuff;
            if (bArr[3] == 0) {
                DRM[0] = bArr[4];
            }
            return bArr[3] & 255;
        }
        return 48;
    }

    public int SetGPIO(byte ComAddr, byte OutputPin) {
        byte[] buffer = {5, ComAddr, 70, OutputPin, 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 70, 1000);
        if (result == 0) {
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    public int GetDeviceID(byte ComAddr, byte[] DeviceID) {
        byte[] buffer = {4, ComAddr, 76, 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 76, 1000);
        if (result == 0) {
            byte[] bArr = this.recvBuff;
            if (bArr[3] == 0) {
                System.arraycopy(bArr, 4, DeviceID, 0, 4);
            }
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    public int GetGPIOStatus(byte ComAddr, byte[] OutputPin) {
        byte[] buffer = {4, ComAddr, 71, 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 71, 1000);
        if (result == 0) {
            byte[] bArr = this.recvBuff;
            if (bArr[3] == 0) {
                OutputPin[0] = bArr[4];
            }
            return bArr[3] & 255;
        }
        return 48;
    }

    public int SetWritePower(byte ComAddr, byte WritePower) {
        byte[] buffer = {5, ComAddr, 121, WritePower, 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 121, 1000);
        if (result == 0) {
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    public int GetWritePower(byte ComAddr, byte[] WritePower) {
        byte[] buffer = {4, ComAddr, 122, 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 122, 1000);
        if (result == 0) {
            byte[] bArr = this.recvBuff;
            if (bArr[3] == 0) {
                WritePower[0] = bArr[4];
            }
            return bArr[3] & 255;
        }
        return 48;
    }

    public int RetryTimes(byte ComAddr, byte[] Times) {
        byte[] buffer = {5, ComAddr, 123, Times[0], 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 123, 1000);
        if (result == 0) {
            byte[] bArr = this.recvBuff;
            if (bArr[3] == 0) {
                Times[0] = bArr[4];
            }
            return bArr[3] & 255;
        }
        return 48;
    }

    public int SetBeepNotification(byte ComAddr, byte BeepEn) {
        byte[] buffer = {5, ComAddr, 64, BeepEn, 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 64, 400);
        if (result == 0) {
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    public int ReadData_G2(byte ComAddr, byte ENum, byte[] EPC, byte Mem, byte WordPtr, byte Num, byte[] Password, byte MaskMem, byte[] MaskAdr, byte MaskLen, byte[] MaskData, byte[] Data, byte[] Errorcode) {
        int maskbyte;
        byte b;
        if ((ENum & 255) < 16) {
            byte[] buffer = new byte[(ENum * 2) + 13];
            buffer[0] = (byte) ((ENum * 2) + 12);
            buffer[1] = ComAddr;
            buffer[2] = 2;
            buffer[3] = ENum;
            if ((ENum & 255) > 0) {
                System.arraycopy(EPC, 0, buffer, 4, ENum * 2);
            }
            buffer[(ENum * 2) + 4] = Mem;
            buffer[(ENum * 2) + 5] = WordPtr;
            buffer[(ENum * 2) + 6] = Num;
            System.arraycopy(Password, 0, buffer, (ENum * 2) + 7, 4);
            getCRC(buffer, buffer[0] - 1);
            SendCMD(buffer);
            int result = GetCMDData(this.recvBuff, this.recvLength, 2, PathInterpolatorCompat.MAX_NUM_POINTS);
            if (result == 0) {
                byte[] bArr = this.recvBuff;
                if (bArr[3] == 0) {
                    Errorcode[0] = 0;
                    System.arraycopy(bArr, 4, Data, 0, Num * 2);
                    b = 255;
                } else {
                    b = 255;
                    if ((bArr[3] & 255) == 252) {
                        Errorcode[0] = bArr[4];
                    }
                }
                return this.recvBuff[3] & b;
            }
            return 48;
        }
        if ((ENum & 255) != 255 || MaskLen == 0) {
            return 255;
        }
        int mLen = MaskLen & 255;
        if (mLen % 8 == 0) {
            maskbyte = mLen / 8;
        } else {
            maskbyte = (mLen / 8) + 1;
        }
        byte[] buffer2 = new byte[maskbyte + 17];
        buffer2[0] = (byte) (maskbyte + 16);
        buffer2[1] = ComAddr;
        buffer2[2] = 2;
        buffer2[3] = ENum;
        buffer2[4] = Mem;
        buffer2[5] = WordPtr;
        buffer2[6] = Num;
        System.arraycopy(Password, 0, buffer2, 7, 4);
        buffer2[11] = MaskMem;
        buffer2[12] = MaskAdr[0];
        buffer2[13] = MaskAdr[1];
        buffer2[14] = MaskLen;
        System.arraycopy(MaskData, 0, buffer2, 15, maskbyte);
        getCRC(buffer2, buffer2[0] - 1);
        SendCMD(buffer2);
        int result2 = GetCMDData(this.recvBuff, this.recvLength, 2, PathInterpolatorCompat.MAX_NUM_POINTS);
        if (result2 == 0) {
            byte[] bArr2 = this.recvBuff;
            if (bArr2[3] == 0) {
                Errorcode[0] = 0;
                System.arraycopy(bArr2, 4, Data, 0, Num * 2);
            } else if ((bArr2[3] & 255) == 252) {
                Errorcode[0] = bArr2[4];
            }
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    public int ExtReadData_G2(byte ComAddr, byte ENum, byte[] EPC, byte Mem, byte[] WordPtr, byte Num, byte[] Password, byte MaskMem, byte[] MaskAdr, byte MaskLen, byte[] MaskData, byte[] Data, byte[] Errorcode) {
        int maskbyte;
        byte b;
        byte b2;
        if ((ENum & 255) < 16) {
            byte[] buffer = new byte[(ENum * 2) + 14];
            buffer[0] = (byte) ((ENum * 2) + 13);
            buffer[1] = ComAddr;
            buffer[2] = 21;
            buffer[3] = ENum;
            if ((ENum & 255) > 0) {
                System.arraycopy(EPC, 0, buffer, 4, ENum * 2);
            }
            buffer[(ENum * 2) + 4] = Mem;
            buffer[(ENum * 2) + 5] = WordPtr[0];
            buffer[(ENum * 2) + 6] = WordPtr[1];
            buffer[(ENum * 2) + 7] = Num;
            System.arraycopy(Password, 0, buffer, (ENum * 2) + 8, 4);
            getCRC(buffer, buffer[0] - 1);
            SendCMD(buffer);
            int result = GetCMDData(this.recvBuff, this.recvLength, 21, PathInterpolatorCompat.MAX_NUM_POINTS);
            if (result == 0) {
                byte[] bArr = this.recvBuff;
                if (bArr[3] == 0) {
                    Errorcode[0] = 0;
                    System.arraycopy(bArr, 4, Data, 0, Num * 2);
                    b2 = 255;
                } else {
                    b2 = 255;
                    if ((bArr[3] & 255) == 252) {
                        Errorcode[0] = bArr[4];
                    }
                }
                return this.recvBuff[3] & b2;
            }
            return 48;
        }
        if ((ENum & 255) != 255 || MaskLen == 0) {
            return 255;
        }
        int mLen = MaskLen & 255;
        if (mLen % 8 == 0) {
            maskbyte = mLen / 8;
        } else {
            maskbyte = (mLen / 8) + 1;
        }
        byte[] buffer2 = new byte[maskbyte + 18];
        buffer2[0] = (byte) (maskbyte + 17);
        buffer2[1] = ComAddr;
        buffer2[2] = 21;
        buffer2[3] = ENum;
        buffer2[4] = Mem;
        buffer2[5] = WordPtr[0];
        buffer2[6] = WordPtr[1];
        buffer2[7] = Num;
        System.arraycopy(Password, 0, buffer2, 8, 4);
        buffer2[12] = MaskMem;
        buffer2[13] = MaskAdr[0];
        buffer2[14] = MaskAdr[1];
        buffer2[15] = MaskLen;
        System.arraycopy(MaskData, 0, buffer2, 16, maskbyte);
        getCRC(buffer2, buffer2[0] - 1);
        SendCMD(buffer2);
        int result2 = GetCMDData(this.recvBuff, this.recvLength, 21, PathInterpolatorCompat.MAX_NUM_POINTS);
        if (result2 == 0) {
            byte[] bArr2 = this.recvBuff;
            if (bArr2[3] == 0) {
                Errorcode[0] = 0;
                System.arraycopy(bArr2, 4, Data, 0, Num * 2);
                b = 255;
            } else {
                b = 255;
                if ((bArr2[3] & 255) == 252) {
                    Errorcode[0] = bArr2[4];
                }
            }
            return this.recvBuff[3] & b;
        }
        return 48;
    }

    public int WriteData_G2(byte ComAddr, byte WNum, byte ENum, byte[] EPC, byte Mem, byte WordPtr, byte[] Writedata, byte[] Password, byte MaskMem, byte[] MaskAdr, byte MaskLen, byte[] MaskData, byte[] Errorcode) {
        int maskbyte;
        byte b;
        if ((ENum & 255) < 16) {
            byte[] buffer = new byte[((ENum + WNum) * 2) + 13];
            buffer[0] = (byte) (((ENum + WNum) * 2) + 12);
            buffer[1] = ComAddr;
            buffer[2] = 3;
            buffer[3] = WNum;
            buffer[4] = ENum;
            if ((ENum & 255) > 0) {
                System.arraycopy(EPC, 0, buffer, 5, ENum * 2);
            }
            buffer[(ENum * 2) + 5] = Mem;
            buffer[(ENum * 2) + 6] = WordPtr;
            System.arraycopy(Writedata, 0, buffer, (ENum * 2) + 7, WNum * 2);
            System.arraycopy(Password, 0, buffer, (ENum * 2) + (WNum * 2) + 7, 4);
            getCRC(buffer, buffer[0] - 1);
            SendCMD(buffer);
            int result = GetCMDData(this.recvBuff, this.recvLength, 3, PathInterpolatorCompat.MAX_NUM_POINTS);
            if (result == 0) {
                byte[] bArr = this.recvBuff;
                if (bArr[3] == 0) {
                    Errorcode[0] = 0;
                    b = 255;
                } else {
                    b = 255;
                    if ((bArr[3] & 255) == 252) {
                        Errorcode[0] = bArr[4];
                    }
                }
                return bArr[3] & b;
            }
            return 48;
        }
        if ((ENum & 255) != 255 || MaskLen == 0) {
            return 255;
        }
        int mLen = MaskLen & 255;
        if (mLen % 8 == 0) {
            maskbyte = mLen / 8;
        } else {
            maskbyte = (mLen / 8) + 1;
        }
        byte[] buffer2 = new byte[(WNum * 2) + 17 + maskbyte];
        buffer2[0] = (byte) ((WNum * 2) + 16 + maskbyte);
        buffer2[1] = ComAddr;
        buffer2[2] = 3;
        buffer2[3] = WNum;
        buffer2[4] = ENum;
        buffer2[5] = Mem;
        buffer2[6] = WordPtr;
        System.arraycopy(Writedata, 0, buffer2, 7, WNum * 2);
        System.arraycopy(Password, 0, buffer2, (WNum * 2) + 7, 4);
        buffer2[(WNum * 2) + 11] = MaskMem;
        buffer2[(WNum * 2) + 12] = MaskAdr[0];
        buffer2[(WNum * 2) + 13] = MaskAdr[1];
        buffer2[(WNum * 2) + 14] = MaskLen;
        System.arraycopy(MaskData, 0, buffer2, (WNum * 2) + 15, maskbyte);
        getCRC(buffer2, buffer2[0] - 1);
        SendCMD(buffer2);
        int result2 = GetCMDData(this.recvBuff, this.recvLength, 3, PathInterpolatorCompat.MAX_NUM_POINTS);
        if (result2 == 0) {
            byte[] bArr2 = this.recvBuff;
            if (bArr2[3] == 0) {
                Errorcode[0] = 0;
            } else if ((bArr2[3] & 255) == 252) {
                Errorcode[0] = bArr2[4];
            }
            return bArr2[3] & 255;
        }
        return 48;
    }

    public int ExtWriteData_G2(byte ComAddr, byte WNum, byte ENum, byte[] EPC, byte Mem, byte[] WordPtr, byte[] Writedata, byte[] Password, byte MaskMem, byte[] MaskAdr, byte MaskLen, byte[] MaskData, byte[] Errorcode) {
        int maskbyte;
        byte b;
        if ((ENum & 255) < 16) {
            byte[] buffer = new byte[((ENum + WNum) * 2) + 14];
            buffer[0] = (byte) (((ENum + WNum) * 2) + 13);
            buffer[1] = ComAddr;
            buffer[2] = 22;
            buffer[3] = WNum;
            buffer[4] = ENum;
            if (ENum > 0) {
                System.arraycopy(EPC, 0, buffer, 5, ENum * 2);
            }
            buffer[(ENum * 2) + 5] = Mem;
            buffer[(ENum * 2) + 6] = WordPtr[0];
            buffer[(ENum * 2) + 7] = WordPtr[1];
            System.arraycopy(Writedata, 0, buffer, (ENum * 2) + 8, WNum * 2);
            System.arraycopy(Password, 0, buffer, (ENum * 2) + (WNum * 2) + 8, 4);
            getCRC(buffer, buffer[0] - 1);
            SendCMD(buffer);
            int result = GetCMDData(this.recvBuff, this.recvLength, 22, PathInterpolatorCompat.MAX_NUM_POINTS);
            if (result == 0) {
                byte[] bArr = this.recvBuff;
                if (bArr[3] == 0) {
                    Errorcode[0] = 0;
                    b = 255;
                } else {
                    b = 255;
                    if ((bArr[3] & 255) == 252) {
                        Errorcode[0] = bArr[4];
                    }
                }
                return bArr[3] & b;
            }
            return 48;
        }
        if ((ENum & 255) != 255 || MaskLen == 0) {
            return 255;
        }
        int mLen = MaskLen & 255;
        if (mLen % 8 == 0) {
            maskbyte = mLen / 8;
        } else {
            maskbyte = (mLen / 8) + 1;
        }
        byte[] buffer2 = new byte[(WNum * 2) + 18 + maskbyte];
        buffer2[0] = (byte) ((WNum * 2) + 17 + maskbyte);
        buffer2[1] = ComAddr;
        buffer2[2] = 22;
        buffer2[3] = WNum;
        buffer2[4] = ENum;
        buffer2[5] = Mem;
        buffer2[6] = WordPtr[0];
        buffer2[7] = WordPtr[1];
        System.arraycopy(Writedata, 0, buffer2, 8, WNum * 2);
        System.arraycopy(Password, 0, buffer2, (WNum * 2) + 8, 4);
        buffer2[(WNum * 2) + 12] = MaskMem;
        buffer2[(WNum * 2) + 13] = MaskAdr[0];
        buffer2[(WNum * 2) + 14] = MaskAdr[1];
        buffer2[(WNum * 2) + 15] = MaskLen;
        System.arraycopy(MaskData, 0, buffer2, (WNum * 2) + 16, maskbyte);
        getCRC(buffer2, buffer2[0] - 1);
        SendCMD(buffer2);
        int result2 = GetCMDData(this.recvBuff, this.recvLength, 22, PathInterpolatorCompat.MAX_NUM_POINTS);
        if (result2 == 0) {
            byte[] bArr2 = this.recvBuff;
            if (bArr2[3] == 0) {
                Errorcode[0] = 0;
            } else if ((bArr2[3] & 255) == 252) {
                Errorcode[0] = bArr2[4];
            }
            return bArr2[3] & 255;
        }
        return 48;
    }

    public int BlockWrite_G2(byte ComAddr, byte WNum, byte ENum, byte[] EPC, byte Mem, byte WordPtr, byte[] Writedata, byte[] Password, byte MaskMem, byte[] MaskAdr, byte MaskLen, byte[] MaskData, byte[] Errorcode) {
        int maskbyte;
        byte ENum2 = ENum;
        if ((ENum2 & 255) < 16) {
            byte[] buffer = new byte[((ENum2 + WNum) * 2) + 13];
            buffer[0] = (byte) (((ENum2 + WNum) * 2) + 12);
            buffer[1] = ComAddr;
            buffer[2] = 16;
            buffer[3] = WNum;
            buffer[4] = ENum2;
            if (ENum2 > 0) {
                System.arraycopy(EPC, 0, buffer, 5, ENum2 * 2);
            }
            buffer[(ENum2 * 2) + 5] = Mem;
            buffer[(ENum2 * 2) + 6] = WordPtr;
            System.arraycopy(Writedata, 0, buffer, (ENum2 * 2) + 7, WNum * 2);
            System.arraycopy(Password, 0, buffer, (ENum2 * 2) + (WNum * 2) + 7, 4);
            getCRC(buffer, buffer[0] - 1);
            SendCMD(buffer);
            int result = GetCMDData(this.recvBuff, this.recvLength, 16, PathInterpolatorCompat.MAX_NUM_POINTS);
            if (result == 0) {
                byte[] bArr = this.recvBuff;
                if (bArr[3] == 0) {
                    Errorcode[0] = 0;
                } else if ((bArr[3] & 255) == 252) {
                    Errorcode[0] = bArr[4];
                }
                return bArr[3] & 255;
            }
            return 48;
        }
        if ((ENum2 & 255) != 255 || MaskLen == 0) {
            return 255;
        }
        int mLen = MaskLen & 255;
        if (mLen % 8 == 0) {
            maskbyte = mLen / 8;
        } else {
            maskbyte = (mLen / 8) + 1;
        }
        byte[] buffer2 = new byte[(WNum * 2) + 17 + maskbyte];
        buffer2[0] = (byte) ((WNum * 2) + 16 + maskbyte);
        buffer2[1] = ComAddr;
        buffer2[2] = 16;
        buffer2[3] = WNum;
        buffer2[4] = ENum2;
        if ((ENum2 & 255) == 255) {
            ENum2 = 0;
        }
        System.arraycopy(EPC, 0, buffer2, 5, ENum2 * 2);
        buffer2[(ENum2 * 2) + 5] = Mem;
        buffer2[(ENum2 * 2) + 6] = WordPtr;
        System.arraycopy(Writedata, 0, buffer2, (ENum2 * 2) + 7, WNum * 2);
        System.arraycopy(Password, 0, buffer2, (ENum2 * 2) + (WNum * 2) + 7, 4);
        buffer2[(ENum2 * 2) + (WNum * 2) + 11] = MaskMem;
        buffer2[(ENum2 * 2) + (WNum * 2) + 12] = MaskAdr[0];
        buffer2[(ENum2 * 2) + (WNum * 2) + 13] = MaskAdr[1];
        buffer2[(ENum2 * 2) + (WNum * 2) + 14] = MaskLen;
        System.arraycopy(MaskData, 0, buffer2, (ENum2 * 2) + (WNum * 2) + 15, maskbyte);
        getCRC(buffer2, buffer2[0] - 1);
        SendCMD(buffer2);
        int result2 = GetCMDData(this.recvBuff, this.recvLength, 16, PathInterpolatorCompat.MAX_NUM_POINTS);
        if (result2 == 0) {
            byte[] bArr2 = this.recvBuff;
            if (bArr2[3] == 0) {
                Errorcode[0] = 0;
            } else if ((bArr2[3] & 255) == 252) {
                Errorcode[0] = bArr2[4];
            }
            return bArr2[3] & 255;
        }
        return 48;
    }

    public int WriteEPC_G2(byte ComAddr, byte ENum, byte[] Password, byte[] WriteEPC, byte[] Errorcode) {
        int epclen = ENum & 255;
        if (epclen > 31 || epclen < 0) {
            return 255;
        }
        byte[] buffer = new byte[(ENum * 2) + 10];
        buffer[0] = (byte) ((ENum * 2) + 9);
        buffer[1] = ComAddr;
        buffer[2] = 4;
        buffer[3] = ENum;
        System.arraycopy(Password, 0, buffer, 4, 4);
        System.arraycopy(WriteEPC, 0, buffer, 8, ENum * 2);
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 4, 2000);
        if (result == 0) {
            byte[] bArr = this.recvBuff;
            if (bArr[3] != 0) {
                if ((bArr[3] & 255) == 252) {
                    Errorcode[0] = bArr[4];
                }
            } else {
                Errorcode[0] = 0;
            }
            return 255 & bArr[3];
        }
        return 48;
    }

    public int Lock_G2(byte ComAddr, byte ENum, byte[] EPC, byte select, byte setprotect, byte[] Password, byte[] Errorcode) {
        byte[] buffer = new byte[(ENum * 2) + 12];
        buffer[0] = (byte) ((ENum * 2) + 11);
        buffer[1] = ComAddr;
        buffer[2] = 6;
        buffer[3] = ENum;
        if (ENum > 0) {
            System.arraycopy(EPC, 0, buffer, 4, ENum * 2);
        }
        buffer[(ENum * 2) + 4] = select;
        buffer[(ENum * 2) + 5] = setprotect;
        System.arraycopy(Password, 0, buffer, (ENum * 2) + 6, 4);
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 6, 1000);
        if (result == 0) {
            byte[] bArr = this.recvBuff;
            if (bArr[3] == 0) {
                Errorcode[0] = 0;
            } else if ((bArr[3] & 255) == 252) {
                Errorcode[0] = bArr[4];
            }
            return bArr[3] & 255;
        }
        return 48;
    }

    public int Kill_G2(byte ComAddr, byte ENum, byte[] EPC, byte[] Password, byte[] Errorcode) {
        byte[] buffer = new byte[(ENum * 2) + 10];
        buffer[0] = (byte) ((ENum * 2) + 9);
        buffer[1] = ComAddr;
        buffer[2] = 5;
        buffer[3] = ENum;
        if (ENum > 0) {
            System.arraycopy(EPC, 0, buffer, 4, ENum * 2);
        }
        System.arraycopy(Password, 0, buffer, (ENum * 2) + 4, 4);
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 5, 1000);
        if (result == 0) {
            byte[] bArr = this.recvBuff;
            if (bArr[3] == 0) {
                Errorcode[0] = 0;
            } else if ((bArr[3] & 255) == 252) {
                Errorcode[0] = bArr[4];
            }
            return bArr[3] & 255;
        }
        return 48;
    }

    public int BlockErase_G2(byte ComAddr, byte ENum, byte[] EPC, byte Mem, byte WordPtr, byte num, byte[] Password, byte[] Errorcode) {
        byte[] buffer = new byte[(ENum * 2) + 13];
        buffer[0] = (byte) ((ENum * 2) + 12);
        buffer[1] = ComAddr;
        buffer[2] = 7;
        buffer[3] = ENum;
        if (ENum > 0) {
            System.arraycopy(EPC, 0, buffer, 4, ENum * 2);
        }
        buffer[(ENum * 2) + 4] = Mem;
        buffer[(ENum * 2) + 5] = WordPtr;
        buffer[(ENum * 2) + 6] = num;
        System.arraycopy(Password, 0, buffer, (ENum * 2) + 7, 4);
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 7, 1000);
        if (result == 0) {
            byte[] bArr = this.recvBuff;
            if (bArr[3] == 0) {
                Errorcode[0] = 0;
            } else if ((bArr[3] & 255) == 252) {
                Errorcode[0] = bArr[4];
            }
            return bArr[3] & 255;
        }
        return 48;
    }

    public int MeasureReturnLoss(byte ComAddr, byte[] TestFreq, byte Ant, byte[] ReturnLoss) {
        byte[] buffer = new byte[10];
        buffer[0] = 9;
        buffer[1] = ComAddr;
        buffer[2] = -111;
        System.arraycopy(TestFreq, 0, buffer, 3, 4);
        buffer[7] = Ant;
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 145, 600);
        if (result == 0) {
            byte[] bArr = this.recvBuff;
            if (bArr[3] == 0) {
                ReturnLoss[0] = bArr[4];
            }
            return bArr[3] & 255;
        }
        return 48;
    }

    public int SetCheckAnt(byte ComAddr, byte CheckAnt) {
        byte[] buffer = {5, ComAddr, 102, CheckAnt, 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 102, 500);
        if (result == 0) {
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    public int SetReadParameter(byte ComAddr, byte[] Parameter) {
        byte[] buffer = {9, ComAddr, 117, Parameter[0], Parameter[1], Parameter[2], Parameter[3], Parameter[4], 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 117, 500);
        if (result == 0) {
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    public int GetReadParameter(byte ComAddr, byte[] Parameter) {
        byte[] buffer = {4, ComAddr, 119, 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 119, 300);
        if (result == 0) {
            System.arraycopy(this.recvBuff, 4, Parameter, 0, 6);
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    public int SetWorkMode(byte ComAddr, byte ReadMode) {
        byte[] buffer = {5, ComAddr, 118, ReadMode, 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 119, 300);
        if (result == 0) {
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    public int MeasureTemperature(byte ComAddr, byte[] Temp) {
        byte[] buffer = {4, ComAddr, -110, 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 146, 600);
        if (result == 0) {
            byte[] bArr = this.recvBuff;
            if (bArr[3] == 0) {
                Temp[0] = bArr[4];
                Temp[1] = bArr[5];
            }
            return bArr[3] & 255;
        }
        return 48;
    }

    public int SetProfile(byte ComAddr, byte[] Profile) {
        byte[] buffer = {5, ComAddr, ByteCompanionObject.MAX_VALUE, Profile[0], 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, WorkQueueKt.MASK, 400);
        if (result == 0) {
            byte[] bArr = this.recvBuff;
            if (bArr[3] == 0) {
                Profile[0] = bArr[4];
            }
            return bArr[3] & 255;
        }
        return 48;
    }

    public int FST_TranImage(byte ComAddr, byte DataLen, byte[] StartAddr, byte[] ImageDate) {
        int len = DataLen & 255;
        byte[] buffer = new byte[len + 8];
        buffer[0] = (byte) (len + 7);
        buffer[1] = ComAddr;
        buffer[2] = -48;
        buffer[3] = DataLen;
        buffer[4] = StartAddr[0];
        buffer[5] = StartAddr[1];
        System.arraycopy(ImageDate, 0, buffer, 6, len);
        getCRC(buffer, (buffer[0] & 255) - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 208, 1500);
        if (result == 0) {
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    public int FST_ShowImage(byte ComAddr, byte ENum, byte[] EPC) {
        int len = ENum * 2;
        byte[] buffer = new byte[len + 6];
        buffer[0] = (byte) (len + 5);
        buffer[1] = ComAddr;
        buffer[2] = -47;
        buffer[3] = ENum;
        if (len > 0) {
            System.arraycopy(EPC, 0, buffer, 4, len);
        }
        getCRC(buffer, (buffer[0] & 255) - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 209, 12000);
        if (result == 0) {
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    public int LedOn_kx2005x(byte ComAddr, byte ENum, byte[] EPC, byte CarrierTime, byte[] Password, byte MaskMem, byte[] MaskAdr, byte MaskLen, byte[] MaskData) {
        int maskbyte;
        if ((ENum & 255) < 16) {
            byte[] buffer = new byte[(ENum * 2) + 11];
            buffer[0] = (byte) ((ENum * 2) + 10);
            buffer[1] = ComAddr;
            buffer[2] = -52;
            buffer[3] = ENum;
            if (ENum > 0 && ENum < 32) {
                System.arraycopy(EPC, 0, buffer, 4, ENum * 2);
            }
            buffer[(ENum * 2) + 4] = CarrierTime;
            System.arraycopy(Password, 0, buffer, (ENum * 2) + 5, 4);
            getCRC(buffer, buffer[0] - 1);
            SendCMD(buffer);
            int result = GetCMDData(this.recvBuff, this.recvLength, 204, PathInterpolatorCompat.MAX_NUM_POINTS);
            if (result == 0) {
                return this.recvBuff[3] & 255;
            }
            return 48;
        }
        if ((ENum & 255) != 255 || MaskLen == 0) {
            return 255;
        }
        int mLen = MaskLen & 255;
        if (mLen % 8 == 0) {
            maskbyte = mLen / 8;
        } else {
            maskbyte = (mLen / 8) + 1;
        }
        byte[] buffer2 = new byte[maskbyte + 15];
        buffer2[0] = (byte) (maskbyte + 14);
        buffer2[1] = ComAddr;
        buffer2[2] = -52;
        buffer2[3] = ENum;
        buffer2[4] = CarrierTime;
        System.arraycopy(Password, 0, buffer2, 5, 4);
        buffer2[9] = MaskMem;
        buffer2[10] = MaskAdr[0];
        buffer2[11] = MaskAdr[1];
        buffer2[12] = MaskLen;
        System.arraycopy(MaskData, 0, buffer2, 13, maskbyte);
        getCRC(buffer2, buffer2[0] - 1);
        SendCMD(buffer2);
        int result2 = GetCMDData(this.recvBuff, this.recvLength, 204, PathInterpolatorCompat.MAX_NUM_POINTS);
        if (result2 == 0) {
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    public int Fd_InitRegfile(byte ComAddr, byte ENum, byte[] EPC, byte[] Password, byte MaskMem, byte[] MaskAdr, byte MaskLen, byte[] MaskData, byte[] Errorcode) {
        int maskbyte;
        if ((ENum & 255) < 0 || (ENum & 255) >= 16) {
            if ((ENum & 255) != 255 || MaskLen == 0) {
                return 255;
            }
            int mLen = MaskLen & 255;
            if (mLen % 8 == 0) {
                maskbyte = mLen / 8;
            } else {
                maskbyte = (mLen / 8) + 1;
            }
            byte[] buffer = new byte[maskbyte + 14];
            buffer[0] = (byte) (maskbyte + 13);
            buffer[1] = ComAddr;
            buffer[2] = -64;
            buffer[3] = ENum;
            System.arraycopy(Password, 0, buffer, 4, 4);
            buffer[8] = MaskMem;
            buffer[9] = MaskAdr[0];
            buffer[10] = MaskAdr[1];
            buffer[11] = MaskLen;
            System.arraycopy(MaskData, 0, buffer, 12, maskbyte);
            getCRC(buffer, buffer[0] - 1);
            SendCMD(buffer);
            int result = GetCMDData(this.recvBuff, this.recvLength, 192, 1500);
            if (result == 0) {
                Errorcode[0] = 0;
                byte[] bArr = this.recvBuff;
                if ((bArr[3] & 255) == 252) {
                    Errorcode[0] = bArr[4];
                }
                return bArr[3] & 255;
            }
            return 48;
        }
        byte[] buffer2 = new byte[(ENum * 2) + 10];
        buffer2[0] = (byte) ((ENum * 2) + 9);
        buffer2[1] = ComAddr;
        buffer2[2] = -64;
        buffer2[3] = ENum;
        if (ENum > 0 && ENum < 16) {
            System.arraycopy(EPC, 0, buffer2, 4, ENum * 2);
        }
        System.arraycopy(Password, 0, buffer2, (ENum * 2) + 4, 4);
        getCRC(buffer2, buffer2[0] - 1);
        SendCMD(buffer2);
        int result2 = GetCMDData(this.recvBuff, this.recvLength, 192, 1500);
        if (result2 == 0) {
            Errorcode[0] = 0;
            byte[] bArr2 = this.recvBuff;
            if ((bArr2[3] & 255) == 252) {
                Errorcode[0] = bArr2[4];
            }
            return bArr2[3] & 255;
        }
        return 48;
    }

    public int Fd_ReadReg(byte ComAddr, byte ENum, byte[] EPC, byte[] RegAddr, byte[] Password, byte MaskMem, byte[] MaskAdr, byte MaskLen, byte[] MaskData, byte[] Data, byte[] Errorcode) {
        int maskbyte;
        byte b;
        if ((ENum & 255) < 0 || (ENum & 255) >= 16) {
            if ((ENum & 255) != 255 || MaskLen == 0) {
                return 255;
            }
            int mLen = MaskLen & 255;
            if (mLen % 8 == 0) {
                maskbyte = mLen / 8;
            } else {
                maskbyte = (mLen / 8) + 1;
            }
            byte[] buffer = new byte[maskbyte + 16];
            buffer[0] = (byte) (maskbyte + 15);
            buffer[1] = ComAddr;
            buffer[2] = -63;
            buffer[3] = ENum;
            System.arraycopy(RegAddr, 0, buffer, 4, 2);
            System.arraycopy(Password, 0, buffer, 6, 4);
            buffer[10] = MaskMem;
            buffer[11] = MaskAdr[0];
            buffer[12] = MaskAdr[1];
            buffer[13] = MaskLen;
            System.arraycopy(MaskData, 0, buffer, 14, maskbyte);
            getCRC(buffer, buffer[0] - 1);
            SendCMD(buffer);
            int result = GetCMDData(this.recvBuff, this.recvLength, 193, 1500);
            if (result == 0) {
                Errorcode[0] = 0;
                byte[] bArr = this.recvBuff;
                if (bArr[3] == 0) {
                    System.arraycopy(bArr, 4, Data, 0, 2);
                } else if ((bArr[3] & 255) == 252) {
                    Errorcode[0] = bArr[4];
                }
                return this.recvBuff[3] & 255;
            }
            return 48;
        }
        byte[] buffer2 = new byte[(ENum * 2) + 12];
        buffer2[0] = (byte) ((ENum * 2) + 11);
        buffer2[1] = ComAddr;
        buffer2[2] = -63;
        buffer2[3] = ENum;
        if (ENum > 0 && ENum < 16) {
            System.arraycopy(EPC, 0, buffer2, 4, ENum * 2);
        }
        System.arraycopy(RegAddr, 0, buffer2, (ENum * 2) + 4, 2);
        System.arraycopy(Password, 0, buffer2, (ENum * 2) + 6, 4);
        getCRC(buffer2, buffer2[0] - 1);
        SendCMD(buffer2);
        int result2 = GetCMDData(this.recvBuff, this.recvLength, 193, 1500);
        if (result2 == 0) {
            Errorcode[0] = 0;
            byte[] bArr2 = this.recvBuff;
            if (bArr2[3] == 0) {
                System.arraycopy(bArr2, 4, Data, 0, 2);
                b = 255;
            } else {
                b = 255;
                if ((bArr2[3] & 255) == 252) {
                    Errorcode[0] = bArr2[4];
                }
            }
            return this.recvBuff[3] & b;
        }
        return 48;
    }

    public int Fd_WriteReg(byte ComAddr, byte ENum, byte[] EPC, byte[] RegAddr, byte[] RegData, byte[] Password, byte MaskMem, byte[] MaskAdr, byte MaskLen, byte[] MaskData, byte[] Errorcode) {
        int maskbyte;
        if ((ENum & 255) < 0 || (ENum & 255) >= 16) {
            if ((ENum & 255) != 255 || MaskLen == 0) {
                return 255;
            }
            int mLen = MaskLen & 255;
            if (mLen % 8 == 0) {
                maskbyte = mLen / 8;
            } else {
                maskbyte = (mLen / 8) + 1;
            }
            byte[] buffer = new byte[maskbyte + 18];
            buffer[0] = (byte) (maskbyte + 17);
            buffer[1] = ComAddr;
            buffer[2] = -62;
            buffer[3] = ENum;
            System.arraycopy(RegAddr, 0, buffer, 4, 2);
            System.arraycopy(RegData, 0, buffer, 6, 2);
            System.arraycopy(Password, 0, buffer, 8, 4);
            buffer[12] = MaskMem;
            buffer[13] = MaskAdr[0];
            buffer[14] = MaskAdr[1];
            buffer[15] = MaskLen;
            System.arraycopy(MaskData, 0, buffer, 16, maskbyte);
            getCRC(buffer, buffer[0] - 1);
            SendCMD(buffer);
            int result = GetCMDData(this.recvBuff, this.recvLength, 194, 1500);
            if (result == 0) {
                Errorcode[0] = 0;
                byte[] bArr = this.recvBuff;
                if ((bArr[3] & 255) == 252) {
                    Errorcode[0] = bArr[4];
                }
                return bArr[3] & 255;
            }
            return 48;
        }
        byte[] buffer2 = new byte[(ENum * 2) + 14];
        buffer2[0] = (byte) ((ENum * 2) + 13);
        buffer2[1] = ComAddr;
        buffer2[2] = -62;
        buffer2[3] = ENum;
        if (ENum > 0 && ENum < 16) {
            System.arraycopy(EPC, 0, buffer2, 4, ENum * 2);
        }
        System.arraycopy(RegAddr, 0, buffer2, (ENum * 2) + 4, 2);
        System.arraycopy(RegData, 0, buffer2, (ENum * 2) + 6, 2);
        System.arraycopy(Password, 0, buffer2, (ENum * 2) + 8, 4);
        getCRC(buffer2, buffer2[0] - 1);
        SendCMD(buffer2);
        int result2 = GetCMDData(this.recvBuff, this.recvLength, 194, 1500);
        if (result2 == 0) {
            Errorcode[0] = 0;
            byte[] bArr2 = this.recvBuff;
            if ((bArr2[3] & 255) == 252) {
                Errorcode[0] = bArr2[4];
            }
            return bArr2[3] & 255;
        }
        return 48;
    }

    public int Fd_ReadMemory(byte ComAddr, byte ENum, byte[] EPC, byte[] StartAddr, byte ReadLen, byte[] Password, byte AuthType, byte[] AuthPwd, byte MaskMem, byte[] MaskAdr, byte MaskLen, byte[] MaskData, byte[] Data, byte[] Errorcode) {
        int maskbyte;
        if ((ENum & 255) >= 0 && (ENum & 255) < 16) {
            byte[] buffer = new byte[(ENum * 2) + 18];
            buffer[0] = (byte) ((ENum * 2) + 17);
            buffer[1] = ComAddr;
            buffer[2] = -61;
            buffer[3] = ENum;
            if (ENum > 0 && ENum < 16) {
                System.arraycopy(EPC, 0, buffer, 4, ENum * 2);
            }
            System.arraycopy(StartAddr, 0, buffer, (ENum * 2) + 4, 2);
            buffer[(ENum * 2) + 6] = ReadLen;
            System.arraycopy(Password, 0, buffer, (ENum * 2) + 7, 4);
            buffer[(ENum * 2) + 11] = AuthType;
            System.arraycopy(AuthPwd, 0, buffer, (ENum * 2) + 12, 4);
            getCRC(buffer, buffer[0] - 1);
            SendCMD(buffer);
            int result = GetCMDData(this.recvBuff, this.recvLength, 195, 1500);
            if (result == 0) {
                byte[] bArr = this.recvBuff;
                if (bArr[3] == 0) {
                    Errorcode[0] = 0;
                    System.arraycopy(bArr, 4, Data, 0, ReadLen & 255);
                } else if ((bArr[3] & 255) == 252) {
                    Errorcode[0] = bArr[4];
                }
                return this.recvBuff[3] & 255;
            }
            return 48;
        }
        if ((ENum & 255) != 255 || MaskLen == 0) {
            return 255;
        }
        int mLen = MaskLen & 255;
        if (mLen % 8 == 0) {
            maskbyte = mLen / 8;
        } else {
            maskbyte = (mLen / 8) + 1;
        }
        byte[] buffer2 = new byte[maskbyte + 22];
        buffer2[0] = (byte) (maskbyte + 21);
        buffer2[1] = ComAddr;
        buffer2[2] = -61;
        buffer2[3] = ENum;
        System.arraycopy(StartAddr, 0, buffer2, 4, 2);
        buffer2[6] = ReadLen;
        System.arraycopy(Password, 0, buffer2, 7, 4);
        buffer2[11] = AuthType;
        System.arraycopy(AuthPwd, 0, buffer2, 12, 4);
        buffer2[16] = MaskMem;
        buffer2[17] = MaskAdr[0];
        buffer2[18] = MaskAdr[1];
        buffer2[19] = MaskLen;
        System.arraycopy(MaskData, 0, buffer2, 20, maskbyte);
        getCRC(buffer2, buffer2[0] - 1);
        SendCMD(buffer2);
        int result2 = GetCMDData(this.recvBuff, this.recvLength, 195, 1500);
        if (result2 != 0) {
            return 48;
        }
        byte[] bArr2 = this.recvBuff;
        if (bArr2[3] == 0) {
            Errorcode[0] = 0;
            System.arraycopy(bArr2, 4, Data, 0, ReadLen & 255);
        } else if ((bArr2[3] & 255) == 252) {
            Errorcode[0] = bArr2[4];
        }
        return this.recvBuff[3] & 255;
    }

    public int Fd_WriteMemory(byte ComAddr, byte ENum, byte[] EPC, byte[] StartAddr, int WriteLen, byte[] WriteData, byte[] Password, byte AuthType, byte[] AuthPwd, byte MaskMem, byte[] MaskAdr, byte MaskLen, byte[] MaskData, byte[] Errorcode) {
        int maskbyte;
        if ((ENum & 255) >= 0 && (ENum & 255) < 16) {
            byte[] buffer = new byte[(ENum * 2) + 18 + WriteLen];
            buffer[0] = (byte) ((ENum * 2) + 17 + WriteLen);
            buffer[1] = ComAddr;
            buffer[2] = -60;
            buffer[3] = (byte) WriteLen;
            buffer[4] = ENum;
            if (ENum > 0 && ENum < 16) {
                System.arraycopy(EPC, 0, buffer, 5, ENum * 2);
            }
            System.arraycopy(StartAddr, 0, buffer, (ENum * 2) + 5, 2);
            System.arraycopy(WriteData, 0, buffer, (ENum * 2) + 7, WriteLen);
            System.arraycopy(Password, 0, buffer, (ENum * 2) + 7 + WriteLen, 4);
            buffer[(ENum * 2) + 11] = AuthType;
            System.arraycopy(AuthPwd, 0, buffer, (ENum * 2) + 12, 4);
            getCRC(buffer, buffer[0] - 1);
            SendCMD(buffer);
            int result = GetCMDData(this.recvBuff, this.recvLength, 196, 1500);
            if (result == 0) {
                Errorcode[0] = 0;
                byte[] bArr = this.recvBuff;
                if ((bArr[3] & 255) == 252) {
                    Errorcode[0] = bArr[4];
                }
                return bArr[3] & 255;
            }
            return 48;
        }
        if ((ENum & 255) != 255 || MaskLen == 0) {
            return 255;
        }
        int mLen = MaskLen & 255;
        if (mLen % 8 == 0) {
            maskbyte = mLen / 8;
        } else {
            maskbyte = (mLen / 8) + 1;
        }
        byte[] buffer2 = new byte[maskbyte + 22 + WriteLen];
        buffer2[0] = (byte) (maskbyte + 21 + WriteLen);
        buffer2[1] = ComAddr;
        buffer2[2] = -60;
        buffer2[3] = (byte) WriteLen;
        buffer2[4] = ENum;
        System.arraycopy(StartAddr, 0, buffer2, 5, 2);
        System.arraycopy(WriteData, 0, buffer2, 7, WriteLen);
        System.arraycopy(Password, 0, buffer2, WriteLen + 7, 4);
        buffer2[WriteLen + 11] = AuthType;
        System.arraycopy(AuthPwd, 0, buffer2, WriteLen + 12, 4);
        buffer2[WriteLen + 16] = MaskMem;
        buffer2[WriteLen + 17] = MaskAdr[0];
        buffer2[WriteLen + 18] = MaskAdr[1];
        buffer2[WriteLen + 19] = MaskLen;
        System.arraycopy(MaskData, 0, buffer2, WriteLen + 20, maskbyte);
        getCRC(buffer2, buffer2[0] - 1);
        SendCMD(buffer2);
        int result2 = GetCMDData(this.recvBuff, this.recvLength, 196, 1500);
        if (result2 == 0) {
            Errorcode[0] = 0;
            byte[] bArr2 = this.recvBuff;
            if ((bArr2[3] & 255) == 252) {
                Errorcode[0] = bArr2[4];
            }
            return bArr2[3] & 255;
        }
        return 48;
    }

    public int Fd_GetTemperature(byte ComAddr, byte ENum, byte[] EPC, byte MeaType, byte ResultSel, byte FieldChkEn, byte EPStorageEn, byte UserBlockAddr, byte[] Password, byte MaskMem, byte[] MaskAdr, byte MaskLen, byte[] MaskData, byte[] Temp, byte[] Errorcode) {
        int maskbyte;
        byte b;
        byte b2;
        if ((ENum & 255) < 0 || (ENum & 255) >= 16) {
            if ((ENum & 255) != 255 || MaskLen == 0) {
                return 255;
            }
            int mLen = MaskLen & 255;
            if (mLen % 8 == 0) {
                maskbyte = mLen / 8;
            } else {
                maskbyte = (mLen / 8) + 1;
            }
            byte[] buffer = new byte[maskbyte + 19];
            buffer[0] = (byte) (maskbyte + 18);
            buffer[1] = ComAddr;
            buffer[2] = -59;
            buffer[3] = ENum;
            buffer[4] = MeaType;
            buffer[5] = ResultSel;
            buffer[6] = FieldChkEn;
            buffer[7] = EPStorageEn;
            buffer[8] = UserBlockAddr;
            System.arraycopy(Password, 0, buffer, 9, 4);
            buffer[13] = MaskMem;
            buffer[14] = MaskAdr[0];
            buffer[15] = MaskAdr[1];
            buffer[16] = MaskLen;
            System.arraycopy(MaskData, 0, buffer, 17, maskbyte);
            getCRC(buffer, buffer[0] - 1);
            SendCMD(buffer);
            int result = GetCMDData(this.recvBuff, this.recvLength, 197, 1500);
            if (result == 0) {
                Errorcode[0] = 0;
                byte[] bArr = this.recvBuff;
                if (bArr[3] == 0) {
                    System.arraycopy(bArr, 4, Temp, 0, 2);
                    b = 255;
                } else {
                    b = 255;
                    if ((bArr[3] & 255) == 252) {
                        Errorcode[0] = bArr[4];
                    }
                }
                return this.recvBuff[3] & b;
            }
            return 48;
        }
        byte[] buffer2 = new byte[(ENum * 2) + 15];
        buffer2[0] = (byte) ((ENum * 2) + 14);
        buffer2[1] = ComAddr;
        buffer2[2] = -59;
        buffer2[3] = ENum;
        if (ENum > 0 && ENum < 16) {
            System.arraycopy(EPC, 0, buffer2, 4, ENum * 2);
        }
        buffer2[(ENum * 2) + 4] = MeaType;
        buffer2[(ENum * 2) + 5] = ResultSel;
        buffer2[(ENum * 2) + 6] = FieldChkEn;
        buffer2[(ENum * 2) + 7] = EPStorageEn;
        buffer2[(ENum * 2) + 8] = UserBlockAddr;
        System.arraycopy(Password, 0, buffer2, (ENum * 2) + 9, 4);
        getCRC(buffer2, buffer2[0] - 1);
        SendCMD(buffer2);
        int result2 = GetCMDData(this.recvBuff, this.recvLength, 197, 1500);
        if (result2 == 0) {
            Errorcode[0] = 0;
            byte[] bArr2 = this.recvBuff;
            if (bArr2[3] == 0) {
                System.arraycopy(bArr2, 4, Temp, 0, 2);
                b2 = 255;
            } else {
                b2 = 255;
                if ((bArr2[3] & 255) == 252) {
                    Errorcode[0] = bArr2[4];
                }
            }
            return this.recvBuff[3] & b2;
        }
        return 48;
    }

    public int Fd_StartLogging(byte ComAddr, byte ENum, byte[] EPC, byte[] StartDelay, byte[] VdetStep, byte[] Password, byte MaskMem, byte[] MaskAdr, byte MaskLen, byte[] MaskData, byte[] Errorcode) {
        int maskbyte;
        Errorcode[0] = 0;
        if ((ENum & 255) < 0 || (ENum & 255) >= 16) {
            if ((ENum & 255) != 255 || MaskLen == 0) {
                return 255;
            }
            int mLen = MaskLen & 255;
            if (mLen % 8 == 0) {
                maskbyte = mLen / 8;
            } else {
                maskbyte = (mLen / 8) + 1;
            }
            byte[] buffer = new byte[maskbyte + 18];
            buffer[0] = (byte) (maskbyte + 17);
            buffer[1] = ComAddr;
            buffer[2] = -58;
            buffer[3] = ENum;
            System.arraycopy(StartDelay, 0, buffer, 4, 2);
            System.arraycopy(VdetStep, 0, buffer, 6, 2);
            System.arraycopy(Password, 0, buffer, 8, 4);
            buffer[12] = MaskMem;
            buffer[13] = MaskAdr[0];
            buffer[14] = MaskAdr[1];
            buffer[15] = MaskLen;
            System.arraycopy(MaskData, 0, buffer, 16, maskbyte);
            getCRC(buffer, buffer[0] - 1);
            SendCMD(buffer);
            int result = GetCMDData(this.recvBuff, this.recvLength, 198, 1500);
            if (result == 0) {
                Errorcode[0] = 0;
                byte[] bArr = this.recvBuff;
                if ((bArr[3] & 255) == 252) {
                    Errorcode[0] = bArr[4];
                }
                return bArr[3] & 255;
            }
            return 48;
        }
        byte[] buffer2 = new byte[(ENum * 2) + 14];
        buffer2[0] = (byte) ((ENum * 2) + 13);
        buffer2[1] = ComAddr;
        buffer2[2] = -58;
        buffer2[3] = ENum;
        if (ENum > 0 && ENum < 16) {
            System.arraycopy(EPC, 0, buffer2, 4, ENum * 2);
        }
        System.arraycopy(StartDelay, 0, buffer2, (ENum * 2) + 4, 2);
        System.arraycopy(VdetStep, 0, buffer2, (ENum * 2) + 6, 2);
        System.arraycopy(Password, 0, buffer2, (ENum * 2) + 8, 4);
        getCRC(buffer2, buffer2[0] - 1);
        SendCMD(buffer2);
        int result2 = GetCMDData(this.recvBuff, this.recvLength, 198, 1500);
        if (result2 == 0) {
            Errorcode[0] = 0;
            byte[] bArr2 = this.recvBuff;
            if ((bArr2[3] & 255) == 252) {
                Errorcode[0] = bArr2[4];
            }
            return bArr2[3] & 255;
        }
        return 48;
    }

    public int Fd_StopLogging(byte ComAddr, byte ENum, byte[] EPC, byte[] Password, byte[] StopPwd, byte MaskMem, byte[] MaskAdr, byte MaskLen, byte[] MaskData, byte[] Errorcode) {
        int maskbyte;
        Errorcode[0] = 0;
        if ((ENum & 255) >= 0 && (ENum & 255) < 16) {
            byte[] buffer = new byte[(ENum * 2) + 14];
            buffer[0] = (byte) ((ENum * 2) + 13);
            buffer[1] = ComAddr;
            buffer[2] = -57;
            buffer[3] = ENum;
            if (ENum > 0 && ENum < 16) {
                System.arraycopy(EPC, 0, buffer, 4, ENum * 2);
            }
            System.arraycopy(Password, 0, buffer, (ENum * 2) + 4, 4);
            System.arraycopy(StopPwd, 0, buffer, (ENum * 2) + 8, 4);
            getCRC(buffer, buffer[0] - 1);
            SendCMD(buffer);
            int result = GetCMDData(this.recvBuff, this.recvLength, 199, 1500);
            if (result == 0) {
                Errorcode[0] = 0;
                byte[] bArr = this.recvBuff;
                if ((bArr[3] & 255) == 252) {
                    Errorcode[0] = bArr[4];
                }
                return bArr[3] & 255;
            }
            return 48;
        }
        if ((ENum & 255) != 255 || MaskLen == 0) {
            return 255;
        }
        int mLen = MaskLen & 255;
        if (mLen % 8 == 0) {
            maskbyte = mLen / 8;
        } else {
            maskbyte = (mLen / 8) + 1;
        }
        byte[] buffer2 = new byte[maskbyte + 18];
        buffer2[0] = (byte) (maskbyte + 17);
        buffer2[1] = ComAddr;
        buffer2[2] = -57;
        buffer2[3] = ENum;
        System.arraycopy(Password, 0, buffer2, 4, 4);
        System.arraycopy(Password, 0, buffer2, 8, 4);
        buffer2[12] = MaskMem;
        buffer2[13] = MaskAdr[0];
        buffer2[14] = MaskAdr[1];
        buffer2[15] = MaskLen;
        System.arraycopy(MaskData, 0, buffer2, 16, maskbyte);
        getCRC(buffer2, buffer2[0] - 1);
        SendCMD(buffer2);
        int result2 = GetCMDData(this.recvBuff, this.recvLength, 199, 1500);
        if (result2 == 0) {
            Errorcode[0] = 0;
            byte[] bArr2 = this.recvBuff;
            if ((bArr2[3] & 255) == 252) {
                Errorcode[0] = bArr2[4];
            }
            return bArr2[3] & 255;
        }
        return 48;
    }

    public int Fd_OP_Mode_Chk(byte ComAddr, byte ENum, byte[] EPC, byte RefreshCfg, byte[] Password, byte MaskMem, byte[] MaskAdr, byte MaskLen, byte[] MaskData, byte[] ModeStatus, byte[] Errorcode) {
        int maskbyte;
        Errorcode[0] = 0;
        if ((ENum & 255) < 0 || (ENum & 255) >= 16) {
            if ((ENum & 255) != 255 || MaskLen == 0) {
                return 255;
            }
            int mLen = MaskLen & 255;
            if (mLen % 8 == 0) {
                maskbyte = mLen / 8;
            } else {
                maskbyte = (mLen / 8) + 1;
            }
            byte[] buffer = new byte[maskbyte + 15];
            buffer[0] = (byte) (maskbyte + 14);
            buffer[1] = ComAddr;
            buffer[2] = -56;
            buffer[3] = ENum;
            buffer[4] = RefreshCfg;
            System.arraycopy(Password, 0, buffer, 5, 4);
            buffer[9] = MaskMem;
            buffer[10] = MaskAdr[0];
            buffer[11] = MaskAdr[1];
            buffer[12] = MaskLen;
            System.arraycopy(MaskData, 0, buffer, 13, maskbyte);
            getCRC(buffer, buffer[0] - 1);
            SendCMD(buffer);
            int result = GetCMDData(this.recvBuff, this.recvLength, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, 1500);
            if (result == 0) {
                Errorcode[0] = 0;
                byte[] bArr = this.recvBuff;
                if ((bArr[3] & 255) == 0) {
                    ModeStatus[0] = bArr[4];
                    ModeStatus[1] = bArr[5];
                } else if ((bArr[3] & 255) == 252) {
                    Errorcode[0] = bArr[4];
                }
                return bArr[3] & 255;
            }
            return 48;
        }
        byte[] buffer2 = new byte[(ENum * 2) + 11];
        buffer2[0] = (byte) ((ENum * 2) + 10);
        buffer2[1] = ComAddr;
        buffer2[2] = -56;
        buffer2[3] = ENum;
        if (ENum > 0 && ENum < 16) {
            System.arraycopy(EPC, 0, buffer2, 4, ENum * 2);
        }
        buffer2[(ENum * 2) + 4] = RefreshCfg;
        System.arraycopy(Password, 0, buffer2, (ENum * 2) + 5, 4);
        getCRC(buffer2, buffer2[0] - 1);
        SendCMD(buffer2);
        int result2 = GetCMDData(this.recvBuff, this.recvLength, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, 1500);
        if (result2 == 0) {
            Errorcode[0] = 0;
            byte[] bArr2 = this.recvBuff;
            if ((bArr2[3] & 255) == 0) {
                ModeStatus[0] = bArr2[4];
                ModeStatus[1] = bArr2[5];
            } else if ((bArr2[3] & 255) == 252) {
                Errorcode[0] = bArr2[4];
            }
            return bArr2[3] & 255;
        }
        return 48;
    }

    /* JADX WARN: Code restructure failed: missing block: B:56:0x00fe, code lost:
    
        return r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:73:0x00eb, code lost:
    
        if (r22[0] <= 0) goto L60;
     */
    /* JADX WARN: Code restructure failed: missing block: B:74:0x00ed, code lost:
    
        return 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:75:0x00ee, code lost:
    
        return 1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    int GetMemDataFromPort(byte r20, byte[] r21, int[] r22, int r23) {
        /*
            Method dump skipped, instructions count: 428
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.rfid.trans.BaseReader.GetMemDataFromPort(byte, byte[], int[], int):int");
    }

    public int Fd_ExtReadMemory(byte ComAddr, byte ENum, byte[] EPC, byte[] StartAddr, byte[] ReadLen, byte[] Password, byte AuthType, byte[] AuthPwd, byte MaskMem, byte[] MaskAdr, byte MaskLen, byte[] MaskData, byte[] Data, int[] dataLen, byte[] Errorcode) {
        int maskbyte;
        int i;
        this.packIndex = -1;
        if ((ENum & 255) >= 0 && (ENum & 255) < 16) {
            byte[] buffer = new byte[(ENum * 2) + 19];
            buffer[0] = (byte) ((ENum * 2) + 18);
            buffer[1] = ComAddr;
            buffer[2] = -53;
            buffer[3] = ENum;
            if (ENum <= 0 || ENum >= 16) {
                i = 4;
            } else {
                i = 4;
                System.arraycopy(EPC, 0, buffer, 4, ENum * 2);
            }
            System.arraycopy(StartAddr, 0, buffer, (ENum * 2) + i, 2);
            System.arraycopy(ReadLen, 0, buffer, (ENum * 2) + 6, 2);
            System.arraycopy(Password, 0, buffer, (ENum * 2) + 8, i);
            buffer[(ENum * 2) + 12] = AuthType;
            System.arraycopy(AuthPwd, 0, buffer, (ENum * 2) + 13, i);
            getCRC(buffer, buffer[0] - 1);
            SendCMD(buffer);
            return GetMemDataFromPort(ComAddr, Data, dataLen, 203);
        }
        if ((ENum & 255) != 255 || MaskLen == 0) {
            return 255;
        }
        int mLen = MaskLen & 255;
        if (mLen % 8 == 0) {
            maskbyte = mLen / 8;
        } else {
            maskbyte = (mLen / 8) + 1;
        }
        byte[] buffer2 = new byte[maskbyte + 23];
        buffer2[0] = (byte) (maskbyte + 22);
        buffer2[1] = ComAddr;
        buffer2[2] = -53;
        buffer2[3] = ENum;
        System.arraycopy(StartAddr, 0, buffer2, 4, 2);
        System.arraycopy(ReadLen, 0, buffer2, 6, 2);
        System.arraycopy(Password, 0, buffer2, 8, 4);
        buffer2[12] = AuthType;
        System.arraycopy(AuthPwd, 0, buffer2, 13, 4);
        buffer2[17] = MaskMem;
        buffer2[18] = MaskAdr[0];
        buffer2[19] = MaskAdr[1];
        buffer2[20] = MaskLen;
        System.arraycopy(MaskData, 0, buffer2, 21, maskbyte);
        getCRC(buffer2, buffer2[0] - 1);
        SendCMD(buffer2);
        return GetMemDataFromPort(ComAddr, Data, dataLen, 203);
    }

    public int ReadData_GJB(byte ComAddr, byte TNum, byte[] TagID, byte ReadMem, byte[] WordPtr, byte WordNum, byte[] Readword, byte[] Data, byte[] Errorcode) {
        byte TNum2 = TNum;
        byte[] buffer = new byte[(TNum2 * 2) + 14];
        buffer[0] = (byte) ((TNum2 * 2) + 13);
        buffer[1] = ComAddr;
        buffer[2] = 88;
        buffer[3] = TNum2;
        if ((TNum2 & 255) == 255) {
            TNum2 = 0;
        }
        if (TNum2 > 0) {
            System.arraycopy(TagID, 0, buffer, 4, TNum2 * 2);
        }
        buffer[(TNum2 * 2) + 4] = ReadMem;
        buffer[(TNum2 * 2) + 5] = WordPtr[0];
        buffer[(TNum2 * 2) + 6] = WordPtr[1];
        buffer[(TNum2 * 2) + 7] = WordNum;
        System.arraycopy(Readword, 0, buffer, (TNum2 * 2) + 8, 4);
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 88, PathInterpolatorCompat.MAX_NUM_POINTS);
        if (result != 0) {
            return 48;
        }
        byte[] bArr = this.recvBuff;
        if (bArr[3] != 0) {
            if ((bArr[3] & 255) == 252) {
                Errorcode[0] = bArr[4];
            }
        } else {
            Errorcode[0] = 0;
            System.arraycopy(bArr, 4, Data, 0, WordNum * 2);
        }
        return this.recvBuff[3] & 255;
    }

    public int WriteData_GJB(byte ComAddr, byte WNum, byte TNum, byte[] TagID, byte WMem, byte[] WordPtr, byte[] Writedata, byte[] WPwd, byte[] Errorcode) {
        byte TNum2 = TNum;
        byte[] buffer = new byte[((TNum2 + WNum) * 2) + 14];
        buffer[0] = (byte) (((TNum2 + WNum) * 2) + 13);
        buffer[1] = ComAddr;
        buffer[2] = 89;
        buffer[3] = WNum;
        buffer[4] = TNum2;
        if ((TNum2 & 255) == 255) {
            TNum2 = 0;
        }
        if (TNum2 > 0) {
            System.arraycopy(TagID, 0, buffer, 5, TNum2 * 2);
        }
        buffer[(TNum2 * 2) + 5] = WMem;
        buffer[(TNum2 * 2) + 6] = WordPtr[0];
        buffer[(TNum2 * 2) + 7] = WordPtr[1];
        System.arraycopy(Writedata, 0, buffer, (TNum2 * 2) + 8, WNum * 2);
        System.arraycopy(WPwd, 0, buffer, (TNum2 * 2) + (WNum * 2) + 9, 4);
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 89, PathInterpolatorCompat.MAX_NUM_POINTS);
        if (result == 0) {
            byte[] bArr = this.recvBuff;
            if (bArr[3] == 0) {
                Errorcode[0] = 0;
            } else if ((bArr[3] & 255) == 252) {
                Errorcode[0] = bArr[4];
            }
            return bArr[3] & 255;
        }
        return 48;
    }

    public int Lock_GJB(byte ComAddr, byte TNum, byte[] TagID, byte LocMem, byte Cfg, byte Action, byte[] LockPwd, byte[] Errorcode) {
        byte TNum2 = TNum;
        byte[] buffer = new byte[(TNum2 * 2) + 13];
        buffer[0] = (byte) ((TNum2 * 2) + 12);
        buffer[1] = ComAddr;
        buffer[2] = 91;
        buffer[3] = TNum2;
        if ((TNum2 & 255) == 255) {
            TNum2 = 0;
        }
        if (TNum2 > 0) {
            System.arraycopy(TagID, 0, buffer, 4, TNum2 * 2);
        }
        buffer[(TNum2 * 2) + 4] = LocMem;
        buffer[(TNum2 * 2) + 5] = Cfg;
        buffer[(TNum2 * 2) + 6] = Action;
        System.arraycopy(LockPwd, 0, buffer, (TNum2 * 2) + 7, 4);
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 91, 1000);
        if (result == 0) {
            byte[] bArr = this.recvBuff;
            if (bArr[3] == 0) {
                Errorcode[0] = 0;
            } else if ((bArr[3] & 255) == 252) {
                Errorcode[0] = bArr[4];
            }
            return bArr[3] & 255;
        }
        return 48;
    }

    public int Kill_GJB(byte ComAddr, byte TNum, byte[] TagID, byte[] KillPwd, byte[] Errorcode) {
        byte[] buffer = new byte[(TNum * 2) + 10];
        buffer[0] = (byte) ((TNum * 2) + 9);
        buffer[1] = ComAddr;
        buffer[2] = 92;
        buffer[3] = TNum;
        if ((TNum & 255) == 255) {
            TNum = 0;
        }
        if (TNum > 0) {
            System.arraycopy(TagID, 0, buffer, 4, TNum * 2);
        }
        System.arraycopy(KillPwd, 0, buffer, (TNum * 2) + 4, 4);
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 92, 1000);
        if (result == 0) {
            byte[] bArr = this.recvBuff;
            if (bArr[3] == 0) {
                Errorcode[0] = 0;
            } else if ((bArr[3] & 255) == 252) {
                Errorcode[0] = bArr[4];
            }
            return bArr[3] & 255;
        }
        return 48;
    }

    public int EraseData_GJB(byte ComAddr, byte TNum, byte[] TagID, byte EMem, byte[] WordPtr, byte[] ELen, byte[] WPwd, byte[] Errorcode) {
        byte TNum2 = TNum;
        byte[] buffer = new byte[(TNum2 * 2) + 15];
        buffer[0] = (byte) ((TNum2 * 2) + 14);
        buffer[1] = ComAddr;
        buffer[2] = Message.HEAD;
        buffer[3] = TNum2;
        if ((TNum2 & 255) == 255) {
            TNum2 = 0;
        }
        if (TNum2 > 0) {
            System.arraycopy(TagID, 0, buffer, 4, TNum2 * 2);
        }
        buffer[(TNum2 * 2) + 4] = EMem;
        buffer[(TNum2 * 2) + 5] = WordPtr[0];
        buffer[(TNum2 * 2) + 6] = WordPtr[1];
        buffer[(TNum2 * 2) + 7] = ELen[0];
        buffer[(TNum2 * 2) + 8] = ELen[1];
        System.arraycopy(WPwd, 0, buffer, (TNum2 * 2) + 9, 4);
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 90, PathInterpolatorCompat.MAX_NUM_POINTS);
        if (result == 0) {
            byte[] bArr = this.recvBuff;
            if (bArr[3] == 0) {
                Errorcode[0] = 0;
            } else if ((bArr[3] & 255) == 252) {
                Errorcode[0] = bArr[4];
            }
            return bArr[3] & 255;
        }
        return 48;
    }

    public int ReadData_GB(byte ComAddr, byte TNum, byte[] TagID, byte ReadMem, byte[] WordPtr, byte WordNum, byte[] Readword, byte[] Data, byte[] Errorcode) {
        byte TNum2 = TNum;
        byte[] buffer = new byte[(TNum2 * 2) + 14];
        buffer[0] = (byte) ((TNum2 * 2) + 13);
        buffer[1] = ComAddr;
        buffer[2] = 88;
        buffer[3] = TNum2;
        if ((TNum2 & 255) == 255) {
            TNum2 = 0;
        }
        if (TNum2 > 0) {
            System.arraycopy(TagID, 0, buffer, 4, TNum2 * 2);
        }
        buffer[(TNum2 * 2) + 4] = ReadMem;
        buffer[(TNum2 * 2) + 5] = WordPtr[0];
        buffer[(TNum2 * 2) + 6] = WordPtr[1];
        buffer[(TNum2 * 2) + 7] = WordNum;
        System.arraycopy(Readword, 0, buffer, (TNum2 * 2) + 8, 4);
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 88, 2000);
        if (result != 0) {
            return 48;
        }
        byte[] bArr = this.recvBuff;
        if (bArr[3] != 0) {
            if ((bArr[3] & 255) == 252) {
                Errorcode[0] = bArr[4];
            }
        } else {
            Errorcode[0] = 0;
            System.arraycopy(bArr, 4, Data, 0, WordNum * 2);
        }
        return this.recvBuff[3] & 255;
    }

    public int WriteData_GB(byte ComAddr, byte WNum, byte TNum, byte[] TagID, byte WMem, byte[] WordPtr, byte[] Writedata, byte[] WPwd, byte[] Errorcode) {
        byte TNum2 = TNum;
        byte[] buffer = new byte[((TNum2 + WNum) * 2) + 14];
        buffer[0] = (byte) (((TNum2 + WNum) * 2) + 13);
        buffer[1] = ComAddr;
        buffer[2] = 89;
        buffer[3] = WNum;
        buffer[4] = TNum2;
        if ((TNum2 & 255) == 255) {
            TNum2 = 0;
        }
        if (TNum2 > 0) {
            System.arraycopy(TagID, 0, buffer, 5, TNum2 * 2);
        }
        buffer[(TNum2 * 2) + 5] = WMem;
        buffer[(TNum2 * 2) + 6] = WordPtr[0];
        buffer[(TNum2 * 2) + 7] = WordPtr[1];
        System.arraycopy(Writedata, 0, buffer, (TNum2 * 2) + 8, WNum * 2);
        System.arraycopy(WPwd, 0, buffer, (TNum2 * 2) + (WNum * 2) + 9, 4);
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 89, 2000);
        if (result == 0) {
            byte[] bArr = this.recvBuff;
            if (bArr[3] == 0) {
                Errorcode[0] = 0;
            } else if ((bArr[3] & 255) == 252) {
                Errorcode[0] = bArr[4];
            }
            return bArr[3] & 255;
        }
        return 48;
    }

    public int Lock_GB(byte ComAddr, byte TNum, byte[] TagID, byte LocMem, byte Cfg, byte Action, byte[] LockPwd, byte[] Errorcode) {
        byte TNum2 = TNum;
        byte[] buffer = new byte[(TNum2 * 2) + 13];
        buffer[0] = (byte) ((TNum2 * 2) + 12);
        buffer[1] = ComAddr;
        buffer[2] = 91;
        buffer[3] = TNum2;
        if ((TNum2 & 255) == 255) {
            TNum2 = 0;
        }
        if (TNum2 > 0) {
            System.arraycopy(TagID, 0, buffer, 4, TNum2 * 2);
        }
        buffer[(TNum2 * 2) + 4] = LocMem;
        buffer[(TNum2 * 2) + 5] = Cfg;
        buffer[(TNum2 * 2) + 6] = Action;
        System.arraycopy(LockPwd, 0, buffer, (TNum2 * 2) + 7, 4);
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 91, 1000);
        if (result == 0) {
            byte[] bArr = this.recvBuff;
            if (bArr[3] == 0) {
                Errorcode[0] = 0;
            } else if ((bArr[3] & 255) == 252) {
                Errorcode[0] = bArr[4];
            }
            return bArr[3] & 255;
        }
        return 48;
    }

    public int Kill_GB(byte ComAddr, byte TNum, byte[] TagID, byte[] KillPwd, byte[] Errorcode) {
        byte[] buffer = new byte[(TNum * 2) + 10];
        buffer[0] = (byte) ((TNum * 2) + 9);
        buffer[1] = ComAddr;
        buffer[2] = 92;
        buffer[3] = TNum;
        if ((TNum & 255) == 255) {
            TNum = 0;
        }
        if (TNum > 0) {
            System.arraycopy(TagID, 0, buffer, 4, TNum * 2);
        }
        System.arraycopy(KillPwd, 0, buffer, (TNum * 2) + 4, 4);
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 92, 1000);
        if (result == 0) {
            byte[] bArr = this.recvBuff;
            if (bArr[3] == 0) {
                Errorcode[0] = 0;
            } else if ((bArr[3] & 255) == 252) {
                Errorcode[0] = bArr[4];
            }
            return bArr[3] & 255;
        }
        return 48;
    }

    public int EraseData_GB(byte ComAddr, byte TNum, byte[] TagID, byte EMem, byte[] WordPtr, byte[] ELen, byte[] WPwd, byte[] Errorcode) {
        byte TNum2 = TNum;
        byte[] buffer = new byte[(TNum2 * 2) + 15];
        buffer[0] = (byte) ((TNum2 * 2) + 14);
        buffer[1] = ComAddr;
        buffer[2] = Message.HEAD;
        buffer[3] = TNum2;
        if ((TNum2 & 255) == 255) {
            TNum2 = 0;
        }
        if (TNum2 > 0) {
            System.arraycopy(TagID, 0, buffer, 4, TNum2 * 2);
        }
        buffer[(TNum2 * 2) + 4] = EMem;
        buffer[(TNum2 * 2) + 5] = WordPtr[0];
        buffer[(TNum2 * 2) + 6] = WordPtr[1];
        buffer[(TNum2 * 2) + 7] = ELen[0];
        buffer[(TNum2 * 2) + 8] = ELen[1];
        System.arraycopy(WPwd, 0, buffer, (TNum2 * 2) + 9, 4);
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 90, 2000);
        if (result == 0) {
            byte[] bArr = this.recvBuff;
            if (bArr[3] == 0) {
                Errorcode[0] = 0;
            } else if ((bArr[3] & 255) == 252) {
                Errorcode[0] = bArr[4];
            }
            return bArr[3] & 255;
        }
        return 48;
    }

    public int InventorySingle_6B(byte ComAddr, byte[] id) {
        byte[] buffer = {4, ComAddr, 80, 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 80, 1000);
        if (result == 0) {
            byte[] bArr = this.recvBuff;
            if (bArr[3] == 0) {
                System.arraycopy(bArr, 5, id, 0, 10);
            }
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    public int InventoryMutiple_6B(byte ComAddr, byte Condition, byte Address, byte Mask, byte[] Word_data, byte[] id, int[] number) {
        byte[] buffer = new byte[16];
        buffer[0] = 15;
        buffer[1] = ComAddr;
        buffer[2] = 81;
        buffer[3] = Condition;
        buffer[4] = Address;
        buffer[5] = Mask;
        System.arraycopy(Word_data, 0, buffer, 6, 8);
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 81, PathInterpolatorCompat.MAX_NUM_POINTS);
        if (result != 0) {
            return 48;
        }
        byte[] bArr = this.recvBuff;
        if (bArr[3] == 21 || bArr[3] == 22 || bArr[3] == 23 || bArr[3] == 24) {
            number[0] = bArr[5] & 255;
            System.arraycopy(bArr, 6, id, 0, number[0] * 10);
        }
        return this.recvBuff[3] & 255;
    }

    public int ReadData_6B(byte ComAddr, byte Address, byte[] id, byte num, byte[] data) {
        byte[] buffer = new byte[15];
        buffer[0] = 14;
        buffer[1] = ComAddr;
        buffer[2] = 82;
        buffer[3] = Address;
        System.arraycopy(id, 0, buffer, 4, 8);
        buffer[12] = num;
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 82, 2000);
        if (result == 0) {
            byte[] bArr = this.recvBuff;
            if (bArr[3] == 0) {
                System.arraycopy(bArr, 4, data, 0, num & 255);
            }
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    public int WriteData_6B(byte ComAddr, byte Address, byte[] id, byte num, byte[] data) {
        byte[] buffer = new byte[(num & 255) + 14];
        buffer[0] = (byte) ((num & 255) + 13);
        buffer[1] = ComAddr;
        buffer[2] = 83;
        buffer[3] = Address;
        System.arraycopy(id, 0, buffer, 4, 8);
        System.arraycopy(data, 0, buffer, 12, num);
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 83, 2000);
        if (result == 0) {
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    public int Lock_6B(byte ComAddr, byte Address, byte[] id) {
        byte[] buffer = new byte[14];
        buffer[0] = 13;
        buffer[1] = ComAddr;
        buffer[2] = 85;
        buffer[3] = Address;
        System.arraycopy(id, 0, buffer, 4, 8);
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 85, 1000);
        if (result == 0) {
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    public int CheckLock_6B(byte ComAddr, byte Address, byte[] id, byte[] LockState) {
        byte[] buffer = new byte[14];
        buffer[0] = 13;
        buffer[1] = ComAddr;
        buffer[2] = 84;
        buffer[3] = Address;
        System.arraycopy(id, 0, buffer, 4, 8);
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 84, 1000);
        if (result == 0) {
            byte[] bArr = this.recvBuff;
            if (bArr[3] == 0) {
                LockState[0] = bArr[4];
            }
            return bArr[3] & 255;
        }
        return 48;
    }

    public int RfOutput(byte ComAddr, byte OnOff) {
        byte[] buffer = {5, ComAddr, 48, OnOff, 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 48, 1000);
        if (result == 0) {
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    public int SelectCMD(byte ComAddr, byte ant, byte Session, byte Selaction, byte Truncate) {
        byte[] buffer = {12, ComAddr, -102, ant, Session, Selaction, 1, 0, 0, 0, Truncate, 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 154, 500);
        if (result == 0) {
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    public int SelectCMDByTime(byte ComAddr, byte ant, byte Session, byte Selaction, byte Truncate, byte time) {
        byte[] buffer = {13, ComAddr, -104, ant, Session, Selaction, 1, 0, 0, 0, Truncate, 0, 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, SyslogAppender.LOG_LOCAL3, 500);
        if (result == 0) {
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    public int SelectCMDByTimeNoACk(byte ComAddr, byte ant, byte Session, byte Selaction, byte Truncate, byte time) {
        byte[] buffer = {13, ComAddr, -104, ant, Session, Selaction, 1, 0, 0, 0, Truncate, 0, 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        return 0;
    }

    public int SetCfgParameter(byte ComAddr, byte opt, byte cfgNo, byte[] cfgData, int len) {
        byte[] buffer = new byte[len + 7];
        buffer[0] = (byte) (len + 6);
        buffer[1] = ComAddr;
        buffer[2] = -22;
        buffer[3] = opt;
        buffer[4] = cfgNo;
        if (len > 0) {
            System.arraycopy(cfgData, 0, buffer, 5, len);
        }
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 234, 1000);
        if (result == 0) {
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    public int GetCfgParameter(byte ComAddr, byte cfgNo, byte[] cfgData, int[] len) {
        byte[] buffer = {5, ComAddr, -21, cfgNo, 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 235, 1000);
        if (result == 0) {
            byte[] bArr = this.recvBuff;
            if (bArr[3] == 0) {
                len[0] = this.recvLength[0] - 6;
                System.arraycopy(bArr, 4, cfgData, 0, len[0]);
            } else {
                len[0] = 0;
            }
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    public int OperateControl(byte ComAddr, byte[] Control) {
        byte[] buffer = {5, ComAddr, ByteCompanionObject.MAX_VALUE, Control[0], 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, WorkQueueKt.MASK, 400);
        if (result == 0) {
            byte[] bArr = this.recvBuff;
            if (bArr[3] == 0) {
                Control[0] = bArr[4];
            }
            return bArr[3] & 255;
        }
        return 48;
    }

    public int SetCustomRegion(byte ComAddr, byte flags, int band, int FreSpace, int FreNum, int StartFre) {
        byte[] buffer = {11, ComAddr, 34, flags, (byte) band, (byte) FreSpace, (byte) FreNum, (byte) (StartFre >> 16), (byte) (StartFre >> 8), (byte) StartFre, 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 34, 500);
        if (result == 0) {
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    public int GetCustomRegion(byte ComAddr, int[] band, int[] FreSpace, int[] FreNum, int[] StartFre) {
        byte[] buffer = {4, ComAddr, -98, 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 158, 500);
        if (result == 0) {
            byte[] bArr = this.recvBuff;
            if (bArr[0] == 11) {
                band[0] = bArr[4] & 255;
                FreSpace[0] = bArr[5] & 255;
                FreNum[0] = bArr[6] & 255;
                StartFre[0] = ((bArr[7] & 255) << 16) + ((bArr[8] & 255) << 8) + (bArr[9] & 255);
            } else if (bArr[0] == 8) {
                band[0] = bArr[4] & 255;
                FreSpace[0] = bArr[5] & 255;
                FreNum[0] = bArr[6] & 255;
            }
            return bArr[3] & 255;
        }
        return 48;
    }

    public int GetModuleDescribe(byte ComAddr, byte[] Describe) {
        byte[] buffer = {5, ComAddr, -26, 2, 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 230, 500);
        if (result == 0) {
            byte[] bArr = this.recvBuff;
            if (bArr[3] == 0 && bArr[0] == 22) {
                System.arraycopy(bArr, 5, Describe, 0, 16);
            }
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    public int SetRegionTable(byte ComAddr, byte fccTable) {
        byte[] buffer = {6, ComAddr, -26, 4, fccTable, 0, 0};
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 230, 400);
        if (result == 0) {
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    public int SetInventoryMatchData(byte ComAddr, byte MatchType, List<MaskClass> MaskList) {
        byte[] buffer = new byte[7];
        byte[] temp = new byte[256];
        if (MaskList == null || (MaskList != null && MaskList.size() == 0)) {
            buffer[0] = 6;
            buffer[1] = ComAddr;
            buffer[2] = -26;
            buffer[3] = 7;
            buffer[4] = 0;
            getCRC(buffer, buffer[0] - 1);
            SendCMD(buffer);
            int result = GetCMDData(this.recvBuff, this.recvLength, 230, 500);
            if (result == 0) {
                return this.recvBuff[3] & 255;
            }
            return 48;
        }
        byte MactchCount = (byte) MaskList.size();
        int index = 0;
        for (int i = 0; i < MactchCount; i++) {
            MaskClass mask = MaskList.get(i);
            int index2 = index + 1;
            temp[index] = mask.MaskMem;
            int index3 = index2 + 1;
            temp[index2] = mask.MaskAdr[0];
            int index4 = index3 + 1;
            temp[index3] = mask.MaskAdr[1];
            int index5 = index4 + 1;
            temp[index4] = mask.MaskLen;
            int maskleng = ((mask.MaskLen & 255) + 7) / 8;
            System.arraycopy(mask.MaskData, 0, temp, index5, maskleng);
            index = index5 + maskleng;
        }
        byte[] buffer2 = new byte[index + 8];
        buffer2[0] = (byte) (index + 7);
        buffer2[1] = ComAddr;
        buffer2[2] = -26;
        buffer2[3] = 7;
        buffer2[4] = MactchCount;
        buffer2[5] = MatchType;
        System.arraycopy(temp, 0, buffer2, 6, index);
        getCRC(buffer2, buffer2[0] - 1);
        SendCMD(buffer2);
        int result2 = GetCMDData(this.recvBuff, this.recvLength, 230, 500);
        if (result2 == 0) {
            return this.recvBuff[3] & 255;
        }
        return 48;
    }

    public int Inventory_Temp_YH(byte ComAddr, byte ENum, byte[] EPC, byte SelTarget, byte SelAction, byte WaitTime, byte QValue, byte Session, byte Target, byte inAnt, byte Scantime, List<ReadTag> tagList) {
        if (ENum >= 0 && ENum <= 15) {
            byte[] buffer = new byte[(ENum * 2) + 14];
            buffer[0] = (byte) ((ENum * 2) + 13);
            buffer[1] = ComAddr;
            buffer[2] = 23;
            buffer[3] = ENum;
            if (ENum > 0 && ENum < 255) {
                System.arraycopy(EPC, 0, buffer, 4, ENum * 2);
            }
            buffer[(ENum * 2) + 4] = SelTarget;
            buffer[(ENum * 2) + 5] = SelAction;
            buffer[(ENum * 2) + 6] = WaitTime;
            buffer[(ENum * 2) + 7] = QValue;
            buffer[(ENum * 2) + 8] = Session;
            buffer[(ENum * 2) + 9] = Target;
            buffer[(ENum * 2) + 10] = inAnt;
            buffer[(ENum * 2) + 11] = Scantime;
            getCRC(buffer, buffer[0] - 1);
            SendCMD(buffer);
            return GetInventoryData_TEMP(ComAddr, 23, Scantime, tagList);
        }
        return 255;
    }

    private int GetInventoryData_TEMP(byte ComAddr, int cmd, int scanTime, List<ReadTag> tagList) {
        int scanTime2;
        int Count;
        int btLength;
        long beginTime;
        int scanTime3;
        byte ComAddr2;
        int Count2;
        int btLength2;
        long beginTime2;
        int index;
        long beginTime3;
        int epclen;
        int nLen;
        int num;
        int index2;
        int scanTime4 = scanTime == 0 ? 5000 : scanTime;
        byte[] btArray = new byte[2000];
        long beginTime4 = SystemClock.elapsedRealtime();
        int btLength3 = 0;
        byte ComAddr3 = ComAddr;
        while (true) {
            try {
                byte[] buffer = this.msg.Read();
                if (buffer != null) {
                    if (this.logswitch == 1) {
                        try {
                            Log.d("Recv", bytesToHexString(buffer, 0, buffer.length));
                            RFIDLogCallBack rFIDLogCallBack = this.msgCallback;
                            if (rFIDLogCallBack != null) {
                                rFIDLogCallBack.RecvMessageCallback(buffer);
                            }
                        } catch (Exception e) {
                            e = e;
                            e.toString();
                            return 48;
                        }
                    }
                    beginTime4 = SystemClock.elapsedRealtime();
                    try {
                        int Count3 = buffer.length;
                        if (Count3 == 0) {
                            scanTime2 = scanTime4;
                        } else {
                            try {
                                byte[] daw = new byte[Count3 + btLength3];
                                System.arraycopy(btArray, 0, daw, 0, btLength3);
                                System.arraycopy(buffer, 0, daw, btLength3, Count3);
                                int index3 = 0;
                                while (true) {
                                    if (daw.length - index3 <= 5) {
                                        scanTime2 = scanTime4;
                                        Count = Count3;
                                        btLength = btLength3;
                                        beginTime = beginTime4;
                                        break;
                                    }
                                    if ((ComAddr3 & 255) == 255) {
                                        ComAddr3 = 0;
                                    }
                                    try {
                                        if ((daw[index3] & 255) >= 5 && daw[index3 + 1] == ComAddr3) {
                                            if ((daw[index3 + 2] & 255) != cmd) {
                                                try {
                                                    if ((daw[index3 + 2] & 255) != 0) {
                                                    }
                                                } catch (Exception e2) {
                                                    e = e2;
                                                    e.toString();
                                                    return 48;
                                                }
                                            }
                                            try {
                                                int len = daw[index3] & 255;
                                                byte[] buffer2 = buffer;
                                                if (daw.length < index3 + len + 1) {
                                                    scanTime2 = scanTime4;
                                                    Count = Count3;
                                                    btLength = btLength3;
                                                    beginTime = beginTime4;
                                                    break;
                                                }
                                                byte[] epcArr = new byte[len + 1];
                                                System.arraycopy(daw, index3, epcArr, 0, epcArr.length);
                                                if (!CheckCRC(epcArr, epcArr.length)) {
                                                    scanTime3 = scanTime4;
                                                    ComAddr2 = ComAddr3;
                                                    Count2 = Count3;
                                                    btLength2 = btLength3;
                                                    beginTime2 = beginTime4;
                                                    index3++;
                                                } else {
                                                    if (epcArr[2] == 0) {
                                                        return 254;
                                                    }
                                                    int nLen2 = (epcArr[0] & 255) + 1;
                                                    int index4 = index3 + nLen2;
                                                    scanTime3 = scanTime4;
                                                    try {
                                                        int status = epcArr[3] & 255;
                                                        if (status != 1 && status != 2 && status != 3 && status != 4) {
                                                            return status;
                                                        }
                                                        ComAddr2 = ComAddr3;
                                                        int num2 = epcArr[5] & 255;
                                                        if (num2 > 0) {
                                                            int m = 6;
                                                            Count2 = Count3;
                                                            int Count4 = 0;
                                                            while (Count4 < num2) {
                                                                int btLength4 = btLength3;
                                                                try {
                                                                    int btLength5 = epcArr[m];
                                                                    beginTime3 = beginTime4;
                                                                    epclen = btLength5 & 255 & WorkQueueKt.MASK;
                                                                } catch (Exception e3) {
                                                                    e = e3;
                                                                }
                                                                try {
                                                                    int i = (epcArr[m] & 255) >> 7;
                                                                    ReadTag tag = new ReadTag();
                                                                    tag.antId = 1;
                                                                    if (epclen > 0) {
                                                                        byte[] btArr = new byte[epclen];
                                                                        nLen = nLen2;
                                                                        num = num2;
                                                                        index2 = index4;
                                                                        System.arraycopy(epcArr, m + 1, btArr, 0, btArr.length);
                                                                        tag.epcId = bytesToHexString(btArr, 0, btArr.length);
                                                                        tag.memId = null;
                                                                    } else {
                                                                        nLen = nLen2;
                                                                        num = num2;
                                                                        index2 = index4;
                                                                        tag.epcId = "";
                                                                        tag.memId = null;
                                                                    }
                                                                    tag.rssi = epcArr[m + 1 + epclen] & 255;
                                                                    tag.phase = 0;
                                                                    if (tagList != null) {
                                                                        tagList.add(tag);
                                                                    }
                                                                    m = m + 2 + epclen;
                                                                    Count4++;
                                                                    btLength3 = btLength4;
                                                                    beginTime4 = beginTime3;
                                                                    nLen2 = nLen;
                                                                    num2 = num;
                                                                    index4 = index2;
                                                                } catch (Exception e4) {
                                                                    e = e4;
                                                                    e.toString();
                                                                    return 48;
                                                                }
                                                            }
                                                            btLength2 = btLength3;
                                                            beginTime2 = beginTime4;
                                                            index = index4;
                                                        } else {
                                                            Count2 = Count3;
                                                            btLength2 = btLength3;
                                                            beginTime2 = beginTime4;
                                                            index = index4;
                                                        }
                                                        if (status == 1 || status == 2) {
                                                            return 0;
                                                        }
                                                        index3 = index;
                                                    } catch (Exception e5) {
                                                        e = e5;
                                                    }
                                                }
                                                Count3 = Count2;
                                                buffer = buffer2;
                                                scanTime4 = scanTime3;
                                                ComAddr3 = ComAddr2;
                                                btLength3 = btLength2;
                                                beginTime4 = beginTime2;
                                            } catch (Exception e6) {
                                                e = e6;
                                                e.toString();
                                                return 48;
                                            }
                                        }
                                        index3++;
                                        Count3 = Count3;
                                        buffer = buffer;
                                        scanTime4 = scanTime4;
                                        ComAddr3 = ComAddr3;
                                        btLength3 = btLength3;
                                        beginTime4 = beginTime4;
                                    } catch (Exception e7) {
                                        e = e7;
                                    }
                                }
                                try {
                                    if (daw.length > index3) {
                                        btLength3 = daw.length - index3;
                                        try {
                                            System.arraycopy(daw, index3, btArray, 0, btLength3);
                                        } catch (Exception e8) {
                                            e = e8;
                                            e.toString();
                                            return 48;
                                        }
                                    } else {
                                        btLength3 = 0;
                                    }
                                    beginTime4 = beginTime;
                                } catch (Exception e9) {
                                    e = e9;
                                }
                            } catch (Exception e10) {
                                e = e10;
                            }
                        }
                    } catch (Exception e11) {
                        e = e11;
                    }
                } else {
                    scanTime2 = scanTime4;
                    int btLength6 = btLength3;
                    try {
                        SystemClock.sleep(5L);
                        btLength3 = btLength6;
                    } catch (Exception e12) {
                        e = e12;
                        e.toString();
                        return 48;
                    }
                }
                try {
                    if (SystemClock.elapsedRealtime() - beginTime4 >= CoroutineLiveDataKt.DEFAULT_TIMEOUT) {
                        return 48;
                    }
                    scanTime4 = scanTime2;
                } catch (Exception e13) {
                    e = e13;
                    e.toString();
                    return 48;
                }
            } catch (Exception e14) {
                e = e14;
            }
        }
    }

    public int Inventory_temp_YL(byte address, byte ENum, byte[] EPC, byte[] Password, byte[] Data, byte[] Errorcode) {
        if (ENum < 0 || ENum >= 16) {
            return 255;
        }
        byte[] buffer = new byte[(ENum * 2) + 10];
        buffer[0] = (byte) ((ENum * 2) + 9);
        buffer[1] = address;
        buffer[2] = -51;
        buffer[3] = ENum;
        if (ENum > 0) {
            System.arraycopy(EPC, 0, buffer, 4, ENum * 2);
        }
        System.arraycopy(Password, 0, buffer, (ENum * 2) + 4, 4);
        getCRC(buffer, buffer[0] - 1);
        SendCMD(buffer);
        int result = GetCMDData(this.recvBuff, this.recvLength, 205, 1500);
        if (result != 0) {
            return 48;
        }
        byte[] bArr = this.recvBuff;
        if ((bArr[2] & 255) != 205) {
            return 238;
        }
        if (bArr[3] == 0) {
            System.arraycopy(bArr, 4, Data, 0, 2);
        }
        return 255 & this.recvBuff[3];
    }

    private int GetPageData(byte[] data, int[] Nlen) {
        byte[] buffer;
        byte[] bArr = new byte[2560];
        byte[] bArr2 = new byte[Priority.INFO_INT];
        long beginTime = System.currentTimeMillis();
        do {
            try {
                if (System.currentTimeMillis() - beginTime < 2000) {
                    SystemClock.sleep(20L);
                    buffer = this.msg.Read();
                } else {
                    return 48;
                }
            } catch (Exception e) {
                e.toString();
                return 48;
            }
        } while (buffer == null);
        if (buffer[0] == 6) {
            return 0;
        }
        return buffer[0] == 21 ? 1 : 1;
    }

    public int SendPage(byte[] page) {
        try {
            byte[] buffer = new byte[261];
            System.arraycopy(page, 0, buffer, 0, 261);
            this.msg.mOutStream.write(buffer);
            int result = GetPageData(this.recvBuff, this.recvLength);
            return result;
        } catch (Exception e) {
            return 48;
        }
    }
}
