package de.larmic.maven.bitbucket.dom;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.nio.file.Files;

/**
 * Created by larmic on 17.01.14.
 */
public class XmlDocumentConverterTest {

    private final DocumentAppender documentAppender = new DocumentAppender();

    private XmlDocumentConverter xmlDocumentConverter;

    @BeforeMethod
    public void setUp() throws Exception {
        xmlDocumentConverter = new XmlDocumentConverter();
    }

    @Test
    public void testConvertDocumentToString() throws Exception {
        final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        final Document document = dBuilder.newDocument();
        final Element rootElement = document.createElement("root");
        document.appendChild(rootElement);

        this.documentAppender.appendTextNode(document, rootElement, "title", "hi this is a title");

        final Element release1 = document.createElement("release");
        rootElement.appendChild(release1);

        this.documentAppender.appendElementAttribute(document, release1, "version", "1.0");

        final Element ticket1OnVersion1 = document.createElement("ticket");
        release1.appendChild(ticket1OnVersion1);
        this.documentAppender.appendElementAttribute(document, ticket1OnVersion1, "id", "13");

        final Element ticket2OnVersion1 = document.createElement("ticket");
        release1.appendChild(ticket2OnVersion1);
        this.documentAppender.appendElementAttribute(document, ticket2OnVersion1, "id", "14");

        final Element release2 = document.createElement("release");
        rootElement.appendChild(release2);

        this.documentAppender.appendElementAttribute(document, release2, "version", "2.0");

        final Element ticket1OnVersion2 = document.createElement("ticket");
        release2.appendChild(ticket1OnVersion2);
        this.documentAppender.appendElementAttribute(document, ticket1OnVersion2, "id", "7");

        final String xml = this.xmlDocumentConverter.convertDocumentToString(document);
        final File file = new File("bitbucket/src/test/resources/releaseNotes.xml");
        Assert.assertEquals(xml, new String(Files.readAllBytes(file.toPath())));
    }
}
