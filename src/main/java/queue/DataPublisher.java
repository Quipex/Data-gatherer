package queue;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class DataPublisher {
    public void publish() {
        log.debug("Publishing data");
    }
}
