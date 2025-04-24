package jxl.format;

/* loaded from: classes.dex */
public final class Orientation {
    private String string;
    private int value;
    private static Orientation[] orientations = new Orientation[0];
    public static Orientation HORIZONTAL = new Orientation(0, "horizontal");
    public static Orientation VERTICAL = new Orientation(255, "vertical");
    public static Orientation PLUS_90 = new Orientation(90, "up 90");
    public static Orientation MINUS_90 = new Orientation(180, "down 90");
    public static Orientation PLUS_45 = new Orientation(45, "up 45");
    public static Orientation MINUS_45 = new Orientation(135, "down 45");
    public static Orientation STACKED = new Orientation(255, "stacked");

    protected Orientation(int val, String s) {
        this.value = val;
        this.string = s;
        Orientation[] oldorients = orientations;
        Orientation[] orientationArr = new Orientation[oldorients.length + 1];
        orientations = orientationArr;
        System.arraycopy(oldorients, 0, orientationArr, 0, oldorients.length);
        orientations[oldorients.length] = this;
    }

    public int getValue() {
        return this.value;
    }

    public String getDescription() {
        return this.string;
    }

    public static Orientation getOrientation(int val) {
        int i = 0;
        while (true) {
            Orientation[] orientationArr = orientations;
            if (i < orientationArr.length) {
                if (orientationArr[i].getValue() != val) {
                    i++;
                } else {
                    return orientations[i];
                }
            } else {
                return HORIZONTAL;
            }
        }
    }
}
