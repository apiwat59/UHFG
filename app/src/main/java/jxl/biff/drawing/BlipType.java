package jxl.biff.drawing;

/* loaded from: classes.dex */
final class BlipType {
    private String desc;
    private int value;
    private static BlipType[] types = new BlipType[0];
    public static final BlipType ERROR = new BlipType(0, "Error");
    public static final BlipType UNKNOWN = new BlipType(1, "Unknown");
    public static final BlipType EMF = new BlipType(2, "EMF");
    public static final BlipType WMF = new BlipType(3, "WMF");
    public static final BlipType PICT = new BlipType(4, "PICT");
    public static final BlipType JPEG = new BlipType(5, "JPEG");
    public static final BlipType PNG = new BlipType(6, "PNG");
    public static final BlipType DIB = new BlipType(7, "DIB");
    public static final BlipType FIRST_CLIENT = new BlipType(32, "FIRST");
    public static final BlipType LAST_CLIENT = new BlipType(255, "LAST");

    private BlipType(int val, String d) {
        this.value = val;
        this.desc = d;
        BlipType[] blipTypeArr = types;
        BlipType[] newtypes = new BlipType[blipTypeArr.length + 1];
        System.arraycopy(blipTypeArr, 0, newtypes, 0, blipTypeArr.length);
        newtypes[types.length] = this;
        types = newtypes;
    }

    public String getDescription() {
        return this.desc;
    }

    public int getValue() {
        return this.value;
    }

    public static BlipType getType(int val) {
        BlipType type = UNKNOWN;
        int i = 0;
        while (true) {
            BlipType[] blipTypeArr = types;
            if (i < blipTypeArr.length) {
                if (blipTypeArr[i].value != val) {
                    i++;
                } else {
                    BlipType type2 = blipTypeArr[i];
                    return type2;
                }
            } else {
                return type;
            }
        }
    }
}
