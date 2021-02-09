package fr.univtln.bruno.samples.websocket.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univtln.bruno.samples.websocket.message.Message;
import fr.univtln.bruno.samples.websocket.model.Person;
import jakarta.websocket.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.glassfish.tyrus.client.ClientManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Date;
import java.util.Optional;

/**
 * This class provides a simple chat client using websocket.
 * java -jar JavaClient/target/JavaClient-2.0-jar-with-dependencies.jar
 */
@Log
@RequiredArgsConstructor(staticName = "of")
// The classes to encode/decode the messages between Java and JSON using Jackson
@ClientEndpoint(encoders = {Message.EncoderDecoder.class},
        decoders = {Message.EncoderDecoder.class})
public class Client {
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

    /**
     * The sender of the message in this client
     */
    @NonNull
    private final Person sender;

    /**
     * The description of the websocket session
     */
    private Session session;

    /**
     * Starts created a random person (the sender), start a client and connect it to the server.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        Client websocketClient = Client.of(Person.builder().firstname("John").lastname("Doe").build());
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            final ClientManager client = ClientManager.createClient();
            client.connectToServer(
                    websocketClient,
                    URI.create("ws://" + SERVER_IP + ":" + SERVER_PORT + "/echo")
            );

            //read the next message and send it. Stops on empty message.
            log.info("Send empty line to stop the client.");
            String line;
            do {
                line = reader.readLine();
                if (!"".equals(line))
                    websocketClient.sendMessage(line);
            } while (!"".equals(line));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                websocketClient.closeSession();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * The reaction on connect
     *
     * @param session        The data about the opened connection
     * @param endpointConfig The about the other endpoint
     * @throws IOException     Network exception
     * @throws EncodeException Message Encoding/Decoding exception
     */
    @OnOpen
    public void onOpen(final Session session, EndpointConfig endpointConfig) throws IOException, EncodeException {
        this.session = session;
        log.info("I am " + sender);
        if (!endpointConfig.getUserProperties().isEmpty())
            log.info("Endpoint User properties " + endpointConfig.getUserProperties());
        log.info("Session Id " + session.getId());
        log.info("Sending 'HELLO' message to server");
        session.getBasicRemote().sendObject(Message.builder()
                .date(new Date())
                .sender(sender)
                .messageContent("HELLO")
                .build());
    }

    /**
     * The reaction method on the reception of a new message
     *
     * @param message The decoded received message.
     */
    @OnMessage
    public void onMessage(Message message) {
        log.info("RECEIVED !");
        log.info(message.getDate() + " (" + message.getSender().getId()
                + ") " + message.getMessageContent());
    }

    /**
     * The reaction method on the end of the connection
     *
     * @param session        The data about the opened connection
     * @param endpointConfig The about the other endpoint
     */
    @OnClose
    public void onClose(final Session session, EndpointConfig endpointConfig) {
        log.info("Session " + session.getId() + " closed. " + endpointConfig);
    }

    /**
     * The method to start an clean close of the connection initiated by the client.
     *
     * @throws IOException Network Exception
     */
    public void closeSession() throws IOException {
        if (session.isOpen())
            session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "OK"));
    }

    /**
     * The method to send a message to the chat server.
     * It just sends a Message object which automatically encoded in JSON
     *
     * @param message The text message to be sent.
     */
    public void sendMessage(String message) {
        try {
            Message message1;
            session.getBasicRemote()
                    .sendObject(
                            message1 = Message.builder()
                                    .date(new Date())
                                    .sender(sender)
                                    .messageContent(message)
                                    .build());
            log.info("---JSON Sent--> " + new ObjectMapper().writeValueAsString(message1));
        } catch (IOException e) {
            log.info("IO Exception " + e.getLocalizedMessage());
        } catch (EncodeException e) {
            log.severe("Encode Exception: " + e.getLocalizedMessage());
        }
    }
}
