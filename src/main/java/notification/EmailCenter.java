package notification;

import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.config.TransportStrategy;
import utils.Config;

public class EmailCenter {
    private static String login = Config.getValue("email.login");
    private static String password = Config.getValue("email.password");
    private static String server = Config.getValue("email.server_host");
    private static String destination = Config.getValue("email.destination_address");
    private static String sender = Config.getValue("email.sender");
    private static Mailer mailer = getDefaultMailer();

    private static Mailer getDefaultMailer() {
        return MailerBuilder
                .withSMTPServer(server, 465, login, password)
                .withTransportStrategy(TransportStrategy.SMTPS)
                .withDebugLogging(true)
                .buildMailer();
    }

    public static void sendError(String message) {
        sendError(message, "An exception occurred");
    }

    public static void sendError(String message, String subject) {
        Email email = createEmail(message, subject, MessageType.ERROR);
        mailer.sendMail(email, true);
    }

    public static void sendInfo(String message) {
        sendInfo(message, "New information");
    }

    public static void sendInfo(String message, String subject) {
        Email email = createEmail(message, subject, MessageType.INFO);
        mailer.sendMail(email, true);
    }

    private static Email createEmail(String message, String subject, MessageType type) {
        String builtSubj = "[" + type.getTypeName() + "] " + subject;
        return EmailBuilder.startingBlank()
                .from(sender, login)
                .to("Creator", destination)
                .withSubject(builtSubj)
                .withPlainText(message)
                .buildEmail();
    }
}
