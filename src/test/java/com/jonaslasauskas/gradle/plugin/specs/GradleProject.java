package com.jonaslasauskas.gradle.plugin.specs;

import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.write;
import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.joining;
import static org.gradle.api.Project.DEFAULT_BUILD_FILE;
import static org.gradle.api.initialization.Settings.DEFAULT_SETTINGS_FILE;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.rules.TemporaryFolder;



/**
 * Gives a convenient tools for building a Gradle project.
 * 
 * Main purpose of this class is to ease basic (e.g. creating a file) and
 * frequently used (e.g. java main class with trivial implementation) actions.
 */
public final class GradleProject extends TemporaryFolder {
  
  public static GradleProject forTestingPluginAt(File classpath) {
    return new GradleProject(classpath);
  }
  
  
  private final boolean persistent;
  
  private final GradleRunner runner;
  
  private final List<String> buildScript;
  
  private Optional<String> rootProjectName = empty();
  
  private final List<String> subprojectNames;
  
  private final Map<String, String[]> files;
  
  
  GradleProject(File pluginPath) {
    super(Optional.ofNullable(System.getenv("PERSISTENT_TEST_GRADLE_PROJECT_DIR")).map(File::new).orElse(null));
    this.persistent = System.getenv("PERSISTENT_TEST_GRADLE_PROJECT_DIR") != null;
    this.runner = GradleRunner.create().withPluginClasspath(singletonList(pluginPath));
    
    this.buildScript = new ArrayList<>();
    this.subprojectNames = new ArrayList<>();
    this.files = new HashMap<>();
  }
  
  public GradleProject usingGradleVersion(String gradleVersion) {
    runner.withGradleVersion(gradleVersion);
    
    return this;
  }
  
  public GradleProject withBuildScript(String... contentLines) {
    this.buildScript.clear();
    this.buildScript.addAll(asList(contentLines));
    
    return this;
  }
  
  public GradleProject named(String name) {
    this.rootProjectName = Optional.of(format("rootProject.name = ''{0}''", name));
    
    return this;
  }
  
  public GradleProject withSubproject(String name) {
    this.subprojectNames.add(name);
    
    return this;
  }
  
  public GradleProject withEntryPointClassAt(String packageName, String className) {
    return withEntryPointClassAt(packageName, className, packageName + "." + className);
  }
  
  public GradleProject withEntryPointClassAt(String packageName, String className, String contentPrint) {
    return withFile(
        "src/main/java/" + packageName.replace('.', '/') + "/" + className + ".java",
        "package " + packageName + ";",
        "class " + className + " {",
        "  public static void main(String[] args) { System.out.println(\"" + contentPrint + "\"); }",
        "}");
  }
  
  public GradleProject withFile(String fileName, String... content) {
    this.files.put(fileName, content);
    
    return this;
  }
  
  public GradleProject withAdditionalBuildScript(String... lines) {
    this.buildScript.addAll(asList(lines));
    
    return this;
  }
  
  public BuildResult buildWithArguments(String... arguments) {
    writeFiles();
    
    return runner
        .withProjectDir(getRoot())
        .withTestKitDir(new File(getRoot(), ".gradle_home"))
        .withArguments(arguments)
        .withDebug(true)
        .build();
  }
  
  private void writeFiles() {
    files.forEach(this::writeFileContent);
    
    writeFileContent(DEFAULT_BUILD_FILE, buildScript);
    buildSettings().ifPresent(content -> writeFileContent(DEFAULT_SETTINGS_FILE, content));
  }
  
  private void writeFileContent(String fileName, String... content) {
    writeFileContent(fileName, asList(content));
  }
  
  private void writeFileContent(String fileName, Iterable<? extends CharSequence> content) {
    try {
      Path filePath = getRoot().toPath().resolve(fileName);
      createDirectories(filePath.getParent());
      write(filePath, content, utf8);
    } catch (IOException e) {
      throw new RuntimeException("Could not write content to '" + fileName + "'", e);
    }
  }
  
  private Optional<Iterable<String>> buildSettings() {
    ArrayList<String> lines = new ArrayList<>();
    
    rootProjectName.ifPresent(lines::add);
    
    if (!subprojectNames.isEmpty()) {
      lines.add(subprojectNames.stream().collect(joining("', '", "include '", "'")));
    }
    
    return lines.isEmpty() ? empty() : Optional.of(lines);
  }
  
  public File file(String path) {
    return new File(getRoot(), path);
  }
  
  @Override protected void after() {
    if (!persistent) {
      super.after();
    }
  }
  
  
  private static Charset utf8 = Charset.forName("utf-8");
  
}
