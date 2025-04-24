package com.gg.reader.api.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kotlin.text.Typography;

/* loaded from: classes.dex */
public class JsonReader {
    public static final int CURRENT = 1;
    public static final int FIRST = 0;
    public static final int NEXT = 2;
    private static Map<Character, Character> escapes;
    private StringBuffer buf = new StringBuffer();
    private char c;
    private CharacterIterator it;
    private Object token;
    private static final Object OBJECT_END = new Object();
    private static final Object ARRAY_END = new Object();
    private static final Object COLON = new Object();
    private static final Object COMMA = new Object();

    static {
        HashMap hashMap = new HashMap();
        escapes = hashMap;
        Character valueOf = Character.valueOf(Typography.quote);
        hashMap.put(valueOf, valueOf);
        escapes.put('\\', '\\');
        escapes.put('/', '/');
        escapes.put('b', '\b');
        escapes.put('f', '\f');
        escapes.put('n', '\n');
        escapes.put('r', '\r');
        escapes.put('t', '\t');
    }

    private char next() {
        char next = this.it.next();
        this.c = next;
        return next;
    }

    private void skipWhiteSpace() {
        while (Character.isWhitespace(this.c)) {
            next();
        }
    }

    public Object read(CharacterIterator ci, int start) {
        this.it = ci;
        if (start == 0) {
            this.c = ci.first();
        } else if (start == 1) {
            this.c = ci.current();
        } else if (start == 2) {
            this.c = ci.next();
        }
        return read();
    }

    public Object read(CharacterIterator it) {
        return read(it, 2);
    }

    public Object read(String string) {
        return read(new StringCharacterIterator(string), 0);
    }

    private Object read() {
        skipWhiteSpace();
        char ch = this.c;
        next();
        if (ch == '\"') {
            this.token = string();
        } else if (ch == ',') {
            this.token = COMMA;
        } else if (ch == ':') {
            this.token = COLON;
        } else if (ch == '[') {
            this.token = array();
        } else if (ch == ']') {
            this.token = ARRAY_END;
        } else if (ch == 'f') {
            next();
            next();
            next();
            next();
            this.token = Boolean.FALSE;
        } else if (ch == 'n') {
            next();
            next();
            next();
            this.token = null;
        } else if (ch == 't') {
            next();
            next();
            next();
            this.token = Boolean.TRUE;
        } else if (ch == '{') {
            this.token = object();
        } else if (ch == '}') {
            this.token = OBJECT_END;
        } else {
            char previous = this.it.previous();
            this.c = previous;
            if (Character.isDigit(previous) || this.c == '-') {
                this.token = number();
            }
        }
        return this.token;
    }

    private Object object() {
        Map<Object, Object> ret = new HashMap<>();
        Object key = read();
        while (true) {
            Object obj = this.token;
            Object obj2 = OBJECT_END;
            if (obj != obj2) {
                read();
                if (this.token != obj2) {
                    ret.put(key, read());
                    if (read() == COMMA) {
                        key = read();
                    }
                }
            } else {
                return ret;
            }
        }
    }

    private Object array() {
        List<Object> ret = new ArrayList<>();
        Object value = read();
        while (this.token != ARRAY_END) {
            ret.add(value);
            if (read() == COMMA) {
                value = read();
            }
        }
        return ret;
    }

    private Object number() {
        boolean isFloatingPoint = false;
        this.buf.setLength(0);
        if (this.c == '-') {
            add();
        }
        int length = 0 + addDigits();
        if (this.c == '.') {
            add();
            length += addDigits();
            isFloatingPoint = true;
        }
        char c = this.c;
        if (c == 'e' || c == 'E') {
            add();
            char c2 = this.c;
            if (c2 == '+' || c2 == '-') {
                add();
            }
            addDigits();
            isFloatingPoint = true;
        }
        String s = this.buf.toString();
        return isFloatingPoint ? length < 17 ? Double.valueOf(s) : new BigDecimal(s) : length < 19 ? Long.valueOf(s) : new BigInteger(s);
    }

    private int addDigits() {
        int ret = 0;
        while (Character.isDigit(this.c)) {
            add();
            ret++;
        }
        return ret;
    }

    private Object string() {
        this.buf.setLength(0);
        while (true) {
            char c = this.c;
            if (c != '\"') {
                if (c == '\\') {
                    next();
                    char c2 = this.c;
                    if (c2 == 'u') {
                        add(unicode());
                    } else {
                        Object value = escapes.get(Character.valueOf(c2));
                        if (value != null) {
                            add(((Character) value).charValue());
                        }
                    }
                } else {
                    add();
                }
            } else {
                next();
                return this.buf.toString();
            }
        }
    }

    private void add(char cc) {
        this.buf.append(cc);
        next();
    }

    private void add() {
        add(this.c);
    }

    private char unicode() {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            char next = next();
            switch (next) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    value = ((value << 4) + this.c) - 48;
                    break;
                default:
                    switch (next) {
                        case 'A':
                        case 'B':
                        case 'C':
                        case 'D':
                        case 'E':
                        case 'F':
                            value = ((value << 4) + this.c) - 75;
                            break;
                        default:
                            switch (next) {
                                case 'a':
                                case 'b':
                                case 'c':
                                case 'd':
                                case 'e':
                                case 'f':
                                    value = ((value << 4) + this.c) - 107;
                                    break;
                            }
                    }
            }
        }
        return (char) value;
    }

    public <T> T jsonToClass(String json, Class<T> t) {
        T newInstance = null;
        try {
            Map map = (Map) read(json);
            if (map.size() > 0) {
                newInstance = t.newInstance();
                Field[] field = t.getDeclaredFields();
                for (int i = 0; i < field.length; i++) {
                    field[i].setAccessible(true);
                    String name = field[i].getName();
                    String name2 = name.replaceFirst(name.substring(0, 1), name.substring(0, 1).toUpperCase());
                    if (map.get(name).getClass().getName().equals("java.lang.String")) {
                        Method m = newInstance.getClass().getMethod("set" + name2, String.class);
                        m.invoke(newInstance, map.get(name));
                    }
                    if (map.get(name).getClass().getName().equals("java.lang.Integer")) {
                        Method m2 = newInstance.getClass().getMethod("set" + name2, Integer.class);
                        m2.invoke(newInstance, map.get(name));
                    }
                    if (map.get(name).getClass().getName().equals("java.lang.Long")) {
                        Method m3 = newInstance.getClass().getMethod("set" + name2, Integer.TYPE);
                        m3.invoke(newInstance, Integer.valueOf(((Long) map.get(name)).intValue()));
                    }
                }
            }
            return newInstance;
        } catch (Exception e) {
            return null;
        }
    }
}
