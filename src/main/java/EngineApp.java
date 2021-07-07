import domain.POGSeq;
import domain.SeqState;
import domain.event.Event;
import domain.event.POG;
import domain.event.StreamObject;
import domain.query.Query;
import operators.Operators;
import utils.ImportService;

import java.io.FileNotFoundException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EngineApp {

    public static void main(String[] args) throws FileNotFoundException {
        int max_time_in_order = 5;
        String filename = "src/main/resources/stream_with_POGs_20_out_of_order_50_negative_20_number_of_events_1000.json";
        List<StreamObject> stream = new ImportService().importStreamFromFile(filename);
        Query query = new Query(args);
        POGSeq pogSeq = new POGSeq(query);
        SeqState seqState = new SeqState(new ArrayList<>(), query);

        double totalLatency = 0.0;
        for (StreamObject streamObject : stream) {
            Instant inputTime = Instant.now();
            if (streamObject.isPog()) {
                //if a Stream Object is POG
                pogSeq.updatePOGs((POG) streamObject);
                // then purge and update events in SeqState
                seqState.setFullList(Operators.purge(seqState.getFullList(), pogSeq, query));
            } else {
                // if a Stream Object is an Event
                Event ev = (Event) streamObject;
                if (query.isEventTypeInQuery(ev)) {
                    // if event is Out Of Order
                    if (streamObject.getDelay() > max_time_in_order) {
                        seqState.addOutOfOrderEvent(ev);
                     //if event is In Order
                    } else {
                        seqState.addInOrderEvent(ev);
                    }
                }
            }
            long latency = ChronoUnit.MICROS.between(inputTime, Instant.now());
            totalLatency += latency;
        }
        // Output statistics
        System.out.println("Query " + query);
        System.out.println("Average App Latency " + totalLatency/stream.size());
        System.out.println("SeqState content: " + seqState);
        System.out.println("Matching sequences: " + seqState.getSequenceList());

        List<StreamObject> notPogs = stream.stream().filter(it-> !it.isPog()).collect(Collectors.toList());
        List<Event> events = notPogs.stream().map(it -> (Event)it).collect(Collectors.toList());
        SeqState seqState2 = new SeqState(events, query);
        System.out.println("SeqState for all events content: " + seqState2);
        System.out.println("All matching sequences: " + seqState2.getSequenceList());
    }
}
