# Query Engine App

### Input Event Stream
We use the event stream generated by EventGenerator App, 
that contains event of different Type (from A to E) and POGs.

### Query input
Query can be given in format "window_time eventType, 
NOT_eventType, eventType" for e.x"10 A,NOT_B,C,D".  
Time should be given in minutes, and event type should be an upper case
letter from A to E for positive event and NOT_+  an upper case
letter from A to E for negative event.

##### Working Plan
######(a)create an system for in_order_event

1. convert input format to query format   + 
2. convert eventObject from input as POG and Event Type + 
3. create classifier that labels event as "POG", "OOO_EVENT" out_of_order, "POS_EVENT" +
4. let work window operator
    - return as actual stream for windows part of stream list
    - at each input event recalculate time window
    - sort with ts
(all POG changes schould be implemented in event 
selection from stream, bei windows operator)
5. define sel op that filter by id + 
6. define win_seq op that filter by positive event order
    - filter for initial eventType with all id
    - for each such event 
        - apply seq
        - check if there all  other in given order for each positive event
        - if yes save them in SeqState
7. devine win_neg op that filter seq state
    - for each negative event type search it in
    each SeqState in given order 
    - if yes filter the seq from SeqState
    
######(b) add pog handling