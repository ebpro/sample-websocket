package fr.univtln.bruno.samples.websocket.server;

import fr.univtln.bruno.samples.websocket.ParameterSingleton;
import fr.univtln.bruno.samples.websocket.message.Message;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.java.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class provides a simple echo server (simple chat) using websockets
 * mvn package && java -jar Server/target/Server-2.0-jar-with-dependencies.jar
 */
@Log
// The path where this server listen and the classes to encode/decode the messages between Java and JSON using Jackson
@ServerEndpoint(value = "/echo",
        encoders = {Message.EncoderDecoder.class},
        decoders = {Message.EncoderDecoder.class})
public class Server {
    private static final List<Session> sessions = new ArrayList<>();
    private static org.glassfish.tyrus.server.Server websocketServer;

    public static void start() throws jakarta.websocket.DeploymentException {
        log.info("Server starting...");
        websocketServer =
                new org.glassfish.tyrus.server.Server(ParameterSingleton.SERVER_IP, ParameterSingleton.SERVER_PORT, "/", null, Server.class);
        websocketServer.start();
    }

    public static void stop() {
        websocketServer.stop();
    }

    public static void main(String[] args) {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            start();
            log.info("Please press a key to stop the server.");

            String line = reader.readLine();
        } catch (DeploymentException e) {
            log.severe("Server start error " + e.getLocalizedMessage());
        } catch (IOException e) {
            log.severe("IO Exception "+e.getLocalizedMessage());
        } finally {
            stop();
        }
    }

    /**
     * Reaction method when a client connect. We add thi session to the list of known sessions.
     *
     * @param session        the new session
     * @param endpointConfig the parameters of this session
     */
    @OnOpen
    public void onOpen(final Session session, EndpointConfig endpointConfig) {
        log.info("new Client connected in session " + session.getId() + " parameters: " + endpointConfig.getUserProperties());
        sessions.add(session);
    }

    /**
     * Reaction method when a message is received. We simply send it to all the known sessions.
     *
     * @param message The message as a java instance decoded from JSON
     * @param peer    The session origin of the message
     * @throws IOException     Network exception
     * @throws EncodeException Decoding exception
     */
    @OnMessage
    public void echo(Message message, Session peer) throws IOException, EncodeException {
        log.info("From client: " + peer.getId() + " Received: " + message);
        for (Session session : sessions)
            session.getBasicRemote().sendObject(message);
    }

    /**
     * Reaction method when a client leave.
     * Remove the session from the known sessions.
     *
     * @param session        the ended session
     * @param endpointConfig the parameters of this session
     */
    @OnClose
    public void onClose(final Session session, EndpointConfig endpointConfig) {
        log.info(session.getId() + " left with parameters: " + endpointConfig.getUserProperties());
        sessions.remove(session);
    }
}