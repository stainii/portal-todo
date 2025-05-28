package be.stijnhooft.portal.todo.utils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Optional;
import java.util.TimeZone;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DateTimeUtilsTest {

    @BeforeAll
    public static void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Brussels"));
    }

    @Test
    public void addDurationToWhenDateTimeIsNull() {
        Optional<LocalDateTime> result = DateTimeUtils.addDaysTo(null, 2);
        assertThat(result, is(Optional.empty()));
    }

    @Test
    public void addDurationToWhenDurationIsNull() {
        LocalDateTime dateTime = LocalDateTime.of(2019, 10, 10, 12, 13);
        Optional<LocalDateTime> result = DateTimeUtils.addDaysTo(dateTime, null);
        assertThat(result, is(Optional.empty()));
    }

    @Test
    public void addDurationToWhenSuccess() {
        LocalDateTime dateTime = LocalDateTime.of(2019, 10, 10, 12, 13);
        Optional<LocalDateTime> result = DateTimeUtils.addDaysTo(dateTime, 2);
        assertThat("result presence", result.isPresent(), is(true));
        assertThat(result.get(), is(LocalDateTime.of(2019, 10, 12, 12, 13)));
    }

    @Test
    public void parseAsLocalDateWhenProvidingOnlyDate() {
        assertThat(DateTimeUtils.parseAsLocalDate("2020-01-02"),
                is(equalTo(LocalDate.of(2020, 1, 2))));
    }

    @Test
    public void parseAsLocalDateWhenProvidingDateTimeWithoutTimeZoneWithATBetweenDateAndTime() {
        assertThat(DateTimeUtils.parseAsLocalDate("2020-01-02T10:00:00"),
                is(equalTo(LocalDate.of(2020, 1, 2))));
    }

    @Test
    public void parseAsLocalDateWhenProvidingWithTimeZoneUTCWhichResultToTheNextDay() {
        assertThat(DateTimeUtils.parseAsLocalDate("2020-01-01T23:00:00Z"),
                is(equalTo(LocalDate.of(2020, 1, 2))));
    }

    @Test
    public void parseAsLocalDateWhenProvidingWithTimeZoneUTC() {
        assertThat(DateTimeUtils.parseAsLocalDate("2020-01-02T10:00:00Z"),
                is(equalTo(LocalDate.of(2020, 1, 2))));
    }

    @Test
    public void parseAsLocalDateWhenProvidingWithTimeZonePlus0100() {
        assertThat(DateTimeUtils.parseAsLocalDate("2020-01-02T10:00:00+01:00"),
                is(equalTo(LocalDate.of(2020, 1, 2))));
    }

    @Test
    public void parseAsLocalDateTimeWhenProvidingOnlyDate() {
        assertThat(DateTimeUtils.parseAsLocalDateTime("2020-01-02"),
                is(equalTo(LocalDateTime.of(2020, 1, 2, 0, 0))));
    }

    @Test
    public void parseAsLocalDateTimeWhenProvidingDateTimeWithoutTimeZoneWithATBetweenDateAndTime() {
        assertThat(DateTimeUtils.parseAsLocalDateTime("2020-01-02T10:00:00"),
                is(equalTo(LocalDateTime.of(2020, 1, 2, 10, 0))));
    }

    @Test
    public void parseAsLocalDateTimeWhenProvidingWithTimeZoneUTCWhichResultToTheNextDay() {
        assertThat(DateTimeUtils.parseAsLocalDateTime("2020-01-01T23:00:00Z"),
                is(equalTo(ZonedDateTime.of(2020, 1, 1, 23, 0, 0, 0, ZoneId.of("UTC"))
                        .withZoneSameInstant(ZoneId.systemDefault())
                        .toLocalDateTime())));
    }

    @Test
    public void parseAsLocalDateTimeWhenProvidingWithTimeZoneUTC() {
        assertThat(DateTimeUtils.parseAsLocalDateTime("2020-01-02T10:00:00Z"),
                is(equalTo(ZonedDateTime.of(2020, 1, 2, 10, 0, 0, 0, ZoneId.of("UTC"))
                        .withZoneSameInstant(ZoneId.systemDefault())
                        .toLocalDateTime())));
    }

    @Test
    public void parseAsLocalDateTimeWhenProvidingWithTimeZonePlus0100() {
        assertThat(DateTimeUtils.parseAsLocalDateTime("2020-01-02T10:00:00+01:00"),
                is(equalTo(LocalDateTime.of(2020, 1, 2, 10, 0))));
    }










    @Test
    public void parseAsZonedDateTimeWhenProvidingOnlyDate() {
        assertThat(DateTimeUtils.parseAsZonedDateTime("2020-01-02"),
                is(equalTo(ZonedDateTime.of(2020, 1, 2, 0, 0, 0, 0, ZoneId.systemDefault()))));
    }

    @Test
    public void parseAsZonedDateTimeWhenProvidingDateTimeWithoutTimeZoneWithATBetweenDateAndTime() {
        assertThat(DateTimeUtils.parseAsZonedDateTime("2020-01-02T10:00:00"),
                is(equalTo(ZonedDateTime.of(2020, 1, 2, 10, 0, 0, 0, ZoneId.systemDefault()))));
    }

    @Test
    public void parseAsZonedDateTimeWhenProvidingWithTimeZoneUTCWhichResultToTheNextDay() {
        assertThat(DateTimeUtils.parseAsZonedDateTime("2020-01-01T23:00:00Z"),
                is(equalTo(ZonedDateTime.of(2020, 1, 1, 23, 0, 0, 0, ZoneId.of("Z")))));
    }

    @Test
    public void parseAsZonedDateTimeWhenProvidingWithTimeZoneUTC() {
        assertThat(DateTimeUtils.parseAsZonedDateTime("2020-01-02T10:00:00Z"),
                is(equalTo(ZonedDateTime.of(2020, 1, 2, 10, 0, 0, 0, ZoneId.of("Z")))));
    }

    @Test
    public void parseAsZonedDateTimeWhenProvidingWithTimeZonePlus0100() {
        assertThat(DateTimeUtils.parseAsZonedDateTime("2020-01-02T10:00:00+01:00"),
                is(equalTo(ZonedDateTime.of(2020, 1, 2, 10, 0, 0, 0, ZoneId.ofOffset("", ZoneOffset.ofHours(1))))));
    }

    @Test
    public void parseAsZonedDateTimeWhenProvidingNull() {
        assertThat(DateTimeUtils.parseAsZonedDateTime(null), is(nullValue()));
    }

    @Test
    public void parseAsLocalDateTimeWhenProvidingNull() {
        assertThat(DateTimeUtils.parseAsLocalDateTime(null), is(nullValue()));
    }

    @Test
    public void parseAsLocalDateWhenProvidingNull() {
        assertThat(DateTimeUtils.parseAsLocalDate(null), is(nullValue()));
    }

}
