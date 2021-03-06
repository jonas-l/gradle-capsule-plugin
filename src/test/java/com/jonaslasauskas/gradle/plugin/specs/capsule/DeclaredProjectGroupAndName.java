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



@RunWith(Theories.class) public class DeclaredProjectGroupAndName {
  
  @DataPoint public static final String gradleVersion = GradleVersion.underTest();
  
  @Rule public final GradleProject project = GradleProject
      .forTestingPluginAt(new File("build/libs/gradle-capsule-plugin.jar"))
      .withBuildScript(
          "plugins { id 'com.jonaslasauskas.capsule' }",
          "repositories { jcenter() }",
          "group = 'com.example'",
          "capsule.capsuleManifest.applicationClass = 'test.Main'")
      .withEntryPointClassAt("test", "Main")
      .named("test");
  
  
  public DeclaredProjectGroupAndName(String version) {
    project.usingGradleVersion(version);
  }
  
  @Test public void is_used_as_default_applicationId() throws Exception {
    project.buildWithArguments("assemble");
    
    Execution capsuleVersion = CapsuleJar.at(project.file("build/libs/test-capsule.jar"))
        .withSystemProperty("capsule.version")
        .run();
    
    assertThat(capsuleVersion).succeededAnd().standardOutput().contains("com.example.test");
  }
  
  @Test public void has_no_effect_when_applicationId_entry_in_capsuleManifest_exists() throws Exception {
    project
        .withAdditionalBuildScript("capsule.capsuleManifest.applicationId = 'capsuleManifestId'")
        .buildWithArguments("assemble");
    
    Execution capsuleVersion = CapsuleJar.at(project.file("build/libs/test-capsule.jar"))
        .withSystemProperty("capsule.version")
        .run();
    
    assertThat(capsuleVersion).succeededAnd().standardOutput().contains("capsuleManifestId");
  }
  
}
