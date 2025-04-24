package jxl.biff.drawing;

/* loaded from: classes.dex */
final class EscherRecordType {
    private int value;
    private static EscherRecordType[] types = new EscherRecordType[0];
    public static final EscherRecordType UNKNOWN = new EscherRecordType(0);
    public static final EscherRecordType DGG_CONTAINER = new EscherRecordType(61440);
    public static final EscherRecordType BSTORE_CONTAINER = new EscherRecordType(61441);
    public static final EscherRecordType DG_CONTAINER = new EscherRecordType(61442);
    public static final EscherRecordType SPGR_CONTAINER = new EscherRecordType(61443);
    public static final EscherRecordType SP_CONTAINER = new EscherRecordType(61444);
    public static final EscherRecordType DGG = new EscherRecordType(61446);
    public static final EscherRecordType BSE = new EscherRecordType(61447);
    public static final EscherRecordType DG = new EscherRecordType(61448);
    public static final EscherRecordType SPGR = new EscherRecordType(61449);
    public static final EscherRecordType SP = new EscherRecordType(61450);
    public static final EscherRecordType OPT = new EscherRecordType(61451);
    public static final EscherRecordType CLIENT_ANCHOR = new EscherRecordType(61456);
    public static final EscherRecordType CLIENT_DATA = new EscherRecordType(61457);
    public static final EscherRecordType CLIENT_TEXT_BOX = new EscherRecordType(61453);
    public static final EscherRecordType SPLIT_MENU_COLORS = new EscherRecordType(61726);

    private EscherRecordType(int val) {
        this.value = val;
        EscherRecordType[] escherRecordTypeArr = types;
        EscherRecordType[] newtypes = new EscherRecordType[escherRecordTypeArr.length + 1];
        System.arraycopy(escherRecordTypeArr, 0, newtypes, 0, escherRecordTypeArr.length);
        newtypes[types.length] = this;
        types = newtypes;
    }

    public int getValue() {
        return this.value;
    }

    public static EscherRecordType getType(int val) {
        EscherRecordType type = UNKNOWN;
        int i = 0;
        while (true) {
            EscherRecordType[] escherRecordTypeArr = types;
            if (i < escherRecordTypeArr.length) {
                if (val != escherRecordTypeArr[i].value) {
                    i++;
                } else {
                    EscherRecordType type2 = escherRecordTypeArr[i];
                    return type2;
                }
            } else {
                return type;
            }
        }
    }
}
