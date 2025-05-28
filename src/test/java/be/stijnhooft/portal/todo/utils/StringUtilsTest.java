package be.stijnhooft.portal.todo.utils;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

;

public class StringUtilsTest {

    @Test
    public void fillInVariablesWhenTextIsNull() {
        assertThat(StringUtils.fillInVariables(null, new HashMap<>()), is(Optional.empty()));
    }

    @Test
    public void fillInVariablesWhenVariableMapIsNull() {
        assertThrows(NullPointerException.class, () ->
            StringUtils.fillInVariables("text", null));
    }

    @Test
    public void fillInVariablesWhenOneOfTheVariableNamesIsNull() {
        var variables = new HashMap<String, String>();
        variables.put(null, "something");
        variables.put("adjective", "funny");
        variables.put("name", "Stijn");

        Optional<String> result = StringUtils.fillInVariables("This is a ${adjective} test. Hello, ${name}!", variables);

        assertThat("result presence", result.isPresent(), is(true));
        assertThat(result.get(), is("This is a funny test. Hello, Stijn!"));

    }

    @Test
    public void fillInVariablesWhenOneOfTheVariableValuesIsNull() {
        var variables = new HashMap<String, String>();
        variables.put("adjective", null);
        variables.put("name", "Stijn");

        Optional<String> result = StringUtils.fillInVariables("This is a ${adjective} test. Hello, ${name}!", variables);

        assertThat("result presence", result.isPresent(), is(true));
        assertThat(result.get(), is("This is a  test. Hello, Stijn!"));
    }

    @Test
    public void fillInVariablesWhenNotAllVariablesArePresentInTheMap() {
        var variables = new HashMap<String, String>();
        variables.put("name", "Stijn");

        Optional<String> result = StringUtils.fillInVariables("This is a ${adjective} test. Hello, ${name}!", variables);

        assertThat("result presence", result.isPresent(), is(true));
        assertThat(result.get(), is("This is a ${adjective} test. Hello, Stijn!"));
    }

    @Test
    public void fillInVariablesWhenTheMapContainsVariablesThatAreNotInTheText() {
        var variables = new HashMap<String, String>();
        variables.put("random", "something");
        variables.put("adjective", "funny");
        variables.put("name", "Stijn");

        Optional<String> result = StringUtils.fillInVariables("This is a ${adjective} test. Hello, ${name}!", variables);

        assertThat("result presence", result.isPresent(), is(true));
        assertThat(result.get(), is("This is a funny test. Hello, Stijn!"));
    }

    @Test
    public void fillInVariablesWhenSuccess() {
        var variables = new HashMap<String, String>();
        variables.put("adjective", "funny");
        variables.put("name", "Stijn");

        Optional<String> result = StringUtils.fillInVariables("This is a ${adjective} test. Hello, ${name}!", variables);

        assertThat("result presence", result.isPresent(), is(true));
        assertThat(result.get(), is("This is a funny test. Hello, Stijn!"));
    }
}
