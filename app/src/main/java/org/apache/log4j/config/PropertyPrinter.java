package org.apache.log4j.config;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.config.PropertyGetter;

/* loaded from: classes.dex */
public class PropertyPrinter implements PropertyGetter.PropertyCallback {
    protected Hashtable appenderNames;
    protected boolean doCapitalize;
    protected Hashtable layoutNames;
    protected int numAppenders;
    protected PrintWriter out;

    public PropertyPrinter(PrintWriter out) {
        this(out, false);
    }

    public PropertyPrinter(PrintWriter out, boolean doCapitalize) {
        this.numAppenders = 0;
        this.appenderNames = new Hashtable();
        this.layoutNames = new Hashtable();
        this.out = out;
        this.doCapitalize = doCapitalize;
        print(out);
        out.flush();
    }

    protected String genAppName() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("A");
        int i = this.numAppenders;
        this.numAppenders = i + 1;
        stringBuffer.append(i);
        return stringBuffer.toString();
    }

    protected boolean isGenAppName(String name) {
        if (name.length() < 2 || name.charAt(0) != 'A') {
            return false;
        }
        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) < '0' || name.charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }

    public void print(PrintWriter out) {
        printOptions(out, Logger.getRootLogger());
        Enumeration cats = LogManager.getCurrentLoggers();
        while (cats.hasMoreElements()) {
            printOptions(out, (Logger) cats.nextElement());
        }
    }

    protected void printOptions(PrintWriter out, Logger cat) {
        String catKey;
        Enumeration appenders = cat.getAllAppenders();
        Level prio = cat.getLevel();
        String appenderString = prio == null ? "" : prio.toString();
        while (appenders.hasMoreElements()) {
            Appender app = (Appender) appenders.nextElement();
            String str = (String) this.appenderNames.get(app);
            String name = str;
            if (str == null) {
                String name2 = app.getName();
                name = name2;
                if (name2 == null || isGenAppName(name)) {
                    name = genAppName();
                }
                this.appenderNames.put(app, name);
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("log4j.appender.");
                stringBuffer.append(name);
                printOptions(out, app, stringBuffer.toString());
                if (app.getLayout() != null) {
                    Object layout = app.getLayout();
                    StringBuffer stringBuffer2 = new StringBuffer();
                    stringBuffer2.append("log4j.appender.");
                    stringBuffer2.append(name);
                    stringBuffer2.append(".layout");
                    printOptions(out, layout, stringBuffer2.toString());
                }
            }
            StringBuffer stringBuffer3 = new StringBuffer();
            stringBuffer3.append(appenderString);
            stringBuffer3.append(", ");
            stringBuffer3.append(name);
            appenderString = stringBuffer3.toString();
        }
        if (cat == Logger.getRootLogger()) {
            catKey = "log4j.rootLogger";
        } else {
            StringBuffer stringBuffer4 = new StringBuffer();
            stringBuffer4.append("log4j.logger.");
            stringBuffer4.append(cat.getName());
            catKey = stringBuffer4.toString();
        }
        if (appenderString != "") {
            StringBuffer stringBuffer5 = new StringBuffer();
            stringBuffer5.append(catKey);
            stringBuffer5.append("=");
            stringBuffer5.append(appenderString);
            out.println(stringBuffer5.toString());
        }
        if (!cat.getAdditivity() && cat != Logger.getRootLogger()) {
            StringBuffer stringBuffer6 = new StringBuffer();
            stringBuffer6.append("log4j.additivity.");
            stringBuffer6.append(cat.getName());
            stringBuffer6.append("=false");
            out.println(stringBuffer6.toString());
        }
    }

    protected void printOptions(PrintWriter out, Object obj, String fullname) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(fullname);
        stringBuffer.append("=");
        stringBuffer.append(obj.getClass().getName());
        out.println(stringBuffer.toString());
        StringBuffer stringBuffer2 = new StringBuffer();
        stringBuffer2.append(fullname);
        stringBuffer2.append(".");
        PropertyGetter.getProperties(obj, this, stringBuffer2.toString());
    }

    @Override // org.apache.log4j.config.PropertyGetter.PropertyCallback
    public void foundProperty(Object obj, String prefix, String name, Object value) {
        if ((obj instanceof Appender) && "name".equals(name)) {
            return;
        }
        if (this.doCapitalize) {
            name = capitalize(name);
        }
        PrintWriter printWriter = this.out;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(prefix);
        stringBuffer.append(name);
        stringBuffer.append("=");
        stringBuffer.append(value.toString());
        printWriter.println(stringBuffer.toString());
    }

    public static String capitalize(String name) {
        if (Character.isLowerCase(name.charAt(0)) && (name.length() == 1 || Character.isLowerCase(name.charAt(1)))) {
            StringBuffer newname = new StringBuffer(name);
            newname.setCharAt(0, Character.toUpperCase(name.charAt(0)));
            return newname.toString();
        }
        return name;
    }

    public static void main(String[] args) {
        new PropertyPrinter(new PrintWriter(System.out));
    }
}
