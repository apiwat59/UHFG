package org.apache.log4j.chainsaw;

import com.apkfuns.logutils.Constant;
import java.util.StringTokenizer;
import org.apache.log4j.Level;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/* loaded from: classes.dex */
class XMLFileHandler extends DefaultHandler {
    private static final String TAG_EVENT = "log4j:event";
    private static final String TAG_LOCATION_INFO = "log4j:locationInfo";
    private static final String TAG_MESSAGE = "log4j:message";
    private static final String TAG_NDC = "log4j:NDC";
    private static final String TAG_THROWABLE = "log4j:throwable";
    private final StringBuffer mBuf = new StringBuffer();
    private String mCategoryName;
    private Level mLevel;
    private String mLocationDetails;
    private String mMessage;
    private final MyTableModel mModel;
    private String mNDC;
    private int mNumEvents;
    private String mThreadName;
    private String[] mThrowableStrRep;
    private long mTimeStamp;

    XMLFileHandler(MyTableModel aModel) {
        this.mModel = aModel;
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void startDocument() throws SAXException {
        this.mNumEvents = 0;
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void characters(char[] aChars, int aStart, int aLength) {
        this.mBuf.append(String.valueOf(aChars, aStart, aLength));
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void endElement(String aNamespaceURI, String aLocalName, String aQName) {
        if (TAG_EVENT.equals(aQName)) {
            addEvent();
            resetData();
            return;
        }
        if (TAG_NDC.equals(aQName)) {
            this.mNDC = this.mBuf.toString();
            return;
        }
        if (TAG_MESSAGE.equals(aQName)) {
            this.mMessage = this.mBuf.toString();
            return;
        }
        if (TAG_THROWABLE.equals(aQName)) {
            StringTokenizer st = new StringTokenizer(this.mBuf.toString(), "\n\t");
            String[] strArr = new String[st.countTokens()];
            this.mThrowableStrRep = strArr;
            if (strArr.length > 0) {
                strArr[0] = st.nextToken();
                int i = 1;
                while (true) {
                    String[] strArr2 = this.mThrowableStrRep;
                    if (i < strArr2.length) {
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append(Constant.SPACE);
                        stringBuffer.append(st.nextToken());
                        strArr2[i] = stringBuffer.toString();
                        i++;
                    } else {
                        return;
                    }
                }
            }
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void startElement(String aNamespaceURI, String aLocalName, String aQName, Attributes aAtts) {
        this.mBuf.setLength(0);
        if (TAG_EVENT.equals(aQName)) {
            this.mThreadName = aAtts.getValue("thread");
            this.mTimeStamp = Long.parseLong(aAtts.getValue("timestamp"));
            this.mCategoryName = aAtts.getValue("logger");
            this.mLevel = Level.toLevel(aAtts.getValue("level"));
            return;
        }
        if (TAG_LOCATION_INFO.equals(aQName)) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(aAtts.getValue("class"));
            stringBuffer.append(".");
            stringBuffer.append(aAtts.getValue("method"));
            stringBuffer.append("(");
            stringBuffer.append(aAtts.getValue("file"));
            stringBuffer.append(":");
            stringBuffer.append(aAtts.getValue("line"));
            stringBuffer.append(")");
            this.mLocationDetails = stringBuffer.toString();
        }
    }

    int getNumEvents() {
        return this.mNumEvents;
    }

    private void addEvent() {
        this.mModel.addEvent(new EventDetails(this.mTimeStamp, this.mLevel, this.mCategoryName, this.mNDC, this.mThreadName, this.mMessage, this.mThrowableStrRep, this.mLocationDetails));
        this.mNumEvents++;
    }

    private void resetData() {
        this.mTimeStamp = 0L;
        this.mLevel = null;
        this.mCategoryName = null;
        this.mNDC = null;
        this.mThreadName = null;
        this.mMessage = null;
        this.mThrowableStrRep = null;
        this.mLocationDetails = null;
    }
}
