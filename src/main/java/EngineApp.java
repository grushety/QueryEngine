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
            if (streamObject.isPog()) {
                pogSeq.updatePOGs((POG)streamObject);
                seqState.setFullList(Operators.purge(seqState.getFullList(), pogSeq, query));
            } else {
                Event ev = (Event) streamObject;
                if (streamObject.getDelay() > max_time_in_order) {
                    seqState.addOutOfOrderEvent(ev);
                    System.out.println(seqState);
                } else {
                    seqState.addOutOfOrderEvent(ev);
                    System.out.println(seqState);
                }
            }
        }
    }
}
