package com.jonaslasauskas.gradle.plugin.capsule;

import java.util.HashMap;

import org.gradle.api.tasks.Input;



public final class Manifest {
  
  public final String premainClass = "Capsule";
  
  public final String mainClass = "Capsule";
  
  @Input private String applicationId;
  
  @Input private String applicationClass;
  
  
  public void setApplicationId(String id) {
    applicationId = id;
  }
  
  public String getApplicationId() {
    return applicationId;
  }
  
  public void setApplicationClass(String className) {
    applicationClass = className;
  }
  
  public String getApplicationClass() {
    return applicationClass;
  }
  
  void defaultApplicationClassTo(String className) {
    if (applicationClass == null) {
      applicationClass = className;
    }
  }
  
  public void writeTo(org.gradle.api.java.archives.Manifest jarManifest) {
    HashMap<String, String> capsuleAttributes = new HashMap<>();
    capsuleAttributes.put("Premain-Class", premainClass);
    capsuleAttributes.put("Main-Class", mainClass);
    capsuleAttributes.put("Application-ID", applicationId);
    capsuleAttributes.put("Application-Class", applicationClass);
    
    jarManifest.attributes(capsuleAttributes);
  }
  
}
