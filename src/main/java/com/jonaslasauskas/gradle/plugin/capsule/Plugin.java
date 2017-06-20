package com.jonaslasauskas.gradle.plugin.capsule;

import static org.gradle.api.plugins.BasePlugin.BUILD_GROUP;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.JavaPlugin;



/**
 * Introduces {@link Capsule capsule} task with all necessary dependencies on
 * artifacts and other tasks.
 */
class Plugin implements org.gradle.api.Plugin<Project> {
  
  @Override public void apply(Project project) {
    project.getPluginManager().apply(JavaPlugin.class);
    
    project.getConfigurations().create("capsule").defaultDependencies(dependencySet -> {
      dependencySet.add(project.getDependencies().create("co.paralleluniverse:capsule:1.0.3"));
    });
    
    project.getTasks().withType(Capsule.class).all(task -> task.executesInside(project));
    
    Capsule capsuleTask = project.getTasks().create("capsule", Capsule.class);
    capsuleTask.setGroup(BUILD_GROUP);
    capsuleTask.setDescription("Assembles a jar archive containing Capsule, caplets, and necessary jars to run an application.");
    
    Task assembleTask = project.getTasks().findByName("assemble");
    assembleTask.dependsOn(capsuleTask);
    
    Task jarTask = project.getTasks().findByName("jar");
    capsuleTask.dependsOn(jarTask);
  }
  
}
