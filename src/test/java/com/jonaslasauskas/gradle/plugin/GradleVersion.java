package com.jonaslasauskas.gradle.plugin;

import java.util.Optional;



public final class GradleVersion {
  
  public static final String underTest() {
    return fromEnvironmentVariable().orElse(current());
  }
  
  public static final String current() {
    return org.gradle.util.GradleVersion.current().getVersion();
  }
  
  public static final Optional<String> fromEnvironmentVariable() {
    return Optional.ofNullable(System.getenv("GRADLE_VERSION_UNDER_TEST"));
  }
  
  private GradleVersion() {}
  
}
