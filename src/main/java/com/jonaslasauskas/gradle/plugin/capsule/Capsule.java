package com.jonaslasauskas.gradle.plugin.capsule;


import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.bundling.Jar;



public class Capsule extends Jar {
  
  @TaskAction public void encapsulate() {}
  
}
