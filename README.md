# Demo Web Socket

## Objectives
A simple Java Websocket Server and Client with a minimal javascript web client.

## Building
```shell script
mvn clean package
```

## Running
### Java
By default the server and the client bind on localhost:8025

Run the server
```shell script
java -jar Server/target/Server-2.0-jar-with-dependencies.jar
```

Run one or mode clients
```shell script
java -jar JavaClient/target/JavaClient-2.0-jar-with-dependencies.jar
```

Run the web client in a jetty server 
```shell script
mvn --projects WebClient jetty:run-war
```
