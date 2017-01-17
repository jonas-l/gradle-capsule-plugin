package com.jonaslasauskas.gradle.plugin;

import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static org.gradle.api.Project.DEFAULT_BUILD_FILE;
import static org.gradle.api.initialization.Settings.DEFAULT_SETTINGS_FILE;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Optional;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.rules.TemporaryFolder;



public final class GradleProject extends TemporaryFolder {
  
  public static ForPluginTesting forTestingPluginAt(File classpath) {
    return new ForPluginTesting(classpath);
  }
  
  
  private final GradleRunner runner;
  
  private final Iterable<? extends CharSequence> buildScript;
  
  private Optional<String> buildSettings = empty();
  
  
  GradleProject(GradleRunner runner, String... buildScriptLines) {
    this.runner = runner;
    this.buildScript = asList(buildScriptLines);
  }
  
  public GradleProject usingGradleVersion(String gradleVersion) {
    runner.withGradleVersion(gradleVersion);
    
    return this;
  }
  
  public GradleProject named(String name) {
    this.buildSettings = Optional.of(format("rootProject.name = ''{0}''", name));
    
    return this;
  }
  
  public BuildResult buildWithArguments(String... arguments) {
    writeFiles();
    
    return runner
        .withProjectDir(this.getRoot())
        .withArguments(arguments)
        .build();
  }
  
  private void writeFiles() {
    writeFileContent(DEFAULT_BUILD_FILE, buildScript);
    buildSettings.ifPresent(content -> writeFileContent(DEFAULT_SETTINGS_FILE, singletonList(content)));
  }
  
  private void writeFileContent(String fileName, Iterable<? extends CharSequence> content) {
    try {
      Files.write(getRoot().toPath().resolve(fileName), content, utf8);
    } catch (IOException e) {
      throw new RuntimeException("Could not write content to '" + fileName + "'", e);
    }
  }
  
  public File absoluteFileFor(String path) {
    return new File(getRoot(), path);
  }
  
  
  public static final class ForPluginTesting {
    
    private final GradleRunner runner;
    
    
    ForPluginTesting(File pluginPath) {
      runner = GradleRunner.create()
          .withPluginClasspath(singletonList(pluginPath));
    }
    
    public GradleProject withBuildScript(String... contentLines) {
      return new GradleProject(runner, contentLines);
    }
    
  }
  
  
  private static Charset utf8 = Charset.forName("utf-8");
  
}
