package de.larmic.maven.bitbucket.dom;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created by larmic on 18.01.14.
 */
public class HtmlDocumentConverterTest {

    private final DemoDom demoDom = new DemoDom("demoDom.html");

    private DocumentConverter htmlDocumentConverter;

    @BeforeMethod
    public void setUp() throws Exception {
        htmlDocumentConverter = new HtmlDocumentConverter();
    }

    @Test
    public void testConvertDocumentToString() throws Exception {
        final String xml = this.htmlDocumentConverter.convertDocumentToString(demoDom.createDemoDocument());

        Assert.assertEquals(xml, demoDom.getTestFileContent());
    }

}
