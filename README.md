# Demo Web Socket

## Objectives
A simple Java Websocket Server and Client with a minimal javascript web client.

## Building
```shell script
mvn clean package
```

## Running
### Java
By default, the server and the client bind on localhost:8025

Run the server
```shell script
java -jar target/sample-websocket-2.0-SNAPSHOT-withdependencies.jar
```

Run one or mode clients
```shell script
java -cp target/sample-websocket-2.0-SNAPSHOT-withdependencies.jar fr.univtln.bruno.samples.websocket.client.Client
```

