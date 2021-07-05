package domain;

import domain.event.EventType;
import domain.event.POG;
import domain.query.Query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class POGSeq {
    private List<EventType> typesInQuery;
    private POG[] POGSeq;


    public POGSeq(Query query) {
        typesInQuery = query.getAllPatternTypes();
        POGSeq = new POG[typesInQuery.size()];
    }

    public void updatePOGs(POG pog) {
        EventType pog_type = pog.getType();
        int index = typesInQuery.indexOf(pog_type);
        if (index != -1) {
            POG currentPog = POGSeq[index];
            if (currentPog == null) {
                POGSeq[index] = pog;
            } else {
                if (pog.getTs().compareTo(currentPog.getTs()) >= 0) {
                    POGSeq[index] = pog;
                }
            }
        }
    }

    public Set<POG> filterPOGSeq(Set<EventType> eventTypeSet) {
        Set<POG> pogs = new HashSet<>();
        for (EventType type : eventTypeSet) {
            int index = typesInQuery.indexOf(type);
            POG currentPog = POGSeq[index];
            if (currentPog != null) {
                pogs.add(POGSeq[index]);
            }
        }
        return pogs;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (POG pog : POGSeq) {
            if (pog == null) {
                str.append(" null ");
            } else {
                str.append(pog.toString());
            }
        }
        return str.toString();
    }
}
