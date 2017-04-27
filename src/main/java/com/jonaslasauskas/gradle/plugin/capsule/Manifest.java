package com.jonaslasauskas.gradle.plugin.capsule;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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
  
  @Input private List<String> jvmArgs = new ArrayList<>();
  
  @Input private List<String> args = new ArrayList<>();
  
  @Input private Map<String, String> environmentVariables = new LinkedHashMap<>();
  
  @Input private Map<String, String> systemProperties = new LinkedHashMap<>();
  
  @Input private boolean capsuleInClassPath = true;
  
  
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
  
  public List<String> getJvmArgs() {
    return jvmArgs;
  }
  
  public void setJvmArgs(List<String> jvmArgs) {
    this.jvmArgs = jvmArgs;
  }
  
  public List<String> getArgs() {
    return args;
  }
  
  public void setArgs(List<String> args) {
    this.args = args;
  }
  
  public Map<String, String> getEnvironmentVariables() {
    return environmentVariables;
  }
  
  public void setEnvironmentVariables(Map<String, String> environmentVariables) {
    this.environmentVariables = environmentVariables;
  }
  
  public Map<String, String> getSystemProperties() {
    return systemProperties;
  }
  
  public void setSystemProperties(Map<String, String> systemProperties) {
    this.systemProperties = systemProperties;
  }
  
  public boolean isCapsuleInClassPath() {
    return capsuleInClassPath;
  }
  
  public void setCapsuleInClassPath(boolean capsuleInClassPath) {
    this.capsuleInClassPath = capsuleInClassPath;
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
        .putIfPresent("JVM-Args", jvmArgs)
        .putIfPresent("Args", args)
        .putIfPresent("Environment-Variables", environmentVariables)
        .putIfPresent("System-Properties", systemProperties)
        .putIfPresent("Capsule-In-Class-Path", !capsuleInClassPath ? "false" : null)
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
    
    public Attributes putIfPresent(String name, List<String> values) {
      if (values != null && !values.isEmpty()) {
        map.put(name, String.join(" ", values));
      }
      
      return this;
    }
    
    public void writeTo(org.gradle.api.java.archives.Manifest manifest) {
      manifest.attributes(map);
    }
    
  }
  
}
