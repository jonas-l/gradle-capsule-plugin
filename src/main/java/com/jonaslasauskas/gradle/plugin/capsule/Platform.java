package com.jonaslasauskas.gradle.plugin.capsule;

import static java.util.Arrays.stream;



public enum Platform {
  
  WINDOWS("Windows"), MACOS("MacOS"), LINUX("Linux"), SOLARIS("Solaris"), UNIX("Unix"), POSIX("POSIX");
  
  
  public static boolean anySectionNameMatches(String otherName) {
    return stream(Platform.values()).anyMatch(platform -> platform.sectionName.equals(otherName));
  }
  
  
  final String sectionName;
  
  
  private Platform(String sectionName) {
    this.sectionName = sectionName;
  }
  
}
