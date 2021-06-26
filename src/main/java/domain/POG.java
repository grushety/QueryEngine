package domain;

import java.time.Instant;

public class POG extends StreamObject {

    public POG(EventType type, Instant ts, Instant ats){
        super(type, ts, ats, true);
    }
}
