package de.larmic.maven.bitbucket.dom;

import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;

/**
 * Created by larmic on 17.01.14.
 */
public interface DocumentConverter {

    String convertDocumentToString(final Document doc) throws TransformerException;

}
