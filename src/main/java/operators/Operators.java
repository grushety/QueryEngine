package operators;

import domain.event.Event;
import domain.event.EventType;
import domain.query.PatternItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Operators {
    public Operators() {
    }

    public static List<Event> select(int id, List<Event> events) {
        List<Event> filteredEvents = events.stream().filter(it -> it.getId() == id).collect(Collectors.toList());
        if (filteredEvents.isEmpty()) {
            return null;
        } else {
            return events.stream().filter(it -> it.getId() == id).collect(Collectors.toList());
        }
    }

    public static Boolean winSeq(List<Event> events, List<PatternItem> positivePatterns) {
        List<Boolean> validPattern = new ArrayList<>();
        int p = 0;
        int i = 0;
        // for each event in filtered (by id) and sorted (by ts)  event
        // check for each event in pattern if exist an event of given type in list of events in given order

        for (Event currentEvent : events) {
            PatternItem currentPattern = positivePatterns.get(p);
            i++;
            if (currentEvent.getType().equals(currentPattern.getEventType())) {
                validPattern.add(true);
                p++;
            }
            if (p>= positivePatterns.size()){
                break;
            }
        }
        return validPattern.size() == positivePatterns.size();
    }

    public static Boolean winNeg(List<Event> events, List<PatternItem> negativePatterns) {
        List<Boolean> validPattern = new ArrayList<>();
        for (PatternItem currentItem : negativePatterns) {
            List<PatternItem> posNeg = new ArrayList<>();
            if (currentItem.getBefore() != null) {
                posNeg.add(currentItem.getBefore());
            }
            posNeg.add(currentItem);
            if (currentItem.getAfter() != null) {
                posNeg.add(currentItem.getAfter());
            }
            validPattern.add(winSeq(events, posNeg));
        }
        return !validPattern.contains(true);
    }
}
