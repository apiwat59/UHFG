package org.apache.log4j.helpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

/* loaded from: classes.dex */
public class PatternParser {
    static final int CLASS_LOCATION_CONVERTER = 1002;
    private static final int CONVERTER_STATE = 1;
    private static final int DOT_STATE = 3;
    private static final char ESCAPE_CHAR = '%';
    static final int FILE_LOCATION_CONVERTER = 1004;
    static final int FULL_LOCATION_CONVERTER = 1000;
    static final int LEVEL_CONVERTER = 2002;
    static final int LINE_LOCATION_CONVERTER = 1003;
    private static final int LITERAL_STATE = 0;
    private static final int MAX_STATE = 5;
    static final int MESSAGE_CONVERTER = 2004;
    static final int METHOD_LOCATION_CONVERTER = 1001;
    private static final int MINUS_STATE = 2;
    private static final int MIN_STATE = 4;
    static final int NDC_CONVERTER = 2003;
    static final int RELATIVE_TIME_CONVERTER = 2000;
    static final int THREAD_CONVERTER = 2001;
    static /* synthetic */ Class class$java$text$DateFormat;
    PatternConverter head;
    protected int i;
    protected String pattern;
    protected int patternLength;
    PatternConverter tail;
    protected StringBuffer currentLiteral = new StringBuffer(32);
    protected FormattingInfo formattingInfo = new FormattingInfo();
    int state = 0;

    public PatternParser(String pattern) {
        this.pattern = pattern;
        this.patternLength = pattern.length();
    }

    private void addToList(PatternConverter pc) {
        if (this.head == null) {
            this.tail = pc;
            this.head = pc;
        } else {
            this.tail.next = pc;
            this.tail = pc;
        }
    }

    protected String extractOption() {
        int end;
        int i;
        int i2 = this.i;
        if (i2 < this.patternLength && this.pattern.charAt(i2) == '{' && (end = this.pattern.indexOf(125, this.i)) > (i = this.i)) {
            String r = this.pattern.substring(i + 1, end);
            this.i = end + 1;
            return r;
        }
        return null;
    }

    protected int extractPrecisionOption() {
        String opt = extractOption();
        int r = 0;
        if (opt == null) {
            return 0;
        }
        try {
            r = Integer.parseInt(opt);
            if (r <= 0) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("Precision option (");
                stringBuffer.append(opt);
                stringBuffer.append(") isn't a positive integer.");
                LogLog.error(stringBuffer.toString());
                return 0;
            }
            return r;
        } catch (NumberFormatException e) {
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2.append("Category option \"");
            stringBuffer2.append(opt);
            stringBuffer2.append("\" not a decimal integer.");
            LogLog.error(stringBuffer2.toString(), e);
            return r;
        }
    }

    public PatternConverter parse() {
        this.i = 0;
        while (true) {
            int i = this.i;
            if (i >= this.patternLength) {
                break;
            }
            String str = this.pattern;
            this.i = i + 1;
            char c = str.charAt(i);
            int i2 = this.state;
            if (i2 == 0) {
                int i3 = this.i;
                if (i3 == this.patternLength) {
                    this.currentLiteral.append(c);
                } else if (c == '%') {
                    char charAt = this.pattern.charAt(i3);
                    if (charAt == '%') {
                        this.currentLiteral.append(c);
                        this.i++;
                    } else if (charAt == 'n') {
                        this.currentLiteral.append(Layout.LINE_SEP);
                        this.i++;
                    } else {
                        if (this.currentLiteral.length() != 0) {
                            addToList(new LiteralPatternConverter(this.currentLiteral.toString()));
                        }
                        this.currentLiteral.setLength(0);
                        this.currentLiteral.append(c);
                        this.state = 1;
                        this.formattingInfo.reset();
                    }
                } else {
                    this.currentLiteral.append(c);
                }
            } else if (i2 == 1) {
                this.currentLiteral.append(c);
                if (c == '-') {
                    this.formattingInfo.leftAlign = true;
                } else if (c == '.') {
                    this.state = 3;
                } else if (c >= '0' && c <= '9') {
                    this.formattingInfo.min = c - '0';
                    this.state = 4;
                } else {
                    finalizeConverter(c);
                }
            } else if (i2 == 3) {
                this.currentLiteral.append(c);
                if (c >= '0' && c <= '9') {
                    this.formattingInfo.max = c - '0';
                    this.state = 5;
                } else {
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("Error occured in position ");
                    stringBuffer.append(this.i);
                    stringBuffer.append(".\n Was expecting digit, instead got char \"");
                    stringBuffer.append(c);
                    stringBuffer.append("\".");
                    LogLog.error(stringBuffer.toString());
                    this.state = 0;
                }
            } else if (i2 == 4) {
                this.currentLiteral.append(c);
                if (c >= '0' && c <= '9') {
                    FormattingInfo formattingInfo = this.formattingInfo;
                    formattingInfo.min = (formattingInfo.min * 10) + (c - '0');
                } else if (c == '.') {
                    this.state = 3;
                } else {
                    finalizeConverter(c);
                }
            } else if (i2 == 5) {
                this.currentLiteral.append(c);
                if (c >= '0' && c <= '9') {
                    FormattingInfo formattingInfo2 = this.formattingInfo;
                    formattingInfo2.max = (formattingInfo2.max * 10) + (c - '0');
                } else {
                    finalizeConverter(c);
                    this.state = 0;
                }
            }
        }
        if (this.currentLiteral.length() != 0) {
            addToList(new LiteralPatternConverter(this.currentLiteral.toString()));
        }
        return this.head;
    }

    protected void finalizeConverter(char c) {
        PatternConverter pc;
        DateFormat df;
        IllegalArgumentException illegalArgumentException = null;
        if (c == 'C') {
            pc = new ClassNamePatternConverter(this, this.formattingInfo, extractPrecisionOption());
            this.currentLiteral.setLength(0);
        } else if (c == 'F') {
            pc = new LocationPatternConverter(this, this.formattingInfo, 1004);
            this.currentLiteral.setLength(0);
        } else if (c == 'X') {
            String xOpt = extractOption();
            pc = new MDCPatternConverter(this.formattingInfo, xOpt);
            this.currentLiteral.setLength(0);
        } else if (c == 'p') {
            pc = new BasicPatternConverter(this.formattingInfo, LEVEL_CONVERTER);
            this.currentLiteral.setLength(0);
        } else if (c == 'r') {
            pc = new BasicPatternConverter(this.formattingInfo, RELATIVE_TIME_CONVERTER);
            this.currentLiteral.setLength(0);
        } else if (c == 't') {
            pc = new BasicPatternConverter(this.formattingInfo, THREAD_CONVERTER);
            this.currentLiteral.setLength(0);
        } else if (c == 'x') {
            pc = new BasicPatternConverter(this.formattingInfo, NDC_CONVERTER);
            this.currentLiteral.setLength(0);
        } else if (c == 'L') {
            pc = new LocationPatternConverter(this, this.formattingInfo, 1003);
            this.currentLiteral.setLength(0);
        } else if (c == 'M') {
            pc = new LocationPatternConverter(this, this.formattingInfo, 1001);
            this.currentLiteral.setLength(0);
        } else if (c == 'c') {
            pc = new CategoryPatternConverter(this, this.formattingInfo, extractPrecisionOption());
            this.currentLiteral.setLength(0);
        } else if (c == 'd') {
            String dOpt = extractOption();
            String dateFormatStr = dOpt != null ? dOpt : AbsoluteTimeDateFormat.ISO8601_DATE_FORMAT;
            if (dateFormatStr.equalsIgnoreCase(AbsoluteTimeDateFormat.ISO8601_DATE_FORMAT)) {
                df = new ISO8601DateFormat();
            } else if (dateFormatStr.equalsIgnoreCase(AbsoluteTimeDateFormat.ABS_TIME_DATE_FORMAT)) {
                df = new AbsoluteTimeDateFormat();
            } else if (dateFormatStr.equalsIgnoreCase(AbsoluteTimeDateFormat.DATE_AND_TIME_DATE_FORMAT)) {
                df = new DateTimeDateFormat();
            } else {
                try {
                    df = new SimpleDateFormat(dateFormatStr);
                } catch (IllegalArgumentException e) {
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("Could not instantiate SimpleDateFormat with ");
                    stringBuffer.append(dateFormatStr);
                    LogLog.error(stringBuffer.toString(), e);
                    Class cls = class$java$text$DateFormat;
                    if (cls == null) {
                        cls = class$("java.text.DateFormat");
                        class$java$text$DateFormat = cls;
                    }
                    df = (DateFormat) OptionConverter.instantiateByClassName("org.apache.log4j.helpers.ISO8601DateFormat", cls, null);
                    illegalArgumentException = e;
                }
            }
            pc = new DatePatternConverter(this.formattingInfo, df);
            this.currentLiteral.setLength(0);
        } else if (c == 'l') {
            pc = new LocationPatternConverter(this, this.formattingInfo, 1000);
            this.currentLiteral.setLength(0);
        } else if (c != 'm') {
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2.append("Unexpected char [");
            stringBuffer2.append(c);
            stringBuffer2.append("] at position ");
            stringBuffer2.append(this.i);
            stringBuffer2.append(" in conversion patterrn.");
            LogLog.error(stringBuffer2.toString());
            pc = new LiteralPatternConverter(this.currentLiteral.toString());
            this.currentLiteral.setLength(0);
        } else {
            pc = new BasicPatternConverter(this.formattingInfo, MESSAGE_CONVERTER);
            this.currentLiteral.setLength(0);
        }
        addConverter(pc);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    protected void addConverter(PatternConverter pc) {
        this.currentLiteral.setLength(0);
        addToList(pc);
        this.state = 0;
        this.formattingInfo.reset();
    }

    private static class BasicPatternConverter extends PatternConverter {
        int type;

        BasicPatternConverter(FormattingInfo formattingInfo, int type) {
            super(formattingInfo);
            this.type = type;
        }

        @Override // org.apache.log4j.helpers.PatternConverter
        public String convert(LoggingEvent event) {
            switch (this.type) {
                case PatternParser.RELATIVE_TIME_CONVERTER /* 2000 */:
                    return Long.toString(event.timeStamp - LoggingEvent.getStartTime());
                case PatternParser.THREAD_CONVERTER /* 2001 */:
                    return event.getThreadName();
                case PatternParser.LEVEL_CONVERTER /* 2002 */:
                    return event.getLevel().toString();
                case PatternParser.NDC_CONVERTER /* 2003 */:
                    return event.getNDC();
                case PatternParser.MESSAGE_CONVERTER /* 2004 */:
                    return event.getRenderedMessage();
                default:
                    return null;
            }
        }
    }

    private static class LiteralPatternConverter extends PatternConverter {
        private String literal;

        LiteralPatternConverter(String value) {
            this.literal = value;
        }

        @Override // org.apache.log4j.helpers.PatternConverter
        public final void format(StringBuffer sbuf, LoggingEvent event) {
            sbuf.append(this.literal);
        }

        @Override // org.apache.log4j.helpers.PatternConverter
        public String convert(LoggingEvent event) {
            return this.literal;
        }
    }

    private static class DatePatternConverter extends PatternConverter {
        private Date date;
        private DateFormat df;

        DatePatternConverter(FormattingInfo formattingInfo, DateFormat df) {
            super(formattingInfo);
            this.date = new Date();
            this.df = df;
        }

        @Override // org.apache.log4j.helpers.PatternConverter
        public String convert(LoggingEvent event) {
            this.date.setTime(event.timeStamp);
            try {
                String converted = this.df.format(this.date);
                return converted;
            } catch (Exception ex) {
                LogLog.error("Error occured while converting date.", ex);
                return null;
            }
        }
    }

    private static class MDCPatternConverter extends PatternConverter {
        private String key;

        MDCPatternConverter(FormattingInfo formattingInfo, String key) {
            super(formattingInfo);
            this.key = key;
        }

        @Override // org.apache.log4j.helpers.PatternConverter
        public String convert(LoggingEvent event) {
            Object val = event.getMDC(this.key);
            if (val == null) {
                return null;
            }
            return val.toString();
        }
    }

    private class LocationPatternConverter extends PatternConverter {
        private final /* synthetic */ PatternParser this$0;
        int type;

        LocationPatternConverter(PatternParser this$0, FormattingInfo formattingInfo, int type) {
            super(formattingInfo);
            this.this$0 = this$0;
            this.type = type;
        }

        @Override // org.apache.log4j.helpers.PatternConverter
        public String convert(LoggingEvent event) {
            LocationInfo locationInfo = event.getLocationInformation();
            switch (this.type) {
                case 1000:
                    return locationInfo.fullInfo;
                case 1001:
                    return locationInfo.getMethodName();
                case 1002:
                default:
                    return null;
                case 1003:
                    return locationInfo.getLineNumber();
                case 1004:
                    return locationInfo.getFileName();
            }
        }
    }

    private static abstract class NamedPatternConverter extends PatternConverter {
        int precision;

        abstract String getFullyQualifiedName(LoggingEvent loggingEvent);

        NamedPatternConverter(FormattingInfo formattingInfo, int precision) {
            super(formattingInfo);
            this.precision = precision;
        }

        @Override // org.apache.log4j.helpers.PatternConverter
        public String convert(LoggingEvent event) {
            String n = getFullyQualifiedName(event);
            if (this.precision <= 0) {
                return n;
            }
            int len = n.length();
            int end = len - 1;
            for (int i = this.precision; i > 0; i--) {
                end = n.lastIndexOf(46, end - 1);
                if (end == -1) {
                    return n;
                }
            }
            return n.substring(end + 1, len);
        }
    }

    private class ClassNamePatternConverter extends NamedPatternConverter {
        private final /* synthetic */ PatternParser this$0;

        ClassNamePatternConverter(PatternParser this$0, FormattingInfo formattingInfo, int precision) {
            super(formattingInfo, precision);
            this.this$0 = this$0;
        }

        @Override // org.apache.log4j.helpers.PatternParser.NamedPatternConverter
        String getFullyQualifiedName(LoggingEvent event) {
            return event.getLocationInformation().getClassName();
        }
    }

    private class CategoryPatternConverter extends NamedPatternConverter {
        private final /* synthetic */ PatternParser this$0;

        CategoryPatternConverter(PatternParser this$0, FormattingInfo formattingInfo, int precision) {
            super(formattingInfo, precision);
            this.this$0 = this$0;
        }

        @Override // org.apache.log4j.helpers.PatternParser.NamedPatternConverter
        String getFullyQualifiedName(LoggingEvent event) {
            return event.getLoggerName();
        }
    }
}
