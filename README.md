# Query Engine App
The project was created for master module **"Event Processing"** of Humboldt University Berlin.  
The project explores the topic of **"Out-of-order handling in CEP (POG-based solution)"**.
As a basis was taken the article *"Sequence Pattern Query Processing over Out-of-Order Event Streams"*[[1]](#1).

### General Setting
The project consists of two parts: an **Event Generator App** and **Query Engine App**.  
To simplify the task, we decided to keep them separated instead of generating and processing events in real time.  
The **Event Generator App** produce a stream of events of different Type (from A to J), which also contains POGs, 
and saves it in JSON-format.
We can vary the number of negative and out-of-order events in the stream using the settings in the Generator App.
We can also vary a POG percentage in generated data.   
The **Query Engine App** imports a produced by Event Generator App json file and process it one by one with given query and time 
window.

#### Query input
Query should be given as App argument.  
The supported query format is  **"window_time eventType,!eventType,eventType"**
( *"40 A,!B,C,D"*)", where "!" used for negative events.  
Time should be given in seconds, and event type should be an upper case
letter from A to J.

### Description
**POGSeq**. 
For each event type, we keep the biggest generation timestamp sufficient  
due to the POG ordering assumption made in the article.
Like in the article[[1]](#1) we created an array called POGSeq to hold the resulting POG with 
one array position for each event type in the query . Unlike the article[[1]](#1) we use one
POG Array for negative and positive events in the query to enable simplify add operation 
not depending of whether positive or negative (according query) event  is arrived.
We always apply both operation to check if a sequence matches to a query pattern.

Each update in POGSeq trigger an purge operation. Purge implemented according Algorithm 2 of article[[1]](#1)
 
The main difference between adding in-order and out-of-order event is that 
if we adding an out of order event, we need to insert it into the list of actual events (and 
in the sequence corresponding to the query if exists) in accordance with its e_i.ts (that means we add 
one sort operation for list).

**Average App Latency computation**.
In the original work, the latency of the application was calculated as 
<a href="https://www.codecogs.com/eqnedit.php?latex=\frac{&space;\sum&space;Seq_{i}.ts&space;-&space;max(e_{i}.ats)}{NumResult}" target="_blank"><img src="https://latex.codecogs.com/gif.latex?\frac{&space;\sum&space;Seq_{i}.ts&space;-&space;max(e_{i}.ts)}{NumResult}" title="\frac{ \sum Seq_{i}.ts - max(e_{i}.ts)}{NumResult}" /></a>.  
Where <a href="https://www.codecogs.com/eqnedit.php?latex=Seq_{i}.ts" target="_blank"><img src="https://latex.codecogs.com/gif.latex?Seq_{i}.ts" title="Seq_{i}.ts" /></a> is Sequence output time, <a href="https://www.codecogs.com/eqnedit.php?latex=max_{i}.ats" target="_blank"><img src="https://latex.codecogs.com/gif.latex?max_{i}.ats" title="max_{i}.ats" /></a> is actual maximum of event arrival time and
and *NumResult* is total number of processed events.  
Since in the presented simulation we do not process events in real time, 
we use the local time when the event is transmitted to the *SeqState* for processing <a href="https://www.codecogs.com/eqnedit.php?latex=e_{i}.lts" target="_blank"><img src="https://latex.codecogs.com/gif.latex?e_{i}.lts" title="e_{i}.lts" /></a> instead of the <a href="https://www.codecogs.com/eqnedit.php?latex=max_{i}.ats" target="_blank"><img src="https://latex.codecogs.com/gif.latex?max_{i}.ats" title="max_{i}.ats" /></a>.   
Thus, we calculate the latency as follows <a href="https://www.codecogs.com/eqnedit.php?latex=\frac{\sum&space;Seq_{i}.ts&space;-&space;e_{i}.lts}{NumResult}" target="_blank"><img src="https://latex.codecogs.com/gif.latex?\frac{\sum&space;Seq_{i}.ts&space;-&space;e_{i}.lts}{NumResult}" title="\frac{\sum Seq_{i}.ts - e_{i}.lts}{NumResult}" /></a>  
We also calculate latency in microseconds, 
and not in milliseconds as in the base article, 
since the shown difference in milliseconds is not significant.


## References
<a id="1">[1]</a> 
M. Liu, M. Li, E. Rundensteiner, D. Golovnya and K. Claypool,  *"Sequence Pattern Query Processing over Out-of-Order Event Streams,"* in 2013 IEEE 29th International Conference on Data Engineering (ICDE), null, 2009 pp. 784-795.
doi: 10.1109/ICDE.2009.95