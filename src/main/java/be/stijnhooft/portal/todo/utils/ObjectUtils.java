package be.stijnhooft.portal.todo.utils;

import be.stijnhooft.portal.todo.model.task.Task;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.*;

public class ObjectUtils {

    @SneakyThrows
    public static Map<String, String> getAllFieldsAndTheirValues(Object object) {
        Map<String, String> allFieldsAndTheirValues = new HashMap<>();
        for (Field field : object.getClass().getDeclaredFields()) {
            boolean wasAccessible = field.canAccess(object);
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

    public static boolean notEqual(Object object1, Object object2) {
        return org.apache.commons.lang3.ObjectUtils.notEqual(object1, object2);
    }
}
