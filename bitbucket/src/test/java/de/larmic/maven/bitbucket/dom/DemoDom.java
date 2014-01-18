package de.larmic.maven.bitbucket.dom;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by larmic on 18.01.14.
 */
public class DemoDom {

    private final DocumentAppender documentAppender = new DocumentAppender();
    private final String testFileContent;


    public DemoDom(final String resourceFileName) {
        this.testFileContent = new FileReader(resourceFileName).getTestFileContent();
    }

    public Document createDemoDocument() throws ParserConfigurationException {
        final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        final Document document = dBuilder.newDocument();
        final Element rootElement = document.createElement("root");
        document.appendChild(rootElement);

        this.documentAppender.appendTextNode(document, rootElement, "title", "hi this is a title");

        final Element release1 = document.createElement("release");
        rootElement.appendChild(release1);

        this.documentAppender.appendElementAttribute(document, release1, "version", "Release 1");

        final Element ticket1Version1 = document.createElement("ticket");
        release1.appendChild(ticket1Version1);
        this.documentAppender.appendElementAttribute(document, ticket1Version1, "id", "7");
        this.documentAppender.appendTextNode(document, ticket1Version1, "title", "Test release note ticket 1 (release 1)");
        this.documentAppender.appendTextNode(document, ticket1Version1, "author", "larmic");
        this.documentAppender.appendTextNode(document, ticket1Version1, "priority", "major");
        this.documentAppender.appendTextNode(document, ticket1Version1, "kind", "proposal");

        final Element ticket2Version1 = document.createElement("ticket");
        release1.appendChild(ticket2Version1);
        this.documentAppender.appendElementAttribute(document, ticket2Version1, "id", "8");
        this.documentAppender.appendTextNode(document, ticket2Version1, "title", "Test release note ticket 2 (release 1)");
        this.documentAppender.appendTextNode(document, ticket1Version1, "author", "");
        this.documentAppender.appendTextNode(document, ticket2Version1, "priority", "major");
        this.documentAppender.appendTextNode(document, ticket2Version1, "kind", "");

        final Element ticket3Version1 = document.createElement("ticket");
        release1.appendChild(ticket3Version1);
        this.documentAppender.appendElementAttribute(document, ticket3Version1, "id", "10");
        this.documentAppender.appendTextNode(document, ticket3Version1, "title", "Test release note ticket 3 (release 1)");
        this.documentAppender.appendTextNode(document, ticket3Version1, "priority", "major");
        this.documentAppender.appendTextNode(document, ticket3Version1, "kind", "-");

        final Element release2 = document.createElement("release");
        rootElement.appendChild(release2);

        this.documentAppender.appendElementAttribute(document, release2, "version", "Release 2");

        final Element ticket1Version2 = document.createElement("ticket");
        release2.appendChild(ticket1Version2);
        this.documentAppender.appendElementAttribute(document, ticket1Version2, "id", "9");
        this.documentAppender.appendTextNode(document, ticket1Version2, "title", "Test release note ticket 1 (release 2)");
        this.documentAppender.appendTextNode(document, ticket1Version2, "priority", "blocker");
        this.documentAppender.appendTextNode(document, ticket1Version2, "kind", "task");

        final Element ticket2Version2 = document.createElement("ticket");
        release2.appendChild(ticket2Version2);
        this.documentAppender.appendElementAttribute(document, ticket2Version2, "id", "11");
        this.documentAppender.appendTextNode(document, ticket2Version2, "title", "Test release note ticket 2 (release 2)");
        this.documentAppender.appendTextNode(document, ticket2Version2, "priority", "trivial");
        this.documentAppender.appendTextNode(document, ticket2Version2, "kind", "enhancement");

        return document;
    }

    public String getTestFileContent() {
        return testFileContent;
    }


}
