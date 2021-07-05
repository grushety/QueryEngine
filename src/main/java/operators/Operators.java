package operators;

import domain.POGSeq;
import domain.event.Event;
import domain.event.EventType;
import domain.event.POG;
import domain.query.PatternItem;
import domain.query.Query;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    public static List<Event> purge(List<Event> events, POGSeq pogSeq, Query query){
        List<Event> copy = new ArrayList<>(events);
        for (Event event: events){
            if (isToPurge(events, pogSeq, query, event)){
                copy.remove(event);
            }
        }
        return copy;
    }

    public static boolean isToPurge(List<Event> events, POGSeq pogSeq, Query query, Event event){
        Instant start = event.getTs().minus(query.getWindow());
        Instant end = event.getTs();
        int index = query.getEventIndexInPattern(event);
        if(index >= 0) {
            Set<EventType> typesBefore = query.getEventTypesBefore(index);
            Set<EventType> typesAfter = query.getEventTypesAfter(index);
            // for POGs of all type before this Event Type
            List<POG> beforePOGs = pogSeq.filterPOGSeq(typesBefore);
            List<POG> afterPOGs = pogSeq.filterPOGSeq(typesAfter);
            for (POG pog : beforePOGs) {
                if (pog.getTs().isAfter(end)) {
                    Event relevantEventBefore = findIfTypeInWindow(start, end, pog.getType(), events);
                    if (relevantEventBefore == null) {
                        return true;
                    } else {
                        start = relevantEventBefore.getTs();
                    }
                }
            }
            start = event.getTs();
            end= event.getTs().plus(query.getWindow());
            // for POG of all type after this Event Type
            for (POG pog : afterPOGs) {
                if (pog.getTs().isAfter(end)) {
                    Event relevantEventAfter = findIfTypeInWindow(start, end, pog.getType(), events);
                    if (relevantEventAfter == null) {
                        return true;
                    }
                    else {
                        start = relevantEventAfter.getTs();
                    }
                }
            }
            return false;
        }
        return true;
    }

    private static Event findIfTypeInWindow(Instant start, Instant end, EventType type, List<Event> events){
        Optional<Event> resultEvent = events
                .stream()
                .filter(it-> it.getType().equals(type)&&it.getTs().isAfter(start) && end.isAfter(it.getTs()))
                .findFirst();
        return resultEvent.orElse(null);
    }

    public boolean isInWindow(Instant start, Instant end, Event event){
        return event.getTs().isAfter(start) && end.isAfter(event.getTs());
    }
}
