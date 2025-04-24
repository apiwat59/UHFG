package jxl.write.biff;

import jxl.biff.IntegerHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class CalcModeRecord extends WritableRecordData {
    private CalcMode calculationMode;
    static CalcMode manual = new CalcMode(0);
    static CalcMode automatic = new CalcMode(1);
    static CalcMode automaticNoTables = new CalcMode(-1);

    /* JADX INFO: Access modifiers changed from: private */
    static class CalcMode {
        int value;

        public CalcMode(int m) {
            this.value = m;
        }
    }

    public CalcModeRecord(CalcMode cm) {
        super(Type.CALCMODE);
        this.calculationMode = cm;
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        byte[] data = new byte[2];
        IntegerHelper.getTwoBytes(this.calculationMode.value, data, 0);
        return data;
    }
}
