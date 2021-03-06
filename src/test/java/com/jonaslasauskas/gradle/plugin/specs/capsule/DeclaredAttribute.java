package com.jonaslasauskas.gradle.plugin.specs.capsule;

import static com.jonaslasauskas.gradle.plugin.specs.ExecutionSubject.assertThat;

import java.io.File;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

import com.jonaslasauskas.gradle.plugin.specs.CapsuleJar;
import com.jonaslasauskas.gradle.plugin.specs.ExecutableJar;
import com.jonaslasauskas.gradle.plugin.specs.GradleProject;
import com.jonaslasauskas.gradle.plugin.specs.GradleVersion;
import com.jonaslasauskas.gradle.plugin.specs.ExecutableJar.Execution;



@RunWith(Theories.class) public class DeclaredAttribute {
  
  @DataPoint public static final String gradleVersion = GradleVersion.underTest();
  
  @Rule public final GradleProject project = GradleProject.forTestingPluginAt(new File("build/libs/gradle-capsule-plugin.jar"));
  
  
  public DeclaredAttribute(String version) {
    project.usingGradleVersion(version);
  }
  
  @Test public void min_java_version_fails_when_available_java_version_too_low() throws Exception {
    project
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
        .withEntryPointClassAt("test", "Main")
        .named("test")
        .buildWithArguments("assemble");
    
    ExecutableJar capsuleJar = CapsuleJar.at(project.file("build/libs/test-capsule.jar"));
    Execution execution = capsuleJar.run();
    
    assertThat(execution).failedAnd().standardError().contains("Min. Java version: 1.9");
  }
  
  @Test public void min_java_version_succeeds_when_later_java_version_is_available() throws Exception {
    project
        .withBuildScript(
            "plugins { id 'com.jonaslasauskas.capsule' }",
            "repositories { jcenter() }",
            "capsule { ",
            "  capsuleManifest {",
            "    applicationId = 'test'",
            "    applicationClass = 'test.Main'",
            "    minJavaVersion = '1.1'",
            "  }",
            "}")
        .withEntryPointClassAt("test", "Main")
        .named("test")
        .buildWithArguments("assemble");
    
    ExecutableJar capsuleJar = CapsuleJar.at(project.file("build/libs/test-capsule.jar"));
    Execution execution = capsuleJar.run();
    
    assertThat(execution).succeeded();
  }
  
  @Test public void min_update_version_fails_when_available_update_version_too_low() throws Exception {
    project
        .withBuildScript(
            "plugins { id 'com.jonaslasauskas.capsule' }",
            "repositories { jcenter() }",
            "capsule { ",
            "  capsuleManifest {",
            "    applicationId = 'test'",
            "    applicationClass = 'test.Main'",
            "    minJavaVersion = '1.8'",
            "    javaVersion = '1.8'",
            "    minUpdateVersion['1.8'] = '999'",
            "  }",
            "}")
        .withEntryPointClassAt("test", "Main")
        .named("test")
        .buildWithArguments("assemble");
    
    ExecutableJar capsuleJar = CapsuleJar.at(project.file("build/libs/test-capsule.jar"));
    Execution execution = capsuleJar.run();
    
    assertThat(execution).failedAnd().standardError().contains("Min. update version: {1.8=999}");
  }
  
  @Test public void min_update_version_succeeds_when_later_update_version_is_available() throws Exception {
    project
        .withBuildScript(
            "plugins { id 'com.jonaslasauskas.capsule' }",
            "repositories { jcenter() }",
            "capsule { ",
            "  capsuleManifest {",
            "    applicationId = 'test'",
            "    applicationClass = 'test.Main'",
            "    minUpdateVersion['1.8'] = '1'",
            "  }",
            "}")
        .withEntryPointClassAt("test", "Main")
        .named("test")
        .buildWithArguments("assemble");
    
    ExecutableJar capsuleJar = CapsuleJar.at(project.file("build/libs/test-capsule.jar"));
    Execution execution = capsuleJar.run();
    
    assertThat(execution).succeeded();
  }
  
  @Test public void java_version_fails_when_available_java_version_too_high() throws Exception {
    project
        .withBuildScript(
            "plugins { id 'com.jonaslasauskas.capsule' }",
            "repositories { jcenter() }",
            "capsule { ",
            "  capsuleManifest {",
            "    applicationId = 'test'",
            "    applicationClass = 'test.Main'",
            "    javaVersion = '1.3'",
            "  }",
            "}")
        .withEntryPointClassAt("test", "Main")
        .named("test")
        .buildWithArguments("assemble");
    
    ExecutableJar capsuleJar = CapsuleJar.at(project.file("build/libs/test-capsule.jar"));
    Execution execution = capsuleJar.run();
    
    assertThat(execution).failedAnd().standardError().contains("JavaVersion: 1.3");
  }
  
  @Test public void java_version_succeeds_when_earlier_java_version_is_available() throws Exception {
    project
        .withBuildScript(
            "plugins { id 'com.jonaslasauskas.capsule' }",
            "repositories { jcenter() }",
            "capsule { ",
            "  capsuleManifest {",
            "    applicationId = 'test'",
            "    applicationClass = 'test.Main'",
            "    javaVersion = '1.9'",
            "  }",
            "}")
        .withEntryPointClassAt("test", "Main")
        .named("test")
        .buildWithArguments("assemble");
    
    ExecutableJar capsuleJar = CapsuleJar.at(project.file("build/libs/test-capsule.jar"));
    Execution execution = capsuleJar.run();
    
    assertThat(execution).succeeded();
  }
  
  @Test public void jdk_required_succeeds_when_jdk_is_available() throws Exception {
    project
        .withBuildScript(
            "plugins { id 'com.jonaslasauskas.capsule' }",
            "repositories { jcenter() }",
            "capsule { ",
            "  capsuleManifest {",
            "    applicationId = 'test'",
            "    applicationClass = 'test.Main'",
            "    jdkRequired = true",
            "  }",
            "}")
        .withEntryPointClassAt("test", "Main")
        .named("test")
        .buildWithArguments("assemble");
    
    ExecutableJar capsuleJar = CapsuleJar.at(project.file("build/libs/test-capsule.jar"));
    Execution execution = capsuleJar.run();
    
    assertThat(execution).succeeded();
  }
  
  @Test public void jvm_args_are_passed_during_execution() throws Exception {
    project
        .withBuildScript(
            "plugins { id 'com.jonaslasauskas.capsule' }",
            "repositories { jcenter() }",
            "capsule { ",
            "  capsuleManifest {",
            "    applicationId = 'test'",
            "    applicationClass = 'test.Main'",
            "    jvmArgs << '-Dgreeting=Args'",
            "  }",
            "}")
        .withFile("src/main/java/test/Main.java",
            "package test;",
            "class Main {",
            "  public static void main(String[] args) {",
            "    System.out.println(\"Hello \" + System.getProperty(\"greeting\"));",
            "  }",
            "}")
        .named("test")
        .buildWithArguments("assemble");
    
    ExecutableJar capsuleJar = CapsuleJar.at(project.file("build/libs/test-capsule.jar"));
    Execution execution = capsuleJar.run();
    
    assertThat(execution).succeededAnd().standardOutput().contains("Hello Args");
  }
  
  @Test public void args_items_are_prepended_to_arguments_passed_when_executing_capsule() throws Exception {
    project
        .withBuildScript(
            "plugins { id 'com.jonaslasauskas.capsule' }",
            "repositories { jcenter() }",
            "capsule { ",
            "  capsuleManifest {",
            "    applicationId = 'test'",
            "    applicationClass = 'test.Main'",
            "    args << 'Hello'",
            "  }",
            "}")
        .withFile("src/main/java/test/Main.java",
            "package test;",
            "class Main {",
            "  public static void main(String[] args) {",
            "    System.out.println(String.join(\" \", args));",
            "  }",
            "}")
        .named("test")
        .buildWithArguments("assemble");
    
    ExecutableJar capsuleJar = CapsuleJar.at(project.file("build/libs/test-capsule.jar"));
    Execution execution = capsuleJar.runWithArguments("world!");
    
    assertThat(execution).succeededAnd().standardOutput().contains("Hello world!");
  }
  
  @Test public void environment_variables_are_passed_when_executing_capsule() throws Exception {
    project
        .withBuildScript(
            "plugins { id 'com.jonaslasauskas.capsule' }",
            "repositories { jcenter() }",
            "capsule { ",
            "  capsuleManifest {",
            "    applicationId = 'test'",
            "    applicationClass = 'test.Main'",
            "    environmentVariables['greeting'] = 'Hello'",
            "  }",
            "}")
        .withFile("src/main/java/test/Main.java",
            "package test;",
            "class Main {",
            "  public static void main(String[] args) {",
            "    System.out.println(System.getenv(\"greeting\") + \" world!\");",
            "  }",
            "}")
        .named("test")
        .buildWithArguments("assemble");
    
    ExecutableJar capsuleJar = CapsuleJar.at(project.file("build/libs/test-capsule.jar"));
    Execution execution = capsuleJar.run();
    
    assertThat(execution).succeededAnd().standardOutput().contains("Hello world!");
  }
  
  @Test public void system_properties_are_passed_when_executing_capsule() throws Exception {
    project
        .withBuildScript(
            "plugins { id 'com.jonaslasauskas.capsule' }",
            "repositories { jcenter() }",
            "capsule { ",
            "  capsuleManifest {",
            "    applicationId = 'test'",
            "    applicationClass = 'test.Main'",
            "    systemProperties['greeting'] = 'Hello'",
            "  }",
            "}")
        .withFile("src/main/java/test/Main.java",
            "package test;",
            "class Main {",
            "  public static void main(String[] args) {",
            "    System.out.println(System.getProperty(\"greeting\") + \" world!\");",
            "  }",
            "}")
        .named("test")
        .buildWithArguments("assemble");
    
    ExecutableJar capsuleJar = CapsuleJar.at(project.file("build/libs/test-capsule.jar"));
    Execution execution = capsuleJar.run();
    
    assertThat(execution).succeededAnd().standardOutput().contains("Hello world!");
  }
  
  @Test public void capsule_in_class_path_includes_capsule_when_set_to_true() throws Exception {
    project
        .withBuildScript(
            "plugins { id 'com.jonaslasauskas.capsule' }",
            "repositories { jcenter() }",
            "capsule { ",
            "  capsuleManifest {",
            "    applicationId = 'test'",
            "    applicationClass = 'test.Main'",
            "    capsuleInClassPath = true",
            "  }",
            "  from('capsule.txt')",
            "}")
        .withFile("capsule.txt", "resource in capsule")
        .withFile("src/main/java/test/Main.java",
            "package test;",
            "import java.io.*;",
            "class Main {",
            "  public static void main(String[] args) throws Exception {",
            "    InputStream resourceStream = Main.class.getClassLoader().getResourceAsStream(\"capsule.txt\");",
            "    if (resourceStream == null) {",
            "      System.out.println(\"capsule.txt not found in \" + System.getProperty(\"java.class.path\"));",
            "      System.exit(1);",
            "    }",
            "    System.out.println(new BufferedReader(new InputStreamReader(resourceStream)).readLine());",
            "    System.out.println(\"capsule.txt found in \" + System.getProperty(\"java.class.path\"));",
            "  }",
            "}")
        .named("test")
        .buildWithArguments("assemble");
    
    ExecutableJar capsuleJar = CapsuleJar.at(project.file("build/libs/test-capsule.jar"));
    Execution execution = capsuleJar.run();
    
    assertThat(execution).succeededAnd().standardOutput().contains("resource in capsule");
  }
  
  @Ignore("Possibly bug in Capsule: setting Capsule-In-Class-Path to 'false' still includes capsule jar to classpath") //
  @Test public void capsule_in_class_path_does_not_include_capsule_when_set_to_false() throws Exception {
    project
        .withBuildScript(
            "plugins { id 'com.jonaslasauskas.capsule' }",
            "repositories { jcenter() }",
            "capsule { ",
            "  capsuleManifest {",
            "    applicationId = 'test'",
            "    applicationClass = 'test.Main'",
            "    capsuleInClassPath = false",
            "  }",
            "  from('capsule.txt')",
            "}")
        .withFile("capsule.txt", "resource in capsule")
        .withFile("src/main/java/test/Main.java",
            "package test;",
            "import java.io.*;",
            "class Main {",
            "  public static void main(String[] args) throws Exception {",
            "    InputStream resourceStream = Main.class.getClassLoader().getResourceAsStream(\"capsule.txt\");",
            "    if (resourceStream == null) {",
            "      System.out.println(\"capsule.txt not found in \" + System.getProperty(\"java.class.path\"));",
            "      System.exit(1);",
            "    }",
            "    System.out.println(new BufferedReader(new InputStreamReader(resourceStream)).readLine());",
            "    System.out.println(\"capsule.txt found in \" + System.getProperty(\"java.class.path\"));",
            "  }",
            "}")
        .named("test")
        .buildWithArguments("assemble");
    
    ExecutableJar capsuleJar = CapsuleJar.at(project.file("build/libs/test-capsule.jar"));
    Execution execution = capsuleJar.run();
    
    assertThat(execution).failedAnd().exitCode().isEqualTo(1);
  }
  
  @Test public void capsule_log_level_prints_corresponding_diagnostic_information() throws Exception {
    project
        .withBuildScript(
            "plugins { id 'com.jonaslasauskas.capsule' }",
            "repositories { jcenter() }",
            "capsule { ",
            "  capsuleManifest {",
            "    applicationId = 'test'",
            "    applicationClass = 'test.Main'",
            "    logLevel = VERBOSE",
            "  }",
            "}")
        .withEntryPointClassAt("test", "Main")
        .named("test")
        .buildWithArguments("assemble");
    
    ExecutableJar capsuleJar = CapsuleJar.at(project.file("build/libs/test-capsule.jar"));
    Execution execution = capsuleJar.run();
    
    assertThat(execution).succeededAnd().standardError().contains("CAPSULE: Initialized app ID: test");
  }
  
}
