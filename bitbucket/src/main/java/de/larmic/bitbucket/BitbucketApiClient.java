package de.larmic.bitbucket;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
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

    private final boolean basicAuthentication;
    private final HttpHost basicAuthHost;
    private final HttpClientContext basicAuthContext;

    public BitbucketApiClient(final String accountName, final String repositorySlug) {
        this.client = HttpClients.createDefault();

        this.bitbucketApi1RepositoryUrl = String.format(BITBUCKET_API_I_FORMAT, BITBUCKET_API_HOST, accountName, repositorySlug);

        this.basicAuthentication = false;
        this.basicAuthHost = null;
        this.basicAuthContext = null;
    }

    public BitbucketApiClient(final String accountName, final String repositorySlug, final String userName, final String password) {
        this.client = HttpClients.createDefault();

        this.bitbucketApi1RepositoryUrl = String.format(BITBUCKET_API_I_FORMAT, BITBUCKET_API_HOST, accountName, repositorySlug);

        this.basicAuthentication = true;
        this.basicAuthHost = new HttpHost(BITBUCKET_API_HOST, AuthScope.ANY_PORT, "https");
        this.basicAuthContext = createBasicAuthenticationContext(this.basicAuthHost, userName, password);
    }

    public CloseableHttpResponse execute(final String apiUrlPath) throws IOException {
        final HttpGet httpGet = new HttpGet(this.bitbucketApi1RepositoryUrl + apiUrlPath);

        if (this.basicAuthentication) {
          return this.client.execute(basicAuthHost, httpGet, basicAuthContext);
        }

        return this.client.execute(httpGet);
    }

    public void close() throws IOException {
        this.client.close();
    }

    private HttpClientContext createBasicAuthenticationContext(final HttpHost basicAuthHost, final String userName, final String password) {
        final UsernamePasswordCredentials basicCredentials = new UsernamePasswordCredentials(userName, password);
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(basicAuthHost), basicCredentials);

        final AuthCache authCache = new BasicAuthCache();
        final BasicScheme basicAuth = new BasicScheme();
        authCache.put(basicAuthHost, basicAuth);

        final HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credentialsProvider);
        context.setAuthCache(authCache);

        return context;
    }

}
