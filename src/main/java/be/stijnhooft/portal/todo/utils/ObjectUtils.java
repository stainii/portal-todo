package be.stijnhooft.portal.todo.utils;

import be.stijnhooft.portal.todo.model.Task;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ObjectUtils {

    @SneakyThrows
    public static <T> T patch(T oldVersion, T newVersion) {
        for (Field field : Task.class.getDeclaredFields()) {
            boolean wasAccessible = field.isAccessible();
            field.setAccessible(true);

            Object valueInNewVersion = field.get(newVersion);
            if (valueInNewVersion != null) {
                field.set(oldVersion, valueInNewVersion);
            }

            field.setAccessible(wasAccessible);
        }

        return oldVersion;
    }

    @SneakyThrows
    public static Map<String, String> getAllFieldsAndTheirValues(Object object) {
        Map<String, String> allFieldsAndTheirValues = new HashMap<>();
        for (Field field : object.getClass().getDeclaredFields()) {
            boolean wasAccessible = field.isAccessible();
            field.setAccessible(true);

            String fieldName = field.getName();
            Object fieldValue = field.get(object);

            if (fieldValue != null) {
                allFieldsAndTheirValues.put(fieldName, fieldValue.toString());
            }

            field.setAccessible(wasAccessible);
        }
        return allFieldsAndTheirValues;
    }

}
