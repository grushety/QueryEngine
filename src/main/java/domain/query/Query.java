package domain.query;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class Query {
    private List<PatternItem> eventPattern;
    private Duration window;

    public Query(List<PatternItem> eventPattern, Duration window) {
        this.window = window;
        this.eventPattern = eventPattern;
    }

    public Query(String[] input) {
        try {
            int number = Integer.parseInt(input[0]);
            this.window = Duration.ofMinutes(number);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
        String[] eventPatternArr = input[1].split(",");
        List<PatternItem> eventPattern = new LinkedList<>();
        for (String item : eventPatternArr) {
            PatternItem patternItem = new PatternItem(item);
            eventPattern.add(patternItem);
        }
        for (PatternItem pattern : eventPattern) {
            int index = eventPattern.indexOf(pattern);
            pattern.setBefore(findPreviousPositive(index, eventPattern));
            pattern.setAfter(findNextPositive(index, eventPattern));
        }
        this.eventPattern = eventPattern;
    }

    public void setEventPattern(List<PatternItem> eventPattern) {
        this.eventPattern = eventPattern;
    }

    public List<PatternItem> getEventPattern() {
        return eventPattern;
    }

    public Duration getWindow() {
        return window;
    }

    public String toString() {
        StringBuilder str = new StringBuilder("Time Window: " + window.toString() + " Event Pattern: [");
        for (PatternItem item : eventPattern) {
            str.append(item.toStringComplete()).append(",");
        }
        str.append("]");
        return str.toString();
    }

    public List<PatternItem> getPositivePattern() {
        return this.eventPattern.stream().filter(PatternItem::isPositive).collect(Collectors.toList());
    }

    public List<PatternItem> getNegativePattern() {
        return this.eventPattern.stream().filter(it -> !it.isPositive()).collect(Collectors.toList());
    }

    private PatternItem findNextPositive(int index, List<PatternItem> pattern){
        if ( index==pattern.size()){
            return null;
        }
        else {
            List<PatternItem> subPattern = pattern.subList(index + 1, pattern.size());
            Optional<PatternItem> item = subPattern.stream().filter(PatternItem::isPositive).findFirst();
            return item.orElse(null);
        }
    }

    private PatternItem findPreviousPositive(int index, List<PatternItem> pattern){
        if (index == 0){
            return null;
        }
        else {
            List<PatternItem> subPattern = new ArrayList<>(pattern.subList(0, index));
            Collections.reverse(subPattern);
            Optional<PatternItem> item = subPattern.stream().filter(PatternItem::isPositive).findFirst();
            return item.orElse(null);
        }
    }
}