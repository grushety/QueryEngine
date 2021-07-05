package domain;

import domain.event.Event;
import domain.event.StreamObject;
import domain.query.Query;
import operators.Operators;

import java.util.*;
import java.util.stream.Collectors;

public class SeqState {
    private List<Event> fullList = new ArrayList<>();
    private Map<Integer, List<Event>> matchingSequences = new HashMap<>();
    private List<Integer> ids = new ArrayList<>();
    private Query query;

    public SeqState(List<Event> events, Query query) {
        if (!events.isEmpty()) {
            events.sort(StreamObject.getTsComparator());
            this.setIds();
            this.setSequenceList();
        }
        this.fullList = events;
        this.query = query;
    }

    private void setIds() {
        this.ids = new ArrayList<>(new HashSet<>(fullList.stream().map(Event::getId).collect(Collectors.toList())));
    }

    public List<Event> getFullList() {
        return fullList;
    }

    public void setFullList(List<Event> events) {
        this.fullList = events;
        this.setIds();
        this.setSequenceList();
    }

    public Map<Integer, List<Event>> getSequenceList() {
        return matchingSequences;
    }

    private void setSequenceList() {
        Map<Integer, List<Event>> seqState = new HashMap<>();
        for (Integer id : ids) {
            List<Event> filteredEvents = Operators.select(id, fullList);
            if (filteredEvents != null) {
                Boolean passPositiveFilter = Operators.winSeq(filteredEvents, query.getPositivePattern());
                Boolean passNegativeFilter = Operators.winNeg(filteredEvents, query.getNegativePattern());
                if (passNegativeFilter && passPositiveFilter) {
                    seqState.put(id, filteredEvents);
                }
            }
        }
        matchingSequences = seqState;
    }

    public void addOutOfOrderEvent(Event event) {
        int id = event.getId();
        if (!ids.contains(id)) { ids.add(id); }
        fullList.add(event);
        if (matchingSequences.containsKey(id)) {
            List<Event> filteredEvents = matchingSequences.get(id);
            filteredEvents.add(event);
            // events need to be sorted after ts
            filteredEvents.sort(StreamObject.getTsComparator());
            Boolean passPositiveFilter = Operators.winSeq(filteredEvents, query.getPositivePattern());
            Boolean passNegativeFilter = Operators.winNeg(filteredEvents, query.getNegativePattern());
            if (passPositiveFilter && passNegativeFilter) {
                matchingSequences.put(id, filteredEvents);
            } else {
                matchingSequences.remove(id);
            }
        } else {
            fullCheck(id);
        }
    }

    public void addInOrderEvent(Event event) {
        int id = event.getId();
        if (!ids.contains(id)) {
            ids.add(id);
        }
        fullList.add(event);
        if (matchingSequences.containsKey(id)) {
            List<Event> filteredEvents = matchingSequences.get(id);
            filteredEvents.add(event);
            Boolean passPositiveFilter = Operators.winSeq(filteredEvents, query.getPositivePattern());
            Boolean passNegativeFilter = Operators.winNeg(filteredEvents, query.getNegativePattern());
            if (passPositiveFilter && passNegativeFilter) {
                matchingSequences.put(id, filteredEvents);
            } else {
                matchingSequences.remove(id);
            }
        } else {
            fullCheck(id);
        }
    }

    private void fullCheck(int id) {
        List<Event> filteredEvents = Operators.select(id, fullList);
        Boolean passPositiveFilter = Operators.winSeq(filteredEvents, query.getPositivePattern());
        Boolean passNegativeFilter = Operators.winNeg(filteredEvents, query.getNegativePattern());
        if (passPositiveFilter && passNegativeFilter) {
            matchingSequences.put(id, filteredEvents);
        }
    }

    public String toString() {
        return "Events size: " + fullList.size() + ", Map size: " + matchingSequences.size() + ", Ids: " + ids.size();
    }
}
