package p3.util;

import java.lang.reflect.Field;

public class Utils {

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Field field, Object instance) {
        try {
            return (T) field.get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T setFieldValue(Field field, Object instance, T value) {
        try {
            T oldValue = getFieldValue(field, instance);
            field.set(instance, value);
            return oldValue;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
