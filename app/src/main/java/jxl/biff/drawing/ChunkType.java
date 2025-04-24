package jxl.biff.drawing;

import java.util.Arrays;

/* loaded from: classes.dex */
class ChunkType {
    private byte[] id;
    private String name;
    private static ChunkType[] chunkTypes = new ChunkType[0];
    public static ChunkType IHDR = new ChunkType(73, 72, 68, 82, "IHDR");
    public static ChunkType IEND = new ChunkType(73, 69, 78, 68, "IEND");
    public static ChunkType PHYS = new ChunkType(112, 72, 89, 115, "pHYs");
    public static ChunkType UNKNOWN = new ChunkType(255, 255, 255, 255, "UNKNOWN");

    private ChunkType(int d1, int d2, int d3, int d4, String n) {
        this.id = new byte[]{(byte) d1, (byte) d2, (byte) d3, (byte) d4};
        this.name = n;
        ChunkType[] chunkTypeArr = chunkTypes;
        ChunkType[] ct = new ChunkType[chunkTypeArr.length + 1];
        System.arraycopy(chunkTypeArr, 0, ct, 0, chunkTypeArr.length);
        ct[chunkTypes.length] = this;
        chunkTypes = ct;
    }

    public String getName() {
        return this.name;
    }

    public static ChunkType getChunkType(byte d1, byte d2, byte d3, byte d4) {
        byte[] cmp = {d1, d2, d3, d4};
        boolean found = false;
        ChunkType chunk = UNKNOWN;
        int i = 0;
        while (true) {
            ChunkType[] chunkTypeArr = chunkTypes;
            if (i >= chunkTypeArr.length || found) {
                break;
            }
            if (Arrays.equals(chunkTypeArr[i].id, cmp)) {
                chunk = chunkTypes[i];
                found = true;
            }
            i++;
        }
        return chunk;
    }
}
