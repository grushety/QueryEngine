package domain;

import java.time.Instant;

public class Event extends StreamObject {
    private int id;

    public Event(int id, EventType type, Instant ts, Instant ats){
        super(type, ts, ats, false);
        this.id = id;
    }

    public int getId(){
        return id;
    }
}
