package com.jonaslasauskas.gradle.plugin.capsule;


import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.bundling.Jar;



public class Capsule extends Jar {
  
  public Capsule() {
    this.setClassifier("capsule");
  }
  
  @TaskAction public void encapsulate() {}
  
  void executesInside(Project project) {
    this.setBaseName(project.getName());
  }
  
}
