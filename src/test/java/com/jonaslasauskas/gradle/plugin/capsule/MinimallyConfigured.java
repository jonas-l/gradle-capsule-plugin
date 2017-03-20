package com.jonaslasauskas.gradle.plugin.capsule;

import static com.google.common.truth.Truth.assert_;
import static com.jonaslasauskas.gradle.plugin.BuildResultSubject.assertThat;
import static com.jonaslasauskas.gradle.plugin.ExecutionSubject.execution;

import java.io.File;

import org.gradle.testkit.runner.BuildResult;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

import com.jonaslasauskas.gradle.plugin.CapsuleJar;
import com.jonaslasauskas.gradle.plugin.ExecutableJar;
import com.jonaslasauskas.gradle.plugin.ExecutableJar.Execution;
import com.jonaslasauskas.gradle.plugin.GradleProject;
import com.jonaslasauskas.gradle.plugin.GradleVersion;



@RunWith(Theories.class) public class MinimallyConfigured {
  
  @DataPoint public static final String gradleVersion = GradleVersion.underTest();
  
  @Rule public final GradleProject project = GradleProject
      .forTestingPluginAt(new File("build/libs/gradle-capsule-plugin.jar"))
      .withBuildScript(
          "plugins { id 'com.jonaslasauskas.capsule' }",
          "repositories { jcenter() }",
          "capsule { ",
          "  capsuleManifest {",
          "    applicationId = 'test'",
          "    applicationClass = 'test.Main'",
          "  }",
          "}")
      .withFile("src/main/java/test/Main.java",
          "package test;",
          "class Main {",
          "  public static void main(String[] args) { System.out.println(\"Hello Gradle, Capsule\"); }",
          "}");
  
  
  public MinimallyConfigured(String version) {
    project.usingGradleVersion(version);
  }
  
  @Test public void is_listed_among_gradle_tasks() throws Exception {
    BuildResult build = project.buildWithArguments("tasks");
    
    assert_().that(build.getOutput()).contains("capsule - ");
  }
  
  @Test public void executes_capsule_task_on_assemble() throws Exception {
    BuildResult build = project.buildWithArguments("assemble");
    
    assertThat(build).task(":capsule").succeeded();
  }
  
  @Test public void produces_jar_with_capsule_classifier() throws Exception {
    project.named("test").buildWithArguments("assemble");
    
    File libs = project.file("build/libs");
    assert_().that(libs.list()).asList().contains("test-capsule.jar");
  }
  
  @Test public void capsule_jar_reports_default_capsule_version() throws Exception {
    project.named("test").buildWithArguments("assemble");
    
    ExecutableJar capsuleJar = CapsuleJar.at(project.file("build/libs/test-capsule.jar"));
    Execution execution = capsuleJar.withSystemProperty("capsule.version").run();
    
    assert_().about(execution()).that(execution).succeededAnd().standardOutput().contains("Version 1.0");
  }
  
  @Test public void capsule_jar_executes_application_class() throws Exception {
    project.named("test").buildWithArguments("assemble");
    
    ExecutableJar capsuleJar = CapsuleJar.at(project.file("build/libs/test-capsule.jar"));
    Execution execution = capsuleJar.run();
    
    assert_().about(execution()).that(execution).succeededAnd().standardOutput().startsWith("Hello Gradle, Capsule");
  }
  
}
