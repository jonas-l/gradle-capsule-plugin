package com.jonaslasauskas.gradle.plugin.capsule;


import java.io.File;
import java.util.Set;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.util.ConfigureUtil;

import groovy.lang.Closure;



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
  
  void executesInside(Project project) {
    this.setBaseName(project.getName());
    
    project.afterEvaluate(p -> {
      mergeContentOf(p.getConfigurations().getAt("capsule"), p);
      
      from(p.getTasks().getAt("jar").getOutputs().getFiles());
      
      defaultAttributesUsingDetailsFrom(p);
      capsuleManifest.writeTo(getManifest());
    });
  }
  
  private void mergeContentOf(Configuration configuration, Project project) {
    Set<File> capsuleArtifacts = configuration.resolve();
    from(capsuleArtifacts.stream().map(project::zipTree).toArray());
  }
  
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
