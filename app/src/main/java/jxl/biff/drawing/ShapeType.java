package jxl.biff.drawing;

/* loaded from: classes.dex */
final class ShapeType {
    private int value;
    private static ShapeType[] types = new ShapeType[0];
    public static final ShapeType MIN = new ShapeType(0);
    public static final ShapeType PICTURE_FRAME = new ShapeType(75);
    public static final ShapeType HOST_CONTROL = new ShapeType(201);
    public static final ShapeType TEXT_BOX = new ShapeType(202);
    public static final ShapeType UNKNOWN = new ShapeType(-1);

    ShapeType(int v) {
        this.value = v;
        ShapeType[] old = types;
        ShapeType[] shapeTypeArr = new ShapeType[types.length + 1];
        types = shapeTypeArr;
        System.arraycopy(old, 0, shapeTypeArr, 0, old.length);
        types[old.length] = this;
    }

    static ShapeType getType(int v) {
        ShapeType st = UNKNOWN;
        boolean found = false;
        int i = 0;
        while (true) {
            ShapeType[] shapeTypeArr = types;
            if (i >= shapeTypeArr.length || found) {
                break;
            }
            if (shapeTypeArr[i].value == v) {
                found = true;
                st = shapeTypeArr[i];
            }
            i++;
        }
        return st;
    }

    public int getValue() {
        return this.value;
    }
}
