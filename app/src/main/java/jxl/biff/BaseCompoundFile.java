package jxl.biff;

import jxl.common.Assert;
import jxl.common.Logger;

/* loaded from: classes.dex */
public abstract class BaseCompoundFile {
    protected static final int BIG_BLOCK_DEPOT_BLOCKS_POS = 76;
    protected static final int BIG_BLOCK_SIZE = 512;
    private static final int CHILD_POS = 76;
    private static final int COLOUR_POS = 67;
    public static final String COMP_OBJ_NAME = "\u0001CompObj";
    public static final int DIRECTORY_PS_TYPE = 1;
    protected static final int EXTENSION_BLOCK_POS = 68;
    public static final int FILE_PS_TYPE = 2;
    private static final int NEXT_POS = 72;
    public static final int NONE_PS_TYPE = 0;
    protected static final int NUM_BIG_BLOCK_DEPOT_BLOCKS_POS = 44;
    protected static final int NUM_EXTENSION_BLOCK_POS = 72;
    protected static final int NUM_SMALL_BLOCK_DEPOT_BLOCKS_POS = 64;
    private static final int PREVIOUS_POS = 68;
    protected static final int PROPERTY_STORAGE_BLOCK_SIZE = 128;
    public static final int ROOT_ENTRY_PS_TYPE = 5;
    protected static final int ROOT_START_BLOCK_POS = 48;
    private static final int SIZE_OF_NAME_POS = 64;
    private static final int SIZE_POS = 120;
    protected static final int SMALL_BLOCK_DEPOT_BLOCK_POS = 60;
    protected static final int SMALL_BLOCK_SIZE = 64;
    protected static final int SMALL_BLOCK_THRESHOLD = 4096;
    private static final int START_BLOCK_POS = 116;
    private static final int TYPE_POS = 66;
    private static Logger logger = Logger.getLogger(BaseCompoundFile.class);
    protected static final byte[] IDENTIFIER = {-48, -49, 17, -32, -95, -79, 26, -31};
    public static final String ROOT_ENTRY_NAME = "Root Entry";
    public static final String WORKBOOK_NAME = "Workbook";
    public static final String SUMMARY_INFORMATION_NAME = "\u0005SummaryInformation";
    public static final String DOCUMENT_SUMMARY_INFORMATION_NAME = "\u0005DocumentSummaryInformation";
    public static final String[] STANDARD_PROPERTY_SETS = {ROOT_ENTRY_NAME, WORKBOOK_NAME, SUMMARY_INFORMATION_NAME, DOCUMENT_SUMMARY_INFORMATION_NAME};

    public class PropertyStorage {
        public int child;
        public int colour;
        public byte[] data;
        public String name;
        public int next;
        public int previous;
        public int size;
        public int startBlock;
        public int type;

        public PropertyStorage(byte[] d) {
            this.data = d;
            int nameSize = IntegerHelper.getInt(d[64], d[65]);
            if (nameSize > 64) {
                BaseCompoundFile.logger.warn("property set name exceeds max length - truncating");
                nameSize = 64;
            }
            byte[] bArr = this.data;
            this.type = bArr[66];
            this.colour = bArr[67];
            this.startBlock = IntegerHelper.getInt(bArr[116], bArr[117], bArr[118], bArr[119]);
            byte[] bArr2 = this.data;
            this.size = IntegerHelper.getInt(bArr2[120], bArr2[121], bArr2[122], bArr2[123]);
            byte[] bArr3 = this.data;
            this.previous = IntegerHelper.getInt(bArr3[68], bArr3[69], bArr3[70], bArr3[71]);
            byte[] bArr4 = this.data;
            this.next = IntegerHelper.getInt(bArr4[72], bArr4[73], bArr4[74], bArr4[75]);
            byte[] bArr5 = this.data;
            this.child = IntegerHelper.getInt(bArr5[76], bArr5[77], bArr5[78], bArr5[79]);
            int chars = nameSize > 2 ? (nameSize - 1) / 2 : 0;
            StringBuffer n = new StringBuffer("");
            for (int i = 0; i < chars; i++) {
                n.append((char) this.data[i * 2]);
            }
            this.name = n.toString();
        }

        public PropertyStorage(String name) {
            this.data = new byte[128];
            Assert.verify(name.length() < 32);
            IntegerHelper.getTwoBytes((name.length() + 1) * 2, this.data, 64);
            for (int i = 0; i < name.length(); i++) {
                this.data[i * 2] = (byte) name.charAt(i);
            }
        }

        public void setType(int t) {
            this.type = t;
            this.data[66] = (byte) t;
        }

        public void setStartBlock(int sb) {
            this.startBlock = sb;
            IntegerHelper.getFourBytes(sb, this.data, 116);
        }

        public void setSize(int s) {
            this.size = s;
            IntegerHelper.getFourBytes(s, this.data, 120);
        }

        public void setPrevious(int prev) {
            this.previous = prev;
            IntegerHelper.getFourBytes(prev, this.data, 68);
        }

        public void setNext(int nxt) {
            this.next = nxt;
            IntegerHelper.getFourBytes(nxt, this.data, 72);
        }

        public void setChild(int dir) {
            this.child = dir;
            IntegerHelper.getFourBytes(dir, this.data, 76);
        }

        public void setColour(int col) {
            int i = col == 0 ? 0 : 1;
            this.colour = i;
            this.data[67] = (byte) i;
        }
    }

    protected BaseCompoundFile() {
    }
}
