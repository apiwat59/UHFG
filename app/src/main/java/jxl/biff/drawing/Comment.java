package jxl.biff.drawing;

import java.io.IOException;
import jxl.WorkbookSettings;
import jxl.biff.ContinueRecord;
import jxl.biff.IntegerHelper;
import jxl.biff.StringHelper;
import jxl.common.Assert;
import jxl.common.Logger;
import jxl.write.biff.File;

/* loaded from: classes.dex */
public class Comment implements DrawingGroupObject {
    private static Logger logger = Logger.getLogger(Comment.class);
    private int blipId;
    private int column;
    private String commentText;
    private DrawingData drawingData;
    private DrawingGroup drawingGroup;
    private int drawingNumber;
    private EscherContainer escherData;
    private ContinueRecord formatting;
    private double height;
    private boolean initialized;
    private MsoDrawingRecord mso;
    private MsoDrawingRecord msoDrawingRecord;
    private NoteRecord note;
    private ObjRecord objRecord;
    private int objectId;
    private Origin origin;
    private EscherContainer readSpContainer;
    private int referenceCount;
    private int row;
    private int shapeId;
    private EscherContainer spContainer;
    private ContinueRecord text;
    private TextObjectRecord txo;
    private ShapeType type;
    private double width;
    private WorkbookSettings workbookSettings;

    public Comment(MsoDrawingRecord msorec, ObjRecord obj, DrawingData dd, DrawingGroup dg, WorkbookSettings ws) {
        boolean z = false;
        this.initialized = false;
        this.drawingGroup = dg;
        this.msoDrawingRecord = msorec;
        this.drawingData = dd;
        this.objRecord = obj;
        this.initialized = false;
        this.workbookSettings = ws;
        this.origin = Origin.READ;
        this.drawingData.addData(this.msoDrawingRecord.getData());
        this.drawingNumber = this.drawingData.getNumDrawings() - 1;
        this.drawingGroup.addDrawing(this);
        if (this.msoDrawingRecord != null && this.objRecord != null) {
            z = true;
        }
        Assert.verify(z);
        if (!this.initialized) {
            initialize();
        }
    }

    public Comment(DrawingGroupObject dgo, DrawingGroup dg, WorkbookSettings ws) {
        this.initialized = false;
        Comment d = (Comment) dgo;
        Assert.verify(d.origin == Origin.READ);
        this.msoDrawingRecord = d.msoDrawingRecord;
        this.objRecord = d.objRecord;
        this.initialized = false;
        this.origin = Origin.READ;
        this.drawingData = d.drawingData;
        this.drawingGroup = dg;
        this.drawingNumber = d.drawingNumber;
        dg.addDrawing(this);
        this.mso = d.mso;
        this.txo = d.txo;
        this.text = d.text;
        this.formatting = d.formatting;
        this.note = d.note;
        this.width = d.width;
        this.height = d.height;
        this.workbookSettings = ws;
    }

    public Comment(String txt, int c, int r) {
        this.initialized = false;
        this.initialized = true;
        this.origin = Origin.WRITE;
        this.column = c;
        this.row = r;
        this.referenceCount = 1;
        this.type = ShapeType.TEXT_BOX;
        this.commentText = txt;
        this.width = 3.0d;
        this.height = 4.0d;
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
            logger.warn("client anchor not found");
        } else {
            this.column = ((int) clientAnchor.getX1()) - 1;
            this.row = ((int) clientAnchor.getY1()) + 1;
            this.width = clientAnchor.getX2() - clientAnchor.getX1();
            this.height = clientAnchor.getY2() - clientAnchor.getY1();
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
        if (this.spContainer == null) {
            this.spContainer = new SpContainer();
            Sp sp = new Sp(this.type, this.shapeId, 2560);
            this.spContainer.add(sp);
            Opt opt = new Opt();
            opt.addProperty(344, false, false, 0);
            opt.addProperty(385, false, false, 134217808);
            opt.addProperty(387, false, false, 134217808);
            opt.addProperty(959, false, false, 131074);
            this.spContainer.add(opt);
            double d = this.column;
            Double.isNaN(d);
            double d2 = d + 1.3d;
            double d3 = this.row;
            Double.isNaN(d3);
            double max = Math.max(0.0d, d3 - 0.6d);
            double d4 = this.column;
            Double.isNaN(d4);
            double d5 = d4 + 1.3d + this.width;
            double d6 = this.row;
            double d7 = this.height;
            Double.isNaN(d6);
            ClientAnchor clientAnchor = new ClientAnchor(d2, max, d5, d6 + d7, 1);
            this.spContainer.add(clientAnchor);
            ClientData clientData = new ClientData();
            this.spContainer.add(clientData);
            ClientTextBox clientTextBox = new ClientTextBox();
            this.spContainer.add(clientTextBox);
        }
        return this.spContainer;
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

    public void setTextObject(TextObjectRecord t) {
        this.txo = t;
    }

    public void setNote(NoteRecord t) {
        this.note = t;
    }

    public void setText(ContinueRecord t) {
        this.text = t;
    }

    public void setFormatting(ContinueRecord t) {
        this.formatting = t;
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

    public void addMso(MsoDrawingRecord d) {
        this.mso = d;
        this.drawingData.addRawData(d.getData());
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public void writeAdditionalRecords(File outputFile) throws IOException {
        if (this.origin == Origin.READ) {
            outputFile.write(this.objRecord);
            MsoDrawingRecord msoDrawingRecord = this.mso;
            if (msoDrawingRecord != null) {
                outputFile.write(msoDrawingRecord);
            }
            outputFile.write(this.txo);
            outputFile.write(this.text);
            ContinueRecord continueRecord = this.formatting;
            if (continueRecord != null) {
                outputFile.write(continueRecord);
                return;
            }
            return;
        }
        ObjRecord objrec = new ObjRecord(this.objectId, ObjRecord.EXCELNOTE);
        outputFile.write(objrec);
        ClientTextBox textBox = new ClientTextBox();
        MsoDrawingRecord msod = new MsoDrawingRecord(textBox.getData());
        outputFile.write(msod);
        TextObjectRecord txorec = new TextObjectRecord(getText());
        outputFile.write(txorec);
        byte[] textData = new byte[(this.commentText.length() * 2) + 1];
        textData[0] = 1;
        StringHelper.getUnicodeBytes(this.commentText, textData, 1);
        ContinueRecord textContinue = new ContinueRecord(textData);
        outputFile.write(textContinue);
        byte[] frData = new byte[16];
        IntegerHelper.getTwoBytes(0, frData, 0);
        IntegerHelper.getTwoBytes(0, frData, 2);
        IntegerHelper.getTwoBytes(this.commentText.length(), frData, 8);
        IntegerHelper.getTwoBytes(0, frData, 10);
        ContinueRecord frContinue = new ContinueRecord(frData);
        outputFile.write(frContinue);
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public void writeTailRecords(File outputFile) throws IOException {
        if (this.origin == Origin.READ) {
            outputFile.write(this.note);
        } else {
            NoteRecord noteRecord = new NoteRecord(this.column, this.row, this.objectId);
            outputFile.write(noteRecord);
        }
    }

    public int getRow() {
        return this.note.getRow();
    }

    public int getColumn() {
        return this.note.getColumn();
    }

    public String getText() {
        if (this.commentText == null) {
            Assert.verify(this.text != null);
            byte[] td = this.text.getData();
            if (td[0] == 0) {
                this.commentText = StringHelper.getString(td, td.length - 1, 1, this.workbookSettings);
            } else {
                this.commentText = StringHelper.getUnicodeString(td, (td.length - 1) / 2, 1);
            }
        }
        return this.commentText;
    }

    public int hashCode() {
        return this.commentText.hashCode();
    }

    public void setCommentText(String t) {
        this.commentText = t;
        if (this.origin == Origin.READ) {
            this.origin = Origin.READ_WRITE;
        }
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public boolean isFirst() {
        return this.msoDrawingRecord.isFirst();
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public boolean isFormObject() {
        return true;
    }
}
