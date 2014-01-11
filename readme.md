larmic-maven-plugins - A collection of maven plugins
=============



## Install

Actually no maven repository is used so you have to install maven plugin by yourself.

* Checkout the source: `git@bitbucket.org:larmicBB/larmic-maven-plugins.git` and install it yourself.
* Use maven to install it `maven install`

## Getting started

There are multiple ways you can use larmic-maven-plugins.

Using console
-------------

Call

    $ de.larmic.maven.plugins:bitbucket:$pluginVersion:$pluginGoal -DaccountName=$myBtbucketAccount -DrepositorySlug=$myBitbucketRepository

in your maven project root directory you want to execute goal in.

Using pom.xml settings
----------------------

Add

```xml
<pluginGroups>
    <pluginGroup>de.larmic.maven.plugins</pluginGroup>
</pluginGroups>
```

to your maven settings.xml and call

    $ bitbucket:$pluginGoal -DaccountName=$myBtbucketAccount -DrepositorySlug=$myBitbucketRepository

in your maven project root directory you want to execute goal in.


