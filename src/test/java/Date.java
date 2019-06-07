import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.format.DateTimeFormatter;

@Log4j2
public class Date {
    @Test
    public void sixtyDaysBefore() {
        LocalDate endDate = LocalDate.of(2019, Month.JULY, 10);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        log.info(LocalDateTime.of(endDate, LocalTime.now()).format(formatter));
    }

    @Test
    public void testWaitUntil() {

        final String s = ZonedDateTime.now().toString();
        log.info(s);
    }

    @Test
    public void timezone() {
        System.out.println(LocalDateTime.now(ZoneId.of("Europe/Helsinki")));
    }
}
