package com.jonaslasauskas.gradle.plugin.capsule;

import static java.util.stream.Collectors.joining;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;



public final class Manifest {
  
  public final String premainClass = "Capsule";
  
  public final String mainClass = "Capsule";
  
  @Input private String applicationId;
  
  @Input private String applicationClass;
  
  @Input @Optional private String minJavaVersion;
  
  @Input @Optional private Map<String, String> minUpdateVersion = new LinkedHashMap<>();
  
  @Input @Optional private String javaVersion;
  
  @Input private boolean jdkRequired = false;
  
  
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
  
  public Map<String, String> getMinUpdateVersion() {
    return minUpdateVersion;
  }
  
  public void setMinUpdateVersion(Map<String, String> minUpdateVersion) {
    this.minUpdateVersion = minUpdateVersion;
  }
  
  public String getJavaVersion() {
    return javaVersion;
  }
  
  public void setJavaVersion(String javaVersion) {
    this.javaVersion = javaVersion;
  }
  
  public boolean isJdkRequired() {
    return jdkRequired;
  }
  
  public void setJdkRequired(boolean jdkRequired) {
    this.jdkRequired = jdkRequired;
  }
  
  public void writeTo(org.gradle.api.java.archives.Manifest jarManifest) {
    new Attributes()
        .putIfPresent("Premain-Class", premainClass)
        .putIfPresent("Main-Class", mainClass)
        .putIfPresent("Application-ID", applicationId)
        .putIfPresent("Application-Class", applicationClass)
        .putIfPresent("Min-Java-Version", minJavaVersion)
        .putIfPresent("Min-Update-Version", minUpdateVersion)
        .putIfPresent("Java-Version", javaVersion)
        .putIfPresent("JDK-Required", jdkRequired ? "true" : null)
        .writeTo(jarManifest);
  }
  
  
  private static class Attributes {
    
    private final HashMap<String, String> map;
    
    
    public Attributes() {
      this.map = new HashMap<>();
    }
    
    public Attributes putIfPresent(String name, String value) {
      if (value != null) {
        map.put(name, value);
      }
      
      return this;
    }
    
    public Attributes putIfPresent(String name, Map<String, String> valueMap) {
      if (valueMap != null && !valueMap.isEmpty()) {
        String value = valueMap.entrySet().stream()
            .map(entry -> entry.getKey() + "=" + entry.getValue())
            .collect(joining(" "));
        map.put(name, value);
      }
      
      return this;
    }
    
    public void writeTo(org.gradle.api.java.archives.Manifest manifest) {
      manifest.attributes(map);
    }
    
  }
  
}
