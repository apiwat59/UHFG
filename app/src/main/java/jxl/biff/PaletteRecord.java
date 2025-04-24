package jxl.biff;

import com.gg.reader.api.protocol.gx.EnumG;
import jxl.format.Colour;
import jxl.format.RGB;
import jxl.read.biff.Record;

/* loaded from: classes.dex */
public class PaletteRecord extends WritableRecordData {
    private static final int numColours = 56;
    private boolean dirty;
    private boolean initialized;
    private boolean read;
    private RGB[] rgbColours;

    public PaletteRecord(Record t) {
        super(t);
        this.rgbColours = new RGB[56];
        this.initialized = false;
        this.dirty = false;
        this.read = true;
    }

    public PaletteRecord() {
        super(Type.PALETTE);
        this.rgbColours = new RGB[56];
        this.initialized = true;
        this.dirty = false;
        this.read = false;
        Colour[] colours = Colour.getAllColours();
        for (Colour c : colours) {
            setColourRGB(c, c.getDefaultRGB().getRed(), c.getDefaultRGB().getGreen(), c.getDefaultRGB().getBlue());
        }
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        if (this.read && !this.dirty) {
            return getRecord().getData();
        }
        byte[] data = new byte[EnumG.BaseMid_SetGbBaseband];
        IntegerHelper.getTwoBytes(56, data, 0);
        for (int i = 0; i < 56; i++) {
            int pos = (i * 4) + 2;
            data[pos] = (byte) this.rgbColours[i].getRed();
            data[pos + 1] = (byte) this.rgbColours[i].getGreen();
            data[pos + 2] = (byte) this.rgbColours[i].getBlue();
        }
        return data;
    }

    private void initialize() {
        byte[] data = getRecord().getData();
        int numrecords = IntegerHelper.getInt(data[0], data[1]);
        for (int i = 0; i < numrecords; i++) {
            int pos = (i * 4) + 2;
            int red = IntegerHelper.getInt(data[pos], (byte) 0);
            int green = IntegerHelper.getInt(data[pos + 1], (byte) 0);
            int blue = IntegerHelper.getInt(data[pos + 2], (byte) 0);
            this.rgbColours[i] = new RGB(red, green, blue);
        }
        this.initialized = true;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void setColourRGB(Colour c, int r, int g, int b) {
        int pos = c.getValue() - 8;
        if (pos < 0 || pos >= 56) {
            return;
        }
        if (!this.initialized) {
            initialize();
        }
        this.rgbColours[pos] = new RGB(setValueRange(r, 0, 255), setValueRange(g, 0, 255), setValueRange(b, 0, 255));
        this.dirty = true;
    }

    public RGB getColourRGB(Colour c) {
        int pos = c.getValue() - 8;
        if (pos < 0 || pos >= 56) {
            return c.getDefaultRGB();
        }
        if (!this.initialized) {
            initialize();
        }
        return this.rgbColours[pos];
    }

    private int setValueRange(int val, int min, int max) {
        return Math.min(Math.max(val, min), max);
    }
}
