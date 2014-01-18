package de.larmic.maven.bitbucket;

import java.lang.reflect.Field;

/**
 * Created by larmic on 13.01.14.
 */
public class ReflectionUtils {

    public static final void setField(final Object object, final String propertyName, final Object value) throws NoSuchFieldException,
            IllegalAccessException {
        Field field;
        try {
            field = object.getClass().getDeclaredField(propertyName);
        } catch (NoSuchFieldException e) {
            field = object.getClass().getSuperclass().getDeclaredField(propertyName);
        }
        field.setAccessible(true);
        field.set(object, value);
    }

}
