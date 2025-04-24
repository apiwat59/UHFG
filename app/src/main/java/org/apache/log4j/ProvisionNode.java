package org.apache.log4j;

import java.util.Vector;

/* loaded from: classes.dex */
class ProvisionNode extends Vector {
    ProvisionNode(Logger logger) {
        addElement(logger);
    }
}
