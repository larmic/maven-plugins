package de.larmic.maven.bitbucket;

import org.testng.annotations.BeforeMethod;

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

    //@Test
    public void testExecute() throws Exception {
        ReflectionUtils.setField(mojo, "targetDirectory", new File("./bitbucket/target"));
        ReflectionUtils.setField(mojo, "accountName", "larmicBB");
        ReflectionUtils.setField(mojo, "repositorySlug", "larmic-maven-plugins");
        ReflectionUtils.setField(mojo, "title", "testtitle");

        this.mojo.execute();
    }
}
