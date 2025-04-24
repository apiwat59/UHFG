package jxl.biff.drawing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import jxl.common.Assert;
import jxl.common.Logger;
import jxl.read.biff.Record;
import jxl.write.biff.File;

/* loaded from: classes.dex */
public class DrawingGroup implements EscherStream {
    private static Logger logger = Logger.getLogger(DrawingGroup.class);
    private BStoreContainer bstoreContainer;
    private byte[] drawingData;
    private int drawingGroupId;
    private ArrayList drawings;
    private boolean drawingsOmitted;
    private EscherContainer escherData;
    private HashMap imageFiles;
    private boolean initialized;
    private int maxObjectId;
    private int maxShapeId;
    private int numBlips;
    private int numCharts;
    private Origin origin;

    public DrawingGroup(Origin o) {
        this.origin = o;
        this.initialized = o == Origin.WRITE;
        this.drawings = new ArrayList();
        this.imageFiles = new HashMap();
        this.drawingsOmitted = false;
        this.maxObjectId = 1;
        this.maxShapeId = 1024;
    }

    public DrawingGroup(DrawingGroup dg) {
        this.drawingData = dg.drawingData;
        this.escherData = dg.escherData;
        this.bstoreContainer = dg.bstoreContainer;
        this.initialized = dg.initialized;
        this.drawingData = dg.drawingData;
        this.escherData = dg.escherData;
        this.bstoreContainer = dg.bstoreContainer;
        this.numBlips = dg.numBlips;
        this.numCharts = dg.numCharts;
        this.drawingGroupId = dg.drawingGroupId;
        this.drawingsOmitted = dg.drawingsOmitted;
        this.origin = dg.origin;
        this.imageFiles = (HashMap) dg.imageFiles.clone();
        this.maxObjectId = dg.maxObjectId;
        this.maxShapeId = dg.maxShapeId;
        this.drawings = new ArrayList();
    }

    public void add(MsoDrawingGroupRecord mso) {
        addData(mso.getData());
    }

    public void add(Record cont) {
        addData(cont.getData());
    }

    private void addData(byte[] msodata) {
        byte[] bArr = this.drawingData;
        if (bArr == null) {
            byte[] bArr2 = new byte[msodata.length];
            this.drawingData = bArr2;
            System.arraycopy(msodata, 0, bArr2, 0, msodata.length);
        } else {
            byte[] newdata = new byte[bArr.length + msodata.length];
            System.arraycopy(bArr, 0, newdata, 0, bArr.length);
            System.arraycopy(msodata, 0, newdata, this.drawingData.length, msodata.length);
            this.drawingData = newdata;
        }
    }

    final void addDrawing(DrawingGroupObject d) {
        this.drawings.add(d);
        this.maxObjectId = Math.max(this.maxObjectId, d.getObjectId());
        this.maxShapeId = Math.max(this.maxShapeId, d.getShapeId());
    }

    public void add(Chart c) {
        this.numCharts++;
    }

    public void add(DrawingGroupObject d) {
        if (this.origin == Origin.READ) {
            this.origin = Origin.READ_WRITE;
            BStoreContainer bsc = getBStoreContainer();
            Dgg dgg = (Dgg) this.escherData.getChildren()[0];
            this.drawingGroupId = (dgg.getCluster(1).drawingGroupId - this.numBlips) - 1;
            int numBlips = bsc != null ? bsc.getNumBlips() : 0;
            this.numBlips = numBlips;
            if (bsc != null) {
                Assert.verify(numBlips == bsc.getNumBlips());
            }
        }
        if (!(d instanceof Drawing)) {
            this.maxObjectId++;
            this.maxShapeId++;
            d.setDrawingGroup(this);
            d.setObjectId(this.maxObjectId, this.numBlips + 1, this.maxShapeId);
            if (this.drawings.size() > this.maxObjectId) {
                logger.warn("drawings length " + this.drawings.size() + " exceeds the max object id " + this.maxObjectId);
                return;
            }
            return;
        }
        Drawing drawing = (Drawing) d;
        Drawing refImage = (Drawing) this.imageFiles.get(d.getImageFilePath());
        if (refImage == null) {
            this.maxObjectId++;
            this.maxShapeId++;
            this.drawings.add(drawing);
            drawing.setDrawingGroup(this);
            drawing.setObjectId(this.maxObjectId, this.numBlips + 1, this.maxShapeId);
            this.numBlips++;
            this.imageFiles.put(drawing.getImageFilePath(), drawing);
            return;
        }
        refImage.setReferenceCount(refImage.getReferenceCount() + 1);
        drawing.setDrawingGroup(this);
        drawing.setObjectId(refImage.getObjectId(), refImage.getBlipId(), refImage.getShapeId());
    }

    public void remove(DrawingGroupObject d) {
        if (getBStoreContainer() == null) {
            return;
        }
        if (this.origin == Origin.READ) {
            this.origin = Origin.READ_WRITE;
            this.numBlips = getBStoreContainer().getNumBlips();
            Dgg dgg = (Dgg) this.escherData.getChildren()[0];
            this.drawingGroupId = (dgg.getCluster(1).drawingGroupId - this.numBlips) - 1;
        }
        EscherRecord[] children = getBStoreContainer().getChildren();
        BlipStoreEntry bse = (BlipStoreEntry) children[d.getBlipId() - 1];
        bse.dereference();
        if (bse.getReferenceCount() == 0) {
            getBStoreContainer().remove(bse);
            Iterator i = this.drawings.iterator();
            while (i.hasNext()) {
                DrawingGroupObject drawing = (DrawingGroupObject) i.next();
                if (drawing.getBlipId() > d.getBlipId()) {
                    drawing.setObjectId(drawing.getObjectId(), drawing.getBlipId() - 1, drawing.getShapeId());
                }
            }
            this.numBlips--;
        }
    }

    private void initialize() {
        EscherRecordData er = new EscherRecordData(this, 0);
        Assert.verify(er.isContainer());
        EscherContainer escherContainer = new EscherContainer(er);
        this.escherData = escherContainer;
        Assert.verify(escherContainer.getLength() == this.drawingData.length);
        Assert.verify(this.escherData.getType() == EscherRecordType.DGG_CONTAINER);
        this.initialized = true;
    }

    private BStoreContainer getBStoreContainer() {
        if (this.bstoreContainer == null) {
            if (!this.initialized) {
                initialize();
            }
            EscherRecord[] children = this.escherData.getChildren();
            if (children.length > 1 && children[1].getType() == EscherRecordType.BSTORE_CONTAINER) {
                this.bstoreContainer = (BStoreContainer) children[1];
            }
        }
        return this.bstoreContainer;
    }

    @Override // jxl.biff.drawing.EscherStream
    public byte[] getData() {
        return this.drawingData;
    }

    public void write(File outputFile) throws IOException {
        if (this.origin == Origin.WRITE) {
            DggContainer dggContainer = new DggContainer();
            int i = this.numBlips;
            Dgg dgg = new Dgg(this.numCharts + i + 1, i);
            dgg.addCluster(1, 0);
            dgg.addCluster(this.numBlips + 1, 0);
            dggContainer.add(dgg);
            int drawingsAdded = 0;
            BStoreContainer bstoreCont = new BStoreContainer();
            Iterator i2 = this.drawings.iterator();
            while (i2.hasNext()) {
                Object o = i2.next();
                if (o instanceof Drawing) {
                    BlipStoreEntry bse = new BlipStoreEntry((Drawing) o);
                    bstoreCont.add(bse);
                    drawingsAdded++;
                }
            }
            if (drawingsAdded > 0) {
                bstoreCont.setNumBlips(drawingsAdded);
                dggContainer.add(bstoreCont);
            }
            dggContainer.add(new Opt());
            SplitMenuColors splitMenuColors = new SplitMenuColors();
            dggContainer.add(splitMenuColors);
            this.drawingData = dggContainer.getData();
        } else if (this.origin == Origin.READ_WRITE) {
            DggContainer dggContainer2 = new DggContainer();
            int i3 = this.numBlips;
            Dgg dgg2 = new Dgg(this.numCharts + i3 + 1, i3);
            dgg2.addCluster(1, 0);
            dgg2.addCluster(this.drawingGroupId + this.numBlips + 1, 0);
            dggContainer2.add(dgg2);
            BStoreContainer bstoreCont2 = new BStoreContainer();
            bstoreCont2.setNumBlips(this.numBlips);
            BStoreContainer readBStoreContainer = getBStoreContainer();
            if (readBStoreContainer != null) {
                EscherRecord[] children = readBStoreContainer.getChildren();
                for (EscherRecord escherRecord : children) {
                    BlipStoreEntry bse2 = (BlipStoreEntry) escherRecord;
                    bstoreCont2.add(bse2);
                }
            }
            Iterator i4 = this.drawings.iterator();
            while (i4.hasNext()) {
                DrawingGroupObject dgo = (DrawingGroupObject) i4.next();
                if (dgo instanceof Drawing) {
                    Drawing d = (Drawing) dgo;
                    if (d.getOrigin() == Origin.WRITE) {
                        BlipStoreEntry bse3 = new BlipStoreEntry(d);
                        bstoreCont2.add(bse3);
                    }
                }
            }
            dggContainer2.add(bstoreCont2);
            Opt opt = new Opt();
            opt.addProperty(191, false, false, 524296);
            opt.addProperty(385, false, false, 134217737);
            opt.addProperty(448, false, false, 134217792);
            dggContainer2.add(opt);
            SplitMenuColors splitMenuColors2 = new SplitMenuColors();
            dggContainer2.add(splitMenuColors2);
            this.drawingData = dggContainer2.getData();
        }
        MsoDrawingGroupRecord msodg = new MsoDrawingGroupRecord(this.drawingData);
        outputFile.write(msodg);
    }

    final int getNumberOfBlips() {
        return this.numBlips;
    }

    byte[] getImageData(int blipId) {
        int numBlips = getBStoreContainer().getNumBlips();
        this.numBlips = numBlips;
        Assert.verify(blipId <= numBlips);
        Assert.verify(this.origin == Origin.READ || this.origin == Origin.READ_WRITE);
        EscherRecord[] children = getBStoreContainer().getChildren();
        BlipStoreEntry bse = (BlipStoreEntry) children[blipId - 1];
        return bse.getImageData();
    }

    public void setDrawingsOmitted(MsoDrawingRecord mso, ObjRecord obj) {
        this.drawingsOmitted = true;
        if (obj != null) {
            this.maxObjectId = Math.max(this.maxObjectId, obj.getObjectId());
        }
    }

    public boolean hasDrawingsOmitted() {
        return this.drawingsOmitted;
    }

    public void updateData(DrawingGroup dg) {
        this.drawingsOmitted = dg.drawingsOmitted;
        this.maxObjectId = dg.maxObjectId;
        this.maxShapeId = dg.maxShapeId;
    }
}
