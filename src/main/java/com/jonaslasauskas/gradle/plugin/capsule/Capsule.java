package com.jonaslasauskas.gradle.plugin.capsule;


import static org.gradle.api.plugins.JavaPlugin.RUNTIME_CONFIGURATION_NAME;

import java.io.File;
import java.util.Set;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.util.ConfigureUtil;

import groovy.lang.Closure;



/**
 * Produces capsule jar.
 * 
 * <p>
 * It is responsible for configuring {@link Jar} task to include application
 * itself, necessary dependencies, and specifying manifest entries.
 * 
 * <p>
 * This class dedicates manifest entries formatting to {@link Manifest} class.
 */
public class Capsule extends Jar {
  
  private Manifest capsuleManifest = new Manifest();
  
  
  public Capsule() {
    this.setClassifier("capsule");
  }
  
  @Nested public Manifest getCapsuleManifest() {
    return this.capsuleManifest;
  }
  
  public Manifest capsuleManifest(Closure<?> configuration) {
    ConfigureUtil.configure(configuration, capsuleManifest);
    
    return this.capsuleManifest;
  }
  
  public Manifest capsuleManifest(Action<Manifest> manifest) {
    manifest.execute(getCapsuleManifest());
    return this.capsuleManifest;
  }
  
  /**
   * Gives a context for the task from which default values can be resolved.
   * 
   * <p>
   * Having access to the project itself, the task resolves sensible defaults
   * for various manifest entries.
   * 
   * @param project Context the task is used in.
   */
  void executesInside(Project project) {
    this.setBaseName(project.getName());
    
    project.afterEvaluate(p -> {
      mergeContentOf(p.getConfigurations().getAt("capsule"), p);
      
      from(p.getTasks().getAt("jar").getOutputs().getFiles());
      from(p.getConfigurations().getAt(RUNTIME_CONFIGURATION_NAME));
      
      defaultAttributesUsingDetailsFrom(p);
      capsuleManifest.writeTo(getManifest());
    });
  }
  
  private void mergeContentOf(Configuration configuration, Project project) {
    Set<File> capsuleArtifacts = configuration.resolve();
    from(capsuleArtifacts.stream().map(project::zipTree).toArray());
  }
  
  /**
   * Resolve default values to be used in manifest.
   * 
   * @param project Context the default values should be resolved from.
   */
  private void defaultAttributesUsingDetailsFrom(Project project) {
    Object projectGroup = project.getGroup();
    String projectName = project.getName();
    if (projectGroup != null) {
      capsuleManifest.defaultApplicationIdTo(projectGroup + "." + projectName);
    }
    
    Jar jarTask = (Jar) project.getTasks().getAt("jar");
    capsuleManifest.defaultApplicationClassTo((String) jarTask.getManifest().getAttributes().get("Main-Class"));
  }
  
}
