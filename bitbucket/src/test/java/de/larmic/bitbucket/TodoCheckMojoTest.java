package de.larmic.bitbucket;

// TODO no bitbucket ticket
// TODO #0 not existing bitbucket ticket
// TODO #1 open bitbucket ticket
// TODO #2 closed bitbucket ticket

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

/**
 * Created by larmic on 08.01.14.
 */
public class TodoCheckMojoTest {

    private TodoCheckMojo mojo;

    @BeforeMethod
    public void setUp() throws Exception {
        mojo = new TodoCheckMojo();
    }

    @Test
    public void testExecute() throws Exception {
        final Log logMock = mock(Log.class);

        this.setField(mojo, "accountName", "larmicBB");
        this.setField(mojo, "repositorySlug", "larmic-maven-plugins");
        this.setField(mojo, "sourceDirectory", new File("./"));
        this.setField(mojo, "testSourceDirectory", new File("./"));

        // call execute method to log result
        // helpful when debug a test problem
        mojo.execute();

        mojo.setLog(logMock);

        mojo.execute();

        verify(logMock, times(2)).info(this.getClass().getSimpleName() + ".java Line: 3, Text: no bitbucket ticket");
        verify(logMock, times(2)).error("[COULD NOT FIND TICKET] " + this.getClass().getSimpleName() + ".java Line: 4, Text: not existing bitbucket ticket");
        verify(logMock, times(2)).info(this.getClass().getSimpleName() + ".java Line: 5, Text: open bitbucket ticket");
        verify(logMock, times(2)).error("[TICKET IS RESOLVED] " + this.getClass().getSimpleName() + ".java Line: 6, Text: closed bitbucket ticket");
    }

    @Test
    public void testExecuteNoTestSourceDirectory() throws Exception {
        this.setField(mojo, "accountName", "larmicBB");
        this.setField(mojo, "repositorySlug", "larmic-maven-plugins");
        this.setField(mojo, "sourceDirectory", new File("./"));

        mojo.execute();
    }

    @Test
    public void testExecuteNoSourceDirectory() throws Exception {
        this.setField(mojo, "accountName", "larmicBB");
        this.setField(mojo, "repositorySlug", "larmic-maven-plugins");
        this.setField(mojo, "testSourceDirectory", new File("./"));

        mojo.execute();
    }

    @Test(expectedExceptions = MojoExecutionException.class)
    public void testExecuteWithEmptyAccountName() throws Exception {
        mojo.execute();
    }

    @Test(expectedExceptions = MojoExecutionException.class)
    public void testName() throws Exception {
        this.setField(mojo, "accountName", "larmicBB");

        mojo.execute();
    }

    private void setField(final Object object, final String propertyName, final Object value) throws NoSuchFieldException, IllegalAccessException {
        final Field field = object.getClass().getDeclaredField(propertyName);
        field.setAccessible(true);
        field.set(object, value);
    }
}
