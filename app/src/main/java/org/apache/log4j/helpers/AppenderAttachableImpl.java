package org.apache.log4j.helpers;

import java.util.Enumeration;
import java.util.Vector;
import org.apache.log4j.Appender;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.LoggingEvent;

/* loaded from: classes.dex */
public class AppenderAttachableImpl implements AppenderAttachable {
    protected Vector appenderList;

    @Override // org.apache.log4j.spi.AppenderAttachable
    public void addAppender(Appender newAppender) {
        if (newAppender == null) {
            return;
        }
        if (this.appenderList == null) {
            this.appenderList = new Vector(1);
        }
        if (!this.appenderList.contains(newAppender)) {
            this.appenderList.addElement(newAppender);
        }
    }

    public int appendLoopOnAppenders(LoggingEvent event) {
        Vector vector = this.appenderList;
        if (vector == null) {
            return 0;
        }
        int size = vector.size();
        for (int i = 0; i < size; i++) {
            Appender appender = (Appender) this.appenderList.elementAt(i);
            appender.doAppend(event);
        }
        return size;
    }

    @Override // org.apache.log4j.spi.AppenderAttachable
    public Enumeration getAllAppenders() {
        Vector vector = this.appenderList;
        if (vector == null) {
            return null;
        }
        return vector.elements();
    }

    @Override // org.apache.log4j.spi.AppenderAttachable
    public Appender getAppender(String name) {
        Vector vector = this.appenderList;
        if (vector == null || name == null) {
            return null;
        }
        int size = vector.size();
        for (int i = 0; i < size; i++) {
            Appender appender = (Appender) this.appenderList.elementAt(i);
            if (name.equals(appender.getName())) {
                return appender;
            }
        }
        return null;
    }

    @Override // org.apache.log4j.spi.AppenderAttachable
    public boolean isAttached(Appender appender) {
        Vector vector = this.appenderList;
        if (vector == null || appender == null) {
            return false;
        }
        int size = vector.size();
        for (int i = 0; i < size; i++) {
            Appender a = (Appender) this.appenderList.elementAt(i);
            if (a == appender) {
                return true;
            }
        }
        return false;
    }

    @Override // org.apache.log4j.spi.AppenderAttachable
    public void removeAllAppenders() {
        Vector vector = this.appenderList;
        if (vector != null) {
            int len = vector.size();
            for (int i = 0; i < len; i++) {
                Appender a = (Appender) this.appenderList.elementAt(i);
                a.close();
            }
            this.appenderList.removeAllElements();
            this.appenderList = null;
        }
    }

    @Override // org.apache.log4j.spi.AppenderAttachable
    public void removeAppender(Appender appender) {
        Vector vector;
        if (appender == null || (vector = this.appenderList) == null) {
            return;
        }
        vector.removeElement(appender);
    }

    @Override // org.apache.log4j.spi.AppenderAttachable
    public void removeAppender(String name) {
        Vector vector;
        if (name == null || (vector = this.appenderList) == null) {
            return;
        }
        int size = vector.size();
        for (int i = 0; i < size; i++) {
            if (name.equals(((Appender) this.appenderList.elementAt(i)).getName())) {
                this.appenderList.removeElementAt(i);
                return;
            }
        }
    }
}
