package utils;

import lombok.extern.log4j.Log4j2;

import java.time.*;
import java.time.temporal.ChronoUnit;

@Log4j2
public final class ThreadUtils {
    private static final int FIFTEEN_SECS = 15 * 1000;
    private static final String ZONE_ID = Configuration.getValue("time.zone_id");

    /**
     * @param desiredTime thread sleeps until next datetime with the same hours and minutes
     */
    public static void waitUntil(LocalTime desiredTime) {
        LocalDateTime currentTime = LocalDateTime.now(ZoneId.of(ZONE_ID));
        long timeToWait = millisToWait(desiredTime, currentTime);
        log.info("Waiting " + timeToWait + "ms to start");
        sleep(timeToWait);
    }

    private static long millisToWait(LocalTime desiredTime, LocalDateTime currentDateTime) {
        final LocalDate today = currentDateTime.toLocalDate();
        final LocalDate tomorrow = today.plusDays(1);
        if (desiredTime.getHour() > currentDateTime.getHour()) {
            return millisUntil(currentDateTime, today, desiredTime);
        }
        if (desiredTime.getHour() < currentDateTime.getHour()) {
            return millisUntil(currentDateTime, tomorrow, desiredTime);
        }
//       IF EQUAL HOURS
        if (desiredTime.getMinute() > currentDateTime.getMinute()) {
            return millisUntil(currentDateTime, today, desiredTime);
        }
        if (desiredTime.getMinute() < currentDateTime.getMinute()) {
            return millisUntil(currentDateTime, tomorrow, desiredTime);
        }
//       IF HOURS AND MINUTES ARE EQUAL RETURN 0ms
        return 0;
    }

    private static long millisUntil(LocalDateTime currentDateTime, LocalDate desiredDate, LocalTime desiredTime) {
        LocalDateTime desiredDateTime = LocalDateTime.of(desiredDate, desiredTime);
        return currentDateTime.until(desiredDateTime, ChronoUnit.MILLIS);
    }

    /**
     * @return true if hours and minutes are the same
     */
    private static boolean sameHoursAndMinutes(LocalTime desiredTime, LocalDateTime currentTime) {
        return currentTime.getHour() == desiredTime.getHour() &&
                currentTime.getMinute() == desiredTime.getMinute();
    }

    /**
     * Same as Thread.sleep, but InterruptedException is handled
     * @param millis time for which thread goes halted
     */
    public static void sleep(long millis) {
        log.debug("Thread sleeps for " + millis + "ms");
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error(Thread.currentThread().getName() + " got interrupted", e);
        }
    }
}
