package de.larmic.maven.bitbucket.dom;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import java.io.StringWriter;

/**
 * Created by larmic on 18.01.14.
 */
public class HtmlDocumentConverter implements DocumentConverter {

    private final boolean hideAuthor;
    private final boolean hidePriority;
    private final boolean hideKind;
    private final boolean hideTicketNumber;

    public HtmlDocumentConverter(final boolean hideAuthor, final boolean hidePriority, final boolean hideKind, final boolean hideTicketNumber) {
        this.hideAuthor = hideAuthor;
        this.hidePriority = hidePriority;
        this.hideKind = hideKind;
        this.hideTicketNumber = hideTicketNumber;
    }

    @Override
    public String convertDocumentToString(final Document doc) throws TransformerException {
        final StringBuilder html = new StringBuilder();

        final String applicationName = doc.getFirstChild().getChildNodes().item(0).getFirstChild().getNodeValue();

        final VelocityEngine engine = new VelocityEngine();
        engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        engine.init();

        final Template template = engine.getTemplate("/template.vm");

        final VelocityContext context = new VelocityContext();
        context.put("applicationName", applicationName);
        context.put("content", this.convertHtmlContent(doc));

        final StringWriter writer = new StringWriter();
        template.merge(context, writer);

        html.append(writer.toString());

        return html.toString();
    }


    private String convertHtmlContent(final Document doc) {
        final StringBuilder html = new StringBuilder();

        this.appendLine(html, "      <ul>");

        for (int release = 1; release < doc.getFirstChild().getChildNodes().getLength(); release++) {
            final Node releaseNode = doc.getFirstChild().getChildNodes().item(release);

            this.appendLine(html, "        <li>");
            this.appendLine(html, "          " + releaseNode.getAttributes().getNamedItem("version").getTextContent());
            this.appendLine(html, "          <ul>");

            for (int ticket = 0; ticket < releaseNode.getChildNodes().getLength(); ticket++) {
                final Node ticketNode = releaseNode.getChildNodes().item(ticket);

                this.appendLine(html, "            <li>");
                this.appendLine(html, "              " + ticketNode.getFirstChild().getFirstChild().getNodeValue());

                if (!(hideTicketNumber && hideKind && hidePriority && hideAuthor)) {
                    this.appendLine(html, "              <ul>");

                    if (!hideTicketNumber) {
                        final String ticketNumber = ticketNode.getAttributes().item(0).getNodeValue();
                        this.appendLine(html, "                <li>Ticketnummer: " + ticketNumber + "</li>");
                    }

                    String author = "-";
                    String priority = "-";
                    String kind = "-";

                    for (int attribute = 0; attribute < ticketNode.getChildNodes().getLength(); attribute++) {
                        final Node attributeNode = ticketNode.getChildNodes().item(attribute);

                        if (isValueSet(attributeNode, "priority")) {
                            priority = attributeNode.getFirstChild().getNodeValue();
                        } else if (isValueSet(attributeNode, "kind")) {
                            kind = attributeNode.getFirstChild().getNodeValue();
                        } else if (isValueSet(attributeNode, "author")) {
                            author = attributeNode.getFirstChild().getNodeValue();
                        }
                    }

                    if (!hideAuthor) {
                        this.appendLine(html, "                <li>Autor: " + author + "</li>");
                    }
                    if (!hidePriority) {
                        this.appendLine(html, "                <li>Prioritaet: " + priority + "</li>");
                    }
                    if (!hideKind) {
                        this.appendLine(html, "                <li>Art: " + kind + "</li>");
                    }

                    this.appendLine(html, "              </ul>");
                }

                this.appendLine(html, "            </li>");

            }

            this.appendLine(html, "          </ul>");
            this.appendLine(html, "        </li>");
        }

        html.append("      </ul>");

        return html.toString();
    }

    private boolean isValueSet(final Node attributeNode, final String nodeName) {
        return nodeName.equals(attributeNode.getNodeName()) && attributeNode.getFirstChild().getNodeValue() != null && !"".equals(attributeNode.getFirstChild().getNodeValue());
    }

    private void appendLine(final StringBuilder html, final String text) {
        html.append(text);
        html.append("\n");
    }
}
