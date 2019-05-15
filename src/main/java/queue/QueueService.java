package queue;

public interface QueueService {

    /**
     * Sends string to the queue with the given name, as a separate message
     * @param queueName must be not null
     * @param content to be sent
     * @return true if data has been sent successfully
     */
    boolean sendToQueue(String queueName, String content);

    /**
     * Reads messages from the queue with the given name. Optionally deletes the message after reading.
     * @param queueName must be not null
     * @param deleteMessage if true deletes message after reading
     * @return
     */
    Iterable<String> getFromQueue(String queueName, boolean deleteMessage);

    boolean isQueueEmpty();
}
