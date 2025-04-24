package jxl.biff.formula;

/* loaded from: classes.dex */
abstract class NumberValue extends Operand implements ParsedThing {
    public abstract double getValue();

    protected NumberValue() {
    }

    @Override // jxl.biff.formula.ParseItem
    public void getString(StringBuffer buf) {
        buf.append(Double.toString(getValue()));
    }
}
