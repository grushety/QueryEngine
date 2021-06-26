import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import domain.StreamObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;

public class EngineApp {

    public static void main(String[] args) throws FileNotFoundException {
        final Type TYPE = new TypeToken<List<StreamObject>>() {}.getType();
        String filename = "src/main/resources/stream_with_POGs_20_out_of_order_30_negative_30_number_of_events_1000.json";
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader(filename));
        List<StreamObject> stream = gson.fromJson(reader, TYPE);
        System.out.println("Length of the stream is " + stream.size()+ "objects");
        // Just to check if it is worked
        //Gson gson1 = new GsonBuilder().setPrettyPrinting().create();
        //String json = gson1.toJson(stream); // converts to json
        //System.out.println(json);
    }
}
