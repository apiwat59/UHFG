package org.apache.log4j.chainsaw;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.apache.log4j.Logger;

/* loaded from: classes.dex */
class ExitAction extends AbstractAction {
    public static final ExitAction INSTANCE;
    private static final Logger LOG;
    static /* synthetic */ Class class$org$apache$log4j$chainsaw$ExitAction;

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        Class cls = class$org$apache$log4j$chainsaw$ExitAction;
        if (cls == null) {
            cls = class$("org.apache.log4j.chainsaw.ExitAction");
            class$org$apache$log4j$chainsaw$ExitAction = cls;
        }
        LOG = Logger.getLogger(cls);
        INSTANCE = new ExitAction();
    }

    private ExitAction() {
    }

    public void actionPerformed(ActionEvent aIgnore) {
        LOG.info("shutting down");
        System.exit(0);
    }
}
