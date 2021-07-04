import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
        List<Event> focusedEvent = new ArrayList<>();
        // ToDo: pog should array of actual pogs
        List<POG> pogs = new ArrayList<>();
        List<Event> outOfOrderEvents = new ArrayList<>();
        for (StreamObject streamObject : stream) {
            if (streamObject.isPog()) {
                pogs.add((POG) streamObject);
            } else {
                Event ev = (Event) streamObject;
                if (streamObject.getDelay() > max_time_in_order) {
                    outOfOrderEvents.add(ev);
                } else {
                    focusedEvent.add(ev);
                }
            }
        }
        SeqState seqState = new SeqState(null, query);
    }
}
