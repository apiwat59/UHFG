package jxl.biff.drawing;

import jxl.biff.IntegerHelper;
import jxl.common.Logger;

/* loaded from: classes.dex */
class ClientAnchor extends EscherAtom {
    private static final Logger logger = Logger.getLogger(ClientAnchor.class);
    private byte[] data;
    private int properties;
    private double x1;
    private double x2;
    private double y1;
    private double y2;

    public ClientAnchor(EscherRecordData erd) {
        super(erd);
        byte[] bytes = getBytes();
        this.properties = IntegerHelper.getInt(bytes[0], bytes[1]);
        int x1Cell = IntegerHelper.getInt(bytes[2], bytes[3]);
        int x1Fraction = IntegerHelper.getInt(bytes[4], bytes[5]);
        double d = x1Cell;
        double d2 = x1Fraction;
        Double.isNaN(d2);
        Double.isNaN(d);
        this.x1 = d + (d2 / 1024.0d);
        int y1Cell = IntegerHelper.getInt(bytes[6], bytes[7]);
        int y1Fraction = IntegerHelper.getInt(bytes[8], bytes[9]);
        double d3 = y1Cell;
        double d4 = y1Fraction;
        Double.isNaN(d4);
        Double.isNaN(d3);
        this.y1 = d3 + (d4 / 256.0d);
        int x2Cell = IntegerHelper.getInt(bytes[10], bytes[11]);
        int x2Fraction = IntegerHelper.getInt(bytes[12], bytes[13]);
        double d5 = x2Cell;
        double d6 = x2Fraction;
        Double.isNaN(d6);
        Double.isNaN(d5);
        this.x2 = d5 + (d6 / 1024.0d);
        int y2Cell = IntegerHelper.getInt(bytes[14], bytes[15]);
        int y2Fraction = IntegerHelper.getInt(bytes[16], bytes[17]);
        double d7 = y2Cell;
        double d8 = y2Fraction;
        Double.isNaN(d8);
        Double.isNaN(d7);
        this.y2 = d7 + (d8 / 256.0d);
    }

    public ClientAnchor(double x1, double y1, double x2, double y2, int props) {
        super(EscherRecordType.CLIENT_ANCHOR);
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.properties = props;
    }

    @Override // jxl.biff.drawing.EscherAtom, jxl.biff.drawing.EscherRecord
    byte[] getData() {
        byte[] bArr = new byte[18];
        this.data = bArr;
        IntegerHelper.getTwoBytes(this.properties, bArr, 0);
        IntegerHelper.getTwoBytes((int) this.x1, this.data, 2);
        double d = this.x1;
        double d2 = (int) d;
        Double.isNaN(d2);
        int x1fraction = (int) ((d - d2) * 1024.0d);
        IntegerHelper.getTwoBytes(x1fraction, this.data, 4);
        IntegerHelper.getTwoBytes((int) this.y1, this.data, 6);
        double d3 = this.y1;
        double d4 = (int) d3;
        Double.isNaN(d4);
        int y1fraction = (int) ((d3 - d4) * 256.0d);
        IntegerHelper.getTwoBytes(y1fraction, this.data, 8);
        IntegerHelper.getTwoBytes((int) this.x2, this.data, 10);
        double d5 = this.x2;
        double d6 = (int) d5;
        Double.isNaN(d6);
        int x2fraction = (int) ((d5 - d6) * 1024.0d);
        IntegerHelper.getTwoBytes(x2fraction, this.data, 12);
        IntegerHelper.getTwoBytes((int) this.y2, this.data, 14);
        double d7 = this.y2;
        double d8 = (int) d7;
        Double.isNaN(d8);
        int y2fraction = (int) ((d7 - d8) * 256.0d);
        IntegerHelper.getTwoBytes(y2fraction, this.data, 16);
        return setHeaderData(this.data);
    }

    double getX1() {
        return this.x1;
    }

    double getY1() {
        return this.y1;
    }

    double getX2() {
        return this.x2;
    }

    double getY2() {
        return this.y2;
    }

    int getProperties() {
        return this.properties;
    }
}
