package com.jonaslasauskas.gradle.plugin;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.CharStreams.copy;
import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;



public final class ExecutableJar {
  
  public static ExecutableJar at(File jarFile) {
    File directory = jarFile.getParentFile();
    assertThat(directory.list()).asList().named("Files at %s", directory).contains(jarFile.getName());
    
    return new ExecutableJar(jarFile);
  }
  
  
  private final String jarFilePath;
  
  private final List<String> systemProperties;
  
  
  public ExecutableJar(File jarFile) {
    jarFilePath = jarFile.getAbsolutePath();
    systemProperties = new ArrayList<>();
  }
  
  public ExecutableJar withSystemProperty(String property) {
    systemProperties.add("-D" + property);
    
    return this;
  }
  
  public ExecutableJar withSystemProperty(String property, String value) {
    systemProperties.add("-D" + property + "=" + value);
    
    return this;
  }
  
  public Execution run() throws IOException {
    return new Execution(new ProcessBuilder(jarCommand()));
  }
  
  public Execution runWithArguments(String... arguments) {
    List<String> command = jarCommand();
    command.addAll(asList(arguments));
    
    return new Execution(new ProcessBuilder(command));
  }
  
  private List<String> jarCommand() {
    List<String> command = new ArrayList<>();
    command.add("java");
    command.addAll(systemProperties);
    command.add("-jar");
    command.add(jarFilePath);
    return command;
  }
  
  
  public static final class Execution {
    
    public final Iterable<String> command;
    
    public final int exitCode;
    
    public final String output;
    
    public final String error;
    
    
    public Execution(ProcessBuilder processBuilder) {
      try {
        Process process = processBuilder.start();
        
        try (
            Reader outputReader = new InputStreamReader(process.getInputStream(), UTF_8);
            Reader errorReader = new InputStreamReader(process.getErrorStream(), UTF_8)) {
          
          process.waitFor(42, SECONDS);
          
          StringBuilder outputBuilder = new StringBuilder();
          copy(outputReader, outputBuilder);
          
          StringBuilder errorBuilder = new StringBuilder();
          copy(errorReader, errorBuilder);
          
          command = processBuilder.command();
          exitCode = process.exitValue();
          output = outputBuilder.toString();
          error = errorBuilder.toString();
        } catch (InterruptedException e) {
          throw new RuntimeException("Execution failed.", e);
        }
      } catch (IOException e) {
        throw new RuntimeException("Execution did not start.", e);
      }
    }
    
  }
  
}
