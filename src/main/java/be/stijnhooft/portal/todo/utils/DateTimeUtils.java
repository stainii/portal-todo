package be.stijnhooft.portal.todo.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;

public class DateTimeUtils {

    private static final DateTimeFormatter LOOSE_ISO_DATE_TIME_ZONE_PARSER = DateTimeFormatter.ofPattern(
            "[yyyyMMdd][yyyy-MM-dd][yyyy-DDD]['T'[HHmmss][HHmm][HH:mm:ss][HH:mm][.SSSSSSSSS][.SSSSSSSS][.SSSSSSS][.SSSSSS][.SSSSS][.SSSS][.SSS][.SS][.S]][OOOO][O][z][XXXXX][XXXX]['['VV']']");

    public static Optional<LocalDateTime> addDaysTo(LocalDateTime base, Integer days) {
        if (base == null || days == null) {
            return Optional.empty();
        } else {
            return Optional.of(base.plusDays(days));
        }
    }

    public static LocalDate parseAsLocalDate(String input) {
        if (input == null) {
            return null;
        }
        return parseAsZonedDateTime(input)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static ZonedDateTime parseAsZonedDateTime(String input) {
        if (input == null) {
            return null;
        }

        TemporalAccessor temporalAccessor = LOOSE_ISO_DATE_TIME_ZONE_PARSER.parseBest(input, ZonedDateTime::from, LocalDateTime::from, LocalDate::from);
        if (temporalAccessor instanceof ZonedDateTime) {
            return ((ZonedDateTime) temporalAccessor);
        }
        if (temporalAccessor instanceof LocalDateTime) {
            return ((LocalDateTime) temporalAccessor)
                    .atZone(ZoneId.systemDefault());
        }
        return ((LocalDate) temporalAccessor).atStartOfDay(ZoneId.systemDefault());
    }

    public static LocalDateTime parseAsLocalDateTime(String input) {
        if (input == null)  {
            return null;
        }
        return parseAsZonedDateTime(input)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
