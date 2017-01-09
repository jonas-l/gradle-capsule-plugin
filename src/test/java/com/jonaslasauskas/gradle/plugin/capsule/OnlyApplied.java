package com.jonaslasauskas.gradle.plugin.capsule;

import static com.google.common.truth.Truth.assert_;
import static com.jonaslasauskas.gradle.plugin.BuildResultSubject.assertThat;

import java.io.File;

import org.gradle.testkit.runner.BuildResult;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

import com.jonaslasauskas.gradle.plugin.GradleProject;
import com.jonaslasauskas.gradle.plugin.GradleVersion;



@RunWith(Theories.class) public class OnlyApplied {
  
  @DataPoint public static final String gradleVersion = GradleVersion.underTest();
  
  @Rule public final GradleProject project = GradleProject
      .forTestingPluginAt(new File("build/libs/gradle-capsule-plugin.jar"))
      .withBuildScript("plugins { id 'com.jonaslasauskas.capsule' }");
  
  
  public OnlyApplied(String version) {
    project.usingGradleVersion(version);
  }
  
  @Test public void executes_capsule_task_on_assemble() throws Exception {
    BuildResult build = project.buildWithArguments("assemble");
    
    assertThat(build).task(":capsule").succeeded();
  }
  
  @Test public void produces_jar_with_capsule_classifier() throws Exception {
    project.named("test").buildWithArguments("assemble");
    
    File libs = new File(project.buildDirectory(), "libs");
    assert_().that(libs.list()).asList().contains("test-capsule.jar");
  }
  
}
