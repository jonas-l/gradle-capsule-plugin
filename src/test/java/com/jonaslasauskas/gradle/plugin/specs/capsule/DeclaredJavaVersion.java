package com.jonaslasauskas.gradle.plugin.specs.capsule;

import static com.jonaslasauskas.gradle.plugin.specs.ExecutionSubject.assertThat;
import static java.util.Arrays.asList;

import java.io.File;

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



@RunWith(Theories.class) public class DeclaredJavaVersion {
  
  @DataPoint public static final String gradleVersion = GradleVersion.underTest();
  
  @Rule public final GradleProject project = GradleProject.forTestingPluginAt(new File("build/libs/gradle-capsule-plugin.jar"));
  
  
  public DeclaredJavaVersion(String version) {
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
            "    systemProperties['javaVersion'] = 'default'",
            "    java(6) {",
            "      systemProperties['javaVersion'] = '6'",
            "    }",
            "    java(7) {",
            "      systemProperties['javaVersion'] = '7'",
            "    }",
            "    java(8) {",
            "      systemProperties['javaVersion'] = '8'",
            "    }",
            "    java(9) {",
            "      systemProperties['javaVersion'] = '9'",
            "    }",
            "  }",
            "}")
        .withFile("src/main/java/test/Main.java",
            "package test;",
            "class Main {",
            "  public static void main(String[] args) {",
            "    System.out.print(System.getProperty(\"javaVersion\"));",
            "  }",
            "}")
        .named("test")
        .buildWithArguments("assemble");
    
    ExecutableJar capsuleJar = CapsuleJar.at(project.file("build/libs/test-capsule.jar"));
    Execution execution = capsuleJar.run();
    
    assertThat(execution).succeededAnd().standardOutput().isIn(asList("6", "7", "8", "9"));
  }
  
}
