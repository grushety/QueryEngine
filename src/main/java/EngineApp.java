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
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class EngineApp {

    public static void main(String[] args) throws FileNotFoundException {
        int max_time_in_order = 10;
        List<StreamObject> stream = new ImportService().importStreamFromFile();
        Operators op = new Operators();
        Query query = new Query(args);
        System.err.println(query.toString());

        POGSeq pogSeq = new POGSeq(query);
        SeqState seqState = new SeqState(new ArrayList<>(), query);

        for (StreamObject streamObject : stream) {
            Instant inputTime = Instant.now();
            if (streamObject.isPog()) {
                pogSeq.updatePOGs((POG) streamObject);
                //System.out.println(pogSeq);
                seqState.setFullList(Operators.purge(seqState.getFullList(), pogSeq, query));

                long purgeLatency = ChronoUnit.MILLIS.between(inputTime, Instant.now());
                System.out.println("purgeLatency: " + purgeLatency);
            } else {
                Event ev = (Event) streamObject;
                if (query.isEventTypeInQuery(ev)) {
                    if (streamObject.getDelay() > max_time_in_order) {
                        seqState.addOutOfOrderEvent(ev);

                        long oooLatency = ChronoUnit.MILLIS.between(inputTime, Instant.now());
                        System.out.println("Out-of-order Latency: " + oooLatency);
                        //System.out.println(seqState);
                    } else {
                        seqState.addOutOfOrderEvent(ev);
                        long ioLatency = ChronoUnit.MILLIS.between(inputTime, Instant.now());
                        System.out.println("In Order Latency: " + ioLatency);
                        //System.out.println(seqState);
                    }
                }
            }
        }
    }
}
