package queue;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import exceptions.ApplicationException;
import lombok.extern.log4j.Log4j2;
import utils.Config;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

@Log4j2
final class RabbitMQConfig {
    private static final String CONNECTION_NAME = Config.getValue("queue.connection.name");
    private static Connection connection;

    public static Connection getConnection() throws ApplicationException {
        if (connection == null) {
            connection = createConnection();
        }
        return connection;
    }

    private static Connection createConnection() throws ApplicationException {
        ConnectionFactory factory = createFactory();
        return createConnection(factory);
    }

    private static Connection createConnection(ConnectionFactory factory) throws ApplicationException {
        try {
            return factory.newConnection(CONNECTION_NAME);
        } catch (IOException | TimeoutException e) {
            log.error("Can't establish connection to a queue", e);
            throw new ApplicationException(e);
        }
    }

    private static ConnectionFactory createFactory() throws ApplicationException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setExceptionHandler(new EmailNotificationExceptionHandler());
        final String uri = Config.getValue("queue.uri");
        try {
            factory.setUri(uri);
        } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
            final String message = "Uri is invalid: " + uri;
            log.error(message, e);
            throw new ApplicationException(e);
        }
        //Recommended settings
        factory.setRequestedHeartbeat(30);
        factory.setConnectionTimeout(30000);

        return factory;
    }
}
