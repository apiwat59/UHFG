package jxl.biff.drawing;

import android.R;
import androidx.core.app.FrameMetricsAggregator;
import java.io.IOException;
import jxl.WorkbookSettings;
import jxl.common.Assert;
import jxl.common.Logger;
import jxl.write.biff.File;
import kotlinx.coroutines.scheduling.WorkQueueKt;

/* loaded from: classes.dex */
public class ComboBox implements DrawingGroupObject {
    private static Logger logger = Logger.getLogger(ComboBox.class);
    private int blipId;
    private int column;
    private DrawingData drawingData;
    private DrawingGroup drawingGroup;
    private int drawingNumber;
    private EscherContainer escherData;
    private double height;
    private boolean initialized;
    private MsoDrawingRecord msoDrawingRecord;
    private ObjRecord objRecord;
    private int objectId;
    private Origin origin;
    private EscherContainer readSpContainer;
    private int referenceCount;
    private int row;
    private int shapeId;
    private EscherContainer spContainer;
    private ShapeType type;
    private double width;
    private WorkbookSettings workbookSettings;

    public ComboBox(MsoDrawingRecord mso, ObjRecord obj, DrawingData dd, DrawingGroup dg, WorkbookSettings ws) {
        boolean z = false;
        this.initialized = false;
        this.drawingGroup = dg;
        this.msoDrawingRecord = mso;
        this.drawingData = dd;
        this.objRecord = obj;
        this.initialized = false;
        this.workbookSettings = ws;
        this.origin = Origin.READ;
        this.drawingData.addData(this.msoDrawingRecord.getData());
        this.drawingNumber = this.drawingData.getNumDrawings() - 1;
        this.drawingGroup.addDrawing(this);
        if (mso != null && obj != null) {
            z = true;
        }
        Assert.verify(z);
        initialize();
    }

    public ComboBox(DrawingGroupObject dgo, DrawingGroup dg, WorkbookSettings ws) {
        this.initialized = false;
        ComboBox d = (ComboBox) dgo;
        Assert.verify(d.origin == Origin.READ);
        this.msoDrawingRecord = d.msoDrawingRecord;
        this.objRecord = d.objRecord;
        this.initialized = false;
        this.origin = Origin.READ;
        this.drawingData = d.drawingData;
        this.drawingGroup = dg;
        this.drawingNumber = d.drawingNumber;
        dg.addDrawing(this);
        this.workbookSettings = ws;
    }

    public ComboBox() {
        this.initialized = false;
        this.initialized = true;
        this.origin = Origin.WRITE;
        this.referenceCount = 1;
        this.type = ShapeType.HOST_CONTROL;
    }

    private void initialize() {
        EscherContainer spContainer = this.drawingData.getSpContainer(this.drawingNumber);
        this.readSpContainer = spContainer;
        Assert.verify(spContainer != null);
        EscherRecord[] children = this.readSpContainer.getChildren();
        Sp sp = (Sp) this.readSpContainer.getChildren()[0];
        this.objectId = this.objRecord.getObjectId();
        this.shapeId = sp.getShapeId();
        ShapeType type = ShapeType.getType(sp.getShapeType());
        this.type = type;
        if (type == ShapeType.UNKNOWN) {
            logger.warn("Unknown shape type");
        }
        ClientAnchor clientAnchor = null;
        for (int i = 0; i < children.length && clientAnchor == null; i++) {
            if (children[i].getType() == EscherRecordType.CLIENT_ANCHOR) {
                clientAnchor = (ClientAnchor) children[i];
            }
        }
        if (clientAnchor == null) {
            logger.warn("Client anchor not found");
        } else {
            this.column = (int) clientAnchor.getX1();
            this.row = (int) clientAnchor.getY1();
        }
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
    public final int getShapeId() {
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
        if (this.origin == Origin.READ) {
            return getReadSpContainer();
        }
        SpContainer spc = new SpContainer();
        Sp sp = new Sp(this.type, this.shapeId, 2560);
        spc.add(sp);
        Opt opt = new Opt();
        opt.addProperty(WorkQueueKt.MASK, false, false, R.string.accessibility_system_action_dpad_left_label);
        opt.addProperty(191, false, false, 524296);
        opt.addProperty(FrameMetricsAggregator.EVERY_DURATION, false, false, 524288);
        opt.addProperty(959, false, false, 131072);
        spc.add(opt);
        ClientAnchor clientAnchor = new ClientAnchor(this.column, this.row, r5 + 1, r6 + 1, 1);
        spc.add(clientAnchor);
        ClientData clientData = new ClientData();
        spc.add(clientData);
        return spc;
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
        return this.column;
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public void setX(double x) {
        if (this.origin == Origin.READ) {
            if (!this.initialized) {
                initialize();
            }
            this.origin = Origin.READ_WRITE;
        }
        this.column = (int) x;
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public double getY() {
        if (!this.initialized) {
            initialize();
        }
        return this.row;
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public void setY(double y) {
        if (this.origin == Origin.READ) {
            if (!this.initialized) {
                initialize();
            }
            this.origin = Origin.READ_WRITE;
        }
        this.row = (int) y;
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
        Assert.verify(this.origin == Origin.READ || this.origin == Origin.READ_WRITE);
        if (!this.initialized) {
            initialize();
        }
        return this.drawingGroup.getImageData(this.blipId);
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public ShapeType getType() {
        return this.type;
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public byte[] getImageBytes() {
        Assert.verify(false);
        return null;
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public String getImageFilePath() {
        Assert.verify(false);
        return null;
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public void writeAdditionalRecords(File outputFile) throws IOException {
        if (this.origin == Origin.READ) {
            outputFile.write(this.objRecord);
        } else {
            ObjRecord objrec = new ObjRecord(this.objectId, ObjRecord.COMBOBOX);
            outputFile.write(objrec);
        }
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public void writeTailRecords(File outputFile) {
    }

    public int getRow() {
        return 0;
    }

    public int getColumn() {
        return 0;
    }

    public int hashCode() {
        return getClass().getName().hashCode();
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public boolean isFirst() {
        return this.msoDrawingRecord.isFirst();
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public boolean isFormObject() {
        return false;
    }
}
