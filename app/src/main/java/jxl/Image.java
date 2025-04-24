package jxl;

import java.io.File;
import jxl.common.LengthUnit;

/* loaded from: classes.dex */
public interface Image {
    double getColumn();

    double getHeight();

    double getHeight(LengthUnit lengthUnit);

    double getHorizontalResolution(LengthUnit lengthUnit);

    byte[] getImageData();

    File getImageFile();

    int getImageHeight();

    int getImageWidth();

    double getRow();

    double getVerticalResolution(LengthUnit lengthUnit);

    double getWidth();

    double getWidth(LengthUnit lengthUnit);
}
