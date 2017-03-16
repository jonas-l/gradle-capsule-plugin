package com.jonaslasauskas.gradle.plugin.capsule;

import java.util.HashMap;

import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;



public final class Manifest {
  
  public final String premainClass = "Capsule";
  
  public final String mainClass = "Capsule";
  
  @Input private String applicationId;
  
  @Input private String applicationClass;
  
  @Input @Optional private String minJavaVersion;
  
  
  public void setApplicationId(String id) {
    applicationId = id;
  }
  
  public String getApplicationId() {
    return applicationId;
  }
  
  public void setApplicationClass(String className) {
    applicationClass = className;
  }
  
  void defaultApplicationClassTo(String className) {
    if (applicationClass == null) {
      applicationClass = className;
    }
  }
  
  public String getApplicationClass() {
    return applicationClass;
  }
  
  void defaultApplicationIdTo(String id) {
    if (applicationId == null) {
      applicationId = id;
    }
  }
  
  public String getMinJavaVersion() {
    return minJavaVersion;
  }
  
  public void setMinJavaVersion(String minJavaVersion) {
    this.minJavaVersion = minJavaVersion;
  }
  
  public void writeTo(org.gradle.api.java.archives.Manifest jarManifest) {
    HashMap<String, String> capsuleAttributes = new HashMap<>();
    capsuleAttributes.put("Premain-Class", premainClass);
    capsuleAttributes.put("Main-Class", mainClass);
    capsuleAttributes.put("Application-ID", applicationId);
    capsuleAttributes.put("Application-Class", applicationClass);
    if (minJavaVersion != null) {
      capsuleAttributes.put("Min-Java-Version", minJavaVersion);
    }
    
    jarManifest.attributes(capsuleAttributes);
  }
  
}
