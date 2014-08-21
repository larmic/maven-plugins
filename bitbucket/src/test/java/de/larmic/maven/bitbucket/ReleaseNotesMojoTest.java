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
        ReflectionUtils.setField(mojo, "basedir", new File(""));
        ReflectionUtils.setField(mojo, "accountName", "larmicBB");
        ReflectionUtils.setField(mojo, "repositorySlug", "larmic-maven-plugins");
        ReflectionUtils.setField(mojo, "title", "testtitle");
        ReflectionUtils.setField(mojo, "ignoreTicketWithNoVersion", true);
        ReflectionUtils.setField(mojo, "relativePath", "/target");

        this.mojo.execute();

        final File xmlFile = new File("target/releaseNotes.xml");
        final File htmlFile = new File("target/releaseNotes.html");

        mojo.getLog().error("File path: " + xmlFile.getAbsolutePath());
        mojo.getLog().error("File path: " + xmlFile.exists());
        mojo.getLog().error("File content: " + new FileReader("projectReleaseNotes.xml").getTestFileContent());

        Assert.assertEquals(Files.readFile(xmlFile), new FileReader("projectReleaseNotes.xml").getTestFileContent());
        Assert.assertEquals(Files.readFile(htmlFile), new FileReader("projectReleaseNotes.html").getTestFileContent());

        java.nio.file.Files.delete(xmlFile.toPath());
        java.nio.file.Files.delete(htmlFile.toPath());

        try {
            java.nio.file.Files.delete(xmlFile.toPath().getParent());
        } catch (Exception e) {

        }
    }
}
