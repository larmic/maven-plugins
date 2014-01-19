package de.larmic.maven.bitbucket;

import de.larmic.maven.bitbucket.dom.DocumentAppender;
import de.larmic.maven.bitbucket.dom.HtmlDocumentConverter;
import de.larmic.maven.bitbucket.dom.XmlDocumentConverter;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.maven.plugin.MojoExecutionException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * @goal createReleaseNotes
 */
public class ReleaseNotesMojo extends AbstractBitbucketMojo {

    public static final int MAX_ISSUE_LIMIT = 50;

    private final DocumentAppender documentAppender = new DocumentAppender();


    /**
     * @parameter expression="${basedir}"
     */
    private File basedir;

    /**
     * @parameter expression="${releasenotes.title}" default-value="Release notes ${project.name}"
     */
    private String title;

    /**
     * @parameter expression="${releasenotes.ignoreTicketWithNoVersion}" default-value="false"
     */
    private boolean ignoreTicketWithNoVersion;

    /**
     * @parameter expression="${releasenotes.hideTicketNumber}" default-value="false"
     */
    private boolean hideTicketNumber;

    /**
     * @parameter expression="${releasenotes.hideAuthor}" default-value="false"
     */
    private boolean hideAuthor;

    /**
     * @parameter expression="${releasenotes.hidePriority}" default-value="false"
     */
    private boolean hidePriority;

    /**
     * @parameter expression="${releasenotes.hideKind}" default-value="false"
     */
    private boolean hideKind;

    /**
     * @parameter expression="${releasenotes.relativePath}" default-value="/src/main/webapp/"
     */
    private String relativePath;

    @Override
    public void executeMojo() throws MojoExecutionException {
        final XmlDocumentConverter xmlDocumentConverter = new XmlDocumentConverter();
        final HtmlDocumentConverter htmlDocumentConverter = new HtmlDocumentConverter(this.hideAuthor, this.hidePriority, this.hideKind, this.hideTicketNumber);

        try {
            final Map<String, List<JSONObject>> issues = findIssues();

            final String xmlContent = xmlDocumentConverter.convertDocumentToString(this.createReleaseNotesDocument(issues));
            final Path xmlFile = new File(this.basedir.getAbsolutePath() + this.relativePath + "/releasenotes.xml").toPath();
            this.writeContent(xmlContent, xmlFile);

            final String htmlContent = htmlDocumentConverter.convertDocumentToString(this.createReleaseNotesDocument(issues));
            final Path htmlFile = new File(this.basedir.getAbsolutePath() + this.relativePath + "/releasenotes.html").toPath();
            this.writeContent(htmlContent, htmlFile);
        } catch (IOException e) {
            throw new MojoExecutionException("Could not create release notes file", e);
        } catch (TransformerException e) {
            throw new MojoExecutionException("Could not transform document to xml", e);
        }
    }

    private void writeContent(final String xmlContent, final Path xmlFile) throws IOException {
        if (Files.exists(xmlFile)) {
            Files.delete(xmlFile);
        }

        if (!Files.exists(xmlFile.getParent())) {
            Files.createDirectories(xmlFile.getParent());
        }

        Files.createFile(xmlFile);


        Files.write(xmlFile, xmlContent.getBytes(), StandardOpenOption.CREATE);

        getLog().info(xmlFile.toUri() + " created.");
    }

    private Map<String, List<JSONObject>> findIssues() throws IOException, MojoExecutionException {
        final Map<String, List<JSONObject>> issues = new HashMap<>();

        final BitbucketApiClient bitbucketApiClient = createBitbucketApiClient();

        try {
            final String apiQuery = createApiQuery(1, 0);
            final CloseableHttpResponse response = bitbucketApiClient.execute(apiQuery);

            getLog().info("Opening " + bitbucketApiClient.getBitbucketApi1RepositoryUrl() + apiQuery);

            final StatusLine responseStatusLine = response.getStatusLine();

            if (responseStatusLine.getStatusCode() != BitbucketApiClient.STATUS_CODE_OK) {
                getLog().error(responseStatusLine.getStatusCode() + " (" + responseStatusLine.getReasonPhrase() + ") username or password incorrect.");
                throw new MojoExecutionException(responseStatusLine.getStatusCode() + " (" + responseStatusLine.getReasonPhrase() + ") " +
                        bitbucketApiClient.getBitbucketApi1RepositoryUrl() + apiQuery);
            } else {
                final Integer count = ((Long) this.createJSON(response).get("count")).intValue();

                if (count > 0) {
                    for (int issueNumber = 0; issueNumber <= count / MAX_ISSUE_LIMIT; issueNumber += MAX_ISSUE_LIMIT) {
                        final int startIssue = issueNumber;
                        final CloseableHttpResponse r = bitbucketApiClient.execute(createApiQuery(MAX_ISSUE_LIMIT, startIssue));

                        final ArrayList<JSONObject> part = (JSONArray) createJSON(r).get("issues");

                        for (final JSONObject issue : part) {
                            final JSONObject metadata = (JSONObject) issue.get("metadata");
                            final String version = metadata.get("version") != null ? (String) metadata.get("version") : "";

                            if (!"".equals(version) || !ignoreTicketWithNoVersion) {
                                if (issues.get(version) == null) {
                                    issues.put(version, new ArrayList<JSONObject>());
                                }

                                issues.get(version).add(issue);
                            }
                        }
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

    private Document createReleaseNotesDocument(final Map<String, List<JSONObject>> issues) throws MojoExecutionException {
        final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            final Document document = dBuilder.newDocument();
            final Element rootElement = document.createElement("releasenotes");
            document.appendChild(rootElement);

            this.documentAppender.appendTextNode(document, rootElement, "title", this.title);

            final SortedSet<String> versions = sortKeys(issues.keySet());

            for (final String version : versions) {
                final Element release = document.createElement("release");
                rootElement.appendChild(release);

                this.documentAppender.appendElementAttribute(document, release, "version", !"".equals(version) ? version : "no version");

                for (final JSONObject issue : issues.get(version)) {
                    final JSONObject metadata = (JSONObject) issue.get("metadata");

                    final Element ticket = document.createElement("ticket");
                    release.appendChild(ticket);

                    this.documentAppender.appendElementAttribute(document, ticket, "id", issue.get("local_id").toString());

                    this.documentAppender.appendTextNode(document, ticket, "title", (String) issue.get("title"));
                    this.documentAppender.appendTextNode(document, ticket, "priority", (String) issue.get("priority"));
                    this.documentAppender.appendTextNode(document, ticket, "kind", (String) metadata.get("kind"));
                    this.documentAppender.appendTextNode(document, ticket, "milestone", (String) metadata.get("milestone"));
                    this.documentAppender.appendTextNode(document, ticket, "component", (String) metadata.get("component"));
                }
            }

            return document;
        } catch (ParserConfigurationException e) {
            throw new MojoExecutionException("Could not create document", e);
        }
    }


    private SortedSet<String> sortKeys(final Set<String> keys) {
        final Comparator<String> stringComparator = new Comparator<String>() {
            @Override
            public int compare(final String o1, final String o2) {
                if ("".equals(o1) && !"".equals(o2) || "".equals(o2) && !"".equals(o1)) {
                    return -1;
                }

                return o2.compareTo(o1);
            }
        };
        final SortedSet<String> sortedKeys = new TreeSet<>(stringComparator);
        sortedKeys.addAll(keys);
        return sortedKeys;
    }
}
