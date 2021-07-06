package utils;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import domain.event.Event;
import domain.event.POG;
import domain.event.StreamObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ImportService {
    public ImportService(){}

    public List<StreamObject> importStreamFromFile(String filename) throws FileNotFoundException {
        List<StreamObject> stream = new ArrayList<>();
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader(filename));
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(reader).getAsJsonArray();
        for(JsonElement el : jsonArray )
        {
            JsonObject obj = el.getAsJsonObject();
            boolean isPog = obj.get("pog").getAsBoolean();
            if (isPog){
                POG pog = gson.fromJson( obj , POG.class);
                stream.add(pog);
            }
            else {
                Event event = gson.fromJson( obj , Event.class);
                stream.add(event);
            }
        }
        return stream;
    }
}
