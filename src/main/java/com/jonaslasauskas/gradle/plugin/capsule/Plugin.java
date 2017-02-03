package com.jonaslasauskas.gradle.plugin.capsule;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.JavaPlugin;



class Plugin implements org.gradle.api.Plugin<Project> {
  
  @Override public void apply(Project project) {
    project.getPluginManager().apply(JavaPlugin.class);
    
    project.getConfigurations().create("capsule").defaultDependencies(dependencySet -> {
      dependencySet.add(project.getDependencies().create("co.paralleluniverse:capsule:1.0.3"));
    });
    
    Capsule capsuleTask = project.getTasks().create("capsule", Capsule.class);
    project.getTasks().withType(Capsule.class).all(task -> task.executesInside(project));
    
    Task assembleTask = project.getTasks().findByName("assemble");
    assembleTask.dependsOn(capsuleTask);
    
    Task jarTask = project.getTasks().findByName("jar");
    capsuleTask.dependsOn(jarTask);
  }
  
}
