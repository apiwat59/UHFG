package jxl.biff.drawing;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

/* loaded from: classes.dex */
public class PNGReader {
    private static byte[] PNG_MAGIC_NUMBER = {-119, 80, 78, 71, 13, 10, 26, 10};
    private int horizontalResolution;
    private Chunk ihdr;
    private Chunk phys;
    private int pixelHeight;
    private int pixelWidth;
    private byte[] pngData;
    private int resolutionUnit;
    private int verticalResolution;

    public PNGReader(byte[] data) {
        this.pngData = data;
    }

    void read() {
        byte[] header = new byte[PNG_MAGIC_NUMBER.length];
        System.arraycopy(this.pngData, 0, header, 0, header.length);
        boolean pngFile = Arrays.equals(PNG_MAGIC_NUMBER, header);
        if (!pngFile) {
            return;
        }
        int pos = 8;
        while (true) {
            byte[] bArr = this.pngData;
            if (pos >= bArr.length) {
                break;
            }
            int length = getInt(bArr[pos], bArr[pos + 1], bArr[pos + 2], bArr[pos + 3]);
            byte[] bArr2 = this.pngData;
            ChunkType chunkType = ChunkType.getChunkType(bArr2[pos + 4], bArr2[pos + 5], bArr2[pos + 6], bArr2[pos + 7]);
            if (chunkType == ChunkType.IHDR) {
                this.ihdr = new Chunk(pos + 8, length, chunkType, this.pngData);
            } else if (chunkType == ChunkType.PHYS) {
                this.phys = new Chunk(pos + 8, length, chunkType, this.pngData);
            }
            pos += length + 12;
        }
        byte[] ihdrData = this.ihdr.getData();
        this.pixelWidth = getInt(ihdrData[0], ihdrData[1], ihdrData[2], ihdrData[3]);
        this.pixelHeight = getInt(ihdrData[4], ihdrData[5], ihdrData[6], ihdrData[7]);
        Chunk chunk = this.phys;
        if (chunk != null) {
            byte[] physData = chunk.getData();
            this.resolutionUnit = physData[8];
            this.horizontalResolution = getInt(physData[0], physData[1], physData[2], physData[3]);
            this.verticalResolution = getInt(physData[4], physData[5], physData[6], physData[7]);
        }
    }

    private int getInt(byte d1, byte d2, byte d3, byte d4) {
        int i1 = d1 & 255;
        int i2 = d2 & 255;
        int i3 = d3 & 255;
        int i4 = d4 & 255;
        int val = (i1 << 24) | (i2 << 16) | (i3 << 8) | i4;
        return val;
    }

    public int getHeight() {
        return this.pixelHeight;
    }

    public int getWidth() {
        return this.pixelWidth;
    }

    public int getHorizontalResolution() {
        if (this.resolutionUnit == 1) {
            return this.horizontalResolution;
        }
        return 0;
    }

    public int getVerticalResolution() {
        if (this.resolutionUnit == 1) {
            return this.verticalResolution;
        }
        return 0;
    }

    public static void main(String[] args) {
        try {
            File f = new File(args[0]);
            int size = (int) f.length();
            byte[] data = new byte[size];
            FileInputStream fis = new FileInputStream(f);
            fis.read(data);
            fis.close();
            PNGReader reader = new PNGReader(data);
            reader.read();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
