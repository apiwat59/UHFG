package jxl.biff.drawing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import jxl.CellView;
import jxl.Image;
import jxl.Sheet;
import jxl.common.Assert;
import jxl.common.LengthConverter;
import jxl.common.LengthUnit;
import jxl.common.Logger;

/* loaded from: classes.dex */
public class Drawing implements DrawingGroupObject, Image {
    private static final double DEFAULT_FONT_SIZE = 10.0d;
    private int blipId;
    private DrawingData drawingData;
    private DrawingGroup drawingGroup;
    private int drawingNumber;
    private EscherContainer escherData;
    private double height;
    private ImageAnchorProperties imageAnchorProperties;
    private byte[] imageData;
    private File imageFile;
    private boolean initialized;
    private MsoDrawingRecord msoDrawingRecord;
    private ObjRecord objRecord;
    private int objectId;
    private Origin origin;
    private PNGReader pngReader;
    private EscherContainer readSpContainer;
    private int referenceCount;
    private int shapeId;
    private Sheet sheet;
    private ShapeType type;
    private double width;
    private double x;
    private double y;
    private static Logger logger = Logger.getLogger(Drawing.class);
    public static ImageAnchorProperties MOVE_AND_SIZE_WITH_CELLS = new ImageAnchorProperties(1);
    public static ImageAnchorProperties MOVE_WITH_CELLS = new ImageAnchorProperties(2);
    public static ImageAnchorProperties NO_MOVE_OR_SIZE_WITH_CELLS = new ImageAnchorProperties(3);

    protected static class ImageAnchorProperties {
        private static ImageAnchorProperties[] o = new ImageAnchorProperties[0];
        private int value;

        ImageAnchorProperties(int v) {
            this.value = v;
            ImageAnchorProperties[] oldArray = o;
            ImageAnchorProperties[] imageAnchorPropertiesArr = new ImageAnchorProperties[oldArray.length + 1];
            o = imageAnchorPropertiesArr;
            System.arraycopy(oldArray, 0, imageAnchorPropertiesArr, 0, oldArray.length);
            o[oldArray.length] = this;
        }

        int getValue() {
            return this.value;
        }

        static ImageAnchorProperties getImageAnchorProperties(int val) {
            ImageAnchorProperties iap = Drawing.MOVE_AND_SIZE_WITH_CELLS;
            int pos = 0;
            while (true) {
                ImageAnchorProperties[] imageAnchorPropertiesArr = o;
                if (pos < imageAnchorPropertiesArr.length) {
                    if (imageAnchorPropertiesArr[pos].getValue() == val) {
                        ImageAnchorProperties iap2 = o[pos];
                        return iap2;
                    }
                    pos++;
                } else {
                    return iap;
                }
            }
        }
    }

    public Drawing(MsoDrawingRecord mso, ObjRecord obj, DrawingData dd, DrawingGroup dg, Sheet s) {
        boolean z = false;
        this.initialized = false;
        this.drawingGroup = dg;
        this.msoDrawingRecord = mso;
        this.drawingData = dd;
        this.objRecord = obj;
        this.sheet = s;
        this.initialized = false;
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

    protected Drawing(DrawingGroupObject dgo, DrawingGroup dg) {
        this.initialized = false;
        Drawing d = (Drawing) dgo;
        Assert.verify(d.origin == Origin.READ);
        this.msoDrawingRecord = d.msoDrawingRecord;
        this.objRecord = d.objRecord;
        this.initialized = false;
        this.origin = Origin.READ;
        this.drawingData = d.drawingData;
        this.drawingGroup = dg;
        this.drawingNumber = d.drawingNumber;
        dg.addDrawing(this);
    }

    public Drawing(double x, double y, double w, double h, File image) {
        this.initialized = false;
        this.imageFile = image;
        this.initialized = true;
        this.origin = Origin.WRITE;
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.referenceCount = 1;
        this.imageAnchorProperties = MOVE_WITH_CELLS;
        this.type = ShapeType.PICTURE_FRAME;
    }

    public Drawing(double x, double y, double w, double h, byte[] image) {
        this.initialized = false;
        this.imageData = image;
        this.initialized = true;
        this.origin = Origin.WRITE;
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.referenceCount = 1;
        this.imageAnchorProperties = MOVE_WITH_CELLS;
        this.type = ShapeType.PICTURE_FRAME;
    }

    private void initialize() {
        EscherContainer spContainer = this.drawingData.getSpContainer(this.drawingNumber);
        this.readSpContainer = spContainer;
        Assert.verify(spContainer != null);
        EscherRecord[] children = this.readSpContainer.getChildren();
        Sp sp = (Sp) this.readSpContainer.getChildren()[0];
        this.shapeId = sp.getShapeId();
        this.objectId = this.objRecord.getObjectId();
        ShapeType type = ShapeType.getType(sp.getShapeType());
        this.type = type;
        if (type == ShapeType.UNKNOWN) {
            logger.warn("Unknown shape type");
        }
        Opt opt = (Opt) this.readSpContainer.getChildren()[1];
        if (opt.getProperty(260) != null) {
            this.blipId = opt.getProperty(260).value;
        }
        if (opt.getProperty(261) != null) {
            this.imageFile = new File(opt.getProperty(261).stringValue);
        } else if (this.type == ShapeType.PICTURE_FRAME) {
            logger.warn("no filename property for drawing");
            this.imageFile = new File(Integer.toString(this.blipId));
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
            this.x = clientAnchor.getX1();
            this.y = clientAnchor.getY1();
            this.width = clientAnchor.getX2() - this.x;
            this.height = clientAnchor.getY2() - this.y;
            this.imageAnchorProperties = ImageAnchorProperties.getImageAnchorProperties(clientAnchor.getProperties());
        }
        if (this.blipId == 0) {
            logger.warn("linked drawings are not supported");
        }
        this.initialized = true;
    }

    @Override // jxl.Image
    public File getImageFile() {
        return this.imageFile;
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public String getImageFilePath() {
        File file = this.imageFile;
        if (file == null) {
            int i = this.blipId;
            return i != 0 ? Integer.toString(i) : "__new__image__";
        }
        return file.getPath();
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
        if (this.origin == Origin.READ) {
            return getReadSpContainer();
        }
        SpContainer spContainer = new SpContainer();
        Sp sp = new Sp(this.type, this.shapeId, 2560);
        spContainer.add(sp);
        Opt opt = new Opt();
        opt.addProperty(260, true, false, this.blipId);
        if (this.type == ShapeType.PICTURE_FRAME) {
            File file = this.imageFile;
            String filePath = file != null ? file.getPath() : "";
            opt.addProperty(261, true, true, filePath.length() * 2, filePath);
            opt.addProperty(447, false, false, 65536);
            opt.addProperty(959, false, false, 524288);
            spContainer.add(opt);
        }
        double d = this.x;
        double d2 = this.y;
        ClientAnchor clientAnchor = new ClientAnchor(d, d2, d + this.width, d2 + this.height, this.imageAnchorProperties.getValue());
        spContainer.add(clientAnchor);
        ClientData clientData = new ClientData();
        spContainer.add(clientData);
        return spContainer;
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
        Assert.verify(this.origin == Origin.READ || this.origin == Origin.READ_WRITE);
        if (!this.initialized) {
            initialize();
        }
        return this.drawingGroup.getImageData(this.blipId);
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public byte[] getImageBytes() throws IOException {
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
        if (this.origin == Origin.READ) {
            outputFile.write(this.objRecord);
        } else {
            ObjRecord objrec = new ObjRecord(this.objectId, ObjRecord.PICTURE);
            outputFile.write(objrec);
        }
    }

    @Override // jxl.biff.drawing.DrawingGroupObject
    public void writeTailRecords(jxl.write.biff.File outputFile) throws IOException {
    }

    @Override // jxl.Image
    public double getColumn() {
        return getX();
    }

    @Override // jxl.Image
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

    private double getWidthInPoints() {
        int lastCol;
        double d;
        Drawing drawing = this;
        if (drawing.sheet == null) {
            logger.warn("calculating image width:  sheet is null");
            return 0.0d;
        }
        double d2 = drawing.x;
        int firstCol = (int) d2;
        int lastCol2 = ((int) Math.ceil(d2 + drawing.width)) - 1;
        CellView cellView = drawing.sheet.getColumnView(firstCol);
        int firstColWidth = cellView.getSize();
        double d3 = drawing.x;
        double d4 = firstCol;
        Double.isNaN(d4);
        double d5 = 1.0d - (d3 - d4);
        double d6 = firstColWidth;
        Double.isNaN(d6);
        double firstColImageWidth = d5 * d6;
        double pointSize = cellView.getFormat() != null ? cellView.getFormat().getFont().getPointSize() : DEFAULT_FONT_SIZE;
        double firstColWidthInPoints = ((firstColImageWidth * 0.59d) * pointSize) / 256.0d;
        double lastColWidthInPoints = 0.0d;
        if (lastCol2 != firstCol) {
            CellView cellView2 = drawing.sheet.getColumnView(lastCol2);
            int lastColWidth = cellView2.getSize();
            double d7 = drawing.x + drawing.width;
            double d8 = lastCol2;
            Double.isNaN(d8);
            double d9 = d7 - d8;
            double d10 = lastColWidth;
            Double.isNaN(d10);
            double lastColImageWidth = d9 * d10;
            double pointSize2 = cellView2.getFormat() != null ? cellView2.getFormat().getFont().getPointSize() : DEFAULT_FONT_SIZE;
            lastColWidthInPoints = ((lastColImageWidth * 0.59d) * pointSize2) / 256.0d;
        }
        double width = 0.0d;
        int i = 0;
        while (i < (lastCol2 - firstCol) - 1) {
            CellView cellView3 = drawing.sheet.getColumnView(firstCol + 1 + i);
            if (cellView3.getFormat() != null) {
                lastCol = lastCol2;
                d = cellView3.getFormat().getFont().getPointSize();
            } else {
                lastCol = lastCol2;
                d = DEFAULT_FONT_SIZE;
            }
            double pointSize3 = d;
            double size = cellView3.getSize();
            Double.isNaN(size);
            width += ((size * 0.59d) * pointSize3) / 256.0d;
            i++;
            drawing = this;
            lastCol2 = lastCol;
        }
        double widthInPoints = width + firstColWidthInPoints + lastColWidthInPoints;
        return widthInPoints;
    }

    private double getHeightInPoints() {
        if (this.sheet == null) {
            logger.warn("calculating image height:  sheet is null");
            return 0.0d;
        }
        double d = this.y;
        int firstRow = (int) d;
        int lastRow = ((int) Math.ceil(d + this.height)) - 1;
        int firstRowHeight = this.sheet.getRowView(firstRow).getSize();
        double d2 = this.y;
        double d3 = firstRow;
        Double.isNaN(d3);
        double d4 = 1.0d - (d2 - d3);
        double d5 = firstRowHeight;
        Double.isNaN(d5);
        double d6 = d4 * d5;
        int lastRowHeight = 0;
        if (lastRow != firstRow) {
            lastRowHeight = this.sheet.getRowView(lastRow).getSize();
            double d7 = this.y + this.height;
            double d8 = lastRow;
            Double.isNaN(d8);
            double d9 = d7 - d8;
            double d10 = lastRowHeight;
            Double.isNaN(d10);
            double lastRowImageHeight = d9 * d10;
        }
        double height = 0.0d;
        for (int i = 0; i < (lastRow - firstRow) - 1; i++) {
            double size = this.sheet.getRowView(firstRow + 1 + i).getSize();
            Double.isNaN(size);
            height += size;
        }
        double d11 = firstRowHeight;
        Double.isNaN(d11);
        double d12 = lastRowHeight;
        Double.isNaN(d12);
        double heightInTwips = d11 + height + d12;
        double heightInPoints = heightInTwips / 20.0d;
        return heightInPoints;
    }

    @Override // jxl.Image
    public double getWidth(LengthUnit unit) {
        double widthInPoints = getWidthInPoints();
        return LengthConverter.getConversionFactor(LengthUnit.POINTS, unit) * widthInPoints;
    }

    @Override // jxl.Image
    public double getHeight(LengthUnit unit) {
        double heightInPoints = getHeightInPoints();
        return LengthConverter.getConversionFactor(LengthUnit.POINTS, unit) * heightInPoints;
    }

    @Override // jxl.Image
    public int getImageWidth() {
        return getPngReader().getWidth();
    }

    @Override // jxl.Image
    public int getImageHeight() {
        return getPngReader().getHeight();
    }

    @Override // jxl.Image
    public double getHorizontalResolution(LengthUnit unit) {
        int res = getPngReader().getHorizontalResolution();
        double d = res;
        double conversionFactor = LengthConverter.getConversionFactor(LengthUnit.METRES, unit);
        Double.isNaN(d);
        return d / conversionFactor;
    }

    @Override // jxl.Image
    public double getVerticalResolution(LengthUnit unit) {
        int res = getPngReader().getVerticalResolution();
        double d = res;
        double conversionFactor = LengthConverter.getConversionFactor(LengthUnit.METRES, unit);
        Double.isNaN(d);
        return d / conversionFactor;
    }

    private PNGReader getPngReader() {
        byte[] imdata;
        PNGReader pNGReader = this.pngReader;
        if (pNGReader != null) {
            return pNGReader;
        }
        if (this.origin == Origin.READ || this.origin == Origin.READ_WRITE) {
            imdata = getImageData();
        } else {
            try {
                imdata = getImageBytes();
            } catch (IOException e) {
                logger.warn("Could not read image file");
                imdata = new byte[0];
            }
        }
        PNGReader pNGReader2 = new PNGReader(imdata);
        this.pngReader = pNGReader2;
        pNGReader2.read();
        return this.pngReader;
    }

    protected void setImageAnchor(ImageAnchorProperties iap) {
        this.imageAnchorProperties = iap;
        if (this.origin == Origin.READ) {
            this.origin = Origin.READ_WRITE;
        }
    }

    protected ImageAnchorProperties getImageAnchor() {
        if (!this.initialized) {
            initialize();
        }
        return this.imageAnchorProperties;
    }
}
