# lsmsd1
Large Scale and Multi-Structured Databases - WORKGROUP TASK 1

In order to start the application it's necessary to follow this steps.

### SERVER
1) Start MySQL Server and run the scripts "db.sql" and "populate.sql" to create and populate the database.  
2) Start the server: "java -jar bin/RistogoServer-1.0.0.jar".  
	2.1) If MySQL server has a root password pass it as an argument "-p [password]".  
	2.2) if MySQL server is listening on a port different from the default one (3306) pass it as an argument "-dbport [port]".  
	2.3) if MySQL server is running on a different host specify that passing the argument "-H [host_ip_address]".  
	2.4) In order to use the key-value database pass the argument -L.  

### CLIENT
3) Start the client: "java -jar bin/Ristogo-1.0.0.jar".  
	3.1) To start GUI pass the argument -g
