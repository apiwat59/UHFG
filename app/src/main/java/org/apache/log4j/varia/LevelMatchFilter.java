package org.apache.log4j.varia;

import org.apache.log4j.Level;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/* loaded from: classes.dex */
public class LevelMatchFilter extends Filter {
    boolean acceptOnMatch = true;
    Level levelToMatch;

    public void setLevelToMatch(String level) {
        this.levelToMatch = OptionConverter.toLevel(level, null);
    }

    public String getLevelToMatch() {
        Level level = this.levelToMatch;
        if (level == null) {
            return null;
        }
        return level.toString();
    }

    public void setAcceptOnMatch(boolean acceptOnMatch) {
        this.acceptOnMatch = acceptOnMatch;
    }

    public boolean getAcceptOnMatch() {
        return this.acceptOnMatch;
    }

    @Override // org.apache.log4j.spi.Filter
    public int decide(LoggingEvent event) {
        Level level = this.levelToMatch;
        if (level == null) {
            return 0;
        }
        boolean matchOccured = false;
        if (level.equals(event.getLevel())) {
            matchOccured = true;
        }
        if (!matchOccured) {
            return 0;
        }
        if (this.acceptOnMatch) {
            return 1;
        }
        return -1;
    }
}
