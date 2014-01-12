package de.larmic.bitbucket;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by larmic on 11.01.14.
 */
public class BitbucketApiClientTest {

    public static final String BITBUCKET_ACCOUNT_NAME = "larmicBB";
    public static final String PRIVATE_REPOSITORY = "larmic-ts";
    public static final String PUBLIC_REPOSITORY = "larmic-maven-plugins";
    public static final String BITBUCKET_USER_NAME = "larmicBB";

    @Test
    public void testExecuteNoAuthentication() throws Exception {
        final BitbucketApiClient client = new BitbucketApiClient(BITBUCKET_ACCOUNT_NAME, PUBLIC_REPOSITORY);

        try {
            final CloseableHttpResponse response = client.execute("issues/?limit=1&start=0&sort=local_id&status=resolved");
            Assert.assertEquals(response.getStatusLine().getStatusCode(), 200);
        } finally {
            client.close();
        }
    }

    @Test
    public void testExecuteAuthenticationFailed() throws Exception {
        final String wrongPassword = "x";

        final BitbucketApiClient client = new BitbucketApiClient(BITBUCKET_ACCOUNT_NAME, PRIVATE_REPOSITORY, BITBUCKET_USER_NAME, wrongPassword);

        try {
            final CloseableHttpResponse response = client.execute("issues/?limit=1&start=0&sort=local_id&status=resolved");
            Assert.assertEquals(response.getStatusLine().getStatusCode(), 401);
        } finally {
            client.close();
        }
    }

    @Test(enabled = false, description = "set correct password to enable test")
    public void testExecuteAuthentication() throws Exception {
        final BitbucketApiClient client = new BitbucketApiClient(BITBUCKET_ACCOUNT_NAME, PRIVATE_REPOSITORY, BITBUCKET_USER_NAME, "x");

        try {
            final CloseableHttpResponse response = client.execute("issues/?limit=1&start=0&sort=local_id&status=resolved");
            Assert.assertEquals(response.getStatusLine().getStatusCode(), 200);
        } finally {
            client.close();
        }
    }
}
