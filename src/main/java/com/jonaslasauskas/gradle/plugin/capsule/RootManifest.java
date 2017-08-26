package com.jonaslasauskas.gradle.plugin.capsule;

import java.util.ArrayList;
import java.util.List;

import org.gradle.api.tasks.Input;
import org.gradle.util.ConfigureUtil;

import groovy.lang.Closure;



/**
 * Represents main section of Capsule manifest.
 * 
 * <p>
 * It supports nested {@link #platform(Platform, Closure) platform} and
 * {@link #java(int, Closure) java version} manifests.
 */
public final class RootManifest extends Manifest {
  
  public static final Platform WINDOWS = Platform.WINDOWS;
  
  public static final Platform MACOS = Platform.MACOS;
  
  public static final Platform LINUX = Platform.LINUX;
  
  public static final Platform SOLARIS = Platform.SOLARIS;
  
  public static final Platform UNIX = Platform.UNIX;
  
  public static final Platform POSIX = Platform.POSIX;
  
  
  public final String premainClass = "Capsule";
  
  public final String mainClass = "Capsule";
  
  
  @Input private String applicationId;
  
  private List<Manifest> nestedManifests = new ArrayList<>();
  
  
  public void setApplicationId(String id) {
    applicationId = id;
  }
  
  public String getApplicationId() {
    return applicationId;
  }
  
  void defaultApplicationIdTo(String id) {
    if (applicationId == null) {
      applicationId = id;
    }
  }
  
  void platform(Platform platform, Closure<?> configuration) {
    Manifest platformManifest = new Manifest(platform.sectionName);
    ConfigureUtil.configure(configuration, platformManifest);
    nestedManifests.add(platformManifest);
  }
  
  void java(int version, Closure<?> configuration) {
    Manifest javaManifest = new Manifest("Java-" + version);
    ConfigureUtil.configure(configuration, javaManifest);
    nestedManifests.add(javaManifest);
  }
  
  @Override void writeTo(org.gradle.api.java.archives.Manifest jarManifest) {
    new Attributes()
        .putIfPresent("Premain-Class", premainClass)
        .putIfPresent("Main-Class", mainClass)
        .putIfPresent("Application-ID", applicationId)
        .writeTo(jarManifest);
    super.writeTo(jarManifest);
    
    nestedManifests.forEach(manifest -> manifest.writeTo(jarManifest));
  }
  
}
