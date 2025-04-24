package org.apache.log4j.chainsaw;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/* loaded from: classes.dex */
class LoadXMLAction extends AbstractAction {
    private static final Logger LOG;
    static /* synthetic */ Class class$org$apache$log4j$chainsaw$LoadXMLAction;
    private final JFileChooser mChooser;
    private final XMLFileHandler mHandler;
    private final JFrame mParent;
    private final XMLReader mParser;

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        Class cls = class$org$apache$log4j$chainsaw$LoadXMLAction;
        if (cls == null) {
            cls = class$("org.apache.log4j.chainsaw.LoadXMLAction");
            class$org$apache$log4j$chainsaw$LoadXMLAction = cls;
        }
        LOG = Logger.getLogger(cls);
    }

    LoadXMLAction(JFrame aParent, MyTableModel aModel) throws SAXException, ParserConfigurationException {
        JFileChooser jFileChooser = new JFileChooser();
        this.mChooser = jFileChooser;
        jFileChooser.setMultiSelectionEnabled(false);
        jFileChooser.setFileSelectionMode(0);
        this.mParent = aParent;
        XMLFileHandler xMLFileHandler = new XMLFileHandler(aModel);
        this.mHandler = xMLFileHandler;
        XMLReader xMLReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
        this.mParser = xMLReader;
        xMLReader.setContentHandler(xMLFileHandler);
    }

    public void actionPerformed(ActionEvent aIgnore) {
        Logger logger = LOG;
        logger.info("load file called");
        if (this.mChooser.showOpenDialog(this.mParent) == 0) {
            logger.info("Need to load a file");
            File chosen = this.mChooser.getSelectedFile();
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("loading the contents of ");
            stringBuffer.append(chosen.getAbsolutePath());
            logger.info(stringBuffer.toString());
            try {
                int num = loadFile(chosen.getAbsolutePath());
                JFrame jFrame = this.mParent;
                StringBuffer stringBuffer2 = new StringBuffer();
                stringBuffer2.append("Loaded ");
                stringBuffer2.append(num);
                stringBuffer2.append(" events.");
                JOptionPane.showMessageDialog(jFrame, stringBuffer2.toString(), "CHAINSAW", 1);
            } catch (Exception e) {
                LOG.warn("caught an exception loading the file", e);
                JFrame jFrame2 = this.mParent;
                StringBuffer stringBuffer3 = new StringBuffer();
                stringBuffer3.append("Error parsing file - ");
                stringBuffer3.append(e.getMessage());
                JOptionPane.showMessageDialog(jFrame2, stringBuffer3.toString(), "CHAINSAW", 0);
            }
        }
    }

    private int loadFile(String aFile) throws SAXException, IOException {
        int numEvents;
        synchronized (this.mParser) {
            StringBuffer buf = new StringBuffer();
            buf.append("<?xml version=\"1.0\" standalone=\"yes\"?>\n");
            buf.append("<!DOCTYPE log4j:eventSet ");
            buf.append("[<!ENTITY data SYSTEM \"file:///");
            buf.append(aFile);
            buf.append("\">]>\n");
            buf.append("<log4j:eventSet xmlns:log4j=\"Claira\">\n");
            buf.append("&data;\n");
            buf.append("</log4j:eventSet>\n");
            InputSource is = new InputSource(new StringReader(buf.toString()));
            this.mParser.parse(is);
            numEvents = this.mHandler.getNumEvents();
        }
        return numEvents;
    }
}
