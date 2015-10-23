![hubchart](src/main/webapp/images/banner_hubchart.png)

Statistic panel for the hubzilla/zot community network

Install instructions
--------------------

** Openshift platform **

Cartridges to add:

* JBoss Application Server 7 
* MySQL 5.5 
* phpMyAdmin 4.0 

Push the git whole git repository to openshift private repository.

Open phpMyAdmin and execute all *.sql scripts you find in the project root folder.

Navigate to the /admin.jsp page and define an accessKey and a seed hub.

After the accessKey is defined the /admin.jsp will allow you to force a network discovery or a general poll.

The OpenShift `jbossas` cartridge documentation can be found at:
http://openshift.github.io/documentation/oo_cartridge_guide.html#jbossas
