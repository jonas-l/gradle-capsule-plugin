package com.jonaslasauskas.gradle.plugin.capsule;

import org.gradle.api.tasks.Input;



/**
 * Represents main section of Capsule manifest.
 */
public final class RootManifest extends Manifest {
  
  public final String premainClass = "Capsule";
  
  public final String mainClass = "Capsule";
  
  
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
  
  @Override void writeTo(org.gradle.api.java.archives.Manifest jarManifest) {
    new Attributes()
        .putIfPresent("Premain-Class", premainClass)
        .putIfPresent("Main-Class", mainClass)
        .putIfPresent("Application-ID", applicationId)
        .writeTo(jarManifest);
    super.writeTo(jarManifest);
  }
  
}
