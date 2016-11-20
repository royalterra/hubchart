![hubchart](src/main/webapp/images/banner_hubchart.png)

Statistics panel for the hubzilla/zot community network

Install instructions
--------------------

*Openshift platform*

Cartridges to add:

* JBoss Application Server 7 
* MySQL 5.5 
* phpMyAdmin 4.0 

1. Push the cloned github repository to your openshift private repository.

2. Open phpMyAdmin and execute all *.sql scripts you find in the project root folder.

3. Navigate to the /admin.jsp page and define an accessKey and a seed hub.

4. The accessKey will allow you to use the /admin.jsp page to launch these jobs: discover, enqueue, poll and draw.


Hubchart scheduled jobs
----------------------

**discover** [daily] - obtains a list of new hubs parsing the connections of known hubs

**enqueue** [daily] - marks the order in which the known hubs will be polled (based on the last successful poll time)

**poll** [hourly] - consumes a part of the hubs queue (the queue is emptied during the day)

**draw** [daily] - calculates the statistic data, draws graphics and creates the rss feed
