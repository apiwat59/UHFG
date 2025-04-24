package jxl;

import jxl.biff.formula.FormulaException;

/* loaded from: classes.dex */
public interface FormulaCell extends Cell {
    String getFormula() throws FormulaException;
}
