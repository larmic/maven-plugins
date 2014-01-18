# larmic-maven-plugins

Containing maven plugins:

* **bitbucket:checkTodo** Logs info messages to find all code Todos in source and test source directory. Checks bitbucket ticket status and logs error
message if ticket is already resolved.

* **bitbucket:createReleaseNotes** Creates a release notes file (actual xml) to the specified directory. Checks resolved bitbucket tickets and sorts it by version.

## Install

larmic-maven-plugins is accessible by maven central repository. Add following dependency to use it

```xml
<dependency>
    <groupId>de.larmic</groupId>
    <artifactId>larmic-maven-bitbucket-plugin</artifactId>
    <version>1.1</version>
</dependency>
```

Or add the following to your pom.xml

```xml
<build>
    <plugins>
        <plugin>
            <groupId>de.larmic</groupId>
            <artifactId>larmic-maven-bitbucket-plugin</artifactId>
            <version>1.1</version>
            <configuration>
                <accountName>$repositoryBitbucketAccount</accountName>
                <repositorySlug>$bitbucketRepositorySlug</repositorySlug>
                <userName>$bitbucketAccount</userName>
            </configuration>
        </plugin>
	</plugins>
</build>
```

Or you can clone git repository and install maven plugin by yourself.

* Checkout the source: `git clone git@bitbucket.org:larmicBB/larmic-maven-plugins.git`.
* Use maven to install it `maven install`

## Getting started

There are multiple ways you can use larmic-maven-plugins.

### Using console

Call

    $ mvn de.larmic:larmic-maven-bitbucket-plugin:$pluginVersion:$pluginGoal -Dbitbucket.accountName=$repositoryBitbucketAccount -Dbitbucket
    .repositorySlug=$bitbucketRepositorySlug

in your maven project root directory you want to execute goal in.

### Using settings.xml settings

Add

```xml
<pluginGroups>
    <pluginGroup>de.larmic</pluginGroup>
</pluginGroups>
```

to your maven settings.xml and call

    $ mvn bitbucket:$pluginGoal -Dbitbucket.userName=$repositoryBitbucketAccount -Dbitbucket.password=$bitbucketRepositorySlug

in your maven project root directory you want to execute goal in.

### Using pom.xml settings

Add

```xml
<build>
    <plugins>
        <plugin>
            <groupId>de.larmic</groupId>
            <artifactId>larmic-maven-bitbucket-plugin</artifactId>
            <version>1.1</version>
            <configuration>
                <accountName>$repositoryBitbucketAccount</accountName>
                <repositorySlug>$bitbucketRepositorySlug</repositorySlug>
                <userName>$bitbucketAccount</userName>
            </configuration>
        </plugin>
	</plugins>
</build>
```

to your projects pom.xml and call

    $ mvn bitbucket:$pluginGoal -Dbitbucket.password=$bitbucketPassword

### Configuring account name and Repository slug in pom.xml

You can set bitbucket repository account name and slug in your maven pom.xml

```xml
<project>
    ...
    <properties>
        <bitbucket.accountName>$repositoryBitbucketAccount</bitbucket.accountName>
        <bitbucket.repositorySlug>$bitbucketRepositorySlug</bitbucket.repositorySlug>
        <!-- user name and password is needed if repository needs an authentication -->
        <bitbucket.userName>$userName</bitbucket.userName>
        <bitbucket.password>$password</bitbucket.password>
    </properties>
</project>
```

Now you can call maven plugin without explicit indicating property

    $ mvn bitbucket:$pluginGoal
    
## Parameters

### Plugin checkTodo

* **sourceDirectory** All files in source directory will be scanned. Default is `${project.build.sourceDirectory}`. Could be empty.
* **testSourceDirectory** All files in test source directory will be scanned. Default is `${project.build.testSourceDirectory}`. Could be empty.

### Plugin createReleaseNotes

* **title** Title is part of the content of the created file. Default is `Release notes ${project.name}`.
* **ignoreTicketWithNoVersion** Ignores bitbucket tickets with no version is set when value is `true`. Default is `false`.
* **relativePath** Project relative path the files are created in. Default is `/src/main/webapp/`.

## Examples

Update settings.xml

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
    ...
    <pluginGroups>
        <pluginGroup>de.larmic</pluginGroup>
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

### Call bitbucket checkTodo plugin

    $ mvn bitbucket:checkTodo
    [INFO] Scanning for projects...
    [INFO]
    [INFO] ------------------------------------------------------------------------
    [INFO] Building Bitbucket maven plugins 1.0-SNAPSHOT
    [INFO] ------------------------------------------------------------------------
    [INFO]
    [INFO] --- bitbucket:1.0-SNAPSHOT:checkTodo (default-cli) @ bitbucket ---
    [INFO]
    [INFO] Scanning in directory /Users/larmic/Work/Private/workspace/larmic-maven-plugins/bitbucket/src/main/java
    [INFO]
    [INFO] Found 0 in directory /Users/larmic/Work/Private/workspace/larmic-maven-plugins/bitbucket/src/main/java
    [INFO]
    [INFO] Scanning in directory /Users/larmic/Work/Private/workspace/larmic-maven-plugins/bitbucket/src/test/java
    [INFO]
    [INFO] TodoCheckMojoTest.java Line: 3, Text: no bitbucket ticket
    [ERROR] [COULD NOT FIND TICKET] TodoCheckMojoTest.java Line: 4, Text: not existing bitbucket ticket
    [INFO] TodoCheckMojoTest.java Line: 5, Text: open bitbucket ticket
    [ERROR] [TICKET IS RESOLVED] TodoCheckMojoTest.java Line: 6, Text: closed bitbucket ticket
    [INFO] Found 0 in directory /Users/larmic/Work/Private/workspace/larmic-maven-plugins/bitbucket/src/test/java
    [INFO]
    [INFO] Scan completed. Found 0 TODOs.
    [INFO]
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------
    [INFO] Total time: 3.005s
    [INFO] Finished at: Sat Jan 11 12:31:11 CET 2014
    [INFO] Final Memory: 12M/245M
    [INFO] ------------------------------------------------------------------------
    $
    
### Call bitbucket createReleaseNotes plugin

    $ mvn bitbucket:createReleaseNotes
    
will create 

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<releasenotes>
  <title>testtitle</title>
  <release version="Release 1">
    <ticket id="7">
      <title>Test release note ticket 1 (release 1)</title>
      <priority>major</priority>
      <kind>proposal</kind>
      <milestone/>
      <component/>
    </ticket>
    <ticket id="8">
      <title>Test release note ticket 2 (release 1)</title>
      <priority>major</priority>
      <kind>bug</kind>
      <milestone/>
      <component/>
    </ticket>
    <ticket id="10">
      <title>Test release note ticket 3 (release 1)</title>
      <priority>major</priority>
      <kind>bug</kind>
      <milestone/>
      <component/>
    </ticket>
  </release>
  <release version="Release 2">
    <ticket id="9">
      <title>Test release note ticket 1 (release 2)</title>
      <priority>blocker</priority>
      <kind>task</kind>
      <milestone/>
      <component/>
    </ticket>
    <ticket id="11">
      <title>Test release note ticket 2 (release 2)</title>
      <priority>trivial</priority>
      <kind>enhancement</kind>
      <milestone/>
      <component/>
    </ticket>
  </release>
</releasenotes>
```