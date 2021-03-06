package com.jonaslasauskas.gradle.plugin.specs.capsule;

import static com.jonaslasauskas.gradle.plugin.specs.ExecutionSubject.assertThat;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

import com.google.common.truth.StringSubject;
import com.jonaslasauskas.gradle.plugin.specs.CapsuleJar;
import com.jonaslasauskas.gradle.plugin.specs.ExecutableJar;
import com.jonaslasauskas.gradle.plugin.specs.GradleProject;
import com.jonaslasauskas.gradle.plugin.specs.GradleVersion;
import com.jonaslasauskas.gradle.plugin.specs.ExecutableJar.Execution;



@RunWith(Theories.class) public class DeclaredJarMainClassManifestEntry {
  
  @DataPoint public static final String gradleVersion = GradleVersion.underTest();
  
  @Rule public final GradleProject project = GradleProject
      .forTestingPluginAt(new File("build/libs/gradle-capsule-plugin.jar"))
      .withBuildScript(
          "plugins { id 'com.jonaslasauskas.capsule' }",
          "repositories { jcenter() }",
          "jar.manifest.attributes 'Main-Class': 'test.JarMain'",
          "capsule.capsuleManifest.applicationId = 'test'")
      .withEntryPointClassAt("test", "JarMain", "Hello from Jar!");
  
  
  public DeclaredJarMainClassManifestEntry(String version) {
    project.usingGradleVersion(version);
  }
  
  @Test public void is_used_as_default_applicationClass() throws Exception {
    project.named("test").buildWithArguments("assemble");
    
    Execution original = ExecutableJar.at(project.file("build/libs/test.jar")).run();
    Execution capsule = CapsuleJar.at(project.file("build/libs/test-capsule.jar")).run();
    
    assertThat(original).succeeded();
    StringSubject capsuleOutput = assertThat(capsule).succeededAnd().standardOutput();
    capsuleOutput.isNotEmpty();
    capsuleOutput.isEqualTo(original.output);
  }
  
  @Test public void has_no_effect_when_applicationClass_entry_in_capsuleManifest_exists() throws Exception {
    project
        .withAdditionalBuildScript("capsule.capsuleManifest.applicationClass = 'test.CapsuleMain'")
        .withEntryPointClassAt("test", "CapsuleMain", "Hello from Capsule!")
        .named("test").buildWithArguments("assemble");
    
    Execution capsule = CapsuleJar.at(project.file("build/libs/test-capsule.jar")).run();
    
    assertThat(capsule).succeededAnd().standardOutput().startsWith("Hello from Capsule!");
  }
  
}
