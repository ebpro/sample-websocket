package fr.univtln.bruno.samples.websocket;

import fr.univtln.bruno.samples.websocket.message.Message;
import fr.univtln.bruno.samples.websocket.model.Person;
import fr.univtln.bruno.samples.websocket.server.Server;
import jakarta.websocket.*;
import lombok.extern.java.Log;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;
import org.glassfish.tyrus.client.ThreadPoolConfig;
import org.junit.*;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * A simple junit integration test for A websocket endpoint.
 */
@Log
public class ServerIT {

    private static ClientEndpointConfig clientEndpointConfig = null;
    private static ClientManager clientManager = null;
    private static ThreadPoolConfig workerThreadPoolConfig = null;
    private CountDownLatch messageLatch;

    private static void setupWebsocketClient() {
        // Websocket client setup
        clientEndpointConfig = ClientEndpointConfig.Builder.create()
                .encoders(Collections.singletonList(Message.EncoderDecoder.class))
                .decoders(Collections.singletonList(Message.EncoderDecoder.class))
                .build();
        clientManager = ClientManager.createClient();

        workerThreadPoolConfig = ThreadPoolConfig.defaultConfig();
        workerThreadPoolConfig.setDaemon(false);
        workerThreadPoolConfig.setMaxPoolSize(4);
        workerThreadPoolConfig.setCorePoolSize(3);

        clientManager.getProperties().put(ClientProperties.SHARED_CONTAINER, false);
        clientManager.getProperties().put(ClientProperties.WORKER_THREAD_POOL_CONFIG, workerThreadPoolConfig);
    }

    /**
     * Starts the application before the tests.
     */
    @BeforeClass
    public static void setUp() throws DeploymentException {
        //start the Tyrus web container
        Server.start();
        setupWebsocketClient();
        if (clientManager != null)
            clientManager.shutdown();
    }

    /**
     * Stops the application at the end of the test.
     */
    @AfterClass
    public static void tearDown() {
        Server.stop();
    }

    @Before
    public void beforeEach() {
    }

    @After
    public void afterEach() {
    }

    @Test
    public void testHello() throws InterruptedException, IOException, DeploymentException {
        messageLatch = new CountDownLatch(1);
        try {
            clientManager.connectToServer(new ClientTestEndpoint(), clientEndpointConfig,
                    URI.create("ws://" + Server.SERVER_IP + ":" + Server.SERVER_PORT + "/echo"));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        boolean mesageReceivedByClient = messageLatch.await(30, TimeUnit.SECONDS);
        Assert.assertTrue("Time lapsed before message was received by client.", mesageReceivedByClient);
    }

    private class ClientTestEndpoint extends Endpoint {


        @Override
        public void onOpen(Session session, EndpointConfig config) {
            try {
                session.addMessageHandler(new MessageHandler.Whole<Message>() {
                    @Override
                    public void onMessage(Message message) {
                        log.info(message.getMessageContent());
                        messageLatch.countDown(); // signal that the message was received by the client
                    }
                });
                Message message = Message.builder()
                        .date(new Date())
                        .sender(Person.builder().firstname("John").lastname("Doe").build())
                        .messageContent("HELLO")
                        .build();
                session.getBasicRemote().sendObject(message);
            } catch (IOException | EncodeException e) {
                e.printStackTrace();
            }
        }
    }
}