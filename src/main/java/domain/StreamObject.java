package domain;

import java.time.Instant;

public class StreamObject implements Comparable<StreamObject>{
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

    @Override
    public int compareTo(StreamObject o) {
        return getAts().compareTo(o.getAts());
    }
}
