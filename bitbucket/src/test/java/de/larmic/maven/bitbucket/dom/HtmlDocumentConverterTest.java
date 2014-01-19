package de.larmic.maven.bitbucket.dom;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by larmic on 18.01.14.
 */
public class HtmlDocumentConverterTest {

    @Test
    public void testConvertDocumentToString() throws Exception {
        final DemoDom demoDom = new DemoDom("demoDom.html");
        final HtmlDocumentConverter htmlDocumentConverter = new HtmlDocumentConverter(false, false, false, false);

        final String xml = htmlDocumentConverter.convertDocumentToString(demoDom.createDemoDocument());

        Assert.assertEquals(xml, demoDom.getTestFileContent());
    }

    @Test
    public void testConvertDocumentToStringNoAuthor() throws Exception {
        final DemoDom demoDom = new DemoDom("demoDomNoAuthor.html");
        final HtmlDocumentConverter htmlDocumentConverter = new HtmlDocumentConverter(true, false, false, false);

        final String xml = htmlDocumentConverter.convertDocumentToString(demoDom.createDemoDocument());

        Assert.assertEquals(xml, demoDom.getTestFileContent());
    }

    @Test
    public void testConvertDocumentToStringNoPriority() throws Exception {
        final DemoDom demoDom = new DemoDom("demoDomNoPriority.html");
        final HtmlDocumentConverter htmlDocumentConverter = new HtmlDocumentConverter(false, true, false, false);

        final String xml = htmlDocumentConverter.convertDocumentToString(demoDom.createDemoDocument());

        Assert.assertEquals(xml, demoDom.getTestFileContent());
    }

    @Test
    public void testConvertDocumentToStringNoKind() throws Exception {
        final DemoDom demoDom = new DemoDom("demoDomNoKind.html");
        final HtmlDocumentConverter htmlDocumentConverter = new HtmlDocumentConverter(false, false, true, false);

        final String xml = htmlDocumentConverter.convertDocumentToString(demoDom.createDemoDocument());

        Assert.assertEquals(xml, demoDom.getTestFileContent());
    }

    @Test
    public void testConvertDocumentToStringNoTicketNumber() throws Exception {
        final DemoDom demoDom = new DemoDom("demoDomNoTicketNumber.html");
        final HtmlDocumentConverter htmlDocumentConverter = new HtmlDocumentConverter(false, false, false, true);

        final String xml = htmlDocumentConverter.convertDocumentToString(demoDom.createDemoDocument());

        Assert.assertEquals(xml, demoDom.getTestFileContent());
    }

    @Test
    public void testConvertDocumentToStringNoAll() throws Exception {
        final DemoDom demoDom = new DemoDom("demoDomNoAll.html");
        final HtmlDocumentConverter htmlDocumentConverter = new HtmlDocumentConverter(true, true, true, true);

        final String xml = htmlDocumentConverter.convertDocumentToString(demoDom.createDemoDocument());

        Assert.assertEquals(xml, demoDom.getTestFileContent());
    }

}
