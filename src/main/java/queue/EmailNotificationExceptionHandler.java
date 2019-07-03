package queue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.impl.ForgivingExceptionHandler;
import notification.EmailCenter;

public class EmailNotificationExceptionHandler extends ForgivingExceptionHandler {
    @Override
    public void handleConsumerException(Channel channel, Throwable exception, Consumer consumer, String consumerTag, String methodName) {
        super.handleConsumerException(channel, exception, consumer, consumerTag, methodName);
        EmailCenter.sendException(exception);
    }
}
