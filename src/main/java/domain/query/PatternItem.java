package domain.query;

import domain.event.EventType;

public class PatternItem {
    private EventType eventType;
    private boolean positive;

    public PatternItem(EventType eventType, boolean positive) {
        this.eventType = eventType;
        this.positive = positive;
    }

    public PatternItem(String input) throws IllegalArgumentException {
        if (input.charAt(0) == '!' && input.length() > 1) {
            this.positive = false;
            input = input.substring(1);
        } else {
            this.positive = true;
        }
        this.eventType = EventType.valueOf(input);
    }

    public EventType getEventType() {
        return eventType;
    }

    public boolean isPositive() {
        return positive;
    }

    public String toString() {
        String str;
        if (positive) {
            str = "";
        } else {
            str = "not ";
        }
        return str + eventType.toString();
    }
}
