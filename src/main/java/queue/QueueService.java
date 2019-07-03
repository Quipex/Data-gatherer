package queue;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.DeliverCallback;
import exceptions.QueueException;

public interface QueueService {

    /**
     * Publish a message
     *
     * @param payload to be published
     * @throws QueueException if an error is encountered
     */
    void publish(String queue, String[][] payload) throws QueueException;

    /**
     * Publish a message
     *
     * @param message to be published
     * @throws QueueException if an error is encountered
     */
    void publish(String queue, String message) throws QueueException;

    /**
     * Consumes a message when arrives to a queue
     *
     * @return consumerTag
     */
    String consume(String queue, boolean deleteMessage,
                   DeliverCallback deliverCallback, CancelCallback cancelCallback) throws QueueException;

    /**
     * @return number of the messages waiting to be consumed
     */
    long messageCount(String queueName);

    /**
     * Purges queue and returns number of purged messages
     *
     * @return number of purged messages
     */
    int purge(String queueName);

    /**
     * Declares queue
     *
     * @param queueName of the queue
     */
    void declareQueue(String queueName);
}
