package domain.query;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Query {
    private List<PatternItem> eventPattern;
    private Duration window;

    public Query(List<PatternItem> eventPattern, Duration window){
        this.window = window;
        this.eventPattern = eventPattern;
    }

    public Query(String[] input){
        try{
            int number = Integer.parseInt(input[0]);
            this.window = Duration.ofMinutes(number);
        } catch (NumberFormatException ex){
            ex.printStackTrace();
        }
        String[] eventPatternArr = input[1].split(",");
        List<PatternItem> eventPattern = new ArrayList<>();
        for (String item: eventPatternArr){
            PatternItem patternItem = new PatternItem(item);
            eventPattern.add(patternItem);
        }
        this.eventPattern = eventPattern;
    }

    public void setEventPattern(List<PatternItem> eventPattern){
        this.eventPattern = eventPattern;
    }
    public List<PatternItem> getEventPattern(){
        return eventPattern;
    }

    public Duration getWindow(){
        return window;
    }

    public String toString(){
        StringBuilder str = new StringBuilder("Time Window: " + window.toString() + " Event Pattern: [");
        for (PatternItem item:eventPattern){
            str.append(item.toString()).append(",");
        }
        str.append("]");
        return str.toString();
    }
}
