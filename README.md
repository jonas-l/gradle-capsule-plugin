Capsule plugin for Gradle
=========================

Produces a [Capsule][1] jar as configured in Gradle build script.

> A capsule is a single executable JAR that contains everything your application needs to run either in the form of embedded files or as declarative metadata.

This plugin implicitly applies [Java plugin][2] and hooks into the workflow before `assemble` task:

```bob

  .-------------.
  | compileJava |
  '-------------'
         ^
         |
  .-------------.
  |   classes   |
  '-------------'
         ^
         |
  .-------------.
  |     jar     |
  '-------------'
         ^
         |        .---------.
         |        | capsule |
         |        '---------'
         |        ^
  .-------------./
  |  assemble   |
  '-------------'
         ^
         |
  .-------------.
  |    build    |
  '-------------'

```

[1]: http://www.capsule.io/
[2]: https://docs.gradle.org/current/userguide/java_plugin.html
