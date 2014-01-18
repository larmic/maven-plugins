package de.larmic.maven.bitbucket;

import de.larmic.maven.bitbucket.dom.FileReader;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.reporters.Files;

import java.io.File;

/**
 * Created by larmic on 13.01.14.
 */
public class ReleaseNotesMojoTest {

    private ReleaseNotesMojo mojo;

    @BeforeMethod
    public void setUp() throws Exception {
        mojo = new ReleaseNotesMojo();
    }

    @Test
    public void testExecute() throws Exception {
        ReflectionUtils.setField(mojo, "sourceDirectory", new File(""));
        ReflectionUtils.setField(mojo, "accountName", "larmicBB");
        ReflectionUtils.setField(mojo, "repositorySlug", "larmic-maven-plugins");
        ReflectionUtils.setField(mojo, "title", "testtitle");
        ReflectionUtils.setField(mojo, "ignoreTicketWithNoVersion", true);
        ReflectionUtils.setField(mojo, "relativePath", "/bitbucket");

        this.mojo.execute();

        final File file = new File("bitbucket/releaseNotes.xml");

        Assert.assertEquals(Files.readFile(file), new FileReader("projectReleaseNotes.xml").getTestFileContent());

        java.nio.file.Files.delete(file.toPath());
        java.nio.file.Files.delete(file.toPath().getParent());
    }
}
