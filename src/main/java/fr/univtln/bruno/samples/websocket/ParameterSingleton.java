package fr.univtln.bruno.samples.websocket;

import lombok.extern.java.Log;

import java.util.Optional;

@Log
public class ParameterSingleton {
    public static final String SERVER_IP;
    public static final int SERVER_PORT;

    static {
        SERVER_IP = Optional.ofNullable(System.getProperty("fr.univtln.bruno.demo.websocket.server.ip")).orElse("localhost");
        int port = 8025;
        try {
            port = Integer.parseInt(Optional.ofNullable(System.getProperty("fr.univtln.bruno.demo.websocket.server.port")).orElse("8025"));
        } catch (NumberFormatException e) {
            log.severe("Server port is not a number, using default value");
            System.exit(0);
        }
        SERVER_PORT = port;
        log.info("Server IP:" + SERVER_IP + " Port: " + SERVER_PORT);
    }

}
