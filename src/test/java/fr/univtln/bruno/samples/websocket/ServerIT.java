package fr.univtln.bruno.samples.websocket;

import jakarta.websocket.DeploymentException;
import org.junit.*;


/**
 * A simple junit integration test for A REST service.
 */
public class ServerIT {
    private static org.glassfish.tyrus.server.Server server;

    /**
     * Starts the application before the tests.
     */
    @BeforeClass
    public static void setUp() throws DeploymentException {
        //start the Grizzly2 web container
        server.start();
    }

    /**
     * Stops the application at the end of the test.
     */
    @AfterClass
    public static void tearDown() {
        server.stop();
    }

    @Before
    public void beforeEach() {
    }

    @After
    public void afterEach() {
    }

    @Test
    public void testHello() {

    }
}
