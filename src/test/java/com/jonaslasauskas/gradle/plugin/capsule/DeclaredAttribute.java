package com.jonaslasauskas.gradle.plugin.capsule;

import static com.jonaslasauskas.gradle.plugin.ExecutionSubject.assertThat;

import java.io.File;

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



@RunWith(Theories.class) public class DeclaredAttribute {
  
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
          "    minJavaVersion = '1.9'",
          "  }",
          "}")
      .withEntryPointClassAt("test", "Main");
  
  
  public DeclaredAttribute(String version) {
    project.usingGradleVersion(version);
  }
  
  @Test public void min_java_version_fails_when_available_java_version_too_low() throws Exception {
    project.named("test").buildWithArguments("assemble");
    
    ExecutableJar capsuleJar = CapsuleJar.at(project.file("build/libs/test-capsule.jar"));
    Execution execution = capsuleJar.run();
    
    assertThat(execution).failedAnd().standardError().contains("Min. Java version: 1.9");
  }
  
  @Test public void min_java_version_succeeds_when_later_java_version_is_available() throws Exception {
    project
        .withAdditionalBuildScript("capsule.capsuleManifest.minJavaVersion = '1.7'")
        .named("test")
        .buildWithArguments("assemble");
    
    ExecutableJar capsuleJar = CapsuleJar.at(project.file("build/libs/test-capsule.jar"));
    Execution execution = capsuleJar.run();
    
    assertThat(execution).succeeded();
  }
  
}
