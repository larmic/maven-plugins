package de.larmic.maven.bitbucket;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * @goal createReleaseNotes
 */
public class ReleaseNotesMojo extends AbstractMojo {

    public static final int MAX_ISSUE_LIMIT = 50;
    /**
     * @parameter expression="${project.build.outputDirectory}"
     */
    private File targetDirectory;

    /**
     * @parameter expression="${bitbucket.accountName}"
     */
    private String accountName;

    /**
     * @parameter expression="${bitbucket.accountName}"
     */
    private String repositorySlug;

    /**
     * @parameter expression="${bitbucket.userName}"
     */
    private String userName;

    /**
     * @parameter expression="${bitbucket.password}"
     */
    private String password;

    /**
     * @parameter expression="${releasenotes.title}" default-value="Release notes ${project.name}"
     */
    private String title;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final Path xmlFile = new File(targetDirectory.getAbsolutePath() + "/releasenotes.xml").toPath();

        try {
            Files.delete(xmlFile);
            Files.createFile(xmlFile);
            final Map<String, List<JSONObject>> issues = findIssues();

            final String content = this.convertDocumentToString(this.createReleaseNotesDocument(issues));

            Files.write(xmlFile, content.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new MojoExecutionException("Could not create release notes file " + xmlFile.getFileName(), e);
        }
    }

    private Map<String, List<JSONObject>> findIssues() throws IOException {
        final Map<String, List<JSONObject>> issues = new HashMap<>();

        final BitbucketApiClient bitbucketApiClient = createBitbucketApiClient();

        try {
            final CloseableHttpResponse response = bitbucketApiClient.execute(createApiQuery(1, 0));

            final Integer count = ((Long) this.createJSON(response).get("count")).intValue();

            if (count > 0) {
                for (int issueNumber = 0; issueNumber <= count / MAX_ISSUE_LIMIT; issueNumber += MAX_ISSUE_LIMIT) {
                    final int startIssue = issueNumber;
                    final CloseableHttpResponse r = bitbucketApiClient.execute(createApiQuery(MAX_ISSUE_LIMIT, startIssue));

                    final ArrayList<JSONObject> part = (JSONArray) createJSON(r).get("issues");

                    for (final JSONObject issue : part) {
                        final JSONObject metadata = (JSONObject) issue.get("metadata");
                        final String version = metadata.get("version") != null ? (String) metadata.get("version") : "";

                        if (issues.get(version) == null) {
                            issues.put(version, new ArrayList<JSONObject>());
                        }

                        issues.get(version).add(issue);
                    }
                }
            }

        } finally {
            bitbucketApiClient.close();
        }

        return issues;
    }

    private String createApiQuery(final int maxIssueLimit, final int startIssue) {
        return "issues/?limit=" + maxIssueLimit + "&start=" + startIssue + "&sort=local_id&status=resolved";
    }

    public JSONObject createJSON(final CloseableHttpResponse response) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

        try {
            return (JSONObject) new JSONParser().parse(reader);
        } catch (ParseException e) {
            throw new IOException("Could not parse input stream", e);
        }
    }

    private BitbucketApiClient createBitbucketApiClient() {
        if (this.userName == null && !"".equals(this.userName)) {
            return new BitbucketApiClient(this.accountName, this.repositorySlug);
        }

        return new BitbucketApiClient(this.accountName, this.repositorySlug, this.userName, this.password);
    }

    private Document createReleaseNotesDocument(final Map<String, List<JSONObject>> issues) throws MojoExecutionException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            final Document document = dBuilder.newDocument();
            final Element rootElement = document.createElement("releasenotes");
            document.appendChild(rootElement);

            this.appendTextNode(document, rootElement, "title", this.title);

            final SortedSet<String> versions = sortKeys(issues.keySet());

            for (final String version : versions) {
                final Element release = document.createElement("release");
                rootElement.appendChild(release);

                appendElementAttribute(document, release, "version", !"".equals(version) ? version : "no version");

                for (final JSONObject issue : issues.get(version)) {
                    final JSONObject metadata = (JSONObject) issue.get("metadata");

                    final Element ticket = document.createElement("ticket");
                    release.appendChild(ticket);

                    appendElementAttribute(document, ticket, "id", issue.get("local_id").toString());

                    this.appendTextNode(document, ticket, "title", (String) issue.get("title"));
                    this.appendTextNode(document, ticket, "priority", (String) issue.get("priority"));
                    this.appendTextNode(document, ticket, "kind", (String) metadata.get("kind"));
                    this.appendTextNode(document, ticket, "milestone", (String) metadata.get("milestone"));
                    this.appendTextNode(document, ticket, "component", (String) metadata.get("component"));
                }
            }

            return document;
        } catch (ParserConfigurationException e) {
            throw new MojoExecutionException("Could not create document", e);
        }
    }

    private void appendElementAttribute(final Document document, final Element parent, final String attributeName, final String data) {
        final Attr attr = document.createAttribute(attributeName);
        attr.setValue(data);
        parent.setAttributeNode(attr);
    }

    private void appendTextNode(final Document document, final Element parent, final String tagName, final String data) {
        final Element element = document.createElement(tagName);
        element.appendChild(document.createTextNode(data != null ? data : ""));
        parent.appendChild(element);
    }


    private SortedSet<String> sortKeys(final Set<String> keys) {
        final Comparator<String> stringComparator = new Comparator<String>() {
            @Override
            public int compare(final String o1, final String o2) {
                if ("".equals(o1) && !"".equals(o2) || "".equals(o2) && !"".equals(o1)) {
                    return -1;
                }

                return o1.compareTo(o2);
            }
        };
        final SortedSet<String> sortedKeys = new TreeSet<>(stringComparator);
        sortedKeys.addAll(keys);
        return sortedKeys;
    }


    private String convertDocumentToString(final Document doc) throws MojoExecutionException {
        try {
            final StringWriter sw = new StringWriter();
            final TransformerFactory tf = TransformerFactory.newInstance();
            final Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            return sw.toString();
        } catch (Exception ex) {
            throw new MojoExecutionException("Error converting to String", ex);
        }
    }
}
