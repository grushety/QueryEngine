import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

public class EngineApp {

    public static void main(String[] args) throws FileNotFoundException {
        int max_time_in_order = 5;
        List<StreamObject> stream = new ImportService().importStreamFromFile();
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
        System.out.println("sum latency: " + totalLatency);
        System.out.println("average App Latency " + totalLatency/stream.size());
    }
}
