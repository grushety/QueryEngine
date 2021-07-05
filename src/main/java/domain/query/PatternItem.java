package domain.query;

import domain.event.EventType;

public class PatternItem {
    private EventType eventType;
    private PatternItem before;
    private PatternItem after;
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

    public PatternItem getBefore(){
        return before;
    }

    public void setBefore(PatternItem before){
        this.before = before;
    }

    public PatternItem getAfter(){
        return after;
    }

    public void setAfter(PatternItem after){
        this.after = after;
    }

    public String toString(){
        String str;
        if (positive) {
            str = eventType.toString();
        } else {
            str = "not " + eventType.toString();
        }
        return str;
    }

    public String toStringComplete() {
        String str = "[" + toString();
        if (getBefore() != null ){
            str = str + ", before: " + getBefore().toString();
        }
        else {
            str = str + ", before: null";
        }
        if (getAfter() != null){
            str = str + ", after: " + getAfter().toString()+ "]";
        }
        else {
            str = str + ", after: null ]";
        }
        return str;
    }
}
