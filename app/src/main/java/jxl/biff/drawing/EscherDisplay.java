package jxl.biff.drawing;

import java.io.BufferedWriter;
import java.io.IOException;
import jxl.biff.drawing.Opt;

/* loaded from: classes.dex */
public class EscherDisplay {
    private EscherStream stream;
    private BufferedWriter writer;

    public EscherDisplay(EscherStream s, BufferedWriter bw) {
        this.stream = s;
        this.writer = bw;
    }

    public void display() throws IOException {
        EscherRecordData er = new EscherRecordData(this.stream, 0);
        EscherContainer ec = new EscherContainer(er);
        displayContainer(ec, 0);
    }

    private void displayContainer(EscherContainer ec, int level) throws IOException {
        displayRecord(ec, level);
        int level2 = level + 1;
        EscherRecord[] children = ec.getChildren();
        for (EscherRecord er : children) {
            if (er.getEscherData().isContainer()) {
                displayContainer((EscherContainer) er, level2);
            } else {
                displayRecord(er, level2);
            }
        }
    }

    private void displayRecord(EscherRecord er, int level) throws IOException {
        indent(level);
        EscherRecordType type = er.getType();
        this.writer.write(Integer.toString(type.getValue(), 16));
        this.writer.write(" - ");
        if (type == EscherRecordType.DGG_CONTAINER) {
            this.writer.write("Dgg Container");
            this.writer.newLine();
            return;
        }
        if (type == EscherRecordType.BSTORE_CONTAINER) {
            this.writer.write("BStore Container");
            this.writer.newLine();
            return;
        }
        if (type == EscherRecordType.DG_CONTAINER) {
            this.writer.write("Dg Container");
            this.writer.newLine();
            return;
        }
        if (type == EscherRecordType.SPGR_CONTAINER) {
            this.writer.write("Spgr Container");
            this.writer.newLine();
            return;
        }
        if (type == EscherRecordType.SP_CONTAINER) {
            this.writer.write("Sp Container");
            this.writer.newLine();
            return;
        }
        if (type == EscherRecordType.DGG) {
            this.writer.write("Dgg");
            this.writer.newLine();
            return;
        }
        if (type == EscherRecordType.BSE) {
            this.writer.write("Bse");
            this.writer.newLine();
            return;
        }
        if (type == EscherRecordType.DG) {
            Dg dg = new Dg(er.getEscherData());
            this.writer.write("Dg:  drawing id " + dg.getDrawingId() + " shape count " + dg.getShapeCount());
            this.writer.newLine();
            return;
        }
        if (type == EscherRecordType.SPGR) {
            this.writer.write("Spgr");
            this.writer.newLine();
            return;
        }
        if (type == EscherRecordType.SP) {
            Sp sp = new Sp(er.getEscherData());
            this.writer.write("Sp:  shape id " + sp.getShapeId() + " shape type " + sp.getShapeType());
            this.writer.newLine();
            return;
        }
        if (type == EscherRecordType.OPT) {
            Opt opt = new Opt(er.getEscherData());
            Opt.Property p260 = opt.getProperty(260);
            Opt.Property p261 = opt.getProperty(261);
            this.writer.write("Opt (value, stringValue): ");
            if (p260 != null) {
                this.writer.write("260: " + p260.value + ", " + p260.stringValue + ";");
            }
            if (p261 != null) {
                this.writer.write("261: " + p261.value + ", " + p261.stringValue + ";");
            }
            this.writer.newLine();
            return;
        }
        if (type == EscherRecordType.CLIENT_ANCHOR) {
            this.writer.write("Client Anchor");
            this.writer.newLine();
            return;
        }
        if (type == EscherRecordType.CLIENT_DATA) {
            this.writer.write("Client Data");
            this.writer.newLine();
        } else if (type == EscherRecordType.CLIENT_TEXT_BOX) {
            this.writer.write("Client Text Box");
            this.writer.newLine();
        } else if (type == EscherRecordType.SPLIT_MENU_COLORS) {
            this.writer.write("Split Menu Colors");
            this.writer.newLine();
        } else {
            this.writer.write("???");
            this.writer.newLine();
        }
    }

    private void indent(int level) throws IOException {
        for (int i = 0; i < level * 2; i++) {
            this.writer.write(32);
        }
    }
}
