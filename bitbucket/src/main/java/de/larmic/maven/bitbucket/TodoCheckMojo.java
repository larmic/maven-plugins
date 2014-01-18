package de.larmic.maven.bitbucket;

import org.apache.http.client.methods.CloseableHttpResponse;
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
public class TodoCheckMojo extends AbstractBitbucketMojo {

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

    public void executeMojo() throws MojoExecutionException {
        getLog().info("");

        int numberOfTodos = 0;

        numberOfTodos += this.walkFileTreeInDirectory("Source directory", sourceDirectory);
        numberOfTodos += this.walkFileTreeInDirectory("Test source directory", testSourceDirectory);

        getLog().info("Scan completed. Found " + numberOfTodos + " TODOs.");
        getLog().info("");
    }

    private int walkFileTreeInDirectory(final String directoryType, final File directory) {
        if (directory == null) {
            getLog().info(directoryType + " does not exists. Directory will be skipped");

            return 0;
        }

        getLog().info("Scanning in directory " + directory.getAbsolutePath());
        getLog().info("");

        final int[] numberOfTodos = new int[1];

        try {
            Files.walkFileTree(directory.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                    final Map<Integer, TodoMatcher> todos = readTodosFromFile(file);

                    if (!todos.isEmpty()) {
                        for (Map.Entry<Integer, TodoMatcher> entry : todos.entrySet()) {
                            logTodo(file.getFileName().toString(), todos.get(entry.getKey()), entry.getKey());
                        }
                    }

                    numberOfTodos[0] += todos.size();

                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            getLog().error("Could not walk directory", e);
        }

        if (numberOfTodos[0] > 0) {
            getLog().info("");
        }
        getLog().info("Found " + numberOfTodos[0] + " in directory " + directory.getAbsolutePath());
        getLog().info("");

        return numberOfTodos[0];
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

    private void logTodo(final String fileName, final TodoMatcher matcher, final int lineNumber) throws IOException {
        if (matcher.getTicketNumber() != null) {
            final BitbucketApiClient bitbucketApiClient = createBitbucketApiClient();

            try {
                final CloseableHttpResponse response = bitbucketApiClient.execute("issues/" + matcher.getTicketNumber());

                if (response.getStatusLine().getStatusCode() != BitbucketApiClient.STATUS_CODE_OK) {
                    getLog().error(createTodoLog(fileName, lineNumber, matcher.getTodoDescription(), "Could not find ticket"));
                } else {
                    if (isTicketClosed(response)) {
                        getLog().error(createTodoLog(fileName, lineNumber, matcher.getTodoDescription(), "Ticket is resolved"));
                    } else {
                        getLog().info(createTodoLog(fileName, lineNumber, matcher.getTodoDescription()));
                    }
                }
            } finally {
                bitbucketApiClient.close();
            }
        } else {
            getLog().info(createTodoLog(fileName, lineNumber, matcher.getTodoDescription()));
        }
    }

    private String createTodoLog(final String fileName, final int lineNumber, final String todoText) {
        return this.createTodoLog(fileName, lineNumber, todoText, null);
    }

    private String createTodoLog(final String fileName, final int lineNumber, final String todoText, final String optional) {
        final StringBuilder log = new StringBuilder();

        if (optional != null && !"".equals(optional)) {
            log.append("[");
            log.append(optional.toUpperCase());
            log.append("] ");
        }
        log.append(fileName);
        log.append(" Line: ");
        log.append(lineNumber + 1);
        log.append(", Text: ");
        log.append(todoText);


        return log.toString();
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

        // TODO use this call instead?
        // List<String> lines = Files.readAllLines(file, Charset.defaultCharset());

        final InputStream in = Files.newInputStream(file);
        final CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
        decoder.onMalformedInput(CodingErrorAction.IGNORE);
        return new BufferedReader(new InputStreamReader(in, decoder));
    }
}
