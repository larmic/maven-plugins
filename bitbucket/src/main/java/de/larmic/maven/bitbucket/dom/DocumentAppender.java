package de.larmic.maven.bitbucket.dom;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by larmic on 17.01.14.
 */
public class DocumentAppender {

    public void appendElementAttribute(final Document document, final Element parent, final String attributeName, final String data) {
        final Attr attr = document.createAttribute(attributeName);
        attr.setValue(data);
        parent.setAttributeNode(attr);
    }

    public void appendTextNode(final Document document, final Element parent, final String tagName, final String data) {
        final Element element = document.createElement(tagName);
        element.appendChild(document.createTextNode(data != null ? data : ""));
        parent.appendChild(element);
    }

}
