package be.stijnhooft.portal.todo.utils;

import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.Assert.*;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class DateTimeUtilsTest {

    @Test
    public void addDurationToWhenDateTimeIsNull() {
        Optional<LocalDateTime> result = DateTimeUtils.addDurationTo(null, Duration.of(2, ChronoUnit.SECONDS));
        assertThat(result, is(Optional.empty()));
    }

    @Test
    public void addDurationToWhenDurationIsNull() {
        LocalDateTime dateTime = LocalDateTime.of(2019, 10, 10, 12, 13);
        Optional<LocalDateTime> result = DateTimeUtils.addDurationTo(dateTime, null);
        assertThat(result, is(Optional.empty()));
    }

    @Test
    public void addDurationToWhenSuccess() {
        LocalDateTime dateTime = LocalDateTime.of(2019, 10, 10, 12, 13);
        Optional<LocalDateTime> result = DateTimeUtils.addDurationTo(dateTime, Duration.of(2, ChronoUnit.MINUTES));
        assertThat("result presence", result.isPresent(), is(true));
        assertThat(result.get(), is(LocalDateTime.of(2019, 10, 10, 12, 15)));
    }
}
