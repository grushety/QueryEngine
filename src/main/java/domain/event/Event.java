package domain.event;

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

    @Override
    public String toString() {
        return "id: " +  id + ", type: "  + super.getType() + ", generation time: " + super.getTs().toString()
                + ", arrival time: " + super.getAts().toString();
    }
}
