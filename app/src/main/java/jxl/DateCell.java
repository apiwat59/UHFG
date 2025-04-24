package jxl;

import java.text.DateFormat;
import java.util.Date;

/* loaded from: classes.dex */
public interface DateCell extends Cell {
    Date getDate();

    DateFormat getDateFormat();

    boolean isTime();
}
