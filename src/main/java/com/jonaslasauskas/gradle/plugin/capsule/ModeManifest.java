package com.jonaslasauskas.gradle.plugin.capsule;

import java.util.ArrayList;
import java.util.List;

import org.gradle.util.ConfigureUtil;

import groovy.lang.Closure;



/**
 * Acts as a manifest container for particular (or no) mode. Enables nested
 * platform and java version manifests.
 */
class ModeManifest extends Manifest {
  
  public static final Platform WINDOWS = Platform.WINDOWS;
  
  public static final Platform MACOS = Platform.MACOS;
  
  public static final Platform LINUX = Platform.LINUX;
  
  public static final Platform SOLARIS = Platform.SOLARIS;
  
  public static final Platform UNIX = Platform.UNIX;
  
  public static final Platform POSIX = Platform.POSIX;
  
  private final List<Manifest> nestedManifests = new ArrayList<>();
  
  
  public ModeManifest() {
    super();
  }
  
  public ModeManifest(String sectionName) {
    super(sectionName);
  }
  
  void platform(Platform platform, Closure<?> configuration) {
    Manifest platformManifest = new Manifest(this.sectionNamePostfixedWith(platform.sectionName));
    ConfigureUtil.configure(configuration, platformManifest);
    nestedManifests.add(platformManifest);
  }
  
  void java(int version, Closure<?> configuration) {
    Manifest javaManifest = new Manifest(this.sectionNamePostfixedWith(JavaVersion.of(version).sectionName));
    ConfigureUtil.configure(configuration, javaManifest);
    nestedManifests.add(javaManifest);
  }
  
  @Override void writeTo(org.gradle.api.java.archives.Manifest jarManifest) {
    super.writeTo(jarManifest);
    
    nestedManifests.forEach(manifest -> manifest.writeTo(jarManifest));
  }
  
}
