package jxl.biff;

import jxl.common.Logger;
import kotlin.text.Typography;

/* loaded from: classes.dex */
public abstract class HeaderFooter {
    private static final String BOLD_TOGGLE = "&B";
    private static final String CENTRE = "&C";
    private static final String DATE = "&D";
    private static final String DOUBLE_UNDERLINE_TOGGLE = "&E";
    private static final String ITALICS_TOGGLE = "&I";
    private static final String LEFT_ALIGN = "&L";
    private static final String OUTLINE_TOGGLE = "&O";
    private static final String PAGENUM = "&P";
    private static final String RIGHT_ALIGN = "&R";
    private static final String SHADOW_TOGGLE = "&H";
    private static final String STRIKETHROUGH_TOGGLE = "&S";
    private static final String SUBSCRIPT_TOGGLE = "&Y";
    private static final String SUPERSCRIPT_TOGGLE = "&X";
    private static final String TIME = "&T";
    private static final String TOTAL_PAGENUM = "&N";
    private static final String UNDERLINE_TOGGLE = "&U";
    private static final String WORKBOOK_NAME = "&F";
    private static final String WORKSHEET_NAME = "&A";
    private static Logger logger = Logger.getLogger(HeaderFooter.class);
    private Contents centre;
    private Contents left;
    private Contents right;

    protected abstract Contents createContents();

    protected abstract Contents createContents(String str);

    protected abstract Contents createContents(Contents contents);

    /* JADX INFO: Access modifiers changed from: protected */
    public static class Contents {
        private StringBuffer contents;

        protected Contents() {
            this.contents = new StringBuffer();
        }

        protected Contents(String s) {
            this.contents = new StringBuffer(s);
        }

        protected Contents(Contents copy) {
            this.contents = new StringBuffer(copy.getContents());
        }

        protected String getContents() {
            StringBuffer stringBuffer = this.contents;
            return stringBuffer != null ? stringBuffer.toString() : "";
        }

        private void appendInternal(String txt) {
            if (this.contents == null) {
                this.contents = new StringBuffer();
            }
            this.contents.append(txt);
        }

        private void appendInternal(char ch) {
            if (this.contents == null) {
                this.contents = new StringBuffer();
            }
            this.contents.append(ch);
        }

        protected void append(String txt) {
            appendInternal(txt);
        }

        protected void toggleBold() {
            appendInternal(HeaderFooter.BOLD_TOGGLE);
        }

        protected void toggleUnderline() {
            appendInternal(HeaderFooter.UNDERLINE_TOGGLE);
        }

        protected void toggleItalics() {
            appendInternal(HeaderFooter.ITALICS_TOGGLE);
        }

        protected void toggleStrikethrough() {
            appendInternal(HeaderFooter.STRIKETHROUGH_TOGGLE);
        }

        protected void toggleDoubleUnderline() {
            appendInternal(HeaderFooter.DOUBLE_UNDERLINE_TOGGLE);
        }

        protected void toggleSuperScript() {
            appendInternal(HeaderFooter.SUPERSCRIPT_TOGGLE);
        }

        protected void toggleSubScript() {
            appendInternal(HeaderFooter.SUBSCRIPT_TOGGLE);
        }

        protected void toggleOutline() {
            appendInternal(HeaderFooter.OUTLINE_TOGGLE);
        }

        protected void toggleShadow() {
            appendInternal(HeaderFooter.SHADOW_TOGGLE);
        }

        protected void setFontName(String fontName) {
            appendInternal("&\"");
            appendInternal(fontName);
            appendInternal(Typography.quote);
        }

        protected boolean setFontSize(int size) {
            String fontSize;
            if (size < 1 || size > 99) {
                return false;
            }
            if (size < 10) {
                fontSize = "0" + size;
            } else {
                fontSize = Integer.toString(size);
            }
            appendInternal(Typography.amp);
            appendInternal(fontSize);
            return true;
        }

        protected void appendPageNumber() {
            appendInternal(HeaderFooter.PAGENUM);
        }

        protected void appendTotalPages() {
            appendInternal(HeaderFooter.TOTAL_PAGENUM);
        }

        protected void appendDate() {
            appendInternal(HeaderFooter.DATE);
        }

        protected void appendTime() {
            appendInternal(HeaderFooter.TIME);
        }

        protected void appendWorkbookName() {
            appendInternal(HeaderFooter.WORKBOOK_NAME);
        }

        protected void appendWorkSheetName() {
            appendInternal(HeaderFooter.WORKSHEET_NAME);
        }

        protected void clear() {
            this.contents = null;
        }

        protected boolean empty() {
            StringBuffer stringBuffer = this.contents;
            if (stringBuffer == null || stringBuffer.length() == 0) {
                return true;
            }
            return false;
        }
    }

    protected HeaderFooter() {
        this.left = createContents();
        this.right = createContents();
        this.centre = createContents();
    }

    protected HeaderFooter(HeaderFooter hf) {
        this.left = createContents(hf.left);
        this.right = createContents(hf.right);
        this.centre = createContents(hf.centre);
    }

    protected HeaderFooter(String s) {
        if (s == null || s.length() == 0) {
            this.left = createContents();
            this.right = createContents();
            this.centre = createContents();
            return;
        }
        int leftPos = s.indexOf(LEFT_ALIGN);
        int rightPos = s.indexOf(RIGHT_ALIGN);
        int centrePos = s.indexOf(CENTRE);
        if (leftPos == -1 && rightPos == -1 && centrePos == -1) {
            this.centre = createContents(s);
        } else {
            if (leftPos != -1) {
                int endLeftPos = s.length();
                if (centrePos > leftPos) {
                    endLeftPos = centrePos;
                    if (rightPos > leftPos && endLeftPos > rightPos) {
                        endLeftPos = rightPos;
                    }
                } else if (rightPos > leftPos) {
                    endLeftPos = rightPos;
                }
                this.left = createContents(s.substring(leftPos + 2, endLeftPos));
            }
            if (rightPos != -1) {
                int endRightPos = s.length();
                if (centrePos > rightPos) {
                    endRightPos = centrePos;
                    if (leftPos > rightPos && endRightPos > leftPos) {
                        endRightPos = leftPos;
                    }
                } else if (leftPos > rightPos) {
                    endRightPos = leftPos;
                }
                this.right = createContents(s.substring(rightPos + 2, endRightPos));
            }
            if (centrePos != -1) {
                int endCentrePos = s.length();
                if (rightPos > centrePos) {
                    endCentrePos = rightPos;
                    if (leftPos > centrePos && endCentrePos > leftPos) {
                        endCentrePos = leftPos;
                    }
                } else if (leftPos > centrePos) {
                    endCentrePos = leftPos;
                }
                this.centre = createContents(s.substring(centrePos + 2, endCentrePos));
            }
        }
        if (this.left == null) {
            this.left = createContents();
        }
        if (this.centre == null) {
            this.centre = createContents();
        }
        if (this.right == null) {
            this.right = createContents();
        }
    }

    public String toString() {
        StringBuffer hf = new StringBuffer();
        if (!this.left.empty()) {
            hf.append(LEFT_ALIGN);
            hf.append(this.left.getContents());
        }
        if (!this.centre.empty()) {
            hf.append(CENTRE);
            hf.append(this.centre.getContents());
        }
        if (!this.right.empty()) {
            hf.append(RIGHT_ALIGN);
            hf.append(this.right.getContents());
        }
        return hf.toString();
    }

    protected Contents getRightText() {
        return this.right;
    }

    protected Contents getCentreText() {
        return this.centre;
    }

    protected Contents getLeftText() {
        return this.left;
    }

    protected void clear() {
        this.left.clear();
        this.right.clear();
        this.centre.clear();
    }
}
