package de.larmic.maven.bitbucket.dom;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created by larmic on 17.01.14.
 */
public class XmlDocumentConverterTest {

    private final DemoDom demoDom = new DemoDom();

    private XmlDocumentConverter xmlDocumentConverter;

    @BeforeMethod
    public void setUp() throws Exception {
        xmlDocumentConverter = new XmlDocumentConverter();
    }

    @Test
    public void testConvertDocumentToString() throws Exception {
        final String xml = this.xmlDocumentConverter.convertDocumentToString(demoDom.createDemoDocument());

        Assert.assertEquals(xml, demoDom.getTestFileContent());
    }
}
