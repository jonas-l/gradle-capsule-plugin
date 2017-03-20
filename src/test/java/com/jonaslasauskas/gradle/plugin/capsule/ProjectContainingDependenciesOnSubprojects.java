package com.jonaslasauskas.gradle.plugin.capsule;

import static com.jonaslasauskas.gradle.plugin.ExecutionSubject.assertThat;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

import com.jonaslasauskas.gradle.plugin.CapsuleJar;
import com.jonaslasauskas.gradle.plugin.ExecutableJar.Execution;
import com.jonaslasauskas.gradle.plugin.GradleProject;
import com.jonaslasauskas.gradle.plugin.GradleVersion;



@RunWith(Theories.class) public class ProjectContainingDependenciesOnSubprojects {
  
  @DataPoint public static final String gradleVersion = GradleVersion.underTest();
  
  @Rule public final GradleProject project = GradleProject
      .forTestingPluginAt(new File("build/libs/gradle-capsule-plugin.jar"))
      .withBuildScript(
          "plugins { id 'com.jonaslasauskas.capsule' }",
          "repositories { jcenter() }",
          "dependencies {",
          "  compile project(':subproject')",
          "}",
          "capsule { ",
          "  capsuleManifest {",
          "    applicationId = 'test'",
          "    applicationClass = 'test.Main'",
          "  }",
          "}")
      .named("test")
      .withSubproject("subproject")
      .withFile("subproject/build.gradle", "apply plugin: 'java'")
      .withFile("subproject/src/main/java/subproject/Message.java",
          "package subproject;",
          "public class Message {",
          "  public String toString() { return \"Hello from Subproject\"; }",
          "}");
  
  
  public ProjectContainingDependenciesOnSubprojects(String version) {
    project.usingGradleVersion(version);
  }
  
  @Test public void bundle_subproject_jars() throws Exception {
    project
        .withFile("src/main/java/test/Main.java",
            "package test;",
            "import subproject.Message;", // from subproject dependency
            "class Main {",
            "  public static void main(String[] args) {",
            "    System.out.println(new Message());",
            "  }",
            "}")
        .buildWithArguments("assemble");
    
    Execution execution = CapsuleJar.at(project.file("build/libs/test-capsule.jar")).run();
    
    assertThat(execution).succeededAnd().standardOutput().contains("Hello from Subproject");
  }
  
}
