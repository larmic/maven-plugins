package de.larmic.maven.bitbucket.dom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by larmic on 18.01.14.
 */
public class FileReader {

    private final String testFileContent;

    public FileReader(final String resourceFileName) {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final InputStream stream = loader.getResourceAsStream(resourceFileName);

        this.testFileContent = getStringFromInputStream(stream);
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
