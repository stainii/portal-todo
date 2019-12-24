package be.stijnhooft.portal.todo.utils;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class DateTimeUtilsTest {

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
}
