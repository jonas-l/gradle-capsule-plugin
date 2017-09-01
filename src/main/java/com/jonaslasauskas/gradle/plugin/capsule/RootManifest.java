package com.jonaslasauskas.gradle.plugin.capsule;

import java.util.ArrayList;
import java.util.List;

import org.gradle.api.GradleException;
import org.gradle.api.tasks.Input;
import org.gradle.util.ConfigureUtil;

import groovy.lang.Closure;



/**
 * Represents main section of Capsule manifest.
 * 
 * <p>
 * It supports nested {@link ModeManifest#platform(Platform, Closure) platform},
 * {@link ModeManifest#java(int, Closure) java version}, and
 * {@link #mode(String, Closure) mode} manifests.
 */
public final class RootManifest extends ModeManifest {
  
  public final String premainClass = "Capsule";
  
  public final String mainClass = "Capsule";
  
  
  private final List<Manifest> nestedManifests = new ArrayList<>();
  
  
  @Input private String applicationId;
  
  
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
  
  void mode(String name, Closure<?> configuration) {
    if (Platform.anySectionNameMatches(name)) { throw new GradleException("Capsule mode cannot be named as platform '" + name + "'."); }
    if (JavaVersion.validRepresentation(name)) { throw new GradleException("Capsule mode cannot be named as java version '" + name + "'."); }
    
    ModeManifest modeManifest = new ModeManifest(name);
    ConfigureUtil.configure(configuration, modeManifest);
    nestedManifests.add(modeManifest);
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
