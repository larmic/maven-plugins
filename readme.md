# larmic-maven-plugins

Containing maven plugins:

* **bitbucket:checkTodo** Logs info messages to find all code Todos in source and test source directory. Checks bitbucket ticket status and logs error
message if ticket is already resolved.

## Install

Actually no maven repository is used so you have to install maven plugin by yourself.

* Checkout the source: `git@bitbucket.org:larmicBB/larmic-maven-plugins.git` and install it yourself.
* Use maven to install it `maven install`

## Getting started

There are multiple ways you can use larmic-maven-plugins.

### Using console

Call

    $ de.larmic.maven.plugins:bitbucket:$pluginVersion:$pluginGoal -DaccountName=$repositoryBitbucketAccount -DrepositorySlug=$bitbucketRepositorySlug

in your maven project root directory you want to execute goal in.

### Using pom.xml settings

Add

```xml
<pluginGroups>
    <pluginGroup>de.larmic.maven.plugins</pluginGroup>
</pluginGroups>
```

to your maven settings.xml and call

    $ bitbucket:$pluginGoal -DaccountName=$repositoryBitbucketAccount -DrepositorySlug=$bitbucketRepositorySlug

in your maven project root directory you want to execute goal in.

### Configuring account name and Repository slug in pom.xml

You can set bitbucket repository account name and slug in your maven pom.xml

```xml
<project>
    ...
    <properties>
        <bitbucket:accountName>$repositoryBitbucketAccount</bitbucket:accountName>
        <bitbucket:repositorySlug>$bitbucketRepositorySlug</bitbucket:repositorySlug>
    </properties>
</project>
```

