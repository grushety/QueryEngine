package domain;

import domain.event.Event;
import domain.event.StreamObject;
import domain.query.Query;
import operators.Operators;

import java.util.*;
import java.util.stream.Collectors;

public class SeqState {
    private List<Event> fullList = new ArrayList<>();
    private Map<Integer, List<Event>> sequenceList = new HashMap<>();
    private List<Integer> ids = new ArrayList<>();
    private Query query;

    public SeqState(List<Event> events, Query query) {
        if (events != null) {
            events.sort(StreamObject.getTsComparator());
            this.setIds();
            this.setSequenceList();
        }
        this.fullList = events;
        this.query = query;
    }

    public List<Integer> getIds() {
        return ids;
    }

    private void setIds() {
        this.ids = new ArrayList<>(new HashSet<>(fullList.stream().map(Event::getId).collect(Collectors.toList())));
    }

    public List<Event> getFullList() {
        return fullList;
    }

    public void setFullList(List<Event> events) {
        this.fullList = events;
    }

    public Map<Integer, List<Event>> getSequenceList() {
        return sequenceList;
    }

    public void setSequenceList() {
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
        sequenceList = seqState;
    }

    public void addOutOfOrderEvent(Event event) {
        int id = event.getId();
        if (!ids.contains(id)) {
            ids.add(id);
        }
        fullList.add(event);
        if (sequenceList.containsKey(id)) {
            List<Event> filteredEvents = sequenceList.get(id);
            filteredEvents.add(event);
            filteredEvents.sort(StreamObject.getTsComparator());
            Boolean passPositiveFilter = Operators.winSeq(filteredEvents, query.getPositivePattern());
            Boolean passNegativeFilter = Operators.winNeg(filteredEvents, query.getNegativePattern());
            if (passPositiveFilter && passNegativeFilter) {
                sequenceList.put(id, filteredEvents);
            } else {
                sequenceList.remove(id);
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
        if (sequenceList.containsKey(id)) {
            List<Event> filteredEvents = sequenceList.get(id);
            Boolean passPositiveFilter = Operators.winSeq(filteredEvents, query.getPositivePattern());
            Boolean passNegativeFilter = Operators.winNeg(filteredEvents, query.getNegativePattern());
            if (passPositiveFilter && passNegativeFilter) {
                sequenceList.put(id, filteredEvents);
            } else {
                sequenceList.remove(id);
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
            sequenceList.put(id, filteredEvents);
        }
    }

    public void updateAfterPurge(List<Event> events) {
        if(events != null) {
            events.sort(StreamObject.getTsComparator());
            this.fullList = events;
            this.setIds();
            this.setSequenceList();
        }
        else {
            setEmpty();
        }
    }

    public void setEmpty(){
        this.fullList = new ArrayList<>();
        this.ids = new ArrayList<>();
        this.sequenceList = new HashMap<>();
    }
}
