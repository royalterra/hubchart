![hubchart](src/main/webapp/images/banner_hubchart.png)

Statistics panel for the hubzilla/zot community network

Install instructions
--------------------

*Openshift platform*

Cartridges to add:

* JBoss Application Server 7 
* MySQL 5.5 
* phpMyAdmin 4.0 

Push the git whole git repository to openshift private repository.

Open phpMyAdmin and execute all *.sql scripts you find in the project root folder.

Navigate to the /admin.jsp page and define an accessKey and a seed hub.

Defining an accessKey will allow you to use the /admin.jsp page to launch a network discovery and enqueue hubs to be polled.


Hubchart jobs
-------------

discover [daily] - uses known hubs to obtain lists of yet-to-be-known hubs
enqueue [daily] - marks the order in which the known hubs will be polled (based on the last successful poll time)
poll [hourly] - consumes a part of the hubs queue every hour, the queue is emptied during the day
draw [daily] - calculates the statistic data, draws graphics and creates the rss feed
