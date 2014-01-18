package de.larmic.maven.bitbucket;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Created by larmic on 18.01.14.
 */
public abstract class AbstractBitbucketMojo extends AbstractMojo {

    /**
     * @parameter expression="${bitbucket.accountName}"
     */
    private String accountName;

    /**
     * @parameter expression="${bitbucket.repositorySlug}"
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

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {

        if (this.accountName == null) {
            throw new MojoExecutionException("maven account name is not set. Use -Dbitbucket.accountName=... or set property in pom.xml");
        }

        if (this.repositorySlug == null) {
            throw new MojoExecutionException("maven repository slug is not set. Use -Dbitbucket.repositorySlug=... or set property in pom.xml");
        }

        this.executeMojo();
    }

    protected abstract void executeMojo() throws MojoExecutionException;

    protected BitbucketApiClient createBitbucketApiClient() {
        if (this.userName == null && !"".equals(this.userName)) {
            return new BitbucketApiClient(this.accountName, this.repositorySlug);
        }

        return new BitbucketApiClient(this.accountName, this.repositorySlug, this.userName, this.password);
    }
}
