package de.larmic.maven.bitbucket;

import java.lang.reflect.Field;

/**
 * Created by larmic on 13.01.14.
 */
public class ReflectionUtils {

    public static final void setField(final Object object, final String propertyName, final Object value) throws NoSuchFieldException,
            IllegalAccessException {
        final Field field = object.getClass().getDeclaredField(propertyName);
        field.setAccessible(true);
        field.set(object, value);
    }

}
