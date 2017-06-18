Capsule plugin for Gradle
=========================

Produces a [Capsule][capsule] jar matching java project's configuration.

> A capsule is a single executable JAR that contains everything your application needs to run either in the form of embedded files or as declarative metadata.


Getting started
---------------

First, you have to declare the plugin at the top of your build script:

```groovy
plugins {
  id 'com.jonaslasauskas.capsule'
}
```

It requires a repository to download Capsule base artifact. Declare `mavenCentral()`, `jcenter()`, or any other repository which contains `co.paralleluniverse:capsule:*`. For now let's use `jcenter()`:

```groovy
repositories {
  jcenter()
}
```

This plugin implicitly applies [Java plugin][gradle-java-plugin] and introduces a `capsule` task which will be executed on `assemble`.

`capsule` task will try to resolve any necessary properties, but in case they are not available you should declare them manually:

```groovy
capsule {
  capsuleManifest {
    applicationId = 'your.application.id'
    applicationClass = 'your.package.MainApplicationClass'
  }
}
```

Here

`applicationId`: a unique name which usually follows Java's package naming convention (for more information see [The Capsule ID][capsule-id] section).

`applicationClass`: fully qualified name of the main class.

Furthermore, `capsule` extends from `jar` task which means it supports all [`jar` properties and methods][gradle-jar-task].

You are now ready to put your project into capsule:

```shell
$ ./gradlew assemble
```

If everything went well you will find a Capsule jar located in `build/libs/`. The new jar has `capsule` classifier. So if your project is named `test`, you will find `build/libs/test-capsule.jar` file which is an executable Capsule:

```shell
$ java -jar build/libs/test-capsule.jar
```


Contributing
------------

[Gradle Capsule Plugin is hosted on GitLab][main-repo] but there's a [mirrored project repository on GitHub][mirrored-repo] where contributions can be submitted as well.

Furthermore, there are a few guidelines which can be found in [contributing guide](CONTRIBUTING.md).


[capsule]: http://www.capsule.io/
[gradle-java-plugin]: https://docs.gradle.org/current/userguide/java_plugin.html
[capsule-id]: http://www.capsule.io/user-guide/#the-capsule-id
[gradle-jar-task]: https://docs.gradle.org/current/dsl/org.gradle.api.tasks.bundling.Jar.html
[main-repo]: https://gitlab.com/jonas-l/gradle-capsule-plugin
[mirrored-repo]: https://github.com/jonas-l/gradle-capsule-plugin
