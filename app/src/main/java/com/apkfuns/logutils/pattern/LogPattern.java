package com.apkfuns.logutils.pattern;

import com.apkfuns.logutils.utils.Utils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public abstract class LogPattern {
    private final int count;
    private final int length;

    protected abstract String doApply(StackTraceElement stackTraceElement);

    public static class PlainLogPattern extends LogPattern {
        private final String string;

        public PlainLogPattern(int count, int length, String string) {
            super(count, length);
            this.string = string;
        }

        @Override // com.apkfuns.logutils.pattern.LogPattern
        protected String doApply(StackTraceElement caller) {
            return this.string;
        }
    }

    public static class DateLogPattern extends LogPattern {
        private final SimpleDateFormat dateFormat;

        public DateLogPattern(int count, int length, String dateFormat) {
            super(count, length);
            if (dateFormat != null) {
                this.dateFormat = new SimpleDateFormat(dateFormat);
            } else {
                this.dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
            }
        }

        @Override // com.apkfuns.logutils.pattern.LogPattern
        protected String doApply(StackTraceElement caller) {
            return this.dateFormat.format(new Date());
        }
    }

    public static class CallerLogPattern extends LogPattern {
        private int callerCount;
        private int callerLength;

        public CallerLogPattern(int count, int length, int callerCount, int callerLength) {
            super(count, length);
            this.callerCount = callerCount;
            this.callerLength = callerLength;
        }

        @Override // com.apkfuns.logutils.pattern.LogPattern
        protected String doApply(StackTraceElement caller) {
            String stackTrace;
            if (caller == null) {
                throw new IllegalArgumentException("Caller not found");
            }
            if (caller.getLineNumber() < 0) {
                stackTrace = String.format("%s#%s", caller.getClassName(), caller.getMethodName());
            } else {
                String stackTrace2 = caller.toString();
                stackTrace = String.format("%s.%s%s", caller.getClassName(), caller.getMethodName(), stackTrace2.substring(stackTrace2.lastIndexOf(40), stackTrace2.length()));
            }
            try {
                return Utils.shortenClassName(stackTrace, this.callerCount, this.callerLength);
            } catch (Exception e) {
                return e.getMessage();
            }
        }

        @Override // com.apkfuns.logutils.pattern.LogPattern
        protected boolean isCallerNeeded() {
            return true;
        }
    }

    public static class ConcatenateLogPattern extends LogPattern {
        private final List<LogPattern> patternList;

        public ConcatenateLogPattern(int count, int length, List<LogPattern> patternList) {
            super(count, length);
            this.patternList = new ArrayList(patternList);
        }

        public void addPattern(LogPattern pattern) {
            this.patternList.add(pattern);
        }

        @Override // com.apkfuns.logutils.pattern.LogPattern
        protected String doApply(StackTraceElement caller) {
            StringBuilder builder = new StringBuilder();
            for (LogPattern pattern : this.patternList) {
                builder.append(pattern.apply(caller));
            }
            return builder.toString();
        }

        @Override // com.apkfuns.logutils.pattern.LogPattern
        protected boolean isCallerNeeded() {
            for (LogPattern pattern : this.patternList) {
                if (pattern.isCallerNeeded()) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class ThreadNameLogPattern extends LogPattern {
        public ThreadNameLogPattern(int count, int length) {
            super(count, length);
        }

        @Override // com.apkfuns.logutils.pattern.LogPattern
        protected String doApply(StackTraceElement caller) {
            return Thread.currentThread().getName();
        }
    }

    private LogPattern(int count, int length) {
        this.count = count;
        this.length = length;
    }

    public final String apply(StackTraceElement caller) {
        String string = doApply(caller);
        return Utils.shorten(string, this.count, this.length);
    }

    protected boolean isCallerNeeded() {
        return false;
    }

    public static LogPattern compile(String pattern) {
        if (pattern == null) {
            return null;
        }
        try {
            return new Compiler().compile(pattern);
        } catch (Exception e) {
            return new PlainLogPattern(0, 0, pattern);
        }
    }

    public static class Compiler {
        private String patternString;
        private int position;
        private List<ConcatenateLogPattern> queue;
        public static final Pattern PERCENT_PATTERN = Pattern.compile("%%");
        public static final Pattern NEWLINE_PATTERN = Pattern.compile("%n");
        public static final Pattern CALLER_PATTERN = Pattern.compile("%([+-]?\\d+)?(\\.([+-]?\\d+))?caller(\\{([+-]?\\d+)?(\\.([+-]?\\d+))?\\})?");
        public static final Pattern DATE_PATTERN = Pattern.compile("%date(\\{(.*?)\\})?");
        public static final Pattern CONCATENATE_PATTERN = Pattern.compile("%([+-]?\\d+)?(\\.([+-]?\\d+))?\\(");
        public static final Pattern DATE_PATTERN_SHORT = Pattern.compile("%d(\\{(.*?)\\})?");
        public static final Pattern CALLER_PATTERN_SHORT = Pattern.compile("%([+-]?\\d+)?(\\.([+-]?\\d+))?c(\\{([+-]?\\d+)?(\\.([+-]?\\d+))?\\})?");
        public static final Pattern THREAD_NAME_PATTERN = Pattern.compile("%([+-]?\\d+)?(\\.([+-]?\\d+))?thread");
        public static final Pattern THREAD_NAME_PATTERN_SHORT = Pattern.compile("%([+-]?\\d+)?(\\.([+-]?\\d+))?t");

        public LogPattern compile(String string) {
            if (string == null) {
                return null;
            }
            this.position = 0;
            this.patternString = string;
            ArrayList arrayList = new ArrayList();
            this.queue = arrayList;
            arrayList.add(new ConcatenateLogPattern(0, 0, new ArrayList()));
            while (true) {
                int length = string.length();
                int i = this.position;
                if (length <= i) {
                    break;
                }
                int index = string.indexOf("%", i);
                int bracketIndex = string.indexOf(")", this.position);
                if (this.queue.size() > 1 && bracketIndex < index) {
                    List<ConcatenateLogPattern> list = this.queue;
                    list.get(list.size() - 1).addPattern(new PlainLogPattern(0, 0, string.substring(this.position, bracketIndex)));
                    ConcatenateLogPattern concatenateLogPattern = this.queue.get(r3.size() - 2);
                    List<ConcatenateLogPattern> list2 = this.queue;
                    concatenateLogPattern.addPattern(list2.remove(list2.size() - 1));
                    this.position = bracketIndex + 1;
                }
                if (index == -1) {
                    List<ConcatenateLogPattern> list3 = this.queue;
                    list3.get(list3.size() - 1).addPattern(new PlainLogPattern(0, 0, string.substring(this.position)));
                    break;
                }
                List<ConcatenateLogPattern> list4 = this.queue;
                list4.get(list4.size() - 1).addPattern(new PlainLogPattern(0, 0, string.substring(this.position, index)));
                this.position = index;
                parse();
            }
            return this.queue.get(0);
        }

        private void parse() {
            Matcher matcher = findPattern(PERCENT_PATTERN);
            if (matcher != null) {
                List<ConcatenateLogPattern> list = this.queue;
                list.get(list.size() - 1).addPattern(new PlainLogPattern(0, 0, "%"));
                this.position = matcher.end();
                return;
            }
            Matcher matcher2 = findPattern(NEWLINE_PATTERN);
            if (matcher2 != null) {
                List<ConcatenateLogPattern> list2 = this.queue;
                list2.get(list2.size() - 1).addPattern(new PlainLogPattern(0, 0, "\n"));
                this.position = matcher2.end();
                return;
            }
            Matcher findPattern = findPattern(CALLER_PATTERN);
            Matcher matcher3 = findPattern;
            if (findPattern == null) {
                Matcher findPattern2 = findPattern(CALLER_PATTERN_SHORT);
                matcher3 = findPattern2;
                if (findPattern2 == null) {
                    Matcher findPattern3 = findPattern(DATE_PATTERN);
                    Matcher matcher4 = findPattern3;
                    if (findPattern3 == null) {
                        Matcher findPattern4 = findPattern(DATE_PATTERN_SHORT);
                        matcher4 = findPattern4;
                        if (findPattern4 == null) {
                            Matcher findPattern5 = findPattern(THREAD_NAME_PATTERN);
                            Matcher matcher5 = findPattern5;
                            if (findPattern5 == null) {
                                Matcher findPattern6 = findPattern(THREAD_NAME_PATTERN_SHORT);
                                matcher5 = findPattern6;
                                if (findPattern6 == null) {
                                    Matcher matcher6 = findPattern(CONCATENATE_PATTERN);
                                    if (matcher6 != null) {
                                        int count = Integer.parseInt(matcher6.group(1) == null ? "0" : matcher6.group(1));
                                        int length = Integer.parseInt(matcher6.group(3) != null ? matcher6.group(3) : "0");
                                        this.queue.add(new ConcatenateLogPattern(count, length, new ArrayList()));
                                        this.position = matcher6.end();
                                        return;
                                    }
                                    throw new IllegalArgumentException();
                                }
                            }
                            int count2 = Integer.parseInt(matcher5.group(1) == null ? "0" : matcher5.group(1));
                            int length2 = Integer.parseInt(matcher5.group(3) != null ? matcher5.group(3) : "0");
                            List<ConcatenateLogPattern> list3 = this.queue;
                            list3.get(list3.size() - 1).addPattern(new ThreadNameLogPattern(count2, length2));
                            this.position = matcher5.end();
                            return;
                        }
                    }
                    String dateFormat = matcher4.group(2);
                    List<ConcatenateLogPattern> list4 = this.queue;
                    list4.get(list4.size() - 1).addPattern(new DateLogPattern(0, 0, dateFormat));
                    this.position = matcher4.end();
                    return;
                }
            }
            String dateFormat2 = matcher3.group(1);
            int count3 = Integer.parseInt(dateFormat2 == null ? "0" : matcher3.group(1));
            int length3 = Integer.parseInt(matcher3.group(3) == null ? "0" : matcher3.group(3));
            int countCaller = Integer.parseInt(matcher3.group(5) == null ? "0" : matcher3.group(5));
            int lengthCaller = Integer.parseInt(matcher3.group(7) != null ? matcher3.group(7) : "0");
            List<ConcatenateLogPattern> list5 = this.queue;
            list5.get(list5.size() - 1).addPattern(new CallerLogPattern(count3, length3, countCaller, lengthCaller));
            this.position = matcher3.end();
        }

        private Matcher findPattern(Pattern pattern) {
            Matcher matcher = pattern.matcher(this.patternString);
            if (matcher.find(this.position) && matcher.start() == this.position) {
                return matcher;
            }
            return null;
        }
    }

    public static class Log2FileNamePattern {
        private Date date = new Date();
        private String patternString;

        public Log2FileNamePattern(String patternString) {
            this.patternString = patternString;
        }

        public String doApply() {
            if (this.patternString == null) {
                return null;
            }
            String temp = this.patternString;
            Matcher matcher = Compiler.DATE_PATTERN_SHORT.matcher(this.patternString);
            while (matcher.find()) {
                String format = matcher.group(2);
                SimpleDateFormat dateFormat = new SimpleDateFormat(format);
                String dateString = dateFormat.format(this.date);
                temp = temp.replace(matcher.group(0), dateString);
            }
            return temp;
        }
    }
}
