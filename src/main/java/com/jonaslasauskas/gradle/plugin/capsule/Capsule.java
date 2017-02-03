package com.jonaslasauskas.gradle.plugin.capsule;


import java.io.File;
import java.util.Set;

import org.gradle.api.Action;
import org.gradle.api.Project;
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
      Set<File> capsuleArtifacts = p.getConfigurations().getAt("capsule").resolve();
      from(capsuleArtifacts.stream().map(project::zipTree).toArray());
      
      from(p.getTasks().getAt("jar").getOutputs().getFiles());
      
      capsuleManifest.writeTo(getManifest());
    });
  }
  
}
