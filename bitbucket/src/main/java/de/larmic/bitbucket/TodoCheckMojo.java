package de.larmic.bitbucket;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @goal checkTodo
 */
public class TodoCheckMojo extends AbstractMojo {

    public static final String REST_REPOSITORY_URL = "https://bitbucket.org/api/1.0/repositories/larmicBB/larmic-maven-plugins/";
    public static final String TICKET_STATUS_RESOLVED = "resolved";
    public static final String TICKET_STATUS = "status";

    /**
     * @parameter expression="${project.build.sourceDirectory}"
     */
    private File sourceDirectory;

    /**
     * @parameter expression="${project.build.testSourceDirectory}"
     */
    private File testSourceDirectory;

    public void execute() throws MojoExecutionException {
        try {
            walkFileTreeInDirectory(sourceDirectory);
            walkFileTreeInDirectory(testSourceDirectory);
        } catch (IOException e) {
            getLog().error("Could not walk directory", e);
        }
    }

    private void walkFileTreeInDirectory(final File directory) throws IOException {
        getLog().info("Checking TODOs in directory " + directory.getAbsolutePath());

        Files.walkFileTree(directory.toPath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                final Map<Integer, TodoMatcher> todos = readTodosFromFile(file);

                if (!todos.isEmpty()) {
                    getLog().info("Found " + todos.size() + " TODOs in file " + file.getFileName());

                    for (Map.Entry<Integer, TodoMatcher> entry : todos.entrySet()) {
                        logTodo(todos.get(entry.getKey()), entry.getKey());
                    }
                }

                return FileVisitResult.CONTINUE;
            }
        });
    }

    private Map<Integer, TodoMatcher> readTodosFromFile(final Path file) throws IOException {
        final BufferedReader bufferedReader = createBufferedReader(file);
        final Map<Integer, TodoMatcher> todos = new HashMap<>();

        String line;
        int lineNumber = 0;

        while ((line = bufferedReader.readLine()) != null) {
            final TodoMatcher todoMatcher = new TodoMatcher(line);

            if (todoMatcher.matches()) {
                todos.put(lineNumber, todoMatcher);
            }

            lineNumber++;
        }

        bufferedReader.close();

        // using tree map to sort keys
        return new TreeMap<>(todos);
    }

    private void logTodo(final TodoMatcher matcher, final int lineNumber) throws IOException {
        if (matcher.getTicketNumber() != null) {
            final CloseableHttpClient httpclient = HttpClients.createDefault();

            try {
                final HttpGet httpGet = new HttpGet(REST_REPOSITORY_URL + "issues/" + matcher.getTicketNumber());
                final CloseableHttpResponse response = httpclient.execute(httpGet);

                if (response.getStatusLine().getStatusCode() != 200) {
                    getLog().error("Line " + (lineNumber + 1) + ": Could not find ticket number #" + matcher.getTicketNumber());
                } else {
                    if (isTicketClosed(response)) {
                        getLog().error("Line " + (lineNumber + 1) + ": Ticket #" + matcher.getTicketNumber() + " is resolved");
                    } else {
                        getLog().info("Line " + (lineNumber + 1) + ": // TODO #" + matcher.getTicketNumber() + " " + matcher.getTodoDescription());
                    }
                }
            } finally {
                httpclient.close();
            }
        } else {
            getLog().info("Line " + (lineNumber + 1) + ": // TODO " + matcher.getTodoDescription());
        }
    }

    private boolean isTicketClosed(final CloseableHttpResponse response) throws IOException {
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            final JSONObject json = (JSONObject) new JSONParser().parse(reader);
            return TICKET_STATUS_RESOLVED.equals(json.get(TICKET_STATUS));
        } catch (ParseException e) {
            throw new IOException(e);
        }
    }

    private BufferedReader createBufferedReader(final Path file) throws IOException {
        // REVIEW default J1.7 way throws malformed exception when opening wrong encoded files
        //CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
        //decoder.onMalformedInput(CodingErrorAction.IGNORE);
        //final BufferedReader bufferedReader = Files.newBufferedReader(file, decoder.charset());

        final InputStream in = Files.newInputStream(file);
        final CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
        decoder.onMalformedInput(CodingErrorAction.IGNORE);
        return new BufferedReader(new InputStreamReader(in, decoder));
    }
}
