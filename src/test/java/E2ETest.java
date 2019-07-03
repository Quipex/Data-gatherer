import com.rabbitmq.client.DeliverCallback;
import controller.QueueController;
import exceptions.ApplicationException;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import queue.RabbitMQService;
import utils.Config;
import utils.ThreadUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Log4j2
public class E2ETest {
    private static String dataQueue = "TestDataQueue";
    private static String requestQueue = "TestRequestQueue";
    private RabbitMQService queueService = new RabbitMQService();
    private QueueController controller = new QueueController(queueService, dataQueue, requestQueue);
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Config.getValue("time.datetime_format"));

    public E2ETest() throws ApplicationException {
    }

    @BeforeEach
    void setUp() {
        purge();
    }

    @Test
    void fullRun() throws InterruptedException {
        controller.run();
        Thread.sleep(500);

//        publishing request
        queueService.publish(requestQueue,
                LocalDateTime.now().minusDays(90).format(formatter) + ";" + LocalDateTime.now().format(formatter));

        Thread.sleep(500);
        DeliverCallback callback = (consumerTag, message) -> {
            String[][] objects = toStrArr(message.getBody());
            log.info("received (" + objects.length + " messages):\n" + Arrays.deepToString(objects));
        };
        queueService.consume(dataQueue, true, callback, null);

        Thread.sleep(5000);
    }

    private boolean purgeQueue(String queueName) {
        if (queueService.messageCount(queueName) > 0) {
            log.debug("Got messages on " + queueName + " queue, purging");
            int mesNum = queueService.purge(queueName);
            log.debug("Purged " + mesNum + " messages");
            return true;
        }
        return false;
    }

    @Test
    void readData() {
        DeliverCallback callback = (consumerTag, message) -> {
            String[][] objects = toStrArr(message.getBody());
            log.info("received (" + objects.length + " messages):\n" + Arrays.deepToString(objects));
        };
        queueService.consume("Data", false, callback, null);
        ThreadUtils.sleep(3000);
    }

    @Test
    void purge() {
        String[] queues = new String[]{
                dataQueue,
                requestQueue
        };
        for (String queue : queues) {
            purgeQueue(queue);
        }
    }

    String[][] toStrArr(byte[] content) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(content);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (String[][]) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
