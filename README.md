# Query Engine App
The project was created for the master module **" Event Processing"** of Humboldt University Berlin.  
The project explores the topic of **"Out-of-order handling in CEP (POG-based solution)"**.
We took as the basis the article *"Sequence Pattern Query Processing over Out-of-Order Event Streams"*[[1]](#1).

### General Setting
The project consists of an **Event Generator App**[[2]](#2) and **Query Engine App**.  
To simplify the task, we decided to make the event processing off-line instead of generating and processing events in real-time.  
The **Event Generator App** produces a stream of events of different types (from A to J) and POG elements.
The produced stream then is saved using JSON format.
We can vary the number of negative and out-of-order events in the stream using the settings in the Generator App.
We can also vary the POG percentage in generated data.   
The **Query Engine App** imports a produced JSON file and process Stream Objects from it with a given query and time window.

#### Query input
A query should be given as an App argument.  
The supported query format is  **"window_time eventType,!eventType,eventType"** ( *"40 A,!B,C,D"*)",  where "!" used for negative events.  
The user should give time in seconds, and the event type should be an upper case letter from A to J.

### Description
**POGSeq**. 
In the POGSeq data structure, we keep a POG element with the biggest generation timestamp for each event type.  Due to the POG ordering assumption made in the article[[1]](#1), it should be sufficient.
POGSeq is organized as an Array to hold the resulting POG with one array position for each event type in the query. Unlike the article[[1]](#1), we have used one POG Array for negative and positive events in the query to simplify add-operation ( it not depends on whether positive or negative (according to query) event has arrived).
We always apply both operations to check if a sequence matches a query pattern.
Each update in POGSeq triggers a purge operation. Purge implemented according to Algorithm 2 of article[[1]](#1)
 
**SeqState**  
A SeqState is a fundamental data structure containing an actual stack of events on which basic operations are performed: adding an element, purging the stack of old events and searching sequences that match the pattern in the query.  
Also, SeqState contains a list of all found matching sequences for the current event stack.

**In Order Event**  
In-order-event initiates a search of matching sequences only if the event has the same type as the last positive pattern event in the request. In other cases, an event will be added
to the actual events storage of SeqState (suitable for the time window).

**Out of Order Event**  
An out of order event always initiates a search for matching sequences. An event will be inserted into the list of actual events by its time of occurrence.

**Average App Latency computation**.
In the original work, the latency of the application was calculated as 
<a href="https://www.codecogs.com/eqnedit.php?latex=\frac{&space;\sum&space;Seq_{i}.ts&space;-&space;max(e_{i}.ats)}{NumResult}" target="_blank"><img src="https://latex.codecogs.com/gif.latex?\frac{&space;\sum&space;Seq_{i}.ts&space;-&space;max(e_{i}.ts)}{NumResult}" title="\frac{ \sum Seq_{i}.ts - max(e_{i}.ts)}{NumResult}" /></a>.  
Where <a href="https://www.codecogs.com/eqnedit.php?latex=Seq_{i}.ts" target="_blank"><img src="https://latex.codecogs.com/gif.latex?Seq_{i}.ts" title="Seq_{i}.ts" /></a> is Sequence output time, <a href="https://www.codecogs.com/eqnedit.php?latex=max_{i}.ats" target="_blank"><img src="https://latex.codecogs.com/gif.latex?max_{i}.ats" title="max_{i}.ats" /></a> is actual maximum of event arrival time and and *NumResult* is total number of processed events.  
Since in the presented simulation we do not process events in real time,  we use the local time when the event is transmitted to the *SeqState* for processing <a href="https://www.codecogs.com/eqnedit.php?latex=e_{i}.lts" target="_blank"><img src="https://latex.codecogs.com/gif.latex?e_{i}.lts" title="e_{i}.lts" /></a> instead of the <a href="https://www.codecogs.com/eqnedit.php?latex=max_{i}.ats" target="_blank"><img src="https://latex.codecogs.com/gif.latex?max_{i}.ats" title="max_{i}.ats" /></a>.   
Thus, we calculate the latency as follows <a href="https://www.codecogs.com/eqnedit.php?latex=\frac{\sum&space;Seq_{i}.ts&space;-&space;e_{i}.lts}{NumResult}" target="_blank"><img src="https://latex.codecogs.com/gif.latex?\frac{\sum&space;Seq_{i}.ts&space;-&space;e_{i}.lts}{NumResult}" title="\frac{\sum Seq_{i}.ts - e_{i}.lts}{NumResult}" /></a>  
We also calculate latency in microseconds (not in milliseconds as in [[1]](#1)) since the recorded difference in milliseconds is not significant.

We have implemented the presented Partial-Order-Guarantee-based solution (conservative method)  using java version "1.8.0_201".  
Experiments are run on a machine with AMD Ryzen 3 2200U Prozessor and 8 GB RAM.

## References
<a id="1">[1]</a> 
M. Liu, M. Li, E. Rundensteiner, D. Golovnya and K. Claypool,  *" Sequence Pattern Query Processing over Out-of-Order Event Streams,"* in 2013 IEEE 29th International Conference on Data Engineering (ICDE), null, 2009 pp. 784-795.
DOI: 10.1109/ICDE.2009.95  
<a id="2">[2]</a>
*Event Generator App*: https://github.com/grushety/EventGenerator
