package jxl.biff.drawing;

import jxl.WorkbookSettings;
import jxl.biff.ByteData;
import jxl.biff.IndexMapping;
import jxl.biff.IntegerHelper;
import jxl.biff.Type;
import jxl.common.Assert;
import jxl.common.Logger;
import jxl.read.biff.File;

/* loaded from: classes.dex */
public class Chart implements ByteData, EscherStream {
    private static final Logger logger = Logger.getLogger(Chart.class);
    private byte[] data;
    private DrawingData drawingData;
    private int drawingNumber;
    private int endpos;
    private File file;
    private boolean initialized;
    private MsoDrawingRecord msoDrawingRecord;
    private ObjRecord objRecord;
    private int startpos;
    private WorkbookSettings workbookSettings;

    public Chart(MsoDrawingRecord mso, ObjRecord obj, DrawingData dd, int sp, int ep, File f, WorkbookSettings ws) {
        this.msoDrawingRecord = mso;
        this.objRecord = obj;
        this.startpos = sp;
        this.endpos = ep;
        this.file = f;
        this.workbookSettings = ws;
        boolean z = true;
        if (mso != null) {
            this.drawingData = dd;
            dd.addData(mso.getRecord().getData());
            this.drawingNumber = this.drawingData.getNumDrawings() - 1;
        }
        this.initialized = false;
        if ((mso == null || obj == null) && (mso != null || obj != null)) {
            z = false;
        }
        Assert.verify(z);
    }

    @Override // jxl.biff.ByteData
    public byte[] getBytes() {
        if (!this.initialized) {
            initialize();
        }
        return this.data;
    }

    @Override // jxl.biff.drawing.EscherStream
    public byte[] getData() {
        return this.msoDrawingRecord.getRecord().getData();
    }

    private void initialize() {
        File file = this.file;
        int i = this.startpos;
        this.data = file.read(i, this.endpos - i);
        this.initialized = true;
    }

    public void rationalize(IndexMapping xfMapping, IndexMapping fontMapping, IndexMapping formatMapping) {
        if (!this.initialized) {
            initialize();
        }
        int pos = 0;
        while (true) {
            byte[] bArr = this.data;
            if (pos < bArr.length) {
                int code = IntegerHelper.getInt(bArr[pos], bArr[pos + 1]);
                byte[] bArr2 = this.data;
                int length = IntegerHelper.getInt(bArr2[pos + 2], bArr2[pos + 3]);
                Type type = Type.getType(code);
                if (type == Type.FONTX) {
                    byte[] bArr3 = this.data;
                    int fontind = IntegerHelper.getInt(bArr3[pos + 4], bArr3[pos + 5]);
                    IntegerHelper.getTwoBytes(fontMapping.getNewIndex(fontind), this.data, pos + 4);
                } else if (type == Type.FBI) {
                    byte[] bArr4 = this.data;
                    int fontind2 = IntegerHelper.getInt(bArr4[pos + 12], bArr4[pos + 13]);
                    IntegerHelper.getTwoBytes(fontMapping.getNewIndex(fontind2), this.data, pos + 12);
                } else if (type == Type.IFMT) {
                    byte[] bArr5 = this.data;
                    int formind = IntegerHelper.getInt(bArr5[pos + 4], bArr5[pos + 5]);
                    IntegerHelper.getTwoBytes(formatMapping.getNewIndex(formind), this.data, pos + 4);
                } else if (type == Type.ALRUNS) {
                    byte[] bArr6 = this.data;
                    int numRuns = IntegerHelper.getInt(bArr6[pos + 4], bArr6[pos + 5]);
                    int fontPos = pos + 6;
                    for (int i = 0; i < numRuns; i++) {
                        byte[] bArr7 = this.data;
                        int fontind3 = IntegerHelper.getInt(bArr7[fontPos + 2], bArr7[fontPos + 3]);
                        IntegerHelper.getTwoBytes(fontMapping.getNewIndex(fontind3), this.data, fontPos + 2);
                        fontPos += 4;
                    }
                }
                int numRuns2 = length + 4;
                pos += numRuns2;
            } else {
                return;
            }
        }
    }

    EscherContainer getSpContainer() {
        EscherContainer spContainer = this.drawingData.getSpContainer(this.drawingNumber);
        return spContainer;
    }

    MsoDrawingRecord getMsoDrawingRecord() {
        return this.msoDrawingRecord;
    }

    ObjRecord getObjRecord() {
        return this.objRecord;
    }
}
