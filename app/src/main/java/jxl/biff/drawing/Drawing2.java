package jxl.biff.drawing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import jxl.common.Assert;
import jxl.common.Logger;

/* loaded from: classes.dex */
public class Drawing2 implements DrawingGroupObject {
    private static Logger logger = Logger.getLogger(Drawing.class);
    private int blipId;
    private DrawingData drawingData;
    private DrawingGroup drawingGroup;
    private int drawingNumber;
    private EscherContainer escherData;
    private double height;
    private byte[] imageData;
    private File imageFile;
    private boolean initialized;
    private MsoDrawingRecord msoDrawingRecord;
    private int objectId;
    private Origin origin;
    private EscherContainer readSpContainer;
    private int referenceCount;
    private int shapeId;
    private ShapeType type;
    private double width;
    private double x;
    private double y;

    public Drawing2(MsoDrawingRecord mso, DrawingData dd, DrawingGroup dg) {
        this.initialized = false;
        this.drawingGroup = dg;
        this.msoDrawingRecord = mso;
        this.drawingData = dd;
        this.initialized = false;
        this.origin = Origin.READ;
        this.drawingData.addRawData(this.msoDrawingRecord.getData());
        this.drawingGroup.addDrawing(this);
        Assert.verify(mso != null);
        initialize();
    }

    protected Drawing2(DrawingGroupObject dgo, DrawingGroup dg) {
        this.initialized = false;
        Drawing2 d = (Drawing2) dgo;
        Assert.verify(d.origin == Origin.READ);
        this.msoDrawingRecord = d.msoDrawingRecord;
        this.initialized = false;
        this.origin = Origin.READ;
        this.drawingData = d.drawingData;
        this.drawingGroup = dg;
        this.drawingNumber = d.drawingNumber;
        dg.addDrawing(this);
    }

    public Drawing2(double x, double y, double w, double h, File image) {
        this.initialized = false;
        this.imageFile = image;
        this.initialized = true;
        this.origin = Origin.WRITE;
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.referenceCount = 1;
        this.type = ShapeType.PICTURE_FRAME;
    }

    public Drawing2(double x, double y, double w, double h, byte[] image) {
        this.initialized = false;
        this.imageData = image;
        this.initialized = true;
        this.origin = Origin.WRITE;
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.referenceCount = 1;
        this.type = ShapeType.PICTURE_FRAME;
    }

    private void initialize() {
        this.initialized = true;
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public final void setObjectId(int objid, int bip, int sid) {
        this.objectId = objid;
        this.blipId = bip;
        this.shapeId = sid;
        if (this.origin == Origin.READ) {
            this.origin = Origin.READ_WRITE;
        }
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public final int getObjectId() {
        if (!this.initialized) {
            initialize();
        }
        return this.objectId;
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public int getShapeId() {
        if (!this.initialized) {
            initialize();
        }
        return this.shapeId;
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public final int getBlipId() {
        if (!this.initialized) {
            initialize();
        }
        return this.blipId;
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public MsoDrawingRecord getMsoDrawingRecord() {
        return this.msoDrawingRecord;
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public EscherContainer getSpContainer() {
        if (!this.initialized) {
            initialize();
        }
        Assert.verify(this.origin == Origin.READ);
        return getReadSpContainer();
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public void setDrawingGroup(DrawingGroup dg) {
        this.drawingGroup = dg;
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public DrawingGroup getDrawingGroup() {
        return this.drawingGroup;
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public Origin getOrigin() {
        return this.origin;
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public int getReferenceCount() {
        return this.referenceCount;
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public void setReferenceCount(int r) {
        this.referenceCount = r;
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public double getX() {
        if (!this.initialized) {
            initialize();
        }
        return this.x;
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public void setX(double x) {
        if (this.origin == Origin.READ) {
            if (!this.initialized) {
                initialize();
            }
            this.origin = Origin.READ_WRITE;
        }
        this.x = x;
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public double getY() {
        if (!this.initialized) {
            initialize();
        }
        return this.y;
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public void setY(double y) {
        if (this.origin == Origin.READ) {
            if (!this.initialized) {
                initialize();
            }
            this.origin = Origin.READ_WRITE;
        }
        this.y = y;
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public double getWidth() {
        if (!this.initialized) {
            initialize();
        }
        return this.width;
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public void setWidth(double w) {
        if (this.origin == Origin.READ) {
            if (!this.initialized) {
                initialize();
            }
            this.origin = Origin.READ_WRITE;
        }
        this.width = w;
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public double getHeight() {
        if (!this.initialized) {
            initialize();
        }
        return this.height;
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public void setHeight(double h) {
        if (this.origin == Origin.READ) {
            if (!this.initialized) {
                initialize();
            }
            this.origin = Origin.READ_WRITE;
        }
        this.height = h;
    }

    private EscherContainer getReadSpContainer() {
        if (!this.initialized) {
            initialize();
        }
        return this.readSpContainer;
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public byte[] getImageData() {
        Assert.verify(false);
        Assert.verify(this.origin == Origin.READ || this.origin == Origin.READ_WRITE);
        if (!this.initialized) {
            initialize();
        }
        return this.drawingGroup.getImageData(this.blipId);
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public byte[] getImageBytes() throws IOException {
        Assert.verify(false);
        if (this.origin == Origin.READ || this.origin == Origin.READ_WRITE) {
            return getImageData();
        }
        Assert.verify(this.origin == Origin.WRITE);
        File file = this.imageFile;
        if (file == null) {
            Assert.verify(this.imageData != null);
            return this.imageData;
        }
        byte[] data = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(this.imageFile);
        fis.read(data, 0, data.length);
        fis.close();
        return data;
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public ShapeType getType() {
        return this.type;
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public void writeAdditionalRecords(jxl.write.biff.File outputFile) throws IOException {
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public void writeTailRecords(jxl.write.biff.File outputFile) throws IOException {
    }

    public double getColumn() {
        return getX();
    }

    public double getRow() {
        return getY();
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public boolean isFirst() {
        return this.msoDrawingRecord.isFirst();
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public boolean isFormObject() {
        return false;
    }

    public void removeRow(int r) {
        if (this.y > r) {
            setY(r);
        }
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public String getImageFilePath() {
        Assert.verify(false);
        return null;
    }
}
