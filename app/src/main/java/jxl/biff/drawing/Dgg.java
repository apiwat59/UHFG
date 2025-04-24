package jxl.biff.drawing;

import java.util.ArrayList;
import jxl.biff.IntegerHelper;
import jxl.common.Logger;

/* loaded from: classes.dex */
class Dgg extends EscherAtom {
    private static Logger logger = Logger.getLogger(Dgg.class);
    private ArrayList clusters;
    private byte[] data;
    private int drawingsSaved;
    private int maxShapeId;
    private int numClusters;
    private int shapesSaved;

    static final class Cluster {
        int drawingGroupId;
        int shapeIdsUsed;

        Cluster(int dgId, int sids) {
            this.drawingGroupId = dgId;
            this.shapeIdsUsed = sids;
        }
    }

    public Dgg(EscherRecordData erd) {
        super(erd);
        this.clusters = new ArrayList();
        byte[] bytes = getBytes();
        this.maxShapeId = IntegerHelper.getInt(bytes[0], bytes[1], bytes[2], bytes[3]);
        this.numClusters = IntegerHelper.getInt(bytes[4], bytes[5], bytes[6], bytes[7]);
        this.shapesSaved = IntegerHelper.getInt(bytes[8], bytes[9], bytes[10], bytes[11]);
        this.drawingsSaved = IntegerHelper.getInt(bytes[12], bytes[13], bytes[14], bytes[15]);
        int pos = 16;
        for (int i = 0; i < this.numClusters; i++) {
            int dgId = IntegerHelper.getInt(bytes[pos], bytes[pos + 1]);
            int sids = IntegerHelper.getInt(bytes[pos + 2], bytes[pos + 3]);
            Cluster c = new Cluster(dgId, sids);
            this.clusters.add(c);
            pos += 4;
        }
    }

    public Dgg(int numShapes, int numDrawings) {
        super(EscherRecordType.DGG);
        this.shapesSaved = numShapes;
        this.drawingsSaved = numDrawings;
        this.clusters = new ArrayList();
    }

    void addCluster(int dgid, int sids) {
        Cluster c = new Cluster(dgid, sids);
        this.clusters.add(c);
    }

    @Override // jxl.biff.drawing.EscherAtom, jxl.biff.drawing.EscherRecord
    byte[] getData() {
        int size = this.clusters.size();
        this.numClusters = size;
        byte[] bArr = new byte[(size * 4) + 16];
        this.data = bArr;
        IntegerHelper.getFourBytes(this.shapesSaved + 1024, bArr, 0);
        IntegerHelper.getFourBytes(this.numClusters, this.data, 4);
        IntegerHelper.getFourBytes(this.shapesSaved, this.data, 8);
        IntegerHelper.getFourBytes(1, this.data, 12);
        int pos = 16;
        for (int i = 0; i < this.numClusters; i++) {
            Cluster c = (Cluster) this.clusters.get(i);
            IntegerHelper.getTwoBytes(c.drawingGroupId, this.data, pos);
            IntegerHelper.getTwoBytes(c.shapeIdsUsed, this.data, pos + 2);
            pos += 4;
        }
        return setHeaderData(this.data);
    }

    int getShapesSaved() {
        return this.shapesSaved;
    }

    int getDrawingsSaved() {
        return this.drawingsSaved;
    }

    Cluster getCluster(int i) {
        return (Cluster) this.clusters.get(i);
    }
}
