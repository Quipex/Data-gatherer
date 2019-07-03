package queue;

import com.rabbitmq.client.*;
import exceptions.ApplicationException;
import exceptions.QueueException;
import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

@Log4j2
public class RabbitMQService implements QueueService {

    private static final String EXCHANGE = "";
    private Channel rabbitChannel;

    /**
     * Creates new channel by getting active connection
     *
     * @throws ApplicationException if connection error occurred
     */
    public RabbitMQService() throws ApplicationException {
        Connection connection = RabbitMQConfig.getConnection();
        try {
            Optional<Channel> optional = connection.openChannel();
            log.debug("Opening a channel...");
            if (optional.isPresent()) {
                rabbitChannel = optional.get();
                log.debug("Opened channel '" + rabbitChannel.getChannelNumber() + "'");
            } else {
                throw new QueueException("Can't get a new channel");
            }
        } catch (IOException e) {
            log.error(e);
            throw new QueueException("Can't open new channel", e);
        }
    }

    private static byte[] toStream(String[][] message) {
        byte[] stream;

        try (ByteArrayOutputStream byteArrStream = new ByteArrayOutputStream();
             ObjectOutputStream objectStream = new ObjectOutputStream(byteArrStream)) {
            objectStream.writeObject(message);
            stream = byteArrStream.toByteArray();
        } catch (IOException e) {
            log.error(e);
            throw new QueueException(e);
        }

        return stream;
    }

    @Override
    public void publish(String queueName, String[][] payload) throws QueueException {
        publish(queueName, toStream(payload), null);
        log.info("[" + queueName + "] Sent(" + payload.length + " objects)");
        log.trace("Contents:\n" + Arrays.deepToString(payload));
    }


    @Override
    public void publish(String queueName, String message) throws QueueException {
        publish(queueName, message.getBytes(StandardCharsets.UTF_8), getUTF_8Properties());
        log.info("[" + queueName + "] Sent: " + message);
    }

    private void publish(String queueName, byte[] content, AMQP.BasicProperties props) {
        try {
            rabbitChannel.basicPublish(EXCHANGE, queueName, props, content);
        } catch (IOException e) {
            log.error(e);
            throw new QueueException(e);
        }
    }

    private AMQP.BasicProperties getUTF_8Properties() {
        return new AMQP.BasicProperties.Builder().contentType("text/plain").contentEncoding("UTF-8").build();
    }

    @Override
    public String consume(String queueName, boolean deleteMessage,
                          DeliverCallback deliverCallback, CancelCallback cancelCallback) throws QueueException {
        try {
            return rabbitChannel.basicConsume(queueName, deleteMessage, deliverCallback, cancelCallback);
        } catch (IOException e) {
            log.error(e);
            throw new QueueException(e);
        }
    }

    @Override
    public long messageCount(String queueName) {
        try {
            return rabbitChannel.messageCount(queueName);
        } catch (IOException e) {
            log.error(e);
            throw new QueueException(e);
        }
    }

    @Override
    public int purge(String queueName) {
        AMQP.Queue.PurgeOk response;
        try {
            response = rabbitChannel.queuePurge(queueName);
        } catch (IOException e) {
            log.error(e);
            throw new QueueException(e);
        }
        return response.getMessageCount();
    }

    @Override
    public void declareQueue(String queueName) {
        try {
            rabbitChannel.queueDeclare(queueName, true, false, false, null);
        } catch (IOException e) {
            log.error(e);
            throw new QueueException(e);
        }
    }
}
