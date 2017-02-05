package com.jonaslasauskas.gradle.plugin.capsule;

import static com.jonaslasauskas.gradle.plugin.ExecutionSubject.assertThat;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

import com.google.common.truth.StringSubject;
import com.jonaslasauskas.gradle.plugin.ExecutableJar;
import com.jonaslasauskas.gradle.plugin.ExecutableJar.Execution;
import com.jonaslasauskas.gradle.plugin.GradleProject;
import com.jonaslasauskas.gradle.plugin.GradleVersion;



@RunWith(Theories.class) public class DeclaredJarMainClassManifestEntry {
  
  @DataPoint public static final String gradleVersion = GradleVersion.underTest();
  
  @Rule public final GradleProject project = GradleProject
      .forTestingPluginAt(new File("build/libs/gradle-capsule-plugin.jar"))
      .withBuildScript(
          "plugins { id 'com.jonaslasauskas.capsule' }",
          "repositories { jcenter() }",
          "jar.manifest.attributes 'Main-Class': 'test.JarMain'",
          "capsule.capsuleManifest.applicationId = 'test'")
      .withFile("src/main/java/test/JarMain.java",
          "package test;",
          "class JarMain {",
          "  public static void main(String[] args) { System.out.println(\"Hello from jar!\"); }",
          "}");
  
  
  public DeclaredJarMainClassManifestEntry(String version) {
    project.usingGradleVersion(version);
  }
  
  @Test public void is_used_as_default_applicationClass() throws Exception {
    project.named("test").buildWithArguments("assemble");
    
    Execution original = ExecutableJar.at(project.file("build/libs/test.jar")).run();
    Execution capsule = ExecutableJar.at(project.file("build/libs/test-capsule.jar")).run();
    
    assertThat(original).succeeded();
    assertThat(capsule).succeeded();
    StringSubject capsuleOutput = assertThat(capsule).standardOutput();
    capsuleOutput.isNotEmpty();
    capsuleOutput.isEqualTo(original.output);
  }
  
  @Test public void has_no_effect_when_applicationClass_entry_in_capsuleManifest_exists() throws Exception {
    project
        .withAdditionalBuildScript(
            "capsule.capsuleManifest.applicationClass = 'test.CapsuleMain'")
        .withFile("src/main/java/test/CapsuleMain.java",
            "package test;",
            "class CapsuleMain {",
            "  public static void main(String[] args) { System.out.println(\"Hello from Capsule!\"); }",
            "}")
        .named("test").buildWithArguments("assemble");
    
    Execution capsule = ExecutableJar.at(project.file("build/libs/test-capsule.jar")).run();
    assertThat(capsule).succeeded();
    assertThat(capsule).standardOutput().startsWith("Hello from Capsule!");
  }
  
}
