## About JayVee

JayVee (JV) is a Java vision library intended to fill a different purpose than the other
vision libraries typically available (OpenCV, BoofCV).  JayVee is intended to provide
a complete architecture for writing vision applications that is foolproof and performant.  
To understand the differences, take a look at how JayVee's priorities stack up against
other libraries.

---

##### Typical Priorities

1. Vertical Scaling (e.g. performance)
2. Completeness
3. Usability
4. Debuggability
5. Horizontal Scaling

##### JayVee Priorities

1. Usability
2. Debuggability
3. Horizontal Scaling
4. Vertical Scaling
5. Completeness

---

Computer vision is an extremely complex task.  By prioritizing usability and debuggability
above performance we are able to save countless hours of developer time.  In JayVee you
should never get a segmentation fault or confusing error message, algoithm preconditions
can be checked as the input comes in, and every method is completely documented with 
detailed instructions on each of the parameters and advice on setting them (we don't require
you to memorize the algorithm's journal article just to use the feature).  In the end the
hit to performance turns out to be small and is easily made up with any kind of cluster
through JayVee's automatic support for horizontal scaling.