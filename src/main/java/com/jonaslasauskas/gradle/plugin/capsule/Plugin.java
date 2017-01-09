package com.jonaslasauskas.gradle.plugin.capsule;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.JavaPlugin;



class Plugin implements org.gradle.api.Plugin<Project> {
  
  @Override public void apply(Project project) {
    project.getPluginManager().apply(JavaPlugin.class);
    
    Capsule capsuleTask = project.getTasks().create("capsule", Capsule.class);
    capsuleTask.executesInside(project);
    
    Task assembleTask = project.getTasks().findByName("assemble");
    assembleTask.dependsOn(capsuleTask);
  }
  
}
