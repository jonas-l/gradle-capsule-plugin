package com.jonaslasauskas.gradle.plugin.capsule;

import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;

import java.util.regex.Pattern;



final class JavaVersion {
  
  private static final String VERSION_PREFIX = "Java-";
  
  private static final Pattern VERSION_PATTERN = compile(quote(VERSION_PREFIX) + "(\\d+)");
  
  
  public final String sectionName;
  
  
  public static JavaVersion of(int version) {
    return new JavaVersion(VERSION_PREFIX + version);
  }
  
  public static boolean validRepresentation(String javaVersionString) {
    return VERSION_PATTERN.matcher(javaVersionString).find();
  }
  
  public JavaVersion(String sectionName) {
    this.sectionName = sectionName;
  }
  
}
