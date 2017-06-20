package com.jonaslasauskas.gradle.plugin.specs.capsule;

import static com.jonaslasauskas.gradle.plugin.specs.ExecutionSubject.assertThat;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

import com.jonaslasauskas.gradle.plugin.specs.CapsuleJar;
import com.jonaslasauskas.gradle.plugin.specs.GradleProject;
import com.jonaslasauskas.gradle.plugin.specs.GradleVersion;
import com.jonaslasauskas.gradle.plugin.specs.ExecutableJar.Execution;



@RunWith(Theories.class) public class ProjectContainingExternalDependencies {
  
  @DataPoint public static final String gradleVersion = GradleVersion.underTest();
  
  @Rule public final GradleProject project = GradleProject
      .forTestingPluginAt(new File("build/libs/gradle-capsule-plugin.jar"))
      .withBuildScript(
          "plugins { id 'com.jonaslasauskas.capsule' }",
          "repositories { mavenCentral() }",
          "dependencies {",
          "  compile 'ch.qos.logback:logback-classic:1.2.1'",
          "}",
          "capsule { ",
          "  capsuleManifest {",
          "    applicationId = 'test'",
          "    applicationClass = 'test.Main'",
          "  }",
          "}")
      .named("test");
  
  
  public ProjectContainingExternalDependencies(String version) {
    project.usingGradleVersion(version);
  }
  
  @Test public void bundle_transient_dependencies_as_well() throws Exception {
    project
        .withFile("src/main/java/test/Main.java",
            "package test;",
            "import org.slf4j.LoggerFactory;", // from transient dependency
            "class Main {",
            "  public static void main(String[] args) {",
            "    LoggerFactory.getLogger(Main.class).info(\"Hello world\");",
            "  }",
            "}")
        .buildWithArguments("assemble");
    
    Execution execution = CapsuleJar.at(project.file("build/libs/test-capsule.jar")).run();
    
    assertThat(execution).succeededAnd().standardOutput().contains("Hello world");
  }
  
}
