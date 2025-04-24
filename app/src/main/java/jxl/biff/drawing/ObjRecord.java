package jxl.biff.drawing;

import androidx.core.internal.view.SupportMenu;
import com.android.usbserial.driver.UsbId;
import jxl.biff.IntegerHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;
import jxl.common.Assert;
import jxl.common.Logger;
import jxl.read.biff.Record;

/* loaded from: classes.dex */
public class ObjRecord extends WritableRecordData {
    private static final int CLIPBOARD_FORMAT_LENGTH = 6;
    private static final int COMBOBOX_STRUCTURE_LENGTH = 44;
    private static final int COMMON_DATA_LENGTH = 22;
    private static final int END_LENGTH = 4;
    private static final int NOTE_STRUCTURE_LENGTH = 26;
    private static final int PICTURE_OPTION_LENGTH = 6;
    private int objectId;
    private boolean read;
    private ObjType type;
    private static final Logger logger = Logger.getLogger(ObjRecord.class);
    public static final ObjType GROUP = new ObjType(0, "Group");
    public static final ObjType LINE = new ObjType(1, "Line");
    public static final ObjType RECTANGLE = new ObjType(2, "Rectangle");
    public static final ObjType OVAL = new ObjType(3, "Oval");
    public static final ObjType ARC = new ObjType(4, "Arc");
    public static final ObjType CHART = new ObjType(5, "Chart");
    public static final ObjType TEXT = new ObjType(6, "Text");
    public static final ObjType BUTTON = new ObjType(7, "Button");
    public static final ObjType PICTURE = new ObjType(8, "Picture");
    public static final ObjType POLYGON = new ObjType(9, "Polygon");
    public static final ObjType CHECKBOX = new ObjType(11, "Checkbox");
    public static final ObjType OPTION = new ObjType(12, "Option");
    public static final ObjType EDITBOX = new ObjType(13, "Edit Box");
    public static final ObjType LABEL = new ObjType(14, "Label");
    public static final ObjType DIALOGUEBOX = new ObjType(15, "Dialogue Box");
    public static final ObjType SPINBOX = new ObjType(16, "Spin Box");
    public static final ObjType SCROLLBAR = new ObjType(17, "Scrollbar");
    public static final ObjType LISTBOX = new ObjType(18, "List Box");
    public static final ObjType GROUPBOX = new ObjType(19, "Group Box");
    public static final ObjType COMBOBOX = new ObjType(20, "Combo Box");
    public static final ObjType MSOFFICEDRAWING = new ObjType(30, "MS Office Drawing");
    public static final ObjType FORMCONTROL = new ObjType(20, "Form Combo Box");
    public static final ObjType EXCELNOTE = new ObjType(25, "Excel Note");
    public static final ObjType UNKNOWN = new ObjType(255, "Unknown");

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX INFO: Access modifiers changed from: private */
    public static final class ObjType {
        private static ObjType[] types = new ObjType[0];
        public String desc;
        public int value;

        ObjType(int v, String d) {
            this.value = v;
            this.desc = d;
            ObjType[] oldtypes = types;
            ObjType[] objTypeArr = new ObjType[types.length + 1];
            types = objTypeArr;
            System.arraycopy(oldtypes, 0, objTypeArr, 0, oldtypes.length);
            types[oldtypes.length] = this;
        }

        public String toString() {
            return this.desc;
        }

        public static ObjType getType(int val) {
            ObjType retval = ObjRecord.UNKNOWN;
            for (int i = 0; i < types.length && retval == ObjRecord.UNKNOWN; i++) {
                ObjType[] objTypeArr = types;
                if (objTypeArr[i].value == val) {
                    retval = objTypeArr[i];
                }
            }
            return retval;
        }
    }

    public ObjRecord(Record t) {
        super(t);
        byte[] data = t.getData();
        int objtype = IntegerHelper.getInt(data[4], data[5]);
        this.read = true;
        ObjType type = ObjType.getType(objtype);
        this.type = type;
        if (type == UNKNOWN) {
            logger.warn("unknown object type code " + objtype);
        }
        this.objectId = IntegerHelper.getInt(data[6], data[7]);
    }

    ObjRecord(int objId, ObjType t) {
        super(Type.OBJ);
        this.objectId = objId;
        this.type = t;
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        if (this.read) {
            return getRecord().getData();
        }
        ObjType objType = this.type;
        if (objType == PICTURE || objType == CHART) {
            return getPictureData();
        }
        if (objType == EXCELNOTE) {
            return getNoteData();
        }
        if (objType == COMBOBOX) {
            return getComboBoxData();
        }
        Assert.verify(false);
        return null;
    }

    private byte[] getPictureData() {
        byte[] data = new byte[38];
        IntegerHelper.getTwoBytes(21, data, 0);
        IntegerHelper.getTwoBytes(18, data, 0 + 2);
        IntegerHelper.getTwoBytes(this.type.value, data, 0 + 4);
        IntegerHelper.getTwoBytes(this.objectId, data, 0 + 6);
        IntegerHelper.getTwoBytes(UsbId.FTDI_FT4232H, data, 0 + 8);
        int pos = 0 + 22;
        IntegerHelper.getTwoBytes(7, data, pos);
        IntegerHelper.getTwoBytes(2, data, pos + 2);
        IntegerHelper.getTwoBytes(SupportMenu.USER_MASK, data, pos + 4);
        int pos2 = pos + 6;
        IntegerHelper.getTwoBytes(8, data, pos2);
        IntegerHelper.getTwoBytes(2, data, pos2 + 2);
        IntegerHelper.getTwoBytes(1, data, pos2 + 4);
        int pos3 = pos2 + 6;
        IntegerHelper.getTwoBytes(0, data, pos3);
        IntegerHelper.getTwoBytes(0, data, pos3 + 2);
        int i = pos3 + 4;
        return data;
    }

    private byte[] getNoteData() {
        byte[] data = new byte[52];
        IntegerHelper.getTwoBytes(21, data, 0);
        IntegerHelper.getTwoBytes(18, data, 0 + 2);
        IntegerHelper.getTwoBytes(this.type.value, data, 0 + 4);
        IntegerHelper.getTwoBytes(this.objectId, data, 0 + 6);
        IntegerHelper.getTwoBytes(16401, data, 0 + 8);
        int pos = 0 + 22;
        IntegerHelper.getTwoBytes(13, data, pos);
        IntegerHelper.getTwoBytes(22, data, pos + 2);
        int pos2 = pos + 26;
        IntegerHelper.getTwoBytes(0, data, pos2);
        IntegerHelper.getTwoBytes(0, data, pos2 + 2);
        int i = pos2 + 4;
        return data;
    }

    private byte[] getComboBoxData() {
        byte[] data = new byte[70];
        IntegerHelper.getTwoBytes(21, data, 0);
        IntegerHelper.getTwoBytes(18, data, 0 + 2);
        IntegerHelper.getTwoBytes(this.type.value, data, 0 + 4);
        IntegerHelper.getTwoBytes(this.objectId, data, 0 + 6);
        IntegerHelper.getTwoBytes(0, data, 0 + 8);
        int pos = 0 + 22;
        IntegerHelper.getTwoBytes(12, data, pos);
        IntegerHelper.getTwoBytes(20, data, pos + 2);
        data[pos + 14] = 1;
        data[pos + 16] = 4;
        data[pos + 20] = 16;
        data[pos + 24] = 19;
        data[pos + 26] = -18;
        data[pos + 27] = 31;
        data[pos + 30] = 4;
        data[pos + 34] = 1;
        data[pos + 35] = 6;
        data[pos + 38] = 2;
        data[pos + 40] = 8;
        data[pos + 42] = 64;
        int pos2 = pos + 44;
        IntegerHelper.getTwoBytes(0, data, pos2);
        IntegerHelper.getTwoBytes(0, data, pos2 + 2);
        int i = pos2 + 4;
        return data;
    }

    @Override // jxl.biff.RecordData
    public Record getRecord() {
        return super.getRecord();
    }

    public ObjType getType() {
        return this.type;
    }

    public int getObjectId() {
        return this.objectId;
    }
}
