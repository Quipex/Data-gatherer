package model;

import java.time.LocalDateTime;

public interface Dateable {
    @SuppressWarnings("RedundantIfStatement")
    default boolean fitsBetweenDates(LocalDateTime from, LocalDateTime to) {
        if (getTimestamp().compareTo(to) > 0)
            return false;
        if (getTimestamp().compareTo(from) < 0)
            return false;

        return true;
    }

    LocalDateTime getTimestamp();
}
