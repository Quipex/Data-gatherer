package queue;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Log4j2
public class CommandConsumer {

    private DataPublisher publisher;

    public CommandConsumer(DataPublisher publisher) {
        this.publisher = publisher;
    }

    public DeliverCallback getDeliverCallback() {
        return (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            Date timestamp = delivery.getProperties().getTimestamp();
            log.debug("[" + consumerTag + "] Received a command (written at " + timestamp + "): " + message);
            publisher.publish();
        };
    }

    public CancelCallback getCancelCallback() {
        return consumerTag -> log.debug("[" + consumerTag + "] Cancelled receiving message");
    }
}
