package queue;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import exceptions.QueueException;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

@Log4j2
public class RabbitMQService {

    private Channel rabbitChannel;
    private String queueName;

    public RabbitMQService(Channel rabbitChannel, String queueName) {
        this.rabbitChannel = rabbitChannel;
        try {
            changeQueue(queueName);
        } catch (IOException e) {
            throw new QueueException(e);
        }
    }

    /**
     * Declares a queue and changes internal queueName if no errors occured
     *
     * @param queueName to declare
     * @return status of a queue declared
     * @throws IOException if an error is encountered
     */
    public AMQP.Queue.DeclareOk changeQueue(String queueName) throws IOException {
        AMQP.Queue.DeclareOk status = rabbitChannel.queueDeclare(queueName, true, false, false, null);
        this.queueName = queueName;
        log.debug("Changed queue to " + queueName);
        return status;
    }

    /**
     * @return name of the queue
     */
    public String getQueueName() {
        return queueName;
    }

    /**
     * Publish a message
     *
     * @param content to be published
     * @throws IOException if an error is encountered
     */
    public void publish(String content) throws IOException {
        rabbitChannel.basicPublish("main", queueName, null, content.getBytes());
    }

    /**
     * Consumes a message when arrives to a queue
     */
    public String consume(boolean deleteMessage,
                               DeliverCallback deliverCallback, CancelCallback cancelCallback) throws IOException {
        return rabbitChannel.basicConsume(queueName, deleteMessage, deliverCallback, cancelCallback);
    }
}
