package com.jonaslasauskas.gradle.plugin;

import java.io.File;



public final class CapsuleJar {
  
  public static ExecutableJar at(File capsuleJarFile) {
    File capsuleCacheDir = new File(capsuleJarFile.getParentFile(), ".capsule");
    
    return ExecutableJar
        .at(capsuleJarFile)
        .withEnvironmentVariable("CAPSULE_CACHE_DIR", capsuleCacheDir.getAbsolutePath());
  }
  
  private CapsuleJar() {}
  
}
