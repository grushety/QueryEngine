package domain.event;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;

public class StreamObject{
    private EventType type;
    private Instant ts; // generation timestamp
    private Instant ats; // arrival timestamp
    private boolean pog;

    StreamObject(EventType type, Instant ts, Instant ats, boolean pog){
        this.type = type;
        this.ts = ts;
        this.ats = ats;
        this.pog = pog;
    }

    public EventType getType(){
        return type;
    }
    public Instant getTs(){
        return ts;
    }
    public void setTs(Instant ts){
        this.ts = ts;
    }
    public Instant getAts(){
        return ats;
    }
    public void setAts(Instant ats){
        this.ats = ats;
    }
    public boolean isPog(){
        return pog;
    }

    public long getDelay(){
        Duration delay = Duration.between(ts, ats);
        return delay.toSeconds();
    }

    public static Comparator<StreamObject> getAtsComparator() {
        return new Comparator<StreamObject>() {
            public int compare(StreamObject a, StreamObject b) {
                return a.getAts().compareTo(b.getAts());
            }
        };
    }

    public static Comparator<StreamObject> getTsComparator() {
        return new Comparator<StreamObject>() {
            @Override
            public int compare(StreamObject a, StreamObject b) {
                return a.getTs().compareTo(b.getTs());
            }
        };
    }
}
