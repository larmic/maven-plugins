package de.larmic.maven.bitbucket.dom;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by larmic on 18.01.14.
 */
public class DemoDom {

    private final DocumentAppender documentAppender = new DocumentAppender();
    private final String testFileContent;


    public DemoDom() {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final InputStream stream = loader.getResourceAsStream("demoDom.xml");

        this.testFileContent = getStringFromInputStream(stream);
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

        return document;
    }

    public String getTestFileContent() {
        return testFileContent;
    }

    private static String getStringFromInputStream(final InputStream is) {
        final StringBuilder sb = new StringBuilder();

        BufferedReader br = null;
        String fileContent;

        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((fileContent = br.readLine()) != null) {
                sb.append(fileContent);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }

}
