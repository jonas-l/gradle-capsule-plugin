package com.jonaslasauskas.gradle.plugin;

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
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;



public final class GradleProject implements TestRule {
  
  public static ForPluginTesting forTestingPluginAt(File classpath) {
    return new ForPluginTesting(classpath);
  }
  
  
  private final TemporaryFolder temporaryFolder;
  
  private final GradleRunner runner;
  
  private final List<String> buildScript;
  
  private Optional<String> rootProjectName = empty();
  
  private final List<String> subprojectNames;
  
  private final Map<String, String[]> files;
  
  
  GradleProject(TemporaryFolder temporaryFolder, GradleRunner runner, String... buildScriptLines) {
    this.temporaryFolder = temporaryFolder;
    this.runner = runner;
    this.buildScript = new ArrayList<>();
    this.subprojectNames = new ArrayList<>();
    this.files = new HashMap<>();
    
    this.buildScript.addAll(asList(buildScriptLines));
  }
  
  public GradleProject usingGradleVersion(String gradleVersion) {
    runner.withGradleVersion(gradleVersion);
    
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
        .withProjectDir(temporaryFolder.getRoot())
        .withTestKitDir(new File(temporaryFolder.getRoot(), ".gradle_home"))
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
      Path filePath = temporaryFolder.getRoot().toPath().resolve(fileName);
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
    return new File(temporaryFolder.getRoot(), path);
  }
  
  @Override public Statement apply(Statement base, Description description) {
    return temporaryFolder.apply(base, description);
  }
  
  
  public static final class ForPluginTesting extends TemporaryFolder {
    
    private final GradleRunner runner;
    
    private final boolean persistent;
    
    
    private ForPluginTesting(File pluginPath) {
      super(Optional.ofNullable(System.getenv("PERSISTENT_TEST_GRADLE_PROJECT_DIR")).map(File::new).orElse(null));
      this.persistent = System.getenv("PERSISTENT_TEST_GRADLE_PROJECT_DIR") != null;
      runner = GradleRunner.create().withPluginClasspath(singletonList(pluginPath));
    }
    
    public GradleProject withBuildScript(String... contentLines) {
      return new GradleProject(this, runner, contentLines);
    }
    
    @Override protected void after() {
      if (!persistent) {
        super.after();
      }
    }
    
  }
  
  
  private static Charset utf8 = Charset.forName("utf-8");
  
}
