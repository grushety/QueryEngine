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
    public Operators() {}

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
            boolean isToPurge = isToPurge(events, pogSeq, query, event);
            if (isToPurge){
                copy.remove(event);
            }
        }
        //System.out.println("PURGE: before " + events.size()+ ", after: " + copy.size());
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
            Set<POG> beforePOGs = pogSeq.filterPOGSeq(typesBefore);
            Set<POG> afterPOGs = pogSeq.filterPOGSeq(typesAfter);
            for (POG pog : beforePOGs) {
                if (pog.getTs().isAfter(end)) {
                    Optional<Event> relevantEventBefore = findIfTypeInWindow(start, end, pog.getType(), events);
                    if (relevantEventBefore.isEmpty()) {
                        return true;
                    } else {
                        start = relevantEventBefore.get().getTs();
                    }
                }
            }
            start = event.getTs();
            end= event.getTs().plus(query.getWindow());
            // for POG of all type after this Event Type
            for (POG pog : afterPOGs) {
                if (pog.getTs().isAfter(end)) {
                    Optional<Event> relevantEventAfter = findIfTypeInWindow(start, end, pog.getType(), events);
                    if (relevantEventAfter.isEmpty()) {
                        return true;
                    }
                    else {
                        start = relevantEventAfter.get().getTs();
                    }
                }
            }
            return false;
        }
        return false;
    }

    private static Optional<Event> findIfTypeInWindow(Instant start, Instant end, EventType type, List<Event> events){
        return events.stream()
                .filter(it-> it.getType().equals(type)&&it.getTs().isAfter(start) && end.isAfter(it.getTs()))
                .findFirst();
    }

    public boolean isInWindow(Instant start, Instant end, Event event){
        return event.getTs().isAfter(start) && end.isAfter(event.getTs());
    }
}
