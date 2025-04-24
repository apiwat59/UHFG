package jxl;

import jxl.biff.HeaderFooter;

/* loaded from: classes.dex */
public final class HeaderFooter extends jxl.biff.HeaderFooter {

    public static class Contents extends HeaderFooter.Contents {
        Contents() {
        }

        Contents(String s) {
            super(s);
        }

        Contents(Contents copy) {
            super(copy);
        }

        @Override // jxl.biff.HeaderFooter.Contents
        public void append(String txt) {
            super.append(txt);
        }

        @Override // jxl.biff.HeaderFooter.Contents
        public void toggleBold() {
            super.toggleBold();
        }

        @Override // jxl.biff.HeaderFooter.Contents
        public void toggleUnderline() {
            super.toggleUnderline();
        }

        @Override // jxl.biff.HeaderFooter.Contents
        public void toggleItalics() {
            super.toggleItalics();
        }

        @Override // jxl.biff.HeaderFooter.Contents
        public void toggleStrikethrough() {
            super.toggleStrikethrough();
        }

        @Override // jxl.biff.HeaderFooter.Contents
        public void toggleDoubleUnderline() {
            super.toggleDoubleUnderline();
        }

        @Override // jxl.biff.HeaderFooter.Contents
        public void toggleSuperScript() {
            super.toggleSuperScript();
        }

        @Override // jxl.biff.HeaderFooter.Contents
        public void toggleSubScript() {
            super.toggleSubScript();
        }

        @Override // jxl.biff.HeaderFooter.Contents
        public void toggleOutline() {
            super.toggleOutline();
        }

        @Override // jxl.biff.HeaderFooter.Contents
        public void toggleShadow() {
            super.toggleShadow();
        }

        @Override // jxl.biff.HeaderFooter.Contents
        public void setFontName(String fontName) {
            super.setFontName(fontName);
        }

        @Override // jxl.biff.HeaderFooter.Contents
        public boolean setFontSize(int size) {
            return super.setFontSize(size);
        }

        @Override // jxl.biff.HeaderFooter.Contents
        public void appendPageNumber() {
            super.appendPageNumber();
        }

        @Override // jxl.biff.HeaderFooter.Contents
        public void appendTotalPages() {
            super.appendTotalPages();
        }

        @Override // jxl.biff.HeaderFooter.Contents
        public void appendDate() {
            super.appendDate();
        }

        @Override // jxl.biff.HeaderFooter.Contents
        public void appendTime() {
            super.appendTime();
        }

        @Override // jxl.biff.HeaderFooter.Contents
        public void appendWorkbookName() {
            super.appendWorkbookName();
        }

        @Override // jxl.biff.HeaderFooter.Contents
        public void appendWorkSheetName() {
            super.appendWorkSheetName();
        }

        @Override // jxl.biff.HeaderFooter.Contents
        public void clear() {
            super.clear();
        }

        @Override // jxl.biff.HeaderFooter.Contents
        public boolean empty() {
            return super.empty();
        }
    }

    public HeaderFooter() {
    }

    public HeaderFooter(HeaderFooter hf) {
        super(hf);
    }

    public HeaderFooter(String s) {
        super(s);
    }

    @Override // jxl.biff.HeaderFooter
    public String toString() {
        return super.toString();
    }

    public Contents getRight() {
        return (Contents) super.getRightText();
    }

    public Contents getCentre() {
        return (Contents) super.getCentreText();
    }

    public Contents getLeft() {
        return (Contents) super.getLeftText();
    }

    @Override // jxl.biff.HeaderFooter
    public void clear() {
        super.clear();
    }

    @Override // jxl.biff.HeaderFooter
    protected HeaderFooter.Contents createContents() {
        return new Contents();
    }

    @Override // jxl.biff.HeaderFooter
    protected HeaderFooter.Contents createContents(String s) {
        return new Contents(s);
    }

    @Override // jxl.biff.HeaderFooter
    protected HeaderFooter.Contents createContents(HeaderFooter.Contents c) {
        return new Contents((Contents) c);
    }
}
