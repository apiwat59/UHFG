package jxl.biff.drawing;

import java.util.ArrayList;
import jxl.common.Assert;
import jxl.common.Logger;

/* loaded from: classes.dex */
public class DrawingData implements EscherStream {
    private static Logger logger = Logger.getLogger(DrawingData.class);
    private EscherRecord[] spContainers;
    private int numDrawings = 0;
    private byte[] drawingData = null;
    private boolean initialized = false;

    private void initialize() {
        EscherRecordData er = new EscherRecordData(this, 0);
        Assert.verify(er.isContainer());
        EscherContainer dgContainer = new EscherContainer(er);
        dgContainer.getChildren();
        EscherRecord[] children = dgContainer.getChildren();
        EscherContainer spgrContainer = null;
        for (int i = 0; i < children.length && spgrContainer == null; i++) {
            EscherRecord child = children[i];
            if (child.getType() == EscherRecordType.SPGR_CONTAINER) {
                spgrContainer = (EscherContainer) child;
            }
        }
        Assert.verify(spgrContainer != null);
        EscherRecord[] spgrChildren = spgrContainer.getChildren();
        boolean nestedContainers = false;
        for (int i2 = 0; i2 < spgrChildren.length && !nestedContainers; i2++) {
            if (spgrChildren[i2].getType() == EscherRecordType.SPGR_CONTAINER) {
                nestedContainers = true;
            }
        }
        if (!nestedContainers) {
            this.spContainers = spgrChildren;
        } else {
            ArrayList sps = new ArrayList();
            getSpContainers(spgrContainer, sps);
            EscherRecord[] escherRecordArr = new EscherRecord[sps.size()];
            this.spContainers = escherRecordArr;
            this.spContainers = (EscherRecord[]) sps.toArray(escherRecordArr);
        }
        this.initialized = true;
    }

    private void getSpContainers(EscherContainer spgrContainer, ArrayList sps) {
        EscherRecord[] spgrChildren = spgrContainer.getChildren();
        for (int i = 0; i < spgrChildren.length; i++) {
            if (spgrChildren[i].getType() == EscherRecordType.SP_CONTAINER) {
                sps.add(spgrChildren[i]);
            } else if (spgrChildren[i].getType() == EscherRecordType.SPGR_CONTAINER) {
                getSpContainers((EscherContainer) spgrChildren[i], sps);
            } else {
                logger.warn("Spgr Containers contains a record other than Sp/Spgr containers");
            }
        }
    }

    public void addData(byte[] data) {
        addRawData(data);
        this.numDrawings++;
    }

    public void addRawData(byte[] data) {
        byte[] bArr = this.drawingData;
        if (bArr == null) {
            this.drawingData = data;
            return;
        }
        byte[] newArray = new byte[bArr.length + data.length];
        System.arraycopy(bArr, 0, newArray, 0, bArr.length);
        System.arraycopy(data, 0, newArray, this.drawingData.length, data.length);
        this.drawingData = newArray;
        this.initialized = false;
    }

    final int getNumDrawings() {
        return this.numDrawings;
    }

    EscherContainer getSpContainer(int drawingNum) {
        if (!this.initialized) {
            initialize();
        }
        int i = drawingNum + 1;
        EscherRecord[] escherRecordArr = this.spContainers;
        if (i >= escherRecordArr.length) {
            throw new DrawingDataException();
        }
        EscherContainer spContainer = (EscherContainer) escherRecordArr[drawingNum + 1];
        Assert.verify(spContainer != null);
        return spContainer;
    }

    @Override // jxl.biff.drawing.EscherStream
    public byte[] getData() {
        return this.drawingData;
    }
}
