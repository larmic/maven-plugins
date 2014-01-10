package de.larmic.bitbucket;

import org.apache.maven.plugin.logging.Log;
import org.testng.annotations.Test;

import java.io.File;
import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

/**
 * Created by larmic on 08.01.14.
 */
public class TodoCheckMojoTest {

    // TODO no bitbucket ticket
    // TODO #0 not existing bitbucket ticket
    // TODO #1 open bitbucket ticket
    // TODO #2 closed bitbucket ticket

    @Test
    public void testExecute() throws Exception {
        final Log logMock = mock(Log.class);
        final TodoCheckMojo mojo = new TodoCheckMojo();

        this.setField(mojo, "sourceDirectory", new File("./"));
        this.setField(mojo, "testSourceDirectory", new File("./"));

        mojo.execute();

        mojo.setLog(logMock);

        mojo.execute();

        verify(logMock, times(2)).info("Found 4 TODOs in file " + this.getClass().getSimpleName() + ".java");
        verify(logMock, times(2)).info("Line 16: // TODO no bitbucket ticket");
        verify(logMock, times(2)).error("Line 17: Could not find ticket number #0");
        verify(logMock, times(2)).info("Line 18: // TODO #1 open bitbucket ticket");
        verify(logMock, times(2)).error("Line 19: Ticket #2 is resolved");
    }

    private void setField(final Object object, final String propertyName, final Object value) throws NoSuchFieldException, IllegalAccessException {
        final Field field = object.getClass().getDeclaredField(propertyName);
        field.setAccessible(true);
        field.set(object, value);
    }
}
