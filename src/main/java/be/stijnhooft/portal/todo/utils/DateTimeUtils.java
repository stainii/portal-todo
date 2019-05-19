package be.stijnhooft.portal.todo.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class DateTimeUtils {

    public static Optional<LocalDateTime> addDurationTo(LocalDateTime base, Duration duration) {
        if (base == null || duration == null) {
            return Optional.empty();
        } else {
            return Optional.of(base.plus(duration));
        }
    }

}
