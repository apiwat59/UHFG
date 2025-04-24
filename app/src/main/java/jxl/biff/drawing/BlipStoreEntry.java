package jxl.biff.drawing;

import java.io.IOException;
import jxl.biff.IntegerHelper;
import jxl.common.Assert;
import jxl.common.Logger;

/* loaded from: classes.dex */
class BlipStoreEntry extends EscherAtom {
    private static final int IMAGE_DATA_OFFSET = 61;
    private static Logger logger = Logger.getLogger(BlipStoreEntry.class);
    private byte[] data;
    private int imageDataLength;
    private int referenceCount;
    private BlipType type;
    private boolean write;

    public BlipStoreEntry(EscherRecordData erd) {
        super(erd);
        this.type = BlipType.getType(getInstance());
        this.write = false;
        byte[] bytes = getBytes();
        this.referenceCount = IntegerHelper.getInt(bytes[24], bytes[25], bytes[26], bytes[27]);
    }

    public BlipStoreEntry(Drawing d) throws IOException {
        super(EscherRecordType.BSE);
        this.type = BlipType.PNG;
        setVersion(2);
        setInstance(this.type.getValue());
        byte[] imageData = d.getImageBytes();
        int length = imageData.length;
        this.imageDataLength = length;
        byte[] bArr = new byte[length + 61];
        this.data = bArr;
        System.arraycopy(imageData, 0, bArr, 61, length);
        this.referenceCount = d.getReferenceCount();
        this.write = true;
    }

    public BlipType getBlipType() {
        return this.type;
    }

    @Override // jxl.biff.drawing.EscherAtom, jxl.biff.drawing.EscherRecord
    public byte[] getData() {
        if (this.write) {
            this.data[0] = (byte) this.type.getValue();
            this.data[1] = (byte) this.type.getValue();
            IntegerHelper.getFourBytes(this.imageDataLength + 8 + 17, this.data, 20);
            IntegerHelper.getFourBytes(this.referenceCount, this.data, 24);
            IntegerHelper.getFourBytes(0, this.data, 28);
            byte[] bArr = this.data;
            bArr[32] = 0;
            bArr[33] = 0;
            bArr[34] = 126;
            bArr[35] = 1;
            bArr[36] = 0;
            bArr[37] = 110;
            IntegerHelper.getTwoBytes(61470, bArr, 38);
            IntegerHelper.getFourBytes(this.imageDataLength + 17, this.data, 40);
        } else {
            this.data = getBytes();
        }
        return setHeaderData(this.data);
    }

    void dereference() {
        int i = this.referenceCount - 1;
        this.referenceCount = i;
        Assert.verify(i >= 0);
    }

    int getReferenceCount() {
        return this.referenceCount;
    }

    byte[] getImageData() {
        byte[] allData = getBytes();
        byte[] imageData = new byte[allData.length - 61];
        System.arraycopy(allData, 61, imageData, 0, imageData.length);
        return imageData;
    }
}
