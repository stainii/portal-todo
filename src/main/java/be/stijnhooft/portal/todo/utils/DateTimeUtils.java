package be.stijnhooft.portal.todo.utils;

import java.time.LocalDateTime;
import java.util.Optional;

public class DateTimeUtils {

    public static Optional<LocalDateTime> addDaysTo(LocalDateTime base, Integer days) {
        if (base == null || days == null) {
            return Optional.empty();
        } else {
            return Optional.of(base.plusDays(days));
        }
    }

}
