package jxl.biff.drawing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import jxl.WorkbookSettings;
import jxl.biff.IntegerHelper;
import jxl.common.Logger;
import jxl.write.biff.File;

/* loaded from: classes.dex */
public class SheetDrawingWriter {
    private static Logger logger = Logger.getLogger(SheetDrawingWriter.class);
    private Chart[] charts = new Chart[0];
    private ArrayList drawings;
    private boolean drawingsModified;
    private WorkbookSettings workbookSettings;

    public SheetDrawingWriter(WorkbookSettings ws) {
    }

    public void setDrawings(ArrayList dr, boolean mod) {
        this.drawings = dr;
        this.drawingsModified = mod;
    }

    public void write(File outputFile) throws IOException {
        int len;
        Object[] spContainerData;
        if (this.drawings.size() == 0 && this.charts.length == 0) {
            return;
        }
        boolean modified = this.drawingsModified;
        int numImages = this.drawings.size();
        Iterator i = this.drawings.iterator();
        while (i.hasNext() && !modified) {
            DrawingGroupObject d = (DrawingGroupObject) i.next();
            if (d.getOrigin() != Origin.READ) {
                modified = true;
            }
        }
        if (numImages > 0 && !modified) {
            DrawingGroupObject d2 = (DrawingGroupObject) this.drawings.get(0);
            if (!d2.isFirst()) {
                modified = true;
            }
        }
        if (numImages == 0) {
            Chart[] chartArr = this.charts;
            if (chartArr.length == 1 && chartArr[0].getMsoDrawingRecord() == null) {
                modified = false;
            }
        }
        if (!modified) {
            writeUnmodified(outputFile);
            return;
        }
        Object[] spContainerData2 = new Object[this.charts.length + numImages];
        int length = 0;
        EscherContainer firstSpContainer = null;
        for (int i2 = 0; i2 < numImages; i2++) {
            DrawingGroupObject drawing = (DrawingGroupObject) this.drawings.get(i2);
            EscherContainer spc = drawing.getSpContainer();
            if (spc != null) {
                byte[] data = spc.getData();
                spContainerData2[i2] = data;
                if (i2 == 0) {
                    firstSpContainer = spc;
                } else {
                    length += data.length;
                }
            }
        }
        int i3 = 0;
        while (true) {
            Chart[] chartArr2 = this.charts;
            if (i3 >= chartArr2.length) {
                break;
            }
            EscherContainer spContainer = chartArr2[i3].getSpContainer();
            byte[] data2 = spContainer.setHeaderData(spContainer.getBytes());
            spContainerData2[i3 + numImages] = data2;
            if (i3 == 0 && numImages == 0) {
                firstSpContainer = spContainer;
            } else {
                length += data2.length;
            }
            i3++;
        }
        DgContainer dgContainer = new DgContainer();
        Dg dg = new Dg(this.charts.length + numImages);
        dgContainer.add(dg);
        SpgrContainer spgrContainer = new SpgrContainer();
        SpContainer spContainer2 = new SpContainer();
        Spgr spgr = new Spgr();
        spContainer2.add(spgr);
        Sp sp = new Sp(ShapeType.MIN, 1024, 5);
        spContainer2.add(sp);
        spgrContainer.add(spContainer2);
        spgrContainer.add(firstSpContainer);
        dgContainer.add(spgrContainer);
        byte[] firstMsoData = dgContainer.getData();
        IntegerHelper.getFourBytes(IntegerHelper.getInt(firstMsoData[4], firstMsoData[5], firstMsoData[6], firstMsoData[7]) + length, firstMsoData, 4);
        int len2 = IntegerHelper.getInt(firstMsoData[28], firstMsoData[29], firstMsoData[30], firstMsoData[31]);
        IntegerHelper.getFourBytes(len2 + length, firstMsoData, 28);
        if (numImages > 0 && ((DrawingGroupObject) this.drawings.get(0)).isFormObject()) {
            byte[] msodata2 = new byte[firstMsoData.length - 8];
            System.arraycopy(firstMsoData, 0, msodata2, 0, msodata2.length);
            firstMsoData = msodata2;
        }
        MsoDrawingRecord msoDrawingRecord = new MsoDrawingRecord(firstMsoData);
        outputFile.write(msoDrawingRecord);
        if (numImages > 0) {
            DrawingGroupObject firstDrawing = (DrawingGroupObject) this.drawings.get(0);
            firstDrawing.writeAdditionalRecords(outputFile);
        } else {
            Chart chart = this.charts[0];
            ObjRecord objRecord = chart.getObjRecord();
            outputFile.write(objRecord);
            outputFile.write(chart);
        }
        int i4 = 1;
        while (i4 < spContainerData2.length) {
            byte[] bytes = (byte[]) spContainerData2[i4];
            if (i4 < numImages) {
                len = len2;
                if (((DrawingGroupObject) this.drawings.get(i4)).isFormObject()) {
                    byte[] bytes2 = new byte[bytes.length - 8];
                    spContainerData = spContainerData2;
                    System.arraycopy(bytes, 0, bytes2, 0, bytes2.length);
                    bytes = bytes2;
                } else {
                    spContainerData = spContainerData2;
                }
            } else {
                len = len2;
                spContainerData = spContainerData2;
            }
            msoDrawingRecord = new MsoDrawingRecord(bytes);
            outputFile.write(msoDrawingRecord);
            if (i4 < numImages) {
                DrawingGroupObject d3 = (DrawingGroupObject) this.drawings.get(i4);
                d3.writeAdditionalRecords(outputFile);
            } else {
                Chart chart2 = this.charts[i4 - numImages];
                ObjRecord objRecord2 = chart2.getObjRecord();
                outputFile.write(objRecord2);
                outputFile.write(chart2);
            }
            i4++;
            len2 = len;
            spContainerData2 = spContainerData;
        }
        Iterator i5 = this.drawings.iterator();
        while (i5.hasNext()) {
            DrawingGroupObject dgo2 = (DrawingGroupObject) i5.next();
            dgo2.writeTailRecords(outputFile);
        }
    }

    private void writeUnmodified(File outputFile) throws IOException {
        EscherContainer[] spContainers;
        if (this.charts.length == 0 && this.drawings.size() == 0) {
            return;
        }
        if (this.charts.length == 0 && this.drawings.size() != 0) {
            Iterator i = this.drawings.iterator();
            while (i.hasNext()) {
                DrawingGroupObject d = (DrawingGroupObject) i.next();
                outputFile.write(d.getMsoDrawingRecord());
                d.writeAdditionalRecords(outputFile);
            }
            Iterator i2 = this.drawings.iterator();
            while (i2.hasNext()) {
                ((DrawingGroupObject) i2.next()).writeTailRecords(outputFile);
            }
            return;
        }
        if (this.drawings.size() == 0 && this.charts.length != 0) {
            int i3 = 0;
            while (true) {
                Chart[] chartArr = this.charts;
                if (i3 < chartArr.length) {
                    Chart curChart = chartArr[i3];
                    if (curChart.getMsoDrawingRecord() != null) {
                        outputFile.write(curChart.getMsoDrawingRecord());
                    }
                    if (curChart.getObjRecord() != null) {
                        outputFile.write(curChart.getObjRecord());
                    }
                    outputFile.write(curChart);
                    i3++;
                } else {
                    return;
                }
            }
        } else {
            int numDrawings = this.drawings.size();
            int length = 0;
            Chart[] chartArr2 = this.charts;
            EscherContainer[] spContainers2 = new EscherContainer[chartArr2.length + numDrawings];
            boolean[] isFormObject = new boolean[chartArr2.length + numDrawings];
            for (int i4 = 0; i4 < numDrawings; i4++) {
                DrawingGroupObject d2 = (DrawingGroupObject) this.drawings.get(i4);
                spContainers2[i4] = d2.getSpContainer();
                if (i4 > 0) {
                    length += spContainers2[i4].getLength();
                }
                if (d2.isFormObject()) {
                    isFormObject[i4] = true;
                }
            }
            int i5 = 0;
            while (true) {
                Chart[] chartArr3 = this.charts;
                if (i5 >= chartArr3.length) {
                    break;
                }
                spContainers2[i5 + numDrawings] = chartArr3[i5].getSpContainer();
                length += spContainers2[i5 + numDrawings].getLength();
                i5++;
            }
            DgContainer dgContainer = new DgContainer();
            Dg dg = new Dg(this.charts.length + numDrawings);
            dgContainer.add(dg);
            SpgrContainer spgrContainer = new SpgrContainer();
            SpContainer spContainer = new SpContainer();
            Spgr spgr = new Spgr();
            spContainer.add(spgr);
            Sp sp = new Sp(ShapeType.MIN, 1024, 5);
            spContainer.add(sp);
            spgrContainer.add(spContainer);
            spgrContainer.add(spContainers2[0]);
            dgContainer.add(spgrContainer);
            byte[] firstMsoData = dgContainer.getData();
            IntegerHelper.getFourBytes(IntegerHelper.getInt(firstMsoData[4], firstMsoData[5], firstMsoData[6], firstMsoData[7]) + length, firstMsoData, 4);
            int len = IntegerHelper.getInt(firstMsoData[28], firstMsoData[29], firstMsoData[30], firstMsoData[31]);
            IntegerHelper.getFourBytes(len + length, firstMsoData, 28);
            if (isFormObject[0]) {
                byte[] cbytes = new byte[firstMsoData.length - 8];
                System.arraycopy(firstMsoData, 0, cbytes, 0, cbytes.length);
                firstMsoData = cbytes;
            }
            MsoDrawingRecord msoDrawingRecord = new MsoDrawingRecord(firstMsoData);
            outputFile.write(msoDrawingRecord);
            DrawingGroupObject dgo = (DrawingGroupObject) this.drawings.get(0);
            dgo.writeAdditionalRecords(outputFile);
            int i6 = 1;
            while (true) {
                int length2 = length;
                int length3 = spContainers2.length;
                if (i6 >= length3) {
                    break;
                }
                byte[] bytes = spContainers2[i6].getBytes();
                int len2 = len;
                byte[] bytes2 = spContainers2[i6].setHeaderData(bytes);
                boolean[] isFormObject2 = isFormObject;
                if (!isFormObject[i6]) {
                    spContainers = spContainers2;
                } else {
                    byte[] cbytes2 = new byte[bytes2.length - 8];
                    spContainers = spContainers2;
                    System.arraycopy(bytes2, 0, cbytes2, 0, cbytes2.length);
                    bytes2 = cbytes2;
                }
                MsoDrawingRecord msoDrawingRecord2 = new MsoDrawingRecord(bytes2);
                outputFile.write(msoDrawingRecord2);
                if (i6 < numDrawings) {
                    DrawingGroupObject dgo2 = (DrawingGroupObject) this.drawings.get(i6);
                    dgo2.writeAdditionalRecords(outputFile);
                } else {
                    Chart chart = this.charts[i6 - numDrawings];
                    ObjRecord objRecord = chart.getObjRecord();
                    outputFile.write(objRecord);
                    outputFile.write(chart);
                }
                i6++;
                length = length2;
                len = len2;
                isFormObject = isFormObject2;
                spContainers2 = spContainers;
            }
            Iterator i7 = this.drawings.iterator();
            while (i7.hasNext()) {
                DrawingGroupObject dgo22 = (DrawingGroupObject) i7.next();
                dgo22.writeTailRecords(outputFile);
            }
        }
    }

    public void setCharts(Chart[] ch) {
        this.charts = ch;
    }

    public Chart[] getCharts() {
        return this.charts;
    }
}
