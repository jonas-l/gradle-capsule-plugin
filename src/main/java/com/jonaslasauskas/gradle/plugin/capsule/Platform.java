package com.jonaslasauskas.gradle.plugin.capsule;

public enum Platform {
  
  WINDOWS("Windows"), MACOS("MacOS"), LINUX("Linux"), SOLARIS("Solaris"), UNIX("Unix"), POSIX("POSIX");
  
  final String sectionName;
  
  
  private Platform(String sectionName) {
    this.sectionName = sectionName;
  }
  
}
