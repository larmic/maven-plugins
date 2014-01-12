package de.larmic.bitbucket;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

/**
 * Created by larmic on 12.01.14.
 */
public class BitbucketApiClient {

    public static final String BITBUCKET_API_HOST = "api.bitbucket.org";
    public static final String BITBUCKET_API_I_FORMAT = "https://%s/1.0/repositories/%s/%s/";

    private final CloseableHttpClient client;
    private final String bitbucketApi1RepositoryUrl;

    public BitbucketApiClient(final String accountName, final String repositorySlug) {
        this.client = HttpClients.createDefault();

        this.bitbucketApi1RepositoryUrl = String.format(BITBUCKET_API_I_FORMAT, BITBUCKET_API_HOST, accountName, repositorySlug);
    }

    public CloseableHttpResponse execute(final String apiUrlPath) throws IOException {
        final HttpGet httpGet = new HttpGet(this.bitbucketApi1RepositoryUrl + apiUrlPath);
        return this.client.execute(httpGet);
    }

    public void close() throws IOException {
        this.client.close();
    }

}
