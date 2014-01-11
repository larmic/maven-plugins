# larmic-maven-plugins

Containing maven plugins:

* **bitbucket:checkTodo** Logs info messages to find all code Todos in source and test source directory. Checks bitbucket ticket status and logs error
message if ticket is already resolved.

## Install

Actually no maven repository is used so you have to install maven plugin by yourself.

* Checkout the source: `git clone git@bitbucket.org:larmicBB/larmic-maven-plugins.git` and install it yourself.
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

You can set bitbucket repository account name and slug n your maven pom.xml

```xml
<project>
    ...
    <properties>
        <bitbucket:accountName>$repositoryBitbucketAccount</bitbucket:accountName>
        <bitbucket:repositorySlug>$bitbucketRepositorySlug</bitbucket:repositorySlug>
    </properties>
</project>
```

Now you can call maven plugin without explicit indicating property

    $ bitbucket:$pluginGoal

## Examples

Update settings.xml

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
    ...
    <pluginGroups>
        <pluginGroup>de.larmic.maven.plugins</pluginGroup>
    </pluginGroups>
</settings>
```

Update pom.xml

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    ...
    <properties>
        <bitbucket.accountName>larmicBB</bitbucket.accountName>
        <bitbucket.repositorySlug>larmic-maven-plugins</bitbucket.repositorySlug>
    </properties>
</project>
```

Call bitbucket checkTodo plugin

    $ mvn bitbucket:checkTodo
    [INFO] Scanning for projects...
    [INFO]
    [INFO] ------------------------------------------------------------------------
    [INFO] Building Bitbucket maven plugins 1.0-SNAPSHOT
    [INFO] ------------------------------------------------------------------------
    [INFO]
    [INFO] --- bitbucket:1.0-SNAPSHOT:checkTodo (default-cli) @ bitbucket ---
    [INFO] Checking TODOs in directory /Users/larmic/Work/Private/workspace/larmic-maven-plugins/bitbucket/src/main/java
    [INFO] Checking TODOs in directory /Users/larmic/Work/Private/workspace/larmic-maven-plugins/bitbucket/src/test/java
    [INFO] Found 4 TODOs in file TodoCheckMojoTest.java
    [INFO] Line 3: // TODO no bitbucket ticket
    [ERROR] Line 4: Could not find ticket number #0
    [INFO] Line 5: // TODO #1 open bitbucket ticket
    [ERROR] Line 6: Ticket #2 is resolved
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------
    [INFO] Total time: 3.005s
    [INFO] Finished at: Sat Jan 11 12:31:11 CET 2014
    [INFO] Final Memory: 12M/245M
    [INFO] ------------------------------------------------------------------------
    $