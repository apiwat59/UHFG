package jxl.read.biff;

import java.util.ArrayList;
import java.util.Iterator;
import jxl.WorkbookSettings;
import jxl.biff.BaseCompoundFile;
import jxl.biff.IntegerHelper;
import jxl.common.Logger;
import kotlinx.coroutines.scheduling.WorkQueueKt;

/* loaded from: classes.dex */
public final class CompoundFile extends BaseCompoundFile {
    private static Logger logger = Logger.getLogger(CompoundFile.class);
    private int[] bigBlockChain;
    private int[] bigBlockDepotBlocks;
    private byte[] data;
    private int extensionBlock;
    private int numBigBlockDepotBlocks;
    private int numExtensionBlocks;
    private ArrayList propertySets;
    private byte[] rootEntry;
    private BaseCompoundFile.PropertyStorage rootEntryPropertyStorage;
    private int rootStartBlock;
    private int sbdStartBlock;
    private WorkbookSettings settings;
    private int[] smallBlockChain;

    public CompoundFile(byte[] d, WorkbookSettings ws) throws BiffException {
        this.data = d;
        this.settings = ws;
        for (int i = 0; i < IDENTIFIER.length; i++) {
            if (this.data[i] != IDENTIFIER[i]) {
                throw new BiffException(BiffException.unrecognizedOLEFile);
            }
        }
        this.propertySets = new ArrayList();
        byte[] bArr = this.data;
        this.numBigBlockDepotBlocks = IntegerHelper.getInt(bArr[44], bArr[45], bArr[46], bArr[47]);
        byte[] bArr2 = this.data;
        this.sbdStartBlock = IntegerHelper.getInt(bArr2[60], bArr2[61], bArr2[62], bArr2[63]);
        byte[] bArr3 = this.data;
        this.rootStartBlock = IntegerHelper.getInt(bArr3[48], bArr3[49], bArr3[50], bArr3[51]);
        byte[] bArr4 = this.data;
        this.extensionBlock = IntegerHelper.getInt(bArr4[68], bArr4[69], bArr4[70], bArr4[71]);
        byte[] bArr5 = this.data;
        int i2 = IntegerHelper.getInt(bArr5[72], bArr5[73], bArr5[74], bArr5[75]);
        this.numExtensionBlocks = i2;
        this.bigBlockDepotBlocks = new int[this.numBigBlockDepotBlocks];
        int pos = 76;
        int bbdBlocks = this.numBigBlockDepotBlocks;
        bbdBlocks = i2 != 0 ? 109 : bbdBlocks;
        for (int i3 = 0; i3 < bbdBlocks; i3++) {
            this.bigBlockDepotBlocks[i3] = IntegerHelper.getInt(d[pos], d[pos + 1], d[pos + 2], d[pos + 3]);
            pos += 4;
        }
        for (int j = 0; j < this.numExtensionBlocks; j++) {
            int pos2 = (this.extensionBlock + 1) * 512;
            int pos3 = this.numBigBlockDepotBlocks;
            int blocksToRead = Math.min(pos3 - bbdBlocks, WorkQueueKt.MASK);
            for (int i4 = bbdBlocks; i4 < bbdBlocks + blocksToRead; i4++) {
                this.bigBlockDepotBlocks[i4] = IntegerHelper.getInt(d[pos2], d[pos2 + 1], d[pos2 + 2], d[pos2 + 3]);
                pos2 += 4;
            }
            bbdBlocks += blocksToRead;
            if (bbdBlocks < this.numBigBlockDepotBlocks) {
                this.extensionBlock = IntegerHelper.getInt(d[pos2], d[pos2 + 1], d[pos2 + 2], d[pos2 + 3]);
            }
        }
        readBigBlockDepot();
        readSmallBlockDepot();
        this.rootEntry = readData(this.rootStartBlock);
        readPropertySets();
    }

    private void readBigBlockDepot() {
        int index = 0;
        this.bigBlockChain = new int[(this.numBigBlockDepotBlocks * 512) / 4];
        for (int i = 0; i < this.numBigBlockDepotBlocks; i++) {
            int pos = (this.bigBlockDepotBlocks[i] + 1) * 512;
            for (int j = 0; j < 128; j++) {
                int[] iArr = this.bigBlockChain;
                byte[] bArr = this.data;
                iArr[index] = IntegerHelper.getInt(bArr[pos], bArr[pos + 1], bArr[pos + 2], bArr[pos + 3]);
                pos += 4;
                index++;
            }
        }
    }

    private void readSmallBlockDepot() throws BiffException {
        int[] iArr;
        int index = 0;
        int sbdBlock = this.sbdStartBlock;
        this.smallBlockChain = new int[0];
        if (sbdBlock == -1) {
            logger.warn("invalid small block depot number");
            return;
        }
        int blockCount = 0;
        while (true) {
            iArr = this.bigBlockChain;
            if (blockCount > iArr.length || sbdBlock == -2) {
                break;
            }
            int[] oldChain = this.smallBlockChain;
            int[] iArr2 = new int[this.smallBlockChain.length + 128];
            this.smallBlockChain = iArr2;
            System.arraycopy(oldChain, 0, iArr2, 0, oldChain.length);
            int pos = (sbdBlock + 1) * 512;
            for (int j = 0; j < 128; j++) {
                int[] iArr3 = this.smallBlockChain;
                byte[] bArr = this.data;
                iArr3[index] = IntegerHelper.getInt(bArr[pos], bArr[pos + 1], bArr[pos + 2], bArr[pos + 3]);
                pos += 4;
                index++;
            }
            sbdBlock = this.bigBlockChain[sbdBlock];
            blockCount++;
        }
        if (blockCount > iArr.length) {
            throw new BiffException(BiffException.corruptFileFormat);
        }
    }

    private void readPropertySets() {
        int offset = 0;
        while (true) {
            byte[] bArr = this.rootEntry;
            if (offset >= bArr.length) {
                break;
            }
            byte[] d = new byte[128];
            System.arraycopy(bArr, offset, d, 0, d.length);
            BaseCompoundFile.PropertyStorage ps = new BaseCompoundFile.PropertyStorage(d);
            if (ps.name == null || ps.name.length() == 0) {
                if (ps.type == 5) {
                    ps.name = BaseCompoundFile.ROOT_ENTRY_NAME;
                    logger.warn("Property storage name for " + ps.type + " is empty - setting to " + BaseCompoundFile.ROOT_ENTRY_NAME);
                } else if (ps.size != 0) {
                    logger.warn("Property storage type " + ps.type + " is non-empty and has no associated name");
                }
            }
            this.propertySets.add(ps);
            if (ps.name.equalsIgnoreCase(BaseCompoundFile.ROOT_ENTRY_NAME)) {
                this.rootEntryPropertyStorage = ps;
            }
            offset += 128;
        }
        if (this.rootEntryPropertyStorage == null) {
            this.rootEntryPropertyStorage = (BaseCompoundFile.PropertyStorage) this.propertySets.get(0);
        }
    }

    public byte[] getStream(String streamName) throws BiffException {
        BaseCompoundFile.PropertyStorage ps = findPropertyStorage(streamName, this.rootEntryPropertyStorage);
        if (ps == null) {
            ps = getPropertyStorage(streamName);
        }
        if (ps.size >= 4096 || streamName.equalsIgnoreCase(BaseCompoundFile.ROOT_ENTRY_NAME)) {
            return getBigBlockStream(ps);
        }
        return getSmallBlockStream(ps);
    }

    public byte[] getStream(int psIndex) throws BiffException {
        BaseCompoundFile.PropertyStorage ps = getPropertyStorage(psIndex);
        if (ps.size >= 4096 || ps.name.equalsIgnoreCase(BaseCompoundFile.ROOT_ENTRY_NAME)) {
            return getBigBlockStream(ps);
        }
        return getSmallBlockStream(ps);
    }

    public BaseCompoundFile.PropertyStorage findPropertyStorage(String name) {
        return findPropertyStorage(name, this.rootEntryPropertyStorage);
    }

    private BaseCompoundFile.PropertyStorage findPropertyStorage(String name, BaseCompoundFile.PropertyStorage base) {
        if (base.child == -1) {
            return null;
        }
        BaseCompoundFile.PropertyStorage child = getPropertyStorage(base.child);
        if (child.name.equalsIgnoreCase(name)) {
            return child;
        }
        BaseCompoundFile.PropertyStorage prev = child;
        while (prev.previous != -1) {
            prev = getPropertyStorage(prev.previous);
            if (prev.name.equalsIgnoreCase(name)) {
                return prev;
            }
        }
        BaseCompoundFile.PropertyStorage next = child;
        while (next.next != -1) {
            next = getPropertyStorage(next.next);
            if (next.name.equalsIgnoreCase(name)) {
                return next;
            }
        }
        return findPropertyStorage(name, child);
    }

    private BaseCompoundFile.PropertyStorage getPropertyStorage(String name) throws BiffException {
        Iterator i = this.propertySets.iterator();
        boolean found = false;
        boolean multiple = false;
        BaseCompoundFile.PropertyStorage ps = null;
        while (i.hasNext()) {
            BaseCompoundFile.PropertyStorage ps2 = (BaseCompoundFile.PropertyStorage) i.next();
            if (ps2.name.equalsIgnoreCase(name)) {
                multiple = found;
                found = true;
                ps = ps2;
            }
        }
        if (multiple) {
            logger.warn("found multiple copies of property set " + name);
        }
        if (!found) {
            throw new BiffException(BiffException.streamNotFound);
        }
        return ps;
    }

    private BaseCompoundFile.PropertyStorage getPropertyStorage(int index) {
        return (BaseCompoundFile.PropertyStorage) this.propertySets.get(index);
    }

    private byte[] getBigBlockStream(BaseCompoundFile.PropertyStorage ps) {
        int numBlocks = ps.size / 512;
        if (ps.size % 512 != 0) {
            numBlocks++;
        }
        byte[] streamData = new byte[numBlocks * 512];
        int block = ps.startBlock;
        int count = 0;
        while (block != -2 && count < numBlocks) {
            int pos = (block + 1) * 512;
            System.arraycopy(this.data, pos, streamData, count * 512, 512);
            count++;
            block = this.bigBlockChain[block];
        }
        if (block != -2 && count == numBlocks) {
            logger.warn("Property storage size inconsistent with block chain.");
        }
        return streamData;
    }

    private byte[] getSmallBlockStream(BaseCompoundFile.PropertyStorage ps) throws BiffException {
        int[] iArr;
        byte[] rootdata = readData(this.rootEntryPropertyStorage.startBlock);
        byte[] sbdata = new byte[0];
        int block = ps.startBlock;
        int blockCount = 0;
        while (true) {
            iArr = this.smallBlockChain;
            if (blockCount > iArr.length || block == -2) {
                break;
            }
            byte[] olddata = sbdata;
            sbdata = new byte[olddata.length + 64];
            System.arraycopy(olddata, 0, sbdata, 0, olddata.length);
            int pos = block * 64;
            System.arraycopy(rootdata, pos, sbdata, olddata.length, 64);
            block = this.smallBlockChain[block];
            if (block == -1) {
                logger.warn("Incorrect terminator for small block stream " + ps.name);
                block = -2;
            }
            blockCount++;
        }
        if (blockCount > iArr.length) {
            throw new BiffException(BiffException.corruptFileFormat);
        }
        return sbdata;
    }

    private byte[] readData(int bl) throws BiffException {
        int[] iArr;
        int block = bl;
        byte[] entry = new byte[0];
        int blockCount = 0;
        while (true) {
            iArr = this.bigBlockChain;
            if (blockCount > iArr.length || block == -2) {
                break;
            }
            byte[] oldEntry = entry;
            entry = new byte[oldEntry.length + 512];
            System.arraycopy(oldEntry, 0, entry, 0, oldEntry.length);
            int pos = (block + 1) * 512;
            System.arraycopy(this.data, pos, entry, oldEntry.length, 512);
            int[] iArr2 = this.bigBlockChain;
            if (iArr2[block] == block) {
                throw new BiffException(BiffException.corruptFileFormat);
            }
            block = iArr2[block];
            blockCount++;
        }
        if (blockCount > iArr.length) {
            throw new BiffException(BiffException.corruptFileFormat);
        }
        return entry;
    }

    public int getNumberOfPropertySets() {
        return this.propertySets.size();
    }

    public BaseCompoundFile.PropertyStorage getPropertySet(int index) {
        return getPropertyStorage(index);
    }
}
