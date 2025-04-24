package org.apache.log4j.varia;

import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/* loaded from: classes.dex */
public class StringMatchFilter extends Filter {
    public static final String ACCEPT_ON_MATCH_OPTION = "AcceptOnMatch";
    public static final String STRING_TO_MATCH_OPTION = "StringToMatch";
    boolean acceptOnMatch = true;
    String stringToMatch;

    public String[] getOptionStrings() {
        return new String[]{STRING_TO_MATCH_OPTION, ACCEPT_ON_MATCH_OPTION};
    }

    public void setOption(String key, String value) {
        if (key.equalsIgnoreCase(STRING_TO_MATCH_OPTION)) {
            this.stringToMatch = value;
        } else if (key.equalsIgnoreCase(ACCEPT_ON_MATCH_OPTION)) {
            this.acceptOnMatch = OptionConverter.toBoolean(value, this.acceptOnMatch);
        }
    }

    public void setStringToMatch(String s) {
        this.stringToMatch = s;
    }

    public String getStringToMatch() {
        return this.stringToMatch;
    }

    public void setAcceptOnMatch(boolean acceptOnMatch) {
        this.acceptOnMatch = acceptOnMatch;
    }

    public boolean getAcceptOnMatch() {
        return this.acceptOnMatch;
    }

    @Override // org.apache.log4j.spi.Filter
    public int decide(LoggingEvent event) {
        String str;
        String msg = event.getRenderedMessage();
        if (msg == null || (str = this.stringToMatch) == null || msg.indexOf(str) == -1) {
            return 0;
        }
        return this.acceptOnMatch ? 1 : -1;
    }
}
