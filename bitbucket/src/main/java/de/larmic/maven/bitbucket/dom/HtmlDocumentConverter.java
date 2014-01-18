package de.larmic.maven.bitbucket.dom;

import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;

/**
 * Created by larmic on 18.01.14.
 */
public class HtmlDocumentConverter implements DocumentConverter {
    @Override
    public String convertDocumentToString(final Document doc) throws TransformerException {
        final StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("\n");
        html.append("<html lang=\"en\">");
        html.append("\n");

        this.convertHtmlHead(doc, html);
        this.convertHtmlBody(doc, html);

        return html.toString();
    }

    private void convertHtmlHead(final Document doc, final StringBuilder html) {
        html.append("<head>");
        html.append("\n");
        html.append("  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />");
        html.append("\n");
        html.append("  <meta charset=\"utf-8\">");
        html.append("\n");
        html.append("  <title>" + doc.getFirstChild().getChildNodes().item(0).getFirstChild().getNodeValue() + "</title>");
        html.append("\n");
        html.append("  <style>");
        html.append("\n");
        html.append("    body {");
        html.append("\n");
        html.append("      margin: 0;");
        html.append("\n");
        html.append("      background: none repeat scroll 0 0 #F5F5F5;");
        html.append("\n");
        html.append("      color: #707070;");
        html.append("\n");
        html.append("      font-size: 12px;");
        html.append("\n");
        html.append("      line-height: 1.66667;");
        html.append("\n");
        html.append("      font-family: Arial,sans-serif;");
        html.append("\n");
        html.append("    }");
        html.append("\n");
        html.append("    h1 {");
        html.append("\n");
        html.append("      margin: 0;");
        html.append("\n");
        html.append("    }");
        html.append("\n");
        html.append("    header {");
        html.append("\n");
        html.append("      background: none repeat scroll 0 0 #205081;");
        html.append("\n");
        html.append("      border-bottom: 1px solid #2E3D54;");
        html.append("\n");
        html.append("      padding: 10px;");
        html.append("\n");
        html.append("      font-family: 'proxima-nova',sans-serif;");
        html.append("\n");
        html.append("      color: #FFFFFF;");
        html.append("\n");
        html.append("      font-size: 20px;");
        html.append("\n");
        html.append("      font-weight: normal;");
        html.append("\n");
        html.append("      letter-spacing: 2px;");
        html.append("\n");
        html.append("      line-height: 1.1;");
        html.append("\n");
        html.append("      text-align: center;");
        html.append("\n");
        html.append("      text-shadow: 0 2px 2px #333333;");
        html.append("\n");
        html.append("    }");
        html.append("\n");
        html.append("    footer {");
        html.append("\n");
        html.append("      text-align: center;");
        html.append("\n");
        html.append("    }");
        html.append("\n");
        html.append("    a {");
        html.append("\n");
        html.append("      text-decoration: none;");
        html.append("\n");
        html.append("      color: #205081;");
        html.append("\n");
        html.append("    }");
        html.append("\n");
        html.append("    a:hover {");
        html.append("\n");
        html.append("      text-decoration: underline;");
        html.append("\n");
        html.append("    }");
        html.append("\n");
        html.append("    #content {");
        html.append("\n");
        html.append("      background: none repeat scroll 0 0 #FFFFFF;");
        html.append("\n");
        html.append("      border: 1px solid #CCCCCC;");
        html.append("\n");
        html.append("      border-radius: 5px;");
        html.append("\n");
        html.append("      margin: 20px auto 60px;");
        html.append("\n");
        html.append("      padding: 30px;");
        html.append("\n");
        html.append("      width: 600px;");
        html.append("\n");
        html.append("    }");
        html.append("\n");
        html.append("  </style>");
        html.append("\n");
        html.append("</head>");
        html.append("\n");
    }

    private void convertHtmlBody(final Document doc, final StringBuilder html) {
        html.append("<body>");
        html.append("\n");
        html.append("  <header role=\"banner\">");
        html.append("\n");
        html.append("    <h1>Release notes</h1>");
        html.append("\n");
        html.append("  </header>");
        html.append("\n");
        html.append("  <div role=\"main\">");
        html.append("\n");
        html.append("    <div id=\"content\">");
        html.append("\n");
        html.append("");
        html.append("\n");
        html.append("    </div>");
        html.append("\n");
        html.append("  </div>");
        html.append("\n");
        html.append("  <footer role=\"contentinfo\">");
        html.append("\n");
        html.append("    <section>");
        html.append("\n");
        html.append("      Release notes are created by <a href=\"https://bitbucket.org/larmicBB/larmic-maven-plugins/overview\">Maven release notes plugin</a>.");
        html.append("\n");
        html.append("    </section>");
        html.append("\n");
        html.append("  </footer>");
        html.append("\n");
        html.append("</body>");
        html.append("\n");
    }
}
