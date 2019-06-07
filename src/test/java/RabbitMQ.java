import com.rabbitmq.client.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Config;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;

@Log4j2
public class RabbitMQ {

    private static final String TEST_QUEUE_NAME = "MyTestQueue";
    private static final String CONNECTION_NAME = "MyTestConnection";
    private Connection connection;

    @BeforeEach
    void setUp() throws IOException, TimeoutException {
        ConnectionFactory factory = getConnectionFactory();
        connection = factory.newConnection(CONNECTION_NAME);
    }

    @AfterEach
    void tearDown() throws IOException {
        connection.close();
    }

    @Test
    void sendToQueueAndReadFromIt() throws Exception {
        String message = "Hello, world! Time:";
        sendToQueue(message);
        sendToQueue(message);
        sendToQueue(message);
        Thread.sleep(10000);
        readFromQueue();
        Thread.sleep(10000);
    }

    private void readFromQueue() throws Exception {
        Channel channel = connection.createChannel();
        channel.queueDeclare(TEST_QUEUE_NAME, false, false, false, null);
        log.debug("[read]Opened queue.");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            log.info("[" + consumerTag + "]" + "Received '" + message + "'");
        };
        channel.basicConsume(TEST_QUEUE_NAME, true, deliverCallback,
                consumerTag -> log.debug("Consumer tag: " + consumerTag));
    }

    private void sendToQueue(String message) throws Exception {
        Channel channel = connection.createChannel();
        channel.queueDeclare(TEST_QUEUE_NAME, false, false, false, null);
        log.debug("[send]Opened queue");
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .contentType("text")
                .contentEncoding("UTF-8")
                .build();
        channel.basicPublish("", TEST_QUEUE_NAME, props, (message + LocalDateTime.now()).getBytes());
        log.debug("Message sent!");
    }

    private ConnectionFactory getConnectionFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        try {
            factory.setUri(Config.getValue("queue.uri"));
        } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        return factory;
    }


}
