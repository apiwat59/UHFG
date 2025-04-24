package jxl.write;

import java.io.File;
import jxl.biff.drawing.Drawing;
import jxl.biff.drawing.DrawingGroup;
import jxl.biff.drawing.DrawingGroupObject;

/* loaded from: classes.dex */
public class WritableImage extends Drawing {
    public static Drawing.ImageAnchorProperties MOVE_AND_SIZE_WITH_CELLS = Drawing.MOVE_AND_SIZE_WITH_CELLS;
    public static Drawing.ImageAnchorProperties MOVE_WITH_CELLS = Drawing.MOVE_WITH_CELLS;
    public static Drawing.ImageAnchorProperties NO_MOVE_OR_SIZE_WITH_CELLS = Drawing.NO_MOVE_OR_SIZE_WITH_CELLS;

    public WritableImage(double x, double y, double width, double height, File image) {
        super(x, y, width, height, image);
    }

    public WritableImage(double x, double y, double width, double height, byte[] imageData) {
        super(x, y, width, height, imageData);
    }

    public WritableImage(DrawingGroupObject d, DrawingGroup dg) {
        super(d, dg);
    }

    @Override // jxl.biff.drawing.Drawing, jxl.Image
    public double getColumn() {
        return super.getX();
    }

    public void setColumn(double c) {
        super.setX(c);
    }

    @Override // jxl.biff.drawing.Drawing, jxl.Image
    public double getRow() {
        return super.getY();
    }

    public void setRow(double c) {
        super.setY(c);
    }

    @Override // jxl.biff.drawing.Drawing, jxl.biff.drawing.DrawingGroupObject
    public double getWidth() {
        return super.getWidth();
    }

    @Override // jxl.biff.drawing.Drawing, jxl.biff.drawing.DrawingGroupObject
    public void setWidth(double c) {
        super.setWidth(c);
    }

    @Override // jxl.biff.drawing.Drawing, jxl.biff.drawing.DrawingGroupObject
    public double getHeight() {
        return super.getHeight();
    }

    @Override // jxl.biff.drawing.Drawing, jxl.biff.drawing.DrawingGroupObject
    public void setHeight(double c) {
        super.setHeight(c);
    }

    @Override // jxl.biff.drawing.Drawing, jxl.Image
    public File getImageFile() {
        return super.getImageFile();
    }

    @Override // jxl.biff.drawing.Drawing, jxl.biff.drawing.DrawingGroupObject
    public byte[] getImageData() {
        return super.getImageData();
    }

    @Override // jxl.biff.drawing.Drawing
    public void setImageAnchor(Drawing.ImageAnchorProperties iap) {
        super.setImageAnchor(iap);
    }

    @Override // jxl.biff.drawing.Drawing
    public Drawing.ImageAnchorProperties getImageAnchor() {
        return super.getImageAnchor();
    }
}
