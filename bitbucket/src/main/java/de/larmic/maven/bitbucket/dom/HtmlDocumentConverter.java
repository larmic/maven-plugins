package de.larmic.maven.bitbucket.dom;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;

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

        this.appendLine(html, "<!DOCTYPE html>");
        this.appendLine(html, "<html lang=\"en\">");

        this.convertHtmlHead(doc, html);
        this.convertHtmlBody(doc, html);

        return html.toString();
    }

    private void convertHtmlHead(final Document doc, final StringBuilder html) {
        this.appendLine(html, "<head>");
        this.appendLine(html, "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />");
        this.appendLine(html, "  <meta charset=\"utf-8\">");
        this.appendLine(html, "  <title>" + doc.getFirstChild().getChildNodes().item(0).getFirstChild().getNodeValue() + "</title>");
        this.appendLine(html, "  <style>");
        this.appendLine(html, "    body {");
        this.appendLine(html, "      margin: 0;");
        this.appendLine(html, "      background: none repeat scroll 0 0 #F5F5F5;");
        this.appendLine(html, "      color: #707070;");
        this.appendLine(html, "      font-size: 12px;");
        this.appendLine(html, "      line-height: 1.66667;");
        this.appendLine(html, "      font-family: Arial,sans-serif;");
        this.appendLine(html, "    }");
        this.appendLine(html, "    h1 {");
        this.appendLine(html, "      margin: 0;");
        this.appendLine(html, "    }");
        this.appendLine(html, "    header {");
        this.appendLine(html, "      background: none repeat scroll 0 0 #205081;");
        this.appendLine(html, "      border-bottom: 1px solid #2E3D54;");
        this.appendLine(html, "      padding: 10px;");
        this.appendLine(html, "      font-family: 'proxima-nova',sans-serif;");
        this.appendLine(html, "      color: #FFFFFF;");
        this.appendLine(html, "      font-size: 20px;");
        this.appendLine(html, "      font-weight: normal;");
        this.appendLine(html, "      letter-spacing: 2px;");
        this.appendLine(html, "      line-height: 1.1;");
        this.appendLine(html, "      text-align: center;");
        this.appendLine(html, "      text-shadow: 0 2px 2px #333333;");
        this.appendLine(html, "    }");
        this.appendLine(html, "    footer {");
        this.appendLine(html, "      text-align: center;");
        this.appendLine(html, "      padding-bottom: 40px;");
        this.appendLine(html, "    }");
        this.appendLine(html, "    a {");
        this.appendLine(html, "      text-decoration: none;");
        this.appendLine(html, "      color: #205081;");
        this.appendLine(html, "    }");
        this.appendLine(html, "    a:hover {");
        this.appendLine(html, "      text-decoration: underline;");
        this.appendLine(html, "    }");
        this.appendLine(html, "    #content {");
        this.appendLine(html, "      background: none repeat scroll 0 0 #FFFFFF;");
        this.appendLine(html, "      border: 1px solid #CCCCCC;");
        this.appendLine(html, "      border-radius: 5px;");
        this.appendLine(html, "      margin: 20px auto 60px;");
        this.appendLine(html, "      padding: 30px;");
        this.appendLine(html, "      width: 600px;");
        this.appendLine(html, "    }");
        this.appendLine(html, "  </style>");
        this.appendLine(html, "</head>");
    }

    private void convertHtmlBody(final Document doc, final StringBuilder html) {
        this.appendLine(html, "<body>");
        this.appendLine(html, "  <header role=\"banner\">");
        this.appendLine(html, "    <h1>" + doc.getFirstChild().getChildNodes().item(0).getFirstChild().getNodeValue() + "</h1>");
        this.appendLine(html, "  </header>");
        this.appendLine(html, "  <div role=\"main\">");
        this.appendLine(html, "    <div id=\"content\">");

        this.convertHtmlContent(doc, html);

        this.appendLine(html, "    </div>");
        this.appendLine(html, "  </div>");
        this.appendLine(html, "  <footer role=\"contentinfo\">");
        this.appendLine(html, "    <section>");
        this.appendLine(html, "      Release notes are created by <a href=\"https://bitbucket.org/larmicBB/larmic-maven-plugins/overview\">Maven release notes plugin</a>.");
        this.appendLine(html, "    </section>");
        this.appendLine(html, "  </footer>");
        this.appendLine(html, "</body>");
    }

    private void convertHtmlContent(final Document doc, final StringBuilder html) {
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

        this.appendLine(html, "      </ul>");
    }

    private boolean isValueSet(final Node attributeNode, final String nodeName) {
        return nodeName.equals(attributeNode.getNodeName()) && attributeNode.getFirstChild().getNodeValue() != null && !"".equals(attributeNode.getFirstChild().getNodeValue());
    }

    private void appendLine(final StringBuilder html, final String text) {
        html.append(text);
        html.append("\n");
    }
}
