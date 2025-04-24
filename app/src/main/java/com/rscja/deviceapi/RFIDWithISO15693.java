package com.rscja.deviceapi;

import android.util.Log;
import com.rscja.deviceapi.entity.ISO15693Entity;
import com.rscja.deviceapi.exception.ConfigurationException;
import com.rscja.deviceapi.exception.RFIDNotFoundException;
import com.rscja.deviceapi.exception.RFIDReadFailureException;
import com.rscja.utility.StringUtility;

/* loaded from: classes.dex */
public class RFIDWithISO15693 extends b {
    private static RFIDWithISO15693 a = null;

    @Override // com.rscja.deviceapi.b
    public /* bridge */ /* synthetic */ boolean free() {
        return super.free();
    }

    @Override // com.rscja.deviceapi.b
    public /* bridge */ /* synthetic */ String getVersion() {
        return super.getVersion();
    }

    @Override // com.rscja.deviceapi.b
    public /* bridge */ /* synthetic */ boolean init() {
        return super.init();
    }

    protected RFIDWithISO15693() throws ConfigurationException {
    }

    public static synchronized RFIDWithISO15693 getInstance() throws ConfigurationException {
        RFIDWithISO15693 rFIDWithISO15693;
        synchronized (RFIDWithISO15693.class) {
            if (a == null) {
                a = new RFIDWithISO15693();
            }
            rFIDWithISO15693 = a;
        }
        return rFIDWithISO15693;
    }

    public enum TagType {
        ICODE2(0),
        TI2048(4),
        STLRIS64K(8),
        NUll_(100);

        private final int a;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static TagType[] valuesCustom() {
            TagType[] valuesCustom = values();
            int length = valuesCustom.length;
            TagType[] tagTypeArr = new TagType[length];
            System.arraycopy(valuesCustom, 0, tagTypeArr, 0, length);
            return tagTypeArr;
        }

        public final int getValue() {
            return this.a;
        }

        TagType(int value) {
            this.a = value;
        }
    }

    public synchronized ISO15693Entity inventory() {
        char[] ISO15693_getSystemInformation;
        char[] ISO15693_inventory = getDeviceAPI().ISO15693_inventory(1, 0);
        if (ISO15693_inventory[0] != 0) {
            Log.e("RFIDWithISO15693", "inventory() err:" + Character.digit(ISO15693_inventory[0], 10));
            return null;
        }
        char[] cArr = new char[8];
        for (int i = 0; i < 8; i++) {
            cArr[i] = ISO15693_inventory[i + 4];
        }
        String str = "";
        if (ISO15693_inventory.length > 10) {
            char c = ISO15693_inventory[10];
            if (c == 2) {
                str = TagType.STLRIS64K.toString();
            } else if (c == 4) {
                str = TagType.ICODE2.toString();
            } else if (c == 7) {
                str = TagType.TI2048.toString();
            }
        }
        char[] cArr2 = new char[8];
        for (int i2 = 0; i2 < 8; i2++) {
            cArr2[i2] = cArr[7 - i2];
        }
        ISO15693Entity iSO15693Entity = new ISO15693Entity(StringUtility.chars2HexString(cArr2, 8), str, cArr);
        if (iSO15693Entity.getType().equals(TagType.STLRIS64K.toString())) {
            ISO15693_getSystemInformation = getDeviceAPI().ISO15693_getSystemInformation(8, cArr, 0);
        } else {
            ISO15693_getSystemInformation = getDeviceAPI().ISO15693_getSystemInformation(0, cArr, 0);
        }
        if (ISO15693_getSystemInformation[0] == 0 && ISO15693_getSystemInformation[1] > '\f') {
            iSO15693Entity.setAFI(StringUtility.char2HexString(ISO15693_getSystemInformation[12]));
            iSO15693Entity.setDESFID(StringUtility.char2HexString(ISO15693_getSystemInformation[11]));
        }
        return iSO15693Entity;
    }

    public synchronized ISO15693Entity read(int block) throws RFIDReadFailureException {
        char[] ISO15693_read_sm;
        ISO15693Entity inventory = inventory();
        if (inventory == null) {
            return null;
        }
        char[] originalUID = inventory.getOriginalUID();
        if (inventory.getType().equals(TagType.STLRIS64K.toString())) {
            ISO15693_read_sm = getDeviceAPI().ISO15693_read_sm(8, originalUID, originalUID.length, block, 1);
        } else {
            ISO15693_read_sm = getDeviceAPI().ISO15693_read_sm(0, originalUID, originalUID.length, block, 1);
        }
        if (ISO15693_read_sm[0] != 0) {
            Log.e("RFIDWithISO15693", "read() err:" + ISO15693_read_sm[0]);
            throw new RFIDReadFailureException();
        }
        char c = ISO15693_read_sm[1];
        char[] cArr = new char[c];
        for (int i = 0; i < c; i++) {
            cArr[i] = ISO15693_read_sm[i + 2];
        }
        inventory.setData(StringUtility.chars2HexString(cArr, c));
        return inventory;
    }

    public synchronized boolean write(int block, String hexData) throws RFIDNotFoundException {
        if (StringUtility.isEmpty(hexData)) {
            return false;
        }
        ISO15693Entity inventory = inventory();
        if (inventory == null) {
            throw new RFIDNotFoundException();
        }
        char[] hexString2Chars = StringUtility.hexString2Chars(hexData);
        char[] cArr = new char[4];
        if (hexString2Chars.length < 4) {
            int length = 4 - hexString2Chars.length;
            for (int i = 0; i < hexString2Chars.length; i++) {
                cArr[i + length] = hexString2Chars[i];
            }
        } else {
            for (int i2 = 0; i2 < 4; i2++) {
                cArr[i2] = hexString2Chars[i2];
            }
        }
        char[] originalUID = inventory.getOriginalUID();
        int i3 = -1;
        if (inventory.getType().equals(TagType.ICODE2.toString())) {
            i3 = getDeviceAPI().ISO15693_write_sm(0, originalUID, 0, block, 1, cArr, 4);
        } else if (inventory.getType().equals(TagType.TI2048.toString())) {
            i3 = getDeviceAPI().ISO15693_write_sm(4, originalUID, 0, block, 1, cArr, 4);
        } else if (inventory.getType().equals(TagType.STLRIS64K.toString())) {
            i3 = getDeviceAPI().ISO15693_write_sm(8, originalUID, 0, block, 1, cArr, 4);
        }
        if (i3 == 0) {
            return true;
        }
        Log.e("RFIDWithISO15693", "write() err:" + i3);
        return false;
    }

    public synchronized boolean writeAFI(int iAFI) throws RFIDNotFoundException {
        char[] cArr = new char[1];
        int parseInt = Integer.parseInt(new StringBuilder(String.valueOf(iAFI)).toString(), 16);
        int i = -1;
        ISO15693Entity inventory = inventory();
        if (inventory == null) {
            throw new RFIDNotFoundException();
        }
        if (inventory.getType().equals(TagType.ICODE2.toString())) {
            i = getDeviceAPI().ISO15693_writeAFI(0, cArr, 0, parseInt);
        } else if (inventory.getType().equals(TagType.TI2048.toString())) {
            i = getDeviceAPI().ISO15693_writeAFI(4, cArr, 0, parseInt);
        } else if (inventory.getType().equals(TagType.STLRIS64K.toString())) {
            i = getDeviceAPI().ISO15693_writeAFI(0, cArr, 0, parseInt);
        }
        if (i == 0) {
            return true;
        }
        Log.e("RFIDWithISO15693", "writeAFI() err:" + i);
        return false;
    }

    public synchronized boolean lockAFI() throws RFIDNotFoundException {
        if (inventory() == null) {
            throw new RFIDNotFoundException();
        }
        int ISO15693_lockAFI = getDeviceAPI().ISO15693_lockAFI(0, new char[1], 0);
        if (ISO15693_lockAFI == 0) {
            return true;
        }
        Log.e("RFIDWithISO15693", "LockAFI() err:" + ISO15693_lockAFI);
        return false;
    }

    public synchronized boolean writeDSFID(int iDSFID) throws RFIDNotFoundException {
        char[] cArr = new char[1];
        int parseInt = Integer.parseInt(new StringBuilder(String.valueOf(iDSFID)).toString(), 16);
        int i = -1;
        ISO15693Entity inventory = inventory();
        if (inventory == null) {
            throw new RFIDNotFoundException();
        }
        if (inventory.getType().equals(TagType.ICODE2.toString())) {
            i = getDeviceAPI().ISO15693_writeDSFID(0, cArr, 0, parseInt);
        } else if (inventory.getType().equals(TagType.TI2048.toString())) {
            i = getDeviceAPI().ISO15693_writeDSFID(4, cArr, 0, parseInt);
        } else if (inventory.getType().equals(TagType.STLRIS64K.toString())) {
            i = getDeviceAPI().ISO15693_writeDSFID(0, cArr, 0, parseInt);
        }
        if (i == 0) {
            return true;
        }
        Log.e("RFIDWithISO15693", "writeDSFID() err:" + i);
        return false;
    }

    public synchronized boolean lockDSFID() throws RFIDNotFoundException {
        char[] cArr = new char[1];
        ISO15693Entity inventory = inventory();
        if (inventory == null) {
            throw new RFIDNotFoundException();
        }
        inventory.getType().equals(TagType.STLRIS64K.toString());
        int ISO15693_lockDSFID = getDeviceAPI().ISO15693_lockDSFID(0, cArr, 0);
        if (ISO15693_lockDSFID == 0) {
            return true;
        }
        Log.e("RFIDWithISO15693", "LockDSFID() err:" + ISO15693_lockDSFID);
        return false;
    }
}
