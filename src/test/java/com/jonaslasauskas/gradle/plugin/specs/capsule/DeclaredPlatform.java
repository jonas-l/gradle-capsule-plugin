package com.jonaslasauskas.gradle.plugin.specs.capsule;

import static com.jonaslasauskas.gradle.plugin.specs.BuildResultSubject.assertThat;
import static com.jonaslasauskas.gradle.plugin.specs.ExecutionSubject.assertThat;
import static java.util.Arrays.asList;

import java.io.File;

import org.gradle.testkit.runner.BuildResult;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

import com.jonaslasauskas.gradle.plugin.specs.CapsuleJar;
import com.jonaslasauskas.gradle.plugin.specs.ExecutableJar;
import com.jonaslasauskas.gradle.plugin.specs.ExecutableJar.Execution;
import com.jonaslasauskas.gradle.plugin.specs.GradleProject;
import com.jonaslasauskas.gradle.plugin.specs.GradleVersion;



@RunWith(Theories.class) public class DeclaredPlatform {
  
  @DataPoint public static final String gradleVersion = GradleVersion.underTest();
  
  @Rule public final GradleProject project = GradleProject.forTestingPluginAt(new File("build/libs/gradle-capsule-plugin.jar"));
  
  
  public DeclaredPlatform(String version) {
    project.usingGradleVersion(version);
  }
  
  @Test public void overrides_main_map_value() throws Exception {
    project
        .withBuildScript(
            "plugins { id 'com.jonaslasauskas.capsule' }",
            "repositories { jcenter() }",
            "capsule { ",
            "  capsuleManifest {",
            "    applicationId = 'test'",
            "    applicationClass = 'test.Main'",
            "    systemProperties['platform'] = 'default'",
            "    platform(POSIX) {",
            "      systemProperties['platform'] = 'posix'",
            "    }",
            "    platform(WINDOWS) {",
            "      systemProperties['platform'] = 'windows'",
            "    }",
            "  }",
            "}")
        .withFile("src/main/java/test/Main.java",
            "package test;",
            "class Main {",
            "  public static void main(String[] args) {",
            "    System.out.print(System.getProperty(\"platform\"));",
            "  }",
            "}")
        .named("test")
        .buildWithArguments("assemble");
    
    ExecutableJar capsuleJar = CapsuleJar.at(project.file("build/libs/test-capsule.jar"));
    Execution execution = capsuleJar.run();
    
    assertThat(execution).succeededAnd().standardOutput().isIn(asList("posix", "windows"));
  }
  
  @Test public void change_causes_capsule_task_execution() throws Exception {
    project
        .withBuildScript(
            "plugins { id 'com.jonaslasauskas.capsule' }",
            "repositories { jcenter() }",
            "capsule { ",
            "  capsuleManifest {",
            "    applicationId = 'test'",
            "    applicationClass = 'test.Main'",
            "    systemProperties['platform'] = 'default'",
            "    platform(POSIX) {", // <-- initially POSIX
            "      systemProperties['platform'] = 'other'",
            "    }",
            "  }",
            "}")
        .withFile("src/main/java/test/Main.java",
            "package test;",
            "class Main {",
            "  public static void main(String[] args) {",
            "    System.out.print(System.getProperty(\"platform\"));",
            "  }",
            "}")
        .buildWithArguments("assemble");
    
    BuildResult result = project
        .withBuildScript(
            "plugins { id 'com.jonaslasauskas.capsule' }",
            "repositories { jcenter() }",
            "capsule { ",
            "  capsuleManifest {",
            "    applicationId = 'test'",
            "    applicationClass = 'test.Main'",
            "    systemProperties['platform'] = 'default'",
            "    platform(WINDOWS) {", // <-- WINDOWS instead of POSIX
            "      systemProperties['platform'] = 'other'",
            "    }",
            "  }",
            "}")
        .buildWithArguments("assemble");
    
    assertThat(result).task(":capsule").succeeded();
  }
  
}
