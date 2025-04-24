package jxl.write.biff;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import jxl.biff.BaseCompoundFile;
import jxl.biff.IntegerHelper;
import jxl.common.Assert;
import jxl.common.Logger;
import jxl.read.biff.BiffException;
import kotlinx.coroutines.scheduling.WorkQueueKt;

/* loaded from: classes.dex */
final class CompoundFile extends BaseCompoundFile {
    private static Logger logger = Logger.getLogger(CompoundFile.class);
    private int additionalPropertyBlocks;
    private ArrayList additionalPropertySets;
    private int bbdPos;
    private int bbdStartBlock;
    private byte[] bigBlockDepot;
    private ExcelDataOutput excelData;
    private int excelDataBlocks;
    private int excelDataStartBlock;
    private int extensionBlock;
    private int numBigBlockDepotBlocks;
    private int numExtensionBlocks;
    private int numPropertySets;
    private int numRootEntryBlocks;
    private int numSmallBlockDepotBlocks;
    private int numSmallBlockDepotChainBlocks;
    private int numSmallBlocks;
    private OutputStream out;
    private int requiredSize;
    private int rootStartBlock;
    private int sbdStartBlock;
    private int sbdStartBlockChain;
    private int size;
    private HashMap standardPropertySets;

    private static final class ReadPropertyStorage {
        byte[] data;
        int number;
        BaseCompoundFile.PropertyStorage propertyStorage;

        ReadPropertyStorage(BaseCompoundFile.PropertyStorage ps, byte[] d, int n) {
            this.propertyStorage = ps;
            this.data = d;
            this.number = n;
        }
    }

    public CompoundFile(ExcelDataOutput data, int l, OutputStream os, jxl.read.biff.CompoundFile rcf) throws CopyAdditionalPropertySetsException, IOException {
        this.size = l;
        this.excelData = data;
        readAdditionalPropertySets(rcf);
        this.numRootEntryBlocks = 1;
        ArrayList arrayList = this.additionalPropertySets;
        this.numPropertySets = (arrayList != null ? arrayList.size() : 0) + 4;
        if (this.additionalPropertySets != null) {
            this.numSmallBlockDepotChainBlocks = getBigBlocksRequired(this.numSmallBlocks * 4);
            this.numSmallBlockDepotBlocks = getBigBlocksRequired(this.numSmallBlocks * 64);
            this.numRootEntryBlocks += getBigBlocksRequired(this.additionalPropertySets.size() * 128);
        }
        int blocks = getBigBlocksRequired(l);
        if (l >= 4096) {
            this.requiredSize = blocks * 512;
        } else {
            this.requiredSize = 4096;
        }
        this.out = os;
        int i = this.requiredSize / 512;
        this.excelDataBlocks = i;
        this.numBigBlockDepotBlocks = 1;
        int startTotalBlocks = i + 8 + 8 + this.additionalPropertyBlocks + this.numSmallBlockDepotBlocks + this.numSmallBlockDepotChainBlocks + this.numRootEntryBlocks;
        int totalBlocks = startTotalBlocks + 1;
        double d = totalBlocks;
        Double.isNaN(d);
        int ceil = (int) Math.ceil(d / 128.0d);
        this.numBigBlockDepotBlocks = ceil;
        int totalBlocks2 = ceil + startTotalBlocks;
        double d2 = totalBlocks2;
        Double.isNaN(d2);
        int ceil2 = (int) Math.ceil(d2 / 128.0d);
        this.numBigBlockDepotBlocks = ceil2;
        int totalBlocks3 = startTotalBlocks + ceil2;
        int totalBlocks4 = 109 - 1;
        if (ceil2 > totalBlocks4) {
            this.extensionBlock = 0;
            int bbdBlocksLeft = (ceil2 - 109) + 1;
            double d3 = bbdBlocksLeft;
            Double.isNaN(d3);
            int ceil3 = (int) Math.ceil(d3 / 127.0d);
            this.numExtensionBlocks = ceil3;
            int totalBlocks5 = ceil3 + startTotalBlocks + this.numBigBlockDepotBlocks;
            double d4 = totalBlocks5;
            Double.isNaN(d4);
            int ceil4 = (int) Math.ceil(d4 / 128.0d);
            this.numBigBlockDepotBlocks = ceil4;
            totalBlocks3 = ceil4 + this.numExtensionBlocks + startTotalBlocks;
        } else {
            this.extensionBlock = -2;
            this.numExtensionBlocks = 0;
        }
        int i2 = this.numExtensionBlocks;
        this.excelDataStartBlock = i2;
        this.sbdStartBlock = -2;
        if (this.additionalPropertySets != null && this.numSmallBlockDepotBlocks != 0) {
            this.sbdStartBlock = this.excelDataBlocks + i2 + this.additionalPropertyBlocks + 16;
        }
        this.sbdStartBlockChain = -2;
        int i3 = this.sbdStartBlock;
        if (i3 != -2) {
            this.sbdStartBlockChain = i3 + this.numSmallBlockDepotBlocks;
        }
        int i4 = this.sbdStartBlockChain;
        if (i4 != -2) {
            this.bbdStartBlock = i4 + this.numSmallBlockDepotChainBlocks;
        } else {
            this.bbdStartBlock = i2 + this.excelDataBlocks + this.additionalPropertyBlocks + 16;
        }
        int i5 = this.bbdStartBlock + this.numBigBlockDepotBlocks;
        this.rootStartBlock = i5;
        if (totalBlocks3 != i5 + this.numRootEntryBlocks) {
            logger.warn("Root start block and total blocks are inconsistent  generated file may be corrupt");
            logger.warn("RootStartBlock " + this.rootStartBlock + " totalBlocks " + totalBlocks3);
        }
    }

    private void readAdditionalPropertySets(jxl.read.biff.CompoundFile readCompoundFile) throws CopyAdditionalPropertySetsException, IOException {
        byte[] data;
        if (readCompoundFile == null) {
            return;
        }
        this.additionalPropertySets = new ArrayList();
        this.standardPropertySets = new HashMap();
        int blocksRequired = 0;
        int numPropertySets = readCompoundFile.getNumberOfPropertySets();
        for (int i = 0; i < numPropertySets; i++) {
            BaseCompoundFile.PropertyStorage ps = readCompoundFile.getPropertySet(i);
            boolean standard = false;
            if (ps.name.equalsIgnoreCase(BaseCompoundFile.ROOT_ENTRY_NAME)) {
                standard = true;
                ReadPropertyStorage rps = new ReadPropertyStorage(ps, null, i);
                this.standardPropertySets.put(BaseCompoundFile.ROOT_ENTRY_NAME, rps);
            }
            int j = 0;
            while (true) {
                if (j >= STANDARD_PROPERTY_SETS.length || standard) {
                    break;
                }
                if (ps.name.equalsIgnoreCase(STANDARD_PROPERTY_SETS[j])) {
                    BaseCompoundFile.PropertyStorage ps2 = readCompoundFile.findPropertyStorage(ps.name);
                    Assert.verify(ps2 != null);
                    if (ps2 == ps) {
                        standard = true;
                        ReadPropertyStorage rps2 = new ReadPropertyStorage(ps, null, i);
                        this.standardPropertySets.put(STANDARD_PROPERTY_SETS[j], rps2);
                    }
                }
                j++;
            }
            if (!standard) {
                try {
                    if (ps.size > 0) {
                        data = readCompoundFile.getStream(i);
                    } else {
                        data = new byte[0];
                    }
                    ReadPropertyStorage rps3 = new ReadPropertyStorage(ps, data, i);
                    this.additionalPropertySets.add(rps3);
                    if (data.length > 4096) {
                        int blocks = getBigBlocksRequired(data.length);
                        blocksRequired += blocks;
                    } else {
                        int blocks2 = getSmallBlocksRequired(data.length);
                        this.numSmallBlocks += blocks2;
                    }
                } catch (BiffException e) {
                    logger.error(e);
                    throw new CopyAdditionalPropertySetsException();
                }
            }
        }
        this.additionalPropertyBlocks = blocksRequired;
    }

    public void write() throws IOException {
        writeHeader();
        writeExcelData();
        writeDocumentSummaryData();
        writeSummaryData();
        writeAdditionalPropertySets();
        writeSmallBlockDepot();
        writeSmallBlockDepotChain();
        writeBigBlockDepot();
        writePropertySets();
    }

    private void writeAdditionalPropertySets() throws IOException {
        ArrayList arrayList = this.additionalPropertySets;
        if (arrayList == null) {
            return;
        }
        Iterator i = arrayList.iterator();
        while (i.hasNext()) {
            ReadPropertyStorage rps = (ReadPropertyStorage) i.next();
            byte[] data = rps.data;
            if (data.length > 4096) {
                int numBlocks = getBigBlocksRequired(data.length);
                int requiredSize = numBlocks * 512;
                this.out.write(data, 0, data.length);
                byte[] padding = new byte[requiredSize - data.length];
                this.out.write(padding, 0, padding.length);
            }
        }
    }

    private void writeExcelData() throws IOException {
        this.excelData.writeData(this.out);
        byte[] padding = new byte[this.requiredSize - this.size];
        this.out.write(padding);
    }

    private void writeDocumentSummaryData() throws IOException {
        byte[] padding = new byte[4096];
        this.out.write(padding);
    }

    private void writeSummaryData() throws IOException {
        byte[] padding = new byte[4096];
        this.out.write(padding);
    }

    private void writeHeader() throws IOException {
        int i;
        byte[] headerBlock = new byte[512];
        byte[] extensionBlockData = new byte[this.numExtensionBlocks * 512];
        System.arraycopy(IDENTIFIER, 0, headerBlock, 0, IDENTIFIER.length);
        headerBlock[24] = 62;
        headerBlock[26] = 3;
        headerBlock[28] = -2;
        headerBlock[29] = -1;
        headerBlock[30] = 9;
        headerBlock[32] = 6;
        headerBlock[57] = 16;
        IntegerHelper.getFourBytes(this.numBigBlockDepotBlocks, headerBlock, 44);
        IntegerHelper.getFourBytes(this.sbdStartBlockChain, headerBlock, 60);
        IntegerHelper.getFourBytes(this.numSmallBlockDepotChainBlocks, headerBlock, 64);
        IntegerHelper.getFourBytes(this.extensionBlock, headerBlock, 68);
        IntegerHelper.getFourBytes(this.numExtensionBlocks, headerBlock, 72);
        IntegerHelper.getFourBytes(this.rootStartBlock, headerBlock, 48);
        int pos = 76;
        int blocksToWrite = Math.min(this.numBigBlockDepotBlocks, 109);
        int blocksWritten = 0;
        for (int i2 = 0; i2 < blocksToWrite; i2++) {
            IntegerHelper.getFourBytes(this.bbdStartBlock + i2, headerBlock, pos);
            pos += 4;
            blocksWritten++;
        }
        for (int i3 = pos; i3 < 512; i3++) {
            headerBlock[i3] = -1;
        }
        this.out.write(headerBlock);
        int pos2 = 0;
        int extBlock = 0;
        while (true) {
            i = this.numExtensionBlocks;
            if (extBlock >= i) {
                break;
            }
            int blocksToWrite2 = Math.min(this.numBigBlockDepotBlocks - blocksWritten, WorkQueueKt.MASK);
            for (int j = 0; j < blocksToWrite2; j++) {
                IntegerHelper.getFourBytes(this.bbdStartBlock + blocksWritten + j, extensionBlockData, pos2);
                pos2 += 4;
            }
            blocksWritten += blocksToWrite2;
            int nextBlock = blocksWritten == this.numBigBlockDepotBlocks ? -2 : extBlock + 1;
            IntegerHelper.getFourBytes(nextBlock, extensionBlockData, pos2);
            pos2 += 4;
            extBlock++;
        }
        if (i > 0) {
            for (int i4 = pos2; i4 < extensionBlockData.length; i4++) {
                extensionBlockData[i4] = -1;
            }
            this.out.write(extensionBlockData);
        }
    }

    private void checkBbdPos() throws IOException {
        if (this.bbdPos >= 512) {
            this.out.write(this.bigBlockDepot);
            this.bigBlockDepot = new byte[512];
            this.bbdPos = 0;
        }
    }

    private void writeBlockChain(int startBlock, int numBlocks) throws IOException {
        int blocksToWrite = numBlocks - 1;
        int blockNumber = startBlock + 1;
        while (blocksToWrite > 0) {
            int bbdBlocks = Math.min(blocksToWrite, (512 - this.bbdPos) / 4);
            for (int i = 0; i < bbdBlocks; i++) {
                IntegerHelper.getFourBytes(blockNumber, this.bigBlockDepot, this.bbdPos);
                this.bbdPos += 4;
                blockNumber++;
            }
            blocksToWrite -= bbdBlocks;
            checkBbdPos();
        }
        IntegerHelper.getFourBytes(-2, this.bigBlockDepot, this.bbdPos);
        this.bbdPos += 4;
        checkBbdPos();
    }

    private void writeAdditionalPropertySetBlockChains() throws IOException {
        ArrayList arrayList = this.additionalPropertySets;
        if (arrayList == null) {
            return;
        }
        int blockNumber = this.excelDataStartBlock + this.excelDataBlocks + 16;
        Iterator i = arrayList.iterator();
        while (i.hasNext()) {
            ReadPropertyStorage rps = (ReadPropertyStorage) i.next();
            if (rps.data.length > 4096) {
                int numBlocks = getBigBlocksRequired(rps.data.length);
                writeBlockChain(blockNumber, numBlocks);
                blockNumber += numBlocks;
            }
        }
    }

    private void writeSmallBlockDepotChain() throws IOException {
        if (this.sbdStartBlockChain == -2) {
            return;
        }
        byte[] smallBlockDepotChain = new byte[this.numSmallBlockDepotChainBlocks * 512];
        int pos = 0;
        int sbdBlockNumber = 1;
        Iterator i = this.additionalPropertySets.iterator();
        while (i.hasNext()) {
            ReadPropertyStorage rps = (ReadPropertyStorage) i.next();
            if (rps.data.length <= 4096 && rps.data.length != 0) {
                int numSmallBlocks = getSmallBlocksRequired(rps.data.length);
                for (int j = 0; j < numSmallBlocks - 1; j++) {
                    IntegerHelper.getFourBytes(sbdBlockNumber, smallBlockDepotChain, pos);
                    pos += 4;
                    sbdBlockNumber++;
                }
                IntegerHelper.getFourBytes(-2, smallBlockDepotChain, pos);
                pos += 4;
                sbdBlockNumber++;
            }
        }
        this.out.write(smallBlockDepotChain);
    }

    private void writeSmallBlockDepot() throws IOException {
        ArrayList arrayList = this.additionalPropertySets;
        if (arrayList == null) {
            return;
        }
        byte[] smallBlockDepot = new byte[this.numSmallBlockDepotBlocks * 512];
        int pos = 0;
        Iterator i = arrayList.iterator();
        while (i.hasNext()) {
            ReadPropertyStorage rps = (ReadPropertyStorage) i.next();
            if (rps.data.length <= 4096) {
                int smallBlocks = getSmallBlocksRequired(rps.data.length);
                int length = smallBlocks * 64;
                System.arraycopy(rps.data, 0, smallBlockDepot, pos, rps.data.length);
                pos += length;
            }
        }
        this.out.write(smallBlockDepot);
    }

    private void writeBigBlockDepot() throws IOException {
        this.bigBlockDepot = new byte[512];
        this.bbdPos = 0;
        for (int i = 0; i < this.numExtensionBlocks; i++) {
            IntegerHelper.getFourBytes(-3, this.bigBlockDepot, this.bbdPos);
            this.bbdPos += 4;
            checkBbdPos();
        }
        int i2 = this.excelDataStartBlock;
        writeBlockChain(i2, this.excelDataBlocks);
        int summaryInfoBlock = this.excelDataStartBlock + this.excelDataBlocks + this.additionalPropertyBlocks;
        for (int i3 = summaryInfoBlock; i3 < summaryInfoBlock + 7; i3++) {
            IntegerHelper.getFourBytes(i3 + 1, this.bigBlockDepot, this.bbdPos);
            this.bbdPos += 4;
            checkBbdPos();
        }
        IntegerHelper.getFourBytes(-2, this.bigBlockDepot, this.bbdPos);
        this.bbdPos += 4;
        checkBbdPos();
        for (int i4 = summaryInfoBlock + 8; i4 < summaryInfoBlock + 15; i4++) {
            IntegerHelper.getFourBytes(i4 + 1, this.bigBlockDepot, this.bbdPos);
            this.bbdPos += 4;
            checkBbdPos();
        }
        IntegerHelper.getFourBytes(-2, this.bigBlockDepot, this.bbdPos);
        this.bbdPos += 4;
        checkBbdPos();
        writeAdditionalPropertySetBlockChains();
        int i5 = this.sbdStartBlock;
        if (i5 != -2) {
            writeBlockChain(i5, this.numSmallBlockDepotBlocks);
            writeBlockChain(this.sbdStartBlockChain, this.numSmallBlockDepotChainBlocks);
        }
        for (int i6 = 0; i6 < this.numBigBlockDepotBlocks; i6++) {
            IntegerHelper.getFourBytes(-3, this.bigBlockDepot, this.bbdPos);
            this.bbdPos += 4;
            checkBbdPos();
        }
        int i7 = this.rootStartBlock;
        writeBlockChain(i7, this.numRootEntryBlocks);
        if (this.bbdPos != 0) {
            for (int i8 = this.bbdPos; i8 < 512; i8++) {
                this.bigBlockDepot[i8] = -1;
            }
            this.out.write(this.bigBlockDepot);
        }
    }

    private int getBigBlocksRequired(int length) {
        int blocks = length / 512;
        return length % 512 > 0 ? blocks + 1 : blocks;
    }

    private int getSmallBlocksRequired(int length) {
        int blocks = length / 64;
        return length % 64 > 0 ? blocks + 1 : blocks;
    }

    private void writePropertySets() throws IOException {
        ReadPropertyStorage rps;
        byte[] propertySetStorage = new byte[this.numRootEntryBlocks * 512];
        int[] mappings = null;
        if (this.additionalPropertySets != null) {
            mappings = new int[this.numPropertySets];
            for (int i = 0; i < STANDARD_PROPERTY_SETS.length; i++) {
                ReadPropertyStorage rps2 = (ReadPropertyStorage) this.standardPropertySets.get(STANDARD_PROPERTY_SETS[i]);
                if (rps2 != null) {
                    mappings[rps2.number] = i;
                } else {
                    logger.warn("Standard property set " + STANDARD_PROPERTY_SETS[i] + " not present in source file");
                }
            }
            int newMapping = STANDARD_PROPERTY_SETS.length;
            Iterator i2 = this.additionalPropertySets.iterator();
            while (i2.hasNext()) {
                mappings[((ReadPropertyStorage) i2.next()).number] = newMapping;
                newMapping++;
            }
        }
        int size = 0;
        int i3 = 4096;
        if (this.additionalPropertySets != null) {
            int size2 = 0 + (getBigBlocksRequired(this.requiredSize) * 512);
            size = size2 + (getBigBlocksRequired(4096) * 512) + (getBigBlocksRequired(4096) * 512);
            Iterator i4 = this.additionalPropertySets.iterator();
            while (i4.hasNext()) {
                ReadPropertyStorage rps3 = (ReadPropertyStorage) i4.next();
                if (rps3.propertyStorage.type != 1) {
                    size = rps3.propertyStorage.size >= 4096 ? size + (getBigBlocksRequired(rps3.propertyStorage.size) * 512) : size + (getSmallBlocksRequired(rps3.propertyStorage.size) * 64);
                }
            }
        }
        BaseCompoundFile.PropertyStorage ps = new BaseCompoundFile.PropertyStorage(BaseCompoundFile.ROOT_ENTRY_NAME);
        ps.setType(5);
        ps.setStartBlock(this.sbdStartBlock);
        ps.setSize(size);
        ps.setPrevious(-1);
        ps.setNext(-1);
        ps.setColour(0);
        int child = 1;
        if (this.additionalPropertySets != null) {
            child = mappings[((ReadPropertyStorage) this.standardPropertySets.get(BaseCompoundFile.ROOT_ENTRY_NAME)).propertyStorage.child];
        }
        ps.setChild(child);
        System.arraycopy(ps.data, 0, propertySetStorage, 0, 128);
        int pos = 0 + 128;
        BaseCompoundFile.PropertyStorage ps2 = new BaseCompoundFile.PropertyStorage(BaseCompoundFile.WORKBOOK_NAME);
        ps2.setType(2);
        ps2.setStartBlock(this.excelDataStartBlock);
        ps2.setSize(this.requiredSize);
        int previous = 3;
        int next = -1;
        if (this.additionalPropertySets != null) {
            ReadPropertyStorage rps4 = (ReadPropertyStorage) this.standardPropertySets.get(BaseCompoundFile.WORKBOOK_NAME);
            previous = rps4.propertyStorage.previous != -1 ? mappings[rps4.propertyStorage.previous] : -1;
            next = rps4.propertyStorage.next != -1 ? mappings[rps4.propertyStorage.next] : -1;
        }
        ps2.setPrevious(previous);
        ps2.setNext(next);
        ps2.setChild(-1);
        System.arraycopy(ps2.data, 0, propertySetStorage, pos, 128);
        int pos2 = pos + 128;
        BaseCompoundFile.PropertyStorage ps3 = new BaseCompoundFile.PropertyStorage(BaseCompoundFile.SUMMARY_INFORMATION_NAME);
        ps3.setType(2);
        ps3.setStartBlock(this.excelDataStartBlock + this.excelDataBlocks);
        ps3.setSize(4096);
        int previous2 = 1;
        int next2 = 3;
        if (this.additionalPropertySets != null && (rps = (ReadPropertyStorage) this.standardPropertySets.get(BaseCompoundFile.SUMMARY_INFORMATION_NAME)) != null) {
            previous2 = rps.propertyStorage.previous != -1 ? mappings[rps.propertyStorage.previous] : -1;
            next2 = rps.propertyStorage.next != -1 ? mappings[rps.propertyStorage.next] : -1;
        }
        ps3.setPrevious(previous2);
        ps3.setNext(next2);
        ps3.setChild(-1);
        System.arraycopy(ps3.data, 0, propertySetStorage, pos2, 128);
        int pos3 = pos2 + 128;
        BaseCompoundFile.PropertyStorage ps4 = new BaseCompoundFile.PropertyStorage(BaseCompoundFile.DOCUMENT_SUMMARY_INFORMATION_NAME);
        ps4.setType(2);
        ps4.setStartBlock(this.excelDataStartBlock + this.excelDataBlocks + 8);
        ps4.setSize(4096);
        ps4.setPrevious(-1);
        ps4.setNext(-1);
        ps4.setChild(-1);
        System.arraycopy(ps4.data, 0, propertySetStorage, pos3, 128);
        int pos4 = pos3 + 128;
        ArrayList arrayList = this.additionalPropertySets;
        if (arrayList != null) {
            int bigBlock = this.excelDataStartBlock + this.excelDataBlocks + 16;
            int smallBlock = 0;
            Iterator i5 = arrayList.iterator();
            while (i5.hasNext()) {
                ReadPropertyStorage rps5 = (ReadPropertyStorage) i5.next();
                int block = rps5.data.length > i3 ? bigBlock : smallBlock;
                BaseCompoundFile.PropertyStorage ps5 = new BaseCompoundFile.PropertyStorage(rps5.propertyStorage.name);
                ps5.setType(rps5.propertyStorage.type);
                ps5.setStartBlock(block);
                ps5.setSize(rps5.propertyStorage.size);
                int previous3 = rps5.propertyStorage.previous != -1 ? mappings[rps5.propertyStorage.previous] : -1;
                int next3 = rps5.propertyStorage.next != -1 ? mappings[rps5.propertyStorage.next] : -1;
                int child2 = rps5.propertyStorage.child != -1 ? mappings[rps5.propertyStorage.child] : -1;
                ps5.setPrevious(previous3);
                ps5.setNext(next3);
                ps5.setChild(child2);
                int[] mappings2 = mappings;
                System.arraycopy(ps5.data, 0, propertySetStorage, pos4, 128);
                pos4 += 128;
                if (rps5.data.length > 4096) {
                    bigBlock += getBigBlocksRequired(rps5.data.length);
                } else {
                    smallBlock += getSmallBlocksRequired(rps5.data.length);
                }
                mappings = mappings2;
                i3 = 4096;
            }
            this.out.write(propertySetStorage);
            return;
        }
        this.out.write(propertySetStorage);
    }
}
